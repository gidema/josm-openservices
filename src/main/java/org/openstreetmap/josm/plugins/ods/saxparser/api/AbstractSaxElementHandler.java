package org.openstreetmap.josm.plugins.ods.saxparser.api;

import java.util.Objects;

import javax.xml.namespace.QName;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public abstract class AbstractSaxElementHandler implements SaxElementHandler {
    private SaxRootHandler rootHandler;

    public AbstractSaxElementHandler(SaxHandler parentHandler) {
        super();
        Objects.requireNonNull(parentHandler);
        this.rootHandler = parentHandler.getRootHandler();
        if (parentHandler instanceof SaxRootHandler) {
            ((SaxRootHandler)parentHandler).setRootElementHandler(this);
        }
    }
    
    @Override
    public <T> T getContextItem(Class<T> type) {
        return rootHandler.getContextItem(type);
    }


    @Override
    public final SaxRootHandler getRootHandler() {
        return rootHandler;
    }

    @Override
    public void endElement(QName qName) throws SAXException {
        // Override if required
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        // Override if required
    }

    @Override
    public final void delegate(SaxElementHandler childHandler, QName qName, Attributes atts) throws SAXException {
        getRootHandler().startDelegation(childHandler, qName, atts);
    }
    
    protected SAXException unexpectedElement(QName qName) {
        return new SAXException(String.format("Unexpected element: %s:%s", qName.getPrefix(), qName.getLocalPart()));
    }
}
