package org.openstreetmap.josm.plugins.ods.entities.actual.impl;

import org.openstreetmap.josm.plugins.ods.entities.EntityType;
import org.openstreetmap.josm.plugins.ods.entities.actual.AddressNode;

public class AddressNodeEntityType implements EntityType<AddressNode> {
    private final static AddressNodeEntityType INSTANCE = new AddressNodeEntityType();

    private AddressNodeEntityType() {
        // Hide default constructor
    }

    public static AddressNodeEntityType getInstance() {
        return INSTANCE;
    }
    //
    //    @Override
    //    public boolean recognize(OsmPrimitive primitive) {
    //        return (primitive.hasKey("addr:housenumber") &&
    //            (primitive.getDisplayType() == OsmPrimitiveType.NODE));
    //    }

    //    @Override
    //    public Class<AddressNode> getEntityClass() {
    //        return AddressNode.class;
    //    }

}
