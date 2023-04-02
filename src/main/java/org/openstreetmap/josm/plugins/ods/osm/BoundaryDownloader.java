package org.openstreetmap.josm.plugins.ods.osm;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.openstreetmap.josm.data.gpx.GpxData;
import org.openstreetmap.josm.data.notes.Note;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.gui.progress.ProgressMonitor;
import org.openstreetmap.josm.io.IllegalDataException;
import org.openstreetmap.josm.io.OsmReader;
import org.openstreetmap.josm.io.OsmServerReader;
import org.openstreetmap.josm.io.OsmTransferException;

/**
 * Read content from OSM server for a given (complex) boundary.
 * In contrast to the Josm BoundingboxDownloader, this class can download multiple small bounding boxes at the same time.
 * Gps trails and notes will not be loaded.
 * 
 * @since 7.0
 */
public abstract class BoundaryDownloader extends OsmServerReader {

    /**
     * The boundary of the desired map data.
     */

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
    abstract protected String getTaskName();

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
    abstract protected DataSet parseDataSet(InputStream source,
            ProgressMonitor progressMonitor) throws IllegalDataException;

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
    @SuppressWarnings("static-method")
    public boolean considerAsFullDownload() {
        return true;
    }
}
