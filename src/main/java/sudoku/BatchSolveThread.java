package sudoku;

import generator.SudokuGenerator;
import generator.SudokuGeneratorFactory;
import solver.SudokuSolver;
import solver.SudokuSolverFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

class BatchSolveThread extends Thread {
    private String fileName;
    private String puzzleString;
    private boolean printSolution;
    private boolean printSolutionPath;
    private boolean printStatistic;
    private int[] results;
    private int bruteForceAnz;
    private int templateAnz;
    private int unsolvedAnz = 0;
    private int givenUpAnz = 0;
    private int count;
    private long ticks;
    private SudokuGenerator generator = SudokuGeneratorFactory.getDefaultGeneratorInstance();
    private ClipboardMode clipboardMode;
    private Set<SolutionType> types;
    private boolean outputGrid = false;
    private String outFileName = null;
    private boolean findAllSteps = false;
    private boolean bruteForceTest = false;
    private List<SolutionType> testTypes = null;
    private StepStatistic[] stepStatistics;
    private StepStatistic[] singleStepStatistics;
    private FindAllSteps findAllStepsInstance = null;

    BatchSolveThread(
            String fn,
            String pStr,
            boolean ps,
            boolean pp,
            boolean pst,
            ClipboardMode cm,
            Set<SolutionType> t,
            String ofn,
            boolean fas,
            boolean bft,
            List<SolutionType> tt
    ) {
        this.fileName = fn;
        this.puzzleString = pStr;
        this.printSolution = ps;
        this.printSolutionPath = pp;
        this.printStatistic = pst;
        this.clipboardMode = cm;
        this.types = t;
        if (this.clipboardMode != null && this.types != null) {
            this.outputGrid = true;
        }

        this.outFileName = ofn;
        this.findAllSteps = fas;
        this.bruteForceTest = bft;
        this.testTypes = tt;
        if (this.bruteForceTest) {
            this.findAllStepsInstance = new FindAllSteps();
        }

        if (this.printStatistic) {
            this.stepStatistics = new StepStatistic[SolutionType.values().length];
            this.singleStepStatistics = new StepStatistic[SolutionType.values().length];

            for (int i = 0; i < this.stepStatistics.length; i++) {
                this.stepStatistics[i] = new StepStatistic(SolutionType.values()[i]);
                this.singleStepStatistics[i] = new StepStatistic(SolutionType.values()[i]);
            }
        }
    }

    private void adjustStatistics(SolutionStep step) {
        int anzCand = step.getAnzCandidatesToDelete();
        int anzSet = step.getAnzSet();
        this.stepStatistics[step.getType().ordinal()].anzSteps++;
        this.stepStatistics[step.getType().ordinal()].anzCandDel += anzCand;
        this.stepStatistics[step.getType().ordinal()].anzSet += anzSet;
        this.singleStepStatistics[step.getType().ordinal()].anzSteps++;
        this.singleStepStatistics[step.getType().ordinal()].anzCandDel += anzCand;
        this.singleStepStatistics[step.getType().ordinal()].anzSet += anzSet;
    }

    private void clearSingleStepStatistics() {
        for (int i = 0; i < this.singleStepStatistics.length; i++) {
            this.singleStepStatistics[i].anzCandDel = 0;
            this.singleStepStatistics[i].anzSet = 0;
            this.singleStepStatistics[i].anzSteps = 0;
        }
    }

    public void printStatistic(PrintWriter out, boolean single) throws IOException {
        if (out != null) {
            if (!single) {
                out.println();
                out.println("Statistics total:");
            } else {
                out.println("    Statistics:");
            }
        } else if (!single) {
            System.out.println();
            System.out.println("Statistics total:");
        } else {
            System.out.println("    Statistics:");
        }

        if (single) {
            this.printStatistic(out, this.singleStepStatistics, false);
        } else {
            this.printStatistic(out, this.stepStatistics, true);
        }
    }

    private void printStatistic(PrintWriter out, StepStatistic[] stat, boolean total) throws IOException {
        int anzSteps = 0;
        int anzSet = 0;
        int anzCandDel = 0;
        int anzInvalidSteps = 0;
        int anzInvalidSet = 0;
        int anzInvalidCandDel = 0;

        for (int i = 0; i < stat.length; i++) {
            if (stat[i].anzSteps > 0) {
                if (out != null) {
                    out.printf("      %8d - %8d/%8d: %s", stat[i].anzSteps, stat[i].anzSet, stat[i].anzCandDel, stat[i].type.getStepName());
                    out.println();
                    if (this.bruteForceTest) {
                        out.printf("      Invalid: %3d - %3d/%3d", stat[i].anzInvalidSteps, stat[i].anzInvalidSet, stat[i].anzInvalidCandDel);
                        out.println();
                    }
                } else {
                    System.out.printf("      %8d - %8d/%8d: %s", stat[i].anzSteps, stat[i].anzSet, stat[i].anzCandDel, stat[i].type.getStepName());
                    System.out.println();
                    if (this.bruteForceTest) {
                        System.out.printf("        Invalid: %3d - %3d/%3d", stat[i].anzInvalidSteps, stat[i].anzInvalidSet, stat[i].anzInvalidCandDel);
                        System.out.println();
                    }
                }
            }

            anzSteps += stat[i].anzSteps;
            anzSet += stat[i].anzSet;
            anzCandDel += stat[i].anzCandDel;
            anzInvalidSteps += stat[i].anzInvalidSteps;
            anzInvalidSet += stat[i].anzInvalidSet;
            anzInvalidCandDel += stat[i].anzInvalidCandDel;
        }

        if (total) {
            if (out != null) {
                out.println("      ---------------------------------------------------");
                out.printf("      %8d - %8d/%8d", anzSteps, anzSet, anzCandDel);
                out.println();
                if (this.bruteForceTest) {
                    out.printf("      %8d - %8d/%8d", anzInvalidSteps, anzInvalidSet, anzInvalidCandDel);
                    out.println();
                }
            } else {
                System.out.println("      ---------------------------------------------------");
                System.out.printf("      %8d - %8d/%8d", anzSteps, anzSet, anzCandDel);
                if (this.bruteForceTest) {
                    System.out.printf("      %8d - %8d/%8d", anzInvalidSteps, anzInvalidSet, anzInvalidCandDel);
                }
            }

            if (out != null) {
                SudokuSolverFactory.getDefaultSolverInstance().printStatistics(out);
            } else {
                SudokuSolverFactory.getDefaultSolverInstance().printStatistics(System.out);
            }
        }
    }

    @Override
    public void run() {
        System.out.println("Starting batch solve...");
        this.results = new int[Options.DEFAULT_DIFFICULTY_LEVELS.length];
        this.bruteForceAnz = 0;
        this.templateAnz = 0;
        this.unsolvedAnz = 0;
        this.givenUpAnz = 0;
        BufferedReader inFile = null;
        PrintWriter outFile = null;
        this.ticks = System.currentTimeMillis();
        this.count = 0;

        try {
            if (this.fileName != null) {
                inFile = new BufferedReader(new FileReader(this.fileName));
            }

            if (this.outFileName == null) {
                this.outFileName = this.fileName + ".out.txt";
            }

            if (this.outFileName.equals("stdout")) {
                outFile = null;
            } else {
                outFile = new PrintWriter(new BufferedWriter(new FileWriter(this.outFileName)));
            }

            String line = null;
            SudokuSolver solver = SudokuSolverFactory.getDefaultSolverInstance();
            Sudoku2 sudoku = new Sudoku2();
            Sudoku2 tmpSudoku = null;
            Sudoku2 solvedSudoku = null;
            List<SolutionStep> allSteps = null;
            if (this.bruteForceTest) {
                allSteps = new ArrayList<>();
            }

            long outTicks = 0L;

            while (!this.isInterrupted() && inFile != null && (line = inFile.readLine()) != null || this.puzzleString != null) {
                if (this.puzzleString != null) {
                    line = this.puzzleString;
                    this.puzzleString = null;
                }

                line = line.trim();
                if (line.length() != 0) {
                    sudoku.setSudoku(line);
                    if (this.outputGrid || this.bruteForceTest) {
                        tmpSudoku = sudoku.clone();
                    }

                    if (this.bruteForceTest) {
                        solvedSudoku = sudoku.clone();
                        this.generator.validSolution(solvedSudoku);
                    }

                    this.count++;
                    boolean needsGuessing = false;
                    boolean needsTemplates = false;
                    boolean givenUp = false;
                    boolean unsolved = false;
                    List<SolutionStep> steps = null;
                    if (this.findAllSteps) {
                        steps = new ArrayList<>();
                        Thread thread = new Thread(new FindAllSteps(steps, sudoku, null));
                        thread.start();
                        thread.join();
                    } else {
                        this.generator.validSolution(sudoku);
                        solver.setSudoku(sudoku);
                        solver.solve();
                        steps = solver.getSteps();

                        for (int i = 0; i < steps.size(); i++) {
                            if (steps.get(i).getType() == SolutionType.BRUTE_FORCE && !needsGuessing) {
                                needsGuessing = true;
                                unsolved = true;
                                this.bruteForceAnz++;
                            }

                            if ((steps.get(i).getType() == SolutionType.TEMPLATE_DEL || steps.get(i).getType() == SolutionType.TEMPLATE_SET) && !needsTemplates) {
                                needsTemplates = true;
                                unsolved = true;
                                this.templateAnz++;
                            }

                            if (steps.get(i).getType() == SolutionType.GIVE_UP && !givenUp) {
                                givenUp = true;
                                unsolved = true;
                                this.givenUpAnz++;
                            }
                        }

                        if (unsolved) {
                            this.unsolvedAnz++;
                        }

                        for (int i = 0; i < sudoku.getValues().length; i++) {
                            if (sudoku.getValue(i) != sudoku.getSolution(i)) {
                                System.out.println("Invalid solution: ");
                                System.out.println("   Sudoku: " + line);
                                System.out.println("   Solution:      " + Arrays.toString(sudoku.getValues()));
                                System.out.println("   True Solution: " + Arrays.toString(sudoku.getSolution()));
                            }
                        }
                    }

                    String guess = needsGuessing ? " " + SolutionType.BRUTE_FORCE.getArgName() : "";
                    String template = needsTemplates ? " " + SolutionType.TEMPLATE_DEL.getArgName() : "";
                    String giveUp = givenUp ? " " + SolutionType.GIVE_UP.getArgName() : "";
                    if (this.printSolution || this.bruteForceTest) {
                        solvedSudoku = sudoku.clone();
                        if (sudoku.isSolved()) {
                            line = sudoku.getSudoku(ClipboardMode.VALUES_ONLY);
                        } else {
                            this.generator.validSolution(solvedSudoku);
                            line = solvedSudoku.getSudoku(ClipboardMode.VALUES_ONLY);
                        }
                    }

                    String out = line + " #" + this.count;
                    if (!this.findAllSteps) {
                        out = out + " " + solver.getLevel().getName() + " (" + solver.getScore() + ")" + guess + template + giveUp;
                        this.results[solver.getLevel().getOrdinal()]++;
                    }

                    if (outFile != null) {
                        outFile.println(out);
                    } else {
                        System.out.println(out);
                    }

                    if (this.printSolutionPath || this.findAllSteps || this.printStatistic || this.bruteForceTest) {
                        steps = new ArrayList<>(steps);

                        for (int i = 0; i < steps.size(); i++) {
                            if (this.outputGrid || this.bruteForceTest) {
                                if (this.types != null
                                        && this.clipboardMode != null
                                        && this.types.contains(steps.get(i).getType())
                                        && (this.printSolutionPath || this.findAllSteps)) {
                                    String grid = tmpSudoku.getSudoku(this.clipboardMode, steps.get(i));
                                    String[] gridLines = grid.split("\r\n");
                                    int end = this.clipboardMode == ClipboardMode.PM_GRID_WITH_STEP ? gridLines.length - 2 : gridLines.length;

                                    for (int j = 0; j < end; j++) {
                                        if (outFile != null) {
                                            outFile.println("   " + gridLines[j]);
                                        } else {
                                            System.out.println("   " + gridLines[j]);
                                        }
                                    }
                                }

                                if (this.bruteForceTest && !steps.get(i).getType().isSingle()) {
                                    allSteps.clear();
                                    this.findAllStepsInstance.setSteps(allSteps);
                                    this.findAllStepsInstance.setSudoku(tmpSudoku);
                                    this.findAllStepsInstance.setTestType(this.testTypes);
                                    this.findAllStepsInstance.run();

                                    for (SolutionStep act : allSteps) {
                                        if (this.testTypes.contains(act.getType())) {
                                            boolean invalid = false;
                                            this.adjustStatistics(act);
                                            if (!act.getValues().isEmpty()) {
                                                for (int index : act.getIndices()) {
                                                    if (sudoku.getValue(index) != solvedSudoku.getValue(index)) {
                                                        invalid = true;
                                                        this.stepStatistics[act.getType().ordinal()].anzInvalidSet++;
                                                    }
                                                }
                                            }

                                            for (Candidate cand : act.getCandidatesToDelete()) {
                                                if (cand.getValue() == solvedSudoku.getValue(cand.getIndex())) {
                                                    invalid = true;
                                                    this.stepStatistics[act.getType().ordinal()].anzInvalidCandDel++;
                                                }
                                            }

                                            if (invalid) {
                                                this.stepStatistics[act.getType().ordinal()].anzInvalidSteps++;
                                                if (outFile != null) {
                                                    outFile.println("INVALID:");
                                                    outFile.println(sudoku.getSudoku(ClipboardMode.LIBRARY, act));
                                                } else {
                                                    System.out.println("INVALID:");
                                                    System.out.println(sudoku.getSudoku(ClipboardMode.LIBRARY, act));
                                                }
                                            }
                                        }
                                    }
                                }

                                solver.doStep(tmpSudoku, steps.get(i));
                            }

                            if (this.printStatistic && !this.bruteForceTest) {
                                this.adjustStatistics(steps.get(i));
                            }

                            if (this.printSolutionPath || this.findAllSteps) {
                                if (outFile != null) {
                                    outFile.write("   ");
                                    if (this.printStatistic) {
                                        outFile.write(steps.get(i).getCandidateString(false, true) + ": ");
                                    }

                                    outFile.println(steps.get(i).toString(2));
                                } else {
                                    System.out.print("   ");
                                    if (this.printStatistic) {
                                        System.out.print(steps.get(i).getCandidateString(false, true) + ": ");
                                    }

                                    System.out.println(steps.get(i).toString(2));
                                }
                            }
                        }

                        if (this.printStatistic && (this.printSolutionPath || this.findAllSteps)) {
                            this.printStatistic(outFile, true);
                            this.clearSingleStepStatistics();
                        }
                    }

                    if (this.count % 100 == 0 && System.currentTimeMillis() - outTicks > 2000L) {
                        outTicks = System.currentTimeMillis();
                        double ticks2 = outTicks - this.getTicks();
                        System.out.printf("%d (%.03fms per puzzle\r\n", this.count, ticks2 / this.count);
                    }
                }
            }

            if (this.printStatistic) {
                this.printStatistic(outFile, false);
            }
        } catch (Exception ex) {
            System.out.println("Error in batch solve:");
            ex.printStackTrace();
        } finally {
            try {
                if (inFile != null) {
                    inFile.close();
                }

                if (outFile != null) {
                    outFile.close();
                }
            } catch (Exception ex) {
                System.out.println("Error closing files:");
                ex.printStackTrace();
            }
        }

        if (this.isInterrupted()) {
            System.out.println("Interrupted, shutting down...");
        } else {
            System.out.println("Done!");
        }

        this.ticks = System.currentTimeMillis() - this.getTicks();
    }

    public int getBruteForceAnz() {
        return this.bruteForceAnz;
    }

    public int getTemplateAnz() {
        return this.templateAnz;
    }

    public int getUnsolvedAnz() {
        return this.unsolvedAnz;
    }

    public int getGivenUpAnz() {
        return this.givenUpAnz;
    }

    public long getTicks() {
        return this.ticks;
    }

    public int getResult(int index) {
        return this.results[index];
    }

    public int getResultLength() {
        return this.results.length;
    }

    public int getCount() {
        return this.count;
    }

    public StepStatistic[] getStepStatistics() {
        return this.stepStatistics;
    }
}
