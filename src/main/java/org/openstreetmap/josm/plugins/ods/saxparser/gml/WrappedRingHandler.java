package org.openstreetmap.josm.plugins.ods.saxparser.gml;

import javax.xml.namespace.QName;

import org.locationtech.jts.geom.LinearRing;
import org.openstreetmap.josm.plugins.ods.saxparser.api.AbstractSaxElementHandler;
import org.openstreetmap.josm.plugins.ods.saxparser.api.SaxElementHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Handle a Linear ring that is wrapped in an other element. Typically 'exterior' or 'interior'
 * 
 * @author Idema
 *
 */
public class WrappedRingHandler extends AbstractSaxElementHandler {
    private final LinearRingHandler ringHandler;
    
    public WrappedRingHandler(SaxElementHandler parentHandler, AxisOrder axisOrder) {
        super(parentHandler);
        this.ringHandler = new LinearRingHandler(this, axisOrder);
    }

    @Override
    public void start(Attributes atts) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public SaxElementHandler startElement(QName qName, Attributes atts) throws SAXException {
        switch (qName.getLocalPart()) {
        case "LinearRing":
            return ringHandler;
        default:
            throw unexpectedElement(qName);
        }
    }

    
    @Override
    public void end() {
        // No action required
    }

    public LinearRing getLinearRing() {
        return ringHandler.getRing();
    }
}
