package org.openstreetmap.josm.plugins.ods.geotools;

import org.geotools.data.DataStore;

/**
 * Builder for geotools DataStore objects;
 *  
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public interface DataStoreBuilder {
    public void setTimeOut(int timeout);
    public DataStore build();
}
