package org.openstreetmap.josm.plugins.ods.entities;

import javax.xml.namespace.QName;

import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;
import org.openstreetmap.josm.plugins.ods.saxparser.opengis.wfs.WfsFeature;

/**
 * Create OdEntity objects from WfsFeature objects.
 * 
 * @author Gertjan
 *
 * @param <T extends OdEntity>
 */
public interface OdEntityFactory {
    public boolean appliesTo(QName featureType);
    public void process(WfsFeature feature, DownloadResponse response);
}
