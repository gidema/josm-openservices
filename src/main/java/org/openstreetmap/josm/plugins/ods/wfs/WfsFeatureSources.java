package org.openstreetmap.josm.plugins.ods.wfs;

import java.util.ArrayList;
import java.util.Arrays;

public class WfsFeatureSources extends ArrayList<WfsFeatureSource> {

    private static final long serialVersionUID = 1L;

    public WfsFeatureSources(WfsFeatureSource ... featureSources) {
        super(Arrays.asList(featureSources));
    }

}
