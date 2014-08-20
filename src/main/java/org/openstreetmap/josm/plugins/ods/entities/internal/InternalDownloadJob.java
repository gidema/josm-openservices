package org.openstreetmap.josm.plugins.ods.entities.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.swing.JOptionPane;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.plugins.ods.DownloadJob;
import org.openstreetmap.josm.plugins.ods.DownloadTask;
import org.openstreetmap.josm.plugins.ods.analysis.Analyzer;
import org.openstreetmap.josm.plugins.ods.entities.BuildException;
import org.openstreetmap.josm.plugins.ods.entities.EntityFactory;
import org.openstreetmap.josm.plugins.ods.entities.EntitySet;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.AddressToBuildingMatcher;
import org.openstreetmap.josm.plugins.ods.issue.Issue;
import org.openstreetmap.josm.plugins.ods.jts.Boundary;
import org.openstreetmap.josm.tools.I18n;

public class InternalDownloadJob implements DownloadJob {
//    private final OdsModule module;
    private InternalDownloadTask downloadTask;
    private InternalDataLayer dataLayer;
    private EntityFactory entityFactory;
    private List<Analyzer> analyzers;
    private EntitySet newEntities;

    @Inject
    public InternalDownloadJob(InternalDataLayer dataLayer, InternalDownloadTask downloadTask, EntityFactory entityFactory) {
        super();
        this.downloadTask = downloadTask;
//        this.module = module;
        this.dataLayer = dataLayer;
        this.entityFactory = entityFactory;
        Double tolerance = 2e-7;
        analyzers = new ArrayList<>(5);
        analyzers.add(new AddressToBuildingMatcher());
//        analyzers.add(new AddressToStreetMatcher());
    }

    public void setBoundary(Boundary boundary) {
        downloadTask.setBoundary(boundary);
    }

//    public void setup() {
//        InternalDownloadTask downloadTask = new InternalDownloadTask(module, boundary);
//        downloadTasks = Collections.singletonList(downloadTask);
//    }
//
    @Override
    public List<Callable<?>> getPrepareCallables() {
        List<Callable<?>> callables = new ArrayList<>(1);
        callables.add(downloadTask.getPrepareCallable());
        return callables;
    }

    @Override
    public List<Callable<?>> getDownloadCallables() {
        List<Callable<?>> callables = new ArrayList<>(1);
        callables.add(downloadTask.getDownloadCallable());
        return callables;
    }

    @Override
    public List<? extends DownloadTask> getDownloadTasks() {
        return Collections.singletonList(downloadTask);
    }

    @Override
    public void build() throws BuildException {
//        if (task.failed) {
//            JOptionPane.showMessageDialog(Main.parent, task.errorMessage);
//            return;
//        }
        DataSet dataSet = downloadTask.getDataSet();
        dataLayer.getOsmDataLayer().mergeFrom(dataSet);

        BuiltEnvironmentEntityBuilder builder = new BuiltEnvironmentEntityBuilder(
                dataLayer);
        builder.setEntityFactory(entityFactory);
        try {
            builder.build();
            newEntities = builder.getNewEntities();
            analyze();
        } catch (BuildException e) {
            Collection<Issue> issues = e.getIssues();
            StringBuilder sb = new StringBuilder(1000);
            sb.append(I18n.trn("An object could not be built:",
                    "{0} objects could not be built:", issues.size(),
                    issues.size()));
            for (Issue issue : e.getIssues()) {
                sb.append("\n").append(issue.getMessage());
            }
            JOptionPane.showMessageDialog(Main.parent, sb.toString());
        }
    }
    
    private void analyze() {
        for (Analyzer analyzer : analyzers) {
            analyzer.analyze(dataLayer, newEntities);
        }        
    }
}
