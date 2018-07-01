package org.openstreetmap.josm.plugins.ods.domains.buildings.impl;

import org.openstreetmap.josm.plugins.ods.domains.buildings.OsmBuilding;
import org.openstreetmap.josm.plugins.ods.entities.GeoIndex;
import org.openstreetmap.josm.plugins.ods.entities.impl.GeoIndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.impl.OneOrManyIndex;
import org.openstreetmap.josm.plugins.ods.entities.storage.AbstractOsmEntityStore;

/**
 * Store building entities created from osm primitives.
 * This store has indexes on the referenceId, the primitiveId and a geoIndex.
 * The primary index is on the primitive Id
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class OsmBuildingStore extends AbstractOsmEntityStore<OsmBuilding> {
    private final OneOrManyIndex<OsmBuilding, Long> idIndex = new OneOrManyIndex<>(OsmBuilding::getBuildingId);
    private final GeoIndex<OsmBuilding> geoIndexImpl = new GeoIndexImpl<>(OsmBuilding.class, OsmBuilding::getGeometry);

    public OsmBuildingStore() {
        super();
        addIndex(idIndex);
        addIndex(geoIndexImpl);
    }

    public OneOrManyIndex<OsmBuilding, Long> getIdIndex() {
        return idIndex;
    }

    @Override
    public GeoIndex<OsmBuilding> getGeoIndex() {
        return geoIndexImpl;
    }
}
