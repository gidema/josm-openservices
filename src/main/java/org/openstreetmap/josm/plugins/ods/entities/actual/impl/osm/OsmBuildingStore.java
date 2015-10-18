package org.openstreetmap.josm.plugins.ods.entities.actual.impl.osm;

import org.openstreetmap.josm.plugins.ods.entities.EntityStore;
import org.openstreetmap.josm.plugins.ods.entities.GeoIndex;
import org.openstreetmap.josm.plugins.ods.entities.GeoIndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.Index;
import org.openstreetmap.josm.plugins.ods.entities.IndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.UniqueIndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.BuildingImpl;

/**
 * Store building entities created from osm primitives.
 * This store has indexes on the referenceId, the primitiveId and a geoIndex.
 * The primary index is on the primitive Id 
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class OsmBuildingStore extends EntityStore<Building> {
    private UniqueIndexImpl<Building> primitiveIndex = new UniqueIndexImpl<>(Building.class, "primitiveId");
    private Index<Building> idIndex = new IndexImpl<>(Building.class, "referenceId");
    private GeoIndex<Building> geoIndexImpl = new GeoIndexImpl<Building, BuildingImpl>(BuildingImpl.class, "geometry");

    public OsmBuildingStore() {
        super();
        getIndexes().add(primitiveIndex);
        getIndexes().add(idIndex);
        getIndexes().add(geoIndexImpl);
    }

    @Override
    public UniqueIndexImpl<Building> getPrimaryIndex() {
        return primitiveIndex;
    }

    public Index<Building> getIdIndex() {
        return idIndex;
    }


    @Override
    public GeoIndex<Building> getGeoIndex() {
        return geoIndexImpl;
    }
}
