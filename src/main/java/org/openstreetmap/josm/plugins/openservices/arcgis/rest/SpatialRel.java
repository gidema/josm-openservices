package org.openstreetmap.josm.plugins.openservices.arcgis.rest;

enum SpatialRel {
  INTERSECTS("esriSpatialRelIntersects"),
  CONTAINS("esriSpatialRelContains"),
  CROSSES("esriSpatialRelCrosses"),
  ENVELOPE_INTERSECTS("esriSpatialRelEnvelopeIntersects"),
  INDEX_INTERSECTS("esriSpatialRelIndexIntersects"),
  OVERLAPS("esriSpatialRelOverlaps"),
  TOUCHES("esriSpatialRelTouches"),
  WITHIN("esriSpatialRelWithin");
  
  private String name;

  private SpatialRel(String name) {
    this.name = name;
  }
  
  @Override
  public String toString() {
    return name;
  }  
}