package org.openstreetmap.josm.plugins.ods.geotools;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
    private Set<String> featureTypes = new HashSet<>();
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
        // TODO move next line to configuration phase
        Map<?, ?> connectionParameters = getConnectionParameters();
        try {
            dataStore = DataStoreFinder.getDataStore(connectionParameters);
            featureTypes.addAll(Arrays.asList(getDataStore().getTypeNames()));
            initialized = true;
        } catch (IOException e) {
            throw new InitializationException(
                    "Unable to connect to the datastore", e);
        }
    }

    protected abstract Map<?, ?> getConnectionParameters()
            throws InitializationException;

    protected Set<String> getFeatureTypes() {
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
        return new GtFeatureSource(this, feature, null);
    }
}
