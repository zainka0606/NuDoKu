package solver;

import sudoku.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ColoringSolver extends AbstractSolver {
    private static final int C1 = 0;
    private static final int C2 = 1;
    private static final int MAX_COLOR = 20;
    private SudokuSet[][][] sets = new SudokuSet[10][20][2];
    private int[] anzColorPairs = new int[this.sets.length];
    private int[] stepNumbers = new int[this.sets.length];
    private SudokuSet startSet = new SudokuSet();
    private SudokuSet tmpSet1 = new SudokuSet();
    private SudokuSet deleteSet = new SudokuSet();
    private List<SolutionStep> steps = new ArrayList<>();
    private SolutionStep globalStep = new SolutionStep();

    public ColoringSolver(SudokuStepFinder finder) {
        super(finder);

        for (int i = 0; i < this.sets.length; i++) {
            for (int j = 0; j < this.sets[i].length; j++) {
                for (int k = 0; k < this.sets[i][j].length; k++) {
                    this.sets[i][j][k] = new SudokuSet();
                }
            }

            this.anzColorPairs[i] = 0;
            this.stepNumbers[i] = -1;
        }
    }

    @Override
    protected SolutionStep getStep(SolutionType type) {
        SolutionStep result = null;
        this.sudoku = this.finder.getSudoku();
        switch (type) {
            case SIMPLE_COLORS:
            case SIMPLE_COLORS_TRAP:
            case SIMPLE_COLORS_WRAP:
                result = this.findSimpleColorStep(true);
                break;
            case MULTI_COLORS:
            case MULTI_COLORS_1:
            case MULTI_COLORS_2:
                result = this.findMultiColorStep(true);
        }

        return result;
    }

    @Override
    protected boolean doStep(SolutionStep step) {
        boolean handled = true;
        this.sudoku = this.finder.getSudoku();
        switch (step.getType()) {
            case SIMPLE_COLORS:
            case SIMPLE_COLORS_TRAP:
            case SIMPLE_COLORS_WRAP:
            case MULTI_COLORS:
            case MULTI_COLORS_1:
            case MULTI_COLORS_2:
                for (Candidate cand : step.getCandidatesToDelete()) {
                    this.sudoku.delCandidate(cand.getIndex(), cand.getValue());
                }
                break;
            default:
                handled = false;
        }

        return handled;
    }

    protected List<SolutionStep> findAllSimpleColors() {
        this.sudoku = this.finder.getSudoku();
        List<SolutionStep> oldList = this.steps;
        List<SolutionStep> newList = new ArrayList<>();
        this.steps = newList;
        this.findSimpleColorSteps(false);
        Collections.sort(this.steps);
        this.steps = oldList;
        return newList;
    }

    protected List<SolutionStep> findAllMultiColors() {
        this.sudoku = this.finder.getSudoku();
        List<SolutionStep> oldList = this.steps;
        List<SolutionStep> newList = new ArrayList<>();
        this.steps = newList;
        this.findMultiColorSteps(false);
        Collections.sort(this.steps);
        this.steps = oldList;
        return newList;
    }

    private SolutionStep findSimpleColorStep(boolean onlyOne) {
        this.steps.clear();
        SolutionStep step = this.findSimpleColorSteps(onlyOne);
        if (onlyOne && step != null) {
            return step;
        } else if (this.steps.size() > 0) {
            Collections.sort(this.steps);
            return this.steps.get(0);
        } else {
            return null;
        }
    }

    private SolutionStep findSimpleColorSteps(boolean onlyOne) {
        for (int i = 1; i <= 9; i++) {
            SolutionStep step = this.findSimpleColorStepsForCandidate(i, onlyOne);
            if (onlyOne && step != null) {
                return step;
            }
        }

        return null;
    }

    private SolutionStep findSimpleColorStepsForCandidate(int cand, boolean onlyOne) {
        int anzColors = this.doColoring(cand);

        for (int i = 0; i < anzColors; i++) {
            SudokuSet set1 = this.sets[cand][i][0];
            SudokuSet set2 = this.sets[cand][i][1];
            this.globalStep.reset();
            if (this.checkColorWrap(set1)) {
                for (int j = 0; j < set1.size(); j++) {
                    this.globalStep.addCandidateToDelete(set1.get(j), cand);
                }
            }

            if (this.checkColorWrap(set2)) {
                for (int j = 0; j < set2.size(); j++) {
                    this.globalStep.addCandidateToDelete(set2.get(j), cand);
                }
            }

            if (!this.globalStep.getCandidatesToDelete().isEmpty()) {
                this.globalStep.setType(SolutionType.SIMPLE_COLORS_WRAP);
                this.globalStep.addValue(cand);
                this.globalStep.addColorCandidates(set1, 0);
                this.globalStep.addColorCandidates(set2, 1);
                SolutionStep step = (SolutionStep) this.globalStep.clone();
                if (onlyOne) {
                    return step;
                }

                this.steps.add(step);
            }

            this.globalStep.reset();
            this.checkCandidateToDelete(set1, set2, cand);
            if (!this.globalStep.getCandidatesToDelete().isEmpty()) {
                this.globalStep.setType(SolutionType.SIMPLE_COLORS_TRAP);
                this.globalStep.addValue(cand);
                this.globalStep.addColorCandidates(set1, 0);
                this.globalStep.addColorCandidates(set2, 1);
                SolutionStep step = (SolutionStep) this.globalStep.clone();
                if (onlyOne) {
                    return step;
                }

                this.steps.add(step);
            }
        }

        return null;
    }

    private boolean checkColorWrap(SudokuSet set) {
        for (int i = 0; i < set.size() - 1; i++) {
            for (int j = i + 1; j < set.size(); j++) {
                if (Sudoku2.buddies[set.get(i)].contains(set.get(j))) {
                    return true;
                }
            }
        }

        return false;
    }

    private void checkCandidateToDelete(SudokuSet set1, SudokuSet set2, int cand) {
        this.deleteSet.clear();

        for (int i = 0; i < set1.size(); i++) {
            for (int j = 0; j < set2.size(); j++) {
                this.tmpSet1.set(Sudoku2.buddies[set1.get(i)]);
                this.tmpSet1.and(Sudoku2.buddies[set2.get(j)]);
                this.tmpSet1.and(this.finder.getCandidates()[cand]);
                this.deleteSet.or(this.tmpSet1);
            }
        }

        if (!this.deleteSet.isEmpty()) {
            for (int i = 0; i < this.deleteSet.size(); i++) {
                this.globalStep.addCandidateToDelete(this.deleteSet.get(i), cand);
            }
        }
    }

    private SolutionStep findMultiColorStep(boolean onlyOne) {
        this.steps.clear();
        SolutionStep step = this.findMultiColorSteps(onlyOne);
        if (onlyOne && step != null) {
            return step;
        } else if (this.steps.size() > 0) {
            Collections.sort(this.steps);
            return this.steps.get(0);
        } else {
            return null;
        }
    }

    private SolutionStep findMultiColorSteps(boolean onlyOne) {
        for (int i = 1; i <= 9; i++) {
            SolutionStep step = this.findMultiColorStepsForCandidate(i, onlyOne);
            if (onlyOne && step != null) {
                return step;
            }
        }

        return null;
    }

    private SolutionStep findMultiColorStepsForCandidate(int cand, boolean onlyOne) {
        int anzColors = this.doColoring(cand);

        for (int i = 0; i < anzColors; i++) {
            for (int j = 0; j < anzColors; j++) {
                if (i != j) {
                    SudokuSet set11 = this.sets[cand][i][0];
                    SudokuSet set12 = this.sets[cand][i][1];
                    SudokuSet set21 = this.sets[cand][j][0];
                    SudokuSet set22 = this.sets[cand][j][1];
                    this.globalStep.reset();
                    if (this.checkMultiColor1(set11, set21, set22)) {
                        for (int k = 0; k < set11.size(); k++) {
                            this.globalStep.addCandidateToDelete(set11.get(k), cand);
                        }
                    }

                    if (this.checkMultiColor1(set12, set21, set22)) {
                        for (int k = 0; k < set12.size(); k++) {
                            this.globalStep.addCandidateToDelete(set12.get(k), cand);
                        }
                    }

                    if (!this.globalStep.getCandidatesToDelete().isEmpty()) {
                        this.globalStep.setType(SolutionType.MULTI_COLORS_2);
                        this.globalStep.addValue(cand);
                        this.globalStep.addColorCandidates(set11, 0);
                        this.globalStep.addColorCandidates(set12, 1);
                        this.globalStep.addColorCandidates(set21, 2);
                        this.globalStep.addColorCandidates(set22, 3);
                        SolutionStep step = (SolutionStep) this.globalStep.clone();
                        if (onlyOne) {
                            return step;
                        }

                        this.steps.add(step);
                    }

                    this.globalStep.reset();
                    if (this.checkMultiColor2(set11, set21)) {
                        this.checkCandidateToDelete(set12, set22, cand);
                    }

                    if (this.checkMultiColor2(set11, set22)) {
                        this.checkCandidateToDelete(set12, set21, cand);
                    }

                    if (this.checkMultiColor2(set12, set21)) {
                        this.checkCandidateToDelete(set11, set22, cand);
                    }

                    if (this.checkMultiColor2(set12, set22)) {
                        this.checkCandidateToDelete(set11, set21, cand);
                    }

                    if (!this.globalStep.getCandidatesToDelete().isEmpty()) {
                        this.globalStep.setType(SolutionType.MULTI_COLORS_1);
                        this.globalStep.addValue(cand);
                        this.globalStep.addColorCandidates(set11, 0);
                        this.globalStep.addColorCandidates(set12, 1);
                        this.globalStep.addColorCandidates(set21, 2);
                        this.globalStep.addColorCandidates(set22, 3);
                        SolutionStep step = (SolutionStep) this.globalStep.clone();
                        if (onlyOne) {
                            return step;
                        }

                        this.steps.add(step);
                    }
                }
            }
        }

        return null;
    }

    private boolean checkMultiColor1(SudokuSet set, SudokuSet s21, SudokuSet s22) {
        boolean seeS21 = false;
        boolean seeS22 = false;

        for (int i = 0; i < set.size(); i++) {
            this.tmpSet1.set(Sudoku2.buddies[set.get(i)]);
            if (!this.tmpSet1.andEmpty(s21)) {
                seeS21 = true;
            }

            if (!this.tmpSet1.andEmpty(s22)) {
                seeS22 = true;
            }

            if (seeS21 && seeS22) {
                return true;
            }
        }

        return false;
    }

    private boolean checkMultiColor2(SudokuSet set1, SudokuSet set2) {
        for (int i = 0; i < set1.size(); i++) {
            for (int j = 0; j < set2.size(); j++) {
                if (Sudoku2.buddies[set1.get(i)].contains(set2.get(j))) {
                    return true;
                }
            }
        }

        return false;
    }

    private int doColoring(int cand) {
        if (this.stepNumbers[cand] == this.finder.getStepNumber()) {
            return this.anzColorPairs[cand];
        }

        this.anzColorPairs[cand] = 0;
        this.stepNumbers[cand] = this.finder.getStepNumber();
        this.startSet.set(this.finder.getCandidates()[cand]);
        int[] values = this.startSet.getValues();
        int size = this.startSet.size();
        byte[][] free = this.sudoku.getFree();

        for (int i = 0; i < size; i++) {
            int index = values[i];
            if (free[Sudoku2.CONSTRAINTS[index][0]][cand] != 2 && free[Sudoku2.CONSTRAINTS[index][1]][cand] != 2 && free[Sudoku2.CONSTRAINTS[index][2]][cand] != 2
            ) {
                this.startSet.remove(values[i]);
            }
        }

        while (!this.startSet.isEmpty()) {
            SudokuSet[] actSets = this.sets[cand][this.anzColorPairs[cand]];
            actSets[0].clear();
            actSets[1].clear();
            int index = this.startSet.get(0);
            this.doColoringForColorRecursive(index, cand, true);
            if (!actSets[0].isEmpty() && !actSets[1].isEmpty()) {
                this.anzColorPairs[cand]++;
            } else {
                actSets[0].clear();
                actSets[1].clear();
            }
        }

        return this.anzColorPairs[cand];
    }

    private void doColoringForColorRecursive(int index, int cand, boolean on) {
        if (index != -1 && this.startSet.contains(index)) {
            if (on) {
                this.sets[cand][this.anzColorPairs[cand]][0].add(index);
            } else {
                this.sets[cand][this.anzColorPairs[cand]][1].add(index);
            }

            this.startSet.remove(index);
            this.doColoringForColorRecursive(this.getConjugateIndex(index, cand, Sudoku2.CONSTRAINTS[index][0]), cand, !on);
            this.doColoringForColorRecursive(this.getConjugateIndex(index, cand, Sudoku2.CONSTRAINTS[index][1]), cand, !on);
            this.doColoringForColorRecursive(this.getConjugateIndex(index, cand, Sudoku2.CONSTRAINTS[index][2]), cand, !on);
        }
    }

    private int getConjugateIndex(int index, int cand, int constraint) {
        if (this.sudoku.getFree()[constraint][cand] != 2) {
            return -1;
        }

        this.tmpSet1.set(this.finder.getCandidates()[cand]);
        this.tmpSet1.and(Sudoku2.ALL_CONSTRAINTS_TEMPLATES[constraint]);
        int result = this.tmpSet1.get(0);
        if (result == index) {
            result = this.tmpSet1.get(1);
        }

        return result;
    }
}
