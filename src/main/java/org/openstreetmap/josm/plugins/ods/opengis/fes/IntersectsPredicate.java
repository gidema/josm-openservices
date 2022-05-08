package org.openstreetmap.josm.plugins.ods.opengis.fes;

import javax.xml.namespace.QName;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.openstreetmap.josm.plugins.ods.gml.GmlXmlFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class IntersectsPredicate implements FilterPredicate {
    private final QName property;
    private final Geometry geometry;
    
    public IntersectsPredicate(QName property, Geometry geometry) {
        this.property = property;
        this.geometry = geometry;
    }

    @Override
    public Element buildXmlElement(Document doc) {
        Element intersects = doc.createElementNS(Fes.NS_FES_20, "Intersects");
        Element propertyNode = doc.createElementNS(Fes.NS_FES_20, "PropertyName");
        propertyNode.setTextContent(property.getPrefix() + ":" + property.getLocalPart());
        intersects.appendChild(propertyNode);
        Element geometryElement;
        switch (geometry.getGeometryType()) {
        case Geometry.TYPENAME_MULTIPOLYGON:
            geometryElement = GmlXmlFactory.createMultiPolygon(doc, (MultiPolygon)geometry, "MP1");
            break;
//        case Geometry.TYPENAME_LINEARRING:
//            geometryElement = buildGmlElement(doc, (LinearRing)geometry);
//            break;
        case Geometry.TYPENAME_POLYGON:
            geometryElement = GmlXmlFactory.createPolygon(doc, (Polygon)geometry, "P1");
            break;
        default:
            throw new RuntimeException("Unsupported geometry type for bounding box");
        }
        intersects.appendChild(geometryElement);
        return intersects;
    }
}
