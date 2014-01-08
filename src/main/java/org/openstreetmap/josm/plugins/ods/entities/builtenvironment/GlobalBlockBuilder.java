package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;

import java.util.Iterator;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.OdsWorkingSet;
import org.openstreetmap.josm.plugins.ods.analysis.GlobalAnalyzer;

/**
 * The GlobalBlockBuilder builds Global blocks by relating internal
 * and external blocks to each other.
 * 
 * @author gertjan
 *
 */
public class GlobalBlockBuilder implements GlobalAnalyzer {
    private Double tolerance;
    BlockStore blockStore = BlockStore.instance;

    public GlobalBlockBuilder(Double tolerance) {
        super();
        this.tolerance = tolerance;
    }

    @Override
    public void analyze(OdsWorkingSet workingSet) {
        addInternalBlocks(workingSet);
        addExternalBlocks(workingSet);
    }
    
    private void addInternalBlocks(OdsWorkingSet workingSet) {
        BuiltEnvironment be = new BuiltEnvironment(
        workingSet.getInternalDataLayer().getEntitySet());
        Iterator<Block> it = be.getBlocks().iterator();
        while (it.hasNext()) {
            Block block = it.next();
            GlobalBlock gb = new GlobalBlockImpl();
            gb.add(block);
            blockStore.add(gb);
        }
    }
    
    private void addExternalBlocks(OdsWorkingSet workingSet) {
        BuiltEnvironment be = new BuiltEnvironment(
        workingSet.getInternalDataLayer().getEntitySet());
        Iterator<Block> it = be.getBlocks().iterator();
        while (it.hasNext()) {
            Block block = it.next();
            List<GlobalBlock> globalBlocks = blockStore.query(block.getGeometry(), tolerance);
            if (globalBlocks.size() == 0) {
                GlobalBlock gb = new GlobalBlockImpl();
                gb.add(block);
                blockStore.add(gb);
                break;
            }
            else {
                Iterator<GlobalBlock> it2 = globalBlocks.iterator();
                GlobalBlock gb = it2.next();
                gb.add(block);
                while (it2.hasNext()) {
                    GlobalBlock gb2 = it2.next();
                    gb.merge(gb2);
                    blockStore.remove(gb2);
                }
            }
        }
    }
}
