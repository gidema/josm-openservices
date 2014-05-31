package org.openstreetmap.josm.plugins.ods;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collections;
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
import org.openstreetmap.josm.plugins.ods.builtenvironment.BlockStore;
import org.openstreetmap.josm.plugins.ods.builtenvironment.BlockStoreImpl;
import org.openstreetmap.josm.plugins.ods.geotools.GTDataLayer;
import org.openstreetmap.josm.plugins.ods.jts.Boundary;
import org.openstreetmap.josm.plugins.ods.osm.OdsOsmDataLayer;

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
//    private final Map<String, OdsDataSource> dataSources = new HashMap<>();
    public GTDataLayer gTDataLayer;
    public OdsOsmDataLayer odsOsmDataLayer;
    public OsmDataLayer polygonLayer;
    // private boolean useToolbox = false;
    // private JDialog toolbox;
    // private final List<OldOdsAction> actions = new LinkedList<>();
    String osmQuery;
    private final Map<OsmPrimitive, Feature> relatedFeatures = new HashMap<>();
    // OdsDownloadAction downloadAction;
    private boolean active = false;
//    private EntityFactory entityFactory;
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

//    public final Map<String, OdsDataSource> getDataSources() {
//        return dataSources;
//    }

//    public void setOsmQuery(String query) {
//        /**
//         * Currently, we pass the osm (overpass) query through http get. This
//         * doesn't allow linefeed or carriage return characters, so we need to
//         * strip them.
//         */
//        if (query == null) {
//            osmQuery = null;
//            return;
//        }
//        this.osmQuery = query.replaceAll("\\s", "");
//    }
//
//    public final String getOsmQuery() {
//        return osmQuery;
//    }

    public Feature getRelatedFeature(OsmPrimitive primitive) {
        return relatedFeatures.get(primitive);
    }

    public void setExternalDataLayer(GTDataLayer layer) {
        this.gTDataLayer = layer;
    }

    public GTDataLayer getExternalDataLayer() {
        return gTDataLayer;
    }

//    public void addDataSource(OdsDataSource dataSource) {
//        dataSources.put(dataSource.getFeatureType(), dataSource);
//    }

    public void activate() {
        // if (!active) {
        // downloadAction = new OdsDownloadAction();
        // initToolbox();
        // }
        if (gTDataLayer != null) {
            gTDataLayer.activate();
        }
        if (odsOsmDataLayer != null) {
            odsOsmDataLayer.activate();
        }
        if (polygonLayer == null) {
            loadPolygonLayer();
        }
        active = true;
    }

    public void deActivate() {
        if (gTDataLayer != null) {
            gTDataLayer.deActivate();
        }
        if (odsOsmDataLayer != null) {
            odsOsmDataLayer.deActivate();
        }
        if (polygonLayer != null) {
            Main.map.mapView.removeLayer(polygonLayer);
            polygonLayer = null;
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

    public void setInternalDataLayer(OdsOsmDataLayer layer) {
        this.odsOsmDataLayer = layer;
    }
    
    public OdsOsmDataLayer getInternalDataLayer() {
        return odsOsmDataLayer;
    }

    // Implement LayerChangeListener

    @Override
    public void activeLayerChange(Layer oldLayer, Layer newLayer) {
        // if (!active) return;
        // if (newLayer != null
        // && (newLayer == gTDataLayer.getOsmDataLayer() ||
        // newLayer == odsOsmDataLayer.getOsmDataLayer())) {
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
        if (oldLayer.equals(polygonLayer)) {
            if (polygonLayer.requiresSaveToFile()) {
                Main.saveUnsavedModifications(Collections.singletonList(polygonLayer), false);
            }
        }
    }


    public OsmDataLayer getPolygonLayer() {
        return polygonLayer;
    }
    
    public void loadPolygonLayer() {
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
}
