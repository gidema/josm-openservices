package org.openstreetmap.josm.plugins.ods.saxparser.gml;

import javax.xml.namespace.QName;

import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.openstreetmap.josm.plugins.ods.saxparser.api.AbstractSaxElementHandler;
import org.openstreetmap.josm.plugins.ods.saxparser.api.SaxElementHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class LinearRingHandler extends AbstractSaxElementHandler {
    private final PosListHandler posListHandler;
    private GeometryFactory geometryFactory;
    private LinearRing linearRing;

    public LinearRingHandler(SaxElementHandler parentHandler, AxisOrder axisOrder) {
        super(parentHandler);
        this.posListHandler = new PosListHandler(this, axisOrder);
        this.geometryFactory = parentHandler.getContextItem(GeometryFactory.class);
    }

    public LinearRing getRing() {
        return linearRing;
    }

    @Override
    public void start(Attributes atts) {
        linearRing = null;
    }

    @Override
    public SaxElementHandler startElement(QName qName, Attributes atts) throws SAXException {
        switch (qName.getLocalPart()) {
        case "posList":
            return posListHandler;
        default:
            throw unexpectedElement(qName);
        }
    }

    @Override
    public void end() {
        this.linearRing = geometryFactory.createLinearRing(posListHandler.getCoordinates());
    }
}
