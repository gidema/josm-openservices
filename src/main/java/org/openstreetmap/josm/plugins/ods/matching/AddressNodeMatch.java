package org.openstreetmap.josm.plugins.ods.matching;

import java.util.Objects;

import org.openstreetmap.josm.plugins.ods.entities.actual.AddressNode;

public class AddressNodeMatch extends MatchImpl<AddressNode> {
    Object id;
    private boolean houseNumberMatch;
    private boolean fullHouseNumberMatch;
    private boolean postcodeMatch;
    private boolean streetMatch;
    private boolean cityMatch;

    public AddressNodeMatch(AddressNode an1, AddressNode an2) {
        super(an1, an2);
        this.id = an2.getReferenceId();
        if (an1.getReferenceId() == null) {
            an1.setReferenceId(id);
        }
        houseNumberMatch = Objects.equals(an1.getHouseNumber(), an2.getHouseNumber());
        fullHouseNumberMatch = Objects.equals(an1.getFullHouseNumber(), an2.getFullHouseNumber());
        postcodeMatch = Objects.equals(an1.getPostcode(), an2.getPostcode());
        streetMatch = Objects.equals(an1.getStreet(), an2.getStreet());
        cityMatch = Objects.equals(an1.getCityName(), an2.getCityName());
    }

    public Object getId() {
        return id;
    }

    @Override
    public boolean isGeometryMatch() {
        // If the addressNodes are in the same building, we don't look at
        // their exact location
        if (Objects.equals(getOsmEntity().getBuilding().getReferenceId(),
                getOpenDataEntity().getBuilding().getReferenceId())) {
            return true;
        }
        // TODO Check other cases
        return false;
    }

    @Override
    public boolean isAttributeMatch() {
        return houseNumberMatch && fullHouseNumberMatch && postcodeMatch
            && streetMatch && cityMatch;
    }

    @Override
    public void analyze() {
        // TODO Auto-generated method stub
        
    }
}