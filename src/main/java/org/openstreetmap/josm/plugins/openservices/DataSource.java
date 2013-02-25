package org.openstreetmap.josm.plugins.openservices;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.geotools.feature.FeatureCollection;
import org.opengis.feature.Feature;
import org.openstreetmap.josm.plugins.openservices.wfs.WFSDownloadTask;
import org.openstreetmap.josm.plugins.openservices.wfs.WFSService;

public class DataSource {
  private final FeatureStore store = new FeatureStore();
  private final List<Service> services = new LinkedList<Service>();
  private final Set<ServiceLayer> layers = new HashSet<ServiceLayer>();
  private final List<FutureTask<?>> tasks = new LinkedList<FutureTask<?>>();

  private String name;

  protected void setName(String name) {
    this.name = name;
  }

  protected String getName() {
    return name;
  }

  public void addService(Service service) {
    services.add(service);
  }
  
  public void addLayer(ServiceLayer layer) {
    layers.add(layer);
  }
  
  public Service getService(String featureName) {
    for (Service service : services) {
      if (service.getFeatureType().getName().getLocalPart().equals(featureName)) {
        return service;
      }
    }
    return null;
  }

  public List<Feature> addFeatures(FeatureCollection<?, ?> features, Service service) {
    List<Feature> newFeatures = store.addFeatures(features);
    for (ServiceLayer layer: layers) {
      layer.addFeatures(service, newFeatures);
    }

    return store.addFeatures(features);
  }

  public List<WFSDownloadTask> getTasks() {
    List<WFSDownloadTask> tasks = new LinkedList<WFSDownloadTask>();
    for (int i = 0; i < services.size(); i++) {
      Service service = services.get(i);
      tasks.add(new WFSDownloadTask((WFSService) service, this));
    }
    return tasks;
  }
  
  
  public void postDownload() {
    if (tasks == null) {
      return;
    }
    for (int i = 0; i < services.size(); i++) {
      @SuppressWarnings("unchecked")
      FutureTask<FeatureCollection<?, ?>> task = (FutureTask<FeatureCollection<?, ?>>) tasks.get(i);
      if (!task.isCancelled()) {
        try {
          FeatureCollection<?, ?> featureCollection = task.get();
          List<Feature> newFeatures = store.addFeatures(featureCollection);
          for (ServiceLayer layer: layers) {
            layer.addFeatures(services.get(i), newFeatures);
          }
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (ExecutionException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      } 
    }
  }
}
