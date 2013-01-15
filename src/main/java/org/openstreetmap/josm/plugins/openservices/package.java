/**
 * Provides a framework for working with open vector data from web services like wfs.
 * 
 * Plug-ins based on this framework can download data from web services, convert
 * it into OSM format and add relevant tags. The data set is restricted to a
 * bounding box selected by the user.
 * The framework remembers unique identifiers from the original data. This makes
 * it possible to extend the data set without creating duplicate objects.
 *
 */

package org.openstreetmap.josm.plugins.openservices; 