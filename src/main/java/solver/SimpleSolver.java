package solver;

import sudoku.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SimpleSolver extends AbstractSolver {
    private boolean[] singleFound = new boolean[81];
    private List<SolutionStep> steps;
    private SolutionStep globalStep = new SolutionStep();
    private boolean[] sameConstraint = new boolean[Sudoku2.CONSTRAINTS[0].length];
    private boolean[] foundConstraint = new boolean[Sudoku2.CONSTRAINTS[0].length];
    private int[] constraint = new int[Sudoku2.CONSTRAINTS[0].length];
    private int[] indices2 = new int[2];
    private int[] indices3 = new int[3];
    private int[] indices4 = new int[4];
    private List<SolutionStep> cachedSteps = new ArrayList<>();
    private int cachedStepsNumber = -1;
    private int[] tmpArr1 = new int[9];
    private short[] ipcMask = new short[10];

    protected SimpleSolver(SudokuStepFinder finder) {
        super(finder);
        this.steps = new ArrayList<>();
    }

    public static void main(String[] args) {
        Sudoku2 sudoku = new Sudoku2();
        sudoku.setSudoku(
                ":0110:38:.1.57.4..7+521+4.6..........5.+2...1........+7.2..7562+839.2.+7......569214....4.7.....::319 329 338 378 388 398 819 829 837 838 848 878 888 898::"
        );
        SudokuSolver solver = SudokuSolverFactory.getDefaultSolverInstance();
        SolutionStep step = solver.getHint(sudoku, false);
        System.out.println(step);
        System.exit(0);
    }

    @Override
    protected SolutionStep getStep(SolutionType type) {
        SolutionStep result = null;
        this.sudoku = this.finder.getSudoku();
        switch (type) {
            case FULL_HOUSE:
                result = this.findFullHouse(false);
                break;
            case HIDDEN_SINGLE:
                result = this.findHiddenSingle();
                break;
            case HIDDEN_PAIR:
                result = this.findHiddenXle(2);
                break;
            case HIDDEN_TRIPLE:
                result = this.findHiddenXle(3);
                break;
            case HIDDEN_QUADRUPLE:
                result = this.findHiddenXle(4);
                break;
            case NAKED_SINGLE:
                result = this.findNakedSingle();
                break;
            case LOCKED_PAIR:
                result = this.findNakedXle(2, true);
                break;
            case NAKED_PAIR:
                result = this.findNakedXle(2, false);
                break;
            case LOCKED_TRIPLE:
                result = this.findNakedXle(3, true);
                break;
            case NAKED_TRIPLE:
                result = this.findNakedXle(3, false);
                break;
            case NAKED_QUADRUPLE:
                result = this.findNakedXle(4, false);
                break;
            case LOCKED_CANDIDATES:
            case LOCKED_CANDIDATES_1:
            case LOCKED_CANDIDATES_2:
                result = this.findLockedCandidates(type);
        }

        return result;
    }

    @Override
    protected boolean doStep(SolutionStep step) {
        boolean handled = true;
        this.sudoku = this.finder.getSudoku();
        switch (step.getType()) {
            case FULL_HOUSE:
            case HIDDEN_SINGLE:
            case NAKED_SINGLE:
                this.sudoku.setCell(step.getIndices().get(0), step.getValues().get(0));
                break;
            case HIDDEN_PAIR:
            case HIDDEN_TRIPLE:
            case HIDDEN_QUADRUPLE:
            case LOCKED_PAIR:
            case NAKED_PAIR:
            case LOCKED_TRIPLE:
            case NAKED_TRIPLE:
            case NAKED_QUADRUPLE:
            case LOCKED_CANDIDATES:
            case LOCKED_CANDIDATES_1:
            case LOCKED_CANDIDATES_2:
                for (Candidate cand : step.getCandidatesToDelete()) {
                    this.sudoku.delCandidate(cand.getIndex(), cand.getValue());
                }
                break;
            default:
                handled = false;
        }

        return handled;
    }

    protected List<SolutionStep> findAllFullHouses() {
        this.sudoku = this.finder.getSudoku();
        List<SolutionStep> oldList = this.steps;
        List<SolutionStep> newList = new ArrayList<>();
        this.steps = newList;
        this.findFullHouse(true);
        Collections.sort(this.steps);
        this.steps = oldList;
        return newList;
    }

    private SolutionStep findFullHouse(boolean all) {
        SolutionStep step = null;
        byte[][] free = this.sudoku.getFree();
        SudokuSinglesQueue nsQueue = this.sudoku.getNsQueue();

        for (int queueIndex = nsQueue.getFirstIndex(); queueIndex != -1; queueIndex = nsQueue.getNextIndex()) {
            int index = nsQueue.getIndex(queueIndex);
            int value = nsQueue.getValue(queueIndex);
            if (this.sudoku.getValue(index) == 0) {
                for (int i = 0; i < Sudoku2.CONSTRAINTS[index].length; i++) {
                    int constr = Sudoku2.CONSTRAINTS[index][i];
                    boolean valid = true;

                    for (int j = 1; j <= 9; j++) {
                        if (j != value && free[constr][j] != 0) {
                            valid = false;
                            break;
                        }
                    }

                    if (valid) {
                        step = new SolutionStep(SolutionType.FULL_HOUSE);
                        step.addValue(value);
                        step.addIndex(index);
                        if (!all) {
                            return step;
                        }

                        this.steps.add(step);
                        break;
                    }
                }
            }
        }

        return step;
    }

    private SolutionStep findNakedSingle() {
        SolutionStep step = null;
        SudokuSinglesQueue nsQueue = this.sudoku.getNsQueue();
        int queueIndex = -1;

        while ((queueIndex = nsQueue.getSingle()) != -1) {
            int index = nsQueue.getIndex(queueIndex);
            int value = nsQueue.getValue(queueIndex);
            if (this.sudoku.getValue(index) == 0) {
                step = new SolutionStep(SolutionType.NAKED_SINGLE);
                step.addValue(value);
                step.addIndex(index);
                break;
            }
        }

        return step;
    }

    protected List<SolutionStep> findAllNakedSingles() {
        this.sudoku = this.finder.getSudoku();
        List<SolutionStep> oldList = this.steps;
        List<SolutionStep> newList = new ArrayList<>();
        this.steps = newList;
        SudokuSinglesQueue nsQueue = this.sudoku.getNsQueue();

        for (int queueIndex = nsQueue.getFirstIndex(); queueIndex != -1; queueIndex = nsQueue.getNextIndex()) {
            int index = nsQueue.getIndex(queueIndex);
            int value = nsQueue.getValue(queueIndex);
            if (this.sudoku.getValue(index) == 0) {
                SolutionStep step = new SolutionStep(SolutionType.NAKED_SINGLE);
                step.addValue(value);
                step.addIndex(index);
                this.steps.add(step);
            }
        }

        Collections.sort(this.steps);
        this.steps = oldList;
        return newList;
    }

    private SolutionStep findNakedXle(int anz, boolean lockedOnly) {
        SudokuUtil.clearStepList(this.steps);
        if (this.cachedSteps.size() > 0 && this.cachedStepsNumber == this.finder.getStepNumber()) {
            SolutionType type = SolutionType.NAKED_PAIR;
            if (anz == 2 && lockedOnly) {
                type = SolutionType.LOCKED_PAIR;
            }

            if (anz == 3 && !lockedOnly) {
                type = SolutionType.NAKED_TRIPLE;
            }

            if (anz == 3 && lockedOnly) {
                type = SolutionType.LOCKED_TRIPLE;
            }

            if (anz == 4) {
                type = SolutionType.NAKED_QUADRUPLE;
            }

            for (SolutionStep step : this.cachedSteps) {
                if (step.getType() == type) {
                    return step;
                }
            }
        }

        this.cachedSteps.clear();
        this.cachedStepsNumber = this.finder.getStepNumber();
        SolutionStep step = this.findNakedXleInEntity(Sudoku2.BLOCKS, anz, lockedOnly, !lockedOnly, true);
        if (step == null && !lockedOnly) {
            step = this.findNakedXleInEntity(Sudoku2.LINES, anz, lockedOnly, !lockedOnly, true);
            return step != null ? step : this.findNakedXleInEntity(Sudoku2.COLS, anz, lockedOnly, !lockedOnly, true);
        } else {
            return step;
        }
    }

    protected List<SolutionStep> findAllNakedXle() {
        this.sudoku = this.finder.getSudoku();
        List<SolutionStep> oldList = this.steps;
        List<SolutionStep> newList = new ArrayList<>();
        this.steps = newList;
        List<SolutionStep> tmpSteps = this.findAllNakedSingles();
        this.steps.addAll(tmpSteps);

        for (int i = 2; i <= 4; i++) {
            this.findNakedXleInEntity(Sudoku2.BLOCKS, i, false, false, false);
            this.findNakedXleInEntity(Sudoku2.LINES, i, false, false, false);
            this.findNakedXleInEntity(Sudoku2.COLS, i, false, false, false);
        }

        Collections.sort(this.steps);
        this.steps = oldList;
        return newList;
    }

    private SolutionStep findNakedXleInEntity(int[][] indices, int anz, boolean lockedOnly, boolean nakedOnly, boolean onlyOne) {
        SolutionStep step = null;

        for (int entity = 0; entity < indices.length; entity++) {
            int maxIndex = 0;

            for (int i = 0; i < indices[entity].length; i++) {
                int tmpAnz = Sudoku2.ANZ_VALUES[this.sudoku.getCell(indices[entity][i])];
                if (tmpAnz != 0 && tmpAnz <= anz) {
                    this.tmpArr1[maxIndex++] = indices[entity][i];
                }
            }

            if (maxIndex >= anz) {
                for (int i1 = 0; i1 < maxIndex - anz + 1; i1++) {
                    short cell1 = this.sudoku.getCell(this.tmpArr1[i1]);

                    for (int i2 = i1 + 1; i2 < maxIndex - anz + 2; i2++) {
                        short cell2 = (short) (cell1 | this.sudoku.getCell(this.tmpArr1[i2]));
                        if (Sudoku2.ANZ_VALUES[cell2] <= anz) {
                            if (anz == 2) {
                                if (Sudoku2.ANZ_VALUES[cell2] == anz) {
                                    step = this.createSubsetStep(this.tmpArr1[i1], this.tmpArr1[i2], -1, -1, cell2, SolutionType.NAKED_PAIR, lockedOnly, nakedOnly);
                                    if (step != null && onlyOne) {
                                        return step;
                                    }
                                }
                            } else {
                                for (int i3 = i2 + 1; i3 < maxIndex - anz + 3; i3++) {
                                    short cell3 = (short) (cell2 | this.sudoku.getCell(this.tmpArr1[i3]));
                                    if (Sudoku2.ANZ_VALUES[cell3] <= anz) {
                                        if (anz == 3) {
                                            if (Sudoku2.ANZ_VALUES[cell3] == anz) {
                                                step = this.createSubsetStep(
                                                        this.tmpArr1[i1], this.tmpArr1[i2], this.tmpArr1[i3], -1, cell3, SolutionType.NAKED_TRIPLE, lockedOnly, nakedOnly
                                                );
                                                if (step != null && onlyOne) {
                                                    return step;
                                                }
                                            }
                                        } else {
                                            for (int i4 = i3 + 1; i4 < maxIndex; i4++) {
                                                short cell4 = (short) (cell3 | this.sudoku.getCell(this.tmpArr1[i4]));
                                                if (Sudoku2.ANZ_VALUES[cell4] <= anz && Sudoku2.ANZ_VALUES[cell4] == anz) {
                                                    step = this.createSubsetStep(
                                                            this.tmpArr1[i1],
                                                            this.tmpArr1[i2],
                                                            this.tmpArr1[i3],
                                                            this.tmpArr1[i4],
                                                            cell4,
                                                            SolutionType.NAKED_QUADRUPLE,
                                                            lockedOnly,
                                                            nakedOnly
                                                    );
                                                    if (step != null && onlyOne) {
                                                        return step;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    private SolutionStep findHiddenSingle() {
        SolutionStep step = null;
        byte[][] free = this.sudoku.getFree();
        SudokuSinglesQueue hsQueue = this.sudoku.getHsQueue();
        int queueIndex = -1;

        while ((queueIndex = hsQueue.getSingle()) != -1) {
            int index = hsQueue.getIndex(queueIndex);
            int value = hsQueue.getValue(queueIndex);
            if (this.sudoku.getValue(index) == 0) {
                for (int i = 0; i < Sudoku2.CONSTRAINTS[index].length; i++) {
                    if (free[Sudoku2.CONSTRAINTS[index][i]][value] == 1) {
                        step = new SolutionStep(SolutionType.HIDDEN_SINGLE);
                        step.addValue(value);
                        step.addIndex(index);
                        return step;
                    }
                }
                break;
            }
        }

        return step;
    }

    protected List<SolutionStep> findAllHiddenSingles() {
        this.sudoku = this.finder.getSudoku();
        List<SolutionStep> oldList = this.steps;
        List<SolutionStep> newList = new ArrayList<>();
        this.steps = newList;
        Arrays.fill(this.singleFound, false);
        byte[][] free = this.sudoku.getFree();
        SudokuSinglesQueue hsQueue = this.sudoku.getHsQueue();

        for (int queueIndex = hsQueue.getFirstIndex(); queueIndex != -1; queueIndex = hsQueue.getNextIndex()) {
            int index = hsQueue.getIndex(queueIndex);
            int value = hsQueue.getValue(queueIndex);
            if (this.sudoku.getValue(index) == 0 && !this.singleFound[index]) {
                for (int i = 0; i < Sudoku2.CONSTRAINTS[index].length; i++) {
                    if (free[Sudoku2.CONSTRAINTS[index][i]][value] == 1) {
                        SolutionStep step = new SolutionStep(SolutionType.HIDDEN_SINGLE);
                        step.addValue(value);
                        step.addIndex(index);
                        step.setEntity(i);
                        this.steps.add(step);
                        this.singleFound[index] = true;
                        break;
                    }
                }
            }
        }

        Collections.sort(this.steps);
        this.steps = oldList;
        return newList;
    }

    protected List<SolutionStep> findAllHiddenXle() {
        this.sudoku = this.finder.getSudoku();
        List<SolutionStep> oldList = this.steps;
        List<SolutionStep> newList = new ArrayList<>();
        this.steps = newList;
        List<SolutionStep> tmpSteps = this.findAllHiddenSingles();
        this.steps.addAll(tmpSteps);

        for (int i = 2; i <= 4; i++) {
            this.findHiddenXleInEntity(18, Sudoku2.BLOCKS, i, false);
            this.findHiddenXleInEntity(0, Sudoku2.LINES, i, false);
            this.findHiddenXleInEntity(9, Sudoku2.COLS, i, false);
        }

        Collections.sort(this.steps);
        this.steps = oldList;
        return newList;
    }

    private SolutionStep findHiddenXle(int anz) {
        SudokuUtil.clearStepList(this.steps);
        SolutionStep step = this.findHiddenXleInEntity(18, Sudoku2.BLOCKS, anz, true);
        if (step != null) {
            return step;
        }

        step = this.findHiddenXleInEntity(0, Sudoku2.LINES, anz, true);
        return step != null ? step : this.findHiddenXleInEntity(9, Sudoku2.COLS, anz, true);
    }

    private SolutionStep findHiddenXleInEntity(int constraintBase, int[][] indices, int anz, boolean onlyOne) {
        SolutionStep step = null;

        for (int entity = 0; entity < indices.length; entity++) {
            int maxIndex = 0;

            for (int i = 0; i < indices[entity].length; i++) {
                if (this.sudoku.getCell(indices[entity][i]) != 0) {
                    maxIndex++;
                }
            }

            if (maxIndex > anz) {
                short candMask = 0;
                byte[][] free = this.sudoku.getFree();

                for (int i = 1; i <= 9; i++) {
                    int actFree = free[constraintBase + entity][i];
                    if (actFree != 0 && actFree <= anz) {
                        candMask |= Sudoku2.MASKS[i];
                        this.ipcMask[i] = 0;

                        for (int j = 0; j < 9; j++) {
                            if ((this.sudoku.getCell(indices[entity][j]) & Sudoku2.MASKS[i]) != 0) {
                                this.ipcMask[i] = (short) (this.ipcMask[i] | Sudoku2.MASKS[j + 1]);
                            }
                        }
                    }
                }

                if (Sudoku2.ANZ_VALUES[candMask] >= anz) {
                    int[] candArr = Sudoku2.POSSIBLE_VALUES[candMask];

                    for (int i1 = 0; i1 < candArr.length - anz + 1; i1++) {
                        short cand1 = Sudoku2.MASKS[candArr[i1]];
                        short cell1 = this.ipcMask[candArr[i1]];

                        for (int i2 = i1 + 1; i2 < candArr.length - anz + 2; i2++) {
                            short cand2 = (short) (cand1 | Sudoku2.MASKS[candArr[i2]]);
                            short cell2 = (short) (cell1 | this.ipcMask[candArr[i2]]);
                            if (anz == 2) {
                                if (Sudoku2.ANZ_VALUES[cell2] == anz) {
                                    int[] tmp = Sudoku2.POSSIBLE_VALUES[cell2];
                                    step = this.createSubsetStep(
                                            indices[entity][tmp[0] - 1], indices[entity][tmp[1] - 1], -1, -1, cand2, SolutionType.HIDDEN_PAIR, onlyOne, onlyOne
                                    );
                                    if (step != null && onlyOne) {
                                        return step;
                                    }
                                }
                            } else {
                                for (int i3 = i2 + 1; i3 < candArr.length - anz + 3; i3++) {
                                    short cand3 = (short) (cand2 | Sudoku2.MASKS[candArr[i3]]);
                                    short cell3 = (short) (cell2 | this.ipcMask[candArr[i3]]);
                                    if (anz == 3) {
                                        if (Sudoku2.ANZ_VALUES[cell3] == anz) {
                                            int[] tmp = Sudoku2.POSSIBLE_VALUES[cell3];
                                            step = this.createSubsetStep(
                                                    indices[entity][tmp[0] - 1],
                                                    indices[entity][tmp[1] - 1],
                                                    indices[entity][tmp[2] - 1],
                                                    -1,
                                                    cand3,
                                                    SolutionType.HIDDEN_TRIPLE,
                                                    onlyOne,
                                                    onlyOne
                                            );
                                            if (step != null && onlyOne) {
                                                return step;
                                            }
                                        }
                                    } else {
                                        for (int i4 = i3 + 1; i4 < candArr.length; i4++) {
                                            short cand4 = (short) (cand3 | Sudoku2.MASKS[candArr[i4]]);
                                            short cell4 = (short) (cell3 | this.ipcMask[candArr[i4]]);
                                            if (Sudoku2.ANZ_VALUES[cell4] == anz) {
                                                int[] tmp = Sudoku2.POSSIBLE_VALUES[cell4];
                                                step = this.createSubsetStep(
                                                        indices[entity][tmp[0] - 1],
                                                        indices[entity][tmp[1] - 1],
                                                        indices[entity][tmp[2] - 1],
                                                        indices[entity][tmp[3] - 1],
                                                        cand4,
                                                        SolutionType.HIDDEN_QUADRUPLE,
                                                        onlyOne,
                                                        onlyOne
                                                );
                                                if (step != null && onlyOne) {
                                                    return step;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    private SolutionStep findLockedCandidates(SolutionType type) {
        SudokuUtil.clearStepList(this.steps);
        SolutionStep step = null;
        if (type == SolutionType.LOCKED_CANDIDATES || type == SolutionType.LOCKED_CANDIDATES_1) {
            step = this.findLockedCandidatesInEntityN(18, Sudoku2.BLOCKS, true);
            if (step != null) {
                return step;
            }
        }

        if (type == SolutionType.LOCKED_CANDIDATES || type == SolutionType.LOCKED_CANDIDATES_2) {
            step = this.findLockedCandidatesInEntityN(0, Sudoku2.LINES, true);
            if (step != null) {
                return step;
            }

            step = this.findLockedCandidatesInEntityN(9, Sudoku2.COLS, true);
            if (step != null) {
                return step;
            }
        }

        return null;
    }

    protected List<SolutionStep> findAllLockedCandidates() {
        this.sudoku = this.finder.getSudoku();
        List<SolutionStep> oldList = this.steps;
        List<SolutionStep> newList = new ArrayList<>();
        this.steps = newList;
        this.findLockedCandidatesInEntityN(18, Sudoku2.BLOCKS, false);
        this.findLockedCandidatesInEntityN(0, Sudoku2.LINES, false);
        this.findLockedCandidatesInEntityN(9, Sudoku2.COLS, false);
        Collections.sort(this.steps);
        this.steps = oldList;
        return newList;
    }

    private SolutionStep findLockedCandidatesInEntityN(int constraintBase, int[][] indices, boolean onlyOne) {
        SolutionStep step = null;
        byte[][] free = this.sudoku.getFree();

        for (int constr = 0; constr < 9; constr++) {
            for (int cand = 1; cand <= 9; cand++) {
                int unitFree = free[constr + constraintBase][cand];
                if (unitFree == 2 || unitFree == 3) {
                    boolean first = true;
                    this.sameConstraint[0] = this.sameConstraint[1] = this.sameConstraint[2] = true;

                    for (int i = 0; i < indices[constr].length; i++) {
                        int index = indices[constr][i];
                        short cell = this.sudoku.getCell(index);
                        if ((cell & Sudoku2.MASKS[cand]) != 0) {
                            if (first) {
                                this.constraint[0] = Sudoku2.CONSTRAINTS[index][0];
                                this.constraint[1] = Sudoku2.CONSTRAINTS[index][1];
                                this.constraint[2] = Sudoku2.CONSTRAINTS[index][2];
                                first = false;
                            } else {
                                for (int j = 0; j < Sudoku2.CONSTRAINTS[0].length; j++) {
                                    if (this.sameConstraint[j] && this.constraint[j] != Sudoku2.CONSTRAINTS[index][j]) {
                                        this.sameConstraint[j] = false;
                                    }
                                }
                            }
                        }
                    }

                    int skipConstraint = constraintBase + constr;
                    int aktConstraint = -1;
                    if (constraintBase != 18) {
                        if (this.sameConstraint[2] && free[this.constraint[2]][cand] > unitFree) {
                            step = this.createLockedCandidatesStep(SolutionType.LOCKED_CANDIDATES_2, cand, skipConstraint, Sudoku2.ALL_UNITS[this.constraint[2]]);
                            if (onlyOne) {
                                return step;
                            }

                            this.steps.add(step);
                        }
                    } else {
                        if (this.sameConstraint[0] && free[this.constraint[0]][cand] > unitFree) {
                            aktConstraint = this.constraint[0];
                        } else {
                            if (!this.sameConstraint[1] || free[this.constraint[1]][cand] <= unitFree) {
                                continue;
                            }

                            aktConstraint = this.constraint[1];
                        }

                        step = this.createLockedCandidatesStep(SolutionType.LOCKED_CANDIDATES_1, cand, skipConstraint, Sudoku2.ALL_UNITS[aktConstraint]);
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

    private SolutionStep createLockedCandidatesStep(SolutionType type, int cand, int skipConstraint, int[] indices) {
        this.globalStep.reset();
        this.globalStep.setType(type);
        this.globalStep.addValue(cand);
        this.globalStep.setEntity(Sudoku2.CONSTRAINT_TYPE_FROM_CONSTRAINT[skipConstraint]);
        this.globalStep.setEntityNumber(Sudoku2.CONSTRAINT_NUMBER_FROM_CONSTRAINT[skipConstraint]);

        for (int i = 0; i < indices.length; i++) {
            int index = indices[i];
            if ((this.sudoku.getCell(index) & Sudoku2.MASKS[cand]) != 0) {
                if (Sudoku2.CONSTRAINTS[index][0] != skipConstraint
                        && Sudoku2.CONSTRAINTS[index][1] != skipConstraint
                        && Sudoku2.CONSTRAINTS[index][2] != skipConstraint) {
                    this.globalStep.addCandidateToDelete(index, cand);
                } else {
                    this.globalStep.addIndex(index);
                }
            }
        }

        return (SolutionStep) this.globalStep.clone();
    }

    private SolutionStep createSubsetStep(int i1, int i2, int i3, int i4, short cands, SolutionType type, boolean lockedOnly, boolean nakedOnly) {
        if (i4 >= 0) {
            this.indices4[0] = i1;
            this.indices4[1] = i2;
            this.indices4[2] = i3;
            this.indices4[3] = i4;
            return this.createSubsetStep(this.indices4, cands, type, lockedOnly, nakedOnly);
        } else if (i3 >= 0) {
            this.indices3[0] = i1;
            this.indices3[1] = i2;
            this.indices3[2] = i3;
            return this.createSubsetStep(this.indices3, cands, type, lockedOnly, nakedOnly);
        } else {
            this.indices2[0] = i1;
            this.indices2[1] = i2;
            return this.createSubsetStep(this.indices2, cands, type, lockedOnly, nakedOnly);
        }
    }

    private SolutionStep createSubsetStep(int[] indices, short cands, SolutionType type, boolean lockedOnly, boolean nakedHiddenOnly) {
        this.globalStep.reset();
        this.globalStep.setType(type);
        this.sameConstraint[0] = this.sameConstraint[1] = this.sameConstraint[2] = true;
        this.constraint[0] = Sudoku2.CONSTRAINTS[indices[0]][0];
        this.constraint[1] = Sudoku2.CONSTRAINTS[indices[0]][1];
        this.constraint[2] = Sudoku2.CONSTRAINTS[indices[0]][2];

        for (int i = 1; i < indices.length; i++) {
            for (int j = 0; j < Sudoku2.CONSTRAINTS[0].length; j++) {
                if (this.sameConstraint[j] && this.constraint[j] != Sudoku2.CONSTRAINTS[indices[i]][j]) {
                    this.sameConstraint[j] = false;
                }
            }
        }

        int anzFoundConstraints = 0;
        if (type.isHiddenSubset()) {
            for (int i = 0; i < indices.length; i++) {
                short candsToDelete = (short) (this.sudoku.getCell(indices[i]) & ~cands);
                if (candsToDelete != 0) {
                    int[] candArray = Sudoku2.POSSIBLE_VALUES[candsToDelete];

                    for (int k = 0; k < candArray.length; k++) {
                        this.globalStep.addCandidateToDelete(indices[i], candArray[k]);
                    }
                }
            }
        } else {
            this.foundConstraint[0] = this.foundConstraint[1] = this.foundConstraint[2] = false;

            for (int i = 0; i < this.sameConstraint.length; i++) {
                if (this.sameConstraint[i]) {
                    int[] cells = Sudoku2.ALL_UNITS[this.constraint[i]];

                    for (int j = 0; j < cells.length; j++) {
                        boolean skip = false;

                        for (int k = 0; k < indices.length; k++) {
                            if (cells[j] == indices[k]) {
                                skip = true;
                                break;
                            }
                        }

                        if (!skip) {
                            short candsToDelete = (short) (this.sudoku.getCell(cells[j]) & cands);
                            if (candsToDelete != 0) {
                                int[] candArray = Sudoku2.POSSIBLE_VALUES[candsToDelete];

                                for (int k = 0; k < candArray.length; k++) {
                                    this.globalStep.addCandidateToDelete(cells[j], candArray[k]);
                                    if (!this.foundConstraint[i] && (i == 2 || Sudoku2.CONSTRAINTS[cells[j]][2] != this.constraint[2])) {
                                        this.foundConstraint[i] = true;
                                        anzFoundConstraints++;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (this.globalStep.getAnzCandidatesToDelete() == 0) {
            return null;
        }

        boolean isLocked = false;
        if (indices.length < 4
                && anzFoundConstraints > 1
                && !type.isHiddenSubset()
                && (this.sameConstraint[2] && this.sameConstraint[0] || this.sameConstraint[2] && this.sameConstraint[1])) {
            isLocked = true;
        }

        if (isLocked) {
            if (type == SolutionType.NAKED_PAIR) {
                this.globalStep.setType(SolutionType.LOCKED_PAIR);
            }

            if (type == SolutionType.NAKED_TRIPLE) {
                this.globalStep.setType(SolutionType.LOCKED_TRIPLE);
            }
        }

        for (int i = 0; i < indices.length; i++) {
            this.globalStep.addIndex(indices[i]);
        }

        int[] candArray = Sudoku2.POSSIBLE_VALUES[cands];

        for (int i = 0; i < candArray.length; i++) {
            this.globalStep.addValue(candArray[i]);
        }

        SolutionStep step = (SolutionStep) this.globalStep.clone();
        if (lockedOnly && !nakedHiddenOnly) {
            if (!isLocked) {
                this.cachedSteps.add(step);
                step = null;
            }
        } else if (nakedHiddenOnly && !lockedOnly) {
            if (isLocked) {
                this.cachedSteps.add(step);
                step = null;
            }
        } else if (!lockedOnly && !nakedHiddenOnly) {
            this.steps.add(step);
        }

        return step;
    }
}
