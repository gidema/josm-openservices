package org.openstreetmap.josm.plugins.ods.crs;

import org.openstreetmap.josm.data.osm.OsmPrimitive;

public class InvalidMultiPolygonException extends InvalidGeometryException {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private OsmPrimitive primitive;
    
    public InvalidMultiPolygonException(OsmPrimitive primitive, String message) {
        super(message);
        this.primitive = primitive;
    }

    @Override
    public OsmPrimitive getPrimitive() {
        return primitive;
    }
}
