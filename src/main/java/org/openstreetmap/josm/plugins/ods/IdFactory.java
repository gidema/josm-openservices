package org.openstreetmap.josm.plugins.ods;

import java.io.Serializable;

import org.opengis.feature.simple.SimpleFeature;

/**
 * A functional Interface for retrieving a unique Id from a feature.
 * <br>TODO Change to real functional interface once we switch to Java8.
 *  
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public interface IdFactory {
    public Serializable getId(SimpleFeature feature);
}
