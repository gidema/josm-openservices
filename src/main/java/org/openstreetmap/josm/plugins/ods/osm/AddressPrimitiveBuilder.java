package org.openstreetmap.josm.plugins.ods.osm;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.builtenvironment.Address;

public class AddressPrimitiveBuilder {
    public static void buildTags(Address address, OsmPrimitive primitive) {
        primitive.put("addr:housenumber", address.getHouseNumber());
        primitive.put("addr:street", address.getStreetName());
        primitive.put("addr:postcode", address.getPostcode());
        primitive.put("addr:city", address.getCityName());
    }
}
