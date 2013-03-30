package org.openstreetmap.josm.plugins.openservices.arcgis.rest;

import java.util.Locale;

import org.geotools.feature.FeatureCollection;
import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.plugins.openservices.OdsDataSource;
import org.openstreetmap.josm.plugins.openservices.OdsDownloadTask;
import org.openstreetmap.josm.plugins.openservices.ServiceException;
import org.openstreetmap.josm.plugins.openservices.crs.JTSCoordinateTransform;
import org.openstreetmap.josm.plugins.openservices.crs.JTSCoordinateTransformFactory;
import org.openstreetmap.josm.plugins.openservices.crs.Proj4jCRSTransformFactory;

import com.vividsolutions.jts.geom.Coordinate;

public class AGRestDownloadTask extends OdsDownloadTask {

  public AGRestDownloadTask(OdsDataSource dataSource) {
    super(dataSource);
  }

  @Override
  protected FeatureCollection<?, SimpleFeature> getFeatures()
      throws ServiceException {
    service.init();
    RestQuery query = getQuery();
    AGRestReader reader = new AGRestReader(query, service.getFeatureType());
    return reader.getFeatures();
  }

  private RestQuery getQuery() {
    RestQuery query = new RestQuery();  
    query.setService((AGRestService)service);
    query.setInSR(service.getSRID());
    query.setOutSR(service.getSRID());
    query.setGeometry(formatBounds(currentBounds, query.getInSR()));
    query.setOutFields("*");
    return query;
  }

  private static String formatBounds(Bounds bounds, Long srid) {
    LatLon min = bounds.getMin();
    LatLon max = bounds.getMax();
    if (!srid.equals(4326L)) {
      Coordinate minCoord = new Coordinate(min.lon(), min.lat());
      Coordinate maxCoord = new Coordinate(max.lon(), max.lat());
      JTSCoordinateTransformFactory f = new Proj4jCRSTransformFactory();
      JTSCoordinateTransform t = f.createJTSCoordinateTransform(4326L, srid);
      minCoord = t.transform(minCoord);
      maxCoord = t.transform(maxCoord);
      min = new LatLon(minCoord.y, minCoord.x);
      max = new LatLon(maxCoord.y, maxCoord.x);
    }
    return String.format(Locale.ENGLISH, "%f,%f,%f,%f",
        min.getX(), min.getY(),
        max.getX(), max.getY());
  }
  
  

}
