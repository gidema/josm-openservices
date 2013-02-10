package org.openstreetmap.josm.plugins.openservices;

import org.opengis.feature.simple.SimpleFeature;



/**
 * Create Josm primitives from java objects and add them to
 * the supplied Josm DataSet 
 * 
 * @author Gertjan Idema
 *
 */
public interface ObjectToJosmMapper {
  public void create(SimpleFeature feature);
  public void create(Object o);
  public void create(SimpleFeature feature, JosmObjectFactory objectFactory);
}
