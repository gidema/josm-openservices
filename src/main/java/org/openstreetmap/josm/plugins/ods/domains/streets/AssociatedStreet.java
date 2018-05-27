package org.openstreetmap.josm.plugins.ods.domains.streets;

import java.util.Collection;

import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OsmAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OsmBuilding;

public interface AssociatedStreet {
    public Relation getOsmPrimitive();
    public String getName();
    public Collection<OsmBuilding> getBuildings();
    public Collection<OsmAddressNode> getAddressNodes();
    public Collection<OsmStreet> getStreets();
}
