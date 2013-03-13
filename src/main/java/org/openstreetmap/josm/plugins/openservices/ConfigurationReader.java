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
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.gui.MainMenu;

public class ConfigurationReader {
  private final ClassLoader classLoader;
 
  public ConfigurationReader(ClassLoader classLoader) {
    this.classLoader = classLoader;
  }

  public void read(URL configFile) throws ConfigurationException {
    try {
      XMLConfiguration conf = new XMLConfiguration();
      conf.setDelimiterParsingDisabled(true);
      conf.load(configFile);
      configureHostTypes(conf);
      configureHosts(conf);
      configureLayers(conf);
      configureActions(conf);
      configureFeatureMappers(conf);
    } catch (NoSuchElementException e) {
      throw new ConfigurationException(e.getMessage(), e.getCause());
    }
  }
  
  private void configureHostTypes(HierarchicalConfiguration conf) throws ConfigurationException {
    List<HierarchicalConfiguration> confs = conf.configurationsAt("host-type");
    for (HierarchicalConfiguration c : confs) {
      configureHostType(c);
    }
  }
  
  private void configureHostType(HierarchicalConfiguration conf) throws ConfigurationException {
    conf.setThrowExceptionOnMissing(true);
    String typeName = conf.getString("[@name]");
    String hostClass = conf.getString("[@class]");
    HostType hostType = new HostType(typeName, hostClass);
    OpenDataServices.registerHostType(hostType);
  }

  private void configureHosts(HierarchicalConfiguration conf) throws ConfigurationException {
    List<HierarchicalConfiguration> confs = conf.configurationsAt("host");
    for (HierarchicalConfiguration c : confs) {
      configureHost(c);
    }
  }
  
  private void configureHost(HierarchicalConfiguration conf) throws ConfigurationException {
    conf.setThrowExceptionOnMissing(true);
    String name = conf.getString("[@name]");
    String type = conf.getString("[@type]");
    String url = conf.getString("[@url]");
    HostType hostType = OpenDataServices.getHostType(type);
    try {
      Host host = hostType.newHost();
      host.setHostType(hostType);
      host.setName(name);
      host.setUrl(url);
      OpenDataServices.registerHost(host);
    }
    catch (ServiceException e) {
      throw new ConfigurationException(e);
    }
  }

  private void configureDataSources(HierarchicalConfiguration conf, OdsLayer layer) throws ConfigurationException {
    for (HierarchicalConfiguration c : conf.configurationsAt("datasource")) {
      configureDataSource(c, layer);
    }
  }

  private void configureDataSource(HierarchicalConfiguration conf, OdsLayer layer) throws ConfigurationException {
    //String name = conf.getString("[@name]");
    Service service = configureService(conf);
    OdsDataSource dataSource = service.newDataSource();
    dataSource.setService(service);
    String filter = conf.getString("filter", null);
    if (filter != null) {
      configureFilter(dataSource, filter);
    }
    layer.addDataSource(dataSource);
  }

  private void configureFilter(OdsDataSource dataSource,
      String filter) throws ConfigurationException {
    try {
      dataSource.setFilter(CQL.toFilter(filter));
    } catch (CQLException e) {
      throw new ConfigurationException("Error in filter", e);
    }
    
  }

  private Service configureService(HierarchicalConfiguration conf) throws ConfigurationException {
    conf.setThrowExceptionOnMissing(true);
    String hostName = conf.getString("[@host]");
    String feature = conf.getString("[@feature]");
    Host host = OpenDataServices.getHost(hostName);
    try {
      return host.getService(feature);
    } catch (ServiceException e) {
      throw new ConfigurationException(e);
    }
  }
  
  private void configureLayers(HierarchicalConfiguration conf) throws ConfigurationException {
    for (HierarchicalConfiguration c : conf.configurationsAt("layer")) {
      configureLayer(c);
    }
  }
  
  private void configureLayer(HierarchicalConfiguration conf) throws ConfigurationException {
    String name = conf.getString("[@name]");
    OdsLayer layer = new OdsLayer();
    layer.setName(name);
    configureDataSources(conf, layer);
    String osmQuery = conf.getString("osm_query");
    layer.setOsmQuery(osmQuery);
    OpenDataServices.registerLayer(layer);
  }

  private void configureActions(HierarchicalConfiguration conf) throws ConfigurationException {
    for (HierarchicalConfiguration c : conf.configurationsAt("action")) {
      configureAction(c);
    }
  }
  
  private void configureAction(HierarchicalConfiguration conf) throws ConfigurationException {
    String name = conf.getString("[@name]");
    String type = conf.getString("[@type]");
    String menu = conf.getString("[@menu]");
    OdsLayer layer = OpenDataServices.getLayer(name);
    DownloadAction action = new DownloadAction();
    action.setName(name);
    action.setLayer(layer);
    configureMenu(action, menu);
  }
  
  private synchronized void configureMenu(Action action, String menu) {
    String[] menuParts = menu.split("\\.");
    String baseMenuName = menuParts[0];
    JMenu parent = getBaseMenu(baseMenuName, 4);
    for (int i=1; i<menuParts.length; i++) {
      parent = getChildMenu(parent, menuParts[i]);
    }
    parent.add(action);
  }

  private void configureFeatureMappers(HierarchicalConfiguration conf) throws ConfigurationException {
    for (HierarchicalConfiguration c : conf.configurationsAt("map")) {
      configureFeatureMapper(c);
    }
  }
  
  private void configureFeatureMapper(HierarchicalConfiguration conf) throws ConfigurationException {
    DefaultFeatureMapper mapper = new DefaultFeatureMapper();
    mapper.setFeatureName(conf.getString("[@feature]"));
    for (HierarchicalConfiguration c : conf.configurationsAt("tag")) {
      String value = c.getString("[@value]");
      String property = c.getString("[@property]");
      String format = c.getString("[@format]");
      String expression = c.getString("[@expression]");
      c.setThrowExceptionOnMissing(true);
      String key = c.getString("[@key]");
      if (value != null) {
        mapper.addTagBuilder(new FixedTagBuilder(key, value));
      }
      else if (property != null) {
        mapper.addTagBuilder(new PropertyTagBuilder(key, property, format));
      }
      else if (expression != null) {
        mapper.addTagBuilder(new ExpressionTagBuilder(key, expression));
      }
    }
    configureGeometryMapper(mapper, conf.configurationAt("geometry"));
    OpenDataServices.registerFeatureMapper(mapper);
  }
  
  private void configureGeometryMapper(DefaultFeatureMapper mapper, SubnodeConfiguration conf) throws ConfigurationException {
    String className = conf.getString("[@mapper]");
    String mapTo = conf.getString("[@map-to]");
    Boolean merge = Boolean.valueOf(conf.getString("[@merge]", "false"));
    if (className == null) {
      DefaultGeometryMapper geometryMapper = new DefaultGeometryMapper();
      geometryMapper.setTargetPrimitive(mapTo);
      mapper.setGeometryMapper(geometryMapper);
    }
    else {
      try {
        DefaultGeometryMapper geometryMapper = (DefaultGeometryMapper) classLoader.loadClass(className).newInstance();
        geometryMapper.setTargetPrimitive(mapTo);
        mapper.setGeometryMapper(geometryMapper);
      } catch (Exception e) {
        throw new ConfigurationException("Could not configure Geometry mapper", e);
      }
    }
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
