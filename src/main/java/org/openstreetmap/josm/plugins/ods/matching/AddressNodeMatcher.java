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
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuilding;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OsmAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OsmBuilding;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.Address;
import org.openstreetmap.josm.plugins.ods.entities.EntityStore;

/**
 * Matcher that tries to find matches between Open Data (OD) entities and entities that
 * are present on the OSM layer.
 * TODO This implementation is directed to address nodes in the Netherlands (BAG addresses) and
 * should therefore be moved to the ods-bag plug-in.
 *
 * @author Gertjan Idema
 *
 */
public class AddressNodeMatcher implements Matcher {
    private final OdsModule module;

    private final Map<Object, Match<OsmAddressNode, OdAddressNode>> addressNodeMatches = new HashMap<>();
    private final EntityStore<OsmAddressNode> osmAddressNodeStore;
    private final EntityStore<OdAddressNode> odAddressNodeStore;
    private final List<OsmAddressNode> unidentifiedOsmAddressNodes = new LinkedList<>();
    private final List<OdAddressNode> unmatchedOpenDataAddressNodes = new LinkedList<>();
    private final List<OsmAddressNode> unmatchedOsmAddressNodes = new LinkedList<>();

    public AddressNodeMatcher(OdsModule module) {
        super();
        this.module = module;
        odAddressNodeStore = module.getOpenDataLayerManager().getEntityStore(OdAddressNode.class);
        osmAddressNodeStore = module.getOsmLayerManager().getEntityStore(OsmAddressNode.class);
    }

    @Override
    public void run() {
        matchBuildingAddressNodes();
        matchOtherAddressNodes();
    }

    /**
     * Try to match address nodes for matching buildings
     */
    private void matchBuildingAddressNodes() {
        EntityStore<OdBuilding> buildingStore = module.getOpenDataLayerManager().getEntityStore(OdBuilding.class);
        for (OdBuilding building : buildingStore) {
            if (building.getMatch() != null && building.getMatch().isSimple()) {
                matchAddresses(building.getMatch());
            }
        }
    }

    private void matchAddresses(BuildingMatch match) {
        OsmBuilding osmBuilding = match.getOsmEntity();
        OdBuilding odBuilding = match.getOpenDataEntity();
        Map<AddressKey, OdAddressNode> odNodes = new HashMap<>();
        for (OdAddressNode anOd : odBuilding.getAddressNodes()) {
            odNodes.put(new AddressKey(anOd), anOd);
        }
        for (OsmAddressNode anOsm : osmBuilding.getAddressNodes()) {
            AddressKey key = new AddressKey(anOsm);
            OdAddressNode anOd = odNodes.get(key);
            if (anOd != null) {
                matchAddressNodes(anOsm, anOd);
            }
        }
    }

    private void matchAddressNodes(OsmAddressNode anOsm, OdAddressNode anOd) {
        Address adOsm = anOsm.getAddress();
        Address adOd = anOd.getAddress();

        if (Objects.equals(adOsm.getHouseNumber(), adOd.getHouseNumber())
                && Objects.equals(adOsm.getPostcode(), adOd.getPostcode())) {
            AddressNodeMatch match = new AddressNodeMatch(anOsm, anOd);
            match.analyze();
            match.updateMatchTags();
            addressNodeMatches.put(match.getId(), match);
        }
    }

    private void matchOtherAddressNodes() {
        unmatchedOpenDataAddressNodes.clear();
        for (OdAddressNode anOd : odAddressNodeStore) {
            if (anOd.getMatch() == null) {
                unmatchedOpenDataAddressNodes.add(anOd);
            }
        }
        unmatchedOsmAddressNodes.clear();
        for (OsmAddressNode anOsm : osmAddressNodeStore) {
            if (anOsm.getMatch() == null) {
                unmatchedOsmAddressNodes.add(anOsm);
            }
        }
        analyze();
    }

    public void analyze() {
        for (Match<OsmAddressNode, OdAddressNode> match : addressNodeMatches.values()) {
            if (match.isSimple()) {
                match.analyze();
                match.updateMatchTags();
            }
        }
        for (OdAddressNode addressNode: unmatchedOpenDataAddressNodes) {
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
        private final Integer houseNumber;
        private final String postcode;
        private final Character houseLetter;
        private final String houseNumberExtra;

        public AddressKey(OsmAddressNode an) {
            this(an.getAddress());
        }

        public AddressKey(OdAddressNode an) {
            this(an.getAddress());
        }

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
