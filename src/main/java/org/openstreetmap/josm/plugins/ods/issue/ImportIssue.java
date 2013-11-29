package org.openstreetmap.josm.plugins.ods.issue;

import java.io.Serializable;

public class ImportIssue implements Issue {
    private Serializable objectId;
    private String message;
    private Exception cause;
    
    public ImportIssue(Serializable objectId, String message) {
        super();
        this.objectId = objectId;
        this.message = objectId.toString() + ": " + message;
    }

    public ImportIssue(Serializable objectId, Exception cause) {
        super();
        this.objectId = objectId;
        this.cause = cause;
        this.message = cause.getMessage();
    }


    @Override
    public String getMessage() {
        return message;
    }

    public Serializable getObjectId() {
        return objectId;
    }

    @Override
    public Exception getCause() {
        return cause;
    }

}
