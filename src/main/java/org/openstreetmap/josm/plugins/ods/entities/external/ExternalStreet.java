package org.openstreetmap.josm.plugins.ods.entities.external;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Address;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.City;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Street;

import com.vividsolutions.jts.geom.Geometry;

public class ExternalStreet extends ExternalEntity implements Street {
    private String fullName;
    private String cityName;
    private String streetName;
    private City city;
    private Set<Address> addresses = new HashSet<>();
    
    public ExternalStreet(String cityName, String streetName) {
        this.cityName = cityName;
        this.streetName = streetName;
        this.fullName = getFullName(cityName, streetName);
    }

    
//    @Override
//    public BuiltEnvironment getEntitySet() {
//        return (BuiltEnvironment) super.getEntitySet();
//    }


    public void build() {
        // Nothing to build
    }
    
    @Override
    public Class<? extends Entity> getType() {
        return Street.class;
    }

    @Override
    public Serializable getId() {
        return getName();
    }

    @Override
    public City getCity() {
        return city;
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
    public Geometry getGeometry() {
        return null;
    }
    
    public static String getFullName(String cityName, String streetName) {
        return streetName + "|" + cityName;
    }
    
    @Override
    protected void buildTags(OsmPrimitive primitive) {
        primitive.put("name", getStreetName());
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