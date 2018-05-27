package org.openstreetmap.josm.plugins.ods.domains.places.impl;

import org.openstreetmap.josm.plugins.ods.domains.places.OdCity;
import org.openstreetmap.josm.plugins.ods.entities.EntityStore;
import org.openstreetmap.josm.plugins.ods.entities.GeoIndex;
import org.openstreetmap.josm.plugins.ods.entities.GeoIndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.IndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.UniqueIndexImpl;

public class OpenDataCityStore extends EntityStore<OdCity> {
    private final UniqueIndexImpl<OdCity> primaryIndex = new UniqueIndexImpl<>(OdCity.class, "referenceId");
    private final IndexImpl<OdCity> idIndex = new IndexImpl<>(OdCity.class, "referenceId");
    private final GeoIndex<OdCity> geoIndex = new GeoIndexImpl<>(OdCity.class, "geometry");

    public OpenDataCityStore() {
        super();
        addIndex(primaryIndex);
        addIndex(idIndex);
        addIndex(geoIndex);
    }

    @Override
    public IndexImpl<OdCity> getIdIndex() {
        return idIndex;
    }

    @Override
    public UniqueIndexImpl<OdCity> getPrimaryIndex() {
        return primaryIndex;
    }


    @Override
    public GeoIndex<OdCity> getGeoIndex() {
        return geoIndex;
    }
}
