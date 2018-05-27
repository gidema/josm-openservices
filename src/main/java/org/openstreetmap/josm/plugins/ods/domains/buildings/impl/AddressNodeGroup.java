package org.openstreetmap.josm.plugins.ods.domains.buildings.impl;

import java.util.ArrayList;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuilding;

import com.vividsolutions.jts.geom.Point;

public class AddressNodeGroup {
    private final Point geometry;
    private final List<OdAddressNode> addressNodes = new ArrayList<>();
    private final OdBuilding building;

    public AddressNodeGroup(OdAddressNode addressNode) {
        geometry = addressNode.getGeometry();
        addressNodes.add(addressNode);
        building = addressNode.getBuilding();
    }

    public void addAddressNode(OdAddressNode node) {
        addressNodes.add(node);
    }

    public List<OdAddressNode> getAddressNodes() {
        return addressNodes;
    }

    public Point getGeometry() {
        return geometry;
    }

    public OdBuilding getBuilding() {
        return building;
    }
}
