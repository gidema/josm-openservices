package org.openstreetmap.josm.plugins.ods.osm.update;

import org.openstreetmap.josm.data.osm.Node;

public class NodeMatch {
    private final Node odNode;
    private final Node osmNode;
//    private final Envelope envelope;
    private final boolean hasTags;
    private boolean matched = false;
    private final SpecialReferrers referrers;
    
    public NodeMatch(Node odNode, Node osmNode, SpecialReferrers referrers) {
        super();
        this.odNode = odNode;
        this.osmNode = osmNode;
        this.referrers = referrers;
//        this.envelope = envelope;
        this.hasTags = osmNode.getInterestingTags().size() > 0;
    }


    public Node getNode() {
        return osmNode;
    }
    
//    public Envelope getEnvelope() {
//        return envelope;
//    }

    public boolean hasTags() {
        return hasTags;
    }
    
    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }

    public double distanceTo(Node node) {
        return this.osmNode.getCoor().distance(node.getCoor());
    }
    
    @Override
    public int hashCode() {
        return osmNode.hashCode();
    }

    public boolean equals(NodeMatch other) {
        if (other == this) return true;
        return other.getNode().equals(getNode());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof NodeMatch)) return false;
        return equals((NodeMatch)obj);
    }
    
    public static enum SpecialReferrers {
        NONE,
        BUILDING,
        OTHER,
        BOTH
    }
}
