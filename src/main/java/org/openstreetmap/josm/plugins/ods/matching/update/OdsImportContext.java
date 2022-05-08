package org.openstreetmap.josm.plugins.ods.matching.update;

import java.util.List;

import org.openstreetmap.josm.plugins.ods.context.OdsContextJob;

public interface OdsImportContext {

    public List<OdsContextJob> getPostImportJobs();

}
