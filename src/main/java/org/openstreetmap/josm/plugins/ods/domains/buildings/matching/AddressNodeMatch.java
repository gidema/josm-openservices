package org.openstreetmap.josm.plugins.ods.domains.buildings.matching;

import java.util.Collection;

import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OsmAddressNode;
import org.openstreetmap.josm.plugins.ods.matching.MatchImpl;

public class AddressNodeMatch extends MatchImpl<OdAddressNode, OsmAddressNode> {
    private AddressNodeMatch(OdAddressNode an1, OsmAddressNode an2) {
        super(an1, an2);
    }

    public AddressNodeMatch(OdAddressNode odNode, Collection<OsmAddressNode> osmNodes) {
        super(odNode, osmNodes);
    }

    public static void create(OdAddressNode an1, OsmAddressNode an2) {
        AddressNodeMatch match = new AddressNodeMatch(an1, an2);
        an1.setMatch(match);
        an2.setMatch(match);
    }

    public static void create(OdAddressNode odNode, Collection<OsmAddressNode> osmNodes) {
        AddressNodeMatch match = new AddressNodeMatch(odNode, osmNodes);
        odNode.setMatch(match);
        osmNodes.forEach(node -> node.setMatch(match));
    }
}