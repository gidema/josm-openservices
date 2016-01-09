package org.openstreetmap.josm.plugins.ods.matching;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.EntityType;

public interface Match<E extends Entity> {
    final static AtomicLong idCounter = new AtomicLong(0);
    
    public Object getId();
    /**
     * A match is considered simple, if it contains exactly one OSM
     * entity and one Open Data entity
     * 
     * @return true if this is a simple match
     */
    boolean isSimple();
    
    boolean isSingleSided();
    
    public EntityType<E> getEntityType();
    
    public E getOsmEntity();
    
    public E getOpenDataEntity();
    
    public List<? extends E> getOsmEntities();
    
    public List<? extends E> getOpenDataEntities();
    
    public <E2 extends E> void addOsmEntity(E2 entity);
    
    public <E2 extends E> void addOpenDataEntity(E2 entity);

    public MatchStatus getGeometryMatch();
    
    public MatchStatus getAttributeMatch();

    public MatchStatus getStatusMatch();
    
    public void analyze();

    public void updateMatchTags();
    
    static Long generateUniqueId() {
        return idCounter.decrementAndGet();
    }
}
