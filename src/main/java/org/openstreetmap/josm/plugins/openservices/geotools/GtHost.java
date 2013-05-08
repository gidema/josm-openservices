package org.openstreetmap.josm.plugins.openservices.geotools;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.geotools.data.DataStore;
import org.openstreetmap.josm.plugins.openservices.Host;
import org.openstreetmap.josm.plugins.openservices.Service;
import org.openstreetmap.josm.plugins.openservices.ServiceException;

/**
 * Class to represent a WFS service host.
 * @author Gertjan Idema
 *
 */
public abstract class GtHost extends Host {
  private List<String> featureTypes;
  
  protected List<String> getFeatureTypes() throws GtException {
    if (featureTypes == null) {
      try {
        featureTypes = Arrays.asList(getDataStore().getTypeNames());
      } catch (IOException e) {
        throw new GtException("Unable to retreive features from service");
      }
    }
    return featureTypes;
  }

  /**
   * @return the DataStore object
   * @throws GtException 
   */
  public abstract DataStore getDataStore() throws GtException;
  
  @Override
  public boolean hasFeatureType(String type) throws ServiceException {
    init();
    return getFeatureTypes().contains(type);
  }

  @Override
  public Service getService(String feature) {
    GtService service = new GtService();
    service.setHost(this);
    service.setFeatureName(feature);
    return service;
  }
}
