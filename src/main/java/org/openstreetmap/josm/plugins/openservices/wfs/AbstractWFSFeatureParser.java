package org.openstreetmap.josm.plugins.openservices.wfs;

import java.util.Date;

import org.opengis.feature.simple.SimpleFeature;

/**
 * Abstract WFSFeatureParser provides helper methods for object conversion
 * 
 * @author Gertjan Idema
 *
 */
public abstract class AbstractWFSFeatureParser implements WFSFeatureParser {

  static protected String parseString(SimpleFeature feature, String attribute) {
    return (String) feature.getAttribute(attribute);
  }
  
  static protected Boolean parseBoolean(SimpleFeature feature, String attribute) {
    return (Boolean) feature.getAttribute(attribute);
  }
  
  static protected Integer parseInt(SimpleFeature feature, String attribute) {
    return (Integer) feature.getAttribute(attribute);
  }

  static protected Double parseDouble(SimpleFeature feature, String attribute) {
    return (Double) feature.getAttribute(attribute);
  }
  
  static protected Date parseDate(SimpleFeature feature, String attribute) {
    return (Date) feature.getAttribute(attribute);
  }
  
  static protected Long parseNumberAsLong(SimpleFeature feature, String attribute) {
    Double value = (Double) feature.getAttribute(attribute);
    return value == null ? null : value.longValue();
  }
  
  static protected Integer parseNumberAsInt(SimpleFeature feature, String attribute) {
    Double value = (Double) feature.getAttribute(attribute);
    return value == null ? null : value.intValue();
  }

}
