package org.openstreetmap.josm.plugins.ods.io;

import java.net.MalformedURLException;
import java.net.URL;

import org.openstreetmap.josm.io.OsmServerReader;
import org.openstreetmap.josm.plugins.ods.io.DownloadRequest;

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
     * @return The base url for the request, including the path if available
     * as an URL
     * @throws MalformedURLException 
     */
    public URL getHostUrl() throws MalformedURLException;
 
    /**
     * @return True is this host supports an non-rectangular bounding-box. False otherwise
     */
    public boolean supportsPolygon();
    
    /**
     * Create a ServerReader for the supplied request.
     * @param request
     * @return an OsmServerReader for the request
     */
    public OsmServerReader getServerReader(DownloadRequest request);
}
