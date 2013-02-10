package org.openstreetmap.josm.plugins.openservices;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.FeatureCollection;
import org.opengis.feature.Feature;
import org.opengis.filter.identity.FeatureId;

public class FeatureStore {
  private final Map<FeatureId, Object> featureStore = 
      new HashMap<FeatureId, Object>();

  public boolean contains(Feature feature) {
    return featureStore.containsKey(feature.getIdentifier());
  }
  
  /**
   * Add features to the featureStore
   * @param features the features to add
   * @return A list of all features that were added
   */
  public List<Feature> addFeatures(FeatureCollection<?, ?> features) {
    List<Feature> newFeatures = new LinkedList<Feature>();
    SimpleFeatureIterator i = (SimpleFeatureIterator) features.features();
    try {
      while (i.hasNext()) {
        Feature feature = i.next();
        if (!this.contains(feature)) {
          featureStore.put(feature.getIdentifier(), feature);
          newFeatures.add(feature);
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    finally {
      //i.close();
    }
    return newFeatures;
  }

}
