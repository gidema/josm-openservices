package org.openstreetmap.josm.plugins.openservices.arcgis.rest;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.plugins.openservices.DownloadJob;
import org.openstreetmap.josm.plugins.openservices.ImportDataLayer;
import org.openstreetmap.josm.plugins.openservices.MappingException;
import org.openstreetmap.josm.plugins.openservices.crs.JTSCoordinateTransform;
import org.openstreetmap.josm.plugins.openservices.crs.JTSCoordinateTransformFactory;
import org.openstreetmap.josm.plugins.openservices.crs.Proj4jCRSTransformFactory;
import org.openstreetmap.josm.plugins.openservices.entities.Entity;
import org.openstreetmap.josm.plugins.openservices.entities.ImportEntityBuilder;
import org.openstreetmap.josm.plugins.openservices.metadata.MetaData;

import com.vividsolutions.jts.geom.Coordinate;

public class AGRestDownloadJob implements DownloadJob {
    AGRestDataSource dataSource;
    AGRestFeatureSource featureSource;
    Bounds bounds;
    SimpleFeatureCollection featureCollection;
    MetaData metaData;
    // OdsFeatureSet featureSet;
    ImportDataLayer dataLayer;
    Set<Entity> newEntities;

    public AGRestDownloadJob(AGRestDataSource dataSource, ImportDataLayer dataLayer, Bounds bounds, Set<Entity> newEntities) {
        this.dataSource = dataSource;
        this.dataLayer = dataLayer;
        this.bounds = bounds;
        this.newEntities = newEntities;
    }

    @Override
    public Callable<?> getPrepareCallable() {
        return new Callable<Object>() {

            @Override
            public Object call() throws ExecutionException {
                try {
                    dataSource.initialize();
                    metaData = dataSource.getMetaData();
                    featureSource = (AGRestFeatureSource) dataSource
                            .getOdsFeatureSource();
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
                List<SimpleFeature> featureList = new LinkedList<SimpleFeature>();
                try {
                    RestQuery query = getQuery();
                    AGRestReader reader = new AGRestReader(query,
                            featureSource.getFeatureType());
                    SimpleFeatureIterator it = reader.getFeatures().features();
                    while (it.hasNext()) {
                        featureList.add(it.next());
                    }
                } catch (Exception e) {
                    throw new ExecutionException(e.getMessage(), e.getCause());
                }
                ImportEntityBuilder<?> builder = dataSource.getEntityBuilder();
                try {
                    for (SimpleFeature feature : featureList) {
                        Entity entity = builder.build(feature);
                        if (dataLayer.getEntitySet().add(entity)) {
                            newEntities.add(entity);
                        };
                    }
                } catch (MappingException e) {
                    throw new ExecutionException(e.getMessage(), e.getCause());
                }
                return null;
            }
        };
    }

    // @Override
    // public OdsFeatureSet getFeatureSet() {
    // return featureSet;
    // }

    RestQuery getQuery() {
        RestQuery query = new RestQuery();
        query.setFeatureSource(featureSource);
        query.setInSR(featureSource.getSRID());
        query.setOutSR(featureSource.getSRID());
        query.setGeometry(formatBounds(bounds, query.getInSR()));
        query.setOutFields("*");
        return query;
    }

    private static String formatBounds(Bounds bounds, Long srid) {
        LatLon min = bounds.getMin();
        LatLon max = bounds.getMax();
        if (!srid.equals(4326L)) {
            Coordinate minCoord = new Coordinate(min.lon(), min.lat());
            Coordinate maxCoord = new Coordinate(max.lon(), max.lat());
            JTSCoordinateTransformFactory f = new Proj4jCRSTransformFactory();
            JTSCoordinateTransform t = f.createJTSCoordinateTransform(4326L,
                    srid);
            minCoord = t.transform(minCoord);
            maxCoord = t.transform(maxCoord);
            min = new LatLon(minCoord.y, minCoord.x);
            max = new LatLon(maxCoord.y, maxCoord.x);
        }
        return String.format(Locale.ENGLISH, "%f,%f,%f,%f", min.getX(),
                min.getY(), max.getX(), max.getY());
    }

    @Override
    public Set<Entity> getNewEntities() {
        return newEntities;
    }
}
