package org.openstreetmap.josm.plugins.ods.domains.buildings.impl;

import java.util.Collections;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.domains.buildings.BuildingType;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuilding;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuildingUnit;
import org.openstreetmap.josm.plugins.ods.entities.OdEntity;
import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;
import org.openstreetmap.josm.plugins.ods.entities.impl.AbstractOdEntity;
import org.openstreetmap.josm.plugins.ods.entities.impl.ZeroOneMany;
import org.openstreetmap.josm.plugins.ods.matching.Match;

public class DefaultOdBuildingUnit extends AbstractOdEntity implements OdBuildingUnit {
    private Long buildingUnitId;
    private Double area;
    private BuildingType buildingType;
    private final List<OdAddressNode> addressNodes = Collections.emptyList();
    private final ZeroOneMany<OdBuilding> buildings = new ZeroOneMany<>();

    @Override
    public Match<? extends OdEntity, ? extends OsmEntity> getMatch() {
        // The BuildingUnit entity type doesn't have an OSM equivalent, so matching is not applicable.
        throw new UnsupportedOperationException();
    }

    @Override
    public Long getBuildingUnitId() {
        return buildingUnitId;
    }

    @Override
    public void setBuildingUnitId(Long id) {
        this.buildingUnitId = id;
    }

    @Override
    public void addBuilding(OdBuilding building) {
        buildings.add(building);
    }

    @Override
    public ZeroOneMany<OdBuilding> getBuildings() {
        return buildings;
    }

    @Override
    public List<OdAddressNode> getAddressNodes() {
        return addressNodes;
    }

    @Override
    public void setArea(Double area) {
        this.area = area;
    }

    @Override
    public Double getArea() {
        return area;
    }

    public void setBuildingType(BuildingType buildingType) {
        this.buildingType = buildingType;
    }

    @Override
    public BuildingType getBuildingType() {
        return buildingType;
    }

}
