package org.openstreetmap.josm.plugins.ods.jts;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.openstreetmap.josm.data.osm.DataIntegrityProblemException;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.RelationMember;
import org.openstreetmap.josm.data.osm.Way;

import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.prep.PreparedPolygon;

/**
 * Filter a Josm dataSet using a Polygon. All primitives that are completely
 * outside the dataSet will be removed from the dataSet
 * 
 * @author gertjan
 * 
 */
public class PolygonFilter {
    private final static GeoUtil geoUtil = GeoUtil.getInstance();

    private final PreparedPolygon pp;
    private final Set<OsmPrimitive> keep = new HashSet<>();

    public PolygonFilter(Polygon polygon) {
        this.pp = new PreparedPolygon(polygon);
    }

    public DataSet filter(DataSet dataSet) {
        // Create a Collection of all primitives we should keep
        for (Node node : dataSet.getNodes()) {
            if (node.isIncomplete()) {
                continue;
            }
            Point point = geoUtil.toPoint(node);
            if (pp.contains(point)) {
                keep(node, true);
            }
        }
        // Now create a new dataSet containing the primitives to keep

        // The nodes are easy. We can simply clone them
        DataSet newDataSet = new DataSet();
        Iterator<OsmPrimitive> it = keep.iterator();
        while (it.hasNext()) {
            OsmPrimitive primitive = it.next();
            if (primitive.getType() == OsmPrimitiveType.NODE) {
                Node newNode = clone((Node) primitive);
                newDataSet.addPrimitive(newNode);
            }
        }

        // Now recreate the ways
        it = keep.iterator();
        while (it.hasNext()) {
            OsmPrimitive primitive = it.next();
            if (primitive.getType() == OsmPrimitiveType.WAY) {
                Way oldWay = (Way) primitive;
                Way newWay = clone(oldWay, newDataSet);
                newDataSet.addPrimitive(newWay);
            }
        }

        // And finally the relations
        // Iterator<Relation> it2 = dataSet.getRelations().iterator();
        it = keep.iterator();
        while (it.hasNext()) {
            OsmPrimitive primitive = it.next();
            // Relation oldRelation = it.next();
            if (primitive.getType() == OsmPrimitiveType.RELATION) {
                if (newDataSet.getPrimitiveById(primitive.getPrimitiveId()) == null) {
                    try {
                        Relation oldRelation = (Relation) primitive;
                        Relation newRelation = clone(oldRelation, newDataSet);
                        newDataSet.addPrimitive(newRelation);
                    } catch (DataIntegrityProblemException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return newDataSet;
    }

    private void keep(OsmPrimitive primitive) {
        switch (primitive.getType()) {
        case NODE:
            keep((Node)primitive, false);
            break;
        case WAY:
            keep((Way)primitive);
            break;
        case RELATION:
            keep((Relation)primitive);
            break;
        default:
            break;
        }
    }
    
    private void keep(Node node, boolean keepReferrers) {
        if (keep.add(node)) {
            if (keepReferrers) {
                for (OsmPrimitive referrer : node.getReferrers()) {
                    keep(referrer);
                }
            }
        }
    }
    
    private void keep(Way way) {
        if (keep.add(way)) {
            for (OsmPrimitive referrer : way.getReferrers()) {
                keep(referrer);
            }
            if (!way.isIncomplete()) {
                for (Node node : way.getNodes()) {
                    keep(node);
                }
            }
        }
    }
    
    private void keep (Relation relation) {
        if (keep.add(relation)) {
            for (OsmPrimitive referrer : relation.getReferrers()) {
                keep(referrer);
            }
        }
    }

    private Node clone(Node node) {
        Node clone = new Node(node);
        clone.setKeys(node.getKeys());
        return clone;
    }

    private Way clone(Way way, DataSet newDataSet) {
        Way clone = new Way(way);
        if (way.isIncomplete()) {
            return clone;
        }
        clone.setKeys(way.getKeys());
        List<Node> nodes = new ArrayList<>(way.getNodesCount());
        for (int i = 0; i < way.getNodesCount(); i++) {
            Node node = way.getNode(i);
            Node newNode = (Node) newDataSet.getPrimitiveById(node.getId(),
                    OsmPrimitiveType.NODE);
            if (newNode == null) {
                newNode = clone(node);
                newDataSet.addPrimitive(newNode);
            }
            nodes.add(newNode);
        }
        clone.setNodes(nodes);
        return clone;
    }

    private Relation clone(Relation relation, DataSet newDataSet)
            throws DataIntegrityProblemException {
        Relation clone = new Relation(relation);
        if (relation.isIncomplete()) {
            return clone;
        }
        clone.setKeys(relation.getKeys());
        for (int i = 0; i < clone.getMembersCount(); i++) {
            RelationMember member = clone.getMember(i);
            OsmPrimitiveType memberType = member.getType();
            long memberId = member.getUniqueId();
            OsmPrimitive newPrimitive = newDataSet.getPrimitiveById(memberId,
                    memberType);
            if (newPrimitive == null) {
                newPrimitive = cloneShalow(member.getMember(), newDataSet);
                newDataSet.addPrimitive(newPrimitive);
            }
            RelationMember newMember = new RelationMember(member.getRole(),
                    newPrimitive);
            clone.setMember(i, newMember);
        }
        return clone;
    }

    private OsmPrimitive cloneShalow(OsmPrimitive member, DataSet newDataSet) {
        OsmPrimitive newPrimitive = null;
        switch (member.getType()) {
        case NODE:
            newPrimitive = new Node(member.getId(), 0);
            break;
        case WAY:
            //newPrimitive = new Way(member.getId(), member.getVersion());
            newPrimitive = new Way(member.getId(), 0);
            break;
        case RELATION:
            newPrimitive = clone((Relation)member, newDataSet);
            break;
        default:
            break;

        }
        return newPrimitive;
    }
}
