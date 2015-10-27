package org.openstreetmap.josm.plugins.ods;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JMenu;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.gui.MapView;
import org.openstreetmap.josm.gui.MapView.LayerChangeListener;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.entities.EntityFactory;
import org.openstreetmap.josm.plugins.ods.entities.managers.DataManager;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OpenDataLayerManager;
import org.openstreetmap.josm.plugins.ods.entities.osm.InternalDataLayer;
import org.openstreetmap.josm.plugins.ods.gui.OdsAction;
import org.openstreetmap.josm.plugins.ods.io.MainDownloader;
import org.openstreetmap.josm.plugins.ods.jts.GeoUtil;

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
    private final List<OdsAction> actions = new LinkedList<>();
    
    private final Map<String, OdsDataSource> dataSources = new HashMap<>();
    private final OpenDataLayerManager externalDataLayer;
    private PolygonDataLayer polygonDataLayer;
    private final InternalDataLayer internalDataLayer;
    
    private DataManager dataManager = new DataManager();

    String osmQuery;
    private boolean active = false;
    private EntityFactory entityFactory;

    public OdsModule(OdsModulePlugin plugin, OpenDataLayerManager externalDataLayer, InternalDataLayer internalDataLayer) {
        this.plugin = plugin;
        this.externalDataLayer = externalDataLayer;
        this.internalDataLayer = internalDataLayer;
        this.polygonDataLayer = new PolygonDataLayer(this);
        MapView.addLayerChangeListener(this);
    }

    public abstract GeoUtil getGeoUtil();
    
    public abstract CRSUtil getCrsUtil();
    
    public abstract String getName();

    public abstract String getDescription();

    public DataManager getDataManager() {
        return dataManager;
    }
    
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

    public OpenDataLayerManager getOpenDataLayerManager() {
        return externalDataLayer;
    }
//    
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
        getInternalDataLayer().initialize();
        getOpenDataLayerManager().initialize();
        if (usePolygonFile()) {
            polygonDataLayer.initialize();
        }
        active = true;
    }

    public void deActivate() {
        if (getInternalDataLayer() != null) {
            OsmDataLayer internalOsmLayer = getInternalDataLayer().getOsmDataLayer();
            Main.map.mapView.removeLayer(internalOsmLayer);
        }
        if (getOpenDataLayerManager() != null) {
            OsmDataLayer externalOsmLayer = getOpenDataLayerManager().getOsmDataLayer();
            Main.map.mapView.removeLayer(externalOsmLayer);
        }
        if (polygonDataLayer != null) {
            OsmDataLayer osmLayer = polygonDataLayer.getOsmDataLayer();
            Main.map.mapView.removeLayer(osmLayer);
        }
        active = false;
    }

    public List<OdsAction> getActions() {
        return actions;
    }
    
    public void addAction(OdsAction action) {
        actions.add(action);
    }

    void activateOsmLayer() {
        Main.map.mapView.setActiveLayer(getInternalDataLayer().getOsmDataLayer());
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
        InternalDataLayer internalDataLayer = getInternalDataLayer();
        OpenDataLayerManager externalDataLayer = getOpenDataLayerManager();
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

    public abstract MainDownloader getDownloader();

//    public abstract OdsDownloader getDownloader();

    public boolean usePolygonFile() {
        return false;
    }
    
    public String getPluginDir() {
        return plugin.getPluginDir();
    }
}
