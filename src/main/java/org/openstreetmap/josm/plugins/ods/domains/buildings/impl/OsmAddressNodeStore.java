package org.openstreetmap.josm.plugins.ods.domains.buildings.impl;

import org.openstreetmap.josm.plugins.ods.domains.buildings.OsmAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.matching.PcHousenumberAddressKey;
import org.openstreetmap.josm.plugins.ods.entities.GeoIndex;
import org.openstreetmap.josm.plugins.ods.entities.impl.GeoIndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.impl.OneOrManyIndex;
import org.openstreetmap.josm.plugins.ods.entities.impl.ZeroOneMany;
import org.openstreetmap.josm.plugins.ods.entities.storage.AbstractOsmEntityStore;

/**
 * Store address nodes created from osm primitives.
 * This store has .. indexes:
 *   primitiveIndex. This is also the primary index and indexes the unique primitiveId.
 *   geoIndex. The geographical index on the addressNodes
 *   zipHousenrIndex. An index on the zipcode and the numerical part of the
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class OsmAddressNodeStore extends AbstractOsmEntityStore<OsmAddressNode> {
    private final GeoIndex<OsmAddressNode> geoIndex = new GeoIndexImpl<>(OsmAddressNode.class, OsmAddressNode::getGeometry);
    private final OneOrManyIndex<OsmAddressNode, PcHousenumberAddressKey> pcHousenrIndex =
            new OneOrManyIndex<>(PcHousenumberAddressKey::new);

    public OsmAddressNodeStore() {
        super();
        addIndex(pcHousenrIndex);
        addIndex(geoIndex);
    }

    @Override
    public GeoIndex<OsmAddressNode> getGeoIndex() {
        return geoIndex;
    }

    public ZeroOneMany<OsmAddressNode> lookup(PcHousenumberAddressKey key) {
        return pcHousenrIndex.get(key);
    }
}
