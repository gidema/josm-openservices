package org.openstreetmap.josm.plugins.ods.matching;

import java.util.Objects;

import org.openstreetmap.josm.plugins.ods.entities.actual.Building;

import com.vividsolutions.jts.geom.Point;

public class BuildingMatch extends MatchImpl<Building> {
    /**
     * A double value indicating the match between the areas of the 2 buildings.
     * 
     */
    private double areaMatch;
    private double centroidDistance;
    private boolean startDateMatch;
    private boolean constructionMatch;
    
    public BuildingMatch(Building osmBuilding, Building openDataBuilding) {
        super(osmBuilding, openDataBuilding);
    }
    
    @Override
    public void analyze() {
        compareAreas();
        compareCentroids();
        compareAttributes();
    }
    
    private void compareAttributes() {
        startDateMatch = Objects.equals(getOsmEntity().getStartDate(), getOpenDataEntity().getStartDate());
        constructionMatch = (getOsmEntity().isUnderConstruction() == getOpenDataEntity().isUnderConstruction());
    }

    private void compareAreas() {
        double osmArea = getOsmEntity().getGeometry().getArea();
        double odArea = getOpenDataEntity().getGeometry().getArea();
        if (osmArea == 0.0 || odArea == 0.0) {
            areaMatch = 0.0;
        }
        areaMatch = (osmArea - odArea) / osmArea;
    }
    
    private void compareCentroids() {
        Point osmCentroid = getOsmEntity().getGeometry().getCentroid();
        Point odCentroid = getOpenDataEntity().getGeometry().getCentroid();
        centroidDistance = osmCentroid.distance(odCentroid);
    }

    @Override
    public boolean isGeometryMatch() {
        return Math.abs(areaMatch) < 0.01 && centroidDistance < 1e-5;
    }

    @Override
    public boolean isAttributeMatch() {
        return startDateMatch && constructionMatch;
    }
}