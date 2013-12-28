package org.openstreetmap.josm.plugins.ods.jts;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.RelationMember;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.ods.crs.InvalidMultiPolygonException;
import org.openstreetmap.josm.plugins.ods.issue.InvalidMultiPolygonIssue;
import org.openstreetmap.josm.plugins.ods.issue.JosmIssue;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

public class MultiPolygonBuilder {
    private GeoUtil geoUtil = GeoUtil.getInstance();
    private Relation relation;
    List<Way> outerWays = new LinkedList<Way>();
    List<Way> innerWays = new LinkedList<Way>();
    List<RelationMember> otherMembers = new LinkedList<RelationMember>();
    private JosmIssue issue;
    private Geometry geometry;

    public MultiPolygonBuilder(Relation relation) {
        this.relation = relation;
    }
    
    public void build() throws InvalidMultiPolygonException {
        if (relation.isIncomplete()) {
            return;
        }
        if (!relation.isMultipolygon()) {
            throw new InvalidMultiPolygonException(relation, 
                 "");
        }
        analyzeMembers();
        if (issue != null) return;
        buildGeometry();
    }
    
    private void analyzeMembers() {
        for (RelationMember member : relation.getMembers()) {
            if ("outer".equals(member.getRole())) {
                if (member.isWay()) {
                    outerWays.add(member.getWay());
                }
                else {
                    issue =new InvalidMultiPolygonIssue(relation,
                        "One or more outer relation members are not ways");
                    return;
                }
            }
            else if ("inner".equals(member.getRole())) {
                if (member.isWay()) {
                    innerWays.add(member.getWay());
                }
                else {
                    issue = new InvalidMultiPolygonIssue(relation,
                        "One or more inner relation members are not ways");
                    return;
                }
            }
            else {
                otherMembers.add(member);
            }
        }
    }
     
    public boolean isInComplete() {
        return relation.isIncomplete();
    }
    
    public boolean hasIssue() {
        return issue != null;
    }
    
    public JosmIssue getIssue() {
        return issue;
    }
    
    public boolean hasOtherMembers() {
        return !otherMembers.isEmpty();
    }

    public List<RelationMember> getOtherMembers() {
        return otherMembers;
    }

    private void buildGeometry() throws IllegalArgumentException {
        List<Polygon> holes = new LinkedList<Polygon>();
        for (Way way : innerWays) {
            holes.add(geoUtil.toPolygon(way));
        }
        List<Polygon> polygons = new ArrayList<Polygon>(outerWays.size());
        for (Way way : outerWays) {
            Polygon shell = geoUtil.toPolygon(way);
           List<LinearRing> innerRings = new LinkedList<LinearRing>();
            Iterator<Polygon> it = holes.iterator();
            while (it.hasNext()) {
                Polygon hole = it.next();
                if (shell.covers(hole)) {
                    innerRings.add((LinearRing) hole.getBoundary());
                    it.remove();
                }
            }
            polygons.add(geoUtil.createPolygon((LinearRing) shell.getBoundary(), innerRings));
        }
        if (holes.size() > 0) {
            throw new IllegalArgumentException(
                "One or more of the inner rings of this relation don't fall " +
                "within one of the outer rings.");
            
        }
        if (polygons.size() == 1) {
            geometry = polygons.get(0);
        }
        geometry = geoUtil.createMultiPolygon(polygons);
    }

    public Geometry getGeometry() {
        return geometry;
    }
}
