package org.openstreetmap.josm.plugins.ods.entities.osm;

import java.util.ArrayList;
import java.util.Arrays;

public class OsmEntityBuilders extends ArrayList<OsmEntityBuilder<?>> {

    private static final long serialVersionUID = 1L;

    public OsmEntityBuilders(OsmEntityBuilder<?> ... entityBuilders ) {
        super(Arrays.asList(entityBuilders));
    }

}
