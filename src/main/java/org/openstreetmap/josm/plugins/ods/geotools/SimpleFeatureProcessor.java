package org.openstreetmap.josm.plugins.ods.geotools;

import org.geotools.data.simple.SimpleFeatureIterator;

/**
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public interface SimpleFeatureProcessor {
    void process(SimpleFeatureIterator features);

}
