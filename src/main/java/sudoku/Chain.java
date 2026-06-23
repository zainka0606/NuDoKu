package sudoku;

import solver.Als;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Chain implements Cloneable {
    public static final int NORMAL_NODE = 0;
    public static final int GROUP_NODE = 1;
    public static final int ALS_NODE = 2;
    public static final String[] TYPE_NAMES = new String[]{"NORMAL_NODE", "GROUP_NODE", "ALS_NODE"};
    private static final int EQUALS_MASK = 1073741807;
    private static final int CAND_MASK = 15;
    private static final int STRONG_MASK = 16;
    private static final int INDEX_MASK = 127;
    private static final int INDEX1_MASK = 4064;
    private static final int INDEX1_OFFSET = 5;
    private static final int INDEX2_MASK = 520192;
    private static final int INDEX2_OFFSET = 12;
    private static final int INDEX3_MASK = 66584576;
    private static final int INDEX3_OFFSET = 19;
    private static final int ALS_INDEX_MASK = 67104768;
    private static final int ALS_INDEX_OFFSET = 12;
    private static final int NO_INDEX = 127;
    private static final int MODE_MASK = 1006632960;
    private static final int MODE_DEL_MASK = -1006632961;
    private static final int MODE_OFFSET = 26;
    private static final int NORMAL_NODE_MASK = 0;
    private static final int GROUP_NODE_MASK = 67108864;
    private static final int ALS_NODE_MASK = 134217728;
    private int start;
    private int end;
    private int length;
    private int[] chain;

    public Chain() {
    }

    public Chain(int start, int end, int[] chain) {
        this.start = start;
        this.end = end;
        this.chain = chain;
        this.length = -1;
    }

    public static int makeSEntry(int cellIndex, int candidate, boolean isStrong) {
        return makeSEntry(cellIndex, 0, 0, candidate, isStrong, 0);
    }

    public static int makeSEntry(int cellIndex, int candidate, boolean isStrong, int nodeType) {
        return makeSEntry(cellIndex, 0, 0, candidate, isStrong, nodeType);
    }

    public static int makeSEntry(int cellIndex, int alsIndex, int candidate, boolean isStrong, int nodeType) {
        int tmpIndex = getSHigherAlsIndex(alsIndex);
        alsIndex = getSLowerAlsIndex(alsIndex);
        return makeSEntry(cellIndex, alsIndex, tmpIndex, candidate, isStrong, nodeType);
    }

    public static int makeSEntry(int cellIndex1, int cellIndex2, int cellIndex3, int candidate, boolean isStrong, int nodeType) {
        int entry = cellIndex1 << 5 | candidate;
        if (isStrong) {
            entry |= 16;
        }

        if (nodeType != 0) {
            switch (nodeType) {
                case 1:
                    entry |= 67108864;
                    break;
                case 2:
                    entry |= 134217728;
            }
        }

        if (cellIndex2 == -1) {
            if (nodeType == 0) {
                cellIndex2 = 0;
            } else {
                cellIndex2 = 127;
            }
        }

        if (cellIndex3 == -1) {
            if (nodeType == 0) {
                cellIndex3 = 0;
            } else {
                cellIndex3 = 127;
            }
        }

        entry |= cellIndex2 << 12;
        return entry | cellIndex3 << 19;
    }

    public static int getSHigherAlsIndex(int alsIndex) {
        return alsIndex >> 7 & 127;
    }

    public static int getSLowerAlsIndex(int alsIndex) {
        int var1;
        return var1 = alsIndex & 127;
    }

    public static int getSCellIndex(int entry) {
        return entry > 0 ? entry >> 5 & 127 : -entry >> 5 & 127;
    }

    public static int getSCellIndex2(int entry) {
        int result = -1;
        if (entry > 0) {
            result = entry >> 12 & 127;
        } else {
            result = -entry >> 12 & 127;
        }

        if (result == 127) {
            result = -1;
        }

        return result;
    }

    public static int getSCellIndex3(int entry) {
        int result = -1;
        if (entry > 0) {
            result = entry >> 19 & 127;
        } else {
            result = -entry >> 19 & 127;
        }

        if (result == 127) {
            result = -1;
        }

        return result;
    }

    public static int getSAlsIndex(int entry) {
        int result = -1;
        if (entry < 0) {
            entry = -entry;
        }

        return (entry & 67104768) >> 12;
    }

    public static int replaceSAlsIndex(int entry, int newAlsIndex) {
        entry &= -67104769;
        newAlsIndex <<= 12;
        newAlsIndex &= 67104768;
        return entry | newAlsIndex;
    }

    public static int getSCandidate(int entry) {
        return entry > 0 ? entry & 15 : -entry & 15;
    }

    public static boolean isSStrong(int entry) {
        return entry > 0 ? (entry & 16) != 0 : (-entry & 16) != 0;
    }

    public static int getSNodeType(int entry) {
        return entry > 0 ? (entry & 1006632960) >> 26 : (-entry & 1006632960) >> 26;
    }

    public static int setSStrong(int entry, boolean strong) {
        if (strong) {
            entry |= 16;
        } else {
            entry &= -17;
        }

        return entry;
    }

    public static void getSNodeBuddies(int entry, int candidate, List<Als> alses, SudokuSetBase set) {
        if (getSNodeType(entry) == 0) {
            set.set(Sudoku2.buddies[getSCellIndex(entry)]);
        } else if (getSNodeType(entry) == 1) {
            set.set(Sudoku2.buddies[getSCellIndex(entry)]);
            set.and(Sudoku2.buddies[getSCellIndex2(entry)]);
            if (getSCellIndex3(entry) != -1) {
                set.and(Sudoku2.buddies[getSCellIndex3(entry)]);
            }
        } else if (getSNodeType(entry) == 2) {
            Als als = alses.get(getSAlsIndex(entry));
            set.set(als.buddiesPerCandidat[candidate]);
        } else {
            set.clear();
            Logger.getLogger(Chain.class.getName()).log(Level.SEVERE, "getSNodeBuddies() gesamt: invalid node type ({0})", getSNodeType(entry));
        }
    }

    public static String toString(int entry) {
        if (entry == Integer.MIN_VALUE) {
            return "MIN";
        }

        String sign = "";
        if (entry < 0) {
            sign = "-";
        }

        return getSNodeType(entry) == 2
                ? sign
                  + TYPE_NAMES[getSNodeType(entry)]
                  + "/"
                  + getSAlsIndex(entry)
                  + "/"
                  + getSCellIndex(entry)
                  + "/"
                  + isSStrong(entry)
                  + "/"
                  + getSCandidate(entry)
                : sign
                  + TYPE_NAMES[getSNodeType(entry)]
                  + "/"
                  + getSCellIndex3(entry)
                  + "/"
                  + getSCellIndex2(entry)
                  + "/"
                  + getSCellIndex(entry)
                  + "/"
                  + isSStrong(entry)
                  + "/"
                  + getSCandidate(entry);
    }

    public static void main(String[] args) {
        int entry = makeSEntry(0, 1, true);
        System.out.println("Entry: " + getSCellIndex(entry) + "/" + getSCandidate(entry) + "/" + isSStrong(entry));
    }

    @Override
    public Object clone() {
        try {
            Chain newChain = (Chain) super.clone();
            newChain.start = this.start;
            newChain.end = this.end;
            newChain.chain = Arrays.copyOf(this.chain, this.end + 1);
            return newChain;
        } catch (CloneNotSupportedException ex) {
            return null;
        }
    }

    public void reset() {
        this.start = 0;
        this.end = 0;
        this.length = -1;
    }

    public void resetLength() {
        this.length = -1;
    }

    public int getLength() {
        return this.getLength(null);
    }

    public int getLength(List<AlsInSolutionStep> alses) {
        if (this.length == -1) {
            this.length = this.calculateLength(alses);
        }

        return this.length;
    }

    private int calculateLength(List<AlsInSolutionStep> alses) {
        double tmpLength = 0.0;

        for (int i = this.start; i <= this.end; i++) {
            tmpLength++;
            if (getSNodeType(this.chain[i]) == 2) {
                if (alses != null) {
                    int alsIndex = getSAlsIndex(this.chain[i]);
                    if (alses.size() > alsIndex) {
                        tmpLength += alses.get(alsIndex).getChainPenalty();
                    } else {
                        tmpLength += 5.0;
                    }
                } else {
                    tmpLength += 2.5;
                }
            }
        }

        return (int) tmpLength;
    }

    public int getStart() {
        return this.start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return this.end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int[] getChain() {
        return this.chain;
    }

    public void setChain(int[] chain) {
        this.chain = chain;
    }

    public void setEntry(int index, int entry) {
        this.chain[index] = entry;
    }

    public void setEntry(int index, int cellIndex, int candidate, boolean isStrong) {
        this.setEntry(index, makeSEntry(cellIndex, candidate, isStrong));
    }

    public void replaceAlsIndex(int entryIndex, int newAlsIndex) {
        this.chain[entryIndex] = replaceSAlsIndex(this.chain[entryIndex], newAlsIndex);
    }

    public int getCellIndex(int index) {
        return getSCellIndex(this.chain[index]);
    }

    public int getCandidate(int index) {
        return getSCandidate(this.chain[index]);
    }

    public boolean isStrong(int index) {
        return isSStrong(this.chain[index]);
    }

    public int getNodeType(int index) {
        return getSNodeType(this.chain[index]);
    }

    public void getNodeBuddies(int index, SudokuSetBase set, List<Als> alses) {
        getSNodeBuddies(this.chain[index], this.getCandidate(index), alses, set);
    }

    @Override
    public String toString() {
        StringBuilder tmp = new StringBuilder();

        for (int i = this.start; i <= this.end; i++) {
            tmp.append(toString(this.chain[i]));
            tmp.append(" ");
        }

        return tmp.toString();
    }
}
