package org.openstreetmap.josm.plugins.ods;

import javax.xml.namespace.QName;

import org.openstreetmap.josm.plugins.ods.metadata.MetaData;
import org.openstreetmap.josm.plugins.ods.saxparser.gml.AxisOrder;

/**
 * 
 * @author Gertjan Idema
 * 
 */
public interface OdsFeatureSource {

    public void initialize() throws InitializationException;

    public Host getHost();
    
    public QName getFeatureType();
    
    public MetaData getMetaData();

    public QName getGeometryProperty();

    public AxisOrder getAxisOrder();
}
