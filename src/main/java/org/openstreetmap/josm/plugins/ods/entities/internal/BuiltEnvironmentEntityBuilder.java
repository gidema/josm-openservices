package org.openstreetmap.josm.plugins.ods.entities.internal;

import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.ods.entities.BuildException;
import org.openstreetmap.josm.plugins.ods.entities.EntityStore;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.BuiltEnvironmentEntitySet;
import org.openstreetmap.josm.plugins.ods.issue.Issue;

public class BuiltEnvironmentEntityBuilder {
    BuiltEnvironmentEntitySet entitySet;
    DataSet dataset;
    EntityStore buildings;
    EntityStore addresses;
    

    public BuiltEnvironmentEntityBuilder(InternalDataLayer dataLayer) {
        entitySet = new BuiltEnvironmentEntitySet(dataLayer.getEntitySet());
        dataset = dataLayer.data;
        buildings = entitySet.getBuildings();
        addresses = entitySet.getAddresses();
    }

    public void build() throws BuildException {
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

    private void build(Node node) {
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
            InternalBuilding building = new InternalBuilding(way);
            building.build();
            entitySet.getBuildings().add(building);
        }
    }

    private void buildAddress(Node node) {
        if (addresses.get(node.getId()) == null) {
            InternalAddress address = new InternalAddress(node);
            address.build();
            entitySet.getAddresses().add(address);
        }
    }

    private void buildBuilding(Relation relation) throws BuildException {
        if (buildings.get(relation.getId()) == null) {
            InternalBuilding building = new InternalBuilding(relation);
            building.build();
            entitySet.getBuildings().add(building);
        }
    }
}
