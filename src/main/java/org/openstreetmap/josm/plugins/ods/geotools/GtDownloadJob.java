package org.openstreetmap.josm.plugins.ods.geotools;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
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
import org.openstreetmap.josm.plugins.ods.DownloadJob;
import org.openstreetmap.josm.plugins.ods.ImportDataLayer;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.entities.BuildException;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.imported.ImportedEntityBuilder;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;

public class GtDownloadJob implements DownloadJob {
    GtDataSource dataSource;
    ImportDataLayer dataLayer;
    Bounds bounds;
    SimpleFeatureSource featureSource;
    Filter filter;
    MetaData metaData;
    Set<Entity> newEntities;

    protected GtDownloadJob(GtDataSource dataSource, ImportDataLayer dataLayer, Bounds bounds, Set<Entity> newEntities) {
        super();
        this.dataSource = dataSource;
        this.dataLayer = dataLayer;
        this.bounds = bounds;
        this.metaData = dataSource.getMetaData();
        this.newEntities = newEntities;
    }
    
//    protected GtDownloadJob(GtDataSource dataSource, Bounds bounds) {
//        super();
//        this.dataSource = dataSource;
//        this.bounds = bounds;
//        this.metaData = dataSource.getMetaData();
//    }

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
                    // gtFeatureSource.initialize();
                    String geometryProperty = gtFeatureSource.getFeatureType()
                            .getGeometryDescriptor().getLocalName();
                    // TODO Find faster solution for the following line
                    ReferencedEnvelope bbox = CRSUtil.getInstance().createBoundingBox(
                            gtFeatureSource.getCrs(), bounds);
                    Filter bboxFilter = ff.bbox(ff.property(geometryProperty),
                            bbox);
                    Filter dataFilter = dataSource.getFilter();
                    filter = bboxFilter;
                    // if (dataFilter != null) {
                    // filter = ff.and(filter, dataFilter);
                    // }
                    featureSource = gtFeatureSource.getFeatureSource();
                } catch (Exception e) {
                    throw new ExecutionException(e.getMessage(), e.getCause());
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
                List<SimpleFeature> featureList;
                try {
                    SimpleFeatureCollection featureCollection = featureSource.getFeatures(filter);
                    featureList = new LinkedList<SimpleFeature>();
                    it = featureCollection.features();
                    // retrieve all features
                    while (!Thread.interrupted() && it.hasNext()) {
                        featureList.add(it.next());
                    }
                } catch (Exception e) {
                    throw new ExecutionException(e.getMessage(), e.getCause());
                } finally {
                    if (it != null)
                        it.close();
                }
                ImportedEntityBuilder builder = dataSource.getEntityBuilder();
                try {
                    for (SimpleFeature feature : featureList) {
                        Entity entity = builder.build(feature);
                        if (dataLayer.getEntitySet().add(entity)) {
                            newEntities.add(entity);
                        };
                    }
                } catch (BuildException e) {
                    throw new ExecutionException(e.getMessage(), e.getCause());
                }
                return null;
            }
        };
    }

    @Override
    public Set<Entity> getNewEntities() {
        return newEntities;
    }
    // @Override
    // public OdsFeatureSet getFeatureSet() {
    // return featureSet;
    // }
}
