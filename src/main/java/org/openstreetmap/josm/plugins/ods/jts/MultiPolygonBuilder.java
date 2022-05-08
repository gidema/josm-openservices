package org.openstreetmap.josm.plugins.ods.jts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.RelationMember;
import org.openstreetmap.josm.tools.I18n;
import org.openstreetmap.josm.tools.Logging;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.operation.polygonize.Polygonizer;

public class MultiPolygonBuilder {
    private final GeoUtil geoUtil;
    private final Relation relation;
//    private final List<Way> outerWays = new LinkedList<Way>();
//    private final List<Way> innerWays = new LinkedList<Way>();
//    private final List<List<Node>> nonClosedWays = new ArrayList<List<Node>>();
//    private final List<RelationMember> otherMembers = new LinkedList<RelationMember>();
    private final List<Node> memberNodes = new LinkedList<>();
//    private JosmIssue issue;
    private Geometry geometry;

    public MultiPolygonBuilder(GeoUtil geoUtil, Relation relation) {
        this.geoUtil = geoUtil;
        this.relation = relation;
    }
    
//    public void build() throws InvalidMultiPolygonException {
//        build1();
//    }
    
//    private void build1() throws InvalidMultiPolygonException {
//        if (relation.isIncomplete()) {
//            return;
//        }
//        if (!relation.isMultipolygon()) {
//            throw new InvalidMultiPolygonException(relation, 
//                 "");
//        }
//        analyzeMembers();
//        if (issue != null) return;
//        buildGeometry();
//    }
    
    /**
     * Build a (Multi)Polyon using the JTS Polygonizer class and a list of member nodes
     */
    public void build() {
        Polygonizer outerPolygonizer = new Polygonizer();
        Polygonizer innerPolygonizer = new Polygonizer();
        for (RelationMember member : relation.getMembers()) {
            switch (member.getType()) {
            case NODE:
                memberNodes.add(member.getNode());
                break;
            case WAY:
                switch (member.getRole()) {
                case "inner":
                    innerPolygonizer.add(geoUtil.toLineString(member.getWay()));
                    break;
                case "outer":
                    outerPolygonizer.add(geoUtil.toLineString(member.getWay()));
                    break;
                default:
                    Logging.warn(I18n.tr("Invalid role '{0}' in building relation {1}", member.getRole(), relation.getUniqueId()));
                }
                break;
            case RELATION:
                Logging.warn(I18n.tr("The relation for building {0} contains a member relation", relation.getUniqueId()));
                break;
            default:
                break;
            }
        }
        @SuppressWarnings("unchecked")
        Collection<Polygon> outerPolygons = outerPolygonizer.getPolygons();
        @SuppressWarnings("unchecked")
        Collection<Polygon> innerPolygons = innerPolygonizer.getPolygons();
        
        switch (outerPolygons.size()) {
        case 0:
            Logging.warn(I18n.tr("The relation for building {0} contains no closed outer ring.", relation.getUniqueId()));
            geometry = null;
            break;
        case 1:
            LinearRing outerRing = outerPolygons.iterator().next().getExteriorRing();
            List<LinearRing> innerRings = outerPolygons.stream().map(Polygon::getExteriorRing).collect(Collectors.toList());
            geometry = geoUtil.createPolygon(outerRing, innerRings);
            break;
        default:
            geometry = createMultiPolyon(outerPolygons, innerPolygons);
            break;
        }
    }

    
    private Geometry createMultiPolyon(Collection<Polygon> outerPolygons,
            Collection<Polygon> innerPolygons) {
        List<Polygon> polygons = new ArrayList<>(outerPolygons.size());
        outerPolygons.forEach(outerPolygon -> {
            polygons.add(createPolygon(outerPolygon, innerPolygons));
        });
        return geoUtil.createMultiPolygon(polygons);
    }

    private Polygon createPolygon(Polygon outerPolygon,
            Collection<Polygon> innerPolygons) {
        List<LinearRing> innerRings = innerPolygons.stream().filter(outerPolygon::covers).map(Polygon::getExteriorRing).collect(Collectors.toList());
        return geoUtil.createPolygon(outerPolygon.getExteriorRing(), innerRings);
    }

    public Geometry getGeometry() {
        return geometry;
    }
    
    public List<Node> getMemberNodes() {
        return memberNodes;
    }
}
