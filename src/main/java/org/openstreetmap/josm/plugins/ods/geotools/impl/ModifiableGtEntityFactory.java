package org.openstreetmap.josm.plugins.ods.geotools.impl;

import java.util.LinkedList;
import java.util.List;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.entities.EntityModifier;
import org.openstreetmap.josm.plugins.ods.geotools.GtEntityFactory;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;

public abstract class ModifiableGtEntityFactory<T> implements GtEntityFactory<T> {
    private List<EntityModifier<T>> modifiers = new LinkedList<>();

    @SuppressWarnings("unchecked")
    public void addModifier(EntityModifier<?> modifier) {
        if (modifier.getTargetType().equals(this.getTargetType())) {
            modifiers.add((EntityModifier<T>) modifier);
        }
    }

    @Override
    public final T create(SimpleFeature feature, DownloadResponse response) {
        T entity = createEntity(feature, response);
        modifiers.forEach(m -> {
            if (m.isApplicable(entity)) {
                m.modify(entity);
            }
        });
        return entity;
    }

    protected abstract T createEntity(SimpleFeature feature,
            DownloadResponse response);
}
