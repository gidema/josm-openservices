package org.openstreetmap.josm.plugins.ods.domains.buildings.matching;

import static org.openstreetmap.josm.plugins.ods.domains.buildings.BuildingStatus.FUNCTIONAL;
import static org.openstreetmap.josm.plugins.ods.domains.buildings.BuildingStatus.IN_USE_NOT_MEASURED;
import static org.openstreetmap.josm.plugins.ods.domains.buildings.BuildingStatus.UNDER_CONSTRUCTION;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.openstreetmap.josm.command.ChangePropertyCommand;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.ODS;
import org.openstreetmap.josm.plugins.ods.domains.buildings.BuildingStatus;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuilding;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OsmBuilding;
import org.openstreetmap.josm.plugins.ods.entities.Deviation;
import org.openstreetmap.josm.plugins.ods.entities.impl.ZeroOneMany;
import org.openstreetmap.josm.plugins.ods.matching.AspectAnalyzer;

public class BuildingStatusAnalyzer implements AspectAnalyzer<OsmBuilding> {
    final static String DIFF_KEY = ODS.KEY.DIFF_KEY + "status";

    @Override
    public void analyze(OsmBuilding osmBuilding) {
        BuildingMatch match = osmBuilding.getMatch();
        if (match == null) {
            osmBuilding.removeDeviation(BuildingStatusDeviation.class);
            return;
        }
        ZeroOneMany<OdBuilding> odBuildings = match.getOpenDataEntities();
        if (odBuildings.isOne()) {
            OdBuilding odBuilding = odBuildings.getOne();
            BuildingStatus osmStatus = osmBuilding.getStatus();
            BuildingStatus odStatus = odBuilding.getStatus();
            if ((odStatus == FUNCTIONAL || odStatus == IN_USE_NOT_MEASURED) &&
                    osmStatus == UNDER_CONSTRUCTION) {
                osmBuilding.addDeviation(new BuildingStatusDeviation(osmBuilding, FUNCTIONAL, odBuilding.getSourceDate()));
                osmBuilding.getPrimitive().put(DIFF_KEY, "yes");
                return;
            }
        }
    }

    public class BuildingStatusDeviation implements Deviation {
        private final OsmBuilding building;
        private final String sourceDate;

        public BuildingStatusDeviation(OsmBuilding building, BuildingStatus newStatus, String sourceDate) {
            super();
            this.building = building;
            assert newStatus == FUNCTIONAL;
            this.sourceDate = sourceDate;
        }

        @Override
        public boolean isFixable() {
            return true;
        }

        @Override
        public Command getFix() {
            Set<OsmPrimitive> primitives = Collections.singleton(building.getPrimitive());

            Map<String, String> tags = new HashMap<>();
            tags.put("construction", null);
            tags.put("building", building.getPrimitive().get("construction"));
            tags.put("source:date", sourceDate);
            return new ChangePropertyCommand(primitives, tags);
        }

        @Override
        public void clearOdsTags() {
            building.getPrimitive().remove(DIFF_KEY);
        }
    }
}
