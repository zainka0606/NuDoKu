package sudoku;

import generator.SudokuGenerator;
import generator.SudokuGeneratorFactory;

import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Sudoku2 implements Cloneable {
    public static final int LENGTH = 81;
    public static final int UNITS = 9;
    public static final int BLOCK = 0;
    public static final int LINE = 1;
    public static final int COL = 2;
    public static final int CELL = 3;
    public static final int[][] LINES = new int[][]{
            {0, 1, 2, 3, 4, 5, 6, 7, 8},
            {9, 10, 11, 12, 13, 14, 15, 16, 17},
            {18, 19, 20, 21, 22, 23, 24, 25, 26},
            {27, 28, 29, 30, 31, 32, 33, 34, 35},
            {36, 37, 38, 39, 40, 41, 42, 43, 44},
            {45, 46, 47, 48, 49, 50, 51, 52, 53},
            {54, 55, 56, 57, 58, 59, 60, 61, 62},
            {63, 64, 65, 66, 67, 68, 69, 70, 71},
            {72, 73, 74, 75, 76, 77, 78, 79, 80}
    };
    public static final int[][] COLS = new int[][]{
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
    public static final int[][] BLOCKS = new int[][]{
            {0, 1, 2, 9, 10, 11, 18, 19, 20},
            {3, 4, 5, 12, 13, 14, 21, 22, 23},
            {6, 7, 8, 15, 16, 17, 24, 25, 26},
            {27, 28, 29, 36, 37, 38, 45, 46, 47},
            {30, 31, 32, 39, 40, 41, 48, 49, 50},
            {33, 34, 35, 42, 43, 44, 51, 52, 53},
            {54, 55, 56, 63, 64, 65, 72, 73, 74},
            {57, 58, 59, 66, 67, 68, 75, 76, 77},
            {60, 61, 62, 69, 70, 71, 78, 79, 80}
    };
    public static final int[][] ALL_UNITS = new int[][]{
            LINES[0],
            LINES[1],
            LINES[2],
            LINES[3],
            LINES[4],
            LINES[5],
            LINES[6],
            LINES[7],
            LINES[8],
            COLS[0],
            COLS[1],
            COLS[2],
            COLS[3],
            COLS[4],
            COLS[5],
            COLS[6],
            COLS[7],
            COLS[8],
            BLOCKS[0],
            BLOCKS[1],
            BLOCKS[2],
            BLOCKS[3],
            BLOCKS[4],
            BLOCKS[5],
            BLOCKS[6],
            BLOCKS[7],
            BLOCKS[8]
    };
    public static SudokuSet[] ALL_CONSTRAINTS_TEMPLATES = new SudokuSet[ALL_UNITS.length];
    public static long[] ALL_CONSTRAINTS_TEMPLATES_M1 = new long[ALL_UNITS.length];
    public static long[] ALL_CONSTRAINTS_TEMPLATES_M2 = new long[ALL_UNITS.length];
    public static final int[][] LINE_BLOCK_UNITS = new int[][]{
            LINES[0],
            LINES[1],
            LINES[2],
            LINES[3],
            LINES[4],
            LINES[5],
            LINES[6],
            LINES[7],
            LINES[8],
            BLOCKS[0],
            BLOCKS[1],
            BLOCKS[2],
            BLOCKS[3],
            BLOCKS[4],
            BLOCKS[5],
            BLOCKS[6],
            BLOCKS[7],
            BLOCKS[8]
    };
    public static SudokuSet[] LINE_BLOCK_TEMPLATES = new SudokuSet[LINE_BLOCK_UNITS.length];
    public static final int[][] COL_BLOCK_UNITS = new int[][]{
            COLS[0],
            COLS[1],
            COLS[2],
            COLS[3],
            COLS[4],
            COLS[5],
            COLS[6],
            COLS[7],
            COLS[8],
            BLOCKS[0],
            BLOCKS[1],
            BLOCKS[2],
            BLOCKS[3],
            BLOCKS[4],
            BLOCKS[5],
            BLOCKS[6],
            BLOCKS[7],
            BLOCKS[8]
    };
    public static SudokuSet[] COL_BLOCK_TEMPLATES = new SudokuSet[COL_BLOCK_UNITS.length];
    public static final int[] CONSTRAINT_TYPE_FROM_CONSTRAINT = new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    public static final int[] CONSTRAINT_NUMBER_FROM_CONSTRAINT = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    public static final short[] MASKS = new short[]{0, 1, 2, 4, 8, 16, 32, 64, 128, 256};
    public static final short MAX_MASK = 511;
    public static final int[][] POSSIBLE_VALUES = new int[512][];
    public static final int[] ANZ_VALUES = new int[512];
    public static final short[] CAND_FROM_MASK = new short[512];
    private static final boolean DEBUG = false;
    private static final int[] BLOCK_FROM_INDEX = new int[]{
            0,
            0,
            0,
            1,
            1,
            1,
            2,
            2,
            2,
            0,
            0,
            0,
            1,
            1,
            1,
            2,
            2,
            2,
            0,
            0,
            0,
            1,
            1,
            1,
            2,
            2,
            2,
            3,
            3,
            3,
            4,
            4,
            4,
            5,
            5,
            5,
            3,
            3,
            3,
            4,
            4,
            4,
            5,
            5,
            5,
            3,
            3,
            3,
            4,
            4,
            4,
            5,
            5,
            5,
            6,
            6,
            6,
            7,
            7,
            7,
            8,
            8,
            8,
            6,
            6,
            6,
            7,
            7,
            7,
            8,
            8,
            8,
            6,
            6,
            6,
            7,
            7,
            7,
            8,
            8,
            8
    };
    public static int[][] CONSTRAINTS = new int[81][3];
    public static SudokuSetBase[] templates = new SudokuSetBase[46656];
    public static SudokuSet[] buddies = new SudokuSet[81];
    public static long[] buddiesM1 = new long[81];
    public static long[] buddiesM2 = new long[81];
    public static SudokuSetBase[][] groupedBuddies = new SudokuSetBase[11][256];
    public static long[][] groupedBuddiesM1 = new long[11][256];
    public static long[][] groupedBuddiesM2 = new long[11][256];
    public static SudokuSet[] LINE_TEMPLATES = new SudokuSet[LINES.length];
    public static SudokuSet[] COL_TEMPLATES = new SudokuSet[COLS.length];
    public static SudokuSet[] BLOCK_TEMPLATES = new SudokuSet[BLOCKS.length];

    static {
        long ticks = System.currentTimeMillis();
        initBuddies();
        ticks = System.currentTimeMillis() - ticks;
        ticks = System.currentTimeMillis();
        initTemplates();
        ticks = System.currentTimeMillis() - ticks;
        ticks = System.currentTimeMillis();
        initGroupedBuddies();
        ticks = System.currentTimeMillis() - ticks;
        POSSIBLE_VALUES[0] = new int[0];
        ANZ_VALUES[0] = 0;
        int[] temp = new int[9];

        for (int i = 1; i <= 511; i++) {
            int index = 0;
            int mask = 1;

            for (int j = 1; j <= 511; j++) {
                if ((i & mask) != 0) {
                    temp[index++] = j;
                }

                mask <<= 1;
            }

            POSSIBLE_VALUES[i] = new int[index];
            System.arraycopy(temp, 0, POSSIBLE_VALUES[i], 0, index);
            ANZ_VALUES[i] = index;
        }

        int index = 0;

        for (int line = 0; line < 9; line++) {
            int boxBase = 18 + line / 3 * 3;

            for (int col = 9; col < 18; col++) {
                CONSTRAINTS[index][0] = line;
                CONSTRAINTS[index][1] = col;
                CONSTRAINTS[index][2] = boxBase + col / 3 % 3;
                index++;
            }
        }

        for (int i = 1; i < CAND_FROM_MASK.length; i++) {
            short j = -1;

            while ((i & MASKS[++j]) == 0) {
            }

            CAND_FROM_MASK[i] = j;
        }
    }

    private short[] cells = new short[81];
    private short[] userCells = new short[81];
    private byte[][] free = new byte[ALL_UNITS.length][10];
    private int unsolvedCellsAnz;
    private int[] values = new int[81];
    private boolean[] fixed = new boolean[81];
    private int[] solution = new int[81];
    private boolean solutionSet = false;
    private DifficultyLevel level = null;
    private int score;
    private String initialState = null;
    private SudokuStatus status = SudokuStatus.EMPTY;
    private SudokuStatus statusGivens = SudokuStatus.EMPTY;
    private SudokuSinglesQueue nsQueue = new SudokuSinglesQueue();
    private SudokuSinglesQueue hsQueue = new SudokuSinglesQueue();

    public Sudoku2() {
        this.clearSudoku();
    }

    public static int getLine(int index) {
        return index / 9;
    }

    public static int getCol(int index) {
        return index % 9;
    }

    public static int getBlock(int index) {
        return BLOCK_FROM_INDEX[index];
    }

    public static int getIndex(int line, int col) {
        return line * 9 + col;
    }

    private static void initBuddies() {
        if (buddies[0] == null) {
            for (int i = 0; i < 81; i++) {
                buddies[i] = new SudokuSet();

                for (int j = 0; j < 81; j++) {
                    if (i != j && (getLine(i) == getLine(j) || getCol(i) == getCol(j) || getBlock(i) == getBlock(j))) {
                        buddies[i].add(j);
                    }
                }

                buddiesM1[i] = buddies[i].getMask1();
                buddiesM2[i] = buddies[i].getMask2();
            }

            for (int i = 0; i < 9; i++) {
                LINE_TEMPLATES[i] = new SudokuSet();

                for (int j = 0; j < LINES[i].length; j++) {
                    LINE_TEMPLATES[i].add(LINES[i][j]);
                }

                LINE_BLOCK_TEMPLATES[i] = LINE_TEMPLATES[i];
                ALL_CONSTRAINTS_TEMPLATES[i] = LINE_TEMPLATES[i];
                COL_TEMPLATES[i] = new SudokuSet();

                for (int j = 0; j < COLS[i].length; j++) {
                    COL_TEMPLATES[i].add(COLS[i][j]);
                }

                COL_BLOCK_TEMPLATES[i] = COL_TEMPLATES[i];
                ALL_CONSTRAINTS_TEMPLATES[i + 9] = COL_TEMPLATES[i];
                BLOCK_TEMPLATES[i] = new SudokuSet();

                for (int j = 0; j < BLOCKS[i].length; j++) {
                    BLOCK_TEMPLATES[i].add(BLOCKS[i][j]);
                }

                LINE_BLOCK_TEMPLATES[i + 9] = BLOCK_TEMPLATES[i];
                COL_BLOCK_TEMPLATES[i + 9] = BLOCK_TEMPLATES[i];
                ALL_CONSTRAINTS_TEMPLATES[i + 18] = BLOCK_TEMPLATES[i];
            }

            for (int i = 0; i < ALL_CONSTRAINTS_TEMPLATES.length; i++) {
                ALL_CONSTRAINTS_TEMPLATES_M1[i] = ALL_CONSTRAINTS_TEMPLATES[i].getMask1();
                ALL_CONSTRAINTS_TEMPLATES_M2[i] = ALL_CONSTRAINTS_TEMPLATES[i].getMask2();
            }
        }
    }

    private static void initGroupedBuddies() {
        for (int i = 0; i < 11; i++) {
            initGroupForGroupedBuddies(i * 8, groupedBuddies[i]);
        }

        for (int i = 0; i < groupedBuddies.length; i++) {
            for (int j = 0; j < groupedBuddies[i].length; j++) {
                groupedBuddiesM1[i][j] = groupedBuddies[i][j].getMask1();
                groupedBuddiesM2[i][j] = groupedBuddies[i][j].getMask2();
            }
        }
    }

    private static void initGroupForGroupedBuddies(int groupOffset, SudokuSetBase[] groupArray) {
        SudokuSet groupSet = new SudokuSet();

        for (int i = 0; i < 256; i++) {
            groupSet.clear();
            int mask = 1;

            for (int j = 0; j < 8; j++) {
                if ((i & mask) != 0 && groupOffset + j + 1 <= 81) {
                    groupSet.add(groupOffset + j);
                }

                mask <<= 1;
            }

            SudokuSetBase buddiesSet = new SudokuSetBase(true);

            for (int j = 0; j < groupSet.size(); j++) {
                buddiesSet.and(buddies[groupSet.get(j)]);
            }

            groupArray[i] = buddiesSet;
        }
    }

    public static void getBuddies(SudokuSetBase cells, SudokuSetBase buddiesOut) {
        buddiesOut.setAll();
        if (cells.mask1 != 0L) {
            int i = 0;

            for (int j = 0; i < 8; j += 8) {
                int mIndex = (int) (cells.mask1 >> j & 255L);
                buddiesOut.and(groupedBuddies[i][mIndex]);
                i++;
            }
        }

        if (cells.mask2 != 0L) {
            int i = 8;

            for (int j = 0; i < 11; j += 8) {
                int mIndex = (int) (cells.mask2 >> j & 255L);
                buddiesOut.and(groupedBuddies[i][mIndex]);
                i++;
            }
        }
    }

    public static void getBuddies(long mask1, long mask2, SudokuSetBase buddiesOut) {
        long outM1 = -1L;
        long outM2 = 131071L;
        if (mask1 != 0L) {
            int i = 0;

            for (int j = 0; i < 8; j += 8) {
                int mIndex = (int) (mask1 >> j & 255L);
                outM1 &= groupedBuddiesM1[i][mIndex];
                outM2 &= groupedBuddiesM2[i][mIndex];
                i++;
            }
        }

        if (mask2 != 0L) {
            int i = 8;

            for (int j = 0; i < 11; j += 8) {
                int mIndex = (int) (mask2 >> j & 255L);
                outM1 &= groupedBuddiesM1[i][mIndex];
                outM2 &= groupedBuddiesM2[i][mIndex];
                i++;
            }
        }

        buddiesOut.set(outM1, outM2);
    }

    private static void initTemplates() {
        try {
            long ticks = System.currentTimeMillis();
            ObjectInputStream in = new ObjectInputStream(Sudoku2.class.getResourceAsStream("/templates.dat"));
            templates = (SudokuSetBase[]) in.readObject();
            in.close();
            ticks = System.currentTimeMillis() - ticks;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        for (int i = 0; i < LINES.length; i++) {
            for (int j = 0; j < LINES[i].length; j++) {
                LINE_TEMPLATES[i].add(LINES[i][j]);
                COL_TEMPLATES[i].add(COLS[i][j]);
                BLOCK_TEMPLATES[i].add(BLOCKS[i][j]);
            }
        }
    }

    public Sudoku2 clone() {
        Sudoku2 newSudoku = null;

        try {
            newSudoku = (Sudoku2) super.clone();
            newSudoku.cells = (short[]) this.cells.clone();
            newSudoku.userCells = (short[]) this.userCells.clone();
            newSudoku.values = (int[]) this.values.clone();
            newSudoku.solution = (int[]) this.solution.clone();
            newSudoku.fixed = (boolean[]) this.fixed.clone();
            newSudoku.free = new byte[this.free.length][];

            for (int i = 0; i < this.free.length; i++) {
                newSudoku.free[i] = (byte[]) this.free[i].clone();
            }

            if (this.initialState != null) {
                newSudoku.initialState = this.initialState;
            }

            newSudoku.nsQueue = this.nsQueue.clone();
            newSudoku.hsQueue = this.hsQueue.clone();
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error while cloning", ex);
        }

        return newSudoku;
    }

    public void set(Sudoku2 src) {
        System.arraycopy(src.cells, 0, this.cells, 0, 81);
        System.arraycopy(src.userCells, 0, this.userCells, 0, 81);
        System.arraycopy(src.values, 0, this.values, 0, 81);
        System.arraycopy(src.solution, 0, this.solution, 0, 81);
        System.arraycopy(src.fixed, 0, this.fixed, 0, 81);

        for (int i = 0; i < this.free.length; i++) {
            System.arraycopy(src.free[i], 0, this.free[i], 0, 10);
        }

        this.unsolvedCellsAnz = src.unsolvedCellsAnz;
        this.solutionSet = src.solutionSet;
        this.score = src.score;
        this.level = src.level;
        if (src.initialState != null) {
            this.initialState = src.initialState;
        }

        this.status = src.status;
        this.statusGivens = src.statusGivens;
        this.nsQueue.set(src.nsQueue);
        this.hsQueue.set(src.hsQueue);
    }

    public void setBS(Sudoku2 src) {
        this.cells = Arrays.copyOf(src.cells, this.cells.length);
        this.values = Arrays.copyOf(src.values, this.values.length);

        for (int i = 0; i < this.free.length; i++) {
            this.free[i] = Arrays.copyOf(src.free[i], this.free[i].length);
        }

        this.unsolvedCellsAnz = src.unsolvedCellsAnz;
        this.nsQueue.clear();
        this.hsQueue.clear();
    }

    public final void clearSudoku() {
        for (int i = 0; i < this.cells.length; i++) {
            this.cells[i] = 511;
            this.userCells[i] = 0;
        }

        for (int i = 0; i < this.free.length; i++) {
            for (int j = 1; j < this.free[i].length; j++) {
                this.free[i][j] = 9;
            }
        }

        for (int i = 0; i < this.values.length; i++) {
            this.values[i] = 0;
            this.solution[i] = 0;
            this.fixed[i] = false;
        }

        this.unsolvedCellsAnz = 81;
        this.initialState = null;
        this.solutionSet = false;
        this.status = SudokuStatus.EMPTY;
        this.statusGivens = SudokuStatus.EMPTY;
        this.nsQueue.clear();
        this.hsQueue.clear();
    }

    public void resetSudoku() {
        if (this.initialState != null) {
            this.setSudoku(this.initialState, true);
        }
    }

    public void setSudoku(String init) {
        this.setSudoku(init, true);
    }

    public void setSudoku(String init, boolean saveInitialState) {
        this.clearSudoku();
        if (init != null) {
            String lineEnd = null;
            int[][] cands = new int[9][9];
            if (init.contains("\r\n")) {
                lineEnd = "\r\n";
            } else if (init.contains("\r")) {
                lineEnd = "\r";
            } else if (init.contains("\n")) {
                lineEnd = "\n";
            }

            String[] lines = null;
            if (lineEnd != null) {
                lines = init.split(lineEnd);
            } else {
                lines = new String[]{init};
            }

            int anzLines = lines.length;
            boolean libraryFormat = false;
            String libraryCandStr = null;
            if (anzLines == 1) {
                int anzDoppelpunkt = this.getAnzPatternInString(init, ":");
                if (anzDoppelpunkt == 6 || anzDoppelpunkt == 7) {
                    libraryFormat = true;
                    String[] libLines = init.split(":");
                    lines[0] = libLines[3];
                    if (libLines.length >= 5) {
                        libraryCandStr = libLines[4];
                    } else {
                        libraryCandStr = "";
                    }
                }
            }

            if (anzLines == 1 && lines[0].contains("#")) {
                String tmpStr = lines[0].substring(0, lines[0].indexOf("#")).trim();
                if (tmpStr.length() >= 81) {
                    lines[0] = tmpStr;
                }
            }

            if (anzLines == 1 && this.getAnzPatternInString(init, ",") >= 6) {
                String[] gsfLines = init.split(",");
                lines[0] = gsfLines[4];
            }

            boolean[] solvedButNotGivens = new boolean[81];
            if (libraryFormat) {
                StringBuilder tmp = new StringBuilder(lines[0]);

                for (int i = 0; i < tmp.length(); i++) {
                    char ch = tmp.charAt(i);
                    if (ch == '+') {
                        solvedButNotGivens[i] = true;
                        tmp.deleteCharAt(i);
                        if (i >= 0) {
                            i--;
                        }
                    }
                }
            }

            for (int i = 0; i < lines.length; i++) {
                if (lines[i] != null) {
                    StringBuilder tmp = new StringBuilder(lines[i].trim());
                    int tmpIndex = -1;

                    while ((tmpIndex = tmp.indexOf("---")) >= 0) {
                        if (tmpIndex > 0) {
                            char ch = tmp.charAt(tmpIndex - 1);
                            if (!Character.isDigit(ch) && ch != ' ' && ch != '|') {
                                tmpIndex--;
                            }
                        }

                        int endIndex = tmpIndex + 1;

                        while (endIndex < tmp.length() && tmp.charAt(endIndex) == '-') {
                            endIndex++;
                        }

                        if (endIndex < tmp.length() - 1) {
                            char ch = tmp.charAt(endIndex + 1);
                            if (!Character.isDigit(ch) && ch != ' ' && ch != '|') {
                                endIndex++;
                            }
                        }

                        tmp.delete(tmpIndex, endIndex + 1);
                    }

                    for (int j = 0; j < tmp.length(); j++) {
                        char ch = tmp.charAt(j);
                        if (ch == '|') {
                            tmp.setCharAt(j, ' ');
                        } else if (!Character.isDigit(ch) && ch != '.' && ch != ' ') {
                            tmp.deleteCharAt(j);
                            if (j >= 0) {
                                j--;
                            }
                        }
                    }

                    int index = 0;

                    while ((index = tmp.indexOf("  ")) != -1) {
                        tmp.deleteCharAt(index);
                    }

                    lines[i] = tmp.toString().trim();
                    if (lines[i].length() == 0) {
                        for (int j = i; j < lines.length - 1; j++) {
                            lines[j] = lines[j + 1];
                        }

                        lines[lines.length - 1] = null;
                        anzLines--;
                        i--;
                    }
                }
            }

            if (anzLines == 10) {
                anzLines--;
            }

            boolean logAgain = false;
            boolean ssGivensRead = false;
            String ssGivens = null;
            boolean ssCellsRead = false;
            String ssCells = null;

            while (anzLines > 9 && anzLines % 9 == 0) {
                if (!ssGivensRead) {
                    ssGivens = this.getSSString(lines);
                    ssGivensRead = true;
                    ssCellsRead = true;
                    ssCells = ssGivens;
                } else {
                    ssCells = this.getSSString(lines);
                    ssCellsRead = true;
                }

                logAgain = true;

                for (int i = 9; i < anzLines; i++) {
                    lines[i - 9] = lines[i];
                    if (i >= anzLines - 9) {
                        lines[i] = null;
                    }
                }

                anzLines -= 9;
            }

            if (logAgain) {
            }

            int sRow = 0;
            int sCol = 0;
            int sIndex = 0;
            boolean singleDigits = true;
            boolean isPmGrid = false;
            String sInit = lines[0];

            for (int i = 1; i < anzLines; i++) {
                sInit = sInit + " " + lines[i];
            }

            if (sInit.length() > 81) {
                singleDigits = false;
            }

            if (sInit.length() > 162) {
                isPmGrid = true;
            }

            while (sIndex < sInit.length()) {
                char ch = sInit.charAt(sIndex);

                while (sIndex < sInit.length() && !Character.isDigit(ch) && ch != '.') {
                    ch = sInit.charAt(++sIndex);
                }

                if (sIndex >= sInit.length()) {
                    break;
                }

                if (isPmGrid) {
                    if (ch == '.' || ch == '0') {
                        Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Invalid character: {0}", ch);
                        cands[sRow][sCol] = 0;
                        sIndex++;
                    } else if (singleDigits) {
                        cands[sRow][sCol] = Integer.parseInt(sInit.substring(sIndex, sIndex + 1));
                        sIndex++;
                    } else {
                        int endIndex = sInit.indexOf(" ", sIndex);
                        if (endIndex < 0) {
                            endIndex = sInit.length();
                        }

                        cands[sRow][sCol] = Integer.parseInt(sInit.substring(sIndex, endIndex));
                        sIndex = endIndex;
                    }
                } else {
                    if (Character.isDigit(ch) && Character.digit(ch, 10) > 0) {
                        boolean given = true;
                        if (libraryFormat) {
                            given = !solvedButNotGivens[sRow * 9 + sCol];
                        }

                        this.setCell(sRow, sCol, Character.digit(ch, 10), given);
                    }

                    sIndex++;
                }

                if (++sCol == 9) {
                    sCol = 0;
                    sRow++;
                }
            }

            if (isPmGrid) {
                int[] cands1 = new int[10];

                for (int row = 0; row < cands.length; row++) {
                    for (int col = 0; col < cands[row].length; col++) {
                        Arrays.fill(cands1, 0);

                        for (int sum = cands[row][col]; sum > 0; sum /= 10) {
                            cands1[sum % 10] = 1;
                        }

                        int cellIndex = getIndex(row, col);

                        for (int i = 1; i < cands1.length; i++) {
                            if (cands1[i] == 0 && this.isCandidate(cellIndex, i)) {
                                this.setCandidate(row, col, i, false);
                            } else if (cands1[i] == 1 && !this.isCandidate(cellIndex, i)) {
                                this.setCandidate(row, col, i, true);
                            }
                        }
                    }
                }

                for (int i = 0; i < this.values.length; i++) {
                    if (this.getAnzCandidates(i) == 1) {
                        if (ssCellsRead) {
                            char ch = ssCells.charAt(i);
                            if (ch != '0' && ch != '.') {
                                this.setCell(i, Character.digit(ch, 10), true);
                            }
                        } else {
                            for (int j = 1; j <= 9; j++) {
                                if (this.isCandidate(i, j)) {
                                    int count = 0;

                                    for (int k = 0; k < buddies[i].size(); k++) {
                                        int buddyIndex = buddies[i].get(k);
                                        if (this.values[buddyIndex] == 0 && this.isCandidate(buddyIndex, j)) {
                                            count++;
                                            break;
                                        }
                                    }

                                    if (count == 0) {
                                        this.setCell(i, j, true);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (libraryFormat && libraryCandStr.length() > 0) {
                String[] candArr = libraryCandStr.split(" ");

                for (int i = 0; i < candArr.length; i++) {
                    if (candArr[i].length() != 0) {
                        int candPos = Integer.parseInt(candArr[i]);
                        int col = candPos % 10;
                        candPos /= 10;
                        int row = candPos % 10;
                        candPos /= 10;
                        this.setCandidate(row - 1, col - 1, candPos, false);
                    }
                }
            }

            if (ssGivensRead) {
                this.setGivens(ssGivens);
            }

            if (saveInitialState) {
                this.setInitialState(this.getSudoku(ClipboardMode.LIBRARY));
            }

            this.status = SudokuStatus.VALID;
            this.statusGivens = SudokuStatus.VALID;
        }
    }

    private String getSSString(String[] lines) {
        StringBuilder ssTemp = new StringBuilder();

        for (int i = 0; i < 9; i++) {
            ssTemp.append(lines[i]);
        }

        for (int i = 0; i < ssTemp.length(); i++) {
            char ch = ssTemp.charAt(i);
            if (!Character.isDigit(ch) && ch != '.') {
                ssTemp.deleteCharAt(i);
                i--;
            }
        }

        return ssTemp.toString();
    }

    public boolean checkUserCands() {
        if (!this.solutionSet) {
            return false;
        }

        for (int i = 0; i < 81; i++) {
            if (this.values[i] == 0 && (this.userCells[i] & MASKS[this.solution[i]]) == 0) {
                return false;
            }
        }

        return true;
    }

    public int getAnzCandidates(int index) {
        return ANZ_VALUES[this.cells[index]];
    }

    public int[] getAllCandidates(int index) {
        return POSSIBLE_VALUES[this.cells[index]];
    }

    public int[] getAllCandidates(int index, boolean user) {
        return user ? POSSIBLE_VALUES[this.userCells[index]] : this.getAllCandidates(index);
    }

    public int getAnzCandidates(int index, boolean user) {
        return user ? ANZ_VALUES[this.userCells[index]] : this.getAnzCandidates(index);
    }

    private int getAnzPatternInString(String str, String pattern) {
        int anzPattern = 0;
        int index = -1;

        while ((index = str.indexOf(pattern, index + 1)) >= 0) {
            anzPattern++;
        }

        return anzPattern;
    }

    public void rebuildInternalData() {
        this.nsQueue.clear();
        this.hsQueue.clear();

        for (int i = 0; i < this.free.length; i++) {
            for (int j = 0; j < this.free[i].length; j++) {
                this.free[i][j] = 0;
            }
        }

        int anz = 0;

        for (int index = 0; index < this.values.length; index++) {
            if (this.values[index] != 0) {
                this.cells[index] = 0;
            } else {
                anz++;
                int[] cands = POSSIBLE_VALUES[this.cells[index]];

                for (int i = 0; i < cands.length; i++) {
                    for (int j = 0; j < CONSTRAINTS[index].length; j++) {
                        this.free[CONSTRAINTS[index][j]][cands[i]]++;
                    }
                }

                if (ANZ_VALUES[this.cells[index]] == 1) {
                    this.addNakedSingle(index, CAND_FROM_MASK[this.cells[index]]);
                }
            }
        }

        this.unsolvedCellsAnz = anz;

        for (int i = 0; i < this.free.length; i++) {
            for (int j = 1; j <= 9; j++) {
                if (this.free[i][j] == 1) {
                    while (!this.addHiddenSingle(i, j)) {
                    }
                }
            }
        }
    }

    public boolean checkSudoku() {
        this.rebuildInternalData();

        for (int index = 0; index < this.values.length; index++) {
            if (this.values[index] != 0) {
                if (!this.isValidValue(index, this.values[index])) {
                    return false;
                }

                if (this.solutionSet && this.solution[index] != this.values[index]) {
                    return false;
                }
            } else {
                int[] cands = POSSIBLE_VALUES[this.cells[index]];

                for (int i = 0; i < cands.length; i++) {
                    if (!this.isValidValue(index, cands[i])) {
                        return false;
                    }
                }

                if (this.solutionSet && !this.isCandidate(index, this.solution[index])) {
                    return false;
                }
            }
        }

        return true;
    }

    public String getSudoku(ClipboardMode mode) {
        return this.getSudoku(mode, null);
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
            for (int i = 0; i < 81; i++) {
                if (this.getValue(i) != 0 && (mode != ClipboardMode.CLUES_ONLY || this.isFixed(i))) {
                    if (mode == ClipboardMode.LIBRARY && !this.isFixed(i)) {
                        out.append("+");
                    }

                    out.append(Integer.toString(this.getValue(i)));
                } else {
                    out.append(dot);
                }
            }
        }

        if (mode == ClipboardMode.PM_GRID
                || mode == ClipboardMode.PM_GRID_WITH_STEP
                || mode == ClipboardMode.CLUES_ONLY_FORMATTED
                || mode == ClipboardMode.VALUES_ONLY_FORMATTED) {
            StringBuilder[] cellBuffers = new StringBuilder[this.cells.length];

            for (int i = 0; i < this.cells.length; i++) {
                cellBuffers[i] = new StringBuilder();
                int value = this.getValue(i);
                if (mode == ClipboardMode.CLUES_ONLY_FORMATTED && !this.isFixed(i)) {
                    value = 0;
                }

                if (value != 0) {
                    cellBuffers[i].append(String.valueOf(value));
                } else {
                    String candString = "";
                    if (mode != ClipboardMode.CLUES_ONLY_FORMATTED && mode != ClipboardMode.VALUES_ONLY_FORMATTED) {
                        candString = this.getCandidateString(i);
                    }

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

            String separator = System.getProperty("line.separator");

            for (int i = 0; i < 9; i++) {
                if (i % 3 == 0) {
                    this.writeLine(out, i, fieldLengths, null, true, separator);
                }

                this.writeLine(out, i, fieldLengths, cellBuffers, false, separator);
            }

            this.writeLine(out, 9, fieldLengths, null, true, separator);
            if (mode == ClipboardMode.PM_GRID_WITH_STEP && step != null) {
                out.append(step.toString(2));
            }
        }

        if (mode == ClipboardMode.LIBRARY) {
            boolean first = true;
            out.append(":");

            for (int i = 0; i < this.cells.length; i++) {
                if (this.getValue(i) == 0) {
                    for (int j = 1; j <= 9; j++) {
                        if (this.isValidValue(i, j) && !this.isCandidate(i, j)) {
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

    private void writeLine(StringBuilder out, int line, int[] fieldLengths, StringBuilder[] cellBuffers, boolean drawOutline, String separator) {
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

        out.append(separator);
    }

    public int getValue(int line, int col) {
        return this.getValue(getIndex(line, col));
    }

    public int getValue(int index) {
        return this.values[index];
    }

    public int getSolution(int line, int col) {
        return this.getSolution(getIndex(line, col));
    }

    public int getSolution(int index) {
        return !this.solutionSet ? 0 : this.solution[index];
    }

    public boolean isFixed(int line, int col) {
        return this.isFixed(getIndex(line, col));
    }

    public boolean isFixed(int index) {
        return this.fixed[index];
    }

    public void setIsFixed(int index, boolean isFixed) {
        this.fixed[index] = isFixed;
    }

    public boolean isCandidate(int line, int col, int cand) {
        return this.isCandidate(getIndex(line, col), cand);
    }

    public boolean isCandidate(int index, int cand) {
        return (this.cells[index] & MASKS[cand]) != 0;
    }

    public boolean isCandidate(int line, int col, int cand, boolean user) {
        return this.isCandidate(getIndex(line, col), cand, user);
    }

    public boolean isCandidate(int index, int cand, boolean user) {
        return user ? (this.userCells[index] & MASKS[cand]) != 0 : this.isCandidate(index, cand);
    }

    public boolean isCandidateValid(int index, int value, boolean user) {
        return this.isCandidate(index, value, user) && this.isValidValue(index, value);
    }

    public boolean areCandidatesValid(int index, boolean[] candidates, boolean user) {
        if (this.values[index] != 0) {
            return false;
        }

        if (candidates[candidates.length - 1]) {
            return this.getAnzCandidates(index) == 2;
        }

        if (!Options.getInstance().isUseOrInsteadOfAndForFilter()) {
            for (int i = 1; i < candidates.length - 1; i++) {
                if (candidates[i] && !this.isCandidate(index, i, user)) {
                    return false;
                }
            }

            return true;
        } else {
            for (int i = 1; i < candidates.length; i++) {
                if (candidates[i] && this.isCandidate(index, i, user)) {
                    return true;
                }
            }

            return false;
        }
    }

    public String getCandidateString(int index) {
        StringBuilder tmp = new StringBuilder();
        int[] cands = POSSIBLE_VALUES[this.cells[index]];

        for (int i = 0; i < cands.length; i++) {
            tmp.append(cands[i]);
        }

        return tmp.toString();
    }

    public void getCandidateSet(int line, int col, SudokuSet candSet) {
        this.getCandidateSet(getIndex(line, col), candSet);
    }

    public void getCandidateSet(int index, SudokuSet candSet) {
        candSet.set(this.cells[index] << 1);
    }

    public boolean delCandidate(int index, int value) {
        return this.setCandidate(index, value, false);
    }

    public boolean delCandidate(int index, int value, boolean user) {
        return this.setCandidate(index, value, false, user);
    }

    public void setCandidate(int index, int value) {
        this.setCandidate(index, value, true);
    }

    public boolean setCandidate(int line, int col, int value, boolean set) {
        return this.setCandidate(getIndex(line, col), value, set);
    }

    public boolean setCandidate(int index, int value, boolean set) {
        if (set) {
            if ((this.cells[index] & MASKS[value]) == 0) {
                this.cells[index] = (short) (this.cells[index] | MASKS[value]);
                int newAnz = ANZ_VALUES[this.cells[index]];
                if (newAnz == 1) {
                    this.addNakedSingle(index, value);
                } else if (newAnz == 2) {
                    this.nsQueue.deleteNakedSingle(index);
                }

                for (int i = 0; i < CONSTRAINTS[index].length; i++) {
                    int newFree = ++this.free[CONSTRAINTS[index][i]][value];
                    if (newFree == 1) {
                        this.addHiddenSingle(CONSTRAINTS[index][i], value);
                    } else if (newFree == 2) {
                        this.hsQueue.deleteHiddenSingle(CONSTRAINTS[index][i], value);
                    }
                }
            }
        } else if ((this.cells[index] & MASKS[value]) != 0) {
            this.cells[index] = (short) (this.cells[index] & ~MASKS[value]);
            if (this.cells[index] == 0) {
                return false;
            }

            if (ANZ_VALUES[this.cells[index]] == 1) {
                this.addNakedSingle(index, CAND_FROM_MASK[this.cells[index]]);
            }

            for (int i = 0; i < CONSTRAINTS[index].length; i++) {
                int newFree = --this.free[CONSTRAINTS[index][i]][value];
                if (newFree == 1) {
                    this.addHiddenSingle(CONSTRAINTS[index][i], value);
                } else if (newFree == 0) {
                    this.hsQueue.deleteHiddenSingle(CONSTRAINTS[index][i], value);
                }
            }
        }

        return true;
    }

    public boolean setCandidate(int index, int value, boolean set, boolean user) {
        boolean ret = this.setCandidate(index, value, set);
        if (user) {
            if (set) {
                this.userCells[index] = (short) (this.userCells[index] | MASKS[value]);
            } else {
                this.userCells[index] = (short) (this.userCells[index] & ~MASKS[value]);
            }
        }

        return ret;
    }

    public boolean setCell(int line, int col, int value) {
        return this.setCell(getIndex(line, col), value);
    }

    public boolean setCell(int index, int value) {
        return this.setCell(index, value, false, true);
    }

    public boolean setCell(int line, int col, int value, boolean isFixed) {
        return this.setCell(getIndex(line, col), value, isFixed, true);
    }

    public boolean setCell(int index, int value, boolean isFixed) {
        return this.setCell(index, value, isFixed, true);
    }

    public boolean setCell(int index, int value, boolean isFixed, boolean user) {
        if (value == 0) {
        }

        if (this.values[index] == value) {
            return true;
        }

        boolean valid = true;
        int oldValue = this.values[index];
        this.values[index] = value;
        this.fixed[index] = isFixed;
        if (value != 0) {
            int[] cands = POSSIBLE_VALUES[this.cells[index]];
            this.cells[index] = 0;
            if (user) {
                this.userCells[index] = 0;
            }

            this.unsolvedCellsAnz--;

            for (int i = 0; i < buddies[index].size(); i++) {
                int buddyIndex = buddies[index].get(i);
                if (!this.setCandidate(buddyIndex, value, false)) {
                    valid = false;
                }

                if (user) {
                    this.userCells[buddyIndex] = (short) (this.userCells[buddyIndex] & ~MASKS[value]);
                }
            }

            for (int i = 0; i < cands.length; i++) {
                int cand = cands[i];

                for (int j = 0; j < CONSTRAINTS[index].length; j++) {
                    int constr = CONSTRAINTS[index][j];
                    int newFree = --this.free[constr][cand];
                    if (newFree == 1 && value != cand) {
                        this.addHiddenSingle(constr, cand);
                    } else if (newFree == 0 && cand != value) {
                        valid = false;
                    }
                }
            }
        } else {
            for (int cand = 1; cand <= 9; cand++) {
                if (this.isValidValue(index, cand)) {
                    this.setCandidate(index, cand);
                }
            }

            for (int i = 0; i < buddies[index].size(); i++) {
                int buddyIndex = buddies[index].get(i);
                if (this.getValue(buddyIndex) == 0 && this.isValidValue(buddyIndex, oldValue)) {
                    this.setCandidate(buddyIndex, oldValue);
                }
            }

            this.rebuildInternalData();
        }

        return valid;
    }

    public void setCellBS(int index, int value) {
        this.values[index] = value;
        this.cells[index] = 0;

        for (int i = 0; i < buddies[index].size(); i++) {
            int buddyIndex = buddies[index].get(i);
            this.cells[buddyIndex] = (short) (this.cells[buddyIndex] & ~MASKS[value]);
        }
    }

    public boolean isValidValue(int line, int col, int value) {
        return this.isValidValue(getIndex(line, col), value);
    }

    public boolean isValidValue(int index, int value) {
        for (int i = 0; i < buddies[index].size(); i++) {
            if (this.values[buddies[index].get(i)] == value) {
                return false;
            }
        }

        return true;
    }

    public boolean isSolved() {
        return this.unsolvedCellsAnz == 0;
    }

    public int getSolvedCellsAnz() {
        return 81 - this.unsolvedCellsAnz;
    }

    public int getFixedCellsAnz() {
        int anz = 0;

        for (int i = 0; i < this.fixed.length; i++) {
            if (this.fixed[i]) {
                anz++;
            }
        }

        return anz;
    }

    public int getUnsolvedCandidatesAnz() {
        int anz = 0;

        for (int i = 0; i < this.cells.length; i++) {
            anz += ANZ_VALUES[this.cells[i]];
        }

        return anz;
    }

    public void setNoClues() {
        for (int i = 0; i < this.fixed.length; i++) {
            this.fixed[i] = false;
        }

        this.setStatusGivens(SudokuStatus.INVALID);
    }

    public void setGivens(String givens) {
        for (int i = 0; i < givens.length(); i++) {
            char ch = givens.charAt(i);
            if (Character.isDigit(ch) && ch != '0') {
                this.fixed[i] = true;
            } else {
                this.fixed[i] = false;
            }
        }

        Sudoku2 act = new Sudoku2();
        act.set(this);
        SudokuGenerator generator = SudokuGeneratorFactory.getDefaultGeneratorInstance();
        int anzSol = generator.getNumberOfSolutions(act);
        this.setStatusGivens(anzSol);
    }

    private void addNakedSingle(int index, int value) {
        this.nsQueue.addSingle(index, value);
    }

    private boolean addHiddenSingle(int constraint, int value) {
        for (int i = 0; i < ALL_UNITS[constraint].length; i++) {
            int hsIndex = ALL_UNITS[constraint][i];
            if (this.isCandidate(hsIndex, value)) {
                this.hsQueue.addSingle(hsIndex, value);
                return true;
            }
        }

        return false;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public DifficultyLevel getLevel() {
        return this.level;
    }

    public void setLevel(DifficultyLevel level) {
        this.level = level;
    }

    public String getInitialState() {
        return this.initialState;
    }

    public void setInitialState(String initialState) {
        this.initialState = initialState;
    }

    public int[] getValues() {
        return this.values;
    }

    public void setValues(int[] values) {
        this.values = values;
    }

    public int[] getSolution() {
        return this.solution;
    }

    public void setSolution(int[] solution) {
        this.solution = solution;
        this.solutionSet = true;
    }

    public boolean isSolutionSet() {
        return this.solutionSet;
    }

    public void setSolutionSet(boolean solutionSet) {
        this.solutionSet = solutionSet;
    }

    public boolean[] getFixed() {
        return this.fixed;
    }

    public void setFixed(boolean[] fixed) {
        this.fixed = fixed;
    }

    public short getCell(int index) {
        return this.cells[index];
    }

    public short[] getCells() {
        return this.cells;
    }

    public void setCells(short[] cells) {
        this.cells = cells;
    }

    public short[] getUserCells() {
        return this.userCells;
    }

    public void setUserCells(short[] userCells) {
        this.userCells = userCells;
    }

    public byte[][] getFree() {
        return this.free;
    }

    public void setFree(byte[][] free) {
        this.free = free;
    }

    public int getUnsolvedCellsAnz() {
        return this.unsolvedCellsAnz;
    }

    public void setUnsolvedCellsAnz(int unsolvedCellsAnz) {
        this.unsolvedCellsAnz = unsolvedCellsAnz;
    }

    public SudokuSinglesQueue getNsQueue() {
        return this.nsQueue;
    }

    public void setNsQueue(SudokuSinglesQueue nsQueue) {
        this.nsQueue = nsQueue;
    }

    public SudokuSinglesQueue getHsQueue() {
        return this.hsQueue;
    }

    public void setHsQueue(SudokuSinglesQueue hsQueue) {
        this.hsQueue = hsQueue;
    }

    public SudokuStatus getStatus() {
        return this.status;
    }

    public void setStatus(SudokuStatus status) {
        this.status = status;
    }

    public void setStatus(int numberOfSolutions) {
        switch (numberOfSolutions) {
            case 0:
                this.status = SudokuStatus.INVALID;
                break;
            case 1:
                this.status = SudokuStatus.VALID;
                break;
            default:
                this.status = SudokuStatus.MULTIPLE_SOLUTIONS;
        }
    }

    public SudokuStatus getStatusGivens() {
        return this.statusGivens;
    }

    public void setStatusGivens(SudokuStatus statusGivens) {
        this.statusGivens = statusGivens;
    }

    public void setStatusGivens(int numberOfSolutions) {
        switch (numberOfSolutions) {
            case 0:
                this.statusGivens = SudokuStatus.INVALID;
                break;
            case 1:
                this.statusGivens = SudokuStatus.VALID;
                break;
            default:
                this.statusGivens = SudokuStatus.MULTIPLE_SOLUTIONS;
        }
    }

    public boolean userCandidatesEmpty() {
        for (int i = 0; i < this.userCells.length; i++) {
            if (this.userCells[i] != 0) {
                return false;
            }
        }

        return true;
    }

    public void switchToAllCandidates() {
        for (int i = 0; i < this.userCells.length; i++) {
            if (this.values[i] == 0 && this.solution[i] != 0) {
                this.userCells[i] = (short) (this.userCells[i] | MASKS[this.solution[i]]);
            }
        }

        System.arraycopy(this.userCells, 0, this.cells, 0, 81);
        this.rebuildInternalData();
    }

    public void rebuildAllCandidates() {
        for (int i = 0; i < this.cells.length; i++) {
            if (this.values[i] != 0) {
                this.cells[i] = 0;
            } else {
                for (int cand = 1; cand <= 9; cand++) {
                    if (this.isValidValue(i, cand)) {
                        this.cells[i] = (short) (this.cells[i] | MASKS[cand]);
                    }
                }
            }
        }

        this.rebuildInternalData();
    }

    public void printSinglesQueues() {
        System.out.println("Naked Singles:\r\n");
        System.out.println(this.nsQueue);
        System.out.println("Hidden Singles:\r\n");
        System.out.println(this.hsQueue);
    }

    public short getRemainingCandidates() {
        short result = 0;

        for (int i = 0; i < this.cells.length; i++) {
            if (this.values[i] == 0) {
                result |= this.cells[i];
            }
        }

        return result;
    }
}
