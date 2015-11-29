package org.openstreetmap.josm.plugins.ods;

import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.actual.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.matching.AddressNodeMatcher;
import org.openstreetmap.josm.plugins.ods.matching.BuildingMatcher;

public class MatcherManager {
    private BuildingMatcher buildingMatcher;
    private AddressNodeMatcher addressNodeMatcher;
    
    private Map<Class<? extends Entity>, Matcher<? extends Entity>> matchers = new HashMap<>();
    
    public MatcherManager(OdsModule module) {
        buildingMatcher = new BuildingMatcher(module);
        addressNodeMatcher = new AddressNodeMatcher(module);
        matchers.put(Building.class, buildingMatcher);
        matchers.put(AddressNode.class, addressNodeMatcher);
    }
    
    @SuppressWarnings("unchecked")
    public <E extends Entity> Matcher<? extends E> getMatcher(Class<E> clazz) {
        return (Matcher<? extends E>) matchers.get(clazz);
        
    }

    public void reset() {
        for (Matcher<?> matcher : matchers.values()) {
            matcher.reset();
        }
    }
}
