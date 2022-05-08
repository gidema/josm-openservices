package org.openstreetmap.josm.plugins.ods.matching;

import org.openstreetmap.josm.plugins.ods.Matcher;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.context.OdsContextJob;

public class OdsMatchingJob implements OdsContextJob {

    @Override
    public void run(OdsContext context) {
        Matchers matchers = context.getComponent(Matchers.class);
        matchers.forEach(Matcher::run);
    }
}
