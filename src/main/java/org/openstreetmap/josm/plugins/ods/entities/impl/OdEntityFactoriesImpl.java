package org.openstreetmap.josm.plugins.ods.entities.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.openstreetmap.josm.plugins.ods.entities.OdEntityFactories;
import org.openstreetmap.josm.plugins.ods.entities.OdEntityFactory;

public class OdEntityFactoriesImpl implements OdEntityFactories {
    private final List<OdEntityFactory> factories;
    
    
    public OdEntityFactoriesImpl(OdEntityFactory ... factories) {
        super();
        this.factories = Arrays.asList(factories);
    }

    @Override
    public List<OdEntityFactory> getFactories() {
        return factories;
    }

    @Override
    public List<OdEntityFactory> getFactories(QName featureType) {
        return factories.stream().filter(f -> f.appliesTo(featureType)).collect(Collectors.toList());
    }
}
