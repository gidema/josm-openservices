package org.openstreetmap.josm.plugins.ods.entities.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.swing.JOptionPane;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.plugins.ods.analysis.Analyzer;
import org.openstreetmap.josm.plugins.ods.entities.BuildException;
import org.openstreetmap.josm.plugins.ods.entities.EntityFactory;
import org.openstreetmap.josm.plugins.ods.entities.EntitySet;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.AddressToBuildingMatcher;
import org.openstreetmap.josm.plugins.ods.io.DownloadJob;
import org.openstreetmap.josm.plugins.ods.io.Downloader;
import org.openstreetmap.josm.plugins.ods.issue.Issue;
import org.openstreetmap.josm.tools.I18n;

public class OsmDownloadJob implements DownloadJob {
    private OsmDownloader downloader;
    private InternalDataLayer dataLayer;
    private EntityFactory entityFactory;
    private List<Analyzer> analyzers;
    private EntitySet newEntities;

    @Inject
    public OsmDownloadJob(InternalDataLayer dataLayer, OsmDownloader downloader, EntityFactory entityFactory) {
        super();
        this.downloader = downloader;
        this.dataLayer = dataLayer;
        this.entityFactory = entityFactory;
        Double tolerance = 2e-7;
        analyzers = new ArrayList<>(5);
        analyzers.add(new AddressToBuildingMatcher());
//        analyzers.add(new AddressToStreetMatcher());
    }

    @Override
    public List<? extends Downloader> getDownloaders() {
        return Collections.singletonList(downloader);
    }

    public void build() throws BuildException {
        DataSet dataSet = downloader.getDataSet();
        dataLayer.getOsmDataLayer().mergeFrom(dataSet);

        BuiltEnvironmentEntityBuilder builder = new BuiltEnvironmentEntityBuilder(
                dataLayer);
        builder.setEntityFactory(entityFactory);
        try {
            builder.build();
            newEntities = builder.getNewEntities();
            analyze();
        } catch (BuildException e) {
            Collection<Issue> issues = e.getIssues();
            StringBuilder sb = new StringBuilder(1000);
            sb.append(I18n.trn("An object could not be built:",
                    "{0} objects could not be built:", issues.size(),
                    issues.size()));
            for (Issue issue : e.getIssues()) {
                sb.append("\n").append(issue.getMessage());
            }
            JOptionPane.showMessageDialog(Main.parent, sb.toString());
        }
    }
    
    private void analyze() {
        for (Analyzer analyzer : analyzers) {
            analyzer.analyze(dataLayer, newEntities);
        }        
    }
}
