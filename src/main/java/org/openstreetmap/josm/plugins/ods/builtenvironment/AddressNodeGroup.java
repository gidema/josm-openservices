package org.openstreetmap.josm.plugins.ods.builtenvironment;

import java.util.ArrayList;
import java.util.List;

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
