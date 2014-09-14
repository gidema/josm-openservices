package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;

import org.openstreetmap.josm.plugins.ods.entities.EntityStore;
import org.openstreetmap.josm.plugins.ods.entities.GeoIndex;
import org.openstreetmap.josm.plugins.ods.entities.GeoIndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.UniqueIndexImpl;

public class OsmCityStore extends EntityStore<City> {
    private UniqueIndexImpl<City> idIndex = new UniqueIndexImpl<>(City.class, "referenceId");
    private GeoIndex<City> geoIndex = new GeoIndexImpl<>(City.class, "geometry");

    public OsmCityStore() {
        super();
        getIndexes().add(idIndex);
        getIndexes().add(geoIndex);
    }

    @Override
    public UniqueIndexImpl<City> getPrimaryIndex() {
        return idIndex;
    }


    @Override
    public GeoIndex<City> getGeoIndex() {
        return geoIndex;
    } 
}
