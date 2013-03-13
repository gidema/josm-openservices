package org.openstreetmap.josm.plugins.openservices.arcgis.rest;

import org.geotools.feature.FeatureCollection;
import org.json.simple.JSONObject;
import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.plugins.openservices.OdsDownloadTask;
import org.openstreetmap.josm.plugins.openservices.OdsDataSource;
import org.openstreetmap.josm.plugins.openservices.Service;
import org.openstreetmap.josm.plugins.openservices.ServiceException;

public class AGRestDownloadTask extends OdsDownloadTask {

  public AGRestDownloadTask(Service service, OdsDataSource dataSource) {
    super(service, dataSource);
  }

  @Override
  protected FeatureCollection<?, SimpleFeature> getFeatures()
      throws ServiceException {
    AGRestService agService = (AGRestService) service;
    service.init();
    RestQuery query = getQuery();
    AGRestReader reader = new AGRestReader(query);
    JSONObject json = reader.getJson();
    AGRestFeatureParser featureParser = new AGRestFeatureParser(agService);
    return featureParser.parse(json);

  }

  private RestQuery getQuery() {
    RestQuery query = new RestQuery();  
    query.setService((AGRestService)service);
    query.setInSR(4326L);
    query.setGeometry(formatBounds(currentBounds));
    query.setOutFields("*");
    return query;
  }

  private static String formatBounds(Bounds bounds) {
    return String.format("%f,%f,%f,%f",
        bounds.getMin().getX(), bounds.getMin().getY(),
        bounds.getMax().getX(), bounds.getMax().getY());
  }
  
  

}
