package org.openstreetmap.josm.plugins.openservices;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;

public class FeatureStore {
  private final IdFactory idFactory;
  private final FeatureType featureType;
  private final Map<Serializable, SimpleFeature> featureStore = 
      new HashMap<Serializable, SimpleFeature>();
  private final List<FeatureListener> featureListeners = new LinkedList<FeatureListener>();
  private final Map<SimpleFeatureType, IdFactory> idFactories = new HashMap<SimpleFeatureType, IdFactory>();

  
  protected FeatureStore(IdFactory idFactory) {
    super();
    this.idFactory = idFactory;
    this.featureType = idFactory.getFeatureType();
  }

  public void addFeatureListener(FeatureListener listener) {
    featureListeners.add(listener);
  }
  
//  public void addIdFactory(IdFactory idFactory) {
//    idFactories.put(idFactory.getFeatureType(), idFactory);
//  }
  
  /**
   * Add features to the featureStore
   * @param features the features to add
   * @return A list of all features that were added
   */
  public void addFeature(SimpleFeature feature) {
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
//  public void addFeatures(FeatureCollection<?, SimpleFeature> features) {
//    SimpleFeatureIterator i = (SimpleFeatureIterator) features.features();
//    while (i.hasNext()) {
//      SimpleFeature feature = i.next();
//      addFeature(feature);
//    }
//  }
}
