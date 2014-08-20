package org.openstreetmap.josm.plugins.ods.entities.external;

import java.util.Iterator;

import javax.inject.Inject;

import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.DataSource;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.DataLayer;
import org.openstreetmap.josm.plugins.ods.PrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.entities.DefaultEntitySet;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.EntitySet;
import org.openstreetmap.josm.plugins.ods.entities.EntityStore;

/**
 * To distinct the ODS DataLayer from a normal Osm datalayer, we create a
 * subclass of OsmDataLayer. I'd prefer to subclass Layer instead, but if we did
 * so, we would lose to much functionality that depends directly on the
 * OsmDataLayer class.
 * 
 * @author Gertjan Idema
 * 
 */
public class ExternalDataLayer implements DataLayer {
    private OsmDataLayer osmDataLayer;
    private EntitySet entitySet;
    private PrimitiveBuilder primitiveBuilder;

    /**
     * Simple constructor providing a new (empty) dataset and a new
     * 
     * @param name
     */
    @Inject
    public ExternalDataLayer(String name) {
        DataSet dataSet = new DataSet();
        osmDataLayer = new OsmDataLayer(dataSet, name, null);
        entitySet = new DefaultEntitySet();
        primitiveBuilder = new PrimitiveBuilder(dataSet);
    }

    
    @Override
    public OsmDataLayer getOsmDataLayer() {
        return osmDataLayer;
    }


    @Override
    public boolean isInternal() {
        return false;
    }


    public EntitySet getEntitySet() {
        return entitySet;
    }

    public void merge(EntitySet newEntities) {
        DataSet data = osmDataLayer.data;
        data.beginUpdate();
        Iterator<EntityStore<? extends Entity>> stores = newEntities.stores();
        while (stores.hasNext()) {
            EntityStore<? extends Entity> store = stores.next();
            Iterator<? extends Entity> entities = store.iterator();
            while (entities.hasNext()) {
                ExternalEntity entity = (ExternalEntity) entities.next();
                if (!entity.isIncomplete() && !entity.isDeleted()) {
                    if (entitySet.add(entity)) {
                        entity.createPrimitives(primitiveBuilder);
                    }
                };
            }
        }
//        data.dataSources.add(new DataSource(newEntities.getBoundary().));
        data.endUpdate();
    }
}
