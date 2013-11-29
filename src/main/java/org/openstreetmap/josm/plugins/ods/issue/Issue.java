package org.openstreetmap.josm.plugins.ods.issue;

public interface Issue {
    public String getMessage();
    public Exception getCause();
}
