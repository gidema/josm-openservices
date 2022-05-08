package org.openstreetmap.josm.plugins.ods.wfs;

import javax.xml.namespace.QName;

import org.openstreetmap.josm.plugins.ods.Host;
import org.openstreetmap.josm.plugins.ods.saxparser.gml.AxisOrder;

public interface WfsHost extends Host {

    public String getXmlNs();

    public String getXmlPrepix();

    public Integer getPageSize();

    public String getHttpMethod();

    public QName getGeometryProperty();

    public Long getSrid();

    public AxisOrder getAxisOrder();
}
