package org.openstreetmap.josm.plugins.openservices;

public abstract class Host {
  private String name;
  private String type;
  private String url;

  public final String getName() {
    return name;
  }

  public final void setName(String name) {
    this.name = name;
  }

  public final String getUrl() {
    return url;
  }

  public final void setUrl(String url) {
    this.url = url;
  }

  public final String getType() {
    return type;
  }

  public final void setType(String type) {
    this.type = type;
  }
  
  public boolean equals(Host other) {
    return other.getName().equals(name)
        && other.getType().equals(type)
        && other.getUrl().equals(url);
  }

  public abstract void init() throws ServiceException;

  public abstract boolean hasFeatureType(String feature) throws ServiceException;

  public abstract Service getService(String feature) throws ServiceException;
}
