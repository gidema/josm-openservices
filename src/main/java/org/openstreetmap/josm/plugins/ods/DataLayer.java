package org.openstreetmap.josm.plugins.ods;

import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.entities.EntitySet;

/**
 * The DataLayer combines the Osm Datalayer that is shown in the 
 * user interface, with the ODS EntitySet that contains the underlying
 * data.
 * 
 * @author gertjan
 *
 */
public interface DataLayer {
    public OsmDataLayer getOsmDataLayer();
    public EntitySet getEntitySet();
    public boolean isInternal();
}
