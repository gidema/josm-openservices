package org.openstreetmap.josm.plugins.ods.osm.alignment;

import java.util.Collection;
import java.util.List;

import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.data.osm.Way;

/**
 * A Multiway aligner aligns a collection of ways by snapping nearby nodes to other nodes or ways.
 * The result of the alignment process is a CommandSequence that has yet to be executed.
 * 
 * It is currently assumed that all ways in the set are allowed to be interconnected. Newer versions of
 * this interface may add restriction. For example to prevent inner- and outer rings of a polygon to be interconnected.
 * 
 * The alignment process can be tuned by policies.
 * The PreserveNodePolicy determines which node's history should be preserved when snapping nodes.
 * The PreserveLocationPolicy determines which location should be preserved when snapping nodes.
 * 
 * @author gertjan
 *
 */
public interface MultiWayAligner {
    public List<Command> align(Collection<Way> ways);
}
