package generator;

import solver.SudokuSolver;
import solver.SudokuSolverFactory;
import sudoku.*;

import java.awt.*;

public class BackgroundGenerator {
    private static final int MAX_TRIES = 20000;
    private int anz = 0;
    private GenerateSudokuProgressDialog progressDialog = null;

    public String generate(DifficultyLevel level, GameMode mode) {
        Sudoku2 sudoku = this.generate(level, mode, null);
        return sudoku != null ? sudoku.getSudoku(ClipboardMode.CLUES_ONLY) : null;
    }

    public Sudoku2 generate(DifficultyLevel level, GameMode mode, GenerateSudokuProgressDialog dlg) {
        long actMillis = System.currentTimeMillis();
        this.progressDialog = dlg;
        Sudoku2 sudoku = null;
        SudokuGenerator creator = null;
        SudokuSolver solver = null;
        this.setAnz(0);
        if (dlg == null) {
            solver = SudokuSolverFactory.getInstance();
            creator = SudokuGeneratorFactory.getInstance();
        } else {
            solver = SudokuSolverFactory.getDefaultSolverInstance();
            creator = SudokuGeneratorFactory.getDefaultGeneratorInstance();
        }

        while (dlg == null || !Thread.currentThread().isInterrupted()) {
            sudoku = creator.generateSudoku(true);
            if (sudoku == null) {
                return null;
            }

            Sudoku2 solvedSudoku = sudoku.clone();
            boolean ok = solver.solve(level, solvedSudoku, true, null, false, Options.getInstance().solverSteps, mode);
            boolean containsTrainingStep = true;
            if (mode != GameMode.PLAYING) {
                containsTrainingStep = false;

                for (SolutionStep step : solver.getSteps()) {
                    if (step.getType().getStepConfig().isEnabledTraining()) {
                        containsTrainingStep = true;
                        break;
                    }
                }
            }

            if (!ok || !containsTrainingStep || solvedSudoku.getLevel().getOrdinal() != level.getOrdinal() && mode != GameMode.LEARNING) {
                this.setAnz(this.getAnz() + 1);
                if (dlg != null) {
                    if (System.currentTimeMillis() - actMillis > 500L) {
                        actMillis = System.currentTimeMillis();
                        EventQueue.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                BackgroundGenerator.this.progressDialog.updateProgressLabel();
                            }
                        });
                    }
                    continue;
                }

                if (this.getAnz() <= 20000) {
                    continue;
                }

                sudoku = null;
                break;
            }

            sudoku.setLevel(solvedSudoku.getLevel());
            sudoku.setScore(solvedSudoku.getScore());
            break;
        }

        if (dlg == null) {
            SudokuGeneratorFactory.giveBack(creator);
            SudokuSolverFactory.giveBack(solver);
        }

        return sudoku;
    }

    public synchronized int getAnz() {
        return this.anz;
    }

    public synchronized void setAnz(int anz) {
        this.anz = anz;
    }
}
