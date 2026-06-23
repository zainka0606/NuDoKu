package sudoku;

import solver.SudokuSolver;
import solver.SudokuSolverFactory;

import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProgressChecker implements Runnable {
    private Sudoku2 sudoku = new Sudoku2();
    private Sudoku2 passedInSudoku = new Sudoku2();
    private boolean passedIn = false;
    private Thread thread;
    private boolean threadStarted = false;
    private MainFrame mainFrame = null;
    private SudokuSolver solver = null;

    public ProgressChecker(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.thread = new Thread(this);
    }

    public void startCheck(Sudoku2 actSudoku) {
        if (this.thread != null) {
            if (!this.threadStarted) {
                this.thread.start();
                this.threadStarted = true;
            }

            synchronized (this.thread) {
                this.passedInSudoku.set(actSudoku);
                this.passedIn = true;
                this.thread.notify();
            }
        }
    }

    @Override
    public void run() {
        while (!this.thread.isInterrupted()) {
            try {
                synchronized (this.thread) {
                    if (!this.passedIn) {
                        this.thread.wait();
                    }

                    if (!this.passedIn) {
                        continue;
                    }

                    this.sudoku.set(this.passedInSudoku);
                    this.sudoku.checkSudoku();
                    this.passedIn = false;
                }

                if (this.sudoku.getStatus() != SudokuStatus.VALID) {
                    Logger.getLogger(this.getClass().getName())
                            .log(Level.SEVERE, "Progress check scheduled for invalid sudoku ({0})", this.sudoku.getSudoku(ClipboardMode.LIBRARY));
                } else {
                    if (this.solver == null) {
                        this.solver = SudokuSolverFactory.getInstance();
                    }

                    this.solver.setSudoku(this.sudoku);
                    if (this.solver.solve()) {
                        this.mainFrame.setCurrentLevel(this.sudoku.getLevel());
                        this.mainFrame.setCurrentScore(this.sudoku.getScore());
                        EventQueue.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                ProgressChecker.this.mainFrame.setProgressLabel();
                            }
                        });
                    }

                    SudokuSolverFactory.giveBack(this.solver);
                    this.solver = null;
                }
            } catch (InterruptedException ex) {
                this.thread.interrupt();
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error checking progress", ex);
            }
        }
    }
}
