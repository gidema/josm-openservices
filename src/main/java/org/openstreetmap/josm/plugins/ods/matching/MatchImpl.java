package org.openstreetmap.josm.plugins.ods.matching;

import org.openstreetmap.josm.plugins.ods.entities.OdEntity;
import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;
import org.openstreetmap.josm.plugins.ods.entities.impl.ZeroOneMany;

public abstract class MatchImpl<T1 extends OsmEntity, T2 extends OdEntity> implements Match<T1, T2> {
    private Object id;
    private final ZeroOneMany<T1> osmEntities;
    private final ZeroOneMany<T2> openDataEntities;

    public MatchImpl(ZeroOneMany<T1> osmEntities, T2 openDataEntity) {
        this(osmEntities, new ZeroOneMany<>(openDataEntity));
    }

    public MatchImpl(T1 osmEntity, ZeroOneMany<T2> odEntities) {
        this(new ZeroOneMany<>(osmEntity), odEntities);
    }

    public MatchImpl(T1 osmEntity, T2 openDataEntity) {
        this(new ZeroOneMany<>(osmEntity), new ZeroOneMany<>(openDataEntity));
    }

    public MatchImpl(ZeroOneMany<T1> osmEntities,
            ZeroOneMany<T2> openDataEntities) {
        super();
        this.osmEntities = osmEntities;
        this.openDataEntities = openDataEntities;
    }


    @Override
    public boolean isSimple() {
        return osmEntities.isOne() && openDataEntities.isOne();
    }

    // TODO Deprecate
    @Override
    public boolean isSingleSided() {
        return osmEntities.isEmpty() || openDataEntities.isEmpty();
    }

    @Override
    public ZeroOneMany<T1> getOsmEntities() {
        return osmEntities;
    }

    @Override
    public ZeroOneMany<T2> getOpenDataEntities() {
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

    //    @Override
    //    public void updateMatchTags() {
    //        //        OsmPrimitive osm = getOpenDataEntity().getPrimitive();
    //        //        if (osm != null) {
    //        //            osm.put(ODS.KEY.BASE, "true");
    //        //            osm.put(ODS.KEY.GEOMETRY_MATCH, getGeometryMatch().toString());
    //        //            osm.put(ODS.KEY.STATUS, getOpenDataEntity().getStatus().toString());
    //        //            osm.put(ODS.KEY.STATUS_MATCH, getStatusMatch().toString());
    //        //            osm.put(ODS.KEY.TAG_MATCH, getAttributeMatch().toString());
    //        //            if (getOpenDataEntity().getStatus() == EntityStatus.REMOVAL_DUE) {
    //        //                osm.put(ODS.KEY.STATUS, EntityStatus.REMOVAL_DUE.toString());
    //        //            }
    //        //        }
    //    }
    //
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
        if (!(obj instanceof Match)) {
            return false;
        }
        return (id.equals(((Match<?, ?>)obj).getId()));
    }
}
