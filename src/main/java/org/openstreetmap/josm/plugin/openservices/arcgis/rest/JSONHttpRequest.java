package org.openstreetmap.josm.plugin.openservices.arcgis.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JSONHttpRequest extends HttpRequest {
  private JSONObject json;
  final static JSONParser jsonParser = new JSONParser();
  
  @Override
  protected void processResult(InputStream is) throws IOException {
    Reader reader = new InputStreamReader(is);
    try {
      json = (JSONObject) jsonParser.parse(reader);
    } catch (ParseException e) {
      throw new IOException("Could not parse the JSON result", e);
    }
  }
  
  public JSONObject getJson() {
    return json;
  }

}
