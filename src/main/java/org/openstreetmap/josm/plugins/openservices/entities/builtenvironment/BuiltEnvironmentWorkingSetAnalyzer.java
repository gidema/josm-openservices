package org.openstreetmap.josm.plugins.openservices.entities.builtenvironment;

import org.openstreetmap.josm.plugins.openservices.JosmDataLayer;
import org.openstreetmap.josm.plugins.openservices.OdsWorkingSet;
import org.openstreetmap.josm.plugins.openservices.entities.EntitySet;

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
