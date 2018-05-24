package org.openstreetmap.josm.plugins.ods.entities.actual;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.plugins.ods.entities.Entity;

import com.vividsolutions.jts.geom.Point;

public interface AddressNode extends Entity, Address {
    public static boolean IsAddressNode(OsmPrimitive primitive) {
        boolean validTagging = primitive.hasKey("addr:housenumber");
        boolean validGeometry = primitive.getDisplayType() == OsmPrimitiveType.NODE;
        return validTagging && validGeometry;
    }

    public Address getAddress();

    public Object getBuildingRef();

    public void setBuilding(Building building);

    public Building getBuilding();

    public void setGeometry(Point point);

    @Override
    public Point getGeometry();
}
