package org.openstreetmap.josm.plugins.openservices.entities.imported;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.openservices.MappingException;
import org.openstreetmap.josm.plugins.openservices.entities.Entity;
import org.openstreetmap.josm.plugins.openservices.metadata.MetaData;
import org.openstreetmap.josm.plugins.openservices.metadata.MetaDataException;

/**
 * An EntityBuilder builds an Ods Entity object from a GeoTools feature
 *  
 * @author Gertjan Idema
 * 
 */
public interface ImportedEntityBuilder<T extends Entity> {

	/**
	 * Set the metaData context for this mapper
	 * 
	 * @param context
	 * @throws MetaDataException
	 */
	public void setContext(MetaData context) throws MetaDataException;

	/**
	 * Get the name of the feature this mapper can handle.
	 * 
	 * @return The name of the feature
	 */
	public String getFeatureName();

	
	/**
	 * Create an ODS Entity object from the provided feature.
	 * 
	 * @param feature
	 * @return
	 * @throws MappingException
	 */
	public T build(SimpleFeature feature) throws MappingException;
}
