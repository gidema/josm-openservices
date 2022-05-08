package org.openstreetmap.josm.plugins.ods.io;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.operation.union.CascadedPolygonUnion;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.context.OdsContextJob;
import org.openstreetmap.josm.plugins.ods.entities.storage.ModifiedEntityStores;
import org.openstreetmap.josm.plugins.ods.jts.GeoUtil;

/**
 * This class calculates the areas that contain modified (added, or removed) entities.
 * These areas are grouped into a smaller collection of larger areas that can be used to download OSM objects in these areas.
 * 
 * Because the coordinates are in lat ,lon we cannot easily use the JTS buffer functionality to create a buffer around the features.
 * In stead, we use fixed delta's for the latitude and longitude expansion around 52 degrees lattitude. 
 * 
 * @author Idema
 *
 */
public class ModifiedAreasFactory implements OdsContextJob {
    static double bufferSize = 25.0; // (25 meters)
    static double earthCircumference = 40075017; // in Meters
    double dLat = bufferSize / earthCircumference * 360;
    double dLon = dLat * Math.cos((52 / 180) * Math.PI);

    
    @Override
    public void run(OdsContext context) {
        GeoUtil geoUtil = context.getComponent(GeoUtil.class);
        ModifiedEntityStores modifiedEntityStores = context.getComponent(ModifiedEntityStores.class);
        // Collect the bounding boxes of all modified entities with a bounding box around them 
        List<Polygon> buffers = new ArrayList<>();
        modifiedEntityStores.forEach(entityStore -> {
            entityStore.forEach(geoObject -> {
                Envelope envelope = geoObject.getGeometry().getEnvelopeInternal();
                envelope.expandBy(dLon, dLat);
                buffers.add(geoUtil.toPolygon(envelope));
            });
        });
        // Create a union of the buffered bounding boxes to combine overlapping areas
        Geometry union = CascadedPolygonUnion.union(buffers);
        // The resulting geometry is expected to be a MultiPolygon in most cases and incidentally a single Polygon
        List<Envelope> downloadAreas;
        if (union != null) {
            MultiPolygon mpUnion;
            switch(union.getGeometryType()) {
            case Geometry.TYPENAME_MULTIPOLYGON:
                mpUnion = (MultiPolygon) union;
                break;
            case Geometry.TYPENAME_POLYGON:
                mpUnion = union.getFactory().createMultiPolygon(new Polygon[] {(Polygon) union});
                break;
            default:
                throw new UnsupportedOperationException("Unexpected geometry type. Only Polygon and MultiPolygon are expected here");
            }
            downloadAreas = new ArrayList<>(mpUnion.getNumGeometries());
            for (int i=0 ; i< mpUnion.getNumGeometries(); i++) {
                downloadAreas.add(mpUnion.getGeometryN(i).getEnvelopeInternal());
            }
        }
        else {
            downloadAreas = Collections.emptyList();
        }
        int i = 0;
    }

}
