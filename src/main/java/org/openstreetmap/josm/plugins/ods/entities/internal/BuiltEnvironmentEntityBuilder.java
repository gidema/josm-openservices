package org.openstreetmap.josm.plugins.ods.entities.internal;

import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.ods.entities.BuildException;
import org.openstreetmap.josm.plugins.ods.entities.DefaultEntitySet;
import org.openstreetmap.josm.plugins.ods.entities.EntityFactory;
import org.openstreetmap.josm.plugins.ods.entities.EntitySet;
import org.openstreetmap.josm.plugins.ods.entities.EntityStore;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Building;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.BuiltEnvironment;
import org.openstreetmap.josm.plugins.ods.issue.Issue;

public class BuiltEnvironmentEntityBuilder {
    BuiltEnvironment environment;
    EntitySet newEntities;
    DataSet dataset;
    EntityStore<Building> buildings;
    EntityStore<AddressNode> addresses;
    EntityFactory entityFactory;
    

    public BuiltEnvironmentEntityBuilder(InternalDataLayer dataLayer) {
        environment = new BuiltEnvironment(dataLayer.getEntitySet());
        dataset = dataLayer.getOsmDataLayer().data;
        buildings = environment.getBuildings();
        addresses = environment.getAddresses();
    }

    public void setEntityFactory(EntityFactory entityFactory) {
        this.entityFactory = entityFactory;
    }

    public EntitySet getNewEntities() {
        return newEntities;
    }

    public void build() throws BuildException {
        newEntities = new DefaultEntitySet();
        List<Issue> issues = new LinkedList<Issue>();
        for (OsmPrimitive primitive : dataset.allPrimitives()) {
            try {
                if (primitive.isIncomplete()) {
                    continue;
                }
                switch (primitive.getType()) {
                case NODE:
                    build((Node)primitive);
                    break;
                case RELATION:
                    build((Relation)primitive);
                    break;
                case WAY:
                    build((Way)primitive);
                    break;
                default:
                    break;
                }
            } catch (BuildException e) {
                issues.add(e.getIssue());
            }
        }
        if (!issues.isEmpty()) {
            throw new BuildException(issues);
        }
    }

    private void build(Way way) throws BuildException {
        if (way.hasKey("building")) {
            buildBuilding(way);
        }
    }

    private void build(Relation relation) throws BuildException {
        if (relation.hasKey("building")) {
            buildBuilding(relation);
        }
        
    }

    private void build(Node node) throws BuildException {
        if (node.hasKey("addr:housenumber")) {
            buildAddress(node);
        }
        else if (node.hasKey("building")) {
            buildBuilding(node);
        }
    }

    private void buildBuilding(Node node) {
        // TODO Auto-generated method stub
        
    }

    private void buildBuilding(Way way) throws BuildException {
        if (buildings.get(way.getId()) == null) {
            Building building = (Building) entityFactory.buildEntity("building", way);
            buildings.add(building);
            newEntities.add(building);
        }
    }

    private void buildAddress(Node node) throws BuildException {
        if (addresses.get(node.getId()) == null) {
            AddressNode address = (AddressNode) entityFactory.buildEntity("address", node);
//            address.build();
            addresses.add(address);
            newEntities.add(address);
        }
    }

    private void buildBuilding(Relation relation) throws BuildException {
        if (buildings.get(relation.getId()) == null) {
            Building building = (Building) entityFactory.buildEntity("building", relation);
            building.build();
            buildings.add(building);
            newEntities.add(building);
        }
    }
}
