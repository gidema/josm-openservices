package org.openstreetmap.josm.plugins.ods.context;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Defines a list of OdsContextJob
 * 
 * @author Idema
 *
 */
public class ContextJobList extends LinkedList<OdsContextJob> {

    private static final long serialVersionUID = 1L;

    
    private ContextJobList(List<? extends OdsContextJob> c) {
        super(c);
    }

    public static ContextJobList of(OdsContextJob ... jobs) {
        return new ContextJobList(Arrays.asList(jobs));
    }
}
