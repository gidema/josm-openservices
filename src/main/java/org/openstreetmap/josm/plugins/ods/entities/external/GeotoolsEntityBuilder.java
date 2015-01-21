package org.openstreetmap.josm.plugins.ods.entities.external;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.Context;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;

/**
 * A GeotoolsEntityBuilder creates an Entity of type <T> from a SimpleFeature object.
 * The normal behaviour for implementations of this class is to have an EntityStore<T> field that is set in 
 * the constructor. The new Entity get built only if the EntityStore doesn't already contain an Entity with
 * the same EntityReferenceId.
 *   
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 * @param <T>
 */
public interface GeotoolsEntityBuilder<T extends Entity>  {
    
    /**
     * Get the referenceId for T from the given feature
     * @param feature
     * @return
     */
    public Object getReferenceId(SimpleFeature feature);
    
    /**
     * Create the entity and store somewhere.
     * Typically this is in and EntityStore<T>
     * 
     * @param feature
     */
    public void buildGtEntity(SimpleFeature feature);
    
    /**
     * Set the metaData.
     * TODO There is a lot of overlap between the Context and the MetaData concepts. Maybe we should combine
     * these concepts.
     * 
     * @param metaData
     */
    public void setMetaData(MetaData metaData);
    
    
    /**
     * Set the context.
     * Currently there is only 1 relevant context property: EntitySource. Then entity Source get added to each Entity
     * and can be used to check if the Entity was downloaded in the current download  session.
     * @param ctx
     */
    public void setContext(Context ctx);
}
