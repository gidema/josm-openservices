package org.openstreetmap.josm.plugins.ods.saxparser.gml;

import javax.xml.namespace.QName;

import static org.openstreetmap.josm.plugins.ods.saxparser.gml.AxisOrder.*;

import org.locationtech.jts.geom.Coordinate;
import org.openstreetmap.josm.plugins.ods.saxparser.api.AbstractSaxElementHandler;
import org.openstreetmap.josm.plugins.ods.saxparser.api.SaxElementHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class PosHandler extends AbstractSaxElementHandler {
    private final AxisOrder axisOrder;
    private int dimensions = 2;
    private StringBuilder sb = new StringBuilder(32);
    private Coordinate coordinate;

    public PosHandler(SaxElementHandler parentHandler, AxisOrder axisOrder) {
        super(parentHandler);
        this.axisOrder = axisOrder;
    }

    @Override
    public void start(Attributes atts) {
        sb.setLength(0);
        this.coordinate = null;
    }

    @Override
    public SaxElementHandler startElement(QName qName, Attributes atts) throws SAXException {
        throw unexpectedElement(qName);
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        sb.append(ch, start, length);
    }

    @Override
    public void end() {
        String[] values = sb.toString().split(" ");
        double x;
        double y;
        if (axisOrder == XY || axisOrder == LonLat) {
            x = Double.parseDouble(values[0]);
            y = Double.parseDouble(values[1]);
        }
        else {
            x = Double.parseDouble(values[1]);
            y = Double.parseDouble(values[0]);
        }
        double z = Double.NaN;
        if (dimensions == 3) {
            z = Double.parseDouble(values[2]);
        }
        coordinate = new Coordinate(x, y, z);
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

}
