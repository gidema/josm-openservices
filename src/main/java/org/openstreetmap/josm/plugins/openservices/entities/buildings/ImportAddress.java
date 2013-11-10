package org.openstreetmap.josm.plugins.openservices.entities.buildings;

import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.josm.plugins.openservices.PrimitiveBuilder;
import org.openstreetmap.josm.plugins.openservices.entities.EntitySet;
import org.openstreetmap.josm.plugins.openservices.entities.imprt.ImportEntity;

import com.vividsolutions.jts.geom.Point;

public abstract class ImportAddress extends ImportEntity implements Address {
    private EntitySet entitySet;
    private Place place;
    private Block block;
    private Building building;
    private Street street;
    private String postcode;
    private String houseNumber;
    private String houseName;
    private Point geometry;
    
	@Override
	public String getName() {
		return null;
	}

	@Override
	public String getNamespace() {
		return Address.NAMESPACE;
	}

	@Override
	public void setEntitySet(EntitySet entitySet) {
		this.entitySet = entitySet;
	}

	public EntitySet getEntitySet() {
		return entitySet;
	}

	@Override
	public Place getPlace() {
		return place;
	}

	@Override
	public Block getBlock() {
		return block;
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
	    return street.getName();
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

	public void setPlace(Place place) {
		this.place = place;
	}

	public void setBlock(Block block) {
		this.block = block;
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
	
	public Point getGeometry() {
		return geometry;
	}

	public void setGeometry(Point geometry) {
		this.geometry = geometry;
	}

	protected Map<String, String> getKeys() {
		Map<String, String> keys = new HashMap<String, String>();
		if (getHouseNumber() != null) {
			keys.put("addr:housenumber", getHouseNumber());
		}
		if (getPostcode() != null) {
			keys.put("addr:postcode", getPostcode());
		}
		if (getStreet() != null) {
			keys.put("addr:street", getStreet().getName());
		}
		if (getPlace() != null) {
			keys.put("addr:city", getPlace().getName());
		}
		return keys;
	}
	
    @Override
    public void createPrimitives(PrimitiveBuilder builder) {
        if (getPrimitives() == null) {
            setPrimitives(builder.build(getGeometry(), getKeys()));
        }
    }
}
