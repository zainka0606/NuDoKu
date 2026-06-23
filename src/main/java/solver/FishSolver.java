package solver;

import sudoku.*;

import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.*;

public class FishSolver extends AbstractSolver {
    private static final SolutionType[] BASIC_TYPES = new SolutionType[]{
            SolutionType.X_WING, SolutionType.SWORDFISH, SolutionType.JELLYFISH, SolutionType.SQUIRMBAG, SolutionType.WHALE, SolutionType.LEVIATHAN
    };
    private static final SolutionType[] FINNED_BASIC_TYPES = new SolutionType[]{
            SolutionType.FINNED_X_WING,
            SolutionType.FINNED_SWORDFISH,
            SolutionType.FINNED_JELLYFISH,
            SolutionType.FINNED_SQUIRMBAG,
            SolutionType.FINNED_WHALE,
            SolutionType.FINNED_LEVIATHAN
    };
    private static final SolutionType[] SASHIMI_BASIC_TYPES = new SolutionType[]{
            SolutionType.SASHIMI_X_WING,
            SolutionType.SASHIMI_SWORDFISH,
            SolutionType.SASHIMI_JELLYFISH,
            SolutionType.SASHIMI_SQUIRMBAG,
            SolutionType.SASHIMI_WHALE,
            SolutionType.SASHIMI_LEVIATHAN
    };
    private static final SolutionType[] FRANKEN_TYPES = new SolutionType[]{
            SolutionType.FRANKEN_X_WING,
            SolutionType.FRANKEN_SWORDFISH,
            SolutionType.FRANKEN_JELLYFISH,
            SolutionType.FRANKEN_SQUIRMBAG,
            SolutionType.FRANKEN_WHALE,
            SolutionType.FRANKEN_LEVIATHAN
    };
    private static final SolutionType[] FINNED_FRANKEN_TYPES = new SolutionType[]{
            SolutionType.FINNED_FRANKEN_X_WING,
            SolutionType.FINNED_FRANKEN_SWORDFISH,
            SolutionType.FINNED_FRANKEN_JELLYFISH,
            SolutionType.FINNED_FRANKEN_SQUIRMBAG,
            SolutionType.FINNED_FRANKEN_WHALE,
            SolutionType.FINNED_FRANKEN_LEVIATHAN
    };
    private static final SolutionType[] MUTANT_TYPES = new SolutionType[]{
            SolutionType.MUTANT_X_WING,
            SolutionType.MUTANT_SWORDFISH,
            SolutionType.MUTANT_JELLYFISH,
            SolutionType.MUTANT_SQUIRMBAG,
            SolutionType.MUTANT_WHALE,
            SolutionType.MUTANT_LEVIATHAN
    };
    private static final SolutionType[] FINNED_MUTANT_TYPES = new SolutionType[]{
            SolutionType.FINNED_MUTANT_X_WING,
            SolutionType.FINNED_MUTANT_SWORDFISH,
            SolutionType.FINNED_MUTANT_JELLYFISH,
            SolutionType.FINNED_MUTANT_SQUIRMBAG,
            SolutionType.FINNED_MUTANT_WHALE,
            SolutionType.FINNED_MUTANT_LEVIATHAN
    };
    private static final int UNDEFINED = -1;
    private static final int BASIC = 0;
    private static final int FRANKEN = 1;
    private static final int MUTANT = 2;
    private static final int LINE_MASK = 1;
    private static final int COL_MASK = 2;
    private static final int BLOCK_MASK = 4;
    private static final int[] MASKS = new int[]{4, 1, 2};
    private int candidate;
    private long candidatesM1;
    private long candidatesM2;
    private long delCandTemplatesM1;
    private long delCandTemplatesM2;
    private SudokuSet createFishSet = new SudokuSet();
    private boolean withoutFins;
    private boolean withFins;
    private boolean withEndoFins;
    private boolean sashimi;
    private boolean kraken;
    private int fishType = -1;
    private int minSize;
    private int maxSize;
    private int[] baseUnits = new int[27];
    private long[] baseCandidatesM1 = new long[27];
    private long[] baseCandidatesM2 = new long[27];
    private int numberOfBaseUnits = 0;
    private int[] allCoverUnits = new int[27];
    private long[] allCoverCandidatesM1 = new long[27];
    private long[] allCoverCandidatesM2 = new long[27];
    private int numberOfAllCoverUnits = 0;
    private int[] coverUnits = new int[27];
    private long[] coverCandidatesM1 = new long[27];
    private long[] coverCandidatesM2 = new long[27];
    private int numberOfCoverUnits = 0;
    private boolean[] baseUnitsUsed = new boolean[this.baseUnits.length];
    private FishSolver.BaseStackEntry[] baseStack = new FishSolver.BaseStackEntry[9];
    private int baseLevel = 0;
    private boolean[] coverUnitsUsed = new boolean[this.allCoverUnits.length];
    private FishSolver.CoverStackEntry[] coverStack = new FishSolver.CoverStackEntry[9];
    private int coverLevel = 0;
    private SortedMap<String, Integer> deletesMap = new TreeMap<>();
    private long aktEndoFinSetM1;
    private long aktEndoFinSetM2;
    private long aktCannibalismSetM1;
    private long aktCannibalismSetM2;
    private SudokuSet templateSet = new SudokuSet();
    private SudokuSet krakenDeleteCandSet = new SudokuSet();
    private SudokuSet krakenFinSet = new SudokuSet();
    private SudokuSet krakenCannibalisticSet = new SudokuSet();
    private FindAllStepsProgressDialog dlg = null;
    private SolutionStep globalStep = new SolutionStep(SolutionType.HIDDEN_SINGLE);
    private TablingSolver tablingSolver = null;
    private long tmpSetM1;
    private long tmpSetM2;
    private long tmpSet1M1;
    private long tmpSet1M2;
    private long tmpSet2M1;
    private long tmpSet2M2;
    private SudokuSet getBuddiesSet = new SudokuSet();
    private long checkSashimiSetM1;
    private long checkSashimiSetM2;
    private long finBuddiesM1;
    private long finBuddiesM2;
    private long finsM1;
    private long finsM2;
    private boolean searchAll;
    private boolean siamese;
    private boolean doTemplates;
    private List<SolutionStep> steps = new ArrayList<>();
    private List<SolutionStep> cachedSteps = new ArrayList<>();
    private int lastStepNumber = 0;
    private int maxBaseCombinations = 0;
    private int baseGesamt = 0;
    private int baseShowGesamt = 0;
    private int coverGesamt = 0;
    private int versucheFisch = 0;
    private int versucheFins = 0;
    private int[] anzFins = new int[]{
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0
    };

    protected FishSolver(SudokuStepFinder finder) {
        super(finder);

        for (int i = 0; i < this.baseStack.length; i++) {
            this.baseStack[i] = new FishSolver.BaseStackEntry();
        }

        for (int i = 0; i < this.coverStack.length; i++) {
            this.coverStack[i] = new FishSolver.CoverStackEntry();
        }
    }

    public static void main(String[] args) {
        try {
            XMLDecoder in = new XMLDecoder(
                    new BufferedInputStream(new FileInputStream("C:\\Sudoku\\Sonstiges\\Bug reports\\20111208 Comparison Exception\\fishse1326274402326.dat"))
            );
            List<SolutionStep> steps = (List<SolutionStep>) in.readObject();
            in.close();
            System.out.println("anz: " + steps.size());

            for (int i = 0; i < steps.size(); i++) {
                for (int j = i + 1; j < steps.size(); j++) {
                    int c1 = steps.get(i).compareTo(steps.get(j));
                    int c2 = steps.get(j).compareTo(steps.get(i));
                    if (c1 != 0 || c2 != 0) {
                        if (c1 == 0 && c2 != 0 || c2 == 0 && c1 != 0) {
                            System.out.println("error: " + c1 + "/" + c2 + "/" + i + "/" + j);
                        }

                        if (c1 < 0 && c2 < 0 || c1 > 0 && c2 > 0) {
                            System.out.println("error: " + c1 + "/" + c2 + "/" + i + "/" + j);
                        }
                    }
                }
            }

            int counter = 0;

            for (int i = 0; i < steps.size(); i++) {
                for (int j = 0; j < steps.size(); j++) {
                    if (j != i) {
                        for (int k = 0; k < steps.size(); k++) {
                            if (k != i && k != j) {
                                counter++;
                                int cij = steps.get(i).compareTo(steps.get(j));
                                int cjk = steps.get(j).compareTo(steps.get(k));
                                int cik = steps.get(i).compareTo(steps.get(k));
                                if (cij != 0 || cik != 0 || cjk != 0) {
                                    if (cij <= 0 && cjk <= 0 && cik >= 0) {
                                        System.out.println("error: " + cij + "/" + cjk + "/" + cik + " - " + i + "/" + j + "/" + k);
                                    }

                                    if (cij >= 0 && cjk >= 0 && cik <= 0) {
                                        System.out.println("error: " + cij + "/" + cjk + "/" + cik + " - " + i + "/" + j + "/" + k);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            System.out.println("Counter = " + counter);
            Collections.sort(steps);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        System.exit(0);
    }

    @Override
    protected SolutionStep getStep(SolutionType type) {
        SolutionStep result = null;
        this.sudoku = this.finder.getSudoku();
        int size = 2;
        switch (type) {
            case LEVIATHAN:
                size++;
            case WHALE:
                size++;
            case SQUIRMBAG:
                size++;
            case JELLYFISH:
                size++;
            case SWORDFISH:
                size++;
            case X_WING:
                this.searchAll = false;
                result = this.getAnyFish(size, true, false, false, false, 0);
                break;
            case FINNED_LEVIATHAN:
                size++;
            case FINNED_WHALE:
                size++;
            case FINNED_SQUIRMBAG:
                size++;
            case FINNED_JELLYFISH:
                size++;
            case FINNED_SWORDFISH:
                size++;
            case FINNED_X_WING:
                this.searchAll = false;
                result = this.getAnyFish(size, false, true, false, false, 0);
                break;
            case SASHIMI_LEVIATHAN:
                size++;
            case SASHIMI_WHALE:
                size++;
            case SASHIMI_SQUIRMBAG:
                size++;
            case SASHIMI_JELLYFISH:
                size++;
            case SASHIMI_SWORDFISH:
                size++;
            case SASHIMI_X_WING:
                this.searchAll = false;
                result = this.getAnyFish(size, false, true, true, false, 0);
                break;
            case FRANKEN_LEVIATHAN:
                size++;
            case FRANKEN_WHALE:
                size++;
            case FRANKEN_SQUIRMBAG:
                size++;
            case FRANKEN_JELLYFISH:
                size++;
            case FRANKEN_SWORDFISH:
                size++;
            case FRANKEN_X_WING:
                this.searchAll = false;
                result = this.getAnyFish(size, true, false, false, true, 1);
                break;
            case FINNED_FRANKEN_LEVIATHAN:
                size++;
            case FINNED_FRANKEN_WHALE:
                size++;
            case FINNED_FRANKEN_SQUIRMBAG:
                size++;
            case FINNED_FRANKEN_JELLYFISH:
                size++;
            case FINNED_FRANKEN_SWORDFISH:
                size++;
            case FINNED_FRANKEN_X_WING:
                this.searchAll = false;
                result = this.getAnyFish(size, false, true, false, true, 1);
                break;
            case MUTANT_LEVIATHAN:
                size++;
            case MUTANT_WHALE:
                size++;
            case MUTANT_SQUIRMBAG:
                size++;
            case MUTANT_JELLYFISH:
                size++;
            case MUTANT_SWORDFISH:
                size++;
            case MUTANT_X_WING:
                this.searchAll = false;
                result = this.getAnyFish(size, true, false, false, true, 2);
                break;
            case FINNED_MUTANT_LEVIATHAN:
                size++;
            case FINNED_MUTANT_WHALE:
                size++;
            case FINNED_MUTANT_SQUIRMBAG:
                size++;
            case FINNED_MUTANT_JELLYFISH:
                size++;
            case FINNED_MUTANT_SWORDFISH:
                size++;
            case FINNED_MUTANT_X_WING:
                this.searchAll = false;
                result = this.getAnyFish(size, false, true, false, true, 2);
                break;
            case KRAKEN_FISH:
            case KRAKEN_FISH_TYPE_1:
            case KRAKEN_FISH_TYPE_2:
                this.searchAll = false;
                result = this.getKrakenFish();
        }

        return result;
    }

    @Override
    protected boolean doStep(SolutionStep step) {
        boolean handled = true;
        this.sudoku = this.finder.getSudoku();
        switch (step.getType()) {
            case LEVIATHAN:
            case WHALE:
            case SQUIRMBAG:
            case JELLYFISH:
            case SWORDFISH:
            case X_WING:
            case FINNED_LEVIATHAN:
            case FINNED_WHALE:
            case FINNED_SQUIRMBAG:
            case FINNED_JELLYFISH:
            case FINNED_SWORDFISH:
            case FINNED_X_WING:
            case SASHIMI_LEVIATHAN:
            case SASHIMI_WHALE:
            case SASHIMI_SQUIRMBAG:
            case SASHIMI_JELLYFISH:
            case SASHIMI_SWORDFISH:
            case SASHIMI_X_WING:
            case FRANKEN_LEVIATHAN:
            case FRANKEN_WHALE:
            case FRANKEN_SQUIRMBAG:
            case FRANKEN_JELLYFISH:
            case FRANKEN_SWORDFISH:
            case FRANKEN_X_WING:
            case FINNED_FRANKEN_LEVIATHAN:
            case FINNED_FRANKEN_WHALE:
            case FINNED_FRANKEN_SQUIRMBAG:
            case FINNED_FRANKEN_JELLYFISH:
            case FINNED_FRANKEN_SWORDFISH:
            case FINNED_FRANKEN_X_WING:
            case MUTANT_LEVIATHAN:
            case MUTANT_WHALE:
            case MUTANT_SQUIRMBAG:
            case MUTANT_JELLYFISH:
            case MUTANT_SWORDFISH:
            case MUTANT_X_WING:
            case FINNED_MUTANT_LEVIATHAN:
            case FINNED_MUTANT_WHALE:
            case FINNED_MUTANT_SQUIRMBAG:
            case FINNED_MUTANT_JELLYFISH:
            case FINNED_MUTANT_SWORDFISH:
            case FINNED_MUTANT_X_WING:
            case KRAKEN_FISH:
            case KRAKEN_FISH_TYPE_1:
            case KRAKEN_FISH_TYPE_2:
                for (Candidate cand : step.getCandidatesToDelete()) {
                    this.sudoku.delCandidate(cand.getIndex(), cand.getValue());
                }
                break;
            default:
                handled = false;
        }

        return handled;
    }

    protected List<SolutionStep> getAllFishes(int minSize, int maxSize, int maxFins, int maxEndoFins, FindAllStepsProgressDialog dlg, int forCandidate, int type) {
        this.dlg = dlg;
        this.sudoku = this.finder.getSudoku();
        int oldMaxFins = Options.getInstance().getMaxFins();
        int oldEndoFins = Options.getInstance().getMaxEndoFins();
        Options.getInstance().setMaxFins(maxFins);
        Options.getInstance().setMaxEndoFins(maxEndoFins);
        List<SolutionStep> oldSteps = this.steps;
        this.steps = new ArrayList<>();
        this.kraken = false;
        this.searchAll = true;
        this.fishType = -1;
        long millis1 = System.currentTimeMillis();

        for (int i = 1; i <= 9; i++) {
            if (forCandidate == -1 || forCandidate == i) {
                long millis = System.currentTimeMillis();
                this.baseGesamt = 0;
                this.baseShowGesamt = 0;
                this.getFishes(i, minSize, maxSize, true, true, false, true, type);
                millis = System.currentTimeMillis() - millis;
            }
        }

        millis1 = System.currentTimeMillis() - millis1;
        List<SolutionStep> result = this.steps;
        if (result != null) {
            this.findSiameseFish(result);
            Collections.sort(result);
        }

        this.steps = oldSteps;
        Options.getInstance().setMaxFins(oldMaxFins);
        Options.getInstance().setMaxEndoFins(oldEndoFins);
        this.dlg = null;
        return result;
    }

    private SolutionStep getAnyFish(int size, boolean withoutFins, boolean withFins, boolean sashimi, boolean withEndoFins, int fishType) {
        this.searchAll = false;
        if (this.finder.getStepNumber() != this.lastStepNumber) {
            this.cachedSteps.clear();
            this.lastStepNumber = this.finder.getStepNumber();
        } else {
            for (int i = 0; i < this.cachedSteps.size(); i++) {
                SolutionStep step = this.cachedSteps.get(i);
                SolutionType type = step.getType();
                if (type.getFishSize() == size
                        && (fishType == 0 && type.isBasicFish() || fishType == 1 && type.isFrankenFish() || fishType == 2 && type.isMutantFish())
                        && withFins
                        && (step.getFins().size() > 0 || step.getEndoFins().size() > 0)
                        && sashimi == type.isSashimiFish()) {
                    this.cachedSteps.clear();
                    return step;
                }
            }
        }

        this.steps.clear();
        this.kraken = false;
        SolutionStep step = null;

        for (int cand = 1; cand <= 9; cand++) {
            step = this.getFishes(cand, size, size, withoutFins, withFins, sashimi, withEndoFins, fishType);
            if (!this.searchAll && !this.siamese && step != null) {
                return step;
            }
        }

        if ((this.searchAll || this.siamese) && this.steps.size() > 0) {
            this.findSiameseFish(this.steps);
            Collections.sort(this.steps);
            return this.steps.get(0);
        } else {
            return step;
        }
    }

    protected List<SolutionStep> getAllKrakenFishes(
            int minSize, int maxSize, int maxFins, int maxEndoFins, FindAllStepsProgressDialog dlg, int forCandidate, int type
    ) {
        this.tablingSolver = this.finder.getTablingSolver();
        synchronized (this.tablingSolver) {
            this.dlg = dlg;
            this.sudoku = this.finder.getSudoku();
            boolean oldCheckTemplates = Options.getInstance().isCheckTemplates();
            Options.getInstance().setCheckTemplates(false);
            int oldMaxFins = Options.getInstance().getMaxFins();
            int oldEndoFins = Options.getInstance().getMaxEndoFins();
            Options.getInstance().setMaxFins(maxFins);
            Options.getInstance().setMaxEndoFins(maxEndoFins);
            List<SolutionStep> oldSteps = this.steps;
            this.steps = new ArrayList<>();
            this.kraken = true;
            this.searchAll = true;
            this.tablingSolver.initForKrakenSearch();
            long millis1 = System.currentTimeMillis();

            for (int i = 1; i <= 9; i++) {
                if (forCandidate == -1 || forCandidate == i) {
                    long millis = System.currentTimeMillis();
                    this.baseGesamt = 0;
                    this.baseShowGesamt = 0;
                    this.getFishes(i, minSize, maxSize, true, true, false, true, type);
                    millis = System.currentTimeMillis() - millis;
                }
            }

            millis1 = System.currentTimeMillis() - millis1;
            List<SolutionStep> result = this.steps;
            if (result != null) {
                Collections.sort(result);
            }

            this.steps = oldSteps;
            Options.getInstance().setCheckTemplates(oldCheckTemplates);
            Options.getInstance().setMaxFins(oldMaxFins);
            Options.getInstance().setMaxEndoFins(oldEndoFins);
            this.kraken = false;
            this.dlg = null;
            return result;
        }
    }

    private SolutionStep getKrakenFish() {
        this.tablingSolver = this.finder.getTablingSolver();
        synchronized (this.tablingSolver) {
            this.baseGesamt = 0;
            this.baseShowGesamt = 0;
            this.steps = new ArrayList<>();
            boolean oldCheckTemplates = Options.getInstance().isCheckTemplates();
            Options.getInstance().setCheckTemplates(false);
            int oldMaxFins = Options.getInstance().getMaxFins();
            int oldEndoFins = Options.getInstance().getMaxEndoFins();
            Options.getInstance().setMaxFins(Options.getInstance().getMaxKrakenFins());
            Options.getInstance().setMaxEndoFins(Options.getInstance().getMaxKrakenEndoFins());
            this.kraken = true;
            this.tablingSolver.initForKrakenSearch();
            this.withEndoFins = Options.getInstance().getMaxKrakenEndoFins() != 0 && Options.getInstance().getKrakenMaxFishType() > 0;
            int size = Options.getInstance().getKrakenMaxFishSize();

            for (int i = 1; i <= 9; i++) {
                this.getFishes(i, 2, size, false, true, true, this.withEndoFins, Options.getInstance().getKrakenMaxFishType());
                if (this.steps.size() > 0) {
                    break;
                }
            }

            this.kraken = false;
            Options.getInstance().setCheckTemplates(oldCheckTemplates);
            Options.getInstance().setMaxFins(oldMaxFins);
            Options.getInstance().setMaxEndoFins(oldEndoFins);
            if (this.steps.size() > 0) {
                this.findSiameseFish(this.steps);
                Collections.sort(this.steps);
                return this.steps.get(0);
            } else {
                return null;
            }
        }
    }

    private SolutionStep getFishes(
            int candidate, int minSize, int maxSize, boolean withoutFins, boolean withFins, boolean sashimi, boolean withEndoFins, int fishType
    ) {
        this.deletesMap.clear();
        this.siamese = Options.getInstance().isAllowDualsAndSiamese();
        this.fishType = fishType;
        this.candidate = candidate;
        this.candidatesM1 = this.finder.getCandidates()[candidate].getMask1();
        this.candidatesM2 = this.finder.getCandidates()[candidate].getMask2();
        this.doTemplates = Options.getInstance().isCheckTemplates();
        if (fishType == 0 && maxSize <= 5 || fishType == 1 && maxSize <= 4 || fishType == 2 && maxSize <= 3) {
            this.doTemplates = false;
        }

        this.withoutFins = withoutFins;
        this.withFins = withFins;
        this.withEndoFins = withEndoFins;
        this.sashimi = sashimi;
        this.minSize = minSize;
        this.maxSize = maxSize;
        if (this.doTemplates) {
            this.delCandTemplatesM1 = this.finder.getDelCandTemplates(false)[candidate].getMask1();
            this.delCandTemplatesM2 = this.finder.getDelCandTemplates(false)[candidate].getMask2();
        }

        SolutionStep step = this.getFishes(true);
        if (fishType != 2 && (this.searchAll || this.siamese || step == null)) {
            SolutionStep step2 = this.getFishes(false);
            if (step2 == null) {
                step2 = step;
            }

            return step2;
        } else {
            return step;
        }
    }

    private SolutionStep getFishes(boolean lines) {
        if (this.doTemplates) {
            this.templateSet.set(this.finder.getDelCandTemplates(false)[this.candidate]);
            this.templateSet.and(this.finder.getCandidates()[this.candidate]);
            if (this.templateSet.isEmpty()) {
                return null;
            }
        }

        this.initForCandidat(this.maxSize, this.withFins, lines);
        Arrays.fill(this.baseUnitsUsed, false);
        this.baseLevel = 1;
        this.baseStack[0].candidatesM1 = 0L;
        this.baseStack[0].candidatesM2 = 0L;
        this.baseStack[0].endoFinsM1 = 0L;
        this.baseStack[0].endoFinsM2 = 0L;
        this.baseStack[1].aktIndex = 0;
        this.baseStack[1].lastUnit = -1;
        int aktBaseIndex = 0;
        FishSolver.BaseStackEntry bEntry = null;

        do {
            while (this.baseStack[this.baseLevel].aktIndex < this.numberOfBaseUnits) {
                bEntry = this.baseStack[this.baseLevel];
                aktBaseIndex = bEntry.aktIndex++;
                this.baseGesamt++;
                this.baseShowGesamt++;
                if (this.dlg != null && this.baseShowGesamt % 100 == 0) {
                    this.dlg.updateFishProgressBar(this.baseShowGesamt);
                }

                this.aktEndoFinSetM1 = this.baseStack[this.baseLevel - 1].candidatesM1 & this.baseCandidatesM1[aktBaseIndex];
                this.aktEndoFinSetM2 = this.baseStack[this.baseLevel - 1].candidatesM2 & this.baseCandidatesM2[aktBaseIndex];
                if (this.aktEndoFinSetM1 == 0L && this.aktEndoFinSetM2 == 0L
                        || this.withFins
                        && this.withEndoFins
                        && this.getSize(this.baseStack[this.baseLevel - 1].endoFinsM1, this.baseStack[this.baseLevel - 1].endoFinsM2)
                        + this.getSize(this.aktEndoFinSetM1, this.aktEndoFinSetM2)
                        <= Options.getInstance().getMaxEndoFins()) {
                    bEntry.candidatesM1 = this.baseStack[this.baseLevel - 1].candidatesM1 | this.baseCandidatesM1[aktBaseIndex];
                    bEntry.candidatesM2 = this.baseStack[this.baseLevel - 1].candidatesM2 | this.baseCandidatesM2[aktBaseIndex];
                    bEntry.endoFinsM1 = this.baseStack[this.baseLevel - 1].endoFinsM1 | this.aktEndoFinSetM1;
                    bEntry.endoFinsM2 = this.baseStack[this.baseLevel - 1].endoFinsM2 | this.aktEndoFinSetM2;
                    if (bEntry.lastUnit != -1) {
                        this.baseUnitsUsed[bEntry.lastUnit] = false;
                    }

                    bEntry.lastUnit = this.baseUnits[aktBaseIndex];
                    this.baseUnitsUsed[this.baseUnits[aktBaseIndex]] = true;
                    this.finBuddiesM1 = -1L;
                    this.finBuddiesM2 = 131071L;
                    if (this.doTemplates && (bEntry.endoFinsM1 != 0L || bEntry.endoFinsM2 != 0L)) {
                        Sudoku2.getBuddies(bEntry.endoFinsM1, bEntry.endoFinsM2, this.getBuddiesSet);
                        this.finBuddiesM1 = this.getBuddiesSet.getMask1() & ~this.candidatesM1 & this.delCandTemplatesM1;
                        this.finBuddiesM2 = this.getBuddiesSet.getMask2() & ~this.candidatesM2 & this.delCandTemplatesM2;
                    }

                    if (this.baseLevel >= this.minSize && this.baseLevel <= this.maxSize && (this.finBuddiesM1 != 0L || this.finBuddiesM2 != 0L)) {
                        SolutionStep step = this.searchCoverUnits(bEntry.candidatesM1, bEntry.candidatesM2, bEntry.endoFinsM1, bEntry.endoFinsM2);
                        if (!this.searchAll && !this.siamese && step != null) {
                            return step;
                        }
                    }

                    if (this.baseLevel < this.maxSize) {
                        this.baseLevel++;
                        bEntry = this.baseStack[this.baseLevel];
                        bEntry.aktIndex = aktBaseIndex + 1;
                        bEntry.lastUnit = -1;
                    }
                } else if (this.dlg != null) {
                    for (int j = 1; j <= this.maxSize - this.baseLevel; j++) {
                        this.baseShowGesamt = this.baseShowGesamt + SudokuUtil.combinations(this.numberOfBaseUnits - aktBaseIndex, j);
                    }
                }
            }

            if (this.baseStack[this.baseLevel].lastUnit != -1) {
                this.baseUnitsUsed[this.baseStack[this.baseLevel].lastUnit] = false;
                this.baseStack[this.baseLevel].lastUnit = -1;
            }

            this.baseLevel--;
        } while (this.baseLevel > 0);

        return this.steps.size() > 0 ? this.steps.get(0) : null;
    }

    private SolutionStep searchCoverUnits(long baseSetM1, long baseSetM2, long endoFinSetM1, long endoFinSetM2) {
        this.numberOfCoverUnits = 0;

        for (int i = 0; i < this.numberOfAllCoverUnits; i++) {
            if (!this.baseUnitsUsed[this.allCoverUnits[i]]
                    && ((baseSetM1 & this.allCoverCandidatesM1[i]) != 0L || (baseSetM2 & this.allCoverCandidatesM2[i]) != 0L)) {
                this.coverUnits[this.numberOfCoverUnits] = this.allCoverUnits[i];
                this.coverCandidatesM1[this.numberOfCoverUnits] = this.allCoverCandidatesM1[i];
                this.coverCandidatesM2[this.numberOfCoverUnits++] = this.allCoverCandidatesM2[i];
            }
        }

        Arrays.fill(this.coverUnitsUsed, false);
        this.coverLevel = 1;
        this.coverStack[0].candidatesM1 = 0L;
        this.coverStack[0].candidatesM2 = 0L;
        this.coverStack[0].cannibalisticM1 = 0L;
        this.coverStack[0].cannibalisticM2 = 0L;
        this.coverStack[1].aktIndex = 0;
        this.coverStack[1].lastUnit = -1;
        int aktCoverIndex = 0;
        FishSolver.CoverStackEntry cEntry = null;

        do {
            while (this.coverStack[this.coverLevel].aktIndex < this.numberOfCoverUnits - this.baseLevel + this.coverLevel) {
                cEntry = this.coverStack[this.coverLevel];
                aktCoverIndex = cEntry.aktIndex++;
                this.aktCannibalismSetM1 = this.coverStack[this.coverLevel - 1].candidatesM1 & this.coverCandidatesM1[aktCoverIndex];
                this.aktCannibalismSetM2 = this.coverStack[this.coverLevel - 1].candidatesM2 & this.coverCandidatesM2[aktCoverIndex];
                cEntry.candidatesM1 = this.coverStack[this.coverLevel - 1].candidatesM1 | this.coverCandidatesM1[aktCoverIndex];
                cEntry.candidatesM2 = this.coverStack[this.coverLevel - 1].candidatesM2 | this.coverCandidatesM2[aktCoverIndex];
                cEntry.cannibalisticM1 = this.coverStack[this.coverLevel - 1].cannibalisticM1 | this.aktCannibalismSetM1;
                cEntry.cannibalisticM2 = this.coverStack[this.coverLevel - 1].cannibalisticM2 | this.aktCannibalismSetM2;
                if (cEntry.lastUnit != -1) {
                    this.coverUnitsUsed[cEntry.lastUnit] = false;
                }

                cEntry.lastUnit = this.coverUnits[aktCoverIndex];
                this.coverUnitsUsed[this.coverUnits[aktCoverIndex]] = true;
                this.coverGesamt++;
                if (this.coverLevel == this.baseLevel) {
                    this.versucheFisch++;
                    this.finsM1 = this.finsM2 = 0L;
                    long m1 = ~cEntry.candidatesM1 & baseSetM1;
                    long m2 = ~cEntry.candidatesM2 & baseSetM2;
                    boolean isCovered = true;
                    if (m1 != 0L) {
                        isCovered = false;
                        this.finsM1 = m1;
                    }

                    if (m2 != 0L) {
                        isCovered = false;
                        this.finsM2 = m2;
                    }

                    this.finsM1 |= endoFinSetM1;
                    this.finsM2 |= endoFinSetM2;
                    int finSize = 0;
                    if (isCovered && this.withoutFins && this.finsM1 == 0L && this.finsM2 == 0L) {
                        this.anzFins[0]++;
                        this.tmpSetM1 = cEntry.candidatesM1 & ~baseSetM1;
                        this.tmpSetM2 = cEntry.candidatesM2 & ~baseSetM2;
                        if (this.tmpSetM1 != 0L || this.tmpSetM2 != 0L || cEntry.cannibalisticM1 != 0L || cEntry.cannibalisticM2 != 0L) {
                            SolutionStep step = this.createFishStep(
                                    this.coverLevel,
                                    false,
                                    this.finsM1,
                                    this.finsM2,
                                    this.tmpSetM1,
                                    this.tmpSetM2,
                                    cEntry.cannibalisticM1,
                                    cEntry.cannibalisticM2,
                                    endoFinSetM1,
                                    endoFinSetM2,
                                    this.tmpSetM1,
                                    this.tmpSetM2,
                                    false
                            );
                            if (!this.searchAll && !this.siamese && step != null) {
                                return step;
                            }
                        }
                    } else if (this.withFins && (finSize = this.getSize(this.finsM1, this.finsM2)) > 0 && finSize <= Options.getInstance().getMaxFins()) {
                        this.versucheFins++;
                        this.anzFins[finSize]++;
                        Sudoku2.getBuddies(this.finsM1, this.finsM2, this.getBuddiesSet);
                        this.finBuddiesM1 = this.getBuddiesSet.getMask1();
                        this.finBuddiesM2 = this.getBuddiesSet.getMask2();
                        if (this.finBuddiesM1 != 0L || this.finBuddiesM2 != 0L) {
                            this.tmpSetM1 = cEntry.candidatesM1 & ~baseSetM1;
                            this.tmpSetM2 = cEntry.candidatesM2 & ~baseSetM2;
                            this.tmpSet2M1 = this.tmpSetM1;
                            this.tmpSet2M2 = this.tmpSetM2;
                            this.tmpSetM1 = this.tmpSetM1 & this.finBuddiesM1;
                            this.tmpSetM2 = this.tmpSetM2 & this.finBuddiesM2;
                            this.tmpSet1M1 = cEntry.cannibalisticM1 & this.finBuddiesM1;
                            this.tmpSet1M2 = cEntry.cannibalisticM2 & this.finBuddiesM2;
                            if (!this.kraken && (this.tmpSetM1 != 0L || this.tmpSetM2 != 0L || this.tmpSet1M1 != 0L || this.tmpSet1M2 != 0L)) {
                                SolutionStep step = this.createFishStep(
                                        this.coverLevel,
                                        true,
                                        this.finsM1,
                                        this.finsM2,
                                        this.tmpSetM1,
                                        this.tmpSetM2,
                                        this.tmpSet1M1,
                                        this.tmpSet1M2,
                                        endoFinSetM1,
                                        endoFinSetM2,
                                        this.tmpSet2M1,
                                        this.tmpSet2M2,
                                        false
                                );
                                if (step != null && !this.searchAll && !this.siamese) {
                                    return step;
                                }
                            } else if (this.kraken && this.tmpSetM1 == 0L && this.tmpSetM2 == 0L && this.tmpSet1M1 == 0L && this.tmpSet1M2 == 0L) {
                                this.tmpSet2M1 = this.tmpSet2M1 | cEntry.cannibalisticM1;
                                this.tmpSet2M2 = this.tmpSet2M2 | cEntry.cannibalisticM2;
                                SolutionStep step = this.searchForKraken(
                                        this.tmpSet2M1,
                                        this.tmpSet2M2,
                                        baseSetM1,
                                        baseSetM2,
                                        this.finsM1,
                                        this.finsM2,
                                        cEntry.cannibalisticM1,
                                        cEntry.cannibalisticM2,
                                        endoFinSetM1,
                                        endoFinSetM2
                                );
                                if (step != null && !this.searchAll) {
                                    return step;
                                }
                            }
                        }
                    }
                }

                if (this.coverLevel < this.maxSize) {
                    this.coverLevel++;
                    cEntry = this.coverStack[this.coverLevel];
                    cEntry.aktIndex = aktCoverIndex + 1;
                    cEntry.lastUnit = -1;
                }
            }

            if (this.coverStack[this.coverLevel].lastUnit != -1) {
                this.coverUnitsUsed[this.coverStack[this.coverLevel].lastUnit] = false;
                this.coverStack[this.coverLevel].lastUnit = -1;
            }

            this.coverLevel--;
        } while (this.coverLevel > 0);

        return this.steps.size() > 0 ? this.steps.get(0) : null;
    }

    private SolutionStep searchForKraken(
            long deleteSetM1,
            long deleteSetM2,
            long baseSetM1,
            long baseSetM2,
            long finsM1,
            long finsM2,
            long cannibalisticM1,
            long cannibalisticM2,
            long endoFinsM1,
            long endoFinsM2
    ) {
        if (deleteSetM1 != 0L || deleteSetM2 != 0L) {
            this.krakenDeleteCandSet.set(deleteSetM1, deleteSetM2);
            this.krakenFinSet.set(finsM1, finsM2);

            for (int j = 0; j < this.krakenDeleteCandSet.size(); j++) {
                int endIndex = this.krakenDeleteCandSet.get(j);
                if (this.tablingSolver.checkKrakenTypeOne(this.krakenFinSet, endIndex, this.candidate)) {
                    this.krakenCannibalisticSet.set(cannibalisticM1, cannibalisticM2);
                    if (this.krakenCannibalisticSet.contains(endIndex)) {
                        this.krakenCannibalisticSet.clear();
                        this.krakenCannibalisticSet.add(endIndex);
                    } else {
                        this.krakenCannibalisticSet.clear();
                    }

                    SolutionStep step = this.createFishStep(
                            this.coverLevel,
                            true,
                            finsM1,
                            finsM2,
                            0L,
                            0L,
                            this.krakenCannibalisticSet.getMask1(),
                            this.krakenCannibalisticSet.getMask2(),
                            endoFinsM1,
                            endoFinsM2,
                            deleteSetM1,
                            deleteSetM2,
                            true
                    );
                    step.setSubType(step.getType());
                    step.setType(SolutionType.KRAKEN_FISH_TYPE_1);
                    step.addCandidateToDelete(endIndex, this.candidate);

                    for (int k = 0; k < this.krakenFinSet.size(); k++) {
                        Chain tmpChain = this.tablingSolver.getKrakenChain(this.krakenFinSet.get(k), this.candidate, endIndex, this.candidate);
                        step.addChain((Chain) tmpChain.clone());
                    }

                    this.tablingSolver.adjustChains(step);
                    step = this.addKrakenStep(step);
                    if (step != null && !this.searchAll) {
                        return step;
                    }
                }
            }
        }

        this.krakenCannibalisticSet.clear();

        for (int coverIndex = 0; coverIndex < this.numberOfCoverUnits; coverIndex++) {
            if (this.coverUnitsUsed[this.coverUnits[coverIndex]]) {
                this.tmpSetM1 = this.coverCandidatesM1[coverIndex] & baseSetM1 & ~cannibalisticM1;
                this.tmpSetM2 = this.coverCandidatesM2[coverIndex] & baseSetM2 & ~cannibalisticM2;
                if (this.coverCandidatesM1[coverIndex] != this.tmpSetM1 || this.coverCandidatesM2[coverIndex] != this.tmpSetM2) {
                    this.tmpSetM1 |= finsM1;
                    this.tmpSetM2 |= finsM2;
                    this.krakenDeleteCandSet.set(this.tmpSetM1, this.tmpSetM2);
                    this.krakenFinSet.clear();

                    for (int endCandidate = 1; endCandidate <= 9; endCandidate++) {
                        if (this.tablingSolver.checkKrakenTypeTwo(this.krakenDeleteCandSet, this.krakenFinSet, this.candidate, endCandidate)) {
                            for (int j = 0; j < this.krakenFinSet.size(); j++) {
                                int endIndex = this.krakenFinSet.get(j);
                                SolutionStep step = this.createFishStep(
                                        this.coverLevel, true, finsM1, finsM2, 0L, 0L, 0L, 0L, endoFinsM1, endoFinsM2, deleteSetM1, deleteSetM2, true
                                );
                                step.setSubType(step.getType());
                                step.setType(SolutionType.KRAKEN_FISH_TYPE_2);
                                step.addCandidateToDelete(endIndex, endCandidate);

                                for (int k = 0; k < this.krakenDeleteCandSet.size(); k++) {
                                    Chain tmpChain = this.tablingSolver.getKrakenChain(this.krakenDeleteCandSet.get(k), this.candidate, endIndex, endCandidate);
                                    step.addChain((Chain) tmpChain.clone());
                                }

                                this.tablingSolver.adjustChains(step);
                                step = this.addKrakenStep(step);
                                if (step != null && !this.searchAll) {
                                    return step;
                                }
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    private SolutionStep addFishStep() {
        if (!this.searchAll && !this.siamese) {
            return (SolutionStep) this.globalStep.clone();
        }

        if (this.fishType != -1 && !this.searchAll) {
            SolutionType type = this.globalStep.getType();
            if (this.fishType == 0 && !type.isBasicFish()) {
                return null;
            }

            if (this.fishType == 1 && !type.isFrankenFish()) {
                return null;
            }

            if (this.fishType == 2 && !type.isMutantFish()) {
                return null;
            }
        }

        if (Options.getInstance().isOnlyOneFishPerStep()) {
            String delOrg = this.globalStep.getCandidateString();
            int startIndex = delOrg.indexOf(41);
            startIndex = delOrg.indexOf(40, startIndex);
            String del = delOrg.substring(0, startIndex);
            Integer oldIndex = this.deletesMap.get(del);
            SolutionStep tmpStep = null;
            if (oldIndex != null) {
                tmpStep = this.steps.get(oldIndex);
            }

            if (tmpStep == null || this.globalStep.getType().compare(tmpStep.getType()) < 0) {
                if (oldIndex != null) {
                    this.steps.remove(oldIndex.intValue());
                    this.steps.add(oldIndex, (SolutionStep) this.globalStep.clone());
                } else {
                    this.steps.add((SolutionStep) this.globalStep.clone());
                    this.deletesMap.put(del, this.steps.size() - 1);
                }
            }
        } else {
            this.steps.add((SolutionStep) this.globalStep.clone());
        }

        return null;
    }

    private SolutionStep addKrakenStep(SolutionStep step) {
        String del = step.getCandidateString() + " " + step.getValues().get(0);
        Integer oldIndex = this.deletesMap.get(del);
        SolutionStep tmpStep = null;
        if (oldIndex != null) {
            tmpStep = this.steps.get(oldIndex);
        }

        if (tmpStep != null
                && step.getSubType().compare(tmpStep.getSubType()) >= 0
                && (step.getSubType().compare(tmpStep.getSubType()) != 0 || step.getChainLength() >= tmpStep.getChainLength())) {
            return null;
        }

        this.steps.add(step);
        this.deletesMap.put(del, this.steps.size() - 1);
        return step;
    }

    private void findSiameseFish(List<SolutionStep> fishes) {
        if (Options.getInstance().isAllowDualsAndSiamese()) {
            int maxIndex = fishes.size();

            for (int i = 0; i < maxIndex - 1; i++) {
                for (int j = i + 1; j < maxIndex; j++) {
                    SolutionStep step1 = fishes.get(i);
                    SolutionStep step2 = fishes.get(j);
                    if (step1.getValues().get(0) == step2.getValues().get(0)
                            && step1.getBaseEntities().size() == step2.getBaseEntities().size()
                            && SolutionType.getStepConfig(step1.getType()).getCategory().ordinal() == SolutionType.getStepConfig(step2.getType()).getCategory().ordinal()
                    ) {
                        boolean baseSetEqual = true;

                        for (int k = 0; k < step1.getBaseEntities().size(); k++) {
                            if (!step1.getBaseEntities().get(k).equals(step2.getBaseEntities().get(k))) {
                                baseSetEqual = false;
                                break;
                            }
                        }

                        if (baseSetEqual && !step1.getCandidatesToDelete().get(0).equals(step2.getCandidatesToDelete().get(0))) {
                            SolutionStep siameseStep = (SolutionStep) step1.clone();
                            siameseStep.setIsSiamese(true);

                            for (int k = 0; k < step2.getCoverEntities().size(); k++) {
                                siameseStep.addCoverEntity(step2.getCoverEntities().get(k));
                            }

                            for (int k = 0; k < step2.getFins().size(); k++) {
                                siameseStep.addFin(step2.getFins().get(k));
                            }

                            for (int k = 0; k < step2.getCandidatesToDelete().size(); k++) {
                                siameseStep.addCandidateToDelete(step2.getCandidatesToDelete().get(k));
                            }

                            siameseStep.getPotentialEliminations().or(step2.getPotentialEliminations());
                            siameseStep.getPotentialCannibalisticEliminations().or(step2.getPotentialCannibalisticEliminations());
                            fishes.add(siameseStep);
                        }
                    }
                }
            }
        }
    }

    private SolutionStep createFishStep(
            int size,
            boolean withFins,
            long finSetM1,
            long finSetM2,
            long deleteSetM1,
            long deleteSetM2,
            long cannibalisticSetM1,
            long cannibalisticSetM2,
            long endoFinSetM1,
            long endoFinSetM2,
            long potentialEliminationsM1,
            long potentialEliminationsM2,
            boolean kraken
    ) {
        this.globalStep.reset();
        SolutionType type = SolutionType.X_WING;
        int baseMask = this.getUnitMask(this.baseUnitsUsed);
        int coverMask = this.getUnitMask(this.coverUnitsUsed);
        boolean isSashimi = false;
        if (baseMask == 1 && coverMask == 2 || baseMask == 2 && coverMask == 1) {
            for (int i = 0; i < this.numberOfBaseUnits; i++) {
                if (this.baseUnitsUsed[this.baseUnits[i]]) {
                    this.checkSashimiSetM1 = this.baseCandidatesM1[i] & ~finSetM1;
                    this.checkSashimiSetM2 = this.baseCandidatesM2[i] & ~finSetM2;
                    if (this.getSizeLTE1(this.checkSashimiSetM1, this.checkSashimiSetM2)) {
                        isSashimi = true;
                        break;
                    }
                }
            }
        }

        if ((baseMask != 1 || coverMask != 2) && (baseMask != 2 || coverMask != 1)) {
            if ((baseMask == 1 || baseMask == 5) && (coverMask == 2 || coverMask == 6) || (baseMask == 2 || baseMask == 6) && (coverMask == 1 || coverMask == 5)) {
                if (withFins) {
                    type = FINNED_FRANKEN_TYPES[size - 2];
                } else {
                    type = FRANKEN_TYPES[size - 2];
                }
            } else if (withFins) {
                type = FINNED_MUTANT_TYPES[size - 2];
            } else {
                type = MUTANT_TYPES[size - 2];
            }
        } else if (isSashimi) {
            type = SASHIMI_BASIC_TYPES[size - 2];
        } else if (withFins) {
            type = FINNED_BASIC_TYPES[size - 2];
        } else {
            type = BASIC_TYPES[size - 2];
        }

        this.globalStep.setType(type);
        this.globalStep.addValue(this.candidate);
        long bm1 = this.baseStack[this.baseLevel].candidatesM1 & ~finSetM1;
        long bm2 = this.baseStack[this.baseLevel].candidatesM2 & ~finSetM2;
        this.createFishSet.set(bm1, bm2);

        for (int i = 0; i < this.createFishSet.size(); i++) {
            this.globalStep.addIndex(this.createFishSet.get(i));
        }

        for (int i = 0; i < this.baseUnitsUsed.length; i++) {
            if (this.baseUnitsUsed[i]) {
                this.globalStep.addBaseEntity(Sudoku2.CONSTRAINT_TYPE_FROM_CONSTRAINT[i], Sudoku2.CONSTRAINT_NUMBER_FROM_CONSTRAINT[i]);
            }
        }

        for (int i = 0; i < this.coverUnitsUsed.length; i++) {
            if (this.coverUnitsUsed[i]) {
                this.globalStep.addCoverEntity(Sudoku2.CONSTRAINT_TYPE_FROM_CONSTRAINT[i], Sudoku2.CONSTRAINT_NUMBER_FROM_CONSTRAINT[i]);
            }
        }

        this.createFishSet.set(deleteSetM1, deleteSetM2);

        for (int k = 0; k < this.createFishSet.size(); k++) {
            this.globalStep.addCandidateToDelete(this.createFishSet.get(k), this.candidate);
        }

        this.createFishSet.set(cannibalisticSetM1, cannibalisticSetM2);

        for (int k = 0; k < this.createFishSet.size(); k++) {
            this.globalStep.addCannibalistic(this.createFishSet.get(k), this.candidate);
            this.globalStep.addCandidateToDelete(this.createFishSet.get(k), this.candidate);
        }

        bm1 = finSetM1 & ~endoFinSetM1;
        bm2 = finSetM2 & ~endoFinSetM2;
        this.createFishSet.set(bm1, bm2);

        for (int i = 0; i < this.createFishSet.size(); i++) {
            this.globalStep.addFin(this.createFishSet.get(i), this.candidate);
        }

        this.createFishSet.set(endoFinSetM1, endoFinSetM2);

        for (int i = 0; i < this.createFishSet.size(); i++) {
            this.globalStep.addEndoFin(this.createFishSet.get(i), this.candidate);
        }

        this.createFishSet.set(potentialEliminationsM1, potentialEliminationsM2);
        this.globalStep.getPotentialEliminations().set(this.createFishSet);
        this.createFishSet.set(cannibalisticSetM1, cannibalisticSetM2);
        this.globalStep.getPotentialCannibalisticEliminations().set(this.createFishSet);
        if (!this.searchAll && this.fishType == 0 && withFins && this.sashimi != isSashimi) {
            this.cachedSteps.add((SolutionStep) this.globalStep.clone());
            return null;
        } else {
            return kraken ? (SolutionStep) this.globalStep.clone() : this.addFishStep();
        }
    }

    private int getUnitMask(boolean[] used) {
        int mask = 0;

        for (int i = 0; i < used.length; i++) {
            if (used[i]) {
                mask |= MASKS[Sudoku2.CONSTRAINT_TYPE_FROM_CONSTRAINT[i]];
            }
        }

        return mask;
    }

    private void initForCandidat(int size, boolean withFins, boolean lines) {
        this.numberOfBaseUnits = this.numberOfAllCoverUnits = 0;

        for (int i = 0; i < Sudoku2.ALL_CONSTRAINTS_TEMPLATES.length; i++) {
            if (i < 18 || this.fishType != 0) {
                this.tmpSetM1 = Sudoku2.ALL_CONSTRAINTS_TEMPLATES_M1[i] & this.candidatesM1;
                this.tmpSetM2 = Sudoku2.ALL_CONSTRAINTS_TEMPLATES_M2[i] & this.candidatesM2;
                if (this.tmpSetM1 != 0L || this.tmpSetM2 != 0L) {
                    if (i < 9) {
                        if (lines || this.fishType == 2) {
                            this.addUnit(i, this.tmpSetM1, this.tmpSetM2, true, size, withFins);
                            if (this.fishType == 2) {
                                this.addUnit(i, this.tmpSetM1, this.tmpSetM2, false, size, withFins);
                            }
                        } else if (!lines || this.fishType == 2) {
                            this.addUnit(i, this.tmpSetM1, this.tmpSetM2, false, size, withFins);
                            if (this.fishType == 2) {
                                this.addUnit(i, this.tmpSetM1, this.tmpSetM2, true, size, withFins);
                            }
                        }
                    } else if (i < 18) {
                        if (lines || this.fishType == 2) {
                            this.addUnit(i, this.tmpSetM1, this.tmpSetM2, false, size, withFins);
                            if (this.fishType == 2) {
                                this.addUnit(i, this.tmpSetM1, this.tmpSetM2, true, size, withFins);
                            }
                        } else if (!lines || this.fishType == 2) {
                            this.addUnit(i, this.tmpSetM1, this.tmpSetM2, true, size, withFins);
                            if (this.fishType == 2) {
                                this.addUnit(i, this.tmpSetM1, this.tmpSetM2, false, size, withFins);
                            }
                        }
                    } else if (this.fishType != 0) {
                        this.addUnit(i, this.tmpSetM1, this.tmpSetM2, false, size, withFins);
                        this.addUnit(i, this.tmpSetM1, this.tmpSetM2, true, size, withFins);
                    }
                }
            }
        }

        this.maxBaseCombinations = 0;
        if (this.dlg != null) {
            for (int i = 1; i <= this.maxSize; i++) {
                this.maxBaseCombinations = this.maxBaseCombinations + SudokuUtil.combinations(this.numberOfBaseUnits, i);
            }

            this.dlg.resetFishProgressBar(this.maxBaseCombinations);
        }
    }

    private void addUnit(int unit, long setM1, long setM2, boolean base, int size, boolean withFins) {
        if (base) {
            if (withFins || this.getSize(setM1, setM2) <= size) {
                this.baseUnits[this.numberOfBaseUnits] = unit;
                this.baseCandidatesM1[this.numberOfBaseUnits] = setM1;
                this.baseCandidatesM2[this.numberOfBaseUnits++] = setM2;
            }
        } else {
            this.allCoverUnits[this.numberOfAllCoverUnits] = unit;
            this.allCoverCandidatesM1[this.numberOfAllCoverUnits] = setM1;
            this.allCoverCandidatesM2[this.numberOfAllCoverUnits++] = setM2;
        }
    }

    private int getSize(long mask1, long mask2) {
        int anzahl = 0;
        if (mask1 != 0L) {
            for (int i = 0; i < 64; i += 8) {
                anzahl += SudokuSet.anzValues[(int) (mask1 >> i & 255L)];
            }
        }

        if (mask2 != 0L) {
            for (int i = 0; i < 24; i += 8) {
                anzahl += SudokuSet.anzValues[(int) (mask2 >> i & 255L)];
            }
        }

        return anzahl;
    }

    private boolean getSizeLTE1(long mask1, long mask2) {
        int anzahl = 0;
        if (mask1 != 0L) {
            for (int i = 0; i < 64; i += 8) {
                anzahl += SudokuSet.anzValues[(int) (mask1 >> i & 255L)];
                if (anzahl > 1) {
                    return false;
                }
            }
        }

        if (mask2 != 0L) {
            for (int i = 0; i < 24; i += 8) {
                anzahl += SudokuSet.anzValues[(int) (mask2 >> i & 255L)];
                if (anzahl > 1) {
                    return false;
                }
            }
        }

        return true;
    }

    private void printSet(String text, long m1, long m2) {
        SudokuSetBase set = new SudokuSetBase();
        set.setMask1(m1);
        set.setMask2(m2);
        set.setInitialized(false);
        System.out.println(text + ": " + set);
    }

    protected void printStatistics() {
        System.out
                .println(
                        "baseAnz: "
                                + this.baseGesamt
                                + "("
                                + this.baseShowGesamt
                                + "), coverAnz: "
                                + this.coverGesamt
                                + ", Fische: "
                                + this.versucheFisch
                                + ", Fins: "
                                + this.versucheFins
                );
        StringBuffer tmpBuffer = new StringBuffer();

        for (int i = 0; i < this.anzFins.length; i++) {
            tmpBuffer.append(" ").append(this.anzFins[i]);
        }

        System.out.println(tmpBuffer);
    }

    private class BaseStackEntry {
        int aktIndex = 0;
        int lastUnit = 0;
        long candidatesM1 = 0L;
        long candidatesM2 = 0L;
        long endoFinsM1 = 0L;
        long endoFinsM2 = 0L;

        private BaseStackEntry() {
        }
    }

    private class CoverStackEntry {
        int aktIndex = 0;
        int lastUnit = 0;
        long candidatesM1 = 0L;
        long candidatesM2 = 0L;
        long cannibalisticM1 = 0L;
        long cannibalisticM2 = 0L;

        private CoverStackEntry() {
        }
    }
}
