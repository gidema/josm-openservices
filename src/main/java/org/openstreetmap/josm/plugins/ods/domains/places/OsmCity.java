package org.openstreetmap.josm.plugins.ods.domains.places;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;

import com.vividsolutions.jts.geom.MultiPolygon;

public interface OsmCity extends OsmEntity {
    public static boolean isCity(OsmPrimitive primitive) {
        boolean validTagging = "administrative".equals(primitive.get("boundary"))
                && "10".equals(primitive.get("admin_level"));
        boolean validGeometry = primitive.getType().equals(OsmPrimitiveType.RELATION)
                || primitive.getType().equals(OsmPrimitiveType.CLOSEDWAY);
        return validTagging && validGeometry;
    }

    public String getName();

    @Override
    public MultiPolygon getGeometry();
}
