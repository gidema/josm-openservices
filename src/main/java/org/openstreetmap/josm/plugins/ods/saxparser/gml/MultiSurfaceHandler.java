package org.openstreetmap.josm.plugins.ods.saxparser.gml;

import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.openstreetmap.josm.plugins.ods.saxparser.api.SaxElementHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class MultiSurfaceHandler extends GeometryHandler {
    private final SurfaceMemberHandler surfaceMemberHandler;

    private List<Polygon> polygons = new LinkedList<>();
    private MultiPolygon multiPolygon;

    public MultiSurfaceHandler(SaxElementHandler parentHandler, boolean nested, AxisOrder axisOrder) {
        super(parentHandler, nested);
        this.surfaceMemberHandler = new SurfaceMemberHandler(this, false, axisOrder);
    }

    @Override
    public void start(Attributes atts) {
        super.start(atts);
    }

    @Override
    public SaxElementHandler startElement(QName qName, Attributes atts) throws SAXException {
        switch (qName.getLocalPart()) {
        case "surfaceMember":
            return surfaceMemberHandler;
        default:
            throw unexpectedElement(qName);
        }
    }

    @Override
    public void endElement(QName qName) throws SAXException {
        switch (qName.getLocalPart()) {
        case "surfaceMember":
            polygons.add(surfaceMemberHandler.getGeometry());
            break;
        default:
            throw unexpectedElement(qName);
        }
    }

    @Override
    public void end() {
        this.multiPolygon = geometryFactory.createMultiPolygon(polygons.toArray(new Polygon[polygons.size()]));
        multiPolygon.setSRID(getSrid().intValue());
    }

    @Override
    public MultiPolygon getGeometry() {
        return multiPolygon;
    }
}
