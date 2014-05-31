package org.openstreetmap.josm.plugins.ods.osm;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.DataLayer;
import org.openstreetmap.josm.plugins.ods.entities.DefaultEntitySet;
import org.openstreetmap.josm.plugins.ods.entities.EntitySet;

/**
 * DataLayer implementation for the OSM datalayer type.
 * 
 * 
 * @author Gertjan Idema
 * 
 */
public class OdsOsmDataLayer implements DataLayer {
    private String name;
    private OsmDataLayer osmDataLayer;
    private EntitySet entitySet;
    private boolean active = false;

    /**
     * Simple constructor providing a new (empty) dataset and a new
     * 
     * @param name
     */
    public OdsOsmDataLayer(String name) {
        this.name = "OSM_" + name;
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

    @Override
    public OsmDataLayer getOsmDataLayer() {
        return osmDataLayer;
    }

    @Override
    public DataLayerType getType() {
        return DataLayerType.OSM;
    }

    public EntitySet getEntitySet() {
        return entitySet;
    }
}
