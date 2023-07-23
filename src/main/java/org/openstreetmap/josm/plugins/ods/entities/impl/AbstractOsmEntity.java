package org.openstreetmap.josm.plugins.ods.entities.impl;

import static org.openstreetmap.josm.plugins.ods.entities.Entity.Completeness.Unknown;

import java.util.HashMap;
import java.util.Map;

import org.locationtech.jts.geom.Geometry;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;

public abstract class AbstractOsmEntity implements OsmEntity {
    private String sourceDate;
    private String source;
    private Geometry geometry;
    private Completeness completeness = Unknown;
    private Map<String, String> otherTags = new HashMap<>();
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
    public Map<String, String> getOtherTags() {
        return otherTags;
    }

    public void setOtherTags(Map<String, String> otherTags) {
        this.otherTags = otherTags;
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
    //    public Match<? extends Entity> getMatch() {
    //        return match;
    //    }
    //
    //    @Override
    //    public <E extends Entity> void setMatch(Match<E> match) {
    //        this.match = match;
    //    }
}
