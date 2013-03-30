package org.openstreetmap.josm.plugins.openservices;

import java.awt.event.ActionEvent;

public class OdsWorkingSetAction extends OdsAction {
  
  
  public OdsWorkingSetAction(OdsWorkingSet workingSet) {
    super();
    this.setWorkingSet(workingSet);
    this.setName(workingSet.getName());
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    OdsDownloadAction downloadAction = workingSet.getDownloadAction();
    downloadAction.actionPerformed(e);
  }  
}
