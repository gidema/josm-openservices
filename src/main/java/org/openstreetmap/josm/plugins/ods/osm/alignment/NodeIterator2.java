package org.openstreetmap.josm.plugins.ods.osm.alignment;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.command.ChangeNodesCommand;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.command.MoveCommand;
import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Way;

public class NodeIterator2 {
    private final Way way;
    private final List<Node> nodes;
    private int index;
    private final boolean closed;
    private boolean modified = false;
    private final List<Command> movedNodes = new LinkedList<>();

    public NodeIterator2(Way way) {
        this.way = way;
        this.index = 1;
        this.nodes = new ArrayList<>(way.getNodesCount() + 5);
        this.nodes.addAll(way.getNodes());
        this.closed = way.isClosed();
    }


    /**
     * Check if there is at least 1 node after the current node.
     *
     * @return true if there is at least one next node. false otherwise
     */
    public boolean hasNextNode() {
        return index  < nodes.size() - 1;
    }

    public Node next() {
        if(hasNextNode()) {
            index = index + 1;
            return nodes.get(index);
        }
        return null;
    }

    public Node peek() {
        return nodes.get(index);
    }

    public Node peekNext() {
        if (hasNextNode()) {
            return nodes.get(index + 1);
        }
        return null;
    }

    /**
     * Insert the given node to list of nodes after the current index;
     *
     * @param node The node to insert
     * After the node has been inserted, the index points to the new inserted node.
     * @return True on success; False when trying to add a node to the end of a closed line;
     */
    public boolean insertNodeAfter(Node node) {
        if (!hasNextNode() && closed) return false;
        nodes.add(index + 1, node);
        modified = true;
        next();
        return true;
    }

    /*
     * Replace the node at index with the provided node.
     * If the way is closed and the index is at the first or last node
     * then replace the node at the other end as well
     */
    public boolean updateNode(int idx, Node node) {
        if (idx < 0 || idx >= nodes.size()) return false;
        nodes.set(idx, node);
        modified = true;
        if (closed) {
            if (idx == 0) {
                nodes.set(nodes.size() - 1, node);
            }
            else if (idx == nodes.size() - 1) {
                nodes.set(0,  node);
            }
        }
        return true;
    }

    public boolean isModified() {
        return modified;
    }

    protected int getIndex() {
        return index;
    }

    public Integer nextIndex() {
        if (hasNextNode()) {
            return index + 1;
        }
        return null;
    }

    protected Node getNode(int idx) {
        return nodes.get(idx);
    }

    /**
     * Collapse the segment at the current index.
     * The start and end nodes of the segments will be replaced with
     * 1 node in the middle of the segment.
     * TODO handle tags
     *
     */
    public void collapseSegment() {
        if (hasNextNode()) {
            if (peek().getReferrers().size() > peekNext().getReferrers().size()) {
                mergeAdjacentNodes(nextIndex(), getIndex(), false);
            }
            else if (peekNext().getReferrers().size() > peek().getReferrers().size()) {
                mergeAdjacentNodes(getIndex(), nextIndex(), false);
            }
            else {
                mergeAdjacentNodes(nextIndex(), getIndex(), true);
            }
            modified = true;
        }
        // TODO implement else
    }

    private static EastNorth middle(EastNorth en1, EastNorth en2) {
        double east = (en1.east() + en2.east())/2;
        double north = (en1.north() + en2.north())/2;
        return new EastNorth(east, north);
    }

    /*
     * Close the iterator and perform the necessary updates.
     */
    public Command close() {
        if (!modified) return null;
        return new ChangeNodesCommand(way, nodes);
    }

    public void moveNode(Node node, EastNorth en) {
        movedNodes.add(new MoveCommand(node, node.getEastNorth(), en));
    }

    private void mergeAdjacentNodes(int index1, int index2, boolean toMiddle) {
        EastNorth middle = middle(nodes.get(index1).getEastNorth(), nodes.get(index2).getEastNorth());
        nodes.remove(index1);
        int idx = (index2 > index1 ? index2 - 1 : index2);
        if (closed && index1 == 0) {
            nodes.set(nodes.size() -1, nodes.get(0));
        }
        else if (closed && index1 == nodes.size()) {
            nodes.set(0, nodes.get(nodes.size() -1));
        }
        if (toMiddle) {
            moveNode(nodes.get(idx), middle);
        }
    }

    /**
     * Calculate the angle between the current segment and the current segment
     * of the provide NodeIterator2
     *
     * @param it
     * @return
     */
    public Double angle(NodeIterator2 it) {
        return angle() - it.angle();
    }
    /**
     * Calculate the angle of the current segment to the x-axis
     *
     * @return
     */
    public Double angle() {
        Double x1 = this.peek().getEastNorth().east();
        Double y1 = this.peek().getEastNorth().north();
        Double x2 = this.peekNext().getEastNorth().east();
        Double y2 = this.peekNext().getEastNorth().north();
        return Math.atan2(y1 - y2, x1 - x2);
    }
}
