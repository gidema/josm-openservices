package org.openstreetmap.josm.plugins.ods.saxparser.gml;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.openstreetmap.josm.plugins.ods.saxparser.api.AbstractSaxElementHandler;
import org.openstreetmap.josm.plugins.ods.saxparser.api.SaxElementHandler;
import org.xml.sax.Attributes;

public abstract class GeometryHandler extends AbstractSaxElementHandler {
    private final boolean nested;
    private String id;
    private String srsName;
    protected final GeometryFactory geometryFactory;

    public GeometryHandler(SaxElementHandler parentHandler, boolean nested) {
        super(parentHandler);
        this.nested = nested;
        this.geometryFactory = getContextItem(GeometryFactory.class);
    }

    protected String getId() {
        return id;
    }

    protected String getSrsName() {
        return srsName;
    }
    
    protected Long getSrid() {
        if (srsName.startsWith("urn:ogc:def:crs:EPSG::")) {
            return Long.valueOf(srsName.split("::")[1]);
        }
        if (srsName.startsWith("http://www.opengis.net/gml/srs/epsg.xml#")) {
            return Long.valueOf(srsName.split("#")[1]);
        }
        return null;
    }

    @Override
    public void start(Attributes atts) {
        if (!nested) {
            this.id =atts.getValue("gml:id");
            this.srsName = atts.getValue("srsName");
        }
    }
    
    public abstract Geometry getGeometry();
}
