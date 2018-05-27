package org.openstreetmap.josm.plugins.ods.domains.buildings.impl;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.openstreetmap.josm.plugins.ods.domains.buildings.BuildingType;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddress;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuilding;
import org.openstreetmap.josm.plugins.ods.domains.places.OdCity;
import org.openstreetmap.josm.plugins.ods.entities.impl.AbstractOdEntity;
import org.openstreetmap.josm.plugins.ods.matching.BuildingMatch;

public abstract class AbstractOdBuilding extends AbstractOdEntity implements OdBuilding {
    private OdAddress address;
    private final List<OdAddressNode> addressNodes = new LinkedList<>();
    private BuildingType buildingType = BuildingType.UNCLASSIFIED;
    private String startDate;
    private final Set<OdBuilding> neighbours = new HashSet<>();
    private OdCity city;
    private BuildingMatch match;

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
        sb.append("OdBuilding ").append(getReferenceId());
        for (OdAddressNode a :addressNodes) {
            sb.append("\n").append(a.toString());
        }
        return sb.toString();
    }
}
