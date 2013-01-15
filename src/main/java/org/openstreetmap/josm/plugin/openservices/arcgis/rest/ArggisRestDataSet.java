package org.openstreetmap.josm.plugin.openservices.arcgis.rest;

import java.util.Iterator;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.openstreetmap.josm.plugin.openservices.CustomDataSet;

public abstract class ArggisRestDataSet<T> extends CustomDataSet<T> {
  private final ArcgisJsonParser<T> jsonParser;

  public ArggisRestDataSet(ArcgisJsonParser<T> jsonParser) {
    super();
    this.jsonParser = jsonParser;
  }
  
  /**
   * Add Json features to the dataSet
   * @param layer 
   * @param features
   */
  public void addFeatures(ArcgisRestLayer layer, JSONArray features) {
    @SuppressWarnings("unchecked")
    Iterator<JSONObject> i = features.iterator();
    while (i.hasNext()) {
      JSONObject feature = i.next();
      add(jsonParser.parse(layer, feature));
    }
  }
}
