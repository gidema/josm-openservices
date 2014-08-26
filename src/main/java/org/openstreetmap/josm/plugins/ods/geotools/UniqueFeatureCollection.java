package org.openstreetmap.josm.plugins.ods.geotools;

import java.util.HashSet;
import java.util.Set;

import org.geotools.feature.DefaultFeatureCollection;
import org.opengis.feature.simple.SimpleFeature;

/**
 * FeatureCollecion with customized id. 
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class UniqueFeatureCollection extends DefaultFeatureCollection {
    private String attributeName;
    private Set<Object> existing = new HashSet<>();
    
    public UniqueFeatureCollection(String attributeName) {
        this.attributeName = attributeName;
    }
    
    @Override
    public boolean add(SimpleFeature o) {
        Object id = o.getAttribute(attributeName);
        if (!existing.contains(id)) {
            super.add(o);
            existing.add(id);
            return true;
        }
        else {
            return false;
        }
    }
}
