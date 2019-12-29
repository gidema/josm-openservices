package org.openstreetmap.josm.plugins.ods.domains.buildings;

/**
 * House numbers are generally more than just a number.
 * There can be prefixes, postfixes, floorlevels etc. In the addressing of
 * a particular house or building unit.
 * For this reason, the house number deserves it's own interface. Implementations
 * are country-specific and should be in the appropriate submodules.
 * 
 * @author gertjan
 *
 */
public interface HouseNumber extends Comparable<HouseNumber> {
    public Integer getMainHouseNumber();
    public String getFullHouseNumber();
}
