package org.openstreetmap.josm.plugins.openservices;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.OsmPrimitive;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPolygon;
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
      Map<String, String> tags, DataSet dataSet) {
    if (geometry instanceof GeometryCollection && !targetPrimitive.equals("MULTIPOLYGON")) {
      return createPrimitives((GeometryCollection)geometry, tags, dataSet);
    }
    return Collections.singletonList(createPrimitive(geometry, tags, dataSet));
  }
  
  protected OsmPrimitive createPrimitive(Geometry geometry,
      Map<String, String> tags, DataSet dataSet) {
    OsmPrimitive primitive = null;
    if (targetPrimitive.equals("WAY")) {
      if (geometry instanceof LineString) {
        primitive = objectFactory.buildWay((LineString)geometry, dataSet);
      }
      else if (geometry instanceof Polygon) {
        Polygon polygon = (Polygon) geometry;
        if (polygon.getNumInteriorRing() == 0) {
          primitive = objectFactory.buildWay(polygon, dataSet);
        }
        else {
          primitive = objectFactory.buildMultiPolygon(polygon, dataSet);
        }
      }
    } else if (targetPrimitive.equals("MULTIPOLYGON")) {
      if (geometry instanceof Polygon) {
        primitive = objectFactory.buildMultiPolygon((Polygon) geometry, dataSet);
      } else if (geometry instanceof MultiPolygon) {
        primitive = objectFactory.buildMultiPolygon((MultiPolygon) geometry, dataSet);
      }
    }
    else if (targetPrimitive.equals("NODE")) {
      primitive = objectFactory.buildNode((Point)geometry, dataSet, merge);
    } else if (targetPrimitive.equals("POLYGON")) {
      primitive = objectFactory.buildPolygon((Polygon)geometry, dataSet);
    }
    if (primitive != null) {
      for (Entry<String, String> entry : tags.entrySet()) {
        primitive.put(entry.getKey(), entry.getValue());
      }
    }
    return primitive;
  }

  private List<OsmPrimitive> createPrimitives(GeometryCollection gc,
    Map<String, String> tags, DataSet dataSet) {
    List<OsmPrimitive> primitives = new ArrayList<OsmPrimitive>(gc.getNumGeometries());
    for (int i = 0; i < gc.getNumGeometries(); i++) {
      primitives.add(createPrimitive(gc.getGeometryN(i), tags, dataSet));
    }
    return primitives;
  }
}
