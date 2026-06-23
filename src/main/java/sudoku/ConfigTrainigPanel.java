package sudoku;

import generator.BackgroundGeneratorThread;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.ResourceBundle;

public class ConfigTrainigPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private StepConfig[] steps;
    private DefaultListModel model;
    private boolean listView = true;
    private JLabel chosenLabel;
    private JTextArea chosenTextArea;
    private JPanel jPanel1;
    private JPanel jPanel4;
    private JScrollPane jScrollPane1;
    private JToolBar jToolBar1;
    private JToggleButton listButton;
    private JButton resetButton;
    private JList stepList;
    private JScrollPane stepScrollPane;
    private JTree stepTree;
    private JToggleButton treeButton;
    private JLabel warningLabel;

    public ConfigTrainigPanel() {
        this.initComponents();
        this.stepList.setSelectionMode(0);
        this.stepList.setCellRenderer(new ConfigTrainigPanel.CheckBoxRenderer());
        this.model = new DefaultListModel();
        this.stepList.setModel(this.model);
        this.stepTree.setCellRenderer(new CheckRenderer());
        this.stepTree.getSelectionModel().setSelectionMode(1);
        this.stepTree.putClientProperty("JTree.lineStyle", "Angled");
        this.initAll(false);
        this.checkButtons(false);
    }

    private void initComponents() {
        this.stepTree = new JTree();
        this.jPanel1 = new JPanel();
        this.resetButton = new JButton();
        this.chosenLabel = new JLabel();
        this.jScrollPane1 = new JScrollPane();
        this.chosenTextArea = new JTextArea();
        this.warningLabel = new JLabel();
        this.jPanel4 = new JPanel();
        this.stepScrollPane = new JScrollPane();
        this.stepList = new JList();
        this.jToolBar1 = new JToolBar();
        this.listButton = new JToggleButton();
        this.treeButton = new JToggleButton();
        this.stepTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                ConfigTrainigPanel.this.stepTreeMousePressed(evt);
            }
        });
        this.resetButton.setMnemonic(ResourceBundle.getBundle("intl/ConfigTrainingPanel").getString("ConfigTrainingPanel.resetButton.mnemonic").charAt(0));
        ResourceBundle bundle = ResourceBundle.getBundle("intl/ConfigTrainingPanel");
        this.resetButton.setText(bundle.getString("ConfigTrainingPanel.resetButton.text"));
        this.resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigTrainigPanel.this.resetButtonActionPerformed(evt);
            }
        });
        this.chosenLabel.setText(bundle.getString("ConfigTrainigPanel.chosenLabel.text"));
        this.chosenTextArea.setColumns(20);
        this.chosenTextArea.setEditable(false);
        this.chosenTextArea.setFont(new Font("SansSerif", 0, 12));
        this.chosenTextArea.setLineWrap(true);
        this.chosenTextArea.setRows(5);
        this.chosenTextArea.setWrapStyleWord(true);
        this.jScrollPane1.setViewportView(this.chosenTextArea);
        this.warningLabel.setText(bundle.getString("ConfigTrainigPanel.warningLabel.text"));
        this.warningLabel.setVerticalAlignment(1);
        GroupLayout jPanel1Layout = new GroupLayout(this.jPanel1);
        this.jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                Alignment.TRAILING,
                                jPanel1Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                jPanel1Layout.createParallelGroup(Alignment.TRAILING)
                                                        .addComponent(this.warningLabel, Alignment.LEADING, -1, 214, 32767)
                                                        .addComponent(this.resetButton)
                                                        .addComponent(this.chosenLabel, Alignment.LEADING)
                                                        .addComponent(this.jScrollPane1, -1, 214, 32767)
                                        )
                                        .addContainerGap()
                        )
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                jPanel1Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(this.chosenLabel)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.jScrollPane1, -2, 215, -2)
                                        .addPreferredGap(ComponentPlacement.UNRELATED)
                                        .addComponent(this.warningLabel, -1, 146, 32767)
                                        .addPreferredGap(ComponentPlacement.UNRELATED)
                                        .addComponent(this.resetButton)
                        )
        );
        this.jPanel4.setLayout(new BorderLayout());
        this.stepList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                ConfigTrainigPanel.this.stepListMouseClicked(evt);
            }
        });
        this.stepScrollPane.setViewportView(this.stepList);
        this.jPanel4.add(this.stepScrollPane, "Center");
        this.listButton.setIcon(new ImageIcon(this.getClass().getResource("/img/listview16b.png")));
        this.listButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigTrainigPanel.this.listButtonActionPerformed(evt);
            }
        });
        this.jToolBar1.add(this.listButton);
        this.treeButton.setIcon(new ImageIcon(this.getClass().getResource("/img/treeview16b.png")));
        this.treeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigTrainigPanel.this.treeButtonActionPerformed(evt);
            }
        });
        this.jToolBar1.add(this.treeButton);
        this.jPanel4.add(this.jToolBar1, "North");
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                Alignment.TRAILING,
                                layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(this.jPanel4, -1, 227, 32767)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.jPanel1, -2, -1, -2)
                        )
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                Alignment.TRAILING,
                                layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                layout.createParallelGroup(Alignment.TRAILING)
                                                        .addComponent(this.jPanel4, Alignment.LEADING, -1, 437, 32767)
                                                        .addComponent(this.jPanel1, Alignment.LEADING, -1, -1, 32767)
                                        )
                                        .addContainerGap()
                        )
        );
    }

    private void stepTreeMousePressed(MouseEvent evt) {
        TreePath path = this.stepTree.getPathForLocation(evt.getX(), evt.getY());
        if (path != null) {
            CheckNode act = (CheckNode) path.getLastPathComponent();
            if (act != null) {
                act.toggleSelectionState();
                this.chosenTextArea.setText(Options.getInstance().getTrainingStepsString(this.steps, false));
                this.stepTree.repaint();
            }
        }
    }

    private void treeButtonActionPerformed(ActionEvent evt) {
        this.checkButtons(false);
    }

    private void listButtonActionPerformed(ActionEvent evt) {
        this.checkButtons(true);
    }

    private void resetButtonActionPerformed(ActionEvent evt) {
        this.initAll(true);
    }

    private void stepListMouseClicked(MouseEvent evt) {
        int index = this.stepList.locationToIndex(evt.getPoint());
        if (index == this.stepList.getSelectedIndex()) {
            StepConfig conf = (StepConfig) this.stepList.getSelectedValue();
            conf.setEnabledTraining(!conf.isEnabledTraining());
            this.chosenTextArea.setText(Options.getInstance().getTrainingStepsString(this.steps, false));
            this.stepList.repaint();
        }
    }

    public void okPressed() {
        boolean somethingChanged = false;
        StepConfig[] orgSteps0 = Options.getInstance().solverSteps;

        for (int i = 0; i < this.steps.length; i++) {
            for (int j = 0; j < orgSteps0.length; j++) {
                if (this.steps[i].getType() == orgSteps0[j].getType() && orgSteps0[j].isEnabled()) {
                    if (orgSteps0[j].isEnabledTraining() != this.steps[i].isEnabledTraining()) {
                        somethingChanged = true;
                    }

                    orgSteps0[j].setEnabledTraining(this.steps[i].isEnabledTraining());
                    break;
                }
            }
        }

        StepConfig[] orgSteps1 = Options.getInstance().getOrgSolverSteps();

        for (int i = 0; i < this.steps.length; i++) {
            for (int j = 0; j < orgSteps1.length; j++) {
                if (this.steps[i].getType() == orgSteps1[j].getType() && orgSteps1[j].isEnabled()) {
                    orgSteps1[j].setEnabledTraining(this.steps[i].isEnabledTraining());
                    break;
                }
            }
        }

        if (somethingChanged) {
            BackgroundGeneratorThread.getInstance().resetTrainingPractising();
        }
    }

    private void initAll(boolean setDefault) {
        if (setDefault) {
            this.steps = Options.getInstance().copyStepConfigs(Options.getInstance().solverSteps, true, false, false, false);
            StepConfig[] orgSteps = Options.DEFAULT_SOLVER_STEPS;

            for (int i = 0; i < this.steps.length; i++) {
                for (int j = 0; j < orgSteps.length; j++) {
                    if (this.steps[i].getType() == orgSteps[j].getType()) {
                        this.steps[i].setEnabledTraining(orgSteps[j].isEnabledTraining());
                        break;
                    }
                }
            }
        } else {
            this.steps = Options.getInstance().copyStepConfigs(Options.getInstance().solverSteps, true, false, false, false);
        }

        this.model.removeAllElements();

        for (int i = 0; i < this.steps.length; i++) {
            if (this.steps[i].isEnabled()) {
                this.model.addElement(this.steps[i]);
            }
        }

        this.stepList.setSelectedIndex(-1);
        this.stepList.ensureIndexIsVisible(0);
        this.stepList.repaint();
        this.buildTree();
        this.chosenTextArea.setText(Options.getInstance().getTrainingStepsString(this.steps, false));
    }

    public void buildTree() {
        CheckNode root = new CheckNode();

        for (int i = 0; i < this.steps.length; i++) {
            if (this.steps[i].isEnabled()) {
                Enumeration<?> en = root.children();

                CheckNode act;
                for (act = null; en.hasMoreElements(); act = null) {
                    act = (CheckNode) en.nextElement();
                    if (act.getCategory() == this.steps[i].getCategory()) {
                        break;
                    }
                }

                if (act == null) {
                    act = new CheckNode(
                            this.steps[i].getCategoryName(), true, this.steps[i].isEnabledTraining() ? 2 : 0, null, false, false, true, this.steps[i].getCategory()
                    );
                    root.add(act);
                }

                act.add(
                        new CheckNode(this.steps[i].getType().getStepName(), false, this.steps[i].isEnabledTraining() ? 2 : 0, this.steps[i], false, false, true, null)
                );
                if (act.getSelectionState() == 2 && !this.steps[i].isEnabledTraining()) {
                    act.setSelectionState(1);
                }

                if (act.getSelectionState() == 0 && this.steps[i].isEnabledTraining()) {
                    act.setSelectionState(1);
                }
            }
        }

        DefaultTreeModel tmpModel = new DefaultTreeModel(root);
        this.stepTree.setModel(tmpModel);
        this.stepTree.setShowsRootHandles(true);
        this.stepTree.setRootVisible(false);
        this.stepTree.setRowHeight(-1);
    }

    private void checkButtons(boolean setList) {
        boolean changeView = false;
        if (this.listView != setList) {
            changeView = true;
        }

        this.listView = setList;
        if (this.listView) {
            this.listButton.setSelected(true);
            this.treeButton.setSelected(false);
            if (changeView) {
                this.stepScrollPane.setViewportView(this.stepList);
            }

            this.stepList.requestFocusInWindow();
        } else {
            this.listButton.setSelected(false);
            this.treeButton.setSelected(true);
            if (changeView) {
                this.buildTree();
                this.stepScrollPane.setViewportView(this.stepTree);
            }

            this.stepTree.requestFocusInWindow();
        }
    }

    class CheckBoxRenderer extends JCheckBox implements ListCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getListCellRendererComponent(JList listBox, Object obj, int index, boolean isSelected, boolean hasFocus) {
            if (isSelected) {
                Color bg = UIManager.getColor("List.selectionBackground");
                if (bg == null) {
                    bg = UIManager.getColor("List[Selected].textBackground");
                }

                Color fg = UIManager.getColor("List.selectionForeground");
                if (fg == null) {
                    fg = UIManager.getColor("List[Selected].textForeground");
                }

                this.setBackground(bg);
                this.setForeground(fg);
                this.setOpaque(true);
            } else {
                this.setBackground(UIManager.getColor("List.background"));
                this.setForeground(UIManager.getColor("List.foreground"));
                this.setOpaque(false);
            }

            this.setText(((StepConfig) obj).toString());
            this.setSelected(((StepConfig) obj).isEnabledTraining());
            return this;
        }
    }
}
