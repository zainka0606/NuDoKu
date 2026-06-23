package solver;

import sudoku.SolutionStep;
import sudoku.SolutionType;

public class GiveUpSolver extends AbstractSolver {
    public GiveUpSolver(SudokuStepFinder finder) {
        super(finder);
    }

    @Override
    protected SolutionStep getStep(SolutionType type) {
        return type == SolutionType.GIVE_UP ? new SolutionStep(SolutionType.GIVE_UP) : null;
    }

    @Override
    protected boolean doStep(SolutionStep step) {
        boolean handled = false;
        switch (step.getType()) {
            case GIVE_UP:
                handled = true;
                break;
            default:
                handled = false;
        }

        return handled;
    }
}
