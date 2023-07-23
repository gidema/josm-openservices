package org.openstreetmap.josm.plugins.ods.entities.impl;

import static org.openstreetmap.josm.plugins.ods.entities.Entity.Completeness.Unknown;

import org.locationtech.jts.geom.Geometry;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.OdEntity;

public abstract class AbstractOdEntity implements OdEntity {
    private String sourceDate;
    private String source;
    private Geometry geometry;
    private Entity.Completeness completeness = Unknown;
    private OsmPrimitive primitive;

    @Override
    public void setSourceDate(String string) {
        this.sourceDate = string;
    }

    @Override
    public String getSourceDate() {
        return sourceDate;
    }

    @Override
    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    @Override
    public Geometry getGeometry() {
        return geometry;
    }

    @Override
    public Completeness getCompleteness() {
        return completeness;
    }

    @Override
    public void setCompleteness(Completeness completeness) {
        this.completeness = completeness;
    }

    @Override
    public void setPrimitive(OsmPrimitive primitive) {
        this.primitive = primitive;
    }

//    @Override
//    public void setStatus(EntityStatus status) {
//        this.status = status;
//    }
//
//    @Override
//    public EntityStatus getStatus() {
//        return status;
//    }

    @Override
    public Long getPrimitiveId() {
        return (primitive == null ? null : primitive.getUniqueId());
    }

    @Override
    public OsmPrimitive getPrimitive() {
        return primitive;
    }

    //    @Override
    //    public Match<?, ?> getMatch() {
    //        return match;
    //    }
    //
    //    @Override
    //    public <E1 extends OsmEntity, E2 extends OdEntity> void setMatch(
    //            Match<E1, E2> match) {
    //        this.match = match;
    //    }
}
