package org.openstreetmap.josm.plugins.ods.geotools;

import java.util.List;

import org.geotools.data.Query;
import org.opengis.filter.Filter;
import org.openstreetmap.josm.plugins.ods.DefaultOdsDataSource;
import org.openstreetmap.josm.plugins.ods.OdsFeatureSource;

public class GtDataSource extends DefaultOdsDataSource {
  
  public GtDataSource(OdsFeatureSource odsFeatureSource, Query query) {
    super(odsFeatureSource, query);
  }
}
