package org.openstreetmap.josm.plugins.openservices.tags;

import java.util.List;
import java.util.Map;

import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.openservices.PrimitiveBuilder;

import com.vividsolutions.jts.geom.Geometry;

/**
 * A Geometry mapper maps a Geometry object to a list of OSM primitives.
 * In a lot of cases, the resulting list will contain only one object. 
 * When the provided Geometry is a GeometryCollection, the result will contain
 * multiple OSM primitives, all with the same tags.
 * 
 * @author Gertjan Idema
 *
 */
public interface GeometryMapper {
  
  /**
   * Map a geometry to a list of OSM primitives. 
   * @param geometry
   * @param tags
   * @param dataSet 
   * @return
   */
  public List<OsmPrimitive> createPrimitives(Geometry geometry, Map<String, String> tags, DataSet dataSet);

  // TODO remove this method and use a GeometryMapperFactory instead 
  public void setObjectFactory(PrimitiveBuilder primitiveBuilder);
}
