package org.openstreetmap.josm.plugins.openservices.issue;

public class DefaultIssue implements Issue {
    private String message;
    private Exception cause;
    
    public DefaultIssue(String message) {
        super();
        this.message = message;
    }

    public DefaultIssue(String message, Exception cause) {
        super();
        this.message = message;
        this.cause = cause;
    }

    public DefaultIssue(Exception cause) {
        super();
        this.cause = cause;
        this.message = cause.getMessage();
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Exception getCause() {
        return cause;
    }

}
