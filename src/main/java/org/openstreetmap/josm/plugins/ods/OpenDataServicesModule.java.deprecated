package org.openstreetmap.josm.plugins.ods;

import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtilProj4j;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class OpenDataServicesModule extends AbstractModule {
    private OpenDataServicesPlugin plugin;
    
    public OpenDataServicesModule(OpenDataServicesPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Module configuration
     * 
     * @author Gertjan Idema <mail@gertjanidema.nl>
     *
     */
    @Override
    protected void configure() {
        bind(OpenDataServicesPlugin.class).toInstance(plugin);
        bind(OpenDataServices.class).in(Singleton.class);
        // Use an instance of CRSUtilProj4j to implement CRSUtil
        bind(CRSUtil.class).to(CRSUtilProj4j.class).in(Singleton.class);
    }
}
