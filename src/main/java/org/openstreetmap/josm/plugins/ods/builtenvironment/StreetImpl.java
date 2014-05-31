package org.openstreetmap.josm.plugins.ods.builtenvironment;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.builtenvironment.Address;
import org.openstreetmap.josm.plugins.ods.builtenvironment.City;
import org.openstreetmap.josm.plugins.ods.builtenvironment.Street;
import org.openstreetmap.josm.plugins.ods.entities.Entity;

import com.vividsolutions.jts.geom.Geometry;

public class StreetImpl implements Street {
    private String fullName;
    private String cityName;
    private String streetName;
    private City city;
    private Set<Address> addresses = new HashSet<>();
    
    public StreetImpl(String cityName, String streetName) {
        this.cityName = cityName;
        this.streetName = streetName;
        this.fullName = getFullName(cityName, streetName);
    }

    @Override
    public Class<? extends Entity> getType() {
        return Street.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Comparable<String> getId() {
        return getName();
    }

    @Override
    public boolean hasReferenceId() {
        return false;
    }


    @Override
    public Object getReferenceId() {
        return null;
    }

    @Override
    public City getCity() {
        return city;
    }

    public boolean hasName() {
        return true;
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

    @Override
    public boolean hasGeometry() {
        return false;
    }

    
    @Override
    public void setGeometry(Geometry geometry) {
        // TODO Auto-generated method stub
    }

    @Override
    public Geometry getGeometry() {
        return null;
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

    @Override
    public String getSource() {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public boolean isInternal() {
        return false;
    }


    @Override
    public boolean isIncomplete() {
        return false;
    }


    @Override
    public boolean isDeleted() {
        return false;
    }


    @Override
    public Collection<OsmPrimitive> getPrimitives() {
        return null;
    }

    @Override
    public Map<String, String> getOtherTags() {
        return Collections.emptyMap();
    }
}
