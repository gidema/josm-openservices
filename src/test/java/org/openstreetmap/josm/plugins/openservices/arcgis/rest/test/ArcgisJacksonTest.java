package org.openstreetmap.josm.plugins.openservices.arcgis.rest.test;

import java.io.IOException;
import java.io.InputStream;

import org.geotools.feature.FeatureCollection;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeatureType;
import org.openstreetmap.josm.plugins.openservices.arcgis.rest.json.FeatureCollectionParser;
import org.openstreetmap.josm.plugins.openservices.arcgis.rest.json.FeatureTypeParser;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;

public class ArcgisJacksonTest {
  @Test
  public void testFeatureCollectionParser() throws JsonParseException, IOException {
    parseFeatureCollection();
  }
  
  private FeatureCollection<?, ?> parseFeatureCollection() throws JsonProcessingException, IOException {
    InputStream inputStream = getClass().getResourceAsStream("/testdata.json");
    SimpleFeatureType featureType = parseFeatureType();
    FeatureCollectionParser parser = new FeatureCollectionParser(featureType);
    FeatureCollection<?, ?> features = parser.parse(inputStream);
    inputStream.close();
    return features;
  }

  @Test
  public void testFeatureTypeParser() throws JsonProcessingException, IOException {
    parseFeatureType();
  }
  
  private SimpleFeatureType parseFeatureType() throws JsonProcessingException, IOException {
    FeatureTypeParser parser = new FeatureTypeParser();
    InputStream inputStream = getClass().getResourceAsStream("/featuretype.json");
    return parser.parse(inputStream, "prefix");
  }
}
