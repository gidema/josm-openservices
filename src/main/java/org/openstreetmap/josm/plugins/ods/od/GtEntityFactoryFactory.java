package org.openstreetmap.josm.plugins.ods.od;

import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;

import org.opengis.feature.type.Name;
import org.openstreetmap.josm.plugins.ods.entities.EntityModifier;
import org.openstreetmap.josm.plugins.ods.geotools.GtEntityFactory;
import org.openstreetmap.josm.plugins.ods.geotools.impl.ModifiableGtEntityFactory;

public class GtEntityFactoryFactory {
    static private List<GtEntityFactory<?>> factories = new LinkedList<>();
    static private List<EntityModifier<?>> modifiers = new LinkedList<>();
   
    static {
        loadModifiers();
        loadFactories();
    }
    
    private static List<GtEntityFactory<?>> loadFactories() {
        @SuppressWarnings("rawtypes")
        ServiceLoader<GtEntityFactory> serviceLoader = ServiceLoader.load(GtEntityFactory.class);
        serviceLoader.forEach(factory -> {
            if (factory instanceof ModifiableGtEntityFactory) {
                ModifiableGtEntityFactory<?> mFactory = (ModifiableGtEntityFactory<?>) factory;
                modifiers.forEach(modifier -> {
                    if (modifier.getTargetType().equals(factory.getTargetType())) {
                        mFactory.addModifier(modifier);
                    }
                });
                factories.add(factory);
            }
        });
        return factories;
    }
    
    private static void loadModifiers() {
        @SuppressWarnings("rawtypes")
        ServiceLoader<EntityModifier> serviceLoader = ServiceLoader.load(EntityModifier.class);
        serviceLoader.forEach(modifier -> {
                modifiers.add(modifier);
        });
    }
    
    @SuppressWarnings("unchecked")
    public static <T> GtEntityFactory<T> create(Name name, Class<T> targetType) {
        for (GtEntityFactory<?> factory : factories) {
            if (factory.isApplicable(name, targetType)) {
                return (GtEntityFactory<T>) factory;
            }
        }
        return null;
    }
}
