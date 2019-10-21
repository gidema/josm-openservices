package org.openstreetmap.josm.plugins.ods.geotools;

import java.io.IOException;

import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.DefaultFeatureCollection;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.openstreetmap.josm.plugins.ods.InitializationException;
import org.openstreetmap.josm.plugins.ods.Normalisation;
import org.openstreetmap.josm.plugins.ods.crs.CRSException;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.entities.EntityStore;
import org.openstreetmap.josm.plugins.ods.entities.OdEntity;
import org.openstreetmap.josm.plugins.ods.entities.OdEntityBuilder;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureDownloader;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureUtil;
import org.openstreetmap.josm.plugins.ods.geotools.impl.PagingFeatureReader;
import org.openstreetmap.josm.plugins.ods.geotools.impl.SimpleFeatureReader;
import org.openstreetmap.josm.plugins.ods.io.DownloadRequest;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;
import org.openstreetmap.josm.plugins.ods.io.Status;
import org.openstreetmap.josm.tools.I18n;
import org.openstreetmap.josm.tools.Logging;

import org.locationtech.jts.geom.Geometry;

public class GtDownloader<T extends OdEntity> implements FeatureDownloader {
    private final GtDataSource dataSource;
    private final CRSUtil crsUtil;
    private DownloadRequest request;
    private DownloadResponse response;
    private SimpleFeatureSource featureSource;
    private Query query;
    DefaultFeatureCollection downloadedFeatures;
    private final EntityStore<T> entityStore;
    private final Status status = new Status();
    private final OdEntityBuilder<T> entityBuilder;
    private Normalisation normalisation = Normalisation.FULL;

    public GtDownloader(GtDataSource dataSource, CRSUtil crsUtil,
            OdEntityBuilder<T> entityBuilder, EntityStore<T> entityStore) {
        super();
        this.dataSource = dataSource;
        this.crsUtil = crsUtil;
        this.entityBuilder = entityBuilder;
        this.entityStore = entityStore;
    }

    @Override
    public void setNormalisation(Normalisation normalisation) {
        this.normalisation = normalisation;
    }


    @Override
    public void setup(DownloadRequest request) {
        this.request = request;
    }

    @Override
    public void setResponse(DownloadResponse response) {
        this.response = response;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public void prepare() {
        status.clear();
        try {
            // TODO rename dataSource.initialize() to prepare()
            dataSource.initialize();
            GtFeatureSource gtFeatureSource = dataSource.getOdsFeatureSource();
            // TODO check if selected boundaries overlap with
            // featureSource boundaries;
            featureSource = gtFeatureSource.getFeatureSource();
            query = dataSource.getQuery();
            // Clone the query, so we can moderate the filter by setting the download area.
            query = new Query(query);
            FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
            String geometryProperty = gtFeatureSource.getFeatureType()
                    .getGeometryDescriptor().getLocalName();
            Filter filter = query.getFilter();
            filter = ff.intersects(ff.property(geometryProperty), ff.literal(getArea()));
            Filter dataFilter = dataSource.getQuery().getFilter();
            if (dataFilter != null) {
                filter = ff.and(filter, dataFilter);
            }
            query.setFilter(filter);
        } catch (InitializationException e) {
            Logging.error(e);
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
        Geometry area = request.getBoundary().getMultiPolygon();
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
    public void download() {
        downloadedFeatures = new DefaultFeatureCollection();
        GtFeatureReader reader;
        if (dataSource.getPageSize() > 0) {
            reader = new PagingFeatureReader(dataSource, query);
        }
        else {
            reader = new SimpleFeatureReader(dataSource, query);
        }
        try {
            reader.read((f) -> {
                FeatureUtil.normalizeFeature(f, normalisation);
                downloadedFeatures.add(f);
            }, null);
        } catch (DataCutOffException e) {
            String featureType = dataSource.getFeatureType();
            status.setMessage(I18n.tr(
                    "To many {0} objects. Please choose a smaller download area.", featureType));
            status.setCancelled(true);
            Thread.currentThread().interrupt();
            downloadedFeatures.clear();
        }
        catch (IOException e) {
            Logging.warn(e);
            status.setException(e);
            return;
        }
        if (downloadedFeatures.isEmpty()) {
            if (dataSource.isRequired()) {
                String featureType = dataSource.getFeatureType();
                status.setMessage(I18n.tr("The selected download area contains no {0} objects.",
                        featureType));
            }
        }
        if (!status.isSucces()) {
            Thread.currentThread().interrupt();
            return;
        }
    }

    @Override
    public void process() {
        for (SimpleFeature feature : downloadedFeatures) {
            T entity = entityBuilder.build(feature, response);
            if (!entityStore.contains(entity.getPrimaryId())) {
                entityStore.add(entity);
            }
        }
        entityStore.extendBoundary(request.getBoundary().getMultiPolygon());
    }

    public GtDataSource getDataSource() {
        return dataSource;
    }


    @Override
    public void cancel() {
        status.setCancelled(true);
    }
}
