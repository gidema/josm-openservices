package org.openstreetmap.josm.plugins.ods;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.opengis.feature.Feature;
import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.gui.MapView;
import org.openstreetmap.josm.gui.MapView.LayerChangeListener;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.gui.progress.NullProgressMonitor;
import org.openstreetmap.josm.io.IllegalDataException;
import org.openstreetmap.josm.io.OsmImporter;
import org.openstreetmap.josm.plugins.ods.entities.EntityFactory;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.BlockStore;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.BlockStoreImpl;
import org.openstreetmap.josm.plugins.ods.entities.external.ExternalDataLayer;
import org.openstreetmap.josm.plugins.ods.entities.internal.InternalDataLayer;
import org.openstreetmap.josm.plugins.ods.jts.Boundary;

/**
 * TODO update this comment The OdsWorkingSet is the main component of the ODS
 * plugin. It manages a pair of interrelated layers which are a normal OSM layer
 * and a ODS layer.
 * 
 * The data in the ODS layer is retrieved from 1 or more ODS dataSources.
 * 
 * @author Gertjan Idema
 * 
 */
public class OdsWorkingSet implements LayerChangeListener {
    private OdsModule module;
    // TODO move dataSources to externalLayer
    private final Map<String, OdsDataSource> dataSources = new HashMap<>();
    public ExternalDataLayer externalDataLayer;
    public InternalDataLayer internalDataLayer;
    public OsmDataLayer polygonLayer;
    // private boolean useToolbox = false;
    // private JDialog toolbox;
    // private final List<OldOdsAction> actions = new LinkedList<>();
    String osmQuery;
    private final Map<OsmPrimitive, Feature> relatedFeatures = new HashMap<>();
    // OdsDownloadAction downloadAction;
    private boolean active = false;
    private EntityFactory entityFactory;
    // TODO this is a dependency on the BuiltEnvironment submodule
    // Change to a more generic solution like a Container pattern
    private BlockStore blockStore = new BlockStoreImpl();

    public OdsWorkingSet(OdsModule module) {
        this.module = module;
        MapView.addLayerChangeListener(this);
    }

    public BlockStore getBlockStore() {
        return blockStore;
    }

    // public void addAction(OldOdsAction action) {
    // action.setWorkingSet(this);
    // actions.add(action);
    // }

    protected String getName() {
        return module.getName();
    }

    public String getDescription() {
        return module.getDescription();
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
            externalDataLayer = new ExternalDataLayer("ODS " + getName());
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

    public void activate() {
        // if (!active) {
        // downloadAction = new OdsDownloadAction();
        // initToolbox();
        // }
        getExternalDataLayer();
        getInternalDataLayer();
        getPolygonLayer();
        active = true;
    }

    public void deActivate() {
        if (internalDataLayer != null) {
            OsmDataLayer internalOsmLayer = internalDataLayer.getOsmDataLayer();
            Main.map.mapView.removeLayer(internalOsmLayer);
            internalDataLayer = null;
        }
        if (externalDataLayer != null) {
            OsmDataLayer externalOsmLayer = externalDataLayer.getOsmDataLayer();
            Main.map.mapView.removeLayer(externalOsmLayer);
            externalDataLayer = null;
        }
        if (polygonLayer != null) {
            Main.map.mapView.removeLayer(polygonLayer);
        }
        // toolbox.setVisible(false);
        // toolbox = null;
        active = false;
    }

    // public JDialog getToolbox() {
    // return toolbox;
    // }
    //
    // private void initToolbox() {
    // toolbox = new JDialog((Frame) Main.parent, "ODS");
    // if (useToolbox) {
    // toolbox.setLayout(new BoxLayout(toolbox.getContentPane(),
    // BoxLayout.Y_AXIS));
    // toolbox.setLocation(300, 300);
    // toolbox.setMinimumSize(new Dimension(110, 0));
    // toolbox.add(new JButton(downloadAction));
    // for (Action action : actions) {
    // toolbox.add(new JButton(action));
    // }
    // int width = toolbox.getContentPane().getWidth();
    // for (Component comp : toolbox.getComponents()) {
    // comp.setSize(width, comp.getHeight());
    // }
    // toolbox.pack();
    // }
    // }

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
            internalDataLayer = new InternalDataLayer("OSM " + getName());
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
        // if (!active) return;
        // if (newLayer != null
        // && (newLayer == externalDataLayer.getOsmDataLayer() ||
        // newLayer == internalDataLayer.getOsmDataLayer())) {
        // if (useToolbox) {
        // getToolbox().setVisible(true);
        // }
        // }
        // else if (active) {
        // getToolbox().setVisible(false);
        // }
    }

    @Override
    public void layerAdded(Layer newLayer) {
        // No action required
    }

    @Override
    public void layerRemoved(Layer oldLayer) {
        // boolean deActivate = false;
        if (!active)
            return;
        OsmDataLayer externalOsmLayer = (externalDataLayer == null ? null
                : externalDataLayer.getOsmDataLayer());
        OsmDataLayer internalOsmLayer = (internalDataLayer == null ? null
                : internalDataLayer.getOsmDataLayer());
        if (oldLayer == externalOsmLayer) {
            externalDataLayer = null;
        } else if (oldLayer == internalOsmLayer) {
            internalDataLayer = null;
        } else if (oldLayer == polygonLayer) {
            polygonLayer = null;
        }
        //
        // if (oldLayer == externalOsmLayer) {
        // if (Main.map != null &&
        // Main.map.mapView.getAllLayers().contains(internalOsmLayer)) {
        // if
        // (!Main.saveUnsavedModifications(Collections.singletonList(internalOsmLayer),
        // false))
        // return;
        // Main.map.mapView.removeLayer(internalOsmLayer);
        // deActivate = true;
        // internalDataLayer = null;
        // externalDataLayer = null;
        // }
        // } else if (oldLayer == internalOsmLayer) {
        // if (Main.map != null &&
        // Main.map.mapView.getAllLayers().contains(externalOsmLayer)) {
        // Main.map.mapView.removeLayer(externalOsmLayer);
        // deActivate = true;
        // externalDataLayer = null;
        // internalDataLayer = null;
        // }
        // }
        // if (deActivate) {
        // deActivate();
        // }
    }

    // public Action getActivateAction() {
    // return new ActivateAction();
    // }

    // public OdsDownloadAction getDownloadAction() {
    // return downloadAction;
    // }
    //
    // private class ActivateAction extends AbstractAction {
    //
    // private static final long serialVersionUID = -4943320068119307331L;
    //
    // @Override
    // public void actionPerformed(ActionEvent e) {
    // activate();
    // // downloadAction.actionPerformed(e);
    // }
    // }

    public void setEntityFactory(EntityFactory entityFactory) {
        this.entityFactory = entityFactory;
    }

    public EntityFactory getEntityFactory() {
        return entityFactory;
    }

    public OsmDataLayer getPolygonLayer() {
        if (polygonLayer == null) {
            String layerName = "ODS Polygons";
            File polygonFile = module.getPolygonFilePath();
            if (polygonFile.exists()) {
                OsmImporter importer = new OsmImporter();
                try {
                    polygonLayer = importer.loadLayer(
                            new FileInputStream(polygonFile), polygonFile,
                            layerName, NullProgressMonitor.INSTANCE).getLayer();
                    polygonLayer.setUploadDiscouraged(true);
                    Main.map.mapView.addLayer(polygonLayer);
                    // Main.map.mapView.zoomTo(polygonLayer.data.);
                    Main.info("");
                } catch (FileNotFoundException e) {
                    // Won't happen as we checked this
                    e.printStackTrace();
                } catch (IllegalDataException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return polygonLayer;
        // else {
        //
        // File dir = polygonFile.getParentFile();
        // if (!dir.isDirectory()) {
        // dir.mkdirs();
        // }
        // File polygonFile = new File(pluginDir, "polygons.osm");
        // if (!polygonFile.exists()) {
        // if (!createIfMissing) {
        // return null;
        // }
        //
        // }
        // if (pluginDir == null) {
        // Files.createDirectory(dir, attrs)
        // pluginDir.
        // }
        //
        // // TODO Auto-generated method stub
    }

}
