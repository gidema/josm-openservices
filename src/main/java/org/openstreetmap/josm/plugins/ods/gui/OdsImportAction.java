package org.openstreetmap.josm.plugins.ods.gui;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openstreetmap.josm.Main;
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
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.LayerManager;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmLayerManager;
import org.openstreetmap.josm.tools.ImageProvider;

public class OdsImportAction extends OdsAction {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public OdsImportAction(OdsModule module) {
        super(module, "Import", ImageProvider.get("download"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Layer layer = Main.map.mapView.getActiveLayer();
        LayerManager layerManager = getModule().getLayerManager(layer);
        // This action should only occur when the OpenData layer is active
        assert (layerManager != null && !layerManager.isOsm());
        
        OsmDataLayer osmLayer = (OsmDataLayer) layer;
        List<Entity> entitiesToImport = new LinkedList<>();
        for (OsmPrimitive primitive : osmLayer.data.getAllSelected()) {
            Entity entity = layerManager.getEntity(primitive);
            if (entity != null && entity.getMatch() == null) {
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
    }
    
    private void importPrimitives(PrimitiveDeepCopy deepCopy) {
        OsmLayerManager osmLayerManager = getModule().getOsmLayerManager();
        DataSet dataSet = getModule().getOsmLayerManager().getOsmDataLayer().data;
        List<NodeData> nodeData = new LinkedList<>();
        List<WayData> wayData = new LinkedList<>();
        List<RelationData> relationData = new LinkedList<>();
        Map<Long, Long> newNodeIds = new HashMap<>();
        Map<Long, Long> newWayIds = new HashMap<>();
        Map<Long, Long> newRelationIds = new HashMap<>();
        for (PrimitiveData osmData : deepCopy.getAll()) {
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
        }
        for (RelationData data : relationData) {
            Relation relation = new Relation();
            dataSet.addPrimitive(relation);
            relation.load(data);
        }
    }

    @Override
    public void activeLayerChange(Layer oldLayer, Layer newLayer) {
        LayerManager layerManager = getModule().getLayerManager(newLayer);
        this.setEnabled(layerManager != null && !layerManager.isOsm());
    }
}
