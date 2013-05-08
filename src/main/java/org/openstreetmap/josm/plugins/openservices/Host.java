package org.openstreetmap.josm.plugins.openservices;

import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.plugins.openservices.metadata.MetaData;
import org.openstreetmap.josm.plugins.openservices.metadata.MetaDataException;
import org.openstreetmap.josm.plugins.openservices.metadata.MetaDataLoader;

public abstract class Host {
  private String name;
  private String type;
  private String url;
  private MetaData metaData;
  private final List<MetaDataLoader> metaDataLoaders = new LinkedList<MetaDataLoader>();
  private Boolean initialized = false;

  public final String getName() {
    return name;
  }

  public final void setName(String name) {
    this.name = name;
  }

  public final String getUrl() {
    return url;
  }

  public final void setUrl(String url) {
    this.url = url;
  }

  public final String getType() {
    return type;
  }

  public final void setType(String type) {
    this.type = type;
  }
  
  public void addMetaDataLoader(MetaDataLoader metaDataLoader) {
    metaDataLoaders.add(metaDataLoader);
  }

  public MetaData getMetaData() {
    if (metaData == null) {
      metaData = new MetaData();
    }
    return metaData;
  }
  
  public boolean equals(Host other) {
    return other.getName().equals(name)
        && other.getType().equals(type)
        && other.getUrl().equals(url);
  }

  public void init() throws ServiceException {
    if (!initialized) {
      metaData = new MetaData();
      for (MetaDataLoader loader : metaDataLoaders) {
        try {
          loader.populateMetaData(metaData);
        } catch (MetaDataException e) {
          throw new ServiceException(e);
        }
      }
      initialized = true;
    }    

  }

  public abstract boolean hasFeatureType(String feature) throws ServiceException;

  public abstract Service getService(String feature) throws ServiceException;
}
