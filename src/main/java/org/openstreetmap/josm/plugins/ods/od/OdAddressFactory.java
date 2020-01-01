package org.openstreetmap.josm.plugins.ods.od;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddress;

public interface OdAddressFactory {
    public OdAddress create(SimpleFeature feature);
//    public Class<? extends OdAddress> getTargetType();
    public void addModifier(OdAddressModifier<?> modifier);
}
