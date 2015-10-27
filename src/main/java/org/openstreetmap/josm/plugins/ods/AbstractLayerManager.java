package org.openstreetmap.josm.plugins.ods;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.LayerManager;

/**
 * 
 * @author Gertjan Idema
 * 
 */
public abstract class AbstractLayerManager implements LayerManager {
    private String name;
    private OsmDataLayer osmDataLayer;

    public AbstractLayerManager(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public OsmDataLayer getOsmDataLayer() {
        if (osmDataLayer == null) {
            osmDataLayer = createOsmDataLayer();
            Main.main.addLayer(osmDataLayer);
        }
        return osmDataLayer;
    }
    
    protected OsmDataLayer createOsmDataLayer() {
        return new OsmDataLayer(new DataSet(), getName(), null);
    }

    public void initialize() {
        Layer oldLayer = null;
        if (Main.map != null) {
            oldLayer = Main.main.getActiveLayer();
        }
        this.getOsmDataLayer();
        if (oldLayer != null) {
            Main.map.mapView.setActiveLayer(oldLayer);
        }
    }
    
    public void reset() {
        // TODO close the osm datalayer properly
        this.osmDataLayer = null;
        // TODO clear the ODS data stores
    }
}
