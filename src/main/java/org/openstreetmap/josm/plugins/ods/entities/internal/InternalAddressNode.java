package org.openstreetmap.josm.plugins.ods.entities.internal;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map.Entry;

import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Address;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Block;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Building;
import org.openstreetmap.josm.plugins.ods.jts.GeoUtil;

import com.vividsolutions.jts.geom.Point;

public class InternalAddressNode extends InternalEntity implements AddressNode {
    private Address address;
    private String source = null;
    private String sourceDate;
    
    public InternalAddressNode(OsmPrimitive primitive) {
        super(primitive);
        address = new InternalAddress(primitive);
    }
    
    @Override
    public Address getAddress() {
        return address;
    }

    @Override
    public Class<? extends Entity> getType() {
        return AddressNode.class;
    }

    public void build() {
        Iterator<Entry<String, String>> it =
            primitive.getKeys().entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, String> entry = it.next();
            String key = entry.getKey();
            String value = entry.getValue();
            if ("addr:housenumber".equals(key) ||
                 "addr:street".equals(key) ||
                 "addr:housename".equals(key) ||
                 "addr:city".equals(key) ||
                 "addr:postcode".equals(key)) {
                // Ignore address related keys
                continue;
            }
            // TODO improve this (also move to Bag specific)
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
    public Point getGeometry() {
        GeoUtil geoUtil = GeoUtil.getInstance();
        return geoUtil.toPoint((Node)getPrimitive());
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
