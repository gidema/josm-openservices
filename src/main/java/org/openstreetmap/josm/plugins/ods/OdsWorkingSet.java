package org.openstreetmap.josm.plugins.ods;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;

import org.opengis.feature.Feature;
import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.gui.MapView;
import org.openstreetmap.josm.gui.MapView.LayerChangeListener;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.plugins.ods.entities.EntityFactory;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.BlockStore;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.BlockStoreImpl;
import org.openstreetmap.josm.plugins.ods.entities.external.ExternalDataLayer;
import org.openstreetmap.josm.plugins.ods.entities.internal.InternalDataLayer;
import org.openstreetmap.josm.plugins.ods.jts.Boundary;

/**
 * The OdsWorkingSet is the main component of the ODS plugin. It manages a pair
 * of interrelated layers which are a normal OSM layer and a ODS layer. It also
 * handles a toolbox containing a list of actions.
 * 
 * The toolbox is available when either of the two layers is active. Otherwise
 * the toolbox is hidden.
 * 
 * All actions are restricted to the two layers belonging to the workingSet.
 * 
 * The data in the ODS layer is retrieved from 1 or more ODS dataSources.
 * 
 * @author Gertjan Idema
 * 
 */
public class OdsWorkingSet implements LayerChangeListener {
    private String name;
    private String description;
    private final Map<String, OdsDataSource> dataSources = new HashMap<>();
    public ExternalDataLayer externalDataLayer;
    public InternalDataLayer internalDataLayer;
    private boolean useToolbox = false;
    private JDialog toolbox;
    private final List<OdsAction> actions = new LinkedList<>();
    String osmQuery;
    private final Map<OsmPrimitive, Feature> relatedFeatures = new HashMap<>();
    OdsDownloadAction downloadAction;
    private boolean active = false;
    private EntityFactory entityFactory;
    // TODO this is a dependency on the BuiltEnvironment submodule
    // Change to a more generic solution like a Container pattern
    private BlockStore blockStore = new BlockStoreImpl();

    public OdsWorkingSet() {
        MapView.addLayerChangeListener(this);
    }

    public BlockStore getBlockStore() {
        return blockStore;
    }
    
    public void addAction(OdsAction action) {
        action.setWorkingSet(this);
        actions.add(action);
    }

    protected String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Feature getRelatedFeature(OsmPrimitive primitive) {
        return relatedFeatures.get(primitive);
    }

    public ExternalDataLayer getExternalDataLayer() {
        Layer oldLayer = null;
        if (Main.map != null) {
            oldLayer = Main.main.getActiveLayer();
        }
        if (externalDataLayer == null) {
            externalDataLayer = new ExternalDataLayer("ODS " + name);
            Main.main.addLayer(externalDataLayer.getOsmDataLayer());
        }
        if (oldLayer != null) {
            Main.map.mapView.setActiveLayer(oldLayer);
        }
        return externalDataLayer;
    }

    public void addDataSource(OdsDataSource dataSource) {
        dataSources.put(dataSource.getFeatureType(), dataSource);
    }

    void activate() {
        if (!active) {
            downloadAction = new OdsDownloadAction();
            downloadAction.setWorkingSet(this);
            initToolbox();
        }
        getExternalDataLayer();
        getInternalDataLayer();
        active = true;
    }

    private void deActivate() {
    	if (internalDataLayer != null) {
    		internalDataLayer.getOsmDataLayer().destroy();
            internalDataLayer = null;
    	}
    	if (externalDataLayer != null) {
            externalDataLayer.getOsmDataLayer().destroy();
            externalDataLayer = null;
    	}
        toolbox.setVisible(false);
        toolbox = null;
        active = false;
    }

    public JDialog getToolbox() {
        return toolbox;
    }

    private void initToolbox() {
        toolbox = new JDialog((Frame) Main.parent, "ODS");
        if (useToolbox) {
            toolbox.setLayout(new BoxLayout(toolbox.getContentPane(),
                    BoxLayout.Y_AXIS));
            toolbox.setLocation(300, 300);
            toolbox.setMinimumSize(new Dimension(110, 0));
            toolbox.add(new JButton(downloadAction));
            for (Action action : actions) {
                toolbox.add(new JButton(action));
            }
            int width = toolbox.getContentPane().getWidth();
            for (Component comp : toolbox.getComponents()) {
                comp.setSize(width, comp.getHeight());
            }
            toolbox.pack();
        }
    }

    public void download(Boundary boundary, boolean downloadOsmData)
            throws ExecutionException, InterruptedException {
        new OdsDownloadAction().run();
    }

    void activateOsmLayer() {
        Layer osmLayer = getInternalDataLayer().getOsmDataLayer();
        Main.map.mapView.setActiveLayer(osmLayer);
    }

    public InternalDataLayer getInternalDataLayer() {
        Layer oldLayer = null;
        if (Main.map != null) {
            oldLayer = Main.main.getActiveLayer();
        }
        if (internalDataLayer == null) {
            internalDataLayer = new InternalDataLayer("OSM " + name);
            Main.main.addLayer(internalDataLayer.getOsmDataLayer());
        }
        if (oldLayer != null) {
            Main.map.mapView.setActiveLayer(oldLayer);
        }
        return internalDataLayer;
    }

    // Implement LayerChangeListener

    @Override
    public void activeLayerChange(Layer oldLayer, Layer newLayer) {
        if (!active) return;
        if (newLayer != null
                && (newLayer == externalDataLayer.getOsmDataLayer() || 
                newLayer == internalDataLayer.getOsmDataLayer())) {
            if (useToolbox) {
                getToolbox().setVisible(true);
            }
        } else if (active) {
            getToolbox().setVisible(false);
        }
    }

    @Override
    public void layerAdded(Layer newLayer) {
        // No action required
    }

    @Override
    public void layerRemoved(Layer oldLayer) {
        boolean deActivate = false;
        if (!active) return;
        if (oldLayer == externalDataLayer.getOsmDataLayer()) {
            if (Main.map != null && Main.map.mapView.getAllLayers().contains(internalDataLayer.getOsmDataLayer())) {
                Main.map.mapView.removeLayer(internalDataLayer.getOsmDataLayer());
                deActivate = true;
                internalDataLayer = null;
                externalDataLayer = null;
            }
        } else if (oldLayer == internalDataLayer.getOsmDataLayer()) {
            if (Main.map != null && Main.map.mapView.getAllLayers().contains(externalDataLayer.getOsmDataLayer())) {
                Main.map.mapView.removeLayer(externalDataLayer.getOsmDataLayer());
                deActivate = true;
                externalDataLayer = null;
                internalDataLayer = null;
            }
        }
        if (deActivate) {
            deActivate();
        }
    }

    public Action getActivateAction() {
        return new ActivateAction();
    }

    public OdsDownloadAction getDownloadAction() {
        return downloadAction;
    }

    private class ActivateAction extends AbstractAction {

        private static final long serialVersionUID = -4943320068119307331L;

        @Override
        public void actionPerformed(ActionEvent e) {
            activate();
            downloadAction.actionPerformed(e);
        }
    }

    public void setEntityFactory(EntityFactory entityFactory) {
        this.entityFactory = entityFactory;
    }

    public EntityFactory getEntityFactory() {
        return entityFactory;
    }
}
