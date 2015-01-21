package org.openstreetmap.josm.plugins.ods.osm;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.command.ChangeCommand;
import org.openstreetmap.josm.command.ChangeNodesCommand;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.command.DeleteCommand;
import org.openstreetmap.josm.command.SequenceCommand;
import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Way;

public class NodeIterator {
    private Way way;
    private List<Node> nodes;
    private int index;
    private boolean closed;
    private boolean reversed;
    private boolean modified = false;
    private List<Command> movedNodes = new LinkedList<>();
    
    public NodeIterator(Way way, int startIndex, boolean reversed) {
        this.way = way;
        this.index = startIndex;
        this.reversed = reversed;
        this.nodes = new ArrayList<Node>(way.getNodesCount() + 5);
        this.nodes.addAll(way.getNodes());
        this.closed = nodes.get(0).equals(nodes.get(nodes.size() - 1));
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
    
//    public boolean hasNextNode() {
//        if (reversed) {
//            return index > 0;
//        }
//        return index + 1 < nodes.size();
//    }
//    
//    public boolean hasNextSegment() {
//        if (reversed) {
//            return index > 1;
//        }
//        return index + 2 < nodes.size();
//    }

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
    
//    public LineSegment getSegmentBefore() {
//        if (hasPrevious()) {
//            return new LineSegment(peekPrevious(), peek());
//        }
//        return null;
//    }
    
//    public LineSegment getSegmentAfter() {
//        if (hasNextNode()) {
//            return new LineSegment(peek(), peekNext());
//        }
//        return null;
//    }
    
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
    
    public boolean updateNode(int index, Node node) {
        if (index < 0 || index >= nodes.size()) return false;
        nodes.set(index, node);
        modified = true;
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

    protected Node getNode(int index) {
        return nodes.get(index);
    }

//    protected void setModified(boolean modified) {
//        this.modified = modified;
//    }

//    public List<Node> getNodes() {
//        return nodes;
//    }
    
    public double distanceToNode(EastNorth en) {
        return en.distance(peek().getEastNorth());
    }

    public double distanceToSegment(EastNorth en) {
        return Util.distancePointLine(en, peek().getEastNorth(), peekNext().getEastNorth());
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
    
    public void close(boolean undoable) {
        if (!modified) return;
        List<Command> commands = new LinkedList<>(); 
        List<Node> oldNodes = way.getNodes();
        Command command = new ChangeNodesCommand(way, nodes);
        command.executeCommand();
        commands.add(command);
        if (!movedNodes.isEmpty()) {
            command = new SequenceCommand("Move nodes", movedNodes);
            command.executeCommand();
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
            command.executeCommand();
            commands.add(command);
        }
        // If undoable, undo the commands in reverse order and the execute them as 1 SequenceCommand.
        if (undoable) {
            if (undoable && commands != null) {
                for (int i = commands.size() -1; i>=0; i--) {
                    commands.get(i).undoCommand();
                }
                Main.main.undoRedo.add(new SequenceCommand("Align buildings", commands));
                
            }
        }
        if (commands != null && Main.map != null) {
            Main.map.mapView.repaint();
        }
    }

    public void moveNode(int index, LatLon coor) {
        Node node = nodes.get(index);
        moveNode(node, coor);
    }
    
    public void moveNode(Node node, LatLon coor) {
        Node newNode = new Node(node);
        newNode.setCoor(coor);
        movedNodes.add(new ChangeCommand(node, newNode));
    }
    
    public void moveNode(int index, EastNorth en) {
        Node node = nodes.get(index);
        moveNode(node, en);
    }
    
    public void moveNode(Node node, EastNorth en) {
        Node newNode = new Node(node);
        newNode.setEastNorth(en);
        movedNodes.add(new ChangeCommand(node, newNode));
    }
    
    private void mergeAdjacentNodes(int index1, int index2, boolean toMiddle) {
        EastNorth middle = middle(nodes.get(index1).getEastNorth(), nodes.get(index2).getEastNorth());
        nodes.remove(index1);
        int index = (index2 > index1 ? index2 - 1 : index2);
        if (closed && index1 == 0) {
            nodes.set(nodes.size() -1, nodes.get(0));
        }
        else if (closed && index1 == nodes.size()) {
            nodes.set(0, nodes.get(nodes.size() -1));
        }
        if (toMiddle) {
            moveNode(nodes.get(index), middle);
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
