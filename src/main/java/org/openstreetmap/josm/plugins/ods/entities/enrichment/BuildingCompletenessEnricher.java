package org.openstreetmap.josm.plugins.ods.entities.enrichment;

import static org.openstreetmap.josm.plugins.ods.entities.Entity.Completeness.Complete;
import static org.openstreetmap.josm.plugins.ods.entities.Entity.Completeness.Incomplete;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuilding;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OpenDataBuildingStore;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygonal;
import org.locationtech.jts.geom.prep.PreparedPolygon;

/**
 * Enricher to update the completeness parameter for an open data building;
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class BuildingCompletenessEnricher implements Consumer<OdBuilding> {
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
    public void accept(OdBuilding building) {
        if (building.getCompleteness() != Complete) {
            for (PreparedPolygon prep : boundaries) {
                if (prep.covers(building.getGeometry())) {
                    building.setCompleteness(Complete);
                    return;
                }
            }
            building.setCompleteness(Incomplete);
        }
    }
}
