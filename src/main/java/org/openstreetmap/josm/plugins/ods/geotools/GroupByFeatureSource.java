package org.openstreetmap.josm.plugins.ods.geotools;

import java.awt.RenderingHints.Key;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Set;

import org.geotools.data.DataAccess;
import org.geotools.data.FeatureListener;
import org.geotools.data.Query;
import org.geotools.data.QueryCapabilities;
import org.geotools.data.ResourceInfo;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;

public class GroupByFeatureSource implements SimpleFeatureSource {
    private final SimpleFeatureSource wrappedSource;
    private final SimpleFeatureType featureType;
    private final LinkedList<FeatureListener> listeners = new LinkedList<>();
    private final GroupByQuery query;
//    private final String newKey;
//    private int idIndex;
//    private int[] attributeMapping;
    
    public GroupByFeatureSource(Name newName, SimpleFeatureSource wrappedSource, GroupByQuery query) {
        super();
        this.query = query;
        this.wrappedSource = wrappedSource;
//        this.newKey = newKey;
        // TODO use Hints to retrieve FeatureTypeFactory
        this.featureType = wrappedSource.getSchema();
//        wrappedSource.getSchema().getDescriptor(newKey);
    }

    @Override
    public Name getName() {
        return wrappedSource.getName();
    }

    @Override
    public ResourceInfo getInfo() {
        return wrappedSource.getInfo();
    }

    @Override
    public DataAccess<SimpleFeatureType, SimpleFeature> getDataStore() {
        return wrappedSource.getDataStore();
    }

    @Override
    public QueryCapabilities getQueryCapabilities() {
        return wrappedSource.getQueryCapabilities();
    }

    @Override
    public void addFeatureListener(FeatureListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeFeatureListener(FeatureListener listener) {
        listeners.remove(listener);
    }

    @Override
    public SimpleFeatureType getSchema() {
        return featureType;
    }

    @Override
    public ReferencedEnvelope getBounds() throws IOException {
        return wrappedSource.getBounds();
    }

    @Override
    public ReferencedEnvelope getBounds(Query query) throws IOException {
        return wrappedSource.getBounds();
    }

    @Override
    public int getCount(Query query) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Key> getSupportedHints() {
        return wrappedSource.getSupportedHints();
    }

    @Override
    public SimpleFeatureCollection getFeatures() throws IOException {
        SimpleFeatureCollection oldFeatures = wrappedSource.getFeatures();
        return getFeatures(oldFeatures);
    }

    @Override
    public SimpleFeatureCollection getFeatures(Filter filter) throws IOException {
        SimpleFeatureCollection oldFeatures = wrappedSource.getFeatures(filter);
        return getFeatures(oldFeatures);
    }

    @Override
    public SimpleFeatureCollection getFeatures(Query query) throws IOException {
        SimpleFeatureCollection oldFeatures = wrappedSource.getFeatures(query);
        return getFeatures(oldFeatures);
    }

    private SimpleFeatureCollection getFeatures(
            SimpleFeatureCollection wrappedFeatures) {
        return new GroupByFeatureCollectionWrapper(wrappedFeatures, query);
    }
    
    
//    private SimpleFeatureCollection getFeatures(SimpleFeatureCollection oldFeatures) {
//        DefaultFeatureCollection newFeatures = new DefaultFeatureCollection(oldFeatures.getID(), featureType);
//        SimpleFeatureIterator it = oldFeatures.features();
//        while (it.hasNext()) {
//            SimpleFeature oldFeature = it.next();
//            List<Object> values = new ArrayList<>(attributeMapping.length);
//            for (int i = 0; i < attributeMapping.length; i++) {
//                values.add(oldFeature.getAttribute(i));
//            }
//            FeatureId featureId = new FeatureIdImpl(oldFeature.getAttribute(idIndex).toString());
//            SimpleFeature newFeature = new SimpleFeatureImpl(values, featureType, featureId);
//            newFeatures.add(newFeature);
//        }
//        return newFeatures;
//    }

//    private SimpleFeatureType createFeatureType(Name newName, SimpleFeatureSource wrappedSource, GroupByQuery query) {
//        FeatureTypeFactory ftf = new FeatureTypeFactoryImpl();
//        SimpleFeatureType oldType = wrappedSource.getSchema();
////        List<PropertyDescriptor> newSchema = new ArrayList<>(attributes.size());
////        attributeMapping = new int[attributes.size()];
////        for (int i = 0; i < attributes.size(); i++) {
////            String name = attributes.get(i);
////            int index = oldType.indexOf(name);
////            assert index != -1;
////            attributeMapping[i] = index;
////            newSchema.add(oldType.getDescriptor(index));
////        }
//        idIndex = oldType.indexOf(newKey);
//        return ftf.createFeatureType(newName, newSchema, oldType.getGeometryDescriptor(),
//            oldType.isAbstract(), oldType.getRestrictions(),
//            null, oldType.getDescription());
//    }
}
