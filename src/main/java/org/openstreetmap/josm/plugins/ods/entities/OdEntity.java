package org.openstreetmap.josm.plugins.ods.entities;

/**
 * An OdEntity is an Entity that has been derived from an external open data source.
 * There doesn't necessarily have to a 1 to 1 relation between an OdEntity and a feature
 * from the data source. Retrieved features can be combined or split to create 1 or more
 * OdEntities.
 *
 * @author Gertjan Idema
 *
 */
public interface OdEntity extends Entity, GeoObject {

    /**
     * Get the <code>DownloadResponse</code> of the feature from which this Entity has
     * created.
     *
     * @return The download response, or null if this is a derived entity.
     */
//    public DownloadResponse getDownloadResponse();

    /**
     * Check if the OSM primitive related to this entity can safely be imported to the OSM layer
     * if it doesn't exist there yet.
     * 
     * @return
     */
    boolean readyForImport();

    String getStatusTag();
}
