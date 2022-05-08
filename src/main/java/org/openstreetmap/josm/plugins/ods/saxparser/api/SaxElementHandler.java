package org.openstreetmap.josm.plugins.ods.saxparser.api;

import javax.xml.namespace.QName;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public interface SaxElementHandler extends SaxHandler {

    public void start(Attributes atts);
    /*
     * Handle an XML startElement message.
     */
    public SaxElementHandler startElement(QName qName, Attributes atts) throws SAXException;
    
    /*
     * Handle an XML endElement message.
     */
    public void endElement(QName qName) throws SAXException;
    
    public void end();

    public void characters(char[] ch, int start, int length);
    
    public void delegate(SaxElementHandler childHandler, QName qName, Attributes atts) throws SAXException;
}
