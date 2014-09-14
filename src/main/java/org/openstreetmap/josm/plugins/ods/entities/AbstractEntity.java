package org.openstreetmap.josm.plugins.ods.entities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openstreetmap.josm.data.osm.OsmPrimitive;

import com.vividsolutions.jts.geom.Geometry;

public abstract class AbstractEntity implements Entity {
    private Object referenceId;
    private String sourceDate;
    private String source;
    private Geometry geometry;
    private Boolean incomplete;
    private Map<String, String> otherTags = new HashMap<>();
    private List<OsmPrimitive> primitives;
    private Long primitiveId;

    public Object getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Object referenceId) {
        this.referenceId = referenceId;
    }

    public void setSourceDate(String string) {
        this.sourceDate = string;
    }

    public String getSourceDate() {
        return sourceDate;
    }

    public void setSource(String source) {
        this.source = source;
    }
    
    @Override
    public String getSource() {
        return source;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }
    
    public Geometry getGeometry() {
        return geometry;
    }
    
    public Boolean getIncomplete() {
        return incomplete;
    }

    public void setIncomplete(Boolean incomplete) {
        this.incomplete = incomplete;
    }

    @Override
    public Map<String, String> getOtherTags() {
        return otherTags;
    }

    public void setOtherTags(Map<String, String> otherTags) {
        this.otherTags = otherTags;
    }

    public void setPrimitives(List<OsmPrimitive> primitives) {
        this.primitives = primitives;
        if (primitives.isEmpty()) return;
        primitiveId = Long.MAX_VALUE ;
        for (OsmPrimitive primitive : primitives) {
            primitiveId = Math.min(primitiveId, primitive.getId());
        }
    }

    @Override
    public boolean isIncomplete() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isDeleted() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Long getPrimitiveId() {
        return primitiveId;
    }

    @Override
    public List<OsmPrimitive> getPrimitives() {
        return primitives;
    }
}
