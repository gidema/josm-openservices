package org.openstreetmap.josm.plugins.openservices.entities.josm;

import org.openstreetmap.josm.plugins.openservices.entities.BuildException;


/**
 * Build entities from the Josm dataset
 * @author gertjan
 *
 */
public interface JosmEntityBuilder {
    public void build() throws BuildException;

}
