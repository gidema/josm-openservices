package org.openstreetmap.josm.plugins.openservices;


import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JMenu;

import nl.gertjanidema.conversion.valuemapper.ValueMapper;
import nl.gertjanidema.conversion.valuemapper.ValueMapperException;
import nl.gertjanidema.conversion.valuemapper.ValueMapperFactory;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.openstreetmap.josm.plugins.openservices.entities.ImportEntityBuilder;
import org.openstreetmap.josm.plugins.openservices.metadata.HttpMetaDataLoader;
import org.openstreetmap.josm.plugins.openservices.metadata.MetaDataAttribute;
import org.openstreetmap.josm.plugins.openservices.metadata.MetaDataLoader;
import org.openstreetmap.josm.plugins.openservices.tags.DefaultFeatureMapper;
import org.openstreetmap.josm.plugins.openservices.tags.DefaultGeometryMapper;
import org.openstreetmap.josm.plugins.openservices.tags.ExpressionTagBuilder;
import org.openstreetmap.josm.plugins.openservices.tags.FixedTagBuilder;
import org.openstreetmap.josm.plugins.openservices.tags.MetaTagBuilder;
import org.openstreetmap.josm.plugins.openservices.tags.PropertyTagBuilder;
import org.openstreetmap.josm.tools.ImageProvider;

public class ConfigurationReader {
  private final ClassLoader classLoader;
 
  public ConfigurationReader(ClassLoader classLoader) {
    this.classLoader = classLoader;
  }

  public void read(URL configFile) throws ConfigurationException {
    try {
      XMLConfiguration conf = new XMLConfiguration();
      conf.setDelimiterParsingDisabled(true);
      conf.setAttributeSplittingDisabled(true);
      conf.load(configFile);
      configureImports(conf);
      configureHosts(conf);
      configureEntityBuilders(conf);
      configureLayers(conf);
      configureFeatureMappers(conf);
    } catch (NoSuchElementException e) {
      throw new ConfigurationException(e.getMessage(), e.getCause());
    }
  }
  
  private void configureImports(HierarchicalConfiguration conf) throws ConfigurationException {
    List<HierarchicalConfiguration> confs = conf.configurationsAt("import");
    for (HierarchicalConfiguration c : confs) {
      configureImport(c);
    }
  }
  
  private void configureImport(HierarchicalConfiguration conf) throws ConfigurationException {
    conf.setThrowExceptionOnMissing(true);
    String type = conf.getString("[@type]");
    String name = conf.getString("[@name]");
    String className = conf.getString("[@class]");
    Class<?> clazz;
    try {
      clazz = classLoader.loadClass(className);
      OpenDataServices.registerImport(type, name, clazz);
    } catch (ClassNotFoundException e) {
      throw new ConfigurationException(e.getMessage());
    }
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
    Host host = OpenDataServices.registerHost(type, name, url);
    for (MetaDataLoader metaDataLoader : parseMetaDataLoaders(conf)) {
      host.addMetaDataLoader(metaDataLoader);
    }
  }

  private void configureDataSources(HierarchicalConfiguration conf, OdsWorkingSet layer) throws ConfigurationException {
    for (HierarchicalConfiguration c : conf.configurationsAt("datasource")) {
      configureDataSource(c, layer);
    }
  }

  private void configureDataSource(HierarchicalConfiguration conf, OdsWorkingSet layer) throws ConfigurationException {
    OdsFeatureSource odsFeatureSource = configureOdsFeatureSource(conf);
    OdsDataSource dataSource = odsFeatureSource.newDataSource();
    String filter = conf.getString("filter", null);
    if (filter != null) {
      configureFilter(dataSource, filter);
    }
    String idAttribute = conf.getString("id[@attribute]", null);
    configureIdFactory(dataSource, idAttribute);
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

  private void configureIdFactory(OdsDataSource dataSource,
      String idAttribute) throws ConfigurationException {
    DefaultIdFactory idFactory = new DefaultIdFactory(dataSource);
    idFactory.setKeyAttribute(idAttribute);
    dataSource.setIdFactory(idFactory);
  }

  private OdsFeatureSource configureOdsFeatureSource(HierarchicalConfiguration conf) throws ConfigurationException {
    conf.setThrowExceptionOnMissing(true);
    String hostName = conf.getString("[@host]");
    String feature = conf.getString("[@feature]");
    Host host = OpenDataServices.getHost(hostName);
    try {
      return host.getOdsFeatureSource(feature);
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
    OdsWorkingSet workingSet = new OdsWorkingSet();
    workingSet.setName(name);
    configureDataSources(conf, workingSet);
    String osmQuery = conf.getString("osm_query");
    workingSet.setOsmQuery(osmQuery);
    configureActions(workingSet, conf);
    JMenu odsMenu = OpenDataServicesPlugin.getMenu();
    Action action = new OdsWorkingSetAction(workingSet);
    odsMenu.add(action);
  }

  private void configureActions(OdsWorkingSet layer, HierarchicalConfiguration conf) throws ConfigurationException {
    for (HierarchicalConfiguration c : conf.configurationsAt("action")) {
      configureAction(layer, c);
    }
  }
  
  private void configureAction(OdsWorkingSet layer, HierarchicalConfiguration conf) throws ConfigurationException {
    String name = conf.getString("[@name]", null);
    String type = conf.getString("[@type]");
    String iconName = conf.getString("[@icon]");
    try {
      OdsAction action = (OdsAction) OpenDataServices.createObject("action", type);
      if (name != null) {
        action.setName(name);
      }
      if (iconName != null) {
        ImageIcon icon = ImageProvider.getIfAvailable(iconName);
        if (icon == null) {
          throw new ConfigurationException("No icon found named " + iconName);
        }
        action.setIcon(icon);
      }
      layer.addAction(action);
    } catch (Exception e) {
      throw new ConfigurationException(e);
    }
  }
  
//  private synchronized void configureMenu(Action action, String menu) {
//    String[] menuParts = menu.split("\\.");
//    String baseMenuName = menuParts[0];
//    JMenu parent = getBaseMenu(baseMenuName, 4);
//    for (int i=1; i<menuParts.length; i++) {
//      parent = getChildMenu(parent, menuParts[i]);
//    }
//    parent.add(action);
//  }

  private void configureEntityBuilders(HierarchicalConfiguration conf) throws ConfigurationException {
	  for (HierarchicalConfiguration c : conf.configurationsAt("entity")) {
		  configureEntityBuilder(c);
	  }
  }
  
  private void configureEntityBuilder(HierarchicalConfiguration conf) throws ConfigurationException {
	    String className = conf.getString("[@builder]");
	    if (className != null) {
	        try {
	            ImportEntityBuilder<?> builder = (ImportEntityBuilder<?>) classLoader.loadClass(className).newInstance();
	            OpenDataServices.registerEntityBuilder(builder);
	          } catch (Exception e) {
	            throw new ConfigurationException("Could not configure Entity builder", e);
	          }
	    }
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
      String meta = c.getString("[@meta]");
      c.setThrowExceptionOnMissing(true);
      String key = c.getString("[@key]");
      if (value != null) {
        mapper.addTagBuilder(new FixedTagBuilder(key, value));
      }
      else if (meta != null) {
        mapper.addTagBuilder(new MetaTagBuilder(key, meta, format));
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

  private List<MetaDataLoader> parseMetaDataLoaders(HierarchicalConfiguration conf) throws ConfigurationException {
    List<MetaDataLoader> loaders = new LinkedList<MetaDataLoader>();
    for (HierarchicalConfiguration c : conf.configurationsAt("meta")) {
      loaders.add(parseMetaDataLoader(c));
    }
    return loaders;
  }
  
  private MetaDataLoader parseMetaDataLoader(HierarchicalConfiguration conf) throws ConfigurationException {
    String url = conf.getString("[@url]", null);
    // TODO implement POST
    // String method = c.getString("method", "GET");
    if (url == null) {
      throw new ConfigurationException("Required parameter 'url' is missing");
    }
    HttpMetaDataLoader metaDataLoader = new HttpMetaDataLoader(url);
    configureMetaDataProperties(conf, metaDataLoader);
    return metaDataLoader;
  }
  
  private void configureMetaDataProperties(HierarchicalConfiguration conf, HttpMetaDataLoader metaDataLoader) throws ConfigurationException {
    for (HierarchicalConfiguration c : conf.configurationsAt("property")) {
      configureMetaDataProperty(c, metaDataLoader);
    }
  }

  private void configureMetaDataProperty(HierarchicalConfiguration conf, HttpMetaDataLoader metaDataLoader) throws ConfigurationException {
    String name = conf.getString("[@name]");
    String type = conf.getString("[@type]", "string");
    String query = conf.getString("[@query]", null);
    String pattern = conf.getString("[@pattern]", null);
    Properties properties = new Properties();
    if (pattern != null) {
      properties.put("pattern", pattern);
    }
    ValueMapperFactory vmFactory = new ValueMapperFactory();
    ValueMapper valueMapper;
    try {
      valueMapper = vmFactory.createValueMapper(type, properties);
      MetaDataAttribute attr = new MetaDataAttribute(name, query, valueMapper);
      metaDataLoader.addAttribute(attr);
    } catch (ValueMapperException e) {
      throw new ConfigurationException(e);
    }
    
  }
}
