package org.openstreetmap.josm.plugins.ods.entities.internal;

import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.DataLayer;

/**
 * To distinct the ODS DataLayer from a normal Osm datalayer, we create a
 * subclass of OsmDataLayer. I'd prefer to subclass Layer instead, but if we did
 * so, we would lose to much functionality that depends directly on the
 * OsmDataLayer class.
 * 
 * @author Gertjan Idema
 * 
 */
public class InternalDataLayer implements DataLayer {
    private OsmDataLayer osmDataLayer;
//    private EntitySet entitySet;

    /**
     * Simple constructor providing a new (empty) dataset and a new
     * 
     * @param name
     */
    public InternalDataLayer(String name) {
        DataSet dataSet = new DataSet();
        osmDataLayer = new OsmDataLayer(dataSet, name, null);
//        entitySet = new DefaultEntitySet();
    }

    
    @Override
    public OsmDataLayer getOsmDataLayer() {
        return osmDataLayer;
    }


    @Override
    public boolean isInternal() {
        return false;
    }


//    public EntitySet getEntitySet() {
//        return entitySet;
//    }
}
