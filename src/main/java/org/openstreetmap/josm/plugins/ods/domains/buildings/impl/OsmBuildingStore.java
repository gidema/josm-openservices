package org.openstreetmap.josm.plugins.ods.domains.buildings.impl;

import org.openstreetmap.josm.plugins.ods.domains.buildings.OsmBuilding;
import org.openstreetmap.josm.plugins.ods.entities.EntityStore;
import org.openstreetmap.josm.plugins.ods.entities.GeoIndex;
import org.openstreetmap.josm.plugins.ods.entities.GeoIndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.Index;
import org.openstreetmap.josm.plugins.ods.entities.IndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.UniqueIndexImpl;

/**
 * Store building entities created from osm primitives.
 * This store has indexes on the referenceId, the primitiveId and a geoIndex.
 * The primary index is on the primitive Id
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class OsmBuildingStore extends EntityStore<OsmBuilding> {
    private final UniqueIndexImpl<OsmBuilding> primitiveIndex = new UniqueIndexImpl<>(OsmBuilding.class, "primitiveId");
    private final Index<OsmBuilding> idIndex = new IndexImpl<>(OsmBuilding.class, "referenceId");
    private final GeoIndex<OsmBuilding> geoIndexImpl = new GeoIndexImpl<>(OsmBuilding.class, "geometry");

    public OsmBuildingStore() {
        super();
        addIndex(primitiveIndex);
        addIndex(idIndex);
        addIndex(geoIndexImpl);
    }

    @Override
    public UniqueIndexImpl<OsmBuilding> getPrimaryIndex() {
        return primitiveIndex;
    }

    @Override
    public Index<OsmBuilding> getIdIndex() {
        return idIndex;
    }

    @Override
    public GeoIndex<OsmBuilding> getGeoIndex() {
        return geoIndexImpl;
    }
}
