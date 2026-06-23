package sudoku;

import java.util.SortedSet;
import java.util.TreeSet;

public class Sudoku {
    private static final int[][] COLS = new int[][]{
            {0, 9, 18, 27, 36, 45, 54, 63, 72},
            {1, 10, 19, 28, 37, 46, 55, 64, 73},
            {2, 11, 20, 29, 38, 47, 56, 65, 74},
            {3, 12, 21, 30, 39, 48, 57, 66, 75},
            {4, 13, 22, 31, 40, 49, 58, 67, 76},
            {5, 14, 23, 32, 41, 50, 59, 68, 77},
            {6, 15, 24, 33, 42, 51, 60, 69, 78},
            {7, 16, 25, 34, 43, 52, 61, 70, 79},
            {8, 17, 26, 35, 44, 53, 62, 71, 80}
    };
    private static final int CPL = 9;
    private SudokuSetBase[] possiblePositions = new SudokuSetBase[10];
    private SudokuSetBase[] allowedPositions = new SudokuSetBase[10];
    private SudokuSet[] positions = new SudokuSet[10];
    private SudokuCell[] cells = new SudokuCell[81];
    private DifficultyLevel level;
    private int score;
    private String initialState = null;

    public Sudoku() {
        for (int i = 0; i <= 9; i++) {
            this.allowedPositions[i] = new SudokuSet(true);
            this.allowedPositions[i].setAll();
            this.possiblePositions[i] = new SudokuSet(true);
            this.possiblePositions[i].setAll();
            this.positions[i] = new SudokuSet();
        }

        for (int i = 0; i < this.cells.length; i++) {
            this.cells[i] = new SudokuCell();
        }
    }

    private static int getCol(int index) {
        return index % 9;
    }

    public SudokuCell[] getCells() {
        return this.cells;
    }

    public void setCells(SudokuCell[] cells) {
        this.cells = cells;
    }

    public DifficultyLevel getLevel() {
        return this.level;
    }

    public void setLevel(DifficultyLevel level) {
        this.level = level;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public SudokuSetBase[] getPossiblePositions() {
        return this.possiblePositions;
    }

    public void setPossiblePositions(SudokuSetBase[] possiblePositions) {
        this.possiblePositions = possiblePositions;
    }

    public SudokuSetBase[] getAllowedPositions() {
        return this.allowedPositions;
    }

    public void setAllowedPositions(SudokuSetBase[] allowedPositions) {
        this.allowedPositions = allowedPositions;
    }

    public SudokuSet[] getPositions() {
        return this.positions;
    }

    public void setPositions(SudokuSet[] positions) {
        this.positions = positions;
    }

    public String getInitialState() {
        return this.initialState;
    }

    public void setInitialState(String initialState) {
        this.initialState = initialState;
    }

    public String getSudoku(ClipboardMode mode, SolutionStep step) {
        String dot = Options.getInstance().isUseZeroInsteadOfDot() ? "0" : ".";
        StringBuilder out = new StringBuilder();
        if (mode == ClipboardMode.LIBRARY) {
            if (step == null) {
                out.append(":0000:x:");
            } else {
                String type = step.getType().getLibraryType();
                if (step.getType().isFish() && step.isIsSiamese()) {
                    type = type + "1";
                }

                out.append(":").append(type).append(":");
                SortedSet<Integer> candToDeleteSet = new TreeSet<>();
                if (step.getType().useCandToDelInLibraryFormat()) {
                    for (Candidate cand : step.getCandidatesToDelete()) {
                        candToDeleteSet.add(cand.getValue());
                    }
                }

                if (candToDeleteSet.isEmpty()) {
                    for (int i = 0; i < step.getValues().size(); i++) {
                        candToDeleteSet.add(step.getValues().get(i));
                    }
                }

                for (int cand : candToDeleteSet) {
                    out.append(cand);
                }

                out.append(":");
            }
        }

        if (mode == ClipboardMode.CLUES_ONLY || mode == ClipboardMode.VALUES_ONLY || mode == ClipboardMode.LIBRARY) {
            for (SudokuCell cell : this.cells) {
                if (cell.getValue() != 0 && (mode != ClipboardMode.CLUES_ONLY || cell.isIsFixed())) {
                    if (mode == ClipboardMode.LIBRARY && !cell.isIsFixed()) {
                        out.append("+");
                    }

                    out.append(Integer.toString(cell.getValue()));
                } else {
                    out.append(dot);
                }
            }
        }

        if (mode == ClipboardMode.PM_GRID || mode == ClipboardMode.PM_GRID_WITH_STEP) {
            StringBuilder[] cellBuffers = new StringBuilder[this.cells.length];

            for (int i = 0; i < this.cells.length; i++) {
                cellBuffers[i] = new StringBuilder();
                if (this.cells[i].getValue() != 0) {
                    cellBuffers[i].append(String.valueOf(this.cells[i].getValue()));
                } else {
                    String candString = this.cells[i].getCandidateString(1);
                    if (candString.isEmpty()) {
                        candString = dot;
                    }

                    cellBuffers[i].append(candString);
                }
            }

            if (mode == ClipboardMode.PM_GRID_WITH_STEP && step != null) {
                boolean[] cellsWithExtraChar = new boolean[this.cells.length];

                for (int index : step.getIndices()) {
                    this.insertOrReplaceChar(cellBuffers[index], '*');
                    cellsWithExtraChar[index] = true;
                }

                if (SolutionType.isFish(step.getType()) || step.getType() == SolutionType.W_WING) {
                    for (Candidate cand : step.getFins()) {
                        int index = cand.getIndex();
                        this.insertOrReplaceChar(cellBuffers[index], '#');
                        cellsWithExtraChar[index] = true;
                    }
                }

                if (SolutionType.isFish(step.getType())) {
                    for (Candidate cand : step.getEndoFins()) {
                        int index = cand.getIndex();
                        this.insertOrReplaceChar(cellBuffers[index], '@');
                        cellsWithExtraChar[index] = true;
                    }
                }

                for (Chain chain : step.getChains()) {
                    for (int i = chain.getStart(); i <= chain.getEnd(); i++) {
                        if (chain.getNodeType(i) != 2) {
                            int index = chain.getCellIndex(i);
                            this.insertOrReplaceChar(cellBuffers[index], '*');
                            cellsWithExtraChar[index] = true;
                            if (chain.getNodeType(i) == 1) {
                                index = Chain.getSCellIndex2(chain.getChain()[i]);
                                if (index != -1) {
                                    this.insertOrReplaceChar(cellBuffers[index], '*');
                                    cellsWithExtraChar[index] = true;
                                }

                                index = Chain.getSCellIndex3(chain.getChain()[i]);
                                if (index != -1) {
                                    this.insertOrReplaceChar(cellBuffers[index], '*');
                                    cellsWithExtraChar[index] = true;
                                }
                            }
                        }
                    }
                }

                char alsChar = 'A';

                for (AlsInSolutionStep als : step.getAlses()) {
                    for (int index : als.getIndices()) {
                        this.insertOrReplaceChar(cellBuffers[index], alsChar);
                        cellsWithExtraChar[index] = true;
                    }

                    alsChar++;
                }

                for (Candidate cand : step.getCandidatesToDelete()) {
                    int index = cand.getIndex();
                    char candidate = Character.forDigit(cand.getValue(), 10);

                    for (int i = 0; i < cellBuffers[index].length(); i++) {
                        if (cellBuffers[index].charAt(i) == candidate && (i == 0 || i > 0 && cellBuffers[index].charAt(i - 1) != '-')) {
                            cellBuffers[index].insert(i, '-');
                            if (i == 0) {
                                cellsWithExtraChar[index] = true;
                            }
                        }
                    }
                }

                for (int i = 0; i < cellsWithExtraChar.length; i++) {
                    if (cellsWithExtraChar[i]) {
                        int[] indices = COLS[getCol(i)];

                        for (int j = 0; j < indices.length; j++) {
                            if (Character.isDigit(cellBuffers[indices[j]].charAt(0))) {
                                cellBuffers[indices[j]].insert(0, ' ');
                            }
                        }
                    }
                }
            }

            int[] fieldLengths = new int[COLS.length];

            for (int i = 0; i < cellBuffers.length; i++) {
                int col = getCol(i);
                if (cellBuffers[i].length() > fieldLengths[col]) {
                    fieldLengths[col] = cellBuffers[i].length();
                }
            }

            for (int i = 0; i < fieldLengths.length; i++) {
                fieldLengths[i] += 2;
            }

            for (int i = 0; i < 9; i++) {
                if (i % 3 == 0) {
                    this.writeLine(out, i, fieldLengths, null, true);
                }

                this.writeLine(out, i, fieldLengths, cellBuffers, false);
            }

            this.writeLine(out, 9, fieldLengths, null, true);
            if (mode == ClipboardMode.PM_GRID_WITH_STEP && step != null) {
                out.append(step.toString(2));
            }
        }

        if (mode == ClipboardMode.LIBRARY) {
            int type = 1;
            boolean first = true;
            out.append(":");

            for (int i = 0; i < this.cells.length; i++) {
                SudokuCell cell = this.cells[i];
                if (cell.getValue() == 0) {
                    for (int j = 1; j <= 9; j++) {
                        if (cell.isCandidate(2, j) && !cell.isCandidate(type, j)) {
                            if (first) {
                                first = false;
                            } else {
                                out.append(" ");
                            }

                            out.append(Integer.toString(j)).append(Integer.toString(i / 9 + 1)).append(Integer.toString(i % 9 + 1));
                        }
                    }
                }
            }

            if (step == null) {
                out.append("::");
            } else {
                String candString = step.getCandidateString(true);
                out.append(":").append(candString).append(":");
                if (candString.isEmpty()) {
                    out.append(step.getValueIndexString());
                }

                out.append(":");
                if (step.getType().isSimpleChainOrLoop()) {
                    out.append(step.getChainLength() - 1);
                }
            }
        }

        return out.toString();
    }

    private void insertOrReplaceChar(StringBuilder buffer, char ch) {
        if (Character.isDigit(buffer.charAt(0))) {
            buffer.insert(0, ch);
        } else {
            buffer.replace(0, 1, Character.toString(ch));
        }
    }

    private void writeLine(StringBuilder out, int line, int[] fieldLengths, StringBuilder[] cellBuffers, boolean drawOutline) {
        if (drawOutline) {
            char leftRight = '.';
            char middle = '.';
            if (line == 3 || line == 6) {
                leftRight = ':';
                middle = '+';
            } else if (line == 9) {
                leftRight = '\'';
                middle = '\'';
            }

            out.append(leftRight);

            for (int i = 0; i < fieldLengths[0] + fieldLengths[1] + fieldLengths[2]; i++) {
                out.append('-');
            }

            out.append(middle);

            for (int i = 0; i < fieldLengths[3] + fieldLengths[4] + fieldLengths[5]; i++) {
                out.append('-');
            }

            out.append(middle);

            for (int i = 0; i < fieldLengths[6] + fieldLengths[7] + fieldLengths[8]; i++) {
                out.append('-');
            }

            out.append(leftRight);
        } else {
            for (int i = line * 9; i < (line + 1) * 9; i++) {
                if (i % 3 == 0) {
                    out.append("|");
                    if (i % 9 != 8) {
                        out.append(' ');
                    }
                } else {
                    out.append(' ');
                }

                int tmp = fieldLengths[getCol(i)];
                out.append(cellBuffers[i]);
                tmp -= cellBuffers[i].length();

                for (int j = 0; j < tmp - 1; j++) {
                    out.append(' ');
                }
            }

            out.append('|');
        }

        out.append("\r\n");
    }
}
