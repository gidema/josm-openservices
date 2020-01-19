package org.openstreetmap.josm.plugins.ods.osm.test.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.UploadPolicy;
import org.openstreetmap.josm.io.IllegalDataException;
import org.openstreetmap.josm.io.OsmReader;

/**
 * This class provides utility methods to load DataSets with test data and
 * a Dataset with expected result.
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class TestDataLoader {
    private static Map<URL, DataSet> cache = new HashMap<>();
    private final String path;
    private final String name;
    private DataSet osmData;
    private DataSet odData;
    private DataSet resultData;

    public TestDataLoader(String path, String name) throws IOException {
        super();
        this.path = path;
        this.name = name;
        load();
    }

    public DataSet getOsmData() {
        return osmData;
    }

    public DataSet getOdData() {
        return odData;
    }

    public DataSet getResultData() {
        return resultData;
    }

    private void load() throws IOException {
        osmData = loadTestData(path + "/" + name + ".osm.osm");
        odData = loadTestData(path + "/" + name + ".od.osm");
        resultData = loadTestData(path + "/" + name + ".result.osm");

    }

    public static DataSet loadTestData(Class<?> clazz, String name) {
        URL url = clazz.getResource(name);
        return loadTestData(url);
    }

    public static DataSet loadTestData(String path) throws IOException {
        File file = new File(path);
        URL url = file.toURI().toURL();
        return loadTestData(url);
    }

    private static DataSet loadTestData(URL url) {
        if (url == null) {
            return null;
        }
        DataSet dataSet = cache.get(url);
        if (dataSet != null) {
            return new DataSet(dataSet);
        }
        try (InputStream stream = url.openStream()) {
            dataSet = OsmReader.parseDataSet(stream, null);
            dataSet.setUploadPolicy(UploadPolicy.BLOCKED);
            cache.put(url,  dataSet);
            return dataSet;
        } catch (IOException e) {
            Assertions.fail("The file with test data could not be read.");
            return null;
        } catch (IllegalDataException e) {
            Assertions.fail("The file with test data is not a valid OSM file.");
            return null;
        }
    }
}
