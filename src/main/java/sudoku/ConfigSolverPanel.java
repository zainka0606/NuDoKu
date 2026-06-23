package sudoku;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.ResourceBundle;

public class ConfigSolverPanel extends JPanel implements ListDragAndDropChange {
    private static final long serialVersionUID = 1L;
    private StepConfig[] steps;
    private DefaultListModel model;
    private int dropIndex = -1;
    private StepConfig dropObject;
    private Color dndColor;
    private Stroke dndStroke;
    private boolean firstSelected = false;
    private boolean listView = false;
    private JButton downButton;
    private JPanel jPanel1;
    private JPanel jPanel3;
    private JPanel jPanel4;
    private JToolBar jToolBar1;
    private JComboBox levelComboBox;
    private JLabel levelLabel;
    private JToggleButton listButton;
    private JButton resetButton;
    private JLabel scoreLabel;
    private JTextField scoreTextField;
    private JList stepList;
    private JScrollPane stepScrollPane;
    private JTree stepTree;
    private JToggleButton treeButton;
    private JButton upButton;

    public ConfigSolverPanel() {
        this.initComponents();
        Color tmpColor = UIManager.getColor("List.foreground");
        this.dndColor = new Color(tmpColor.getRed(), tmpColor.getGreen(), tmpColor.getBlue(), 100);
        this.dndStroke = new BasicStroke(2.0F, 1, 1);
        this.stepList.setSelectionMode(0);
        this.stepList.setCellRenderer(new ConfigSolverPanel.CheckBoxRenderer());
        this.model = new DefaultListModel();
        this.stepList.setModel(this.model);
        new ListDragAndDrop(this.stepList, this, this);
        this.stepTree.setCellRenderer(new CheckRenderer());
        this.stepTree.getSelectionModel().setSelectionMode(1);
        this.stepTree.putClientProperty("JTree.lineStyle", "Angled");

        for (int i = 1; i < Options.getInstance().getDifficultyLevels().length; i++) {
            this.levelComboBox.addItem(Options.getInstance().getDifficultyLevels()[i].getName());
        }

        NumbersOnlyDocument doc = new NumbersOnlyDocument();
        doc.addDocumentListener(new ConfigSolverPanel.MyDocumentListener());
        this.scoreTextField.setDocument(doc);
        this.initAll(false);
        this.checkButtons(true);
    }

    private void initComponents() {
        this.stepTree = new JTree();
        this.jPanel1 = new JPanel();
        this.jPanel3 = new JPanel();
        this.levelLabel = new JLabel();
        this.scoreLabel = new JLabel();
        this.levelComboBox = new JComboBox();
        this.scoreTextField = new JTextField();
        this.upButton = new JButton();
        this.downButton = new JButton();
        this.resetButton = new JButton();
        this.jPanel4 = new JPanel();
        this.stepScrollPane = new JScrollPane();
        this.stepList = new JList();
        this.jToolBar1 = new JToolBar();
        this.listButton = new JToggleButton();
        this.treeButton = new JToggleButton();
        this.stepTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                ConfigSolverPanel.this.stepTreeMousePressed(evt);
            }
        });
        this.stepTree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent evt) {
                ConfigSolverPanel.this.stepTreeValueChanged(evt);
            }
        });
        ResourceBundle bundle = ResourceBundle.getBundle("intl/ConfigSolverPanel");
        this.jPanel3.setBorder(BorderFactory.createTitledBorder(bundle.getString("ConfigSolverPanel.jPanel3.border.title")));
        this.levelLabel.setDisplayedMnemonic(ResourceBundle.getBundle("intl/ConfigSolverPanel").getString("ConfigSolverPanel.levelLabel.mnemonic").charAt(0));
        this.levelLabel.setLabelFor(this.levelComboBox);
        this.levelLabel.setText(bundle.getString("ConfigSolverPanel.levelLabel.text"));
        this.scoreLabel.setDisplayedMnemonic(ResourceBundle.getBundle("intl/ConfigSolverPanel").getString("ConfigSolverPanel.scoreLabel.mnemonic").charAt(0));
        this.scoreLabel.setLabelFor(this.scoreTextField);
        this.scoreLabel.setText(bundle.getString("ConfigSolverPanel.scoreLabel.text"));
        this.levelComboBox.setEnabled(false);
        this.levelComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigSolverPanel.this.levelComboBoxActionPerformed(evt);
            }
        });
        this.scoreTextField.setEnabled(false);
        GroupLayout jPanel3Layout = new GroupLayout(this.jPanel3);
        this.jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                jPanel3Layout.createSequentialGroup()
                                        .addGroup(jPanel3Layout.createParallelGroup(Alignment.LEADING).addComponent(this.levelLabel).addComponent(this.scoreLabel))
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                jPanel3Layout.createParallelGroup(Alignment.LEADING)
                                                        .addComponent(this.scoreTextField, -1, 224, 32767)
                                                        .addComponent(this.levelComboBox, 0, 224, 32767)
                                        )
                                        .addContainerGap()
                        )
        );
        jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                jPanel3Layout.createSequentialGroup()
                                        .addGroup(jPanel3Layout.createParallelGroup(Alignment.BASELINE).addComponent(this.levelLabel).addComponent(this.levelComboBox, -2, -1, -2))
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(jPanel3Layout.createParallelGroup(Alignment.BASELINE).addComponent(this.scoreLabel).addComponent(this.scoreTextField, -2, -1, -2))
                                        .addContainerGap(-1, 32767)
                        )
        );
        this.upButton.setMnemonic(ResourceBundle.getBundle("intl/ConfigSolverPanel").getString("ConfigSolverPanel.upButton.mnemonic").charAt(0));
        this.upButton.setText(bundle.getString("ConfigSolverPanel.upButton.text"));
        this.upButton.setEnabled(false);
        this.upButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigSolverPanel.this.upButtonActionPerformed(evt);
            }
        });
        this.downButton.setMnemonic(ResourceBundle.getBundle("intl/ConfigSolverPanel").getString("ConfigSolverPanel.downButton.mnemonic").charAt(0));
        this.downButton.setText(bundle.getString("ConfigSolverPanel.downButton.text"));
        this.downButton.setEnabled(false);
        this.downButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigSolverPanel.this.downButtonActionPerformed(evt);
            }
        });
        this.resetButton.setMnemonic(ResourceBundle.getBundle("intl/ConfigSolverPanel").getString("ConfigSolverPanel.resetButton.mnemonic").charAt(0));
        this.resetButton.setText(bundle.getString("ConfigSolverPanel.resetButton.text"));
        this.resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigSolverPanel.this.resetButtonActionPerformed(evt);
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
                                                        .addGroup(jPanel1Layout.createSequentialGroup().addComponent(this.downButton).addGap(105, 105, 105).addComponent(this.resetButton))
                                                        .addComponent(this.upButton)
                                                        .addComponent(this.jPanel3, -1, -1, 32767)
                                        )
                                        .addContainerGap()
                        )
        );
        jPanel1Layout.linkSize(0, this.downButton, this.upButton);
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                Alignment.TRAILING,
                                jPanel1Layout.createSequentialGroup()
                                        .addComponent(this.jPanel3, -2, -1, -2)
                                        .addPreferredGap(ComponentPlacement.RELATED, 301, 32767)
                                        .addComponent(this.upButton)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(Alignment.BASELINE).addComponent(this.downButton).addComponent(this.resetButton))
                        )
        );
        this.jPanel4.setLayout(new BorderLayout());
        this.stepList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                ConfigSolverPanel.this.stepListMouseClicked(evt);
            }
        });
        this.stepList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent evt) {
                ConfigSolverPanel.this.stepListValueChanged(evt);
            }
        });
        this.stepScrollPane.setViewportView(this.stepList);
        this.jPanel4.add(this.stepScrollPane, "Center");
        this.listButton.setIcon(new ImageIcon(this.getClass().getResource("/img/listview16b.png")));
        this.listButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigSolverPanel.this.listButtonActionPerformed(evt);
            }
        });
        this.jToolBar1.add(this.listButton);
        this.treeButton.setIcon(new ImageIcon(this.getClass().getResource("/img/treeview16b.png")));
        this.treeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigSolverPanel.this.treeButtonActionPerformed(evt);
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
                                        .addComponent(this.jPanel4, -1, 276, 32767)
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
            CheckNode last = (CheckNode) this.stepTree.getLastSelectedPathComponent();
            if (act != null && last != null && act == last) {
                last.toggleSelectionState();
                this.stepTree.repaint();
            }
        }
    }

    private void stepTreeValueChanged(TreeSelectionEvent evt) {
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

    private void levelComboBoxActionPerformed(ActionEvent evt) {
        int index = this.stepList.getSelectedIndex();
        if (index != -1) {
            StepConfig conf = (StepConfig) this.stepList.getSelectedValue();
            conf.setLevel(this.levelComboBox.getSelectedIndex() + 1);
        }
    }

    private void stepListValueChanged(ListSelectionEvent evt) {
        if (evt == null || !evt.getValueIsAdjusting()) {
            if (this.stepList.getSelectedValue() == null) {
                return;
            }

            this.firstSelected = true;
            this.levelComboBox.setEnabled(true);
            this.scoreTextField.setEnabled(true);
            StepConfig conf = (StepConfig) this.stepList.getSelectedValue();
            this.levelComboBox.setSelectedIndex(conf.getLevel() - 1);
            this.scoreTextField.setText(Integer.toString(conf.getBaseScore()));
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
        if (this.firstSelected) {
            this.firstSelected = false;
        } else {
            int index = this.stepList.locationToIndex(evt.getPoint());
            if (index == this.stepList.getSelectedIndex()) {
                StepConfig conf = (StepConfig) this.stepList.getSelectedValue();
                conf.setEnabled(!conf.isEnabled());
                this.stepList.repaint();
            }
        }
    }

    private void moveOneStep(int index, boolean up) {
        int toIndex = up ? index + 1 : index - 1;
        StepConfig dummy = this.steps[index];
        this.steps[index] = this.steps[toIndex];
        this.steps[toIndex] = dummy;
        int dummyIndex = this.steps[index].getIndex();
        this.steps[index].setIndex(this.steps[toIndex].getIndex());
        this.steps[toIndex].setIndex(dummyIndex);
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
        Options.getInstance().solverSteps = Options.getInstance().copyStepConfigs(this.steps, false, true);
        Options.getInstance().adjustOrgSolverSteps();
    }

    private void initAll(boolean setDefault) {
        if (setDefault) {
            this.steps = Options.getInstance().copyStepConfigs(Options.DEFAULT_SOLVER_STEPS, true, false);
        } else {
            this.steps = Options.getInstance().copyStepConfigs(Options.getInstance().solverSteps, true, false);
        }

        this.model.removeAllElements();

        for (int i = 0; i < this.steps.length; i++) {
            this.model.addElement(this.steps[i]);
        }

        this.stepList.setSelectedIndex(-1);
        this.stepList.ensureIndexIsVisible(0);
        this.stepList.repaint();
        this.levelComboBox.setSelectedIndex(-1);
        this.scoreTextField.setText("");
        this.buildTree();
    }

    public void buildTree() {
        CheckNode root = new CheckNode();

        for (int i = 0; i < this.steps.length; i++) {
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
                        this.steps[i].getCategoryName(), true, this.steps[i].isEnabled() ? 2 : 0, null, false, false, false, this.steps[i].getCategory()
                );
                root.add(act);
            }

            act.add(new CheckNode(this.steps[i].getType().getStepName(), false, this.steps[i].isEnabled() ? 2 : 0, this.steps[i], false, false, false, null));
            if (act.getSelectionState() == 2 && !this.steps[i].isEnabled()) {
                act.setSelectionState(1);
            }

            if (act.getSelectionState() == 0 && this.steps[i].isEnabled()) {
                act.setSelectionState(1);
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
                this.levelComboBox.setEnabled(false);
                this.scoreTextField.setEnabled(false);
            }

            this.stepTree.requestFocusInWindow();
        }
    }

    class CheckBoxRenderer extends JCheckBox implements ListCellRenderer {
        private static final long serialVersionUID = 1L;
        private boolean isTargetCell;
        private int index;

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
            this.setSelected(((StepConfig) obj).isEnabled());
            this.isTargetCell = false;
            this.index = index;
            if (index == ConfigSolverPanel.this.dropIndex) {
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
                g2.setColor(ConfigSolverPanel.this.dndColor);
                g2.setStroke(ConfigSolverPanel.this.dndStroke);
                g2.drawLine(insets.left - 2, 0, insets.left - 2, 3);
                g2.drawLine(insets.left - 1, 2, this.getSize().width, 2);
            }
        }
    }

    class MyDocumentListener implements DocumentListener {
        @Override
        public void insertUpdate(DocumentEvent e) {
            this.update(e);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            this.update(e);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
        }

        private void update(DocumentEvent e) {
            String txt = ConfigSolverPanel.this.scoreTextField.getText().trim();
            if (txt != null && !txt.isEmpty()) {
                StepConfig conf = (StepConfig) ConfigSolverPanel.this.stepList.getSelectedValue();

                try {
                    int value = Integer.parseInt(txt);
                    conf.setBaseScore(value);
                } catch (NumberFormatException ex) {
                    MessageFormat formatter = new MessageFormat(
                            ResourceBundle.getBundle("intl/ConfigSolverPanel.invalid_value").getString("MainFrame.invalid_filename")
                    );
                    String msg = formatter.format(new Object[]{txt});
                    JOptionPane.showMessageDialog(null, msg, ResourceBundle.getBundle("intl/ConfigSolverPanel").getString("ConfigSolverPanel.invalid_input"), 0);
                }
            }
        }
    }
}
