package org.openstreetmap.josm.plugins.ods.saxparser.opengis.wfs;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import javax.xml.namespace.QName;

import org.openstreetmap.josm.plugins.ods.saxparser.api.AbstractSaxElementHandler;
import org.openstreetmap.josm.plugins.ods.saxparser.api.SaxElementHandler;
import org.openstreetmap.josm.plugins.ods.saxparser.api.SaxHandler;
import org.openstreetmap.josm.plugins.ods.saxparser.gml.AxisOrder;
import org.openstreetmap.josm.plugins.ods.saxparser.opengis.BoundedByHandler;
import org.openstreetmap.josm.plugins.ods.saxparser.opengis.MemberHandler;
import org.openstreetmap.josm.plugins.ods.wfs.WfsFeatureCollection;
import org.openstreetmap.josm.plugins.ods.wfs.WfsFeatureCollectionImpl;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class FeatureCollectionHandler extends AbstractSaxElementHandler {
    private final BoundedByHandler boundedByHandler;
    private final MemberHandler memberHandler;
    private WfsFeatureCollectionImpl featureCollection;

    public FeatureCollectionHandler(SaxHandler parent, QName geometryProperty, AxisOrder axisOrder) {
        super(parent);
        this.boundedByHandler = new BoundedByHandler(this);
        this.memberHandler = new MemberHandler(this, geometryProperty, axisOrder);
    }

    @Override
    public void start(Attributes atts) {
        String attrValue = atts.getValue("timeStamp");
        Instant timeStamp = parseDateTime(attrValue);
        attrValue = atts.getValue("numberMatched");
        Integer numberMatched = null;
        if (attrValue != null && !attrValue.equalsIgnoreCase("unknown")) {
            numberMatched = Integer.valueOf(attrValue);
        }
        attrValue = atts.getValue("numberReturned");
        Integer numberReturned = (attrValue == null ? null : Integer.valueOf(attrValue));
        this.featureCollection = new WfsFeatureCollectionImpl(timeStamp, numberMatched, numberReturned);
    }

    @Override
    public SaxElementHandler startElement(QName qName, Attributes atts) throws SAXException {
        switch(qName.getLocalPart()) {
        case "boundedBy":
            return boundedByHandler;
        case "member":
            return memberHandler;
        default:
            throw unexpectedElement(qName);
        }
    }

    @Override
    public void endElement(QName qName) throws SAXException {
        switch(qName.getLocalPart()) {
        case "boundedBy":
            return;
        case "member":
            featureCollection.add(memberHandler.getWfsFeature());
            break;
        default:
            throw unexpectedElement(qName);
        }
    }

    @Override
    public void end() {
        // No action required
    }

    public WfsFeatureCollection getFeatureCollection() {
        return featureCollection;
    }
    
    static Instant parseDateTime(String dt) {
        if (dt == null) return null;
        if (dt.contains("Z")) {
            return OffsetDateTime.parse(dt).toInstant();
        }
        return LocalDateTime.parse(dt).atOffset(ZoneOffset.UTC).toInstant();
    }
}
