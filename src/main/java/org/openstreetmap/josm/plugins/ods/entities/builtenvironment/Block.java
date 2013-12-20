package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;

import java.util.Set;

import org.openstreetmap.josm.plugins.ods.entities.Entity;

import com.vividsolutions.jts.geom.Polygon;

public interface Block extends Entity {
  public City getCity();
  public Polygon getArea();
  public Set<Building> getBuildings();
  public Set<AddressNode> getAddresses();
}
