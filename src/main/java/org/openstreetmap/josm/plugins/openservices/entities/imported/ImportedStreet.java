package org.openstreetmap.josm.plugins.openservices.entities.imported;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.openstreetmap.josm.plugins.openservices.entities.AbstractEntity;
import org.openstreetmap.josm.plugins.openservices.entities.EntitySet;
import org.openstreetmap.josm.plugins.openservices.entities.builtenvironment.Address;
import org.openstreetmap.josm.plugins.openservices.entities.builtenvironment.Place;
import org.openstreetmap.josm.plugins.openservices.entities.builtenvironment.Street;

public class ImportedStreet extends AbstractEntity implements Street {
    private String name;
    private Set<Address> addresses = new HashSet<Address>();
    
    public ImportedStreet(String name) {
        this.name = name;
    }

    @Override
    public String getNamespace() {
        return Street.NAMESPACE;
    }

    @Override
    public Serializable getId() {
        return name;
    }

    @Override
    public void setEntitySet(EntitySet entitySet) {
        // TODO Auto-generated method stub

    }

    @Override
    public EntitySet getEntitySet() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Place getPlace() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Set<Address> getAddresses() {
        return addresses;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(100);
        sb.append(name);
        for (Address address : addresses) {
            sb.append(" ").append(address.getHouseNumber());
        }
        return sb.toString();
    }
}
