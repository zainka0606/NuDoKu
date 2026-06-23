package sudoku;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SudokuSinglesQueue implements Cloneable {
    private int[] indices = new int[243];
    private int[] values = new int[243];
    private int putIndex = 0;
    private int getIndex = 0;
    private int iterateIndex = 0;

    public SudokuSinglesQueue clone() {
        SudokuSinglesQueue newSudokuSinglesQueue = null;

        try {
            newSudokuSinglesQueue = (SudokuSinglesQueue) super.clone();
            newSudokuSinglesQueue.indices = (int[]) this.indices.clone();
            newSudokuSinglesQueue.values = (int[]) this.values.clone();
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error while cloning", ex);
        }

        return newSudokuSinglesQueue;
    }

    public void set(SudokuSinglesQueue src) {
        System.arraycopy(src.indices, 0, this.indices, 0, this.indices.length);
        System.arraycopy(src.values, 0, this.values, 0, this.values.length);
        this.getIndex = src.getIndex;
        this.putIndex = src.putIndex;
        this.iterateIndex = src.iterateIndex;
    }

    public boolean isEmpty() {
        return this.getIndex >= this.putIndex;
    }

    public void addSingle(int index, int value) {
        this.indices[this.putIndex] = index;
        this.values[this.putIndex++] = value;
    }

    public int getSingle() {
        if (this.getIndex >= this.putIndex) {
            return -1;
        }

        int ret = this.getIndex++;
        if (this.getIndex >= this.putIndex) {
            this.getIndex = this.putIndex = 0;
        }

        return ret;
    }

    public int getIndex(int queueIndex) {
        return this.indices[queueIndex];
    }

    public int getValue(int queueIndex) {
        return this.values[queueIndex];
    }

    public int getFirstIndex() {
        this.iterateIndex = this.getIndex;
        return this.getNextIndex();
    }

    public int getNextIndex() {
        return this.iterateIndex >= this.putIndex ? -1 : this.iterateIndex++;
    }

    public void deleteNakedSingle(int index) {
        for (int i = this.getIndex; i < this.putIndex; i++) {
            if (this.indices[i] == index) {
                for (int j = i + 1; j < this.putIndex; j++) {
                    this.indices[j - 1] = this.indices[j];
                    this.values[j - 1] = this.values[j];
                }

                this.putIndex--;
                break;
            }
        }
    }

    public void deleteHiddenSingle(int constraint, int value) {
        for (int i = this.getIndex; i < this.putIndex; i++) {
            int actIndex = this.indices[i];
            if (this.values[i] == value
                    && (
                    Sudoku2.CONSTRAINTS[actIndex][0] == constraint
                            || Sudoku2.CONSTRAINTS[actIndex][1] == constraint
                            || Sudoku2.CONSTRAINTS[actIndex][2] == constraint
            )) {
                for (int j = i + 1; j < this.putIndex; j++) {
                    this.indices[j - 1] = this.indices[j];
                    this.values[j - 1] = this.values[j];
                }

                this.putIndex--;
                break;
            }
        }
    }

    public void clear() {
        this.getIndex = this.putIndex = 0;
    }

    @Override
    public String toString() {
        StringBuilder tmp = new StringBuilder();
        tmp.append("Singles Queue START\r\n");

        for (int i = this.getIndex; i < this.putIndex; i++) {
            tmp.append("   ").append(this.indices[i]).append("/").append(this.values[i]).append("\r\n");
        }

        tmp.append("Singles Queue END\r\n");
        return tmp.toString();
    }
}
