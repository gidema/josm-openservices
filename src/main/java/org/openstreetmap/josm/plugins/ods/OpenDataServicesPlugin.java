package org.openstreetmap.josm.plugins.ods;

import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;
import org.openstreetmap.josm.tools.I18n;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class OpenDataServicesPlugin extends Plugin {
    public static OpenDataServicesPlugin INSTANCE;
//    private final static String INFO_URL = "http://www.gertjanidema.nl/ods/ods.json";
//    private JsonObject metaInfo;
    private Injector injector;
//    public OpenDataServicesModule mainModule = new OpenDataServicesModule();


    // All available modules
//    private Map<String, OdsModuleConfig> modules = new HashMap<>();
    // The currently active module, or null if no module is active
//    private OdsModuleConfig activeModule;

    public OpenDataServicesPlugin(PluginInformation info) {
        super(info);
        if (INSTANCE != null) {
            throw new java.lang.RuntimeException(I18n.tr(
                "The Open Data Services plug-in has allready been started"));
        }
        INSTANCE = this;
        injector = Guice.createInjector(new OpenDataServicesModule(this));

//        readInfo();
//        checkVersion(info);
//        initializeMenu();
//        addDownloadDialogListener();
    }

    public void registerModule(OdsModuleConfig config) {
        Injector childInjector = injector.createChildInjector(config);
        OdsModule module = childInjector.getInstance(OdsModule.class);
        OpenDataServices ods = injector.getInstance(OpenDataServices.class);
        ods.registerModule(module);       
    }

//    public Collection<OdsModuleConfig> getModules() {
//        return modules.values();
//    }
//
//    public OdsModuleConfig getModule() {
//        if (activeModule == null) {
//            if (!getModules().isEmpty()) {
//                activeModule = modules.values().iterator().next();
//            }
//        }
//        return activeModule;
//    }
//    
//    public static JMenu initializeMenu() {
//        JMenu menu = ODS.getMenu();
//        if (menu == null) {
//            menu = Main.main.menu.addMenu(marktr("ODS"), KeyEvent.VK_UNDEFINED,
//                    4, ht("/Plugin/ODS"));
//            menu.add(new OdsAction());
//            menu.add(new OdsDownloadAction());
//            ODS.setMenu(menu);
//        }
//        return menu;
//    }
//
//    public void checkVersion(PluginInformation info) {
//        if (metaInfo == null) return;
//        String latestVersion = metaInfo.getJsonObject("version").getString("latest");
//        if (!info.version.equals(latestVersion)) {
//            JOptionPane.showMessageDialog(Main.parent, I18n.tr("Your ODS version ({0}) is out of date.\n" +
//                 "Please upgrade to the latest version: {1}", info.version, latestVersion), "Plug-in out of date", JOptionPane.WARNING_MESSAGE);
//
//        }
//    }
//    
//    private void readInfo() {
//        URL url;
//        try {
//            url = new URL(INFO_URL);
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//            throw new RuntimeException(e);
//        }
//        InputStream is = null;
//        JsonReader reader = null;
//        try {
//            is = url.openStream();
//            reader = Json.createReader(is);
//            metaInfo = reader.readObject().getJsonObject("ods");
//            if (metaInfo == null) {
//                JOptionPane.showMessageDialog(Main.parent, I18n.tr("No version information is available at the moment.\n" +
//                        "Your ODS version may be out of date"), "No version info", JOptionPane.WARNING_MESSAGE);
//            }
//        } catch (IOException e) {
//            JOptionPane.showMessageDialog(Main.parent, I18n.tr("No version information is available at the moment.\n" +
//                "Your ODS version may be out of date"), "No version info", JOptionPane.WARNING_MESSAGE);
//            
//        } finally {
//            if (is != null)
//                try {
//                    is.close();
//                } catch (IOException e) {
//                    // Ignore
//                }
//            if (reader != null) reader.close();
//        }
//    }
//    
//    /*
//     * When Josm's default download is called, the results shouldn't end up in
//     * one of the OpenService layers. To achieve this, we intercept the
//     * AbstractDownloadDialog and make sure an OsmData layer is active before
//     * continuing;
//     */
//    private void addDownloadDialogListener() {
//        DownloadDialog.getInstance().addComponentListener(
//                new ComponentAdapter() {
//                    @Override
//                    public void componentShown(ComponentEvent e) {
//                        if (!Main.isDisplayingMapView())
//                            return;
//                        Layer activeLayer = Main.main.getActiveLayer();
//                        if (activeLayer.getName().startsWith("ODS")
//                                || activeLayer.getName().startsWith("OSM")) {
//                            for (Layer layer : Main.map.mapView
//                                    .getAllLayersAsList()) {
//                                if (layer instanceof OsmDataLayer
//                                        && !(layer.getName().startsWith("ODS"))
//                                        && !(layer.getName().startsWith("OSM"))) {
//                                    Main.map.mapView.setActiveLayer(layer);
//                                    return;
//                                }
//                            }
//                        } else if (activeLayer instanceof OsmDataLayer) {
//                            return;
//                        }
//                        Layer newLayer = new OsmDataLayer(new DataSet(),
//                                OsmDataLayer.createNewName(), null);
//                        Main.map.mapView.addLayer(newLayer);
//                        Main.map.mapView.setActiveLayer(newLayer);
//                    }
//                });
//    }
//    
//    /**
//     * Inner class to do the Guice initialization.
//     * This is needed because the Josm plug-in system doesn't use Guice,
//     * so the constructor of the OpenDataServicesPlugin class is the
//     * earliest moment to initialize the Guice module.
//     * 
//     * @author Gertjan Idema <mail@gertjanidema.nl>
//     *
//     */
//    static class GuiceModule extends AbstractModule {
//
//        @Override
//        protected void configure() {
//            // Use an instance of CRSUtilProj4j to implement CRSUtil
//            bind(CRSUtil.class).toInstance(new CRSUtilProj4j());
//            
//        }
//        
//    }
}
