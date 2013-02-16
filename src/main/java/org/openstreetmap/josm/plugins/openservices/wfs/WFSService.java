package org.openstreetmap.josm.plugins.openservices.wfs;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import org.geotools.data.FeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.plugins.openservices.BBoxUtil;
import org.openstreetmap.josm.plugins.openservices.Host;
import org.openstreetmap.josm.plugins.openservices.Service;
import org.openstreetmap.josm.plugins.openservices.ServiceException;

public class WFSService implements Service {
  final static String type = "WFS";
  private boolean initialized = false;
  WFSHost host;
  String feature;
  FeatureSource<?, ?> featureSource;
  CoordinateReferenceSystem crs;


  @Override
  public void setHost(Host host) {
    this.host = (WFSHost) host;
  }
  
  @Override
  public void setFeature(String feature) {
    this.feature = host.getName() + ":" + feature;
  }

  @Override
  public void init() throws ServiceException {
    if (initialized) return;
    initialize();
    initialized = true;
  }
  
  private void initialize() throws ServiceException {
    if (!host.hasFeatureType(feature)) {
      throw new WfsException(String.format("Unknown feature type: '%s'", feature));
    }
    try {
      featureSource = host.getDataStore().getFeatureSource(feature);
    } catch (IOException e) {
      throw new ServiceException(e);
    }
  }
  
  FeatureSource<?, ?> getFeatureSource() {
    return featureSource;
  }
  
  @Override
  public FeatureType getFeatureType() {
    return getFeatureSource().getSchema();
  }

  @Override
  public CoordinateReferenceSystem getCrs() {
    if (crs == null) {
      crs = featureSource.getInfo().getCRS();
    }
    return crs;
  }
  
  @Override
  public String getSRS() {
    ReferenceIdentifier rid = crs.getIdentifiers().iterator().next();
    return rid.toString();
  }
  
  @Override
  public Long getSRID() {
    ReferenceIdentifier rid = crs.getIdentifiers().iterator().next();
    return Long.parseLong(rid.getCode());
  }
  
  @Override
  public FutureTask<FeatureCollection<?,?>> getDownloadTask(Bounds bounds) {
    return new FutureTask<FeatureCollection<?, ?>>(new WFSDownloadTask(bounds));
  }

  class WFSDownloadTask implements Callable<FeatureCollection<?, ?>> {
    private FeatureCollection<?, ?> features;
    private final Bounds bounds;
    
    public WFSDownloadTask(Bounds bounds) {
      this.bounds = bounds;
    }

    @Override
    public FeatureCollection<?, ?> call() throws Exception {
      init();
//      DataStore dataStore = host.getDataStore();
      FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
      // Find faster solution for the following line
      ReferencedEnvelope bbox = BBoxUtil.createBoundingBox(getCrs(), bounds);
      Filter filter = ff.bbox(ff.property(""), bbox);
      features = getFeatureSource().getFeatures(filter);
      return features;
    }
  }
}
