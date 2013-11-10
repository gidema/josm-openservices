package org.openstreetmap.josm.plugins.openservices.arcgis.rest;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Callable;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.plugins.openservices.DownloadJob;
import org.openstreetmap.josm.plugins.openservices.crs.JTSCoordinateTransform;
import org.openstreetmap.josm.plugins.openservices.crs.JTSCoordinateTransformFactory;
import org.openstreetmap.josm.plugins.openservices.crs.Proj4jCRSTransformFactory;
import org.openstreetmap.josm.plugins.openservices.entities.Entity;
import org.openstreetmap.josm.plugins.openservices.metadata.MetaData;

import com.vividsolutions.jts.geom.Coordinate;

public class AGRestDownloadJob implements DownloadJob {
    AGRestDataSource dataSource;
    AGRestFeatureSource featureSource;
    Bounds bounds;
    SimpleFeatureCollection featureCollection;
    MetaData metaData;
    // OdsFeatureSet featureSet;
    List<Exception> exceptions = new LinkedList<Exception>();
    Set<Entity> newEntities = new HashSet<Entity>();

    public AGRestDownloadJob(AGRestDataSource dataSource, Bounds bounds) {
        this.dataSource = dataSource;
        this.bounds = bounds;
    }

    @Override
    public Callable<?> getPrepareCallable() {
        return new Callable<Object>() {

            @Override
            public Object call() {
                try {
                    dataSource.initialize();
                    metaData = dataSource.getMetaData();
                    featureSource = (AGRestFeatureSource) dataSource
                            .getOdsFeatureSource();
                } catch (Exception e) {
                    exceptions.add(e);
                }
                return null;
            }

        };
    }

    @Override
    public Callable<?> getDownloadCallable() {
        return new Callable<Object>() {

            @Override
            public Object call() {
                try {
                    RestQuery query = getQuery();
                    AGRestReader reader = new AGRestReader(query,
                            featureSource.getFeatureType());
                    SimpleFeatureIterator it = reader.getFeatures().features();
                    List<SimpleFeature> featureList = new LinkedList<SimpleFeature>();
                    while (it.hasNext()) {
                        featureList.add(it.next());
                    }
                    dataSource.addFeatures(featureList);
                } catch (Exception e) {
                    exceptions.add(e);
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
    public List<Exception> getExceptions() {
        return exceptions;
    }

    @Override
    public Set<Entity> getNewEntities() {
        return newEntities;
    }
}
