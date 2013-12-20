package org.openstreetmap.josm.plugins.ods.entities.internal;

import java.util.Iterator;
import java.util.Map.Entry;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Address;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.City;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Street;

public class InternalAddress implements Address {
    private OsmPrimitive primitive;
    private String houseNumber;
    private String houseName;
    private String streetName;
    private String placeName;
    private String postcode;
    
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
                houseNumber = value;
            }
            else if ("addr:street".equals(key)) {
                streetName = value;
            }
            else if ("addr:housename".equals(key)) {
                houseName = value;
            }
            else if ("addr:city".equals(key)) {
                placeName = value;
            }
            else if ("addr:postcode".equals(key)) {
                postcode = normalizePostcode(value);
                if (!postcode.equals(value)) {
                    primitive.put("addr:postcode", postcode);
                }
            }
        }
    }
    
    @Override
    public City getCity() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getStreetName() {
        return streetName;
    }

    @Override
    public Street getStreet() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getPostcode() {
        return postcode;
    }

    @Override
    public String getHouseNumber() {
        return houseNumber;
    }

    @Override
    public String getHouseName() {
       return houseName;
    }

    @Override
    public String getPlaceName() {
        return placeName;
    }

    private String normalizePostcode(String postcode) {
        return postcode.replace(" ", "");
    }

    @Override
    public void setStreet(Street street) {
        // TODO Auto-generated method stub   
    }
}