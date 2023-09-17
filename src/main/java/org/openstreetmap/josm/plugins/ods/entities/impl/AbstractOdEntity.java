package org.openstreetmap.josm.plugins.ods.entities.impl;

import static java.util.function.Predicate.not;
import static org.openstreetmap.josm.plugins.ods.entities.Entity.Completeness.Unknown;
import static org.openstreetmap.josm.plugins.ods.mapping.UpdateStatus.*;

import org.locationtech.jts.geom.Geometry;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.ODS;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.OdEntity;
import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;
import org.openstreetmap.josm.plugins.ods.mapping.Mapping;
import org.openstreetmap.josm.plugins.ods.mapping.MatchStatus;
import org.openstreetmap.josm.plugins.ods.mapping.UpdateStatus;

public abstract class AbstractOdEntity implements OdEntity {
    private String sourceDate;
    private String source;
    private Geometry geometry;
    private Entity.Completeness completeness = Unknown;
    private OsmPrimitive primitive;
    private Mapping<? extends OsmEntity, ? extends OdEntity> mapping;
    private MatchStatus attributeMatch = MatchStatus.NULL;
    private MatchStatus statusMatch = MatchStatus.NULL;
    private MatchStatus geometryMatch = MatchStatus.NULL;
//    private MappingStatus mappingStatus = MappingStatus.NULL;
    private UpdateStatus updateStatus = UpdateStatus.Unknown;
//    private boolean updated = false;

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
    public void setAttributeMatch(MatchStatus attributeMatch) {
        this.attributeMatch = attributeMatch;
    }

    @Override
    public void setStatusMatch(MatchStatus statusMatch) {
        this.statusMatch = statusMatch;
    }

    @Override
    public void setGeometryMatch(MatchStatus geometryMatch) {
        this.geometryMatch = geometryMatch;
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

    @Override
    public Long getPrimitiveId() {
        return (primitive == null ? null : primitive.getUniqueId());
    }

    @Override
    public OsmPrimitive getPrimitive() {
        return primitive;
    }

    @Override
    public boolean isMapped(boolean includeDeletions) {
        if (getMapping() == null || getMapping().getOsmEntities().isEmpty()) return false;
        if (includeDeletions) return true;
        return getMapping().getOsmEntities().stream().anyMatch(not(osmEntity -> osmEntity.getPrimitive().isDeleted()));
    }

//    @Override
//    public MappingStatus getMappingStatus() {
//        return mappingStatus;
//    }

    @Override
    public UpdateStatus getUpdateStatus() {
        return updateStatus;
    }

    @Override
    public void setUpdateStatus(UpdateStatus updateStatus) {
        this.updateStatus = updateStatus;
    }

    @Override
    public boolean isUpdated() {
        return updateStatus == AdditionUpdated || updateStatus == DeletionUpdated;
    }

//    @Override
//    public void setMappingStatus(MappingStatus mappingStatus) {
//        this.mappingStatus = mappingStatus;
//    }
//
//    @Override
//    public void setUpdated(boolean updated) {
//        this.updated = updated;
//    }

    @Override
    public void refreshUpdateTags() {
        OsmPrimitive osm = getPrimitive();
        if (osm != null) {
            osm.put(ODS.KEY.BASE, getUpdateStatus().toString());
//            osm.put(ODS.KEY.GEOMETRY_MATCH, getGeometryMatch().toString());
            osm.put(ODS.KEY.GEOMETRY_MATCH, getGeometryMatch().toString());
            osm.put(ODS.KEY.STATUS_MATCH, getStatusMatch().toString());
            osm.put(ODS.KEY.TAG_MATCH, getAttributeMatch().toString());
//            osm.put(ODS.KEY.UPDATED, updated ? "true" : null);
//            osm.put(ODS.KEY.IDMATCH, Boolean.valueOf(isMapped(false)).toString());
        }
     }

    @Override
    public MatchStatus getAttributeMatch() {
        return attributeMatch;
    }

    @Override
    public MatchStatus getStatusMatch() {
        return statusMatch;
    }

    @Override
    public MatchStatus getGeometryMatch() {
        return geometryMatch;
    }

    @Override
    public Mapping<? extends OsmEntity, ? extends OdEntity> getMapping() {
        return mapping;
    }

    @Override
    public void setMapping(Mapping<? extends OsmEntity, ? extends OdEntity> mapping) {
        this.mapping = mapping;
    }
}
