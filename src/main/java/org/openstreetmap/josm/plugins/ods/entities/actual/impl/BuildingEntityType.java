package org.openstreetmap.josm.plugins.ods.entities.actual.impl;

import java.util.function.Predicate;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.plugins.ods.entities.EntityType;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;

/**
 * TODO Rewrite this class and EntityType in general.
 * It is a very crappy solution.
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class BuildingEntityType implements EntityType<Building> {
    private final static BuildingEntityType INSTANCE = new BuildingEntityType();
    
    private BuildingEntityType() {
        // Hide default constructor
    }
    
    public static BuildingEntityType getInstance() {
        return INSTANCE;
    }
    
    public static boolean isBuilding(OsmPrimitive primitive) {
        return ((primitive.hasKey("building") || primitive.hasKey("building:part")) &&
                (primitive.getDisplayType() == OsmPrimitiveType.CLOSEDWAY
                || primitive.getDisplayType() == OsmPrimitiveType.MULTIPOLYGON 
                || primitive.getDisplayType() == OsmPrimitiveType.RELATION));
    }
    
    public final static Predicate<OsmPrimitive> IsBuilding = new Predicate<OsmPrimitive>() {
        @Override
        public boolean test(OsmPrimitive primitive) {
            return isBuilding(primitive);
        }
    };
    
    /**
     * Replace with Josm Predicate
     */
    @Override
    public boolean recognize(OsmPrimitive primitive) {
        return isBuilding(primitive);
    }

    @Override
    public Class<Building> getEntityClass() {
        return Building.class;
    }
}
