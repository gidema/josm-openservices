package org.openstreetmap.josm.plugins.ods.entities.storage;

import java.util.function.Function;

import org.openstreetmap.josm.plugins.ods.entities.OdEntity;

public class SimpleOdEntityStore<T extends OdEntity, K> extends AbstractOdEntityStore<T, K> {

    public SimpleOdEntityStore(Function<T, K> pkFunction) {
        super(pkFunction);
    }
}
