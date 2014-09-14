package org.openstreetmap.josm.plugins.ods.jts;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.tools.I18n;

import com.vividsolutions.jts.geom.Geometry;
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
     * Build a (Multi)Polyon using the JTS Polygonizer class
     */
    public void build() {
        Polygonizer polygonizer = new Polygonizer();
        for (OsmPrimitive primitive : relation.getMemberPrimitivesList()) {
            switch (primitive.getType()) {
            case NODE:
                memberNodes.add((Node) primitive);
                break;
            case WAY:
                polygonizer.add(geoUtil.toLineString((Way) primitive));
                break;
            case RELATION:
                Main.warn(I18n.tr("Relation contains member relation"));
                break;
            default:
                break;
            }
        }
        @SuppressWarnings("unchecked")
        Collection<Polygon> polygons = polygonizer.getPolygons();
        if (polygons.size() == 1) {
            geometry = polygons.iterator().next();
        } else {
            geometry = geoUtil.createMultiPolygon(polygons);
        }
    }
//    private void build2() {
//        Multipolygon mpg = MultipolygonCache.getInstance().get(Main.map.mapView, relation);
//        List<PolyData> polygons = mpg.getCombinedPolygons();
//        if (polygons.isEmpty()) {
//            Main.warn(I18n.tr("Empty multipolygon found"));
//            return;
//        }
//        geometry = geoUtil.buildGeometry(polygons);
//    }
    
//    private void analyzeMembers() {
//        for (RelationMember member : relation.getMembers()) {
//            if ("outer".equals(member.getRole())) {
//                if (member.isWay()) {
//                    outerWays.add(member.getWay());
//                }
//                else {
//                    issue =new InvalidMultiPolygonIssue(relation,
//                        "One or more outer relation members are not ways");
//                    return;
//                }
//            }
//            else if ("inner".equals(member.getRole())) {
//                if (member.isWay()) {
//                    innerWays.add(member.getWay());
//                }
//                else {
//                    issue = new InvalidMultiPolygonIssue(relation,
//                        "One or more inner relation members are not ways");
//                    return;
//                }
//            }
//            else {
//                otherMembers.add(member);
//            }
//        }
//    }
     
    public boolean isInComplete() {
        return relation.isIncomplete();
    }
    
//    public boolean hasIssue() {
//        return issue != null;
//    }
//    
//    public JosmIssue getIssue() {
//        return issue;
//    }
    
//    public boolean hasOtherMembers() {
//        return !otherMembers.isEmpty();
//    }
//
//    public List<RelationMember> getOtherMembers() {
//        return otherMembers;
//    }

//    private void buildGeometry() throws IllegalArgumentException {
//        List<Polygon> holes = new LinkedList<Polygon>();
//        for (Way way : innerWays) {
//            holes.add(geoUtil.toPolygon(way));
//        }
//        List<Polygon> polygons = new ArrayList<Polygon>(outerWays.size());
//        for (Way way : outerWays) {
//            if (way.isClosed()) {
//                Polygon shell = geoUtil.toPolygon(way);
//                List<LinearRing> innerRings = new LinkedList<LinearRing>();
//                Iterator<Polygon> it = holes.iterator();
//                while (it.hasNext()) {
//                    Polygon hole = it.next();
//                    if (shell.covers(hole)) {
//                        innerRings.add((LinearRing) hole.getBoundary());
//                        it.remove();
//                    }
//                }
//                polygons.add(geoUtil.createPolygon((LinearRing) shell.getBoundary(), innerRings));
//            }
//        }
//        if (holes.size() > 0) {
//            throw new IllegalArgumentException(
//                "One or more of the inner rings of this relation don't fall " +
//                "within one of the outer rings.");
//            
//        }
//        if (polygons.size() == 1) {
//            geometry = polygons.get(0);
//        }
//        geometry = geoUtil.createMultiPolygon(polygons);
//    }

    public Geometry getGeometry() {
        return geometry;
    }
    
    public List<Node> getMemberNodes() {
        return memberNodes;
    }

//    private List<List<Node>> joinWays(Collection<Way> ways) {
//        List<List<Node>> result = new ArrayList<List<Node>>();
//        List<Way> waysToJoin = new ArrayList<Way>();
//        for (Way way : ways) {
//            if (way.isClosed()) {
//                result.add(way.getNodes());
//            } else {
//                waysToJoin.add(way);
//            }
//        }
//
//        for (JoinedWay jw : Multipolygon.joinWays(waysToJoin)) {
//            if (!jw.isClosed()) {
//                nonClosedWays.add(jw.getNodes());
//            } else {
//                result.add(jw.getNodes());
//            }
//        }
//        return result;
//    }

}
