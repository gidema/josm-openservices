package org.openstreetmap.josm.plugins.ods.entities.external;

import java.util.List;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.DownloadTask;
import org.openstreetmap.josm.plugins.ods.OdsDataSource;
import org.openstreetmap.josm.plugins.ods.entities.BuildException;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.EntityFactory;
import org.openstreetmap.josm.plugins.ods.entities.EntityStore;

public interface ExternalDownloadTask extends DownloadTask {
    public OdsDataSource getDataSource();
    public List<SimpleFeature> getFeatures();
//    public List<Entity> buildEntities(EntityFactory entityFactory) throws BuildException;
//    public EntityStore getEntityStore();
}
