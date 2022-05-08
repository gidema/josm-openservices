package org.openstreetmap.josm.plugins.ods;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.io.File;

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
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.context.OdsContextImpl;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OdLayerManager;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmLayerManager;
import org.openstreetmap.josm.plugins.ods.gui.MenuActions;

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

    private OdsContext context = new OdsContextImpl();

    String osmQuery;
    private boolean active = false;

    protected void setPlugin(OdsModulePlugin plugin) {
        this.plugin = plugin;
    }

    public void initialize() throws Exception {
        MainApplication.getLayerManager().addActiveLayerChangeListener(this);
        MainApplication.getLayerManager().addLayerChangeListener(this);
    }

    public abstract String getName();

    public abstract String getDescription();

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

    protected abstract OdLayerManager createOpenDataLayerManager();

    protected abstract OsmLayerManager createOsmLayerManager();

    public LayerManager getLayerManager(Layer activeLayer) {
        if (!isActive()) return null;
        OdLayerManager odLayerManager = context.getComponent(OdLayerManager.class);
        if (odLayerManager != null && odLayerManager.getOsmDataLayer() == activeLayer) {
            return odLayerManager;
        }
        OsmLayerManager osmLayerManager = context.getComponent(OsmLayerManager.class);
        if (osmLayerManager != null && osmLayerManager.getOsmDataLayer() == activeLayer) {
            return osmLayerManager;
        }
        PolygonLayerManager polygonLayerManager = context.getComponent(PolygonLayerManager.class);
        if (polygonLayerManager != null && polygonLayerManager.getOsmDataLayer() == activeLayer) {
            return polygonLayerManager;
        }
        return null;
    }

    public boolean isActive() {
        return active;
    }

    public boolean activate() {
        context.clear();
        OdLayerManager odLayerManager = createOpenDataLayerManager();
        context.register(odLayerManager);
        OsmLayerManager osmLayerManager = createOsmLayerManager();
        context.register(osmLayerManager);
        configureContext();
        JMenu menu = OpenDataServicesPlugin.INSTANCE.getMenu();
        MenuActions menuActions = context.getComponent(MenuActions.class);
        menuActions.forEach(action -> menu.add(action));
        osmLayerManager.activate();
        odLayerManager.activate();
        if (usePolygonFile()) {
            PolygonLayerManager polygonLayerManager = new PolygonLayerManager(this);
            context.register(polygonLayerManager);
            polygonLayerManager.activate();
        }
        active = true;
        return true;
    }

    protected abstract void configureContext();

    public void deActivate() {
        if (isActive()) {
            context.clear();
            active = false;
        }
    }

    void activateOsmLayer() {
        OsmLayerManager osmLayerManager = context.getComponent(OsmLayerManager.class);
        MainApplication.getLayerManager().setActiveLayer(osmLayerManager.getOsmDataLayer());
    }


    // Implement ActiveLayerChangeListener
    @Override
    public void activeOrEditLayerChanged(ActiveLayerChangeEvent e) {
        Layer oldLayer = e.getPreviousActiveLayer();
        Layer newLayer = MainApplication.getLayerManager().getActiveLayer();
        if (!isActive()) return;
        MenuActions menuActions = context.getComponent(MenuActions.class);
        menuActions.forEach(action -> action.activeLayerChange(oldLayer, newLayer));
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
            LayerManager layerManager = this.getLayerManager(event.getRemovedLayer());
            if (layerManager != null && layerManager.isActive()) {
                layerManager.deActivate();
                String message = tr("You removed one of the layers that belong to the {0} module." +
                        " For the stability of the {0} module, you have to reset the module.", getName());
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
        return MainApplication.worker.isShutdown();
    }

    public abstract Bounds getBounds();

    public boolean usePolygonFile() {
        return false;
    }

    public OdsContext getContext() {
        return context;
    }

    public File getPluginDir() {
        return plugin.getPluginDirs().getUserDataDirectory(true);
    }

    public void reset() {
        context.reset();
        MainApplication.getMap().mapView.repaint();
    }
}
