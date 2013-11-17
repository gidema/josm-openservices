package org.openstreetmap.josm.plugins.openservices;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.openstreetmap.josm.plugins.openservices.entities.imported.ImportedEntityBuilder;
import org.openstreetmap.josm.plugins.openservices.tags.FeatureMapper;

public class OpenDataServices {
	private static Map<String, Host> hosts = new HashMap<String, Host>();
	private static Map<String, OdsWorkingSet> layers = new HashMap<String, OdsWorkingSet>();
	private static Map<String, FeatureMapper> featureMappers = new HashMap<String, FeatureMapper>();
	private static Map<String, ImportedEntityBuilder<?>> entityBuilders = new HashMap<String, ImportedEntityBuilder<?>>();
	private static Map<String, Class<?>> imports = new HashMap<String, Class<?>>();

	public static void registerImport(String type, String name, Class<?> clazz)
			throws ConfigurationException {
		String key = type + ":" + name;
		if (imports.containsKey(key)) {
			throw new ConfigurationException(String.format(
					"A '%s' import named '%s' already exists", type, name));
		}
		imports.put(key, clazz);
	}

	public static Host registerHost(String type, String name, String url)
			throws ConfigurationException {
		Host existingHost = hosts.get(name);
		if (existingHost != null) {
			if (existingHost.getType().equals(type)
					&& existingHost.getUrl().equals(url))
				return existingHost;
			throw new ConfigurationException(String.format(
					"An other host named '%s' already exists", name));
		}
		Host host = (Host) createObject("host", type);
		host.setName(name);
		host.setUrl(url);
		hosts.put(name, host);
		return host;
	}

	public static void registerLayer(OdsWorkingSet layer)
			throws ConfigurationException {
		if (layers.get(layer.getName()) != null) {
			throw new ConfigurationException(String.format(
					"A layer named '%s' already exists", layer.getName()));
		}
		layers.put(layer.getName(), layer);
	}

	public static void registerFeatureMapper(FeatureMapper mapper)
			throws ConfigurationException {
		if (featureMappers.get(mapper.getFeatureName()) != null) {
			throw new ConfigurationException(
					String.format("A mapper for '%s' already exists",
							mapper.getFeatureName()));
		}
		featureMappers.put(mapper.getFeatureName(), mapper);
	}

	public static void registerEntityBuilder(ImportedEntityBuilder builder)
			throws ConfigurationException {
		if (entityBuilders.get(builder.getFeatureName()) != null) {
			throw new ConfigurationException(String.format(
					"A mapper for '%s' already exists",
					builder.getFeatureName()));
		}
		entityBuilders.put(builder.getFeatureName(), builder);
	}

	public static Host getHost(String name) throws ConfigurationException {
		Host host = hosts.get(name);
		if (host == null) {
			throw new ConfigurationException("Unknown host type:" + name);
		}
		return host;
	}

	public static OdsWorkingSet getLayer(String name)
			throws ConfigurationException {
		OdsWorkingSet layer = layers.get(name);
		if (layer == null) {
			throw new ConfigurationException(String.format(
					"OdsWorkingSet '%s' does not exist", name));
		}
		return layer;
	}

	public static FeatureMapper getFeatureMapper(String feature)
			throws ConfigurationException {
		FeatureMapper mapper = featureMappers.get(feature);
		if (mapper == null) {
			throw new ConfigurationException(String.format(
					"No mapper for featureName '%s' exists", feature));
		}
		return mapper;
	}

	public static ImportedEntityBuilder<?> getEntityBuilder(String feature)
			throws ConfigurationException {
		ImportedEntityBuilder<?> builder = entityBuilders.get(feature);
		if (builder == null) {
			throw new ConfigurationException(String.format(
					"No entity builder for featureName '%s' exists", feature));
		}
		return builder;
	}

	public static Object createObject(String type, String name)
			throws ConfigurationException {
		Class<?> clazz = imports.get(type + ":" + name);
		if (clazz == null) {
			throw new ConfigurationException(String.format(
					"A '%s' type named '%s' doesn't exist", type, name));
		}
		try {
			return clazz.newInstance();
		} catch (Exception e) {
			throw new ConfigurationException(e);
		}
	}

}
