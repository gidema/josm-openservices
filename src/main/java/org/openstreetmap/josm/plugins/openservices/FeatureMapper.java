package org.openstreetmap.josm.plugins.openservices;

import java.util.List;

import org.opengis.feature.Feature;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.OsmPrimitive;

public interface FeatureMapper {
  public String getFeatureName();
  //public List<OsmPrimitive> mapFeature(Feature featureName);
  public List<OsmPrimitive> mapFeature(Feature feature, DataSet dataSet);
  public void setObjectFactory(JosmObjectFactory josmObjectFactory);
}
