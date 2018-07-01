package org.openstreetmap.josm.plugins.ods.matching;

import java.util.Objects;

import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.Address;

public class PcHousenumberAddressKey {
    private final String postcode;
    private final Integer houseNumber;
    private final Character houseLetter;
    private final String houseNumberExtra;

    public PcHousenumberAddressKey(Address address) {
        this(address.getPostcode(), address.getHouseNumber(), address.getHouseLetter(), address.getHouseNumberExtra());
    }

    // TODO consider wrapping
    public PcHousenumberAddressKey(String postcode, Integer houseNumber,
            Character houseLetter, String houseNumberExtra) {
        super();
        this.postcode = (postcode == null ? null :postcode.intern());
        this.houseNumber = houseNumber;
        this.houseLetter = houseLetter;
        this.houseNumberExtra = (houseNumberExtra == null ? null :houseNumberExtra.intern());
    }

    @Override
    public int hashCode() {
        return Objects.hash(postcode, houseNumber, houseNumber, houseNumberExtra);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof PcHousenumberAddressKey)) {
            return false;
        }
        PcHousenumberAddressKey other = (PcHousenumberAddressKey) obj;
        return Objects.equals(other.postcode, postcode) &&
                Objects.equals(other.houseNumber, houseNumber) &&
                Objects.equals(other.houseLetter, houseLetter) &&
                Objects.equals(other.houseNumberExtra, houseNumberExtra);
    }
}
