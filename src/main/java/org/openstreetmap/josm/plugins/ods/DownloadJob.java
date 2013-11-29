package org.openstreetmap.josm.plugins.ods;

import java.util.Set;
import java.util.concurrent.Callable;

import org.openstreetmap.josm.plugins.ods.entities.Entity;

public interface DownloadJob {

    Callable<?> getPrepareCallable();

    Callable<?> getDownloadCallable();

    // OdsFeatureSet getFeatureSet();

    Set<Entity> getNewEntities();
}
