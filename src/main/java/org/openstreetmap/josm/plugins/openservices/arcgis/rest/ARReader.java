package org.openstreetmap.josm.plugins.openservices.arcgis.rest;

import java.io.IOException;
import java.net.MalformedURLException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openstreetmap.josm.gui.progress.ProgressMonitor;

public class ARReader {
  private final RestQuery query;
  private final JSONParser jsonParser = new JSONParser();
  private boolean cancel;
  private ArcgisRestLayer layer;

  public ARReader(RestQuery query) {
    super();
    this.query = query;
  }
  
  public ArcgisRestLayer getLayer() {
    return layer;
  }
  
  public JSONObject getJson(ProgressMonitor progressMonitor) throws ArcgisServerRestException {
    try {
      ArcgisRestHost host = ArcgisRestHost.getHost(query.getHost());
      ArcgisRestService service = host.getService(query.getService());
      if (service == null) {
        throw new ArcgisServerRestException("Service '" + query.getService() + "' not found");
      }
      layer = service.getLayer(query.getLayer());
      if (layer == null) {
        throw new ArcgisServerRestException("Layer '" + query.getLayer() + "' not found");
      }
      String url = layer.getBaseUrl() + "/query";
      JSONHttpRequest request = new JSONHttpRequest();
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
      request.send();
      return request.getJson();
    } catch (MalformedURLException e) {
      throw new ArcgisServerRestException(e);
    } catch (IOException e) {
      throw new ArcgisServerRestException(e);
    }
  }
}
