package sudoku;

import generator.BackgroundGeneratorThread;
import solver.SudokuSolver;
import solver.SudokuSolverFactory;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.UIManager.LookAndFeelInfo;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExtendedPrintProgressDialog extends JDialog implements Runnable, Printable {
    private static final double GAP_FACTOR = 0.02702702702702703;
    private static final double[] PPP = new double[]{1.0, 2.0, 4.0, 2.0, 4.0};
    private static final long serialVersionUID = 1L;
    private Thread thread;
    private JTextField[] numberTextFields;
    private JComboBox[] levelComboBoxes;
    private JComboBox[] modeComboBoxes;
    private JCheckBox[] candCheckBoxes;
    private int layout;
    private boolean printRating;
    private boolean allBlack;
    private boolean printBooklet;
    private boolean manualDuplex;
    private PrinterJob job = null;
    private Sudoku2[] sudokus;
    private boolean[] candidates;
    private int percentage;
    private volatile int numberOfPages;
    private Font smallFont;
    private SudokuPanel panel;
    private int imagePrintSize;
    private int horizontalGap;
    private int verticalGap;
    private int printWidth;
    private int printHeight;
    private int borderWidth;
    private int borderHeight;
    private int footerHeight;
    private boolean initialized;
    private boolean firstHalf = true;
    private boolean jobAborted = false;
    private JButton cancelButton;
    private JPanel jPanel1;
    private JProgressBar printProgressBar;

    public ExtendedPrintProgressDialog(
            Frame parent,
            boolean modal,
            JTextField[] numberTextFields,
            JComboBox[] levelComboBoxes,
            JComboBox[] modeComboBoxes,
            JCheckBox[] candCheckBoxes,
            int layout,
            boolean printRating,
            boolean allBlack,
            boolean printBooklet,
            boolean manualDuplex
    ) {
        super(parent, modal);
        this.initComponents();
        this.getRootPane().setDefaultButton(this.cancelButton);
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(27, 0, false);
        Action escapeAction = new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                ExtendedPrintProgressDialog.this.setVisible(false);
            }
        };
        this.getRootPane().getInputMap(2).put(escapeKeyStroke, "ESCAPE");
        this.getRootPane().getActionMap().put("ESCAPE", escapeAction);
        this.numberTextFields = numberTextFields;
        this.levelComboBoxes = levelComboBoxes;
        this.modeComboBoxes = modeComboBoxes;
        this.candCheckBoxes = candCheckBoxes;
        this.layout = layout;
        this.printRating = printRating;
        this.allBlack = allBlack;
        this.printBooklet = printBooklet;
        this.manualDuplex = manualDuplex;
        this.thread = new Thread(this);
    }

    public static void main(String[] args) {
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ExtendedPrintProgressDialog.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(ExtendedPrintProgressDialog.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ExtendedPrintProgressDialog.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(ExtendedPrintProgressDialog.class.getName()).log(Level.SEVERE, null, ex);
        }

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                ExtendedPrintProgressDialog dialog = new ExtendedPrintProgressDialog(new JFrame(), true, null, null, null, null, 1, false, false, false, false);
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
        this.printProgressBar = new JProgressBar();
        this.jPanel1 = new JPanel();
        this.cancelButton = new JButton();
        this.setDefaultCloseOperation(2);
        ResourceBundle bundle = ResourceBundle.getBundle("intl/ExtendedPrintProgressDialog");
        this.setTitle(bundle.getString("ExtendedPrintProgressDialog.title"));
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                ExtendedPrintProgressDialog.this.formWindowClosing(evt);
            }

            @Override
            public void windowOpened(WindowEvent evt) {
                ExtendedPrintProgressDialog.this.formWindowOpened(evt);
            }
        });
        this.printProgressBar.setStringPainted(true);
        this.cancelButton
                .setMnemonic(ResourceBundle.getBundle("intl/ExtendedPrintProgressDialog").getString("ExtendedPrintProgressDialog.cancelButton.mnemonic").charAt(0));
        this.cancelButton.setText(bundle.getString("ExtendedPrintProgressDialog.cancelButton.text"));
        this.cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ExtendedPrintProgressDialog.this.cancelButtonActionPerformed(evt);
            }
        });
        this.jPanel1.add(this.cancelButton);
        GroupLayout layout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addGroup(
                                                layout.createParallelGroup(Alignment.LEADING)
                                                        .addGroup(layout.createSequentialGroup().addGap(10, 10, 10).addComponent(this.printProgressBar, -1, 210, 32767))
                                                        .addGroup(layout.createSequentialGroup().addContainerGap().addComponent(this.jPanel1, -1, 210, 32767))
                                        )
                                        .addContainerGap()
                        )
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addGap(26, 26, 26)
                                        .addComponent(this.printProgressBar, -2, -1, -2)
                                        .addGap(18, 18, 18)
                                        .addComponent(this.jPanel1, -2, 33, -2)
                                        .addContainerGap(-1, 32767)
                        )
        );
        this.pack();
    }

    private void cancelButtonActionPerformed(ActionEvent evt) {
        this.thread.interrupt();
        this.job.cancel();

        try {
            this.thread.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Interrupted while waiting for generation thread", ex);
        }

        this.setVisible(false);
    }

    private void formWindowClosing(WindowEvent evt) {
        this.cancelButtonActionPerformed(null);
    }

    private void formWindowOpened(WindowEvent evt) {
        this.thread.start();
    }

    @Override
    public void run() {
        try {
            this.initialized = false;
            if (this.job != null) {
                if (this.createSudokus()) {
                    if (this.layout < 3) {
                        this.numberOfPages = (int) Math.ceil(this.sudokus.length / PPP[this.layout]);
                    } else {
                        int adjusted = this.sudokus.length;
                        double factor = PPP[this.layout];
                        if (this.printBooklet) {
                            adjusted = (int) (Math.ceil(this.sudokus.length / (factor * 2.0)) * factor * 2.0);
                        }

                        this.numberOfPages = (int) Math.ceil(adjusted / factor);
                    }

                    try {
                        this.firstHalf = true;
                        this.job.print();
                        if (!this.job.isCancelled() && this.printBooklet) {
                            this.setJobAborted(false);
                            if (this.manualDuplex) {
                                EventQueue.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        ExtendedPrintProgressDialog.this.showMessageDialog();
                                    }
                                });
                                synchronized (this) {
                                    this.wait();
                                }

                                if (!this.isJobAborted()) {
                                    this.firstHalf = false;
                                    this.job.print();
                                }
                            }
                        }
                    } catch (PrinterException ex) {
                        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error while printing", ex);
                    }
                }
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error: no PrinterJob set");
            }
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error while printing", ex);
        }

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                ExtendedPrintProgressDialog.this.setVisible(false);
            }
        });
    }

    private boolean createSudokus() {
        int anzPuzzles = 0;

        for (int i = 0; i < this.numberTextFields.length; i++) {
            anzPuzzles += this.getNumberOfPuzzles(i);
        }

        if (anzPuzzles == 0) {
            return false;
        }

        this.sudokus = new Sudoku2[anzPuzzles];
        this.candidates = new boolean[anzPuzzles];
        int index = 0;

        for (int i = 0; i < this.numberTextFields.length; i++) {
            DifficultyLevel actDiffLevel = Options.getInstance().getDifficultyLevel(this.levelComboBoxes[i].getSelectedIndex() + 1);
            GameMode actGameMode = null;
            boolean withCandidates = this.candCheckBoxes[i].isSelected();
            switch (this.modeComboBoxes[i].getSelectedIndex()) {
                case 0:
                    actGameMode = GameMode.PLAYING;
                    break;
                case 1:
                    actGameMode = GameMode.LEARNING;
                    break;
                case 2:
                    actGameMode = GameMode.PRACTISING;
            }

            for (int j = 0; j < this.getNumberOfPuzzles(i); j++) {
                this.sudokus[index] = this.getSudoku(actDiffLevel, actGameMode);
                if (this.sudokus[index] == null || this.thread.isInterrupted()) {
                    return false;
                }

                this.candidates[index] = withCandidates;
                Options.getInstance().addSudokuToHistory(this.sudokus[index]);
                index++;
                this.setPercentage((int) Math.round(index * 100.0 / anzPuzzles));
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        ExtendedPrintProgressDialog.this.setProgress();
                    }
                });
            }
        }

        return true;
    }

    private Sudoku2 getSudoku(DifficultyLevel level, GameMode mode) {
        if (mode == GameMode.LEARNING) {
            level = Options.getInstance().getDifficultyLevel(DifficultyType.EXTREME.ordinal());
        }

        String preGenSudoku = BackgroundGeneratorThread.getInstance().getSudoku(level, mode);
        Sudoku2 tmpSudoku = null;
        SudokuSolver solver = SudokuSolverFactory.getDefaultSolverInstance();
        if (preGenSudoku == null) {
            GenerateSudokuProgressDialog dlg = new GenerateSudokuProgressDialog(null, true, level, mode);
            dlg.setVisible(true);
            tmpSudoku = dlg.getSudoku();
        } else {
            tmpSudoku = new Sudoku2();
            tmpSudoku.setSudoku(preGenSudoku, true);
            Sudoku2 solvedSudoku = tmpSudoku.clone();
            solver.solve(level, solvedSudoku, true, null, false, Options.getInstance().solverSteps, Options.getInstance().getGameMode());
            tmpSudoku.setLevel(solvedSudoku.getLevel());
            tmpSudoku.setScore(solvedSudoku.getScore());
        }

        if (tmpSudoku == null) {
            return null;
        }

        if (mode == GameMode.LEARNING) {
            for (SolutionStep step : solver.getSteps()) {
                if (step.getType().getStepConfig().isEnabledTraining()) {
                    break;
                }

                solver.doStep(tmpSudoku, step);
            }
        }

        return tmpSudoku;
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex >= this.numberOfPages) {
            return 1;
        }

        if (this.printBooklet && this.manualDuplex && pageIndex >= this.numberOfPages / 2) {
            return 1;
        }

        this.setPercentage(-(pageIndex + 1));
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                ExtendedPrintProgressDialog.this.setProgress();
            }
        });
        Graphics2D printG2 = (Graphics2D) graphics;
        double scale = SudokuUtil.adjustGraphicsForPrinting(printG2);
        printG2.translate((int) (pageFormat.getImageableX() * scale), (int) (pageFormat.getImageableY() * scale));
        this.printWidth = (int) (pageFormat.getImageableWidth() * scale);
        this.printHeight = (int) (pageFormat.getImageableHeight() * scale);
        if (!this.initialized) {
            Font tmpFont = Options.getInstance().getSmallFont();
            this.smallFont = new Font(tmpFont.getName(), tmpFont.getStyle(), (int) (tmpFont.getSize() * scale));
            printG2.setFont(this.smallFont);
            this.footerHeight = 0;
            if (this.printRating) {
                FontMetrics metrics = printG2.getFontMetrics();
                this.footerHeight = metrics.getHeight() * 2;
            }

            this.horizontalGap = 0;
            this.verticalGap = 0;
            this.borderWidth = this.printWidth;
            this.borderHeight = this.printHeight;
            if (!this.printBooklet) {
                if (this.layout == 1 || this.layout == 2 || this.layout == 4) {
                    this.verticalGap = (int) (this.borderHeight * 0.02702702702702703);
                    this.borderHeight = (this.borderHeight - this.verticalGap) / 2;
                }

                if (this.layout == 2 || this.layout == 3 || this.layout == 4) {
                    this.horizontalGap = (int) (this.borderWidth * 0.02702702702702703);
                    this.borderWidth = (this.borderWidth - this.horizontalGap) / 2;
                }
            } else {
                if (this.layout == 3 || this.layout == 4) {
                    this.horizontalGap = (int) ((pageFormat.getWidth() - pageFormat.getImageableWidth()) * scale);
                    this.borderWidth = (this.borderWidth - this.horizontalGap) / 2;
                }

                if (this.layout == 4) {
                    this.verticalGap = (int) ((pageFormat.getHeight() - pageFormat.getImageableHeight()) * scale);
                    this.borderHeight = (this.borderHeight - this.verticalGap) / 2;
                }
            }

            this.imagePrintSize = this.borderWidth < this.borderHeight - this.footerHeight ? this.borderWidth : this.borderHeight - this.footerHeight;
            this.panel = new SudokuPanel(null);
            this.initialized = true;
        }

        int leftStartIndex = 0;
        int rightStartIndex = 0;
        if (!this.printBooklet) {
            leftStartIndex = (int) Math.round(pageIndex * PPP[this.layout]);
            rightStartIndex = leftStartIndex + 1;
            if (PPP[this.layout] == 4.0) {
                rightStartIndex = leftStartIndex + 2;
            }
        } else {
            int dummy = this.layout - 2;
            int firstRightIndex = this.numberOfPages * dummy;
            int actPage = pageIndex;
            if (this.printBooklet && this.manualDuplex) {
                if (this.firstHalf) {
                    actPage = this.numberOfPages - pageIndex * 2 - 1;
                } else {
                    actPage = this.numberOfPages - (pageIndex + 1) * 2;
                }
            }

            leftStartIndex = firstRightIndex - (actPage + 1) * dummy;
            rightStartIndex = firstRightIndex + actPage * dummy;
        }

        switch (this.layout) {
            case 0:
                this.printSudoku(printG2, leftStartIndex, 0, scale);
                break;
            case 1:
                this.printSudoku(printG2, leftStartIndex, 0, scale);
                this.printSudoku(printG2, leftStartIndex + 1, 2, scale);
                break;
            case 2:
                this.printSudoku(printG2, leftStartIndex, 0, scale);
                this.printSudoku(printG2, leftStartIndex + 1, 1, scale);
                this.printSudoku(printG2, leftStartIndex + 2, 2, scale);
                this.printSudoku(printG2, leftStartIndex + 3, 3, scale);
                break;
            case 3:
                if (!this.printBooklet) {
                    this.printSudoku(printG2, leftStartIndex, 0, scale);
                    this.printSudoku(printG2, rightStartIndex, 1, scale);
                } else if ((!this.manualDuplex || this.firstHalf) && (this.manualDuplex || pageIndex % 2 != 1)) {
                    this.printSudoku(printG2, leftStartIndex, 0, scale);
                    this.printSudoku(printG2, rightStartIndex, 1, scale);
                } else {
                    this.printSudoku(printG2, leftStartIndex, 1, scale);
                    this.printSudoku(printG2, rightStartIndex, 0, scale);
                }
                break;
            case 4:
                if (!this.printBooklet) {
                    this.printSudoku(printG2, leftStartIndex, 0, scale);
                    this.printSudoku(printG2, leftStartIndex + 1, 2, scale);
                    this.printSudoku(printG2, rightStartIndex, 1, scale);
                    this.printSudoku(printG2, rightStartIndex + 1, 3, scale);
                } else if ((!this.manualDuplex || this.firstHalf) && (this.manualDuplex || pageIndex % 2 != 1)) {
                    this.printSudoku(printG2, leftStartIndex, 0, scale);
                    this.printSudoku(printG2, leftStartIndex + 1, 2, scale);
                    this.printSudoku(printG2, rightStartIndex, 1, scale);
                    this.printSudoku(printG2, rightStartIndex + 1, 3, scale);
                } else {
                    this.printSudoku(printG2, leftStartIndex, 1, scale);
                    this.printSudoku(printG2, leftStartIndex + 1, 3, scale);
                    this.printSudoku(printG2, rightStartIndex, 0, scale);
                    this.printSudoku(printG2, rightStartIndex + 1, 2, scale);
                }
        }

        return 0;
    }

    private void printSudoku(Graphics2D g2, int index, int position, double scale) {
        if (index < this.sudokus.length && index >= 0) {
            int startX = 0;
            int startY = 0;
            switch (position) {
                case 0:
                    startX = (this.borderWidth - this.imagePrintSize) / 2;
                    startY = (this.borderHeight - this.imagePrintSize - this.footerHeight) / 2;
                    break;
                case 1:
                    startX = this.borderWidth + this.horizontalGap + (this.borderWidth - this.imagePrintSize) / 2;
                    startY = (this.borderHeight - this.imagePrintSize - this.footerHeight) / 2;
                    break;
                case 2:
                    startX = (this.borderWidth - this.imagePrintSize) / 2;
                    startY = this.borderHeight + this.verticalGap + (this.borderHeight - this.imagePrintSize - this.footerHeight) / 2;
                    break;
                case 3:
                    startX = this.borderWidth + this.horizontalGap + (this.borderWidth - this.imagePrintSize) / 2;
                    startY = this.borderHeight + this.verticalGap + (this.borderHeight - this.imagePrintSize - this.footerHeight) / 2;
            }

            Sudoku2 sudoku = this.sudokus[index];
            this.panel.setSudoku(sudoku, true);
            this.panel.setShowCandidates(this.candidates[index]);
            this.panel.printSudoku(g2, startX, startY, this.imagePrintSize, this.allBlack, scale);
            if (this.printRating && sudoku != null && sudoku.getLevel() != null) {
                String title = sudoku.getLevel().getName() + " (" + sudoku.getScore() + ")";
                g2.setFont(this.smallFont);
                FontMetrics metrics = g2.getFontMetrics();
                int textWidth = metrics.stringWidth(title);
                int textHeight = metrics.getHeight();
                g2.setColor(Color.BLACK);
                g2.drawString(title, startX + this.imagePrintSize / 2 - textWidth / 2, (int) (startY + this.imagePrintSize + textHeight * 1.5));
            }
        }
    }

    private int getNumberOfPuzzles(int index) {
        int ret = 0;

        try {
            ret = Integer.parseInt(this.numberTextFields[index].getText());
        } catch (NumberFormatException ex) {
            ret = 0;
        }

        return ret;
    }

    private void setProgress() {
        int value = this.getPercentage();
        if (value >= 0) {
            this.setTitle(ResourceBundle.getBundle("intl/ExtendedPrintProgressDialog").getString("ExtendedPrintProgressDialog.title"));
            this.printProgressBar.setValue(value);
            this.printProgressBar.setString(value + " %");
        } else {
            value = -value;
            this.setTitle(ResourceBundle.getBundle("intl/ExtendedPrintProgressDialog").getString("ExtendedPrintProgressDialog.title2"));
            this.printProgressBar.setValue((int) (value * 100.0 / this.numberOfPages));
            this.printProgressBar.setString(value + " / " + this.numberOfPages);
        }
    }

    private void showMessageDialog() {
        int ret = JOptionPane.showConfirmDialog(
                null,
                ResourceBundle.getBundle("intl/ExtendedPrintProgressDialog").getString("ExtendedPrintProgressDialog.flipPaper"),
                ResourceBundle.getBundle("intl/ExtendedPrintProgressDialog").getString("ExtendedPrintProgressDialog.flipPaperTitle"),
                2
        );
        if (ret == 0) {
            this.setJobAborted(false);
        } else {
            this.setJobAborted(true);
        }

        synchronized (this) {
            this.notify();
        }
    }

    private synchronized int getPercentage() {
        return this.percentage;
    }

    private synchronized void setPercentage(int p) {
        this.percentage = p;
    }

    public void setJob(PrinterJob job) {
        this.job = job;
    }

    private synchronized boolean isJobAborted() {
        return this.jobAborted;
    }

    private synchronized void setJobAborted(boolean ja) {
        this.jobAborted = ja;
    }
}
