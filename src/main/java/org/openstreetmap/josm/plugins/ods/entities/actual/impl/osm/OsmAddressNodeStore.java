package org.openstreetmap.josm.plugins.ods.entities.actual.impl.osm;

import org.openstreetmap.josm.plugins.ods.entities.EntityStore;
import org.openstreetmap.josm.plugins.ods.entities.GeoIndex;
import org.openstreetmap.josm.plugins.ods.entities.GeoIndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.Index;
import org.openstreetmap.josm.plugins.ods.entities.IndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.UniqueIndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.actual.AddressNode;

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
public class OsmAddressNodeStore extends EntityStore<AddressNode> {
    private UniqueIndexImpl<AddressNode> primitiveIndex = new UniqueIndexImpl<>(AddressNode.class, "primaryId");
    private GeoIndex<AddressNode> geoIndex = new GeoIndexImpl<>(AddressNode.class, "geometry");
    private Index<AddressNode> zipHousnrIndex = new IndexImpl<>(AddressNode.class, "postcode", "houseNumber");

    public OsmAddressNodeStore() {
        addIndex(primitiveIndex);
        addIndex(zipHousnrIndex);
        addIndex(geoIndex);
    }
    
    @Override
    public UniqueIndexImpl<AddressNode> getPrimaryIndex() {
        return primitiveIndex;
    }

    @Override
    public IndexImpl<AddressNode> getIdIndex() {
        return null;
    }

    @Override
    public GeoIndex<AddressNode> getGeoIndex() {
        return geoIndex;
    }

}
