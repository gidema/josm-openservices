package org.openstreetmap.josm.plugins.ods;

//import static nl.gertjanidema.josm.bag.BAGDataType.ADRES;
//import static nl.gertjanidema.josm.bag.BAGDataType.LIGPLAATS;
//import static nl.gertjanidema.josm.bag.BAGDataType.PAND;
//import static nl.gertjanidema.josm.bag.BAGDataType.STANDPLAATS;
//import static nl.gertjanidema.josm.bag.BAGDataType.WEGVAK;
import static org.openstreetmap.josm.gui.help.HelpUtil.ht;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.swing.JMenu;
import javax.swing.JOptionPane;

import org.apache.commons.configuration.ConfigurationException;
import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.gui.download.DownloadDialog;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;
import org.openstreetmap.josm.tools.I18n;

public class OpenDataServicesPlugin extends Plugin {
    private final static String INFO_URL = "http://www.gertjanidema.nl/ods/ods.json";
    private JsonObject metaInfo;
    
    public OpenDataServicesPlugin(PluginInformation info) {
        super(info);
        readInfo();
        checkVersion(info);
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            URL configFile = classLoader.getResource("config.xml");
            try {
                ConfigurationReader configurationReader = new ConfigurationReader(
                        classLoader);
                configurationReader.read(configFile);
            } catch (ConfigurationException e) {
                String message = "An error occured trying to registrate the odsFeatureSource types: " + e.getMessage();
                JOptionPane.showMessageDialog(Main.parent,  message, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            String message = "An error occured trying to registrate the odsFeatureSource types: " + e.getMessage();
            JOptionPane.showMessageDialog(Main.parent,  message, "Error", JOptionPane.ERROR_MESSAGE);
        }
        initializeMenu();
        addDownloadDialogListener();
    }

    public static JMenu initializeMenu() {
        JMenu menu = ODS.getMenu();
        if (menu == null) {
            menu = Main.main.menu.addMenu("ODS", "ODS", KeyEvent.VK_UNDEFINED,
                    4, ht("/Plugin/ODS"));
            menu.add(new OdsAction());
            menu.add(new OdsDownloadAction());
            ODS.setMenu(menu);
        }
        return menu;
    }

    public void checkVersion(PluginInformation info) {
        if (metaInfo == null) return;
        String latestVersion = metaInfo.getJsonObject("version").getString("latest");
        if (info.version.compareTo(latestVersion) < 0) {
            JOptionPane.showMessageDialog(Main.parent, I18n.tr("Your ODS version ({0}) is out of date.\n" +
                 "Please upgrade to the latest version: {1}", info.version, latestVersion), "Plug-in out of date", JOptionPane.WARNING_MESSAGE);

        }
    }
    
    private void readInfo() {
        URL url;
        try {
            url = new URL(INFO_URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        InputStream is = null;
        JsonReader reader = null;
        try {
            is = url.openStream();
            reader = Json.createReader(is);
            metaInfo = reader.readObject().getJsonObject("ods");
            if (metaInfo == null) {
                JOptionPane.showMessageDialog(Main.parent, I18n.tr("No version information is available at the moment.\n" +
                        "Your ODS version may be out of date"), "No version info", JOptionPane.WARNING_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(Main.parent, I18n.tr("No version information is available at the moment.\n" +
                "Your ODS version may be out of date"), "No version info", JOptionPane.WARNING_MESSAGE);
            
        } finally {
            if (is != null)
                try {
                    is.close();
                } catch (IOException e) {
                    // Ignore
                }
            if (reader != null) reader.close();
        }
    }
    
    /*
     * When Josm's default download is called, the results shouldn't end up in
     * one of the OpenService layers. To achieve this, we intercept the
     * AbstractDownloadDialog and make sure an OsmData layer is active before
     * continuing;
     */
    private void addDownloadDialogListener() {
        DownloadDialog.getInstance().addComponentListener(
                new ComponentAdapter() {
                    @Override
                    public void componentShown(ComponentEvent e) {
                        if (!Main.isDisplayingMapView())
                            return;
                        Layer activeLayer = Main.main.getActiveLayer();
                        if (activeLayer == null) return;
                        if (activeLayer.getName().startsWith("ODS")
                                || activeLayer.getName().startsWith("OSM")) {
                            for (Layer layer : Main.map.mapView
                                    .getAllLayersAsList()) {
                                if (layer instanceof OsmDataLayer
                                        && !(layer.getName().startsWith("ODS"))
                                        && !(layer.getName().startsWith("OSM"))) {
                                    Main.map.mapView.setActiveLayer(layer);
                                    return;
                                }
                            }
                        } else if (activeLayer instanceof OsmDataLayer) {
                            return;
                        }
                        Layer newLayer = new OsmDataLayer(new DataSet(),
                                OsmDataLayer.createNewName(), null);
                        Main.map.mapView.addLayer(newLayer);
                        Main.map.mapView.setActiveLayer(newLayer);
                    }
                });
    }
}
