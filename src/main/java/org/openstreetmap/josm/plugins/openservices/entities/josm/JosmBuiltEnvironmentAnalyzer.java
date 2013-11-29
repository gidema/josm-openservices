package org.openstreetmap.josm.plugins.openservices.entities.josm;

import java.util.List;

import org.openstreetmap.josm.plugins.openservices.entities.EntitySet;
import org.openstreetmap.josm.plugins.openservices.entities.builtenvironment.BuiltEnvironmentAnalyzer;
import org.openstreetmap.josm.plugins.openservices.entities.builtenvironment.Street;
import org.openstreetmap.josm.plugins.openservices.entities.imported.ImportedAddress;
import org.openstreetmap.josm.plugins.openservices.entities.imported.ImportedStreet;

public class JosmBuiltEnvironmentAnalyzer extends BuiltEnvironmentAnalyzer {
    
    public JosmBuiltEnvironmentAnalyzer(EntitySet entitySet) {
        super(entitySet);
    }

    protected void analyzeAddressStreets(List<ImportedAddress> newAddresses) {
        for (ImportedAddress address : newAddresses) {
            String fullStreetName = ImportedStreet.getFullName(
                address.getPlaceName(), address.getStreetName());
            if (fullStreetName != null) {
                Street street = getStreet(fullStreetName);
                if (street == null) {
                    street = new ImportedStreet(address.getPlaceName(), address.getStreetName());
                    getStreets().add(street);
                }
                address.setStreet(street);
                street.getAddresses().add(address);
            }
        }
    }
}
