package org.openstreetmap.josm.plugins.ods.osm;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.openstreetmap.josm.data.gpx.GpxData;
import org.openstreetmap.josm.data.notes.Note;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.gui.progress.ProgressMonitor;
import org.openstreetmap.josm.io.IllegalDataException;
import org.openstreetmap.josm.io.OsmReader;
import org.openstreetmap.josm.io.OsmTransferException;
import org.openstreetmap.josm.io.OverpassDownloadReader;
import org.openstreetmap.josm.plugins.ods.http.OdsHttpClient;
import org.openstreetmap.josm.plugins.ods.http.OkHttp3Client;
import org.openstreetmap.josm.plugins.ods.io.OverpassHost;
import org.openstreetmap.josm.plugins.ods.jts.Boundary;
import org.openstreetmap.josm.tools.CheckParameterUtil;

/**
 * Read content from OSM server for a given (complex) boundary.
 * In contrast to the Josm BoundingboxDownloader, this class can download multiple small bounding boxes at the same time.
 * Gps trails and notes will not be loaded.
 * 
 * @since 7.0
 */
public class OverpassBoundaryDownloader extends BoundaryDownloader {

    private OdsHttpClient httpClient = new OkHttp3Client();
    /**
     * The boundary of the desired map data.
     */
    private final String url;
    private final Boundary boundary;

    /**
     * Constructs a new {@code BoundingBoxDownloader}.
     * 
     * @param downloadArea
     *            The area to download
     */
    public OverpassBoundaryDownloader(Boundary boundary) {
        CheckParameterUtil.ensureParameterNotNull(boundary, "boundary");
        this.url = OverpassDownloadReader.OVERPASS_SERVER.get() + "interpreter";
        this.boundary = boundary;
    }

    @Override
    public GpxData parseRawGps(ProgressMonitor progressMonitor)
            throws OsmTransferException {
        return null;
    }

    /**
     * Returns the name of the download task to be displayed in the
     * {@link ProgressMonitor}.
     * 
     * @return task name
     */
    @Override
    protected String getTaskName() {
        return tr("Contacting Overpass Server...");
    }

    /**
     * Parse the given input source and return the dataset.
     * 
     * @param source
     *            input stream
     * @param progressMonitor
     *            progress monitor
     * @return dataset
     * @throws IllegalDataException
     *             if an error was found while parsing the OSM data
     *
     * @see OsmReader#parseDataSet(InputStream, ProgressMonitor)
     */
    @Override
    protected DataSet parseDataSet(InputStream source,
            ProgressMonitor progressMonitor) throws IllegalDataException {
        return OsmReader.parseDataSet(source, progressMonitor);
    }

    @Override
    public DataSet parseOsm(ProgressMonitor progressMonitor)
            throws OsmTransferException {
        progressMonitor.beginTask(getTaskName(), 10);
        try {
            DataSet ds = null;
            progressMonitor.indeterminateSubTask(null);
            String postRequest = OverpassHost.buildQlQuery(boundary);
            try (InputStream in = httpClient.getInputStream(url, postRequest, progressMonitor.createSubTaskMonitor(9, false))) {
                if (in == null)
                    return null;
                ds = parseDataSet(in,
                        progressMonitor.createSubTaskMonitor(1, false));
            }
            return ds;
        } catch (OsmTransferException e) {
            throw e;
        } catch (IllegalDataException | IOException e) {
            throw new OsmTransferException(e);
        } finally {
            progressMonitor.finishTask();
            activeConnection = null;
        }
    }

    @Override
    public List<Note> parseNotes(int noteLimit, int daysClosed,
            ProgressMonitor progressMonitor) throws OsmTransferException {
        return Collections.emptyList();
    }

    /**
     * Determines if download is complete for the given bounding box.
     * 
     * @return true if download is complete for the given bounding box (not
     *         filtered)
     */
    @Override
    public boolean considerAsFullDownload() {
        return true;
    }
}
