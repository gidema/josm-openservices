package org.openstreetmap.josm.plugins.ods.analysis;

import org.openstreetmap.josm.plugins.ods.OdsWorkingSet;

/**
 * A GlobalAnalyzer is used to relate data between the internal (OSM)
 * dataLayer and the external (Imported) data.
 *  
 * @author gertjan
 *
 */
public interface GlobalAnalyzer {
    public void analyze(OdsWorkingSet workingSet);
}
