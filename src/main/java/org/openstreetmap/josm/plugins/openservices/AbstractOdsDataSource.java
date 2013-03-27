package org.openstreetmap.josm.plugins.openservices;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;
import org.openstreetmap.josm.data.Bounds;

public abstract class AbstractOdsDataSource implements OdsDataSource {
  protected Service service;
  private final Map<Serializable, SimpleFeature> featureCollection = new HashMap<Serializable, SimpleFeature>();
  private FeatureMapper mapper;
  private final List<FeatureListener> listeners = new LinkedList<FeatureListener>();
  private Filter filter;
  private IdFactory idFactory;
  
  @Override
  public final void setService(Service service) {
    this.service = service;
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
  public void addFeatureListener(FeatureListener featureListener) {
    listeners.add(featureListener);
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
  
  @Override
  public void addFeatures(FeatureCollection<?, SimpleFeature> features, Bounds bounds) {
    FeatureIterator<SimpleFeature> iterator = features.features();
    try {
      List<SimpleFeature> newFeatures = new LinkedList<SimpleFeature>();
      while( iterator.hasNext() ){
        SimpleFeature feature = iterator.next();
        Serializable id = idFactory.getId(feature);
        if (featureCollection.get(id) == null) {
          featureCollection.put(id,  feature);
          newFeatures.add(feature);
        }
      }
      for (FeatureListener listener : listeners) {
        listener.featuresAdded(newFeatures, bounds);
      }
    }
    finally {
       iterator.close();
    }
  }
}
