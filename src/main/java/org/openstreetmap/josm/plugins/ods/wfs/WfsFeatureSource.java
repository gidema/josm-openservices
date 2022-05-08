package org.openstreetmap.josm.plugins.ods.wfs;

import java.util.List;

import javax.xml.namespace.QName;

import org.openstreetmap.josm.plugins.ods.saxparser.gml.AxisOrder;

public interface WfsFeatureSource {
    public WfsHost getHost();
    public QName getFeatureType();
    public QName getGeometryProperty();
    public List<String> getProperties();
    public List<String> getSortBy();
    
    public Boolean isRequired();
    public Integer getPageSize();
    public String getHttpMethod();
    public Long getSrid();
    public AxisOrder getAxisOrder();
}
