package org.openstreetmap.josm.plugins.ods.entities;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.mapping.Mapping;

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

    public Long getPrimitiveId();

    public Mapping<? extends OsmEntity, ? extends OdEntity> getMapping();

    //    public <E1 extends OsmEntity, E2 extends OdEntity> void setMatch(Mapping<E1, E2> match);

    /**
     * Get the OSM primitive(s) from which this entity was constructed,
     * or that was/were constructed from this entity.
     * In most cases the list contains 1 item.
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
