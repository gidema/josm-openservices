package org.openstreetmap.josm.plugins.ods.matching.update;

import java.util.Collection;
import java.util.Iterator;

import org.openstreetmap.josm.plugins.ods.domains.buildings.OsmBuilding;
import org.openstreetmap.josm.plugins.ods.entities.Deviation;

public class BuildingUpdater implements EntityUpdater<OsmBuilding> {
    //    private final BuildingGeometryUpdater geometryUpdater;
    //    private final BuildingGeometryUpdaterNg geometryUpdater;

    public BuildingUpdater() {
        super();
        //        this.geometryUpdater = new BuildingGeometryUpdaterNg(module);
    }

    //    @Override
    //    public void update(List<Match<?, ?>> matches) {
    //        List<Match<OsmBuilding, OdBuilding>> geometryUpdateNeeded = new LinkedList<>();
    //        for (Match<?, ?> match : matches) {
    //            if (match instanceof BuildingMatch) {
    //                BuildingMatch buildingMatch = (BuildingMatch) match;
    //                if (match.getGeometryMatch() == MatchStatus.NO_MATCH) {
    //                    geometryUpdateNeeded.add(buildingMatch);
    //                }
    //                OsmBuilding osmBuilding = buildingMatch.getOsmEntity();
    //                OdBuilding odBuilding = buildingMatch.getOpenDataEntity();
    //                if (match.getAttributeMatch().equals(MatchStatus.NO_MATCH)) {
    //                    updateAttributes(osmBuilding, odBuilding);
    //                }
    //                if (!match.getStatusMatch().equals(MatchStatus.MATCH)) {
    //                    updateStatus(osmBuilding, odBuilding);
    //                }
    //            }
    //        }
    //        //        geometryUpdater.updateGeometries(geometryUpdateNeeded);
    //    }

    @Override
    public void update(Collection<OsmBuilding> osmBuildings) {
        osmBuildings.forEach(osmBuilding -> {
            update(osmBuilding);
        });
    }

    public static void update(OsmBuilding osmBuilding) {
        Iterator<Deviation> it = osmBuilding.getDeviations().iterator();
        boolean updated = false;
        while (it.hasNext()) {
            Deviation deviation = it.next();
            if(deviation.isFixable()) {
                if (deviation.getFix().executeCommand()) {
                    it.remove();
                    deviation.clearOdsTags();
                    updated = true;
                }
            }
        }
        if (updated) {
            // TODO update the osmBuilding entity from its primitives
        }
        //        geometryUpdater.updateGeometries(geometryUpdateNeeded);
    }

    /** Moved to BuildingStatusAnalyzer */
    //    private static void updateAttributes(OsmBuilding osmBuilding, OdBuilding odBuilding) {
    //        OsmPrimitive osmPrimitive = osmBuilding.getPrimitive();
    //        osmBuilding.setSourceDate(odBuilding.getSourceDate());
    //        osmPrimitive.put("source:date", odBuilding.getSourceDate());
    //        osmBuilding.setStartDate(odBuilding.getStartDate());
    //    }

    /** Moved to BuilingStatusAnalyzer */
    //    private static void updateStatus(OsmBuilding osmBuilding, OdBuilding odBuilding) {
    //        OsmPrimitive odPrimitive = odBuilding.getPrimitive();
    //        OsmPrimitive osmPrimitive = osmBuilding.getPrimitive();
    //        if (osmBuilding.getStatus().equals(EntityStatus.CONSTRUCTION) &&
    //                (odBuilding.getStatus().equals(EntityStatus.IN_USE) ||
    //                        odBuilding.getStatus().equals(EntityStatus.IN_USE_NOT_MEASURED))
    //                ) {
    //            osmBuilding.setSourceDate(odBuilding.getSourceDate());
    //            osmPrimitive.put("source:date", odBuilding.getSourceDate());
    //            osmPrimitive.put("building", odPrimitive.get("building"));
    //            osmPrimitive.put("construction", null);
    //            osmBuilding.setStatus(odBuilding.getStatus());
    //            osmPrimitive.setModified(true);
    //        }
    //    }
}
