package org.openstreetmap.josm.plugins.openservices.arcgis.rest.test;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.geotools.kml.v22.KML;
import org.geotools.kml.v22.KMLConfiguration;
import org.geotools.xml.StreamingParser;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.xml.sax.SAXException;

public class ArcgisKMLTest {
  
  @Test
  public void testParseKML() throws IOException, SAXException, ParserConfigurationException {
    InputStream inputStream = getClass().getResourceAsStream("/spoor.kml");
    StreamingParser parser = new StreamingParser(new KMLConfiguration(), inputStream, KML.Placemark);
    SimpleFeature f = null;

    while ((f = (SimpleFeature) parser.parse()) != null) {
      f.getAttribute(0);
    }
  }
}
