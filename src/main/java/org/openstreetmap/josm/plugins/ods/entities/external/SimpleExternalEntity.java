package org.openstreetmap.josm.plugins.ods.entities.external;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.tags.FeatureMapper;

import com.vividsolutions.jts.geom.Geometry;

public class SimpleExternalEntity extends ExternalEntity {
    private SimpleFeature feature;
    private FeatureMapper featureMapper;
	
	public SimpleExternalEntity(SimpleFeature feature) {
        super();
        this.feature = feature;
    }

    @Override
    public Class<? extends Entity> getType() {
        return SimpleExternalEntity.class;
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
    protected void buildTags(OsmPrimitive primitive) {
        // TODO Auto-generated method stub
    }
}
