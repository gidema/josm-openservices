package org.openstreetmap.josm.plugins.ods.matching;

import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.ODS;
import org.openstreetmap.josm.plugins.ods.entities.EntityStatus;
import org.openstreetmap.josm.plugins.ods.entities.OdEntity;
import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;

public abstract class MatchImpl<T1 extends OsmEntity, T2 extends OdEntity> implements Match<T1, T2>, OsmMatch<T1>, OdMatch<T2> {
    private Object id;
    private final List<T1> osmEntities = new LinkedList<>();
    private final List<T2> openDataEntities = new LinkedList<>();

    public MatchImpl(T1 osmEntity, T2 openDataEntity) {
        id = Match.generateUniqueId();
        osmEntities.add(osmEntity);
        openDataEntities.add(openDataEntity);
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
    }

    @Override
    public <E extends T2> void addOpenDataEntity(E entity) {
        openDataEntities.add(entity);
    }

    @Override
    public void updateMatchTags() {
        OdEntity odEntity = getOpenDataEntity();
        OsmPrimitive osm = odEntity.getPrimitive();
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
        osm = getOsmEntity().getPrimitive();
    }

    @Override
    public Object getId() {
        return id;
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
        return (id.equals(((Match<?, ?>)obj).getId()));
    }
}
