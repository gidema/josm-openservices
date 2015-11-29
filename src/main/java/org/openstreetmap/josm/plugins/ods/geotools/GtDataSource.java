package org.openstreetmap.josm.plugins.ods.geotools;

import org.geotools.data.Query;
import org.openstreetmap.josm.plugins.ods.DefaultOdsDataSource;
import org.openstreetmap.josm.plugins.ods.OdsFeatureSource;

public class GtDataSource extends DefaultOdsDataSource {
  
  public GtDataSource(OdsFeatureSource odsFeatureSource, Query query) {
    super(odsFeatureSource, query);
  }
}
