package org.openstreetmap.josm.plugins.ods.gui;

import static org.openstreetmap.josm.gui.help.HelpUtil.ht;
import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.Preferences;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.help.ContextSensitiveHelpAction;
import org.openstreetmap.josm.gui.util.WindowGeometry;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.tools.ImageProvider;
import org.openstreetmap.josm.tools.InputMapUtils;

/**
 * Dialog box to download a polygon area.
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public abstract class AbstractDownloadDialog extends JDialog implements PropertyChangeListener {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    protected boolean canceled;

    protected JCheckBox cbDownloadModified;
    protected OdsContext context;
    /** the download button */
    protected JButton btnDownload;

    public AbstractDownloadDialog(OdsContext context, String title) {
        super(JOptionPane.getFrameForComponent(MainApplication.getMainPanel()), title, ModalityType.DOCUMENT_MODAL);
        this.context = context;
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(buildMainPanel(), BorderLayout.CENTER);
        getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
    }

    public boolean isDownloadModifiedAreas() {
        return cbDownloadModified.isSelected();
    }

    abstract protected JPanel buildMainPanel();

    protected JPanel buildButtonPanel() {
        JPanel pnl = new JPanel();
        pnl.setLayout(new FlowLayout());

        // -- download button
        pnl.add(btnDownload = new JButton(new DownloadAction()));
        InputMapUtils.enableEnter(btnDownload);

        // -- cancel button
        JButton btnCancel;
        CancelAction actCancel = new CancelAction();
        pnl.add(btnCancel = new JButton(actCancel));
        InputMapUtils.enableEnter(btnCancel);

        // -- cancel on ESC
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0), "cancel");
        getRootPane().getActionMap().put("cancel", actCancel);

        // -- help button
        JButton btnHelp;
        pnl.add(btnHelp = new JButton(new ContextSensitiveHelpAction(ht("/Action/Download"))));
        InputMapUtils.enableEnter(btnHelp);

        return pnl;
    }

    /**
     * Remembers the current settings in the download dialog
     *
     */
    public void rememberSettings() {
        Preferences.main().putBoolean("openservices.download.modified", cbDownloadModified.isSelected());
    }

    public void restoreSettings() {
        cbDownloadModified.setSelected(Preferences.main().getBoolean(
                "openservices.download.modified", true));
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            new WindowGeometry(
                    getClass().getName() + ".geometry",
                    WindowGeometry.centerInWindow(
                            getParent(),
                            getDimension()
                            )
                    ).applySafe(this);
        } else if (isShowing()) { // Avoid IllegalComponentStateException like in #8775
            new WindowGeometry(this).remember(getClass().getName() + ".geometry");
        }
        super.setVisible(visible);
    }

    abstract Dimension getDimension();

    /**
     * Replies true if the dialog was canceled
     *
     * @return true if the dialog was canceled
     */
    public boolean isCanceled() {
        return canceled;
    }

    protected void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    class CancelAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        public CancelAction() {
            putValue(NAME, tr("Cancel"));
            putValue(SMALL_ICON, ImageProvider.get("cancel"));
            putValue(SHORT_DESCRIPTION, tr("Click to close the dialog and to abort downloading"));
        }

        public void run() {
            setCanceled(true);
            setVisible(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            run();
        }
    }

    class DownloadAction extends AbstractAction {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        public DownloadAction() {
            putValue(NAME, tr("Download"));
            putValue(SMALL_ICON, ImageProvider.get("download"));
            putValue(SHORT_DESCRIPTION, tr("Click to download the currently selected area"));
        }

        public void run() {
            setCanceled(false);
            setVisible(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            run();
        }
    }

    class WindowEventHandler extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            new CancelAction().run();
        }

        @Override
        public void windowActivated(WindowEvent e) {
            btnDownload.requestFocusInWindow();
        }
    }

    public abstract Bounds getSelectedDownloadArea();
}
