package org.openstreetmap.josm.plugins.openservices;

import java.util.concurrent.Callable;

public interface DownloadJob {

  Callable<?> getPrepareCallable();

  Callable<?> getDownloadCallable();

  //OdsFeatureSet getFeatureSet();

  Exception getException();
}
