package org.openstreetmap.josm.plugins.ods.saxparser.opengis;

import javax.xml.namespace.QName;

import org.openstreetmap.josm.plugins.ods.saxparser.api.AbstractSaxElementHandler;
import org.openstreetmap.josm.plugins.ods.saxparser.api.SaxElementHandler;
import org.openstreetmap.josm.plugins.ods.saxparser.gml.AxisOrder;
import org.openstreetmap.josm.plugins.ods.saxparser.opengis.wfs.WfsFeature;
import org.openstreetmap.josm.plugins.ods.saxparser.opengis.wfs.WfsFeatureHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class MemberHandler extends AbstractSaxElementHandler {
    private final WfsFeatureHandler featureHandler;
    private QName featureType;
    private WfsFeature wfsFeature;
    
    public MemberHandler(SaxElementHandler parentHandler, QName geometryProperty, AxisOrder axisOrder) {
        super(parentHandler);
        this.featureHandler = new WfsFeatureHandler(this, geometryProperty, axisOrder);
    }

    @Override
    public void start(Attributes atts) {
       wfsFeature = null;
    }

    @Override
    public SaxElementHandler startElement(QName qName, Attributes atts) throws SAXException {
        featureType = qName;
        return featureHandler;
    }

    @Override
    public void end() {
       this.wfsFeature = featureHandler.getFeature();
    }

    public QName getFeatureType() {
        return featureType;
    }

    public WfsFeature getWfsFeature() {
        return wfsFeature;
    }
}
