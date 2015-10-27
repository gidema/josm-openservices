package org.openstreetmap.josm.plugins.ods;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.gui.progress.NullProgressMonitor;
import org.openstreetmap.josm.io.IllegalDataException;
import org.openstreetmap.josm.io.OsmImporter;

public class PolygonDataLayer extends AbstractLayerManager {
    private OdsModule module;

    public PolygonDataLayer(OdsModule module) {
        super("ODS Polygons");
        this.module = module;
    }

    @Override
    public boolean isOsm() {
        return false;
    }

    @Override
    protected OsmDataLayer createOsmDataLayer() {
        OsmDataLayer osmDataLayer = null;
        String layerName = "ODS Polygons";
        File polygonFile = getPolygonFilePath();
        if (polygonFile.exists()) {
            OsmImporter importer = new OsmImporter();
            try {
                osmDataLayer = importer.loadLayer(
                        new FileInputStream(polygonFile), polygonFile,
                        layerName, NullProgressMonitor.INSTANCE).getLayer();
                osmDataLayer.setUploadDiscouraged(true);
//                Main.main.addLayer(osmDataLayer);
                // Main.map.mapView.zoomTo(polygonLayer.data.);
            } catch (FileNotFoundException e) {
                // Won't happen as we checked this
                e.printStackTrace();
            } catch (IllegalDataException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return osmDataLayer;
    }

    private File getPolygonFilePath() {
        File pluginDir = new File(module.getPluginDir());
        return new File(pluginDir, "polygons.osm");
    } 
}
