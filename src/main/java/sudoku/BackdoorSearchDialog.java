package sudoku;

import solver.SudokuSolver;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BackdoorSearchDialog extends JDialog implements Runnable {
    private static final int MAX_FOUND = 100;
    private static final long serialVersionUID = 1L;
    private DefaultListModel singlesListModel;
    private DefaultListModel progressListModel;
    private SudokuPanel sudokuPanel;
    private Sudoku2 sudoku;
    private Sudoku2 orgSudoku;
    private SudokuSolver solver;
    private List<Candidate> candidates = new ArrayList<>();
    private BlockingQueue<String> singlesQueue = new ArrayBlockingQueue<>(20);
    private BlockingQueue<String> progressQueue = new ArrayBlockingQueue<>(20);
    private Thread thread;
    private volatile int anzFound;
    private boolean finished = false;
    private String progressLabelString = "";
    private int progressBarMax = 100;
    private int progressBarAct = 0;
    private JComboBox candComboBox;
    private JLabel candLabel;
    private JCheckBox candidatesCheckBox;
    private JCheckBox cellsCheckBox;
    private JButton closeButton;
    private JPanel configPanel;
    private JScrollPane jScrollPane1;
    private JScrollPane jScrollPane2;
    private JPanel progressPanel;
    private JLabel progressResultLabel;
    private JList progressResultList;
    private JPanel progressResultPanel;
    private JPanel resultPanel;
    private JLabel searchLabel;
    private JProgressBar searchProgressBar;
    private Runnable progressBarRunnable = new Runnable() {
        @Override
        public void run() {
            BackdoorSearchDialog.this.updateProgressBar();
        }
    };
    private JLabel singlesResultLabel;
    private JList singlesResultList;
    private JPanel singlesResultPanel;
    private JButton startButton;
    private JButton stopButton;
    private Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            BackdoorSearchDialog.this.update();
        }
    };

    public BackdoorSearchDialog(Frame parent, boolean modal, SudokuPanel sudokuPanel) {
        super(parent, modal);
        this.initComponents();
        this.sudokuPanel = sudokuPanel;
        this.getRootPane().setDefaultButton(this.startButton);
        this.singlesListModel = new DefaultListModel();
        this.singlesResultList.setModel(this.singlesListModel);
        this.progressListModel = new DefaultListModel();
        this.progressResultList.setModel(this.progressListModel);
        if (Options.getInstance().getBdsSearchCandidatesAnz() < 0) {
            Options.getInstance().setBdsSearchCandidatesAnz(0);
        }

        this.cellsCheckBox.setSelected(Options.getInstance().isBdsSearchForCells());
        this.candidatesCheckBox.setSelected(Options.getInstance().isBdsSearchForCandidates());
        this.candComboBox.setSelectedIndex(Options.getInstance().getBdsSearchCandidatesAnz());
        this.candComboBox.setEnabled(this.candidatesCheckBox.isSelected());
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                BackdoorSearchDialog dialog = new BackdoorSearchDialog(new JFrame(), true, null);
                dialog.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    private void initComponents() {
        this.configPanel = new JPanel();
        this.cellsCheckBox = new JCheckBox();
        this.candidatesCheckBox = new JCheckBox();
        this.candLabel = new JLabel();
        this.candComboBox = new JComboBox();
        this.progressPanel = new JPanel();
        this.searchLabel = new JLabel();
        this.searchProgressBar = new JProgressBar();
        this.resultPanel = new JPanel();
        this.singlesResultPanel = new JPanel();
        this.singlesResultLabel = new JLabel();
        this.jScrollPane1 = new JScrollPane();
        this.singlesResultList = new JList();
        this.progressResultPanel = new JPanel();
        this.progressResultLabel = new JLabel();
        this.jScrollPane2 = new JScrollPane();
        this.progressResultList = new JList();
        this.startButton = new JButton();
        this.closeButton = new JButton();
        this.stopButton = new JButton();
        this.setDefaultCloseOperation(2);
        ResourceBundle bundle = ResourceBundle.getBundle("intl/BackdoorSearchDialog");
        this.setTitle(bundle.getString("BackdoorSearchDialog.title"));
        this.configPanel.setBorder(BorderFactory.createTitledBorder(bundle.getString("BackDoorSearchDialog.configPanel.title")));
        this.cellsCheckBox.setMnemonic(ResourceBundle.getBundle("intl/BackdoorSearchDialog").getString("BackdoorSearchPanel.cellsCheckBox.mnemonic").charAt(0));
        this.cellsCheckBox.setSelected(true);
        this.cellsCheckBox.setText(bundle.getString("BackdoorSearchPanel.cellsCheckBox.text"));
        this.cellsCheckBox.setToolTipText(bundle.getString("BackdoorSearchDialog.cellsCheckBox.toolTipText"));
        this.cellsCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                BackdoorSearchDialog.this.cellsCheckBoxActionPerformed(evt);
            }
        });
        this.candidatesCheckBox
                .setMnemonic(ResourceBundle.getBundle("intl/BackdoorSearchDialog").getString("BackdoorSearchPanel.candidatesCheckBox.mnemonic").charAt(0));
        this.candidatesCheckBox.setText(bundle.getString("BackdoorSearchPanel.candidatesCheckBox.text"));
        this.candidatesCheckBox.setToolTipText(bundle.getString("BackdoorSearchDialog.candidatesCheckBox.toolTipText"));
        this.candidatesCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                BackdoorSearchDialog.this.candidatesCheckBoxActionPerformed(evt);
            }
        });
        this.candLabel.setDisplayedMnemonic(ResourceBundle.getBundle("intl/BackdoorSearchDialog").getString("BackdoorSearchPanel.candLabel.mnemonic").charAt(0));
        this.candLabel.setLabelFor(this.candComboBox);
        this.candLabel.setText(bundle.getString("BackdoorSearchPanel.candLabel.text"));
        this.candComboBox.setModel(new DefaultComboBoxModel<>(new String[]{"1", "2", "3"}));
        this.candComboBox.setSelectedIndex(1);
        this.candComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                BackdoorSearchDialog.this.candComboBoxActionPerformed(evt);
            }
        });
        GroupLayout configPanelLayout = new GroupLayout(this.configPanel);
        this.configPanel.setLayout(configPanelLayout);
        configPanelLayout.setHorizontalGroup(
                configPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                configPanelLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(this.cellsCheckBox)
                                        .addGap(18, 18, 18)
                                        .addComponent(this.candidatesCheckBox)
                                        .addGap(18, 18, 18)
                                        .addComponent(this.candLabel)
                                        .addPreferredGap(ComponentPlacement.UNRELATED)
                                        .addComponent(this.candComboBox, -2, 58, -2)
                                        .addContainerGap(113, 32767)
                        )
        );
        configPanelLayout.setVerticalGroup(
                configPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                configPanelLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                configPanelLayout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(this.cellsCheckBox)
                                                        .addComponent(this.candidatesCheckBox)
                                                        .addComponent(this.candLabel)
                                                        .addComponent(this.candComboBox, -2, -1, -2)
                                        )
                                        .addContainerGap(-1, 32767)
                        )
        );
        this.progressPanel.setBorder(BorderFactory.createTitledBorder(bundle.getString("BackdoorSearchDialog.progressPanel.title")));
        this.searchLabel.setText(bundle.getString("BackdoorSearchDialog.searchLabel.text"));
        GroupLayout progressPanelLayout = new GroupLayout(this.progressPanel);
        this.progressPanel.setLayout(progressPanelLayout);
        progressPanelLayout.setHorizontalGroup(
                progressPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                progressPanelLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                progressPanelLayout.createParallelGroup(Alignment.LEADING)
                                                        .addComponent(this.searchLabel)
                                                        .addComponent(this.searchProgressBar, -1, 512, 32767)
                                        )
                                        .addContainerGap()
                        )
        );
        progressPanelLayout.setVerticalGroup(
                progressPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                progressPanelLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(this.searchLabel)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.searchProgressBar, -2, -1, -2)
                                        .addContainerGap(-1, 32767)
                        )
        );
        this.resultPanel.setBorder(BorderFactory.createTitledBorder(bundle.getString("BackdoorSearchDialog.resultPanel.title")));
        this.singlesResultLabel.setText(bundle.getString("BackdoorSearchDialog.singlesResultLabel.text"));
        this.singlesResultList.setModel(new AbstractListModel() {
            String[] strings = new String[]{"Item 1", "Item 2", "Item 3", "Item 4", "Item 5"};

            @Override
            public int getSize() {
                return this.strings.length;
            }

            @Override
            public Object getElementAt(int i) {
                return this.strings[i];
            }
        });
        this.jScrollPane1.setViewportView(this.singlesResultList);
        GroupLayout singlesResultPanelLayout = new GroupLayout(this.singlesResultPanel);
        this.singlesResultPanel.setLayout(singlesResultPanelLayout);
        singlesResultPanelLayout.setHorizontalGroup(
                singlesResultPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(singlesResultPanelLayout.createSequentialGroup().addComponent(this.singlesResultLabel).addContainerGap(135, 32767))
                        .addComponent(this.jScrollPane1, -1, 223, 32767)
        );
        singlesResultPanelLayout.setVerticalGroup(
                singlesResultPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                singlesResultPanelLayout.createSequentialGroup()
                                        .addComponent(this.singlesResultLabel)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.jScrollPane1, -1, 216, 32767)
                        )
        );
        this.progressResultLabel.setText(bundle.getString("BackdoorSearchDialog.progressResultLabel.text"));
        this.progressResultList.setModel(new AbstractListModel() {
            String[] strings = new String[]{"Item 1", "Item 2", "Item 3", "Item 4", "Item 5"};

            @Override
            public int getSize() {
                return this.strings.length;
            }

            @Override
            public Object getElementAt(int i) {
                return this.strings[i];
            }
        });
        this.jScrollPane2.setViewportView(this.progressResultList);
        GroupLayout progressResultPanelLayout = new GroupLayout(this.progressResultPanel);
        this.progressResultPanel.setLayout(progressResultPanelLayout);
        progressResultPanelLayout.setHorizontalGroup(
                progressResultPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                progressResultPanelLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                progressResultPanelLayout.createParallelGroup(Alignment.LEADING)
                                                        .addComponent(this.jScrollPane2, -1, 273, 32767)
                                                        .addGroup(progressResultPanelLayout.createSequentialGroup().addComponent(this.progressResultLabel).addContainerGap(132, 32767))
                                        )
                        )
        );
        progressResultPanelLayout.setVerticalGroup(
                progressResultPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                progressResultPanelLayout.createSequentialGroup()
                                        .addComponent(this.progressResultLabel)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.jScrollPane2, -1, 216, 32767)
                        )
        );
        GroupLayout resultPanelLayout = new GroupLayout(this.resultPanel);
        this.resultPanel.setLayout(resultPanelLayout);
        resultPanelLayout.setHorizontalGroup(
                resultPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                resultPanelLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(this.singlesResultPanel, -1, -1, 32767)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.progressResultPanel, -1, -1, 32767)
                                        .addContainerGap()
                        )
        );
        resultPanelLayout.setVerticalGroup(
                resultPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                Alignment.TRAILING,
                                resultPanelLayout.createSequentialGroup()
                                        .addGroup(
                                                resultPanelLayout.createParallelGroup(Alignment.TRAILING)
                                                        .addComponent(this.progressResultPanel, Alignment.LEADING, -1, -1, 32767)
                                                        .addComponent(this.singlesResultPanel, -1, -1, 32767)
                                        )
                                        .addContainerGap()
                        )
        );
        this.startButton.setMnemonic(ResourceBundle.getBundle("intl/BackdoorSearchDialog").getString("BackdoorSearchDialog.startButton.mnemonic").charAt(0));
        this.startButton.setText(bundle.getString("BackdoorSearchDialog.startButton.text"));
        this.startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                BackdoorSearchDialog.this.startButtonActionPerformed(evt);
            }
        });
        this.closeButton.setMnemonic(ResourceBundle.getBundle("intl/BackdoorSearchDialog").getString("BackdoorSearchDialog.stopButton.mnemonic").charAt(0));
        this.closeButton.setText(bundle.getString("BackdoorSearchDialog.closeButton.text"));
        this.closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                BackdoorSearchDialog.this.closeButtonActionPerformed(evt);
            }
        });
        this.stopButton.setMnemonic(ResourceBundle.getBundle("intl/BackdoorSearchDialog").getString("BackdoorSearchDialog.closeButton.mnemonic").charAt(0));
        this.stopButton.setText(bundle.getString("BackdoorSearchDialog.stopButton.text"));
        this.stopButton.setEnabled(false);
        this.stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                BackdoorSearchDialog.this.stopButtonActionPerformed(evt);
            }
        });
        GroupLayout layout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                Alignment.TRAILING,
                                layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                layout.createParallelGroup(Alignment.TRAILING)
                                                        .addComponent(this.resultPanel, Alignment.LEADING, -1, -1, 32767)
                                                        .addComponent(this.progressPanel, Alignment.LEADING, -1, -1, 32767)
                                                        .addComponent(this.configPanel, Alignment.LEADING, -1, -1, 32767)
                                                        .addGroup(
                                                                layout.createSequentialGroup()
                                                                        .addComponent(this.startButton)
                                                                        .addGap(6, 6, 6)
                                                                        .addComponent(this.stopButton)
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addComponent(this.closeButton)
                                                        )
                                        )
                                        .addContainerGap()
                        )
        );
        layout.linkSize(0, this.closeButton, this.startButton, this.stopButton);
        layout.setVerticalGroup(
                layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(this.configPanel, -2, -1, -2)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.progressPanel, -2, -1, -2)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.resultPanel, -2, -1, -2)
                                        .addPreferredGap(ComponentPlacement.RELATED, -1, 32767)
                                        .addGroup(
                                                layout.createParallelGroup(Alignment.BASELINE).addComponent(this.startButton).addComponent(this.closeButton).addComponent(this.stopButton)
                                        )
                                        .addContainerGap()
                        )
        );
        this.pack();
    }

    private void cellsCheckBoxActionPerformed(ActionEvent evt) {
        Options.getInstance().setBdsSearchForCells(this.cellsCheckBox.isSelected());
    }

    private void candidatesCheckBoxActionPerformed(ActionEvent evt) {
        Options.getInstance().setBdsSearchForCandidates(this.candidatesCheckBox.isSelected());
        this.candComboBox.setEnabled(this.candidatesCheckBox.isSelected());
    }

    private void candComboBoxActionPerformed(ActionEvent evt) {
        Options.getInstance().setBdsSearchCandidatesAnz(this.candComboBox.getSelectedIndex());
    }

    private void closeButtonActionPerformed(ActionEvent evt) {
        this.setVisible(false);
    }

    private void startButtonActionPerformed(ActionEvent evt) {
        this.singlesListModel.clear();
        this.singlesResultList.repaint();
        this.progressListModel.clear();
        this.progressResultList.repaint();
        this.thread = new Thread(this);
        this.thread.start();
        this.setFinished(false);
        this.adjustGUI();
    }

    private void stopButtonActionPerformed(ActionEvent evt) {
        if (this.thread != null && this.thread.isAlive()) {
            this.thread.interrupt();

            try {
                this.thread.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(BackdoorSearchDialog.class.getName()).log(Level.SEVERE, null, ex);
            }

            this.update();
        }
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            int anz = this.sudokuPanel.getSudoku().getSolvedCellsAnz();
            if (anz <= 16) {
                JOptionPane.showMessageDialog(
                        this,
                        ResourceBundle.getBundle("intl/BackdoorSearchDialog").getString("BackdoorSearchDialog.error.message") + " (" + anz + ")",
                        ResourceBundle.getBundle("intl/BackdoorSearchDialog").getString("BackdoorSearchDialog.error.title"),
                        0
                );
                return;
            }
        }

        super.setVisible(visible);
    }

    @Override
    public void run() {
        this.orgSudoku = this.sudokuPanel.getSudoku().clone();
        this.sudoku = this.orgSudoku.clone();
        this.solver = this.sudokuPanel.getSolver();
        EventQueue.invokeLater(this.updateRunnable);

        try {
            if (Options.getInstance().isBdsSearchForCells()) {
                int anz = this.orgSudoku.getUnsolvedCellsAnz();
                this.setAnzFound(0);
                if (!this.checkSingles(1, anz, null)) {
                    int maxAnz = SudokuUtil.combinations(anz, 2);
                    if (!this.checkSingles(2, maxAnz, null)) {
                        maxAnz = SudokuUtil.combinations(anz, 3);
                        this.checkSingles(3, maxAnz, null);
                    }
                }

                this.setAnzFound(0);
                if (!this.checkSingles(1, anz, Options.getInstance().solverStepsProgress)) {
                    int maxAnz = SudokuUtil.combinations(anz, 2);
                    if (!this.checkSingles(2, maxAnz, Options.getInstance().solverStepsProgress)) {
                        maxAnz = SudokuUtil.combinations(anz, 3);
                        this.checkSingles(3, maxAnz, Options.getInstance().solverStepsProgress);
                    }
                }
            }

            if (Options.getInstance().isBdsSearchForCandidates()) {
                int anz = 0;
                this.candidates.clear();

                for (int i = 0; i < 81; i++) {
                    if (this.sudoku.getValue(i) == 0) {
                        int[] cands = this.sudoku.getAllCandidates(i);

                        for (int j = 0; j < cands.length; j++) {
                            if (cands[j] != this.sudoku.getSolution(i)) {
                                this.candidates.add(new Candidate(i, cands[j]));
                                anz++;
                            }
                        }
                    }
                }

                this.setAnzFound(0);
                int maxDepth = Options.getInstance().getBdsSearchCandidatesAnz() + 1;
                if (!this.checkCandidates(1, anz, this.candidates, null) && maxDepth > 1) {
                    int maxAnz = SudokuUtil.combinations(anz, 2);
                    if (!this.checkCandidates(2, maxAnz, this.candidates, null) && maxDepth > 2) {
                        maxAnz = SudokuUtil.combinations(anz, 3);
                        this.checkCandidates(3, maxAnz, this.candidates, null);
                    }
                }

                this.setAnzFound(0);
                if (!this.checkCandidates(1, anz, this.candidates, Options.getInstance().solverStepsProgress) && maxDepth > 1) {
                    int maxAnz = SudokuUtil.combinations(anz, 2);
                    if (!this.checkCandidates(2, maxAnz, this.candidates, Options.getInstance().solverStepsProgress) && maxDepth > 2) {
                        maxAnz = SudokuUtil.combinations(anz, 3);
                        this.checkCandidates(3, maxAnz, this.candidates, Options.getInstance().solverStepsProgress);
                    }
                }
            }
        } catch (InterruptedException ex) {
            return;
        }

        this.setFinished(true);
        this.triggerUpdateProgressbar(ResourceBundle.getBundle("intl/BackdoorSearchDialog").getString("BackdoorSearchDialog.searchLabel.text"), 100, 100);
        EventQueue.invokeLater(this.updateRunnable);
    }

    private void adjustGUI() {
        if (this.thread != null && this.thread.isAlive() && !this.isFinished()) {
            this.startButton.setEnabled(false);
            this.stopButton.setEnabled(true);
            this.cellsCheckBox.setEnabled(false);
            this.candidatesCheckBox.setEnabled(false);
            this.candComboBox.setEnabled(false);
            this.closeButton.setEnabled(false);
        } else {
            this.startButton.setEnabled(true);
            this.stopButton.setEnabled(false);
            this.cellsCheckBox.setEnabled(true);
            this.candidatesCheckBox.setEnabled(true);
            this.candComboBox.setEnabled(this.candidatesCheckBox.isSelected());
            this.closeButton.setEnabled(true);
        }
    }

    private void update() {
        String str = null;

        boolean doRepaint;
        for (doRepaint = false; (str = this.singlesQueue.poll()) != null; doRepaint = true) {
            this.singlesListModel.addElement(str);
        }

        if (doRepaint) {
            this.singlesResultList.repaint();
        }

        for (doRepaint = false; (str = this.progressQueue.poll()) != null; doRepaint = true) {
            this.progressListModel.addElement(str);
        }

        if (doRepaint) {
            this.progressResultList.repaint();
        }

        this.adjustGUI();
    }

    private void updateProgressBar() {
        this.searchLabel.setText(this.getProgressLabelString());
        this.searchProgressBar.setMaximum(this.getProgressBarMax());
        this.searchProgressBar.setValue(this.getProgressBarAct());
    }

    private void triggerUpdateProgressbar(String text, int max, int act) {
        this.setProgressLabelString(text);
        this.setProgressBarMax(max);
        this.setProgressBarAct(act);
        EventQueue.invokeLater(this.progressBarRunnable);
    }

    private boolean checkCandidates(int depth, int max, List<Candidate> candidates, StepConfig[] stepConfigs) throws InterruptedException {
        boolean found = false;
        int end = candidates.size();
        int counter = 0;
        String startStr = ResourceBundle.getBundle("intl/BackdoorSearchDialog").getString("BackdoorSearchDialog.search_candidates") + " (";
        if (stepConfigs == null) {
            startStr = startStr + ResourceBundle.getBundle("intl/BackdoorSearchDialog").getString("BackdoorSearchDialog.singles");
        } else {
            startStr = startStr + ResourceBundle.getBundle("intl/BackdoorSearchDialog").getString("BackdoorSearchDialog.progress");
        }

        startStr = startStr + " - ";
        String label = startStr + ResourceBundle.getBundle("intl/BackdoorSearchDialog").getString("BackdoorSearchDialog.search_candidates1");
        if (depth == 2) {
            label = startStr + ResourceBundle.getBundle("intl/BackdoorSearchDialog").getString("BackdoorSearchDialog.search_candidates2");
        } else if (depth == 3) {
            label = startStr + ResourceBundle.getBundle("intl/BackdoorSearchDialog").getString("BackdoorSearchDialog.search_candidates3");
        }

        label = label + ")";

        for (int i = 0; i < end; i++) {
            if (depth == 1) {
                if (this.checkSingleOrCandidate(-1, -1, -1, candidates.get(i), null, null, stepConfigs)) {
                    found = true;
                    this.incAnzFound();
                    if (this.getAnzFound() > 100) {
                        return true;
                    }
                }

                this.triggerUpdateProgressbar(label, max, ++counter);
            } else {
                for (int j = i + 1; j < end; j++) {
                    if (depth == 2) {
                        if (this.checkSingleOrCandidate(-1, -1, -1, candidates.get(i), candidates.get(j), null, stepConfigs)) {
                            found = true;
                            this.incAnzFound();
                            if (this.getAnzFound() > 100) {
                                return true;
                            }
                        }

                        this.triggerUpdateProgressbar(label, max, ++counter);
                    } else {
                        for (int k = j + 1; k < end; k++) {
                            if (this.checkSingleOrCandidate(-1, -1, -1, candidates.get(i), candidates.get(j), candidates.get(k), stepConfigs)) {
                                found = true;
                                this.incAnzFound();
                                if (this.getAnzFound() > 100) {
                                    return true;
                                }
                            }

                            this.triggerUpdateProgressbar(label, max, ++counter);
                        }
                    }
                }
            }
        }

        return found;
    }

    private boolean checkSingles(int depth, int max, StepConfig[] stepConfigs) throws InterruptedException {
        boolean found = false;
        int end = this.orgSudoku.getCells().length;
        int counter = 0;
        String startStr = ResourceBundle.getBundle("intl/BackdoorSearchDialog").getString("BackdoorSearchDialog.search_cells") + " (";
        if (stepConfigs == null) {
            startStr = startStr + ResourceBundle.getBundle("intl/BackdoorSearchDialog").getString("BackdoorSearchDialog.singles");
        } else {
            startStr = startStr + ResourceBundle.getBundle("intl/BackdoorSearchDialog").getString("BackdoorSearchDialog.progress");
        }

        startStr = startStr + " - ";
        String label = startStr + ResourceBundle.getBundle("intl/BackdoorSearchDialog").getString("BackdoorSearchDialog.search_cells1");
        if (depth == 2) {
            label = startStr + ResourceBundle.getBundle("intl/BackdoorSearchDialog").getString("BackdoorSearchDialog.search_cells2");
        } else if (depth == 3) {
            label = startStr + ResourceBundle.getBundle("intl/BackdoorSearchDialog").getString("BackdoorSearchDialog.search_cells3");
        }

        label = label + ")";

        for (int i = 0; i < end; i++) {
            if (this.orgSudoku.getValue(i) == 0) {
                if (depth == 1) {
                    if (this.checkSingleOrCandidate(i, -1, -1, null, null, null, stepConfigs)) {
                        found = true;
                        this.incAnzFound();
                        if (this.getAnzFound() > 100) {
                            return true;
                        }
                    }

                    this.triggerUpdateProgressbar(label, max, ++counter);
                } else {
                    for (int j = i + 1; j < end; j++) {
                        if (this.orgSudoku.getValue(j) == 0) {
                            if (depth == 2) {
                                if (this.checkSingleOrCandidate(i, j, -1, null, null, null, stepConfigs)) {
                                    found = true;
                                    this.incAnzFound();
                                    if (this.getAnzFound() > 100) {
                                        return true;
                                    }
                                }

                                this.triggerUpdateProgressbar(label, max, ++counter);
                            } else {
                                for (int k = j + 1; k < end; k++) {
                                    if (this.orgSudoku.getValue(k) == 0) {
                                        if (this.checkSingleOrCandidate(i, j, k, null, null, null, stepConfigs)) {
                                            found = true;
                                            this.incAnzFound();
                                            if (this.getAnzFound() > 100) {
                                                return true;
                                            }
                                        }

                                        this.triggerUpdateProgressbar(label, max, ++counter);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return found;
    }

    private boolean checkSingleOrCandidate(int index1, int index2, int index3, Candidate cand1, Candidate cand2, Candidate cand3, StepConfig[] stepConfigs) throws InterruptedException {
        if (this.thread.isInterrupted()) {
            throw new InterruptedException();
        }

        this.sudoku.set(this.orgSudoku);
        if (cand1 == null) {
            this.sudoku.setCell(index1, this.sudoku.getSolution(index1));
        } else {
            this.sudoku.setCandidate(cand1.getIndex(), cand1.getValue(), false);
            if (cand2 != null) {
                this.sudoku.setCandidate(cand2.getIndex(), cand2.getValue(), false);
            }

            if (cand3 != null) {
                this.sudoku.setCandidate(cand3.getIndex(), cand3.getValue(), false);
            }
        }

        boolean isSolved = false;
        if (stepConfigs == null) {
            isSolved = this.solver.solveSinglesOnly(this.sudoku);
        } else {
            isSolved = this.solver.solveWithSteps(this.sudoku, stepConfigs);
        }

        if (isSolved) {
            String cellString = "";
            if (cand1 == null) {
                cellString = SolutionStep.getCellPrint(index1, false);
                if (index2 >= 0) {
                    cellString = cellString + ", " + SolutionStep.getCellPrint(index2, false);
                }

                if (index3 >= 0) {
                    cellString = cellString + ", " + SolutionStep.getCellPrint(index3, false);
                }
            } else {
                cellString = SolutionStep.getCellPrint(cand1.getIndex(), false) + "<>" + cand1.getValue();
                if (cand2 != null) {
                    cellString = cellString + ", " + SolutionStep.getCellPrint(cand2.getIndex(), false) + "<>" + cand2.getValue();
                }

                if (cand3 != null) {
                    cellString = cellString + ", " + SolutionStep.getCellPrint(cand2.getIndex(), false) + "<>" + cand3.getValue();
                }
            }

            if (stepConfigs == null) {
                this.singlesQueue.offer(cellString);
            } else {
                this.progressQueue.offer(cellString);
            }

            EventQueue.invokeLater(this.updateRunnable);
            return true;
        } else {
            return false;
        }
    }

    private synchronized boolean isFinished() {
        return this.finished;
    }

    private synchronized void setFinished(boolean finished) {
        this.finished = finished;
    }

    private synchronized String getProgressLabelString() {
        return this.progressLabelString;
    }

    private synchronized void setProgressLabelString(String progressLabelString) {
        this.progressLabelString = progressLabelString;
    }

    private synchronized int getProgressBarMax() {
        return this.progressBarMax;
    }

    private synchronized void setProgressBarMax(int progressBarMax) {
        this.progressBarMax = progressBarMax;
    }

    private synchronized int getProgressBarAct() {
        return this.progressBarAct;
    }

    private synchronized void setProgressBarAct(int progressBarAct) {
        this.progressBarAct = progressBarAct;
    }

    private synchronized int getAnzFound() {
        return this.anzFound;
    }

    private synchronized void setAnzFound(int anzFound) {
        this.anzFound = anzFound;
    }

    private synchronized void incAnzFound() {
        this.anzFound++;
    }
}
