package org.openstreetmap.josm.plugins.ods.entities.actual.impl.osm;

import org.openstreetmap.josm.plugins.ods.entities.EntityStore;
import org.openstreetmap.josm.plugins.ods.entities.GeoIndex;
import org.openstreetmap.josm.plugins.ods.entities.GeoIndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.Index;
import org.openstreetmap.josm.plugins.ods.entities.UniqueIndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.actual.City;

public class OsmCityStore extends EntityStore<City> {
    private UniqueIndexImpl<City> primaryIndex = new UniqueIndexImpl<>(City.class, "primitiveId");
    private UniqueIndexImpl<City> idIndex = new UniqueIndexImpl<>(City.class, "referenceId");
    private GeoIndex<City> geoIndex = new GeoIndexImpl<>(City.class, "geometry");

    public OsmCityStore() {
        super();
        getIndexes().add(idIndex);
        getIndexes().add(geoIndex);
    }

    @Override
    public UniqueIndexImpl<City> getPrimaryIndex() {
        return primaryIndex;
    }

    
    @Override
    public Index<City> getIdIndex() {
        return idIndex;
    }

    @Override
    public GeoIndex<City> getGeoIndex() {
        return geoIndex;
    } 
}
