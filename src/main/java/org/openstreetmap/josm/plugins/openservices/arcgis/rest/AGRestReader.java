package org.openstreetmap.josm.plugins.openservices.arcgis.rest;

import java.io.IOException;
import java.net.MalformedURLException;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.openstreetmap.josm.plugins.openservices.arcgis.rest.json.FeatureCollectionParser;

public class AGRestReader {
  private final RestQuery query;
  private final FeatureCollectionParser parser;

  public AGRestReader(RestQuery query, FeatureType featureType) {
    super();
    this.query = query;
    this.parser = new FeatureCollectionParser((SimpleFeatureType) featureType);
  }
  
  public SimpleFeatureCollection getFeatures() throws ArcgisServerRestException {
    try {
      AGRestFeatureSource featureSource = query.getFeatureSource();
      String url = String.format("%s/%d/query", featureSource.getHost().getUrl(), featureSource.getFeatureId());
      HttpRequest request = new HttpRequest();
      request.open("GET", url);
      request.addParameter("f", "json");
      request.addParameter("text", query.getText());
      request.addParameter("geometry", query.getGeometry());
      request.addParameter("geometryType", query.getGeometryType().toString());
      request.addParameter("inSR", query.getInSR().toString());
      request.addParameter("spatialRef", query.getSpatialRel().toString());
      request.addParameter("where", query.getWhere());
      request.addParameter("outfields", query.getOutFields());
      request.addParameter("returnGeometry", query.getReturnGeometry().toString());
      if (query.getOutSR() != null) {
        request.addParameter("outSR", query.getOutSR().toString());
      }
      HttpResponse response = request.send();
      SimpleFeatureCollection features = parser.parse(response.getInputStream());
      response.close();
      return features;
    } catch (MalformedURLException e) {
      throw new ArcgisServerRestException(e);
    } catch (IOException e) {
      throw new ArcgisServerRestException(e);
    }
  }
}
