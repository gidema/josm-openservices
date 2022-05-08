package org.openstreetmap.josm.plugins.ods.saxparser.gml;

import javax.xml.namespace.QName;

import org.locationtech.jts.geom.Polygon;
import org.openstreetmap.josm.plugins.ods.saxparser.api.SaxElementHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class SurfaceMemberHandler extends GeometryHandler {
    private final PolygonHandler polygonHandler;
    
    private Polygon polygon;

    public SurfaceMemberHandler(SaxElementHandler parentHandler, boolean nested, AxisOrder axisOrder) {
        super(parentHandler, nested);
        this.polygonHandler = new PolygonHandler(parentHandler, false, axisOrder);
    }

    @Override
    public SaxElementHandler startElement(QName qName, Attributes atts) throws SAXException {
        switch (qName.getLocalPart()) {
        case "Polygon":
            return polygonHandler;
        default:
            throw unexpectedElement(qName);
        }
    }

    @Override
    public void end() {
        this.polygon = polygonHandler.getGeometry();
        this.polygon.setSRID(getSrid().intValue());
    }
    
    @Override
    public Polygon getGeometry() {
        return polygon;
    }
}
