package org.openstreetmap.josm.plugins.ods.domains.buildings.matching;

import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuilding;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OsmBuilding;
import org.openstreetmap.josm.plugins.ods.entities.impl.ZeroOneMany;
import org.openstreetmap.josm.plugins.ods.matching.MatchImpl;

public class BuildingMatch extends MatchImpl<OdBuilding, OsmBuilding> {
    private BuildingMatch(OdBuilding odBuilding, OsmBuilding osmBuilding) {
        super(odBuilding, osmBuilding);
    }

    private BuildingMatch(OdBuilding odBuilding, ZeroOneMany<OsmBuilding> osmBuildings) {
        super(odBuilding, osmBuildings);
    }

    public static BuildingMatch create(OdBuilding odBuilding, OsmBuilding osmBuilding) {
        BuildingMatch match = new BuildingMatch(odBuilding, osmBuilding);
        osmBuilding.setMatch(match);
        odBuilding.setMatch(match);
        return match;
    }

    public static BuildingMatch create(OdBuilding odBuilding, ZeroOneMany<OsmBuilding> osmBuildings) {
        BuildingMatch match = new BuildingMatch(odBuilding, osmBuildings);
        osmBuildings.forEach(b -> b.setMatch(match));
        odBuilding.setMatch(match);
        return match;
    }


    //    @Override
    //    public Class<Building> getEntityClass() {
    //        return Building.class;
    //    }

    //    @Override
    //    public void analyze() {
    //        areaMatch = compareAreas();
    //        centroidMatch = compareCentroids();
    //        //        startDateMatch = compareStartDates();
    //        //        statusMatch = compareStatuses();
    //    }
    //
    //    //    private MatchStatus compareStartDates() {
    //    //        if (Objects.equals(getOsmEntity().getStartDate(), getOpenDataEntity().getStartDate())) {
    //    //            return MATCH;
    //    //        }
    //    //        return NO_MATCH;
    //    //    }
    //
    //    private MatchStatus compareStatuses() {
    //        //        BuildingStatus osmStatus = getOsmEntity().getStatus();
    //        //        BuildingStatus odStatus = getOpenDataEntity().getStatus();
    //        //        if (osmStatus.equals(odStatus)) {
    //        //            return MATCH;
    //        //        }
    //        //        if (osmStatus.equals(FUNCTIONAL) && odStatus.equals(IN_USE_NOT_MEASURED)) {
    //        //            return MATCH;
    //        //        }
    //        //        if (odStatus.equals(PROJECTED) && osmStatus.equals(UNDER_CONSTRUCTION)) {
    //        //            return COMPARABLE;
    //        //        }
    //        //        if (odStatus.equals(UNDER_CONSTRUCTION) &&
    //        //                (osmStatus.equals(FUNCTIONAL) || osmStatus.equals(IN_USE_NOT_MEASURED))) {
    //        //            return COMPARABLE;
    //        //        }
    //        return NO_MATCH;
    //    }
    //
    //    private MatchStatus compareAreas() {
    //        //        double osmArea = getOsmEntity().getGeometry().getArea();
    //        //        double odArea = getOpenDataEntity().getGeometry().getArea();
    //        //        if (osmArea == 0.0 || odArea == 0.0) {
    //        //            areaMatch = NO_MATCH;
    //        //        }
    //        //        double match = (osmArea - odArea) / osmArea;
    //        //        if (match == 0.0) {
    //        //            return MATCH;
    //        //        }
    //        //        if (Math.abs(match) < 0.01) {
    //        //            return COMPARABLE;
    //        //        }
    //        return NO_MATCH;
    //    }
    //
    //    private MatchStatus compareCentroids() {
    //        //        Point osmCentroid = getOsmEntity().getGeometry().getCentroid();
    //        //        Point odCentroid = getOpenDataEntity().getGeometry().getCentroid();
    //        //        double centroidDistance = osmCentroid.distance(odCentroid);
    //        //        if (centroidDistance == 0) {
    //        //            return MATCH;
    //        //        }
    //        //        if (centroidDistance < 1e-5) {
    //        //            return COMPARABLE;
    //        //        }
    //        return NO_MATCH;
    //    }
    //
    //    @Override
    //    public MatchStatus getGeometryMatch() {
    //        return combine(areaMatch, centroidMatch);
    //    }
    //
    //    @Override
    //    public MatchStatus getStatusMatch() {
    //        return statusMatch;
    //    }
    //
    //    @Override
    //    public MatchStatus getAttributeMatch() {
    //        return startDateMatch;
    //    }
}