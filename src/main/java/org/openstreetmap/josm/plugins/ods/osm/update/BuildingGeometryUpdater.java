package org.openstreetmap.josm.plugins.ods.osm.update;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.openstreetmap.josm.command.AddCommand;
import org.openstreetmap.josm.command.ChangeNodesCommand;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.command.DeleteCommand;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.matching.Match;
import org.openstreetmap.josm.plugins.ods.osm.BuildingAligner;

public class BuildingGeometryUpdater {
    private final BuildingAligner buildingAligner;
    private final OsmDataLayer osmDataLayer;
    
    public BuildingGeometryUpdater(OdsModule module) {
        super();
        this.osmDataLayer = module.getOsmLayerManager().getOsmDataLayer();
        this.buildingAligner = new BuildingAligner(module, 
            module.getOsmLayerManager().getEntityStore(Building.class));
    }

    public void updateGeometries(List<Match<Building>> matches) {
        // Update the geometries for the selected buildings
        for (Match<Building> match : matches) {
            updateGeometry(match.getOsmEntity(), match.getOpenDataEntity());
        }
        // Realign the updated buildings to the neighbour buildings
        for (Match<Building> match : matches) {
            buildingAligner.align(match.getOsmEntity());
        }
    }
    
    private void updateGeometry(Building osmBuilding, Building odBuilding) {
        OsmPrimitive osmPrimitive = osmBuilding.getPrimitive();
        OsmPrimitive odPrimitive = odBuilding.getPrimitive();
        // Only update osm ways to start with
        if (osmPrimitive.getDisplayType() != OsmPrimitiveType.CLOSEDWAY ||
                odPrimitive.getDisplayType() != OsmPrimitiveType.CLOSEDWAY) {
            return;
        }
        Way osmWay = (Way) osmPrimitive;
        Way odWay = (Way) odPrimitive;
        DataSet dataSet = osmDataLayer.data;
        List<Node> osmNodes = osmWay.getNodes();
        List<Node> odNodes = odWay.getNodes();
        ListIterator<Node> it = odNodes.listIterator();
        List<Node> nodesToRemove = new LinkedList<>();
        while (it.hasNext()) {
            Node odNode = it.next();
            Node newNode = (Node) dataSet.getPrimitiveById(odNode);
            if (newNode == null) {
                // TODO Try to re-use old nodes
                newNode = new Node(odNode);
                Command addCommand = new AddCommand(dataSet, newNode);
                addCommand.executeCommand();
            }
            it.set(newNode);
        }
        Command cmd = new ChangeNodesCommand(osmWay, odNodes);
        cmd.executeCommand();
        for (Node node: osmNodes) {
            if (node.getReferrers().size() == 0) {
                nodesToRemove.add(node);
            }
        }
        if (nodesToRemove.size() > 0) {
            cmd = new DeleteCommand(nodesToRemove);
            cmd.executeCommand();
        }
    }
}
