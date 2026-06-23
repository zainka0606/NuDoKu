package solver;

import sudoku.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SingleDigitPatternSolver extends AbstractSolver {
    private static final int[][] erOffsets = new int[][]{
            {0, 1, 9, 10}, {0, 2, 9, 11}, {1, 2, 10, 11}, {0, 1, 18, 19}, {0, 2, 18, 20}, {1, 2, 19, 20}, {9, 10, 18, 19}, {9, 11, 18, 20}, {10, 11, 19, 20}
    };
    private static final int[] erLineOffsets = new int[]{2, 2, 2, 1, 1, 1, 0, 0, 0};
    private static final int[] erColOffsets = new int[]{2, 1, 0, 2, 1, 0, 2, 1, 0};
    private static final SudokuSet[][] erSets = new SudokuSet[9][9];
    private static final int[][] erLines = new int[9][9];
    private static final int[][] erCols = new int[9][9];

    static {
        int indexOffset = 0;
        int lineOffset = 0;
        int colOffset = 0;

        for (int i = 0; i < Sudoku2.BLOCKS.length; i++) {
            for (int j = 0; j < erOffsets.length; j++) {
                erSets[i][j] = new SudokuSet();

                for (int k = 0; k < erOffsets[j].length; k++) {
                    erSets[i][j].add(erOffsets[j][k] + indexOffset);
                }
            }

            erLines[i] = new int[9];
            erCols[i] = new int[9];

            for (int j = 0; j < erLineOffsets.length; j++) {
                erLines[i][j] = erLineOffsets[j] + lineOffset;
                erCols[i][j] = erColOffsets[j] + colOffset;
            }

            indexOffset += 3;
            colOffset += 3;
            if (i % 3 == 2) {
                indexOffset += 18;
                lineOffset += 3;
                colOffset = 0;
            }
        }
    }

    private SudokuSet blockCands = new SudokuSet();
    private SudokuSet tmpSet = new SudokuSet();
    private List<SolutionStep> steps = new ArrayList<>();
    private SolutionStep globalStep = new SolutionStep();
    private int[][] only2Indices = new int[18][2];
    private SudokuSet firstUnit = new SudokuSet();

    protected SingleDigitPatternSolver(SudokuStepFinder finder) {
        super(finder);
    }

    public static void main(String[] args) {
        Sudoku2 sudoku = new Sudoku2();
        sudoku.setSudoku(":0401:3:+156+87+49+3+2.4+762.+18+528....+4+7+6....8.+5+9.73....618+8.5...+32.........+3.7.5...49....487.1::381::");
        sudoku.setSudoku(":0401:3:9.567.1..61.5+4...+9.849+3+15+6....8.39.....+2.+9....+987.4...+5+61.+9782.+8+7+9.+26.51..2+1857+96:249 261 165 367 369:328::");
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
            case SKYSCRAPER:
                result = this.findSkyscraper();
                break;
            case TWO_STRING_KITE:
                result = this.findTwoStringKite();
                break;
            case EMPTY_RECTANGLE:
                result = this.findEmptyRectangle();
        }

        return result;
    }

    @Override
    protected boolean doStep(SolutionStep step) {
        boolean handled = true;
        this.sudoku = this.finder.getSudoku();
        switch (step.getType()) {
            case SKYSCRAPER:
            case TWO_STRING_KITE:
            case EMPTY_RECTANGLE:
            case DUAL_TWO_STRING_KITE:
            case DUAL_EMPTY_RECTANGLE:
                for (Candidate cand : step.getCandidatesToDelete()) {
                    this.sudoku.delCandidate(cand.getIndex(), cand.getValue());
                }
                break;
            default:
                handled = false;
        }

        return handled;
    }

    protected List<SolutionStep> findAllEmptyRectangles() {
        this.sudoku = this.finder.getSudoku();
        List<SolutionStep> oldList = this.steps;
        List<SolutionStep> newList = new ArrayList<>();
        this.steps = newList;
        this.findEmptyRectangles(false);
        this.findDualEmptyRectangles(this.steps);
        Collections.sort(this.steps);
        this.steps = oldList;
        return newList;
    }

    protected SolutionStep findEmptyRectangle() {
        this.steps.clear();
        SolutionStep step = this.findEmptyRectangles(true);
        if (step != null && !Options.getInstance().isAllowDualsAndSiamese()) {
            return step;
        } else if (this.steps.size() > 0 && Options.getInstance().isAllowDualsAndSiamese()) {
            this.findDualEmptyRectangles(this.steps);
            Collections.sort(this.steps);
            return this.steps.get(0);
        } else {
            return null;
        }
    }

    private SolutionStep findEmptyRectangles(boolean onlyOne) {
        for (int i = 1; i <= 9; i++) {
            SolutionStep step = this.findEmptyRectanglesForCandidate(i, onlyOne);
            if (step != null && onlyOne && !Options.getInstance().isAllowDualsAndSiamese()) {
                return step;
            }
        }

        return null;
    }

    private SolutionStep findEmptyRectanglesForCandidate(int cand, boolean onlyOne) {
        byte[][] free = this.sudoku.getFree();

        for (int i = 0; i < Sudoku2.BLOCK_TEMPLATES.length; i++) {
            if (free[18 + i][cand] >= 2 && free[18 + i][cand] <= 5) {
                this.blockCands.set(this.finder.getCandidates()[cand]);
                this.blockCands.and(Sudoku2.BLOCK_TEMPLATES[i]);

                for (int j = 0; j < erSets[i].length; j++) {
                    int erLine = 0;
                    int erCol = 0;
                    boolean notEnoughCandidates = true;
                    this.tmpSet.setAnd(this.blockCands, erSets[i][j]);
                    if (this.tmpSet.isEmpty()) {
                        this.tmpSet.setAnd(this.blockCands, Sudoku2.LINE_TEMPLATES[erLines[i][j]]);
                        if (this.tmpSet.size() >= 2) {
                            notEnoughCandidates = false;
                        }

                        this.tmpSet.andNot(Sudoku2.COL_TEMPLATES[erCols[i][j]]);
                        if (!this.tmpSet.isEmpty()) {
                            erLine = erLines[i][j];
                            this.tmpSet.setAnd(this.blockCands, Sudoku2.COL_TEMPLATES[erCols[i][j]]);
                            if (this.tmpSet.size() >= 2) {
                                notEnoughCandidates = false;
                            }

                            this.tmpSet.andNot(Sudoku2.LINE_TEMPLATES[erLines[i][j]]);
                            if (!this.tmpSet.isEmpty()) {
                                erCol = erCols[i][j];
                                if (!notEnoughCandidates || Options.getInstance().isAllowErsWithOnlyTwoCandidates()) {
                                    SolutionStep step = this.checkEmptyRectangle(
                                            cand, i, this.blockCands, Sudoku2.LINES[erLine], Sudoku2.LINE_TEMPLATES, Sudoku2.COL_TEMPLATES, erCol, false, onlyOne
                                    );
                                    if (onlyOne && step != null && !Options.getInstance().isAllowDualsAndSiamese()) {
                                        return step;
                                    }

                                    step = this.checkEmptyRectangle(
                                            cand, i, this.blockCands, Sudoku2.COLS[erCol], Sudoku2.COL_TEMPLATES, Sudoku2.LINE_TEMPLATES, erLine, true, onlyOne
                                    );
                                    if (onlyOne && step != null && !Options.getInstance().isAllowDualsAndSiamese()) {
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

    private SolutionStep checkEmptyRectangle(
            int cand,
            int block,
            SudokuSet blockCands,
            int[] indices,
            SudokuSet[] lineTemplates,
            SudokuSet[] colTemplates,
            int firstCol,
            boolean lineColReversed,
            boolean onlyOne
    ) {
        for (int i = 0; i < indices.length; i++) {
            int index = indices[i];
            if (this.sudoku.getValue(index) == 0 && Sudoku2.getBlock(index) != block && this.sudoku.isCandidate(index, cand)) {
                this.tmpSet.set(this.finder.getCandidates()[cand]);
                int actCol = Sudoku2.getCol(index);
                if (lineColReversed) {
                    actCol = Sudoku2.getLine(index);
                }

                this.tmpSet.and(colTemplates[actCol]);
                if (this.tmpSet.size() == 2) {
                    int index2 = this.tmpSet.get(0);
                    if (index2 == index) {
                        index2 = this.tmpSet.get(1);
                    }

                    int actLine = Sudoku2.getLine(index2);
                    if (lineColReversed) {
                        actLine = Sudoku2.getCol(index2);
                    }

                    this.tmpSet.set(this.finder.getCandidates()[cand]);
                    this.tmpSet.and(lineTemplates[actLine]);

                    for (int j = 0; j < this.tmpSet.size(); j++) {
                        int indexDel = this.tmpSet.get(j);
                        if (Sudoku2.getBlock(indexDel) != block) {
                            int colDel = Sudoku2.getCol(indexDel);
                            if (lineColReversed) {
                                colDel = Sudoku2.getLine(indexDel);
                            }

                            if (colDel == firstCol) {
                                this.globalStep.reset();
                                this.globalStep.setType(SolutionType.EMPTY_RECTANGLE);
                                this.globalStep.setEntity(0);
                                this.globalStep.setEntityNumber(block + 1);
                                this.globalStep.addValue(cand);
                                this.globalStep.addIndex(index);
                                this.globalStep.addIndex(index2);

                                for (int k = 0; k < blockCands.size(); k++) {
                                    this.globalStep.addFin(blockCands.get(k), cand);
                                }

                                this.globalStep.addCandidateToDelete(indexDel, cand);
                                SolutionStep step = (SolutionStep) this.globalStep.clone();
                                if (onlyOne && !Options.getInstance().isAllowDualsAndSiamese()) {
                                    return step;
                                }

                                this.steps.add(step);
                                break;
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    private void findDualEmptyRectangles(List<SolutionStep> ers) {
        if (Options.getInstance().isAllowDualsAndSiamese()) {
            int maxIndex = ers.size();

            for (int i = 0; i < maxIndex - 1; i++) {
                for (int j = i + 1; j < maxIndex; j++) {
                    SolutionStep step1 = ers.get(i);
                    SolutionStep step2 = ers.get(j);
                    if (step1.getEntity() == step2.getEntity()
                            && step1.getEntityNumber() == step2.getEntityNumber()
                            && step1.getFins().size() == step2.getFins().size()) {
                        boolean finsEqual = true;

                        for (int k = 0; k < step1.getFins().size(); k++) {
                            if (!step1.getFins().get(k).equals(step2.getFins().get(k))) {
                                finsEqual = false;
                                break;
                            }
                        }

                        if (finsEqual && !step1.getCandidatesToDelete().get(0).equals(step2.getCandidatesToDelete().get(0))) {
                            SolutionStep dual = (SolutionStep) step1.clone();
                            dual.setType(SolutionType.DUAL_EMPTY_RECTANGLE);
                            dual.addIndex(step2.getIndices().get(0));
                            dual.addIndex(step2.getIndices().get(1));
                            dual.addCandidateToDelete(step2.getCandidatesToDelete().get(0));
                            ers.add(dual);
                        }
                    }
                }
            }
        }
    }

    protected List<SolutionStep> findAllSkyscrapers() {
        this.sudoku = this.finder.getSudoku();
        List<SolutionStep> oldList = this.steps;
        List<SolutionStep> newList = new ArrayList<>();
        this.steps = newList;
        this.findSkyscraper(true, false);
        this.findSkyscraper(false, false);
        Collections.sort(this.steps);
        this.steps = oldList;
        return newList;
    }

    protected SolutionStep findSkyscraper() {
        this.steps.clear();
        SolutionStep step = this.findSkyscraper(true, true);
        return step != null ? step : this.findSkyscraper(false, true);
    }

    private SolutionStep findSkyscraper(boolean lines, boolean onlyOne) {
        int cStart = 0;
        int cEnd = 9;
        if (!lines) {
            cStart += 9;
            cEnd += 9;
        }

        byte[][] free = this.sudoku.getFree();

        for (int cand = 1; cand <= 9; cand++) {
            int constrCount = 0;

            for (int constr = cStart; constr < cEnd; constr++) {
                if (free[constr][cand] == 2) {
                    int[] indices = Sudoku2.ALL_UNITS[constr];
                    int candIndex = 0;

                    for (int i = 0; i < indices.length; i++) {
                        if (this.sudoku.isCandidate(indices[i], cand)) {
                            this.only2Indices[constrCount][candIndex++] = indices[i];
                            if (candIndex >= 2) {
                                break;
                            }
                        }
                    }

                    constrCount++;
                }
            }

            for (int i = 0; i < constrCount; i++) {
                for (int j = i + 1; j < constrCount; j++) {
                    boolean found = false;
                    int otherIndex = 1;
                    if (lines) {
                        if (Sudoku2.getCol(this.only2Indices[i][0]) == Sudoku2.getCol(this.only2Indices[j][0])) {
                            found = true;
                        }

                        if (!found && Sudoku2.getCol(this.only2Indices[i][1]) == Sudoku2.getCol(this.only2Indices[j][1])) {
                            found = true;
                            otherIndex = 0;
                        }
                    } else {
                        if (Sudoku2.getLine(this.only2Indices[i][0]) == Sudoku2.getLine(this.only2Indices[j][0])) {
                            found = true;
                        }

                        if (!found && Sudoku2.getLine(this.only2Indices[i][1]) == Sudoku2.getLine(this.only2Indices[j][1])) {
                            found = true;
                            otherIndex = 0;
                        }
                    }

                    if (found
                            && (!lines || Sudoku2.getCol(this.only2Indices[i][otherIndex]) != Sudoku2.getCol(this.only2Indices[j][otherIndex]))
                            && (lines || Sudoku2.getLine(this.only2Indices[i][otherIndex]) != Sudoku2.getLine(this.only2Indices[j][otherIndex]))) {
                        this.firstUnit.setAnd(this.finder.getCandidates()[cand], Sudoku2.buddies[this.only2Indices[i][otherIndex]]);
                        this.firstUnit.and(Sudoku2.buddies[this.only2Indices[j][otherIndex]]);
                        if (!this.firstUnit.isEmpty()) {
                            SolutionStep step = new SolutionStep(SolutionType.SKYSCRAPER);
                            step.addValue(cand);
                            if (otherIndex == 0) {
                                step.addIndex(this.only2Indices[i][0]);
                                step.addIndex(this.only2Indices[j][0]);
                                step.addIndex(this.only2Indices[i][1]);
                                step.addIndex(this.only2Indices[j][1]);
                            } else {
                                step.addIndex(this.only2Indices[i][1]);
                                step.addIndex(this.only2Indices[j][1]);
                                step.addIndex(this.only2Indices[i][0]);
                                step.addIndex(this.only2Indices[j][0]);
                            }

                            for (int k = 0; k < this.firstUnit.size(); k++) {
                                step.addCandidateToDelete(this.firstUnit.get(k), cand);
                            }

                            if (onlyOne) {
                                return step;
                            }

                            this.steps.add(step);
                        }
                    }
                }
            }
        }

        return null;
    }

    protected List<SolutionStep> findAllTwoStringKites() {
        this.sudoku = this.finder.getSudoku();
        List<SolutionStep> oldList = this.steps;
        List<SolutionStep> newList = new ArrayList<>();
        this.steps = newList;
        this.findTwoStringKite(false);
        if (Options.getInstance().isAllowDualsAndSiamese()) {
            this.findDualTwoStringKites(this.steps);
        }

        Collections.sort(this.steps);
        this.steps = oldList;
        return newList;
    }

    protected SolutionStep findTwoStringKite() {
        this.steps.clear();
        SolutionStep step = this.findTwoStringKite(true);
        if (step != null && !Options.getInstance().isAllowDualsAndSiamese()) {
            return step;
        } else {
            this.findDualTwoStringKites(this.steps);
            if (this.steps.size() > 0) {
                Collections.sort(this.steps);
                return this.steps.get(0);
            } else {
                return null;
            }
        }
    }

    private SolutionStep findTwoStringKite(boolean onlyOne) {
        byte[][] free = this.sudoku.getFree();

        for (int cand = 1; cand <= 9; cand++) {
            int constr1Count = 0;
            int constr2Count = 0;

            for (int constr = 0; constr < 18; constr++) {
                if (free[constr][cand] == 2) {
                    int[] indices = Sudoku2.ALL_UNITS[constr];
                    int candIndex = 0;

                    for (int i = 0; i < indices.length; i++) {
                        if (this.sudoku.isCandidate(indices[i], cand)) {
                            this.only2Indices[constr1Count + constr2Count][candIndex++] = indices[i];
                            if (candIndex >= 2) {
                                break;
                            }
                        }
                    }

                    if (constr < 9) {
                        constr1Count++;
                    } else {
                        constr2Count++;
                    }
                }
            }

            for (int i = 0; i < constr1Count; i++) {
                for (int j = constr1Count; j < constr1Count + constr2Count; j++) {
                    if (Sudoku2.getBlock(this.only2Indices[i][0]) != Sudoku2.getBlock(this.only2Indices[j][0])) {
                        if (Sudoku2.getBlock(this.only2Indices[i][0]) == Sudoku2.getBlock(this.only2Indices[j][1])) {
                            int tmp = this.only2Indices[j][0];
                            this.only2Indices[j][0] = this.only2Indices[j][1];
                            this.only2Indices[j][1] = tmp;
                        } else if (Sudoku2.getBlock(this.only2Indices[i][1]) == Sudoku2.getBlock(this.only2Indices[j][0])) {
                            int tmp = this.only2Indices[i][0];
                            this.only2Indices[i][0] = this.only2Indices[i][1];
                            this.only2Indices[i][1] = tmp;
                        } else {
                            if (Sudoku2.getBlock(this.only2Indices[i][1]) != Sudoku2.getBlock(this.only2Indices[j][1])) {
                                continue;
                            }

                            int tmp = this.only2Indices[j][0];
                            this.only2Indices[j][0] = this.only2Indices[j][1];
                            this.only2Indices[j][1] = tmp;
                            tmp = this.only2Indices[i][0];
                            this.only2Indices[i][0] = this.only2Indices[i][1];
                            this.only2Indices[i][1] = tmp;
                        }
                    }

                    if (this.only2Indices[i][0] != this.only2Indices[j][0]
                            && this.only2Indices[i][0] != this.only2Indices[j][1]
                            && this.only2Indices[i][1] != this.only2Indices[j][0]
                            && this.only2Indices[i][1] != this.only2Indices[j][1]) {
                        int crossIndex = Sudoku2.getIndex(Sudoku2.getLine(this.only2Indices[j][1]), Sudoku2.getCol(this.only2Indices[i][1]));
                        if (this.sudoku.isCandidate(crossIndex, cand)) {
                            SolutionStep step = new SolutionStep(SolutionType.TWO_STRING_KITE);
                            step.addValue(cand);
                            step.addIndex(this.only2Indices[i][1]);
                            step.addIndex(this.only2Indices[j][1]);
                            step.addIndex(this.only2Indices[i][0]);
                            step.addIndex(this.only2Indices[j][0]);
                            step.addCandidateToDelete(crossIndex, cand);
                            step.addFin(this.only2Indices[i][0], cand);
                            step.addFin(this.only2Indices[j][0], cand);
                            if (onlyOne && !Options.getInstance().isAllowDualsAndSiamese()) {
                                return step;
                            }

                            this.steps.add(step);
                        }
                    }
                }
            }
        }

        return null;
    }

    private void findDualTwoStringKites(List<SolutionStep> kites) {
        if (Options.getInstance().isAllowDualsAndSiamese()) {
            int maxIndex = kites.size();

            for (int i = 0; i < maxIndex - 1; i++) {
                for (int j = i + 1; j < maxIndex; j++) {
                    SolutionStep step1 = kites.get(i);
                    SolutionStep step2 = kites.get(j);
                    int b11 = step1.getIndices().get(2);
                    int b12 = step1.getIndices().get(3);
                    int b21 = step2.getIndices().get(2);
                    int b22 = step2.getIndices().get(3);
                    if ((b11 == b21 && b12 == b22 || b12 == b21 && b11 == b22) && !step1.getCandidatesToDelete().get(0).equals(step2.getCandidatesToDelete().get(0))
                    ) {
                        SolutionStep dual = (SolutionStep) step1.clone();
                        dual.setType(SolutionType.DUAL_TWO_STRING_KITE);
                        dual.addIndex(step2.getIndices().get(0));
                        dual.addIndex(step2.getIndices().get(1));
                        dual.addIndex(step2.getIndices().get(2));
                        dual.addIndex(step2.getIndices().get(3));
                        dual.addCandidateToDelete(step2.getCandidatesToDelete().get(0));
                        kites.add(dual);
                    }
                }
            }
        }
    }
}
