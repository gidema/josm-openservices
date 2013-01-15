package org.openstreetmap.josm.plugin.openservices.arcgis.rest;

public class ArcgisRestField {
  private String name;
  private String type;
  private String alias;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }
}
