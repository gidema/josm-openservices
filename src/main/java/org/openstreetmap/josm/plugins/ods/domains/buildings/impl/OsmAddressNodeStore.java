package org.openstreetmap.josm.plugins.ods.domains.buildings.impl;

import org.openstreetmap.josm.plugins.ods.domains.buildings.OsmAddressNode;
import org.openstreetmap.josm.plugins.ods.entities.EntityStore;
import org.openstreetmap.josm.plugins.ods.entities.GeoIndex;
import org.openstreetmap.josm.plugins.ods.entities.GeoIndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.Index;
import org.openstreetmap.josm.plugins.ods.entities.IndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.UniqueIndexImpl;

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
public class OsmAddressNodeStore extends EntityStore<OsmAddressNode> {
    private final UniqueIndexImpl<OsmAddressNode> primitiveIndex = new UniqueIndexImpl<>(OsmAddressNode.class, "primaryId");
    private final GeoIndex<OsmAddressNode> geoIndex = new GeoIndexImpl<>(OsmAddressNode.class, "geometry");
    private final Index<OsmAddressNode> zipHousnrIndex = new IndexImpl<>(OsmAddressNode.class, "postcode", "houseNumber");

    public OsmAddressNodeStore() {
        addIndex(primitiveIndex);
        addIndex(zipHousnrIndex);
        addIndex(geoIndex);
    }

    @Override
    public UniqueIndexImpl<OsmAddressNode> getPrimaryIndex() {
        return primitiveIndex;
    }

    @Override
    public IndexImpl<OsmAddressNode> getIdIndex() {
        return null;
    }

    @Override
    public GeoIndex<OsmAddressNode> getGeoIndex() {
        return geoIndex;
    }

}
