package org.openstreetmap.josm.plugins.ods.mapping;

import org.openstreetmap.josm.plugins.ods.Mapper;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.context.OdsContextJob;

public class OdsMappingJob implements OdsContextJob {

    @Override
    public void run(OdsContext context) {
        Mappers matchers = context.getComponent(Mappers.class);
        matchers.forEach(Mapper::run);
    }
}
