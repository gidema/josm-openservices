package org.openstreetmap.josm.plugins.ods.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openstreetmap.josm.plugins.ods.ParameterType;

public class OdsContextImpl implements OdsContext {
    private static String NO_LABEL = "NO_LABEL";
    
    private Map<Class<?>, Map<String, Object>> components = new HashMap<>();
    private Map<Class<?>, Provider<?>> providers = new HashMap<>();
    private Map<ParameterType<?>, Object> parameters = new HashMap<>();


    @Override
    public void register(Object component) {
        register(component, NO_LABEL);
    }

    @Override
    public <U> void register(U component, String label) {
        @SuppressWarnings("unchecked")
        Class<U> type = (Class<U>) component.getClass();
        register(type, component, label);
    }

    @Override
    public <T, U extends T> void register(Class<T> componentType,
            U implementation) {
        register(componentType, implementation, false);
    }

    @Override
    public <T, U extends T> void register(Class<T> componentType,
            U implementation, String label) {
        register(componentType, implementation, label, false);
    }

    @Override
    public <T, U extends T> void register(Class<T> componentType, U implementation, boolean overwrite) {
        register(componentType, implementation, NO_LABEL, overwrite);
    }
    
    @Override
    public <T, U extends T> void register(Class<T> componentType, U implementation, String label, boolean overwrite) {
        Map<String, Object> map = components.computeIfAbsent(componentType, c -> new HashMap<>());
        Object previous = map.put(label, implementation);
        if (previous != null && !overwrite) {
            throw new RuntimeException(String.format("A component for type %s has already been registered", componentType.toString()));
        }
    }

    @Override
    public <T, U extends T> void register(Class<T> componentType, Class<U> implType) {
        if (getComponent(componentType) != null) {
            throw new RuntimeException(String.format("A component for type %s has already been registered", componentType.toString()));          
        }
        Provider<T> provider = NewInstanceProvider.getProvider(implType);
        providers.put(componentType, provider);
    }


    @Override
    public <T, U extends T> U getComponent(Class<T> componentType) {
        return getComponent(componentType, NO_LABEL);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T, U extends T> U getComponent(Class<T> componentType, String label) {
        Map<String, Object> map = components.get(componentType);
        if (map != null) return (U) map.get(label);
        Provider<U> provider = (Provider<U>) providers.get(componentType);
        if (provider != null) {
            return provider.getComponent(this);
        }
        return null;
    }

    // TODO consider a form of caching
    @SuppressWarnings("unchecked")
    @Override
    public <T, U extends T> List<U> getComponents(Class<T> componentType) {
        List<U> result = new ArrayList<>();
        components.values().forEach(map -> {
            map.forEach((label, component) -> {
                if (label == NO_LABEL && 
                        componentType.isAssignableFrom(component.getClass())) {
                    result.add((U) component);
                }
            });
        });
        return result;
    }

    @Override
    public <T> void setParameter(ParameterType<T> key, T value) {
        parameters.put(key, value);
    }

    @Override
    public <T> T getParameter(ParameterType<T> key) {
        return getParameter(key, null);
    }

    @Override
    public <T> T getParameter(ParameterType<T> key, T defaultValue) {
        @SuppressWarnings("unchecked")
        T value = (T) parameters.get(key);
        if (value != null) return value;
        if (defaultValue != null) return defaultValue;
        throw new RuntimeException(String.format("Parameter '%s' has not been configured.", key.toString()));
    }

    @Override
    public void reset() {
//        components.forEach(OdsComponent::reset);
    }
    
    @Override
    public void clear() {
        components.clear();
    }
}
