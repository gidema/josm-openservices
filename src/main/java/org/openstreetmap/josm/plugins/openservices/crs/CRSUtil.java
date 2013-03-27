package org.openstreetmap.josm.plugins.openservices.crs;

import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class CRSUtil {
  public static String getSrs(CoordinateReferenceSystem crs) {
    return getIdentifier(crs).toString();
  }

  public static Integer getSrid(CoordinateReferenceSystem crs) {
    return Integer.valueOf(getIdentifier(crs).getCode());
  }

  
  private static ReferenceIdentifier getIdentifier(CoordinateReferenceSystem crs) {
    return crs.getIdentifiers().iterator().next();
  }
}
