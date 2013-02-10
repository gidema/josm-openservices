package org.openstreetmap.josm.plugins.openservices;

public class HostType {
  private final String name;
  private final String hostClass;
  
  public HostType(String name, String hostClass) {
    super();
    this.name = name;
    this.hostClass = hostClass;
  }
  
  protected String getName() {
    return name;
  }

  public boolean equals(HostType other) {
    return other.name.equals(name) && other.hostClass.equals(hostClass);
  }
  
  public Host newHost() throws ServiceException {
    try {
      Host host = (Host) (Class.forName(hostClass)).newInstance();
      host.setHostType(this);
      return host;
    } catch (Exception e) {
      throw new ServiceException(e);
    }
  }
}
