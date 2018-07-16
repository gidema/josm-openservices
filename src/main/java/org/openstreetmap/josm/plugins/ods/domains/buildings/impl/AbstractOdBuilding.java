package org.openstreetmap.josm.plugins.ods.domains.buildings.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.openstreetmap.josm.plugins.ods.domains.buildings.BuildingType;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddress;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuilding;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuildingUnit;
import org.openstreetmap.josm.plugins.ods.domains.buildings.matching.BuildingMatch;
import org.openstreetmap.josm.plugins.ods.domains.places.OdCity;
import org.openstreetmap.josm.plugins.ods.entities.impl.AbstractOdEntity;

public abstract class AbstractOdBuilding extends AbstractOdEntity implements OdBuilding {
    private Long buildingId;
    private OdAddress address;
    private final List<OdAddressNode> addressNodes = new LinkedList<>();
    private Set<OdBuildingUnit> buildingUnits;
    private BuildingType buildingType = BuildingType.UNCLASSIFIED;
    private String startDate;
    private final Set<OdBuilding> neighbours = new HashSet<>();
    private OdCity city;
    private BuildingMatch match;

    @Override
    public Long getBuildingId() {
        return buildingId;
    }

    @Override
    public void setBuildingId(Long id) {
        this.buildingId = id;
    }


    @Override
    public void setStartYear(Integer year) {
        this.startDate = Objects.toString(year);
    }

    @Override
    public String getStartDate() {
        return startDate;
    }

    @Override
    public BuildingType getBuildingType() {
        return buildingType;
    }

    @Override
    public void setBuildingType(BuildingType buildingType) {
        this.buildingType = buildingType;
    }

    @Override
    public OdCity getCity() {
        return city;
    }

    public void setAddress(OdAddress address) {
        this.address = address;
    }

    @Override
    public OdAddress getAddress() {
        return address;
    }

    @Override
    public List<OdAddressNode> getAddressNodes() {
        return addressNodes;
    }

    @Override
    public Set<OdBuildingUnit> getBuildingUnits() {
        return buildingUnits;
    }

    @Override
    public void addBuildingUnit(OdBuildingUnit buildingUnit) {
        if (buildingUnits == null) {
            buildingUnits = Collections.singleton(buildingUnit);
            return;
        }
        if (buildingUnits.contains(buildingUnit)) return;
        buildingUnits = new HashSet<>(buildingUnits);
        buildingUnits.add(buildingUnit);
    }

    @Override
    public Set<OdBuilding> getNeighbours() {
        return neighbours;
    }

    //    @Override
    //    public <E1 extends OsmEntity, E2 extends OdEntity> void setMatch(
    //            Match<E1, E2> match) {
    //        // TODO Auto-generated method stub
    //
    //    }
    //
    @Override
    public void setMatch(BuildingMatch match) {
        this.match = match;
    }

    @Override
    public BuildingMatch getMatch() {
        return match;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("OdBuilding ").append(getBuildingId());
        for (OdAddressNode a :addressNodes) {
            sb.append("\n").append(a.toString());
        }
        return sb.toString();
    }
}
