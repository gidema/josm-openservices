package org.openstreetmap.josm.plugins.ods.osm;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openstreetmap.josm.io.IllegalDataException;
import org.openstreetmap.josm.plugins.ods.objects.builtenvironment.BuildingAligner;
import org.openstreetmap.josm.plugins.ods.test.util.JOSMFixture;
import org.openstreetmap.josm.plugins.ods.test.util.TestData;

public class BuildingAlignerTest {
    private TestData testData;
    
    @BeforeClass
    public static void setUpBeforeClass() {
        JOSMFixture.createUnitTestFixture().init();
    }

    @Before
    public void init() throws IOException, IllegalDataException {
        try {
            testData = new TestData(this, "buildingAligner.osm");
        }
        catch (FileNotFoundException e) {
            Assert.fail(e.getMessage());
        }
    }
    
    @Test
    public void deventer1() {
        BuildingAligner buildingAligner = new BuildingAligner(0.05, false);
//        buildingAligner.setBuildings(building1, building2);
    }
}
