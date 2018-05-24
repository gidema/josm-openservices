package org.openstreetmap.josm.plugins.ods.entities.actual.impl;

import org.openstreetmap.josm.plugins.ods.entities.EntityType;
import org.openstreetmap.josm.plugins.ods.entities.actual.City;

public class CityEntityType implements EntityType<City> {
    private final static CityEntityType INSTANCE = new CityEntityType();

    private CityEntityType() {
        // Hide default constructor
    }

    public static CityEntityType getInstance() {
        return INSTANCE;
    }
    //
    //    @Override
    //    public boolean recognize(OsmPrimitive primitive) {
    //        // TODO This code is specific for The Netherlands.
    //        return (primitive.getType().equals(OsmPrimitiveType.RELATION)
    //            &&  "administrative".equals(primitive.get("boundary"))
    //            && "10".equals(primitive.get("admin_level")));
    //    }

    //    @Override
    //    public Class<City> getEntityClass() {
    //        return City.class;
    //    }
}
