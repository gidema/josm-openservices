package org.openstreetmap.josm.plugins.ods.context;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class NewInstanceProvider<T> implements Provider<T> {
    private final Constructor<? extends T> constructor;
    private boolean contextAware = false;
    
    private NewInstanceProvider(Constructor<? extends T> constructor,
            boolean contextAware) {
        super();
        this.constructor = constructor;
        this.contextAware = contextAware;
    }

    public static <T> NewInstanceProvider<T> getProvider(Class<? extends T> implementationClass) {
        try {
            Constructor<? extends T> constructor = implementationClass.getConstructor(OdsContext.class);
            return new NewInstanceProvider<>(constructor, true);
        } catch (NoSuchMethodException e) {
            return getProviderNoContext(implementationClass);
        }
        catch (SecurityException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    private static <T> NewInstanceProvider<T> getProviderNoContext(Class<? extends T> implementationClass) {
        try {
            Constructor<? extends T> constructor = implementationClass.getConstructor();
            return new NewInstanceProvider<>(constructor, false);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public T getComponent(OdsContext context) {
        try {
            if (contextAware) {
                return constructor.newInstance(context);
            }
            return constructor.newInstance();            
        } catch (InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {
            return null;
        }
    }
}
