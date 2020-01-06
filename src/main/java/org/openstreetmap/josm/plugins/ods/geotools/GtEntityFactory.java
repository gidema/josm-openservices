package org.openstreetmap.josm.plugins.ods.geotools;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.Name;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;

/**
 * Create OdEntity objects from geotools SimpleFeature objects.
 * 
 * A meta factory is be used to retrieve implementations for specific combinations
 * of feature type and target <? extends OdEntity> type.
 * 
 * 
 * @author gertjan
 *
 * @param <T extends OdEntity>
 */
public interface GtEntityFactory<T> {
    public boolean isApplicable(Name featureType, Class<?> entityType);
    public Class<T> getTargetType();
    public T create(SimpleFeature feature, DownloadResponse response);
//    public void addModifier(EntityModifier<T> modifier);
}
