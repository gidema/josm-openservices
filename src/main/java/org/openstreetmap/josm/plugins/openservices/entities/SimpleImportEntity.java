package org.openstreetmap.josm.plugins.openservices.entities;

import org.openstreetmap.josm.plugins.openservices.PrimitiveBuilder;
import org.openstreetmap.josm.plugins.openservices.entities.imprt.ImportEntity;
import org.openstreetmap.josm.plugins.openservices.tags.FeatureMapper;

public class SimpleImportEntity extends ImportEntity {
    private FeatureMapper featureMapper;
	
	public void setFeatureMapper(FeatureMapper featureMapper) {
		this.featureMapper = featureMapper;
	}

	
	@Override
    public void createPrimitives(PrimitiveBuilder builder) {
        if (getPrimitives() == null) {
            setPrimitives(featureMapper.mapFeature(getFeature(), builder));
        }
    }
}
