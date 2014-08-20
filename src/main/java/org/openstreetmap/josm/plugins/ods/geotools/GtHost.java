package org.openstreetmap.josm.plugins.ods.geotools;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.openstreetmap.josm.plugins.ods.Host;
import org.openstreetmap.josm.plugins.ods.InitializationException;
import org.openstreetmap.josm.plugins.ods.OdsFeatureSource;

/**
 * Class to represent a Geotools host.
 * 
 * @author Gertjan Idema
 * 
 */
public abstract class GtHost extends Host {
    private List<String> featureTypes;
    private DataStore dataStore;
    private boolean initialized = false;

    public GtHost(String name, String url, Integer maxFeatures) {
        super(name, url, maxFeatures);
    }

    @Override
    public synchronized void initialize() throws InitializationException {
        if (initialized)
            return;
        super.initialize();
        // TODO move to next line configuration fase
        Map<?, ?> connectionParameters = getConnectionParameters();
        try {
            dataStore = DataStoreFinder.getDataStore(connectionParameters);
            featureTypes = Arrays.asList(getDataStore().getTypeNames());
            initialized = true;
        } catch (IOException e) {
            throw new InitializationException(
                    "Unable to connect to the datastore", e);
        }
    }

    protected abstract Map<?, ?> getConnectionParameters()
            throws InitializationException;

    protected List<String> getFeatureTypes() {
        return featureTypes;
    }

    /**
     * @return the DataStore object
     * @throws GtException
     */
    public DataStore getDataStore() {
        return dataStore;
    }

    @Override
    public boolean hasFeatureType(String type) {
        return getFeatureTypes().contains(type);
    }

    @Override
    public OdsFeatureSource getOdsFeatureSource(String feature) {
        return new GtFeatureSource(this, feature);
    }
}
