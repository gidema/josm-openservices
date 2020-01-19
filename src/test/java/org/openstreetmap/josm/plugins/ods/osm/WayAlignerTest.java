package org.openstreetmap.josm.plugins.ods.osm;

import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.ods.test.util.JOSMFixture;
import org.openstreetmap.josm.plugins.ods.test.util.TestData;

public class WayAlignerTest {
    private TestData testData;

    @BeforeAll
    public static void setUpBeforeClass() {
        JOSMFixture.createUnitTestFixture().init();
    }

    @BeforeEach
    public void init() {
        testData = new TestData(this);
    }

    @Test
    public void building3_4() {
        Way building3 = testData.getWay("building3");
        Way building4 = testData.getWay("building4");
        NodeDWithin dWithin = new NodeDWithinLatLon(0.05);
        WayAligner aligner = new WayAligner(building3, building4, dWithin, false);
        aligner.run();
        for (int i=0; i<2; i++) {
            assertSame(building3.getNodes().get(i+2), building4.getNodes().get(i));
        }
    }
}
