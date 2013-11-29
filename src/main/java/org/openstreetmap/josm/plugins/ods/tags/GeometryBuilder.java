package org.openstreetmap.josm.plugins.ods.tags;

import java.util.List;
import java.util.Map;

import org.openstreetmap.josm.data.osm.OsmPrimitive;

import com.vividsolutions.jts.geom.Geometry;

/**
 * A GeometryBuilder creates a collection of OsmPrimitives from a
 * JTS geometry and a collection of tags;
 * 
 * @author Gertjan Idema
 *
 */
public interface GeometryBuilder {
  
  /**
   * Creates a collection of OsmPrimitives from a JTS geometry Object
   * JTS geometry and a collection of tags;
   * 
   * @param geometry
   * @param tags The tags represented as a Map of Strings
   * @return
   */
  public List<OsmPrimitive> createPrimitives(Geometry geometry,
      Map<String, String> tags);

}
