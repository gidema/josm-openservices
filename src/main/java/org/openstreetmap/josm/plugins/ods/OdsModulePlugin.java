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
    private OdsModule module;
    
    public OdsModulePlugin(PluginInformation info, OdsModule module) throws Exception {
        super(info);
        this.module = module;
        OpenDataServicesPlugin.INSTANCE.registerModule(getModule());
        module.setPlugin(this);
//        module.initialize();
    }
    
    public OdsModule getModule() {
        return module;
    };

}
