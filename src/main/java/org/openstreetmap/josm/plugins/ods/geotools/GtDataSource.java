package org.openstreetmap.josm.plugins.ods.geotools;

import org.opengis.filter.Filter;
import org.openstreetmap.josm.plugins.ods.DefaultOdsDataSource;
import org.openstreetmap.josm.plugins.ods.OdsFeatureSource;

public class GtDataSource extends DefaultOdsDataSource {
  
  public GtDataSource(OdsFeatureSource odsFeatureSource, Filter filter) {
    super(odsFeatureSource, filter);
  }
}
