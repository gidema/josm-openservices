package org.openstreetmap.josm.plugins.openservices.tags;

import java.util.List;

import org.opengis.feature.Feature;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.openservices.JosmObjectFactory;
import org.openstreetmap.josm.plugins.openservices.metadata.MetaData;
import org.openstreetmap.josm.plugins.openservices.metadata.MetaDataException;

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
   * @throws MetaDataException 
   */
  public void setContext(MetaData context) throws MetaDataException;
 
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
