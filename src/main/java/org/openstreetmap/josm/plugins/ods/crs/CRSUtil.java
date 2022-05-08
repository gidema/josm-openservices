package org.openstreetmap.josm.plugins.ods.crs;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;

public abstract class CRSUtil {
    public final static Long OSM_SRID = 4326L;
    protected final static PrecisionModel OSM_PRECISION_MODEL = new PrecisionModel();
//    protected final static PrecisionModel OSM_PRECISION_MODEL = new PrecisionModel(
//            10000000);
    protected final static GeometryFactory OSM_GEOMETRY_FACTORY = new GeometryFactory(
            OSM_PRECISION_MODEL, OSM_SRID.intValue());

    public abstract Geometry toOsm(Geometry geometry, Long srid)
            throws CRSException;

    public abstract Geometry fromOsm(Geometry geometry, Long srid)
            throws CRSException;
    
    public abstract Geometry transform(Geometry geometry, Long targetSrid);

}