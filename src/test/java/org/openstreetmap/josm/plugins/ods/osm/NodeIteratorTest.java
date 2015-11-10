package org.openstreetmap.josm.plugins.ods.osm;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.io.IllegalDataException;
import org.openstreetmap.josm.plugins.ods.test.util.JOSMFixture;
import org.openstreetmap.josm.plugins.ods.test.util.TestData;

public class NodeIteratorTest {
    private TestData testData;
    private Way building1;
    
    @BeforeClass
    public static void setUpBeforeClass() {
        JOSMFixture.createUnitTestFixture().init();
    }

    @Before
    public void init() throws IOException, IllegalDataException {
        try {
            testData = new TestData(this);
            building1 = testData.getWay("building1");
        }
        catch (FileNotFoundException e) {
            Assert.fail(e.getMessage());
        }
    }
    
    @Test
    public void constructForwardIterator() {
        NodeIterator it = new NodeIterator(building1, 0, false);
        assertEquals(building1.getNode(0), it.peek());
        assertEquals(building1.getNode(1), it.peekNext());
    }
    
    @Test
    public void constructReverseIterator() {
        NodeIterator it = new NodeIterator(building1, 2, true);
        assertEquals(building1.getNode(2), it.peek());
        assertEquals(building1.getNode(1), it.peekNext());
    }

    @Test
    public void lastSegment() {
        NodeIterator it = new NodeIterator(building1, 0, false);
        while (it.hasNextNodes(2)) {
            it.next();
        }
        int n = building1.getNodesCount();
        assertEquals(building1.getNode(n-2), it.peek());
        assertEquals(building1.getNode(n-1), it.peekNext());
    }
}
