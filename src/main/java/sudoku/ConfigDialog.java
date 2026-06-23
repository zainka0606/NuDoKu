package sudoku;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.text.MessageFormat;
import java.util.ResourceBundle;

public class ConfigDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private ConfigSolverPanel myConfigSolverPanel;
    private ConfigGeneralPanel myGeneralPanel;
    private ConfigLevelFontPanel myLevelFontPanel;
    private ConfigStepPanel myConfigStepPanel;
    private ConfigColorPanel myConfigColorPanel;
    private ConfigFindAllStepsPanel myConfigFindAllStepsPanel;
    private ConfigProgressPanel myConfigProgressPanel;
    private ConfigTrainigPanel myConfigTrainingPanel;
    private ConfigGeneratorPanel myConfigGeneratorPanel;
    private ConfigColorkuPanel myConfigColorkuPanel;
    private JButton cancelButton;
    private JPanel colorKuPanel;
    private JPanel colorPanel;
    private JPanel findAllStepsPanel;
    private JPanel generalPanel;
    private JPanel generatorPanel;
    private JPanel heuristicsPanel;
    private JPanel levelFontPanel;
    private JButton okButton;
    private JPanel solverPanel;
    private JPanel stepConfigPanel;
    private JTabbedPane tabbedPane;
    private JPanel trainingPanel;

    public ConfigDialog(Frame parent, boolean modal, int tabIndex) {
        super(parent, modal);
        this.initComponents();
        this.getRootPane().setDefaultButton(this.okButton);
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(27, 0, false);
        Action escapeAction = new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                ConfigDialog.this.setVisible(false);
            }
        };
        this.getRootPane().getInputMap(2).put(escapeKeyStroke, "ESCAPE");
        this.getRootPane().getActionMap().put("ESCAPE", escapeAction);
        this.myConfigSolverPanel = new ConfigSolverPanel();
        this.solverPanel.add(this.myConfigSolverPanel, "Center");
        this.myGeneralPanel = new ConfigGeneralPanel(parent);
        this.generalPanel.add(this.myGeneralPanel, "Center");
        this.myLevelFontPanel = new ConfigLevelFontPanel(parent);
        this.levelFontPanel.add(this.myLevelFontPanel, "Center");
        this.myConfigStepPanel = new ConfigStepPanel();
        this.stepConfigPanel.add(this.myConfigStepPanel, "Center");
        this.myConfigColorPanel = new ConfigColorPanel();
        this.colorPanel.add(this.myConfigColorPanel, "Center");
        this.myConfigFindAllStepsPanel = new ConfigFindAllStepsPanel();
        this.findAllStepsPanel.add(this.myConfigFindAllStepsPanel, "Center");
        this.myConfigProgressPanel = new ConfigProgressPanel();
        this.heuristicsPanel.add(this.myConfigProgressPanel, "Center");
        this.myConfigTrainingPanel = new ConfigTrainigPanel();
        this.trainingPanel.add(this.myConfigTrainingPanel, "Center");
        this.myConfigGeneratorPanel = new ConfigGeneratorPanel();
        this.generatorPanel.add(this.myConfigGeneratorPanel, "Center");
        this.tabbedPane.remove(8);
        this.myConfigColorkuPanel = new ConfigColorkuPanel(parent);
        this.colorKuPanel.add(this.myConfigColorkuPanel, "Center");
        if (tabIndex != -1) {
            this.tabbedPane.setSelectedIndex(tabIndex);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
        }

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ConfigDialog(new JFrame(), true, -1).setVisible(true);
            }
        });
    }

    private void initComponents() {
        this.tabbedPane = new JTabbedPane();
        this.generalPanel = new JPanel();
        this.levelFontPanel = new JPanel();
        this.solverPanel = new JPanel();
        this.findAllStepsPanel = new JPanel();
        this.heuristicsPanel = new JPanel();
        this.stepConfigPanel = new JPanel();
        this.trainingPanel = new JPanel();
        this.colorPanel = new JPanel();
        this.generatorPanel = new JPanel();
        this.colorKuPanel = new JPanel();
        this.okButton = new JButton();
        this.cancelButton = new JButton();
        this.setDefaultCloseOperation(2);
        ResourceBundle bundle = ResourceBundle.getBundle("intl/ConfigDialog");
        this.setTitle(bundle.getString("ConfigDialog.title"));
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent evt) {
                ConfigDialog.this.formWindowOpened(evt);
            }
        });
        this.generalPanel.setLayout(new BorderLayout());
        this.tabbedPane.addTab(bundle.getString("ConfigDialog.generalPanel.TabConstraints.tabTitle"), this.generalPanel);
        this.levelFontPanel.setLayout(new BorderLayout());
        this.tabbedPane.addTab(bundle.getString("ConfigDialog.levelFontPanel.TabConstraints.tabTitle"), this.levelFontPanel);
        this.solverPanel.setLayout(new BorderLayout());
        this.tabbedPane.addTab(bundle.getString("ConfigDialog.solverPanel.TabConstraints.tabTitle"), this.solverPanel);
        this.findAllStepsPanel.setLayout(new BorderLayout());
        this.tabbedPane.addTab(bundle.getString("ConfigDialog.findAllStepsPanel.TabConstraints.tabTitle"), this.findAllStepsPanel);
        this.heuristicsPanel.setLayout(new BorderLayout());
        this.tabbedPane.addTab(bundle.getString("ConfigDialog.heuristicsPanel.TabConstraints.tabTitle"), this.heuristicsPanel);
        this.stepConfigPanel.setLayout(new BorderLayout());
        this.tabbedPane.addTab(bundle.getString("ConfigDialog.stepConfigPanel.TabConstraints.tabTitle"), this.stepConfigPanel);
        this.trainingPanel.setLayout(new BorderLayout());
        this.tabbedPane.addTab(bundle.getString("ConfigDialog.trainingPanel.TabConstraints.tabTitle"), this.trainingPanel);
        this.colorPanel.setLayout(new BorderLayout());
        this.tabbedPane.addTab(bundle.getString("ConfigDialog.colorPanel.TabConstraints.tabTitle"), this.colorPanel);
        this.generatorPanel.setLayout(new BorderLayout());
        this.tabbedPane.addTab(bundle.getString("ConfigDialog.generatorPanel.TabConstraints.tabTitle"), this.generatorPanel);
        this.colorKuPanel.setLayout(new BorderLayout());
        this.tabbedPane.addTab(bundle.getString("ConfigDialog.colorKuPanel.TabConstraints.tabTitle"), this.colorKuPanel);
        this.okButton.setMnemonic(ResourceBundle.getBundle("intl/ConfigDialog").getString("ConfigDialog.okButton.mnemonic").charAt(0));
        this.okButton.setText(bundle.getString("ConfigDialog.okButton.text"));
        this.okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigDialog.this.okButtonActionPerformed(evt);
            }
        });
        this.cancelButton.setMnemonic(ResourceBundle.getBundle("intl/ConfigDialog").getString("ConfigDialog.cancelButton.mnemonic").charAt(0));
        this.cancelButton.setText(bundle.getString("ConfigDialog.cancelButton.text"));
        this.cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigDialog.this.cancelButtonActionPerformed(evt);
            }
        });
        GroupLayout layout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                layout.createParallelGroup(Alignment.LEADING)
                                                        .addGroup(
                                                                Alignment.TRAILING,
                                                                layout.createSequentialGroup()
                                                                        .addComponent(this.okButton)
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addComponent(this.cancelButton)
                                                        )
                                                        .addComponent(this.tabbedPane, -1, 546, 32767)
                                        )
                                        .addContainerGap()
                        )
        );
        layout.linkSize(0, this.cancelButton, this.okButton);
        layout.setVerticalGroup(
                layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                Alignment.TRAILING,
                                layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(this.tabbedPane, -1, 478, 32767)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(this.okButton).addComponent(this.cancelButton))
                                        .addContainerGap()
                        )
        );
        this.pack();
    }

    private void okButtonActionPerformed(ActionEvent evt) {
        this.myConfigSolverPanel.okPressed();
        this.myGeneralPanel.okPressed();
        this.myLevelFontPanel.okPressed();
        this.myConfigStepPanel.okPressed();
        this.myConfigColorPanel.okPressed();
        this.myConfigFindAllStepsPanel.okPressed();
        this.myConfigProgressPanel.okPressed();
        this.myConfigTrainingPanel.okPressed();
        this.myConfigGeneratorPanel.okPressed();
        this.myConfigColorkuPanel.okPressed();

        try {
            Options.getInstance().writeOptions();
        } catch (FileNotFoundException ex) {
            MessageFormat formatter = new MessageFormat(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.invalid_filename"));
            String msg = formatter.format(new Object[]{ex.getLocalizedMessage()});
            JOptionPane.showMessageDialog(this, msg, ResourceBundle.getBundle("intl/ConfigDialog").getString("ConfigDialog.error"), 0);
        }

        this.setVisible(false);
    }

    private void cancelButtonActionPerformed(ActionEvent evt) {
        this.setVisible(false);
    }

    private void formWindowOpened(WindowEvent evt) {
        int oldHeight = this.getHeight();
        int newHeight = oldHeight;
        int oldWidth = this.getWidth();
        int newWidth = oldWidth;
        int diff = this.cancelButton.getY() + this.cancelButton.getHeight() - (this.getHeight() - this.getInsets().top - this.getInsets().bottom - 5);
        if (diff > 0) {
            newHeight += diff;
        }

        diff = this.tabbedPane.getX() + this.tabbedPane.getWidth() - (this.getWidth() - this.getInsets().right - this.getInsets().left - 5);
        if (diff > 0) {
            newWidth += diff;
        }

        if (newHeight != oldHeight || newWidth != oldWidth) {
            this.setSize(newWidth, newHeight);
        }
    }
}
