package org.openstreetmap.josm.plugins.ods.entities.external;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.PrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.entities.BuildException;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;

import com.vividsolutions.jts.geom.Geometry;

public class SimpleExternalEntity implements ExternalEntity {
    private SimpleFeature feature;
    private Map<String, String> otherTags = new HashMap<>();
//    private EntityType entityType;
	
	public SimpleExternalEntity(SimpleFeature feature, String entityType2) {
        super();
        this.feature = feature;
    }

    @SuppressWarnings("unchecked")
    @Override
    public String getId() {
        return feature.getID();
    }

    @Override
    public boolean hasReferenceId() {
        return false;
    }

    @Override
    public Object getReferenceId() {
        return null;
    }

    @Override
    public Class<? extends Entity> getType() {
        return SimpleExternalEntity.class;
    }

//	@Override
//    public EntityType getEntityType() {
//        return entityType;
//    }

    public void build() {
	    //TODO implement    
	}
	
	
	@Override
    public boolean hasGeometry() {
        return true;
    }

    @Override
    public void setGeometry(Geometry geometry) {
        // TODO Auto-generated method stub
    }

    @Override
    public Geometry getGeometry() {
        return (Geometry) feature.getDefaultGeometry();
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
    public boolean hasName() {
        return false;
    }

    @Override
    public String getName() {
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

    @Override
    public Map<String, String> getOtherTags() {
        return otherTags;
    }
}
