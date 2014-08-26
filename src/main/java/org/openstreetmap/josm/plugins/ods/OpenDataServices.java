package org.openstreetmap.josm.plugins.ods;

import static org.openstreetmap.josm.gui.help.HelpUtil.ht;
import static org.openstreetmap.josm.tools.I18n.marktr;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.swing.JMenu;
import javax.swing.JOptionPane;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.gui.download.DownloadDialog;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.PluginInformation;
import org.openstreetmap.josm.plugins.ods.gui.OdsEnableAction;
import org.openstreetmap.josm.tools.I18n;

public class OpenDataServices {
    public static OpenDataServices INSTANCE;
    private final static String INFO_URL = "http://www.gertjanidema.nl/ods/ods.json";
//    private Plugin plugin;
    private JsonObject metaInfo;

    // All available modules
    private List<OdsModule> modules = new LinkedList<>();
    // The currently active module, or null if no module is active
    private OdsModule activeModule;
    private JMenu menu;

    @Inject
    public OpenDataServices(OpenDataServicesPlugin plugin) {
        if (INSTANCE != null) {
            throw new java.lang.RuntimeException(I18n.tr(
                "The Open Data Services plug-in has allready been started"));
        }
//        this.plugin = plugin;
        INSTANCE = this;
        readInfo();
        checkVersion(plugin.getPluginInformation());
//        try {
//            ClassLoader classLoader = getClass().getClassLoader();
//            URL configFile = classLoader.getResource("config.xml");
//            try {
//                ConfigurationReader configurationReader = new ConfigurationReader(
//                        classLoader);
//                configurationReader.read(configFile);
//            } catch (ConfigurationException e) {
//                String message = "An error occured trying to registrate the odsFeatureSource types: " + e.getMessage();
//                JOptionPane.showMessageDialog(Main.parent,  message, "Error", JOptionPane.ERROR_MESSAGE);
//            }
//        } catch (Exception e) {
//            String message = "An error occured trying to registrate the odsFeatureSource types: " + e.getMessage();
//            JOptionPane.showMessageDialog(Main.parent,  message, "Error", JOptionPane.ERROR_MESSAGE);
//        }
        initializeMenu();
        addDownloadDialogListener();
    }

    public void registerModule(OdsModule module) {
        modules.add(module);
    }

    public List<OdsModule> getModules() {
        return modules;
    }

    public OdsModule getActiveModule() {
        return activeModule;
    }
    public void activate(OdsModule module) {
        if (activeModule == null) {
            this.activeModule = module;
            module.activate();
        }
    }

    public void deactivate(OdsModule module) {
        if (module.equals(activeModule)) {
            activeModule.deActivate();
            activeModule = null;
        }
    }

    private void initializeMenu() {
        if (menu == null) {
            menu = Main.main.menu.addMenu(marktr("ODS"), KeyEvent.VK_UNDEFINED,
                    4, ht("/Plugin/ODS"));
            menu.add(new OdsEnableAction(this));
        }
    }
    
    public JMenu getMenu() {
        return menu;
    }

    public void checkVersion(PluginInformation info) {
        if (metaInfo == null) return;
        String latestVersion = metaInfo.getJsonObject("version").getString("latest");
        if (!info.version.equals(latestVersion)) {
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
