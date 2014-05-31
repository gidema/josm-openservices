package org.openstreetmap.josm.plugins.ods.geotools;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.DataLayer;
import org.openstreetmap.josm.plugins.ods.ODS;
import org.openstreetmap.josm.plugins.ods.OdsDataSource;
import org.openstreetmap.josm.plugins.ods.entities.DefaultEntitySet;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.EntityFactory;
import org.openstreetmap.josm.plugins.ods.entities.EntitySet;
import org.openstreetmap.josm.plugins.ods.entities.EntityStore;
import org.openstreetmap.josm.plugins.ods.osm.PrimitiveFactory;

/**
 * To distinct the ODS DataLayer from a normal Osm datalayer, we create a
 * subclass of OsmDataLayer. I'd prefer to subclass Layer instead, but if we did
 * so, we would lose to much functionality that depends directly on the
 * OsmDataLayer class.
 * 
 * @author Gertjan Idema
 * 
 */
public class GTDataLayer implements DataLayer {
    private String name;
    private OsmDataLayer osmDataLayer;
    private EntitySet entitySet;
    private boolean active = false;
    private List<OdsDataSource> dataSources = new LinkedList<>();
//    private EntityFactory entityFactory;

    /**
     * Simple constructor providing a new (empty) dataset and a new
     * 
     * @param name
     */
    public GTDataLayer(String name) {
        this.name = "ODS_" + name;
    }

    public void addDataSource(OdsDataSource dataSource) {
        dataSources.add(dataSource);
    }
    
    @Override
    public void activate() {
        if (active) return;
        Layer oldLayer = null;
        if (Main.map != null) {
            oldLayer = Main.main.getActiveLayer();
        }
        DataSet dataSet = new DataSet();
        osmDataLayer = new OsmDataLayer(dataSet, name, null);
        entitySet = new DefaultEntitySet();
        Main.main.addLayer(osmDataLayer);
        if (oldLayer != null) {
            Main.map.mapView.setActiveLayer(oldLayer);
        }
        active = true;
    }

    @Override
    public void deActivate() {
        // TODO check if we need to save the layer
        if (!active) return;
        if (osmDataLayer != null) {
            Main.map.mapView.removeLayer(osmDataLayer);
        }
        osmDataLayer = null;
        entitySet = null;
        active = false;
    }

    public List<OdsDataSource> getDataSources() {
        return dataSources;
    }
    
    @Override
    public OsmDataLayer getOsmDataLayer() {
        return osmDataLayer;
    }

    @Override
    public DataLayerType getType() {
        return DataLayerType.GT;
    }

    public EntitySet getEntitySet() {
        return entitySet;
    }

    public void merge(EntitySet newEntities) {
        DataSet data = osmDataLayer.data;
        data.beginUpdate();
        Iterator<EntityStore<? extends Entity>> stores = newEntities.stores();
        PrimitiveFactory factory = getPrimitiveFactory(data);
        while (stores.hasNext()) {
            EntityStore<? extends Entity> store = stores.next();
            Iterator<? extends Entity> entities = store.iterator();
            while (entities.hasNext()) {
                Entity entity = entities.next();
                if (!entity.isIncomplete() && !entity.isDeleted()) {
                    if (entitySet.add(entity)) {
                        factory.buildPrimitives(entity);
//                        entity.createPrimitives(primitiveBuilder);
                    }
                };
            }
        }
        data.endUpdate();
    }
    
//    public void setEntityFactory(@SuppressWarnings("rawtypes") EntityFactory entityFactory) {
//        this.entityFactory = entityFactory;
//    }

    public EntityFactory<SimpleFeature> getEntityFactory() {
        return ODS.getModule().getEntityFactory(SimpleFeature.class, null);
    }
    
    public PrimitiveFactory getPrimitiveFactory(DataSet dataSet) {
        return ODS.getModule().getPrimitiveFactory(dataSet);
    }
}
