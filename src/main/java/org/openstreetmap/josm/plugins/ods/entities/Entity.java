package org.openstreetmap.josm.plugins.ods.entities;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.matching.Match;

import com.vividsolutions.jts.geom.Geometry;

/**
 * ODS entities represent entities like buildings, address nodes,
 * or streets. They are the interface between imported features and
 * Josm primitives.
 * Using these entities gives the possibility to build object relations
 * from geometric relations.
 *
 * @author gertjan
 *
 */
public interface Entity {
    /**
     * The primary id is an unique id determined by the data source.
     * It is unique per dataSource, but not necessarily unique by entity type if
     * the same entity type can be retrieved from more than 1 dataSource.
     * The primary id is used to prevent duplicate objects.
     */
    public Object getPrimaryId();

    public void setSource(String source);
    public String getSource();
    public void setSourceDate(String sourceDate);
    public String getSourceDate();
    public void setCompleteness(Completeness completeness);
    public Completeness getCompleteness();
    public void setStatus(EntityStatus status);
    public EntityStatus getStatus();
    //    public void setReferenceId(Object id);
    //    public Object getReferenceId();
    public Long getPrimitiveId();
    public Geometry getGeometry();
    public void setGeometry(Geometry geometry);

    public Match<? extends OdEntity, ? extends OsmEntity> getMatch();

    /**
     * Get the OSM primitive from which this entity was constructed,
     * or that was constructed from this entity.
     *
     */
    public OsmPrimitive getPrimitive();

    public void setPrimitive(OsmPrimitive primitive);

    /**
     * Enum to define the completeness of an entity.
     * An entity is complete if the full bounding box of the entity has been down-loaded.
     *
     * @author Gertjan Idema
     *
     */
    public enum Completeness {
        Unknown,
        Complete,
        Incomplete
    }
}
