package org.openstreetmap.josm.plugins.ods.gml;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class GmlXmlFactory {
    private final static String NS_GML = "http://www.opengis.net/gml";

    public static Element createPolygon(Document doc, Polygon polygon, String id) {
        Element polygonElement = createPolygon(doc, polygon);
        polygonElement.setAttributeNS(NS_GML, "id", id);
        return polygonElement;
    }
        
    public static Element createMultiPolygon(Document doc, MultiPolygon polygon, String id) {
        Element multiPolygonElement = createMultiPolygon(doc, polygon);
        multiPolygonElement.setAttributeNS(NS_GML, "id", id);
        return multiPolygonElement;
    }
        
    private static Element createPolygon(Document doc, Polygon polygon) {
        Element element = doc.createElementNS(NS_GML, "Polygon");
        element.setPrefix("gml");
        Element exterior = doc.createElementNS(NS_GML, "exterior");
        exterior.appendChild(createLinearRing(doc, polygon.getExteriorRing()));
        element.appendChild(exterior);
        for (int i = 0 ; i < polygon.getNumInteriorRing() ; i++) {
            Element interior = doc.createElementNS(NS_GML, "interior");
            interior.appendChild(createLinearRing(doc, polygon.getInteriorRingN(i)));
            element.appendChild(interior);
        }
        return element;
    }

    private static Element createLinearRing(Document doc, LinearRing ring) {
        Element element = doc.createElementNS(NS_GML, "LinearRing");
        element.appendChild(createPosList(doc, ring.getCoordinateSequence()));
        return element;
    }

    private static Node createPosList(Document doc, CoordinateSequence coordinateSequence) {
        Element posList = doc.createElementNS(NS_GML, "posList");
        StringBuilder sb = new StringBuilder();
        Coordinate coord = new Coordinate();
        for (int i = 0; i < coordinateSequence.size() ; i++) {
            coordinateSequence.getCoordinate(i, coord);
            if (i > 0) sb.append(' ');
            sb.append(coord.x).append(' ').append(coord.y);
        }
        posList.setTextContent(sb.toString());
        return posList;
    }

    public static Element createMultiPolygon(Document doc, MultiPolygon multiPolygon) {
        if (multiPolygon.getNumGeometries() == 1) {
            return createPolygon(doc, (Polygon) multiPolygon.getGeometryN(0));
        }
        Element mpElement = doc.createElementNS(NS_GML, "MultiPolygon");
        for (int i=0 ; i<multiPolygon.getNumGeometries(); i++) {
            mpElement.appendChild(createPolygon(doc, (Polygon) multiPolygon.getGeometryN(i)));
        }
        return mpElement;
    }
    
    @Deprecated
    public static Element createBox(Document doc, Double[] bbox, int srsId) {
        Element box = doc.createElementNS(Gml.NS_GML_32, "Box");
        box.setAttribute("srsName", "urn:x-ogc:def:crs:EPSG:" + Integer.toString(srsId));
        box.appendChild(createCoord(doc, bbox[0], bbox[1]));
        box.appendChild(createCoord(doc, bbox[2], bbox[3]));
        return box;
    }
    
    public static Element createCoord(Document doc, Double x, Double y) {
        Element coord = doc.createElementNS(NS_GML, "coord");
        Element xElement = doc.createElementNS(NS_GML, "X");
        xElement.setTextContent(x.toString());
        coord.appendChild(xElement);
        Element yElement = doc.createElementNS(NS_GML, "Y");
        yElement.setTextContent(y.toString());
        coord.appendChild(yElement);
        return coord;
    }
}
