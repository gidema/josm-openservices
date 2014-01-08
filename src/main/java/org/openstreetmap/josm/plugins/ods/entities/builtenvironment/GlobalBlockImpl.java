package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;

import java.util.HashSet;
import java.util.Set;

import com.vividsolutions.jts.geom.Geometry;

public class GlobalBlockImpl implements GlobalBlock {
    private Geometry geometry;
    private Set<Block> internalBlocks = new HashSet<>();
    private Set<Block> externalBlocks = new HashSet<>();
    
    @Override
    public boolean intersects(Block block, Double tolerance) {
        return geometry.distance(block.getGeometry()) < tolerance;
    }

    @Override
    public void add(Block block) {
        if (block.isInternal()) {
            internalBlocks.add(block);
        } 
        else {
            externalBlocks.add(block);            
        }
        if (geometry == null) {
            geometry = block.getGeometry();
        }
        else {
            geometry = geometry.union(block.getGeometry());
        }
    }
    
    /**
     * Merge the data from the other block into this block.
     * 
     * @see org.openstreetmap.josm.plugins.ods.entities.builtenvironment.GlobalBlock#merge(org.openstreetmap.josm.plugins.ods.entities.builtenvironment.GlobalBlock)
     */
    @Override
    public void merge(GlobalBlock other) {
        for (Block block : other.getInternalBlocks()) {
            internalBlocks.add(block);
        }
        for (Block block : other.getExternalBlocks()) {
            externalBlocks.add(block);
        }
        geometry = geometry.union(other.getGeometry());
    }

    @Override
    public Set<Block> getInternalBlocks() {
        return internalBlocks;
    }

    @Override
    public Set<Block> getExternalBlocks() {
        return externalBlocks;
    }

    @Override
    public Geometry getGeometry() {
        return geometry;
    }
}
