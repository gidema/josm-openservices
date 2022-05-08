package org.openstreetmap.josm.plugins.ods.wfs;

import java.time.Instant;

import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.saxparser.opengis.wfs.WfsFeature;

public interface WfsFeatureCollection extends Iterable<WfsFeature> {
    /**
     * Add a new feature to this feature collection
     * 
     * @param feature
     */
//    public void add(WfsFeature feature);

    /**
     * Get the actual number of features in the collection
     * 
     * @return
     */
    public int getFeatureCount();

    /**
     * Set the time stamp for the creation moment of this feature collection
     * 
     * @param timeStamp
     */
//    public void setTimeStamp(LocalDateTime timeStamp);

    /**
     * Set the reported number of matched features. Null if unknown
     * 
     * @param valueOf
     */
//    public void setNumberMatched(Integer numberMatched);

    /**
     * Set the reported number of returned features.
     * 
     * @param valueOf
     */
//    public void setNumberReturned(Integer numberReturned);
    
    /**
     * Get the time stamp for the creation moment of this feature collection
     * 
     * @return
     */
    public Instant getTimeStamp();

    /**
     * Get the reported number of matched features. Null if unknown
     *
     * @return
     */
    public Integer getNumberMatched();

    /**
     * Get the reported number of returned features. This should normally match the feature count
     * 
     * @return
     */
    public Integer getNumberReturned();

    public void clear();

    /**
     * Transform all features in this collection to another coordinateReference system
     * 
     * @param crsUtil
     * @param osmSrid
     * @return
     */
    public WfsFeatureCollection transform(CRSUtil crsUtil, Long osmSrid);
}
