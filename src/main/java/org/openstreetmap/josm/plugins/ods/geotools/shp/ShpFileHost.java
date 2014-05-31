package org.openstreetmap.josm.plugins.ods.geotools.shp;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.ods.InitializationException;
import org.openstreetmap.josm.plugins.ods.ODS;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.geotools.GtHost;

/**
 * Geotools host for a shapefile.
 * To stay close to geotools, we use one host per file in stead of one
 * host per directory.
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class ShpFileHost extends GtHost {
    private String baseUrl;

    
    private String getBaseUrl() {
        if (baseUrl == null) {
            OdsModule module = ODS.getModule();
            Plugin plugin = (Plugin) module;
            baseUrl = plugin.getPluginDir();
        }
        return baseUrl;
    }

    @Override
    public Map<?, ?> getConnectionParameters()
            throws InitializationException {
        Map<Object, Object> parameters = new HashMap<>();
        try {
            parameters.put( "url", new File(getUrl()).toURI().toURL());
        } catch (MalformedURLException e) {
            throw new InitializationException(e);
        }
        return parameters;
    }

    @Override
    public String getUrl() {
        return getBaseUrl() +"/" + super.getUrl() + ".shp";
    }
}
