package solver;

import sudoku.SolutionStep;
import sudoku.Sudoku2;
import sudoku.SudokuSet;

import java.util.ArrayList;
import java.util.List;

public class GroupNode {
    private static SudokuSet candInHouse = new SudokuSet();
    private static SudokuSet tmpSet = new SudokuSet();
    public SudokuSet indices = new SudokuSet();
    public SudokuSet buddies = new SudokuSet();
    public int cand;
    public int line = -1;
    public int col = -1;
    public int block;
    public int index1;
    public int index2;
    public int index3;

    public GroupNode(int cand, SudokuSet indices) {
        this.cand = cand;
        this.indices.set(indices);
        this.index1 = indices.get(0);
        this.index2 = indices.get(1);
        this.index3 = -1;
        if (indices.size() > 2) {
            this.index3 = indices.get(2);
        }

        this.block = Sudoku2.getBlock(this.index1);
        if (Sudoku2.getLine(this.index1) == Sudoku2.getLine(this.index2)) {
            this.line = Sudoku2.getLine(this.index1);
        }

        if (Sudoku2.getCol(this.index1) == Sudoku2.getCol(this.index2)) {
            this.col = Sudoku2.getCol(this.index1);
        }

        this.buddies.set(Sudoku2.buddies[this.index1]);
        this.buddies.and(Sudoku2.buddies[this.index2]);
        if (this.index3 >= 0) {
            this.buddies.and(Sudoku2.buddies[this.index3]);
        }
    }

    public static List<GroupNode> getGroupNodes(SudokuStepFinder finder) {
        List<GroupNode> groupNodes = new ArrayList<>();
        getGroupNodesForHouseType(groupNodes, finder, Sudoku2.LINE_TEMPLATES);
        getGroupNodesForHouseType(groupNodes, finder, Sudoku2.COL_TEMPLATES);
        return groupNodes;
    }

    private static void getGroupNodesForHouseType(List<GroupNode> groupNodes, SudokuStepFinder finder, SudokuSet[] houses) {
        for (int i = 0; i < houses.length; i++) {
            for (int cand = 1; cand <= 9; cand++) {
                candInHouse.set(houses[i]);
                candInHouse.and(finder.getCandidates()[cand]);
                if (!candInHouse.isEmpty()) {
                    for (int j = 0; j < Sudoku2.BLOCK_TEMPLATES.length; j++) {
                        tmpSet.set(candInHouse);
                        tmpSet.and(Sudoku2.BLOCK_TEMPLATES[j]);
                        if (!tmpSet.isEmpty() && tmpSet.size() >= 2) {
                            groupNodes.add(new GroupNode(cand, tmpSet));
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        Sudoku2 sudoku = new Sudoku2();
        sudoku.setSudoku(
                ":0000:x:.4..1..........5.6......3.15.38.2...7......2..........6..5.7....2.....1....3.14..:211 213 214 225 235 448 465 366 566 468 469::"
        );
        long ticks = System.currentTimeMillis();
        List<GroupNode> groupNodes = getGroupNodes(null);
        ticks = System.currentTimeMillis() - ticks;
        System.out.println("getGroupNodes(): " + ticks + "ms, " + groupNodes.size() + " group nodes");

        for (GroupNode node : groupNodes) {
            System.out.println("  " + node);
        }
    }

    @Override
    public String toString() {
        return "GroupNode: "
                + this.cand
                + " - "
                + SolutionStep.getCompactCellPrint(this.index1, this.index2, this.index3)
                + "  - "
                + this.index1
                + "/"
                + this.index2
                + "/"
                + this.index3
                + " ("
                + this.line
                + "/"
                + this.col
                + "/"
                + this.block
                + ")";
    }
}
