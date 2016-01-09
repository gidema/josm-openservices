package org.openstreetmap.josm.plugins.ods.matching;

import java.util.ArrayList;
import java.util.List;

import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.ods.osm.update.NodeMatch;

public class WayMatch {
    public final Way osmWay;
    public final Way odWay;
    public final List<NodeMatch> matchedNodes;
    public WayMatch(Way odWay, Way osmWay) {
        super();
        this.osmWay = osmWay;
        this.odWay = odWay;
        this.matchedNodes = new ArrayList<>(odWay.getNodesCount());
    }
    
    
}
