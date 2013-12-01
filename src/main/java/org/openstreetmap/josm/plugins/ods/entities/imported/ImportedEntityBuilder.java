package org.openstreetmap.josm.plugins.ods.entities.imported;

import java.lang.reflect.Constructor;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.ODS;
import org.openstreetmap.josm.plugins.ods.entities.BuildException;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.issue.DefaultIssue;
import org.openstreetmap.josm.plugins.ods.issue.Issue;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;
import org.openstreetmap.josm.plugins.ods.metadata.MetaDataException;
import org.openstreetmap.josm.plugins.ods.tags.FeatureMapper;
import org.openstreetmap.josm.tools.I18n;

/**
 * An EntityBuilder builds an Ods Entity object from a GeoTools feature
 *  
 * @author Gertjan Idema
 * 
 */
public class ImportedEntityBuilder {
    private MetaData context;
    private String entityClass;
    private Constructor<? extends ImportedEntity> constructor;
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

    public void setEntityClass(String entityClass) {
        this.entityClass = entityClass;
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
	    ImportedEntity entity;
        try {
            entity = getConstructor().newInstance();
            entity.setFeature(feature);
            entity.init(context);
            entity.build();
            return entity;

        } catch (Exception e) {
            e.printStackTrace();
            Issue issue = new DefaultIssue(I18n.tr(
                "An error occurred while trying to create a entity of type {0}", entityClass));
            throw new BuildException(issue);
        }
	}

    private Constructor<? extends ImportedEntity> getConstructor() throws Exception {
        if (constructor == null) {
            @SuppressWarnings("unchecked")
            Class<? extends ImportedEntity> clazz = (Class<? extends ImportedEntity>) 
                ODS.getClassLoader().loadClass(entityClass);
            constructor = clazz.getConstructor();
        }
        return constructor;
    }
}
