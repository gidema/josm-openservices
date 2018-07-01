package org.openstreetmap.josm.plugins.ods;

import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.josm.tools.I18n;

public class Context {
    private final Map<Object, Object> components = new HashMap<>();
    private final Context parent;

    public Context() {
        this.parent = null;
    }

    public Context(Context parent) {
        this.parent = parent;
    }

    public <T> T put(T object) {
        components.put(object.getClass(), object);
        return object;
    }

    public <T> T put(Class<? super T> clazz, T object) {
        components.put(clazz, object);
        return object;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> key) {
        T result = (T) components.get(key);
        if (result != null) {
            return result;
        }
        if (parent != null) {
            return parent.get(key);
        }
        throw new IllegalArgumentException(I18n.tr("No object of type ''{0}'' was found in the current context. This must be a programming error", key));
    }
}
