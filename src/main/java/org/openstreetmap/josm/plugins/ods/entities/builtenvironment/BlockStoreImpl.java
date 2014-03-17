package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.index.quadtree.Quadtree;

public class BlockStoreImpl implements BlockStore {
    private Double tolerance = 1e-7;
    private Set<Block> blocks = new HashSet<>();
    private Set<Building> internalBuildings = new HashSet<>();
    private Set<Building> externalBuildings = new HashSet<>();
    private Quadtree index = new Quadtree();
     
     @Override
     public void addBuilding(Building building) {
         if (building.isInternal()) {
             if (!internalBuildings.add(building)) {
                 return;
             }
         }
         else {
             if (!externalBuildings.add(building)) {
                 return;
             }
         }
         Envelope envelope = building.getGeometry().getEnvelopeInternal();
         envelope.expandBy(tolerance);
         @SuppressWarnings("unchecked")
         List<Building> nearByBuildings = index.query(envelope);
         Set<Block> nearByBlocks = new HashSet<>();
         for (Building neighbour : nearByBuildings) {
              if (building.getGeometry().distance(neighbour.getGeometry()) < tolerance) {
//                  nearByBlocks.add(neighbour.getBlock());
                  if (building.isInternal() == neighbour.isInternal()) {
                      building.addNeighbour(neighbour);
                      neighbour.addNeighbour(building);
                  }
              }
         }
         if (nearByBlocks.isEmpty()) {
//             Block newBlock = new BlockImpl(this);
//             newBlock.add(building);
//             blocks.add(newBlock);
//             building.setBlock(newBlock);
         } 
         else {
             Iterator<Block> it = nearByBlocks.iterator();
             Block block = it.next();
             block.add(building);
//             building.setBlock(block);
             while (it.hasNext()) {
                 Block otherBlock = it.next();
                 block.merge(otherBlock);
                 block.getStore().remove(otherBlock);
             }
         }
         index.insert(envelope, building);
     }

    @Override
    public void remove(Block block) {
        blocks.remove(block);
    }
}
