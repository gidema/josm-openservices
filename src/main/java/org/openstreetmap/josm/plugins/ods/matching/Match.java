package org.openstreetmap.josm.plugins.ods.matching;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.openstreetmap.josm.plugins.ods.entities.OdEntity;
import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;

public interface Match<T1 extends OsmEntity, T2 extends OdEntity> {
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

    //    public Class<E> getEntityClass();

    public T1 getOsmEntity();

    public T2 getOpenDataEntity();

    public List<? extends T1> getOsmEntities();

    public List<? extends T2> getOpenDataEntities();

    public <E extends T1> void addOsmEntity(E entity);

    public <E extends T2> void addOpenDataEntity(E entity);

    public MatchStatus getGeometryMatch();

    public MatchStatus getAttributeMatch();

    public MatchStatus getStatusMatch();

    public void analyze();

    public void updateMatchTags();

    static Long generateUniqueId() {
        return idCounter.decrementAndGet();
    }
}
