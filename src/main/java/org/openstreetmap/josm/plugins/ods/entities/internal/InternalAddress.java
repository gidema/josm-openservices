package org.openstreetmap.josm.plugins.ods.entities.internal;

import java.util.Iterator;
import java.util.Map.Entry;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.AddressDataImpl;

public class InternalAddress extends AddressDataImpl {
    private OsmPrimitive primitive;
    
    public InternalAddress(OsmPrimitive primitive) {
        this.primitive = primitive;
    }
    
    public void build() {
        Iterator<Entry<String, String>> it =
            primitive.getKeys().entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, String> entry = it.next();
            String key = entry.getKey();
            String value = entry.getValue();
            if ("addr:housenumber".equals(key)) {
                setHouseNumber(value);
            }
            else if ("addr:street".equals(key)) {
                setStreetName(value);
            }
            else if ("addr:housename".equals(key)) {
                setHouseName(value);
            }
            else if ("addr:city".equals(key)) {
                setCityName(value);
            }
            else if ("addr:postcode".equals(key)) {
                setPostcode(normalizePostcode(value));
                if (!getPostcode().equals(value)) {
                    primitive.put("addr:postcode", getPostcode());
                }
            }
        }
    }
    
    private String normalizePostcode(String postcode) {
        return postcode.replace(" ", "");
    }
}
