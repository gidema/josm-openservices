package org.openstreetmap.josm.plugins.ods.osm.update;

import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.josm.data.osm.Node;

public class NodeMatches {
    private Map<Node, NodeMatch> matches = new HashMap<>();
    
    public NodeMatch getMatch(Node node) {
        return matches.get(node);
    }
}
