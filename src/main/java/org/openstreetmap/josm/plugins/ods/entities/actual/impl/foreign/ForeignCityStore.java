package org.openstreetmap.josm.plugins.ods.entities.actual.impl.foreign;

import org.openstreetmap.josm.plugins.ods.entities.EntityStore;
import org.openstreetmap.josm.plugins.ods.entities.GeoIndex;
import org.openstreetmap.josm.plugins.ods.entities.GeoIndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.IndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.UniqueIndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.actual.City;

public class ForeignCityStore extends EntityStore<City> {
    private UniqueIndexImpl<City> primaryIndex = new UniqueIndexImpl<>(City.class, "referenceId");
    private IndexImpl<City> idIndex = new IndexImpl<>(City.class, "referenceId");
    private GeoIndex<City> geoIndex = new GeoIndexImpl<>(City.class, "geometry");

    public ForeignCityStore() {
        super();
        getIndexes().add(idIndex);
        getIndexes().add(geoIndex);
    }

    @Override
    public IndexImpl<City> getIdIndex() {
        return idIndex;
    }

    @Override
    public UniqueIndexImpl<City> getPrimaryIndex() {
        return primaryIndex;
    }


    @Override
    public GeoIndex<City> getGeoIndex() {
        return geoIndex;
    } 
}
