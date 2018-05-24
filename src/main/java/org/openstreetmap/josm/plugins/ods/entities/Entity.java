package org.openstreetmap.josm.plugins.ods.entities;

import java.util.Map;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;
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
    public void setDownloadResponse(DownloadResponse response);
    public DownloadResponse getDownloadResponse();
    public void setSource(String source);
    public String getSource();
    public void setSourceDate(String sourceDate);
    public String getSourceDate();
    boolean isIncomplete();
    public void setStatus(EntityStatus status);
    public EntityStatus getStatus();
    public void setPrimaryId(Object id);
    public Object getPrimaryId();
    public void setReferenceId(Object id);
    public Object getReferenceId();
    public Long getPrimitiveId();
    public Geometry getGeometry();
    public void setGeometry(Geometry geometry);

    public Match<? extends Entity> getMatch();

    public <E extends Entity> void setMatch(Match<E> match);
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
}
