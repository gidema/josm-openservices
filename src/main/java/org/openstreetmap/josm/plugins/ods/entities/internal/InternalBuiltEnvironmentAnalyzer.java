package org.openstreetmap.josm.plugins.ods.entities.internal;

import java.util.List;

import org.openstreetmap.josm.plugins.ods.entities.EntitySet;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.BuiltEnvironmentAnalyzer;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Street;
import org.openstreetmap.josm.plugins.ods.entities.external.ExternalAddress;
import org.openstreetmap.josm.plugins.ods.entities.external.ExternalStreet;

public class InternalBuiltEnvironmentAnalyzer extends BuiltEnvironmentAnalyzer {
    
    public InternalBuiltEnvironmentAnalyzer(EntitySet entitySet) {
        super(entitySet);
    }

    protected void analyzeAddressStreets(List<ExternalAddress> newAddresses) {
        for (ExternalAddress address : newAddresses) {
            String fullStreetName = ExternalStreet.getFullName(
                address.getPlaceName(), address.getStreetName());
            if (fullStreetName != null) {
                Street street = getStreet(fullStreetName);
                if (street == null) {
                    street = new ExternalStreet(address.getPlaceName(), address.getStreetName());
                    getStreets().add(street);
                }
                address.setStreet(street);
                street.getAddresses().add(address);
            }
        }
    }
}
