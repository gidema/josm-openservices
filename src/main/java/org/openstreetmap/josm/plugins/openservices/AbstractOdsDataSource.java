package org.openstreetmap.josm.plugins.openservices;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.geotools.data.memory.MemoryFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

public abstract class AbstractOdsDataSource implements OdsDataSource {
  protected Service service;
  private MemoryFeatureCollection featureCollection;
  private FeatureMapper mapper;
  private final List<FeatureListener> listeners = new LinkedList<FeatureListener>();
  
  @Override
  public final void setService(Service service) {
    this.service = service;
  }

  @Override
  public void addFeatureListener(FeatureListener featureListener) {
    listeners.add(featureListener);
  }

  @Override
  public String getFeatureType() {
    return service.getFeatureName();
  }


  private MemoryFeatureCollection getFeatureCollection() {
    if (featureCollection == null) {
      featureCollection = new MemoryFeatureCollection((SimpleFeatureType) service.getFeatureType());
    }
    return featureCollection;
  }
  
  @Override
  public FeatureMapper getFeatureMapper() {
    if (mapper == null) {
      try {
        String typeName = service.getFeatureType().getName().getLocalPart();
        mapper = OpenServices.getFeatureMapper(typeName);
        mapper.setObjectFactory(new JosmObjectFactory(service.getSRID()));
      } catch (ConfigurationException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return mapper;
  }
  
  @Override
  public void addFeatures(FeatureCollection<?, SimpleFeature> features) {
    FeatureIterator<SimpleFeature> iterator = features.features();
    try {
      List<SimpleFeature> newFeatures = new LinkedList<SimpleFeature>();
      while( iterator.hasNext() ){
        SimpleFeature feature = iterator.next();
        if (getFeatureCollection().add(feature)) {
          newFeatures.add(feature);
        }
      }
      for (FeatureListener listener : listeners) {
        listener.featuresAdded(newFeatures);
      }
    }
    finally {
       iterator.close();
    }
  }
}
