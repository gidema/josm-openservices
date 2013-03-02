package org.openstreetmap.josm.plugins.openservices;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.openstreetmap.josm.data.osm.OsmPrimitive;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Default GeometryMapper implementation.
 * 
 * @author Gertjan Idema
 *
 */
public class DefaultGeometryMapper implements GeometryMapper {
  private JosmObjectFactory objectFactory;
  private String targetPrimitive;
  private final Boolean merge = false;
  
  @Override
  public final void setObjectFactory(JosmObjectFactory objectFactory) {
    this.objectFactory = objectFactory;
  }

  public final void setTargetPrimitive(String targetPrimitive) {
    this.targetPrimitive = targetPrimitive;
  }

  @Override
  public List<OsmPrimitive> createPrimitives(Geometry geometry,
      Map<String, String> tags) {
    if (geometry instanceof GeometryCollection) {
      return createPrimitives((GeometryCollection)geometry, tags);
    }
    return Collections.singletonList(createPrimitive(geometry, tags));
  }
  
  protected OsmPrimitive createPrimitive(Geometry geometry,
      Map<String, String> tags) {
    OsmPrimitive primitive = null;
    if (targetPrimitive.equals("WAY")) {
      if (geometry instanceof LineString) {
        primitive = objectFactory.buildWay((LineString)geometry);
      }
      else if (geometry instanceof Polygon) {
        Polygon polygon = (Polygon) geometry;
        if (polygon.getNumInteriorRing() == 0) {
          primitive = objectFactory.buildWay(polygon);
        }
        else {
          primitive = objectFactory.buildMultiPolygon(polygon);
        }
      }
    } else if (targetPrimitive.equals("NODE")) {
      primitive = objectFactory.buildNode((Point)geometry, merge);
    } else if (targetPrimitive.equals("POLYGON")) {
      primitive = objectFactory.buildPolygon((Polygon)geometry);
    }
    if (primitive != null) {
      for (Entry<String, String> entry : tags.entrySet()) {
        primitive.put(entry.getKey(), entry.getValue());
      }
    }
    return primitive;
  }

  private List<OsmPrimitive> createPrimitives(GeometryCollection gc,
    Map<String, String> tags) {
    List<OsmPrimitive> primitives = new ArrayList<OsmPrimitive>(gc.getNumGeometries());
    for (int i = 0; i < gc.getNumGeometries(); i++) {
      primitives.add(createPrimitive(gc.getGeometryN(i), tags));
    }
    return primitives;
  }

  public void setMerge(Boolean merge) {
    // TODO Auto-generated method stub
    
  }

}
