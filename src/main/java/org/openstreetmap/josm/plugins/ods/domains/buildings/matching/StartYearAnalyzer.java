package org.openstreetmap.josm.plugins.ods.domains.buildings.matching;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import org.openstreetmap.josm.command.ChangePropertyCommand;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Tag;
import org.openstreetmap.josm.plugins.ods.ODS;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuilding;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OsmBuilding;
import org.openstreetmap.josm.plugins.ods.entities.Deviation;
import org.openstreetmap.josm.plugins.ods.entities.impl.ZeroOneMany;
import org.openstreetmap.josm.plugins.ods.matching.AspectAnalyzer;

public class StartYearAnalyzer implements AspectAnalyzer<OsmBuilding> {
    final static String DIFF_KEY = ODS.KEY.DIFF_KEY + "start_year";

    @Override
    public void analyze(OsmBuilding osmBuilding) {
        BuildingMatch match = osmBuilding.getMatch();
        if (match == null) {
            return;
        }
        ZeroOneMany<OdBuilding> odBuildings = match.getOpenDataEntities();
        if (odBuildings.isOne()) {
            String odStartDate = odBuildings.getOne().getStartDate();
            if (!Objects.equals(osmBuilding.getStartDate(), odStartDate)) {
                osmBuilding.addDeviation(new StartDateDeviation(osmBuilding, odStartDate));
                setOdsTags(osmBuilding);
            }
        }
    }

    private static void setOdsTags(OsmBuilding osmBuilding) {
        Tag tag = new Tag(DIFF_KEY, "yes");
        osmBuilding.getPrimitive().put(tag);
    }

    public static class StartDateDeviation implements Deviation {
        private final OsmBuilding building;
        private final String newStartDate;

        public StartDateDeviation(OsmBuilding building, String newStartDate) {
            super();
            this.building = building;
            this.newStartDate = newStartDate;
        }

        @Override
        public boolean isFixable() {
            return true;
        }

        @Override
        public Command getFix() {
            Set<OsmPrimitive> primitives = Collections.singleton(building.getPrimitive());
            return new ChangePropertyCommand(primitives, "start_date", newStartDate);
        }

        @Override
        public void clearOdsTags() {
            building.getPrimitive().remove(DIFF_KEY);
        }

        @Override
        public int hashCode() {
            return Objects.hash(building, newStartDate);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof StartDateDeviation)) return false;
            StartDateDeviation other = (StartDateDeviation) obj;
            return Objects.equals(other.building, this.building) &&
                    Objects.equals(other.newStartDate, this.newStartDate);
        }
    }
}
