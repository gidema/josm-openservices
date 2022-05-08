package org.openstreetmap.josm.plugins.ods.wfs;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.saxparser.opengis.wfs.WfsFeature;
import org.openstreetmap.josm.plugins.ods.wfs.query.WfsRequest;

public class PagingWfsFeatureReader implements WfsFeatureReader {

    private final OdsContext context;
    private final WfsFeatureSource featureSource;
    private final WfsRequest baseRequest;
    private final int pageSize;
    private final List<String> sortBy;
    private final CombinedWfsFeatureCollection featureCollection = new CombinedWfsFeatureCollection();

    public PagingWfsFeatureReader(WfsFeatureSource featureSource, WfsRequest wfsRequest, OdsContext context) {
        super();
        this.context = context;
        this.featureSource = featureSource;
        this.baseRequest = wfsRequest;
        this.pageSize = featureSource.getPageSize();
        this.sortBy = featureSource.getSortBy();
    }

    @Override
    public WfsFeatureCollection read() throws IOException {
        int startIndex = 0;
        int featureCount = readPage(startIndex);
        // TODO We might use the 'next' parameter from the WFS response here.
        while (featureCount == pageSize) {
            startIndex += pageSize;
            featureCount = readPage(startIndex);
        }
        return featureCollection;
    }
    
    private int readPage(Integer startIndex) throws IOException {
        WfsRequest currentRequest = new WfsRequest(baseRequest.getUrl(), baseRequest.getWfsQuery(), startIndex, pageSize, sortBy);
        WfsPageReader pageReader = new DefaultWFSPageReader(featureSource, context);
        WfsFeatureCollection wfc = pageReader.read(currentRequest);
        featureCollection.addFeatureCollection(wfc);
        return wfc.getFeatureCount();
    }
    
    class CombinedWfsFeatureCollection implements WfsFeatureCollection {
        List<WfsFeatureCollection> featureCollections = new ArrayList<>();
        private Integer numberMatched = null;
        private Integer numberReturned = 0;
        private Integer featureCount = 0;
        private Instant timeStamp;

        public CombinedWfsFeatureCollection() {
            // TODO Auto-generated constructor stub
        }

        public void addFeatureCollection(WfsFeatureCollection wfc) {
            this.numberReturned += wfc.getNumberReturned();
            if (wfc.getNumberMatched() != null) numberMatched = wfc.getNumberMatched();
            this.featureCollections.add(wfc);
            this.featureCount += wfc.getFeatureCount();
            this.timeStamp = wfc.getTimeStamp();
        }
        
        @Override
        public Iterator<WfsFeature> iterator() {
            return new FeatureIterator();
        }

        @Override
        public int getFeatureCount() {
            return featureCount;
        }

        @Override
        public Instant getTimeStamp() {
            return timeStamp;
        }

        @Override
        public Integer getNumberMatched() {
            return numberMatched;
        }

        @Override
        public Integer getNumberReturned() {
            return numberReturned;
        }
        
        private class FeatureIterator implements Iterator<WfsFeature> {
            private int currentCollection = 0;
            private Iterator<WfsFeature> currentIterator;
            
            FeatureIterator() {
                if (featureCollections.size() == 0) {
                    currentIterator = Collections.emptyListIterator();
                }
                else {
                    currentIterator = featureCollections.get(0).iterator();
                }
            }
            
            @Override
            public boolean hasNext() {
                if (currentIterator.hasNext()) return true;
                currentCollection += 1;
                if (currentCollection >= featureCollections.size()) {
                    return false;
                }
                currentIterator = featureCollections.get(currentCollection).iterator();
                return currentIterator.hasNext();
            }

            @Override
            public WfsFeature next() {
                return currentIterator.next();
            }
        }

        @Override
        public void clear() {
            this.featureCollections.clear();
            this.featureCount = 0;
            this.numberMatched = null;
            this.numberReturned =0;
        }

        @Override
        public WfsFeatureCollection transform(CRSUtil crsUtil, Long srid) {
            CombinedWfsFeatureCollection transformedCollections = new CombinedWfsFeatureCollection();
            featureCollections.forEach(fc -> {
                transformedCollections.addFeatureCollection(fc.transform(crsUtil, srid));
            });
            return transformedCollections;
        }
    }
}
