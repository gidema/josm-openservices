package org.openstreetmap.josm.plugins.ods.geotools;

import java.io.IOException;

import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.DefaultFeatureCollection;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.openstreetmap.josm.plugins.ods.InitializationException;
import org.openstreetmap.josm.plugins.ods.Normalisation;
import org.openstreetmap.josm.plugins.ods.crs.CRSException;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureDownloader;
import org.openstreetmap.josm.plugins.ods.geotools.impl.PagingFeatureReader;
import org.openstreetmap.josm.plugins.ods.geotools.impl.SimpleFeatureReader;
import org.openstreetmap.josm.plugins.ods.io.DownloadRequest;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;
import org.openstreetmap.josm.plugins.ods.io.Status;
import org.openstreetmap.josm.plugins.ods.parsing.FeatureParser;
import org.openstreetmap.josm.tools.I18n;
import org.openstreetmap.josm.tools.Logging;

import com.vividsolutions.jts.geom.Geometry;

public class GtDownloader implements FeatureDownloader {
    private final GtDataSource dataSource;
    private final CRSUtil crsUtil;
    private DownloadRequest request;
    private DownloadResponse response;
    private SimpleFeatureSource featureSource;
    private Query query;
    DefaultFeatureCollection downloadedFeatures;
    private final Status status = new Status();
    private final FeatureParser parser;
    private Normalisation normalisation = Normalisation.FULL;

    public GtDownloader(GtDataSource dataSource, CRSUtil crsUtil,
            FeatureParser parser) {
        super();
        this.dataSource = dataSource;
        this.crsUtil = crsUtil;
        this.parser = parser;
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
            GtFeatureSource gtFeatureSource = dataSource.getFeatureSource();
            // TODO check if selected boundaries overlap with
            // featureSource boundaries;
            GtQuery gtQuery = dataSource.getQuery();
            featureSource = gtFeatureSource.getFeatureSource();
            FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
            String geometryProperty = gtFeatureSource.getFeatureType()
                    .getGeometryDescriptor().getLocalName();
            Filter filter = ff.intersects(ff.property(geometryProperty),
                    ff.literal(getArea()));
            Filter dataFilter = gtQuery.getFilter();
            if (dataFilter != null) {
                filter = ff.and(filter, dataFilter);
            }
            this.query = new Query(featureSource.getSchema().getTypeName(),
                    filter, gtQuery.getProperties());
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
        if (dataSource.getQuery().getPageSize() > 0) {
            reader = new PagingFeatureReader(dataSource, query);
        } else {
            reader = new SimpleFeatureReader(dataSource, query);
        }
        try {
            reader.read((f) -> {
                // FeatureUtil.normalizeFeature(f, normalisation);
                downloadedFeatures.add(f);
            }, null);
        } catch (DataCutOffException e) {
            String featureType = dataSource.getFeatureType();
            status.setMessage(
                    I18n.tr("To many {0} objects. Please choose a smaller download area.",
                            featureType));
            status.setCancelled(true);
            Thread.currentThread().interrupt();
            downloadedFeatures.clear();
        } catch (IOException e) {
            Logging.warn(e);
            status.setException(e);
            return;
        }
        if (downloadedFeatures.isEmpty()) {
            if (dataSource.isRequired()) {
                String featureType = dataSource.getFeatureType();
                status.setMessage(
                        I18n.tr("The selected download area contains no {0} objects.",
                                featureType));
            }
        }
        this.response = new DownloadResponse(request);
        this.response.setStatus(status);
        // if (!status.isSucces()) {
        // Thread.currentThread().interrupt();
        // return;
        // }
    }

    @Override
    public void process() {
        parser.parse(downloadedFeatures, response);
    }

    public GtDataSource getDataSource() {
        return dataSource;
    }

    @Override
    public void cancel() {
        status.setCancelled(true);
    }
}
