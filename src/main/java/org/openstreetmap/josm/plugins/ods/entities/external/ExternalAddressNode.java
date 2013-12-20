package org.openstreetmap.josm.plugins.ods.entities.external;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Block;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Building;

import com.vividsolutions.jts.geom.Point;

public abstract class ExternalAddressNode extends ExternalEntity implements AddressNode {
    private ExternalAddress address;
    private Block block;
    private Building building;
    private Point geometry;
    
    @Override
    public void build() {
        
    }
    
    public boolean isIncomplete() {
        return (building == null || building.isIncomplete());
    }
    
    public void setAddress(ExternalAddress address) {
        this.address = address;
    }

    @Override
    public ExternalAddress getAddress() {
        return address;
    }
    
	@Override
	public String getName() {
		return null;
	}

	@Override
	public Class<? extends Entity> getType() {
		return AddressNode.class;
	}

	@Override
	public Block getBlock() {
		return block;
	}

	public Building getBuilding() {
		return building;
	}

	public void setBuilding(Building building) {
		this.building =building;
	}


	public void setBlock(Block block) {
		this.block = block;
	}

	
	public Point getGeometry() {
		return geometry;
	}

	public void setGeometry(Point geometry) {
		this.geometry = geometry;
	}

    @Override
    protected void buildTags(OsmPrimitive primitive) {
        address.buildTags(primitive);
	}
}
