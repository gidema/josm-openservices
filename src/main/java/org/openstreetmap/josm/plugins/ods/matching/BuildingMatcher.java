package org.openstreetmap.josm.plugins.ods.matching;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.Matcher;
import org.openstreetmap.josm.plugins.ods.ODS;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuilding;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OsmBuilding;
import org.openstreetmap.josm.plugins.ods.entities.EntityStore;

public class BuildingMatcher implements Matcher {
    private final Map<Long, BuildingMatch> buildingMatches = new HashMap<>();
    private final EntityStore<OsmBuilding> osmBuildingStore;
    private final EntityStore<OdBuilding> odBuildingStore;
    private final List<OsmBuilding> unidentifiedOsmBuildings = new LinkedList<>();
    private final List<OdBuilding> unmatchedOpenDataBuildings = new LinkedList<>();
    private final List<OsmBuilding> unmatchedOsmBuildings = new LinkedList<>();

    public BuildingMatcher(OdsModule module) {
        super();
        osmBuildingStore = module.getOsmLayerManager().getEntityStore(OsmBuilding.class);
        odBuildingStore = module.getOpenDataLayerManager().getEntityStore(OdBuilding.class);
    }

    @Override
    public void run() {
        unmatchedOpenDataBuildings.clear();
        unmatchedOsmBuildings.clear();
        for (OdBuilding building : odBuildingStore) {
            processOpenDataBuilding(building);
        }
        for (OsmBuilding building : osmBuildingStore) {
            processOsmBuilding(building);
        }
        analyze();
    }

    private void processOpenDataBuilding(OdBuilding odBuilding) {
        Long id = (Long) odBuilding.getReferenceId();
        BuildingMatch match = buildingMatches.get(id);
        if (match != null) {
            match.addOpenDataEntity(odBuilding);
            odBuilding.setMatch(match);
            return;
        }
        List<OsmBuilding> osmBuildings = osmBuildingStore.getById(id);
        if (osmBuildings.size() > 0) {
            match = new BuildingMatch(osmBuildings.get(0), odBuilding);
            for (int i=1; i<osmBuildings.size() ; i++) {
                OsmBuilding osmBuilding = osmBuildings.get(i);
                osmBuilding.setMatch(match);
                match.addOsmEntity(osmBuilding);
            }
            buildingMatches.put(id, match);
        } else {
            unmatchedOpenDataBuildings.add(odBuilding);
        }
    }

    private void processOsmBuilding(OsmBuilding osmBuilding) {
        Object id = osmBuilding.getReferenceId();
        if (id == null) {
            unidentifiedOsmBuildings.add(osmBuilding);
            return;
        }
        Long l;
        try {
            l = (Long)id;
        }
        catch (Exception e) {
            unidentifiedOsmBuildings.add(osmBuilding);
            return;
        }
        List<OdBuilding> odBuildings = odBuildingStore.getById(l);
        if (odBuildings.size() > 0) {
            BuildingMatch match = new BuildingMatch(osmBuilding, odBuildings.get(0));
            for (int i=1; i<odBuildings.size(); i++) {
                match.addOpenDataEntity(odBuildings.get(i));
            }
            buildingMatches.put(l, match);
        } else {
            unmatchedOsmBuildings.add(osmBuilding);
        }
    }

    public void analyze() {
        for (Match<OsmBuilding, OdBuilding> match : buildingMatches.values()) {
            if (match.isSimple()) {
                match.analyze();
                match.updateMatchTags();
            }
        }
        for (OdBuilding building: unmatchedOpenDataBuildings) {
            OsmPrimitive osm = building.getPrimitive();
            if (osm != null) {
                osm.put(ODS.KEY.IDMATCH, "false");
                osm.put(ODS.KEY.STATUS, building.getStatus().toString());
            }
        }
    }

    @Override
    public void reset() {
        buildingMatches.clear();
        unidentifiedOsmBuildings.clear();
        unmatchedOpenDataBuildings.clear();
        unmatchedOsmBuildings.clear();
    }
}
