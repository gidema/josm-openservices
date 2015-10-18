package org.openstreetmap.josm.plugins.ods.entities.actual.impl;

import java.util.ArrayList;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.entities.actual.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;

import com.vividsolutions.jts.geom.Point;

public class AddressNodeGroup {
    private Point geometry;
    private List<AddressNode> addressNodes = new ArrayList<AddressNode>();
    private Building building;
    
    public AddressNodeGroup(AddressNode addressNode) {
        geometry = addressNode.getGeometry();
        addressNodes.add(addressNode);
        building = addressNode.getBuilding();
    }
    
    public void addAddressNode(AddressNode node) {
        addressNodes.add(node);
    }
    
    public List<AddressNode> getAddressNodes() {
        return addressNodes;
    }

    public Point getGeometry() {
        return geometry;
    }
    
    public Building getBuilding() {
        return building;
    }
}
