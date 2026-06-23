package solver;

import generator.SudokuGeneratorFactory;
import sudoku.SolutionStep;
import sudoku.SolutionType;
import sudoku.SudokuSet;

public class BruteForceSolver extends AbstractSolver {
    public BruteForceSolver(SudokuStepFinder finder) {
        super(finder);
    }

    @Override
    protected SolutionStep getStep(SolutionType type) {
        SolutionStep result = null;
        this.sudoku = this.finder.getSudoku();
        switch (type) {
            case BRUTE_FORCE:
                result = this.getBruteForce();
            default:
                return result;
        }
    }

    @Override
    protected boolean doStep(SolutionStep step) {
        boolean handled = true;
        this.sudoku = this.finder.getSudoku();
        switch (step.getType()) {
            case BRUTE_FORCE:
                int value = step.getValues().get(0);

                for (int index : step.getIndices()) {
                    this.sudoku.setCell(index, value);
                }
                break;
            default:
                handled = false;
        }

        return handled;
    }

    private SolutionStep getBruteForce() {
        if (!this.sudoku.isSolutionSet()) {
            boolean isValid = SudokuGeneratorFactory.getDefaultGeneratorInstance().validSolution(this.sudoku);
            if (!isValid) {
                return null;
            }
        }

        SudokuSet unsolved = new SudokuSet();

        for (int i = 0; i < 81; i++) {
            if (this.sudoku.getValue(i) == 0) {
                unsolved.add(i);
            }
        }

        int index = unsolved.size() / 2;
        index = unsolved.get(index);
        SolutionStep step = new SolutionStep(SolutionType.BRUTE_FORCE);
        step.addIndex(index);
        step.addValue(this.sudoku.getSolution(index));
        return step;
    }
}
