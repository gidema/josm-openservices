package org.openstreetmap.josm.plugins.ods.geotools;

import java.io.IOException;

import org.geotools.data.DataStore;
import org.openstreetmap.josm.plugins.ods.Host;
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

    @Override
    public OdsFeatureSource getOdsFeatureSource(String feature) {
        return new GtFeatureSource(this, feature, null);
    }

    public abstract DataStore createDataStore() throws IOException;
}
