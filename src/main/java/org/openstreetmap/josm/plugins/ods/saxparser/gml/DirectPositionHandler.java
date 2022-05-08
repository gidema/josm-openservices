package org.openstreetmap.josm.plugins.ods.saxparser.gml;

import javax.xml.namespace.QName;

import org.locationtech.jts.geom.Coordinate;
import org.openstreetmap.josm.plugins.ods.saxparser.api.AbstractSaxElementHandler;
import org.openstreetmap.josm.plugins.ods.saxparser.api.SaxElementHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class DirectPositionHandler extends AbstractSaxElementHandler {
    private final StringBuilder sb = new StringBuilder();
    private Coordinate coordinate;

    public DirectPositionHandler(SaxElementHandler parentHandler) {
        super(parentHandler);
    }

    @Override
    public void start(Attributes atts) {
        this.sb.setLength(0);
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
        String[] coords = sb.toString().split(" ");
        Double x = Double.valueOf(coords[0]);
        Double y = Double.valueOf(coords[1]);
        Double z = (coords.length < 3 ? Double.NaN : Double.valueOf(coords[0]));
        this.coordinate = new Coordinate(x, y, z);
    }
    
    public Coordinate getCoordinate() {
        return this.coordinate;
    }
}
