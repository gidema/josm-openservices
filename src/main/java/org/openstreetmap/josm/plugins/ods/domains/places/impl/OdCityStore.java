package org.openstreetmap.josm.plugins.ods.domains.places.impl;

import org.openstreetmap.josm.plugins.ods.domains.places.OdCity;
import org.openstreetmap.josm.plugins.ods.entities.GeoIndex;
import org.openstreetmap.josm.plugins.ods.entities.impl.GeoIndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.storage.AbstractOdEntityStore;

public class OdCityStore extends AbstractOdEntityStore<OdCity, Long> {
    private final GeoIndex<OdCity> geoIndex = new GeoIndexImpl<>(OdCity.class, OdCity::getGeometry);

    public OdCityStore() {
        super(OdCity::getCityId);
        addIndex(geoIndex);
    }

    @Override
    public GeoIndex<OdCity> getGeoIndex() {
        return geoIndex;
    }
}
