package org.openstreetmap.josm.plugins.ods.io;

import java.net.MalformedURLException;

import org.openstreetmap.josm.io.OsmServerReader;

/**
 * A host that can handle a request for OSM data;
 *  
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public interface OsmHost {
    /**
     * @return The base url for the request, including the path if available
     * as a String
     */
    public String getHostString();

    /**
     * @return True is this host supports an non-rectangular bounding-box. False otherwise
     */
    public boolean supportsPolygon();
    
    /**
     * Create a ServerReader for the supplied request.
     * @param request
     * @return an OsmServerReader for the request
     * @throws MalformedURLException 
     */
    public OsmServerReader getServerReader(DownloadRequest request) throws MalformedURLException;
}
