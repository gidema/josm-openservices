package org.openstreetmap.josm.plugins.ods.context;

import java.util.List;

import org.openstreetmap.josm.plugins.ods.ParameterType;

public interface OdsContext {

    public <U> void register(U component);

    public <U> void register(U component, String label);

    public <T, U extends T> void register(Class<T> componentType, U implementation);

    public <T, U extends T> void register(Class<T> componentType, U implementation, String label);

    public <T, U extends T> void register(Class<T> componentType, U implementation, boolean overwrite);

    public <T, U extends T> void register(Class<T> componentType, U implementation, String label, boolean overwrite);

    public <T, U extends T> void register(Class<T> componentType, Class<U> implType);

    /**
     * Get a component that implements the requested component type, or null if not present
     * @param <T>
     * @param <U>
     * @param componentType
     * @return
     */
    public <T, U extends T> U getComponent(Class<T> componentType);
    
    /**
     * Get a component that implements the requested component type, or null if not present
     * @param <T>
     * @param <U>
     * @param componentType
     * @return
     */
    public <T, U extends T> U getComponent(Class<T> componentType, String label);
    
    /**
     * Get all components that implement the requested component type, or an empty list.
     * @param <T>
     * @param <U>
     * @param componentType
     * @return
     */
    public <T, U extends T> List<U> getComponents(Class<T> componentType);
    
    public void reset();

    public void clear();

    public <T> void setParameter(ParameterType<T> key, T value);
    
    public <T> T getParameter(ParameterType<T> key);

    public <T> T getParameter(ParameterType<T> key, T defaultValue);

}
