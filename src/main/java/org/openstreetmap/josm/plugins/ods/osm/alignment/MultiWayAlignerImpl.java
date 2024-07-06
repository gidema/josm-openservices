package org.openstreetmap.josm.plugins.ods.osm.alignment;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmLayerManager;
import org.openstreetmap.josm.plugins.ods.osm.BufferOps;
import org.openstreetmap.josm.plugins.ods.osm.GCBufferOps;

public class MultiWayAlignerImpl implements MultiWayAligner {
    private final BufferOps bufferOps;
    private final AdjacentWayFinder adjacentWayFinder;

    public MultiWayAlignerImpl(OdsContext context, double BufferSize) {
        OsmLayerManager osmLayerManager = context.getComponent(OsmLayerManager.class);
        this.bufferOps = new GCBufferOps(BufferSize);
        this.adjacentWayFinder = new AdjacentWayFinder(osmLayerManager.getOsmDataLayer().data, bufferOps);
    }

    @Override
    public List<Command> align(Collection<Way> ways) {
        var neighbourCandidates = adjacentWayFinder.find(ways);
        List<Command> commands = alignWays(ways);
        commands.addAll(alignWays(neighbourCandidates));
        return commands;
    }

    private List<Command> alignWays(Collection<Way> ways) {
        List<Command> commands = new LinkedList<>();
        ways.forEach(way -> {
            NodeToLineSnapper snapper = new NodeToLineSnapper(way, bufferOps);
            snapper.run();
            Command command = snapper.getCommand();
            if (command != null) commands.add(command); 
        });
        return commands;
    }
}
