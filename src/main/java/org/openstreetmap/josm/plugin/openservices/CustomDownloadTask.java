// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.plugin.openservices;

import org.openstreetmap.josm.actions.downloadtasks.AbstractDownloadTask;

public abstract class CustomDownloadTask extends AbstractDownloadTask {
    private String layerName;

    public void setLayerName(String layerName) {
        this.layerName = layerName;
    }

    /**
     * @return the layerName
     */
    public String getLayerName() {
        return layerName;
    }

    public abstract CustomDataLayer createTargetLayer(String name);

}
