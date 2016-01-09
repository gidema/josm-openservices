package org.openstreetmap.josm.plugins.ods.entities.opendata;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.EntityBuilder;

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
public interface GeotoolsEntityBuilder<T extends Entity> extends EntityBuilder<SimpleFeature, T>{
    
    /**
     * Get the referenceId for T from the given feature
     * @param feature
     * @return
     */
    public Object getReferenceId(SimpleFeature feature);
}
