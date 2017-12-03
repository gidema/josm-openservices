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
import org.openstreetmap.josm.plugins.ods.LayerManager;
import org.openstreetmap.josm.plugins.ods.Matcher;
import org.openstreetmap.josm.plugins.ods.ODS;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.actual.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmEntitiesBuilder;
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
    private final OdsModule module;
    // TODO Make the importfilter(s) configurable
    private final ImportFilter importFilter = new DefaultImportFilter();
    // TODO Move buildingAligner out of this class in favour of a
    // Observer pattern
    
    public OdsImporter(OdsModule module) {
        super();
        this.module = module;
    }

    public void doImport(Collection<OsmPrimitive> primitives) {
        LayerManager layerManager = module.getOpenDataLayerManager();
        Set<Entity> entitiesToImport = new HashSet<>();
        for (OsmPrimitive primitive : primitives) {
            Entity entity = layerManager.getEntity(primitive);
            if (entity != null && entity.getMatch() == null 
                  && importFilter.test(entity)) {
                entitiesToImport.add(entity);
            }
            for (OsmPrimitive referrer : primitive.getReferrers()) {
                if (referrer.getType().equals(OsmPrimitiveType.RELATION)) {
                    Entity referrerEntity = layerManager.getEntity(referrer);
                    if (referrerEntity != null && referrerEntity.getMatch() == null 
                          && importFilter.test(referrerEntity)) {
                        entitiesToImport.add(referrerEntity);
                    }
                }
            }
        }
        importEntities(entitiesToImport);
    }
    
    private void importEntities(Set<Entity> entitiesToImport) {
        Set<OsmPrimitive> primitivesToImport = new HashSet<>();
        PrimitiveDataBuilder builder = new PrimitiveDataBuilder();
        for (Entity entity : entitiesToImport) {
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
            module.getOsmLayerManager().getOsmDataLayer().data);
        cmd.executeCommand();
        Collection<? extends OsmPrimitive> importedPrimitives = cmd.getParticipatingPrimitives();
        removeOdsTags(importedPrimitives);
        buildImportedEntities(importedPrimitives);
        // Save the current edit layer
        OsmDataLayer savedEditLayer =  MainApplication.getLayerManager().getEditLayer();
        try {
            OsmDataLayer editLayer = module.getOsmLayerManager().getOsmDataLayer();
            MainApplication.getLayerManager().setActiveLayer(editLayer);
            OsmNeighbourFinder neighbourFinder = new OsmNeighbourFinder(module);
            for (OsmPrimitive osm : importedPrimitives) {
                neighbourFinder.findNeighbours(osm);
            }
        }
        finally {
            MainApplication.getLayerManager().setActiveLayer(savedEditLayer);
        }
        updateMatching();
    }
    
    private void updateMatching() {
        Matcher<?> matcher = module.getMatcherManager().getMatcher(Building.class);
        matcher.run();
        matcher = module.getMatcherManager().getMatcher(AddressNode.class);
        matcher.run();
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
        OsmEntitiesBuilder entitiesBuilder = module.getOsmLayerManager().getEntitiesBuilder();
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
