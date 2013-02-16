package org.openstreetmap.josm.plugins.openservices.arcgis.rest;

import java.io.IOException;
import java.net.MalformedURLException;

import org.json.simple.JSONObject;
import org.openstreetmap.josm.gui.progress.ProgressMonitor;

public class AGRestReader {
  private final RestQuery query;

  public AGRestReader(RestQuery query) {
    super();
    this.query = query;
  }
  
  public JSONObject getJson() throws ArcgisServerRestException {
    return getJson(null);
  }

  public JSONObject getJson(ProgressMonitor progressMonitor) throws ArcgisServerRestException {
    try {
      AGRestService service = query.getService();
      String url = String.format("%s/%d/query", service.getHost().getUrl(), service.getFeatureId());
      JSONHttpRequest request = new JSONHttpRequest();
      request.open("GET", url);
      request.addParameter("f", "json");
      request.addParameter("text", query.getText());
      request.addParameter("geometry", query.getGeometry());
      request.addParameter("geometryType", query.getGeometryType().toString());
      request.addParameter("inSR", "4326");
      request.addParameter("spatialRef", query.getSpatialRel().toString());
      request.addParameter("where", query.getWhere());
      request.addParameter("outfields", query.getOutFields());
      request.addParameter("returnGeometry", query.getReturnGeometry().toString());
      if (query.getOutSR() != null) {
        request.addParameter("outSR", query.getOutSR().toString());
      }
      request.send();
      return request.getJson();
    } catch (MalformedURLException e) {
      throw new ArcgisServerRestException(e);
    } catch (IOException e) {
      throw new ArcgisServerRestException(e);
    }
  }
}
