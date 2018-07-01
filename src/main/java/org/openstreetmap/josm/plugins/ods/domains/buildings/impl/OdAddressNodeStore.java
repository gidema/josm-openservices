package org.openstreetmap.josm.plugins.ods.domains.buildings.impl;

import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddressNode;
import org.openstreetmap.josm.plugins.ods.entities.GeoIndex;
import org.openstreetmap.josm.plugins.ods.entities.impl.GeoIndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.impl.OneOrManyIndex;
import org.openstreetmap.josm.plugins.ods.entities.impl.ZeroOneMany;
import org.openstreetmap.josm.plugins.ods.entities.storage.AbstractOdEntityStore;
import org.openstreetmap.josm.plugins.ods.matching.PcHousenumberAddressKey;

public class OdAddressNodeStore extends AbstractOdEntityStore<OdAddressNode> {
    private final GeoIndex<OdAddressNode> geoIndex = new GeoIndexImpl<>(OdAddressNode.class, OdAddressNode::getGeometry);
    private final OneOrManyIndex<OdAddressNode, PcHousenumberAddressKey> postcodeNumberIndex =
            new OneOrManyIndex<>(PcHousenumberAddressKey::new);

    public OdAddressNodeStore() {
        super();
        addIndex(geoIndex);
        addIndex(postcodeNumberIndex);
    }

    @Override
    public GeoIndex<OdAddressNode> getGeoIndex() {
        return geoIndex;
    }

    public ZeroOneMany<OdAddressNode> lookup(PcHousenumberAddressKey key) {
        return postcodeNumberIndex.get(key);
    }
}
