package org.openstreetmap.josm.plugins.ods.saxparser.opengis;

import javax.xml.namespace.QName;

import org.locationtech.jts.geom.Envelope;
import org.openstreetmap.josm.plugins.ods.saxparser.api.AbstractSaxElementHandler;
import org.openstreetmap.josm.plugins.ods.saxparser.api.SaxElementHandler;
import org.openstreetmap.josm.plugins.ods.saxparser.gml.EnvelopeHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class BoundedByHandler extends AbstractSaxElementHandler {
    private final EnvelopeHandler envelopeHandler;

    public BoundedByHandler(SaxElementHandler parentHandler) {
        super(parentHandler);
        this.envelopeHandler = new EnvelopeHandler(this);
   }

    @Override
    public void start(Attributes atts) {
        // TODO Auto-generated method stub

    }

    @Override
    public SaxElementHandler startElement(QName qName, Attributes atts) throws SAXException {
        switch (qName.getLocalPart()) {
        case "Envelope":
            return envelopeHandler;
        default:
            throw unexpectedElement(qName);
        }
    }

    @Override
    public void end() {
        // TODO Auto-generated method stub

    }
    
    public Envelope getEnvelope() {
        return envelopeHandler.getEnvelope();
    }

}
