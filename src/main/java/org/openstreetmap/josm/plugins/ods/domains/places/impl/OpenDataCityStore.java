package org.openstreetmap.josm.plugins.ods.domains.places.impl;

import org.openstreetmap.josm.plugins.ods.domains.places.OdCity;
import org.openstreetmap.josm.plugins.ods.entities.GeoIndex;
import org.openstreetmap.josm.plugins.ods.entities.impl.GeoIndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.impl.OneOrManyIndex;
import org.openstreetmap.josm.plugins.ods.entities.storage.AbstractOdEntityStore;

public class OpenDataCityStore extends AbstractOdEntityStore<OdCity> {
    private final OneOrManyIndex<OdCity, Long> idIndex = new OneOrManyIndex<>(OdCity::getCityId);
    private final GeoIndex<OdCity> geoIndex = new GeoIndexImpl<>(OdCity.class, OdCity::getGeometry);

    public OpenDataCityStore() {
        super();
        addIndex(idIndex);
        addIndex(geoIndex);
    }

    @Override
    public GeoIndex<OdCity> getGeoIndex() {
        return geoIndex;
    }
}
