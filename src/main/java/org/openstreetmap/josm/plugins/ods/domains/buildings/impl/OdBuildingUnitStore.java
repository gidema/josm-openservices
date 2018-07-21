package org.openstreetmap.josm.plugins.ods.domains.buildings.impl;

import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuildingUnit;
import org.openstreetmap.josm.plugins.ods.entities.GeoIndex;
import org.openstreetmap.josm.plugins.ods.entities.impl.GeoIndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.impl.OneOrManyIndex;
import org.openstreetmap.josm.plugins.ods.entities.impl.ZeroOneMany;
import org.openstreetmap.josm.plugins.ods.entities.storage.AbstractOdEntityStore;

/**
 * Store building unit entities created from geotools features.
 * This store has indexes on the referenceId and a geoIndex.
 * The primary index is on the primitive Id
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class OdBuildingUnitStore extends AbstractOdEntityStore<OdBuildingUnit, Long> {
    private final OneOrManyIndex<OdBuildingUnit, Long> idIndex = new OneOrManyIndex<>(OdBuildingUnit::getBuildingUnitId);
    private final GeoIndex<OdBuildingUnit> geoIndexImpl = new GeoIndexImpl<>(OdBuildingUnit.class, OdBuildingUnit::getGeometry);

    public OdBuildingUnitStore() {
        super(OdBuildingUnit::getBuildingUnitId);
        addIndex(idIndex);
        addIndex(geoIndexImpl);
    }

    @Override
    public GeoIndex<OdBuildingUnit> getGeoIndex() {
        return geoIndexImpl;
    }

    public ZeroOneMany<OdBuildingUnit> getByBuildingUnitId(Long id) {
        return idIndex.get(id);
    }
}
