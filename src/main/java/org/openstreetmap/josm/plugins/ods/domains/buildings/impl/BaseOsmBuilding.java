package org.openstreetmap.josm.plugins.ods.domains.buildings.impl;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.openstreetmap.josm.plugins.ods.domains.buildings.BuildingStatus;
import org.openstreetmap.josm.plugins.ods.domains.buildings.BuildingType;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OsmAddress;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OsmAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OsmBuilding;
import org.openstreetmap.josm.plugins.ods.domains.buildings.matching.BuildingMatch;
import org.openstreetmap.josm.plugins.ods.domains.places.OsmCity;
import org.openstreetmap.josm.plugins.ods.entities.impl.AbstractOsmEntity;

public class BaseOsmBuilding extends AbstractOsmEntity implements OsmBuilding {
    private Long buildingId;
    private OsmAddress address;
    private final List<OsmAddressNode> addressNodes = new LinkedList<>();
    private BuildingStatus buildingStatus;
    private BuildingType buildingType = BuildingType.UNCLASSIFIED;
    private String startDate;
    private final Set<OsmBuilding> neighbours = new HashSet<>();
    private OsmCity city;
    private BuildingMatch buildingMatch;

    public void setBuildingId(Long buildingId) {
        this.buildingId = buildingId;
    }

    @Override
    public Long getBuildingId() {
        return buildingId;
    }

    @Override
    public void setStartDate(String startDate) {
        this.startDate = startDate;
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
    public BuildingStatus getStatus() {
        return buildingStatus;
    }

    public void setStatus(BuildingStatus buildingStatus) {
        this.buildingStatus = buildingStatus;
    }

    @Override
    public OsmCity getCity() {
        return city;
    }

    public void setAddress(OsmAddress address) {
        this.address = address;
    }

    @Override
    public OsmAddress getAddress() {
        return address;
    }

    @Override
    public List<OsmAddressNode> getAddressNodes() {
        return addressNodes;
    }

    @Override
    public Set<OsmBuilding> getNeighbours() {
        return neighbours;
    }

    @Override
    public void setMatch(BuildingMatch buildingMatch) {
        this.buildingMatch = buildingMatch;
    }

    @Override
    public BuildingMatch getMatch() {
        return buildingMatch;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("OdBuilding ").append(getBuildingId());
        for (OsmAddressNode a :addressNodes) {
            sb.append("\n").append(a.toString());
        }
        return sb.toString();
    }
}
