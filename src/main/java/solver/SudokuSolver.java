package solver;

import sudoku.*;

import java.io.BufferedWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SudokuSolver {
    private SudokuStepFinder stepFinder = new SudokuStepFinder();
    private Sudoku2 sudoku;
    private List<SolutionStep> steps = new ArrayList<>();
    private List<SolutionStep> tmpSteps = new ArrayList<>();
    private DifficultyLevel level = Options.getInstance().getDifficultyLevel(DifficultyType.EXTREME.ordinal());
    private DifficultyLevel maxLevel = Options.getInstance().getDifficultyLevel(DifficultyType.EXTREME.ordinal());
    private int score;
    private int[] anzSteps = new int[Options.getInstance().solverSteps.length];
    private int[] anzStepsProgress = new int[Options.getInstance().solverSteps.length];
    private long[] stepsNanoTime = new long[Options.getInstance().solverSteps.length];

    public boolean solve(boolean withGui) {
        if (!withGui) {
            return this.solve();
        }

        SolverProgressDialog dlg = new SolverProgressDialog(null, true, this);
        Thread thread = dlg.getThread();

        try {
            thread.join(2000L);
        } catch (InterruptedException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Solver thread was interrupted", ex);
        }

        if (thread.isAlive()) {
            dlg.setVisible(true);
        }

        if (thread.isAlive()) {
            try {
                thread.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Solver thread was interrupted", ex);
            }
        }

        if (dlg.isVisible()) {
            dlg.setVisible(false);
        }

        return dlg.isSolved();
    }

    public boolean solve() {
        return this.solve(Options.getInstance().getDifficultyLevel(DifficultyType.EXTREME.ordinal()), null, false, null, false);
    }

    public boolean solveSinglesOnly(Sudoku2 newSudoku) {
        Sudoku2 tmpSudoku = this.sudoku;
        List<SolutionStep> oldList = this.steps;
        this.setSudoku(newSudoku);
        this.steps = this.tmpSteps;
        SudokuUtil.clearStepListWithNullify(this.steps);
        boolean solved = this.solve(Options.getInstance().getDifficultyLevel(DifficultyType.EXTREME.ordinal()), null, false, null, true);
        this.steps = oldList;
        this.setSudoku(tmpSudoku);
        return solved;
    }

    public boolean solveWithSteps(Sudoku2 newSudoku, StepConfig[] stepConfigs) {
        Sudoku2 tmpSudoku = this.sudoku;
        List<SolutionStep> oldList = this.steps;
        this.setSudoku(newSudoku);
        this.steps = this.tmpSteps;
        SudokuUtil.clearStepListWithNullify(this.steps);
        boolean solved = this.solve(
                Options.getInstance().getDifficultyLevel(DifficultyType.EXTREME.ordinal()), null, false, null, false, stepConfigs, GameMode.PLAYING
        );
        this.steps = oldList;
        this.setSudoku(tmpSudoku);
        return solved;
    }

    public boolean solve(DifficultyLevel maxLevel, Sudoku2 tmpSudoku, boolean rejectTooLowScore, SolverProgressDialog dlg) {
        return this.solve(maxLevel, tmpSudoku, rejectTooLowScore, dlg, false);
    }

    public boolean solve(DifficultyLevel maxLevel, Sudoku2 tmpSudoku, boolean rejectTooLowScore, SolverProgressDialog dlg, boolean singlesOnly) {
        return this.solve(maxLevel, tmpSudoku, rejectTooLowScore, dlg, singlesOnly, Options.getInstance().solverSteps, GameMode.PLAYING);
    }

    public boolean solve(
            DifficultyLevel maxLevel,
            Sudoku2 tmpSudoku,
            boolean rejectTooLowScore,
            SolverProgressDialog dlg,
            boolean singlesOnly,
            StepConfig[] stepConfigs,
            GameMode gameMode
    ) {
        if (tmpSudoku != null) {
            this.setSudoku(tmpSudoku);
        }

        int anzCells = this.sudoku.getUnsolvedCellsAnz();
        if (81 - anzCells < 10) {
            return false;
        }

        int anzCand = this.sudoku.getUnsolvedCandidatesAnz();
        if (dlg != null) {
            dlg.initializeProgressState(anzCand);
        }

        this.maxLevel = maxLevel;
        this.score = 0;
        this.level = Options.getInstance().getDifficultyLevel(DifficultyType.EASY.ordinal());
        SolutionStep step = null;

        for (int i = 0; i < this.anzSteps.length; i++) {
            this.anzSteps[i] = 0;
        }

        boolean acceptAnyway = false;

        do {
            if (dlg != null) {
                dlg.setProgressState(this.sudoku.getUnsolvedCellsAnz(), this.sudoku.getUnsolvedCandidatesAnz());
            }

            step = this.getHint(singlesOnly, stepConfigs, acceptAnyway);
            if (step != null) {
                if (gameMode != GameMode.PLAYING && step.getType().getStepConfig().isEnabledTraining()) {
                    acceptAnyway = true;
                }

                this.steps.add(step);
                this.getStepFinder().doStep(step);
                if (step.getType() == SolutionType.GIVE_UP) {
                    step = null;
                }
            }
        } while (step != null);

        while (this.score > this.level.getMaxScore()) {
            this.level = Options.getInstance().getDifficultyLevel(this.level.getOrdinal() + 1);
        }

        if (this.level.getOrdinal() > maxLevel.getOrdinal() && !acceptAnyway) {
            return false;
        } else if (rejectTooLowScore
                && this.level.getOrdinal() > DifficultyType.EASY.ordinal()
                && !acceptAnyway
                && this.score < Options.getInstance().getDifficultyLevel(this.level.getOrdinal() - 1).getMaxScore()) {
            return false;
        } else {
            this.sudoku.setScore(this.score);
            if (this.sudoku.isSolved()) {
                this.sudoku.setLevel(this.level);
                return true;
            } else {
                this.sudoku.setLevel(Options.getInstance().getDifficultyLevel(DifficultyType.EXTREME.ordinal()));
                return false;
            }
        }
    }

    public void getProgressScore(Sudoku2 tmpSudoku, List<SolutionStep> stepsTocheck, FindAllStepsProgressDialog dlg) {
        if (dlg != null) {
            dlg.resetFishProgressBar(stepsTocheck.size());
        }

        this.resetProgressStepCounters();
        int delta = stepsTocheck.size() / 10;
        if (delta == 0) {
            delta = 1;
        }

        boolean oldCheckTemplates = Options.getInstance().isCheckTemplates();
        Options.getInstance().setCheckTemplates(false);
        long nanos = System.nanoTime();
        Sudoku2 workingSudoku = tmpSudoku.clone();

        for (int i = 0; i < stepsTocheck.size(); i++) {
            SolutionStep step = stepsTocheck.get(i);
            workingSudoku.set(tmpSudoku);
            this.getProgressScore(workingSudoku, step);
            if (i % delta == 0 && dlg != null) {
                dlg.updateFishProgressBar(i);
            }
        }

        Options.getInstance().setCheckTemplates(oldCheckTemplates);
        Sudoku2 var12 = null;
        nanos = System.nanoTime() - nanos;
    }

    public void getProgressScore(Sudoku2 tmpSudoku, SolutionStep orgStep) {
        Sudoku2 save = this.sudoku;
        this.setSudoku(tmpSudoku);
        int progressScoreSingles = 0;
        int progressScoreSinglesOnly = 0;
        int progressScore = 0;
        boolean direct = true;
        SolutionType type = orgStep.getType();
        if (type == SolutionType.FORCING_CHAIN_VERITY || type == SolutionType.FORCING_NET_VERITY || type == SolutionType.TEMPLATE_SET) {
            int anz = orgStep.getIndices().size();
            progressScoreSingles += anz;
            progressScoreSinglesOnly += anz;
        }

        this.getStepFinder().doStep(orgStep);
        SolutionStep step = null;

        do {
            step = this.getHint(false, Options.getInstance().solverStepsProgress, false);
            if (step != null) {
                if (step.getType().isSingle()) {
                    progressScoreSingles++;
                    if (direct) {
                        progressScoreSinglesOnly++;
                    }
                } else {
                    direct = false;
                }

                progressScore += step.getType().getStepConfig().getBaseScore();
                this.getStepFinder().doStep(step);
                if (step.getType() == SolutionType.GIVE_UP) {
                    step = null;
                }
            }
        } while (step != null);

        orgStep.setProgressScoreSingles(progressScoreSingles);
        orgStep.setProgressScoreSinglesOnly(progressScoreSinglesOnly);
        orgStep.setProgressScore(progressScore);
        this.setSudoku(save);
    }

    public SolutionStep getHint(Sudoku2 sudoku, boolean singlesOnly) {
        Sudoku2 save = this.sudoku;
        DifficultyLevel oldMaxLevel = this.maxLevel;
        DifficultyLevel oldLevel = this.level;
        this.maxLevel = Options.getInstance().getDifficultyLevel(DifficultyType.EXTREME.ordinal());
        this.level = Options.getInstance().getDifficultyLevel(DifficultyType.EASY.ordinal());
        this.setSudoku(sudoku);
        SolutionStep step = this.getHint(singlesOnly);
        this.maxLevel = oldMaxLevel;
        this.level = oldLevel;
        this.setSudoku(save);
        return step;
    }

    private SolutionStep getHint(boolean singlesOnly) {
        return this.getHint(singlesOnly, Options.getInstance().solverSteps, false);
    }

    private SolutionStep getHint(boolean singlesOnly, StepConfig[] solverSteps, boolean acceptAnyway) {
        if (this.sudoku.isSolved()) {
            return null;
        }

        SolutionStep hint = null;

        for (int i = 0; i < solverSteps.length; i++) {
            if (solverSteps == Options.getInstance().solverStepsProgress ? solverSteps[i].isEnabledProgress() : solverSteps[i].isEnabled()) {
                SolutionType type = solverSteps[i].getType();
                if (!singlesOnly || type == SolutionType.HIDDEN_SINGLE || type == SolutionType.NAKED_SINGLE || type == SolutionType.FULL_HOUSE) {
                    Logger.getLogger(this.getClass().getName()).log(Level.FINER, "trying {0}: ", SolutionStep.getStepName(type));
                    long nanos = System.nanoTime();
                    hint = this.getStepFinder().getStep(type);
                    nanos = System.nanoTime() - nanos;
                    Logger.getLogger(this.getClass().getName()).log(Level.FINER, "{0}ms ({1})", new Object[]{nanos / 1000L, hint != null ? hint.toString(2) : "-"});
                    this.anzStepsProgress[i]++;
                    this.stepsNanoTime[i] = this.stepsNanoTime[i] + nanos;
                    if (hint != null) {
                        this.anzSteps[i]++;
                        this.score = this.score + solverSteps[i].getBaseScore();
                        if (Options.getInstance().getDifficultyLevels()[solverSteps[i].getLevel()].getOrdinal() > this.level.getOrdinal()) {
                            this.level = Options.getInstance().getDifficultyLevels()[solverSteps[i].getLevel()];
                        }

                        if (acceptAnyway || this.level.getOrdinal() <= this.maxLevel.getOrdinal() && this.score < this.maxLevel.getMaxScore()) {
                            return hint;
                        }

                        return null;
                    }
                }
            }
        }

        return null;
    }

    public void doStep(Sudoku2 sudoku, SolutionStep step) {
        Sudoku2 oldSudoku = this.getSudoku();
        this.getStepFinder().setSudoku(sudoku);
        this.getStepFinder().doStep(step);
        this.getStepFinder().setSudoku(oldSudoku);
    }

    public Sudoku2 getSudoku() {
        return this.sudoku;
    }

    public void setSudoku(Sudoku2 sudoku) {
        SudokuUtil.clearStepList(this.steps);

        for (int i = 0; i < this.anzSteps.length; i++) {
            this.anzSteps[i] = 0;
        }

        this.sudoku = sudoku;
        this.getStepFinder().setSudoku(sudoku);
    }

    public void setSudoku(Sudoku2 sudoku, List<SolutionStep> partSteps) {
        this.steps = new ArrayList<>();

        for (int i = 0; i < partSteps.size(); i++) {
            this.steps.add(partSteps.get(i));
        }

        this.sudoku = sudoku;
        this.getStepFinder().setSudoku(sudoku);
    }

    public List<SolutionStep> getSteps() {
        return this.steps;
    }

    public void setSteps(List<SolutionStep> steps) {
        this.steps = steps;
    }

    public int getAnzUsedSteps() {
        int anz = 0;

        for (int i = 0; i < this.anzSteps.length; i++) {
            if (this.anzSteps[i] > 0) {
                anz++;
            }
        }

        return anz;
    }

    public int[] getAnzSteps() {
        return this.anzSteps;
    }

    public void setAnzSteps(int[] anzSteps) {
        this.anzSteps = anzSteps;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getLevelString() {
        return StepConfig.getLevelName(this.level);
    }

    public DifficultyLevel getLevel() {
        return this.level;
    }

    public void setLevel(DifficultyLevel level) {
        this.level = level;
    }

    public SolutionCategory getCategory(SolutionType type) {
        for (StepConfig configStep : Options.getInstance().solverSteps) {
            if (type == configStep.getType()) {
                return configStep.getCategory();
            }
        }

        return null;
    }

    public String getCategoryName(SolutionType type) {
        SolutionCategory cat = this.getCategory(type);
        return cat == null ? null : cat.getCategoryName();
    }

    public DifficultyLevel getMaxLevel() {
        return this.maxLevel;
    }

    public void setMaxLevel(DifficultyLevel maxLevel) {
        this.maxLevel = maxLevel;
    }

    public void getState(GuiState state, boolean copy) {
        if (copy) {
            state.setAnzSteps((int[]) this.anzSteps.clone());
            state.setSteps((List<SolutionStep>) ((ArrayList) this.steps).clone());
        } else {
            state.setAnzSteps(this.anzSteps);
            state.setSteps(this.steps);
        }
    }

    public void setState(GuiState state) {
        if (state.getAnzSteps().length < this.anzSteps.length) {
            int[] tmpArray = new int[this.anzSteps.length];
            System.arraycopy(state.getAnzSteps(), 0, tmpArray, 0, state.getAnzSteps().length);
            state.setAnzSteps(tmpArray);
        }

        this.anzSteps = state.getAnzSteps();
        this.steps = state.getSteps();
        this.score = state.getSudoku().getScore();
    }

    private void resetProgressStepCounters() {
        for (int i = 0; i < this.anzStepsProgress.length; i++) {
            this.anzStepsProgress[i] = 0;
            this.stepsNanoTime[i] = 0L;
        }
    }

    public long[] getStepsNanoTime() {
        return this.stepsNanoTime;
    }

    public void printStatistics(BufferedWriter out) {
        PrintWriter pw = new PrintWriter(out);
        this.printStatistics(pw);
    }

    public void printStatistics(PrintStream out) {
        out.println();
        out.println("Timing:");

        for (int i = 0; i < Options.getInstance().solverSteps.length; i++) {
            if (this.anzStepsProgress[i] > 0) {
                out.printf(
                        "  %10d/%12.2fus/%12.2fms: %s\r\n",
                        this.anzStepsProgress[i],
                        this.stepsNanoTime[i] / this.anzStepsProgress[i] / 1000.0,
                        this.stepsNanoTime[i] / 1000000.0,
                        Options.getInstance().solverStepsProgress[i].getType().getStepName()
                );
            }
        }

        out.println();
    }

    public void printStatistics(PrintWriter out) {
        out.println();
        out.println("Timing:");

        for (int i = 0; i < Options.getInstance().solverSteps.length; i++) {
            if (this.anzStepsProgress[i] > 0) {
                out.printf(
                        "  %10d/%12.2fus/%12.2fms: %s\r\n",
                        this.anzStepsProgress[i],
                        this.stepsNanoTime[i] / this.anzStepsProgress[i] / 1000.0,
                        this.stepsNanoTime[i] / 1000000.0,
                        Options.getInstance().solverStepsProgress[i].getType().getStepName()
                );
            }
        }

        out.println();
    }

    public SudokuStepFinder getStepFinder() {
        return this.stepFinder;
    }
}
