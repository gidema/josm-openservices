package org.openstreetmap.josm.plugins.openservices.crs;

import java.util.HashMap;
import java.util.Map;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.tools.I18n;
import org.openstreetmap.josm.tools.Pair;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.Polygon;

public abstract class AbstractCRSUtil {
    private static Map<String, CoordinateReferenceSystem> coordinateReferenceSystems = new HashMap<>();

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.openservices.crs.AbstractCRSUtil#getSrs(org.opengis.referencing.crs.CoordinateReferenceSystem)
     */
    public String getSrs(CoordinateReferenceSystem crs) {
        return getIdentifier(crs).toString();
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.openservices.crs.AbstractCRSUtil#getSrid(org.opengis.referencing.crs.CoordinateReferenceSystem)
     */
    public Integer getSrid(CoordinateReferenceSystem crs) {
        return Integer.valueOf(getIdentifier(crs).getCode());
    }

    private ReferenceIdentifier getIdentifier(
            CoordinateReferenceSystem crs) {
        return crs.getIdentifiers().iterator().next();
    }

    public abstract Geometry transform(SimpleFeature feature) throws CRSException;

    public abstract Polygon toPolygon(Bounds bounds);

    public abstract LineSegment toSegment(Pair<Node, Node> nodePair);

    public abstract Coordinate toCoordinate(Node node);

    public abstract Coordinate toCoordinate(LatLon latLon);

    public abstract Coordinate toCoordinate(EastNorth en);

    /**
     * Create a ReferencedEnvelope from a Josm bounds object, using the supplied CoordinateReferenceSystem
     * 
     * @param crs
     * @param bounds
     * @return
     * @throws TransformException 
     */
    public abstract ReferencedEnvelope createBoundingBox(CoordinateReferenceSystem crs,
            Bounds bounds) throws CRSException;

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openstreetmap.josm.plugins.openservices.crs.AbstractCRSUtil#getCrs
     * (java.lang.Long)
     */
    public CoordinateReferenceSystem getCrs(Long srid) throws CRSException {
        String srs = "EPSG:" + srid.toString();
        return getCrs(srs);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openstreetmap.josm.plugins.openservices.crs.AbstractCRSUtil#getCrs
     * (java.lang.String)
     */
    public CoordinateReferenceSystem getCrs(String srs) throws CRSException {
        CoordinateReferenceSystem crs = coordinateReferenceSystems.get(srs);
        try {
            if (crs == null) {
                crs = CRS.decode(srs);
                coordinateReferenceSystems.put(srs, crs);
            }
        } catch (NoSuchAuthorityCodeException e) {
            throw new CRSException(I18n.tr(
                    "The supplied coordinate reference system "
                            + "{0} is unknown", srs));
        } catch (FactoryException e) {
            throw new CRSException(I18n.tr("Unable to create a "
                    + "coordinate reference system for {0}", srs));
        }
        return crs;
    }
}