package sudoku;

import solver.SudokuSolver;
import solver.SudokuSolverFactory;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SolutionPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private MainFrame mainFrame;
    private SudokuSolver solver;
    private int rightMouseClickedIndex = -1;
    private List<String> titels = new ArrayList<>();
    private List<JList> lists = new ArrayList<>();
    private List<List<SolutionStep>> tabSteps = new ArrayList<>();
    private List<Integer> selectedIndices = new ArrayList<>();
    private List<Color[]> stepBackgroundColors = new ArrayList<>();
    private List<Color[]> stepForegroundColors = new ArrayList<>();
    private JList actList = null;
    private List<SolutionStep> actSteps;
    private int actSelectedIndex = -1;
    private Color[] actStepBackgroundColors;
    private Color[] actStepForegroundColors;
    private boolean inTabbedPaneRemoveAll = false;
    private JButton alleEinfachenButton;
    private JMenuItem deleteFromHereMenuItem;
    private JSeparator jSeparator1;
    private JSeparator jSeparator2;
    private JSeparator jSeparator3;
    private JList solutionList;
    private JPopupMenu solutionPopupMenu;
    private JScrollPane solutionScrollPane;
    private JTabbedPane solutionTabbedPane;
    private JMenuItem solveFromHereMenuItem;
    private JPanel southPanel;
    private JMenuItem tabDeleteMenuItem;
    private JMenuItem tabNewMenuItem;
    private JMenuItem tabNewNameMenuItem;
    private JPopupMenu tabPopupMenu;
    private JMenuItem tabPrintMenuItem;
    private JLabel titleLabel;
    private JMenuItem toStepMenuItem;
    private JButton weiterButton;

    public SolutionPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.initComponents();
        int fontSize = 12;
        if (this.getFont().getSize() > 12) {
            fontSize = this.getFont().getSize();
        }

        Font font = this.titleLabel.getFont();
        this.titleLabel.setFont(new Font(font.getName(), 1, fontSize));
        this.addTabPane();
        this.getActTab();
    }

    private void initComponents() {
        this.solutionScrollPane = new JScrollPane();
        this.solutionList = new JList();
        this.solutionPopupMenu = new JPopupMenu();
        this.deleteFromHereMenuItem = new JMenuItem();
        this.jSeparator1 = new JSeparator();
        this.toStepMenuItem = new JMenuItem();
        this.solveFromHereMenuItem = new JMenuItem();
        this.tabPopupMenu = new JPopupMenu();
        this.tabNewMenuItem = new JMenuItem();
        this.jSeparator2 = new JSeparator();
        this.tabNewNameMenuItem = new JMenuItem();
        this.tabDeleteMenuItem = new JMenuItem();
        this.jSeparator3 = new JSeparator();
        this.tabPrintMenuItem = new JMenuItem();
        this.titleLabel = new JLabel();
        this.southPanel = new JPanel();
        this.weiterButton = new JButton();
        this.alleEinfachenButton = new JButton();
        this.solutionTabbedPane = new JTabbedPane();
        this.solutionList.setSelectionMode(0);
        this.solutionList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                SolutionPanel.this.solutionListMouseClicked(evt);
            }
        });
        this.solutionScrollPane.setViewportView(this.solutionList);
        this.deleteFromHereMenuItem
                .setMnemonic(ResourceBundle.getBundle("intl/SolutionPanel").getString("SolutionPanel.deleteFromHereMenuItem.mnemonic").charAt(0));
        ResourceBundle bundle = ResourceBundle.getBundle("intl/SolutionPanel");
        this.deleteFromHereMenuItem.setText(bundle.getString("SolutionPanel.deleteFromHereMenuItem.text"));
        this.deleteFromHereMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                SolutionPanel.this.deleteFromHereMenuItemActionPerformed(evt);
            }
        });
        this.solutionPopupMenu.add(this.deleteFromHereMenuItem);
        this.solutionPopupMenu.add(this.jSeparator1);
        this.toStepMenuItem.setMnemonic(ResourceBundle.getBundle("intl/SolutionPanel").getString("SolutionPanel.toStepMenuItem.mnemonic").charAt(0));
        this.toStepMenuItem.setText(bundle.getString("SolutionPanel.toStepMenuItem.text"));
        this.toStepMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                SolutionPanel.this.toStepMenuItemActionPerformed(evt);
            }
        });
        this.solutionPopupMenu.add(this.toStepMenuItem);
        this.solveFromHereMenuItem
                .setMnemonic(ResourceBundle.getBundle("intl/SolutionPanel").getString("SolutionPanel.solveFromHereMenuItem.mnemonic").charAt(0));
        this.solveFromHereMenuItem.setText(bundle.getString("SolutionPanel.solveFromHereMenuItem.text"));
        this.solveFromHereMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                SolutionPanel.this.solveFromHereMenuItemActionPerformed(evt);
            }
        });
        this.solutionPopupMenu.add(this.solveFromHereMenuItem);
        this.tabNewMenuItem.setMnemonic(ResourceBundle.getBundle("intl/SolutionPanel").getString("SolutionPanel.tabNewMenuItem.mnemonic").charAt(0));
        this.tabNewMenuItem.setText(bundle.getString("SolutionPanel.tabNewMenuItem.text"));
        this.tabNewMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                SolutionPanel.this.tabNewMenuItemActionPerformed(evt);
            }
        });
        this.tabPopupMenu.add(this.tabNewMenuItem);
        this.tabPopupMenu.add(this.jSeparator2);
        this.tabNewNameMenuItem.setMnemonic(ResourceBundle.getBundle("intl/SolutionPanel").getString("SolutionPanel.tabNewNameMenuItem.mnemonic").charAt(0));
        this.tabNewNameMenuItem.setText(bundle.getString("SolutionPanel.tabNewNameMenuItem.text"));
        this.tabNewNameMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                SolutionPanel.this.tabNewNameMenuItemActionPerformed(evt);
            }
        });
        this.tabPopupMenu.add(this.tabNewNameMenuItem);
        this.tabDeleteMenuItem.setMnemonic(ResourceBundle.getBundle("intl/SolutionPanel").getString("SolutionPanel.tabDeleteMenuItem.mnemonic").charAt(0));
        this.tabDeleteMenuItem.setText(bundle.getString("SolutionPanel.tabDeleteMenuItem.text"));
        this.tabDeleteMenuItem.setToolTipText("");
        this.tabDeleteMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                SolutionPanel.this.tabDeleteMenuItemActionPerformed(evt);
            }
        });
        this.tabPopupMenu.add(this.tabDeleteMenuItem);
        this.tabPopupMenu.add(this.jSeparator3);
        this.tabPrintMenuItem.setMnemonic(ResourceBundle.getBundle("intl/SolutionPanel").getString("SolutionPanel.tabPrintMenuItem.mnemonic").charAt(0));
        this.tabPrintMenuItem.setText(bundle.getString("SolutionPanel.tabPrintMenuItem.text"));
        this.tabPrintMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                SolutionPanel.this.tabPrintMenuItemActionPerformed(evt);
            }
        });
        this.tabPopupMenu.add(this.tabPrintMenuItem);
        this.setLayout(new BorderLayout());
        this.titleLabel.setBackground(new Color(0, 51, 255));
        this.titleLabel.setForeground(new Color(255, 255, 255));
        this.titleLabel.setHorizontalAlignment(0);
        this.titleLabel.setText(bundle.getString("SolutionPanel.titleLabel.text"));
        this.titleLabel.setOpaque(true);
        this.add(this.titleLabel, "First");
        this.southPanel.setPreferredSize(new Dimension(100, 40));
        this.weiterButton.setMnemonic(ResourceBundle.getBundle("intl/SolutionPanel").getString("SolutionPanel.weiterButton.mnemonic").charAt(0));
        this.weiterButton.setText(bundle.getString("SolutionPanel.weiterButton.text"));
        this.weiterButton.setToolTipText(bundle.getString("SolutionPanel.weiterButton.toolTipText"));
        this.weiterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                SolutionPanel.this.weiterButtonActionPerformed(evt);
            }
        });
        this.alleEinfachenButton.setMnemonic(ResourceBundle.getBundle("intl/SolutionPanel").getString("SolutionPanel.alleEinfachenButton.mnemonic").charAt(0));
        this.alleEinfachenButton.setText(bundle.getString("SolutionPanel.alleEinfachenButton.text"));
        this.alleEinfachenButton.setToolTipText(bundle.getString("SolutionPanel.alleEinfachenButton.toolTipText"));
        this.alleEinfachenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                SolutionPanel.this.alleEinfachenButtonActionPerformed(evt);
            }
        });
        GroupLayout southPanelLayout = new GroupLayout(this.southPanel);
        this.southPanel.setLayout(southPanelLayout);
        southPanelLayout.setHorizontalGroup(
                southPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                southPanelLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(this.weiterButton)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.alleEinfachenButton)
                                        .addContainerGap(159, 32767)
                        )
        );
        southPanelLayout.linkSize(0, this.alleEinfachenButton, this.weiterButton);
        southPanelLayout.setVerticalGroup(
                southPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                southPanelLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(southPanelLayout.createParallelGroup(Alignment.BASELINE).addComponent(this.weiterButton).addComponent(this.alleEinfachenButton))
                                        .addContainerGap(-1, 32767)
                        )
        );
        this.add(this.southPanel, "Last");
        this.solutionTabbedPane.setTabPlacement(3);
        this.solutionTabbedPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                SolutionPanel.this.solutionTabbedPaneMouseClicked(evt);
            }
        });
        this.solutionTabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent evt) {
                SolutionPanel.this.solutionTabbedPaneStateChanged(evt);
            }
        });
        this.add(this.solutionTabbedPane, "Center");
    }

    private void toStepMenuItemActionPerformed(ActionEvent evt) {
        this.getActTab();
        this.setActSelectedIndex(this.rightMouseClickedIndex);
        this.resetSudokuToIndex(this.actSelectedIndex);
        this.actList.setSelectedIndex(this.actSelectedIndex);
        this.mainFrame.setSolutionStep(this.actSteps.get(this.actSelectedIndex), true);
        this.mainFrame.fixFocus();
    }

    private void solveFromHereMenuItemActionPerformed(ActionEvent evt) {
        this.getActTab();
        this.processDoubleClick(this.rightMouseClickedIndex);
        this.mainFrame.setSolutionStep(this.actSteps.get(this.rightMouseClickedIndex), true);
        this.mainFrame.fixFocus();
    }

    private void deleteFromHereMenuItemActionPerformed(ActionEvent evt) {
        this.getActTab();

        for (int i = this.actSteps.size() - 1; i >= this.rightMouseClickedIndex; i--) {
            this.actSteps.remove(i);
        }

        this.actSteps.add(new SolutionStep(SolutionType.INCOMPLETE));
        this.setStepsInList();
        this.setActSelectedIndex(this.rightMouseClickedIndex);
        this.resetSudokuToIndex(this.actSelectedIndex);
        this.actList.setSelectedIndex(this.actSelectedIndex);
        this.mainFrame.setSolutionStep(null, true);
        this.mainFrame.fixFocus();
    }

    private void solutionListMouseClicked(MouseEvent evt) {
        this.getActTab();
        if (evt.getButton() == 1) {
            int index = this.solutionList.getSelectedIndex();
            if (index != -1) {
                if (evt.getClickCount() == 2) {
                    this.processDoubleClick(index);
                }

                this.mainFrame.setSolutionStep(this.actSteps.get(index), true);
            }
        } else if (evt.getButton() == 3) {
            this.rightMouseClickedIndex = this.solutionList.locationToIndex(evt.getPoint());
            if (this.actSteps.get(this.rightMouseClickedIndex).getType() == SolutionType.INCOMPLETE) {
                this.deleteFromHereMenuItem.setEnabled(false);
                this.toStepMenuItem.setEnabled(false);
                this.solveFromHereMenuItem.setEnabled(true);
            } else {
                this.deleteFromHereMenuItem.setEnabled(true);
                this.toStepMenuItem.setEnabled(true);
                this.solveFromHereMenuItem.setEnabled(false);
            }

            this.solutionPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
        }

        this.mainFrame.fixFocus();
    }

    private void alleEinfachenButtonActionPerformed(ActionEvent evt) {
        if (this.solver != null) {
            this.getActTab();
            if (this.actSelectedIndex == -1) {
                this.weiterButtonActionPerformed(null);
            }

            if (this.actSelectedIndex != -1) {
                while (
                        this.actSelectedIndex < this.actSteps.size() - 1
                                && SolutionType.getStepConfig(this.actSteps.get(this.actSelectedIndex).getType()).getLevel() == DifficultyType.EASY.ordinal()
                ) {
                    this.weiterButtonActionPerformed(null);
                }

                if (this.actSelectedIndex < this.actSteps.size()
                        && SolutionType.getStepConfig(this.actSteps.get(this.actSelectedIndex).getType()).getLevel() == DifficultyType.EASY.ordinal()) {
                    this.weiterButtonActionPerformed(null);
                }

                this.mainFrame.fixFocus();
            }
        }
    }

    private void weiterButtonActionPerformed(ActionEvent evt) {
        this.getActTab();
        if (this.actSelectedIndex != -1) {
            if (this.actList.getSelectedIndex() != this.actSelectedIndex) {
                this.actList.setSelectedIndex(this.actSelectedIndex);
                this.actList.ensureIndexIsVisible(this.actSelectedIndex);
                this.mainFrame.setSolutionStep(this.actSteps.get(this.actSelectedIndex), true);
                return;
            }

            this.mainFrame.stepAusfuehren();
        }

        if (this.actSelectedIndex < this.actSteps.size() - 1) {
            this.setActSelectedIndex(this.actSelectedIndex + 1);
            this.actList.setSelectedIndex(this.actSelectedIndex);
            this.actList.ensureIndexIsVisible(this.actSelectedIndex);
            this.mainFrame.setSolutionStep(this.actSteps.get(this.actSelectedIndex), true);
        }

        this.mainFrame.fixFocus();
    }

    private void solutionTabbedPaneMouseClicked(MouseEvent evt) {
        if (evt.getButton() == 3) {
            for (int i = 0; i < this.solutionTabbedPane.getTabCount(); i++) {
                Rectangle rect = this.solutionTabbedPane.getBoundsAt(i);
                if (rect != null && rect.contains(evt.getPoint())) {
                    this.getActTab();
                    if (this.actSteps != null && !this.actSteps.isEmpty()) {
                        this.tabPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
                        return;
                    }

                    return;
                }
            }
        }
    }

    private void tabNewNameMenuItemActionPerformed(ActionEvent evt) {
        int index = this.solutionTabbedPane.getSelectedIndex();
        if (index != -1) {
            String init = this.titels.get(index);
            String newTitel = JOptionPane.showInputDialog(ResourceBundle.getBundle("intl/SolutionPanel").getString("SolutionPanel.new_name"), init);
            if (newTitel != null) {
                this.setNewTabTitle(index, newTitel);
            }

            this.mainFrame.fixFocus();
        }
    }

    private void tabNewMenuItemActionPerformed(ActionEvent evt) {
        this.getActTab();
        this.addTabPane(this.actSteps);
        this.mainFrame.fixFocus();
    }

    private void tabDeleteMenuItemActionPerformed(ActionEvent evt) {
        this.deleteTabPane();
        this.mainFrame.fixFocus();
    }

    private void solutionTabbedPaneStateChanged(ChangeEvent evt) {
        if (!this.inTabbedPaneRemoveAll) {
            this.getActTab();
            if (this.actList != null && this.actSteps != null) {
                int index = this.actList.getSelectedIndex();
                if (index != -1) {
                    this.resetSudokuToIndex(this.actSelectedIndex);
                    this.mainFrame.setSolutionStep(this.actSteps.get(this.actSelectedIndex), true);
                }
            }
        }
    }

    private void tabPrintMenuItemActionPerformed(ActionEvent evt) {
        if (this.actSteps != null) {
            String initialState = this.mainFrame.getSudokuPanel().getSudoku().getInitialState();
            if (initialState == null) {
                initialState = this.mainFrame.getSudokuPanel().getSudoku().getSudoku(ClipboardMode.CLUES_ONLY);
            }

            new PrintSolutionDialog(this.mainFrame, true, this.actSteps, initialState).setVisible(true);
            this.mainFrame.fixFocus();
        }
    }

    public void setTitleLabelColors(Color fore, Color back) {
        this.titleLabel.setBackground(back);
        this.titleLabel.setForeground(fore);
    }

    public void initialize(List<SolutionStep> newSteps) {
        this.inTabbedPaneRemoveAll = true;
        this.solutionTabbedPane.removeAll();
        this.inTabbedPaneRemoveAll = false;
        this.lists.clear();
        this.titels.clear();
        this.tabSteps.clear();
        this.selectedIndices.clear();
        this.addTabPane();
        this.solver = SudokuSolverFactory.getDefaultSolverInstance();
        if (newSteps != null) {
            this.setActSteps(newSteps);
            this.setStepsInList();
            this.setActSelectedIndex(-1);
            this.actList.ensureIndexIsVisible(0);
        }
    }

    public void initialize(List<String> titels, List<List<SolutionStep>> solutions) {
        if (titels.size() != solutions.size()) {
            Logger.getLogger(this.getClass().getName())
                    .log(
                            Level.SEVERE,
                            "SolutionPanel.initialize(): titels and solutions don''t have the same length ({0}/{1})",
                            new Object[]{titels.size(), solutions.size()}
                    );
        }

        int size = titels.size();
        if (solutions.size() < size) {
            size = solutions.size();
        }

        this.initialize(solutions.get(0));
        this.setNewTabTitle(0, titels.get(0));

        for (int i = 1; i < size; i++) {
            this.addTabPane(solutions.get(i), titels.get(i));
        }
    }

    public void addStep(SolutionStep step) {
        this.getActTab();
        if (this.actSteps.get(this.actSteps.size() - 1).getType() != SolutionType.INCOMPLETE) {
            JOptionPane.showMessageDialog(
                    this,
                    ResourceBundle.getBundle("intl/SolutionPanel").getString("SolutionPanel.cant_add_step"),
                    ResourceBundle.getBundle("intl/SolutionPanel").getString("SolutionPanel.error"),
                    0
            );
        } else {
            this.actSteps.add(this.actSteps.size() - 1, step);
            this.setStepsInList();
        }
    }

    private void resetSudokuToIndex(int index) {
        this.getActTab();
        Sudoku2 sudoku = this.mainFrame.getSudokuPanel().getSudoku();
        sudoku.resetSudoku();

        for (int i = 0; i < index; i++) {
            this.solver.doStep(sudoku, this.actSteps.get(i));
        }

        this.setActSelectedIndex(index);
        this.mainFrame.getSudokuPanel().clearUndoRedo();
        this.mainFrame.getSudokuPanel().clearColoring();
        this.mainFrame.getSudokuPanel().checkProgress();
    }

    private void processDoubleClick(int index) {
        this.getActTab();
        this.resetSudokuToIndex(index);
        if (this.actSteps.get(index).getType() == SolutionType.INCOMPLETE) {
            this.actSteps.remove(this.actSteps.size() - 1);
            Sudoku2 actSudoku = this.mainFrame.getSudokuPanel().getSudoku().clone();
            this.solver.setSudoku(actSudoku, this.actSteps);
            this.solver.solve(true);
            this.setActSteps(this.solver.getSteps());
            this.setStepsInList();
            this.setActSelectedIndex(this.actSelectedIndex);
            this.mainFrame.getSudokuPanel().checkProgress();
        }
    }

    private void setStepsInList() {
        this.getActTab();
        String[] data = new String[this.actSteps.size()];
        this.actStepBackgroundColors = new Color[this.actSteps.size()];
        this.actStepForegroundColors = new Color[this.actSteps.size()];
        int tmpIndex = this.solutionTabbedPane.getSelectedIndex();
        this.stepBackgroundColors.remove(tmpIndex);
        this.stepForegroundColors.remove(tmpIndex);
        this.stepBackgroundColors.add(tmpIndex, this.actStepBackgroundColors);
        this.stepForegroundColors.add(tmpIndex, this.actStepForegroundColors);

        for (int i = 0; i < this.actSteps.size(); i++) {
            data[i] = this.actSteps.get(i).toString(2);
            this.actStepBackgroundColors[i] = Options.getInstance().getDifficultyLevels()[SolutionType.getStepConfig(this.actSteps.get(i).getType()).getLevel()]
                    .getBackgroundColor();
            this.actStepForegroundColors[i] = Options.getInstance().getDifficultyLevels()[SolutionType.getStepConfig(this.actSteps.get(i).getType()).getLevel()]
                    .getForegroundColor();
        }

        this.actList.setListData(data);
    }

    private void stepListMouseClicked(MouseEvent evt) {
        this.getActTab();
        if (evt.getButton() == 1) {
            int index = this.actList.getSelectedIndex();
            if (index != -1) {
                if (evt.getClickCount() == 2) {
                    this.processDoubleClick(index);
                }

                this.mainFrame.setSolutionStep(this.actSteps.get(index), true);
            }
        } else if (evt.getButton() == 3 && this.actSteps != null) {
            this.rightMouseClickedIndex = this.actList.locationToIndex(evt.getPoint());
            if (this.rightMouseClickedIndex != -1) {
                if (this.actSteps.get(this.rightMouseClickedIndex).getType() == SolutionType.INCOMPLETE) {
                    this.deleteFromHereMenuItem.setEnabled(false);
                    this.toStepMenuItem.setEnabled(false);
                    this.solveFromHereMenuItem.setEnabled(true);
                } else {
                    this.deleteFromHereMenuItem.setEnabled(true);
                    this.toStepMenuItem.setEnabled(true);
                    this.solveFromHereMenuItem.setEnabled(false);
                }

                this.solutionPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
            }
        }

        this.mainFrame.fixFocus();
    }

    private void addTabPane() {
        this.addTabPane(null);
    }

    private void addTabPane(List<SolutionStep> steps) {
        String titel = ResourceBundle.getBundle("intl/SolutionPanel").getString("SolutionPanel.solution") + " " + (this.solutionTabbedPane.getTabCount() + 1);
        this.addTabPane(steps, titel);
    }

    private void addTabPane(List<SolutionStep> steps, String titel) {
        JList tmpList = new JList();
        tmpList.setSelectionMode(0);
        tmpList.setCellRenderer(new SolutionPanel.SolutionListRenderer());
        tmpList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                SolutionPanel.this.stepListMouseClicked(evt);
            }
        });
        JScrollPane newPane = new JScrollPane();
        newPane.setViewportView(tmpList);
        this.solutionTabbedPane.add(titel, newPane);
        this.solutionTabbedPane.setSelectedIndex(this.solutionTabbedPane.getTabCount() - 1);
        this.titels.add(titel);
        this.lists.add(tmpList);
        this.selectedIndices.add(-1);
        this.stepBackgroundColors.add(null);
        this.stepForegroundColors.add(null);
        if (steps != null) {
            List<SolutionStep> newSteps = new ArrayList<>();

            for (SolutionStep step : steps) {
                newSteps.add(step);
            }

            this.tabSteps.add(newSteps);
            this.setStepsInList();
        } else {
            this.tabSteps.add(null);
        }

        this.getActTab();
    }

    private void deleteTabPane() {
        if (this.solutionTabbedPane.getTabCount() <= 1) {
            JOptionPane.showMessageDialog(
                    this,
                    ResourceBundle.getBundle("intl/SolutionPanel").getString("SolutionPanel.cant_delete_step"),
                    ResourceBundle.getBundle("intl/SolutionPanel").getString("SolutionPanel.error"),
                    0
            );
        } else {
            int index = this.solutionTabbedPane.getSelectedIndex();
            if (index != -1) {
                this.solutionTabbedPane.remove(index);
                this.titels.remove(index);
                this.tabSteps.remove(index);
                this.lists.remove(index);
                this.stepBackgroundColors.remove(index);
                this.stepForegroundColors.remove(index);
                this.getActTab();
            }
        }
    }

    private void getActTab() {
        int index = this.solutionTabbedPane.getSelectedIndex();
        if (index == -1) {
            index = 0;
        }

        if (this.lists.size() > index) {
            this.actList = this.lists.get(index);
            this.actSteps = this.tabSteps.get(index);
            this.actSelectedIndex = this.selectedIndices.get(index);
            this.actStepBackgroundColors = this.stepBackgroundColors.get(index);
            this.actStepForegroundColors = this.stepForegroundColors.get(index);
        } else {
            this.actList = null;
            this.actSteps = null;
            this.actSelectedIndex = -1;
            this.actStepBackgroundColors = null;
            this.actStepForegroundColors = null;
        }
    }

    private void setActSelectedIndex(int selectedIndex) {
        int index = this.solutionTabbedPane.getSelectedIndex();
        if (index == -1) {
            index = 0;
        }

        this.selectedIndices.remove(index);
        this.selectedIndices.add(index, selectedIndex);
        this.actSelectedIndex = selectedIndex;
    }

    private void setActSteps(List<SolutionStep> steps) {
        int index = this.solutionTabbedPane.getSelectedIndex();
        if (index == -1) {
            index = 0;
        }

        this.tabSteps.remove(index);
        List<SolutionStep> tmpSteps = new ArrayList<>(steps.size());

        for (SolutionStep step : steps) {
            tmpSteps.add(step);
        }

        this.tabSteps.add(index, tmpSteps);
        this.actSteps = steps;
    }

    private void setNewTabTitle(int index, String newTitel) {
        this.titels.remove(index);
        this.titels.add(index, newTitel);
        this.solutionTabbedPane.setTitleAt(index, newTitel);
    }

    public List<String> getTitels() {
        return this.titels;
    }

    public List<List<SolutionStep>> getTabSteps() {
        return this.tabSteps;
    }

    public void getState(GuiState state, boolean copy) {
        if (copy) {
            state.setTitels((List<String>) ((ArrayList) this.titels).clone());
            state.setTabSteps((List<List<SolutionStep>>) ((ArrayList) this.tabSteps).clone());
        } else {
            state.setTitels(this.titels);
            state.setTabSteps(this.tabSteps);
        }
    }

    public void setState(GuiState state) {
        this.initialize(state.getTitels(), state.getTabSteps());
    }

    class SolutionListRenderer extends JLabel implements ListCellRenderer {
        private static final long serialVersionUID = 1L;

        SolutionListRenderer() {
            this.setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            this.setBackground(Color.WHITE);
            if (SolutionPanel.this.actStepBackgroundColors != null) {
                this.setBackground(SolutionPanel.this.actStepBackgroundColors[index]);
            }

            this.setForeground(Color.BLACK);
            if (SolutionPanel.this.actStepForegroundColors != null) {
                this.setForeground(SolutionPanel.this.actStepForegroundColors[index]);
            }

            if (isSelected) {
                this.setBackground(SolutionPanel.this.actList.getSelectionBackground());
                this.setForeground(SolutionPanel.this.actList.getSelectionForeground());
            }

            String text = value != null ? value.toString() : "";
            this.setText("  " + text);
            return this;
        }
    }
}
