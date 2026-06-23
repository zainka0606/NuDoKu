package solver;

import sudoku.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MiscellaneousSolver extends AbstractSolver {
    private List<SolutionStep> steps;
    private SolutionStep globalStep = new SolutionStep(SolutionType.HIDDEN_SINGLE);
    private SudokuSet nonBlockSet = new SudokuSet();
    private SudokuSet blockSet = new SudokuSet();
    private SudokuSet intersectionSet = new SudokuSet();
    private SudokuSet nonBlockSourceSet = new SudokuSet();
    private SudokuSet blockSourceSet = new SudokuSet();
    private MiscellaneousSolver.StackEntry[] stack1 = new MiscellaneousSolver.StackEntry[9];
    private MiscellaneousSolver.StackEntry[] stack2 = new MiscellaneousSolver.StackEntry[9];
    private SudokuSet intersectionActSet = new SudokuSet();
    private short intersectionActCandSet = 0;
    private SudokuSet nonBlockActSet = new SudokuSet();
    private short nonBlockActCandSet = 0;
    private short blockAllowedCandSet = 0;
    private SudokuSet blockActSet = new SudokuSet();
    private short blockActCandSet = 0;
    private SudokuSet tmpSet = new SudokuSet();

    public MiscellaneousSolver(SudokuStepFinder finder) {
        super(finder);

        for (int i = 0; i < this.stack1.length; i++) {
            this.stack1[i] = new MiscellaneousSolver.StackEntry();
            this.stack2[i] = new MiscellaneousSolver.StackEntry();
        }
    }

    public static void main(String[] args) {
        Sudoku2 sudoku = new Sudoku2();
        sudoku.setSudoku(
                ":1101:12369:.....3+5+1+7+1+3+5+42+786+9867+91+54..+6+935+4+82+717183.+2.+54+2+54...........47.55......+4.....+5..9.::184 194 273 371 684 685 694 985::"
        );
        SudokuSolver solver = SudokuSolverFactory.getDefaultSolverInstance();
        boolean singleHint = true;
        if (singleHint) {
            SolutionStep step = solver.getHint(sudoku, false);
            System.out.println(step);
        } else {
            List<SolutionStep> steps = solver.getStepFinder().getAllSueDeCoqs(sudoku);
            solver.getStepFinder().printStatistics();
            if (steps.size() > 0) {
                Collections.sort(steps);

                for (SolutionStep actStep : steps) {
                    System.out.println(actStep);
                }
            }
        }

        System.exit(0);
    }

    @Override
    protected SolutionStep getStep(SolutionType type) {
        SolutionStep result = null;
        this.sudoku = this.finder.getSudoku();
        switch (type) {
            case SUE_DE_COQ:
                result = this.getSueDeCoq(true);
            default:
                return result;
        }
    }

    @Override
    protected boolean doStep(SolutionStep step) {
        boolean handled = true;
        this.sudoku = this.finder.getSudoku();
        switch (step.getType()) {
            case SUE_DE_COQ:
                for (Candidate cand : step.getCandidatesToDelete()) {
                    this.sudoku.delCandidate(cand.getIndex(), cand.getValue());
                }
                break;
            default:
                handled = false;
        }

        return handled;
    }

    protected List<SolutionStep> getAllSueDeCoqs() {
        this.sudoku = this.finder.getSudoku();
        List<SolutionStep> oldSteps = this.steps;
        this.steps = new ArrayList<>();
        this.getSueDeCoqInt(Sudoku2.LINE_TEMPLATES, Sudoku2.BLOCK_TEMPLATES, false);
        this.getSueDeCoqInt(Sudoku2.COL_TEMPLATES, Sudoku2.BLOCK_TEMPLATES, false);
        List<SolutionStep> result = this.steps;
        this.steps = oldSteps;
        return result;
    }

    private SolutionStep getSueDeCoq(boolean onlyOne) {
        SolutionStep step = this.getSueDeCoqInt(Sudoku2.LINE_TEMPLATES, Sudoku2.BLOCK_TEMPLATES, onlyOne);
        return onlyOne && step != null ? step : this.getSueDeCoqInt(Sudoku2.COL_TEMPLATES, Sudoku2.BLOCK_TEMPLATES, onlyOne);
    }

    private SolutionStep getSueDeCoqInt(SudokuSet[] nonBlocks, SudokuSet[] blocks, boolean onlyOne) {
        SudokuSet emptyCells = this.finder.getEmptyCells();

        for (int i = 0; i < nonBlocks.length; i++) {
            this.nonBlockSet.setAnd(nonBlocks[i], emptyCells);

            for (int j = 0; j < blocks.length; j++) {
                this.blockSet.setAnd(blocks[j], emptyCells);
                this.intersectionSet.set(this.nonBlockSet);
                this.intersectionSet.and(this.blockSet);
                if (!this.intersectionSet.isEmpty() && this.intersectionSet.size() >= 2) {
                    SolutionStep step = this.checkIntersection(onlyOne);
                    if (onlyOne && step != null) {
                        return step;
                    }
                }
            }
        }

        return null;
    }

    private SolutionStep checkIntersection(boolean onlyOne) {
        int max = this.intersectionSet.size();
        int nPlus = 0;
        this.intersectionActSet.clear();

        for (int i1 = 0; i1 < max - 1; i1++) {
            int index1 = this.intersectionSet.get(i1);
            this.intersectionActSet.add(index1);
            short cand1 = this.sudoku.getCell(index1);

            for (int i2 = i1 + 1; i2 < max; i2++) {
                int index2 = this.intersectionSet.get(i2);
                short cand2 = (short) (cand1 | this.sudoku.getCell(index2));
                this.intersectionActSet.add(index2);
                nPlus = Sudoku2.ANZ_VALUES[cand2] - 2;
                if (nPlus >= 2) {
                    SolutionStep step = this.checkHouses(nPlus, cand2, onlyOne);
                    if (onlyOne && step != null) {
                        return step;
                    }
                }

                for (int i3 = i2 + 1; i3 < max; i3++) {
                    int index3 = this.intersectionSet.get(i3);
                    short cand3 = (short) (cand2 | this.sudoku.getCell(index3));
                    nPlus = Sudoku2.ANZ_VALUES[cand3] - 3;
                    if (nPlus >= 2) {
                        this.intersectionActSet.add(index3);
                        SolutionStep step = this.checkHouses(nPlus, cand3, onlyOne);
                        if (onlyOne && step != null) {
                            return step;
                        }

                        this.intersectionActSet.remove(index3);
                    }
                }

                this.intersectionActSet.remove(index2);
            }

            this.intersectionActSet.remove(index1);
        }

        return null;
    }

    private SolutionStep checkHouses(int nPlus, short cand, boolean onlyOne) {
        this.intersectionActCandSet = cand;
        this.nonBlockSourceSet.set(this.nonBlockSet);
        this.nonBlockSourceSet.andNot(this.intersectionActSet);
        return this.checkHouses(nPlus, this.nonBlockSourceSet, (short) 511, onlyOne, false);
    }

    private SolutionStep checkHouses(int nPlus, SudokuSet sourceSet, short allowedCandSet, boolean onlyOne, boolean secondCheck) {
        if (sourceSet.isEmpty()) {
            return null;
        }

        MiscellaneousSolver.StackEntry[] stack = secondCheck ? this.stack2 : this.stack1;
        int max = sourceSet.size();
        int level = 1;
        stack[0].aktIndex = -1;
        stack[0].candidates = 0;
        stack[0].indices.clear();
        stack[1].aktIndex = -1;

        do {
            while (stack[level].aktIndex < max - 1) {
                stack[level].aktIndex++;
                stack[level].indices.set(stack[level - 1].indices);
                stack[level].indices.add(sourceSet.get(stack[level].aktIndex));
                stack[level].candidates = (short) (stack[level - 1].candidates | this.sudoku.getCell(sourceSet.get(stack[level].aktIndex)));
                if ((stack[level].candidates & ~allowedCandSet) == 0) {
                    short tmpCands = (short) (stack[level].candidates & this.intersectionActCandSet);
                    int anzContained = Sudoku2.ANZ_VALUES[tmpCands];
                    tmpCands = (short) (stack[level].candidates & ~this.intersectionActCandSet);
                    int anzExtra = Sudoku2.ANZ_VALUES[tmpCands];
                    if (!secondCheck) {
                        if (anzContained > 0 && level > anzExtra && level - anzExtra < nPlus) {
                            this.nonBlockActSet = stack[level].indices;
                            this.nonBlockActCandSet = stack[level].candidates;
                            this.blockSourceSet.set(this.blockSet);
                            this.blockSourceSet.andNot(this.intersectionActSet);
                            this.blockSourceSet.andNot(this.nonBlockActSet);
                            this.blockAllowedCandSet = this.nonBlockActCandSet;
                            this.blockAllowedCandSet = (short) (this.blockAllowedCandSet & ~tmpCands);
                            this.blockAllowedCandSet = (short) (~this.blockAllowedCandSet);
                            SolutionStep step = this.checkHouses(
                                    nPlus - (this.nonBlockActSet.size() - anzExtra), this.blockSourceSet, this.blockAllowedCandSet, onlyOne, true
                            );
                            if (onlyOne && step != null) {
                                return step;
                            }
                        }
                    } else if (anzContained > 0 && stack[level].indices.size() - anzExtra == nPlus) {
                        this.globalStep.reset();
                        this.blockActSet = stack[level].indices;
                        this.blockActCandSet = stack[level].candidates;
                        short tmpCandSet1 = (short) (this.blockActCandSet & this.nonBlockActCandSet);
                        this.tmpSet.set(this.blockSet);
                        this.tmpSet.andNot(this.blockActSet);
                        this.tmpSet.andNot(this.intersectionActSet);
                        short tmpCandSet = (short) ((this.intersectionActCandSet | this.blockActCandSet) & ~this.nonBlockActCandSet | tmpCandSet1);
                        this.checkCandidatesToDelete(this.tmpSet, tmpCandSet);
                        this.tmpSet.set(this.nonBlockSet);
                        this.tmpSet.andNot(this.nonBlockActSet);
                        this.tmpSet.andNot(this.intersectionActSet);
                        tmpCandSet = (short) ((this.intersectionActCandSet | this.nonBlockActCandSet) & ~this.blockActCandSet | tmpCandSet1);
                        this.checkCandidatesToDelete(this.tmpSet, tmpCandSet);
                        if (this.globalStep.getCandidatesToDelete().size() > 0) {
                            this.globalStep.setType(SolutionType.SUE_DE_COQ);

                            for (int j = 0; j < this.intersectionActSet.size(); j++) {
                                this.globalStep.addIndex(this.intersectionActSet.get(j));
                            }

                            int[] cands = Sudoku2.POSSIBLE_VALUES[this.intersectionActCandSet];

                            for (int j = 0; j < cands.length; j++) {
                                this.globalStep.addValue(cands[j]);
                            }

                            this.getSetCandidates(this.nonBlockActSet, this.intersectionActSet, this.nonBlockActCandSet, this.globalStep.getFins());
                            this.getSetCandidates(this.blockActSet, this.intersectionActSet, this.blockActCandSet, this.globalStep.getEndoFins());
                            this.globalStep.addAls(this.intersectionActSet, this.intersectionActCandSet);
                            this.globalStep.addAls(this.blockActSet, this.blockActCandSet);
                            this.globalStep.addAls(this.nonBlockActSet, this.nonBlockActCandSet);
                            SolutionStep step = (SolutionStep) this.globalStep.clone();
                            if (onlyOne) {
                                return step;
                            }

                            this.steps.add(step);
                        }
                    }
                }

                if (stack[level].aktIndex < max - 1) {
                    stack[++level].aktIndex = stack[level - 1].aktIndex;
                }
            }
        } while (--level > 0);

        return null;
    }

    private void getSetCandidates(SudokuSet srcSet1, SudokuSet srcSet2, short candSet, List<Candidate> dest) {
        this.tmpSet.set(srcSet1);
        this.tmpSet.or(srcSet2);

        for (int i = 0; i < this.tmpSet.size(); i++) {
            int index = this.tmpSet.get(i);
            if ((this.sudoku.getCell(index) & candSet) != 0) {
                int[] cands = Sudoku2.POSSIBLE_VALUES[this.sudoku.getCell(index) & candSet];

                for (int j = 0; j < cands.length; j++) {
                    dest.add(new Candidate(index, cands[j]));
                }
            }
        }
    }

    private void checkCandidatesToDelete(SudokuSet tmpSet, short tmpCandSet) {
        if (tmpSet.size() > 0 && Sudoku2.ANZ_VALUES[tmpCandSet] > 0) {
            for (int i = 0; i < tmpSet.size(); i++) {
                int index = tmpSet.get(i);
                short elimCandMask = (short) (this.sudoku.getCell(index) & tmpCandSet);
                if (elimCandMask != 0) {
                    int[] cands = Sudoku2.POSSIBLE_VALUES[elimCandMask];

                    for (int j = 0; j < cands.length; j++) {
                        this.globalStep.addCandidateToDelete(index, cands[j]);
                    }
                }
            }
        }
    }

    private class StackEntry {
        int aktIndex = 0;
        SudokuSet indices = new SudokuSet();
        short candidates = 0;

        private StackEntry() {
        }
    }
}
