package org.openstreetmap.josm.plugins.ods.saxparser.api;

public interface SaxHandler {
    public SaxRootHandler getRootHandler();
    
    public <T> T getContextItem(Class<T> type);

}
