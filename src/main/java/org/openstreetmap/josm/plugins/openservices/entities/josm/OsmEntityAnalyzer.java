package org.openstreetmap.josm.plugins.openservices.entities.josm;

import org.openstreetmap.josm.plugins.openservices.JosmDataLayer;
import org.openstreetmap.josm.plugins.openservices.entities.EntitySet;

public class OsmEntityAnalyzer {
    private JosmDataLayer dataLayer;
    
    public OsmEntityAnalyzer() {
        // TODO Auto-generated constructor stub
    }

    public void setDataLayer(JosmDataLayer dataLayer) {
        this.dataLayer = dataLayer;
    }
    
    public void analyze() {
        EntitySet entitySet = dataLayer.getEntitySet();
    }
}
