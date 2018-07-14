package org.openstreetmap.josm.plugins.ods;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.openstreetmap.josm.gui.io.importexport.OsmImporter;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.gui.progress.NullProgressMonitor;
import org.openstreetmap.josm.io.IllegalDataException;
import org.openstreetmap.josm.tools.Logging;

public class PolygonLayerManager extends AbstractLayerManager {
    private final OdsModule module;
    private OsmDataLayer osmDataLayer;

    public PolygonLayerManager(OdsModule module) {
        super("ODS Polygons");
        this.module = module;
    }

    @Override
    public void activate() {
        if (getOsmDataLayer() != null) {
            super.activate();
        }
    }

    @Override
    public OsmDataLayer getOsmDataLayer() {
        if (osmDataLayer == null) {
            osmDataLayer = createOsmDataLayer();
        }
        return osmDataLayer;
    }

    @Override
    public boolean isOsm() {
        return false;
    }

    @Override
    protected OsmDataLayer createOsmDataLayer() {
        OsmDataLayer layer = null;
        String layerName = "ODS Polygons";
        File polygonFile = getPolygonFilePath();
        if (polygonFile.exists()) {
            OsmImporter importer = new OsmImporter();
            try (
                    InputStream is = new FileInputStream(polygonFile);
                    ) {
                layer = importer.loadLayer(is, polygonFile,
                        layerName, NullProgressMonitor.INSTANCE).getLayer();
                layer.setUploadDiscouraged(true);
                //                Main.main.addLayer(osmDataLayer);
                // Main.map.mapView.zoomTo(polygonLayer.getDataSet().);
            } catch (FileNotFoundException e) {
                // Won't happen as we checked this
                e.printStackTrace();
            } catch (IllegalDataException e) {
                Logging.error(e);
            } catch (IOException e1) {
                Logging.error(e1);
            }
        }
        return layer;
    }

    private File getPolygonFilePath() {
        return new File(module.getPluginDir(), "polygons.osm");
    }
}
