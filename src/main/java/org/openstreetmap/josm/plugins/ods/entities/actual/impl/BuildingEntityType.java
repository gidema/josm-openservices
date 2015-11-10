package org.openstreetmap.josm.plugins.ods.entities.actual.impl;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.plugins.ods.entities.EntityType;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;

public class BuildingEntityType implements EntityType<Building> {
    private final static BuildingEntityType INSTANCE = new BuildingEntityType();
    
    private BuildingEntityType() {
        // Hide default constructor
    }
    
    public static BuildingEntityType getInstance() {
        return INSTANCE;
    }
    
    @Override
    public boolean recognize(OsmPrimitive primitive) {
        return ((primitive.hasKey("building") || primitive.hasKey("building:part")) &&
                (primitive.getDisplayType() == OsmPrimitiveType.CLOSEDWAY
                || primitive.getDisplayType() == OsmPrimitiveType.MULTIPOLYGON 
                || primitive.getDisplayType() == OsmPrimitiveType.RELATION));
    }

    @Override
    public Class<Building> getEntityClass() {
        return Building.class;
    }
}
