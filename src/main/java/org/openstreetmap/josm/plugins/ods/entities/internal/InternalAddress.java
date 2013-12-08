package org.openstreetmap.josm.plugins.ods.entities.internal;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map.Entry;

import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.crs.GeoUtil;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Address;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Block;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Building;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.City;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Street;

import com.vividsolutions.jts.geom.Point;

public class InternalAddress extends InternalEntity implements Address {
    private String houseNumber;
    private String houseName;
    private String streetName;
    private String placeName;
    private String postcode;
    private String source = null;
    private String sourceDate;
    
    public InternalAddress(OsmPrimitive primitive) {
        super(primitive);
    }
    
    @Override
    public String getType() {
        return Address.TYPE;
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
                    getPrimitive().put("addr:postcode", postcode);
                }
            }
            else if ("source".equals(key) && key.toUpperCase().startsWith("BAG")) {
                source = "BAG";
            }
            else if ("bag:extract".equals(key)) {
                sourceDate = parseBagExtract(value);
            }
        }
    }
    
    @Override
    public String getName() {
        return null;
    }

    @Override
    public City getCity() {
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
        GeoUtil geoUtil = GeoUtil.getInstance();
        return geoUtil.toPoint((Node)getPrimitive());
    }

    @Override
    public String getPlaceName() {
        return placeName;
    }

    private String normalizePostcode(String postcode) {
        return postcode.replace(" ", "");
    }
    
    public String getSource() {
        return source;
    }
    
    public String getSourceDate() {
        return sourceDate;
    }

    private String parseBagExtract(String s) {
        if (s.startsWith("9999PND") || s.startsWith("9999LIG") || s.startsWith("9999STA")) {
            StringBuilder sb = new StringBuilder(10);
            sb.append(s.substring(11,15)).append("-").append(s.substring(9,11)).append("-").append(s.substring(7, 9));
            return sb.toString();
        }
        return s;
    }
}
