package org.openstreetmap.josm.plugins.ods.tags;

import java.util.List;
import java.util.Map;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;
import org.openstreetmap.josm.plugins.ods.metadata.MetaDataException;
import org.openstreetmap.josm.plugins.ods.osm.OsmPrimitiveFactory;
import org.openstreetmap.josm.plugins.ods.saxparser.opengis.wfs.WfsFeature;

/**
 * A FeatureMapper maps GeoTools features to a Collection of Josm primitives.
 * 
 * @author Gertjan Idema
 * 
 */
public interface FeatureMapper {

    /**
     * Set the metaData context for this mapper
     * 
     * @param context
     * @throws MetaDataException
     */
    public void setContext(MetaData context) throws MetaDataException;

    /**
     * Get the name of the feature this mapper can handle.
     * 
     * @return The name of the feature
     */
    public String getFeatureName();

    /**
     * Create one ore more Josm primitives from the provided feature. Add them
     * to the dataSet and return them as a list.
     * 
     * @param feature
     * @param dataSet
     * @return
     */
    public List<OsmPrimitive> mapFeature(WfsFeature feature,
            OsmPrimitiveFactory builder);

    public Map<String, String> getKeys();
}
