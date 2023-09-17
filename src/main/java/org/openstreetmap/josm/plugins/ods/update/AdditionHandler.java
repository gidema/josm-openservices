package org.openstreetmap.josm.plugins.ods.update;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.ods.mapping.Mapping;

public interface AdditionHandler {
    public void handle(List<Mapping<?,?>> candidates);

    public Optional<Command> getCommand();

    public Collection<Way> getAddedWays();

    public Collection<Node> getAddedNodes();
}
