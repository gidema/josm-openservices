package org.openstreetmap.josm.plugins.ods.matching.update;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.openstreetmap.josm.command.AddPrimitivesCommand;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.PrimitiveData;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.ods.ODS;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.context.OdsContextJob;
import org.openstreetmap.josm.plugins.ods.entities.OdEntity;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OdLayerManager;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmEntityBuilders;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmLayerManager;

/**
 * The importer imports objects from the OpenData layer to the Osm layer.
 *
 * TODO Use AddPrimitivesCommand
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class OdsImporter {
    private final OdsContext context;
    
    public OdsImporter(OdsContext context) {
        super();
        this.context = context;
    }

    public void doImport(Collection<OsmPrimitive> primitives) {
        OdsImportContext importContext = context.getComponent(OdsImportContext.class);
        OdLayerManager layerManager = context.getComponent(OdLayerManager.class);
        Set<OdEntity> entitiesToImport = new HashSet<>();
        for (OsmPrimitive primitive : primitives) {
            OdEntity entity = layerManager.getEntity(primitive);
            if (entity != null && entity.getMatch() == null
                    && entity.readyForImport()) {
                entitiesToImport.add(entity);
            }
            for (OsmPrimitive referrer : primitive.getReferrers()) {
                if (referrer.getType().equals(OsmPrimitiveType.RELATION)) {
                    OdEntity referrerEntity = layerManager.getEntity(referrer);
                    if (referrerEntity != null && referrerEntity.getMatch() == null
                            && referrerEntity.readyForImport()) {
                        entitiesToImport.add(referrerEntity);
                    }
                }
            }
        }
        importEntities(entitiesToImport);
        for ( OdsContextJob job : importContext.getPostImportJobs()) {
            job.run(context);
        }
    }

    private void importEntities(Set<OdEntity> entitiesToImport) {
        Set<OsmPrimitive> primitivesToImport = new HashSet<>();
        PrimitiveDataBuilder builder = new PrimitiveDataBuilder();
        for (OdEntity entity : entitiesToImport) {
            OsmPrimitive primitive = entity.getPrimitive();
            if (primitive != null) {
                if (primitive.getType().equals(OsmPrimitiveType.RELATION)) {
                    Relation relation = (Relation) primitive;
                    for (OsmPrimitive member : relation.getMemberPrimitives()) {
                        primitivesToImport.add(member);
                        builder.addPrimitive(member);
                    }
                }
                primitivesToImport.add(primitive);
                builder.addPrimitive(primitive);
            }
        }
        AddPrimitivesCommand cmd = new AddPrimitivesCommand(builder.primitiveData,
                context.getComponent(OsmLayerManager.class).getOsmDataLayer().getDataSet());
        cmd.executeCommand();
        ImportedPrimitives importedPrimitives = new ImportedPrimitives(cmd.getParticipatingPrimitives());
        context.register(ImportedPrimitives.class, importedPrimitives, true);
        removeOdsTags(importedPrimitives.get());
        buildImportedEntities(importedPrimitives);
//        updateMatching();
    }

    /**
     * Remove the ODS tags from the selected Osm primitives
     *
     * @param osmData
     */
    private static void removeOdsTags(Collection<? extends OsmPrimitive> primitives) {
        for (OsmPrimitive primitive : primitives) {
            for (String key : primitive.keySet()) {
                if (key.startsWith(ODS.KEY.BASE)) {
                    primitive.put(key, null);
                }
            }
        }
    }

    /**
     * Build entities for the newly imported primitives.
     * We could have created these entities from the OpenData entities instead. But by building them
     * from the Osm primitives, we make sure that all entities in the Osm layer are built the same way,
     * making them consistent with each other.
     *
     * @param importedPrimitives
     */
    private void buildImportedEntities(ImportedPrimitives importedPrimitives) {
        OsmEntityBuilders entityBuilders = context.getComponent(OsmEntityBuilders.class);
        for (OsmPrimitive p : importedPrimitives.get()) {
            entityBuilders.forEach(builder -> builder.buildOsmEntity(p));
        }
    }

    private class PrimitiveDataBuilder {
        List<PrimitiveData> primitiveData = new LinkedList<>();

        public PrimitiveDataBuilder() {
            // TODO Auto-generated constructor stub
        }

        public void addPrimitive(OsmPrimitive primitive) {
            primitiveData.add(primitive.save());
            if (primitive.getType() == OsmPrimitiveType.WAY) {
                for (Node node :((Way)primitive).getNodes()) {
                    addPrimitive(node);
                }
            }
            else if (primitive.getType() == OsmPrimitiveType.RELATION) {
                for (OsmPrimitive osm : ((Relation)primitive).getMemberPrimitives()) {
                    addPrimitive(osm);
                }
            }
        }
    }
    
    public static class  ImportedPrimitives extends AtomicReference<Collection<? extends OsmPrimitive>>{
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public ImportedPrimitives(
                Collection<? extends OsmPrimitive> initialValue) {
            super(initialValue);
        }
    }
}
