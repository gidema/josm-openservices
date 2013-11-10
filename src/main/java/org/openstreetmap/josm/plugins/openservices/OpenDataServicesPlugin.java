package org.openstreetmap.josm.plugins.openservices;

//import static nl.gertjanidema.josm.bag.BAGDataType.ADRES;
//import static nl.gertjanidema.josm.bag.BAGDataType.LIGPLAATS;
//import static nl.gertjanidema.josm.bag.BAGDataType.PAND;
//import static nl.gertjanidema.josm.bag.BAGDataType.STANDPLAATS;
//import static nl.gertjanidema.josm.bag.BAGDataType.WEGVAK;
import static org.openstreetmap.josm.gui.help.HelpUtil.ht;
import static org.openstreetmap.josm.tools.I18n.marktr;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import javax.swing.JMenu;

import org.apache.commons.configuration.ConfigurationException;
import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.gui.download.DownloadDialog;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;

public class OpenDataServicesPlugin extends Plugin {
  private static JMenu menu;
  
  public OpenDataServicesPlugin(PluginInformation info) {
    super(info);
    try {
      ClassLoader classLoader = getClass().getClassLoader();
      URL configFile = classLoader.getResource("config.xml");
      try {
        ConfigurationReader configurationReader = new ConfigurationReader(classLoader);
        configurationReader.read(configFile);
      } catch (ConfigurationException e) {
        Main.info("An error occured trying to registrate the odsFeatureSource types.");
      }
      getMenu();
      configureSources();
      addDownloadDialogListener();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  private void configureSources() {
    File pluginDir = new File(getPluginDir());
    if (pluginDir.isDirectory()) {
      configureJarSources(pluginDir);
    }
  }
  
  private void configureJarSources(File pluginDir) {
    FilenameFilter jarFileFilter = new FilenameFilter() {
      @Override
      public boolean accept(File dir, String name) {
        return name.endsWith(".jar");
      }
    };
    for (String jarFile :pluginDir.list(jarFileFilter)) {
      configureJarSource(new File(pluginDir, jarFile));
    }
  }
  
  public void configureJarSource(File jarFile) {
    try {
      URL url = jarFile.toURI().toURL();
      URLClassLoader classLoader = new URLClassLoader(new URL[] {url}, null);
      URL configFile = classLoader.getResource("config.xml");
      classLoader.close();
      if (configFile == null) {
        Main.warn("Warning: {0} should contain a config.xml file", jarFile);
        return;
      }
      classLoader = new URLClassLoader(new URL[] {url}, getClass().getClassLoader());
      ConfigurationReader configurationReader = new ConfigurationReader(classLoader);
      configurationReader.read(configFile);
      classLoader.close();
    }
    catch (MalformedURLException e) {
      throw new RuntimeException("An unexpected exception occurred", e);
    } catch (ConfigurationException e) {
      Main.warn("A problem occurred when reading {0}", jarFile);
      Main.warn(e.getMessage());
      if (e.getCause() instanceof NullPointerException) {
        e.getCause().printStackTrace();
      }
    } catch (IOException e) {
		// TODO Auto-generated catch block
	}
  }

  public static JMenu getMenu() {
    if (menu == null) {
      menu = Main.main.menu.addMenu(marktr("ODS"), KeyEvent.VK_UNDEFINED, 4, ht("/Plugin/ODS"));
    }
    return menu;
  }
  
  /*
   * When Josm's default download is called, the results shouldn't end up in one
   * of the OpenService layers. To achieve this, we intercept the OdsDownloadDialog and
   * make sure an OsmData layer is active before continuing; 
   */
  private void addDownloadDialogListener() {
    DownloadDialog.getInstance().addComponentListener(new ComponentAdapter() {
      @Override
      public void componentShown(ComponentEvent e) {
        if (!Main.isDisplayingMapView()) return;
        Layer activeLayer = Main.main.getActiveLayer();
        if (activeLayer instanceof ImportDataLayer
            || activeLayer instanceof JosmDataLayer) {
          for (Layer layer : Main.map.mapView.getAllLayersAsList()) {
            if (layer instanceof OsmDataLayer 
                && !(layer instanceof ImportDataLayer)
                && !(layer instanceof JosmDataLayer)) {
              Main.map.mapView.setActiveLayer(layer);
              return;
            }
          }
        }
        else if (activeLayer instanceof OsmDataLayer) {
          return;
        }
        Layer newLayer = new OsmDataLayer(new DataSet(), OsmDataLayer.createNewName(), null);
        Main.map.mapView.addLayer(newLayer);
        Main.map.mapView.setActiveLayer(newLayer);
      }
    });
  }
}
