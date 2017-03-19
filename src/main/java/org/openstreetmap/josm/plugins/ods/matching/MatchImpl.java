package org.openstreetmap.josm.plugins.ods.matching;

import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.ODS;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.EntityStatus;
import org.openstreetmap.josm.plugins.ods.entities.EntityType;

public abstract class MatchImpl<E extends Entity> implements Match<E> {
    private Object id;
    private List<E> osmEntities = new LinkedList<>();
    private List<E> openDataEntities = new LinkedList<>();
    
    public MatchImpl(E osmEntity, E openDataEntity) {
        if (osmEntity != null && osmEntity.getReferenceId() != null) {
            id = osmEntity.getReferenceId();
            if (openDataEntity != null) {
                assert openDataEntity.getReferenceId().equals(id);
            }
        }
        else {
            if (openDataEntity.getReferenceId() != null) {
                id = openDataEntity.getReferenceId();
            }
        }
        if (id == null) {
            id = Match.generateUniqueId();
        }
        osmEntities.add(osmEntity);
        openDataEntities.add(openDataEntity);
        osmEntity.setMatch(this);
        openDataEntity.setMatch(this);
    }

    @Override
    public boolean isSimple() {
        return osmEntities.size() == 1 && openDataEntities.size() == 1;
    }

    @Override
    public boolean isSingleSided() {
        return osmEntities.size() == 0 || openDataEntities.size() == 0;
    }

    @Override
    public E getOsmEntity() {
        if (osmEntities.size() == 0) {
            return null;
        }
        return osmEntities.get(0);
    }

    @Override
    public E getOpenDataEntity() {
        if (openDataEntities.size() == 0) {
            return null;
        }
        return openDataEntities.get(0);
    }

    @Override
    public List<? extends E> getOsmEntities() {
        return osmEntities;
    }

    @Override
    public List<? extends E> getOpenDataEntities() {
        return openDataEntities;
    }

    @Override
    public <E2 extends E>void addOsmEntity(E2 entity) {
        osmEntities.add(entity);
        entity.setMatch(this);
    }

    @Override
    public <E2 extends E> void addOpenDataEntity(E2 entity) {
        openDataEntities.add(entity);
        entity.setMatch(this);
    }

    @Override
    public void updateMatchTags() {
        OsmPrimitive osm = getOpenDataEntity().getPrimitive();
        if (osm != null) {
            osm.put(ODS.KEY.BASE, "true");
            osm.put(ODS.KEY.GEOMETRY_MATCH, getGeometryMatch().toString());
            osm.put(ODS.KEY.STATUS, getOpenDataEntity().getStatus().toString());
            osm.put(ODS.KEY.STATUS_MATCH, getStatusMatch().toString());
            osm.put(ODS.KEY.TAG_MATCH, getAttributeMatch().toString());
            if (getOpenDataEntity().getStatus() == EntityStatus.REMOVAL_DUE) {
                osm.put(ODS.KEY.STATUS, EntityStatus.REMOVAL_DUE.toString());
            }
        }
    }

    @Override
    public Object getId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public EntityType<E> getEntityType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MatchStatus getGeometryMatch() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MatchStatus getAttributeMatch() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MatchStatus getStatusMatch() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Match)) {
            return false;
        }
        return (id.equals(((Match<?>)obj).getId()));
    }
}
