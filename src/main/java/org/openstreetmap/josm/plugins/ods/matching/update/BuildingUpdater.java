package org.openstreetmap.josm.plugins.ods.matching.update;

import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.entities.EntityStatus;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.BuildingEntityType;
import org.openstreetmap.josm.plugins.ods.matching.Match;
import org.openstreetmap.josm.plugins.ods.matching.MatchStatus;
import org.openstreetmap.josm.plugins.ods.osm.update.BuildingGeometryUpdaterNg;

public class BuildingUpdater implements EntityUpdater {
//    private final BuildingGeometryUpdater geometryUpdater;
    private final BuildingGeometryUpdaterNg geometryUpdater;
    
    public BuildingUpdater(OdsModule module) {
        super();
        this.geometryUpdater = new BuildingGeometryUpdaterNg(module);
    }

    public void update(List<Match<?>> matches) {
        List<Match<Building>> geometryUpdateNeeded = new LinkedList<>();
        for (Match<?> match : matches) {
            if (match.getEntityType().equals(BuildingEntityType.getInstance())) {
                @SuppressWarnings("unchecked")
                Match<Building> buildingMatch = (Match<Building>) match;
                if (match.getGeometryMatch() == MatchStatus.NO_MATCH) {
                    geometryUpdateNeeded.add(buildingMatch);
                }
                Building osmBuilding = buildingMatch.getOsmEntity();
                Building odBuilding = buildingMatch.getOpenDataEntity();
                if (match.getAttributeMatch().equals(MatchStatus.NO_MATCH)) {
                    updateAttributes(odBuilding, osmBuilding);
                }
                if (!match.getStatusMatch().equals(MatchStatus.MATCH)) {
                    updateStatus(odBuilding, osmBuilding);
                }
            }
        }
        geometryUpdater.updateGeometries(geometryUpdateNeeded);
    }

    private void updateAttributes(Building odBuilding, Building osmBuilding) {
        OsmPrimitive osmPrimitive = osmBuilding.getPrimitive();
        osmBuilding.setSourceDate(odBuilding.getSourceDate());
        osmPrimitive.put("source:date", odBuilding.getSourceDate());
        osmBuilding.setStartDate(odBuilding.getStartDate());
        osmPrimitive.put("start_date", odBuilding.getStartDate());
        osmPrimitive.setModified(true);
    }

    private void updateStatus(Building odBuilding, Building osmBuilding) {
        OsmPrimitive odPrimitive = odBuilding.getPrimitive();
        OsmPrimitive osmPrimitive = osmBuilding.getPrimitive();
        if (osmBuilding.getStatus().equals(EntityStatus.CONSTRUCTION)
                && odBuilding.getStatus().equals(EntityStatus.IN_USE)) {
            osmBuilding.setSourceDate(odBuilding.getSourceDate());
            osmPrimitive.put("source:date", odBuilding.getSourceDate());
            osmPrimitive.put("building", odPrimitive.get("building"));
            osmPrimitive.put("construction", null);
            osmBuilding.setStatus(odBuilding.getStatus());
            osmPrimitive.setModified(true);
        }
    }
}
