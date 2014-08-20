package org.openstreetmap.josm.plugins.ods;

import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;

/**
 * OdsModulePlugin is the base class for ODS modules that are
 * Josm plug-ins.
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public abstract class OdsModulePlugin extends Plugin {
    
    public OdsModulePlugin(PluginInformation info) throws Exception {
        super(info);
        OpenDataServicesPlugin ods = OpenDataServicesPlugin.INSTANCE;
        ods.registerModule(getModuleConfig());
    }
    
    public abstract OdsModuleConfig getModuleConfig();

}
