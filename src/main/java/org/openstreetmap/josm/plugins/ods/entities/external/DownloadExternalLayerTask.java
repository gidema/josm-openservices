package org.openstreetmap.josm.plugins.ods.entities.external;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JOptionPane;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.Main;
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
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.AddressToBuildingMatcher;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.BuildingCompletenessAnalyzer;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.BuildingSimplifier;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.CrossingBuildingAnalyzer;
import org.openstreetmap.josm.plugins.ods.issue.Issue;
import org.openstreetmap.josm.plugins.ods.jts.Boundary;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;
import org.openstreetmap.josm.tools.I18n;

import com.vividsolutions.jts.geom.prep.PreparedPolygon;

public class DownloadExternalLayerTask implements DownloadTask {
    private boolean cancelled;
    private boolean failed;
    private String message;
    private Exception exception;

    private OdsWorkingSet workingSet;
    private ExternalDataLayer dataLayer;
    private Boundary boundary;
    private List<ExternalDownloadTask> downloadTasks;
    private ExecutorService executor;

    private EntitySet entities;
    private EntityFactory entityFactory;
    private List<Analyzer> analyzers;

    public DownloadExternalLayerTask(Boundary boundary) {
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
        // analyzers.add(new AddressToStreetMatcher());
        setup();
    }

    public void cancel() {
        cancelled = true;
    }
    
    private void setup() {
        downloadTasks = new ArrayList<ExternalDownloadTask>(workingSet
                .getDataSources().size());
        for (OdsDataSource dataSource : workingSet.getDataSources().values()) {
            downloadTasks.add(dataSource.createDownloadTask(boundary));
        }
    }

    @Override
    public Callable<Object> stage(final String stage) {
        switch (stage) {
        case "prepare":
        case "download":
            return stage(stage, downloadTasks);
        case "process":
            return new ProcessStage();
        }
        return null;
    }
    
    private Callable<Object> stage(final String subTask, final List<? extends DownloadTask> tasks) {
        // TODO move this code into a stand alone class.
        return new Callable<Object>() {

            @Override
            public Object call() throws Exception {
                List<Future<?>> futures = new ArrayList<Future<?>>(tasks.size());

                executor = Executors.newFixedThreadPool(tasks.size());
                for (DownloadTask task : downloadTasks) {
                    Callable<Object> callable = task.stage(subTask);
                    if (callable != null) {
                        Future<?> future = executor.submit(callable);
                        futures.add(future);
                    }
                }
                executor.shutdown();
                cancelled = false;
                List<Exception> exceptions = new LinkedList<Exception>();
                // Wait for all futures to finish
                for (Future<?> future : futures) {
                    try {
                        future.get();
                        if (future.isCancelled()) {
                            executor.shutdownNow();
                            cancelled = true;                    
                        }
                    } catch (InterruptedException e) {
                        executor.shutdownNow();
                        cancelled = true;
                    } catch (Exception e) {
                        exceptions.add(e);
                    }
                }
                for (DownloadTask task : tasks) {
                    if (task.failed()) {
                        cancelled = true;
                        if (task.getMessage() != null) {
                            JOptionPane.showMessageDialog(Main.parent, task.getMessage());
                        }
                    }
                }
                for (DownloadTask task : tasks) {
                    if (task.cancelled()) {
                        cancelled = true;
                        if (task.getMessage() != null) {
                            JOptionPane.showMessageDialog(Main.parent, task.getMessage());
                        }
                    }
                }
                if (cancelled) {
                    return null;
                }
                for (DownloadTask task : tasks) {
                    if (task.getMessage() != null) {
                        JOptionPane.showMessageDialog(Main.parent, task.getMessage());
                    }
                }
                if (!exceptions.isEmpty()) {
                    StringBuilder msg = new StringBuilder();
                    msg.append(I18n.trn("An error occured while downloading the data:", 
                        "{0} errors occurred while downloading the data:", exceptions.size(), exceptions.size()));
                    for (Exception e: exceptions) {
                        msg.append("\n").append(e.getMessage());
                    }
                }
                return null;
            }
         };
    }

    @Override
    public boolean cancelled() {
        return cancelled;
    }

    @Override
    public boolean failed() {
        return failed;
    }

    @Override
    public String getMessage() {
        if (message != null)
            return message;
        if (exception != null)
            return exception.getMessage();
        return null;
    }


    class ProcessStage implements Callable<Object> {

        @Override
        public Object call() throws Exception {
            build();
            return null;
        }

        /**
         * Build entities from the imported features. First create individual
         * entities for each feature type Then build the relations between the
         * entities
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
         * Create entities for the downloaded features. Add them to the dataSet
         * and to the new entities collection if they don't exist.
         * 
         * @param task
         * @throws BuildException
         */
        private void buildEntities(ExternalDownloadTask task)
                throws BuildException {
            String entityType = task.getDataSource().getEntityType();
            // EntityStore store =
            // dataLayer.getEntitySet().getStore(entityType);
            MetaData metaData = task.getDataSource().getMetaData();
            List<Issue> issues = new LinkedList<>();
            PreparedPolygon preparedBoundary = new PreparedPolygon(
                    boundary.getPolygon());
            for (SimpleFeature feature : task.getFeatures()) {
                try {
                    Entity entity = entityFactory.buildEntity(entityType,
                            metaData, feature);
                    if (boundary.isRectangular()) {
                        entities.add(entity);
                    } else if (preparedBoundary
                            .intersects(entity.getGeometry())) {
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
}
