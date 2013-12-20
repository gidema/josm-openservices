package org.openstreetmap.josm.plugins.ods;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;

public abstract class OdsAction extends AbstractAction {
    private static final long serialVersionUID = 1L;

    protected OdsWorkingSet workingSet;

    public OdsAction() {
        super();
    }

    public void setWorkingSet(OdsWorkingSet workingSet) {
        this.workingSet = workingSet;
    }

    public void setName(String name) {
        this.putValue(Action.NAME, name);
        this.putValue("toolbar", name);
    }

    public void setDescription(String description) {
        this.putValue(Action.SHORT_DESCRIPTION, description);
    }

    public void setIcon(ImageIcon icon) {
        this.putValue(Action.SMALL_ICON, icon);
    }
}
