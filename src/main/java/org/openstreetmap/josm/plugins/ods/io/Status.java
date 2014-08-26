package org.openstreetmap.josm.plugins.ods.io;

public class Status {
    private boolean failed = false;
    private boolean cancelled = false;
    private String message = null;
    private Exception exception = null;

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public boolean isFailed() {
        return failed;
    }

    public void setFailed(boolean failed) {
        this.failed = failed;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setException(Exception exception) {
        this.exception = exception;
        this.failed = true;
    }

    public boolean isSucces() {
        return !(failed || cancelled);
    }

    public String getMessage() {
        return message;
    }

    public Exception getException() {
        return exception;
    }

    public void clear() {
        this.cancelled = false;
        this.failed = false;
        this.exception = null;
        this.message = null;
    }
}
