package org.openstreetmap.josm.plugins.ods.entities.imported;

import org.openstreetmap.josm.plugins.ods.PrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.tags.FeatureMapper;

public class SimpleImportedEntity extends ImportedEntity {
    private FeatureMapper featureMapper;
	
	public void setFeatureMapper(FeatureMapper featureMapper) {
		this.featureMapper = featureMapper;
	}

	public void build() {
	    //TODO implement    
	}
	
	@Override
    public void createPrimitives(PrimitiveBuilder builder) {
        if (getPrimitives() == null) {
            setPrimitives(featureMapper.mapFeature(getFeature(), builder));
        }
    }
}
