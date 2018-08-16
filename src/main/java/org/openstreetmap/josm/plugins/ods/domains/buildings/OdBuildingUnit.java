package org.openstreetmap.josm.plugins.ods.domains.buildings;

import java.util.List;

import org.openstreetmap.josm.plugins.ods.entities.OdEntity;
import org.openstreetmap.josm.plugins.ods.entities.impl.ZeroOneMany;

public interface OdBuildingUnit extends OdEntity {
    public Long getBuildingUnitId();

    public void setBuildingUnitId(Long id);

    public void addBuilding(OdBuilding building);

    public ZeroOneMany<OdBuilding> getBuildings();

    /**
     * Return the address nodes associated with this building unit.
     * The first item in the list is the main address.
     *
     * @return empty collection if no address nodes are associated with this
     *         building.
     */
    public List<OdAddressNode> getAddressNodes();

    public void setArea(Double area);

    public Double getArea();

    public BuildingType getBuildingType();
}
