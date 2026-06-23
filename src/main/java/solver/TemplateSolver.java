package solver;

import sudoku.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TemplateSolver extends AbstractSolver {
    private List<SolutionStep> steps;
    private SolutionStep globalStep = new SolutionStep(SolutionType.HIDDEN_SINGLE);

    public TemplateSolver(SudokuStepFinder finder) {
        super(finder);
    }

    public static void main(String[] args) {
        Sudoku2 sudoku = new Sudoku2();
        sudoku.setSudoku(":0000:x:7.2.34.8.........2.8..51.74.......51..63.27..29.......14.76..2.8.........2.51.8.7:::");
        TemplateSolver ts = new TemplateSolver(null);
        long millis = System.currentTimeMillis();

        for (int i = 0; i < 1; i++) {
            List<SolutionStep> steps = ts.getAllTemplates();
        }

        millis = System.currentTimeMillis() - millis;
        System.out.println("Zeit: " + millis / 100L + "ms");
    }

    @Override
    protected SolutionStep getStep(SolutionType type) {
        SolutionStep result = null;
        this.sudoku = this.finder.getSudoku();
        switch (type) {
            case TEMPLATE_SET:
                this.getTemplateSet(true);
                if (this.steps.size() > 0) {
                    result = this.steps.get(0);
                }
                break;
            case TEMPLATE_DEL:
                this.getTemplateDel(true);
                if (this.steps.size() > 0) {
                    result = this.steps.get(0);
                }
        }

        return result;
    }

    @Override
    protected boolean doStep(SolutionStep step) {
        boolean handled = true;
        this.sudoku = this.finder.getSudoku();
        switch (step.getType()) {
            case TEMPLATE_SET:
                int value = step.getValues().get(0);

                for (int index : step.getIndices()) {
                    this.sudoku.setCell(index, value);
                }
                break;
            case TEMPLATE_DEL:
                for (Candidate cand : step.getCandidatesToDelete()) {
                    this.sudoku.delCandidate(cand.getIndex(), cand.getValue());
                }
                break;
            default:
                handled = false;
        }

        return handled;
    }

    protected List<SolutionStep> getAllTemplates() {
        this.sudoku = this.finder.getSudoku();
        List<SolutionStep> oldSteps = this.steps;
        this.steps = new ArrayList<>();
        long millis1 = System.currentTimeMillis();
        this.getTemplateSet(false);
        this.getTemplateDel(false);
        millis1 = System.currentTimeMillis() - millis1;
        Logger.getLogger(this.getClass().getName()).log(Level.FINE, "getAllTemplates() gesamt: {0}ms", millis1);
        List<SolutionStep> result = this.steps;
        this.steps = oldSteps;
        return result;
    }

    private void getTemplateSet(boolean initSteps) {
        if (initSteps) {
            this.steps = new ArrayList<>();
        }

        SudokuSet setSet = new SudokuSet();

        for (int i = 1; i <= 9; i++) {
            setSet.set(this.finder.getSetValueTemplates(true)[i]);
            setSet.andNot(this.finder.getPositions()[i]);
            if (!setSet.isEmpty()) {
                this.globalStep.reset();
                this.globalStep.setType(SolutionType.TEMPLATE_SET);
                this.globalStep.addValue(i);

                for (int j = 0; j < setSet.size(); j++) {
                    this.globalStep.addIndex(setSet.get(j));
                }

                this.steps.add((SolutionStep) this.globalStep.clone());
            }
        }
    }

    private void getTemplateDel(boolean initSteps) {
        if (initSteps) {
            this.steps = new ArrayList<>();
        }

        SudokuSet setSet = new SudokuSet();

        for (int i = 1; i <= 9; i++) {
            setSet.set(this.finder.getDelCandTemplates(true)[i]);
            setSet.and(this.finder.getCandidates()[i]);
            if (!setSet.isEmpty()) {
                this.globalStep.reset();
                this.globalStep.setType(SolutionType.TEMPLATE_DEL);
                this.globalStep.addValue(i);

                for (int j = 0; j < setSet.size(); j++) {
                    this.globalStep.addCandidateToDelete(setSet.get(j), i);
                }

                this.steps.add((SolutionStep) this.globalStep.clone());
            }
        }
    }
}
