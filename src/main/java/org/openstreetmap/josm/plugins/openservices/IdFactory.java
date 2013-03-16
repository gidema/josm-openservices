package org.openstreetmap.josm.plugins.openservices;

import java.io.Serializable;

import org.opengis.feature.simple.SimpleFeature;

public interface IdFactory {
  Serializable getId(SimpleFeature feature);
}
