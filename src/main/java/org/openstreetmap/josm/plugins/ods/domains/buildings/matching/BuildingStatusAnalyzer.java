package org.openstreetmap.josm.plugins.ods.domains.buildings.matching;

import static org.openstreetmap.josm.plugins.ods.entities.EntityStatus.CONSTRUCTION;
import static org.openstreetmap.josm.plugins.ods.entities.EntityStatus.IN_USE;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.openstreetmap.josm.command.ChangePropertyCommand;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.ODS;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuilding;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OsmBuilding;
import org.openstreetmap.josm.plugins.ods.entities.Deviation;
import org.openstreetmap.josm.plugins.ods.entities.EntityStatus;
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
            EntityStatus osmStatus = osmBuilding.getStatus();
            EntityStatus odStatus = odBuilding.getStatus();
            if ((odStatus == IN_USE || odStatus == EntityStatus.IN_USE_NOT_MEASURED) &&
                    osmStatus == CONSTRUCTION) {
                osmBuilding.addDeviation(new BuildingStatusDeviation(osmBuilding, IN_USE, odBuilding.getSourceDate()));
                osmBuilding.getPrimitive().put(DIFF_KEY, "yes");
                return;
            }
        }
    }

    public class BuildingStatusDeviation implements Deviation {
        private final OsmBuilding building;
        private final String sourceDate;

        public BuildingStatusDeviation(OsmBuilding building, EntityStatus newStatus, String sourceDate) {
            super();
            this.building = building;
            assert newStatus == IN_USE;
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
