package org.openstreetmap.josm.plugins.ods.saxparser.gml;

import javax.xml.namespace.QName;

import org.locationtech.jts.geom.Envelope;
import org.openstreetmap.josm.plugins.ods.saxparser.api.AbstractSaxElementHandler;
import org.openstreetmap.josm.plugins.ods.saxparser.api.SaxElementHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class EnvelopeHandler extends AbstractSaxElementHandler {
    private final DirectPositionHandler lowerCornerHandler;
    private final DirectPositionHandler upperCornerHandler;
    
    Envelope envelope;

    public EnvelopeHandler(SaxElementHandler parentHandler) {
        super(parentHandler);
        this.lowerCornerHandler = new DirectPositionHandler(this);
        this.upperCornerHandler = new DirectPositionHandler(this);
    }

    @Override
    public void start(Attributes atts) {
        // No action required
    }

    @Override
    public SaxElementHandler startElement(QName qName, Attributes atts) throws SAXException {
        switch(qName.getLocalPart()) {
        case "lowerCorner":
            return lowerCornerHandler;
        case "upperCorner":
            return upperCornerHandler;
        default:
            throw unexpectedElement(qName);
        }
    }

    @Override
    public void end() {
        this.envelope = new Envelope(lowerCornerHandler.getCoordinate(), upperCornerHandler.getCoordinate());
    }

    public Envelope getEnvelope() {
        return envelope;
    }

}
