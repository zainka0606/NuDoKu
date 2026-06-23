package sudoku;

public class SudokuCell {
    public static final int USER = 0;
    public static final int PLAY = 1;
    public static final int ALL = 2;
    private static final short M_1 = 1;
    private static final short M_2 = 2;
    private static final short M_3 = 4;
    private static final short M_4 = 8;
    private static final short M_5 = 16;
    private static final short M_6 = 32;
    private static final short M_7 = 64;
    private static final short M_8 = 128;
    private static final short M_9 = 256;
    private static final short M_ALL = 511;
    private static final short[] masks = new short[]{1, 2, 4, 8, 16, 32, 64, 128, 256};
    private byte value = 0;
    private boolean isFixed = false;
    private short[] candidates = new short[3];

    public SudokuCell() {
    }

    public SudokuCell(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return this.value;
    }

    public void setValue(byte value) {
        this.value = value;
    }

    public boolean isIsFixed() {
        return this.isFixed;
    }

    public void setIsFixed(boolean isFixed) {
        this.isFixed = isFixed;
    }

    public short[] getCandidates() {
        return this.candidates;
    }

    public void setCandidates(short[] candidates) {
        this.candidates = candidates;
    }

    public String getCandidateString(int type) {
        StringBuilder tmp = new StringBuilder();

        for (int i = 1; i <= 9; i++) {
            if (this.isCandidate(type, i)) {
                tmp.append(i);
            }
        }

        return tmp.toString();
    }

    protected boolean isCandidate(int type, int value) {
        short mask = masks[value - 1];
        return (this.candidates[type] & mask) != 0;
    }
}
