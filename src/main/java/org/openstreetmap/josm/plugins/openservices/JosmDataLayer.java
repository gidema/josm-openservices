package org.openstreetmap.josm.plugins.openservices;

import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.openservices.entities.DefaultEntitySet;
import org.openstreetmap.josm.plugins.openservices.entities.EntitySet;

/**
 * To distinct the ODS Osm DataLayer from a normal Osm datalayer, we create a
 * subclass of OsmDataLayer. I'd prefer to subclass Layer instead, but if we did
 * so, we would lose to much functionality that depends directly on the
 * OsmDataLayer class
 * 
 * @author Gertjan Idema
 * 
 */
public class JosmDataLayer extends OsmDataLayer {
    private EntitySet entitySet = new DefaultEntitySet();

    /**
     * Simple constructor providing a new (empty) dataset
     * 
     * @param name
     */
    public JosmDataLayer(String name) {
        super(new DataSet(), name, null);
    }

    public EntitySet getEntitySet() {
        return entitySet;
    }
}
