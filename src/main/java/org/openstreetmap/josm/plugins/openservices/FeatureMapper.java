package org.openstreetmap.josm.plugins.openservices;

import java.util.List;

import org.opengis.feature.Feature;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.openservices.metadata.MetaData;

/**
 * A FeatureMapper maps GeoTools features to a Collection of Josm primitives.
 * 
 * @author Gertjan Idema
 * 
 */
public interface FeatureMapper {
  
  
  /**
   * Set the metaData context for this mapper
   * 
   * @param context
   */
  public void setContext(MetaData context);
 
  /**
   * Get the name of the feature this mapper can handle.
   * 
   * @return The name of the feature
   */
  public String getFeatureName();

  /**
   * Create one ore more Josm primitives from the provided
   * feature. Add them to the dataSet and return them as
   * a list.
   *  
   * @param feature
   * @param dataSet
   * @return
   */
  public List<OsmPrimitive> mapFeature(Feature feature, DataSet dataSet);

  /**
   * Set the josmObjectFactory for this featureMapper.
   * 
   * @param josmObjectFactory
   */
  public void setObjectFactory(JosmObjectFactory josmObjectFactory);
}
