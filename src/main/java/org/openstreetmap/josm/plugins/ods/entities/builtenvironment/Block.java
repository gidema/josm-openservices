package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;

import java.util.Set;

import com.vividsolutions.jts.geom.Geometry;

public interface Block {
    public Integer getId();

    public BlockStore getStore();

    public Geometry getGeometry();

    public void add(Building building);

    public Set<Building> getInternalBuildings();

    public Set<Building> getExternalBuildings();

    public Set<AddressNode> getAddresses();

    public void merge(Block block);

    public boolean isIncomplete();
    
}
