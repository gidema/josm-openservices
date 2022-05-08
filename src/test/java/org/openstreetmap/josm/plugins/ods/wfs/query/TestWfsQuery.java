package org.openstreetmap.josm.plugins.ods.wfs.query;

import java.util.Collections;

import javax.xml.namespace.QName;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.openstreetmap.josm.plugins.ods.opengis.fes.EqualsPredicate;
import org.openstreetmap.josm.plugins.ods.opengis.fes.FesFilter;
import org.openstreetmap.josm.plugins.ods.opengis.fes.FilterPredicate;
import org.openstreetmap.josm.plugins.ods.opengis.fes.IntersectsPredicate;
import org.openstreetmap.josm.plugins.ods.opengis.fes.OrPredicate;

public class TestWfsQuery {
    private final static String NS_BAG = "http://bag.geonovum.nl";
    
    //@Test
    public void testRequest10pander() {
        QName feature = new QName(NS_BAG, "pand", "bag");
        WfsQuery query = new WfsQuery(feature, null, 4326L, null);
        WfsRequest request = new WfsRequest("https://service.pdok.nl/lv/bag/wfs/v2_0", query, 0, 10, Collections.singletonList("identificatie"));
        System.out.print(request.getPostData());
    }

    //@Test
    public void testRequest2pandenByIdentificatie() {
        QName feature = new QName(NS_BAG, "pand", "bag");
        QName identificatie = new QName(NS_BAG, "identificatie");
        FilterPredicate predicate = OrPredicate.of(
                new EqualsPredicate(identificatie, "0344100000012848"),
                new EqualsPredicate(identificatie, "0344100000023210"));
        FesFilter filter = new FesFilter(predicate);
        WfsQuery query = new WfsQuery(feature, filter, 4326L, null);
        WfsRequest request = new WfsRequest("https://service.pdok.nl/lv/bag/wfs/v2_0", query, 0, 10, Collections.singletonList("identificatie"));
        System.out.print(request.getPostData());
    }

    @Test
    public void testRequestVboByIdentificatie() {
        QName feature = new QName(NS_BAG, "verblijfsobject", "bag");
        QName identificatie = new QName(NS_BAG, "identificatie");
        FilterPredicate predicate = OrPredicate.of(
                new EqualsPredicate(identificatie, "0344010000164141"),
                new EqualsPredicate(identificatie, "0344010000164137"));
        FesFilter filter = new FesFilter(predicate);
        WfsQuery query = new WfsQuery(feature, filter, 4326L, null);
        WfsRequest request = new WfsRequest("https://service.pdok.nl/lv/bag/wfs/v2_0", query, 0, 10, Collections.singletonList("identificatie"));
        System.out.print(request.getPostData());
    }

    //@Test
    public void testRequestWoonplaatsIdentificatie() {
        QName feature = new QName(NS_BAG, "woonplaats", "bag");
        QName identificatie = new QName(NS_BAG, "identificatie");
        FilterPredicate predicate = OrPredicate.of(
                new EqualsPredicate(identificatie, "3594"));
        FesFilter filter = new FesFilter(predicate);
        WfsQuery query = new WfsQuery(feature, filter, 4326L, null);
        WfsRequest request = new WfsRequest("https://service.pdok.nl/lv/bag/wfs/v2_0", query, 0, 10, Collections.singletonList("identificatie"));
        System.out.print(request.getPostData());
    }

    //@Test
    public void testRequestPandenByIntersection28992() {
        QName feature = new QName(NS_BAG, "pand", "bag");
        QName geom = new QName(NS_BAG, "geom");
        Polygon boundary = buildPolygon(136300.0, 455300.0, 136600.0, 455500.0);
        FilterPredicate predicate = new IntersectsPredicate(geom, boundary);
        FesFilter filter = new FesFilter(predicate);
        WfsQuery query = new WfsQuery(feature, filter, 28992L, null);
        WfsRequest request = new WfsRequest("https://service.pdok.nl/lv/bag/wfs/v2_0", query, 0, 10, Collections.singletonList("identificatie"));
        System.out.print(request.getPostData());
    }

//    @Test
    public void testRequestPandenByIntersection4326() {
        QName feature = new QName(NS_BAG, "pand", "bag");
        QName geom = new QName(NS_BAG, "geom");
        Polygon boundary = buildPolygon(2.527125, 50.212863, 7.374026, 55.721160);
        FilterPredicate predicate = new IntersectsPredicate(geom, boundary);
        FesFilter filter = new FesFilter(predicate);
        WfsQuery query = new WfsQuery(feature, filter, 4326L, null);
        WfsRequest request = new WfsRequest("https://service.pdok.nl/lv/bag/wfs/v2_0", query, 0, 10, Collections.singletonList("identificatie"));
        System.out.print(request.getPostData());
    }

    private static Polygon buildPolygon(Double minX, Double minY, Double maxX, Double maxY) {
        GeometryFactory geometryFactory = new GeometryFactory();
        Coordinate[] coordinates = new Coordinate[5];
        coordinates[0] = new Coordinate(minX, minY);
        coordinates[1] = new Coordinate(minX, maxY);
        coordinates[2] = new Coordinate(maxX, maxY);
        coordinates[3] = new Coordinate(maxX, minY);
        coordinates[4] = new Coordinate(minX, minY);
        return geometryFactory.createPolygon(coordinates);
    }
}
