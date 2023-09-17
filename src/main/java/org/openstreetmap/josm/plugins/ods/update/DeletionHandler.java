package org.openstreetmap.josm.plugins.ods.update;

import java.util.List;

import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.plugins.ods.mapping.Mapping;

public interface DeletionHandler {
    public void handle(List<Mapping<?,?>> candidates);

    public List<Command> getCommands();
}
