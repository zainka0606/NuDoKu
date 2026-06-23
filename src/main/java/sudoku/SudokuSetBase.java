package sudoku;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SudokuSetBase implements Cloneable, Serializable {
    public static final SudokuSet EMPTY_SET = new SudokuSet();
    public static final long[] MASKS = new long[]{
            1L,
            2L,
            4L,
            8L,
            16L,
            32L,
            64L,
            128L,
            256L,
            512L,
            1024L,
            2048L,
            4096L,
            8192L,
            16384L,
            32768L,
            65536L,
            131072L,
            262144L,
            524288L,
            1048576L,
            2097152L,
            4194304L,
            8388608L,
            16777216L,
            33554432L,
            67108864L,
            134217728L,
            268435456L,
            536870912L,
            1073741824L,
            2147483648L,
            4294967296L,
            8589934592L,
            17179869184L,
            34359738368L,
            68719476736L,
            137438953472L,
            274877906944L,
            549755813888L,
            1099511627776L,
            2199023255552L,
            4398046511104L,
            8796093022208L,
            17592186044416L,
            35184372088832L,
            70368744177664L,
            140737488355328L,
            281474976710656L,
            562949953421312L,
            1125899906842624L,
            2251799813685248L,
            4503599627370496L,
            9007199254740992L,
            18014398509481984L,
            36028797018963968L,
            72057594037927936L,
            144115188075855872L,
            288230376151711744L,
            576460752303423488L,
            1152921504606846976L,
            2305843009213693952L,
            4611686018427387904L,
            Long.MIN_VALUE
    };
    public static final long MAX_MASK1 = -1L;
    public static final long MAX_MASK2 = 131071L;
    private static final long serialVersionUID = 1L;
    protected long mask1 = 0L;
    protected long mask2 = 0L;
    protected boolean initialized = true;

    public SudokuSetBase() {
    }

    public SudokuSetBase(SudokuSetBase init) {
        this.set(init);
    }

    public SudokuSetBase(boolean full) {
        if (full) {
            this.setAll();
        }
    }

    public static boolean andEmpty(SudokuSetBase s1, SudokuSetBase s2) {
        return (s1.mask1 & s2.mask1) == 0L && (s1.mask2 & s2.mask2) == 0L;
    }

    public static void main(String[] args) {
        SudokuSetBase set = new SudokuSetBase();

        for (int i = 0; i < 81; i++) {
            set.add(i);
        }

        System.out.println(set);
    }

    public SudokuSetBase clone() {
        SudokuSetBase newSet = null;

        try {
            newSet = (SudokuSetBase) super.clone();
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error while cloning", ex);
        }

        return newSet;
    }

    public boolean isEmpty() {
        return this.mask1 == 0L && this.mask2 == 0L;
    }

    public void add(int value) {
        if (value >= 64) {
            this.mask2 = this.mask2 | MASKS[value - 64];
        } else {
            this.mask1 = this.mask1 | MASKS[value];
        }

        this.initialized = false;
    }

    public void remove(int value) {
        if (value >= 64) {
            this.mask2 = this.mask2 & ~MASKS[value - 64];
        } else {
            this.mask1 = this.mask1 & ~MASKS[value];
        }

        this.initialized = false;
    }

    public final void set(SudokuSetBase set) {
        this.mask1 = set.mask1;
        this.mask2 = set.mask2;
        this.initialized = false;
    }

    public void set(int[] data) {
        this.mask1 = 0L;
        this.mask2 = 0L;
        this.initialized = false;

        for (int i = 0; i < data.length; i++) {
            this.add(data[i]);
        }
    }

    public void set(long m1, long m2) {
        this.mask1 = m1;
        this.mask2 = m2;
        this.initialized = false;
    }

    public void set(int data) {
        this.mask1 = data;
        this.mask2 = 0L;
        this.initialized = false;
    }

    public boolean contains(int value) {
        return value >= 64 ? (this.mask2 & MASKS[value - 64]) != 0L : (this.mask1 & MASKS[value]) != 0L;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (!(o instanceof SudokuSetBase)) {
            return false;
        }

        SudokuSetBase s = (SudokuSetBase) o;
        return this.mask1 == s.mask1 && this.mask2 == s.mask2;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + (int) (this.mask1 ^ this.mask1 >>> 32);
        return 71 * hash + (int) (this.mask2 ^ this.mask2 >>> 32);
    }

    public void clear() {
        this.mask1 = this.mask2 = 0L;
        this.initialized = false;
    }

    public final void setAll() {
        this.mask1 = -1L;
        this.mask2 = 131071L;
        this.initialized = false;
    }

    public boolean intersects(SudokuSetBase b) {
        return (this.mask1 & b.mask1) != 0L || (this.mask2 & b.mask2) != 0L;
    }

    public boolean intersects(SudokuSetBase b, SudokuSetBase c) {
        boolean result = false;
        long mask = this.mask1 & b.mask1;
        if (mask != 0L) {
            result = true;
            c.mask1 |= mask;
            c.initialized = false;
        }

        mask = this.mask2 & b.mask2;
        if (mask != 0L) {
            result = true;
            c.mask2 |= mask;
            c.initialized = false;
        }

        return result;
    }

    public boolean contains(SudokuSetBase b) {
        return (b.mask1 & ~this.mask1) == 0L && (b.mask2 & ~this.mask2) == 0L;
    }

    public void or(SudokuSetBase b) {
        this.mask1 = this.mask1 | b.mask1;
        this.mask2 = this.mask2 | b.mask2;
        this.initialized = false;
    }

    public void orNot(SudokuSetBase set) {
        this.mask1 = this.mask1 | ~set.mask1;
        this.mask2 = this.mask2 | ~set.mask2;
        this.initialized = false;
    }

    public void and(SudokuSetBase set) {
        this.mask1 = this.mask1 & set.mask1;
        this.mask2 = this.mask2 & set.mask2;
        this.initialized = false;
    }

    public void andNot(SudokuSetBase set) {
        this.mask1 = this.mask1 & ~set.mask1;
        this.mask2 = this.mask2 & ~set.mask2;
        this.initialized = false;
    }

    public boolean andEquals(SudokuSetBase set) {
        long m1 = this.mask1 & set.mask1;
        long m2 = this.mask2 & set.mask2;
        return m1 == this.mask1 && m2 == this.mask2;
    }

    public boolean andNotEquals(SudokuSetBase set) {
        long m1 = this.mask1 & ~set.mask1;
        long m2 = this.mask2 & ~set.mask2;
        return m1 == this.mask1 && m2 == this.mask2;
    }

    public boolean andEmpty(SudokuSetBase set) {
        long m1 = this.mask1 & set.mask1;
        long m2 = this.mask2 & set.mask2;
        return m1 == 0L && m2 == 0L;
    }

    public void not() {
        this.mask1 = ~this.mask1;
        this.mask2 = ~this.mask2 & 131071L;
        this.initialized = false;
    }

    public void orAndAnd(SudokuSetBase s1, SudokuSetBase s2) {
        this.mask1 = this.mask1 | s1.mask1 & s2.mask1;
        this.mask2 = this.mask2 | s1.mask2 & s2.mask2;
        this.initialized = false;
    }

    public void setAnd(SudokuSetBase s1, SudokuSetBase s2) {
        this.mask1 = s1.mask1 & s2.mask1;
        this.mask2 = s1.mask2 & s2.mask2;
        this.initialized = false;
    }

    public void setOr(SudokuSetBase s1, SudokuSetBase s2) {
        this.mask1 = s1.mask1 | s2.mask1;
        this.mask2 = s1.mask2 | s2.mask2;
        this.initialized = false;
    }

    protected String pM(long mask) {
        return Long.toHexString(mask);
    }

    @Override
    public String toString() {
        int[] values = new int[81];
        int anz = 0;
        int index = 0;

        for (int i = 0; i < 64; i++) {
            if ((this.mask1 & MASKS[i]) != 0L) {
                values[index++] = i;
            }
        }

        for (int i = 0; i < 17; i++) {
            if ((this.mask2 & MASKS[i]) != 0L) {
                values[index++] = i + 64;
            }
        }

        this.initialized = true;
        anz = index;
        if (anz == 0) {
            return "empty!";
        }

        StringBuilder tmp = new StringBuilder();
        tmp.append(Integer.toString(values[0]));

        for (int i = 1; i < anz; i++) {
            tmp.append(" ").append(Integer.toString(values[i]));
        }

        tmp.append(" ").append(this.pM(this.mask1)).append("/").append(this.pM(this.mask2));
        return tmp.toString();
    }

    public long getMask1() {
        return this.mask1;
    }

    public void setMask1(long mask1) {
        this.mask1 = mask1;
    }

    public long getMask2() {
        return this.mask2;
    }

    public void setMask2(long mask2) {
        this.mask2 = mask2;
    }

    public boolean isInitialized() {
        return this.initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }
}
