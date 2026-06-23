package solver;

import sudoku.Chain;
import sudoku.Options;
import sudoku.SudokuSet;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TableEntry {
    private static final long EXPANDED = 2305843009213693952L;
    private static final long ON_TABLE = 4611686018427387904L;
    private static final long EXTENDED_TABLE = Long.MIN_VALUE;
    int index = 0;
    int[] entries = new int[Options.getInstance().getMaxTableEntryLength()];
    long[] retIndices = new long[Options.getInstance().getMaxTableEntryLength()];
    SudokuSet[] onSets = new SudokuSet[10];
    SudokuSet[] offSets = new SudokuSet[10];
    SortedMap<Integer, Integer> indices = new TreeMap<>();

    TableEntry() {
        for (int i = 0; i < this.onSets.length; i++) {
            this.onSets[i] = new SudokuSet();
            this.offSets[i] = new SudokuSet();
        }
    }

    public static long makeSRetIndex(long index1, long index2, long index3, long index4, long index5) {
        long tmp = 0L;
        if (index1 > 4096L) {
            index1 = 0L;
        }

        if (index2 > 1023L) {
            index2 = 0L;
        }

        if (index3 > 1023L) {
            index3 = 0L;
        }

        if (index4 > 1023L) {
            index4 = 0L;
        }

        if (index5 > 1023L) {
            index5 = 0L;
        }

        if (index2 > index1) {
            tmp = index2;
            index2 = index1;
            index1 = tmp;
        }

        if (index3 > index1) {
            tmp = index3;
            index3 = index1;
            index1 = tmp;
        }

        if (index4 > index1) {
            tmp = index4;
            index4 = index1;
            index1 = tmp;
        }

        if (index5 > index1) {
            tmp = index5;
            index5 = index1;
            index1 = tmp;
        }

        return (index5 << 42) + (index4 << 32) + (index3 << 22) + (index2 << 12) + index1;
    }

    public static int getSRetIndexAnz(long retIndex) {
        int anz = 1;
        retIndex >>= 12;

        for (int i = 0; i < 4; i++) {
            if ((retIndex & 1023L) != 0L) {
                anz++;
            }

            retIndex >>= 10;
        }

        return anz;
    }

    public static int getSRetIndex(long retIndex, int which) {
        if (which == 0) {
            return (int) (retIndex & 4095L);
        }

        int ret = (int) (retIndex >> which * 10 + 2 & 1023L);
        if (which == 5) {
            ret &= 511;
        }

        return ret;
    }

    void reset() {
        this.index = 0;
        this.entries[0] = 0;
        this.retIndices[0] = 0L;
        this.indices.clear();

        for (int i = 0; i < this.onSets.length; i++) {
            this.onSets[i].clear();
            this.offSets[i].clear();
        }

        for (int i = 0; i < this.entries.length; i++) {
            this.entries[i] = 0;
            this.retIndices[i] = 0L;
        }
    }

    void addEntry(int cellIndex, int cand, int penalty, boolean set) {
        this.addEntry(cellIndex, -1, -1, 0, cand, set, 0, 0, 0, 0, 0, penalty);
    }

    void addEntry(int cellIndex, int cand, boolean set) {
        this.addEntry(cellIndex, -1, -1, 0, cand, set, 0, 0, 0, 0, 0, 0);
    }

    void addEntry(int cellIndex, int cand, boolean set, int reverseIndex) {
        this.addEntry(cellIndex, -1, -1, 0, cand, set, reverseIndex, 0, 0, 0, 0, 0);
    }

    void addEntry(int cellIndex, int cand, boolean set, int ri1, int ri2, int ri3, int ri4, int ri5) {
        this.addEntry(cellIndex, -1, -1, 0, cand, set, ri1, ri2, ri3, ri4, ri5, 0);
    }

    void addEntry(int cellIndex1, int alsIndex, int nodeType, int cand, boolean set, int penalty) {
        this.addEntry(cellIndex1, Chain.getSLowerAlsIndex(alsIndex), Chain.getSHigherAlsIndex(alsIndex), nodeType, cand, set, 0, 0, 0, 0, 0, penalty);
    }

    void addEntry(int cellIndex1, int cellIndex2, int cellIndex3, int nodeType, int cand, boolean set, int ri1, int ri2, int ri3, int ri4, int ri5, int penalty) {
        if (this.index >= this.entries.length) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "addEntry(): TableEntry is already full!");
        } else if (nodeType != 0 || (!set || !this.onSets[cand].contains(cellIndex1)) && (set || !this.offSets[cand].contains(cellIndex1))) {
            int entry = Chain.makeSEntry(cellIndex1, cellIndex2, cellIndex3, cand, set, nodeType);
            this.entries[this.index] = entry;
            this.retIndices[this.index] = makeSRetIndex(ri1, ri2, ri3, ri4, ri5);
            if (ri1 < this.retIndices.length) {
                this.setDistance(this.index, this.getDistance(ri1) + 1);
            }

            if (nodeType == 0) {
                if (set) {
                    this.onSets[cand].add(cellIndex1);
                } else {
                    this.offSets[cand].add(cellIndex1);
                }
            }

            int distance = this.getDistance(this.index);
            distance += penalty;
            this.setDistance(this.index, distance);
            this.indices.put(entry, this.index);
            this.index++;
        }
    }

    int getEntry(int index) {
        return this.entries[index];
    }

    int getEntryIndex(int cellIndex, boolean set, int cand) {
        Integer ret = this.indices.get(Chain.makeSEntry(cellIndex, cand, set));
        return ret == null ? 0 : ret;
    }

    int getEntryIndex(int entry) {
        Integer tmp = this.indices.get(entry);
        if (tmp == null) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "tmp == null: {0}", entry);
        }

        return this.indices.get(entry);
    }

    boolean isFull() {
        return this.index == this.entries.length;
    }

    public int getCellIndex(int index) {
        return Chain.getSCellIndex(this.entries[index]);
    }

    public boolean isStrong(int index) {
        return Chain.isSStrong(this.entries[index]);
    }

    public int getCandidate(int index) {
        return Chain.getSCandidate(this.entries[index]);
    }

    public int getRetIndexAnz(int index) {
        return getSRetIndexAnz(this.retIndices[index]);
    }

    public int getRetIndex(int index, int which) {
        return getSRetIndex(this.retIndices[index], which);
    }

    public void setDistance(int index, int distance) {
        long tmp = distance & 511;
        this.retIndices[index] = this.retIndices[index] & -2301339409586323457L;
        this.retIndices[index] = this.retIndices[index] | tmp << 52;
    }

    public int getDistance(int index) {
        return getSRetIndex(this.retIndices[index], 5) & 511;
    }

    public boolean isExpanded(int index) {
        return (this.retIndices[index] & 2305843009213693952L) != 0L;
    }

    public void setExpanded(int index) {
        this.retIndices[index] = this.retIndices[index] | 2305843009213693952L;
    }

    public boolean isOnTable(int index) {
        return (this.retIndices[index] & 4611686018427387904L) != 0L;
    }

    public void setOnTable(int index) {
        this.retIndices[index] = this.retIndices[index] | 4611686018427387904L;
    }

    public boolean isExtendedTable(int index) {
        return (this.retIndices[index] & Long.MIN_VALUE) != 0L;
    }

    public void setExtendedTable(int index) {
        this.retIndices[index] = this.retIndices[index] | Long.MIN_VALUE;
    }

    public void setExtendedTable() {
        this.retIndices[this.index - 1] = this.retIndices[this.index - 1] | Long.MIN_VALUE;
    }

    public int getNodeType(int index) {
        return Chain.getSNodeType(this.entries[index]);
    }
}
