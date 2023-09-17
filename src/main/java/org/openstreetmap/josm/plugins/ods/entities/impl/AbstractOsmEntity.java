package org.openstreetmap.josm.plugins.ods.entities.impl;

import java.util.HashMap;
import java.util.Map;

import org.locationtech.jts.geom.Geometry;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.entities.OdEntity;
import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;
import org.openstreetmap.josm.plugins.ods.mapping.Mapping;

public abstract class AbstractOsmEntity implements OsmEntity {
    private String sourceDate;
    private String source;
    private Geometry geometry;
    private Map<String, String> otherTags = new HashMap<>();
    private OsmPrimitive primitive;
    private Mapping<? extends OsmEntity, ? extends OdEntity> mapping;

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

    @Override
    public Long getPrimitiveId() {
        return (primitive == null ? null : primitive.getUniqueId());
    }

    @Override
    public OsmPrimitive getPrimitive() {
        return primitive;
    }
    
    @Override
    public void setMapping(Mapping<? extends OsmEntity, ? extends OdEntity> mapping) {
        this.mapping = mapping;
    }

    @Override
    public Mapping<? extends OsmEntity, ? extends OdEntity> getMapping() {
        return mapping;
    }

    @Override
    public boolean isMapped() {
        return mapping != null;
    }    
}
