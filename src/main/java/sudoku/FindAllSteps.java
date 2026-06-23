package sudoku;

import solver.SudokuSolverFactory;
import solver.SudokuStepFinder;

import java.awt.*;
import java.util.List;
import java.util.ResourceBundle;

public class FindAllSteps implements Runnable {
    private FindAllStepsProgressDialog dlg = null;
    private List<SolutionStep> steps;
    private List<SolutionType> testTypes = null;
    private Sudoku2 sudoku;
    private SudokuStepFinder stepFinder = SudokuSolverFactory.getDefaultSolverInstance().getStepFinder();

    public FindAllSteps() {
    }

    public FindAllSteps(List<SolutionStep> steps, Sudoku2 sudoku, FindAllStepsProgressDialog dlg) {
        this();
        this.sudoku = sudoku;
        sudoku.rebuildInternalData();
        this.steps = steps;
        this.dlg = dlg;
    }

    private void updateProgress(String label, int step) {
        if (this.dlg != null) {
            this.dlg.updateProgress(label, step);
        }
    }

    private boolean isAllStepsEnabled(SolutionType type) {
        if (this.testTypes == null) {
            StepConfig[] tmpSteps = Options.getInstance().solverSteps;

            for (int i = 0; i < tmpSteps.length; i++) {
                if (tmpSteps[i].getType() == type) {
                    return tmpSteps[i].isAllStepsEnabled();
                }
            }

            return false;
        } else {
            return this.testTypes.contains(type);
        }
    }

    private boolean isFishTestTypes() {
        for (int i = 0; i < this.testTypes.size(); i++) {
            if (!this.testTypes.get(i).isFish()) {
                return false;
            }
        }

        return true;
    }

    private void filterSteps(List<SolutionStep> steps) {
        for (int i = 0; i < steps.size(); i++) {
            if (!this.isAllStepsEnabled(steps.get(i).getType())) {
                steps.remove(i);
                i--;
            }
        }
    }

    @Override
    public void run() {
        int actStep = 0;
        List<SolutionStep> steps1 = null;

        while (!Thread.interrupted()) {
            switch (actStep) {
                case 0:
                    this.updateProgress(
                            ResourceBundle.getBundle("intl/FindAllStepsProgressDialog").getString("FindAllStepsProgressDialog.simple_solutions"), actStep
                    );
                    steps1 = this.stepFinder.findAllFullHouses(this.sudoku);
                    this.steps.addAll(steps1);
                    steps1 = this.stepFinder.findAllHiddenXle(this.sudoku);
                    this.steps.addAll(steps1);
                    steps1 = this.stepFinder.findAllNakedXle(this.sudoku);
                    this.steps.addAll(steps1);
                    this.filterSteps(this.steps);
                    if (this.isAllStepsEnabled(SolutionType.LOCKED_CANDIDATES_1) && this.isAllStepsEnabled(SolutionType.LOCKED_CANDIDATES_2)) {
                        steps1 = this.stepFinder.findAllLockedCandidates(this.sudoku);
                        this.steps.addAll(steps1);
                    } else if (this.isAllStepsEnabled(SolutionType.LOCKED_CANDIDATES_1)) {
                        steps1 = this.stepFinder.findAllLockedCandidates1(this.sudoku);
                        this.steps.addAll(steps1);
                    } else if (this.isAllStepsEnabled(SolutionType.LOCKED_CANDIDATES_2)) {
                        steps1 = this.stepFinder.findAllLockedCandidates2(this.sudoku);
                        this.steps.addAll(steps1);
                    }

                    if (this.isAllStepsEnabled(SolutionType.SKYSCRAPER)) {
                        steps1 = this.stepFinder.findAllSkyScrapers(this.sudoku);
                        this.steps.addAll(steps1);
                    }

                    if (this.isAllStepsEnabled(SolutionType.EMPTY_RECTANGLE)) {
                        steps1 = this.stepFinder.findAllEmptyRectangles(this.sudoku);
                        this.steps.addAll(steps1);
                    }

                    if (this.isAllStepsEnabled(SolutionType.TWO_STRING_KITE)) {
                        steps1 = this.stepFinder.findAllTwoStringKites(this.sudoku);
                        this.steps.addAll(steps1);
                    }

                    if (this.isAllStepsEnabled(SolutionType.SUE_DE_COQ)) {
                        steps1 = this.stepFinder.getAllSueDeCoqs(this.sudoku);
                        this.steps.addAll(steps1);
                    }
                    break;
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                    this.updateProgress(
                            ResourceBundle.getBundle("intl/FindAllStepsProgressDialog").getString("FindAllStepsProgressDialog.fish") + " " + actStep, actStep
                    );
                    if (this.testTypes == null
                            && Options.getInstance().isAllStepsSearchFish()
                            && Options.getInstance().getAllStepsFishCandidates().charAt(actStep - 1) == '1'
                            || this.testTypes != null && this.isFishTestTypes()) {
                        boolean oldCheckTemplates = Options.getInstance().isCheckTemplates();
                        Options.getInstance().setCheckTemplates(Options.getInstance().isAllStepsCheckTemplates());
                        steps1 = this.stepFinder
                                .getAllFishes(
                                        this.sudoku,
                                        Options.getInstance().getAllStepsMinFishSize(),
                                        Options.getInstance().getAllStepsMaxFishSize(),
                                        Options.getInstance().getAllStepsMaxFins(),
                                        Options.getInstance().getAllStepsMaxEndoFins(),
                                        this.dlg,
                                        actStep,
                                        Options.getInstance().getAllStepsMaxFishType()
                                );
                        this.steps.addAll(steps1);
                        Options.getInstance().setCheckTemplates(oldCheckTemplates);
                    }
                    break;
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:
                case 15:
                case 16:
                case 17:
                case 18:
                    if (this.isAllStepsEnabled(SolutionType.KRAKEN_FISH) && Options.getInstance().getAllStepsKrakenFishCandidates().charAt(actStep - 10) == '1') {
                        this.updateProgress(
                                ResourceBundle.getBundle("intl/FindAllStepsProgressDialog").getString("FindAllStepsProgressDialog.kraken_fish") + " " + (actStep - 9),
                                actStep
                        );
                        steps1 = this.stepFinder
                                .getAllKrakenFishes(
                                        this.sudoku,
                                        Options.getInstance().getAllStepsKrakenMinFishSize(),
                                        Options.getInstance().getAllStepsKrakenMaxFishSize(),
                                        Options.getInstance().getAllStepsMaxKrakenFins(),
                                        Options.getInstance().getAllStepsMaxKrakenEndoFins(),
                                        this.dlg,
                                        actStep - 9,
                                        Options.getInstance().getAllStepsKrakenMaxFishType()
                                );
                        this.steps.addAll(steps1);
                    }
                    break;
                case 19:
                    this.updateProgress(ResourceBundle.getBundle("intl/FindAllStepsProgressDialog").getString("FindAllStepsProgressDialog.uniqueness"), actStep);
                    if (this.isAllStepsEnabled(SolutionType.UNIQUENESS_1)
                            || this.isAllStepsEnabled(SolutionType.UNIQUENESS_2)
                            || this.isAllStepsEnabled(SolutionType.UNIQUENESS_3)
                            || this.isAllStepsEnabled(SolutionType.UNIQUENESS_4)
                            || this.isAllStepsEnabled(SolutionType.UNIQUENESS_5)
                            || this.isAllStepsEnabled(SolutionType.UNIQUENESS_6)
                            || this.isAllStepsEnabled(SolutionType.HIDDEN_RECTANGLE)
                            || this.isAllStepsEnabled(SolutionType.AVOIDABLE_RECTANGLE_1)
                            || this.isAllStepsEnabled(SolutionType.AVOIDABLE_RECTANGLE_2)) {
                        steps1 = this.stepFinder.getAllUniqueness(this.sudoku);
                        this.filterSteps(steps1);
                        this.steps.addAll(steps1);
                    }

                    if (this.isAllStepsEnabled(SolutionType.BUG_PLUS_1)) {
                        this.stepFinder.setSudoku(this.sudoku);
                        SolutionStep result = this.stepFinder.getStep(SolutionType.BUG_PLUS_1);
                        if (result != null) {
                            this.steps.add(result);
                        }
                    }

                    steps1 = this.stepFinder.getAllWings(this.sudoku);
                    this.filterSteps(steps1);
                    this.steps.addAll(steps1);
                    if (this.isAllStepsEnabled(SolutionType.SIMPLE_COLORS)) {
                        steps1 = this.stepFinder.findAllSimpleColors(this.sudoku);
                        this.steps.addAll(steps1);
                    }

                    if (this.isAllStepsEnabled(SolutionType.MULTI_COLORS)) {
                        steps1 = this.stepFinder.findAllMultiColors(this.sudoku);
                        this.steps.addAll(steps1);
                    }
                    break;
                case 20:
                    this.updateProgress(ResourceBundle.getBundle("intl/FindAllStepsProgressDialog").getString("FindAllStepsProgressDialog.chains"), actStep);
                    if (this.isAllStepsEnabled(SolutionType.X_CHAIN)
                            || this.isAllStepsEnabled(SolutionType.XY_CHAIN)
                            || this.isAllStepsEnabled(SolutionType.REMOTE_PAIR)
                            || this.isAllStepsEnabled(SolutionType.TURBOT_FISH)) {
                        steps1 = this.stepFinder.getAllChains(this.sudoku);
                        this.filterSteps(steps1);
                        this.steps.addAll(steps1);
                    }
                    break;
                case 21:
                    this.updateProgress(ResourceBundle.getBundle("intl/FindAllStepsProgressDialog").getString("FindAllStepsProgressDialog.nice_loops"), actStep);
                    if (this.isAllStepsEnabled(SolutionType.NICE_LOOP)) {
                        steps1 = this.stepFinder.getAllNiceLoops(this.sudoku);
                        this.steps.addAll(steps1);
                    }
                    break;
                case 22:
                    this.updateProgress(
                            ResourceBundle.getBundle("intl/FindAllStepsProgressDialog").getString("FindAllStepsProgressDialog.grouped_nice_loops"), actStep
                    );
                    if (this.isAllStepsEnabled(SolutionType.GROUPED_NICE_LOOP)) {
                        steps1 = this.stepFinder.getAllGroupedNiceLoops(this.sudoku);
                        this.steps.addAll(steps1);
                    }
                    break;
                case 23:
                    this.updateProgress(ResourceBundle.getBundle("intl/FindAllStepsProgressDialog").getString("FindAllStepsProgressDialog.templates"), actStep);
                    if (this.isAllStepsEnabled(SolutionType.TEMPLATE_DEL) || this.isAllStepsEnabled(SolutionType.TEMPLATE_SET)) {
                        steps1 = this.stepFinder.getAllTemplates(this.sudoku);
                        this.filterSteps(steps1);
                        this.steps.addAll(steps1);
                    }
                    break;
                case 24:
                    this.updateProgress(ResourceBundle.getBundle("intl/FindAllStepsProgressDialog").getString("FindAllStepsProgressDialog.als"), actStep);
                    if (this.isAllStepsEnabled(SolutionType.ALS_XZ)
                            || this.isAllStepsEnabled(SolutionType.ALS_XY_WING)
                            || this.isAllStepsEnabled(SolutionType.ALS_XY_CHAIN)) {
                        steps1 = this.stepFinder
                                .getAllAlses(
                                        this.sudoku,
                                        this.isAllStepsEnabled(SolutionType.ALS_XZ),
                                        this.isAllStepsEnabled(SolutionType.ALS_XY_WING),
                                        this.isAllStepsEnabled(SolutionType.ALS_XY_CHAIN)
                                );
                        this.filterSteps(steps1);
                        this.steps.addAll(steps1);
                    }

                    if (this.isAllStepsEnabled(SolutionType.DEATH_BLOSSOM)) {
                        steps1 = this.stepFinder.getAllDeathBlossoms(this.sudoku);
                        this.filterSteps(steps1);
                        this.steps.addAll(steps1);
                    }
                    break;
                case 25:
                    if (this.isAllStepsEnabled(SolutionType.FORCING_CHAIN)) {
                        this.updateProgress(
                                ResourceBundle.getBundle("intl/FindAllStepsProgressDialog").getString("FindAllStepsProgressDialog.forcing_Chains"), actStep
                        );
                        steps1 = this.stepFinder.getAllForcingChains(this.sudoku);
                        this.steps.addAll(steps1);
                    }
                    break;
                case 26:
                    if (this.isAllStepsEnabled(SolutionType.FORCING_NET)) {
                        this.updateProgress(ResourceBundle.getBundle("intl/FindAllStepsProgressDialog").getString("FindAllStepsProgressDialog.forcing_Nets"), actStep);
                        steps1 = this.stepFinder.getAllForcingNets(this.sudoku);
                        this.steps.addAll(steps1);
                    }
                    break;
                case 27:
                    this.updateProgress(ResourceBundle.getBundle("intl/FindAllStepsProgressDialog").getString("FindAllStepsProgressDialog.progress_Score"), actStep);
                    SudokuSolverFactory.getDefaultSolverInstance().getProgressScore(this.sudoku, this.steps, this.dlg);
                    break;
                default:
                    if (this.testTypes != null) {
                        return;
                    }

                    Thread.currentThread().interrupt();
            }

            actStep++;
        }

        if (this.dlg != null) {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (FindAllSteps.this.dlg != null) {
                        FindAllSteps.this.dlg.setVisible(false);
                    }
                }
            });
        }
    }

    public List<SolutionStep> getSteps() {
        return this.steps;
    }

    public void setSteps(List<SolutionStep> steps) {
        this.steps = steps;
    }

    public List<SolutionType> getTestType() {
        return this.testTypes;
    }

    public void setTestType(List<SolutionType> testStep) {
        this.testTypes = testStep;
    }

    public Sudoku2 getSudoku() {
        return this.sudoku;
    }

    public void setSudoku(Sudoku2 sudoku) {
        this.sudoku = sudoku;
    }
}
