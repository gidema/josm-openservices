package org.openstreetmap.josm.plugins.ods.od;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.domains.buildings.HouseNumber;

/**
 * Factory to create a house number from a simple feature. 
 * 
 * @author gertjan
 *
 */
public interface HouseNumberFactory<T extends HouseNumber> {

    public T create(SimpleFeature feature);
    public Class<T> getTargetClass();
    
}
