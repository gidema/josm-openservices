package org.openstreetmap.josm.plugins.ods.entities.external;

import java.util.List;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.DownloadTask;
import org.openstreetmap.josm.plugins.ods.OdsDataSource;
import org.openstreetmap.josm.plugins.ods.jts.Boundary;

public interface ExternalDownloadTask extends DownloadTask {
    public OdsDataSource getDataSource();
    public void setBoundary(Boundary boundary);
    public List<SimpleFeature> getFeatures();
}
