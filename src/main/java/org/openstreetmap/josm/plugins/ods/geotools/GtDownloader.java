package org.openstreetmap.josm.plugins.ods.geotools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.util.DefaultProgressListener;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureVisitor;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.ProgressListener;
import org.openstreetmap.josm.plugins.ods.Host;
import org.openstreetmap.josm.plugins.ods.crs.CRSException;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.entities.EntityStore;
import org.openstreetmap.josm.plugins.ods.entities.external.GeotoolsEntityBuilder;
import org.openstreetmap.josm.plugins.ods.io.Downloader;
import org.openstreetmap.josm.plugins.ods.io.Status;
import org.openstreetmap.josm.plugins.ods.jts.Boundary;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;
import org.openstreetmap.josm.plugins.ods.tasks.Task;
import org.openstreetmap.josm.tools.I18n;

import com.vividsolutions.jts.geom.Geometry;

public class GtDownloader implements Downloader {
    private final GtDataSource dataSource;
    private final CRSUtil crsUtil;
    private Boundary boundary;
    private SimpleFeatureSource featureSource;
    private Filter filter;
    private DefaultFeatureCollection downloadedFeatures;
    private final UniqueFeatureCollection allFeatures;
//    private DefaultFeatureCollection newFeatures = new DefaultFeatureCollection();
    private final Status status = new Status();
    private final GeotoolsEntityBuilder<?> entityBuilder;
    private final EntityStore<?> entityStore;
    private final List<Task> tasks;

    private ProgressListener listener;
    
    public GtDownloader(GtDataSource dataSource, UniqueFeatureCollection features, CRSUtil crsUtil,
            GeotoolsEntityBuilder<?> entityBuilder, EntityStore<?> entityStore ,List<Task> tasks) {
        super();
        this.dataSource = dataSource;
        allFeatures = features;
        this.crsUtil = crsUtil;
        this.entityBuilder = entityBuilder;
        this.entityStore = entityStore;
        this.tasks = (tasks != null ? tasks : new ArrayList<Task>());
    }
    
    @Override
    public void setBoundary(Boundary boundary) {
        this.boundary = boundary;
    }
    
    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public void prepare() {
        status.clear();
        entityStore.clear();
        this.downloadedFeatures = new DefaultFeatureCollection();
        try {
            // TODO rename dataSource.initialize() to prepare()
            dataSource.initialize();
            GtFeatureSource gtFeatureSource = (GtFeatureSource) dataSource
                .getOdsFeatureSource();
            // TODO check if selected boundaries overlap with
            // featureSource boundaries;
            FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
            String geometryProperty = gtFeatureSource.getFeatureType()
                .getGeometryDescriptor().getLocalName();
            featureSource = gtFeatureSource.getFeatureSource();
            filter = ff.intersects(ff.property(geometryProperty), ff.literal(getArea()));
            Filter dataFilter = dataSource.getFilter();
            if (dataFilter != null) {
                 filter = ff.and(filter, dataFilter);
            }
        } catch (Exception e) {
            status.setException(e);
        }
        return;
    }
    
    /**
     * Get the download area and transform to the desired
     * CoordinateReferenceSystem
     * 
     * @return The transformed geometry
     */
    private Geometry getArea() {
        CoordinateReferenceSystem targetCRS = featureSource.getInfo().getCRS();
        Geometry area = boundary.getMultiPolygon();
        if (!targetCRS.equals(CRSUtil.OSM_CRS)) {
            try {
                area = crsUtil.fromOsm(area, targetCRS);
            } catch (CRSException e) {
                throw new RuntimeException(e);
            }
        } 
        return area;
    }
    
    @Override
    public void download() throws InterruptedException {
        downloadedFeatures = new DefaultFeatureCollection();
//        System.out.println(featureSource.getName() + ":" + Thread.currentThread());
//        System.out.println(featureSource.getName() + ":" + Thread.currentThread().getThreadGroup());
        listener = new DefaultProgressListener();
        try {
            featureSource.getFeatures(filter).accepts(new FeatureVisitor() {
                int i=0;

                @Override
                public void visit(Feature feature) {
                    downloadedFeatures.add((SimpleFeature) feature);
                    System.out.println(i++);
                }
            }, listener);
            if (status.isCancelled()) {
                return;
            }
            if (downloadedFeatures.isEmpty() && dataSource.isRequired()) {
                String featureType = dataSource.getFeatureType();
                status.setMessage(I18n.tr("The selected download area contains no {0} objects.",
                            featureType));
                status.setCancelled(true);
            }
            else {
                Host host = dataSource.getOdsFeatureSource().getHost();
                host.getMaxFeatures();
                Integer maxFeatures = host.getMaxFeatures();
                if (maxFeatures != null && downloadedFeatures.size() >= maxFeatures) {
                    String featureType = dataSource.getFeatureType();
                    status.setMessage(I18n.tr(
                        "To many {0} objects. Please choose a smaller download area.", featureType));
                    status.setCancelled(true);
                }
            }
            if (!status.isSucces()) {
                 Thread.currentThread().interrupt();
                 return;
            }
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
//        try (SimpleFeatureIterator it = featureSource.getFeatures(filter).features()) {
//            System.out.println(featureSource.getName() + " OK");
//            // retrieve all allFeatures
//            while (it.hasNext()) {
//                if (Thread.currentThread().isInterrupted()) {
//                    status.setCancelled(true);
//                    break;
//                }
//                downloadedFeatures.add(it.next());
//            }
//            if (status.isCancelled()) {
//                return;
//            }
//            if (downloadedFeatures.isEmpty() && dataSource.isRequired()) {
//                String featureType = dataSource.getFeatureType();
//                status.setMessage(I18n.tr("The selected download area contains no {0} objects.",
//                            featureType));
//                status.setCancelled(true);
//            }
//            else {
//                Host host = dataSource.getOdsFeatureSource().getHost();
//                host.getMaxFeatures();
//                Integer maxFeatures = host.getMaxFeatures();
//                if (maxFeatures != null && downloadedFeatures.size() >= maxFeatures) {
//                    String featureType = dataSource.getFeatureType();
//                    status.setMessage(I18n.tr(
//                        "To many {0} objects. Please choose a smaller download area.", featureType));
//                    status.setCancelled(true);
//                }
//            }
//            if (!status.isSucces()) {
//                 Thread.currentThread().interrupt();
//                 return;
//            }
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//            status.setException(e);
//        }
    };
    
    @Override
    public void process() {
        MetaData metaData = dataSource.getMetaData();
        entityBuilder.setMetaData(metaData);
        for (SimpleFeature feature : downloadedFeatures) {
            if (allFeatures.add(feature)) {
                entityBuilder.buildGtEntity(feature);
            }
        }
        entityStore.extendBoundary(boundary.getMultiPolygon());
        for (Task task : tasks) {
            task.run();
        }
    }

//    public DefaultFeatureCollection getNewFeatures() {
//        return newFeatures;
//    }

    public GtDataSource getDataSource() {
        return dataSource;
    }
    
    @Override
    public void cancel() {
        if (listener != null) {
            listener.setCanceled(true);
        }
        status.setCancelled(true);
    }
}
