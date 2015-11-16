package org.openstreetmap.josm.plugins.ods.matching.update;

import java.util.Map;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.BuildingEntityType;
import org.openstreetmap.josm.plugins.ods.matching.Match;
import org.openstreetmap.josm.plugins.ods.matching.MatchStatus;

public class BuildingUpdater implements EntityUpdater<Building> {
    private OdsModule module;
    
    public BuildingUpdater(OdsModule module) {
        super();
        this.module = module;
    }

    public void update(Match<? extends Entity> _match) {
        // TODO force this assert at compile time
        assert (_match.getEntityType().equals(BuildingEntityType.getInstance()));
        @SuppressWarnings("unchecked")
        Match<Building> match = (Match<Building>) _match;
        Building osmBuilding = match.getOsmEntity();
        Building odBuilding = match.getOpenDataEntity();
        if (match.getAttributeMatch().equals(MatchStatus.NO_MATCH)) {
            updateAttributes(odBuilding, osmBuilding);
        }
        if (!match.getStatusMatch().equals(MatchStatus.MATCH)) {
            updateStatus(odBuilding, osmBuilding);
        }
    }

    private void updateAttributes(Building odBuilding, Building osmBuilding) {
        OsmPrimitive odPrimitive = odBuilding.getPrimitive();
        OsmPrimitive osmPrimitive = osmBuilding.getPrimitive();
        osmBuilding.setStartDate(odBuilding.getStartDate());

        Map<String, String> keys = osmPrimitive.getKeys();
        keys.put("start_date", odPrimitive.get("start_date"));
        osmPrimitive.setKeys(keys);
    }

    private void updateStatus(Building odBuilding, Building osmBuilding) {
        OsmPrimitive odPrimitive = odBuilding.getPrimitive();
        OsmPrimitive osmPrimitive = osmBuilding.getPrimitive();
       
    }
}
