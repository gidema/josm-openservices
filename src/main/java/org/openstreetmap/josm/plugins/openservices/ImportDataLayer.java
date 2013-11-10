package org.openstreetmap.josm.plugins.openservices;

import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.openservices.entities.Entity;
import org.openstreetmap.josm.plugins.openservices.entities.EntitySet;
import org.openstreetmap.josm.plugins.openservices.entities.EntitySetListener;

/**
 * To distinct the ODS DataLayer from a normal Osm datalayer, we create a
 * subclass of OsmDataLayer. I'd prefer to subclass Layer instead, but if we did
 * so, we would lose to much functionality that depends directly on the
 * OsmDataLayer class.
 * 
 * @author Gertjan Idema
 * 
 */
public class ImportDataLayer extends OsmDataLayer implements EntitySetListener {
    private EntitySet entitySet;
    private PrimitiveBuilder primitiveBuilder;

    /**
     * Simple constructor providing a new (empty) dataset and a new
     * 
     * @param name
     */
    public ImportDataLayer(String name) {
        super(new DataSet(), name, null);
        entitySet = new EntitySet();
        entitySet.addListener(this);
        primitiveBuilder = new PrimitiveBuilder(data);
        data.setUploadDiscouraged(true);
    }

    public EntitySet getEntitySet() {
        return entitySet;
    }

    @Override
    public void entityAdded(Entity entity) {
        entity.createPrimitives(primitiveBuilder);
    }
}
