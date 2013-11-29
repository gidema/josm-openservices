package org.openstreetmap.josm.plugins.openservices.entities;

import java.util.Collection;

import org.openstreetmap.josm.plugins.openservices.issue.Issue;

public class BuildException extends Exception {
    /**
     * 
     */
    private static final long serialVersionUID = 6237226930016026626L;
    private Issue issue;
    private Collection<Issue> issues;
    
    public BuildException(Issue issue) {
        super(issue.getMessage(), issue.getCause());
    }
    
    public BuildException(Collection<Issue> issues) {
        super("Multiple issues occurred during the build");
        this.issues = issues;
    }

    public Issue getIssue() {
        return issue;
    }
    
    public Collection<Issue> getIssues() {
        return issues;
    }
}
