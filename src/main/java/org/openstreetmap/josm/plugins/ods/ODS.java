package org.openstreetmap.josm.plugins.ods;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JMenu;

import org.apache.commons.configuration.ConfigurationException;
import org.openstreetmap.josm.plugins.ods.tags.FeatureMapper;
import org.openstreetmap.josm.tools.I18n;

/**
 * This class maintains some global objects.
 * It may be better to replace this with some dependency injection container,
 * but for now it serves it purpose.
 * 
 * @author gertjan
 *
 */
public class ODS {
    private static Map<String, OdsModule> modules = new HashMap<>();
	private static Map<String, Host> hosts = new HashMap<>();
	private static Map<String, OdsWorkingSet> layers = new HashMap<>();
	private static Map<String, FeatureMapper> featureMappers = new HashMap<>();
	private static Map<String, Class<?>> imports = new HashMap<>();
    // The classloader for all classes in this Plug-in an its modules.
//	private static ClassLoader classLoader;
	
//	private static OdsModule module;

    private static JMenu menu;

//	public static void setModule(OdsModule module) {
//        ODS.module = module;
//        menu.setText("ODS "+ module.getName());
//    }

    public static OdsModule getModule() {
        //Maximal 1 module at the moment
        if (modules.isEmpty()) return null;
        return modules.values().iterator().next();
    }

	public static JMenu getMenu() {
        return menu;
    }

    public static void setMenu(JMenu menu) {
        ODS.menu = menu;
    }

    public static void registerImport(String type, String name, Class<?> clazz)
			throws ConfigurationException {
		String key = type + ":" + name;
		if (imports.containsKey(key)) {
			throw new ConfigurationException(I18n.tr(
					"A ''{0}'' import named ''{1}'' already exists", type, name));
		}
		imports.put(key, clazz);
	}

	public static Host registerHost(String type, String name, String url, Integer maxFeatures)
			throws ConfigurationException {
		Host existingHost = hosts.get(name);
		if (existingHost != null) {
			if (existingHost.getType().equals(type)
					&& existingHost.getUrl().equals(url))
				return existingHost;
			throw new ConfigurationException(I18n.tr(
					"An other host named ''{0}'' already exists", name));
		}
		Host host = (Host) createObject("host", type);
		host.setName(name);
		host.setUrl(url);
		host.setMaxFeatures(maxFeatures);
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

	public static Host getHost(String name) throws ConfigurationException {
		Host host = hosts.get(name);
		if (host == null) {
			throw new ConfigurationException("Unknown host type:" + name);
		}
		return host;
	}

	public static OdsWorkingSet getLayer(String name)
			throws UnknownLayerException {
		OdsWorkingSet layer = layers.get(name);
		if (layer == null) {
			throw new UnknownLayerException(name);
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

	public static Class<?> getClass(String type, String name) throws ConfigurationException {
        Class<?> clazz = imports.get(type + ":" + name);
        if (clazz == null) {
            throw new ConfigurationException(String.format(
                    "A '%s' type named '%s' doesn't exist", type, name));
        }
	    return clazz;
	}
	
	public static Object createObject(String type, String name)
			throws ConfigurationException {
		Class<?> clazz = getClass(type, name);
		try {
			return clazz.newInstance();
		} catch (Exception e) {
			throw new ConfigurationException(e);
		}
	}
	
//    public static ClassLoader getClassLoader() {
//        return classLoader;
//    }
//
//    public static void setClassLoader(ClassLoader classLoader) {
//        ODS.classLoader = classLoader;
//    }

    public static void registerModule(OdsModule module) {
        modules.put(module.getName(), module);
    }

    public static Collection<OdsModule> getModules() {
        return modules.values();
    }
}
