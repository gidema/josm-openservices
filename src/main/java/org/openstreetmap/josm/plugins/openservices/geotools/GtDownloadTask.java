package org.openstreetmap.josm.plugins.openservices.geotools;

import java.io.IOException;
import java.util.Collection;

import org.geotools.data.FeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.openstreetmap.josm.plugins.openservices.BBoxUtil;
import org.openstreetmap.josm.plugins.openservices.OdsDataSource;
import org.openstreetmap.josm.plugins.openservices.OdsDownloadTask;
import org.openstreetmap.josm.plugins.openservices.ServiceException;

public class GtDownloadTask extends OdsDownloadTask {
  public GtDownloadTask(OdsDataSource dataSource, Collection<SimpleFeature> featureCollection) {
    super(dataSource, featureCollection);
  }

  private GtService getService() {
    return (GtService)dataSource.getService();
  }
  
  @Override
  protected FeatureCollection getFeatures() throws ServiceException {
    // TODO check if selected boundaries overlap with featureSource boundaries; 
    FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
    getService().init();
    // TODO Find faster solution for the following line
    ReferencedEnvelope bbox = BBoxUtil.createBoundingBox(getService().getCrs(), currentBounds);
    Filter bboxFilter = ff.bbox(ff.property(""), bbox);
    Filter dataFilter = dataSource.getFilter();
    Filter filter = bboxFilter;
    if (dataFilter != null) {
      filter = ff.and(filter, dataFilter);
    }
    try {
      FeatureSource<?, SimpleFeature> featureSource = getService().getFeatureSource();
      return featureSource.getFeatures(filter);
    } catch (IOException e) {
      throw new ServiceException(e);
    }
  }
}
