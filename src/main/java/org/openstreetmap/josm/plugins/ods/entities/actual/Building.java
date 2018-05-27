package org.openstreetmap.josm.plugins.ods.entities.actual;

import java.util.Set;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.plugins.ods.domains.buildings.BuildingType;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.matching.BuildingMatch;

import com.vividsolutions.jts.geom.Geometry;

public interface Building extends Entity {
    public static boolean IsBuilding(OsmPrimitive primitive) {
        boolean taggedAsBuilding = primitive.hasKey("building") || primitive.hasKey("building:part")
                || primitive.hasKey("no:building");
        boolean validGeometry = (primitive.getDisplayType() == OsmPrimitiveType.CLOSEDWAY
                || primitive.getDisplayType() == OsmPrimitiveType.MULTIPOLYGON
                || primitive.getDisplayType() == OsmPrimitiveType.RELATION);
        return taggedAsBuilding && validGeometry;
    }

    @Override
    public Geometry getGeometry();

    //    public OsmCity getCity();

    /**
     * Return the address information associated with this building.
     *
     * @return null if no address is associated with the building
     */
    //    public Address getAddress();

    //    /**
    //     * Return the address nodes associated with this building.
    //     *
    //     * @return empty collection if no address nodes are associated with this
    //     *         building.
    //     */
    //    public List<AddressNode> getAddressNodes();

    public Set<? extends Building> getNeighbours();

    /**
     * Check is the full area of this building has been loaded. This is true if
     * the building is completely covered by the downloaded area.
     *
     * @return
     */

    public void setStartDate(String string);

    public String getStartDate();

    public BuildingType getBuildingType();

    public void setMatch(BuildingMatch match);

    @Override
    public BuildingMatch getMatch();

    // Setters
    public void setBuildingType(BuildingType buildingType);

    public void setIncomplete(boolean incomplete);
}
