package org.openstreetmap.josm.plugins.ods.issue;

import org.openstreetmap.josm.data.osm.OsmPrimitive;

public class JosmIssue implements Issue {
    private OsmPrimitive primitive;
    private Exception exception;
    private String message;

    
    public JosmIssue(OsmPrimitive primitive, String message) {
        super();
        this.primitive = primitive;
        StringBuilder sb = new StringBuilder(100);
        sb.append(primitive.getType()).append(' ').append(primitive.getId()).
             append(':').append(message);
        this.message = sb.toString();
    }

    public JosmIssue(OsmPrimitive primitive, Exception exception) {
        super();
        this.primitive = primitive;
        this.exception = exception;
        this.message = exception.getMessage();
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Exception getCause() {
        return exception;
    }
    
    public OsmPrimitive getPrimitive() {
        return primitive;
    }

}
