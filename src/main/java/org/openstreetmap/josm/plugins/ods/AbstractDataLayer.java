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
    private OsmDataLayer osmDataLayer;
    private String name;

    public AbstractDataLayer(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public OsmDataLayer getOsmDataLayer() {
        return osmDataLayer;
    }

    public void initialize() {
        Layer oldLayer = null;
        if (Main.map != null) {
            oldLayer = Main.main.getActiveLayer();
        }
        osmDataLayer = createOsmDataLayer();
        Main.main.addLayer(osmDataLayer);
        if (oldLayer != null) {
            Main.map.mapView.setActiveLayer(oldLayer);
        }
    }
    
    /**
     * Create a new OsmDataLayer.
     * @return the new OsmDataLayer
     */
    protected OsmDataLayer createOsmDataLayer() {
        return new OsmDataLayer(new DataSet(), name, null);
    }

    @Override
    public void reset() {
        osmDataLayer = null;
    }
}
