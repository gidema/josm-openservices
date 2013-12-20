package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;

import java.util.Iterator;

import org.openstreetmap.josm.plugins.ods.DataLayer;
import org.openstreetmap.josm.plugins.ods.analysis.Analyzer;
import org.openstreetmap.josm.plugins.ods.entities.EntitySet;
import org.openstreetmap.josm.plugins.ods.entities.external.ExternalStreet;


/**
 * This analyzer builds the relation between address nodes and streets.
 * Currently it doesn't apply to houseboats and static_caravans, as they
 * don't have an AddressNode assigned.
 * 
 * @author gertjan
 *
 */
public class AddressToStreetMatcher implements Analyzer {
    
    public void analyze(DataLayer dataLayer, EntitySet newEntities) {
        BuiltEnvironment existing = new BuiltEnvironment(dataLayer.getEntitySet());
        BuiltEnvironment bes = new BuiltEnvironment(newEntities);
        Iterator<AddressNode> it = bes.getAddresses().iterator();
        while (it.hasNext()) {
            AddressNode addressNode = it.next();
            Address address = addressNode.getAddress();
            String fullStreetName = ExternalStreet.getFullName(
                address.getPlaceName(), address.getStreetName());
            if (fullStreetName != null) {
                Street street = existing.getStreet(fullStreetName);
                if (street == null) {
                    street = new ExternalStreet(address.getPlaceName(), address.getStreetName());
                    existing.getEntitySet().add(street);
                }
                address.setStreet(street);
                street.getAddresses().add(address);
            }
        }
    }
}