package sudoku;

public class SudokuSet extends SudokuSetBase implements Cloneable {
    private static final long serialVersionUID = 1L;
    public static int[] anzValues = new int[256];
    private static int[][] possibleValues = new int[256][8];

    static {
        for (int i = 0; i < 256; i++) {
            int index = 0;
            int mask = 1;

            for (int j = 0; j < 8; j++) {
                if ((i & mask) != 0) {
                    possibleValues[i][index++] = j;
                }

                mask <<= 1;
            }

            anzValues[i] = index;
        }
    }

    private int[] values = null;
    private int anz = 0;

    public SudokuSet() {
    }

    public SudokuSet(SudokuSetBase init) {
        super(init);
    }

    public SudokuSet(boolean full) {
        super(full);
    }

    public static void main(String[] args) {
        SudokuSet a = new SudokuSet();
        a.add(5);
        a.add(1);
        a.add(7);
        a.add(3);
        a.add(0);
        System.out.println("a: " + a);
        SudokuSet b = new SudokuSet();
        b.add(2);
        b.add(4);
        b.add(3);
        System.out.println("b: " + b);
        System.out.println(a.intersects(b));
        SudokuSet c = new SudokuSet();
        c.add(0);
        c.add(1);
        c.add(5);
        c.add(7);
        c.add(10);
        System.out.println("c: " + c);
        SudokuSet fins = new SudokuSet();
        System.out.println("fins: " + fins);
        a.remove(5);
        System.out.println("a: " + a);
        a.remove(0);
        System.out.println("a: " + a);
        a.remove(7);
        System.out.println("a: " + a);
        a.remove(3);
        System.out.println("a: " + a);
        a.remove(12);
        System.out.println("a: " + a);
        a.remove(1);
        System.out.println("a: " + a);
        a.remove(12);
        System.out.println("a: " + a);
        a.add(70);
        a.add(10);
        a.add(80);
        System.out.println("a: " + a);
        a.clear();
        a.add(0);
        System.out.println("a: " + a);
    }

    public SudokuSet clone() {
        SudokuSet newSet = null;
        newSet = (SudokuSet) super.clone();
        this.values = null;
        this.initialized = false;
        return newSet;
    }

    public int get(int index) {
        if (!this.isInitialized()) {
            this.initialize();
        }

        return this.values[index];
    }

    public int size() {
        if (this.isEmpty()) {
            return 0;
        }

        if (!this.isInitialized()) {
            this.initialize();
        }

        return this.anz;
    }

    @Override
    public void clear() {
        super.clear();
        this.anz = 0;
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

    public boolean isCovered(SudokuSet s1, SudokuSet fins) {
        long m1 = ~s1.mask1 & this.mask1;
        long m2 = ~s1.mask2 & this.mask2;
        boolean covered = true;
        if (m1 != 0L) {
            covered = false;
            fins.mask1 = m1;
            fins.initialized = false;
        }

        if (m2 != 0L) {
            covered = false;
            fins.mask2 = m2;
            fins.initialized = false;
        }

        return covered;
    }

    private void initialize() {
        if (this.values == null) {
            this.values = new int[81];
        }

        int index = 0;
        if (this.mask1 != 0L) {
            for (int i = 0; i < 64; i += 8) {
                int mIndex = (int) (this.mask1 >> i & 255L);

                for (int j = 0; j < anzValues[mIndex]; j++) {
                    this.values[index++] = possibleValues[mIndex][j] + i;
                }
            }
        }

        if (this.mask2 != 0L) {
            for (int i = 0; i < 24; i += 8) {
                int mIndex = (int) (this.mask2 >> i & 255L);

                for (int j = 0; j < anzValues[mIndex]; j++) {
                    this.values[index++] = possibleValues[mIndex][j] + i + 64;
                }
            }
        }

        this.setInitialized(true);
        this.setAnz(index);
    }

    @Override
    public String toString() {
        if (!this.isInitialized()) {
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

        tmp.append(" ").append(this.pM(this.mask1)).append("/").append(this.pM(this.mask2));
        return tmp.toString();
    }

    public int getAnz() {
        return this.anz;
    }

    public void setAnz(int anz) {
        this.anz = anz;
    }
}
