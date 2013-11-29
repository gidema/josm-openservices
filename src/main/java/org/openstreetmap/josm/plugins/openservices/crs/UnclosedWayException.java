package org.openstreetmap.josm.plugins.openservices.crs;

import org.openstreetmap.josm.data.osm.Way;

public class UnclosedWayException extends Exception {
    private Way way;
    
    /**
     * 
     */
    private static final long serialVersionUID = 7323107196151526925L;

    public UnclosedWayException(Way way) {
        this.way = way;
    }

    public Way getWay() {
        return way;
    }
}
