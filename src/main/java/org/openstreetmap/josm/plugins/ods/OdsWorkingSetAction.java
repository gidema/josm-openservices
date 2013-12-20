package org.openstreetmap.josm.plugins.ods;

import java.awt.event.ActionEvent;

import javax.swing.Action;

public class OdsWorkingSetAction extends OdsAction {

    private static final long serialVersionUID = 1L;

    public OdsWorkingSetAction(OdsWorkingSet workingSet) {
        super();
        this.setWorkingSet(workingSet);
        this.setName(workingSet.getName());
        this.setDescription(workingSet.getDescription());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Action action = workingSet.getActivateAction();
        action.actionPerformed(e);
    }
}
