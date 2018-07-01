package org.openstreetmap.josm.plugins.ods.matching;

import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OsmAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OdAddressNodeStore;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OsmAddressNodeStore;
import org.openstreetmap.josm.plugins.ods.entities.impl.ZeroOneMany;

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
    private final OsmAddressNodeStore osmAddressNodeStore;
    private final OdAddressNodeStore odAddressNodeStore;
    private final List<OsmAddressNode> unidentifiedOsmAddressNodes = new LinkedList<>();
    private final List<OdAddressNode> unmatchedOpenDataAddressNodes = new LinkedList<>();
    private final List<OsmAddressNode> unmatchedOsmAddressNodes = new LinkedList<>();

    public AddressNodeMatcher(OsmAddressNodeStore osmAddressNodeStore,
            OdAddressNodeStore odAddressNodeStore) {
        super();
        this.odAddressNodeStore = odAddressNodeStore;
        this.osmAddressNodeStore = osmAddressNodeStore;
    }

    @Override
    public void run() {
        matchPcHousenumber();
        matchOtherAddressNodes();
    }

    private void matchPcHousenumber() {
        odAddressNodeStore.forEach(odNode -> {
            AddressNodeMatch match = odNode.getMatch();
            if (match == null) {
                if (odNode.getPostcode() != null) {
                    PcHousenumberAddressKey key = new PcHousenumberAddressKey(odNode);
                    ZeroOneMany<OsmAddressNode> results = osmAddressNodeStore.lookup(key);
                    if (results.isOne()) {
                        OsmAddressNode osmNode = results.getOne();
                        match = osmNode.getMatch();
                        if (match != null) {
                            match.addOpenDataEntity(odNode);
                        }
                        else {
                            match = new AddressNodeMatch(osmNode, odNode);
                        }
                    }
                }
            }
        });
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
    }

    @Override
    public void reset() {
        unidentifiedOsmAddressNodes.clear();
        unmatchedOpenDataAddressNodes.clear();
        unmatchedOsmAddressNodes.clear();
    }
}
