package sudoku;

import solver.SudokuSolverFactory;
import solver.SudokuStepFinder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RegressionTester {
    private SudokuStepFinder stepFinder;
    private int anzTestCases = 0;
    private int anzGoodCases = 0;
    private int anzBadCases = 0;
    private int anzIgnoreCases = 0;
    private int anzNotImplementedCases = 0;
    private Map<String, Integer> ignoredTechniques = new TreeMap<>();
    private Map<String, Integer> notImplementedTechniques = new TreeMap<>();
    private Map<String, String> failedCases = new TreeMap<>();
    private boolean fastMode = false;

    public RegressionTester() {
        this.stepFinder = SudokuSolverFactory.getDefaultSolverInstance().getStepFinder();
    }

    public static void main(String[] args) {
        RegressionTester tester = new RegressionTester();
        tester.runTest("lib02.txt");
    }

    public void runTest(String testFile) {
        this.runTest(testFile, false);
    }

    public void runTest(String testFile, boolean fastMode) {
        this.fastMode = fastMode;
        String msg = "Starting test run for file " + testFile;
        if (fastMode) {
            msg = msg + " (fast mode)";
        }

        msg = msg + "...";
        System.out.println(msg);
        this.anzTestCases = 0;
        this.anzGoodCases = 0;
        this.anzBadCases = 0;
        this.anzIgnoreCases = 0;
        this.ignoredTechniques.clear();
        this.failedCases.clear();
        int anzLines = 0;

        try {
            BufferedReader in = new BufferedReader(new FileReader(testFile));
            String line = null;

            while ((line = in.readLine()) != null) {
                if (++anzLines % 10 == 0) {
                    System.out.print(".");
                }

                if (anzLines % 400 == 0) {
                    System.out.println();
                }

                line = line.trim();
                if (!line.startsWith("#") && !line.isEmpty()) {
                    this.test(line);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "error reading test cases...", ex);
        }

        System.out.println();
        System.out.println("Test finished!");
        System.out.println(this.anzTestCases + " cases total");
        System.out.println(this.anzGoodCases + " tests succeeded");
        System.out.println(this.anzBadCases + " tests failed");
        System.out.println(this.anzIgnoreCases + " tests were ignored");
        System.out.println(this.anzNotImplementedCases + " tests could not be run because the technique is not implemented");
        if (this.anzIgnoreCases != 0) {
            System.out.println("Ignored techniques:");

            for (String key : this.ignoredTechniques.keySet()) {
                System.out.println("  " + key + ": " + this.ignoredTechniques.get(key));
            }
        }

        if (this.anzNotImplementedCases != 0) {
            System.out.println("Test cases for techniques not implemented:");

            for (String key : this.notImplementedTechniques.keySet()) {
                System.out.println("  " + key + ": " + this.notImplementedTechniques.get(key));
            }
        }

        if (this.anzBadCases != 0) {
            System.out.println("Failed Cases:");

            for (String key : this.failedCases.keySet()) {
                System.out.println("  Should be:" + key);
                System.out.println("  Was:      " + this.failedCases.get(key));
            }
        }
    }

    public void test(String testCase) {
        this.anzTestCases++;
        String[] parts = testCase.split(":");
        int variant = 0;
        boolean failCase = false;
        if (parts[1].contains("-")) {
            int vIndex = parts[1].indexOf(45);
            if (parts[1].charAt(vIndex + 1) == 'x') {
                failCase = true;
            } else {
                try {
                    variant = Integer.parseInt(parts[1].substring(vIndex + 1));
                } catch (NumberFormatException ex) {
                    System.out.println("Invalid variant: " + parts[1]);
                    this.addIgnoredTechnique(testCase);
                    return;
                }
            }

            parts[1] = parts[1].substring(0, vIndex);
            testCase = "";

            for (int i = 0; i < parts.length; i++) {
                testCase = testCase + parts[i];
                if (i < 7) {
                    testCase = testCase + ":";
                }
            }

            if (parts.length < 7) {
                testCase = testCase + ":";
            }
        }

        String start = ":" + parts[1] + ":" + parts[2] + ":";
        SolutionType type = SolutionType.getTypeFromLibraryType(parts[1]);
        if (type == null) {
            this.addIgnoredTechnique(testCase);
        } else {
            Sudoku2 sudoku = new Sudoku2();
            sudoku.setSudoku(testCase);
            List<SolutionStep> steps = null;
            List<SolutionStep> steps1 = null;
            boolean oldOption = false;
            boolean oldOption2 = false;
            switch (type) {
                case FULL_HOUSE:
                    steps = this.stepFinder.findAllFullHouses(sudoku);
                    this.checkResults(testCase, steps, sudoku, start, failCase);
                    break;
                case HIDDEN_SINGLE:
                case HIDDEN_PAIR:
                case HIDDEN_TRIPLE:
                case HIDDEN_QUADRUPLE:
                    steps = this.stepFinder.findAllHiddenXle(sudoku);
                    this.checkResults(testCase, steps, sudoku, start, failCase);
                    break;
                case NAKED_SINGLE:
                case NAKED_PAIR:
                case NAKED_TRIPLE:
                case NAKED_QUADRUPLE:
                    steps = this.stepFinder.findAllNakedXle(sudoku);
                    this.checkResults(testCase, steps, sudoku, start, failCase);
                    break;
                case LOCKED_PAIR:
                case LOCKED_TRIPLE:
                    if (variant == 1) {
                        List<SolutionStep> var55 = this.stepFinder.findAllNakedXle(sudoku);
                        steps1 = this.stepFinder.findAllHiddenXle(sudoku);
                        var55.addAll(steps1);
                        this.checkResults(testCase, var55, sudoku, start, failCase);
                    } else {
                        this.anzNotImplementedCases++;
                        this.notImplementedTechniques.put(testCase, 1);
                    }
                    break;
                case LOCKED_CANDIDATES_1:
                case LOCKED_CANDIDATES_2:
                    steps = this.stepFinder.findAllLockedCandidates(sudoku);
                    this.checkResults(testCase, steps, sudoku, start, failCase);
                    break;
                case SKYSCRAPER:
                    oldOption = Options.getInstance().isAllowDualsAndSiamese();
                    Options.getInstance().setAllowDualsAndSiamese(false);
                    steps = this.stepFinder.findAllSkyScrapers(sudoku);
                    this.checkResults(testCase, steps, sudoku, start, failCase);
                    Options.getInstance().setAllowDualsAndSiamese(oldOption);
                    break;
                case TWO_STRING_KITE:
                    oldOption = Options.getInstance().isAllowDualsAndSiamese();
                    Options.getInstance().setAllowDualsAndSiamese(false);
                    steps = this.stepFinder.findAllTwoStringKites(sudoku);
                    this.checkResults(testCase, steps, sudoku, start, failCase);
                    Options.getInstance().setAllowDualsAndSiamese(oldOption);
                    break;
                case DUAL_TWO_STRING_KITE:
                    oldOption = Options.getInstance().isAllowDualsAndSiamese();
                    Options.getInstance().setAllowDualsAndSiamese(true);
                    steps = this.stepFinder.findAllTwoStringKites(sudoku);
                    this.checkResults(testCase, steps, sudoku, start, failCase);
                    Options.getInstance().setAllowDualsAndSiamese(oldOption);
                    break;
                case EMPTY_RECTANGLE:
                    oldOption = Options.getInstance().isAllowErsWithOnlyTwoCandidates();
                    if (variant == 1) {
                        Options.getInstance().setAllowErsWithOnlyTwoCandidates(true);
                    }

                    List<SolutionStep> var50 = this.stepFinder.findAllEmptyRectangles(sudoku);
                    this.checkResults(testCase, var50, sudoku, start, failCase);
                    Options.getInstance().setAllowErsWithOnlyTwoCandidates(oldOption);
                    break;
                case DUAL_EMPTY_RECTANGLE:
                    oldOption = Options.getInstance().isAllowErsWithOnlyTwoCandidates();
                    oldOption2 = Options.getInstance().isAllowDualsAndSiamese();
                    Options.getInstance().setAllowErsWithOnlyTwoCandidates(true);
                    Options.getInstance().setAllowDualsAndSiamese(true);
                    steps = this.stepFinder.findAllEmptyRectangles(sudoku);
                    this.checkResults(testCase, steps, sudoku, start, failCase);
                    Options.getInstance().setAllowErsWithOnlyTwoCandidates(oldOption);
                    Options.getInstance().setAllowDualsAndSiamese(oldOption2);
                    break;
                case SIMPLE_COLORS:
                case SIMPLE_COLORS_TRAP:
                case SIMPLE_COLORS_WRAP:
                    steps = this.stepFinder.findAllSimpleColors(sudoku);
                    this.checkResults(testCase, steps, sudoku, start, failCase);
                    break;
                case MULTI_COLORS:
                case MULTI_COLORS_1:
                case MULTI_COLORS_2:
                    steps = this.stepFinder.findAllMultiColors(sudoku);
                    this.checkResults(testCase, steps, sudoku, start, failCase);
                    break;
                case UNIQUENESS_1:
                case UNIQUENESS_2:
                case UNIQUENESS_3:
                case UNIQUENESS_4:
                case UNIQUENESS_5:
                case UNIQUENESS_6:
                case HIDDEN_RECTANGLE:
                case AVOIDABLE_RECTANGLE_1:
                case AVOIDABLE_RECTANGLE_2:
                    oldOption = Options.getInstance().isAllowUniquenessMissingCandidates();
                    if (variant == 1) {
                        Options.getInstance().setAllowUniquenessMissingCandidates(false);
                    } else if (variant == 2) {
                        Options.getInstance().setAllowUniquenessMissingCandidates(true);
                    }

                    List<SolutionStep> var46 = this.stepFinder.getAllUniqueness(sudoku);
                    this.checkResults(testCase, var46, sudoku, start, failCase);
                    Options.getInstance().setAllowUniquenessMissingCandidates(oldOption);
                    break;
                case BUG_PLUS_1:
                    steps = new ArrayList<>();
                    this.stepFinder.setSudoku(sudoku);
                    SolutionStep step = this.stepFinder.getStep(type);
                    if (step != null) {
                        steps.add(step);
                    }

                    this.checkResults(testCase, steps, sudoku, start, failCase);
                    break;
                case XY_WING:
                case XYZ_WING:
                case W_WING:
                    steps = this.stepFinder.getAllWings(sudoku);
                    this.checkResults(testCase, steps, sudoku, start, failCase);
                    break;
                case TURBOT_FISH:
                case X_CHAIN:
                case XY_CHAIN:
                case REMOTE_PAIR:
                    oldOption = Options.getInstance().isOnlyOneChainPerStep();
                    Options.getInstance().setOnlyOneChainPerStep(false);
                    steps = this.stepFinder.getAllChains(sudoku);
                    this.checkResults(testCase, steps, sudoku, start, failCase);
                    Options.getInstance().setOnlyOneChainPerStep(oldOption);
                    break;
                case CONTINUOUS_NICE_LOOP:
                case DISCONTINUOUS_NICE_LOOP:
                case AIC:
                    oldOption = Options.getInstance().isOnlyOneChainPerStep();
                    Options.getInstance().setOnlyOneChainPerStep(false);
                    steps = this.stepFinder.getAllNiceLoops(sudoku);
                    this.checkResults(testCase, steps, sudoku, start, failCase);
                    Options.getInstance().setOnlyOneChainPerStep(oldOption);
                    break;
                case GROUPED_CONTINUOUS_NICE_LOOP:
                case GROUPED_DISCONTINUOUS_NICE_LOOP:
                case GROUPED_AIC:
                    oldOption = Options.getInstance().isOnlyOneChainPerStep();
                    oldOption2 = Options.getInstance().isAllowAlsInTablingChains();
                    Options.getInstance().setOnlyOneChainPerStep(false);
                    if ((type != SolutionType.GROUPED_CONTINUOUS_NICE_LOOP || variant != 2)
                            && (type != SolutionType.GROUPED_DISCONTINUOUS_NICE_LOOP || variant != 3 && variant != 4)
                            && (type != SolutionType.GROUPED_AIC || variant != 3 && variant != 4)) {
                        Options.getInstance().setAllowAlsInTablingChains(false);
                    } else {
                        Options.getInstance().setAllowAlsInTablingChains(true);
                    }

                    List<SolutionStep> var41 = this.stepFinder.getAllGroupedNiceLoops(sudoku);
                    this.checkResults(testCase, var41, sudoku, start, failCase);
                    Options.getInstance().setOnlyOneChainPerStep(oldOption);
                    Options.getInstance().setAllowAlsInTablingChains(oldOption2);
                    break;
                case X_WING:
                case FINNED_X_WING:
                case SASHIMI_X_WING:
                    steps = this.findAllFishes(sudoku, 2, 0);
                    this.checkResults(testCase, steps, sudoku, start, failCase);
                    break;
                case FRANKEN_X_WING:
                case FINNED_FRANKEN_X_WING:
                    steps = this.findAllFishes(sudoku, 2, 1);
                    this.checkResults(testCase, steps, sudoku, start, failCase);
                    break;
                case MUTANT_X_WING:
                case FINNED_MUTANT_X_WING:
                    steps = this.findAllFishes(sudoku, 2, 2);
                    this.checkResults(testCase, steps, sudoku, start, failCase);
                    break;
                case SWORDFISH:
                case FINNED_SWORDFISH:
                case SASHIMI_SWORDFISH:
                    oldOption = Options.getInstance().isAllowDualsAndSiamese();
                    Options.getInstance().setAllowDualsAndSiamese(true);
                    steps = this.findAllFishes(sudoku, 3, 0);
                    this.checkResults(testCase, steps, sudoku, start, failCase);
                    Options.getInstance().setAllowDualsAndSiamese(oldOption);
                    break;
                case FRANKEN_SWORDFISH:
                case FINNED_FRANKEN_SWORDFISH:
                    oldOption = Options.getInstance().isAllowDualsAndSiamese();
                    Options.getInstance().setAllowDualsAndSiamese(true);
                    if (!this.fastMode) {
                        steps = this.findAllFishes(sudoku, 3, 1);
                        this.checkResults(testCase, steps, sudoku, start, failCase);
                    } else {
                        this.anzIgnoreCases++;
                        this.ignoredTechniques.put(testCase, 1);
                    }

                    Options.getInstance().setAllowDualsAndSiamese(oldOption);
                    break;
                case MUTANT_SWORDFISH:
                case FINNED_MUTANT_SWORDFISH:
                    oldOption = Options.getInstance().isAllowDualsAndSiamese();
                    Options.getInstance().setAllowDualsAndSiamese(true);
                    if (!this.fastMode) {
                        steps = this.findAllFishes(sudoku, 3, 2);
                        this.checkResults(testCase, steps, sudoku, start, failCase);
                    } else {
                        this.anzIgnoreCases++;
                        this.ignoredTechniques.put(testCase, 1);
                    }

                    Options.getInstance().setAllowDualsAndSiamese(oldOption);
                    break;
                case JELLYFISH:
                case FINNED_JELLYFISH:
                case SASHIMI_JELLYFISH:
                    oldOption = Options.getInstance().isAllowDualsAndSiamese();
                    Options.getInstance().setAllowDualsAndSiamese(true);
                    steps = this.findAllFishes(sudoku, 4, 0);
                    this.checkResults(testCase, steps, sudoku, start, failCase);
                    Options.getInstance().setAllowDualsAndSiamese(oldOption);
                    break;
                case FRANKEN_JELLYFISH:
                case FINNED_FRANKEN_JELLYFISH:
                    oldOption = Options.getInstance().isAllowDualsAndSiamese();
                    Options.getInstance().setAllowDualsAndSiamese(true);
                    if (!this.fastMode) {
                        steps = this.findAllFishes(sudoku, 4, 1);
                        this.checkResults(testCase, steps, sudoku, start, failCase);
                    } else {
                        this.anzIgnoreCases++;
                        this.ignoredTechniques.put(testCase, 1);
                    }

                    Options.getInstance().setAllowDualsAndSiamese(oldOption);
                    break;
                case MUTANT_JELLYFISH:
                case FINNED_MUTANT_JELLYFISH:
                    oldOption = Options.getInstance().isAllowDualsAndSiamese();
                    Options.getInstance().setAllowDualsAndSiamese(true);
                    if (!this.fastMode) {
                        steps = this.findAllFishes(sudoku, 4, 2);
                        this.checkResults(testCase, steps, sudoku, start, failCase);
                    } else {
                        this.anzIgnoreCases++;
                        this.ignoredTechniques.put(testCase, 1);
                    }

                    Options.getInstance().setAllowDualsAndSiamese(oldOption);
                    break;
                case SQUIRMBAG:
                case FINNED_SQUIRMBAG:
                case SASHIMI_SQUIRMBAG:
                    steps = this.findAllFishes(sudoku, 5, 0);
                    this.checkResults(testCase, steps, sudoku, start, failCase);
                    break;
                case FRANKEN_SQUIRMBAG:
                case FINNED_FRANKEN_SQUIRMBAG:
                    if (!this.fastMode) {
                        steps = this.findAllFishes(sudoku, 5, 1);
                        this.checkResults(testCase, steps, sudoku, start, failCase);
                    } else {
                        this.anzIgnoreCases++;
                        this.ignoredTechniques.put(testCase, 1);
                    }
                    break;
                case MUTANT_SQUIRMBAG:
                case FINNED_MUTANT_SQUIRMBAG:
                    if (!this.fastMode) {
                        steps = this.findAllFishes(sudoku, 5, 2);
                        this.checkResults(testCase, steps, sudoku, start, failCase);
                    } else {
                        this.anzIgnoreCases++;
                        this.ignoredTechniques.put(testCase, 1);
                    }
                    break;
                case WHALE:
                case FINNED_WHALE:
                case SASHIMI_WHALE:
                    if (!this.fastMode) {
                        steps = this.findAllFishes(sudoku, 6, 0);
                        this.checkResults(testCase, steps, sudoku, start, failCase);
                    } else {
                        this.anzIgnoreCases++;
                        this.ignoredTechniques.put(testCase, 1);
                    }
                    break;
                case FRANKEN_WHALE:
                case FINNED_FRANKEN_WHALE:
                    if (!this.fastMode) {
                        steps = this.findAllFishes(sudoku, 6, 1);
                        this.checkResults(testCase, steps, sudoku, start, failCase);
                    } else {
                        this.anzIgnoreCases++;
                        this.ignoredTechniques.put(testCase, 1);
                    }
                    break;
                case MUTANT_WHALE:
                case FINNED_MUTANT_WHALE:
                    if (!this.fastMode) {
                        steps = this.findAllFishes(sudoku, 6, 2);
                        this.checkResults(testCase, steps, sudoku, start, failCase);
                    } else {
                        this.anzIgnoreCases++;
                        this.ignoredTechniques.put(testCase, 1);
                    }
                    break;
                case LEVIATHAN:
                case FINNED_LEVIATHAN:
                case SASHIMI_LEVIATHAN:
                    if (!this.fastMode) {
                        steps = this.findAllFishes(sudoku, 7, 0);
                        this.checkResults(testCase, steps, sudoku, start, failCase);
                    } else {
                        this.anzIgnoreCases++;
                        this.ignoredTechniques.put(testCase, 1);
                    }
                    break;
                case FRANKEN_LEVIATHAN:
                case FINNED_FRANKEN_LEVIATHAN:
                    if (!this.fastMode) {
                        steps = this.findAllFishes(sudoku, 7, 1);
                        this.checkResults(testCase, steps, sudoku, start, failCase);
                    } else {
                        this.anzIgnoreCases++;
                        this.ignoredTechniques.put(testCase, 1);
                    }
                    break;
                case MUTANT_LEVIATHAN:
                case FINNED_MUTANT_LEVIATHAN:
                    if (!this.fastMode) {
                        steps = this.findAllFishes(sudoku, 7, 2);
                        this.checkResults(testCase, steps, sudoku, start, failCase);
                    } else {
                        this.anzIgnoreCases++;
                        this.ignoredTechniques.put(testCase, 1);
                    }
                    break;
                case SUE_DE_COQ:
                    steps = this.stepFinder.getAllSueDeCoqs(sudoku);
                    this.checkResults(testCase, steps, sudoku, start, failCase);
                    break;
                case ALS_XZ:
                case ALS_XY_WING:
                case ALS_XY_CHAIN:
                    oldOption = Options.getInstance().isOnlyOneAlsPerStep();
                    oldOption2 = Options.getInstance().isAllowAlsOverlap();
                    Options.getInstance().setOnlyOneAlsPerStep(false);
                    Options.getInstance().setAllowAlsOverlap(false);
                    if (type == SolutionType.ALS_XY_CHAIN && variant == 2 || type == SolutionType.ALS_XY_WING && variant == 2) {
                        Options.getInstance().setAllowAlsOverlap(true);
                    }

                    List<SolutionStep> var21 = this.stepFinder
                            .getAllAlses(sudoku, type == SolutionType.ALS_XZ, type == SolutionType.ALS_XY_WING, type == SolutionType.ALS_XY_CHAIN);
                    this.checkResults(testCase, var21, sudoku, start, failCase);
                    Options.getInstance().setOnlyOneAlsPerStep(oldOption);
                    Options.getInstance().setAllowAlsOverlap(oldOption2);
                    break;
                case DEATH_BLOSSOM:
                    oldOption = Options.getInstance().isOnlyOneAlsPerStep();
                    oldOption2 = Options.getInstance().isAllowAlsOverlap();
                    Options.getInstance().setOnlyOneAlsPerStep(false);
                    Options.getInstance().setAllowAlsOverlap(false);
                    if (variant == 2) {
                        Options.getInstance().setAllowAlsOverlap(true);
                    }

                    steps = this.stepFinder.getAllDeathBlossoms(sudoku);
                    this.checkResults(testCase, steps, sudoku, start, failCase);
                    Options.getInstance().setOnlyOneAlsPerStep(oldOption);
                    Options.getInstance().setAllowAlsOverlap(oldOption2);
                    break;
                case TEMPLATE_SET:
                case TEMPLATE_DEL:
                    steps = this.stepFinder.getAllTemplates(sudoku);
                    this.checkResults(testCase, steps, sudoku, start, failCase);
                    break;
                case FORCING_CHAIN_CONTRADICTION:
                case FORCING_CHAIN_VERITY:
                    oldOption = Options.getInstance().isOnlyOneChainPerStep();
                    oldOption2 = Options.getInstance().isAllowAlsInTablingChains();
                    Options.getInstance().setOnlyOneChainPerStep(false);
                    Options.getInstance().setAllowAlsInTablingChains(false);
                    steps = this.stepFinder.getAllForcingChains(sudoku);
                    this.checkResults(testCase, steps, sudoku, start, failCase);
                    Options.getInstance().setOnlyOneChainPerStep(oldOption);
                    Options.getInstance().setAllowAlsInTablingChains(oldOption2);
                    break;
                case FORCING_NET_CONTRADICTION:
                case FORCING_NET_VERITY:
                    oldOption = Options.getInstance().isOnlyOneChainPerStep();
                    oldOption2 = Options.getInstance().isAllowAlsInTablingChains();
                    Options.getInstance().setOnlyOneChainPerStep(false);
                    Options.getInstance().setAllowAlsInTablingChains(false);
                    steps = this.stepFinder.getAllForcingNets(sudoku);
                    this.checkResults(testCase, steps, sudoku, start, failCase);
                    Options.getInstance().setOnlyOneChainPerStep(oldOption);
                    Options.getInstance().setAllowAlsInTablingChains(oldOption2);
                    break;
                case KRAKEN_FISH_TYPE_1:
                case KRAKEN_FISH_TYPE_2:
                    oldOption = Options.getInstance().isOnlyOneFishPerStep();
                    oldOption2 = Options.getInstance().isCheckTemplates();
                    Options.getInstance().setOnlyOneFishPerStep(false);
                    Options.getInstance().setCheckTemplates(true);
                    steps = this.stepFinder
                            .getAllKrakenFishes(sudoku, 2, 4, Options.getInstance().getAllStepsMaxFins(), Options.getInstance().getAllStepsMaxEndoFins(), null, -1, 1);
                    this.checkResults(testCase, steps, sudoku, start, failCase);
                    Options.getInstance().setOnlyOneFishPerStep(oldOption);
                    Options.getInstance().setCheckTemplates(oldOption2);
                    break;
                default:
                    this.anzIgnoreCases++;
                    this.addIgnoredTechnique(testCase);
            }
        }
    }

    private List<SolutionStep> findAllFishes(Sudoku2 sudoku, int size, int type) {
        boolean oldOption = Options.getInstance().isOnlyOneFishPerStep();
        boolean oldOption2 = Options.getInstance().isCheckTemplates();
        Options.getInstance().setOnlyOneFishPerStep(false);
        Options.getInstance().setCheckTemplates(true);
        List<SolutionStep> steps = this.stepFinder
                .getAllFishes(sudoku, size, size, Options.getInstance().getAllStepsMaxFins(), Options.getInstance().getAllStepsMaxEndoFins(), null, -1, type);
        Options.getInstance().setOnlyOneFishPerStep(oldOption);
        Options.getInstance().setCheckTemplates(oldOption2);
        return steps;
    }

    private void checkResults(String testCase, List<SolutionStep> steps, Sudoku2 sudoku, String start, boolean failCase) {
        boolean found = false;
        boolean exactMatch = false;
        boolean good = true;

        for (SolutionStep step : steps) {
            String result = sudoku.getSudoku(ClipboardMode.LIBRARY, step);
            if (result.startsWith(start)) {
                found = true;
                if (!result.equals(testCase)) {
                    if (exactMatch) {
                        int index1 = testCase.lastIndexOf(58);
                        int index2 = result.lastIndexOf(58);
                        if (testCase.substring(0, index1).equals(result.substring(0, index2))) {
                            continue;
                        }
                    }

                    good = false;
                    this.failedCases.put(testCase, result);
                } else {
                    exactMatch = true;
                }
            }
        }

        if (failCase) {
            if (found) {
                this.anzBadCases++;
                this.failedCases.put(testCase, "Step found for fail case!");
            } else {
                this.anzGoodCases++;
            }
        } else if (!found) {
            this.anzBadCases++;
            this.failedCases.put(testCase, "No step found!");
        } else if (!good) {
            this.anzBadCases++;
        } else {
            this.anzGoodCases++;
        }
    }

    private void addIgnoredTechnique(String technique) {
        int count = 1;
        if (this.ignoredTechniques.containsKey(technique)) {
            count = this.ignoredTechniques.get(technique);
            count++;
        }

        this.ignoredTechniques.put(technique, count);
        this.anzIgnoreCases++;
    }

    private void addNotImplementedTechnique(String technique) {
        int count = 1;
        if (this.notImplementedTechniques.containsKey(technique)) {
            count = this.notImplementedTechniques.get(technique);
            count++;
        }

        this.notImplementedTechniques.put(technique, count);
        this.anzNotImplementedCases++;
    }
}
