package org.openstreetmap.josm.plugins.ods.domains.buildings.matching;

import java.util.HashSet;
import java.util.Set;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.ODS;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuilding;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OsmAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OsmBuilding;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OdAddressNodeStore;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OsmAddressNodeStore;
import org.openstreetmap.josm.plugins.ods.entities.EntityStatus;
import org.openstreetmap.josm.plugins.ods.entities.impl.ZeroOneMany;
import org.openstreetmap.josm.plugins.ods.matching.Matcher;

/**
 * Find a matching OSM address node for each OD address node. If more than 1
 * matching OSM address is found, the best match is chosen and the other OSM
 * address will stay unmatched. Duplicate OSM addresses will be handled
 * elsewhere.
 *
 * <b>Strategy</b> Because this class is targeted on addresses on The
 * Netherlands, we can the advantage of the unique Postcode/address combination.
 * For each OD addresses node with a postcode, we look for matching OSM address
 * nodes using an index on the postcode/address key in the OSM addressNode
 * store. For OD addresses nodes without a postcode, we take the bound OD
 * building, find a matching OSM building and check the bound OSM addresses.
 * After this bulk matching process, we handle the numerous special cases.
 *
 * @author Gertjan Idema
 *
 */
public class AddressNodeMatcher implements Matcher {
    private final OdAddressNodeStore odAddressNodeStore;
    private final OsmAddressNodeStore osmAddressNodeStore;

    public AddressNodeMatcher(OsmAddressNodeStore osmAddressNodeStore,
            OdAddressNodeStore odAddressNodeStore) {
        super();
        this.osmAddressNodeStore = osmAddressNodeStore;
        this.odAddressNodeStore = odAddressNodeStore;
    }

    @Override
    public void run() {
        for (OdAddressNode node : odAddressNodeStore) {
            if (node.getMatch() == null) {
                match(node);
            }
        }
        analyze();
    }

    private void match(OdAddressNode odAddressNode) {
        if (odAddressNode.getPostcode() != null) {
            matchWithPostcode(odAddressNode);
        } else {
            matchWithoutPostcode(odAddressNode);
        }
    }

    private void matchWithPostcode(OdAddressNode odAddressNode) {
        PcHousenumberAddressKey key = new PcHousenumberAddressKey(
                odAddressNode.getAddress());
        ZeroOneMany<OsmAddressNode> osmAddressNodes = osmAddressNodeStore
                .lookup(key);
        switch (osmAddressNodes.getCardinality()) {
        case Zero:
            break;
        case One:
            handleMatch(odAddressNode, osmAddressNodes.getOne());
            break;
        case Many:
            handleMatch(odAddressNode, osmAddressNodes.getMany());
            break;
        }
    }

    private static void handleMatch(OdAddressNode odAddressNode,
            OsmAddressNode osmAddressNode) {
        AddressNodeMatch match = osmAddressNode.getMatch();
        if (match == null) {
            AddressNodeMatch.create(odAddressNode, osmAddressNode);
        }
        else {
            match.addOdEntity(odAddressNode);
        }
    }

    private static void handleMatch(OdAddressNode odAddressNode, Set<OsmAddressNode> many) {
        Set<AddressNodeMatch> matches = new HashSet<>();
        many.forEach(osmAddressNode -> {
            AddressNodeMatch match = osmAddressNode.getMatch();
            if (match != null) {
                matches.add(match);
            }
        });
        if (matches.size() == 0) {
            AddressNodeMatch.create(odAddressNode, many);
        }
    }

    private static void matchWithoutPostcode(OdAddressNode odAddressNode) {
        OdBuilding odBuilding = odAddressNode.getBuilding();
        BuildingMatch match = odBuilding.getMatch();
        if (match != null) {
            ZeroOneMany<OsmBuilding> osmBuildings = match.getOsmEntities();
            switch (osmBuildings.getCardinality()) {
            case Zero:
                break;
            case One:
                OsmBuilding osmBuilding = osmBuildings.getOne();
                matchWithoutPostcode(odAddressNode, osmBuilding);
                break;
            case Many:
                break;
            }
        }
    }

    private static void matchWithoutPostcode(OdAddressNode odAddressNode, OsmBuilding osmBuilding) {
        for (OsmAddressNode osmAddressNode : osmBuilding.getAddressNodes()) {
            if (osmAddressNode.getPostcode() == null) {
                if (osmAddressNode.getAddress().getFullHouseNumber().equals(
                        odAddressNode.getAddress().getFullHouseNumber())) {
                    handleMatch(odAddressNode, osmAddressNode);
                    break;
                }
            }
        }
    }

    public void analyze() {
        for (OdAddressNode addressNode : odAddressNodeStore) {
            updateOdsTags(addressNode);
        }
    }

    private static void updateOdsTags(OdAddressNode odAddressNode) {
        OsmPrimitive osm = odAddressNode.getPrimitive();
        if (osm == null) {
            return;
        }
        AddressNodeMatch match = odAddressNode.getMatch();
        if (match == null
                && odAddressNode.getStatus() != EntityStatus.REMOVED) {
            osm.put(ODS.KEY.IDMATCH, "false");
            osm.put(ODS.KEY.STATUS, odAddressNode.getStatus().toString());
        } else {
            if (odAddressNode.getStatus() != EntityStatus.REMOVED) {
                osm.put(ODS.KEY.IDMATCH, "true");
                osm.put(ODS.KEY.STATUS, odAddressNode.getStatus().toString());
            }
        }
    }

    @Override
    public void reset() {
        // No action required
    }
}
