package org.openstreetmap.josm.plugins.ods;

import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.UploadPolicy;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;

/**
 *
 * @author Gertjan Idema
 *
 */
public abstract class AbstractLayerManager implements LayerManager {
    private final String name;
    private OsmDataLayer osmDataLayer;
    private boolean active = false;

    public AbstractLayerManager(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public OsmDataLayer getOsmDataLayer() {
        return osmDataLayer;
    }

    protected OsmDataLayer createOsmDataLayer() {
        DataSet dataSet = new DataSet();
        OsmDataLayer layer = new OsmDataLayer(dataSet, getName(), null);
        if (!isOsm()) {
            dataSet.setUploadPolicy(UploadPolicy.BLOCKED);
        }
        return layer;
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    public void activate() {
        if (!active) {
            Layer oldLayer = null;
            if (MainApplication.getMap() != null) {
                oldLayer = MainApplication.getLayerManager().getActiveLayer();
            }
            osmDataLayer = createOsmDataLayer();
            MainApplication.getLayerManager().addLayer(osmDataLayer);
            if (oldLayer != null) {
                MainApplication.getLayerManager().setActiveLayer(oldLayer);
            }
            this.active = true;
        }
    }

    @Override
    public void reset() {
        if (isActive()) {
            deActivate();
        }
        activate();
    }

    @Override
    public void deActivate() {
        if (isActive()) {
            active = false;
            if (MainApplication.getLayerManager().containsLayer(this.osmDataLayer)) {
                MainApplication.getLayerManager().removeLayer(this.osmDataLayer);
            }
        }
    }
}
