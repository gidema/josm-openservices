package org.openstreetmap.josm.plugins.ods.geotools;

import java.io.IOException;
import java.time.LocalDate;

import org.geotools.data.DataStore;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.type.FeatureType;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.openstreetmap.josm.plugins.ods.OdsFeatureSource;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;

import exceptions.OdsException;

public class GtFeatureSource implements OdsFeatureSource {
    private boolean initialized = false;
    private boolean available = false;
    private final GtHost host;
    private final String featureName;
    private final String idAttribute;
    private final long maxFeatures;
    private final int timeout = 60000;
    private CoordinateReferenceSystem crs;
    private MetaData metaData;
    private SimpleFeatureSource featureSource;
    private FeatureType featureType;

    public GtFeatureSource(GtHost host, String featureName, String idAttribute) {
        super();
        this.host = host;
        this.maxFeatures = host.getMaxFeatures();
        this.featureName = featureName;
        this.idAttribute = idAttribute;
    }

    @Override
    public final String getFeatureName() {
        return featureName;
    }

    @Override
    public boolean isAvailable() {
        return available;
    }

    protected void setAvailable(boolean available) {
        this.available = available;
    }
    
    @Override
    public GtHost getHost() {
        return host;
    }

    @Override
    public void initialize() throws OdsException {
        if (initialized) return;
        initialized = true;
        setAvailable(false);
        if (!getHost().isAvailable()) {
            String msg = String.format("The feature named '%s' is not accessable, " +
                "because the host %s is unavailable",
                this.getFeatureName(),
                getHost().getName());
            this.setAvailable(false);
            throw new OdsException(msg);
        }
        metaData = new MetaData(host.getMetaData());
        if (!getHost().hasFeatureType(featureName)) {
            String msg = String.format("The feature named '%s' is not known to host '%s'",
                this.getFeatureName(),
                getHost().getName());
            this.setAvailable(false);
            throw new OdsException(msg);
        }
        try {
            /*
             *  First use-a dataStore object with a short timeout, so it will fail
             *  fast if the service is not available;
             */
            DataStore dataStore = getHost().getDataStore(500);
            SimpleFeatureSource fs = dataStore.getFeatureSource(featureName);
            crs = fs.getInfo().getCRS();
            featureType = fs.getSchema();
        }
        catch (@SuppressWarnings("unused") IOException e) {
            String msg = String.format("The feature named '%s' is not accessable, " +
                    "because of a network timeout on host host %s",
                getFeatureName(),
                getHost().getName());
            throw new OdsException(msg);
        }
        /*
         * Now we know the service is available, we can set the required timeout
         */
        try {
            DataStore dataStore = host.getDataStore(timeout);
            featureSource = dataStore.getFeatureSource(featureName);
        }
        catch (@SuppressWarnings("unused") IOException e) {
            String msg = String.format("The feature named '%s' is not accessable, " +
                "because of a network timeout on host host %s",
            getFeatureName(),
            getHost().getName());
            throw new OdsException(msg);
        }
        // TODO do we want these lines here?
        if (!metaData.containsKey("source.date")) {
            metaData.put("source.date", LocalDate.now());
        }
        setAvailable(true);
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
        assert isAvailable();
        return featureType;
    }

    public long getMaxFeatureCount() {
        return maxFeatures;
    }
    
    @Override
    public String getIdAttribute() {
        return idAttribute;
    }

    @Override
    public CoordinateReferenceSystem getCrs() {
        assert isAvailable();
        return crs;
    }

    @Override
    public String getSRS() {
        assert isAvailable();
        ReferenceIdentifier rid = crs.getIdentifiers().iterator().next();
        return rid.toString();
    }

    @Override
    public Long getSRID() {
        assert isAvailable();
        ReferenceIdentifier rid = crs.getIdentifiers().iterator().next();
        return Long.parseLong(rid.getCode());
    }
}
