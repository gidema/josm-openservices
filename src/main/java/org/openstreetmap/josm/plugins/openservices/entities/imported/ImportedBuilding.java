package org.openstreetmap.josm.plugins.openservices.entities.imported;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openstreetmap.josm.plugins.openservices.PrimitiveBuilder;
import org.openstreetmap.josm.plugins.openservices.entities.builtenvironment.Address;
import org.openstreetmap.josm.plugins.openservices.entities.builtenvironment.Block;
import org.openstreetmap.josm.plugins.openservices.entities.builtenvironment.Building;
import org.openstreetmap.josm.plugins.openservices.entities.builtenvironment.Place;

import com.vividsolutions.jts.geom.Polygon;

public abstract class ImportedBuilding extends ImportedEntity implements Building {
    private Polygon geometry;
    private Place place;
    private Block block;
    private Set<Address> addresses = new HashSet<Address>();
    
	@Override
	public String getNamespace() {
		return Building.NAMESPACE;
	}

	@Override
	public void createPrimitives(PrimitiveBuilder builder) {
		if (getPrimitives() == null) {
			setPrimitives(builder.build(getGeometry(), getKeys()));
		}
	}

	protected Map<String, String> getKeys() {
		Map<String, String> keys = new HashMap<String, String>();
		keys.put("building", "yes");
        return keys;	
	}
	
	@Override
	public Polygon getGeometry() {
		return geometry;
	}

	@Override
	public Place getPlace() {
		return place;
	}
	
	@Override
	public Block getBlock() {
		return block;
	}

	@Override
	public Set<Address> getAddresses() {
		return addresses;
	}

	public void setGeometry(Polygon geometry) {
		this.geometry = geometry;
	}

	public void setPlace(Place place) {
		this.place = place;
	}

	public void setBlock(Block block) {
		this.block = block;
	}

    @Override
    public boolean isComplete() {
        return getEntitySet().getBoundary().covers(getGeometry());
    }
}
