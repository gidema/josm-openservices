package org.openstreetmap.josm.plugins.ods.entities.actual;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.plugins.ods.entities.Entity;

import com.vividsolutions.jts.geom.MultiPolygon;

public interface City extends Entity {
    public static boolean isCity(OsmPrimitive primitive) {
        boolean validTagging = "administrative".equals(primitive.get("boundary"))
                && "10".equals(primitive.get("admin_level"));
        boolean validGeometry = primitive.getType().equals(OsmPrimitiveType.RELATION)
                || primitive.getType().equals(OsmPrimitiveType.CLOSEDWAY);
        return validTagging && validGeometry;
    }

    String TYPE = "ods:city";

    public String getName();

    @Override
    public MultiPolygon getGeometry();
}
