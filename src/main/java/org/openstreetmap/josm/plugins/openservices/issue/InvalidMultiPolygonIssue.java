package org.openstreetmap.josm.plugins.openservices.issue;

import org.openstreetmap.josm.data.osm.Relation;

public class InvalidMultiPolygonIssue extends JosmIssue {
    private Relation relation;
    private String message;
    
    public InvalidMultiPolygonIssue(Relation relation, String message) {
        super(relation, message);
    }

    public Relation getRelation() {
        return relation;
    }

    public String getMessage() {
        return message;
    }
}
