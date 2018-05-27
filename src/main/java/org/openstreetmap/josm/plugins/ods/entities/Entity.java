package org.openstreetmap.josm.plugins.ods.entities;

import java.util.Map;

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
    public void setSource(String source);
    public String getSource();
    public void setSourceDate(String sourceDate);
    public String getSourceDate();
    public void setCompleteness(Completeness completeness);
    public Completeness getCompleteness();
    public void setStatus(EntityStatus status);
    public EntityStatus getStatus();
    public void setPrimaryId(Object id);
    public Object getPrimaryId();
    public void setReferenceId(Object id);
    public Object getReferenceId();
    public Long getPrimitiveId();
    public Geometry getGeometry();
    public void setGeometry(Geometry geometry);

    public Match<? extends OsmEntity, ? extends OdEntity> getMatch();

    //    public <E1 extends OsmEntity, E2 extends OdEntity> void setMatch(Match<E1, E2> match);

    /**
     * Get the OSM primitive(s) from which this entity was constructed,
     * or that was/were constructed from this entity.
     * In most cases the list contains 1 item.
     *
     */
    public OsmPrimitive getPrimitive();

    /**
     * Get the tags that are not associated with any of the entity's properties.
     */
    public Map<String, String> getOtherTags();

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
