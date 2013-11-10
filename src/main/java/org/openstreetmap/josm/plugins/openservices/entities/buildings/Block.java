package org.openstreetmap.josm.plugins.openservices.entities.buildings;

import java.util.Set;

import org.openstreetmap.josm.plugins.openservices.entities.Entity;

import com.vividsolutions.jts.geom.Polygon;

public interface Block extends Entity {
  public Place getPlace();
  public Polygon getArea();
  public Set<Building> getBuildings();
  public Set<Address> getAddresses();
}
