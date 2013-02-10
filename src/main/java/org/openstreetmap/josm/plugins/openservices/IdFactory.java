package org.openstreetmap.josm.plugins.openservices;

import java.io.Serializable;

public interface IdFactory {
  Serializable getId(Object object);
}
