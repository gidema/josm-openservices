package org.openstreetmap.josm.plugins.ods.saxparser.impl;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.openstreetmap.josm.plugins.ods.saxparser.api.SaxElementHandler;
import org.openstreetmap.josm.plugins.ods.saxparser.api.SaxRootHandler;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class SaxRootHandlerImpl implements SaxRootHandler {
    private Map<Class<?>, Object> context = new HashMap<>();
    private final Deque<SaxElementHandler> parserStack = new ArrayDeque<>();
    private Locator locator;
    private Map<String, String> prefixMappings;
    private int skippingDepth;
    private boolean skipSubTree = false;
    private SaxElementHandler rootElementHandler;
    private boolean rootProcessed = false;

    
    @Override
    public SaxRootHandler getRootHandler() {
        return this;
    }

    @Override
    public <T, V extends T> void setContextItem(Class<T> key, V value) {
        context.put(key, value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getContextItem(Class<T> type) {
        return (T) context.get(type);
    }

    @Override
    public void setRootElementHandler (SaxElementHandler rootElementHandler) {
        this.rootElementHandler = rootElementHandler;
    }

    @Override
    public SaxElementHandler getDelegate() {
        return parserStack.peekLast();
    }

    @Override
    public void startDelegation(SaxElementHandler delegate, QName qName, Attributes atts) throws SAXException {
        parserStack.addLast(delegate);
        delegate.start(atts);
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }

    public Locator getLocator() {
        return locator;
    }

    @Override
    public final void startDocument() throws SAXException {
        parserStack.addLast(rootElementHandler);
    }

    @Override
    public void endDocument() throws SAXException {
        //
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        if (prefixMappings == null) prefixMappings = new HashMap<>();
        prefixMappings.put(prefix, uri);
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        prefixMappings.remove(prefix);
    }
    
    @Override
    public String getNsUri(String prefix) {
        return prefixMappings == null ? null : prefixMappings.get(prefix);
    }

    @Override
    public final void startElement(String uri, String localName, String qn, Attributes atts) throws SAXException {
        QName qName = new QName(uri, localName);
        if (skipSubTree) {
            skippingDepth++;
        }
        else if (!rootProcessed) {
            parserStack.peekLast().start(atts);
            rootProcessed = true;
        }
        else {
            SaxElementHandler nextHandler = parserStack.peekLast().startElement(qName, atts);
            nextHandler.start(atts);
            parserStack.addLast(nextHandler);
        }
    }

    @Override
    public final void endElement(String uri, String localName, String qn) throws SAXException {
        if (skipSubTree) {
            skippingDepth--;
            skipSubTree = skippingDepth > 0;
        }
        else {
            QName qName = new QName(uri, localName);
            parserStack.removeLast().end();
            if (!parserStack.isEmpty()) {
                parserStack.peekLast().endElement(qName);
            }
        }
     }

    @Override
    public final void characters(char[] ch, int start, int length) throws SAXException {
        if (!skipSubTree) {
            parserStack.peekLast().characters(ch, start, length);
        }
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        // Ignore
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        // Ignore
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
        // Ignore
    }

    @Override
    public void skip() {
        skipSubTree = true;
        skippingDepth = 1;
    }
}
