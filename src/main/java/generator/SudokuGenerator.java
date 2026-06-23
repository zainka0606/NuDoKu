package generator;

import sudoku.Options;
import sudoku.Sudoku2;
import sudoku.SudokuSinglesQueue;
import sudoku.SudokuStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SudokuGenerator {
    private static final boolean DEBUG = false;
    private static final int MAX_TRIES = 1000000;
    private static Sudoku2 EMPTY_GRID = new Sudoku2();
    private int[] solution = new int[81];
    private int solutionCount = 0;
    private SudokuGenerator.RecursionStackEntry[] stack = new SudokuGenerator.RecursionStackEntry[82];
    private int[] generateIndices = new int[81];
    private int[] newFullSudoku = new int[81];
    private int[] newValidSudoku = new int[81];
    private Random rand = new Random();
    private int anzTries = 0;
    private int anzNS = 0;
    private int anzHS = 0;
    private int anzTriesGen = 0;
    private int anzClues = 0;
    private long nanos = 0L;
    private long setNanos = 0L;
    private long actSetNanos = 0L;

    protected SudokuGenerator() {
        for (int i = 0; i < this.stack.length; i++) {
            this.stack[i] = new SudokuGenerator.RecursionStackEntry();
        }
    }

    public static void main(String[] args) {
        System.out.println("Sudoku2!");
        SudokuGenerator bs = SudokuGeneratorFactory.getDefaultGeneratorInstance();
        int anzRuns = 10000;
        long ticks = System.currentTimeMillis();
        int i = 0;

        while (i < anzRuns) {
            i++;
        }

        ticks = System.currentTimeMillis() - ticks;
        System.out.println(bs.getSolutionAsString() + " (" + bs.getSolutionCount() + ")");
        System.out.println("Time: " + (double) ticks / anzRuns + "ms");
        System.out.println(bs.printStat());
        ticks = System.currentTimeMillis();

        for (int ix = 0; ix < anzRuns; ix++) {
            Sudoku2 sudoku = bs.generateSudoku(true);
        }

        ticks = System.currentTimeMillis() - ticks;
        long faktor = 1000000L;
        long nanos = bs.nanos / faktor;
        long setNanos = bs.setNanos / faktor;
        System.out
                .println(
                        "Time: "
                                + (double) ticks / anzRuns
                                + "ms "
                                + bs.anzTriesGen / anzRuns
                                + "/"
                                + bs.anzClues / anzRuns
                                + "/"
                                + nanos
                                + "/"
                                + setNanos
                                + "/"
                                + (nanos - setNanos)
                );
        System.out.println(bs.printStat());
    }

    public int getNumberOfSolutions(Sudoku2 sudoku) {
        long ticks = System.currentTimeMillis();
        this.solve(sudoku);
        if (this.solutionCount == 1) {
            sudoku.setSolution(Arrays.copyOf(this.solution, this.solution.length));
        }

        ticks = System.currentTimeMillis() - ticks;
        Logger.getLogger(this.getClass().getName()).log(Level.FINE, "validSolution() {0}ms", ticks);
        return this.solutionCount;
    }

    public boolean validSolution(Sudoku2 sudoku) {
        long ticks = System.currentTimeMillis();
        this.solve(sudoku);
        boolean unique = this.solutionCount == 1;
        if (unique) {
            sudoku.setSolution(Arrays.copyOf(this.solution, this.solution.length));
        }

        ticks = System.currentTimeMillis() - ticks;
        Logger.getLogger(this.getClass().getName()).log(Level.FINE, "validSolution() {0}ms", ticks);
        return unique;
    }

    private void solve(Sudoku2 sudoku) {
        this.stack[0].sudoku.set(sudoku);
        this.stack[0].index = 0;
        this.stack[0].candidates = null;
        this.stack[0].candIndex = 0;
        this.solve();
    }

    public void solve(String sudokuString) {
        this.stack[0].sudoku.set(EMPTY_GRID);
        this.stack[0].candidates = null;
        this.stack[0].candIndex = 0;

        for (int i = 0; i < sudokuString.length() && i < 81; i++) {
            int value = sudokuString.charAt(i) - '0';
            if (value >= 1 && value <= 9) {
                this.stack[0].sudoku.setCell(i, value, false, false);
                this.setAllExposedSingles(this.stack[0].sudoku);
            }
        }

        this.solve();
    }

    public void solve(int[] cellValues) {
        this.stack[0].sudoku.set(EMPTY_GRID);
        this.stack[0].candidates = null;
        this.stack[0].candIndex = 0;

        for (int i = 0; i < cellValues.length; i++) {
            int value = cellValues[i];
            if (value >= 1 && value <= 9) {
                this.stack[0].sudoku.setCellBS(i, value);
            }
        }

        this.stack[0].sudoku.rebuildInternalData();
        this.setAllExposedSingles(this.stack[0].sudoku);
        this.solve();
    }

    private void solve() {
        this.anzTries = 0;
        this.anzNS = 0;
        this.anzHS = 0;
        this.solutionCount = 0;
        if (this.setAllExposedSingles(this.stack[0].sudoku)) {
            if (this.stack[0].sudoku.getUnsolvedCellsAnz() == 0) {
                this.solution = Arrays.copyOf(this.stack[0].sudoku.getValues(), 81);
                this.solutionCount++;
            } else {
                int level = 0;

                int done;
                do {
                    if (this.stack[level].sudoku.getUnsolvedCellsAnz() == 0) {
                        this.solutionCount++;
                        if (this.solutionCount == 1) {
                            this.solution = Arrays.copyOf(this.stack[level].sudoku.getValues(), 81);
                        } else if (this.solutionCount > 1) {
                            return;
                        }
                    } else {
                        done = -1;
                        int anzCand = 9;
                        Sudoku2 sudoku = this.stack[level].sudoku;

                        for (int i = 0; i < 81; i++) {
                            if (sudoku.getCell(i) != 0 && Sudoku2.ANZ_VALUES[sudoku.getCell(i)] < anzCand) {
                                done = i;
                                anzCand = Sudoku2.ANZ_VALUES[sudoku.getCell(i)];
                            }
                        }

                        level++;
                        if (done < 0) {
                            this.solutionCount = 0;
                            return;
                        }

                        this.stack[level].index = (short) done;
                        this.stack[level].candidates = Sudoku2.POSSIBLE_VALUES[this.stack[level - 1].sudoku.getCell(done)];
                        this.stack[level].candIndex = 0;
                    }

                    done = 0;

                    int nextCand;
                    do {
                        while (this.stack[level].candIndex >= this.stack[level].candidates.length) {
                            if (--level <= 0) {
                                done = 1;
                                break;
                            }
                        }

                        if (done != 0) {
                            break;
                        }

                        nextCand = this.stack[level].candidates[this.stack[level].candIndex++];
                        this.anzTries++;
                        this.stack[level].sudoku.setBS(this.stack[level - 1].sudoku);
                    } while (
                            !this.stack[level].sudoku.setCell(this.stack[level].index, nextCand, false, false) || !this.setAllExposedSingles(this.stack[level].sudoku)
                    );
                } while (done == 0);
            }
        }
    }

    public Sudoku2 generateSudoku(boolean symmetric) {
        int index = Options.getInstance().getGeneratorPatternIndex();
        boolean[] pattern = null;
        ArrayList<GeneratorPattern> patterns = Options.getInstance().getGeneratorPatterns();
        if (index != -1 && index < patterns.size() && patterns.get(index).isValid()) {
            pattern = patterns.get(index).getPattern();
        }

        return this.generateSudoku(symmetric, pattern);
    }

    public Sudoku2 generateSudoku(boolean symmetric, boolean[] pattern) {
        this.generateFullGrid();
        if (pattern == null) {
            this.generateInitPos(symmetric);
        } else {
            boolean ok = false;
            System.out.println("Trying with pattern!");

            for (int i = 0; i < 1000000 && !(ok = this.generateInitPos(pattern)); i++) {
                if (i % 1000 == 0) {
                    System.out.println("  try: " + i);
                }
            }

            if (!ok) {
                System.out.println("nothing found!");
                return null;
            }

            System.out.println("puzzle found!");
        }

        Sudoku2 sudoku = new Sudoku2();

        for (int i = 0; i < this.newValidSudoku.length; i++) {
            if (this.newValidSudoku[i] != 0) {
                sudoku.setCell(i, this.newValidSudoku[i]);
                sudoku.setIsFixed(i, true);
                this.anzClues++;
            }
        }

        sudoku.setStatus(SudokuStatus.VALID);
        sudoku.setStatusGivens(SudokuStatus.VALID);
        return sudoku;
    }

    private void generateFullGrid() {
        while (!this.doGenerateFullGrid()) {
        }
    }

    private boolean doGenerateFullGrid() {
        this.anzTries = 0;
        this.anzNS = 0;
        this.anzHS = 0;
        int actTries = 0;
        int max = this.generateIndices.length;
        int i = 0;

        while (i < max) {
            this.generateIndices[i] = i++;
        }

        for (int ix = 0; ix < max; ix++) {
            int index1 = this.rand.nextInt(max);
            int index2 = this.rand.nextInt(max);

            while (index1 == index2) {
                index2 = this.rand.nextInt(max);
            }

            int dummy = this.generateIndices[index1];
            this.generateIndices[index1] = this.generateIndices[index2];
            this.generateIndices[index2] = dummy;
        }

        this.stack[0].sudoku.set(EMPTY_GRID);
        i = 0;
        this.stack[0].index = -1;

        while (this.stack[i].sudoku.getUnsolvedCellsAnz() != 0) {
            int index = -1;
            int[] actValues = this.stack[i].sudoku.getValues();

            for (int ix = 0; ix < 81; ix++) {
                int actTry = this.generateIndices[ix];
                if (actValues[actTry] == 0) {
                    index = actTry;
                    break;
                }
            }

            this.stack[++i].index = (short) index;
            this.stack[i].candidates = Sudoku2.POSSIBLE_VALUES[this.stack[i - 1].sudoku.getCell(index)];
            this.stack[i].candIndex = 0;
            if (++actTries > 100) {
                return false;
            }

            boolean done = false;

            int nextCand;

            do {
                while (this.stack[i].candIndex >= this.stack[i].candidates.length) {
                    if (--i <= 0) {
                        done = true;
                        break;
                    }
                }

                if (done) {
                    break;
                }

                nextCand = this.stack[i].candidates[this.stack[i].candIndex++];
                this.anzTries++;
                this.stack[i].sudoku.setBS(this.stack[i - 1].sudoku);
            } while (!this.stack[i].sudoku.setCell(this.stack[i].index, nextCand, false, false) || !this.setAllExposedSingles(this.stack[i].sudoku));

            if (done) {
                return false;
            }
        }

        System.arraycopy(this.stack[i].sudoku.getValues(), 0, this.newFullSudoku, 0, this.newFullSudoku.length);
        return true;
    }

    private boolean generateInitPos(boolean[] pattern) {
        System.arraycopy(this.newFullSudoku, 0, this.newValidSudoku, 0, this.newFullSudoku.length);

        for (int i = 0; i < pattern.length; i++) {
            if (!pattern[i]) {
                this.newValidSudoku[i] = 0;
            }
        }

        this.solve(this.newValidSudoku);
        if (this.solutionCount > 1) {
            return false;
        }

        System.out.println("!!!! FOUND ONE !!!!");
        return true;
    }

    private void generateInitPos(boolean isSymmetric) {
        int maxPosToFill = 17;
        boolean[] used = new boolean[81];
        int usedCount = used.length;
        Arrays.fill(used, false);
        System.arraycopy(this.newFullSudoku, 0, this.newValidSudoku, 0, this.newFullSudoku.length);
        int remainingClues = this.newValidSudoku.length;

        while (remainingClues > maxPosToFill && usedCount > 1) {
            int i = this.rand.nextInt(81);

            do {
                if (i < 80) {
                    i++;
                } else {
                    i = 0;
                }
            } while (used[i]);

            used[i] = true;
            usedCount--;
            if (this.newValidSudoku[i] != 0 && (!isSymmetric || i / 9 == 4 && i % 9 == 4 || this.newValidSudoku[9 * (8 - i / 9) + (8 - i % 9)] != 0)) {
                this.newValidSudoku[i] = 0;
                remainingClues--;
                int symm = 0;
                if (isSymmetric && (i / 9 != 4 || i % 9 != 4)) {
                    symm = 9 * (8 - i / 9) + (8 - i % 9);
                    this.newValidSudoku[symm] = 0;
                    used[symm] = true;
                    usedCount--;
                    remainingClues--;
                }

                this.solve(this.newValidSudoku);
                this.anzTriesGen++;
                if (this.solutionCount > 1) {
                    this.newValidSudoku[i] = this.newFullSudoku[i];
                    remainingClues++;
                    if (isSymmetric && (i / 9 != 4 || i % 9 != 4)) {
                        this.newValidSudoku[symm] = this.newFullSudoku[symm];
                        remainingClues++;
                    }
                }
            }
        }
    }

    private boolean setAllExposedSingles(Sudoku2 sudoku) {
        boolean valid = true;
        SudokuSinglesQueue nsQueue = sudoku.getNsQueue();
        SudokuSinglesQueue hsQueue = sudoku.getHsQueue();

        do {
            int singleIndex = 0;

            while (valid && (singleIndex = nsQueue.getSingle()) != -1) {
                int index = nsQueue.getIndex(singleIndex);
                int value = nsQueue.getValue(singleIndex);
                if ((sudoku.getCell(index) & Sudoku2.MASKS[value]) != 0) {
                    this.anzNS++;
                    valid = sudoku.setCell(index, value, false, false);
                }
            }

            while (valid && (singleIndex = hsQueue.getSingle()) != -1) {
                int index = hsQueue.getIndex(singleIndex);
                int value = hsQueue.getValue(singleIndex);
                if ((sudoku.getCell(index) & Sudoku2.MASKS[value]) != 0) {
                    this.anzHS++;
                    valid = sudoku.setCell(index, value, false, false);
                }
            }
        } while (valid && (!nsQueue.isEmpty() || !hsQueue.isEmpty()));

        return valid;
    }

    public int getSolutionCount() {
        return this.solutionCount;
    }

    public int[] getSolution() {
        return this.solution;
    }

    public String getSolutionAsString() {
        return this.getSolutionAsString(this.solution);
    }

    public String getSolutionAsString(int[] array) {
        StringBuilder temp = new StringBuilder();

        for (int i = 0; i < array.length; i++) {
            temp.append("").append(array[i]);
        }

        return temp.toString();
    }

    private String getGridStr(Sudoku2 sudoku) {
        return this.getSolutionAsString(sudoku.getValues());
    }

    public String printStat() {
        return "anzTries: " + this.anzTries + ", anzNS: " + this.anzNS + ", anzHS: " + this.anzHS;
    }

    private class RecursionStackEntry {
        Sudoku2 sudoku = new Sudoku2();
        int index;
        int[] candidates;
        int candIndex;

        private RecursionStackEntry() {
        }
    }
}
