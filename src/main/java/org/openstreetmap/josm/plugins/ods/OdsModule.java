package org.openstreetmap.josm.plugins.ods;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JMenu;
import javax.swing.JOptionPane;

import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerAddEvent;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerChangeListener;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerOrderChangeEvent;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerRemoveEvent;
import org.openstreetmap.josm.gui.layer.MainLayerManager.ActiveLayerChangeEvent;
import org.openstreetmap.josm.gui.layer.MainLayerManager.ActiveLayerChangeListener;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OdLayerManager;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmEntityBuilder;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmLayerManager;
import org.openstreetmap.josm.plugins.ods.gui.OdsAction;
import org.openstreetmap.josm.plugins.ods.io.MainDownloader;

/**
 * The OdsModule is the main component of the ODS plugin. It manages a pair of interrelated layers
 * which are a normal OSM layer and a ODS layer containing data retrieved from an external open data source.
 * A third layer containing polygons to manage download areas is optional.
 *
 * The data in the ODS layer may be retrieved from multiple dataSources.
 *
 * @author Gertjan Idema
 *
 */
public abstract class OdsModule implements ActiveLayerChangeListener, LayerChangeListener {
    private OdsModulePlugin plugin;
    private final List<OdsAction> actions = new LinkedList<>();
    private final List<OsmEntityBuilder<?>> entityBuilders = new LinkedList<>();

    private final Map<String, OdsDataSource> dataSources = new HashMap<>();
    protected final OsmLayerManager osmLayerManager = createOsmLayerManager();
    protected final OdLayerManager odLayerManager = createOdLayerManager();
    private PolygonLayerManager polygonDataLayer;
    //    private MatcherManager matcherManager;


    String osmQuery;

    public OdsModule() {
        super();
    }

    protected abstract OdLayerManager createOdLayerManager();

    protected abstract OsmLayerManager createOsmLayerManager();

    public OsmLayerManager getOsmLayerManager() {
        return osmLayerManager;
    }

    private boolean active = false;

    protected void setPlugin(OdsModulePlugin plugin) {
        this.plugin = plugin;
    }

    public void initialize() throws Exception {
        MainApplication.getLayerManager().addActiveLayerChangeListener(this);
        MainApplication.getLayerManager().addLayerChangeListener(this);
    }

    protected void addOsmEntityBuilder(OsmEntityBuilder<?> entityBuilder) {
        this.entityBuilders.add(entityBuilder);
    }

    public List<OsmEntityBuilder<?>> getEntityBuilders() {
        return entityBuilders;
    }

    //    public abstract GeoUtil getGeoUtil();
    //
    //    public abstract CRSUtil getCrsUtil();

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

    //    public OdLayerManager getOdLayerManager() {
    //        return odLayerManager;
    //    }
    //
    //    public OsmLayerManager getOsmLayerManager() {
    //        return osmLayerManager;
    //    }

    //    public MatcherManager getMatcherManager() {
    //        return matcherManager;
    //    }

    public LayerManager getLayerManager(Layer activeLayer) {
        if (!isActive()) return null;
        if (odLayerManager.getOsmDataLayer() == activeLayer) {
            return odLayerManager;
        }
        if (osmLayerManager.getOsmDataLayer() == activeLayer) {
            return osmLayerManager;
        }
        return null;
    }

    public void addDataSource(OdsDataSource dataSource) {
        dataSources.put(dataSource.getFeatureType(), dataSource);
    }

    public boolean isActive() {
        return active;
    }

    public boolean activate() {
        JMenu menu = OpenDataServicesPlugin.INSTANCE.getMenu();
        for (OdsAction action : getActions()) {
            menu.add(action);
        }
        osmLayerManager.activate();
        odLayerManager.activate();
        if (usePolygonFile()) {
            polygonDataLayer = new PolygonLayerManager(this);
            polygonDataLayer.activate();
        }
        //        this.matcherManager = new MatcherManager(this);
        active = true;
        return true;
    }

    public void deActivate() {
        if (isActive()) {
            osmLayerManager.deActivate();
            odLayerManager.deActivate();
            polygonDataLayer.deActivate();
            active = false;
        }
    }

    public List<OdsAction> getActions() {
        return actions;
    }

    public void addAction(OdsAction action) {
        actions.add(action);
    }

    void activateOsmLayer() {
        MainApplication.getLayerManager().setActiveLayer(osmLayerManager.getOsmDataLayer());
    }


    // Implement ActiveLayerChangeListener
    @Override
    public void activeOrEditLayerChanged(ActiveLayerChangeEvent e) {
        Layer oldLayer = e.getPreviousActiveLayer();
        Layer newLayer = MainApplication.getLayerManager().getActiveLayer();
        if (!isActive()) return;
        for (OdsAction action : actions) {
            action.activeLayerChange(oldLayer, newLayer);
        }
    }

    @Override
    public void layerAdded(LayerAddEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void layerOrderChanged(LayerOrderChangeEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void layerRemoving(LayerRemoveEvent event) {
        // Hack to prevent this method from running when Josm is exiting.
        if (isExiting()) {
            return;
        }
        if (isActive()) {
            Layer removedLayer = event.getRemovedLayer();
            if (removedLayer == odLayerManager.getOsmDataLayer()) {
                String message = tr("You just removed the {0} layer." +
                        " For the stability of the {1} module, you have to reset the module.",
                        odLayerManager.getOsmDataLayer().getName(), getName());
                JOptionPane.showMessageDialog(null, message, tr("ODS layer removed."), JOptionPane.OK_OPTION);
            }
            else if (removedLayer == osmLayerManager.getOsmDataLayer()) {
                String message = tr("You just removed the {0} layer." +
                        " For the stability of the {1} module, you have to reset the module.",
                        osmLayerManager.getOsmDataLayer().getName(), getName());
                JOptionPane.showMessageDialog(null, message, tr("ODS layer removed."), JOptionPane.OK_OPTION);
            }
        }
    }

    /**
     * Check if the Josm application is exiting.
     *
     * @return if Josm is exiting
     */
    private static boolean isExiting() {
        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            if ("org.openstreetmap.josm.Main".equals(element.getClassName()) &&
                    "exitJosm".equals(element.getMethodName())) {
                return true;
            }
        }
        return false;

    }
    public abstract Bounds getBounds();

    public abstract MainDownloader getDownloader();

    public boolean usePolygonFile() {
        return false;
    }

    public File getPluginDir() {
        return plugin.getPluginDirs().getUserDataDirectory(true);
    }

    public void reset() {
        osmLayerManager.reset();
        odLayerManager.reset();
        //        getMatcherManager().reset();
        MainApplication.getMap().mapView.repaint();
    }

    /**
     * Get the tolerance (in degrees) used to match nearby nodes and lines.
     * TODO provide more versatile configuration option
     *
     * @return
     */
    public abstract Double getTolerance();
}
