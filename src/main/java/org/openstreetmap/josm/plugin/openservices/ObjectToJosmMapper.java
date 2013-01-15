package org.openstreetmap.josm.plugin.openservices;



/**
 * Create Josm primitives from java objects and add them to
 * the supplied Josm DataSet 
 * 
 * @author Gertjan Idema
 *
 */
public interface ObjectToJosmMapper<T> {
  public void create(T o);
}
