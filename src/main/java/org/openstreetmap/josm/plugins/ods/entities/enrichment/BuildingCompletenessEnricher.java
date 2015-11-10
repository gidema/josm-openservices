package org.openstreetmap.josm.plugins.ods.entities.enrichment;

import java.util.function.Consumer;

import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.opendata.OpenDataBuildingStore;

import com.vividsolutions.jts.geom.Polygonal;
import com.vividsolutions.jts.geom.prep.PreparedPolygon;

/**
 * Enricher to update the completeness parameter for an open data building;
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class BuildingCompletenessEnricher implements Consumer<Building> {
    PreparedPolygon boundary;
    
    public BuildingCompletenessEnricher(OpenDataBuildingStore buildingStore) {
        super();
        this.boundary = new PreparedPolygon((Polygonal) buildingStore.getBoundary());
    }

    @Override
    public void accept(Building building) {
        if (building.isIncomplete() && boundary.covers(building.getGeometry())) {
            building.setIncomplete(false);
        }
    }
}
