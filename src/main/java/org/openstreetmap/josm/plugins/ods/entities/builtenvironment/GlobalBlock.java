package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;

import java.util.Set;

import com.vividsolutions.jts.geom.Geometry;

/**
 * A global block groups internal and external blocks, that overlap each other.
 * By doing so, we can analyze and replace relatively small groups of related
 * buildings and addresses in a relatively robust way.
 *     
 * @author gertjan
 *
 */
public interface GlobalBlock {
    public void add(Block block);
    public boolean intersects(Block block, Double tolerance);
    public Geometry getGeometry();
    public void merge(GlobalBlock gb2);
    public Set<Block> getInternalBlocks();
    public Set<Block> getExternalBlocks();
}
