package org.openstreetmap.josm.plugins.ods.gui;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.tools.GBC;

/**
 * This Dialog box is used to ask the user what to do after removing one of
 * the layers that are managed by an ODS module.
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class OdsLayerDeletedDialog extends JDialog {
    private OdsModule module;
    private JButton buttonReset;
    private JButton buttonDisable;
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public OdsLayerDeletedDialog(OdsModule module) {
        super(JOptionPane.getFrameForComponent(MainApplication.getMainPanel()), tr("ODS layer removed."),
            ModalityType.DOCUMENT_MODAL);
        this.module = module;
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(buildMainPanel(), BorderLayout.CENTER);

    }

    protected JPanel buildMainPanel() {
        String moduleName = module.getName();
        JLabel lbl = new JLabel(tr("You removed one of the layers that belong to the {0} module." +
            " For the stability of the {0} module, you have to reset, or to disable" +
            " the module.", moduleName));
        buttonReset = new JButton(tr("Reset {0}", moduleName));
        buttonReset.setToolTipText(tr("<html>Reset the {0} module.</html>", moduleName));
        buttonDisable = new JButton(tr("Disable {0}", moduleName));
        buttonDisable.setToolTipText(tr("<html>Disable the {0} module.</html>", moduleName));

        JPanel pnl = new JPanel();
        pnl.setLayout(new GridBagLayout());
        pnl.add(lbl, GBC.eol().anchor(GridBagConstraints.NORTH).insets(5, 5, 5, 5));
        pnl.add(buttonReset,
                GBC.std().anchor(GridBagConstraints.SOUTHWEST).insets(5, 5, 5, 5));
        pnl.add(buttonDisable,
                GBC.eol().anchor(GridBagConstraints.SOUTHWEST).insets(5, 5, 5, 5));
        pnl.revalidate();
        pnl.repaint();
        return pnl;
    }

    protected static Dimension getDimension() {
        return new Dimension(300, 200);
    }

}
