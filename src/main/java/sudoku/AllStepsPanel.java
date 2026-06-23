package sudoku;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JToolBar.Separator;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class AllStepsPanel extends JPanel implements TreeSelectionListener, Runnable {
    private static final long serialVersionUID = 1L;
    private Sudoku2 sudoku;
    private MainFrame mainFrame;
    private DefaultTreeModel model;
    private List<SolutionStep> steps;
    private JToggleButton[] toggleButtons = null;
    private JButton addToSolutionButton;
    private JToggleButton cellSortToggleButton;
    private JButton configureButton;
    private JToggleButton directSingleSortToggleButton;
    private JToggleButton eliminationSortToggleButton;
    private JButton findButton;
    private JPanel jPanel2;
    private JScrollPane jScrollPane1;
    private Separator jSeparator1;
    private JToolBar jToolBar1;
    private JToggleButton singleSortToggleButton;
    private JTree stepsTree;
    private JToggleButton typeSortToggleButton;

    public AllStepsPanel(MainFrame mainFrame, Sudoku2 sudoku) {
        this.mainFrame = mainFrame;
        this.setSudoku(sudoku);
        this.initComponents();
        this.toggleButtons = new JToggleButton[]{
                this.directSingleSortToggleButton, this.singleSortToggleButton, this.cellSortToggleButton, this.eliminationSortToggleButton, this.typeSortToggleButton
        };
        this.stepsTree.getSelectionModel().setSelectionMode(1);
        this.stepsTree.addTreeSelectionListener(this);
        FontMetrics metrics = this.getFontMetrics(this.getFont());
        int rowHeight = (int) (metrics.getHeight() * 1.0);
        this.stepsTree.setRowHeight(rowHeight);
        this.resetPanel();
    }

    private void initComponents() {
        this.jPanel2 = new JPanel();
        this.findButton = new JButton();
        this.addToSolutionButton = new JButton();
        this.jScrollPane1 = new JScrollPane();
        this.stepsTree = new JTree();
        this.jToolBar1 = new JToolBar();
        this.configureButton = new JButton();
        this.jSeparator1 = new Separator();
        this.directSingleSortToggleButton = new JToggleButton();
        this.singleSortToggleButton = new JToggleButton();
        this.cellSortToggleButton = new JToggleButton();
        this.eliminationSortToggleButton = new JToggleButton();
        this.typeSortToggleButton = new JToggleButton();
        this.setLayout(new BorderLayout());
        this.findButton.setMnemonic(ResourceBundle.getBundle("intl/AllStepsPanel").getString("AllStepsPanel.findButton.mnemonic").charAt(0));
        ResourceBundle bundle = ResourceBundle.getBundle("intl/AllStepsPanel");
        this.findButton.setText(bundle.getString("AllStepsPanel.findButton.text"));
        this.findButton.setToolTipText(bundle.getString("AllStepsPanel.findButton.toolTipText"));
        this.findButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                AllStepsPanel.this.findButtonActionPerformed(evt);
            }
        });
        this.addToSolutionButton.setMnemonic(ResourceBundle.getBundle("intl/AllStepsPanel").getString("AllStepsPanel.addToSolution.mnemonic").charAt(0));
        this.addToSolutionButton.setText(bundle.getString("AllStepsPanel.addToSolutionButton.text"));
        this.addToSolutionButton.setToolTipText(bundle.getString("AllStepsPanel.addToSolutionButton.toolTipText"));
        this.addToSolutionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                AllStepsPanel.this.addToSolutionButtonActionPerformed(evt);
            }
        });
        GroupLayout jPanel2Layout = new GroupLayout(this.jPanel2);
        this.jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                jPanel2Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(this.findButton)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.addToSolutionButton)
                                        .addContainerGap(106, 32767)
                        )
        );
        jPanel2Layout.linkSize(0, this.addToSolutionButton, this.findButton);
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                Alignment.TRAILING,
                                jPanel2Layout.createSequentialGroup()
                                        .addContainerGap(-1, 32767)
                                        .addGroup(jPanel2Layout.createParallelGroup(Alignment.BASELINE).addComponent(this.findButton).addComponent(this.addToSolutionButton))
                                        .addContainerGap()
                        )
        );
        this.add(this.jPanel2, "South");
        this.jScrollPane1.setViewportView(this.stepsTree);
        this.add(this.jScrollPane1, "Center");
        this.jToolBar1.setRollover(true);
        this.configureButton.setIcon(new ImageIcon(this.getClass().getResource("/img/settings.png")));
        this.configureButton.setToolTipText(bundle.getString("AllStepsPanel.configureButton.toolTipText"));
        this.configureButton.setFocusable(false);
        this.configureButton.setHorizontalTextPosition(0);
        this.configureButton.setMaximumSize(new Dimension(36, 36));
        this.configureButton.setVerticalTextPosition(3);
        this.configureButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                AllStepsPanel.this.configureButtonActionPerformed(evt);
            }
        });
        this.jToolBar1.add(this.configureButton);
        this.jToolBar1.add(this.jSeparator1);
        this.directSingleSortToggleButton.setIcon(new ImageIcon(this.getClass().getResource("/img/search_d1.png")));
        this.directSingleSortToggleButton.setToolTipText(bundle.getString("AllStepsPanel.directSingleSortToggleButton.toolTipText"));
        this.directSingleSortToggleButton.setFocusable(false);
        this.directSingleSortToggleButton.setHorizontalTextPosition(0);
        this.directSingleSortToggleButton.setVerticalTextPosition(3);
        this.directSingleSortToggleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                AllStepsPanel.this.directSingleSortToggleButtonActionPerformed(evt);
            }
        });
        this.jToolBar1.add(this.directSingleSortToggleButton);
        this.singleSortToggleButton.setIcon(new ImageIcon(this.getClass().getResource("/img/search_s1.png")));
        this.singleSortToggleButton.setToolTipText(bundle.getString("AllStepsPanel.singleSortToggleButton.toolTipText"));
        this.singleSortToggleButton.setFocusable(false);
        this.singleSortToggleButton.setHorizontalTextPosition(0);
        this.singleSortToggleButton.setVerticalTextPosition(3);
        this.singleSortToggleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                AllStepsPanel.this.singleSortToggleButtonActionPerformed(evt);
            }
        });
        this.jToolBar1.add(this.singleSortToggleButton);
        this.cellSortToggleButton.setIcon(new ImageIcon(this.getClass().getResource("/img/search_c1.png")));
        this.cellSortToggleButton.setToolTipText(bundle.getString("AllStepsPanel.cellSortToggleButton.toolTipText"));
        this.cellSortToggleButton.setFocusable(false);
        this.cellSortToggleButton.setHorizontalTextPosition(0);
        this.cellSortToggleButton.setVerticalTextPosition(3);
        this.cellSortToggleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                AllStepsPanel.this.cellSortToggleButtonActionPerformed(evt);
            }
        });
        this.jToolBar1.add(this.cellSortToggleButton);
        this.eliminationSortToggleButton.setIcon(new ImageIcon(this.getClass().getResource("/img/search_e1.png")));
        this.eliminationSortToggleButton.setToolTipText(bundle.getString("AllStepsPanel.eliminationSortToggleButton.toolTipText"));
        this.eliminationSortToggleButton.setFocusable(false);
        this.eliminationSortToggleButton.setHorizontalTextPosition(0);
        this.eliminationSortToggleButton.setVerticalTextPosition(3);
        this.eliminationSortToggleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                AllStepsPanel.this.eliminationSortToggleButtonActionPerformed(evt);
            }
        });
        this.jToolBar1.add(this.eliminationSortToggleButton);
        this.typeSortToggleButton.setIcon(new ImageIcon(this.getClass().getResource("/img/search_t1.png")));
        this.typeSortToggleButton.setToolTipText(bundle.getString("AllStepsPanel.typeSortToggleButton.toolTipText"));
        this.typeSortToggleButton.setFocusable(false);
        this.typeSortToggleButton.setHorizontalTextPosition(0);
        this.typeSortToggleButton.setVerticalTextPosition(3);
        this.typeSortToggleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                AllStepsPanel.this.typeSortToggleButtonActionPerformed(evt);
            }
        });
        this.jToolBar1.add(this.typeSortToggleButton);
        this.add(this.jToolBar1, "First");
    }

    private void addToSolutionButtonActionPerformed(ActionEvent evt) {
        DefaultMutableTreeNode last = (DefaultMutableTreeNode) this.stepsTree.getLastSelectedPathComponent();
        SolutionStep actStep = null;
        if (last != null) {
            if (last.getUserObject() instanceof String) {
                Enumeration<?> children = last.children();

                while (children.hasMoreElements()) {
                    DefaultMutableTreeNode act = (DefaultMutableTreeNode) children.nextElement();
                    if (act.getUserObject() instanceof SolutionStep) {
                        actStep = (SolutionStep) act.getUserObject();
                        break;
                    }
                }
            } else {
                actStep = (SolutionStep) last.getUserObject();
            }
        }

        if (actStep == null) {
            JOptionPane.showMessageDialog(
                    this,
                    ResourceBundle.getBundle("intl/AllStepsPanel").getString("AllStepsPanel.no_solution_selected"),
                    ResourceBundle.getBundle("intl/AllStepsPanel").getString("AllStepsPanel.error"),
                    0
            );
        } else {
            this.mainFrame.getSolutionPanel().addStep(actStep);
        }
    }

    private void findButtonActionPerformed(ActionEvent evt) {
        if (this.sudoku == null) {
            JOptionPane.showMessageDialog(
                    this,
                    ResourceBundle.getBundle("intl/AllStepsPanel").getString("AllStepsPanel.sudoku_not_set"),
                    ResourceBundle.getBundle("intl/AllStepsPanel").getString("AllStepsPanel.error"),
                    0
            );
        } else {
            this.resetTreeNodes();
            new Thread(this).start();
        }
    }

    private void configureButtonActionPerformed(ActionEvent evt) {
        new ConfigDialog(this.mainFrame, true, 3).setVisible(true);
    }

    private void directSingleSortToggleButtonActionPerformed(ActionEvent evt) {
        this.setSortMode(0);
    }

    private void singleSortToggleButtonActionPerformed(ActionEvent evt) {
        this.setSortMode(1);
    }

    private void cellSortToggleButtonActionPerformed(ActionEvent evt) {
        this.setSortMode(2);
    }

    private void eliminationSortToggleButtonActionPerformed(ActionEvent evt) {
        this.setSortMode(3);
    }

    private void typeSortToggleButtonActionPerformed(ActionEvent evt) {
        this.setSortMode(4);
    }

    @Override
    public void run() {
        FindAllStepsProgressDialog dlg = new FindAllStepsProgressDialog(this.mainFrame, true, this.sudoku);
        dlg.setVisible(true);
        this.steps = dlg.getSteps();
        this.createTreeNodes(Options.getInstance().getAllStepsSortMode());
    }

    public void resetPanel() {
        this.model = new DefaultTreeModel(new DefaultMutableTreeNode(ResourceBundle.getBundle("intl/AllStepsPanel").getString("AllStepsPanel.no_solutions")));
        this.steps = null;
        this.stepsTree.setModel(this.model);
        this.adjustToggleButtons();
        this.createTreeNodes(Options.getInstance().getAllStepsSortMode());
    }

    private void adjustToggleButtons() {
        if (this.toggleButtons != null) {
            for (int i = 0; i < this.toggleButtons.length; i++) {
                if (i == Options.getInstance().getAllStepsSortMode()) {
                    this.toggleButtons[i].setSelected(true);
                } else {
                    this.toggleButtons[i].setSelected(false);
                }
            }
        }
    }

    private void setSortMode(int sortMode) {
        Options.getInstance().setAllStepsSortMode(sortMode);
        this.adjustToggleButtons();
        this.createTreeNodes(Options.getInstance().getAllStepsSortMode());
    }

    private void resetTreeNodes() {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) this.model.getRoot();
        root.removeAllChildren();
        root.setUserObject(ResourceBundle.getBundle("intl/AllStepsPanel").getString("AllStepsPanel.searching"));
        this.model.reload();
        this.stepsTree.setRootVisible(true);
        this.stepsTree.repaint();
    }

    private void createTreeNodes(int sortMode) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) this.model.getRoot();
        root.removeAllChildren();
        if (this.steps != null && !this.steps.isEmpty()) {
            switch (sortMode) {
                case 0:
                    this.createTreeNodesDirectSingles(root);
                    break;
                case 1:
                    this.createTreeNodesSingles(root);
                    break;
                case 2:
                    this.createTreeNodesCells(root);
                    break;
                case 3:
                    this.createTreeNodesNumberOfEliminations(root);
                    break;
                case 4:
                    this.createTreeNodesTypes(root);
                    break;
                default:
                    Logger.getLogger(AllStepsPanel.class.getName()).log(Level.SEVERE, "Invalid sort mode ({0})", sortMode);
            }

            this.stepsTree.expandPath(new TreePath(root));
            this.stepsTree.setShowsRootHandles(true);
            this.model.reload();
            this.stepsTree.repaint();
        } else {
            root.setUserObject(ResourceBundle.getBundle("intl/AllStepsPanel").getString("AllStepsPanel.no_solution"));
            this.stepsTree.setRootVisible(true);
            this.model.reload();
            this.stepsTree.repaint();
        }
    }

    private void createTreeNodesTypes(DefaultMutableTreeNode root) {
        Collections.sort(this.steps, new Comparator<SolutionStep>() {
            public int compare(SolutionStep o1, SolutionStep o2) {
                int ret = o1.getType().getStepConfig().getIndex() - o2.getType().getStepConfig().getIndex();
                if (ret == 0) {
                    ret = o1.getType().getStepName().compareTo(o2.getType().getStepName());
                    if (ret == 0) {
                        ret = o1.compareTo(o2);
                    }
                }

                return ret;
            }
        });
        root.setUserObject(ResourceBundle.getBundle("intl/AllStepsPanel").getString("AllStepsPanel.possible_steps"));
        this.stepsTree.setRootVisible(false);
        SolutionStep lastStep = null;
        DefaultMutableTreeNode lastCat1 = null;
        DefaultMutableTreeNode lastEntry = null;

        for (SolutionStep step : this.steps) {
            if (lastStep != null && step.getType() == lastStep.getType()) {
                if (step.isEqualCandidate(lastStep) && step.isEqualValues(lastStep)) {
                    lastEntry.add(new DefaultMutableTreeNode(step));
                } else {
                    lastEntry = new DefaultMutableTreeNode(step);
                    lastCat1.add(lastEntry);
                }
            } else {
                if (lastStep != null) {
                    String dummy = (String) lastCat1.getUserObject();
                    lastCat1.setUserObject(dummy + " (" + lastCat1.getChildCount() + ")");
                }

                lastCat1 = new DefaultMutableTreeNode(step.getType().getStepName());
                root.add(lastCat1);
                lastEntry = new DefaultMutableTreeNode(step);
                lastCat1.add(lastEntry);
            }

            lastStep = step;
        }

        if (lastStep != null) {
            String dummy = (String) lastCat1.getUserObject();
            lastCat1.setUserObject(dummy + " (" + lastCat1.getChildCount() + ")");
        }
    }

    private void createTreeNodesCells(DefaultMutableTreeNode root) {
        SortedMap<Integer, List<SolutionStep>> map = new TreeMap<>();

        for (SolutionStep step : this.steps) {
            for (Candidate delCand : step.getCandidatesToDelete()) {
                int candIndex = delCand.getIndex() * 10 + delCand.getValue();
                List<SolutionStep> stepList = map.get(candIndex);
                if (stepList == null) {
                    stepList = new ArrayList<>();
                    map.put(candIndex, stepList);
                }

                stepList.add(step);
            }
        }

        root.setUserObject(ResourceBundle.getBundle("intl/AllStepsPanel").getString("AllStepsPanel.possible_steps"));
        this.stepsTree.setRootVisible(false);
        DefaultMutableTreeNode lastCat1 = null;
        DefaultMutableTreeNode lastCat2 = null;
        DefaultMutableTreeNode lastEntry = null;
        int lastIndex = -1;

        for (Entry<Integer, List<SolutionStep>> entry : map.entrySet()) {
            int index = entry.getKey() / 10;
            int candidate = entry.getKey() % 10;
            if (index != lastIndex) {
                String cell = SolutionStep.getCellPrint(index, false);
                lastCat1 = new DefaultMutableTreeNode(cell);
                root.add(lastCat1);
                lastIndex = index;
            }

            lastCat2 = new DefaultMutableTreeNode(ResourceBundle.getBundle("intl/AllStepsPanel").getString("AllStepsPanel.candidate") + " " + candidate);
            lastCat1.add(lastCat2);
            List<SolutionStep> stepList = entry.getValue();
            Collections.sort(stepList, new Comparator<SolutionStep>() {
                public int compare(SolutionStep o1, SolutionStep o2) {
                    int ret = o1.getType().getStepConfig().getIndex() - o2.getType().getStepConfig().getIndex();
                    if (ret == 0) {
                        ret = o1.getType().getStepName().compareTo(o2.getType().getStepName());
                        if (ret == 0) {
                            ret = o1.compareTo(o2);
                        }
                    }

                    return ret;
                }
            });
            SolutionStep lastStep = null;

            for (SolutionStep step : stepList) {
                if (lastStep != null && step.isEqual(lastStep)) {
                    lastEntry.add(new DefaultMutableTreeNode(step));
                } else {
                    lastEntry = new DefaultMutableTreeNode(step);
                    lastCat2.add(lastEntry);
                }

                lastStep = step;
            }
        }
    }

    private void createTreeNodesSingles(DefaultMutableTreeNode root) {
        Collections.sort(this.steps, new Comparator<SolutionStep>() {
            public int compare(SolutionStep o1, SolutionStep o2) {
                int ret = o2.getProgressScoreSingles() - o1.getProgressScoreSingles();
                if (ret == 0) {
                    ret = o1.getProgressScore() - o2.getProgressScore();
                    if (ret == 0) {
                        ret = o1.compareTo(o2);
                    }
                }

                return ret;
            }
        });
        root.setUserObject(ResourceBundle.getBundle("intl/AllStepsPanel").getString("AllStepsPanel.possible_steps"));
        this.stepsTree.setRootVisible(false);
        SolutionStep lastStep = null;
        DefaultMutableTreeNode lastCat1 = null;
        DefaultMutableTreeNode lastCat2 = null;
        DefaultMutableTreeNode lastEntry = null;

        for (SolutionStep step : this.steps) {
            if (lastStep == null || step.getProgressScoreSingles() != lastStep.getProgressScoreSingles()) {
                lastCat1 = new DefaultMutableTreeNode(
                        ResourceBundle.getBundle("intl/AllStepsPanel").getString("AllStepsPanel.singles") + " " + step.getProgressScoreSingles()
                );
                root.add(lastCat1);
                lastCat2 = new DefaultMutableTreeNode(
                        ResourceBundle.getBundle("intl/AllStepsPanel").getString("AllStepsPanel.score") + " " + step.getProgressScore()
                );
                lastCat1.add(lastCat2);
                lastEntry = new DefaultMutableTreeNode(step);
                lastCat2.add(lastEntry);
            } else if (step.getProgressScore() != lastStep.getProgressScore()) {
                lastCat2 = new DefaultMutableTreeNode(
                        ResourceBundle.getBundle("intl/AllStepsPanel").getString("AllStepsPanel.score") + " " + step.getProgressScore()
                );
                lastCat1.add(lastCat2);
                lastEntry = new DefaultMutableTreeNode(step);
                lastCat2.add(lastEntry);
            } else if (step.isEqual(lastStep)) {
                lastEntry.add(new DefaultMutableTreeNode(step));
            } else {
                lastEntry = new DefaultMutableTreeNode(step);
                lastCat2.add(lastEntry);
            }

            lastStep = step;
        }
    }

    private void createTreeNodesDirectSingles(DefaultMutableTreeNode root) {
        Collections.sort(this.steps, new Comparator<SolutionStep>() {
            public int compare(SolutionStep o1, SolutionStep o2) {
                int ret = o2.getProgressScoreSinglesOnly() - o1.getProgressScoreSinglesOnly();
                if (ret == 0) {
                    ret = o2.getProgressScoreSingles() - o1.getProgressScoreSingles();
                    if (ret == 0) {
                        ret = o1.getProgressScore() - o2.getProgressScore();
                        if (ret == 0) {
                            ret = o1.compareTo(o2);
                        }
                    }
                }

                return ret;
            }
        });
        root.setUserObject(ResourceBundle.getBundle("intl/AllStepsPanel").getString("AllStepsPanel.possible_steps"));
        this.stepsTree.setRootVisible(false);
        SolutionStep lastStep = null;
        DefaultMutableTreeNode lastCat1 = null;
        DefaultMutableTreeNode lastCat2 = null;
        DefaultMutableTreeNode lastCat3 = null;
        DefaultMutableTreeNode lastEntry = null;

        for (SolutionStep step : this.steps) {
            if (lastStep == null || step.getProgressScoreSinglesOnly() != lastStep.getProgressScoreSinglesOnly()) {
                lastCat1 = new DefaultMutableTreeNode(
                        ResourceBundle.getBundle("intl/AllStepsPanel").getString("AllStepsPanel.direct_singles") + " " + step.getProgressScoreSinglesOnly()
                );
                root.add(lastCat1);
                lastCat2 = new DefaultMutableTreeNode(
                        ResourceBundle.getBundle("intl/AllStepsPanel").getString("AllStepsPanel.singles") + " " + step.getProgressScoreSingles()
                );
                lastCat1.add(lastCat2);
                lastCat3 = new DefaultMutableTreeNode(
                        ResourceBundle.getBundle("intl/AllStepsPanel").getString("AllStepsPanel.score") + " " + step.getProgressScore()
                );
                lastCat2.add(lastCat3);
                lastEntry = new DefaultMutableTreeNode(step);
                lastCat3.add(lastEntry);
            } else if (step.getProgressScoreSingles() != lastStep.getProgressScoreSingles()) {
                lastCat2 = new DefaultMutableTreeNode(
                        ResourceBundle.getBundle("intl/AllStepsPanel").getString("AllStepsPanel.singles") + " " + step.getProgressScoreSingles()
                );
                lastCat1.add(lastCat2);
                lastCat3 = new DefaultMutableTreeNode(
                        ResourceBundle.getBundle("intl/AllStepsPanel").getString("AllStepsPanel.score") + " " + step.getProgressScore()
                );
                lastCat2.add(lastCat3);
                lastEntry = new DefaultMutableTreeNode(step);
                lastCat3.add(lastEntry);
            } else if (step.getProgressScore() != lastStep.getProgressScore()) {
                lastCat3 = new DefaultMutableTreeNode(
                        ResourceBundle.getBundle("intl/AllStepsPanel").getString("AllStepsPanel.score") + " " + step.getProgressScore()
                );
                lastCat2.add(lastCat3);
                lastEntry = new DefaultMutableTreeNode(step);
                lastCat3.add(lastEntry);
            } else if (step.isEqual(lastStep)) {
                lastEntry.add(new DefaultMutableTreeNode(step));
            } else {
                lastEntry = new DefaultMutableTreeNode(step);
                lastCat3.add(lastEntry);
            }

            lastStep = step;
        }
    }

    private void createTreeNodesNumberOfEliminations(DefaultMutableTreeNode root) {
        Collections.sort(this.steps);
        root.setUserObject(ResourceBundle.getBundle("intl/AllStepsPanel").getString("AllStepsPanel.possible_steps"));
        this.stepsTree.setRootVisible(false);
        SolutionStep lastStep = null;
        DefaultMutableTreeNode lastCat = null;
        DefaultMutableTreeNode lastEntry = null;

        for (SolutionStep step : this.steps) {
            if (!step.isSingle() && !step.isForcingChainSet()) {
                Logger.getLogger(AllStepsPanel.class.getName()).log(Level.FINER, step.toString(2));
                if (lastStep == null) {
                    Logger.getLogger(AllStepsPanel.class.getName()).log(Level.FINER, step.getCandidateString());
                    lastCat = new DefaultMutableTreeNode(step.getCandidateString());
                    lastEntry = new DefaultMutableTreeNode(step);
                    root.add(lastCat);
                    lastCat.add(lastEntry);
                } else if (step.isEqual(lastStep)) {
                    lastEntry.add(new DefaultMutableTreeNode(step));
                } else if (step.isEquivalent(lastStep)) {
                    lastEntry = new DefaultMutableTreeNode(step);
                    lastCat.add(lastEntry);
                } else {
                    Logger.getLogger(AllStepsPanel.class.getName()).log(Level.FINER, step.getCandidateString());
                    lastCat = new DefaultMutableTreeNode(step.getCandidateString());
                    lastEntry = new DefaultMutableTreeNode(step);
                    DefaultMutableTreeNode tmp = null;
                    if (tmp == null) {
                        root.insert(lastCat, this.getTopLevelIndex(root, step));
                    } else {
                        SolutionStep tmpStep = this.getStepFromNode(tmp);
                        if (tmpStep.isEquivalent(step)) {
                            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) tmp.getParent();
                            parent.insert(lastCat, parent.getIndex(tmp) + 1);
                        } else {
                            tmp.add(lastCat);
                        }
                    }

                    lastCat.add(lastEntry);
                }

                lastStep = step;
            }
        }

        for (SolutionStep step : this.steps) {
            if (step.isSingle() || step.isForcingChainSet()) {
                if (step.isForcingChainSet() && step.isEqual(lastStep)) {
                    lastEntry.add(new DefaultMutableTreeNode(step));
                } else {
                    lastCat = new DefaultMutableTreeNode(step.getSingleCandidateString());
                    lastEntry = new DefaultMutableTreeNode(step);
                    root.insert(lastCat, 0);
                    lastCat.add(lastEntry);
                }

                lastStep = step;
            }
        }

        this.adjustFishes(root);
    }

    private void adjustFishes(DefaultMutableTreeNode root) {
    }

    private int getTopLevelIndex(DefaultMutableTreeNode root, SolutionStep step) {
        int index = 0;

        for (Enumeration<?> nodes = root.children(); nodes.hasMoreElements(); index++) {
            DefaultMutableTreeNode nextNode = (DefaultMutableTreeNode) nodes.nextElement();
            SolutionStep actStep = this.getStepFromNode(nextNode);
            if (actStep.getCandidatesToDelete().size() < step.getCandidatesToDelete().size()) {
                break;
            }
        }

        return index;
    }

    private SolutionStep getStepFromNode(DefaultMutableTreeNode node) {
        if (node.getUserObject() instanceof SolutionStep) {
            return (SolutionStep) node.getUserObject();
        }

        if (node.getUserObject() instanceof String) {
            Enumeration<?> nodes = node.children();

            while (nodes.hasMoreElements()) {
                DefaultMutableTreeNode nextNode = (DefaultMutableTreeNode) nodes.nextElement();
                if (nextNode.getUserObject() instanceof SolutionStep) {
                    return (SolutionStep) nextNode.getUserObject();
                }
            }
        }

        return null;
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode last = (DefaultMutableTreeNode) this.stepsTree.getLastSelectedPathComponent();
        if (last != null) {
            if (last.getUserObject() instanceof String) {
                Enumeration<?> children = last.children();

                while (children.hasMoreElements()) {
                    DefaultMutableTreeNode act = (DefaultMutableTreeNode) children.nextElement();
                    if (act.getUserObject() instanceof SolutionStep) {
                        this.mainFrame.setSolutionStep((SolutionStep) act.getUserObject(), true);
                        break;
                    }
                }
            } else {
                this.mainFrame.setSolutionStep((SolutionStep) last.getUserObject(), true);
            }
        }
    }

    public void setSudoku(Sudoku2 sudoku) {
        this.sudoku = sudoku;
    }
}
