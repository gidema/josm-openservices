package org.openstreetmap.josm.plugins.ods.entities.external;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.analysis.Analyzer;
import org.openstreetmap.josm.plugins.ods.entities.BuildException;
import org.openstreetmap.josm.plugins.ods.entities.DefaultEntitySet;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.EntityFactory;
import org.openstreetmap.josm.plugins.ods.entities.EntitySet;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.AddressNodeDistributor;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.AddressToBuildingMatcher;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.BuildingCompletenessAnalyzer;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.BuildingSimplifier;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.CrossingBuildingAnalyzer;
import org.openstreetmap.josm.plugins.ods.geotools.GtDownloader;
import org.openstreetmap.josm.plugins.ods.io.DownloadJob;
import org.openstreetmap.josm.plugins.ods.io.Downloader;
import org.openstreetmap.josm.plugins.ods.io.Status;
import org.openstreetmap.josm.plugins.ods.issue.Issue;
import org.openstreetmap.josm.plugins.ods.jts.Boundary;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;

import com.vividsolutions.jts.geom.prep.PreparedPolygon;

public class GeotoolsDownloadJob implements DownloadJob {
//    private OdsWorkingSet workingSet;
    private ExternalDataLayer dataLayer;
    private Boundary boundary;
    private List<GtDownloader> downloaders;
    private EntitySet entities;
    private EntityFactory entityFactory;
    private List<Analyzer> analyzers;
    private Status status = new Status();

    @Inject
    public GeotoolsDownloadJob(ExternalDataLayer dataLayer, List<GtDownloader> downloaders, EntityFactory entityFactory) {
        this.dataLayer = dataLayer;
        this.downloaders = downloaders;
        this.entityFactory = entityFactory;
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
    public List<? extends Downloader> getDownloaders() {
        return downloaders;
    }


    public void setBoundary(Boundary boundary) {
        for (GtDownloader downloader : downloaders) {
            downloader.setBoundary(boundary);
        }
        this.boundary = boundary;
    }
    
    public Status getStatus() {
        return status;
    }

    /** 
     * Build entities from the imported features.
     * First create individual entities for each feature type
     * Then build the relations between the entities
     * 
     * @see org.openstreetmap.josm.plugins.ods.io.DownloadJob#build()
     */
    public void build() throws BuildException {
        // First create entities for all downloaded features
        entities = new DefaultEntitySet();
        for (GtDownloader downloader : downloaders) {
            buildEntities(downloader);
        }
        // Retrieve the results
        entities.extendBoundary(boundary.getMultiPolygon());
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
    private void buildEntities(GtDownloader downloader) throws BuildException {
        MetaData metaData = downloader.getDataSource().getMetaData();
        List<Issue> issues = new LinkedList<>();
        PreparedPolygon preparedBoundary = new PreparedPolygon(boundary.getMultiPolygon());
        for (SimpleFeature feature : downloader.getNewFeatures()) {
            try {
                Entity entity = entityFactory.buildEntity(metaData, feature);
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
