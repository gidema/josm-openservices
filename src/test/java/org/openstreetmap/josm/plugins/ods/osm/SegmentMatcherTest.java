package org.openstreetmap.josm.plugins.ods.osm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.ods.osm.SegmentMatcher.MatchType;
import org.openstreetmap.josm.plugins.ods.test.util.JOSMFixture;
import org.openstreetmap.josm.plugins.ods.test.util.TestData;

class SegmentMatcherTest {
    private TestData testData;
    private final SegmentMatcher matcher = new SegmentMatcher(new NodeDWithinLatLon(0.05));

    @BeforeAll
    static void setUpBeforeClass() {
        JOSMFixture.createUnitTestFixture().init();
    }

    @BeforeEach
    void setUp() {
        testData = new TestData(this, "segmentMatcher.osm");
    }

    @Test
    void testPairA() {
        assertTrue(match("wayA1", "wayA2"));
        assertFalse(matcher.isreversed());
        assertEquals(MatchType.NodeToSegment, matcher.getStartMatch());
        assertEquals(MatchType.SegmentToNode, matcher.getEndMatch());
    }

    @Test
    void testPairB() {
        assertTrue(match("wayB1", "wayB2"));
        assertFalse(matcher.isreversed());
        assertEquals(MatchType.NodeToSegment, matcher.getStartMatch());
        assertEquals(MatchType.NodeToNode, matcher.getEndMatch());
    }

    @Test
    void testPairC() {
        assertTrue(match("wayC1", "wayC2"));
        assertFalse(matcher.isreversed());
        assertEquals(MatchType.NodeToSegment, matcher.getStartMatch());
        assertEquals(MatchType.NodeToSegment, matcher.getEndMatch());
    }

    @Test
    void testPairD() {
        assertTrue(match("wayD1", "wayD2"));
        assertFalse(matcher.isreversed());
        assertEquals(MatchType.NodeToNode, matcher.getStartMatch());
        assertEquals(MatchType.SegmentToNode, matcher.getEndMatch());
    }

    @Test
    void testPairE() {
        assertTrue(match("wayE1", "wayE2"));
        assertFalse(matcher.isreversed());
        assertEquals(MatchType.NodeToNode, matcher.getStartMatch());
        assertEquals(MatchType.NodeToNode, matcher.getEndMatch());
    }

    @Test
    void testPairF() {
        assertTrue(match("wayF1", "wayF2"));
        assertFalse(matcher.isreversed());
        assertEquals(MatchType.NodeToNode, matcher.getStartMatch());
        assertEquals(MatchType.NodeToSegment, matcher.getEndMatch());
    }

    @Test
    void testPairG() {
        assertTrue(match("wayG1", "wayG2"));
        assertFalse(matcher.isreversed());
        assertEquals(MatchType.SegmentToNode, matcher.getStartMatch());
        assertEquals(MatchType.SegmentToNode, matcher.getEndMatch());
    }

    @Test
    void testPairH() {
        assertTrue(match("wayH1", "wayH2"));
        assertFalse(matcher.isreversed());
        assertEquals(MatchType.SegmentToNode, matcher.getStartMatch());
        assertEquals(MatchType.NodeToNode, matcher.getEndMatch());
    }

    @Test
    void testPairI() {
        assertTrue(match("wayI1", "wayI2"));
        assertFalse(matcher.isreversed());
        assertEquals(MatchType.SegmentToNode, matcher.getStartMatch());
        assertEquals(MatchType.NodeToSegment, matcher.getEndMatch());
    }

    @Test
    void testPairAreversed() {
        assertTrue(matchReversed("wayA1", "wayA2"));
        assertTrue(matcher.isreversed());
        assertEquals(MatchType.NodeToSegment, matcher.getStartMatch());
        assertEquals(MatchType.SegmentToNode, matcher.getEndMatch());
    }

    @Test
    void testPairBreversed() {
        assertTrue(matchReversed("wayB1", "wayB2"));
        assertTrue(matcher.isreversed());
        assertEquals(MatchType.NodeToSegment, matcher.getStartMatch());
        assertEquals(MatchType.NodeToNode, matcher.getEndMatch());
    }

    @Test
    void testPairCreversed() {
        assertTrue(matchReversed("wayC1", "wayC2"));
        assertTrue(matcher.isreversed());
        assertEquals(MatchType.NodeToSegment, matcher.getStartMatch());
        assertEquals(MatchType.NodeToSegment, matcher.getEndMatch());
    }

    @Test
    void testPairDreversed() {
        assertTrue(matchReversed("wayD1", "wayD2"));
        assertTrue(matcher.isreversed());
        assertEquals(MatchType.NodeToNode, matcher.getStartMatch());
        assertEquals(MatchType.SegmentToNode, matcher.getEndMatch());
    }

    @Test
    void testPairEreversed() {
        assertTrue(matchReversed("wayE1", "wayE2"));
        assertTrue(matcher.isreversed());
        assertEquals(MatchType.NodeToNode, matcher.getStartMatch());
        assertEquals(MatchType.NodeToNode, matcher.getEndMatch());
    }

    @Test
    void testPairFreversed() {
        assertTrue(matchReversed("wayF1", "wayF2"));
        assertTrue(matcher.isreversed());
        assertEquals(MatchType.NodeToNode, matcher.getStartMatch());
        assertEquals(MatchType.NodeToSegment, matcher.getEndMatch());
    }

    @Test
    void testPairGreversed() {
        assertTrue(matchReversed("wayG1", "wayG2"));
        assertTrue(matcher.isreversed());
        assertEquals(MatchType.SegmentToNode, matcher.getStartMatch());
        assertEquals(MatchType.SegmentToNode, matcher.getEndMatch());
    }

    @Test
    void testPairHreversed() {
        assertTrue(matchReversed("wayH1", "wayH2"));
        assertTrue(matcher.isreversed());
        assertEquals(MatchType.SegmentToNode, matcher.getStartMatch());
        assertEquals(MatchType.NodeToNode, matcher.getEndMatch());
    }

    @Test
    void testPairIreversed() {
        assertTrue(matchReversed("wayI1", "wayI2"));
        assertTrue(matcher.isreversed());
        assertEquals(MatchType.SegmentToNode, matcher.getStartMatch());
        assertEquals(MatchType.NodeToSegment, matcher.getEndMatch());
    }

    @Test
    void testA1B1() {
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
