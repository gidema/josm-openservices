package org.openstreetmap.josm.plugins.ods.builtenvironment;

import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.ods.entities.internal.InternalDataLayer;
import org.openstreetmap.josm.plugins.ods.tasks.Task;


/**
 * This task simplifies the building outline to remove unnecessary nodes.
 * In ODS 0.4 this was done in the layer with down loaded data by simplifying
 * the geometry of the entity. That was no big problem at the time because
 * whole blocks of buildings were imported.
 * In ODS 0.6 we add the functionality to update a single building or a small
 * group of buildings. Simplifying the building outline too early could cause
 * the removal of nodes at the connection to other buildings. Therefore, we 
 * will need to run the simplification process in the OSM layer after the
 * building has been updated and aligned with it's neighbors.
 * Line segments shared between 2 (or more) building are simplified during the
 * alignment process. Segments shared between a building and some other object
 * won't be simplified.
 * This class simplifies segments that are not shared with any other object.
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class BuildingSimplifier implements Task {
    private Double tolerance;
    private InternalDataLayer dataLayer;
    
    public BuildingSimplifier(InternalDataLayer dataLayer, Double tolerance) {
        super();
        this.dataLayer = dataLayer;
        this.tolerance = tolerance;
    }

    public void run() {
        DataSet data = dataLayer.getOsmDataLayer().data;
        // TODO check if this includes new primitives
        for (OsmPrimitive primitive : data.allModifiedPrimitives()) {
            if (primitive.hasKey("building")) {
                simplify(primitive);
            }
        }
    }
    
    private void simplify(OsmPrimitive primitive) {
        switch (primitive.getType()) {
        case WAY:
            simplify((Way)primitive);
            break;
        case RELATION:
            for (OsmPrimitive member : ((Relation)primitive).getMemberPrimitives()) {
                simplify(member);
            }
            break;
        default:
            break;
        }
    }

    private void simplify(Way way) {
        EdgeBuilder edgeBuilder = new EdgeBuilder(way);
        Edge edge = edgeBuilder.nextUnconnectedEdge();
        while (edge != null) {
            simplifyEdge(edge);
            edge = edgeBuilder.nextUnconnectedEdge();
        }
    }
    
    private void simplifyEdge(Edge edge) {
        // TODO Implement the simplifier 
    }

    class EdgeBuilder {
        private Way way;
        private int index;
        
        public EdgeBuilder(Way way) {
            this.way = way;
        }
        
        private Edge nextUnconnectedEdge() {
            int i = index + 1;
            // Skip all connected nodes
            while (i < way.getNodesCount() && way.getNode(i).getReferrers().size() > 1) {
                i++;
            }
            if (i == way.getNodesCount()) return null;
            index = i - 1;
            while (i < way.getNodesCount() && way.getNode(i).getReferrers().size() == 1) {
                i++;
            }
            Edge edge = new Edge(way, index, i);
            index = i;
            return edge;
        }
    }
        
    /**
     * Edge describes a line (sequence of nodes) that is a part of a way. For closed ways, an edge will never cross
     * from the last node of the way to the first or vice-versa. 
     *  
     */
    static class Edge {
        private Way way;
        private int startIndex;
        private int endIndex;

        public Edge(Way way, int startIndex, int endIndex) {
            super();
            this.way = way;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }

        public Way getWay() {
            return way;
        }

        public int getStartIndex() {
            return startIndex;
        }

        public int getEndIndex() {
            return endIndex;
        }
    }   
}
