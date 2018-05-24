package org.openstreetmap.josm.plugins.ods.matching;

import org.openstreetmap.josm.plugins.ods.entities.actual.AddressNode;

public class AddressNodeMatch extends MatchImpl<AddressNode> {
    Object id;
    private MatchStatus houseNumberMatch;
    private MatchStatus fullHouseNumberMatch;
    private MatchStatus postcodeMatch;
    private MatchStatus streetMatch;
    private MatchStatus cityMatch;

    public AddressNodeMatch(AddressNode an1, AddressNode an2) {
        super(an1, an2);
        this.id = an2.getReferenceId();
        if (an1.getReferenceId() == null) {
            an1.setReferenceId(id);
        }
    }

    @Override
    public Class<AddressNode> getEntityClass() {
        return AddressNode.class;
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
        AddressNode an1 = getOsmEntity();
        AddressNode an2 = getOpenDataEntity();
        houseNumberMatch = MatchStatus.match(an1.getHouseNumber(), an2.getHouseNumber());
        fullHouseNumberMatch = MatchStatus.match(an1.getFullHouseNumber(), an2.getFullHouseNumber());
        postcodeMatch = MatchStatus.match(an1.getPostcode(), an2.getPostcode());
        streetMatch = MatchStatus.match(an1.getStreet(), an2.getStreet());
        cityMatch = MatchStatus.match(an1.getCityName(), an2.getCityName());
    }
}