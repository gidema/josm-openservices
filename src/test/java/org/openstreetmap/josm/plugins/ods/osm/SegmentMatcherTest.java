package org.openstreetmap.josm.plugins.ods.osm;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.io.IllegalDataException;
import org.openstreetmap.josm.plugins.ods.osm.SegmentMatcher.MatchType;
import org.openstreetmap.josm.plugins.ods.test.util.JOSMFixture;
import org.openstreetmap.josm.plugins.ods.test.util.TestData;

public class SegmentMatcherTest {
    private TestData testData;
    private SegmentMatcher matcher = new SegmentMatcher(0.05);

    @BeforeClass
    public static void setUpBeforeClass() {
        JOSMFixture.createUnitTestFixture().init();
    }

    @Before
    public void init() throws IOException, IllegalDataException {
        try {
            testData = new TestData(this, "segmentMatcher.osm");
        } catch (FileNotFoundException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testPairA() {
        assertTrue(match("wayA1", "wayA2"));
        assertFalse(matcher.isreversed());
        assertEquals(MatchType.NodeToSegment, matcher.getStartMatch());
        assertEquals(MatchType.SegmentToNode, matcher.getEndMatch());
    }

    @Test
    public void testPairB() {
        assertTrue(match("wayB1", "wayB2"));
        assertFalse(matcher.isreversed());
        assertEquals(MatchType.NodeToSegment, matcher.getStartMatch());
        assertEquals(MatchType.NodeToNode, matcher.getEndMatch());
    }

    @Test
    public void testPairC() {
        assertTrue(match("wayC1", "wayC2"));
        assertFalse(matcher.isreversed());
        assertEquals(MatchType.NodeToSegment, matcher.getStartMatch());
        assertEquals(MatchType.NodeToSegment, matcher.getEndMatch());
    }

    @Test
    public void testPairD() {
        assertTrue(match("wayD1", "wayD2"));
        assertFalse(matcher.isreversed());
        assertEquals(MatchType.NodeToNode, matcher.getStartMatch());
        assertEquals(MatchType.SegmentToNode, matcher.getEndMatch());
    }

    @Test
    public void testPairE() {
        assertTrue(match("wayE1", "wayE2"));
        assertFalse(matcher.isreversed());
        assertEquals(MatchType.NodeToNode, matcher.getStartMatch());
        assertEquals(MatchType.NodeToNode, matcher.getEndMatch());
    }

    @Test
    public void testPairF() {
        assertTrue(match("wayF1", "wayF2"));
        assertFalse(matcher.isreversed());
        assertEquals(MatchType.NodeToNode, matcher.getStartMatch());
        assertEquals(MatchType.NodeToSegment, matcher.getEndMatch());
    }

    @Test
    public void testPairG() {
        assertTrue(match("wayG1", "wayG2"));
        assertFalse(matcher.isreversed());
        assertEquals(MatchType.SegmentToNode, matcher.getStartMatch());
        assertEquals(MatchType.SegmentToNode, matcher.getEndMatch());
    }

    @Test
    public void testPairH() {
        assertTrue(match("wayH1", "wayH2"));
        assertFalse(matcher.isreversed());
        assertEquals(MatchType.SegmentToNode, matcher.getStartMatch());
        assertEquals(MatchType.NodeToNode, matcher.getEndMatch());
    }

    @Test
    public void testPairI() {
        assertTrue(match("wayI1", "wayI2"));
        assertFalse(matcher.isreversed());
        assertEquals(MatchType.SegmentToNode, matcher.getStartMatch());
        assertEquals(MatchType.NodeToSegment, matcher.getEndMatch());
    }

    @Test
    public void testPairAreversed() {
        assertTrue(matchReversed("wayA1", "wayA2"));
        assertTrue(matcher.isreversed());
        assertEquals(MatchType.NodeToSegment, matcher.getStartMatch());
        assertEquals(MatchType.SegmentToNode, matcher.getEndMatch());
    }

    @Test
    public void testPairBreversed() {
        assertTrue(matchReversed("wayB1", "wayB2"));
        assertTrue(matcher.isreversed());
        assertEquals(MatchType.NodeToSegment, matcher.getStartMatch());
        assertEquals(MatchType.NodeToNode, matcher.getEndMatch());
    }

    @Test
    public void testPairCreversed() {
        assertTrue(matchReversed("wayC1", "wayC2"));
        assertTrue(matcher.isreversed());
        assertEquals(MatchType.NodeToSegment, matcher.getStartMatch());
        assertEquals(MatchType.NodeToSegment, matcher.getEndMatch());
    }

    @Test
    public void testPairDreversed() {
        assertTrue(matchReversed("wayD1", "wayD2"));
        assertTrue(matcher.isreversed());
        assertEquals(MatchType.NodeToNode, matcher.getStartMatch());
        assertEquals(MatchType.SegmentToNode, matcher.getEndMatch());
    }

    @Test
    public void testPairEreversed() {
        assertTrue(matchReversed("wayE1", "wayE2"));
        assertTrue(matcher.isreversed());
        assertEquals(MatchType.NodeToNode, matcher.getStartMatch());
        assertEquals(MatchType.NodeToNode, matcher.getEndMatch());
    }

    @Test
    public void testPairFreversed() {
        assertTrue(matchReversed("wayF1", "wayF2"));
        assertTrue(matcher.isreversed());
        assertEquals(MatchType.NodeToNode, matcher.getStartMatch());
        assertEquals(MatchType.NodeToSegment, matcher.getEndMatch());
    }

    @Test
    public void testPairGreversed() {
        assertTrue(matchReversed("wayG1", "wayG2"));
        assertTrue(matcher.isreversed());
        assertEquals(MatchType.SegmentToNode, matcher.getStartMatch());
        assertEquals(MatchType.SegmentToNode, matcher.getEndMatch());
    }

    @Test
    public void testPairHreversed() {
        assertTrue(matchReversed("wayH1", "wayH2"));
        assertTrue(matcher.isreversed());
        assertEquals(MatchType.SegmentToNode, matcher.getStartMatch());
        assertEquals(MatchType.NodeToNode, matcher.getEndMatch());
    }

    @Test
    public void testPairIreversed() {
        assertTrue(matchReversed("wayI1", "wayI2"));
        assertTrue(matcher.isreversed());
        assertEquals(MatchType.SegmentToNode, matcher.getStartMatch());
        assertEquals(MatchType.NodeToSegment, matcher.getEndMatch());
    }

    @Test
    public void testA1B1() {
        assertFalse(match("wayA1", "wayB1"));
    }

    private boolean match(String name1, String name2) {
        Way way1 = testData.getWay(name1);
        Way way2 = testData.getWay(name2);
        NodeIterator it1 = new NodeIterator(way1, 0, false);
        NodeIterator it2 = new NodeIterator(way2, 0, false);
        return matcher.match(it1, it2);
    }

    private boolean matchReversed(String name1, String name2) {
        Way way1 = testData.getWay(name1);
        // Clone the way because we will reverse the order of the nodes
        Way way2 = new Way(testData.getWay(name2));
        List<Node> nodes = way2.getNodes();
        Collections.reverse(nodes);
        way2.setNodes(nodes);
        NodeIterator it1 = new NodeIterator(way1, 0, false);
        NodeIterator it2 = new NodeIterator(way2, 0, false);
        return matcher.match(it1, it2);
    }

}
