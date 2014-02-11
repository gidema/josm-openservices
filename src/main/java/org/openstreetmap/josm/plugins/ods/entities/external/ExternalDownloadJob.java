package org.openstreetmap.josm.plugins.ods.entities.external;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.DownloadJob;
import org.openstreetmap.josm.plugins.ods.DownloadTask;
import org.openstreetmap.josm.plugins.ods.ODS;
import org.openstreetmap.josm.plugins.ods.OdsDataSource;
import org.openstreetmap.josm.plugins.ods.OdsWorkingSet;
import org.openstreetmap.josm.plugins.ods.analysis.Analyzer;
import org.openstreetmap.josm.plugins.ods.entities.BuildException;
import org.openstreetmap.josm.plugins.ods.entities.DefaultEntitySet;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.EntityFactory;
import org.openstreetmap.josm.plugins.ods.entities.EntitySet;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.AddressNodeDistributor;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.AddressNodeDistributor;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.AddressToBuildingMatcher;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.BuildingCompletenessAnalyzer;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.BuildingSimplifier;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.CrossingBuildingAnalyzer;
import org.openstreetmap.josm.plugins.ods.issue.Issue;
import org.openstreetmap.josm.plugins.ods.jts.Boundary;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;

import com.vividsolutions.jts.geom.prep.PreparedPolygon;

public class ExternalDownloadJob implements DownloadJob {
    private OdsWorkingSet workingSet;
    private ExternalDataLayer dataLayer;
    private Boundary boundary;
    private List<ExternalDownloadTask> downloadTasks;
    private EntitySet entities;
    private EntityFactory entityFactory;
    private List<Analyzer> analyzers;

    public ExternalDownloadJob(Boundary boundary) {
        this.workingSet = ODS.getModule().getWorkingSet();
        this.dataLayer = workingSet.getExternalDataLayer();
        this.entityFactory = workingSet.getEntityFactory();
        this.boundary = boundary;
        Double tolerance = 2e-7;
        analyzers = new ArrayList<>(5);
        analyzers.add(new BuildingSimplifier(tolerance));
        analyzers.add(new CrossingBuildingAnalyzer(tolerance));
        analyzers.add(new AddressToBuildingMatcher());
        analyzers.add(new AddressNodeDistributor());
        analyzers.add(new BuildingCompletenessAnalyzer());
        //analyzers.add(new AddressToStreetMatcher());
    }

    @Override
    public void setup() {
        downloadTasks = new ArrayList<ExternalDownloadTask>(workingSet.getDataSources().size());
        for (OdsDataSource dataSource : workingSet.getDataSources().values()) {
            downloadTasks.add(dataSource.createDownloadTask(boundary));
        }
    }
    
    @Override
    public List<Callable<?>> getPrepareCallables() {
        List<Callable<?>> callables = new ArrayList<>(downloadTasks.size());
        for (DownloadTask downloadTask : downloadTasks) {
            callables.add(downloadTask.getPrepareCallable());
        }
        return callables;
    }

    @Override
    public List<Callable<?>> getDownloadCallables() {
        List<Callable<?>> callables = new ArrayList<>(downloadTasks.size());
        for (DownloadTask downloadTask : downloadTasks) {
            callables.add(downloadTask.getDownloadCallable());
        }
        return callables;
    }

    @Override
    public List<? extends DownloadTask> getDownloadTasks() {
        return downloadTasks;
    }

    /** 
     * Build entities from the imported features.
     * First create individual entities for each feature type
     * Then build the relations between the entities
     * 
     * @see org.openstreetmap.josm.plugins.ods.DownloadJob#build()
     */
    public void build() throws BuildException {
        // First create entities for all downloaded features
        entities = new DefaultEntitySet();
        for (ExternalDownloadTask downloadTask : downloadTasks) {
            buildEntities(downloadTask);
        }
        // Retrieve the results
        entities.extendBoundary(boundary.getPolygon());
        // Next establish the relationships between the features
        analyze();
        dataLayer.merge(entities);
    }
    
    private void analyze() {
        for (Analyzer analyzer : analyzers) {
            analyzer.analyze(dataLayer, entities);
        }
    }
    
    /**
     * Create entities for the downloaded features. Add them to the dataSet and to the 
     * new entities collection if they don't exist.
     *  
     * @param task
     * @throws BuildException 
     */
    private void buildEntities(ExternalDownloadTask task) throws BuildException {
        String entityType = task.getDataSource().getEntityType();
//        EntityStore store = dataLayer.getEntitySet().getStore(entityType);
        MetaData metaData = task.getDataSource().getMetaData();
        List<Issue> issues = new LinkedList<>();
        PreparedPolygon preparedBoundary = new PreparedPolygon(boundary.getPolygon());
        for (SimpleFeature feature : task.getFeatures()) {
            try {
                Entity entity = entityFactory.buildEntity(entityType, metaData, feature);
                if (boundary.isRectangular()) {
                    entities.add(entity);
                }
                else if (preparedBoundary.intersects(entity.getGeometry())) {
                    entities.add(entity);
                }
            } catch (BuildException e) {
                issues.add(e.getIssue());
            }
        }
        if (!issues.isEmpty()) {
            throw new BuildException(issues);
        }
    }
}
