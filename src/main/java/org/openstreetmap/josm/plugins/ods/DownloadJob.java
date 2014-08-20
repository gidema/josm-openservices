package org.openstreetmap.josm.plugins.ods;

import java.util.List;
import java.util.concurrent.Callable;

import org.openstreetmap.josm.plugins.ods.entities.BuildException;

public interface DownloadJob {

//    void setup();

    List<Callable<?>> getPrepareCallables();

    List<Callable<?>> getDownloadCallables();
    
    List<? extends DownloadTask> getDownloadTasks();

    void build() throws BuildException;
    // OdsFeatureSet getFeatureSet();
    // List<Entity> getNewEntities();

}
