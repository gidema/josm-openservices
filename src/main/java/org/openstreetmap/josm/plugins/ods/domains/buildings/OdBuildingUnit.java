package org.openstreetmap.josm.plugins.ods.domains.buildings;

import java.util.Set;

import org.openstreetmap.josm.plugins.ods.entities.OdEntity;
import org.openstreetmap.josm.plugins.ods.entities.impl.ZeroOneMany;

public interface OdBuildingUnit extends OdEntity {
    public Long getBuildingUnitId();

    public void setBuildingUnitId(Long id);

    public void setMainAddressNode(OdAddressNode addressNode);

    public void addSecondaryAddressNode(OdAddressNode addressNode);

    public void addBuilding(OdBuilding building);

    public void setStatus(BuildingUnitStatus status);

    public ZeroOneMany<OdBuilding> getBuildings();

    public OdAddressNode getMainAddressNode();

    /**
     * Return the secondary address nodes associated with this building unit.
     *
     * @return empty set if no address nodes are associated with this
     *         building.
     */
    public Set<OdAddressNode> getSecondaryAddressNodes();

    public void setArea(Double area);

    public Double getArea();

    public BuildingType getBuildingType();

    public BuildingUnitStatus getStatus();
}
