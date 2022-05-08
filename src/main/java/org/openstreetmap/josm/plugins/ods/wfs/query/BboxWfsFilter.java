package org.openstreetmap.josm.plugins.ods.wfs.query;

import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.plugins.ods.jts.Boundary;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class BboxWfsFilter implements OdsQueryFilter {
    private final Boundary boundary;
    private final Long srid;
    
    public BboxWfsFilter(Boundary boundary, Long srid) {
        this.boundary = boundary;
        this.srid = srid;
    }
    
    @Override
    public String getQueryParameterName() {
        return "BBOX";
    }
    
    @Override
    public Element buildXmlElement(Document doc) {
        throw new UnsupportedOperationException("A bbox filter can't be converted to a fes filter.");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Bounds bounds = boundary.getBounds().iterator().next();
        sb.append(bounds.getMinLon()).append(',')
        .append(bounds.getMinLat()).append(',')
        .append(bounds.getMaxLon()).append(',')
        .append(bounds.getMaxLat())
        .append(",EPSG:").append(srid);
        return sb.toString();
    }
}
