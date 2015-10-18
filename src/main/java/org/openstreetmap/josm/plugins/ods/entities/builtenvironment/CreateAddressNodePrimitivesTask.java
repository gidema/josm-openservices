package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;

import org.openstreetmap.josm.plugins.ods.Context;
import org.openstreetmap.josm.plugins.ods.PrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.entities.actual.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.foreign.OpenDataAddressNodeStore;
import org.openstreetmap.josm.plugins.ods.tasks.Task;

/**
 * This task creates the OSM primitives and draws them on the datalayer.
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class CreateAddressNodePrimitivesTask implements Task {
    private OpenDataAddressNodeStore addressNodeStore;
    private PrimitiveBuilder<AddressNode> primitiveBuilder;

    public CreateAddressNodePrimitivesTask(OpenDataAddressNodeStore addressNodeStore,
            PrimitiveBuilder<AddressNode> primitiveBuilder) {
        super();
        this.addressNodeStore = addressNodeStore;
        this.primitiveBuilder = primitiveBuilder;
    }


    @Override
    public void run(Context ctx) {
        for (AddressNode addressNode : addressNodeStore) {
            if (!addressNode.isIncomplete()) {
                primitiveBuilder.createPrimitive(addressNode);
            }
        }
    }

}
