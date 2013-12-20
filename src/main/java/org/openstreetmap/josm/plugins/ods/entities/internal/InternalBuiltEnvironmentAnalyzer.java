package org.openstreetmap.josm.plugins.ods.entities.internal;

import java.util.List;

import org.openstreetmap.josm.plugins.ods.entities.EntitySet;
import org.openstreetmap.josm.plugins.ods.entities.external.ExternalAddress;

public class InternalBuiltEnvironmentAnalyzer {
    
    public InternalBuiltEnvironmentAnalyzer(EntitySet entitySet) {
    }

    protected void analyzeAddressStreets(List<ExternalAddress> newAddresses) {
// TODO
//        for (ExternalAddress address : newAddresses) {
//            String fullStreetName = ExternalStreet.getFullName(
//                address.getPlaceName(), address.getStreetName());
//            if (fullStreetName != null) {
//                Street street = getStreet(fullStreetName);
//                if (street == null) {
//                    street = new ExternalStreet(address.getPlaceName(), address.getStreetName());
//                    getStreets().add(street);
//                }
//                address.setStreet(street);
//                street.getAddresses().add(address);
//            }
//        }
    }
}
