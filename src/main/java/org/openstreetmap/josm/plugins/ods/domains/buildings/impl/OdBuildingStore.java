package org.openstreetmap.josm.plugins.ods.domains.buildings.impl;

import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuilding;
import org.openstreetmap.josm.plugins.ods.entities.GeoIndex;
import org.openstreetmap.josm.plugins.ods.entities.impl.GeoIndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.impl.OneOrManyIndex;
import org.openstreetmap.josm.plugins.ods.entities.impl.ZeroOneMany;
import org.openstreetmap.josm.plugins.ods.entities.storage.AbstractOdEntityStore;

/**
 * Store building entities created from geotools features.
 * This store has indexes on the referenceId and a geoIndex.
 * The primary index is on the primitive Id
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class OdBuildingStore extends AbstractOdEntityStore<OdBuilding> {
    private final OneOrManyIndex<OdBuilding, Long> idIndex = new OneOrManyIndex<>(OdBuilding::getBuildingId);
    private final GeoIndex<OdBuilding> geoIndexImpl = new GeoIndexImpl<>(OdBuilding.class, OdBuilding::getGeometry);

    public OdBuildingStore() {
        super();
        addIndex(idIndex);
        addIndex(geoIndexImpl);
    }

    @Override
    public GeoIndex<OdBuilding> getGeoIndex() {
        return geoIndexImpl;
    }

    public ZeroOneMany<OdBuilding> getByBuildingId(Long id) {
        return idIndex.get(id);
    }
}
