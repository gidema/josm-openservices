package org.openstreetmap.josm.plugins.ods.wfs.query;

import javax.xml.namespace.QName;

import org.openstreetmap.josm.data.Bounds;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class BboxWfsFilter implements OdsQueryFilter {
    private final QName geometryProperty;
    private final Bounds bounds;
    private final Long srid;
    
    // TODO use JTS Envelope in stead of OSM Bounds for the bounding box.
    // Bounds is intended for LatLon coordinates, but we use XY coordinates here
    public BboxWfsFilter(QName geometryProperty, Bounds bounds, Long srid) {
        this.geometryProperty = geometryProperty;
        this.bounds = bounds;
        this.srid = srid;
    }
    
    @Override
    public String getQueryParameterName() {
        return "BBOX";
    }
    
    @Override
    public Element buildXmlElement(Document doc) {
        Element filter = doc.createElement("fes:Filter");
        Element bbox = doc.createElement("BBOX");
        filter.appendChild(bbox);
        Element propertyName = doc.createElement("propertyName");
        propertyName.setTextContent(geometryProperty.getPrefix() + ":" + geometryProperty.getLocalPart());
        bbox.appendChild(propertyName);
        Element gmlBbox = doc.createElement("gml:Box");
        gmlBbox.setAttribute("srsName", String.format("urn:x-ogc:def:crs:EPSG:%d", srid));
        bbox.appendChild(gmlBbox);
        Element coord = createCoordElement(doc, bounds.getMinLon(), bounds.getMinLat());
        bbox.appendChild(coord);
        coord = createCoordElement(doc, bounds.getMaxLon(), bounds.getMaxLat());
        bbox.appendChild(coord);
        return filter;
    }

    private static Element createCoordElement(Document doc, Double x, Double y) {
        Element coord = doc.createElement("gml:coord");
        Element xElement = doc.createElement("gml:X");
        xElement.setTextContent(x.toString());
        coord.appendChild(xElement);
        Element yElement = doc.createElement("gml:Y");
        yElement.setTextContent(y.toString());
        coord.appendChild(yElement);
        return coord;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(bounds.getMinLon()).append(',')
        .append(bounds.getMinLat()).append(',')
        .append(bounds.getMaxLon()).append(',')
        .append(bounds.getMaxLat())
        .append(",EPSG:").append(srid);
        return sb.toString();
    }
}
