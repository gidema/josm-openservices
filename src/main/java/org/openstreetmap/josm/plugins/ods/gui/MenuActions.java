package org.openstreetmap.josm.plugins.ods.gui;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class MenuActions {
    private final List<OdsAction> actions = new LinkedList<>();

    public void forEach(Consumer<? super OdsAction> action) {
        actions.forEach(action);
    }
    public void addAction(OdsAction action) {
        actions.add(action);
    }

    
}
