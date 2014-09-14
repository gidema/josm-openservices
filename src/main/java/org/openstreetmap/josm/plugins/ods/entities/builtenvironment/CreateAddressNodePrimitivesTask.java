package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;

import org.openstreetmap.josm.plugins.ods.PrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.tasks.Task;

/**
 * This task creates the OSM primitives and draws them on the datalayer.
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class CreateAddressNodePrimitivesTask implements Task {
    private GtAddressNodeStore addressNodeStore;
    private PrimitiveBuilder<AddressNode> primitiveBuilder;

    public CreateAddressNodePrimitivesTask(GtAddressNodeStore addressNodeStore,
            PrimitiveBuilder<AddressNode> primitiveBuilder) {
        super();
        this.addressNodeStore = addressNodeStore;
        this.primitiveBuilder = primitiveBuilder;
    }


    @Override
    public void run() {
        for (AddressNode addressNode : addressNodeStore) {
            if (!addressNode.isIncomplete()) {
                primitiveBuilder.createPrimitives(addressNode);
            }
        }
    }

}
