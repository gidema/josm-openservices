package org.openstreetmap.josm.plugins.ods;

import org.openstreetmap.josm.gui.layer.OsmDataLayer;

/**
 * The DataLayer combines the Osm Datalayer that is shown in the 
 * user interface, with the ODS EntitySet that contains the underlaying
 * data.
 * 
 * @author gertjan
 *
 */
public interface DataLayer {
    // Get the current OsmDataLayer
    public OsmDataLayer getOsmDataLayer();
    public boolean isInternal();
    
    // Clear the current OsmDataLayer
    public void reset();
}
