package org.openstreetmap.josm.plugins.openservices.entities.josm;

import java.util.Iterator;

import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.openservices.JosmDataLayer;
import org.openstreetmap.josm.plugins.openservices.entities.EntitySet;

public class BuiltEnvironmentEntityBuilder implements JosmEntityBuilder {
    EntitySet entitySet;
    DataSet dataset;

    public BuiltEnvironmentEntityBuilder(JosmDataLayer dataLayer) {
        entitySet = dataLayer.getEntitySet();
        dataset = dataLayer.data;
    }

    @Override
    public void build() {
        buildRelationBuildings();
        buildWayBuildings();
        buildNodeBuildings();
    }

    private void buildNodeBuildings() {
        // TODO Auto-generated method stub
        
    }

    private void buildWayBuildings() {
        Iterator<Way> i = dataset.getWays().iterator();
        while (i.hasNext()) {
            Way way = i.next();
            if (way.hasKey("building")) {
                createBuilding(way);
            }
        }
    }

    private void createBuilding(Way way) {
        // TODO Auto-generated method stub
        
    }

    private void buildRelationBuildings() {
        // TODO Auto-generated method stub
        
    }
    
}
