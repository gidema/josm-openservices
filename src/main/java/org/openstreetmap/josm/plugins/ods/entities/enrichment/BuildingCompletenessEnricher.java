package org.openstreetmap.josm.plugins.ods.entities.enrichment;

import static org.openstreetmap.josm.plugins.ods.entities.Entity.Completeness.Complete;

import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuilding;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OdBuildingStore;

import com.vividsolutions.jts.geom.Polygonal;
import com.vividsolutions.jts.geom.prep.PreparedPolygon;

/**
 * Enricher to update the completeness parameter for an open data building;
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class BuildingCompletenessEnricher {
    private final OdBuildingStore odBuildingStore;

    public BuildingCompletenessEnricher(OdBuildingStore odBuildingStore) {
        super();
        this.odBuildingStore = odBuildingStore;
    }

    public void run() {
        PreparedPolygon preparedPolygon = new PreparedPolygon((Polygonal) odBuildingStore.getBoundary());
        odBuildingStore.forEach(building -> update(preparedPolygon, building));

    }

    private static void update(PreparedPolygon preparedPolygon,OdBuilding building) {
        if (building.getCompleteness() != Complete) {
            if (preparedPolygon.covers(building.getGeometry())) {
                building.setCompleteness(Complete);
                return;
            }
        }
    }
}
