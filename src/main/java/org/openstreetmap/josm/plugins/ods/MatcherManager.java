package org.openstreetmap.josm.plugins.ods;

import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.josm.plugins.ods.matching.AddressNodeMatcher;
import org.openstreetmap.josm.plugins.ods.matching.BuildingMatcher;

public class MatcherManager {
    private final BuildingMatcher buildingMatcher;
    private final AddressNodeMatcher addressNodeMatcher;

    private final Map<Class<? extends Matcher>, Matcher> matchers = new HashMap<>();

    public MatcherManager(OdsModule module) {
        buildingMatcher = new BuildingMatcher(module);
        addressNodeMatcher = new AddressNodeMatcher(module);
        matchers.put(BuildingMatcher.class, buildingMatcher);
        matchers.put(AddressNodeMatcher.class, addressNodeMatcher);
    }

    public <M extends Matcher> Matcher getMatcher(Class<M> clazz) {
        return matchers.get(clazz);

    }

    public void reset() {
        for (Matcher matcher : matchers.values()) {
            matcher.reset();
        }
    }
}
