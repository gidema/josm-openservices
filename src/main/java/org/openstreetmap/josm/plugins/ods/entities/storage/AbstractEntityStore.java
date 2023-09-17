package org.openstreetmap.josm.plugins.ods.entities.storage;

/**
 * The EntityStore stores entities of a single entity type.
 * 
 * @author gertjan
 *
 */
public abstract class AbstractEntityStore<T> implements EntityStore<T> {
    
    public AbstractEntityStore() {
        super();
    }
}
