package org.openstreetmap.josm.plugins.ods.entities.enrichment;

import java.util.function.Consumer;

import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.foreign.OpenDataBuildingStore;
import org.openstreetmap.josm.plugins.ods.jts.GeoUtil;


/**
 * This tasks verifies if there are adjacent buildings in
 * the down loaded data.
 * 
 * TODO consider running over all buildings, not just the new ones.
 * 
 * @author gertjan
 *
 */
public class BuildingNeighboursEnricher implements Consumer<Building> {
    private final OpenDataBuildingStore buildingStore;
    
    public BuildingNeighboursEnricher(OpenDataBuildingStore buildingStore, GeoUtil geoUtil) {
        super();
        this.buildingStore = buildingStore;
    }

    @Override
    public void accept(Building building) {
        // TODO consider using a buffer around the building
        for (Building candidate : buildingStore.getGeoIndex().intersection(building.getGeometry())) {
            if (candidate == building) continue;
            if (building.getNeighbours().contains(candidate)) continue;
            building.getNeighbours().add(candidate);
            candidate.getNeighbours().add(building);
        }
    }
}
