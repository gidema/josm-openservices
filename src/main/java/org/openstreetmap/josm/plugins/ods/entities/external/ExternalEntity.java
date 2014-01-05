package org.openstreetmap.josm.plugins.ods.entities.external;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.PrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.entities.BuildException;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;


public interface ExternalEntity extends Entity {
    
    public void init(MetaData metaData) throws BuildException;
    
    public void createPrimitives(PrimitiveBuilder builder);
    
    public void buildTags(OsmPrimitive primitive);

}
