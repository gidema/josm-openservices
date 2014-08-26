package org.openstreetmap.josm.plugins.ods;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JMenu;

import org.opengis.feature.Feature;
import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.Bounds;
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
import org.openstreetmap.josm.plugins.ods.entities.external.GeotoolsDownloadJob;
import org.openstreetmap.josm.plugins.ods.entities.internal.InternalDataLayer;
import org.openstreetmap.josm.plugins.ods.entities.internal.OsmDownloadJob;
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
    private final InternalDataLayer internalDataLayer;
    private OsmDownloadJob osmDownloadJob;
    private GeotoolsDownloadJob gtDownloadJob;
    private OsmDataLayer polygonLayer;
    private final OdsDownloader downloader;
    String osmQuery;
    private final Map<OsmPrimitive, Feature> relatedFeatures = new HashMap<>();
    private boolean active = false;
    private EntityFactory entityFactory;
    // TODO this is a dependency on the BuiltEnvironment submodule
    // Change to a more generic solution like a Container pattern
    private BlockStore blockStore = new BlockStoreImpl();

    public OdsModule(OdsModulePlugin plugin, OdsDownloader downloader, ExternalDataLayer externalDataLayer, InternalDataLayer internalDataLayer) {
        this.plugin = plugin;
        this.downloader = downloader;
        this.externalDataLayer = externalDataLayer;
        this.internalDataLayer = internalDataLayer;
        MapView.addLayerChangeListener(this);
    }

    public BlockStore getBlockStore() {
        return blockStore;
    }

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

    public Feature getRelatedFeature(OsmPrimitive primitive) {
        return relatedFeatures.get(primitive);
    }

    public ExternalDataLayer getExternalDataLayer() {
        return externalDataLayer;
    }
    
    public void initExternalDatalayer() {
        Layer oldLayer = null;
        if (Main.map != null) {
            oldLayer = Main.main.getActiveLayer();
        }
        Main.main.addLayer(externalDataLayer.getOsmDataLayer());
        if (oldLayer != null) {
            Main.map.mapView.setActiveLayer(oldLayer);
        }
    }
    
    public InternalDataLayer getInternalDataLayer() {
        return internalDataLayer;
    }
    
    private void initInternalDatalayer() {
        Layer oldLayer = null;
        if (Main.map != null) {
            oldLayer = Main.main.getActiveLayer();
        }
        Main.main.addLayer(internalDataLayer.getOsmDataLayer());
        if (oldLayer != null) {
            Main.map.mapView.setActiveLayer(oldLayer);
        }
    }

    public void addDataSource(OdsDataSource dataSource) {
        dataSources.put(dataSource.getFeatureType(), dataSource);
    }

    public boolean isActive() {
        return active;
    }
    
    public void activate() {
        JMenu menu = OpenDataServices.INSTANCE.getMenu();
        for (OdsAction action : getActions()) {
            menu.add(action);
        }
        // if (!active) {
        // downloadAction = new OdsDownloadAction();
        // initToolbox();
        // }
        initExternalDatalayer();
        initInternalDatalayer();
        getPolygonLayer();
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
        if (polygonLayer != null) {
            Main.map.mapView.removeLayer(polygonLayer);
        }
        JMenu menu = OpenDataServices.INSTANCE.getMenu();
        for (OdsAction action : getActions()) {
//            menu.remove(action);
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

    public abstract Bounds getBounds();

    public OsmDataLayer getPolygonLayer() {
        if (polygonLayer == null) {
            String layerName = "ODS Polygons";
            File polygonFile = getPolygonFilePath();
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

//    public OsmDownloadJob getOsmDownloadJob() {
//        return osmDownloadJob;
//    }

//    public GeotoolsDownloadJob getGeotoolsDownloadJob() {
//        return gtDownloadJob;
//    }

    public OdsDownloader getDownloader() {
        return downloader;
    }
    
    public boolean usePolygonFile() {
        return false;
    }

    public File getPolygonFilePath() {
        if (!usePolygonFile()) {
            return null;
        }
        File pluginDir = new File(plugin.getPluginDir());
        return new File(pluginDir, "polygons.osm");
    }

}
