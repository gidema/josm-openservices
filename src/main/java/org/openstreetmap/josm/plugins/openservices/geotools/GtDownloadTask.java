package org.openstreetmap.josm.plugins.openservices.geotools;

import java.io.IOException;

import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.openstreetmap.josm.plugins.openservices.BBoxUtil;
import org.openstreetmap.josm.plugins.openservices.OdsDataSource;
import org.openstreetmap.josm.plugins.openservices.OdsDownloadTask;
import org.openstreetmap.josm.plugins.openservices.ServiceException;

public class GtDownloadTask extends OdsDownloadTask {
  public GtDownloadTask(GtService service, OdsDataSource dataSource) {
    super(service, dataSource);
  }

  @Override
  protected FeatureCollection getFeatures() throws ServiceException {
    FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
    ((GtService)service).init();
    // Find faster solution for the following line
    ReferencedEnvelope bbox = BBoxUtil.createBoundingBox(service.getCrs(), currentBounds);
    Filter bboxFilter = ff.bbox(ff.property(""), bbox);
    Filter dataFilter = dataSource.getFilter();
    Filter filter = bboxFilter;
    if (dataFilter != null) {
      filter = ff.and(filter, dataFilter);
    }
    try {
      return ((GtService)service).getFeatureSource().getFeatures(filter);
    } catch (IOException e) {
      throw new ServiceException(e);
    }
  }
}
