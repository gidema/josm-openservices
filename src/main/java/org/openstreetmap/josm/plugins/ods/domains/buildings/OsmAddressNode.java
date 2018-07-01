package org.openstreetmap.josm.plugins.ods.domains.buildings;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.Address;
import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;
import org.openstreetmap.josm.plugins.ods.matching.AddressNodeMatch;

import com.vividsolutions.jts.geom.Point;

public interface OsmAddressNode extends OsmEntity, Address {
    public static boolean IsAddressNode(OsmPrimitive primitive) {
        boolean validTagging = primitive.hasKey("addr:housenumber");
        boolean validGeometry = primitive.getDisplayType() == OsmPrimitiveType.NODE;
        return validTagging && validGeometry;
    }

    public OsmAddress getAddress();

    public void setAddress(OsmAddress address);

    public void setBuilding(OsmBuilding building);

    public OsmBuilding getBuilding();

    public void setGeometry(Point point);

    @Override
    public Point getGeometry();

    @Override
    public AddressNodeMatch getMatch();

    public void setMatch(AddressNodeMatch match);
}
