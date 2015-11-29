package org.openstreetmap.josm.plugins.ods.matching;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.Matcher;
import org.openstreetmap.josm.plugins.ods.ODS;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.entities.EntityStore;
import org.openstreetmap.josm.plugins.ods.entities.actual.Address;
import org.openstreetmap.josm.plugins.ods.entities.actual.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;

public class AddressNodeMatcher implements Matcher<AddressNode> {
    private OdsModule module;
    
    private Map<Object, Match<AddressNode>> addressNodeMatches = new HashMap<>();
    private EntityStore<AddressNode> odAddressNodeStore;
    private EntityStore<AddressNode> osmAddressNodeStore;
    private List<AddressNode> unidentifiedOsmAddressNodes = new LinkedList<>();
    private List<AddressNode> unmatchedOpenDataAddressNodes = new LinkedList<>();
    private List<AddressNode> unmatchedOsmAddressNodes = new LinkedList<>();

    public AddressNodeMatcher(OdsModule module) {
        super();
        this.module = module;
        odAddressNodeStore = module.getOpenDataLayerManager().getEntityStore(AddressNode.class);
        osmAddressNodeStore = module.getOsmLayerManager().getEntityStore(AddressNode.class);
    }

    public void run() {
        matchBuildingAddressNodes();
        matchOtherAddressNodes();
    }
    
    /**
     * Try to match address nodes for matching buildings
     */
    private void matchBuildingAddressNodes() {
        EntityStore<Building> buildingStore = module.getOpenDataLayerManager().getEntityStore(Building.class);
        for (Building building : buildingStore) {
            if (building.getMatch() != null && building.getMatch().isSimple()) {
                matchAddresses(building.getMatch());
            }
        }
    }
    
    private void matchAddresses(BuildingMatch match) {
        Building odBuilding = match.getOpenDataEntity();
        Building osmBuilding = match.getOsmEntity();
        Map<AddressKey, AddressNode> nodes1 = new HashMap<>();
        for (AddressNode n1 : odBuilding.getAddressNodes()) {
            nodes1.put(new AddressKey(n1), n1);
        }
        for (AddressNode n2 : osmBuilding.getAddressNodes()) {
            AddressKey k2 = new AddressKey(n2);
            AddressNode n1 = nodes1.get(k2);
            if (n1 != null) {
                matchAddressNodes(n2, n1);
            }
        }
    }

    private void matchAddressNodes(AddressNode an1,
            AddressNode an2) {
        
        if (Objects.equals(an1.getHouseNumber(), an2.getHouseNumber())
                && Objects.equals(an1.getPostcode(), an2.getPostcode())) {
            AddressNodeMatch match = new AddressNodeMatch(an1, an2);
            match.analyze();
            match.updateMatchTags();
            addressNodeMatches.put(match.getId(), match);
        }
    }

    private void matchOtherAddressNodes() {
        unmatchedOpenDataAddressNodes.clear();
        for (AddressNode addressNode : odAddressNodeStore) {
            if (addressNode.getMatch() == null) {
                unmatchedOpenDataAddressNodes.add(addressNode);
            }
        }
        unmatchedOsmAddressNodes.clear();
        for (AddressNode addressNode : osmAddressNodeStore) {
            if (addressNode.getMatch() == null) {
                unmatchedOsmAddressNodes.add(addressNode);
            };
        }
        analyze();
    }

//    private void processOpenDataAddressNode(AddressNode odAddressNode) {
//        Long id = (Long) odAddressNode.getReferenceId();
//        Match<AddressNode> match = addressNodeMatches.get(id);
//        if (match != null) {
//            match.addOpenDataEntity(odAddressNode);
//            odAddressNode.setMatch(match);
//            return;
//        }
//        List<AddressNode> osmAddressNodes = osmAddressNodeStore.g;
//        if (osmBuildings.size() > 0) {
//            match = new BuildingMatch(osmBuildings.get(0), odBuilding);
//            for (int i=1; i<osmBuildings.size() ; i++) {
//                Building osmBuilding = osmBuildings.get(i);
//                osmBuilding.setMatch(match);
//                match.addOsmEntity(osmBuilding);
//            }
//            buildingMatches.put(id, match);
//        } else {
//            unmatchedOpenDataBuildings.add(odBuilding);
//        }
//    }
//
//    private void processOsmAddressNode(AddressNode osmAddressNode) {
//        Object id = osmBuilding.getReferenceId();
//        if (id == null) {
//            unidentifiedOsmBuildings.add(osmBuilding);
//            return;
//        }
//        Long l;
//        try {
//            l = (Long)id;
//        }
//        catch (@SuppressWarnings("unused") Exception e) {
//            unidentifiedOsmBuildings.add(osmBuilding);
//            return;
//        }
//        List<Building> odBuildings = odBuildingStore.getById(l);
//        if (odBuildings.size() > 0) {
//            Match<Building> match = new BuildingMatch(osmBuilding, odBuildings.get(0));
//            for (int i=1; i<odBuildings.size(); i++) {
//                match.addOpenDataEntity(odBuildings.get(i));
//            }
//            buildingMatches.put(l, match);
//        } else {
//            unmatchedOsmBuildings.add(osmBuilding);
//        }
//    }
    
    public void analyze() {
        for (Match<AddressNode> match : addressNodeMatches.values()) {
            if (match.isSimple()) {
                match.analyze();
                match.updateMatchTags();
            }
        }
        for (AddressNode addressNode: unmatchedOpenDataAddressNodes) {
            OsmPrimitive osm = addressNode.getPrimitive();
            if (osm != null) {
                osm.put(ODS.KEY.IDMATCH, "false");
                osm.put(ODS.KEY.STATUS, addressNode.getStatus().toString());
            }
        }
    }

    @Override
    public void reset() {
        addressNodeMatches.clear();
        unidentifiedOsmAddressNodes.clear();
        unmatchedOpenDataAddressNodes.clear();
        unmatchedOsmAddressNodes.clear();
    }

    private static class AddressKey {
        private Integer houseNumber;
        private String postcode;
        private Character houseLetter;
        private String houseNumberExtra;
        
        public AddressKey(Address address) {
            this.postcode = address.getPostcode();
            this.houseNumber = address.getHouseNumber();
            this.houseLetter = address.getHouseLetter();
            this.houseNumberExtra = address.getHouseNumberExtra();
        }

        @Override
        public int hashCode() {
            return Objects.hash(postcode, houseNumber, houseLetter, houseNumberExtra);
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof AddressKey)) return false;
            AddressKey key = (AddressKey) obj;
            return Objects.equals(houseNumber, key.houseNumber)
                    && Objects.equals(postcode, key.postcode)
                    && Objects.equals(houseNumberExtra, key.houseNumberExtra);
        }
    }
}
