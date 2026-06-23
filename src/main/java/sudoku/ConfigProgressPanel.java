package sudoku;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

public class ConfigProgressPanel extends JPanel implements ListDragAndDropChange {
    private static final ConfigProgressPanel.SSTSConfig[] SSTS_CONFIG = new ConfigProgressPanel.SSTSConfig[]{
            new ConfigProgressPanel.SSTSConfig(SolutionType.FULL_HOUSE, 100),
            new ConfigProgressPanel.SSTSConfig(SolutionType.NAKED_SINGLE, 200),
            new ConfigProgressPanel.SSTSConfig(SolutionType.HIDDEN_SINGLE, 300),
            new ConfigProgressPanel.SSTSConfig(SolutionType.LOCKED_PAIR, 1000),
            new ConfigProgressPanel.SSTSConfig(SolutionType.NAKED_PAIR, 1100),
            new ConfigProgressPanel.SSTSConfig(SolutionType.LOCKED_CANDIDATES, 1200),
            new ConfigProgressPanel.SSTSConfig(SolutionType.LOCKED_TRIPLE, 1300),
            new ConfigProgressPanel.SSTSConfig(SolutionType.NAKED_TRIPLE, 1400),
            new ConfigProgressPanel.SSTSConfig(SolutionType.NAKED_QUADRUPLE, 1500),
            new ConfigProgressPanel.SSTSConfig(SolutionType.HIDDEN_PAIR, 1600),
            new ConfigProgressPanel.SSTSConfig(SolutionType.X_WING, 2000),
            new ConfigProgressPanel.SSTSConfig(SolutionType.SWORDFISH, 2100),
            new ConfigProgressPanel.SSTSConfig(SolutionType.SIMPLE_COLORS, 2200),
            new ConfigProgressPanel.SSTSConfig(SolutionType.MULTI_COLORS, 2300),
            new ConfigProgressPanel.SSTSConfig(SolutionType.HIDDEN_TRIPLE, 2400),
            new ConfigProgressPanel.SSTSConfig(SolutionType.XY_WING, 2500),
            new ConfigProgressPanel.SSTSConfig(SolutionType.HIDDEN_QUADRUPLE, 2600)
    };
    private static final long serialVersionUID = 1L;
    private StepConfig[] steps;
    private DefaultListModel model;
    private int dropIndex = -1;
    private StepConfig dropObject;
    private Color dndColor;
    private Stroke dndStroke;
    private List<StepConfig> invalidTypes = new ArrayList<>();
    private boolean listView = false;
    private JButton downButton;
    private JPanel jPanel1;
    private JPanel jPanel4;
    private JToolBar jToolBar1;
    private JToggleButton listButton;
    private JButton mediumButton;
    private JButton mediumPlusHardButton;
    private JButton resetButton;
    private JButton sstsButton;
    private JList stepList;
    private JScrollPane stepScrollPane;
    private JTree stepTree;
    private JToggleButton treeButton;
    private JButton upButton;

    public ConfigProgressPanel() {
        this.initComponents();
        Color tmpColor = UIManager.getColor("List.foreground");
        this.dndColor = new Color(tmpColor.getRed(), tmpColor.getGreen(), tmpColor.getBlue(), 100);
        this.dndStroke = new BasicStroke(2.0F, 1, 1);
        this.stepList.setSelectionMode(0);
        this.stepList.setCellRenderer(new ConfigProgressPanel.CheckBoxRenderer());
        this.model = new DefaultListModel();
        this.stepList.setModel(this.model);
        new ListDragAndDrop(this.stepList, this, this);
        this.stepTree.setCellRenderer(new CheckRenderer());
        this.stepTree.getSelectionModel().setSelectionMode(1);
        this.stepTree.putClientProperty("JTree.lineStyle", "Angled");
        this.initAll(false);
        this.checkButtons(true);
    }

    private void initComponents() {
        this.stepTree = new JTree();
        this.jPanel1 = new JPanel();
        this.upButton = new JButton();
        this.downButton = new JButton();
        this.resetButton = new JButton();
        this.sstsButton = new JButton();
        this.mediumButton = new JButton();
        this.mediumPlusHardButton = new JButton();
        this.jPanel4 = new JPanel();
        this.stepScrollPane = new JScrollPane();
        this.stepList = new JList();
        this.jToolBar1 = new JToolBar();
        this.listButton = new JToggleButton();
        this.treeButton = new JToggleButton();
        this.stepTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                ConfigProgressPanel.this.stepTreeMousePressed(evt);
            }
        });
        this.upButton.setMnemonic(ResourceBundle.getBundle("intl/ConfigProgressPanel").getString("ConfigProgressPanel.upButton.mnemonic").charAt(0));
        ResourceBundle bundle = ResourceBundle.getBundle("intl/ConfigProgressPanel");
        this.upButton.setText(bundle.getString("ConfigProgressPanel.upButton.text"));
        this.upButton.setEnabled(false);
        this.upButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigProgressPanel.this.upButtonActionPerformed(evt);
            }
        });
        this.downButton.setMnemonic(ResourceBundle.getBundle("intl/ConfigProgressPanel").getString("ConfigProgressPanel.downButton.mnemonic").charAt(0));
        this.downButton.setText(bundle.getString("ConfigProgressPanel.downButton.text"));
        this.downButton.setEnabled(false);
        this.downButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigProgressPanel.this.downButtonActionPerformed(evt);
            }
        });
        this.resetButton.setMnemonic(ResourceBundle.getBundle("intl/ConfigProgressPanel").getString("ConfigProgressPanel.resetButton.mnemonic").charAt(0));
        this.resetButton.setText(bundle.getString("ConfigProgressPanel.resetButton.text"));
        this.resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigProgressPanel.this.resetButtonActionPerformed(evt);
            }
        });
        this.sstsButton.setMnemonic(ResourceBundle.getBundle("intl/ConfigProgressPanel").getString("ConfigProgressPanel.sstsButton.mnemonic").charAt(0));
        this.sstsButton.setText(bundle.getString("ConfigProgressPanel.sstsButton.text"));
        this.sstsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigProgressPanel.this.sstsButtonActionPerformed(evt);
            }
        });
        this.mediumButton.setMnemonic(ResourceBundle.getBundle("intl/ConfigProgressPanel").getString("ConfigProgressPanel.mediumButton.mnemonic").charAt(0));
        this.mediumButton.setText(bundle.getString("ConfigProgressPanel.mediumButton.text"));
        this.mediumButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigProgressPanel.this.mediumButtonActionPerformed(evt);
            }
        });
        this.mediumPlusHardButton
                .setMnemonic(ResourceBundle.getBundle("intl/ConfigProgressPanel").getString("ConfigProgressPanel.mediumPlusHardButton.mnemonic").charAt(0));
        this.mediumPlusHardButton.setText(bundle.getString("ConfigProgressPanel.mediumPlusHardButton.text"));
        this.mediumPlusHardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigProgressPanel.this.mediumPlusHardButtonActionPerformed(evt);
            }
        });
        GroupLayout jPanel1Layout = new GroupLayout(this.jPanel1);
        this.jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                jPanel1Layout.createSequentialGroup()
                                        .addGroup(
                                                jPanel1Layout.createParallelGroup(Alignment.LEADING)
                                                        .addGroup(
                                                                jPanel1Layout.createSequentialGroup()
                                                                        .addComponent(this.downButton)
                                                                        .addPreferredGap(ComponentPlacement.RELATED, 54, 32767)
                                                                        .addComponent(this.resetButton)
                                                        )
                                                        .addGroup(
                                                                jPanel1Layout.createSequentialGroup()
                                                                        .addComponent(this.upButton)
                                                                        .addPreferredGap(ComponentPlacement.RELATED, 54, 32767)
                                                                        .addComponent(this.sstsButton)
                                                        )
                                                        .addGroup(Alignment.TRAILING, jPanel1Layout.createSequentialGroup().addContainerGap(113, 32767).addComponent(this.mediumButton))
                                                        .addGroup(Alignment.TRAILING, jPanel1Layout.createSequentialGroup().addContainerGap(113, 32767).addComponent(this.mediumPlusHardButton))
                                        )
                                        .addContainerGap()
                        )
        );
        jPanel1Layout.linkSize(0, this.downButton, this.upButton);
        jPanel1Layout.linkSize(0, this.mediumButton, this.mediumPlusHardButton, this.resetButton, this.sstsButton);
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                Alignment.TRAILING,
                                jPanel1Layout.createSequentialGroup()
                                        .addContainerGap(327, 32767)
                                        .addComponent(this.mediumPlusHardButton)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.mediumButton)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(Alignment.BASELINE).addComponent(this.upButton).addComponent(this.sstsButton))
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(Alignment.BASELINE).addComponent(this.downButton).addComponent(this.resetButton))
                        )
        );
        this.jPanel4.setLayout(new BorderLayout());
        this.stepList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                ConfigProgressPanel.this.stepListMouseClicked(evt);
            }
        });
        this.stepList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent evt) {
                ConfigProgressPanel.this.stepListValueChanged(evt);
            }
        });
        this.stepScrollPane.setViewportView(this.stepList);
        this.jPanel4.add(this.stepScrollPane, "Center");
        this.listButton.setIcon(new ImageIcon(this.getClass().getResource("/img/listview16b.png")));
        this.listButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigProgressPanel.this.listButtonActionPerformed(evt);
            }
        });
        this.jToolBar1.add(this.listButton);
        this.treeButton.setIcon(new ImageIcon(this.getClass().getResource("/img/treeview16b.png")));
        this.treeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigProgressPanel.this.treeButtonActionPerformed(evt);
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
                                        .addComponent(this.jPanel4, -1, 239, 32767)
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
                                                        .addComponent(this.jPanel1, Alignment.LEADING, -1, -1, 32767)
                                                        .addComponent(this.jPanel4, Alignment.LEADING, -1, 437, 32767)
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

    private void downButtonActionPerformed(ActionEvent evt) {
        int index = this.stepList.getSelectedIndex();
        if (index < this.steps.length - 1) {
            this.moveOneStep(index, true);
        }
    }

    private void upButtonActionPerformed(ActionEvent evt) {
        int index = this.stepList.getSelectedIndex();
        if (index > 0) {
            this.moveOneStep(index, false);
        }
    }

    private void stepListValueChanged(ListSelectionEvent evt) {
        if (evt == null || !evt.getValueIsAdjusting()) {
            if (this.stepList.getSelectedValue() == null) {
                return;
            }

            this.upButton.setEnabled(true);
            this.downButton.setEnabled(true);
            if (this.stepList.getSelectedIndex() == 0) {
                this.upButton.setEnabled(false);
            }

            if (this.stepList.getSelectedIndex() >= this.steps.length - 1) {
                this.downButton.setEnabled(false);
            }
        }
    }

    private void stepListMouseClicked(MouseEvent evt) {
        int index = this.stepList.locationToIndex(evt.getPoint());
        if (index == this.stepList.getSelectedIndex()) {
            StepConfig conf = (StepConfig) this.stepList.getSelectedValue();
            conf.setEnabledProgress(!conf.isEnabledProgress());
            this.stepList.repaint();
        }
    }

    private void sstsButtonActionPerformed(ActionEvent evt) {
        this.setSSTS();
        this.checkSteps();
    }

    private void mediumButtonActionPerformed(ActionEvent evt) {
        this.setAllBelowLevel(Options.getInstance().getDifficultyLevel(DifficultyType.MEDIUM.ordinal()));
        this.checkSteps();
    }

    private void mediumPlusHardButtonActionPerformed(ActionEvent evt) {
        this.setAllBelowLevel(Options.getInstance().getDifficultyLevel(DifficultyType.HARD.ordinal()));
        this.checkSteps();
    }

    private void setAllBelowLevel(DifficultyLevel level) {
        this.invalidTypes.clear();

        for (int i = 0; i < this.steps.length; i++) {
            this.steps[i].setEnabledProgress(false);
            if (this.steps[i].getLevel() <= level.getOrdinal()) {
                if (this.steps[i].isEnabled()) {
                    this.steps[i].setEnabledProgress(true);
                } else {
                    this.invalidTypes.add(this.steps[i]);
                }
            }
        }
    }

    private void setSSTS() {
        this.invalidTypes.clear();

        for (int i = 0; i < this.steps.length; i++) {
            this.steps[i].setEnabledProgress(false);
        }

        for (int i = 0; i < SSTS_CONFIG.length; i++) {
            this.setOneSSTSStep(SSTS_CONFIG[i]);
        }

        this.sortSteps(this.steps);
    }

    private void setOneSSTSStep(ConfigProgressPanel.SSTSConfig config) {
        StepConfig step = null;
        StepConfig other = null;

        for (int i = 0; i < this.steps.length; i++) {
            if (this.steps[i].getType() == config.type) {
                step = this.steps[i];
            } else if (this.steps[i].getIndexProgress() == config.progressIndex) {
                other = this.steps[i];
            }
        }

        if (other != null) {
            other.setIndexProgress(step.getIndexProgress());
        }

        step.setIndexProgress(config.progressIndex);
        if (!step.isEnabled()) {
            this.invalidTypes.add(step);
        } else {
            step.setEnabledProgress(true);
        }
    }

    private void sortSteps(StepConfig[] array) {
        Arrays.sort(array, new Comparator<StepConfig>() {
            public int compare(StepConfig o1, StepConfig o2) {
                return o1.getIndexProgress() - o2.getIndexProgress();
            }
        });
    }

    private void checkSteps() {
        if (this.invalidTypes.size() > 0) {
            StringBuilder buffer = new StringBuilder();

            for (int i = 0; i < this.invalidTypes.size(); i++) {
                if (i != 0) {
                    buffer.append(", ");
                    if (i % 5 == 0) {
                        buffer.append("\r\n");
                    }
                }

                buffer.append(this.invalidTypes.get(i).getType().getStepName());
            }

            JOptionPane.showMessageDialog(
                    this,
                    ResourceBundle.getBundle("intl/ConfigProgressPanel").getString("ConfigProgressPanel.techniques") + buffer.toString(),
                    ResourceBundle.getBundle("intl/ConfigProgressPanel").getString("ConfigProgressPanel.error"),
                    0
            );
        }

        this.resetView();
        if (!this.listView) {
            this.stepTree.repaint();
        }
    }

    private void moveOneStep(int index, boolean up) {
        int toIndex = up ? index + 1 : index - 1;
        StepConfig dummy = this.steps[index];
        this.steps[index] = this.steps[toIndex];
        this.steps[toIndex] = dummy;
        int dummyIndex = this.steps[index].getIndexProgress();
        this.steps[index].setIndexProgress(this.steps[toIndex].getIndexProgress());
        this.steps[toIndex].setIndexProgress(dummyIndex);
        this.model.remove(index);
        this.model.add(toIndex, this.steps[toIndex]);
        this.stepList.setSelectedIndex(toIndex);
        this.stepList.ensureIndexIsVisible(toIndex);
        this.stepList.repaint();
    }

    @Override
    public void moveStep(int fromIndex, int toIndex) {
        boolean up = fromIndex < toIndex;
        int anz = Math.abs(fromIndex - toIndex);
        if (up) {
            anz--;
        }

        for (int i = 0; i < anz; i++) {
            this.moveOneStep(fromIndex, up);
            if (up) {
                fromIndex++;
            } else {
                fromIndex--;
            }
        }
    }

    @Override
    public void setDropLocation(int index, StepConfig object) {
        this.dropIndex = index;
        this.dropObject = object;
        if (index != -1) {
            if (index <= this.stepList.getFirstVisibleIndex() + 1) {
                this.stepList.ensureIndexIsVisible(index - 1);
            } else if (index >= this.stepList.getLastVisibleIndex() - 1) {
                this.stepList.ensureIndexIsVisible(index + 1);
            }
        }
    }

    public void okPressed() {
        Options instance = Options.getInstance();
        StepConfig[] orgSteps = instance.solverSteps;

        for (int i = 0; i < this.steps.length; i++) {
            for (int j = 0; j < orgSteps.length; j++) {
                if (this.steps[i].getType() == orgSteps[j].getType() && orgSteps[i].getLevel() <= DifficultyType.UNFAIR.ordinal()) {
                    orgSteps[j].setEnabledProgress(this.steps[i].isEnabledProgress());
                    orgSteps[j].setIndexProgress(this.steps[i].getIndexProgress());
                    break;
                }
            }
        }

        orgSteps = instance.getOrgSolverSteps();

        for (int i = 0; i < this.steps.length; i++) {
            for (int j = 0; j < orgSteps.length; j++) {
                if (this.steps[i].getType() == orgSteps[j].getType() && orgSteps[i].getLevel() <= DifficultyType.UNFAIR.ordinal()) {
                    orgSteps[j].setEnabledProgress(this.steps[i].isEnabledProgress());
                    orgSteps[j].setIndexProgress(this.steps[i].getIndexProgress());
                    break;
                }
            }
        }

        instance.solverStepsProgress = instance.copyStepConfigs(instance.getOrgSolverSteps(), false, false, false, true);
    }

    private void initAll(boolean setDefault) {
        if (setDefault) {
            this.steps = Options.getInstance().copyStepConfigs(Options.getInstance().solverStepsProgress, true, false, false, true);
            StepConfig[] orgSteps = Options.DEFAULT_SOLVER_STEPS;

            for (int i = 0; i < this.steps.length; i++) {
                for (int j = 0; j < orgSteps.length; j++) {
                    if (this.steps[i].getType() == orgSteps[j].getType()) {
                        this.steps[i].setEnabledProgress(orgSteps[j].isEnabledProgress());
                        this.steps[i].setIndexProgress(orgSteps[j].getIndexProgress());
                        break;
                    }
                }
            }

            this.sortSteps(this.steps);
        } else {
            this.steps = Options.getInstance().copyStepConfigs(Options.getInstance().solverStepsProgress, true, false, false, true);
        }

        this.resetView();
    }

    private void resetView() {
        this.model.removeAllElements();

        for (int i = 0; i < this.steps.length; i++) {
            if (this.steps[i].getLevel() <= DifficultyType.UNFAIR.ordinal()) {
                this.model.addElement(this.steps[i]);
            }
        }

        this.stepList.setSelectedIndex(-1);
        this.stepList.ensureIndexIsVisible(0);
        this.stepList.repaint();
        this.buildTree();
    }

    public void buildTree() {
        CheckNode root = new CheckNode();

        for (int i = 0; i < this.steps.length; i++) {
            if (this.steps[i].getLevel() <= DifficultyType.UNFAIR.ordinal()) {
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
                            this.steps[i].getCategoryName(), true, this.steps[i].isEnabledProgress() ? 2 : 0, null, false, true, false, this.steps[i].getCategory()
                    );
                    root.add(act);
                }

                act.add(
                        new CheckNode(this.steps[i].getType().getStepName(), false, this.steps[i].isEnabledProgress() ? 2 : 0, this.steps[i], false, true, false, null)
                );
                if (act.getSelectionState() == 2 && !this.steps[i].isEnabledProgress()) {
                    act.setSelectionState(1);
                }

                if (act.getSelectionState() == 0 && this.steps[i].isEnabledProgress()) {
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
                if (this.stepList.getSelectedIndex() >= 0) {
                    this.stepListValueChanged(null);
                }
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

    static class SSTSConfig {
        SolutionType type;
        int progressIndex;

        SSTSConfig(SolutionType type, int progressIndex) {
            this.type = type;
            this.progressIndex = progressIndex;
        }
    }

    class CheckBoxRenderer extends JCheckBox implements ListCellRenderer {
        private static final long serialVersionUID = 1L;
        private boolean isTargetCell;
        private int index;

        public CheckBoxRenderer() {
        }

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
            this.setSelected(((StepConfig) obj).isEnabledProgress());
            this.isTargetCell = false;
            this.index = index;
            if (index == ConfigProgressPanel.this.dropIndex) {
                this.isTargetCell = true;
            }

            return this;
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            if (this.isTargetCell) {
                Insets insets = this.getInsets();
                g2.setColor(ConfigProgressPanel.this.dndColor);
                g2.setStroke(ConfigProgressPanel.this.dndStroke);
                g2.drawLine(insets.left - 2, 0, insets.left - 2, 3);
                g2.drawLine(insets.left - 1, 2, this.getSize().width, 2);
            }
        }
    }
}
