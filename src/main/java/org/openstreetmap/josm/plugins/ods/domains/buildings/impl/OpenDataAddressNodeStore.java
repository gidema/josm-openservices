package org.openstreetmap.josm.plugins.ods.domains.buildings.impl;

import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddressNode;
import org.openstreetmap.josm.plugins.ods.entities.EntityStore;
import org.openstreetmap.josm.plugins.ods.entities.GeoIndex;
import org.openstreetmap.josm.plugins.ods.entities.GeoIndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.Index;
import org.openstreetmap.josm.plugins.ods.entities.IndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.UniqueIndexImpl;

public class OpenDataAddressNodeStore extends EntityStore<OdAddressNode> {
    private final UniqueIndexImpl<OdAddressNode> primaryIndex = new UniqueIndexImpl<>(OdAddressNode.class, "primaryId");
    private final IndexImpl<OdAddressNode> idIndex = new IndexImpl<>(OdAddressNode.class, "referenceId");
    private final GeoIndex<OdAddressNode> geoIndex = new GeoIndexImpl<>(OdAddressNode.class, "geometry");
    private final Index<OdAddressNode> postcodeNumberIndex = new IndexImpl<>(OdAddressNode.class, "postcode", "houseNumber");

    public OpenDataAddressNodeStore() {
        addIndex(primaryIndex);
        addIndex(idIndex);
        addIndex(geoIndex);
        addIndex(postcodeNumberIndex);
    }

    @Override
    public UniqueIndexImpl<OdAddressNode> getPrimaryIndex() {
        return primaryIndex;
    }

    @Override
    public Index<OdAddressNode> getIdIndex() {
        return idIndex;
    }

    @Override
    public GeoIndex<OdAddressNode> getGeoIndex() {
        return geoIndex;
    }
}
