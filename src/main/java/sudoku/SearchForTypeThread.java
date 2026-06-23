package sudoku;

import generator.SudokuGenerator;
import generator.SudokuGeneratorFactory;
import solver.SudokuSolver;
import solver.SudokuSolverFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

class SearchForTypeThread extends Thread {
    private Main m;
    private List<StepType> typeList;
    private DifficultyLevel level;
    private int anz = 0;
    private int anzFound = 0;
    private String outFile = null;

    SearchForTypeThread(Main m, List<StepType> typeList, DifficultyLevel level, String outFile) {
        this.m = m;
        this.typeList = typeList;
        this.level = level;
        this.outFile = outFile;
    }

    private void appendPuzzleString(SearchForTypeThread.PuzzleType pType, boolean mode1) {
        int mode = mode1 ? pType.isPuzzleMode1 : pType.isPuzzleMode2;
        switch (mode) {
            case -1:
            default:
                break;
            case 0:
                pType.puzzleString = pType.puzzleString + " x";
                break;
            case 1:
            case 2:
                pType.puzzleString = pType.puzzleString + " ssts";
                break;
            case 3:
                pType.puzzleString = pType.puzzleString + " s";
        }
    }

    @Override
    public void run() {
        SearchForTypeThread.PuzzleType[] puzzleTypes = new SearchForTypeThread.PuzzleType[this.typeList.size()];
        StringBuilder pathBuffer = new StringBuilder();
        int index = 0;

        for (StepType tmpType : this.typeList) {
            pathBuffer.append(tmpType.type.getArgName()).append("_");
            puzzleTypes[index] = new SearchForTypeThread.PuzzleType(tmpType);
            index++;
        }

        if (this.level == null) {
            pathBuffer.deleteCharAt(pathBuffer.length() - 1);
        } else {
            pathBuffer.append(this.level.getName());
        }

        if (pathBuffer.length() > 50) {
            pathBuffer.delete(50, pathBuffer.length() - 1);
        }

        pathBuffer.append(".txt");
        if (this.outFile != null) {
            pathBuffer = new StringBuilder(this.outFile);
        }

        this.anz = 0;
        this.anzFound = 0;

        try {
            BufferedWriter out = null;
            if (!pathBuffer.toString().equals("stdout")) {
                out = new BufferedWriter(new FileWriter(pathBuffer.toString(), true));
            }

            SudokuGenerator generator = SudokuGeneratorFactory.getDefaultGeneratorInstance();
            SudokuSolver solver = SudokuSolverFactory.getDefaultSolverInstance();
            new Sudoku2();

            while (!this.isInterrupted()) {
                Sudoku2 newSudoku = generator.generateSudoku(false);
                Sudoku2 clonedSudoku = newSudoku.clone();
                solver.setSudoku(clonedSudoku);
                solver.solve();
                if (this.level == null || clonedSudoku.isSolved() && clonedSudoku.getLevel().getOrdinal() == this.level.getOrdinal()) {
                    if (puzzleTypes.length == 0) {
                        String txt = newSudoku.getSudoku(ClipboardMode.CLUES_ONLY);
                        if (out != null) {
                            out.write(txt + " #" + this.level.getName());
                            out.newLine();
                            out.flush();
                        }

                        System.out.println(txt + " #" + this.level.getName());
                        this.anzFound++;
                    }

                    for (int i = 0; i < puzzleTypes.length; i++) {
                        puzzleTypes[i].reset();
                    }

                    List<SolutionStep> steps = solver.getSteps();

                    for (int i = 0; i < steps.size(); i++) {
                        SolutionType type = steps.get(i).getType();

                        for (int j = 0; j < puzzleTypes.length; j++) {
                            if (type.equals(puzzleTypes[j].type.type)) {
                                int anzCandDel = steps.get(i).getAnzCandidatesToDelete();
                                if (puzzleTypes[j].anzCandDel < anzCandDel) {
                                    puzzleTypes[j].anzCandDel = anzCandDel;
                                }

                                StringBuilder stepName = new StringBuilder(" " + type.getArgName());
                                if (type.isFish()) {
                                    if (steps.get(i).getEndoFins().size() > 0) {
                                        stepName.append("e");
                                    }

                                    if (steps.get(i).getCannibalistic().size() > 0) {
                                        stepName.append("c");
                                    }
                                }

                                stepName.append("(").append(anzCandDel).append(")");
                                if (!puzzleTypes[j].immediatelyFollowed) {
                                    if (!puzzleTypes[j].typeSeen) {
                                        this.appendPuzzleString(puzzleTypes[j], true);
                                    } else {
                                        this.appendPuzzleString(puzzleTypes[j], false);
                                        puzzleTypes[j].isPuzzleMode2 = -1;
                                    }

                                    puzzleTypes[j].typeSeen = true;
                                    puzzleTypes[j].immediatelyFollowed = true;
                                }

                                puzzleTypes[j].puzzleString = puzzleTypes[j].puzzleString + stepName.toString();
                            } else {
                                puzzleTypes[j].immediatelyFollowed = false;
                                if (type.isSingle()) {
                                    if (puzzleTypes[j].typeSeen && puzzleTypes[j].isPuzzleMode2 == -1) {
                                        puzzleTypes[j].isPuzzleMode2 = 3;
                                    }

                                    if (puzzleTypes[j].isPuzzleMode1 == -1) {
                                        puzzleTypes[j].isPuzzleMode1 = 3;
                                    }
                                } else if (type.isSSTS()) {
                                    if (puzzleTypes[j].typeSeen) {
                                        if (puzzleTypes[j].isPuzzleMode2 == -1 || puzzleTypes[j].isPuzzleMode2 > 2) {
                                            puzzleTypes[j].isPuzzleMode2 = 1;
                                        }

                                        if (puzzleTypes[j].isPuzzleMode1 > 1) {
                                            puzzleTypes[j].isPuzzleMode1 = 1;
                                        }
                                    } else if (puzzleTypes[j].isPuzzleMode1 == 3 || puzzleTypes[j].isPuzzleMode1 == -1) {
                                        puzzleTypes[j].isPuzzleMode1 = 2;
                                    }
                                } else {
                                    if (puzzleTypes[j].typeSeen) {
                                        puzzleTypes[j].isPuzzleMode2 = 0;
                                    }

                                    puzzleTypes[j].isPuzzleMode1 = 0;
                                }
                            }
                        }
                    }

                    for (int i = 0; i < puzzleTypes.length; i++) {
                        String txt = null;
                        if (puzzleTypes[i].typeSeen && puzzleTypes[i].isPuzzleMode1 >= puzzleTypes[i].type.puzzleType) {
                            if (puzzleTypes[i].type.compType != -1) {
                                switch (puzzleTypes[i].type.compType) {
                                    case 0:
                                        if (puzzleTypes[i].anzCandDel != puzzleTypes[i].type.compAnz) {
                                            continue;
                                        }
                                        break;
                                    case 1:
                                        if (puzzleTypes[i].anzCandDel >= puzzleTypes[i].type.compAnz) {
                                            continue;
                                        }
                                        break;
                                    case 2:
                                        if (puzzleTypes[i].anzCandDel <= puzzleTypes[i].type.compAnz) {
                                            continue;
                                        }
                                }
                            }

                            this.appendPuzzleString(puzzleTypes[i], false);
                            if (txt == null) {
                                txt = newSudoku.getSudoku(ClipboardMode.CLUES_ONLY);
                            }

                            if (out != null) {
                                out.write(txt + " #" + puzzleTypes[i].puzzleString);
                                out.newLine();
                                out.flush();
                            }

                            System.out.println(txt + " #" + puzzleTypes[i].puzzleString);
                            this.anzFound++;
                        }
                    }

                    this.anz++;
                }
            }

            if (out != null) {
                out.close();
            }
        } catch (IOException ex) {
            System.out.println("Error writing sudoku file");
            ex.printStackTrace();
        }
    }

    public int getAnz() {
        return this.anz;
    }

    public int getAnzFound() {
        return this.anzFound;
    }

    private final class PuzzleType {
        StepType type;
        boolean typeSeen = false;
        boolean immediatelyFollowed = false;
        int isPuzzleMode1 = -1;
        int isPuzzleMode2 = -1;
        String puzzleString = "";
        int anzCandDel = 0;

        PuzzleType(StepType type) {
            this.reset();
            this.type = type;
        }

        void reset() {
            this.typeSeen = false;
            this.immediatelyFollowed = false;
            this.isPuzzleMode1 = -1;
            this.isPuzzleMode2 = -1;
            this.puzzleString = "";
            this.anzCandDel = 0;
        }
    }
}
