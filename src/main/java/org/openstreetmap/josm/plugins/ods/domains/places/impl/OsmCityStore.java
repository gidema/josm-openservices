package org.openstreetmap.josm.plugins.ods.domains.places.impl;

import org.openstreetmap.josm.plugins.ods.domains.places.OsmCity;
import org.openstreetmap.josm.plugins.ods.entities.EntityStore;
import org.openstreetmap.josm.plugins.ods.entities.GeoIndex;
import org.openstreetmap.josm.plugins.ods.entities.GeoIndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.Index;
import org.openstreetmap.josm.plugins.ods.entities.UniqueIndexImpl;

public class OsmCityStore extends EntityStore<OsmCity> {
    private final UniqueIndexImpl<OsmCity> primaryIndex = new UniqueIndexImpl<>(OsmCity.class, "primitiveId");
    private final UniqueIndexImpl<OsmCity> idIndex = new UniqueIndexImpl<>(OsmCity.class, "referenceId");
    private final GeoIndex<OsmCity> geoIndex = new GeoIndexImpl<>(OsmCity.class, "geometry");

    public OsmCityStore() {
        super();
        addIndex(idIndex);
        addIndex(geoIndex);
    }

    @Override
    public UniqueIndexImpl<OsmCity> getPrimaryIndex() {
        return primaryIndex;
    }


    @Override
    public Index<OsmCity> getIdIndex() {
        return idIndex;
    }

    @Override
    public GeoIndex<OsmCity> getGeoIndex() {
        return geoIndex;
    }
}
