package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.openstreetmap.josm.plugins.ods.entities.AbstractEntity;

public abstract class BuildingImpl extends AbstractEntity implements Building {
    private Address address;
    private List<AddressNode> addressNodes = new LinkedList<>();
    private BuildingType buildingType = BuildingType.UNCLASSIFIED;
    private boolean underConstruction;
    private boolean deleted = false;
    private String startDate;
    private Set<Building> neighbours = new HashSet<>();
    private City city;
    
    @Override
    public String getStartDate() {
         return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
    
    public void setUnderConstruction(boolean underConstruction) {
        this.underConstruction = underConstruction;
    }

    @Override
    public boolean isUnderConstruction() {
        return underConstruction;
    }

    @Override
    public BuildingType getBuildingType() {
        return buildingType;
    }

    public void setBuildingType(BuildingType buildingType) {
        this.buildingType = buildingType;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public City getCity() {
        return city;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
    
    @Override
    public Address getAddress() {
        return address;
    }

    @Override
    public List<AddressNode> getAddressNodes() {
        return addressNodes;
    }

    @Override
    public Set<Building> getNeighbours() {
        return neighbours;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Building ").append(getReferenceId());
        for (AddressNode a :addressNodes) {
            sb.append("\n").append(a.toString());
        }
        return sb.toString();
    }
}
