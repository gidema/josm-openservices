package org.openstreetmap.josm.plugins.openservices.entities.imported;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.openstreetmap.josm.plugins.openservices.entities.AbstractEntity;
import org.openstreetmap.josm.plugins.openservices.entities.builtenvironment.Address;
import org.openstreetmap.josm.plugins.openservices.entities.builtenvironment.Place;
import org.openstreetmap.josm.plugins.openservices.entities.builtenvironment.Street;

public class ImportedStreet extends AbstractEntity implements Street {
    private String fullName;
    private String cityName;
    private String streetName;
    private Place place;
    private Set<Address> addresses = new HashSet<Address>();
    
    public ImportedStreet(String cityName, String streetName) {
        this.cityName = cityName;
        this.streetName = streetName;
        this.fullName = getFullName(cityName, streetName);
    }

    
//    @Override
//    public BuiltEnvironmentEntitySet getEntitySet() {
//        return (BuiltEnvironmentEntitySet) super.getEntitySet();
//    }


    public void build() {
        // Nothing to build
    }
    
    @Override
    public String getNamespace() {
        return Street.NAMESPACE;
    }

    @Override
    public Serializable getId() {
        return getName();
    }

    @Override
    public Place getPlace() {
        return place;
    }

    @Override
    public String getName() {
        return fullName;
        
    }
    @Override
    public String getCityName() {
        return cityName;
    }

    @Override
    public String getStreetName() {
        return streetName;
    }

    @Override
    public Set<Address> getAddresses() {
        return addresses;
    }

    public static String getFullName(String cityName, String streetName) {
        return streetName + "|" + cityName;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder(100);
        sb.append(getName());
        for (Address address : addresses) {
            sb.append(" ").append(address.getHouseNumber());
        }
        return sb.toString();
    }
}
