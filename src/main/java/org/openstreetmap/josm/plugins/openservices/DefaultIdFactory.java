package org.openstreetmap.josm.plugins.openservices;

import java.io.Serializable;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

public class DefaultIdFactory implements IdFactory {
  private final OdsDataSource dataSource;
  private String keyAttribute;

  public DefaultIdFactory(OdsDataSource dataSource) {
    super();
    this.dataSource = dataSource;
  }

  public final void setKeyAttribute(String keyAttribute) {
    this.keyAttribute = keyAttribute;
  }

  @Override
  public Serializable getId(SimpleFeature feature) {
    if (keyAttribute == null) {
      return feature.getIdentifier().getID();
    }
    try {
      return (Serializable)feature.getAttribute(keyAttribute);
    } catch (ClassCastException e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public SimpleFeatureType getFeatureType() {
    return (SimpleFeatureType) dataSource.getService().getFeatureType();
  }
}
