package sudoku;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class SudokuSetShort implements Cloneable, Serializable {
    public static final SudokuSetShort EMPTY_SET = new SudokuSetShort();
    public static final short[] MASKS = new short[]{0, 1, 2, 4, 8, 16, 32, 64, 128, 256};
    private static final long serialVersionUID = 1L;
    private static final short MAX_MASK = 511;
    private static final int[][] possibleValues = new int[512][];
    private static int[] anzValues = new int[512];

    static {
        possibleValues[0] = new int[0];
        anzValues[0] = 0;
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

            possibleValues[i] = new int[index];
            System.arraycopy(temp, 0, possibleValues[i], 0, index);
            anzValues[i] = index;
        }
    }

    boolean initialized = true;
    private short mask = 0;
    private int[] values = null;
    private int anz = 0;

    public SudokuSetShort() {
    }

    public SudokuSetShort(SudokuSetShort init) {
        this.set(init);
    }

    public SudokuSetShort(boolean full) {
        if (full) {
            this.setAll();
        }
    }

    public static void main(String[] args) {
        SudokuSetShort set = new SudokuSetShort();

        for (int i = 1; i <= 9; i++) {
            set.add(i);
        }

        System.out.println(set);
        set.clear();
        set.add(2);
        set.add(5);
        set.add(6);
        System.out.println(set);
    }

    public SudokuSetShort clone() {
        SudokuSetShort newSet = null;

        try {
            newSet = (SudokuSetShort) super.clone();
            this.values = null;
            this.initialized = false;
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error while cloning", ex);
        }

        return newSet;
    }

    public int get(int index) {
        if (!this.isInitialized()) {
            this.initialize();
        }

        return this.values[index];
    }

    public int size() {
        if (!this.isInitialized()) {
            this.initialize();
        }

        return this.anz;
    }

    public boolean isEmpty() {
        return this.mask == 0;
    }

    public void add(int value) {
        this.mask = (short) (this.mask | MASKS[value]);
        this.initialized = false;
    }

    public void remove(int value) {
        this.mask = (short) (this.mask & ~MASKS[value]);
        this.initialized = false;
    }

    public void set(SudokuSetShort set) {
        this.mask = set.mask;
        this.initialized = false;
    }

    public void set(int data) {
        this.mask = (short) data;
        this.initialized = false;
    }

    public boolean contains(int value) {
        return (this.mask & MASKS[value]) != 0;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (!(o instanceof SudokuSetShort)) {
            return false;
        }

        SudokuSetShort s = (SudokuSetShort) o;
        return this.mask == s.mask;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        return 79 * hash + this.mask;
    }

    public void clear() {
        this.mask = 0;
        this.anz = 0;
        this.initialized = false;
    }

    public void setAll() {
        this.mask = 511;
        this.initialized = false;
    }

    public void or(SudokuSetShort b) {
        this.mask = (short) (this.mask | b.mask);
        this.initialized = false;
    }

    public void orNot(SudokuSetShort set) {
        this.mask = (short) (this.mask | ~set.mask);
        this.initialized = false;
    }

    public void and(SudokuSetShort set) {
        this.mask = (short) (this.mask & set.mask);
        this.initialized = false;
    }

    public void andNot(SudokuSetShort set) {
        this.mask = (short) (this.mask & ~set.mask);
        this.initialized = false;
    }

    public boolean andEquals(SudokuSetShort set) {
        short m = (short) (this.mask & set.mask);
        return m == this.mask;
    }

    public boolean andNotEquals(SudokuSetShort set) {
        short m = (short) (this.mask & ~set.mask);
        return m == this.mask;
    }

    public boolean andEmpty(SudokuSetShort set) {
        short m = (short) (this.mask & set.mask);
        return m == 0;
    }

    public void not() {
        this.mask = (short) (~this.mask);
        this.initialized = false;
    }

    String pM(long mask) {
        return Long.toHexString(mask);
    }

    private void initialize() {
        this.values = possibleValues[this.mask];
        this.anz = anzValues[this.mask];
        this.initialized = true;
    }

    @Override
    public String toString() {
        if (!this.initialized) {
            this.initialize();
        }

        if (this.anz == 0) {
            return "empty!";
        }

        StringBuilder tmp = new StringBuilder();
        tmp.append(Integer.toString(this.values[0]));

        for (int i = 1; i < this.anz; i++) {
            tmp.append(" ").append(Integer.toString(this.values[i]));
        }

        return tmp.toString();
    }

    public long getMask1() {
        return this.mask;
    }

    public boolean isInitialized() {
        return this.initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public short getMask() {
        return this.mask;
    }

    public void setMask(short mask) {
        this.mask = mask;
    }

    public int[] getValues() {
        if (!this.initialized) {
            this.initialize();
        }

        return this.values;
    }

    public void setValues(int[] values) {
        this.values = values;
    }

    public int getAnz() {
        return this.anz;
    }

    public void setAnz(int anz) {
        this.anz = anz;
    }
}
