package org.openstreetmap.josm.plugins.ods.matching;

import java.util.Collection;

import org.openstreetmap.josm.plugins.ods.entities.OdEntity;
import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;
import org.openstreetmap.josm.plugins.ods.entities.impl.ZeroOneMany;

public abstract class MatchImpl<T1 extends OdEntity, T2 extends OsmEntity> implements Match<T1, T2> {
    private Object id;
    private final ZeroOneMany<T1> openDataEntities;
    private final ZeroOneMany<T2> osmEntities;

    public MatchImpl(T1 openDataEntity, ZeroOneMany<T2> osmEntities) {
        this(new ZeroOneMany<>(openDataEntity), osmEntities);
    }

    public MatchImpl(Collection<T1> odEntities, T2 osmEntity) {
        this(new ZeroOneMany<>(odEntities), new ZeroOneMany<>(osmEntity));
    }

    public MatchImpl(T1 odEntity, T2 osmEntity) {
        this(new ZeroOneMany<>(odEntity), new ZeroOneMany<>(osmEntity));
    }

    public MatchImpl(ZeroOneMany<T1> openDataEntities, ZeroOneMany<T2> osmEntities) {
        super();
        this.osmEntities = osmEntities;
        this.openDataEntities = openDataEntities;
    }


    public MatchImpl(T1 odEntity, Collection<T2> osmEntities) {
        this.openDataEntities = new ZeroOneMany<>(odEntity);
        this.osmEntities = new ZeroOneMany<>(osmEntities);
    }

    @Override
    public boolean isSimple() {
        return osmEntities.isOne() && openDataEntities.isOne();
    }

    @Override
    public ZeroOneMany<T1> getOpenDataEntities() {
        return openDataEntities;
    }

    @Override
    public ZeroOneMany<T2> getOsmEntities() {
        return osmEntities;
    }

    @Override
    public <E extends T1> void addOdEntity(E entity) {
        openDataEntities.add(entity);
    }

    @Override
    public <E extends T2> void addOsmEntity(E entity) {
        osmEntities.add(entity);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
