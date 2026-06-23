package solver;

import sudoku.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UniquenessSolver extends AbstractSolver {
    private int[] bugConstraints = new int[3];
    private SolutionStep globalStep = new SolutionStep(SolutionType.FULL_HOUSE);
    private List<SolutionStep> steps = new ArrayList<>();
    private int[] rectangles = new int[400];
    private int rectAnz = 0;
    private int[] indexe = new int[4];
    private int[] tmpRect = new int[4];
    private int cand1;
    private int cand2;
    private List<SolutionStep> cachedSteps = new ArrayList<>();
    private int stepNumber = -1;
    private SudokuSet twoCandidates = new SudokuSet();
    private SudokuSet additionalCandidates = new SudokuSet();
    private SudokuSet tmpSet = new SudokuSet();
    private SudokuSet tmpSet1 = new SudokuSet();
    private boolean lastSearchWasUR = false;
    private boolean lastSearchWasAR = false;

    public UniquenessSolver(SudokuStepFinder finder) {
        super(finder);
    }

    public static void main(String[] args) {
        Sudoku2 sudoku = new Sudoku2();
        sudoku.setSudoku(":0000:x:..513.9.2..1..97..89..2.4..45.29.136962351847.1...6..9.349...78...8..39..89.736..:625 271 571 485::");
        sudoku.setSudoku(
                ":0000:x:+41+67+8.5...35..4+8+178+7.+13+5.+46..4+5716..+1.3+4..7.+5+6+5789+34+2+15+4..1+7.+683.+82+5.17+4+7.1.+48.5.:942 948 252 297 399::"
        );
        sudoku.setSudoku(
                ":0610::8+4+9325+6+1+77+3+2+8..9+4+5+5+6+1+7..+32+8+49327+85+61+1+5+7.3.8+9+2+2+86...+4+7+3+9+7+8..+213+4+3+149+87+2566+2+5..+3+7+8+9::565 965::"
        );
        sudoku.setSudoku(
                ":0610::+1+4.+7+8...+9+2+8.+4+5.1.73+7.6+1...+89+5+38+7+1..+2+72+4+9+6+581+3+86+1+324+9+7+5+6+18+2+3759+4+5+971+4+832+64+3+2596+7+8+1::318 518::"
        );
        sudoku.setSudoku(
                ":0600:13:+5+2+3+9+7+8+1466+1+832+4...+4+7+9+5+6+1+38+29+5+1+6+32.+7.+86+2+49+7+5..+7+34+18526+9+38+7+21....295+8+4....+14+67+5..+2.::189 389::"
        );
        sudoku.setSudoku(
                ":0601:12:....6..2.394...7..2.+6..41....2....4..6.8........6.9....4...739...1.8...2.+2....51.:114 116 819 829 839 849 869 574 575 581 582 495:514 516 526 528 529 534 535::"
        );
        sudoku.setSudoku(
                ":0602:15:+7+1+3+4+5+862+9+4927+63..+8+8+6+51+9+2374+67158+9+4+3+2+9+5+42+3+7+8+612+386+4+1+7+9+5.8..+1.+24+71+47.+2.....+2..+74...::391::"
        );
        sudoku.setSudoku(
                ":0602:16:..3.+1+6.9...78....6.6......1....+8.......45....45..61..7..81.926.9....3........8.1.:514 225 425 226 334 534 934 235 435 735 236 736 244 744 347 447 547 947 248 348 249 349 949 357 857 957 858 859 264 284 784 294 794:233 241 242 251 252 283 293 933 942 952::"
        );
        sudoku.setSudoku(
                ":0602:57:+54.........72+4.5..9.....+463...964.....5...+6........1.281...7........5+7867+5.3.....:213 613 714 715 626 134 834 135 835 151 251 252 663 568 373 195 196:355 356 361 362 363 368 854 855 856 862 863:"
        );
        sudoku.setSudoku(
                ":0603:48:+3+9+6+17+52+8481+4+29+63..5+27.+8.+1+6+9..+5+9.2..1+2.38.19..9.+16.+7...+1.+2+7+6.5+98..8.2+9+7137.9.1+8...:448 465 468 492 692 494:442 447::"
        );
        sudoku.setSudoku(
                ":0608:12:......+531.96...7+425.....+6+9+8......8..7.94..+1.+3....329..+9..+1+28+3+65+832.5.+41+9+6+51+3+49+287:814 815 141 142:714 715 716 732 733 736::"
        );
        sudoku.setSudoku(
                ":0606:34:.51.+8..4+2....2.3...........3..8.+29.....5.4......1.+9+4.....7+5+6+214.6.+29....2........:432 732 832 932 433 633 733 833 933 334 335 635 735 336 881 383 883 392 492 892 992 393 493 893 993 395:435::"
        );
        SudokuSolver solver = SudokuSolverFactory.getDefaultSolverInstance();
        SudokuStepFinder finder = solver.getStepFinder();
        boolean singleHint = false;
        if (singleHint) {
            finder.setSudoku(sudoku);
            SolutionStep step = finder.getStep(SolutionType.BUG_PLUS_1);
            System.out.println(step);
        } else {
            List<SolutionStep> steps = solver.getStepFinder().getAllUniqueness(sudoku);
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
        if (this.sudoku.getStatus() != SudokuStatus.VALID) {
            return null;
        }

        switch (type) {
            case UNIQUENESS_1:
            case UNIQUENESS_2:
            case UNIQUENESS_3:
            case UNIQUENESS_4:
            case UNIQUENESS_5:
            case UNIQUENESS_6:
            case HIDDEN_RECTANGLE:
                result = this.getUniqueness(type);
                break;
            case AVOIDABLE_RECTANGLE_1:
            case AVOIDABLE_RECTANGLE_2:
                if (this.sudoku.getStatusGivens() != SudokuStatus.VALID) {
                    return null;
                }

                result = this.getAvoidableRectangle(type);
                break;
            case BUG_PLUS_1:
                result = this.getBugPlus1();
        }

        return result;
    }

    @Override
    protected boolean doStep(SolutionStep step) {
        boolean handled = true;
        this.sudoku = this.finder.getSudoku();
        switch (step.getType()) {
            case UNIQUENESS_1:
            case UNIQUENESS_2:
            case UNIQUENESS_3:
            case UNIQUENESS_4:
            case UNIQUENESS_5:
            case UNIQUENESS_6:
            case HIDDEN_RECTANGLE:
            case AVOIDABLE_RECTANGLE_1:
            case AVOIDABLE_RECTANGLE_2:
            case BUG_PLUS_1:
                if (step.getCandidatesToDelete().isEmpty()) {
                    System.out.println("ERROR: No candidate to delete!");
                    System.out.println(step.toString(2));
                    System.out.println(this.sudoku.getSudoku(ClipboardMode.LIBRARY));
                }

                for (Candidate cand : step.getCandidatesToDelete()) {
                    if (!this.sudoku.isCandidate(cand.getIndex(), cand.getValue())) {
                        System.out.println("ERROR: " + cand.getIndex() + "/" + cand.getValue());
                        System.out.println(step.toString(2));
                        System.out.println(this.sudoku.getSudoku(ClipboardMode.LIBRARY));
                    }

                    this.sudoku.delCandidate(cand.getIndex(), cand.getValue());
                }
                break;
            default:
                handled = false;
        }

        return handled;
    }

    private SolutionStep getUniqueness(SolutionType type) {
        if (this.finder.getStepNumber() != this.stepNumber || !this.lastSearchWasUR) {
            this.stepNumber = this.finder.getStepNumber();
            this.cachedSteps.clear();
            this.rectAnz = 0;
        } else if (this.cachedSteps.size() > 0) {
            for (SolutionStep step : this.cachedSteps) {
                if (step.getType() == type) {
                    return step;
                }
            }
        }

        this.lastSearchWasUR = true;
        this.lastSearchWasAR = false;
        return this.getAllUniquenessInternal(type, true);
    }

    private SolutionStep getAvoidableRectangle(SolutionType type) {
        if (this.finder.getStepNumber() != this.stepNumber || !this.lastSearchWasAR) {
            this.stepNumber = this.finder.getStepNumber();
            this.cachedSteps.clear();
            this.rectAnz = 0;
        } else if (this.cachedSteps.size() > 0) {
            for (SolutionStep step : this.cachedSteps) {
                if (step.getType() == type) {
                    return step;
                }
            }
        }

        this.lastSearchWasUR = false;
        this.lastSearchWasAR = true;
        return this.getAllAvoidableRectangles(type, true);
    }

    protected List<SolutionStep> getAllUniqueness() {
        this.stepNumber = -1;
        this.cachedSteps.clear();
        this.rectAnz = 0;
        this.lastSearchWasAR = false;
        this.lastSearchWasUR = false;
        this.sudoku = this.finder.getSudoku();
        List<SolutionStep> tmpSteps = new ArrayList<>();
        List<SolutionStep> oldSteps = this.steps;
        this.steps = tmpSteps;
        this.getAllUniquenessInternal(null, false);
        this.getAllAvoidableRectangles(null, false);
        this.steps = oldSteps;
        return tmpSteps;
    }

    private SolutionStep getAllAvoidableRectangles(SolutionType type, boolean onlyOne) {
        for (int i = 0; i < 81; i++) {
            if (this.sudoku.getValue(i) != 0 && !this.sudoku.isFixed(i)) {
                this.cand1 = this.sudoku.getValue(i);
                SolutionStep step = this.findUniquenessForStartCell(i, true, type, onlyOne);
                if (step != null && onlyOne) {
                    return step;
                }

                step = this.findUniquenessForStartCell(i, true, type, onlyOne);
                if (step != null && onlyOne) {
                    return step;
                }
            }
        }

        return null;
    }

    private SolutionStep getAllUniquenessInternal(SolutionType type, boolean onlyOne) {
        for (int i = 0; i < 81; i++) {
            if (this.sudoku.getAnzCandidates(i) == 2) {
                int[] cands = this.sudoku.getAllCandidates(i);
                this.cand1 = cands[0];
                this.cand2 = cands[1];
                SolutionStep step = this.findUniquenessForStartCell(i, false, type, onlyOne);
                if (step != null && onlyOne) {
                    return step;
                }
            }
        }

        return null;
    }

    private SolutionStep getBugPlus1() {
        int index3 = -1;

        for (int i = 0; i < 81; i++) {
            int anz = this.sudoku.getAnzCandidates(i);
            if (anz > 3) {
                return null;
            }

            if (anz == 3) {
                if (index3 != -1) {
                    return null;
                }

                index3 = i;
            }
        }

        if (index3 == -1) {
            return null;
        }

        int cand3 = -1;
        this.bugConstraints[0] = -1;
        this.bugConstraints[1] = -1;
        this.bugConstraints[2] = -1;
        byte[][] free = this.sudoku.getFree();

        for (int constr = 0; constr < free.length; constr++) {
            for (int cand = 1; cand <= 9; cand++) {
                int anz = free[constr][cand];
                if (anz > 3) {
                    return null;
                }

                if (anz == 3) {
                    if (this.bugConstraints[constr / 9] != -1 || cand3 != -1 && cand3 != cand) {
                        return null;
                    }

                    cand3 = cand;
                    this.bugConstraints[constr / 9] = constr;
                }
            }
        }

        if (this.sudoku.isCandidate(index3, cand3)
                && Sudoku2.CONSTRAINTS[index3][0] == this.bugConstraints[0]
                && Sudoku2.CONSTRAINTS[index3][1] == this.bugConstraints[1]
                && Sudoku2.CONSTRAINTS[index3][2] == this.bugConstraints[2]) {
            this.globalStep.reset();
            this.globalStep.setType(SolutionType.BUG_PLUS_1);
            int[] candArr = this.sudoku.getAllCandidates(index3);

            for (int i = 0; i < candArr.length; i++) {
                if (candArr[i] != cand3) {
                    this.globalStep.addCandidateToDelete(index3, candArr[i]);
                }
            }

            return (SolutionStep) this.globalStep.clone();
        } else {
            return null;
        }
    }

    private SolutionStep findUniquenessForStartCell(int index11, boolean avoidable, SolutionType type, boolean onlyOne) {
        boolean allowMissing = Options.getInstance().isAllowUniquenessMissingCandidates();
        int line11 = Sudoku2.getLine(index11);
        int col11 = Sudoku2.getCol(index11);
        int block11 = Sudoku2.getBlock(index11);
        int cell11 = this.sudoku.getCell(index11);
        SudokuSet allowedCand1 = this.finder.getCandidatesAllowed()[this.cand1];
        SudokuSet allowedCand2 = this.finder.getCandidatesAllowed()[this.cand2];
        int[] blockIndices = Sudoku2.BLOCKS[Sudoku2.getBlock(index11)];

        for (int i = 0; i < blockIndices.length; i++) {
            if (blockIndices[i] != index11) {
                int index12 = blockIndices[i];
                if (line11 == Sudoku2.getLine(index12) || col11 == Sudoku2.getCol(index12)) {
                    int cell12 = this.sudoku.getCell(index12);
                    if (!avoidable
                            && this.sudoku.getValue(index12) == 0
                            && (!allowMissing && (cell11 & cell12) == cell11 || allowMissing && allowedCand1.contains(index12) && allowedCand2.contains(index12))
                            || avoidable && this.sudoku.getValue(index12) != 0 && !this.sudoku.isFixed(index12)) {
                        if (avoidable) {
                            this.cand2 = this.sudoku.getValue(index12);
                        }

                        boolean isLines = line11 == Sudoku2.getLine(index12);
                        int[] unit11 = Sudoku2.ALL_UNITS[isLines ? Sudoku2.getCol(index11) + 9 : Sudoku2.getLine(index11)];
                        int[] unit12 = Sudoku2.ALL_UNITS[isLines ? Sudoku2.getCol(index12) + 9 : Sudoku2.getLine(index12)];

                        for (int j = 0; j < unit11.length; j++) {
                            if (Sudoku2.getBlock(unit11[j]) != block11) {
                                int index21 = unit11[j];
                                int index22 = unit12[j];
                                int cell21 = this.sudoku.getCell(index21);
                                int cell22 = this.sudoku.getCell(index22);
                                if ((
                                        !avoidable && !allowMissing && (cell21 & cell11) == cell11 && (cell22 & cell11) == cell11
                                                || allowMissing
                                                && allowedCand1.contains(index21)
                                                && allowedCand1.contains(index22)
                                                && allowedCand2.contains(index22)
                                                && allowedCand2.contains(index22)
                                                || avoidable
                                                && (
                                                this.sudoku.getValue(index21) == this.cand2
                                                        && !this.sudoku.isFixed(index21)
                                                        && this.sudoku.getValue(index22) == 0
                                                        && this.sudoku.isCandidate(index22, this.cand1)
                                                        && this.sudoku.getAnzCandidates(index22) == 2
                                                        || this.sudoku.getValue(index22) == this.cand1
                                                        && !this.sudoku.isFixed(index22)
                                                        && this.sudoku.getValue(index21) == 0
                                                        && this.sudoku.isCandidate(index21, this.cand2)
                                                        && this.sudoku.getAnzCandidates(index21) == 2
                                                        || this.sudoku.getValue(index21) == 0
                                                        && this.sudoku.isCandidate(index21, this.cand2)
                                                        && this.sudoku.getAnzCandidates(index21) == 2
                                                        && this.sudoku.getValue(index22) == 0
                                                        && this.sudoku.isCandidate(index22, this.cand1)
                                                        && this.sudoku.getAnzCandidates(index22) == 2
                                        )
                                )
                                        && this.checkRect(index11, index12, index21, index22)) {
                                    this.indexe[0] = index11;
                                    this.indexe[1] = index12;
                                    this.indexe[2] = index21;
                                    this.indexe[3] = index22;
                                    SolutionStep step = null;
                                    if (avoidable) {
                                        step = this.checkAvoidableRectangle(index21, index22, type, onlyOne);
                                    } else {
                                        step = this.checkURForStep(type, onlyOne);
                                    }

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

        return null;
    }

    private SolutionStep checkURForStep(SolutionType searchType, boolean onlyOne) {
        this.initCheck(this.indexe);
        SolutionStep step = null;
        int twoSize = this.twoCandidates.size();
        short urMask = (short) (Sudoku2.MASKS[this.cand1] | Sudoku2.MASKS[this.cand2]);
        if (twoSize == 3) {
            this.initStep(SolutionType.UNIQUENESS_1);
            int delIndex = this.additionalCandidates.get(0);
            if (this.sudoku.isCandidate(delIndex, this.cand1)) {
                this.globalStep.addCandidateToDelete(delIndex, this.cand1);
            }

            if (this.sudoku.isCandidate(delIndex, this.cand2)) {
                this.globalStep.addCandidateToDelete(delIndex, this.cand2);
            }

            if (this.globalStep.getCandidatesToDelete().size() > 0) {
                step = (SolutionStep) this.globalStep.clone();
                if (onlyOne) {
                    if (searchType == step.getType()) {
                        return step;
                    }

                    this.cachedSteps.add(step);
                } else {
                    this.steps.add(step);
                }
            }
        }

        if (twoSize == 2 || twoSize == 1) {
            short addMask = 0;
            this.tmpSet.setAll();

            for (int i = 0; i < this.additionalCandidates.size(); i++) {
                int index3 = this.additionalCandidates.get(i);
                addMask |= (short) (this.sudoku.getCell(index3) & ~urMask);
                if (Sudoku2.ANZ_VALUES[addMask] > 1) {
                    break;
                }

                this.tmpSet.and(Sudoku2.buddies[index3]);
            }

            if (Sudoku2.ANZ_VALUES[addMask] == 1) {
                int addCand = Sudoku2.CAND_FROM_MASK[addMask];
                this.tmpSet.and(this.finder.getCandidates()[addCand]);
                if (!this.tmpSet.isEmpty()) {
                    SolutionType type = SolutionType.UNIQUENESS_2;
                    int i1 = this.additionalCandidates.get(0);
                    int i2 = this.additionalCandidates.get(1);
                    if (this.additionalCandidates.size() == 3 || Sudoku2.getLine(i1) != Sudoku2.getLine(i2) && Sudoku2.getCol(i1) != Sudoku2.getCol(i2)) {
                        type = SolutionType.UNIQUENESS_5;
                    }

                    this.initStep(type);

                    for (int i = 0; i < this.tmpSet.size(); i++) {
                        this.globalStep.addCandidateToDelete(this.tmpSet.get(i), addCand);
                    }

                    step = (SolutionStep) this.globalStep.clone();
                    if (onlyOne) {
                        if (searchType == step.getType()) {
                            return step;
                        }

                        this.cachedSteps.add(step);
                    } else {
                        this.steps.add(step);
                    }
                }
            }
        }

        if (twoSize == 2) {
            short u3Cands = 0;

            for (int i = 0; i < this.additionalCandidates.size(); i++) {
                int index3 = this.additionalCandidates.get(i);
                u3Cands |= (short) (this.sudoku.getCell(index3) & ~urMask);
            }

            int i1 = this.additionalCandidates.get(0);
            int i2 = this.additionalCandidates.get(1);
            if (Sudoku2.getLine(i1) == Sudoku2.getLine(i2)) {
                step = this.checkUniqueness3(1, Sudoku2.LINES[Sudoku2.getLine(i1)], u3Cands, urMask, searchType, onlyOne);
                if (step != null && onlyOne) {
                    return step;
                }
            }

            if (Sudoku2.getCol(i1) == Sudoku2.getCol(i2)) {
                step = this.checkUniqueness3(2, Sudoku2.COLS[Sudoku2.getCol(i1)], u3Cands, urMask, searchType, onlyOne);
                if (step != null && onlyOne) {
                    return step;
                }
            }

            if (Sudoku2.getBlock(i1) == Sudoku2.getBlock(i2)) {
                step = this.checkUniqueness3(0, Sudoku2.BLOCKS[Sudoku2.getBlock(i1)], u3Cands, urMask, searchType, onlyOne);
                if (step != null && onlyOne) {
                    return step;
                }
            }
        }

        if (twoSize == 2) {
            int i1 = this.additionalCandidates.get(0);
            int i2 = this.additionalCandidates.get(1);
            if (Sudoku2.getLine(i1) == Sudoku2.getLine(i2) || Sudoku2.getCol(i1) == Sudoku2.getCol(i2)) {
                this.tmpSet.setAnd(Sudoku2.buddies[i1], Sudoku2.buddies[i2]);
                int delCand = -1;
                this.tmpSet1.setAnd(this.tmpSet, this.finder.getCandidates()[this.cand1]);
                if (this.tmpSet1.isEmpty()) {
                    delCand = this.cand2;
                } else {
                    this.tmpSet1.setAnd(this.tmpSet, this.finder.getCandidates()[this.cand2]);
                    if (this.tmpSet1.isEmpty()) {
                        delCand = this.cand1;
                    }
                }

                if (delCand != -1) {
                    this.initStep(SolutionType.UNIQUENESS_4);
                    if (this.sudoku.isCandidate(i1, delCand)) {
                        this.globalStep.addCandidateToDelete(i1, delCand);
                    }

                    if (this.sudoku.isCandidate(i2, delCand)) {
                        this.globalStep.addCandidateToDelete(i2, delCand);
                    }

                    if (this.globalStep.getCandidatesToDelete().size() > 0) {
                        step = (SolutionStep) this.globalStep.clone();
                        if (onlyOne) {
                            if (searchType == step.getType()) {
                                return step;
                            }

                            this.cachedSteps.add(step);
                        } else {
                            this.steps.add(step);
                        }
                    }
                }
            }
        }

        if (twoSize == 2) {
            int i1 = this.additionalCandidates.get(0);
            int i2 = this.additionalCandidates.get(1);
            if (Sudoku2.getLine(i1) != Sudoku2.getLine(i2) && Sudoku2.getCol(i1) != Sudoku2.getCol(i2)) {
                this.tmpSet.set(Sudoku2.LINE_TEMPLATES[Sudoku2.getLine(i1)]);
                this.tmpSet.or(Sudoku2.COL_TEMPLATES[Sudoku2.getCol(i1)]);
                this.tmpSet.or(Sudoku2.LINE_TEMPLATES[Sudoku2.getLine(i2)]);
                this.tmpSet.or(Sudoku2.COL_TEMPLATES[Sudoku2.getCol(i2)]);
                this.tmpSet.andNot(this.additionalCandidates);
                this.tmpSet.andNot(this.twoCandidates);
                int delCand = -1;
                this.tmpSet1.setAnd(this.tmpSet, this.finder.getCandidates()[this.cand1]);
                if (this.tmpSet1.isEmpty()) {
                    delCand = this.cand1;
                } else {
                    this.tmpSet1.setAnd(this.tmpSet, this.finder.getCandidates()[this.cand2]);
                    if (this.tmpSet1.isEmpty()) {
                        delCand = this.cand2;
                    }
                }

                if (delCand != -1) {
                    this.initStep(SolutionType.UNIQUENESS_6);
                    if (this.sudoku.isCandidate(i1, delCand)) {
                        this.globalStep.addCandidateToDelete(i1, delCand);
                    }

                    if (this.sudoku.isCandidate(i2, delCand)) {
                        this.globalStep.addCandidateToDelete(i2, delCand);
                    }

                    if (this.globalStep.getCandidatesToDelete().size() > 0) {
                        step = (SolutionStep) this.globalStep.clone();
                        if (onlyOne) {
                            if (searchType == step.getType()) {
                                return step;
                            }

                            this.cachedSteps.add(step);
                        } else {
                            this.steps.add(step);
                        }
                    }
                }
            }
        }

        if (twoSize == 2 || twoSize == 1) {
            int i1 = this.twoCandidates.get(0);
            int i2 = this.twoCandidates.get(1);
            boolean doCheck = true;
            if (twoSize == 2 && (Sudoku2.getLine(i1) == Sudoku2.getLine(i2) || Sudoku2.getCol(i1) == Sudoku2.getCol(i2))) {
                doCheck = false;
            }

            if (doCheck) {
                step = this.checkHiddenRectangle(i1, searchType, onlyOne);
                if (step != null && onlyOne) {
                    return step;
                }

                if (this.twoCandidates.size() == 2) {
                    step = this.checkHiddenRectangle(i2, searchType, onlyOne);
                    if (step != null && onlyOne) {
                        return step;
                    }
                }
            }
        }

        return null;
    }

    private SolutionStep checkAvoidableRectangle(int index21, int index22, SolutionType type, boolean onlyOne) {
        SolutionStep step = null;
        if (this.sudoku.getValue(index21) == 0 && this.sudoku.getValue(index22) == 0) {
            int[] cands = this.sudoku.getAllCandidates(index21);
            int additionalCand = cands[0];
            if (additionalCand == this.cand2) {
                additionalCand = cands[1];
            }

            if (!this.sudoku.isCandidate(index22, additionalCand)) {
                return null;
            }

            this.tmpSet.set(Sudoku2.buddies[index21]);
            this.tmpSet.and(Sudoku2.buddies[index22]);
            this.tmpSet.and(this.finder.getCandidates()[additionalCand]);
            if (this.tmpSet.isEmpty()) {
                return null;
            }

            this.initStep(SolutionType.AVOIDABLE_RECTANGLE_2);

            for (int i = 0; i < this.tmpSet.size(); i++) {
                this.globalStep.addCandidateToDelete(this.tmpSet.get(i), additionalCand);
            }

            this.globalStep.addEndoFin(index21, additionalCand);
            this.globalStep.addEndoFin(index22, additionalCand);
            step = (SolutionStep) this.globalStep.clone();
            if (onlyOne) {
                if (type == SolutionType.AVOIDABLE_RECTANGLE_2) {
                    return step;
                }

                this.cachedSteps.add(step);
            } else {
                this.steps.add(step);
            }
        } else {
            this.initStep(SolutionType.AVOIDABLE_RECTANGLE_1);
            if (this.sudoku.getValue(index21) != 0) {
                if (this.sudoku.isCandidate(index22, this.cand1)) {
                    this.globalStep.addCandidateToDelete(index22, this.cand1);
                }
            } else if (this.sudoku.isCandidate(index21, this.cand2)) {
                this.globalStep.addCandidateToDelete(index21, this.cand2);
            }

            if (this.globalStep.getCandidatesToDelete().size() > 0) {
                step = (SolutionStep) this.globalStep.clone();
                if (onlyOne) {
                    if (type == SolutionType.AVOIDABLE_RECTANGLE_1) {
                        return step;
                    }

                    this.cachedSteps.add(step);
                } else {
                    this.steps.add(step);
                }
            }
        }

        return null;
    }

    private SolutionStep checkHiddenRectangle(int cornerIndex, SolutionType type, boolean onlyOne) {
        int lineC = Sudoku2.getLine(cornerIndex);
        int colC = Sudoku2.getCol(cornerIndex);
        int i1 = this.additionalCandidates.get(0);
        int i2 = this.additionalCandidates.get(1);
        int line1 = Sudoku2.getLine(i1);
        if (line1 == lineC) {
            line1 = Sudoku2.getLine(i2);
        }

        int col1 = Sudoku2.getCol(i1);
        if (col1 == colC) {
            col1 = Sudoku2.getCol(i2);
        }

        SolutionStep step = this.checkCandForHiddenRectangle(line1, col1, this.cand1, this.cand2, type, onlyOne);
        if (step != null && onlyOne) {
            return step;
        }

        step = this.checkCandForHiddenRectangle(line1, col1, this.cand2, this.cand1, type, onlyOne);
        return step != null && onlyOne ? step : null;
    }

    private SolutionStep checkCandForHiddenRectangle(int line, int col, int cand1, int cand2, SolutionType type, boolean onlyOne) {
        this.tmpSet1.setOr(this.twoCandidates, this.additionalCandidates);
        this.tmpSet.set(this.finder.getCandidates()[cand1]);
        this.tmpSet.and(Sudoku2.LINE_TEMPLATES[line]);
        this.tmpSet.andNot(this.tmpSet1);
        if (!this.tmpSet.isEmpty()) {
            return null;
        }

        this.tmpSet.set(this.finder.getCandidates()[cand1]);
        this.tmpSet.and(Sudoku2.COL_TEMPLATES[col]);
        this.tmpSet.andNot(this.tmpSet1);
        if (!this.tmpSet.isEmpty()) {
            return null;
        }

        int delIndex = Sudoku2.getIndex(line, col);
        this.initStep(SolutionType.HIDDEN_RECTANGLE);
        if (this.sudoku.isCandidate(delIndex, cand2)) {
            this.globalStep.addCandidateToDelete(delIndex, cand2);
        }

        if (this.globalStep.getCandidatesToDelete().size() > 0) {
            SolutionStep step = (SolutionStep) this.globalStep.clone();
            if (onlyOne) {
                if (type == step.getType()) {
                    return step;
                }

                this.cachedSteps.add(step);
            } else {
                this.steps.add(step);
            }
        }

        return null;
    }

    private SolutionStep checkUniqueness3(int unitType, int[] unit, short u3Cands, short urMask, SolutionType searchType, boolean onlyOne) {
        SudokuSet u3Indices = new SudokuSet();
        this.tmpSet.set(this.twoCandidates);
        this.tmpSet.or(this.additionalCandidates);

        for (int i = 0; i < unit.length; i++) {
            short cell = this.sudoku.getCell(unit[i]);
            if (cell != 0 && (cell & urMask) == 0 && !this.tmpSet.contains(unit[i])) {
                u3Indices.add(unit[i]);
            }
        }

        if (!u3Indices.isEmpty()) {
            SolutionStep step = this.checkUniqueness3Recursive(
                    unitType, unit, u3Indices, u3Cands, new SudokuSet(this.additionalCandidates), 0, searchType, onlyOne
            );
            if (step != null && onlyOne) {
                return step;
            }
        }

        return null;
    }

    private SolutionStep checkUniqueness3Recursive(
            int type, int[] unit, SudokuSet u3Indices, short candsIncluded, SudokuSet indicesIncluded, int startIndex, SolutionType searchType, boolean onlyOne
    ) {
        SolutionStep step = null;

        for (int i = startIndex; i < u3Indices.size(); i++) {
            short aktCands = candsIncluded;
            SudokuSet aktIndices = indicesIncluded.clone();
            aktIndices.add(u3Indices.get(i));
            aktCands = (short) (aktCands | this.sudoku.getCell(u3Indices.get(i)));
            if ((type != 0 || !this.isSameLineOrCol(aktIndices)) && Sudoku2.ANZ_VALUES[aktCands] == aktIndices.size() - 1) {
                this.initStep(SolutionType.UNIQUENESS_3);

                for (int j = 0; j < unit.length; j++) {
                    if (this.sudoku.getValue(unit[j]) == 0 && !aktIndices.contains(unit[j])) {
                        short delCands = (short) (this.sudoku.getCell(unit[j]) & aktCands);
                        if (Sudoku2.ANZ_VALUES[delCands] != 0) {
                            int[] delCandsArray = Sudoku2.POSSIBLE_VALUES[delCands];

                            for (int k = 0; k < delCandsArray.length; k++) {
                                this.globalStep.addCandidateToDelete(unit[j], delCandsArray[k]);
                            }
                        }
                    }
                }

                if (this.globalStep.getCandidatesToDelete().size() > 0) {
                    int[] aktCandsArray = Sudoku2.POSSIBLE_VALUES[aktCands];

                    for (int k = 0; k < aktCandsArray.length; k++) {
                        int cTmp = aktCandsArray[k];

                        for (int l = 0; l < aktIndices.size(); l++) {
                            if (this.sudoku.isCandidate(aktIndices.get(l), cTmp)) {
                                this.globalStep.addFin(aktIndices.get(l), cTmp);
                            }
                        }
                    }

                    if (type == 1 || type == 2) {
                        int block = this.getBlockForCheck3(aktIndices);
                        if (block != -1) {
                            int[] unit1 = Sudoku2.BLOCKS[block];

                            for (int j = 0; j < unit1.length; j++) {
                                if (this.sudoku.getValue(unit1[j]) == 0 && !aktIndices.contains(unit1[j])) {
                                    short delCands = (short) (this.sudoku.getCell(unit1[j]) & aktCands);
                                    if (Sudoku2.ANZ_VALUES[delCands] != 0) {
                                        int[] delCandsArray = Sudoku2.POSSIBLE_VALUES[delCands];

                                        for (int k = 0; k < delCandsArray.length; k++) {
                                            this.globalStep.addCandidateToDelete(unit1[j], delCandsArray[k]);
                                        }
                                    }
                                }
                            }
                        }
                    }

                    step = (SolutionStep) this.globalStep.clone();
                    if (onlyOne) {
                        if (searchType == step.getType()) {
                            return step;
                        }

                        this.cachedSteps.add(step);
                    } else {
                        this.steps.add(step);
                    }
                }
            }

            step = this.checkUniqueness3Recursive(type, unit, u3Indices, aktCands, aktIndices, i + 1, searchType, onlyOne);
            if (step != null && onlyOne) {
                return step;
            }
        }

        return null;
    }

    private int getBlockForCheck3(SudokuSet aktIndices) {
        if (aktIndices.isEmpty()) {
            return -1;
        }

        int block = Sudoku2.getBlock(aktIndices.get(0));

        for (int i = 1; i < aktIndices.size(); i++) {
            if (Sudoku2.getBlock(aktIndices.get(i)) != block) {
                return -1;
            }
        }

        return block;
    }

    private boolean isSameLineOrCol(SudokuSet aktIndices) {
        if (aktIndices.isEmpty()) {
            return false;
        }

        boolean sameLine = true;
        boolean sameCol = true;
        int line = Sudoku2.getLine(aktIndices.get(0));
        int col = Sudoku2.getCol(aktIndices.get(0));

        for (int i = 1; i < aktIndices.size(); i++) {
            if (Sudoku2.getLine(aktIndices.get(i)) != line) {
                sameLine = false;
            }

            if (Sudoku2.getCol(aktIndices.get(i)) != col) {
                sameCol = false;
            }
        }

        return sameLine || sameCol;
    }

    private void initStep(SolutionType type) {
        this.globalStep.reset();
        this.globalStep.setType(type);
        if (this.indexe != null) {
            this.globalStep.addValue(this.cand1);
            this.globalStep.addValue(this.cand2);
            this.globalStep.addIndex(this.indexe[0]);
            this.globalStep.addIndex(this.indexe[1]);
            this.globalStep.addIndex(this.indexe[2]);
            this.globalStep.addIndex(this.indexe[3]);
        }
    }

    private void initCheck(int[] indices) {
        this.twoCandidates.clear();
        this.additionalCandidates.clear();
        short mask = (short) (~(Sudoku2.MASKS[this.cand1] | Sudoku2.MASKS[this.cand2]));

        for (int i = 0; i < indices.length; i++) {
            short addTemp = (short) (this.sudoku.getCell(indices[i]) & mask);
            if (addTemp == 0) {
                this.twoCandidates.add(indices[i]);
            } else {
                this.additionalCandidates.add(indices[i]);
            }
        }
    }

    private boolean checkRect(int i11, int i12, int i21, int i22) {
        this.tmpRect[0] = i11;
        this.tmpRect[1] = i12;
        this.tmpRect[2] = i21;
        this.tmpRect[3] = i22;

        for (int i = this.tmpRect.length; i > 1; i--) {
            boolean changed = false;

            for (int j = 1; j < i; j++) {
                if (this.tmpRect[j - 1] > this.tmpRect[j]) {
                    int tmp = this.tmpRect[j - 1];
                    this.tmpRect[j - 1] = this.tmpRect[j];
                    this.tmpRect[j] = tmp;
                    changed = true;
                }
            }

            if (!changed) {
                break;
            }
        }

        int rect = ((this.tmpRect[0] * 10 + this.tmpRect[1]) * 10 + this.tmpRect[2]) * 10 + this.tmpRect[3];

        for (int i = 0; i < this.rectAnz; i++) {
            if (this.rectangles[i] == rect) {
                return false;
            }
        }

        if (this.rectAnz < this.rectangles.length) {
            this.rectangles[this.rectAnz++] = rect;
        } else {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Find Uniqueness: Kein Platz mehr in rectangles!");
        }

        return true;
    }
}
