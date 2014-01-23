package org.openstreetmap.josm.plugins.ods;

import java.io.File;

import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;

/**
 * OdsModulePlugin is the base class for ODS modules that are
 * Josm plug-ins. 
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public abstract class OdsModulePlugin extends Plugin implements OdsModule {
    private OdsWorkingSet workingSet;
    private boolean enabled = false;
    
    public OdsModulePlugin(PluginInformation info) {
        super(info);
    }
    
    @Override
    public boolean isEnabled() {
        return enabled;
    }


    @Override
    public void enable() {
        enabled = true;
        getWorkingSet().activate();
    }

    @Override
    public void disable() {
        enabled = false;
        getWorkingSet().deActivate();
    }

    @Override
    public String getDescription() {
        return "";
    }


    @Override
    public OdsWorkingSet getWorkingSet() {
        if (workingSet == null) {
            workingSet = new OdsWorkingSet(this);
        }
        return workingSet;
    }

    @Override
    public boolean usePolygonFile() {
        return false;
    }

    @Override
    public File getPolygonFilePath() {
        if (!usePolygonFile()) {
            return null;
        }
        File pluginDir = new File(getPluginDir());
        return new File(pluginDir, "polygons.osm");
    }
}
