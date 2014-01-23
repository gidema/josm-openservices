package org.openstreetmap.josm.plugins.ods;

import java.io.File;

import org.openstreetmap.josm.data.Bounds;

/**
 * The opendataservices plug-in (ODS) can handle multiple modules.
 * OdsModule is the interface for the modules. Currently, each module
 * has an OdsWorkingSet that contains a part of the functionality.
 * The reason for this is historical. The WorkingSet existed before the
 * concept of the ODS module. In the future we might decide to merge the
 * workingSet functionality into the OdsModule.
 *   
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public interface OdsModule {
    
    public boolean isEnabled();
    
    /**
     * Enable the module
     */
    public void enable();
    
    /**
     * Disable the module
     */
    public void disable();
    /**
     * Get the name of this module
     * @return
     */
    public String getName();
    
    /**
     * Get a short description for this module
     * @return
     */
    public String getDescription();
    
    /**
     * Get this modules workingSet. See this class's comment for the relation between
     *  modules and workingSet
     *  
     * @return
     */
    public OdsWorkingSet getWorkingSet();
    
    /**
     * The module may want to keep track of polygons that have already
     * been imported.
     *  
     * @return 
     * true if the module uses a polygon file to track of polygons  
     */
    public boolean usePolygonFile();
    
    
    /**
     * Get the path to the polygon file.
     * 
     * @return
     */
    public File getPolygonFilePath();
    
    
    /**
     * Get the bounding box for which this module is valid.
     * 
     * @return
     */
    public Bounds getBounds();
}
