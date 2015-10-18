package org.openstreetmap.josm.plugins.ods.entities.actual.impl.foreign;

import org.openstreetmap.josm.plugins.ods.entities.EntityStore;
import org.openstreetmap.josm.plugins.ods.entities.GeoIndex;
import org.openstreetmap.josm.plugins.ods.entities.GeoIndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.Index;
import org.openstreetmap.josm.plugins.ods.entities.UniqueIndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.BuildingImpl;

/**
 * Store building entities created from geotools features.
 * This store has indexes on the referenceId and a geoIndex.
 * The primary index is on the primitive Id
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class OpenDataBuildingStore extends EntityStore<Building> {
    private UniqueIndexImpl<Building> primaryIndex = new UniqueIndexImpl<>(Building.class, "referenceId");
    private Index<Building> idIndex  = primaryIndex;
    private GeoIndex<Building> geoIndexImpl = new GeoIndexImpl<Building, BuildingImpl>(BuildingImpl.class, "geometry");

    public OpenDataBuildingStore() {
        super();
        getIndexes().add(idIndex);
        getIndexes().add(geoIndexImpl);
    }

    @Override
    public UniqueIndexImpl<Building> getPrimaryIndex() {
        return primaryIndex;
    }

    @Override
    public Index<Building> getIdIndex() {
        return idIndex;
    }


    @Override
    public GeoIndex<Building> getGeoIndex() {
        return geoIndexImpl;
    }
}
