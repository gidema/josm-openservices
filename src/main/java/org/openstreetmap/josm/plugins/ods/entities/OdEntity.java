package org.openstreetmap.josm.plugins.ods.entities;

/**
 * An OdEntity is an Entity that has been derived from an external open data source.
 * There doesn't necessarily have to a 1 to 1 relation between an OdEntity and a feature
 * from the data source. Retrieved features can be combined or split to create 1 or more
 * OdEntities.
 *
 * @author Gertjan Idema
 *
 */
public interface OdEntity extends Entity {


}
