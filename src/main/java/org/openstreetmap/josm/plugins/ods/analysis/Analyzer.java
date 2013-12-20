package org.openstreetmap.josm.plugins.ods.analysis;

import org.openstreetmap.josm.plugins.ods.DataLayer;
import org.openstreetmap.josm.plugins.ods.entities.EntitySet;

public interface Analyzer {
    public void analyze(DataLayer dataLayer, EntitySet newEntities);
}
