package org.openstreetmap.josm.plugins.ods;

import java.util.HashMap;

public class Context {
    private HashMap<String, Object> ctx = new HashMap<>();
    
    public void put(String key, Object value) {
        ctx.put(key, value);
    }
    
    public Object get(String key) {
        return ctx.get(key);
    }
}
