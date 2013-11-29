package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;

import org.openstreetmap.josm.plugins.ods.JosmDataLayer;
import org.openstreetmap.josm.plugins.ods.OdsWorkingSet;
import org.openstreetmap.josm.plugins.ods.entities.EntitySet;

public class BuiltEnvironmentWorkingSetAnalyzer {
    private OdsWorkingSet workingSet;
    
    public BuiltEnvironmentWorkingSetAnalyzer() {
        // TODO Auto-generated constructor stub
    }

    public void setWorkingSetLayer(OdsWorkingSet workingSet) {
        this.workingSet = workingSet;
    }
    
    public void analyze() {
        EntitySet osmEntities = workingSet.getOdsOsmDataLayer().getEntitySet();
        EntitySet importedEntities = workingSet.getImportDataLayer().getEntitySet();
        
    }
}
