package org.openstreetmap.josm.plugins.openservices;

import java.io.Serializable;

import org.opengis.feature.simple.SimpleFeature;

public class DefaultIdFactory implements IdFactory {
  private String keyAttribute;

  
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
}
