package org.openstreetmap.josm.plugins.ods.jts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.RelationMember;
import org.openstreetmap.josm.tools.I18n;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.operation.polygonize.Polygonizer;

public class MultiPolygonBuilder {
    private final GeoUtil geoUtil;
    private final Relation relation;
//    private final List<Way> outerWays = new LinkedList<Way>();
//    private final List<Way> innerWays = new LinkedList<Way>();
//    private final List<List<Node>> nonClosedWays = new ArrayList<List<Node>>();
//    private final List<RelationMember> otherMembers = new LinkedList<RelationMember>();
    private final List<Node> memberNodes = new LinkedList<Node>();
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
                    Main.warn(I18n.tr("Invalid role '{0}' in building relation {1}", member.getRole(), relation.getUniqueId()));
                }
                break;
            case RELATION:
                Main.warn(I18n.tr("The relation for building {0} contains a member relation", relation.getUniqueId()));
                break;
            default:
                break;
            }
        }
        @SuppressWarnings("unchecked")
        Collection<Polygon> outerPolygons = outerPolygonizer.getPolygons();
        if (outerPolygons.size() == 0) {
            Main.warn(I18n.tr("The relation for building {0} contains no closed outer ring.", relation.getUniqueId()));
            return;
        }
        if (outerPolygons.size() > 1) {
            Main.warn(I18n.tr("The relation for building {0} contains more than one closed outer rings.", relation.getUniqueId()));
            return;
        }
        @SuppressWarnings("unchecked")
        Collection<Polygon> innerPolygons = innerPolygonizer.getPolygons();
        LinearRing outerRing = (LinearRing) outerPolygons.iterator().next().getExteriorRing();
        List<LinearRing> innerRings = new ArrayList<>(innerPolygons.size());
        for (Polygon innerPolygon : innerPolygons) {
            innerRings.add((LinearRing) innerPolygon.getExteriorRing());
        }
        geometry = geoUtil.createPolygon(outerRing, innerRings);
    }
     
    public boolean isInComplete() {
        return relation.isIncomplete();
    }
    

    public Geometry getGeometry() {
        return geometry;
    }
    
    public List<Node> getMemberNodes() {
        return memberNodes;
    }
}
