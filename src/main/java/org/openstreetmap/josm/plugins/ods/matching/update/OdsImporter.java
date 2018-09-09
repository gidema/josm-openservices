package org.openstreetmap.josm.plugins.ods.matching.update;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.openstreetmap.josm.command.AddPrimitivesCommand;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.PrimitiveData;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.ODS;
import org.openstreetmap.josm.plugins.ods.entities.OdEntity;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OdLayerManager;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmEntitiesBuilder;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmLayerManager;
import org.openstreetmap.josm.plugins.ods.osm.OsmNeighbourFinder;

/**
 * The importer imports objects from the OpenData layer to the Osm layer.
 *
 * TODO Use AddPrimitivesCommand
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class OdsImporter {
    private final OsmNeighbourFinder osmNeighbourFinder;
    private final OdLayerManager odLayerManager;
    private final OsmLayerManager osmLayerManager;
    private final OsmEntitiesBuilder entitiesBuilder;

    public OdsImporter(OsmNeighbourFinder osmNeighbourFinder, OdLayerManager odLayerManager,
            OsmLayerManager osmLayerManager, OsmEntitiesBuilder entitiesBuilder) {
        super();
        this.osmNeighbourFinder = osmNeighbourFinder;
        this.odLayerManager = odLayerManager;
        this.osmLayerManager = osmLayerManager;
        this.entitiesBuilder = entitiesBuilder;
    }

    public void doImport(Collection<OsmPrimitive> primitives) {
        Set<OdEntity> entitiesToImport = new HashSet<>();
        for (OsmPrimitive primitive : primitives) {
            // TODO retrieve entities from data stores
            OdEntity entity = odLayerManager.getEntity(primitive);
            if (entity != null && entity.getMatch() == null) {
                entitiesToImport.add(entity);
            }
            for (OsmPrimitive referrer : primitive.getReferrers()) {
                if (referrer.getType().equals(OsmPrimitiveType.RELATION)) {
                    OdEntity referrerEntity = odLayerManager.getEntity(referrer);
                    if (referrerEntity != null && referrerEntity.getMatch() == null) {
                        entitiesToImport.add(referrerEntity);
                    }
                }
            }
        }
        importEntities(entitiesToImport);
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
                osmLayerManager.getOsmDataLayer().getDataSet());
        cmd.executeCommand();
        Collection<? extends OsmPrimitive> importedPrimitives = cmd.getParticipatingPrimitives();
        removeOdsTags(importedPrimitives);
        buildImportedEntities(importedPrimitives);
        // Save the current edit layer
        OsmDataLayer savedEditLayer =  MainApplication.getLayerManager().getEditLayer();
        try {
            OsmDataLayer editLayer = osmLayerManager.getOsmDataLayer();
            MainApplication.getLayerManager().setActiveLayer(editLayer);
            for (OsmPrimitive osm : importedPrimitives) {
                osmNeighbourFinder.findNeighbours(osm);
            }
        }
        finally {
            MainApplication.getLayerManager().setActiveLayer(savedEditLayer);
        }
        updateMatching();
    }

    private void updateMatching() {
        //        Matcher matcher = module.getMatcherManager().getMatcher(BuildingMatcher.class);
        //        matcher.run();
        //        matcher = module.getMatcherManager().getMatcher(AddressNodeMatcher.class);
        //        matcher.run();
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
    private void buildImportedEntities(
            Collection<? extends OsmPrimitive> importedPrimitives) {
        entitiesBuilder.build(importedPrimitives);
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
}
