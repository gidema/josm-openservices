package org.openstreetmap.josm.plugins.ods.matching;

import java.util.ArrayList;
import java.util.Arrays;

import org.openstreetmap.josm.plugins.ods.Matcher;

/**
 * Marker class for the list of matchers
 * @author Idema
 *
 */
public class Matchers extends ArrayList<Matcher> {
    
    
    public Matchers(Matcher ... matchers) {
        super(Arrays.asList(matchers));
    }

    private static final long serialVersionUID = 1L;
}
