package org.openstreetmap.josm.plugins.openservices;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.FeatureCollection;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

public class FeatureStore {
  private final Map<Serializable, SimpleFeature> featureStore = 
      new HashMap<Serializable, SimpleFeature>();
  private final List<FeatureListener> featureListeners = new LinkedList<FeatureListener>();
  private final Map<SimpleFeatureType, IdFactory> idFactories = new HashMap<SimpleFeatureType, IdFactory>();

  public void addFeatureListener(FeatureListener listener) {
    featureListeners.add(listener);
  }
  
  public void addIdFactory(IdFactory idFactory) {
    idFactories.put(idFactory.getFeatureType(), idFactory);
  }
  
  /**
   * Add features to the featureStore
   * @param features the features to add
   * @return A list of all features that were added
   */
  public void addFeature(SimpleFeature feature) {
    IdFactory idFactory = idFactories.get(feature.getType());
    Serializable id = idFactory.getId(feature);

    if (!featureStore.containsKey(id)) {
      featureStore.put(id, feature);
      for (FeatureListener listener : featureListeners) {
        listener.featureAdded(feature);
      }
    }
  }

  /**
   * Add features to the featureStore
   * @param features the features to add
   * @return A list of all features that were added
   */
  public void addFeatures(FeatureCollection<?, SimpleFeature> features) {
    SimpleFeatureIterator i = (SimpleFeatureIterator) features.features();
    while (i.hasNext()) {
      SimpleFeature feature = i.next();
      addFeature(feature);
    }
  }
}
