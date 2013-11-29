package org.openstreetmap.josm.plugins.openservices.crs;

import org.openstreetmap.josm.data.osm.OsmPrimitive;

public class InvalidPolygonException extends InvalidGeometryException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private OsmPrimitive primitive;

    public InvalidPolygonException(OsmPrimitive primitive, String message) {
        super(message);
        this.primitive = primitive;
    }

    
    @Override
    public OsmPrimitive getPrimitive() {
        return primitive;
    }
}
