package org.openstreetmap.josm.plugins.ods.entities.enrichment;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OpenDataBuildingStore;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygonal;
import com.vividsolutions.jts.geom.prep.PreparedPolygon;

/**
 * Enricher to update the completeness parameter for an open data building;
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class BuildingCompletenessEnricher implements Consumer<Building> {
    List<PreparedPolygon> boundaries = new LinkedList<>();
    
    public BuildingCompletenessEnricher(OpenDataBuildingStore buildingStore) {
        super();
        Geometry boundary = buildingStore.getBoundary();
        for (int i=0; i<boundary.getNumGeometries(); i++) {
            Polygonal polygonal = (Polygonal)boundary.getGeometryN(i);
            boundaries.add(new PreparedPolygon(polygonal));
        }
    }

    @Override
    public void accept(Building building) {
        if (!building.isIncomplete()) {
            return;
        }
        for (PreparedPolygon prep : boundaries) {
            if (prep.covers(building.getGeometry())) {
                building.setIncomplete(false);
                break;
            }
        }
    }
}
