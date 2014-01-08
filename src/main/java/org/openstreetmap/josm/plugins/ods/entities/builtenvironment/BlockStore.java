package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.index.quadtree.Quadtree;

public class BlockStore {
    private List<GlobalBlock> blocks = new LinkedList<>();
    private Quadtree index = new Quadtree();
    
    public final static BlockStore instance = new BlockStore();
    
    private BlockStore() {
        // Hide constructor
    }
    
    /**
     * Add a new global block to the list
     * 
     * @param globalBlock
     */
    public void add(GlobalBlock globalBlock) {
        blocks.add(globalBlock);
        index.insert(globalBlock.getGeometry().getEnvelopeInternal(), globalBlock);
    }
    
    public void remove(GlobalBlock globalBlock) {
        blocks.remove(globalBlock);
    }
    
    public List<GlobalBlock> query(Geometry geometry, Double tolerance) {
        Envelope envelope = geometry.getEnvelopeInternal();
        if (tolerance != null && tolerance > 0) {
            envelope.expandBy(tolerance);
        }
        List<GlobalBlock> blocks = query(envelope);
        Iterator<GlobalBlock> it = blocks.iterator();
        while (it.hasNext()) {
            GlobalBlock block = it.next();
            if (geometry.distance(block.getGeometry()) > tolerance) {
                it.remove();
            }
        }
        return blocks;
    }

    public List<GlobalBlock> query(Envelope envelope) {
        @SuppressWarnings("unchecked")
        List<GlobalBlock> result = index.query(envelope);
        return result;
    }
}
