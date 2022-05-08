package org.openstreetmap.josm.plugins.ods.saxparser.gml;

import static org.openstreetmap.josm.plugins.ods.saxparser.gml.AxisOrder.*;

import javax.xml.namespace.QName;

import org.locationtech.jts.geom.Coordinate;
import org.openstreetmap.josm.plugins.ods.saxparser.api.AbstractSaxElementHandler;
import org.openstreetmap.josm.plugins.ods.saxparser.api.SaxElementHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class PosListHandler extends AbstractSaxElementHandler {
    private final AxisOrder axisOrder;
    private int dimensions = 2;
    private StringBuilder sb = new StringBuilder();
    private Coordinate[] coords;

    public PosListHandler(SaxElementHandler parentHandler, AxisOrder axisOrder) {
        super(parentHandler);
        this.axisOrder = axisOrder;
    }

    @Override
    public void start(Attributes atts) {
        String srsDimension = atts.getValue("srsDimension");
        if (srsDimension != null) {
            dimensions = Integer.parseInt(srsDimension);
        }
        sb.setLength(0);
        this.coords = null;
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
        String[] values = sb.toString().split("\\s+");
        this.coords = new Coordinate[values.length / dimensions];
        int i = 0; int coord = 0;
        while (i < values.length) {
            double x;
            double y;
            if (axisOrder == XY || axisOrder == LonLat) {
                x = Double.parseDouble(values[i++]);
                y = Double.parseDouble(values[i++]);
            }
            else {
                y = Double.parseDouble(values[i++]);
                x = Double.parseDouble(values[i++]);
            }
            double z = Double.NaN;
            if (dimensions == 3) {
                z = Double.parseDouble(values[i++]);
            }
            coords[coord++] = new Coordinate(x, y, z);
        }
    }

    public Coordinate[] getCoordinates() {
        return coords;
    }

}
