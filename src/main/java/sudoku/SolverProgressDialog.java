package sudoku;

import solver.SudokuSolver;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;

public class SolverProgressDialog extends JDialog implements Runnable {
    private static final long serialVersionUID = 1L;
    private SudokuSolver solver = null;
    private Thread thread = null;
    private boolean solved = false;
    private JProgressBar candsProgressBar;
    private JLabel jLabel1;
    private JLabel jLabel3;
    private JLabel unsolvedCandsLabel;
    private JLabel unsolvedCellsLabel;

    public SolverProgressDialog(Frame parent, boolean modal, SudokuSolver solver) {
        super(parent, modal);
        this.solver = solver;
        this.initComponents();
        this.candsProgressBar.setValue(0);
        this.thread = new Thread(this);
        this.thread.start();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                SolverProgressDialog dialog = new SolverProgressDialog(new JFrame(), true, null);
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
        this.jLabel1 = new JLabel();
        this.unsolvedCellsLabel = new JLabel();
        this.jLabel3 = new JLabel();
        this.unsolvedCandsLabel = new JLabel();
        this.candsProgressBar = new JProgressBar();
        this.setDefaultCloseOperation(0);
        ResourceBundle bundle = ResourceBundle.getBundle("intl/SolverProgressDialog");
        this.setTitle(bundle.getString("SolverProgressDialog.title"));
        this.setModal(true);
        this.jLabel1.setText(bundle.getString("SolverProgressDialog.jLabel1.text"));
        this.unsolvedCellsLabel.setText("81");
        this.jLabel3.setText(bundle.getString("SolverProgressDialog.jLabel3.text"));
        this.unsolvedCandsLabel.setText("729");
        this.candsProgressBar.setMaximum(729);
        GroupLayout layout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                layout.createParallelGroup(Alignment.LEADING)
                                                        .addComponent(this.candsProgressBar, -1, 273, 32767)
                                                        .addGroup(
                                                                layout.createSequentialGroup()
                                                                        .addComponent(this.jLabel1)
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addComponent(this.unsolvedCellsLabel)
                                                                        .addGap(18, 18, 18)
                                                                        .addComponent(this.jLabel3)
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addComponent(this.unsolvedCandsLabel)
                                                        )
                                        )
                                        .addContainerGap()
                        )
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                layout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(this.jLabel1)
                                                        .addComponent(this.unsolvedCellsLabel)
                                                        .addComponent(this.jLabel3)
                                                        .addComponent(this.unsolvedCandsLabel)
                                        )
                                        .addGap(18, 18, 18)
                                        .addComponent(this.candsProgressBar, -2, -1, -2)
                                        .addContainerGap(-1, 32767)
                        )
        );
        this.pack();
    }

    @Override
    public void run() {
        this.solved = this.solver.solve(Options.getInstance().getDifficultyLevels()[DifficultyType.EXTREME.ordinal()], null, false, this);
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                SolverProgressDialog.this.setVisible(false);
            }
        });
    }

    public void initializeProgressState(int unsolvedCandidates) {
        int dummy = this.candsProgressBar.getMaximum() - unsolvedCandidates;
        this.candsProgressBar.setMinimum(dummy);
        this.candsProgressBar.setValue(dummy);
    }

    public void setProgressState(final int unsolvedCells, final int unsolvedCandidates) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                SolverProgressDialog.this.unsolvedCellsLabel.setText(Integer.toString(unsolvedCells));
                SolverProgressDialog.this.unsolvedCandsLabel.setText(Integer.toString(unsolvedCandidates));
                SolverProgressDialog.this.candsProgressBar.setValue(SolverProgressDialog.this.candsProgressBar.getMaximum() - unsolvedCandidates);
            }
        });
    }

    public Thread getThread() {
        return this.thread;
    }

    public boolean isSolved() {
        return this.solved;
    }
}
