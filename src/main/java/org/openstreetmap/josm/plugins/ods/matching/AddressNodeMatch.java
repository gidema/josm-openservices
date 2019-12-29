package org.openstreetmap.josm.plugins.ods.matching;

import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddress;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OsmAddress;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OsmAddressNode;

public class AddressNodeMatch extends MatchImpl<OsmAddressNode, OdAddressNode> {
    Object id;
    private MatchStatus houseNumberMatch;
    private MatchStatus fullHouseNumberMatch;
    private MatchStatus postcodeMatch;
    private MatchStatus streetMatch;
    private MatchStatus cityMatch;

    public AddressNodeMatch(OsmAddressNode an1, OdAddressNode an2) {
        super(an1, an2);
        this.id = an2.getReferenceId();
        if (an1.getReferenceId() == null) {
            an1.setReferenceId(id);
        }
        an1.setMatch(this);
        an2.setMatch(this);
    }

    @Override
    public Object getId() {
        return id;
    }

    @Override
    public MatchStatus getGeometryMatch() {
        // If the addressNodes are in the same building, we don't look at
        // their exact location
        return MatchStatus.match(getOsmEntity().getBuilding().getReferenceId(),
                getOpenDataEntity().getBuilding().getReferenceId());
    }

    @Override
    public MatchStatus getStatusMatch() {
        return MatchStatus.match(getOsmEntity().getStatus(), getOpenDataEntity().getStatus());
    }

    @Override
    public MatchStatus getAttributeMatch() {
        return MatchStatus.combine(houseNumberMatch, fullHouseNumberMatch, postcodeMatch,
                streetMatch, cityMatch);
    }

    @Override
    public void analyze() {
        OsmAddress ad1 = getOsmEntity().getAddress();
        OdAddress ad2 = getOpenDataEntity().getAddress();
        houseNumberMatch = MatchStatus.match(ad1.getHouseNumber().getMainHouseNumber(), ad2.getHouseNumber().getMainHouseNumber());
        fullHouseNumberMatch = MatchStatus.match(ad1.getHouseNumber().getFullHouseNumber(), ad2.getHouseNumber().getFullHouseNumber());
        postcodeMatch = MatchStatus.match(ad1.getPostcode(), ad2.getPostcode());
        streetMatch = MatchStatus.match(ad1.getStreetName(), ad2.getStreetName());
        cityMatch = MatchStatus.match(ad1.getCityName(), ad2.getCityName());
    }
}