package org.openstreetmap.josm.plugins.ods;

import static org.openstreetmap.josm.tools.I18n.tr;

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
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.entities.EntityType;
//import org.openstreetmap.josm.plugins.ods.entities.managers.DataManager;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OpenDataLayerManager;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmEntityBuilder;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmLayerManager;
import org.openstreetmap.josm.plugins.ods.gui.OdsAction;
import org.openstreetmap.josm.plugins.ods.io.MainDownloader;
import org.openstreetmap.josm.plugins.ods.jts.GeoUtil;

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
    private final List<EntityType<?>> entityTypes = new LinkedList<>();
    private final List<OsmEntityBuilder<?>> entityBuilders = new LinkedList<>();

    private final Map<String, OdsDataSource> dataSources = new HashMap<>();
    private OpenDataLayerManager openDataLayerManager;
    private PolygonLayerManager polygonDataLayer;
    private OsmLayerManager osmLayerManager;
    private MatcherManager matcherManager;

    String osmQuery;
    private boolean active = false;

    protected void setPlugin(OdsModulePlugin plugin) {
        this.plugin = plugin;
    }

    public void initialize() throws Exception {
        this.osmLayerManager = createOsmLayerManager();
        this.openDataLayerManager = createOpenDataLayerManager();
        MainApplication.getLayerManager().addActiveLayerChangeListener(this);
        MainApplication.getLayerManager().addLayerChangeListener(this);
    }

    protected void addEntityType(EntityType<?> entityType) {
        entityTypes.add(entityType);
    }

    public List<EntityType<?>> getEntityTypes() {
        return entityTypes;
    }

    protected void addOsmEntityBuilder(OsmEntityBuilder<?> entityBuilder) {
        this.entityBuilders.add(entityBuilder);
    }

    public List<OsmEntityBuilder<?>> getEntityBuilders() {
        return entityBuilders;
    }

    public abstract GeoUtil getGeoUtil();

    public abstract CRSUtil getCrsUtil();

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

    protected abstract OpenDataLayerManager createOpenDataLayerManager();

    protected abstract OsmLayerManager createOsmLayerManager();


    public OpenDataLayerManager getOpenDataLayerManager() {
        return openDataLayerManager;
    }

    public OsmLayerManager getOsmLayerManager() {
        return osmLayerManager;
    }

    public MatcherManager getMatcherManager() {
        return matcherManager;
    }

    public LayerManager getLayerManager(Layer activeLayer) {
        if (!isActive()) return null;
        if (openDataLayerManager.getOsmDataLayer() == activeLayer) {
            return openDataLayerManager;
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
        getOsmLayerManager().activate();
        getOpenDataLayerManager().activate();
        if (usePolygonFile()) {
            polygonDataLayer = new PolygonLayerManager(this);
            polygonDataLayer.activate();
        }
        this.matcherManager = new MatcherManager(this);
        active = true;
        return true;
    }

    public void deActivate() {
        if (isActive()) {
            getOsmLayerManager().deActivate();
            getOpenDataLayerManager().deActivate();
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
        MainApplication.getLayerManager().setActiveLayer(getOsmLayerManager().getOsmDataLayer());
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

    public String getPluginDir() {
        return plugin.getPluginDir();
    }

    public void reset() {
        getOsmLayerManager().reset();
        getOpenDataLayerManager().reset();
        getMatcherManager().reset();
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
