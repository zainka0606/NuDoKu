package solver;

import sudoku.SolutionStep;
import sudoku.Sudoku2;
import sudoku.SudokuSet;

public class Als {
    public SudokuSet indices;
    public short candidates;
    public SudokuSet[] indicesPerCandidat = new SudokuSet[10];
    public SudokuSet[] buddiesPerCandidat = new SudokuSet[10];
    public SudokuSet[] buddiesAlsPerCandidat = new SudokuSet[10];
    public SudokuSet buddies;
    public int chainPenalty = -1;

    public Als(SudokuSet indices, short candidates) {
        this.indices = new SudokuSet(indices);
        this.candidates = candidates;
    }

    public static int getChainPenalty(int candSize) {
        if (candSize == 0 || candSize == 1) {
            return 0;
        } else {
            return candSize == 2 ? candSize - 1 : (candSize - 1) * 2;
        }
    }

    public void computeFields(SudokuStepFinder finder) {
        this.buddies = new SudokuSet();

        for (int i = 1; i <= 9; i++) {
            if ((this.candidates & Sudoku2.MASKS[i]) != 0) {
                SudokuSet sudokuCandidates = finder.getCandidates()[i];
                this.indicesPerCandidat[i] = new SudokuSet(this.indices);
                this.indicesPerCandidat[i].and(sudokuCandidates);
                this.buddiesPerCandidat[i] = new SudokuSet();
                Sudoku2.getBuddies(this.indicesPerCandidat[i], this.buddiesPerCandidat[i]);
                this.buddiesPerCandidat[i].andNot(this.indices);
                this.buddiesPerCandidat[i].and(finder.getCandidates()[i]);
                this.buddiesAlsPerCandidat[i] = new SudokuSet(this.buddiesPerCandidat[i]);
                this.buddiesAlsPerCandidat[i].or(this.indicesPerCandidat[i]);
                this.buddies.or(this.buddiesPerCandidat[i]);
            }
        }
    }

    public int getChainPenalty() {
        if (this.chainPenalty == -1) {
            this.chainPenalty = getChainPenalty(Sudoku2.ANZ_VALUES[this.candidates]);
        }

        return this.chainPenalty;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (!(o instanceof Als)) {
            return false;
        }

        Als a = (Als) o;
        return this.indices.equals(a.indices);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return 71 * hash + (this.indices != null ? this.indices.hashCode() : 0);
    }

    @Override
    public String toString() {
        return "ALS: " + SolutionStep.getAls(this);
    }
}
