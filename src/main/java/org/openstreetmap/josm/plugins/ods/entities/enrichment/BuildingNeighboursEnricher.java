package org.openstreetmap.josm.plugins.ods.entities.enrichment;

import java.util.function.Consumer;

import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuilding;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OdBuildingStore;


/**
 * This tasks verifies if there are adjacent buildings in
 * the down loaded data.
 *
 * TODO consider running over all buildings, not just the new ones.
 *
 * @author gertjan
 *
 */
public class BuildingNeighboursEnricher implements Consumer<OdBuilding> {
    private final OdBuildingStore buildingStore;

    public BuildingNeighboursEnricher(OdBuildingStore buildingStore) {
        super();
        this.buildingStore = buildingStore;
    }

    @Override
    public void accept(OdBuilding building) {
        // TODO consider using a buffer around the building
        for (OdBuilding candidate : buildingStore.getGeoIndex().intersection(building.getGeometry())) {
            if (candidate == building) continue;
            if (building.getNeighbours().contains(candidate)) continue;
            building.getNeighbours().add(candidate);
            candidate.getNeighbours().add(building);
        }
    }
}
