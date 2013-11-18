package org.openstreetmap.josm.plugins.openservices.entities.josm;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map.Entry;

import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.openservices.crs.CRSUtil;
import org.openstreetmap.josm.plugins.openservices.entities.builtenvironment.Address;
import org.openstreetmap.josm.plugins.openservices.entities.builtenvironment.Block;
import org.openstreetmap.josm.plugins.openservices.entities.builtenvironment.Building;
import org.openstreetmap.josm.plugins.openservices.entities.builtenvironment.Place;
import org.openstreetmap.josm.plugins.openservices.entities.builtenvironment.Street;

import com.vividsolutions.jts.geom.Point;

public class JosmAddress extends JosmEntity implements Address {
    private String houseNumber;
    private String houseName;
    private String streetName;
    private String placeName;
    private String postcode;
    private boolean bag = false;
    
    public JosmAddress(Node node) {
        super(node);
    }

    private void parseKeys() {
        Iterator<Entry<String, String>> it =
            getPrimitive().getKeys().entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, String> entry = it.next();
            String key = entry.getKey();
            if ("address:housenumber".equals(key)) {
                houseNumber = entry.getValue();
            }
            else if ("address:street".equals(key)) {
                streetName = entry.getValue();
            }
            else if ("address:housename".equals(key)) {
                houseName = entry.getValue();
            }
            else if ("address:city".equals(key)) {
                placeName = entry.getValue();
            }
            else if ("address:postcode".equals(key)) {
                postcode = normalizePostcode(entry.getValue());
                if (!postcode.equals(entry.getValue())) {
                    getPrimitive().put("address:postcode", postcode);
                }
            }
            else if ("source".equals(key) && key.toUpperCase().startsWith("BAG")) {
                bag = true;
            }
        }
    }
    
    @Override
    public String getName() {
        return null;
    }

    @Override
    public Place getPlace() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Block getBlock() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Serializable getBuildingRef() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Building getBuilding() {
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
    public Point getGeometry() {
        return CRSUtil.toPoint((Node)getPrimitive());
    }

    @Override
    public String getPlaceName() {
        return placeName;
    }

    private String normalizePostcode(String postcode) {
        return postcode.replace(" ", "");
    }
}
