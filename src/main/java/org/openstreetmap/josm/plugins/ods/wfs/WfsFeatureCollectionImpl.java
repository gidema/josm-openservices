package org.openstreetmap.josm.plugins.ods.wfs;

import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.saxparser.opengis.wfs.WfsFeature;

public class WfsFeatureCollectionImpl implements WfsFeatureCollection {
    private final Map<String, WfsFeature> features = new HashMap<>();
    private Instant timeStamp;
    private Integer matched;
    private Integer returned;

    public WfsFeatureCollectionImpl(Instant timeStamp, Integer matched,
            Integer returned) {
        super();
        this.timeStamp = timeStamp;
        this.matched = matched;
        this.returned = returned;
    }

    public WfsFeatureCollectionImpl() {
        this(Instant.now(), 0, 0);
    }

    public void add(WfsFeature feature) {
        features.put(feature.getFeatureId(), feature);
    }

    @Override
    public int getFeatureCount() {
        return features.size();
    }
    
    @Override
    public Iterator<WfsFeature> iterator() {
        return features.values().iterator();
    }

    @Override
    public Instant getTimeStamp() {
        return timeStamp;
    }

    @Override
    public Integer getNumberMatched() {
        return matched;
    }

    @Override
    public Integer getNumberReturned() {
        return returned;
    }
    
    @Override
    public WfsFeatureCollection transform(CRSUtil crsUtil, Long targetSrid) {
        WfsFeatureCollectionImpl wfc = new WfsFeatureCollectionImpl(timeStamp, matched, returned);
        this.forEach(feature -> {
            wfc.add(feature.transform(crsUtil, targetSrid));
        });
        return wfc;
    }

    @Override
    public void clear() {
        this.features.clear();
        this.timeStamp = null;
        this.matched = null;
        this.returned = 0;
    }
}
