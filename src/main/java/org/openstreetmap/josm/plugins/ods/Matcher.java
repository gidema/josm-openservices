package org.openstreetmap.josm.plugins.ods;

import org.openstreetmap.josm.plugins.ods.entities.Entity;

public interface Matcher<T extends Entity> {

    void run();

}
