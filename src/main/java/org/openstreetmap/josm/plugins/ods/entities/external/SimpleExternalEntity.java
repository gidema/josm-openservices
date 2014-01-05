package org.openstreetmap.josm.plugins.ods.entities.external;

import java.util.Collection;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.PrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.entities.BuildException;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;
import org.openstreetmap.josm.plugins.ods.tags.FeatureMapper;

import com.vividsolutions.jts.geom.Geometry;

public class SimpleExternalEntity implements ExternalEntity {
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
    public void buildTags(OsmPrimitive primitive) {
        // TODO Auto-generated method stub
    }

    @Override
    public String getSource() {
        return null;
    }

    @Override
    public boolean isInternal() {
        return false;
    }

    @Override
    public boolean isIncomplete() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isDeleted() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Object getId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void init(MetaData metaData) throws BuildException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void createPrimitives(PrimitiveBuilder builder) {
        // TODO Auto-generated method stub
    }

    @Override
    public Collection<OsmPrimitive> getPrimitives() {
        // TODO Auto-generated method stub
        return null;
    }
}
