package org.openstreetmap.josm.plugins.ods.osm;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.command.ChangeNodesCommand;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.command.DeleteCommand;
import org.openstreetmap.josm.command.MoveCommand;
import org.openstreetmap.josm.command.SequenceCommand;
import org.openstreetmap.josm.data.UndoRedoHandler;
import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.gui.MainApplication;

public class NodeIterator {
    private final Way way;
    private final List<Node> nodes;
    private int index;
    private final boolean closed;
    private boolean reversed;
    private boolean modified = false;
    private final List<Command> movedNodes = new LinkedList<>();

    public NodeIterator(Way way, int startIndex, boolean reversed) {
        this.way = way;
        this.index = startIndex;
        this.reversed = reversed;
        this.nodes = new ArrayList<>(way.getNodesCount() + 5);
        this.nodes.addAll(way.getNodes());
        this.closed = way.isClosed();
    }

    public void reset() {
        index = (reversed ? nodes.size() - 1 : 0);
    }


    /**
     * Check if there is at least 1 node after the current node.
     *
     * @return true if there is at least one next node. false otherwise
     */
    public boolean hasNextNode() {
        return hasNextNodes(1);
    }

    /**
     * Check if there are at least n nodes after the current node.
     *
     * @return true if there are at least n next nodes. false otherwise
     */
    public boolean hasNextNodes(int n) {
        if (reversed) {
            return index > n - 1;
        }
        return index + n < nodes.size();
    }

    /**
     * Check if there is at least 1 node before the current node.
     *
     * @return true if there is at least one previous node. false otherwise
     */
    public boolean hasPreviousNode() {
        return hasPreviousNodes(1);
    }

    /**
     * Check if there are at least n nodes before the current node.
     *
     * @return true if there are at least n previous nodes. false otherwise
     */
    public boolean hasPreviousNodes(int n) {
        if (reversed) {
            return index + n < nodes.size();
        }
        return index > n - 1;
    }

    public Node next() {
        if(hasNextNode()) {
            index = (reversed ? index - 1 : index + 1);
            return nodes.get(index);
        }
        return null;
    }

    public Node previous() {
        if(hasPreviousNode()) {
            index = (reversed ? index + 1 : index - 1);
            return nodes.get(index);
        }
        return null;
    }

    public Node peek() {
        return nodes.get(index);
    }

    public Node peekNext() {
        if (hasNextNode()) {
            return (reversed ? nodes.get(index - 1) : nodes.get(index + 1));
        }
        return null;
    }

    public Node peekPrevious() {
        if (hasPreviousNode()) {
            return (reversed ? nodes.get(index + 1) : nodes.get(index - 1));
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
        int pos = (reversed ? index : index+1);
        nodes.add(pos, node);
        modified = true;
        if (!reversed) {
            next();
        }
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

    protected void setReversed(boolean reversed) {
        this.reversed = reversed;
        if (index == 0) {
            index = nodes.size() - 1;
        }
    }

    protected int getIndex() {
        return index;
    }

    public Integer nextIndex() {
        if (hasNextNode()) {
            return (reversed ? index - 1 : index + 1);
        }
        return null;
    }

    public Integer previousIndex() {
        if (hasPreviousNode()) {
            return (reversed ? index - 1 : index + 1);
        }
        return null;
    }

    protected Node getNode(int idx) {
        return nodes.get(idx);
    }

    public boolean dWithin(NodeDWithin dWithin, Node n) {
        return dWithin.check(peek(), n);
    }

    public boolean dSegmentWithin(NodeDWithin dWithin, Node n) {
        return dWithin.check(n, peek(), peekNext());
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
    public void close(boolean undoable) {
        if (!modified) return;
        List<Command> commands = new LinkedList<>();
        List<Node> oldNodes = way.getNodes();
        Command command = new ChangeNodesCommand(way, nodes);
        //        command.executeCommand();
        commands.add(command);
        if (!movedNodes.isEmpty()) {
            command = new SequenceCommand("Move nodes", movedNodes);
            //            command.executeCommand();
            commands.add(command);
        }
        List<Node> orphanNodes = new LinkedList<>();
        // Check for nodes that are not relevant anymore
        for (Node node : oldNodes) {
            if (node.getReferrers().isEmpty() && !node.hasKeys()) {
                orphanNodes.add(node);
            }
        }
        if (!orphanNodes.isEmpty()) {
            command = new DeleteCommand(orphanNodes);
            //            command.executeCommand();
            commands.add(command);
        }
        // If undoable, undo the commands in reverse order and the execute them as 1 SequenceCommand.
        if (!commands.isEmpty()) {
            if (undoable) {
                UndoRedoHandler.getInstance().add(new SequenceCommand("Align buildings", commands));
            }
            else {
                for (Command cmd : commands) {
                    cmd.executeCommand();
                }
            }
            if (MainApplication.getMap() != null) {
                MainApplication.getMap().mapView.repaint();
            }
        }
    }

    /*
     * Move the node at index to the given coordinates.
     */
    public void moveNode(int idx, LatLon coor) {
        Node node = nodes.get(idx);
        moveNode(node, coor);
    }

    public void moveNode(Node node, LatLon coor) {
        movedNodes.add(new MoveCommand(node, coor));
    }

    public void moveNode(int idx, EastNorth en) {
        Node node = nodes.get(idx);
        moveNode(node, en);
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
     * of the provide NodeIterator
     *
     * @param it
     * @return
     */
    public Double angle(NodeIterator it) {
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
