package org.openstreetmap.josm.plugins.ods.od;

import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddress;

public interface OdAddressModifier<T extends OdAddress> {
    public void modify(T address);
    public Class<T> getTargetType();
}
