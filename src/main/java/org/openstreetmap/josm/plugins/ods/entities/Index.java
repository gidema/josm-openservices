package org.openstreetmap.josm.plugins.ods.entities;

public interface Index<T> {

    /**
     * @return true if this index contains no duplicates
     */
    public boolean isUnique();

    /**
     * Add an entity to the index.
     *
     * @param entity The entity to add
     * @return true if the new entity was added successfully
     */
    public boolean insert(T entity);

    /**
     * Remove an entity from the index
     *
     * @param entity The entity to remove
     */
    public void remove(T entity);

    /**
     * Remove all entities from the index.
     */
    public void clear();

}