package org.openstreetmap.josm.plugins.ods.matching;

import java.util.List;

import org.openstreetmap.josm.plugins.ods.entities.Entity;

public interface Match<E extends Entity> {
    
    /**
     * A match is considered simple, if it contains exactly one OSM
     * entity and one Open Data entity
     * 
     * @return true if this is a simple match
     */
    boolean isSimple();
    
    boolean isSingleSided();
    
    public E getOsmEntity();
    
    public E getOpenDataEntity();
    
    public List<? extends E> getOsmEntities();
    
    public List<? extends E> getOpenDataEntities();
    
    public <E2 extends E> void addOsmEntity(E2 entity);
    
    public <E2 extends E> void addOpenDataEntity(E2 entity);

    public boolean isGeometryMatch();
    
    public boolean isAttributeMatch();

    public void analyze();
}
