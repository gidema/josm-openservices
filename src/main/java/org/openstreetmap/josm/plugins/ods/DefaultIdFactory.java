package org.openstreetmap.josm.plugins.ods;

import java.io.Serializable;

import org.opengis.feature.simple.SimpleFeature;

public class DefaultIdFactory implements IdFactory {
    private String idAttribute;

    public DefaultIdFactory(String idAttribute) {
        this.idAttribute = idAttribute;
    }
    
    @Override
    public Serializable getId(SimpleFeature feature) {
        if (idAttribute == null) {
            return feature.getIdentifier().getID();
        }
        try {
            return (Serializable) feature.getAttribute(idAttribute);
        } catch (ClassCastException e) {
            e.printStackTrace();
            return null;
        }
    }
}
