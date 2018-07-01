package org.openstreetmap.josm.plugins.ods.entities;

import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;

/**
 * An OdEntity is an Entity that has been derived from an external open data source.
 * There doesn't necessarily have to a 1 to 1 relation between an OdEntity and a feature
 * from the data source. Retrieved features can be combined or split to create 1 or more
 * OdEntities.
 *
 * @author Gertjan Idema
 *
 */
public interface OdEntity extends Entity {

    /**
     * Get the <code>DownloadResponse</code> of the feature from which this Entity has
     * created.
     *
     * @return The download response, or null if this is a derived entity.
     */
    public DownloadResponse getDownloadResponse();

    /**
     * Set the <code>downloadResponse</code> of the feature from which this Entity has
     * been created.
     *
     * @param response
     */
    public void setDownloadResponse(DownloadResponse response);

    /**
     * Set the primary id for this object.
     *
     * @param primaryId
     */
    public void setPrimaryId(Object primaryId);
}
