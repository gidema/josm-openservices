package org.openstreetmap.josm.plugins.ods.matching;

import org.openstreetmap.josm.plugins.ods.entities.OdEntity;
import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;
import org.openstreetmap.josm.plugins.ods.entities.impl.ZeroOneMany;

public interface Match<T1 extends OdEntity, T2 extends OsmEntity> {
    /**
     * A match is considered simple, if it contains exactly one OSM
     * entity and one Open Data entity
     *
     * @return true if this is a simple match
     */
    boolean isSimple();

    public ZeroOneMany<T1> getOpenDataEntities();

    public ZeroOneMany<T2> getOsmEntities();

    public <E extends T1> void addOdEntity(E entity);

    public <E extends T2> void addOsmEntity(E entity);
}
