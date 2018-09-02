package org.openstreetmap.josm.plugins.ods.domains.buildings.matching;

import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.ODS;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuilding;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OsmBuilding;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OdBuildingStore;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OsmBuildingStore;
import org.openstreetmap.josm.plugins.ods.entities.EntityStatus;
import org.openstreetmap.josm.plugins.ods.entities.impl.ZeroOneMany;
import org.openstreetmap.josm.plugins.ods.matching.Matcher;

public class BuildingMatcher implements Matcher {
    private final OsmBuildingStore osmBuildingStore;
    private final OdBuildingStore odBuildingStore;
    private final List<OsmBuilding> unidentifiedOsmBuildings = new LinkedList<>();
    private final List<OsmBuilding> unmatchedOsmBuildings = new LinkedList<>();

    public BuildingMatcher(OsmBuildingStore osmBuildingStore, OdBuildingStore odBuildingStore) {
        super();
        this.osmBuildingStore = osmBuildingStore;
        this.odBuildingStore = odBuildingStore;
    }

    @Override
    public void run() {
        unmatchedOsmBuildings.clear();
        for (OdBuilding building : odBuildingStore) {
            processOpenDataBuilding(building);
        }
        analyze();
    }

    private void processOpenDataBuilding(OdBuilding odBuilding) {
        Long id = odBuilding.getBuildingId();
        ZeroOneMany<OsmBuilding> osmBuildings = osmBuildingStore.getIdIndex().get(id);
        if (!osmBuildings.isEmpty()) {
            BuildingMatch.create(odBuilding, osmBuildings);
        }
    }

    public void analyze() {
        for (OdBuilding building: odBuildingStore) {
            updateOdsTags(building);
        }
    }

    private static void updateOdsTags(OdBuilding building) {
        OsmPrimitive osm = building.getPrimitive();
        if (osm == null) {
            return;
        }
        BuildingMatch match = building.getMatch();
        if (match == null && building.getStatus() != EntityStatus.REMOVED) {
            osm.put(ODS.KEY.IDMATCH, "false");
            osm.put(ODS.KEY.STATUS, building.getStatus().toString());
        }
        else {
            if (building.getStatus() != EntityStatus.REMOVED) {
                osm.put(ODS.KEY.IDMATCH, "true");
                osm.put(ODS.KEY.STATUS, building.getStatus().toString());
            }
        }
    }

    @Override
    public void reset() {
        //        buildingMatches.clear();
        unidentifiedOsmBuildings.clear();
        //        unmatchedOpenDataBuildings.clear();
        unmatchedOsmBuildings.clear();
    }
}
