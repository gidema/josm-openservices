package org.openstreetmap.josm.plugins.ods.geotools;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.entities.external.ExternalDownloadTask;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;

public class GtDownloadTask implements ExternalDownloadTask {
    GtDataSource dataSource;
//    ExternalDataLayer dataLayer;
    Bounds bounds;
    SimpleFeatureSource featureSource;
    Filter filter;
    MetaData metaData;
    List<SimpleFeature> features;
//    EntityStore entityStore;

    protected GtDownloadTask(GtDataSource dataSource, Bounds bounds) {
        super();
        this.dataSource = dataSource;
//        this.dataLayer = dataLayer;
        this.bounds = bounds;
        this.metaData = dataSource.getMetaData();
    }
    
    @Override
    public GtDataSource getDataSource() {
        return dataSource;
    }

//    @Override
//    public EntityStore getEntityStore() {
//        return entityStore;
//    }
//    
    @Override
    public Callable<?> getPrepareCallable() {
        return new Callable<Object>() {

            @Override
            public Object call() throws ExecutionException {
                try {
                    dataSource.initialize();
                    metaData = dataSource.getMetaData();
                    GtFeatureSource gtFeatureSource = (GtFeatureSource) dataSource
                            .getOdsFeatureSource();
                    // TODO check if selected boundaries overlap with
                    // featureSource boundaries;
                    FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
                    String geometryProperty = gtFeatureSource.getFeatureType()
                            .getGeometryDescriptor().getLocalName();
                    // TODO Find faster solution for the following line
                    ReferencedEnvelope bbox = CRSUtil.getInstance().createBoundingBox(
                            gtFeatureSource.getCrs(), bounds);
                    Filter bboxFilter = ff.bbox(ff.property(geometryProperty),
                            bbox);
                    Filter dataFilter = dataSource.getFilter();
                    filter = bboxFilter;
                    if (dataFilter != null) {
                        filter = ff.and(filter, dataFilter);
                    }
                    featureSource = gtFeatureSource.getFeatureSource();
                } catch (Exception e) {
                    throw new ExecutionException(e.getCause().getMessage(), e.getCause());
                }
                return null;
            }
        };
    }

    @Override
    public Callable<?> getDownloadCallable() {
        return new Callable<Object>() {

            @Override
            public Object call() throws ExecutionException {
                SimpleFeatureIterator it = null;
                try {
                    SimpleFeatureCollection featureCollection = featureSource.getFeatures(filter);
                    features = new LinkedList<SimpleFeature>();
                    it = featureCollection.features();
                    // retrieve all features
                    while (!Thread.interrupted() && it.hasNext()) {
                        features.add(it.next());
                    }
                } catch (Exception e) {
                    throw new ExecutionException(e.getMessage(), e.getCause());
                } finally {
                    if (it != null)
                        it.close();
                }
                return null;
            }
        };
    }

    
//    @Override
//    public List<Entity> buildEntities(EntityFactory entityFactory) throws BuildException {
//        List<Entity> entities = new LinkedList<>();
//        List<Issue> issues = new LinkedList<>();
//        String entityType = dataSource.getEntityType();
////        try {
//            for (SimpleFeature feature : features) {
//                Entity entity = entityFactory.createEntity(entityType, feature);
//                if (entityStore.get(entity.getId()) == null) {
//                    entityStore.add(entity);
//                    entities.add(entity);
//                }
//            }
////        } catch (BuildException e) {
////            issues.add(e.getIssue());
////        }
//        if (!issues.isEmpty()) {
//            throw new BuildException(issues);
//        }
//        return entities;
//    }

    @Override
    public List<SimpleFeature> getFeatures() {
        return features;
    }
}
