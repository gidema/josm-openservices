package org.openstreetmap.josm.plugins.ods.matching;

import java.util.Arrays;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;
import org.openstreetmap.josm.plugins.ods.entities.storage.OsmEntityStore;

/**
 * Analyze OsmEntity objects by comparing them with any matched OdEntity objects.
 * If there is a match, the aspect analyzers are used to compare several aspects of the OsmEntity
 * to its Od counterpart(s). Aspects can be for example the entity's geometry/location or a certain
 * attribute (or combination of attributes).
 * If differences are found, they get reflected in one or more Deviation objects attached to the OsmEntity.
 *
 * @author Gertjan Idema
 *
 * @param <T> The OsmEntity type for this analyzer
 */
public class OsmEntityAnalyzer<T extends OsmEntity> {
    private final OsmEntityStore<T> entityStore;
    private final List<AspectAnalyzer<T>> aspectAnalyzers;
    private Class<T> entityClass;

    @SafeVarargs
    public OsmEntityAnalyzer(Class<T> entityClass, OsmEntityStore<T> entityStore, AspectAnalyzer<T>... analyzers) {
        this(entityClass, entityStore, Arrays.asList(analyzers));
    }

    public OsmEntityAnalyzer(Class<T> entityClass, OsmEntityStore<T> entityStore, List<AspectAnalyzer<T>> aspectAnalyzers) {
        super();
        this.entityClass = entityClass;
        this.entityStore = entityStore;
        this.aspectAnalyzers = aspectAnalyzers;
    }

    public void run() {
        entityStore.forEach(entity -> {
            aspectAnalyzers.forEach(analyzer -> {
                analyzer.analyze(entity);
            });
        });
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }
}
