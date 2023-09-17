package org.openstreetmap.josm.plugins.ods.entities;

import org.openstreetmap.josm.plugins.ods.mapping.Mapping;
import org.openstreetmap.josm.plugins.ods.mapping.MatchStatus;
import org.openstreetmap.josm.plugins.ods.mapping.UpdateStatus;
import org.openstreetmap.josm.plugins.ods.update.UpdateTaskType;

/**
 * An OdEntity is an Entity that has been derived from an external open data source.
 * There doesn't necessarily have to a 1 to 1 relation between an OdEntity and a feature
 * from the data source. Retrieved features can be combined or split to create 1 or more
 * OdEntities.
 *
 * @author Gertjan Idema
 *
 */
public interface OdEntity extends GeoEntity {

    /**
     * Check if the OSM primitive related to this entity can safely be imported to the OSM layer
     * if it doesn't exist there yet.
     * 
     * @return
     */
    boolean readyForImport();
    
    public Completeness getCompleteness();

    public void setCompleteness(Completeness completeness);
    
    public String getStatusTag();

    public void setMapping(Mapping<? extends OsmEntity, ? extends OdEntity> abstractMapping);
    
    /**
     * Check if this entity has been mapped to any entity in the osm dataset.
     *  
     * @param includeDeletions Report the entity as mapped, even if all osm entities have been deleted
     *  
     * @return
     */
    public boolean isMapped(boolean includeDeletions);

    public MatchStatus getGeometryMatch();

    public MatchStatus getAttributeMatch();

    public MatchStatus getStatusMatch();
    
//    public MappingStatus getMappingStatus();
    
    public UpdateStatus getUpdateStatus();
    
    public boolean isUpdated();
    
    public void setAttributeMatch(MatchStatus attributeMatch);

    public void setStatusMatch(MatchStatus statusMatch);

    public void setGeometryMatch(MatchStatus geometryMatch);
    
//    public void setMappingStatus(MappingStatus mapppingStatus);

    public void setUpdateStatus(UpdateStatus mapppingStatus);

    public void refreshUpdateTags();

    public UpdateTaskType getUpdateTaskType();
    
//    public void setUpdated(boolean b);
}
