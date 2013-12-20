package org.openstreetmap.josm.plugins.ods.entities.external;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Address;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Building;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.City;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Street;

public abstract class ExternalAddress implements Address {
    private SimpleFeature feature;
    private City city;
    private Building building;
    private Street street;
    private String postcode;
    private String houseNumber;
    private String houseName;
    
    public ExternalAddress(SimpleFeature feature) {
        super();
        this.feature = feature;
    }

    public SimpleFeature getFeature() {
        return feature;
    }

    @Override
	public City getCity() {
		return city;
	}

	public Building getBuilding() {
		return building;
	}

	public void setBuilding(Building building) {
		this.building = building;
	}

	@Override
	public Street getStreet() {
	    return street;
	}
	
	@Override
	public String getStreetName() {
	    if (getStreet() == null) return null;
        return getStreet().getName();
	}

	@Override
	public String getPostcode() {
		return postcode;
	}

	@Override
	public String getHouseNumber() {
		return houseNumber;
	}

	@Override
	public String getHouseName() {
		return houseName;
	}

	public void setPlace(City city) {
		this.city = city;
	}

	public void setStreet(Street street) {
		this.street = street;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public void setHouseNumber(String houseNumber) {
		this.houseNumber = houseNumber;
	}

	public void setHouseName(String houseName) {
		this.houseName = houseName;
	}
	
    public void buildTags(OsmPrimitive primitive) {
		primitive.put("addr:housenumber", getHouseNumber());
		primitive.put("addr:postcode", getPostcode());
		primitive.put("addr:street", getStreetName());
		primitive.put("addr:city", getPlaceName());
	}
	
    public String getPlaceName() {
        if (getCity() == null) return null;
        return getCity().getName();
    }
}
