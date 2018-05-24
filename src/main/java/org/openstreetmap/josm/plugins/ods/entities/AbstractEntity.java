package org.openstreetmap.josm.plugins.ods.entities;

import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;
import org.openstreetmap.josm.plugins.ods.matching.Match;

import com.vividsolutions.jts.geom.Geometry;

public abstract class AbstractEntity implements Entity {
    private Object primaryId;
    private Object referenceId;
    private DownloadResponse response;
    private String sourceDate;
    private String source;
    private Geometry geometry;
    private EntityStatus status = EntityStatus.UNKNOWN;
    private boolean incomplete = true;
    private Map<String, String> otherTags = new HashMap<>();
    private OsmPrimitive primitive;
    private Match<? extends Entity> match;

    @Override
    public void setPrimaryId(Object primaryId) {
        this.primaryId = primaryId;
    }

    @Override
    public Object getPrimaryId() {
        return primaryId;
    }

    @Override
    public Object getReferenceId() {
        return referenceId;
    }

    @Override
    public void setReferenceId(Object referenceId) {
        this.referenceId = referenceId;
    }

    @Override
    public void setDownloadResponse(DownloadResponse response) {
        this.response = response;
    }

    @Override
    public DownloadResponse getDownloadResponse() {
        return response;
    }

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
    public boolean isIncomplete() {
        return incomplete;
    }

    public void setIncomplete(boolean incomplete) {
        this.incomplete = incomplete;
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
    public void setStatus(EntityStatus status) {
        this.status = status;
    }

    @Override
    public EntityStatus getStatus() {
        return status;
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
    public Match<? extends Entity> getMatch() {
        return match;
    }

    @Override
    public <E extends Entity> void setMatch(Match<E> match) {
        this.match = match;
    }
}
