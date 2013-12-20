package org.openstreetmap.josm.plugins.ods.entities.external;

import java.util.HashSet;
import java.util.Set;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Block;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Building;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.City;

import com.vividsolutions.jts.geom.Geometry;

public abstract class ExternalBuilding extends ExternalEntity implements Building {
    private Geometry geometry;
    private boolean incomplete;
    private City city;
    private Block block;
    private Set<AddressNode> addresses = new HashSet<AddressNode>();
    
    public void setIncomplete(boolean incomplete) {
        this.incomplete = incomplete;
    }
    
	@Override
	public Class<? extends Entity> getType() {
		return Building.class;
	}

    @Override
    protected void buildTags(OsmPrimitive primitive) {
		primitive.put("building", "yes");
	}
	
	@Override
	public Geometry getGeometry() {
		return geometry;
	}

	@Override
	public City getCity() {
		return city;
	}
	
	@Override
	public Block getBlock() {
		return block;
	}

	@Override
	public Set<AddressNode> getAddresses() {
		return addresses;
	}

	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}

	public void setPlace(City city) {
		this.city = city;
	}

	public void setBlock(Block block) {
		this.block = block;
	}

    @Override
    public boolean isIncomplete() {
        return incomplete;
//        return getEntitySet().getBoundary().covers(getGeometry());
    }
}
