package org.openstreetmap.josm.plugins.ods.entities.external;

import java.util.List;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.DownloadTask;
import org.openstreetmap.josm.plugins.ods.OdsDataSource;

public interface ExternalDownloadTask extends DownloadTask {
    public OdsDataSource getDataSource();
    public List<SimpleFeature> getFeatures();
}
