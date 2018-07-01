package org.openstreetmap.josm.plugins.ods.domains.buildings;

import java.util.List;
import java.util.Set;

import org.openstreetmap.josm.plugins.ods.domains.buildings.matching.BuildingMatch;
import org.openstreetmap.josm.plugins.ods.domains.places.OdCity;
import org.openstreetmap.josm.plugins.ods.entities.OdEntity;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;

import com.vividsolutions.jts.geom.Geometry;

public interface OdBuilding extends OdEntity, Building {
    public Long getBuildingId();

    public void setBuildingId(Long id);

    @Override
    public Geometry getGeometry();

    public OdCity getCity();

    /**
     * Return the address information associated with this building.
     *
     * @return null if no address is associated with the building
     */
    public OdAddress getAddress();

    /**
     * Return the address nodes associated with this building.
     *
     * @return empty collection if no address nodes are associated with this
     *         building.
     */
    public List<OdAddressNode> getAddressNodes();

    public Set<OdBuilding> getNeighbours();

    public void setStartYear(Integer year);

    @Override
    public String getStartDate();

    @Override
    public BuildingType getBuildingType();

    @Override
    public BuildingMatch getMatch();

    // Setters
    //    public void setBuildingType(BuildingType buildingType);

    //    public void setIncomplete(boolean incomplete);
}
