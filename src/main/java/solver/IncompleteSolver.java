package solver;

import sudoku.SolutionStep;
import sudoku.SolutionType;

public class IncompleteSolver extends AbstractSolver {
    public IncompleteSolver(SudokuStepFinder finder) {
        super(finder);
    }

    @Override
    protected SolutionStep getStep(SolutionType type) {
        return type == SolutionType.INCOMPLETE ? null : null;
    }

    @Override
    protected boolean doStep(SolutionStep step) {
        boolean handled = false;
        switch (step.getType()) {
            case INCOMPLETE:
                handled = true;
                break;
            default:
                handled = false;
        }

        return handled;
    }
}
