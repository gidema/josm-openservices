package org.openstreetmap.josm.plugins.openservices;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.osm.visitor.BoundingXYVisitor;
import org.openstreetmap.josm.plugins.openservices.entities.Entity;
import org.openstreetmap.josm.plugins.openservices.entities.EntitySet;
import org.openstreetmap.josm.plugins.openservices.entities.ImportEntityAnalyzer;
import org.openstreetmap.josm.plugins.openservices.entities.buildings.BuildingImportEntityAnalyzer;

public class OdsDownloader {
  private static final int NTHREADS = 10;

  private final OdsWorkingSet workingSet;
  private final Collection<DownloadJob> downloadJobs = new LinkedList<DownloadJob>();
  private Bounds bounds;

  protected OdsDownloader(OdsWorkingSet workingSet) {
    super();
    this.workingSet = workingSet;
  }

  public void download(Bounds bounds) throws ExecutionException, InterruptedException {
    this.bounds = bounds;
    // Create a download job for each dataSource
    downloadJobs.add(new DownloadOsmJob(workingSet, bounds));
    Set<Entity> newEntities = new HashSet<Entity>();
    for (OdsDataSource dataSource : workingSet.getDataSources().values()) {
      downloadJobs.add(dataSource.createDownloadJob(workingSet.getImportDataLayer(), bounds, newEntities));
    }
    prepareJobs();
    download();
    analyze(newEntities, bounds);
    computeBboxAndCenterScale();
  }
  
  private void prepareJobs() throws ExecutionException, InterruptedException {
    List<Future<?>> futures = new ArrayList<Future<?>>(downloadJobs.size());

    ExecutorService executor = Executors.newFixedThreadPool(NTHREADS);
    for (DownloadJob job : downloadJobs) {
      Future<?> future = executor.submit(job.getPrepareCallable());
      futures.add(future);
    }
    try {
      // Wait for all futures to finish
      for (Future<?> future : futures) {
        future.get();
      }
    } catch (InterruptedException e) {
      executor.shutdown();
      throw(e);
    } catch (ExecutionException e) {
      executor.shutdown();
      throw(e);
    }
    finally {
      executor.shutdown();
    }
  }
  
  private void download() throws ExecutionException, InterruptedException {
    workingSet.activate();
    workingSet.activateOsmLayer();
    List<Future<?>> futures = new ArrayList<Future<?>>(downloadJobs.size());
    
    ExecutorService executor = Executors.newFixedThreadPool(NTHREADS);
    for (DownloadJob job : downloadJobs) {
      Future<?> future = executor.submit(job.getDownloadCallable());
      futures.add(future);
    }
    //futures.add(osmFuture);
    try {
      // Wait for all futures to finish
      for (Future<?> future : futures) {
        future.get();
      }
      workingSet.getImportDataLayer().getEntitySet().extendBoundary(bounds);
      // Retrieve the results
      for (DownloadJob job : downloadJobs) {
        List<Exception> exceptions = job.getExceptions();
        if (exceptions.size() > 0) {
          // TODO do something
        }
//        else {
//          OdsFeatureSet featureSet = job.getFeatureSet();
//          job.g
//          if (featureSet != null) {
//            workingSet.addFeatures(featureSet, bounds);
//          }
//        }
      }
    } catch (InterruptedException e) {
      executor.shutdown();
      throw(e);
    } catch (ExecutionException e) {
      executor.shutdown();
      throw(e);
    }
    finally {
      executor.shutdown();
    }
  }

  private static String getOverpassUrl(String query, Bounds bounds) {
    String host = "http://overpass-api.de/api";
    String bbox = String.format(Locale.ENGLISH, "%f,%f,%f,%f", bounds.getMin()
        .getY(), bounds.getMin().getX(),
        bounds.getMax().getY(), bounds.getMax().getX());
    String q = query.replaceAll("\\$bbox", bbox);
    q = q.replaceAll("\\{\\{bbox\\}\\}", bbox);
    q = q.replace(";$", "");
    return String.format("%s/interpreter?data=%s;out meta;", host, q);
  }
  
  protected void computeBboxAndCenterScale() {
    BoundingXYVisitor v = new BoundingXYVisitor();
    if (bounds != null) {
      v.visit(bounds);
      Main.map.mapView.recalculateCenterScale(v);
    }
  }
  
  private void analyze(Set<Entity> newEntities, Bounds bounds) {
      EntitySet entitySet = workingSet.getImportDataLayer().getEntitySet();
      // TODO flexible configuration of analyzers
      ImportEntityAnalyzer analyzer = new BuildingImportEntityAnalyzer();
      analyzer.setEntitySet(entitySet);
      analyzer.analyzeNewEntities(newEntities, bounds);
      
  }
}
