package org.openstreetmap.josm.plugins.openservices.entities.builtenvironment;

import org.openstreetmap.josm.plugins.openservices.entities.Entity;

import com.vividsolutions.jts.geom.MultiPolygon;

public interface Place extends Entity {
  String NAMESPACE = "ods.place".intern();
  public String getName();
  public MultiPolygon getGeometry();
}
