package org.openstreetmap.josm.plugins.ods;

import java.awt.event.ActionEvent;

import javax.swing.Action;

public class OdsWorkingSetAction extends OdsAction {
  
  
  public OdsWorkingSetAction(OdsWorkingSet workingSet) {
    super();
    this.setWorkingSet(workingSet);
    this.setName(workingSet.getName());
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    Action action = workingSet.getActivateAction();
    action.actionPerformed(e);
  }  
}
