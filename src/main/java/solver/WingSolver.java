package solver;

import sudoku.*;

import java.util.ArrayList;
import java.util.List;

public class WingSolver extends AbstractSolver {
    private SolutionStep globalStep = new SolutionStep(SolutionType.FULL_HOUSE);
    private List<SolutionStep> steps = new ArrayList<>();
    private SudokuSet preCalcSet1 = new SudokuSet();
    private SudokuSet preCalcSet2 = new SudokuSet();
    private SudokuSet elimSet = new SudokuSet();
    private int[] biCells = new int[81];
    private int[] triCells = new int[81];
    private int wIndex1 = -1;
    private int wIndex2 = -1;

    public WingSolver(SudokuStepFinder finder) {
        super(finder);
    }

    public static void main(String[] args) {
        Sudoku2 sudoku = new Sudoku2();
        sudoku.setSudoku(
                ":0803:14:6..+9+5..7...+9.2.....58.+31...+1+64+3+8+9+7+52...1+7+59+46597+24+6..892+54+1+76+8+3...5+6+2.....68+93...::417 427 437 489 499::"
        );
        sudoku.setSudoku(
                ":0800:123:..+8+2..57.+7.+54....+8..9+8+57...4+5+17+2+98+6+3+2765+83+94+1+9+8+3+6+1+4+7+526+9+23+4+5+1+8+7537+168...+81+4+9+726+3+5::337::"
        );
        SudokuSolver solver = SudokuSolverFactory.getDefaultSolverInstance();
        SolutionStep step = solver.getHint(sudoku, false);
        System.out.println(step);
        System.out.println(sudoku.getSudoku(ClipboardMode.LIBRARY, step));
        System.exit(0);
    }

    @Override
    protected SolutionStep getStep(SolutionType type) {
        SolutionStep result = null;
        this.sudoku = this.finder.getSudoku();
        switch (type) {
            case XY_WING:
                result = this.getXYWing();
                break;
            case XYZ_WING:
                result = this.getXYZWing();
                break;
            case W_WING:
                result = this.getWWing(true);
        }

        return result;
    }

    @Override
    protected boolean doStep(SolutionStep step) {
        boolean handled = true;
        this.sudoku = this.finder.getSudoku();
        switch (step.getType()) {
            case XY_WING:
            case XYZ_WING:
            case W_WING:
                for (Candidate cand : step.getCandidatesToDelete()) {
                    this.sudoku.delCandidate(cand.getIndex(), cand.getValue());
                }
                break;
            default:
                handled = false;
        }

        return handled;
    }

    private SolutionStep getXYWing() {
        return this.getWing(false, true);
    }

    private SolutionStep getXYZWing() {
        return this.getWing(true, true);
    }

    protected List<SolutionStep> getAllWings() {
        this.sudoku = this.finder.getSudoku();
        List<SolutionStep> newSteps = new ArrayList<>();
        List<SolutionStep> oldSteps = this.steps;
        this.steps = newSteps;
        this.getWing(true, false);
        this.getWing(false, false);
        this.getWWing(false);
        this.steps = oldSteps;
        return newSteps;
    }

    private SolutionStep getWing(boolean xyz, boolean onlyOne) {
        int biValueCount = 0;
        int triValueCount = 0;

        for (int i = 0; i < 81; i++) {
            if (this.sudoku.getAnzCandidates(i) == 2) {
                this.biCells[biValueCount++] = i;
            }

            if (xyz && this.sudoku.getAnzCandidates(i) == 3) {
                this.triCells[triValueCount++] = i;
            }
        }

        int endIndex = xyz ? triValueCount : biValueCount;
        int[] biTri = xyz ? this.triCells : this.biCells;

        for (int i = 0; i < endIndex; i++) {
            for (int j = xyz ? 0 : i + 1; j < biValueCount; j++) {
                if (Sudoku2.ANZ_VALUES[this.sudoku.getCell(biTri[i]) | this.sudoku.getCell(this.biCells[j])] == 3) {
                    for (int k = j + 1; k < biValueCount; k++) {
                        int index1 = biTri[i];
                        int index2 = this.biCells[j];
                        int index3 = this.biCells[k];
                        int cell1 = this.sudoku.getCell(index1);
                        int cell2 = this.sudoku.getCell(index2);
                        int cell3 = this.sudoku.getCell(index3);
                        if (Sudoku2.ANZ_VALUES[cell1 | cell2 | cell3] == 3 && cell1 != cell2 && cell2 != cell3 && cell3 != cell1) {
                            int maxTries = xyz ? 1 : 3;

                            for (int tries = 0; tries < maxTries; tries++) {
                                if (tries == 1) {
                                    index1 = this.biCells[j];
                                    index2 = biTri[i];
                                    int var23 = this.sudoku.getCell(index1);
                                    cell2 = this.sudoku.getCell(index2);
                                } else if (tries == 2) {
                                    index1 = this.biCells[k];
                                    index2 = this.biCells[j];
                                    index3 = biTri[i];
                                    int var24 = this.sudoku.getCell(index1);
                                    cell2 = this.sudoku.getCell(index2);
                                    cell3 = this.sudoku.getCell(index3);
                                }

                                if (Sudoku2.buddies[index1].contains(index2) && Sudoku2.buddies[index1].contains(index3)) {
                                    short cell = (short) (cell2 & cell3);
                                    if (Sudoku2.ANZ_VALUES[cell] == 1) {
                                        int candZ = Sudoku2.CAND_FROM_MASK[cell];
                                        this.elimSet.setAnd(Sudoku2.buddies[index2], Sudoku2.buddies[index3]);
                                        this.elimSet.and(this.finder.getCandidates()[candZ]);
                                        if (xyz) {
                                            this.elimSet.and(Sudoku2.buddies[index1]);
                                        }

                                        if (!this.elimSet.isEmpty()) {
                                            this.globalStep.reset();
                                            if (xyz) {
                                                this.globalStep.setType(SolutionType.XYZ_WING);
                                            } else {
                                                this.globalStep.setType(SolutionType.XY_WING);
                                            }

                                            int[] cands = this.sudoku.getAllCandidates(index1);
                                            this.globalStep.addValue(cands[0]);
                                            this.globalStep.addValue(cands[1]);
                                            if (xyz) {
                                                this.globalStep.addValue(cands[2]);
                                            } else {
                                                this.globalStep.addValue(candZ);
                                            }

                                            this.globalStep.addIndex(index1);
                                            this.globalStep.addIndex(index2);
                                            this.globalStep.addIndex(index3);
                                            if (xyz) {
                                                this.globalStep.addFin(index1, candZ);
                                            }

                                            this.globalStep.addFin(index2, candZ);
                                            this.globalStep.addFin(index3, candZ);

                                            for (int l = 0; l < this.elimSet.size(); l++) {
                                                this.globalStep.addCandidateToDelete(this.elimSet.get(l), candZ);
                                            }

                                            SolutionStep step = (SolutionStep) this.globalStep.clone();
                                            if (onlyOne) {
                                                return step;
                                            }

                                            this.steps.add(step);
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

    private SolutionStep getWWing(boolean onlyOne) {
        for (int i = 0; i < this.sudoku.getCells().length; i++) {
            if (this.sudoku.getValue(i) == 0 && this.sudoku.getAnzCandidates(i) == 2) {
                short cell1 = this.sudoku.getCell(i);
                int cand1 = this.sudoku.getAllCandidates(i)[0];
                int cand2 = this.sudoku.getAllCandidates(i)[1];
                this.preCalcSet1.setAnd(Sudoku2.buddies[i], this.finder.getCandidates()[cand1]);
                this.preCalcSet2.setAnd(Sudoku2.buddies[i], this.finder.getCandidates()[cand2]);

                for (int j = i + 1; j < this.sudoku.getCells().length; j++) {
                    if (this.sudoku.getCell(j) == cell1) {
                        this.elimSet.setAnd(this.preCalcSet1, Sudoku2.buddies[j]);
                        if (!this.elimSet.isEmpty()) {
                            SolutionStep step = this.checkLink(cand1, cand2, i, j, this.elimSet, onlyOne);
                            if (onlyOne && step != null) {
                                return step;
                            }
                        }

                        this.elimSet.setAnd(this.preCalcSet2, Sudoku2.buddies[j]);
                        if (!this.elimSet.isEmpty()) {
                            SolutionStep step = this.checkLink(cand2, cand1, i, j, this.elimSet, onlyOne);
                            if (onlyOne && step != null) {
                                return step;
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    private SolutionStep checkLink(int cand1, int cand2, int index1, int index2, SudokuSet elimSet, boolean onlyOne) {
        byte[][] free = this.sudoku.getFree();

        for (int constr = 0; constr < free.length; constr++) {
            if (free[constr][cand2] == 2) {
                boolean sees1 = false;
                boolean sees2 = false;
                int[] indices = Sudoku2.ALL_UNITS[constr];

                for (int i = 0; i < indices.length; i++) {
                    int aktIndex = indices[i];
                    if (aktIndex != index1 && aktIndex != index2 && this.sudoku.isCandidate(aktIndex, cand2)) {
                        if (Sudoku2.buddies[aktIndex].contains(index1)) {
                            sees1 = true;
                            this.wIndex1 = aktIndex;
                        } else if (Sudoku2.buddies[aktIndex].contains(index2)) {
                            sees2 = true;
                            this.wIndex2 = aktIndex;
                        }
                    }

                    if (sees1 && sees2) {
                        break;
                    }
                }

                if (sees1 && sees2) {
                    SolutionStep step = this.createWWingStep(cand1, cand2, index1, index2, elimSet, onlyOne);
                    if (onlyOne && step != null) {
                        return step;
                    }
                }
            }
        }

        return null;
    }

    private SolutionStep createWWingStep(int cand1, int cand2, int index1, int index2, SudokuSet elimSet, boolean onlyOne) {
        this.globalStep.reset();
        this.globalStep.setType(SolutionType.W_WING);
        this.globalStep.addValue(cand1);
        this.globalStep.addValue(cand2);
        this.globalStep.addIndex(index1);
        this.globalStep.addIndex(index2);
        this.globalStep.addFin(index1, cand2);
        this.globalStep.addFin(index2, cand2);
        this.globalStep.addFin(this.wIndex1, cand2);
        this.globalStep.addFin(this.wIndex2, cand2);

        for (int i = 0; i < elimSet.size(); i++) {
            this.globalStep.addCandidateToDelete(elimSet.get(i), cand1);
        }

        SolutionStep step = (SolutionStep) this.globalStep.clone();
        if (onlyOne) {
            return step;
        }

        this.steps.add(step);
        return null;
    }
}
