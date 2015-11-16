package org.openstreetmap.josm.plugins.ods.matching;

import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.ODS;
import org.openstreetmap.josm.plugins.ods.entities.Entity;

public abstract class MatchImpl<E extends Entity> implements Match<E> {
    private List<E> osmEntities = new LinkedList<>();
    private List<E> openDataEntities = new LinkedList<>();
    
    public MatchImpl(E osmEntity, E openDataEntity) {
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
            osm.put(ODS.KEY.STATUS_MATCH, getStatusMatch().toString());
            osm.put(ODS.KEY.ATTRIBUTE_MATCH, getAttributeMatch().toString());
        }
    }
}
