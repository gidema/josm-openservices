package org.openstreetmap.josm.plugins.ods.tags;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.opengis.feature.Feature;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.PrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;
import org.openstreetmap.josm.plugins.ods.metadata.MetaDataException;

public class DefaultFeatureMapper implements FeatureMapper {
    private final List<TagBuilder> tagBuilders = new LinkedList<TagBuilder>();
    private String featureName;
    private GeometryMapper geometryMapper;

    @Override
    public void setContext(MetaData context) throws MetaDataException {
        for (TagBuilder tagBuilder : tagBuilders) {
            if (tagBuilder instanceof MetaTagBuilder) {
                ((MetaTagBuilder) tagBuilder).setContext(context);
            }
        }
    }

    @Override
    public final String getFeatureName() {
        return featureName;
    }

    public final void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public void addTagBuilder(TagBuilder tagBuilder) {
        tagBuilders.add(tagBuilder);
    }

    public void setGeometryMapper(GeometryMapper geometryMapper) {
        this.geometryMapper = geometryMapper;
    }

    @Override
    public List<OsmPrimitive> mapFeature(Feature feature,
            PrimitiveBuilder builder) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, String> getKeys() {
        // TODO Auto-generated method stub
        return null;
    }

    // @Override
    // public void setObjectFactory(JosmObjectFactory objectFactory) {
    // geometryMapper.setObjectFactory(objectFactory);
    // }

    // @Override
    // public List<OsmPrimitive> mapFeature(Feature feature, DataSet dataSet) {
    // Map<String, String> tags = new HashMap<String, String>();
    // for (TagBuilder tagBuilder : tagBuilders) {
    // tagBuilder.createTag(tags, feature);
    // }
    // Geometry geometry = (Geometry)
    // feature.getDefaultGeometryProperty().getValue();
    // return geometryMapper.createPrimitives(geometry, tags, dataSet);
    // }

}