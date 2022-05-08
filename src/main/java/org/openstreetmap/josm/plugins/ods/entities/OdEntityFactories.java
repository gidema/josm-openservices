package org.openstreetmap.josm.plugins.ods.entities;

import java.util.List;

import javax.xml.namespace.QName;

public interface OdEntityFactories {
    /**
     * @return all entity factories
     */
    public List<OdEntityFactory> getFactories();
    
    /**
     * @param featureType
     * @return the entity factories that apply to the given feature type
     */
    public List<OdEntityFactory> getFactories(QName featureType);
}
