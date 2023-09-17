package org.openstreetmap.josm.plugins.ods.mapping;

import java.util.Objects;

public enum MatchStatus {
    NO_MATCH, COMPARABLE, MATCH, NULL;
    
    public static MatchStatus combine(MatchStatus... msArr) {
        for (MatchStatus ms : msArr) {
            if (ms.equals(NO_MATCH)) return NO_MATCH;
        }
        for (MatchStatus ms : msArr) {
            if (ms.equals(COMPARABLE)) return COMPARABLE;
        }
        return MATCH;
    }
    
    public static MatchStatus match(Object o1, Object o2) {
        if (Objects.equals(o1, o2)) {
            return MatchStatus.MATCH;
        }
        return MatchStatus.NO_MATCH;
    }

    @Override
    public String toString() {
        if (this == NULL) return null;
        return super.toString().toLowerCase();
    }
}
