package org.openstreetmap.josm.plugins.ods.osm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import javax.swing.JOptionPane;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.gui.progress.NullProgressMonitor;
import org.openstreetmap.josm.io.BoundingBoxDownloader;
import org.openstreetmap.josm.io.OsmApiException;
import org.openstreetmap.josm.io.OsmServerLocationReader;
import org.openstreetmap.josm.io.OsmServerReader;
import org.openstreetmap.josm.io.OsmTransferException;
import org.openstreetmap.josm.plugins.ods.DataLayer;
import org.openstreetmap.josm.plugins.ods.DownloadTask;
import org.openstreetmap.josm.plugins.ods.ODS;
import org.openstreetmap.josm.plugins.ods.OdsWorkingSet;
import org.openstreetmap.josm.plugins.ods.analysis.Analyzer;
import org.openstreetmap.josm.plugins.ods.builtenvironment.AddressToBuildingMatcher;
import org.openstreetmap.josm.plugins.ods.entities.BuildException;
import org.openstreetmap.josm.plugins.ods.entities.DefaultEntitySet;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.EntityFactory;
import org.openstreetmap.josm.plugins.ods.entities.EntitySet;
import org.openstreetmap.josm.plugins.ods.issue.Issue;
import org.openstreetmap.josm.plugins.ods.jts.Boundary;
import org.openstreetmap.josm.plugins.ods.jts.PolygonFilter;
import org.openstreetmap.josm.tools.I18n;

public class DownloadOsmLayerTask implements DownloadTask {
    private static String overpassQuery = "(node($bbox);rel(bn)->.x;way($bbox);"
            + "node(w)->.x;rel(bw);)";

    private boolean cancelled = false;
    private boolean failed = false;
    private String message = null;
    private Exception exception = null;

    private DownloadSource downloadSource = DownloadSource.OSM;
    private OsmServerReader osmServerReader;

    private final OdsWorkingSet workingSet;
    private DataSet dataSet;

    private final Boundary boundary;
    private DataLayer dataLayer;
    private List<Analyzer> analyzers;
    private EntitySet newEntities;

    public DownloadOsmLayerTask(Boundary boundary) {
        super();
        this.workingSet = ODS.getModule().getWorkingSet();
        this.boundary = boundary;
        this.dataLayer = workingSet.getInternalDataLayer();
        Double tolerance = 2e-7;
        analyzers = new ArrayList<>(5);
        analyzers.add(new AddressToBuildingMatcher());
        // analyzers.add(new AddressToStreetMatcher());
    }

    public void cancel() {
        cancelled = true;
    }
    
    @Override
    public boolean cancelled() {
        return cancelled;
    }

    @Override
    public boolean failed() {
        return failed;
    }

    @Override
    public String getMessage() {
        if (message != null) {
            return message;
        }
        if (exception != null) {
            return exception.getMessage();
        }
        return null;
    }

    @Override
    public Callable<Object> stage(String stage) {
        switch (stage) {
        case "prepare":
            return new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    switch (downloadSource) {
                    case OSM:
                        osmServerReader = new BoundingBoxDownloader(
                                boundary.getBounds());
                        break;
                    case OVERPASS:
                        String url = Overpass.getURL(overpassQuery, boundary);
                        osmServerReader = new OsmServerLocationReader(url);
                        break;
                    }
                    return null;
                };
            };
        case "download":
            return new DownloadStage();            
        case "process":
            return new ProcessStage();
        default:
            return null;
        }
    }

    private void analyze() {
        for (Analyzer analyzer : analyzers) {
            analyzer.analyze(dataLayer, newEntities);
        }
    }

    class DownloadStage implements Callable<Object> {

        @Override
        public Object call() throws Exception {
            try {
                if (cancelled)
                    return null;
                dataSet = parseDataSet();
                if (downloadSource == DownloadSource.OSM) {
                    PolygonFilter filter = new PolygonFilter(
                            boundary.getPolygon());
                    dataSet = filter.filter(dataSet);
                }
                if (dataSet.allPrimitives().isEmpty()) {
                    cancelled = true;
                    message = I18n
                            .tr("The selected download area contains no OSM objects");
                    return null;
                }
            } catch (Exception e) {
                failed = true;
                if (cancelled()) {
                    Main.info(I18n
                            .tr("Ignoring exception because download has been canceled. Exception was: {0}",
                                    e.toString()));
                    return null;
                }
                if (e instanceof OsmApiException) {
                    if (((OsmApiException) e).getResponseCode() == 400) {
                        message = I18n
                                .tr("You tried to download too much Openstreetmap data. Please select a smaller download area.");
                        return null;
                    }
                }
                exception = e;
            }
            return null;
        }

        private DataSet parseDataSet() throws OsmTransferException {
            return osmServerReader.parseOsm(NullProgressMonitor.INSTANCE);
        }
    }

    class ProcessStage implements Callable<Object> {
        public Object call() throws Exception {
            workingSet.odsOsmDataLayer.getOsmDataLayer().mergeFrom(dataSet);
//            BuiltEnvironmentEntityBuilder builder = new BuiltEnvironmentEntityBuilder(
//                    workingSet.internalDataLayer);
//            builder.setEntityFactory(entityFactory);
            try {
                EntityFactory<OsmPrimitive> entityFactory = 
                     ODS.getModule().getEntityFactory(OsmPrimitive.class, null);
                newEntities = new DefaultEntitySet();
                for (OsmPrimitive primitive: dataSet.allPrimitives()) {
                    Entity entity = entityFactory.buildEntity(primitive, null);
                    if (entity != null) {
                        newEntities.add(entity);
                    }
                }
//                builder.build();
//                newEntities = builder.getNewEntities();
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
            return null;
        }
    }
    
    static enum DownloadSource {
        OSM,
        OVERPASS;
    }
}
