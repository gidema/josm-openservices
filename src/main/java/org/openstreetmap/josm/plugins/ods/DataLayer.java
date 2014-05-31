package org.openstreetmap.josm.plugins.ods;

import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.entities.EntitySet;

/**
 * The DataLayer combines the Osm Datalayer that is shown in the 
 * user interface, with the ODS EntitySet that contains the underlying
 * data.
 * There are currently 3 types of datalayers: 
 * OSM: Loads the data from the OpenStreetmap server (or mirror) and builds
 * entities for a subset of the data
 * GT: Loads Geotools features from a data source, builds Entities from the
 * features and builds OSM primitives from the entities
 * 
 * @author gertjan
 *
 */
public interface DataLayer {
    public OsmDataLayer getOsmDataLayer();
    public EntitySet getEntitySet();
    public DataLayerType getType();
//    public boolean isActive();
    public void activate();
    public void deActivate();
    
    public enum DataLayerType {
      OSM, GT;
    }
}
