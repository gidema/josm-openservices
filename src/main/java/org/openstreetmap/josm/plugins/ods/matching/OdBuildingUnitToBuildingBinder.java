package org.openstreetmap.josm.plugins.ods.matching;

import java.util.Collection;

import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuilding;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuildingUnit;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OdBuildingStore;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OdBuildingUnitStore;
import org.openstreetmap.josm.tools.Pair;


/**
 * <p>Try to find a matching building for every OdBuildingUnit in the buildinUnitStore. The referenceId of the address node will be used to do the matching.</p>
 * <p>If the referenceId is null, or no building with this referenceId was found,
 * this must be an error in the integrity of the opendata object.

 * @author gertjan
 *
 */
public class OdBuildingUnitToBuildingBinder {
    private final Collection<Pair<Long, Long>> buildingUnitToBuildingRelation;
    private final OdBuildingStore buildingStore;
    private final OdBuildingUnitStore buildingUnitStore;

    public OdBuildingUnitToBuildingBinder(
            Collection<Pair<Long, Long>> buildingUnitToBuildingRelation,
            OdBuildingStore buildingStore,
            OdBuildingUnitStore buildingUnitStore) {
        super();
        this.buildingUnitToBuildingRelation = buildingUnitToBuildingRelation;
        this.buildingStore = buildingStore;
        this.buildingUnitStore = buildingUnitStore;
    }

    public void run() {
        for(Pair<Long, Long> pair : buildingUnitToBuildingRelation) {
            bindBuildingUnitToBuilding(pair.a, pair.b);
        }
    }

    /**
     * Find a matching building for an address.
     *
     * @param buildingUnit
     */
    public void bindBuildingUnitToBuilding(Long buildingUnitId, Long buildingId) {
        OdBuildingUnit unit = buildingUnitStore.get(buildingUnitId);
        OdBuilding building = buildingStore.get(buildingId);
        if (unit != null && building != null) {
            unit.addBuilding(building);
            building.addBuildingUnit(unit);
        }
        else {
            // TODO Is in necessary to handle this case?
        }
    }
}
