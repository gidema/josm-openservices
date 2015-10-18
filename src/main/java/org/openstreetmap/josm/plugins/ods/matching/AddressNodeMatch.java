package org.openstreetmap.josm.plugins.ods.matching;

import org.openstreetmap.josm.plugins.ods.entities.actual.AddressNode;

public class AddressNodeMatch {
    Object id;
    AddressNode osmAddressNode;
    AddressNode odAddressNode;

    public AddressNodeMatch(AddressNode osmAddressNode, AddressNode gtAddressNode) {
        super();
        this.osmAddressNode = osmAddressNode;
        this.odAddressNode = gtAddressNode;
        this.id = osmAddressNode.getReferenceId();
    }

    public Object getId() {
        return id;
    }

    public AddressNode getOsmAddressNode() {
        return osmAddressNode;
    }

    public AddressNode getGtAddressNode() {
        return odAddressNode;
    }
}