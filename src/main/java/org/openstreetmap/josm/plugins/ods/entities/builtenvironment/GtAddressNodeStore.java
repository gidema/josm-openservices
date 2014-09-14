package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;

import org.openstreetmap.josm.plugins.ods.entities.EntityStore;
import org.openstreetmap.josm.plugins.ods.entities.GeoIndex;
import org.openstreetmap.josm.plugins.ods.entities.GeoIndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.Index;
import org.openstreetmap.josm.plugins.ods.entities.IndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.UniqueIndexImpl;

public class GtAddressNodeStore extends EntityStore<AddressNode> {
    private UniqueIndexImpl<AddressNode> idIndex = new UniqueIndexImpl<>(AddressNode.class, "referenceId");
    private GeoIndex<AddressNode> geoIndex = new GeoIndexImpl<>(AddressNode.class, "geometry");
    private Index<AddressNode> postcodeNumberIndex = new IndexImpl<>(AddressNode.class, "postcode", "houseNumber");

    public GtAddressNodeStore() {
        getIndexes().add(idIndex);
        getIndexes().add(geoIndex);
        getIndexes().add(postcodeNumberIndex);
    }
    
    @Override
    public UniqueIndexImpl<AddressNode> getPrimaryIndex() {
        return idIndex;
    }

    @Override
    public GeoIndex<AddressNode> getGeoIndex() {
        return geoIndex;
    }
}
