package org.openstreetmap.josm.plugins.openservices.entities.imported;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.openservices.entities.BuildException;
import org.openstreetmap.josm.plugins.openservices.entities.Entity;
import org.openstreetmap.josm.plugins.openservices.issue.DefaultIssue;
import org.openstreetmap.josm.plugins.openservices.issue.Issue;
import org.openstreetmap.josm.plugins.openservices.metadata.MetaData;
import org.openstreetmap.josm.plugins.openservices.metadata.MetaDataException;
import org.openstreetmap.josm.plugins.openservices.tags.FeatureMapper;
import org.openstreetmap.josm.tools.I18n;

/**
 * An EntityBuilder builds an Ods Entity object from a GeoTools feature
 *  
 * @author Gertjan Idema
 * 
 */
public class ImportedEntityBuilder {
    private MetaData context;
    private Class<? extends ImportedEntity> entityClass;
    protected FeatureMapper featureMapper;
    
	/**
	 * Set the metaData context for this mapper
	 * 
	 * @param context
	 * @throws MetaDataException
	 */
	public void setContext(MetaData context) throws MetaDataException {
	    this.context = context;
	}

	@SuppressWarnings("unchecked")
    public void setEntityClass(Class<? extends Entity> entityClass) {
        this.entityClass = (Class<? extends ImportedEntity>) entityClass;
    }

    public void setFeatureMapper(FeatureMapper featureMapper) {
        this.featureMapper = featureMapper;
    }

    /**
	 * Create an ODS Entity object from the provided feature.
	 * 
	 * @param feature
	 * @return
	 * @throws BuildException 
	 */
	public Entity build(SimpleFeature feature) throws BuildException {
	    ImportedEntity entity = null;
        try {
            entity = entityClass.newInstance();
        } catch (InstantiationException e) {
            Issue issue = new DefaultIssue(I18n.tr(
               "Unable to create a {0} object", entityClass.getName()), e);
           throw new BuildException(issue);
        } catch(IllegalAccessException e) {
            Issue issue = new DefaultIssue(I18n.tr(
                    "Not allowed to create a {0} object", entityClass.getName()), e);
                throw new BuildException(issue);
        }
	    entity.setFeature(feature);
	    entity.init(context);
	    entity.build();
	    return entity;
	}
}
