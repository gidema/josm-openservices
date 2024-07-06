package org.openstreetmap.josm.plugins.ods.osm.alignment;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.openstreetmap.josm.data.osm.BBox;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.ods.osm.BufferOps;

/**
 * Find adjacent ways within DWithin distance of one or more given other ways. 
 * 
 * @author gertjan
 *
 */
public class AdjacentWayFinder {
    private final DataSet dataSet;
    private final BufferOps dWithin;
    
    public AdjacentWayFinder(DataSet dataSet, BufferOps dWithin) {
        super();
        this.dataSet = dataSet;
        this.dWithin = dWithin;
    }

    public Set<Way> find(Collection<Way> ways) {
        Set<Way> candidates = new HashSet<>();
        ways.forEach(way -> {
            BBox bbox = dWithin.getBBox(way);
            candidates.addAll(dataSet.searchWays(bbox));
        });
        candidates.removeAll(ways);
        return candidates;
    }
}
