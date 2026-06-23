package solver;

import sudoku.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class SudokuStepFinder {
    private SimpleSolver simpleSolver;
    private FishSolver fishSolver;
    private SingleDigitPatternSolver singleDigitPatternSolver;
    private UniquenessSolver uniquenessSolver;
    private WingSolver wingSolver;
    private ColoringSolver coloringSolver;
    private ChainSolver chainSolver;
    private AlsSolver alsSolver;
    private MiscellaneousSolver miscellaneousSolver;
    private TablingSolver tablingSolver;
    private TemplateSolver templateSolver;
    private BruteForceSolver bruteForceSolver;
    private IncompleteSolver incompleteSolver;
    private GiveUpSolver giveUpSolver;
    private AbstractSolver[] solvers;
    private Sudoku2 sudoku;
    private StepConfig[] stepConfigs;
    private int stepNumber = 0;
    private long templateNanos;
    private int templateAnz;
    private boolean initialized = false;
    private boolean simpleOnly = false;
    private SudokuSet[] candidates = new SudokuSet[10];
    private boolean candidatesDirty = true;
    private SudokuSet[] positions = new SudokuSet[10];
    private boolean positionsDirty = true;
    private SudokuSet[] candidatesAllowed = new SudokuSet[10];
    private boolean candidatesAllowedDirty = true;
    private SudokuSet emptyCells = new SudokuSet();
    private SudokuSet[] setValueTemplates = new SudokuSet[10];
    private SudokuSet[] delCandTemplates = new SudokuSet[10];
    private List<List<SudokuSetBase>> candTemplates;
    private boolean templatesDirty = true;
    private boolean templatesListDirty = true;
    private List<Als> alsesOnlyLargerThanOne = null;
    private int alsesOnlyLargerThanOneStepNumber = -1;
    private List<Als> alsesWithOne = null;
    private int alsesWithOneStepNumber = -1;
    private List<RestrictedCommon> restrictedCommons = null;
    private int[] startIndices = null;
    private int[] endIndices = null;
    private boolean lastRcAllowOverlap;
    private int lastRcStepNumber = -1;
    private List<Als> lastRcAlsList = null;
    private boolean lastRcOnlyForward = true;
    private boolean rcOnlyForward = true;
    private SudokuSet indexSet = new SudokuSet();
    private short[] candSets = new short[10];
    private long alsNanos;
    private int anzAlsCalls;
    private int anzAls;
    private int doubleAls;
    private short possibleRestrictedCommonsSet = 0;
    private SudokuSet restrictedCommonBuddiesSet = new SudokuSet();
    private SudokuSet restrictedCommonIndexSet = new SudokuSet();
    private SudokuSet intersectionSet = new SudokuSet();
    private long rcNanos;
    private int rcAnzCalls;
    private int anzRcs;

    public SudokuStepFinder() {
        this(false);
    }

    public SudokuStepFinder(boolean simpleOnly) {
        this.simpleOnly = simpleOnly;
        this.initialized = false;
    }

    private void initialize() {
        if (!this.initialized) {
            for (int i = 0; i < this.candidates.length; i++) {
                this.candidates[i] = new SudokuSet();
                this.positions[i] = new SudokuSet();
                this.candidatesAllowed[i] = new SudokuSet();
            }

            this.candTemplates = new ArrayList<>(10);

            for (int i = 0; i < this.setValueTemplates.length; i++) {
                this.setValueTemplates[i] = new SudokuSet();
                this.delCandTemplates[i] = new SudokuSet();
                this.candTemplates.add(i, new LinkedList<>());
            }

            this.simpleSolver = new SimpleSolver(this);
            if (!this.simpleOnly) {
                this.fishSolver = new FishSolver(this);
                this.singleDigitPatternSolver = new SingleDigitPatternSolver(this);
                this.uniquenessSolver = new UniquenessSolver(this);
                this.wingSolver = new WingSolver(this);
                this.coloringSolver = new ColoringSolver(this);
                this.chainSolver = new ChainSolver(this);
                this.alsSolver = new AlsSolver(this);
                this.miscellaneousSolver = new MiscellaneousSolver(this);
                this.tablingSolver = new TablingSolver(this);
                this.templateSolver = new TemplateSolver(this);
                this.bruteForceSolver = new BruteForceSolver(this);
                this.incompleteSolver = new IncompleteSolver(this);
                this.giveUpSolver = new GiveUpSolver(this);
                this.solvers = new AbstractSolver[]{
                        this.simpleSolver,
                        this.fishSolver,
                        this.singleDigitPatternSolver,
                        this.uniquenessSolver,
                        this.wingSolver,
                        this.coloringSolver,
                        this.chainSolver,
                        this.alsSolver,
                        this.miscellaneousSolver,
                        this.tablingSolver,
                        this.templateSolver,
                        this.bruteForceSolver,
                        this.incompleteSolver,
                        this.giveUpSolver
                };
            } else {
                this.solvers = new AbstractSolver[]{this.simpleSolver};
            }

            this.initialized = true;
        }
    }

    public void cleanUp() {
        if (this.solvers != null) {
            for (AbstractSolver solver : this.solvers) {
                solver.cleanUp();
            }
        }
    }

    public SolutionStep getStep(SolutionType type) {
        this.initialize();
        SolutionStep result = null;

        for (int i = 0; i < this.solvers.length; i++) {
            if ((result = this.solvers[i].getStep(type)) != null) {
                this.stepNumber++;
                return result;
            }
        }

        return result;
    }

    public void doStep(SolutionStep step) {
        this.initialize();

        for (int i = 0; i < this.solvers.length; i++) {
            if (this.solvers[i].doStep(step)) {
                this.setSudokuDirty();
                return;
            }
        }

        throw new RuntimeException("Invalid solution step in doStep() (" + step.getType() + ")");
    }

    public void setSudokuDirty() {
        this.candidatesDirty = true;
        this.candidatesAllowedDirty = true;
        this.positionsDirty = true;
        this.templatesDirty = true;
        this.templatesListDirty = true;
        this.stepNumber++;
    }

    public Sudoku2 getSudoku() {
        return this.sudoku;
    }

    public void setSudoku(Sudoku2 sudoku) {
        if (sudoku != null && this.sudoku != sudoku) {
            this.sudoku = sudoku;
        }

        this.setSudokuDirty();
    }

    public void setStepConfigs(StepConfig[] stepConfigs) {
        this.stepConfigs = stepConfigs;
    }

    protected TablingSolver getTablingSolver() {
        return this.tablingSolver;
    }

    public List<SolutionStep> findAllFullHouses(Sudoku2 newSudoku) {
        this.initialize();
        Sudoku2 oldSudoku = this.getSudoku();
        this.setSudoku(newSudoku);
        List<SolutionStep> steps = this.simpleSolver.findAllFullHouses();
        this.setSudoku(oldSudoku);
        return steps;
    }

    public List<SolutionStep> findAllNakedSingles(Sudoku2 newSudoku) {
        this.initialize();
        Sudoku2 oldSudoku = this.getSudoku();
        this.setSudoku(newSudoku);
        List<SolutionStep> steps = this.simpleSolver.findAllNakedSingles();
        this.setSudoku(oldSudoku);
        return steps;
    }

    public List<SolutionStep> findAllNakedXle(Sudoku2 newSudoku) {
        this.initialize();
        Sudoku2 oldSudoku = this.getSudoku();
        this.setSudoku(newSudoku);
        List<SolutionStep> steps = this.simpleSolver.findAllNakedXle();
        this.setSudoku(oldSudoku);
        return steps;
    }

    public List<SolutionStep> findAllHiddenSingles(Sudoku2 newSudoku) {
        this.initialize();
        Sudoku2 oldSudoku = this.getSudoku();
        this.setSudoku(newSudoku);
        List<SolutionStep> steps = this.simpleSolver.findAllHiddenSingles();
        this.setSudoku(oldSudoku);
        return steps;
    }

    public List<SolutionStep> findAllHiddenXle(Sudoku2 newSudoku) {
        this.initialize();
        Sudoku2 oldSudoku = this.getSudoku();
        this.setSudoku(newSudoku);
        List<SolutionStep> steps = this.simpleSolver.findAllHiddenXle();
        this.setSudoku(oldSudoku);
        return steps;
    }

    public List<SolutionStep> findAllLockedCandidates(Sudoku2 newSudoku) {
        this.initialize();
        Sudoku2 oldSudoku = this.getSudoku();
        this.setSudoku(newSudoku);
        List<SolutionStep> steps = this.simpleSolver.findAllLockedCandidates();
        this.setSudoku(oldSudoku);
        return steps;
    }

    public List<SolutionStep> findAllLockedCandidates1(Sudoku2 newSudoku) {
        this.initialize();
        Sudoku2 oldSudoku = this.getSudoku();
        this.setSudoku(newSudoku);
        List<SolutionStep> steps = this.simpleSolver.findAllLockedCandidates();
        this.setSudoku(oldSudoku);
        List<SolutionStep> resultList = new ArrayList<>();

        for (SolutionStep step : steps) {
            if (step.getType().equals(SolutionType.LOCKED_CANDIDATES_1)) {
                resultList.add(step);
            }
        }

        return resultList;
    }

    public List<SolutionStep> findAllLockedCandidates2(Sudoku2 newSudoku) {
        this.initialize();
        Sudoku2 oldSudoku = this.getSudoku();
        this.setSudoku(newSudoku);
        List<SolutionStep> steps = this.simpleSolver.findAllLockedCandidates();
        this.setSudoku(oldSudoku);
        List<SolutionStep> resultList = new ArrayList<>();

        for (SolutionStep step : steps) {
            if (step.getType().equals(SolutionType.LOCKED_CANDIDATES_2)) {
                resultList.add(step);
            }
        }

        return resultList;
    }

    public List<SolutionStep> getAllFishes(
            Sudoku2 newSudoku, int minSize, int maxSize, int maxFins, int maxEndoFins, FindAllStepsProgressDialog dlg, int forCandidate, int type
    ) {
        this.initialize();
        Sudoku2 oldSudoku = this.getSudoku();
        this.setSudoku(newSudoku);
        List<SolutionStep> steps = this.fishSolver.getAllFishes(minSize, maxSize, maxFins, maxEndoFins, dlg, forCandidate, type);
        this.setSudoku(oldSudoku);
        return steps;
    }

    public List<SolutionStep> getAllKrakenFishes(
            Sudoku2 newSudoku, int minSize, int maxSize, int maxFins, int maxEndoFins, FindAllStepsProgressDialog dlg, int forCandidate, int type
    ) {
        this.initialize();
        Sudoku2 oldSudoku = this.getSudoku();
        this.setSudoku(newSudoku);
        List<SolutionStep> steps = this.fishSolver.getAllKrakenFishes(minSize, maxSize, maxFins, maxEndoFins, dlg, forCandidate, type);
        this.setSudoku(oldSudoku);
        return steps;
    }

    public List<SolutionStep> findAllEmptyRectangles(Sudoku2 newSudoku) {
        this.initialize();
        Sudoku2 oldSudoku = this.getSudoku();
        this.setSudoku(newSudoku);
        List<SolutionStep> steps = this.singleDigitPatternSolver.findAllEmptyRectangles();
        this.setSudoku(oldSudoku);
        return steps;
    }

    public List<SolutionStep> findAllSkyScrapers(Sudoku2 newSudoku) {
        this.initialize();
        Sudoku2 oldSudoku = this.getSudoku();
        this.setSudoku(newSudoku);
        List<SolutionStep> steps = this.singleDigitPatternSolver.findAllSkyscrapers();
        this.setSudoku(oldSudoku);
        return steps;
    }

    public List<SolutionStep> findAllTwoStringKites(Sudoku2 newSudoku) {
        this.initialize();
        Sudoku2 oldSudoku = this.getSudoku();
        this.setSudoku(newSudoku);
        List<SolutionStep> steps = this.singleDigitPatternSolver.findAllTwoStringKites();
        this.setSudoku(oldSudoku);
        return steps;
    }

    public List<SolutionStep> getAllUniqueness(Sudoku2 newSudoku) {
        this.initialize();
        Sudoku2 oldSudoku = this.getSudoku();
        this.setSudoku(newSudoku);
        List<SolutionStep> steps = this.uniquenessSolver.getAllUniqueness();
        this.setSudoku(oldSudoku);
        return steps;
    }

    public List<SolutionStep> getAllWings(Sudoku2 newSudoku) {
        this.initialize();
        Sudoku2 oldSudoku = this.getSudoku();
        this.setSudoku(newSudoku);
        List<SolutionStep> steps = this.wingSolver.getAllWings();
        this.setSudoku(oldSudoku);
        return steps;
    }

    public List<SolutionStep> findAllSimpleColors(Sudoku2 newSudoku) {
        this.initialize();
        Sudoku2 oldSudoku = this.getSudoku();
        this.setSudoku(newSudoku);
        List<SolutionStep> steps = this.coloringSolver.findAllSimpleColors();
        this.setSudoku(oldSudoku);
        return steps;
    }

    public List<SolutionStep> findAllMultiColors(Sudoku2 newSudoku) {
        this.initialize();
        Sudoku2 oldSudoku = this.getSudoku();
        this.setSudoku(newSudoku);
        List<SolutionStep> steps = this.coloringSolver.findAllMultiColors();
        this.setSudoku(oldSudoku);
        return steps;
    }

    public List<SolutionStep> getAllChains(Sudoku2 newSudoku) {
        this.initialize();
        Sudoku2 oldSudoku = this.getSudoku();
        this.setSudoku(newSudoku);
        List<SolutionStep> steps = this.chainSolver.getAllChains();
        this.setSudoku(oldSudoku);
        return steps;
    }

    public List<SolutionStep> getAllAlses(Sudoku2 newSudoku, boolean doXz, boolean doXy, boolean doChain) {
        this.initialize();
        Sudoku2 oldSudoku = this.getSudoku();
        this.setSudoku(newSudoku);
        List<SolutionStep> steps = this.alsSolver.getAllAlses(doXz, doXy, doChain);
        this.setSudoku(oldSudoku);
        return steps;
    }

    public List<SolutionStep> getAllDeathBlossoms(Sudoku2 newSudoku) {
        this.initialize();
        Sudoku2 oldSudoku = this.getSudoku();
        this.setSudoku(newSudoku);
        List<SolutionStep> steps = this.alsSolver.getAllDeathBlossoms();
        this.setSudoku(oldSudoku);
        return steps;
    }

    public List<SolutionStep> getAllSueDeCoqs(Sudoku2 newSudoku) {
        this.initialize();
        Sudoku2 oldSudoku = this.getSudoku();
        this.setSudoku(newSudoku);
        List<SolutionStep> steps = this.miscellaneousSolver.getAllSueDeCoqs();
        this.setSudoku(oldSudoku);
        return steps;
    }

    public List<SolutionStep> getAllNiceLoops(Sudoku2 newSudoku) {
        this.initialize();
        Sudoku2 oldSudoku = this.getSudoku();
        this.setSudoku(newSudoku);
        List<SolutionStep> steps = this.tablingSolver.getAllNiceLoops();
        this.setSudoku(oldSudoku);
        return steps;
    }

    public List<SolutionStep> getAllGroupedNiceLoops(Sudoku2 newSudoku) {
        this.initialize();
        Sudoku2 oldSudoku = this.getSudoku();
        this.setSudoku(newSudoku);
        List<SolutionStep> steps = this.tablingSolver.getAllGroupedNiceLoops();
        this.setSudoku(oldSudoku);
        return steps;
    }

    public List<SolutionStep> getAllForcingChains(Sudoku2 newSudoku) {
        this.initialize();
        Sudoku2 oldSudoku = this.getSudoku();
        this.setSudoku(newSudoku);
        List<SolutionStep> steps = this.tablingSolver.getAllForcingChains();
        this.setSudoku(oldSudoku);
        return steps;
    }

    public List<SolutionStep> getAllForcingNets(Sudoku2 newSudoku) {
        this.initialize();
        Sudoku2 oldSudoku = this.getSudoku();
        this.setSudoku(newSudoku);
        List<SolutionStep> steps = this.tablingSolver.getAllForcingNets();
        this.setSudoku(oldSudoku);
        return steps;
    }

    public List<SolutionStep> getAllTemplates(Sudoku2 newSudoku) {
        this.initialize();
        Sudoku2 oldSudoku = this.getSudoku();
        this.setSudoku(newSudoku);
        List<SolutionStep> steps = this.templateSolver.getAllTemplates();
        this.setSudoku(oldSudoku);
        return steps;
    }

    public SudokuSet[] getCandidates() {
        if (this.candidatesDirty) {
            this.initCandidates();
        }

        return this.candidates;
    }

    public SudokuSet[] getPositions() {
        if (this.positionsDirty) {
            this.initPositions();
        }

        return this.positions;
    }

    private void initCandidates() {
        if (this.candidatesDirty) {
            for (int i = 1; i < this.candidates.length; i++) {
                this.candidates[i].clear();
            }

            short[] cells = this.sudoku.getCells();

            for (int i = 0; i < cells.length; i++) {
                int[] cands = Sudoku2.POSSIBLE_VALUES[cells[i]];

                for (int j = 0; j < cands.length; j++) {
                    this.candidates[cands[j]].add(i);
                }
            }

            this.candidatesDirty = false;
        }
    }

    private void initPositions() {
        if (this.positionsDirty) {
            for (int i = 1; i < this.positions.length; i++) {
                this.positions[i].clear();
            }

            int[] values = this.sudoku.getValues();

            for (int i = 0; i < values.length; i++) {
                if (values[i] != 0) {
                    this.positions[values[i]].add(i);
                }
            }

            this.positionsDirty = false;
        }
    }

    public SudokuSet[] getCandidatesAllowed() {
        if (this.candidatesAllowedDirty) {
            this.initCandidatesAllowed();
        }

        return this.candidatesAllowed;
    }

    public SudokuSet getEmptyCells() {
        if (this.candidatesAllowedDirty) {
            this.initCandidatesAllowed();
        }

        return this.emptyCells;
    }

    private void initCandidatesAllowed() {
        if (this.candidatesAllowedDirty) {
            this.emptyCells.setAll();

            for (int i = 1; i < this.candidatesAllowed.length; i++) {
                this.candidatesAllowed[i].setAll();
            }

            int[] values = this.sudoku.getValues();

            for (int i = 0; i < values.length; i++) {
                if (values[i] != 0) {
                    this.candidatesAllowed[values[i]].andNot(Sudoku2.buddies[i]);
                    this.emptyCells.remove(i);
                }
            }

            for (int i = 1; i < this.candidatesAllowed.length; i++) {
                this.candidatesAllowed[i].and(this.emptyCells);
            }

            this.candidatesAllowedDirty = false;
        }
    }

    protected SudokuSet[] getDelCandTemplates(boolean initLists) {
        if (initLists && this.templatesListDirty || !initLists && this.templatesDirty) {
            this.initCandTemplates(initLists);
        }

        return this.delCandTemplates;
    }

    protected SudokuSet[] getSetValueTemplates(boolean initLists) {
        if (initLists && this.templatesListDirty || !initLists && this.templatesDirty) {
            this.initCandTemplates(initLists);
        }

        return this.setValueTemplates;
    }

    private void initCandTemplates(boolean initLists) {
        this.templateAnz++;
        long nanos = System.nanoTime();
        if (initLists && this.templatesListDirty || !initLists && this.templatesDirty) {
            SudokuSetBase[] allowedPositions = this.getCandidates();
            SudokuSet[] setPositions = this.getPositions();
            SudokuSetBase[] templates = Sudoku2.templates;
            SudokuSetBase[] forbiddenPositions = new SudokuSetBase[10];

            for (int i = 1; i <= 9; i++) {
                this.setValueTemplates[i].setAll();
                this.delCandTemplates[i].clear();
                this.candTemplates.get(i).clear();
                forbiddenPositions[i] = new SudokuSetBase();
                forbiddenPositions[i].set(setPositions[i]);
                forbiddenPositions[i].or(allowedPositions[i]);
                forbiddenPositions[i].not();
            }

            for (int i = 0; i < templates.length; i++) {
                for (int j = 1; j <= 9; j++) {
                    if (setPositions[j].andEquals(templates[i]) && forbiddenPositions[j].andEmpty(templates[i])) {
                        this.setValueTemplates[j].and(templates[i]);
                        this.delCandTemplates[j].or(templates[i]);
                        if (initLists) {
                            this.candTemplates.get(j).add(templates[i]);
                        }
                    }
                }
            }

            if (initLists) {
                int removals = 0;

                do {
                    removals = 0;

                    for (int j = 1; j <= 9; j++) {
                        this.setValueTemplates[j].setAll();
                        this.delCandTemplates[j].clear();
                        ListIterator<SudokuSetBase> it = this.candTemplates.get(j).listIterator();

                        while (it.hasNext()) {
                            SudokuSetBase template = it.next();
                            boolean removed = false;

                            for (int k = 1; k <= 9; k++) {
                                if (k != j && !template.andEmpty(this.setValueTemplates[k])) {
                                    it.remove();
                                    removed = true;
                                    removals++;
                                    break;
                                }
                            }

                            if (!removed) {
                                this.setValueTemplates[j].and(template);
                                this.delCandTemplates[j].or(template);
                            }
                        }
                    }
                } while (removals > 0);
            }

            for (int i = 1; i <= 9; i++) {
                this.delCandTemplates[i].not();
            }

            this.templatesDirty = false;
            if (initLists) {
                this.templatesListDirty = false;
            }
        }

        this.templateNanos = this.templateNanos + (System.nanoTime() - nanos);
    }

    public int getStepNumber() {
        return this.stepNumber;
    }

    public List<Als> getAlses() {
        return this.getAlses(false);
    }

    public List<Als> getAlses(boolean onlyLargerThanOne) {
        if (onlyLargerThanOne) {
            if (this.alsesOnlyLargerThanOneStepNumber == this.stepNumber) {
                return this.alsesOnlyLargerThanOne;
            }

            this.alsesOnlyLargerThanOne = this.doGetAlses(onlyLargerThanOne);
            this.alsesOnlyLargerThanOneStepNumber = this.stepNumber;
            return this.alsesOnlyLargerThanOne;
        } else {
            if (this.alsesWithOneStepNumber == this.stepNumber) {
                return this.alsesWithOne;
            }

            this.alsesWithOne = this.doGetAlses(onlyLargerThanOne);
            this.alsesWithOneStepNumber = this.stepNumber;
            return this.alsesWithOne;
        }
    }

    private List<Als> doGetAlses(boolean onlyLargerThanOne) {
        long actNanos = System.nanoTime();
        List<Als> alses = new ArrayList<>(300);
        alses.clear();

        for (int i = 0; i < Sudoku2.ALL_UNITS.length; i++) {
            for (int j = 0; j < Sudoku2.ALL_UNITS[i].length; j++) {
                this.indexSet.clear();
                this.candSets[0] = 0;
                this.checkAlsRecursive(0, j, Sudoku2.ALL_UNITS[i], alses, onlyLargerThanOne);
            }
        }

        for (Als als : alses) {
            als.computeFields(this);
        }

        this.alsNanos = this.alsNanos + (System.nanoTime() - actNanos);
        this.anzAlsCalls++;
        return alses;
    }

    private void checkAlsRecursive(int anzahl, int startIndex, int[] indexe, List<Als> alses, boolean onlyLargerThanOne) {
        if (++anzahl <= indexe.length - 1) {
            for (int i = startIndex; i < indexe.length; i++) {
                int houseIndex = indexe[i];
                if (this.sudoku.getValue(houseIndex) == 0) {
                    this.indexSet.add(houseIndex);
                    this.candSets[anzahl] = (short) (this.candSets[anzahl - 1] | this.sudoku.getCell(houseIndex));
                    if (Sudoku2.ANZ_VALUES[this.candSets[anzahl]] - anzahl == 1 && (!onlyLargerThanOne || this.indexSet.size() > 1)) {
                        this.anzAls++;
                        Als newAls = new Als(this.indexSet, this.candSets[anzahl]);
                        if (!alses.contains(newAls)) {
                            alses.add(newAls);
                        } else {
                            this.doubleAls++;
                        }
                    }

                    this.checkAlsRecursive(anzahl, i + 1, indexe, alses, onlyLargerThanOne);
                    this.indexSet.remove(houseIndex);
                }
            }
        }
    }

    public String getAlsStatistics() {
        return "Statistic for getAls(): number of calls: "
                + this.anzAlsCalls
                + ", total time: "
                + this.alsNanos / 1000L
                + "us, average: "
                + this.alsNanos / this.anzAlsCalls / 1000L
                + "us\r\n"
                + "    anz: "
                + this.anzAls
                + "/"
                + this.anzAls / this.anzAlsCalls
                + ", double: "
                + this.doubleAls
                + "/"
                + this.doubleAls / this.anzAlsCalls
                + " res: "
                + (this.anzAls - this.doubleAls)
                + "/"
                + (this.anzAls - this.doubleAls) / this.anzAlsCalls;
    }

    public List<RestrictedCommon> getRestrictedCommons(List<Als> alses, boolean allowOverlap) {
        if (this.lastRcStepNumber != this.stepNumber
                || this.lastRcAllowOverlap != allowOverlap
                || this.lastRcAlsList != alses
                || this.lastRcOnlyForward != this.rcOnlyForward) {
            if (this.startIndices == null || this.startIndices.length < alses.size()) {
                this.startIndices = new int[(int) (alses.size() * 1.5)];
                this.endIndices = new int[(int) (alses.size() * 1.5)];
            }

            this.restrictedCommons = this.doGetRestrictedCommons(alses, allowOverlap);
            this.lastRcStepNumber = this.stepNumber;
            this.lastRcAllowOverlap = allowOverlap;
            this.lastRcOnlyForward = this.rcOnlyForward;
            this.lastRcAlsList = alses;
        }

        return this.restrictedCommons;
    }

    public int[] getStartIndices() {
        return this.startIndices;
    }

    public int[] getEndIndices() {
        return this.endIndices;
    }

    public boolean isRcOnlyForward() {
        return this.rcOnlyForward;
    }

    public void setRcOnlyForward(boolean rof) {
        this.rcOnlyForward = rof;
    }

    private List<RestrictedCommon> doGetRestrictedCommons(List<Als> alses, boolean withOverlap) {
        this.rcAnzCalls++;
        long actNanos = 0L;
        actNanos = System.nanoTime();
        this.lastRcOnlyForward = this.rcOnlyForward;
        List<RestrictedCommon> rcs = new ArrayList<>(2000);

        for (int i = 0; i < alses.size(); i++) {
            Als als1 = alses.get(i);
            this.startIndices[i] = rcs.size();
            int start = 0;
            if (this.rcOnlyForward) {
                start = i + 1;
            }

            for (int j = start; j < alses.size(); j++) {
                if (i != j) {
                    Als als2 = alses.get(j);
                    this.intersectionSet.set(als1.indices);
                    this.intersectionSet.and(als2.indices);
                    if (withOverlap || this.intersectionSet.isEmpty()) {
                        this.possibleRestrictedCommonsSet = als1.candidates;
                        this.possibleRestrictedCommonsSet = (short) (this.possibleRestrictedCommonsSet & als2.candidates);
                        if (this.possibleRestrictedCommonsSet != 0) {
                            int rcAnz = 0;
                            RestrictedCommon newRC = null;
                            int[] prcs = Sudoku2.POSSIBLE_VALUES[this.possibleRestrictedCommonsSet];

                            for (int k = 0; k < prcs.length; k++) {
                                int cand = prcs[k];
                                this.restrictedCommonIndexSet.set(als1.indicesPerCandidat[cand]);
                                this.restrictedCommonIndexSet.or(als2.indicesPerCandidat[cand]);
                                if (this.restrictedCommonIndexSet.andEmpty(this.intersectionSet)) {
                                    this.restrictedCommonBuddiesSet.setAnd(als1.buddiesAlsPerCandidat[cand], als2.buddiesAlsPerCandidat[cand]);
                                    if (this.restrictedCommonIndexSet.andEquals(this.restrictedCommonBuddiesSet)) {
                                        if (rcAnz == 0) {
                                            newRC = new RestrictedCommon(i, j, cand);
                                            rcs.add(newRC);
                                            this.anzRcs++;
                                        } else {
                                            newRC.setCand2(cand);
                                        }

                                        rcAnz++;
                                    }
                                }
                            }

                            if (rcAnz > 0) {
                            }
                        }
                    }
                }
            }

            this.endIndices[i] = rcs.size();
        }

        actNanos = System.nanoTime() - actNanos;
        this.rcNanos += actNanos;
        return rcs;
    }

    public String getRCStatistics() {
        return "Statistic for getRestrictedCommons(): number of calls: "
                + this.rcAnzCalls
                + ", total time: "
                + this.rcNanos / 1000L
                + "us, average: "
                + this.rcNanos / this.rcAnzCalls / 1000L
                + "us\r\n"
                + "    anz: "
                + this.anzRcs
                + "/"
                + this.anzRcs / this.rcAnzCalls;
    }

    public void printStatistics() {
    }
}
