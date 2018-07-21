package org.openstreetmap.josm.plugins.ods.domains.buildings.impl;

import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuilding;
import org.openstreetmap.josm.plugins.ods.entities.GeoIndex;
import org.openstreetmap.josm.plugins.ods.entities.impl.GeoIndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.storage.AbstractOdEntityStore;

/**
 * Store building entities created from geotools features.
 * This store has indexes on the referenceId and a geoIndex.
 * The primary index is on the primitive Id
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class OdBuildingStore extends AbstractOdEntityStore<OdBuilding, Long> {
    private final GeoIndex<OdBuilding> geoIndexImpl = new GeoIndexImpl<>(OdBuilding.class, OdBuilding::getGeometry);

    public OdBuildingStore() {
        super(OdBuilding::getBuildingId);
        addIndex(geoIndexImpl);
    }

    @Override
    public GeoIndex<OdBuilding> getGeoIndex() {
        return geoIndexImpl;
    }
}
