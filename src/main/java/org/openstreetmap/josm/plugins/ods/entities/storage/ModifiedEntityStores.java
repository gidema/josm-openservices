package org.openstreetmap.josm.plugins.ods.entities.storage;

import java.util.ArrayList;
import java.util.Arrays;

public class ModifiedEntityStores extends ArrayList<AbstractGeoEntityStore<?>> {

    private static final long serialVersionUID = 1L;

    public ModifiedEntityStores(AbstractGeoEntityStore<?> ... entityStores) {
        super(Arrays.asList(entityStores));
    }
}
