package org.openstreetmap.josm.plugins.ods;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.upload.UploadHook;
import org.openstreetmap.josm.command.ChangePropertyCommand;
import org.openstreetmap.josm.command.SequenceCommand;
import org.openstreetmap.josm.data.APIDataSet;
import org.openstreetmap.josm.data.osm.OsmPrimitive;

/**
 * Removes all ODS tags from all modified objects before upload
 */
class DiscardOdsTagsHook implements UploadHook {

    @Override
    public boolean checkUpload(APIDataSet apiDataSet) {
        List<OsmPrimitive> objectsToUpload = apiDataSet.getPrimitives();

        Map<String, String> map = new HashMap<>();
        for (String key : ODS.KEYS) {
            map.put(key, null);
        }

        SequenceCommand removeKeys = new SequenceCommand(tr("Removed ODS tags"),
                new ChangePropertyCommand(objectsToUpload, map));
        Main.main.undoRedo.add(removeKeys);
        return true;
    }
}