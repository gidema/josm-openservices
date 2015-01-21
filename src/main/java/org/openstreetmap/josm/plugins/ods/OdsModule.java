package org.openstreetmap.josm.plugins.ods;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JMenu;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.gui.MapView;
import org.openstreetmap.josm.gui.MapView.LayerChangeListener;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.entities.EntityFactory;
import org.openstreetmap.josm.plugins.ods.entities.external.ExternalDataLayer;
import org.openstreetmap.josm.plugins.ods.entities.internal.InternalDataLayer;
import org.openstreetmap.josm.plugins.ods.gui.OdsAction;
import org.openstreetmap.josm.plugins.ods.io.OdsDownloader;

/**
 * TODO update this comment The OdsModule is the main component of the ODS
 * plugin. It manages a pair of interrelated layers which are a normal OSM layer
 * and a ODS layer.
 * 
 * The data in the ODS layer is retrieved from 1 or more ODS dataSources.
 * 
 * @author Gertjan Idema
 * 
 */
public abstract class OdsModule implements LayerChangeListener {
    private final OdsModulePlugin plugin;
    
    private final Map<String, OdsDataSource> dataSources = new HashMap<>();
    private final ExternalDataLayer externalDataLayer;
    private PolygonDataLayer polygonDataLayer;
    private final InternalDataLayer internalDataLayer;
    private final OdsDownloader downloader;
    String osmQuery;
    private boolean active = false;
    private EntityFactory entityFactory;
    // TODO this is a dependency on the BuiltEnvironment submodule
    // Change to a more generic solution like a Container pattern
//    private BlockStore blockStore = new BlockStoreImpl();

    public OdsModule(OdsModulePlugin plugin, OdsDownloader downloader, ExternalDataLayer externalDataLayer, InternalDataLayer internalDataLayer) {
        this.plugin = plugin;
        this.downloader = downloader;
        this.externalDataLayer = externalDataLayer;
        this.internalDataLayer = internalDataLayer;
        this.polygonDataLayer = new PolygonDataLayer(this);
        MapView.addLayerChangeListener(this);
    }

//    public BlockStore getBlockStore() {
//        return blockStore;
//    }

    // public void addAction(OldOdsAction action) {
    // action.setWorkingSet(this);
    // actions.add(action);
    // }

    public abstract String getName();

    public abstract String getDescription();

    public final Map<String, OdsDataSource> getDataSources() {
        return dataSources;
    }

    public void setOsmQuery(String query) {
        /**
         * Currently, we pass the osm (overpass) query through http get. This
         * doesn't allow linefeed or carriage return characters, so we need to
         * strip them.
         */
        if (query == null) {
            osmQuery = null;
            return;
        }
        this.osmQuery = query.replaceAll("\\s", "");
    }

    public final String getOsmQuery() {
        return osmQuery;
    }

    public ExternalDataLayer getExternalDataLayer() {
        return externalDataLayer;
    }
    
//    public void initExternalDatalayer() {
//        Layer oldLayer = null;
//        if (Main.map != null) {
//            oldLayer = Main.main.getActiveLayer();
//        }
//        Main.main.addLayer(externalDataLayer.getOsmDataLayer());
//        if (oldLayer != null) {
//            Main.map.mapView.setActiveLayer(oldLayer);
//        }
//    }
    
    public InternalDataLayer getInternalDataLayer() {
        return internalDataLayer;
    }
    
//    private void initInternalDatalayer() {
//        Layer oldLayer = null;
//        if (Main.map != null) {
//            oldLayer = Main.main.getActiveLayer();
//        }
//        Main.main.addLayer(internalDataLayer.getOsmDataLayer());
//        if (oldLayer != null) {
//            Main.map.mapView.setActiveLayer(oldLayer);
//        }
//    }

    public void addDataSource(OdsDataSource dataSource) {
        dataSources.put(dataSource.getFeatureType(), dataSource);
    }

    public boolean isActive() {
        return active;
    }
    
    public void activate() {
        JMenu menu = OpenDataServicesPlugin.INSTANCE.getMenu();
        for (OdsAction action : getActions()) {
            menu.add(action);
        }
        internalDataLayer.initialize();
        externalDataLayer.initialize();
        if (usePolygonFile()) {
            polygonDataLayer.initialize();
        }
        active = true;
    }

    public void deActivate() {
        if (internalDataLayer != null) {
            OsmDataLayer internalOsmLayer = internalDataLayer.getOsmDataLayer();
            Main.map.mapView.removeLayer(internalOsmLayer);
        }
        if (externalDataLayer != null) {
            OsmDataLayer externalOsmLayer = externalDataLayer.getOsmDataLayer();
            Main.map.mapView.removeLayer(externalOsmLayer);
        }
        if (polygonDataLayer != null) {
            OsmDataLayer osmLayer = polygonDataLayer.getOsmDataLayer();
            Main.map.mapView.removeLayer(osmLayer);
        }
        active = false;
    }

    public abstract List<OdsAction> getActions();

    void activateOsmLayer() {
        Main.map.mapView.setActiveLayer(internalDataLayer.getOsmDataLayer());
    }


    // Implement LayerChangeListener

    @Override
    public void activeLayerChange(Layer oldLayer, Layer newLayer) {
        // No action required
    }

    @Override
    public void layerAdded(Layer newLayer) {
        // No action required
    }

    @Override
    public void layerRemoved(Layer oldLayer) {
        if (active) {
            OsmDataLayer externalOsmLayer = (externalDataLayer == null ? null
                    : externalDataLayer.getOsmDataLayer());
            OsmDataLayer internalOsmLayer = (internalDataLayer == null ? null
                    : internalDataLayer.getOsmDataLayer());
            OsmDataLayer polygonOsmLayer = (polygonDataLayer == null ? null
                    : polygonDataLayer.getOsmDataLayer());
            if (oldLayer.equals(externalOsmLayer)) {
                externalDataLayer.reset();
            }
            if (oldLayer.equals(internalOsmLayer)) {
                internalDataLayer.reset();
            }
            if (oldLayer.equals(polygonOsmLayer)) {
                polygonDataLayer.reset();
            }
        }
    }

    public void setEntityFactory(EntityFactory entityFactory) {
        this.entityFactory = entityFactory;
    }

    public EntityFactory getEntityFactory() {
        return entityFactory;
    }

    public abstract Bounds getBounds();

    public OdsDownloader getDownloader() {
        return downloader;
    }
    
    public boolean usePolygonFile() {
        return false;
    }
    
    public String getPluginDir() {
        return plugin.getPluginDir();
    }
}
