package org.openstreetmap.josm.plugins.ods.mapping;

import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.entities.OdEntity;
import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;

public abstract class AbstractMapping<T1 extends OsmEntity, T2 extends OdEntity> implements Mapping<T1, T2> {
    private Object id;
    private final List<T1> osmEntities = new LinkedList<>();
    private final List<T2> openDataEntities = new LinkedList<>();

    public AbstractMapping(T1 osmEntity, T2 odEntity) {
        id = Mapping.generateUniqueId();
        if (osmEntity != null) {
            osmEntities.add(osmEntity);
            osmEntity.setMapping(this);
        }
        if (odEntity != null) {
            openDataEntities.add(odEntity);
            odEntity.setMapping(this);
        }
    }

    @Override
    public boolean isSimple() {
        return osmEntities.size() == 1 && openDataEntities.size() == 1;
    }

    @Override
    public boolean isTwoWay() {
        return !osmEntities.isEmpty() && !openDataEntities.isEmpty();
    }

    @Override
    public T1 getOsmEntity() {
        if (osmEntities.size() == 0) {
            return null;
        }
        return osmEntities.get(0);
    }

    @Override
    public T2 getOpenDataEntity() {
        if (openDataEntities.size() == 0) {
            return null;
        }
        return openDataEntities.get(0);
    }

    @Override
    public List<? extends T1> getOsmEntities() {
        return osmEntities;
    }

    @Override
    public List<? extends T2> getOpenDataEntities() {
        return openDataEntities;
    }

    @Override
    public <E extends T1>void addOsmEntity(E entity) {
        osmEntities.add(entity);
        entity.setMapping(this);
    }

    @Override
    public <E extends T2> void addOpenDataEntity(E entity) {
        openDataEntities.add(entity);
        entity.setMapping(this);
    }

    @Override
    public void refreshUpdateTags() {
        OdEntity odEntity = getOpenDataEntity();
        if (odEntity != null) {
            odEntity.refreshUpdateTags();
        }
    }

    @Override
    public Object getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Mapping)) {
            return false;
        }
        return (id.equals(((Mapping<?, ?>)obj).getId()));
    }
}
