package org.openstreetmap.josm.plugins.ods.saxparser.opengis.wfs;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.openstreetmap.josm.plugins.ods.saxparser.api.AbstractSaxElementHandler;
import org.openstreetmap.josm.plugins.ods.saxparser.api.SaxElementHandler;
import org.openstreetmap.josm.plugins.ods.saxparser.gml.AxisOrder;
import org.openstreetmap.josm.plugins.ods.saxparser.opengis.BoundedByHandler;
import org.openstreetmap.josm.plugins.ods.saxparser.opengis.MemberHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class WfsFeatureHandler extends AbstractSaxElementHandler {
    private final BoundedByHandler boundedByHandler;
    private final GeometryPropertyHandler geomertyPropertyHandler;
    private final StringHandler stringHandler;

    private final MemberHandler parentHandler;
    private final QName geometryProperty;

    private String gmlId;
    private Envelope boundedBy;
    private Geometry geometry;
    private Map<String, String> properties;
    
    private WfsFeature wfsFeature;

    public WfsFeatureHandler(MemberHandler parentHandler, QName geometryProperty, AxisOrder axisOrder) {
        super(parentHandler);
        this.parentHandler = parentHandler;
        this.boundedByHandler = new BoundedByHandler(this);
        this.geomertyPropertyHandler = new GeometryPropertyHandler(this, axisOrder);
        this.geometryProperty = geometryProperty;
        this.stringHandler = new StringHandler(this);
    }

    @Override
    public void start(Attributes atts) {
        gmlId = atts.getValue("gml:id");
        boundedBy = null;
        geometry = null;
        properties = new HashMap<>();
    }

    @Override
    public SaxElementHandler startElement(QName qName, Attributes atts) throws SAXException {
        if (qName.getLocalPart().equals("boundedBy")) return boundedByHandler;
        if (qName.equals(geometryProperty)) return geomertyPropertyHandler;
        return stringHandler;
    }

    @Override
    public void endElement(QName qName) throws SAXException {
        if (qName.getLocalPart().equals("boundedBy")) {
            boundedBy = boundedByHandler.getEnvelope();
        }
        else if (qName.equals(geometryProperty)) {
            geometry = geomertyPropertyHandler.getGeometry();
        }
        else {
            String value = stringHandler.getValue();
            properties.put(qName.getLocalPart(), value);
        }
    }

    @Override
    public void end() {
        wfsFeature = new WfsFeatureImpl(parentHandler.getFeatureType(), gmlId, geometry, properties);
    }

    public WfsFeature getFeature() {
        return wfsFeature;
    }
}
