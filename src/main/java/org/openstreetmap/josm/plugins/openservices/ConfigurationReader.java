package org.openstreetmap.josm.plugins.openservices;


import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.List;
import java.util.NoSuchElementException;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.geotools.xml.Parser;
import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.gui.MainMenu;

public class ConfigurationReader {
 
  public static void read(URL configFile) throws ConfigurationException {
    try {
      HierarchicalConfiguration conf = new XMLConfiguration(configFile);
      configureHostTypes(conf);
      configureHosts(conf);
      configureDataSources(conf);
      configureLayers(conf);
      configureActions(conf);
      configureFeatureMappers(conf);
    } catch (NoSuchElementException e) {
      throw new ConfigurationException(e.getMessage(), e.getCause());
    }
  }
  
  private static void configureHostTypes(HierarchicalConfiguration conf) throws ConfigurationException {
    List<HierarchicalConfiguration> confs = conf.configurationsAt("host-type");
    for (HierarchicalConfiguration c : confs) {
      configureHostType(c);
    }
  }
  
  private static void configureHostType(HierarchicalConfiguration conf) throws ConfigurationException {
    conf.setThrowExceptionOnMissing(true);
    String typeName = conf.getString("[@name]");
    String hostClass = conf.getString("[@class]");
    HostType hostType = new HostType(typeName, hostClass);
    OpenServices.registerHostType(hostType);
  }

  private static void configureHosts(HierarchicalConfiguration conf) throws ConfigurationException {
    List<HierarchicalConfiguration> confs = conf.configurationsAt("host");
    for (HierarchicalConfiguration c : confs) {
      configureHost(c);
    }
  }
  
  private static void configureHost(HierarchicalConfiguration conf) throws ConfigurationException {
    conf.setThrowExceptionOnMissing(true);
    String name = conf.getString("[@name]");
    String type = conf.getString("[@type]");
    String url = conf.getString("[@url]");
    HostType hostType = OpenServices.getHostType(type);
    try {
      Host host = hostType.newHost();
      host.setHostType(hostType);
      host.setName(name);
      host.setUrl(url);
      OpenServices.registerHost(host);
    }
    catch (ServiceException e) {
      throw new ConfigurationException(e);
    }
  }

  private static void configureDataSources(HierarchicalConfiguration conf) throws ConfigurationException {
    for (HierarchicalConfiguration c : conf.configurationsAt("datasource")) {
      configureDataSource(c);
    }
  }

  private static void configureDataSource(HierarchicalConfiguration conf) throws ConfigurationException {
    String name = conf.getString("[@name]");
    DataSource dataSource = new DataSource();
    dataSource.setName(name);
    for (HierarchicalConfiguration c : conf.configurationsAt("service")) {
      Service service = configureService(c);
      dataSource.addService(service);
    }
    OpenServices.registerDataSource(dataSource);
  }

  private static Service configureService(HierarchicalConfiguration conf) throws ConfigurationException {
    conf.setThrowExceptionOnMissing(true);
    String hostName = conf.getString("[@host]");
    String feature = conf.getString("[@feature]");
    Host host = OpenServices.getHost(hostName);
    try {
      return host.getService(feature);
    } catch (ServiceException e) {
      throw new ConfigurationException(e);
    }
  }
  
  private static void configureLayers(HierarchicalConfiguration conf) throws ConfigurationException {
    for (HierarchicalConfiguration c : conf.configurationsAt("layer")) {
      configureLayer(c);
    }
  }
  
  private static void configureLayer(HierarchicalConfiguration conf) throws ConfigurationException {
    String name = conf.getString("[@name]");
    String dsName = conf.getString("[@datasource]");
    String mapperName = conf.getString("[@mapper]");
    Layer layer = new Layer();
    layer.setName(name);
    DataSource dataSource = OpenServices.getDataSource(dsName);
    layer.setDataSource(dataSource);
    dataSource.addLayer(layer);
    OpenServices.registerLayer(layer);
  }

  private static void configureActions(HierarchicalConfiguration conf) throws ConfigurationException {
    for (HierarchicalConfiguration c : conf.configurationsAt("action")) {
      configureAction(c);
    }
  }
  
  private static void configureAction(HierarchicalConfiguration conf) throws ConfigurationException {
    String name = conf.getString("[@name]");
    String type = conf.getString("[@type]");
    String menu = conf.getString("[@menu]");
    Layer layer = OpenServices.getLayer(name);
    DownloadAction action = new DownloadAction();
    action.setName(name);
    action.setLayer(layer);
    configureMenu(action, menu);
  }
  
  private static synchronized void configureMenu(Action action, String menu) {
    String[] menuParts = menu.split("\\.");
    String baseMenuName = menuParts[0];
    JMenu parent = getBaseMenu(baseMenuName, 4);
    for (int i=1; i<menuParts.length; i++) {
      parent = getChildMenu(parent, menuParts[i]);
    }
    parent.add(action);
  }

  private static void configureFeatureMappers(HierarchicalConfiguration conf) throws ConfigurationException {
    for (HierarchicalConfiguration c : conf.configurationsAt("map")) {
      configureFeatureMapper(c);
    }
  }
  
  private static void configureFeatureMapper(HierarchicalConfiguration conf) throws ConfigurationException {
    DefaultFeatureMapper mapper = new DefaultFeatureMapper();
    mapper.setFeatureName(conf.getString("[@feature]"));
    mapper.setPrimitiveType(conf.getString("[@map-to]"));
    for (HierarchicalConfiguration c : conf.configurationsAt("tag")) {
      String value = c.getString("[@value]");
      String property = c.getString("[@property]");
      String format = c.getString("[@format]");
      c.setThrowExceptionOnMissing(true);
      String key = c.getString("[@key]");
      if (value != null) {
        mapper.addFixedTag(key, value);
      }
      else if (property != null) {
        mapper.addPropertyMapper(key, property, format);
      }
    }
    OpenServices.registerFeatureMapper(mapper);
  }
  
  void test() {
    Parser parser;
  }
  
  // TODO move the following 2 functions to a more logical place (Plugin class ?)
  private static JMenu getBaseMenu(String name, int position) {
    MainMenu mainMenu = Main.main.menu;
    JMenu baseMenu = null;
    for (int i=0; i<mainMenu.getMenuCount(); i++) {
      baseMenu = mainMenu.getMenu(i);
      if (baseMenu.getText().equals(name)) {
        return baseMenu;
      }
    }
    baseMenu = mainMenu.addMenu(name, KeyEvent.VK_UNDEFINED, position, null);
    return baseMenu;
  }
  
  private static synchronized JMenu getChildMenu(JMenu parent, String name) {
    JMenu child = null;
    JPopupMenu popup = parent.getPopupMenu();
    for (int i=0; i<popup.getComponentCount(); i++) {
      child = (JMenu) popup.getComponent(i);
      if (child.getText().equals(name)) {
        return child;
      }
    }
    child = new JMenu(name);
    parent.add(child);
    return child;
  }
}
