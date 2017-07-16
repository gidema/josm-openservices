package org.openstreetmap.josm.plugins.ods.geotools;

import java.util.Map;
import java.util.Set;

import org.geotools.data.DataStore;
import org.openstreetmap.josm.plugins.ods.Host;
import org.openstreetmap.josm.plugins.ods.InitializationException;
import org.openstreetmap.josm.plugins.ods.OdsFeatureSource;

/**
 * Class to represent a Geotools host.
 * TODO remove this class in favour of specialised implementations like WFSHost. 
 * 
 * @author Gertjan Idema
 * 
 */
public abstract class GtHost extends Host {

    public GtHost(String name, String url, Integer maxFeatures) {
        super(name, url, maxFeatures);
    }

    protected abstract Map<?, ?> getConnectionParameters()
            throws InitializationException;

    protected abstract Set<String> getFeatureTypes();

    /**
     * @return the DataStore object
     * @throws GtException
     */
    public abstract DataStore getDataStore();

    @Override
    public boolean hasFeatureType(String type) {
        return getFeatureTypes().contains(type);
    }

    @Override
    public OdsFeatureSource getOdsFeatureSource(String feature) {
        return new GtFeatureSource(this, feature, null);
    }
}
