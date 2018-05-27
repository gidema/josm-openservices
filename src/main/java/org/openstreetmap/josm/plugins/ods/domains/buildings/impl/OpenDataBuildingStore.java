package org.openstreetmap.josm.plugins.ods.domains.buildings.impl;

import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuilding;
import org.openstreetmap.josm.plugins.ods.entities.EntityStore;
import org.openstreetmap.josm.plugins.ods.entities.GeoIndex;
import org.openstreetmap.josm.plugins.ods.entities.GeoIndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.Index;
import org.openstreetmap.josm.plugins.ods.entities.IndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.UniqueIndexImpl;

/**
 * Store building entities created from geotools features.
 * This store has indexes on the referenceId and a geoIndex.
 * The primary index is on the primitive Id
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class OpenDataBuildingStore extends EntityStore<OdBuilding> {
    private final UniqueIndexImpl<OdBuilding> primaryIndex = new UniqueIndexImpl<>(OdBuilding.class, "primaryId");
    private final IndexImpl<OdBuilding> idIndex = new IndexImpl<>(OdBuilding.class, "referenceId");
    private final GeoIndex<OdBuilding> geoIndexImpl = new GeoIndexImpl<>(OdBuilding.class, "geometry");

    public OpenDataBuildingStore() {
        super();
        addIndex(primaryIndex);
        addIndex(idIndex);
        addIndex(geoIndexImpl);
    }

    @Override
    public UniqueIndexImpl<OdBuilding> getPrimaryIndex() {
        return primaryIndex;
    }

    @Override
    public Index<OdBuilding> getIdIndex() {
        return idIndex;
    }

    @Override
    public GeoIndex<OdBuilding> getGeoIndex() {
        return geoIndexImpl;
    }
}
