package org.openstreetmap.josm.plugins.openservices;

import org.apache.commons.configuration.ConfigurationException;
import org.opengis.filter.Filter;

public abstract class AbstractOdsDataSource implements OdsDataSource {
  protected Service service;
  private FeatureMapper mapper;
  private Filter filter;
  private IdFactory idFactory;
  
  @Override
  public final void setService(Service service) {
    this.service = service;
  }
  
  @Override
  public Service getService() {
    return service;
  }

  @Override
  public void setFilter(Filter filter) throws ConfigurationException {
    this.filter = filter;
  }

  @Override
  public Filter getFilter() {
    return filter;
  }

  
  @Override
  public void setIdFactory(DefaultIdFactory idFactory) {
    this.idFactory = idFactory;
  }

  @Override
  public IdFactory getIdFactory() {
    if (idFactory == null) {
      idFactory = new DefaultIdFactory(this);
    }
    return idFactory;
  }

  @Override
  public String getFeatureType() {
    return service.getFeatureName();
  }
  
  @Override
  public FeatureMapper getFeatureMapper() {
    if (mapper == null) {
      try {
        String typeName = service.getFeatureType().getName().getLocalPart();
        mapper = OpenDataServices.getFeatureMapper(typeName);
        mapper.setObjectFactory(new JosmObjectFactory(service.getSRID()));
      } catch (ConfigurationException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return mapper;
  }
}
