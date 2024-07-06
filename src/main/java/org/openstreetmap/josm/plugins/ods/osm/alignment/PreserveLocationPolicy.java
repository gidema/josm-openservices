package org.openstreetmap.josm.plugins.ods.osm.alignment;

import org.openstreetmap.josm.data.osm.INode;

public interface PreserveLocationPolicy {
    public int toPreserve(INode node1, INode node2);
}
