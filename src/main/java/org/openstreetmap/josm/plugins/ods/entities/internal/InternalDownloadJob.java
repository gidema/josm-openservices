package org.openstreetmap.josm.plugins.ods.entities.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.swing.JOptionPane;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.io.BoundingBoxDownloader;
import org.openstreetmap.josm.plugins.ods.DownloadJob;
import org.openstreetmap.josm.plugins.ods.DownloadTask;
import org.openstreetmap.josm.plugins.ods.OdsWorkingSet;
import org.openstreetmap.josm.plugins.ods.entities.BuildException;
import org.openstreetmap.josm.plugins.ods.issue.Issue;
import org.openstreetmap.josm.tools.I18n;

public class InternalDownloadJob implements DownloadJob {
    final OdsWorkingSet workingSet;
    final Bounds bounds;
    private List<InternalDownloadTask> downloadTasks;
    BoundingBoxDownloader bbDownloader;
    String overpassQuery;
    List<Exception> exceptions = new LinkedList<Exception>();

    public InternalDownloadJob(OdsWorkingSet workingSet, Bounds bounds) {
        super();
        this.workingSet = workingSet;
        this.bounds = bounds;
    }

    public void setup() {
        InternalDownloadTask downloadTask = new InternalDownloadTask(workingSet, bounds);
        downloadTasks = Collections.singletonList(downloadTask);
    }

    @Override
    public List<Callable<?>> getPrepareCallables() {
        List<Callable<?>> callables = new ArrayList<>(downloadTasks.size());
        for (InternalDownloadTask task : downloadTasks) {
            callables.add(task.getPrepareCallable());
        }
        return callables;
    }

    @Override
    public List<Callable<?>> getDownloadCallables() {
        List<Callable<?>> callables = new ArrayList<>(downloadTasks.size());
        for (InternalDownloadTask task : downloadTasks) {
            callables.add(task.getDownloadCallable());
        }
        return callables;
    }

    @Override
    public List<? extends DownloadTask> getDownloadTasks() {
        return downloadTasks;
    }

    @Override
    public void build() throws BuildException {
        BuiltEnvironmentEntityBuilder builder = new BuiltEnvironmentEntityBuilder(
                workingSet.internalDataLayer);
        try {
            builder.build();
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
}
