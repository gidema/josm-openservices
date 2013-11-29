package org.openstreetmap.josm.plugins.ods.crs;

import org.openstreetmap.josm.data.osm.OsmPrimitive;

public abstract class InvalidGeometryException extends Exception {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    
    public InvalidGeometryException(String message) {
        super(message);
    }


    public abstract OsmPrimitive getPrimitive();
}
