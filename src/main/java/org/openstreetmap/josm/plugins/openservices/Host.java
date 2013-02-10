package org.openstreetmap.josm.plugins.openservices;

public abstract class Host {
  private String name;
  private String url;
  private HostType hostType;

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

  public final HostType getHostType() {
    return hostType;
  }

  public final void setHostType(HostType hostType) {
    this.hostType = hostType;
  }
  
  public boolean equals(Host other) {
    return other.getName().equals(name)
        && other.getHostType().equals(hostType)
        && other.getUrl().equals(url);
  }

  public abstract void init() throws ServiceException;

  public abstract boolean hasFeatureType(String feature) throws ServiceException;

  public abstract Service getService(String feature);
}
