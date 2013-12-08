package org.openstreetmap.josm.plugins.ods.entities.external;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.PrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.tags.FeatureMapper;

import com.vividsolutions.jts.geom.Geometry;

public class ExternalImportedEntity extends ExternalEntity {
    private SimpleFeature feature;
    private FeatureMapper featureMapper;
	
	public ExternalImportedEntity(SimpleFeature feature) {
        super();
        this.feature = feature;
    }

    public void setFeatureMapper(FeatureMapper featureMapper) {
		this.featureMapper = featureMapper;
	}

	public void build() {
	    //TODO implement    
	}
	
	
	@Override
    public Geometry getGeometry() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void createPrimitives(PrimitiveBuilder builder) {
        if (getPrimitives() == null) {
            setPrimitives(featureMapper.mapFeature(getFeature(), builder));
        }
    }
}
