package org.openstreetmap.josm.plugins.ods.entities.managers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.openstreetmap.josm.plugins.ods.entities.actual.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.opendata.OpenDataAddressNodeStore;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.osm.OsmAddressNodeStore;
import org.openstreetmap.josm.plugins.ods.matching.AddressNodeMatch;

public class AddressNodeManager {
    private final DataManager dataManager;
    private OsmAddressNodeStore osmAddressNodes = new OsmAddressNodeStore();
    private OpenDataAddressNodeStore foreignAddressNodes = new OpenDataAddressNodeStore();
    private List<AddressNode> unidentifiedAddressNodes = new LinkedList<>();
    private Map<Long, AddressNodeMatch> addressNodeMatches = new HashMap<>();
    private OsmAddressNodeStore unmatchedOsmAddressNodes = new OsmAddressNodeStore();
    private OpenDataAddressNodeStore unmatchedForeignAddressNodes = new OpenDataAddressNodeStore();
    
    public AddressNodeManager(DataManager dataManager) {
        super();
        this.dataManager = dataManager;
    }
    
    public DataManager getDataManager() {
        return dataManager;
    }

    public Map<Long, AddressNodeMatch> getMatches() {
        return addressNodeMatches;
    }

    public OsmAddressNodeStore getOsmAddressNodes() {
        return osmAddressNodes;
    }

    public OpenDataAddressNodeStore getOpenDataAddressNodes() {
        return foreignAddressNodes;
    }

    public List<AddressNode> getUnidentifiedAddressNodes() {
        return unidentifiedAddressNodes;
    }

    public Map<Long, AddressNodeMatch> getAddressNodeMatches() {
        return addressNodeMatches;
    }

    public OsmAddressNodeStore getUnmatchedOsmAddressNodes() {
        return unmatchedOsmAddressNodes;
    }

    public OpenDataAddressNodeStore getUnmatchedForeignAddressNodes() {
        return unmatchedForeignAddressNodes;
    }
    
    private void addForeignAddressNode(AddressNode addressNode) {
        Long id = (Long) addressNode.getReferenceId();
        assert (id != null); // Foreign AddressNode should always have an id
        if (foreignAddressNodes.getById(id).size() > 0) {
            return; // The AddressNode already exist in the dataset
        }
//        AddressNode osmAddressNode = unmatchedOsmAddressNodes.getByReference(id);
//        if (osmAddressNode != null) {
//            AddressNodeMatch addressNodeMatch = new AddressNodeMatch(osmAddressNode, addressNode);
//            addressNodeMatches.put(id, addressNodeMatch);
//            unmatchedOsmAddressNodes.remove(osmAddressNode);
//        }
//        else {
//            // There is no matching Osm AddressNode for this Foreign AddressNode
//            unmatchedForeignAddressNodes.add(addressNode);
//        }
    }
    
    private void addOsmAddressNode(AddressNode osmAddressNode) {
        Long id = (Long) osmAddressNode.getReferenceId();
        if (id == null) {
            getUnidentifiedAddressNodes().add(osmAddressNode);
            return;
        }
//        AddressNode foreignAddressNode = unmatchedForeignAddressNodes.getByReference(id);
//        if (foreignAddressNode != null) {
//            AddressNodeMatch match = new AddressNodeMatch(osmAddressNode, foreignAddressNode);
//            addressNodeMatches.put(id, match);
//            unmatchedForeignAddressNodes.remove(foreignAddressNode);
//        }
//        else {
//            unmatchedOsmAddressNodes.add(osmAddressNode);
//        }
    }

    public Consumer<AddressNode> getOsmAddressNodeConsumer() {
        return new Consumer<AddressNode>() {

            @Override
            public void accept(AddressNode AddressNode) {
                addOsmAddressNode(AddressNode);
            }
        };
    }

    public Consumer<AddressNode> getForeignAddressNodeConsumer() {
        return new Consumer<AddressNode>() {

            @Override
            public void accept(AddressNode AddressNode) {
                addForeignAddressNode(AddressNode);
            }
        };
    }


}
