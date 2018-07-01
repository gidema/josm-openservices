package org.openstreetmap.josm.plugins.ods.domains.places.impl;

import org.openstreetmap.josm.plugins.ods.domains.places.OsmCity;
import org.openstreetmap.josm.plugins.ods.entities.GeoIndex;
import org.openstreetmap.josm.plugins.ods.entities.impl.GeoIndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.storage.AbstractOsmEntityStore;

public class OsmCityStore extends AbstractOsmEntityStore<OsmCity> {
    //    private final OneOrManyIndex<OsmCity, Long> idIndex = new OneOrManyIndex<>(OsmCity::getCityId);
    private final GeoIndex<OsmCity> geoIndex = new GeoIndexImpl<>(OsmCity.class, OsmCity::getGeometry);

    public OsmCityStore() {
        super();
        addIndex(geoIndex);
    }

    @Override
    public GeoIndex<OsmCity> getGeoIndex() {
        return geoIndex;
    }
}
