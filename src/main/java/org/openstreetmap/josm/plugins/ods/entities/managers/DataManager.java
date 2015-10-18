package org.openstreetmap.josm.plugins.ods.entities.managers;

public class DataManager {
    private BuildingManager buildingManager;
    private AddressNodeManager addressNodeManager;

    public DataManager() {
        super();
        this.buildingManager = new BuildingManager(this);
        this.addressNodeManager = new AddressNodeManager(this);
    }

    public BuildingManager getBuildingManager() {
        return buildingManager;
    }

    public AddressNodeManager getAddressNodeManager() {
        return addressNodeManager;
    }
}
