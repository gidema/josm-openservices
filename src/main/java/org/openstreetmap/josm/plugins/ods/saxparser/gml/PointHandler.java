package org.openstreetmap.josm.plugins.ods.saxparser.gml;

import javax.xml.namespace.QName;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;
import org.openstreetmap.josm.plugins.ods.saxparser.api.SaxElementHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class PointHandler extends GeometryHandler {
    private final PosHandler posHandler;
    
    private Coordinate coordinate;

    public PointHandler(SaxElementHandler parentHandler, boolean nested, AxisOrder axisOrder) {
        super(parentHandler, nested);
        this.posHandler = new PosHandler(this, axisOrder);
    }

    @Override
    public void start(Attributes atts) {
        super.start(atts);
    }

    @Override
    public SaxElementHandler startElement(QName qName, Attributes atts) throws SAXException {
        switch (qName.getLocalPart()) {
        case "pos":
            return posHandler;
        }
        throw unexpectedElement(qName);
    }

    @Override
    public void end() {
        coordinate = posHandler.getCoordinate();
    }
    
    @Override
    public Point getGeometry() {
        Point point = geometryFactory.createPoint(coordinate);
        point.setSRID(getSrid().intValue());
        return point;
    }
}
