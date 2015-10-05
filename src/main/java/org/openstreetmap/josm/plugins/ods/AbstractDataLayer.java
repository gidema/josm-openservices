package org.openstreetmap.josm.plugins.ods;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.gui.layer.Layer;
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
public abstract class AbstractDataLayer implements DataLayer {
    private String name;
    private OsmDataLayer osmDataLayer;

    public AbstractDataLayer(String name) {
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
        this.osmDataLayer = null;
    }
}
