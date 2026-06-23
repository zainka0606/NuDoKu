package solver;

import sudoku.SolutionStep;
import sudoku.SolutionType;
import sudoku.Sudoku2;

public abstract class AbstractSolver {
    protected SudokuStepFinder finder;
    protected Sudoku2 sudoku;

    public AbstractSolver(SudokuStepFinder finder) {
        this.finder = finder;
    }

    protected abstract SolutionStep getStep(SolutionType var1);

    protected abstract boolean doStep(SolutionStep var1);

    protected void cleanUp() {
    }
}
