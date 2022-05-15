package org.openstreetmap.josm.plugins.ods.io;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class TaskStatus {
    private final boolean cancelled;
    private final List<String> warnings;
    private final List<String> errors;
    private final List<Throwable> exceptions;

    
    public TaskStatus() {
        this(false);
    }
    
    public TaskStatus(boolean canceled) {
        super();
        this.cancelled = canceled;
        this.warnings = Collections.emptyList();
        this.errors = Collections.emptyList();
        this.exceptions = Collections.emptyList();
    }

    public TaskStatus(String warning, String error, Throwable exception) {
        super();
        this.cancelled = false;
        this.warnings = warning != null ? Collections.singletonList(warning) : Collections.emptyList();
        this.errors = error != null ? Collections.singletonList(error) : Collections.emptyList();
        this.exceptions = exception != null ? Collections.singletonList(exception) : Collections.emptyList();
    }
    
    public TaskStatus(List<String> warnings, List<String> errors,
            List<Throwable> exceptions) {
        super();
        this.cancelled = false;
        this.warnings = warnings;
        this.errors = errors;
        this.exceptions = exceptions;
    }

    public TaskStatus(Collection<TaskStatus> subResults) {
        this.cancelled = false;
        warnings = new LinkedList<>();
        errors = new LinkedList<>();
        exceptions = new LinkedList<>();
        for (TaskStatus subResult : subResults) {
            warnings.addAll(subResult.getWarnings());
            errors.addAll(subResult.getErrors());
            exceptions.addAll(subResult.getExceptions());
        }
    }
    
    public boolean isCancelled() {
        return cancelled;
    }
    
    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }
    
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
    
    public boolean hasExceptions() {
        return !exceptions.isEmpty();
    }
    
    public List<String> getWarnings() {
        return warnings;
    }

    public List<String> getErrors() {
        return errors;
    }

    public List<Throwable> getExceptions() {
        return exceptions;
    }
    
    public String getWarningString() {
        return String.join("\n", warnings);
    }
    
    public String getErrorString() {
        return String.join("\n", errors);
    }
    
    public String getExceptionString() {
        return String.join("\n", exceptions.stream().map(Throwable::getMessage).collect(Collectors.toList()));
    }
}
