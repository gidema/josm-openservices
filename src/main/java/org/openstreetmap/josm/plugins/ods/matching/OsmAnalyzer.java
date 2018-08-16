package org.openstreetmap.josm.plugins.ods.matching;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;

public class OsmAnalyzer implements Runnable {
    private final Map<Class<? extends OsmEntity>, OsmEntityAnalyzer<?>> analyzers = new HashMap<>();

    public OsmAnalyzer(Collection<OsmEntityAnalyzer<?>> analyzers) {
        analyzers.forEach(a -> {
            this.analyzers.put(a.getEntityClass(), a);
        });
    }

    @Override
    public void run() {
        analyzers.values().forEach(a -> a.run());
    }

    @SuppressWarnings("unchecked")
    public <T extends OsmEntity> OsmEntityAnalyzer<T> getMatchAnalyzer(Class<T> clazz) {
        return (OsmEntityAnalyzer<T>) analyzers.get(clazz);
    }
}
