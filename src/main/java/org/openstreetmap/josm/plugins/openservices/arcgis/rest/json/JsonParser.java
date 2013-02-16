package org.openstreetmap.josm.plugins.openservices.arcgis.rest.json;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.openstreetmap.josm.plugins.openservices.arcgis.rest.AGRestHost;

public class JsonParser {
  public static void parseHostJson(AGRestHost host, JSONObject json) {
    String serviceDescription = (String) json.get("serviceDescription");
    String description = (String) json.get("description");
    String copyrightText = (String) json.get("copyrightText");
    JSONArray layersJson = (JSONArray) json.get("layers");
    List<String> featureTypes = new ArrayList<String>(layersJson.size());
    for (int i = 0; i < layersJson.size(); i++) {
      JSONObject layerJson = (JSONObject) layersJson.get(i);
      Long id = (Long) layerJson.get("id");
      String name = (String) layerJson.get("name");
      String feature = name + "/" + id;
      featureTypes.set(i, feature);
    }
    host.setFeatureTypes(featureTypes);
  }
}
