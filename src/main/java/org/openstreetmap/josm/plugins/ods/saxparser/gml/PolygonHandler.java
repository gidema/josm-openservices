package org.openstreetmap.josm.plugins.ods.saxparser.gml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;
import org.openstreetmap.josm.plugins.ods.saxparser.api.SaxElementHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class PolygonHandler extends GeometryHandler {
    private final WrappedRingHandler exteriorHandler;
    private final WrappedRingHandler interiorHandler;

    private LinearRing exteriorRing = null;
    private final List<LinearRing> interiorRings = new ArrayList<>();

    public PolygonHandler(SaxElementHandler parentHandler, boolean nested, AxisOrder axisOrder) {
        super(parentHandler, nested);
        this.exteriorHandler = new WrappedRingHandler(this, axisOrder);
        this.interiorHandler = new WrappedRingHandler(this, axisOrder);
    }

    @Override
    public void start(Attributes atts) {
        super.start(atts);
        exteriorRing = null;
        interiorRings.clear();
    }

    @Override
    public SaxElementHandler startElement(QName qName, Attributes atts) throws SAXException {
        switch (qName.getLocalPart()) {
        case "exterior":
            return exteriorHandler;
        case "interior":
            return interiorHandler;
        default:
            throw new SAXException("Unexpected element:" + qName.toString());
        }
    }

    @Override
    public void endElement(QName qName) throws SAXException {
        switch (qName.getLocalPart()) {
        case "exterior":
            exteriorRing = exteriorHandler.getLinearRing();
            break;
        case "interior":
            interiorRings.add(interiorHandler.getLinearRing());
            break;
        default:
            throw unexpectedElement(qName);
        }
    }
    
    @Override
    public void end() {
        // No action required
    }

    @Override
    public Polygon getGeometry() {
        Polygon polygon = geometryFactory.createPolygon(exteriorRing, interiorRings.toArray(new LinearRing[interiorRings.size()] ));
        polygon.setSRID(getSrid().intValue());
        return polygon;
    }
}
