package org.openstreetmap.josm.plugins.ods.entities.internal;

import java.util.List;

import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.RelationMember;
import org.openstreetmap.josm.plugins.ods.DataLayer;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.AssociatedStreet;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.AssociatedStreetImpl;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Building;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.BuiltEnvironment;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Street;

public class AssociatedStreetBuilder {
    private List<AssociatedStreet> associatedStreets;
    private BuiltEnvironment be;
     
    public void build(DataLayer dataLayer) {
        be = new BuiltEnvironment(dataLayer.getEntitySet());
        DataSet data = dataLayer.getOsmDataLayer().data;
        for (Relation relation : data.getRelations()) {
            if ("associatedStreet".equals(relation.get("type"))) {
                associatedStreets.add(buildAssociatedStreet(relation));
            }
        }
    }

    private AssociatedStreet buildAssociatedStreet(Relation relation) {
        AssociatedStreetImpl as = new AssociatedStreetImpl();
        as.setName(relation.get("name"));
        for (RelationMember member : relation.getMembers()) {
            if ("house".equals(member.getRole()) || "address".equals(member.getRole())) {
                addHouse(as, member.getUniqueId());
            }
            else if ("street".equals(member.getRole())) {
                addStreet(as, member.getUniqueId());
            }
        }
        return as;
    }

    private void addStreet(AssociatedStreetImpl as, long id) {
        Street street = be.getStreets().get(id);
        if (street != null) {
            as.addStreet(street);
        }
    }

    private void addHouse(AssociatedStreetImpl as, long id) {
        Building building = be.getBuildings().get(id);
        if (building != null) {
            as.addBuilding(building);
            return;
        }
        AddressNode node = be.getAddresses().get(id);
        if (node != null) {
            as.addNode(node);
        }
    }
    
    
}