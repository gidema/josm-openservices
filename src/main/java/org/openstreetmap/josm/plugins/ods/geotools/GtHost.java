package org.openstreetmap.josm.plugins.ods.geotools;

import java.io.IOException;

import org.geotools.data.DataStore;
import org.openstreetmap.josm.plugins.ods.Host;
import org.openstreetmap.josm.plugins.ods.OdsFeatureSource;

/**
 * Class to represent a Geotools host.
 *
 * @author Gertjan Idema
 *
 */
public interface GtHost extends Host {

    @Override
    public OdsFeatureSource getOdsFeatureSource(String feature);

    public abstract DataStore createDataStore() throws IOException;
}
