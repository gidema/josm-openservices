package org.openstreetmap.josm.plugins.ods.matching.update;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.NodeData;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.PrimitiveData;
import org.openstreetmap.josm.data.osm.PrimitiveDeepCopy;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.RelationData;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.data.osm.WayData;
import org.openstreetmap.josm.plugins.ods.LayerManager;
import org.openstreetmap.josm.plugins.ods.Matcher;
import org.openstreetmap.josm.plugins.ods.ODS;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.actual.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmEntitiesBuilder;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmLayerManager;

/**
 * The importer imports objects from the OpenData layer to the Osm layer.
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class OdsImporter {
    private OdsModule module;
    // TODO Make the importfilter(s) configurable
    private ImportFilter importFilter = new DefaultImportFilter();
    
    public OdsImporter(OdsModule module) {
        super();
        this.module = module;
    }

    public void doImport(Collection<OsmPrimitive> primitives) {
        LayerManager layerManager = module.getOpenDataLayerManager();
        List<Entity> entitiesToImport = new LinkedList<>();
        for (OsmPrimitive primitive : primitives) {
            Entity entity = layerManager.getEntity(primitive);
            if (entity != null && entity.getMatch() == null 
                  && importFilter.test(entity)) {
                entitiesToImport.add(entity);
            }
        }
        importEntities(entitiesToImport);
    }

    private void importEntities(List<Entity> entitiesToImport) {
        List<OsmPrimitive> primitivesToImport = new LinkedList<>();
        for (Entity entity : entitiesToImport) {
            OsmPrimitive primitive = entity.getPrimitive();
            if (primitive != null) {
                primitivesToImport.add(primitive);
            }
        }
        PrimitiveDeepCopy deepCopy = new PrimitiveDeepCopy(primitivesToImport);
        importPrimitives(deepCopy);
        updateMatching();
    }
    
    private void updateMatching() {
        Matcher<?> matcher = module.getMatcherManager().getMatcher(Building.class);
        matcher.run();
        matcher = module.getMatcherManager().getMatcher(AddressNode.class);
        matcher.run();
    }

    private void importPrimitives(PrimitiveDeepCopy deepCopy) {
        OsmLayerManager osmLayerManager = module.getOsmLayerManager();
        DataSet dataSet = osmLayerManager.getOsmDataLayer().data;
        List<NodeData> nodeData = new LinkedList<>();
        List<WayData> wayData = new LinkedList<>();
        List<RelationData> relationData = new LinkedList<>();
        Collection<OsmPrimitive> importedPrimitives = new LinkedList<>();
        Map<Long, Long> newNodeIds = new HashMap<>();
        Map<Long, Long> newWayIds = new HashMap<>();
        Map<Long, Long> newRelationIds = new HashMap<>();
        for (PrimitiveData osmData : deepCopy.getAll()) {
            removeOdsTags(osmData);
            switch (osmData.getType()) {
            case NODE:
                nodeData.add((NodeData) osmData);
                break;
            case WAY:
                wayData.add((WayData) osmData);
                break;
            case RELATION:
                relationData.add((RelationData) osmData);
                break;
            default:
                break;
            }
        }
        for (NodeData data : nodeData) {
            Node node = new Node();
            node.load(data);
            dataSet.addPrimitive(node);
            newNodeIds.put(data.getUniqueId(), node.getUniqueId());
            importedPrimitives.add(node);
        }
        for (WayData data : wayData) {
            Way way = new Way();
            dataSet.addPrimitive(way);
            List<Long> nodeIds = new ArrayList<>(data.getNodesCount());
            for (Long nodeId : data.getNodes()) {
                nodeIds.add(newNodeIds.get(nodeId));
            }
            data.setNodes(nodeIds);
            way.load(data);
            newWayIds.put(data.getUniqueId(), way.getUniqueId());
            importedPrimitives.add(way);
        }
        for (RelationData data : relationData) {
            Relation relation = new Relation();
            dataSet.addPrimitive(relation);
            relation.load(data);
            importedPrimitives.add(relation);
        }
        buildImportedEntities(importedPrimitives);
    }

    /**
     * Remove the ODS tags from the osmData object
     * 
     * @param osmData
     */
    private void removeOdsTags(PrimitiveData osmData) {
        for (String key : osmData.keySet()) {
            if (key.startsWith(ODS.KEY.BASE)) {
                osmData.remove(key);
            }
        }
    }

    /**
     * Build entities for the newly imported primitives.
     * We could have created these entities from the OpenData entities instead. But by building them
     * from the Osm primitives, we make sure that all entities in the Osm layer are built the same way,
     * making them consistent with each other.
     * 
     * @param importedPrimitives
     */
    private void buildImportedEntities(
            Collection<OsmPrimitive> importedPrimitives) {
        OsmEntitiesBuilder entitiesBuilder = module.getOsmLayerManager().getEntitiesBuilder();
        entitiesBuilder.build(importedPrimitives);
    }
}
