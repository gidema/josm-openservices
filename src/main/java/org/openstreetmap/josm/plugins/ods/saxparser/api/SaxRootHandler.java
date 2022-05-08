package org.openstreetmap.josm.plugins.ods.saxparser.api;

import javax.xml.namespace.QName;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public interface SaxRootHandler extends ContentHandler, SaxHandler {
    
    public <T, V extends T> void setContextItem(Class<T> key, V value);

    public void setRootElementHandler(SaxElementHandler rootElementHandler);

    public void startDelegation(SaxElementHandler delegate, QName qName, Attributes atts) throws SAXException;

    public SaxElementHandler getDelegate();

    public String getNsUri(String string);

    void skip();

}