package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;

import java.util.Set;

import org.openstreetmap.josm.plugins.ods.entities.Entity;

import com.vividsolutions.jts.geom.Geometry;

public interface Block extends Entity {
  public City getCity();
  public Geometry getGeometry();
  public void add(Building building);
  public Set<Building> getBuildings();
  public Set<AddressNode> getAddresses();
  public void merge(Block block);
}
