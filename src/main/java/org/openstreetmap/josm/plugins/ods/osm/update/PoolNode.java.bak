package org.openstreetmap.josm.plugins.ods.osm.update;

import org.openstreetmap.josm.data.osm.Node;

import com.vividsolutions.jts.geom.Envelope;

public class PoolNode {
    private final Node node;
    private final Envelope envelope;
    private final boolean hasTags;
    private boolean matched = false;
    private final SpecialReferrers referrers;
    
    public PoolNode(Node node, Envelope envelope, SpecialReferrers referrers) {
        super();
        this.node = node;
        this.referrers = referrers;
        this.envelope = envelope;
        this.hasTags = node.getInterestingTags().size() > 0;
    }


    public Node getNode() {
        return node;
    }
    
    public Envelope getEnvelope() {
        return envelope;
    }

    public boolean hasTags() {
        return hasTags;
    }
    
    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }

    public double distanceTo(Node node1) {
        return this.node.getCoor().distance(node1.getCoor());
    }
    
    @Override
    public int hashCode() {
        return node.hashCode();
    }

    public boolean equals(PoolNode other) {
        if (other == this) return true;
        return other.getNode().equals(getNode());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof PoolNode)) return false;
        return equals((PoolNode)obj);
    }
    
    public static enum SpecialReferrers {
        NONE,
        BUILDING,
        OTHER,
        BOTH
    }
}
