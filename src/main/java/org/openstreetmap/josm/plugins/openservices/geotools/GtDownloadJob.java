package org.openstreetmap.josm.plugins.openservices.geotools;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.plugins.openservices.BBoxUtil;
import org.openstreetmap.josm.plugins.openservices.DownloadJob;
import org.openstreetmap.josm.plugins.openservices.OdsFeatureSet;
import org.openstreetmap.josm.plugins.openservices.metadata.MetaData;

public class GtDownloadJob implements DownloadJob {
  GtDataSource dataSource;
  Bounds bounds;
  SimpleFeatureSource featureSource;
  SimpleFeatureCollection featureCollection;
  MetaData metaData;
//  OdsFeatureSet featureSet;
  Exception exception;
  
  
  protected GtDownloadJob(GtDataSource dataSource, Bounds bounds) {
    super();
    this.dataSource = dataSource;
    this.bounds = bounds;
    this.metaData = dataSource.getMetaData();
  }

  @Override
  public Callable<?> getPrepareCallable() {
    return new Callable<Object>() {

      @Override
      public Object call() {
        try {
          dataSource.initialize();
          metaData = dataSource.getMetaData();
          GtFeatureSource gtFeatureSource = (GtFeatureSource) dataSource.getOdsFeatureSource();
          // TODO check if selected boundaries overlap with featureSource boundaries; 
          FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
          //gtFeatureSource.initialize();
          String geometryProperty = gtFeatureSource.getFeatureType().getGeometryDescriptor().getLocalName();
          // TODO Find faster solution for the following line
          ReferencedEnvelope bbox = BBoxUtil.createBoundingBox(gtFeatureSource.getCrs(), bounds);
          Filter bboxFilter = ff.bbox(ff.property(geometryProperty), bbox);
          Filter dataFilter = dataSource.getFilter();
          Filter filter = bboxFilter;
//        if (dataFilter != null) {
//          filter = ff.and(filter, dataFilter);
//        }
          featureSource = gtFeatureSource.getFeatureSource();
          featureCollection = featureSource.getFeatures(filter);
        } 
        catch (Exception e) {
          exception = e;
        }
        return null;
      }
    };
  }
  
  @Override
  public Callable<?> getDownloadCallable() {
    return new Callable<Object>() {

      @Override
      public OdsFeatureSet call() {
        if (exception != null) return null;
        try {
          List<SimpleFeature> featureList = new LinkedList<SimpleFeature>();
          SimpleFeatureIterator it = featureCollection.features();
          while( !Thread.interrupted() && it.hasNext()) {
            featureList.add(it.next());
          }
          it.close();
          dataSource.addFeatures(featureList);
//          featureSet = new OdsFeatureSet(featureSource.getSchema(), featureList, metaData);
        } catch (Exception e) {
          exception = e;
        }
        return null;
      }
    };
  }

  @Override
  public Exception getException() {
    return exception;
  }
//  @Override
//  public OdsFeatureSet getFeatureSet() {
//    return featureSet;
//  }
}
