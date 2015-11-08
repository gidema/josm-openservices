package org.openstreetmap.josm.plugins.ods.matching;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.NodeData;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.data.osm.WayData;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OpenDataLayerManager;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmLayerManager;

public class OdsImporter {
    private OpenDataLayerManager openDataLayerManager;
    private OsmLayerManager osmLayerManager;
    private OsmDataLayer osmLayer;
    private OsmDataLayer openDataLayer;
    private Map<Long, ImportData<Node>> nodes;
    private Map<Long, ImportData<Way>> ways;
    private Map<Long, ImportData<Relation>> relations;
    
    public OdsImporter(OdsModule module) {
        super();
        this.openDataLayerManager = module.getOpenDataLayerManager();
        this.osmLayerManager = module.getOsmLayerManager();
    }

    public void doImport() {
        this.osmLayer = osmLayerManager.getOsmDataLayer();
        this.openDataLayer = openDataLayerManager.getOsmDataLayer();
        collectImportData();
        importNodes();
        importWays();
    }
    
    /**
     * Fill the nodes, ways and relations objects with the data to import.
     */
    private void collectImportData() {
        // Select all nodes from the selection that have a related entity,
        // but have no match in the osmLayer
        nodes = new HashMap<>();
        for (Node node : openDataLayer.data.getSelectedNodes()) {
            Entity entity = openDataLayerManager.getEntity(node);
            if (entity != null && entity.getMatch() == null) {
                ImportData<Node> importData = new ImportData<>();
                importData.openDataPrimitive = (Node) node;
                importData.openDataEntity = entity;
                importData.osmPrimitive = new Node();
                nodes.put(node.getUniqueId(), importData);
            }
        }
        ways = new HashMap<>();
        for (Way way : openDataLayer.data.getSelectedWays()) {
            Entity entity = openDataLayerManager.getEntity(way);
            if (entity != null && entity.getMatch() == null) {
                ImportData<Way> wayImportData = new ImportData<>();
                wayImportData.openDataPrimitive = way;
                wayImportData.openDataEntity = entity;
                wayImportData.osmPrimitive = new Way();
                ways.put(way.getUniqueId(), wayImportData);
            }
            // Add the way's nodes
            for (Node node : way.getNodes()) {
                if (nodes.get(node.getUniqueId()) == null) {
                    ImportData<Node> nodeImportData = new ImportData<>();
                    nodeImportData.openDataPrimitive = node;
                    nodeImportData.osmPrimitive = new Node();
                    nodes.put(node.getUniqueId(), nodeImportData);
                }
            }
        }
        relations = new HashMap<>();
        for (Relation relation : openDataLayer.data.getSelectedRelations()) {
            Entity entity = openDataLayerManager.getEntity(relation);
            if (entity != null && entity.getMatch() == null) {
                ImportData<Relation> relationImportData = new ImportData<>();
                relationImportData.openDataPrimitive = relation;
                relationImportData.openDataEntity = entity;
                relationImportData.osmPrimitive = new Relation();
                relations.put(relation.getUniqueId(), relationImportData);
            }
            // Add the relation's membersnodes
            for (OsmPrimitive osm : relation.getMemberPrimitives()) {
                switch (osm.getType()) {
                case NODE:
                    Node node = (Node) osm;
                    if (nodes.get(node.getUniqueId()) == null) {
                        ImportData<Node> nodeImportData = new ImportData<>();
                        nodeImportData.openDataPrimitive = node;
                        nodeImportData.osmPrimitive = new Node();
                        nodes.put(node.getUniqueId(), nodeImportData);
                    }
                    break;
                case WAY:
                    Way way = (Way) osm;
                    if (ways.get(way.getUniqueId()) == null) {
                        ImportData<Way> wayImportData = new ImportData<>();
                        wayImportData.openDataPrimitive = way;
                        wayImportData.osmPrimitive = new Way();
                        ways.put(way.getUniqueId(), wayImportData);
                    }
                    break;
                case RELATION:
                    Relation rel = (Relation) osm;
                    if (relations.get(rel.getUniqueId()) == null) {
                        ImportData<Relation> relImportData = new ImportData<>();
                        relImportData.openDataPrimitive = rel;
                        relImportData.osmPrimitive = new Relation();
                        relations.put(rel.getUniqueId(), relImportData);
                    }
                    break;
                default:
                    break;
                }
            }
        }
    }
    
    private void importNodes() {
        DataSet dataSet = osmLayer.data;
        for (ImportData<Node> importData : nodes.values()) {
            NodeData nodeData = importData.openDataPrimitive.save();
            Node newNode = importData.osmPrimitive;
            newNode.load(nodeData);
            dataSet.addPrimitive(newNode);
        }
    }

    private void importWays() {
        DataSet dataSet = osmLayer.data;
        for (ImportData<Way> importData : ways.values()) {
            WayData wayData = importData.openDataPrimitive.save();
            List<Long> newNodeIds = new ArrayList<>(wayData.getNodesCount());
            for (Long nodeId : wayData.getNodes()) {
                Long newNodeId = (nodes.get(nodeId)).osmPrimitive.getUniqueId();
                newNodeIds.add(newNodeId);
            }
            Way newWay = importData.osmPrimitive;
            wayData.setNodes(newNodeIds);
            newWay.load(wayData);
            dataSet.addPrimitive(newWay);
        }
    }

    static class ImportData<T extends OsmPrimitive> {
        Entity osmEntity;
        Entity openDataEntity;
        T openDataPrimitive;
        T osmPrimitive;
    }
}
