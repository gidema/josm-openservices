package org.openstreetmap.josm.plugins.openservices;

//import static nl.gertjanidema.josm.bag.BAGDataType.ADRES;
//import static nl.gertjanidema.josm.bag.BAGDataType.LIGPLAATS;
//import static nl.gertjanidema.josm.bag.BAGDataType.PAND;
//import static nl.gertjanidema.josm.bag.BAGDataType.STANDPLAATS;
//import static nl.gertjanidema.josm.bag.BAGDataType.WEGVAK;
import static org.openstreetmap.josm.gui.help.HelpUtil.ht;
import static org.openstreetmap.josm.tools.I18n.marktr;

import java.awt.event.KeyEvent;

import javax.swing.JMenu;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;

public class OpenServicesPlugin extends Plugin {
  private static JMenu menu;
  
  public OpenServicesPlugin(PluginInformation info) {
    super(info);
  }

  public static JMenu getMenu() {
    if (menu == null) {
      menu = Main.main.menu.addMenu(marktr("NlGeo"), KeyEvent.VK_UNDEFINED, 4, ht("/Plugin/Bag"));
    }
    return menu;
  }
}
