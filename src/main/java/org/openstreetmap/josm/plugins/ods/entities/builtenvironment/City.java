package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;

import org.openstreetmap.josm.plugins.ods.entities.Entity;

import com.vividsolutions.jts.geom.MultiPolygon;

public interface City extends Entity {
  String TYPE = "ods:city";
  public String getName();
  public MultiPolygon getGeometry();
}
