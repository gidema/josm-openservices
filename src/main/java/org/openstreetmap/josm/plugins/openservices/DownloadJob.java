package org.openstreetmap.josm.plugins.openservices;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import org.openstreetmap.josm.plugins.openservices.entities.Entity;

public interface DownloadJob {

    Callable<?> getPrepareCallable();

    Callable<?> getDownloadCallable();

    // OdsFeatureSet getFeatureSet();

    List<Exception> getExceptions();

    Set<Entity> getNewEntities();
}
