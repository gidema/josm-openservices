package org.openstreetmap.josm.plugins.ods.saxparser.opengis.wfs;

import javax.xml.namespace.QName;

import org.locationtech.jts.geom.Geometry;
import org.openstreetmap.josm.plugins.ods.saxparser.api.AbstractSaxElementHandler;
import org.openstreetmap.josm.plugins.ods.saxparser.api.SaxElementHandler;
import org.openstreetmap.josm.plugins.ods.saxparser.gml.AxisOrder;
import org.openstreetmap.josm.plugins.ods.saxparser.gml.MultiSurfaceHandler;
import org.openstreetmap.josm.plugins.ods.saxparser.gml.PointHandler;
import org.openstreetmap.josm.plugins.ods.saxparser.gml.PolygonHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class GeometryPropertyHandler extends AbstractSaxElementHandler {
    private final MultiSurfaceHandler multiSurfaceHandler;
    private final PolygonHandler polygonHandler;
    private final PointHandler pointHandler;
    private Geometry geometry;

    public GeometryPropertyHandler(SaxElementHandler parentHandler, AxisOrder axisOrder) {
        super(parentHandler);
        multiSurfaceHandler = new MultiSurfaceHandler(this, false, axisOrder);
        polygonHandler = new PolygonHandler(this, false, axisOrder);
        pointHandler = new PointHandler(this, false, axisOrder);
    }

    @Override
    public void start(Attributes atts) {
        this.geometry = null;
    }

    @Override
    public SaxElementHandler startElement(QName qName, Attributes atts) throws SAXException {
        switch (qName.getLocalPart()) {
        case "MultiSurface":
            return multiSurfaceHandler;
        case "Polygon":
            return polygonHandler;
        case "Point":
            return pointHandler;
        default:
            throw unexpectedElement(qName);
        }
    }

    
    @Override
    public void endElement(QName qName) throws SAXException {
        geometry = null;
        switch (qName.getLocalPart()) {
        case "MultiSurface":
            geometry = multiSurfaceHandler.getGeometry();
            break;
        case "Polygon":
            geometry = polygonHandler.getGeometry();
            break;
        case "Point":
            geometry = pointHandler.getGeometry();
            break;
        default:
            throw unexpectedElement(qName);
        }
    }

    public Geometry getGeometry() {
        return geometry;
    }
    
    @Override
    public void end() {
        // No action required
    }

}
