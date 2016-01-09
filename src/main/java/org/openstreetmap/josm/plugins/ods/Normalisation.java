package org.openstreetmap.josm.plugins.ods;

/**
 * Options for normalisation of geometries.
 * Normalisation is conform the JTS conventions:
 * - The nodes in ways are ordered clockwise
 * - 
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public enum Normalisation {
    NONE, // No normalisation
    CLOCKWISE, // Only normalise ways clockwise
    FULL // Clockwise ways and start in upper-left corner
}
