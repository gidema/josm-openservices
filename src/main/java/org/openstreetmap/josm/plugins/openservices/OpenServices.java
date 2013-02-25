package org.openstreetmap.josm.plugins.openservices;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;

public class OpenServices {
  private static Map<String, HostType> hostTypes =
      new HashMap<String, HostType>();
  private static Map<String, Host> hosts = new HashMap<String, Host>();
  private static Map<String, DataSource> dataSources = new HashMap<String, DataSource>();
  private static Map<String, ServiceLayer> layers = new HashMap<String, ServiceLayer>();
  private static Map<String, FeatureMapper> featureMappers = new HashMap<String, FeatureMapper>();
  
//  public static void configure(URL configFile) throws ConfigurationException {
//    ConfigurationReader.read(configFile);
//  }

  public static void registerHostType(HostType hostType) throws ConfigurationException {
    String name = hostType.getName();
    if (hostTypes.containsKey(name)) {
      throw new ConfigurationException(
        String.format("Host type '%s' already exists", name));
    }
    hostTypes.put(name, hostType);
  }
  
  public static void registerHost(Host host) throws ConfigurationException {
    Host existingHost = hosts.get(host.getName());
    if (existingHost != null) {
      if (existingHost.equals(host)) return;
      throw new ConfigurationException(
          String.format("An other host named '%s' already exists",
            host.getName()));
    }
    hosts.put(host.getName(), host); 
  }

  public static void registerDataSource(DataSource dataSource) throws ConfigurationException {
    if (dataSources.get(dataSource.getName()) != null) {
      throw new ConfigurationException(String.format(
          "An other DataSource named '%s' already exists", dataSource.getName()));
    }
    dataSources.put(dataSource.getName(), dataSource);
  }
  
  public static void registerLayer(ServiceLayer layer) throws ConfigurationException {
    if (layers.get(layer.getName()) != null) {
      throw new ConfigurationException(String.format(
          "A layer named '%s' already exists", layer.getName()));
    }
    layers.put(layer.getName(), layer);
  }
  
  public static void registerFeatureMapper(FeatureMapper mapper) throws ConfigurationException {
    if (featureMappers.get(mapper.getFeatureName()) != null) {
      throw new ConfigurationException(String.format(
          "A mapper for '%s' already exists", mapper.getFeatureName()));
    }
    featureMappers.put(mapper.getFeatureName(), mapper);
  }
  
  public static HostType getHostType(String name) throws ConfigurationException {
    HostType hostType = hostTypes.get(name);
    if (hostType == null) {
      throw new ConfigurationException("Unknown host type:" + name);
    }
    return hostType;
  }
  
  public static Host getHost(String name) throws ConfigurationException {
    Host host = hosts.get(name);
    if (host == null) {
      throw new ConfigurationException("Unknown host type:" + name);
    }
    return host;
  }
  
  public static DataSource getDataSource(String name) throws ConfigurationException {
    DataSource dataSource = dataSources.get(name);
    if (dataSource == null) {
      throw new ConfigurationException("Unknown dataSource:" + name);
    }
    return dataSource;
  }

  public static ServiceLayer getLayer(String name) throws ConfigurationException {
    ServiceLayer layer = layers.get(name);
    if (layer == null) {
      throw new ConfigurationException(String.format(
          "ServiceLayer '%s' does not exist", name));
    }
    return layer;
  }

  public static FeatureMapper getFeatureMapper(String feature) throws ConfigurationException {
    FeatureMapper mapper = featureMappers.get(feature);
    if (mapper == null) {
      throw new ConfigurationException(String.format(
          "No mapper for feature '%s' exists", feature));
    }
    return mapper;
  }
}
