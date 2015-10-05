package org.openstreetmap.josm.plugins.ods.geotools;

import java.io.IOException;
import java.util.Date;

import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.openstreetmap.josm.plugins.ods.Host;
import org.openstreetmap.josm.plugins.ods.InitializationException;
import org.openstreetmap.josm.plugins.ods.OdsDataSource;
import org.openstreetmap.josm.plugins.ods.OdsFeatureSource;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;

public class GtFeatureSource implements OdsFeatureSource {

    private boolean initialized = false;
    private final GtHost host;
    private final String featureName;
    private final String idAttribute;
    SimpleFeatureSource featureSource;
    CoordinateReferenceSystem crs;
    MetaData metaData;

    public GtFeatureSource(GtHost host, String featureName, String idAttribute) {
        super();
        this.host = host;
        this.featureName = featureName;
        this.idAttribute = idAttribute;
    }

    @Override
    public final String getFeatureName() {
        return featureName;
    }

    @Override
    public Host getHost() {
        return host;
    }

    @Override
    public void initialize() throws InitializationException {
        if (initialized)
            return;
        host.initialize();
        metaData = new MetaData(host.getMetaData());
        if (!host.hasFeatureType(featureName)) {
            throw new InitializationException(String.format(
                    "Unknown featureName type: '%s'", featureName));
        }
        try {
            featureSource = host.getDataStore().getFeatureSource(featureName);
            crs = featureSource.getInfo().getCRS();
        } catch (IOException e) {
            throw new InitializationException(e);
        }
        if (!metaData.containsKey("source.date")) {
            metaData.put("source.date", new Date());
        }
        initialized = true;
    }

    @Override
    public MetaData getMetaData() {
        return metaData;
    }

    public SimpleFeatureSource getFeatureSource() {
        return featureSource;
    }

    @Override
    public FeatureType getFeatureType() {
        assert initialized;
        return getFeatureSource().getSchema();
    }

    @Override
    public String getIdAttribute() {
        return idAttribute;
    }

    @Override
    public CoordinateReferenceSystem getCrs() {
        assert initialized;
        return crs;
    }

    @Override
    public String getSRS() {
        assert initialized;
        ReferenceIdentifier rid = crs.getIdentifiers().iterator().next();
        return rid.toString();
    }

    @Override
    public Long getSRID() {
        ReferenceIdentifier rid = crs.getIdentifiers().iterator().next();
        return Long.parseLong(rid.getCode());
    }

    @Override
    public OdsDataSource newDataSource(Filter filter) {
        return new GtDataSource(this, filter);
    }
}
