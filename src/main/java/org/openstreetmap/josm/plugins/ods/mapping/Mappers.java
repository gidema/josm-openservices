package org.openstreetmap.josm.plugins.ods.mapping;

import java.util.ArrayList;
import java.util.Arrays;

import org.openstreetmap.josm.plugins.ods.Mapper;

/**
 * Marker class for the list of mappers
 * @author Idema
 *
 */
public class Mappers extends ArrayList<Mapper> {
    
    
    public Mappers(Mapper ... mappers) {
        super(Arrays.asList(mappers));
    }

    private static final long serialVersionUID = 1L;
}
