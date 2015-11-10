package org.openstreetmap.josm.plugins.ods.entities.actual.impl.opendata;

import org.openstreetmap.josm.plugins.ods.entities.EntityStore;
import org.openstreetmap.josm.plugins.ods.entities.GeoIndex;
import org.openstreetmap.josm.plugins.ods.entities.GeoIndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.Index;
import org.openstreetmap.josm.plugins.ods.entities.IndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.UniqueIndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.actual.AddressNode;

public class OpenDataAddressNodeStore extends EntityStore<AddressNode> {
    private UniqueIndexImpl<AddressNode> primaryIndex = new UniqueIndexImpl<>(AddressNode.class, "primaryId");
    private IndexImpl<AddressNode> idIndex = new IndexImpl<>(AddressNode.class, "referenceId");
    private GeoIndex<AddressNode> geoIndex = new GeoIndexImpl<>(AddressNode.class, "geometry");
    private Index<AddressNode> postcodeNumberIndex = new IndexImpl<>(AddressNode.class, "postcode", "houseNumber");

    public OpenDataAddressNodeStore() {
        addIndex(primaryIndex);
        addIndex(idIndex);
        addIndex(geoIndex);
        addIndex(postcodeNumberIndex);
    }
    
    @Override
    public UniqueIndexImpl<AddressNode> getPrimaryIndex() {
        return primaryIndex;
    }

    @Override
    public Index<AddressNode> getIdIndex() {
        return idIndex;
    }

    @Override
    public GeoIndex<AddressNode> getGeoIndex() {
        return geoIndex;
    }
}
