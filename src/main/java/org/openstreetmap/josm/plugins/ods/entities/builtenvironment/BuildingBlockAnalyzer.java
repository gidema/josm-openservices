package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.openstreetmap.josm.plugins.ods.DataLayer;
import org.openstreetmap.josm.plugins.ods.analysis.Analyzer;
import org.openstreetmap.josm.plugins.ods.entities.EntitySet;

import com.vividsolutions.jts.geom.Geometry;


/**
 * This analyzer verifies if there are overlapping buildings in
 * the downloaded data.
 * 
 * TODO consider running over all buildings, not just the new ones.
 * 
 * @author gertjan
 *
 */
public class BuildingBlockAnalyzer implements Analyzer {
    private final Double tolerance;
    
    public BuildingBlockAnalyzer(Double tolerance) {
        super();
        this.tolerance = tolerance;
    }

    public void analyze(DataLayer dataLayer, EntitySet newEntities) {
        BuiltEnvironment environment = new BuiltEnvironment(dataLayer.getEntitySet());
        BuiltEnvironment newEnvironment = new BuiltEnvironment(newEntities);
        Iterator<Building> buildings1 = newEnvironment.getBuildings().iterator();
        while (buildings1.hasNext()) {
            Building building1 = buildings1.next();
            findTouchingBlocks(building1, environment);
        }
    }
    
    private boolean findTouchingBlocks(Building building, BuiltEnvironment environment) {
        Iterator<Block> blocks = environment.getBlocks().iterator();
        Set<Block> touchingBlocks = new HashSet<>();
        while (blocks.hasNext()) {
            Block block = blocks.next(); 
            if (touches(building, block)) {
                touchingBlocks.add(block);
            }
        }
        if (touchingBlocks.isEmpty()) {
            Block block = new BlockImpl();
            block.add(building);
            building.setBlock(block);
            environment.getBlocks().add(block);
            return false;
        }
        Iterator<Block> it = touchingBlocks.iterator();
        Block block = it.next();
        block.add(building);
        building.setBlock(block);
        while (it.hasNext()) {
            Block otherBlock = it.next();
            block.merge(otherBlock);
            environment.getBlocks().remove(otherBlock);
        }
        return true;
    }
    
    private boolean touches(Building building1, Block block) {
        boolean touches = false;
        for (Building building2 : block.getBuildings()) {
            Geometry geom1 = building1.getGeometry();
            Geometry geom2 = building2.getGeometry();
            if (geom1.isWithinDistance(geom2, tolerance)) {
                touches = true;
                building1.addNeighbour(building2);
                building2.addNeighbour(building1);
            }
        }
        return touches;
    }

}
