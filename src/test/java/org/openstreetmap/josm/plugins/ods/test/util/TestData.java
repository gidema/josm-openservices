package org.openstreetmap.josm.plugins.ods.test.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;

import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.io.IllegalDataException;

public class TestData {
    private final static String DEFAULT_TEST_DATA = "testdata.osm";
    private DataSet dataSet;
    private Map<String, Node> nodes;
    private Map<String, Way> ways;
    private Map<String, Relation> relations;
    
    public TestData(Object object) throws IOException, IllegalDataException {
        this(object, DEFAULT_TEST_DATA);
    }
    
    public TestData(Object object, String name) throws IOException, IllegalDataException {
        this(object.getClass(), name);
    }

    public TestData(Class<?> clazz) throws IOException, IllegalDataException {
        this(clazz, DEFAULT_TEST_DATA);
    }

    public TestData(Class<?> clazz, String name) throws IOException, IllegalDataException {
        this.dataSet = TestDataLoader.loadTestData(clazz, name);
        nodes = new HashMap<>();
        ways = new HashMap<>();
        relations = new HashMap<>();
        for (OsmPrimitive primitive :dataSet.allPrimitives()) {
            String ref = primitive.get("ref:test");
            if (ref != null) {
                switch (primitive.getType()) {
                case NODE:
                    nodes.put(ref, (Node)primitive);
                    break;
                case WAY:
                    ways.put(ref, (Way)primitive);
                    break;
                case RELATION:
                    relations.put(ref, (Relation)primitive);
                    break;
                default:
                    break;
                }
            }
        }
    }

    public Node getNode(String ref) {
        Node node = nodes.get(ref);
        if (node == null) {
            fail("No node with tag ref:test=" + ref + " was found.");
        }
        return nodes.get(ref);
    }
    
    public Way getWay(String ref) {
        Way way = ways.get(ref);
        if (way == null) {
            fail("No way with tag ref:test=" + ref + " was found.");
        }
        return way;
    } 
    
    public Relation getRelation(String ref) {
        Relation relation = relations.get(ref);
        if (relation == null) {
            fail("No relation with tag ref:test=" + ref + " was found.");
        }
        return relation;
    } 
}
