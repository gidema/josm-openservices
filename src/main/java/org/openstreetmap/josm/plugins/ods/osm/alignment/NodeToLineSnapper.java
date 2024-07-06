package org.openstreetmap.josm.plugins.ods.osm.alignment;

import java.util.List;
import java.util.stream.Collectors;

import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.data.osm.BBox;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.ods.osm.BufferOps;

/**
 * The NodeToLineSnapper snaps nodes from 1 way to another nearby way within a given tolerance.
 *
 * A lineSegment that is within 'tolerance'
 * distance from a node on the other way, will be split by adding that
 * node in between the start and end point of that segment.
 * 
 * @author gertjan
 *
 */
public class NodeToLineSnapper {
    private final Way way;
    private final DataSet dataSet;
    private final BufferOps dWithin;
    private Command command;

    public NodeToLineSnapper(Way way1, BufferOps dWithin) {
        this.way = way1;
        this.dataSet = way1.getDataSet();
        this.dWithin = dWithin;
    }
    
    public void run() {
        var it = new NodeIterator2(way);
        while (it.hasNextNode()) {
            Node n1 = it.peek();
            Node n2 = it.peekNext();
            // Create a bounding box around the segment
            BBox bbox = dWithin.getBBox(n1, n2);
            List<Node> candidates = dataSet.searchNodes(bbox)
                .stream()
                .filter(n -> dWithin.check(n, n1, n2))
                .collect(Collectors.toList());
            switch (candidates.size()) {
            case 0: break;
            case 1: it.insertNodeAfter(candidates.get(0));
                  break;
            default: break;
                //TODO Handle addition of multiple nodes to one segment
            }
            it.next();
        }
        this.command = it.close();
    }
    
    public Command getCommand() {
        return command;
    }
}
