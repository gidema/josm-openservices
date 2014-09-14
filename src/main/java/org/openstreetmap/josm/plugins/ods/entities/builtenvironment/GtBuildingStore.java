package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;

import org.openstreetmap.josm.plugins.ods.entities.EntityStore;
import org.openstreetmap.josm.plugins.ods.entities.GeoIndex;
import org.openstreetmap.josm.plugins.ods.entities.GeoIndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.UniqueIndexImpl;

/**
 * Store building entities created from geotools features.
 * This store has indexes on the referenceId and a geoIndex.
 * The primary index is on the primitive Id
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class GtBuildingStore extends EntityStore<Building> {
    private UniqueIndexImpl<Building> idIndex = new UniqueIndexImpl<>(Building.class, "referenceId");
    private GeoIndex<Building> geoIndexImpl = new GeoIndexImpl<Building, BuildingImpl>(BuildingImpl.class, "geometry");

    public GtBuildingStore() {
        super();
        getIndexes().add(idIndex);
        getIndexes().add(geoIndexImpl);
    }

    @Override
    public UniqueIndexImpl<Building> getPrimaryIndex() {
        return idIndex;
    }


    @Override
    public GeoIndex<Building> getGeoIndex() {
        return geoIndexImpl;
    }
}
