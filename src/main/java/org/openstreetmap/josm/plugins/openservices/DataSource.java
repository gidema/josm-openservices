package org.openstreetmap.josm.plugins.openservices;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import org.geotools.feature.FeatureCollection;
import org.opengis.feature.Feature;
import org.openstreetmap.josm.data.Bounds;

public class DataSource {
  private final FeatureStore store = new FeatureStore();
  private final List<Service> services = new LinkedList<Service>();
  private final Set<Layer> layers = new HashSet<Layer>();
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
  
  public void addLayer(Layer layer) {
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

  public void download(Bounds bounds) {
    ExecutorService executor = Executors.newFixedThreadPool(services.size());
    FutureTask<FeatureCollection<?, ?>>[] tasks = new FutureTask[(services.size())];
    for (int i = 0; i < services.size(); i++) {
      Service service = services.get(i);
      tasks[i] = service.getDownloadTask(bounds);
      executor.execute(tasks[i]);
    }
    for (int i=0; i< services.size(); i++) {
      try {
        FeatureCollection<?, ?> featureCollection = tasks[i].get();
        List<Feature> newFeatures = store.addFeatures(featureCollection);
        for (Layer layer: layers) {
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
