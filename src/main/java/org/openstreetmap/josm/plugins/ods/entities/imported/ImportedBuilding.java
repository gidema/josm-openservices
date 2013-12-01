package org.openstreetmap.josm.plugins.ods.entities.imported;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openstreetmap.josm.plugins.ods.PrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Address;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Block;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Building;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Place;

import com.vividsolutions.jts.geom.MultiPolygon;

public abstract class ImportedBuilding extends ImportedEntity implements Building {
    private MultiPolygon geometry;
    private boolean complete;
    private Place place;
    private Block block;
    private Set<Address> addresses = new HashSet<Address>();
    
    public void setComplete(boolean complete) {
        this.complete = complete;
    }
    
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
		Map<String, String> keys = new HashMap<>();
		keys.put("building", "yes");
        return keys;	
	}
	
	@Override
	public MultiPolygon getGeometry() {
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

	public void setGeometry(MultiPolygon geometry) {
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
        return complete;
//        return getEntitySet().getBoundary().covers(getGeometry());
    }
}
