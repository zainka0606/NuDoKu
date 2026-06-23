package solver;

import sudoku.*;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TablingSolver extends AbstractSolver {
    private static final long CLEANUP_INTERVAL = 300000L;
    private static final int MAX_REC_DEPTH = 50;
    private static boolean DEBUG = false;
    private static TablingSolver.TablingComparator tablingComparator = null;
    private List<SolutionStep> steps;
    private SolutionStep globalStep = new SolutionStep(SolutionType.HIDDEN_SINGLE);
    private SortedMap<String, Integer> deletesMap = new TreeMap<>();
    private boolean chainsOnly = true;
    private boolean withGroupNodes = false;
    private boolean withAlsNodes = false;
    private boolean onlyGroupedNiceLoops = false;
    private TableEntry[] onTable = null;
    private TableEntry[] offTable = null;
    private List<TableEntry> entryList = new ArrayList<>(10);
    private SudokuSet tmpSet = new SudokuSet();
    private SudokuSet tmpSet1 = new SudokuSet();
    private SudokuSet tmpSet2 = new SudokuSet();
    private SudokuSet tmpSetC = new SudokuSet();
    private SudokuSet[] tmpOnSets = new SudokuSet[10];
    private SudokuSet[] tmpOffSets = new SudokuSet[10];
    private TreeMap<Integer, Integer> chainAlses = new TreeMap<>();
    private Sudoku2 savedSudoku;
    private int[][] retIndices = new int[50][5];
    private List<GroupNode> groupNodes = null;
    private List<Als> alses = null;
    private SudokuSet[] alsEliminations = new SudokuSet[10];
    private SudokuStepFinder simpleFinder;
    private List<SolutionStep> singleSteps = new ArrayList<>();
    private int[] chain = new int[Options.getInstance().getMaxTableEntryLength()];
    private int chainIndex = 0;
    private int[][] mins = new int[200][Options.getInstance().getMaxTableEntryLength()];
    private int[] minIndexes = new int[this.mins.length];
    private int actMin = 0;
    private int[] tmpChain = new int[Options.getInstance().getMaxTableEntryLength()];
    private Chain[] tmpChains = new Chain[9];
    private int tmpChainsIndex = 0;
    private SudokuSet lassoSet = new SudokuSet();
    private List<TableEntry> extendedTable = null;
    private SortedMap<Integer, Integer> extendedTableMap = null;
    private int extendedTableIndex = 0;
    private boolean initialized = false;
    private long lastUsed = -1L;

    public TablingSolver(SudokuStepFinder finder) {
        super(finder);
        this.simpleFinder = new SudokuStepFinder(true);

        for (int i = 0; i < this.tmpOnSets.length; i++) {
            this.tmpOnSets[i] = new SudokuSet();
            this.tmpOffSets[i] = new SudokuSet();
        }

        this.steps = new ArrayList<>();
        if (tablingComparator == null) {
            tablingComparator = new TablingSolver.TablingComparator();
        }

        for (int i = 0; i < this.tmpChains.length; i++) {
            this.tmpChains[i] = new Chain();
            this.tmpChains[i].setChain(new int[Options.getInstance().getMaxTableEntryLength()]);
        }

        for (int i = 0; i < this.alsEliminations.length; i++) {
            this.alsEliminations[i] = new SudokuSet();
        }
    }

    public static void main(String[] args) {
        SudokuStepFinder finder = new SudokuStepFinder();
        DEBUG = true;
        Sudoku2 sudoku = new Sudoku2();
        sudoku.setSudoku(
                ":0000:x:9...6..2............1.893.......65..41.8...96..24.......352.1..1.........8..1...5:316 716 221 521 621 721 325 725 326 726 741 344 744 944 345 348 748 848 349 749 849 361 861 362 365 366 384 784 985 394 794::"
        );
        sudoku.setSudoku(
                ":0709:1234679:5.81...6.....9.4...39.8..7..6...5.....27.95....58...2..8..5134..51.3.....9...8651:221 224 231 743 445 349 666 793:122 128 131 141 147 151 161 167 219 229 341 348 441 451 461 624 761 769 919 947 967 987::11"
        );
        sudoku.setSudoku(
                ":0711-4:59:...65+4+328+2458.31.+6+63+8....+459+7+31+4+5+86+2+42+1+38+6..+9+8+56..74+13.84.....7.......+8..6...+8.3.:175 275 975 185 285 785 985:578 974::7"
        );
        sudoku.setSudoku(":0000:x:.......123......6+4+1...4..+8+59+1...+45+2......1+67..2....+1+4....35+64+9+1..14..8.+6.6....+2.+7:::");
        finder.setSudoku(sudoku);
        List<SolutionStep> steps = null;
        long ticks = System.currentTimeMillis();
        int anzLoops = 1;

        for (int i = 0; i < anzLoops; i++) {
            steps = finder.getAllForcingChains(sudoku);
        }

        ticks = System.currentTimeMillis() - ticks;
        System.out.println("Dauer: " + ticks / anzLoops + "ms");
        System.out.println("Anzahl Steps: " + steps.size());

        for (int i = 0; i < steps.size(); i++) {
            System.out.println(steps.get(i).toString(2));
        }
    }

    private void initialize() {
        if (!this.initialized) {
            this.onTable = new TableEntry[810];
            this.offTable = new TableEntry[810];

            for (int i = 0; i < this.onTable.length; i++) {
                this.onTable[i] = new TableEntry();
                this.offTable[i] = new TableEntry();
            }

            this.extendedTable = new ArrayList<>();
            this.extendedTableMap = new TreeMap<>();
            this.extendedTableIndex = 0;
            this.initialized = true;
        }

        this.lastUsed = System.currentTimeMillis();
    }

    @Override
    protected void cleanUp() {
        synchronized (this) {
            if (this.initialized && System.currentTimeMillis() - this.lastUsed > 300000L) {
                for (int i = 0; i < this.onTable.length; i++) {
                    this.onTable[i] = null;
                    this.offTable[i] = null;
                }
            }

            this.onTable = null;
            this.offTable = null;
            if (this.extendedTable != null) {
                for (int i = 0; i < this.extendedTableIndex; i++) {
                    this.extendedTable.set(i, null);
                }

                this.extendedTable = null;
            }

            if (this.extendedTableMap != null) {
                this.extendedTableMap.clear();
                this.extendedTableMap = null;
            }

            this.extendedTableIndex = 0;
            this.initialized = false;
        }
    }

    private void resetTmpChains() {
        for (int i = 0; i < this.tmpChains.length; i++) {
            this.tmpChains[i].reset();
        }

        this.tmpChainsIndex = 0;
    }

    @Override
    protected SolutionStep getStep(SolutionType type) {
        SolutionStep result = null;
        this.sudoku = this.finder.getSudoku();
        switch (type) {
            case NICE_LOOP:
            case CONTINUOUS_NICE_LOOP:
            case DISCONTINUOUS_NICE_LOOP:
            case AIC:
                this.withGroupNodes = false;
                this.withAlsNodes = false;
                result = this.getNiceLoops();
                break;
            case GROUPED_NICE_LOOP:
            case GROUPED_CONTINUOUS_NICE_LOOP:
            case GROUPED_DISCONTINUOUS_NICE_LOOP:
            case GROUPED_AIC:
                this.withGroupNodes = true;
                this.withAlsNodes = Options.getInstance().isAllowAlsInTablingChains();
                result = this.getNiceLoops();
                break;
            case FORCING_CHAIN:
            case FORCING_CHAIN_CONTRADICTION:
            case FORCING_CHAIN_VERITY:
                this.steps.clear();
                this.withGroupNodes = true;
                this.withAlsNodes = Options.getInstance().isAllowAlsInTablingChains();
                this.getForcingChains();
                if (this.steps.size() > 0) {
                    Collections.sort(this.steps, tablingComparator);
                    result = this.steps.get(0);
                }
                break;
            case FORCING_NET:
            case FORCING_NET_CONTRADICTION:
            case FORCING_NET_VERITY:
                this.steps.clear();
                this.withGroupNodes = true;
                this.withAlsNodes = Options.getInstance().isAllowAlsInTablingChains();
                this.getForcingNets();
                if (this.steps.size() > 0) {
                    Collections.sort(this.steps, tablingComparator);
                    result = this.steps.get(0);
                }
        }

        return result;
    }

    @Override
    protected boolean doStep(SolutionStep step) {
        boolean handled = true;
        this.sudoku = this.finder.getSudoku();
        switch (step.getType()) {
            case NICE_LOOP:
            case CONTINUOUS_NICE_LOOP:
            case DISCONTINUOUS_NICE_LOOP:
            case AIC:
            case GROUPED_NICE_LOOP:
            case GROUPED_CONTINUOUS_NICE_LOOP:
            case GROUPED_DISCONTINUOUS_NICE_LOOP:
            case GROUPED_AIC:
                for (Candidate cand : step.getCandidatesToDelete()) {
                    this.sudoku.delCandidate(cand.getIndex(), cand.getValue());
                }
                break;
            case FORCING_CHAIN:
            case FORCING_CHAIN_CONTRADICTION:
            case FORCING_CHAIN_VERITY:
            case FORCING_NET:
            case FORCING_NET_CONTRADICTION:
            case FORCING_NET_VERITY:
                if (step.getValues().size() > 0) {
                    for (int i = 0; i < step.getValues().size(); i++) {
                        int value = step.getValues().get(i);
                        int index = step.getIndices().get(i);
                        this.sudoku.setCell(index, value);
                    }
                } else {
                    for (Candidate cand : step.getCandidatesToDelete()) {
                        this.sudoku.delCandidate(cand.getIndex(), cand.getValue());
                    }
                }
                break;
            default:
                handled = false;
        }

        return handled;
    }

    protected synchronized List<SolutionStep> getAllNiceLoops() {
        this.initialize();
        this.sudoku = this.finder.getSudoku();
        long ticks = System.currentTimeMillis();
        this.steps = new ArrayList<>();
        this.withGroupNodes = false;
        this.withAlsNodes = false;
        this.doGetNiceLoops();
        Collections.sort(this.steps);
        ticks = System.currentTimeMillis() - ticks;
        if (DEBUG) {
            System.out.println("getAllNiceLoops() gesamt: " + ticks + "ms");
        }

        return this.steps;
    }

    protected synchronized List<SolutionStep> getAllGroupedNiceLoops() {
        this.initialize();
        this.sudoku = this.finder.getSudoku();
        long ticks = System.currentTimeMillis();
        this.steps = new ArrayList<>();
        this.withGroupNodes = true;
        this.withAlsNodes = Options.getInstance().isAllowAlsInTablingChains();
        this.onlyGroupedNiceLoops = true;
        this.doGetNiceLoops();
        this.onlyGroupedNiceLoops = false;
        Collections.sort(this.steps);
        ticks = System.currentTimeMillis() - ticks;
        if (DEBUG) {
            System.out.println("getAllGroupedNiceLoops() gesamt: " + ticks + "ms");
        }

        return this.steps;
    }

    protected synchronized List<SolutionStep> getAllForcingChains() {
        this.initialize();
        this.sudoku = this.finder.getSudoku();
        List<SolutionStep> oldSteps = this.steps;
        this.steps = new ArrayList<>();
        long millis1 = System.currentTimeMillis();
        this.withGroupNodes = true;
        this.withAlsNodes = Options.getInstance().isAllowAlsInTablingChains();
        this.getForcingChains();
        Collections.sort(this.steps, tablingComparator);
        millis1 = System.currentTimeMillis() - millis1;
        if (DEBUG) {
            System.out.println("getAllForcingChains() gesamt: " + millis1 + "ms");
        }

        List<SolutionStep> result = this.steps;
        this.steps = oldSteps;
        return result;
    }

    protected synchronized List<SolutionStep> getAllForcingNets() {
        this.initialize();
        this.sudoku = this.finder.getSudoku();
        List<SolutionStep> oldSteps = this.steps;
        this.steps = new ArrayList<>();
        long millis1 = System.currentTimeMillis();
        this.withGroupNodes = true;
        this.withAlsNodes = Options.getInstance().isAllowAlsInTablingChains();
        this.getForcingNets();
        Collections.sort(this.steps, tablingComparator);
        millis1 = System.currentTimeMillis() - millis1;
        if (DEBUG) {
            System.out.println("getAllForcingNets() gesamt: " + millis1 + "ms");
        }

        List<SolutionStep> result = this.steps;
        this.steps = oldSteps;
        return result;
    }

    protected void initForKrakenSearch() {
        this.initialize();
        this.sudoku = this.finder.getSudoku();
        this.deletesMap.clear();
        long ticks = System.currentTimeMillis();
        this.chainsOnly = true;
        this.fillTables();
        this.fillTablesWithGroupNodes();
        if (Options.getInstance().isAllowAlsInTablingChains()) {
            this.fillTablesWithAls();
        }

        ticks = System.currentTimeMillis() - ticks;
        if (DEBUG) {
            System.out.println("fillTables(): " + ticks + "ms");
        }

        this.printTableAnz();
        ticks = System.currentTimeMillis();
        this.expandTables(this.onTable);
        this.expandTables(this.offTable);
        ticks = System.currentTimeMillis() - ticks;
        if (DEBUG) {
            System.out.println("expandTables(): " + ticks + "ms");
        }

        this.printTableAnz();
    }

    protected boolean checkKrakenTypeOne(SudokuSet fins, int index, int candidate) {
        for (int i = 0; i < fins.size(); i++) {
            int tableIndex = fins.get(i) * 10 + candidate;
            if (!this.onTable[tableIndex].offSets[candidate].contains(index)) {
                return false;
            }
        }

        return true;
    }

    protected boolean checkKrakenTypeTwo(SudokuSet indices, SudokuSet result, int startCandidate, int endCandidate) {
        result.set(this.finder.getCandidates()[endCandidate]);
        result.andNot(indices);

        for (int i = 0; i < indices.size(); i++) {
            int tableIndex = indices.get(i) * 10 + startCandidate;
            result.and(this.onTable[tableIndex].offSets[endCandidate]);
        }

        return !result.isEmpty();
    }

    protected Chain getKrakenChain(int startIndex, int startCandidate, int endIndex, int endCandidate) {
        this.globalStep.reset();
        this.resetTmpChains();
        this.addChain(this.onTable[startIndex * 10 + startCandidate], endIndex, endCandidate, false);
        return this.globalStep.getChains().get(0);
    }

    private synchronized SolutionStep getNiceLoops() {
        this.initialize();
        this.steps = new ArrayList<>();
        this.doGetNiceLoops();
        if (this.steps.size() > 0) {
            Collections.sort(this.steps);
            return this.steps.get(0);
        } else {
            return null;
        }
    }

    private synchronized void getForcingChains() {
        this.initialize();
        this.chainsOnly = true;
        this.doGetForcingChains();
    }

    private synchronized void getForcingNets() {
        this.initialize();
        this.chainsOnly = false;
        this.doGetForcingChains();
    }

    private void doGetNiceLoops() {
        this.deletesMap.clear();
        long ticks = System.currentTimeMillis();
        this.chainsOnly = true;
        this.fillTables();
        if (this.withGroupNodes) {
            this.fillTablesWithGroupNodes();
        }

        if (this.withAlsNodes) {
            this.fillTablesWithAls();
        }

        ticks = System.currentTimeMillis() - ticks;
        if (DEBUG) {
            System.out.println("fillTables(): " + ticks + "ms");
        }

        this.printTableAnz();
        ticks = System.currentTimeMillis();
        this.expandTables(this.onTable);
        this.expandTables(this.offTable);
        ticks = System.currentTimeMillis() - ticks;
        if (DEBUG) {
            System.out.println("expandTables(): " + ticks + "ms");
        }

        this.printTableAnz();
        ticks = System.currentTimeMillis();
        this.checkNiceLoops(this.onTable);
        this.checkNiceLoops(this.offTable);
        this.checkAics(this.offTable);
        ticks = System.currentTimeMillis() - ticks;
        if (DEBUG) {
            System.out.println("checkNiceLoops(): " + ticks + "ms");
        }
    }

    private void doGetForcingChains() {
        this.deletesMap.clear();
        long ticks = System.currentTimeMillis();
        this.fillTables();
        if (this.withGroupNodes) {
            this.fillTablesWithGroupNodes();
        }

        if (this.withAlsNodes) {
            this.fillTablesWithAls();
        }

        ticks = System.currentTimeMillis() - ticks;
        if (DEBUG) {
            System.out.println("fillTables(): " + ticks + "ms");
        }

        this.printTableAnz();
        ticks = System.currentTimeMillis();
        this.expandTables(this.onTable);
        this.expandTables(this.offTable);
        ticks = System.currentTimeMillis() - ticks;
        if (DEBUG) {
            System.out.println("expandTables(): " + ticks + "ms");
        }

        this.printTableAnz();
        ticks = System.currentTimeMillis();
        this.checkForcingChains();
        ticks = System.currentTimeMillis() - ticks;
        if (DEBUG) {
            System.out.println("checkChains(): " + ticks + "ms");
        }
    }

    private void checkForcingChains() {
        for (int i = 0; i < this.onTable.length; i++) {
            this.checkOneChain(this.onTable[i]);
            this.checkOneChain(this.offTable[i]);
        }

        for (int i = 0; i < this.onTable.length; i++) {
            this.checkTwoChains(this.onTable[i], this.offTable[i]);
        }

        this.checkAllChainsForHouse(null);
        this.checkAllChainsForHouse(Sudoku2.LINE_TEMPLATES);
        this.checkAllChainsForHouse(Sudoku2.COL_TEMPLATES);
        this.checkAllChainsForHouse(Sudoku2.BLOCK_TEMPLATES);
    }

    private void checkAllChainsForHouse(SudokuSet[] houseSets) {
        if (houseSets == null) {
            for (int i = 0; i < 81; i++) {
                if (this.sudoku.getValue(i) == 0) {
                    this.entryList.clear();
                    int[] cands = this.sudoku.getAllCandidates(i);

                    for (int j = 0; j < cands.length; j++) {
                        this.entryList.add(this.onTable[i * 10 + cands[j]]);
                    }

                    this.checkEntryList(this.entryList);
                }
            }
        } else {
            for (int i = 0; i < houseSets.length; i++) {
                for (int j = 1; j < this.finder.getCandidates().length; j++) {
                    this.tmpSet.set(houseSets[i]);
                    this.tmpSet.and(this.finder.getCandidates()[j]);
                    if (!this.tmpSet.isEmpty()) {
                        this.entryList.clear();

                        for (int k = 0; k < this.tmpSet.size(); k++) {
                            this.entryList.add(this.onTable[this.tmpSet.get(k) * 10 + j]);
                        }

                        this.checkEntryList(this.entryList);
                    }
                }
            }
        }
    }

    private void checkEntryList(List<TableEntry> entryList) {
        for (int i = 0; i < entryList.size(); i++) {
            TableEntry entry = entryList.get(i);

            for (int j = 1; j < this.tmpOnSets.length; j++) {
                if (i == 0) {
                    this.tmpOnSets[j].set(entry.onSets[j]);
                    this.tmpOffSets[j].set(entry.offSets[j]);
                } else {
                    this.tmpOnSets[j].and(entry.onSets[j]);
                    this.tmpOffSets[j].and(entry.offSets[j]);
                }
            }
        }

        for (int j = 1; j < this.tmpOnSets.length; j++) {
            if (!this.tmpOnSets[j].isEmpty()) {
                for (int k = 0; k < this.tmpOnSets[j].size(); k++) {
                    if (DEBUG && k > 0) {
                        System.out.println("More than one chein/net found 1");
                    }

                    this.globalStep.reset();
                    this.globalStep.setType(SolutionType.FORCING_CHAIN_VERITY);
                    this.globalStep.addIndex(this.tmpOnSets[j].get(k));
                    this.globalStep.addValue(j);
                    this.resetTmpChains();

                    for (int l = 0; l < entryList.size(); l++) {
                        this.addChain(entryList.get(l), this.tmpOnSets[j].get(k), j, true);
                    }

                    this.replaceOrCopyStep();
                }
            }

            if (!this.tmpOffSets[j].isEmpty()) {
                for (int k = 0; k < this.tmpOffSets[j].size(); k++) {
                    if (DEBUG && k > 0) {
                        System.out.println("More than one chein/net found 2");
                    }

                    this.globalStep.reset();
                    this.globalStep.setType(SolutionType.FORCING_CHAIN_VERITY);
                    this.globalStep.addCandidateToDelete(this.tmpOffSets[j].get(k), j);
                    this.resetTmpChains();

                    for (int l = 0; l < entryList.size(); l++) {
                        this.addChain(entryList.get(l), this.tmpOffSets[j].get(k), j, false);
                    }

                    this.replaceOrCopyStep();
                }
            }
        }
    }

    private void adjustType(SolutionStep step) {
        if (step.isNet()) {
            if (step.getType() == SolutionType.FORCING_CHAIN_CONTRADICTION) {
                step.setType(SolutionType.FORCING_NET_CONTRADICTION);
            }

            if (step.getType() == SolutionType.FORCING_CHAIN_VERITY) {
                step.setType(SolutionType.FORCING_NET_VERITY);
            }
        }
    }

    protected void adjustChains(SolutionStep step) {
        int alsIndex = step.getAlses().size();
        this.chainAlses.clear();

        for (int i = 0; i < step.getChainAnz(); i++) {
            Chain adjChain = step.getChains().get(i);

            for (int j = adjChain.getStart(); j <= adjChain.getEnd(); j++) {
                if (Chain.getSNodeType(adjChain.getChain()[j]) == 2) {
                    int which = Chain.getSAlsIndex(adjChain.getChain()[j]);
                    if (this.chainAlses.containsKey(which)) {
                        int newIndex = this.chainAlses.get(which);
                        adjChain.replaceAlsIndex(j, newIndex);
                    } else {
                        step.addAls(this.alses.get(which).indices, this.alses.get(which).candidates);
                        this.chainAlses.put(which, alsIndex);
                        adjChain.replaceAlsIndex(j, alsIndex);
                        alsIndex++;
                    }
                }
            }
        }
    }

    private void replaceStep(SolutionStep src, SolutionStep dest) {
        this.adjustType(src);
        dest.setType(src.getType());
        if (src.getIndices().size() > 0) {
            for (int i = 0; i < src.getIndices().size(); i++) {
                dest.getIndices().set(i, src.getIndices().get(i));
                dest.getValues().set(i, src.getValues().get(i));
            }
        } else {
            for (int i = 0; i < src.getCandidatesToDelete().size(); i++) {
                dest.getCandidatesToDelete().set(i, src.getCandidatesToDelete().get(i));
            }
        }

        if (src.getAlses().size() > 0) {
            dest.getAlses().clear();

            for (int i = 0; i < src.getAlses().size(); i++) {
                dest.addAls(src.getAlses().get(i));
            }
        }

        dest.getEndoFins().clear();

        for (int i = 0; i < src.getEndoFins().size(); i++) {
            dest.getEndoFins().add(src.getEndoFins().get(i));
        }

        dest.setEntity(src.getEntity());
        dest.setEntityNumber(src.getEntityNumber());
        int i = 0;

        for (i = 0; i < src.getChains().size(); i++) {
            Chain localTmpChain = src.getChains().get(i);
            boolean toShort = dest.getChains().size() > i && dest.getChains().get(i).getChain().length < localTmpChain.getEnd() + 1;
            if (i < dest.getChains().size() && !toShort) {
                Chain destChain = dest.getChains().get(i);

                for (int j = 0; j <= localTmpChain.getEnd(); j++) {
                    destChain.getChain()[j] = localTmpChain.getChain()[j];
                }

                destChain.setStart(localTmpChain.getStart());
                destChain.setEnd(localTmpChain.getEnd());
                destChain.resetLength();
            } else {
                int[] tmp = new int[localTmpChain.getEnd() + 1];

                for (int j = 0; j <= localTmpChain.getEnd(); j++) {
                    tmp[j] = localTmpChain.getChain()[j];
                }

                if (toShort) {
                    Chain destChain = dest.getChains().get(i);
                    destChain.setChain(tmp);
                    destChain.setStart(localTmpChain.getStart());
                    destChain.setEnd(localTmpChain.getEnd());
                    destChain.resetLength();
                } else {
                    dest.addChain(0, localTmpChain.getEnd(), tmp);
                }
            }
        }

        while (i < dest.getChains().size()) {
            dest.getChains().remove(i);
        }
    }

    private void replaceOrCopyStep() {
        this.adjustType(this.globalStep);
        if (this.chainsOnly
                || this.globalStep.getType() != SolutionType.FORCING_CHAIN_CONTRADICTION && this.globalStep.getType() != SolutionType.FORCING_CHAIN_VERITY) {
            this.adjustChains(this.globalStep);
            String del = null;
            if (Options.getInstance().isOnlyOneChainPerStep()) {
                if (this.globalStep.getCandidatesToDelete().size() > 0) {
                    del = this.globalStep.getCandidateString();
                } else {
                    del = this.globalStep.getSingleCandidateString();
                }

                Integer oldIndex = this.deletesMap.get(del);
                SolutionStep actStep = null;
                if (oldIndex != null) {
                    actStep = this.steps.get(oldIndex);
                }

                if (actStep != null) {
                    if (actStep.getChainLength() > this.globalStep.getChainLength()) {
                        this.replaceStep(this.globalStep, actStep);
                    }

                    return;
                }
            }

            List<Chain> oldChains = this.globalStep.getChains();
            int chainAnz = oldChains.size();
            oldChains.clear();

            for (int i = 0; i < chainAnz; i++) {
                oldChains.add((Chain) this.tmpChains[i].clone());
            }

            this.steps.add((SolutionStep) this.globalStep.clone());
            if (del != null) {
                this.deletesMap.put(del, this.steps.size() - 1);
            }
        }
    }

    private String printEntryList(List<TableEntry> entryList) {
        StringBuilder tmp = new StringBuilder();

        for (int i = 0; i < entryList.size(); i++) {
            if (i != 0) {
                tmp.append(", ");
            }

            tmp.append(this.printTableEntry(entryList.get(i).entries[0]));
        }

        return tmp.toString();
    }

    private void checkTwoChains(TableEntry on, TableEntry off) {
        if (on.index != 0 && off.index != 0) {
            for (int i = 1; i < on.onSets.length; i++) {
                this.tmpSet.set(on.onSets[i]);
                this.tmpSet.and(off.onSets[i]);
                this.tmpSet.remove(on.getCellIndex(0));
                if (!this.tmpSet.isEmpty()) {
                    for (int j = 0; j < this.tmpSet.size(); j++) {
                        this.globalStep.reset();
                        this.globalStep.setType(SolutionType.FORCING_CHAIN_VERITY);
                        this.globalStep.addIndex(this.tmpSet.get(j));
                        this.globalStep.addValue(i);
                        this.resetTmpChains();
                        this.addChain(on, this.tmpSet.get(j), i, true);
                        this.addChain(off, this.tmpSet.get(j), i, true);
                        this.replaceOrCopyStep();
                    }
                }
            }

            for (int i = 1; i < on.offSets.length; i++) {
                this.tmpSet.set(on.offSets[i]);
                this.tmpSet.and(off.offSets[i]);
                this.tmpSet.remove(on.getCellIndex(0));
                if (!this.tmpSet.isEmpty()) {
                    for (int j = 0; j < this.tmpSet.size(); j++) {
                        this.globalStep.reset();
                        this.globalStep.setType(SolutionType.FORCING_CHAIN_VERITY);
                        this.globalStep.addCandidateToDelete(this.tmpSet.get(j), i);
                        this.resetTmpChains();
                        this.addChain(on, this.tmpSet.get(j), i, false);
                        this.addChain(off, this.tmpSet.get(j), i, false);
                        this.replaceOrCopyStep();
                    }
                }
            }
        }
    }

    private void checkOneChain(TableEntry entry) {
        if (entry.index != 0) {
            if (entry.isStrong(0) && entry.offSets[entry.getCandidate(0)].contains(entry.getCellIndex(0))
                    || !entry.isStrong(0) && entry.onSets[entry.getCandidate(0)].contains(entry.getCellIndex(0))) {
                this.globalStep.reset();
                this.globalStep.setType(SolutionType.FORCING_CHAIN_CONTRADICTION);
                if (entry.isStrong(0)) {
                    this.globalStep.addCandidateToDelete(entry.getCellIndex(0), entry.getCandidate(0));
                } else {
                    this.globalStep.addIndex(entry.getCellIndex(0));
                    this.globalStep.addValue(entry.getCandidate(0));
                }

                this.globalStep.setEntity(3);
                this.globalStep.setEntityNumber(this.tmpSet.get(0));
                this.resetTmpChains();
                this.addChain(entry, entry.getCellIndex(0), entry.getCandidate(0), !entry.isStrong(0));
                this.replaceOrCopyStep();
            }

            for (int i = 0; i < entry.onSets.length; i++) {
                this.tmpSet.set(entry.onSets[i]);
                this.tmpSet.and(entry.offSets[i]);
                if (!this.tmpSet.isEmpty()) {
                    this.globalStep.reset();
                    this.globalStep.setType(SolutionType.FORCING_CHAIN_CONTRADICTION);
                    if (entry.isStrong(0)) {
                        this.globalStep.addCandidateToDelete(entry.getCellIndex(0), entry.getCandidate(0));
                    } else {
                        this.globalStep.addIndex(entry.getCellIndex(0));
                        this.globalStep.addValue(entry.getCandidate(0));
                    }

                    this.globalStep.setEntity(3);
                    this.globalStep.setEntityNumber(this.tmpSet.get(0));
                    this.resetTmpChains();
                    this.addChain(entry, this.tmpSet.get(0), i, false);
                    this.addChain(entry, this.tmpSet.get(0), i, true);
                    this.replaceOrCopyStep();
                }
            }

            for (int i = 1; i < entry.onSets.length; i++) {
                for (int j = i + 1; j < entry.onSets.length; j++) {
                    this.tmpSet.set(entry.onSets[i]);
                    this.tmpSet.and(entry.onSets[j]);
                    if (!this.tmpSet.isEmpty()) {
                        this.globalStep.reset();
                        this.globalStep.setType(SolutionType.FORCING_CHAIN_CONTRADICTION);
                        if (entry.isStrong(0)) {
                            this.globalStep.addCandidateToDelete(entry.getCellIndex(0), entry.getCandidate(0));
                        } else {
                            this.globalStep.addIndex(entry.getCellIndex(0));
                            this.globalStep.addValue(entry.getCandidate(0));
                        }

                        this.globalStep.setEntity(3);
                        this.globalStep.setEntityNumber(this.tmpSet.get(0));
                        this.resetTmpChains();
                        this.addChain(entry, this.tmpSet.get(0), i, true);
                        this.addChain(entry, this.tmpSet.get(0), j, true);
                        this.replaceOrCopyStep();
                    }
                }
            }

            this.checkHouseSet(entry, Sudoku2.LINE_TEMPLATES, 1);
            this.checkHouseSet(entry, Sudoku2.COL_TEMPLATES, 2);
            this.checkHouseSet(entry, Sudoku2.BLOCK_TEMPLATES, 0);
            this.tmpSet.setAll();

            for (int i = 1; i < entry.offSets.length; i++) {
                this.tmpSet1.set(entry.offSets[i]);
                this.tmpSet1.orNot(this.finder.getCandidates()[i]);
                this.tmpSet.and(this.tmpSet1);
            }

            for (int i = 0; i < entry.onSets.length; i++) {
                this.tmpSet.andNot(entry.onSets[i]);
            }

            this.tmpSet2.clear();

            for (int i = 1; i < this.finder.getPositions().length; i++) {
                this.tmpSet2.or(this.finder.getPositions()[i]);
            }

            this.tmpSet.andNot(this.tmpSet2);
            if (!this.tmpSet.isEmpty()) {
                for (int i = 0; i < this.tmpSet.size(); i++) {
                    this.globalStep.reset();
                    this.globalStep.setType(SolutionType.FORCING_CHAIN_CONTRADICTION);
                    if (entry.isStrong(0)) {
                        this.globalStep.addCandidateToDelete(entry.getCellIndex(0), entry.getCandidate(0));
                    } else {
                        this.globalStep.addIndex(entry.getCellIndex(0));
                        this.globalStep.addValue(entry.getCandidate(0));
                    }

                    this.globalStep.setEntity(3);
                    this.globalStep.setEntityNumber(this.tmpSet.get(i));
                    this.resetTmpChains();
                    int[] cands = this.sudoku.getAllCandidates(this.tmpSet.get(i));

                    for (int j = 0; j < cands.length; j++) {
                        this.addChain(entry, this.tmpSet.get(i), cands[j], false);
                    }

                    if (entry.isStrong(0)) {
                        this.replaceOrCopyStep();
                    } else {
                        this.replaceOrCopyStep();
                    }
                }
            }

            this.checkHouseDel(entry, Sudoku2.LINE_TEMPLATES, 1);
            this.checkHouseDel(entry, Sudoku2.COL_TEMPLATES, 2);
            this.checkHouseDel(entry, Sudoku2.BLOCK_TEMPLATES, 0);
        }
    }

    private void checkHouseDel(TableEntry entry, SudokuSet[] houseSets, int entityTyp) {
        for (int i = 1; i < entry.offSets.length; i++) {
            for (int j = 0; j < houseSets.length; j++) {
                this.tmpSet.set(houseSets[j]);
                this.tmpSet.and(this.finder.getCandidatesAllowed()[i]);
                if (!this.tmpSet.isEmpty() && this.tmpSet.andEquals(entry.offSets[i])) {
                    this.globalStep.reset();
                    this.globalStep.setType(SolutionType.FORCING_CHAIN_CONTRADICTION);
                    if (entry.isStrong(0)) {
                        this.globalStep.addCandidateToDelete(entry.getCellIndex(0), entry.getCandidate(0));
                    } else {
                        this.globalStep.addIndex(entry.getCellIndex(0));
                        this.globalStep.addValue(entry.getCandidate(0));
                    }

                    this.globalStep.setEntity(entityTyp);
                    this.globalStep.setEntityNumber(j);
                    this.resetTmpChains();

                    for (int k = 0; k < this.tmpSet.size(); k++) {
                        this.addChain(entry, this.tmpSet.get(k), i, false);
                    }

                    if (entry.isStrong(0)) {
                        this.replaceOrCopyStep();
                    } else {
                        this.replaceOrCopyStep();
                    }
                }
            }
        }
    }

    private void checkHouseSet(TableEntry entry, SudokuSet[] houseSets, int entityTyp) {
        for (int i = 1; i < entry.onSets.length; i++) {
            for (int j = 0; j < houseSets.length; j++) {
                this.tmpSet.setAnd(houseSets[j], entry.onSets[i]);
                if (this.tmpSet.size() > 1) {
                    this.globalStep.reset();
                    this.globalStep.setType(SolutionType.FORCING_CHAIN_CONTRADICTION);
                    if (entry.isStrong(0)) {
                        this.globalStep.addCandidateToDelete(entry.getCellIndex(0), entry.getCandidate(0));
                    } else {
                        this.globalStep.addIndex(entry.getCellIndex(0));
                        this.globalStep.addValue(entry.getCandidate(0));
                    }

                    this.globalStep.setEntity(entityTyp);
                    this.globalStep.setEntityNumber(j);
                    this.resetTmpChains();

                    for (int k = 0; k < this.tmpSet.size(); k++) {
                        this.addChain(entry, this.tmpSet.get(k), i, true);
                    }

                    if (entry.isStrong(0)) {
                        this.replaceOrCopyStep();
                    } else {
                        this.replaceOrCopyStep();
                    }
                }
            }
        }
    }

    private void checkNiceLoops(TableEntry[] tables) {
        for (int i = 0; i < tables.length; i++) {
            int startIndex = tables[i].getCellIndex(0);

            for (int j = 1; j < tables[i].index; j++) {
                if (tables[i].getNodeType(j) == 0 && tables[i].getCellIndex(j) == startIndex) {
                    this.checkNiceLoop(tables[i], j);
                }
            }
        }
    }

    private void checkAics(TableEntry[] tables) {
        for (int i = 0; i < tables.length; i++) {
            int startIndex = tables[i].getCellIndex(0);
            int startCandidate = tables[i].getCandidate(0);
            SudokuSetBase buddies = Sudoku2.buddies[startIndex];

            for (int j = 1; j < tables[i].index; j++) {
                if (tables[i].getNodeType(j) == 0 && tables[i].isStrong(j) && tables[i].getCellIndex(j) != startIndex) {
                    if (startCandidate == tables[i].getCandidate(j)) {
                        this.tmpSet.set(buddies);
                        this.tmpSet.and(Sudoku2.buddies[tables[i].getCellIndex(j)]);
                        this.tmpSet.and(this.finder.getCandidates()[startCandidate]);
                        if (!this.tmpSet.isEmpty() && this.tmpSet.size() >= 2) {
                            this.checkAic(tables[i], j);
                        }
                    } else if (buddies.contains(tables[i].getCellIndex(j))
                            && this.sudoku.isCandidate(tables[i].getCellIndex(j), startCandidate)
                            && this.sudoku.isCandidate(startIndex, tables[i].getCandidate(j))) {
                        this.checkAic(tables[i], j);
                    }
                }
            }
        }
    }

    private void checkNiceLoop(TableEntry entry, int entryIndex) {
        if (entry.getDistance(entryIndex) > 2) {
            this.globalStep.reset();
            this.globalStep.setType(SolutionType.DISCONTINUOUS_NICE_LOOP);
            this.resetTmpChains();
            this.addChain(entry, entry.getCellIndex(entryIndex), entry.getCandidate(entryIndex), entry.isStrong(entryIndex), true);
            if (!this.globalStep.getChains().isEmpty()) {
                Chain localTmpChain = this.globalStep.getChains().get(0);
                if (localTmpChain.getCellIndex(0) != localTmpChain.getCellIndex(1)) {
                    int[] nlChain = localTmpChain.getChain();
                    int nlChainIndex = localTmpChain.getEnd();
                    int nlChainLength = localTmpChain.getLength();
                    boolean firstLinkStrong = entry.isStrong(1);
                    boolean lastLinkStrong = entry.isStrong(entryIndex);
                    int startCandidate = entry.getCandidate(0);
                    int endCandidate = entry.getCandidate(entryIndex);
                    int startIndex = entry.getCellIndex(0);
                    if (!firstLinkStrong && !lastLinkStrong && startCandidate == endCandidate) {
                        this.globalStep.addCandidateToDelete(startIndex, startCandidate);
                    } else if (firstLinkStrong && lastLinkStrong && startCandidate == endCandidate) {
                        int[] cands = this.sudoku.getAllCandidates(startIndex);

                        for (int i = 0; i < cands.length; i++) {
                            if (cands[i] != startCandidate) {
                                this.globalStep.addCandidateToDelete(startIndex, cands[i]);
                            }
                        }
                    } else if (firstLinkStrong != lastLinkStrong && startCandidate != endCandidate) {
                        if (!firstLinkStrong) {
                            this.globalStep.addCandidateToDelete(startIndex, startCandidate);
                        } else {
                            this.globalStep.addCandidateToDelete(startIndex, endCandidate);
                        }
                    } else if (!firstLinkStrong && !lastLinkStrong && this.sudoku.getAnzCandidates(startIndex) == 2 && startCandidate != endCandidate
                            || firstLinkStrong && lastLinkStrong && startCandidate != endCandidate
                            || firstLinkStrong != lastLinkStrong && startCandidate == endCandidate) {
                        this.globalStep.setType(SolutionType.CONTINUOUS_NICE_LOOP);

                        for (int i = 0; i <= nlChainIndex; i++) {
                            if ((
                                    i == 0 && firstLinkStrong && lastLinkStrong
                                            || i > 0
                                            && Chain.isSStrong(nlChain[i])
                                            && i <= nlChainIndex - 2
                                            && Chain.getSCellIndex(nlChain[i - 1]) != Chain.getSCellIndex(nlChain[i])
                            )
                                    && (
                                    i == 0
                                            || !Chain.isSStrong(nlChain[i + 1])
                                            && Chain.getSCellIndex(nlChain[i]) == Chain.getSCellIndex(nlChain[i + 1])
                                            && Chain.isSStrong(nlChain[i + 2])
                                            && Chain.getSCellIndex(nlChain[i + 1]) != Chain.getSCellIndex(nlChain[i + 2])
                            )) {
                                int c1 = Chain.getSCandidate(nlChain[i]);
                                int c2 = Chain.getSCandidate(nlChain[i + 2]);
                                if (i == 0) {
                                    c1 = startCandidate;
                                    c2 = endCandidate;
                                }

                                int[] cands = this.sudoku.getAllCandidates(Chain.getSCellIndex(nlChain[i]));

                                for (int j = 0; j < cands.length; j++) {
                                    if (cands[j] != c1 && cands[j] != c2) {
                                        this.globalStep.addCandidateToDelete(Chain.getSCellIndex(nlChain[i]), cands[j]);
                                    }
                                }
                            }

                            if (i > 0 && !Chain.isSStrong(nlChain[i]) && Chain.getSCellIndex(nlChain[i - 1]) != Chain.getSCellIndex(nlChain[i])) {
                                int actCand = Chain.getSCandidate(nlChain[i]);
                                Chain.getSNodeBuddies(nlChain[i - 1], actCand, this.alses, this.tmpSet);
                                Chain.getSNodeBuddies(nlChain[i], actCand, this.alses, this.tmpSet1);
                                this.tmpSet.and(this.tmpSet1);
                                this.tmpSet.andNot(this.tmpSetC);
                                this.tmpSet.remove(startIndex);
                                this.tmpSet.and(this.finder.getCandidates()[actCand]);
                                if (!this.tmpSet.isEmpty()) {
                                    for (int j = 0; j < this.tmpSet.size(); j++) {
                                        this.globalStep.addCandidateToDelete(this.tmpSet.get(j), actCand);
                                    }
                                }

                                if (Chain.getSNodeType(nlChain[i]) == 2) {
                                    boolean isForceExit = i < nlChainIndex && Chain.isSStrong(nlChain[i + 1]);
                                    int nextCellIndex = Chain.getSCellIndex(nlChain[i + 1]);
                                    this.tmpSet2.clear();
                                    if (isForceExit) {
                                        int forceCand = Chain.getSCandidate(nlChain[i + 1]);
                                        this.sudoku.getCandidateSet(nextCellIndex, this.tmpSet2);
                                        this.tmpSet2.remove(forceCand);
                                    } else if (i < nlChainIndex) {
                                        this.tmpSet2.add(Chain.getSCandidate(nlChain[i + 1]));
                                    }

                                    Als als = this.alses.get(Chain.getSAlsIndex(nlChain[i]));

                                    for (int j = 1; j < als.buddiesPerCandidat.length; j++) {
                                        if (j != actCand && !this.tmpSet2.contains(j) && als.buddiesPerCandidat[j] != null) {
                                            this.tmpSet.set(als.buddiesPerCandidat[j]);
                                            this.tmpSet.and(this.finder.getCandidates()[j]);
                                            if (!this.tmpSet.isEmpty()) {
                                                for (int k = 0; k < this.tmpSet.size(); k++) {
                                                    this.globalStep.addCandidateToDelete(this.tmpSet.get(k), j);
                                                }
                                            }
                                        }
                                    }

                                    if (isForceExit) {
                                        this.tmpSet1.set(Sudoku2.buddies[nextCellIndex]);

                                        for (int j = 0; j < this.tmpSet2.size(); j++) {
                                            int actExitCand = this.tmpSet2.get(j);
                                            this.tmpSet.set(als.buddiesPerCandidat[actExitCand]);
                                            this.tmpSet.and(this.tmpSet1);
                                            this.tmpSet.and(this.finder.getCandidates()[actExitCand]);
                                            if (!this.tmpSet.isEmpty()) {
                                                for (int k = 0; k < this.tmpSet.size(); k++) {
                                                    this.globalStep.addCandidateToDelete(this.tmpSet.get(k), actExitCand);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (this.globalStep.getCandidatesToDelete().size() > 0) {
                        boolean grouped = false;
                        Chain newChain = this.globalStep.getChains().get(0);

                        for (int i = newChain.getStart(); i <= newChain.getEnd(); i++) {
                            if (Chain.getSNodeType(newChain.getChain()[i]) != 0) {
                                grouped = true;
                                break;
                            }
                        }

                        if (grouped) {
                            if (this.globalStep.getType() == SolutionType.DISCONTINUOUS_NICE_LOOP) {
                                this.globalStep.setType(SolutionType.GROUPED_DISCONTINUOUS_NICE_LOOP);
                            }

                            if (this.globalStep.getType() == SolutionType.CONTINUOUS_NICE_LOOP) {
                                this.globalStep.setType(SolutionType.GROUPED_CONTINUOUS_NICE_LOOP);
                            }

                            if (this.globalStep.getType() == SolutionType.AIC) {
                                this.globalStep.setType(SolutionType.GROUPED_AIC);
                            }
                        }

                        if (this.onlyGroupedNiceLoops && !grouped) {
                            return;
                        }

                        String del = this.globalStep.getCandidateString();
                        Integer oldIndex = this.deletesMap.get(del);
                        if (oldIndex != null && this.steps.get(oldIndex).getChainLength() <= nlChainLength) {
                            return;
                        }

                        this.deletesMap.put(del, this.steps.size());
                        newChain = (Chain) this.globalStep.getChains().get(0).clone();
                        this.globalStep.getChains().clear();
                        this.globalStep.getChains().add(newChain);
                        this.adjustChains(this.globalStep);
                        this.steps.add((SolutionStep) this.globalStep.clone());
                    }
                }
            }
        }
    }

    private void checkAic(TableEntry entry, int entryIndex) {
        if (entry.getDistance(entryIndex) > 2) {
            this.globalStep.reset();
            this.globalStep.setType(SolutionType.AIC);
            int startCandidate = entry.getCandidate(0);
            int endCandidate = entry.getCandidate(entryIndex);
            int startIndex = entry.getCellIndex(0);
            int endIndex = entry.getCellIndex(entryIndex);
            if (startCandidate == endCandidate) {
                this.tmpSet.set(Sudoku2.buddies[startIndex]);
                this.tmpSet.and(Sudoku2.buddies[endIndex]);
                this.tmpSet.and(this.finder.getCandidates()[startCandidate]);
                if (this.tmpSet.size() > 1) {
                    for (int i = 0; i < this.tmpSet.size(); i++) {
                        if (this.tmpSet.get(i) != startIndex) {
                            this.globalStep.addCandidateToDelete(this.tmpSet.get(i), startCandidate);
                        }
                    }
                }
            } else {
                if (this.sudoku.isCandidate(startIndex, endCandidate)) {
                    this.globalStep.addCandidateToDelete(startIndex, endCandidate);
                }

                if (this.sudoku.isCandidate(endIndex, startCandidate)) {
                    this.globalStep.addCandidateToDelete(endIndex, startCandidate);
                }
            }

            if (this.globalStep.getAnzCandidatesToDelete() != 0) {
                this.resetTmpChains();
                this.addChain(entry, entry.getCellIndex(entryIndex), entry.getCandidate(entryIndex), entry.isStrong(entryIndex), false, true);
                if (!this.globalStep.getChains().isEmpty()) {
                    boolean grouped = false;
                    Chain newChain = this.globalStep.getChains().get(0);

                    for (int i = newChain.getStart(); i <= newChain.getEnd(); i++) {
                        if (Chain.getSNodeType(newChain.getChain()[i]) != 0) {
                            grouped = true;
                            break;
                        }
                    }

                    if (grouped) {
                        if (this.globalStep.getType() == SolutionType.DISCONTINUOUS_NICE_LOOP) {
                            this.globalStep.setType(SolutionType.GROUPED_DISCONTINUOUS_NICE_LOOP);
                        }

                        if (this.globalStep.getType() == SolutionType.CONTINUOUS_NICE_LOOP) {
                            this.globalStep.setType(SolutionType.GROUPED_CONTINUOUS_NICE_LOOP);
                        }

                        if (this.globalStep.getType() == SolutionType.AIC) {
                            this.globalStep.setType(SolutionType.GROUPED_AIC);
                        }
                    }

                    if (!this.onlyGroupedNiceLoops || grouped) {
                        String del = this.globalStep.getCandidateString();
                        Integer oldIndex = this.deletesMap.get(del);
                        if (oldIndex == null || this.steps.get(oldIndex).getChainLength() > this.globalStep.getChains().get(0).getLength()) {
                            this.deletesMap.put(del, this.steps.size());
                            newChain = (Chain) this.globalStep.getChains().get(0).clone();
                            this.globalStep.getChains().clear();
                            this.globalStep.getChains().add(newChain);
                            this.adjustChains(this.globalStep);
                            this.steps.add((SolutionStep) this.globalStep.clone());
                        }
                    }
                }
            }
        }
    }

    private void fillTables() {
        for (int i = 0; i < this.onTable.length; i++) {
            this.onTable[i].reset();
            this.offTable[i].reset();
        }

        this.extendedTableMap.clear();
        this.extendedTableIndex = 0;
        if (this.chainsOnly) {
            for (int i = 0; i < this.sudoku.getCells().length; i++) {
                if (this.sudoku.getValue(i) == 0) {
                    for (int j = 1; j <= 9; j++) {
                        if (this.sudoku.isCandidate(i, j)) {
                            int cand = j;
                            this.onTable[i * 10 + cand].addEntry(i, cand, true);
                            this.offTable[i * 10 + cand].addEntry(i, cand, false);
                            int[] cands = this.sudoku.getAllCandidates(i);

                            for (int k = 0; k < cands.length; k++) {
                                int otherCand = cands[k];
                                if (otherCand != cand) {
                                    this.onTable[i * 10 + cand].addEntry(i, otherCand, false);
                                    if (cands.length == 2) {
                                        this.offTable[i * 10 + cand].addEntry(i, otherCand, true);
                                    }
                                }
                            }

                            this.tmpSet1.set(this.finder.getCandidates()[cand]);
                            this.tmpSet1.remove(i);

                            for (int constrIndex = 0; constrIndex < Sudoku2.CONSTRAINTS[i].length; constrIndex++) {
                                int constr = Sudoku2.CONSTRAINTS[i][constrIndex];
                                int anzCands = this.sudoku.getFree()[constr][cand];
                                if (anzCands >= 2) {
                                    this.tmpSet.set(this.tmpSet1);
                                    this.tmpSet.and(Sudoku2.ALL_CONSTRAINTS_TEMPLATES[constr]);
                                    if (!this.tmpSet.isEmpty()) {
                                        for (int k = 0; k < this.tmpSet.size(); k++) {
                                            this.onTable[i * 10 + cand].addEntry(this.tmpSet.get(k), cand, false);
                                        }

                                        if (anzCands == 2) {
                                            this.offTable[i * 10 + cand].addEntry(this.tmpSet.get(0), cand, true);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            this.savedSudoku = this.sudoku.clone();
            this.simpleFinder.setSudoku(this.savedSudoku);

            for (int i = 0; i < this.savedSudoku.getCells().length; i++) {
                if (this.savedSudoku.getValue(i) == 0) {
                    int[] cands = this.savedSudoku.getAllCandidates(i);

                    for (int j = 0; j < cands.length; j++) {
                        int cand = cands[j];
                        this.sudoku.set(this.savedSudoku);
                        this.simpleFinder.setSudoku(this.sudoku);
                        this.getTableEntry(this.onTable[i * 10 + cand], i, cand, true);
                        this.sudoku.set(this.savedSudoku);
                        this.simpleFinder.setSudoku(this.sudoku);
                        this.getTableEntry(this.offTable[i * 10 + cand], i, cand, false);
                    }
                }
            }

            this.sudoku.set(this.savedSudoku);
        }
    }

    private void fillTablesWithGroupNodes() {
        this.groupNodes = GroupNode.getGroupNodes(this.finder);

        for (int i = 0; i < this.groupNodes.size(); i++) {
            GroupNode gn = this.groupNodes.get(i);
            TableEntry onEntry = this.getNextExtendedTableEntry(this.extendedTableIndex);
            onEntry.addEntry(gn.index1, gn.index2, gn.index3, 1, gn.cand, true, 0, 0, 0, 0, 0, 0);
            this.extendedTableMap.put(onEntry.entries[0], this.extendedTableIndex);
            this.extendedTableIndex++;
            TableEntry offEntry = this.getNextExtendedTableEntry(this.extendedTableIndex);
            offEntry.addEntry(gn.index1, gn.index2, gn.index3, 1, gn.cand, false, 0, 0, 0, 0, 0, 0);
            this.extendedTableMap.put(offEntry.entries[0], this.extendedTableIndex);
            this.extendedTableIndex++;
            this.tmpSet.set(this.finder.getCandidates()[gn.cand]);
            this.tmpSet.and(gn.buddies);
            if (!this.tmpSet.isEmpty()) {
                for (int j = 0; j < this.tmpSet.size(); j++) {
                    int index = this.tmpSet.get(j);
                    onEntry.addEntry(index, gn.cand, false);
                    TableEntry tmp = this.onTable[index * 10 + gn.cand];
                    tmp.addEntry(gn.index1, gn.index2, gn.index3, 1, gn.cand, false, 0, 0, 0, 0, 0, 0);
                }

                this.tmpSet1.set(this.tmpSet);
                this.tmpSet1.and(Sudoku2.BLOCK_TEMPLATES[gn.block]);
                if (!this.tmpSet1.isEmpty() && this.tmpSet1.size() == 1) {
                    offEntry.addEntry(this.tmpSet1.get(0), gn.cand, true);
                    TableEntry tmp = this.offTable[this.tmpSet1.get(0) * 10 + gn.cand];
                    tmp.addEntry(gn.index1, gn.index2, gn.index3, 1, gn.cand, true, 0, 0, 0, 0, 0, 0);
                }

                this.tmpSet1.set(this.tmpSet);
                if (gn.line != -1) {
                    this.tmpSet1.and(Sudoku2.LINE_TEMPLATES[gn.line]);
                } else {
                    this.tmpSet1.and(Sudoku2.COL_TEMPLATES[gn.col]);
                }

                if (!this.tmpSet1.isEmpty() && this.tmpSet1.size() == 1) {
                    offEntry.addEntry(this.tmpSet1.get(0), gn.cand, true);
                    TableEntry tmp = this.offTable[this.tmpSet1.get(0) * 10 + gn.cand];
                    tmp.addEntry(gn.index1, gn.index2, gn.index3, 1, gn.cand, true, 0, 0, 0, 0, 0, 0);
                }
            }

            int lineAnz = 0;
            int line1Index = -1;
            int colAnz = 0;
            int col1Index = -1;
            int blockAnz = 0;
            int block1Index = -1;
            GroupNode gn2 = null;

            for (int j = 0; j < this.groupNodes.size(); j++) {
                gn2 = this.groupNodes.get(j);
                if (j != i && gn.cand == gn2.cand) {
                    this.tmpSet2.set(gn.indices);
                    if (this.tmpSet2.andEmpty(gn2.indices)) {
                        if (gn.line != -1 && gn.line == gn2.line) {
                            if (++lineAnz == 1) {
                                line1Index = j;
                            }

                            onEntry.addEntry(gn2.index1, gn2.index2, gn2.index3, 1, gn.cand, false, 0, 0, 0, 0, 0, 0);
                        }

                        if (gn.col != -1 && gn.col == gn2.col) {
                            if (++colAnz == 1) {
                                col1Index = j;
                            }

                            onEntry.addEntry(gn2.index1, gn2.index2, gn2.index3, 1, gn.cand, false, 0, 0, 0, 0, 0, 0);
                        }

                        if (gn.block == gn2.block) {
                            if (++blockAnz == 1) {
                                block1Index = j;
                            }

                            onEntry.addEntry(gn2.index1, gn2.index2, gn2.index3, 1, gn.cand, false, 0, 0, 0, 0, 0, 0);
                        }
                    }
                }
            }

            if (lineAnz == 1) {
                gn2 = this.groupNodes.get(line1Index);
                this.tmpSet.set(Sudoku2.LINE_TEMPLATES[gn.line]);
                this.tmpSet.and(this.finder.getCandidates()[gn.cand]);
                this.tmpSet.andNot(gn.indices);
                this.tmpSet.andNot(gn2.indices);
                if (this.tmpSet.isEmpty()) {
                    offEntry.addEntry(gn2.index1, gn2.index2, gn2.index3, 1, gn.cand, true, 0, 0, 0, 0, 0, 0);
                }
            }

            if (colAnz == 1) {
                gn2 = this.groupNodes.get(col1Index);
                this.tmpSet.set(Sudoku2.COL_TEMPLATES[gn.col]);
                this.tmpSet.and(this.finder.getCandidates()[gn.cand]);
                this.tmpSet.andNot(gn.indices);
                this.tmpSet.andNot(gn2.indices);
                if (this.tmpSet.isEmpty()) {
                    offEntry.addEntry(gn2.index1, gn2.index2, gn2.index3, 1, gn.cand, true, 0, 0, 0, 0, 0, 0);
                }
            }

            if (blockAnz == 1) {
                gn2 = this.groupNodes.get(block1Index);
                this.tmpSet.set(Sudoku2.BLOCK_TEMPLATES[gn.block]);
                this.tmpSet.and(this.finder.getCandidates()[gn.cand]);
                this.tmpSet.andNot(gn.indices);
                this.tmpSet.andNot(gn2.indices);
                if (this.tmpSet.isEmpty()) {
                    offEntry.addEntry(gn2.index1, gn2.index2, gn2.index3, 1, gn.cand, true, 0, 0, 0, 0, 0, 0);
                }
            }
        }
    }

    private void fillTablesWithAls() {
        this.alses = this.finder.getAlses(true);

        for (int i = 0; i < this.alses.size(); i++) {
            Als als = this.alses.get(i);
            if (als.indices.size() != 1) {
                for (int j = 1; j <= 9; j++) {
                    if (als.indicesPerCandidat[j] != null && !als.indicesPerCandidat[j].isEmpty()) {
                        boolean eliminationsPresent = false;

                        for (int k = 1; k <= 9; k++) {
                            this.alsEliminations[k].clear();
                            if (k != j && als.indicesPerCandidat[k] != null) {
                                this.alsEliminations[k].set(this.finder.getCandidates()[k]);
                                this.alsEliminations[k].and(als.buddiesPerCandidat[k]);
                                if (!this.alsEliminations[k].isEmpty()) {
                                    eliminationsPresent = true;
                                }
                            }
                        }

                        if (eliminationsPresent) {
                            int entryIndex = als.indicesPerCandidat[j].get(0);
                            TableEntry offEntry = null;
                            if ((offEntry = this.getAlsTableEntry(entryIndex, i, j)) == null) {
                                offEntry = this.getNextExtendedTableEntry(this.extendedTableIndex);
                                offEntry.addEntry(entryIndex, i, 2, j, false, 0);
                                this.extendedTableMap.put(offEntry.entries[0], this.extendedTableIndex);
                                this.extendedTableIndex++;
                            }

                            this.tmpSet.set(this.finder.getCandidates()[j]);
                            this.tmpSet.and(als.buddiesPerCandidat[j]);
                            int alsEntry = Chain.makeSEntry(entryIndex, i, j, false, 2);

                            for (int k = 0; k < this.tmpSet.size(); k++) {
                                int actIndex = this.tmpSet.get(k);
                                TableEntry tmp = this.onTable[actIndex * 10 + j];
                                tmp.addEntry(entryIndex, i, 2, j, false, 0);

                                for (int l = 0; l < this.groupNodes.size(); l++) {
                                    GroupNode gAct = this.groupNodes.get(l);
                                    if (gAct.cand == j && gAct.indices.contains(actIndex)) {
                                        this.tmpSet1.set(als.indices);
                                        if (this.tmpSet1.andEmpty(gAct.indices)) {
                                            this.tmpSet1.set(als.indicesPerCandidat[j]);
                                            if (this.tmpSet1.andEquals(gAct.buddies)) {
                                                int entry = Chain.makeSEntry(gAct.index1, gAct.index2, gAct.index3, j, true, 1);
                                                TableEntry gTmp = this.extendedTable.get(this.extendedTableMap.get(entry));
                                                if (!gTmp.indices.containsKey(alsEntry)) {
                                                    gTmp.addEntry(entryIndex, i, 2, j, false, 0);
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            for (int k = 1; k <= 9; k++) {
                                if (!this.alsEliminations[k].isEmpty()) {
                                    for (int l = 0; l < this.alsEliminations[k].size(); l++) {
                                        offEntry.addEntry(this.alsEliminations[k].get(l), k, als.getChainPenalty(), false);
                                    }

                                    for (int l = 0; l < this.groupNodes.size(); l++) {
                                        GroupNode gAct = this.groupNodes.get(l);
                                        if (gAct.cand == k) {
                                            this.tmpSet1.set(gAct.indices);
                                            if (this.tmpSet1.andEquals(this.alsEliminations[k])) {
                                                offEntry.addEntry(gAct.index1, gAct.index2, gAct.index3, 1, k, false, 0, 0, 0, 0, 0, als.getChainPenalty());
                                            }
                                        }
                                    }
                                }
                            }

                            for (int k = 0; k < this.alses.size(); k++) {
                                if (k != i) {
                                    Als tmpAls = this.alses.get(k);
                                    this.tmpSet1.set(als.indices);
                                    if (this.tmpSet1.andEmpty(tmpAls.indices)) {
                                        for (int l = 1; l <= 9; l++) {
                                            if (this.alsEliminations[l] != null
                                                    && !this.alsEliminations[l].isEmpty()
                                                    && tmpAls.indicesPerCandidat[l] != null
                                                    && !tmpAls.indicesPerCandidat[l].isEmpty()) {
                                                this.tmpSet1.set(this.alsEliminations[l]);
                                                if (this.tmpSet1.contains(tmpAls.indicesPerCandidat[l])) {
                                                    int tmpAlsIndex = tmpAls.indicesPerCandidat[l].get(0);
                                                    if (this.getAlsTableEntry(tmpAlsIndex, k, l) == null) {
                                                        TableEntry tmpAlsEntry = this.getNextExtendedTableEntry(this.extendedTableIndex);
                                                        tmpAlsEntry.addEntry(tmpAlsIndex, k, 2, l, false, 0);
                                                        this.extendedTableMap.put(tmpAlsEntry.entries[0], this.extendedTableIndex);
                                                        this.extendedTableIndex++;
                                                    }

                                                    offEntry.addEntry(tmpAlsIndex, k, 2, l, false, als.getChainPenalty());
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            for (int k = 0; k < als.buddies.size(); k++) {
                                int cellIndex = als.buddies.get(k);
                                if (this.sudoku.getValue(cellIndex) == 0 && this.sudoku.getAnzCandidates(cellIndex) != 2) {
                                    this.sudoku.getCandidateSet(cellIndex, this.tmpSet1);

                                    for (int l = 1; l <= 9; l++) {
                                        if (this.alsEliminations[l] != null && this.alsEliminations[l].contains(cellIndex)) {
                                            this.tmpSet1.remove(l);
                                        }
                                    }

                                    if (this.tmpSet1.size() == 1) {
                                        offEntry.addEntry(cellIndex, this.tmpSet1.get(0), als.getChainPenalty() + 1, true);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private TableEntry getAlsTableEntry(int entryCellIndex, int alsIndex, int cand) {
        int entry = Chain.makeSEntry(entryCellIndex, alsIndex, cand, false, 2);
        return this.extendedTableMap.containsKey(entry) ? this.extendedTable.get(this.extendedTableMap.get(entry)) : null;
    }

    private TableEntry getNextExtendedTableEntry(int tableIndex) {
        TableEntry entry = null;
        if (tableIndex >= this.extendedTable.size()) {
            entry = new TableEntry();
            this.extendedTable.add(entry);
        } else {
            entry = this.extendedTable.get(tableIndex);
            entry.reset();
        }

        return entry;
    }

    private void getTableEntry(TableEntry entry, int cellIndex, int cand, boolean set) {
        if (set) {
            this.setCell(cellIndex, cand, entry, false, false);
        } else {
            this.sudoku.delCandidate(cellIndex, cand);
            entry.addEntry(cellIndex, cand, false, 0);
            if (this.sudoku.getAnzCandidates(cellIndex) == 1) {
                int setCand = this.sudoku.getAllCandidates(cellIndex)[0];
                this.setCell(cellIndex, setCand, entry, false, true);
            }
        }

        for (int j = 0; j < Options.getInstance().getAnzTableLookAhead(); j++) {
            this.singleSteps.clear();
            List<SolutionStep> dummyList = this.simpleFinder.findAllNakedSingles(this.sudoku);
            this.singleSteps.addAll(dummyList);
            dummyList = this.simpleFinder.findAllHiddenSingles(this.sudoku);
            this.singleSteps.addAll(dummyList);

            for (int i = 0; i < this.singleSteps.size(); i++) {
                SolutionStep step = this.singleSteps.get(i);
                int index = step.getIndices().get(0);
                this.setCell(index, step.getValues().get(0), entry, true, step.getType() == SolutionType.NAKED_SINGLE);
            }
        }
    }

    private void setCell(int cellIndex, int cand, TableEntry entry, boolean getRetIndices, boolean nakedSingle) {
        this.tmpSet.set(this.finder.getCandidates()[cand]);
        this.tmpSet.remove(cellIndex);
        this.tmpSet.and(Sudoku2.buddies[cellIndex]);
        int[] cands = this.sudoku.getAllCandidates(cellIndex);
        int entityType = 1;
        int entityNumberFree = this.sudoku.getFree()[Sudoku2.CONSTRAINTS[cellIndex][0]][cand];
        int dummy = this.sudoku.getFree()[Sudoku2.CONSTRAINTS[cellIndex][1]][cand];
        if (dummy < entityNumberFree) {
            entityType = 2;
            entityNumberFree = dummy;
        }

        int var14 = this.sudoku.getFree()[Sudoku2.CONSTRAINTS[cellIndex][2]][cand];
        if (var14 < entityNumberFree) {
            entityType = 0;
        }

        this.sudoku.setCell(cellIndex, cand);
        int retIndex = entry.index;
        if (getRetIndices) {
            for (int i = 0; i < this.retIndices[0].length; i++) {
                this.retIndices[0][i] = 0;
            }

            if (nakedSingle) {
                int[] cellCands = this.savedSudoku.getAllCandidates(cellIndex);
                if (cellCands.length > this.retIndices[0].length + 1) {
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Too many candidates (setCell() - Naked Single");
                }

                int ri = 0;

                for (int i = 0; i < cellCands.length && ri < this.retIndices[0].length; i++) {
                    if (cellCands[i] != cand) {
                        this.retIndices[0][ri++] = entry.getEntryIndex(cellIndex, false, cellCands[i]);
                    }
                }
            } else if (entityType == 1) {
                this.getRetIndicesForHouse(cellIndex, cand, Sudoku2.LINE_TEMPLATES[Sudoku2.getLine(cellIndex)], entry);
            } else if (entityType == 2) {
                this.getRetIndicesForHouse(cellIndex, cand, Sudoku2.COL_TEMPLATES[Sudoku2.getCol(cellIndex)], entry);
            } else {
                this.getRetIndicesForHouse(cellIndex, cand, Sudoku2.BLOCK_TEMPLATES[Sudoku2.getBlock(cellIndex)], entry);
            }

            entry.addEntry(
                    cellIndex, cand, true, this.retIndices[0][0], this.retIndices[0][1], this.retIndices[0][2], this.retIndices[0][3], this.retIndices[0][4]
            );
        } else {
            entry.addEntry(cellIndex, cand, true);
        }

        for (int i = 0; i < this.tmpSet.size(); i++) {
            entry.addEntry(this.tmpSet.get(i), cand, false, retIndex);
        }

        for (int i = 0; i < cands.length; i++) {
            if (cands[i] != cand) {
                entry.addEntry(cellIndex, cands[i], false, retIndex);
            }
        }
    }

    private void getRetIndicesForHouse(int cellIndex, int cand, SudokuSet houseSet, TableEntry entry) {
        this.tmpSet1.set(this.finder.getCandidates()[cand]);
        this.tmpSet1.remove(cellIndex);
        this.tmpSet1.and(houseSet);
        if (this.tmpSet1.size() > this.retIndices[0].length + 1) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Too many candidates (setCell() - Hidden Single");
        }

        int ri = 0;

        for (int i = 0; i < this.tmpSet1.size() && ri < this.retIndices[0].length; i++) {
            this.retIndices[0][ri++] = entry.getEntryIndex(this.tmpSet1.get(i), false, cand);
        }
    }

    private void expandTables(TableEntry[] table) {
        for (int i = 0; i < table.length; i++) {
            if (table[i].index != 0) {
                TableEntry dest = table[i];
                boolean isFromOnTable = false;
                boolean isFromExtendedTable = false;

                for (int j = 1; j < dest.entries.length && dest.entries[j] != 0; j++) {
                    if (dest.isFull()) {
                        Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "TableEntry full!");
                        break;
                    }

                    TableEntry src = null;
                    int srcTableIndex = dest.getCellIndex(j) * 10 + dest.getCandidate(j);
                    isFromExtendedTable = false;
                    isFromOnTable = false;
                    if (Chain.getSNodeType(dest.entries[j]) != 0) {
                        Integer tmpSI = this.extendedTableMap.get(dest.entries[j]);
                        if (tmpSI == null) {
                            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Table for {0} not found!", this.printTableEntry(dest.entries[j]));
                            continue;
                        }

                        srcTableIndex = tmpSI;
                        src = this.extendedTable.get(srcTableIndex);
                        isFromExtendedTable = true;
                    } else {
                        if (dest.isStrong(j)) {
                            src = this.onTable[srcTableIndex];
                        } else {
                            src = this.offTable[srcTableIndex];
                        }

                        isFromOnTable = dest.isStrong(j);
                    }

                    if (src.index == 0) {
                        StringBuilder tmpBuffer = new StringBuilder();
                        tmpBuffer.append("TableEntry for ").append(dest.entries[j]).append(" not found!\r\n");
                        tmpBuffer.append("i == ").append(i).append(", j == ").append(j).append(", dest.entries[j] == ").append(dest.entries[j]).append(": ");
                        tmpBuffer.append(this.printTableEntry(dest.entries[j]));
                        Logger.getLogger(this.getClass().getName()).log(Level.WARNING, tmpBuffer.toString());
                    } else {
                        int srcBaseDistance = dest.getDistance(j);

                        for (int k = 1; k < src.index; k++) {
                            if (!src.isExpanded(k)) {
                                int srcDistance = src.getDistance(k);
                                if (dest.indices.containsKey(src.entries[k])) {
                                    int orgIndex = dest.getEntryIndex(src.entries[k]);
                                    if (dest.isExpanded(orgIndex)
                                            && (
                                            dest.getDistance(orgIndex) > srcBaseDistance + srcDistance
                                                    || dest.getDistance(orgIndex) == srcBaseDistance + srcDistance && dest.getNodeType(orgIndex) > src.getNodeType(k)
                                    )) {
                                        dest.retIndices[orgIndex] = TableEntry.makeSRetIndex(srcTableIndex, 0L, 0L, 0L, 0L);
                                        dest.setExpanded(orgIndex);
                                        if (isFromExtendedTable) {
                                            dest.setExtendedTable(orgIndex);
                                        } else if (isFromOnTable) {
                                            dest.setOnTable(orgIndex);
                                        }

                                        dest.setDistance(orgIndex, srcBaseDistance + srcDistance);
                                    }
                                } else {
                                    int srcCellIndex = src.getCellIndex(k);
                                    int srcCand = src.getCandidate(k);
                                    boolean srcStrong = src.isStrong(k);
                                    if (Chain.getSNodeType(src.entries[k]) == 0) {
                                        dest.addEntry(srcCellIndex, srcCand, srcStrong, srcTableIndex);
                                    } else {
                                        int tmp = src.entries[k];
                                        dest.addEntry(
                                                Chain.getSCellIndex(tmp),
                                                Chain.getSCellIndex2(tmp),
                                                Chain.getSCellIndex3(tmp),
                                                Chain.getSNodeType(tmp),
                                                srcCand,
                                                srcStrong,
                                                srcTableIndex,
                                                0,
                                                0,
                                                0,
                                                0,
                                                0
                                        );
                                    }

                                    dest.setExpanded(dest.index - 1);
                                    if (isFromExtendedTable) {
                                        dest.setExtendedTable(dest.index - 1);
                                    } else if (isFromOnTable) {
                                        dest.setOnTable(dest.index - 1);
                                    }

                                    dest.setDistance(dest.index - 1, srcBaseDistance + srcDistance);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void addChain(TableEntry entry, int cellIndex, int cand, boolean set) {
        this.addChain(entry, cellIndex, cand, set, false);
    }

    private void addChain(TableEntry entry, int cellIndex, int cand, boolean set, boolean isNiceLoop) {
        this.addChain(entry, cellIndex, cand, set, isNiceLoop, false);
    }

    private void addChain(TableEntry entry, int cellIndex, int cand, boolean set, boolean isNiceLoop, boolean isAic) {
        this.buildChain(entry, cellIndex, cand, set);
        int j = 0;
        if (isNiceLoop || isAic) {
            this.lassoSet.clear();
            if (isNiceLoop && Chain.getSCellIndex(this.chain[0]) == Chain.getSCellIndex(this.chain[1])) {
                return;
            }
        }

        int lastCellIndex = -1;
        int lastCellEntry = -1;
        int firstCellIndex = Chain.getSCellIndex(this.chain[this.chainIndex - 1]);

        for (int i = this.chainIndex - 1; i >= 0; i--) {
            int oldEntry = this.chain[i];
            int newCellIndex = Chain.getSCellIndex(oldEntry);
            if (isNiceLoop || isAic) {
                if (this.lassoSet.contains(newCellIndex)) {
                    return;
                }

                if (lastCellIndex != -1 && (lastCellIndex != firstCellIndex || isAic)) {
                    this.lassoSet.add(lastCellIndex);
                    if (Chain.getSNodeType(lastCellEntry) == 1) {
                        int tmp = Chain.getSCellIndex2(lastCellEntry);
                        if (tmp != -1) {
                            this.lassoSet.add(tmp);
                        }

                        tmp = Chain.getSCellIndex3(lastCellEntry);
                        if (tmp != -1) {
                            this.lassoSet.add(tmp);
                        }
                    } else if (Chain.getSNodeType(lastCellEntry) == 2) {
                        this.lassoSet.or(this.alses.get(Chain.getSAlsIndex(lastCellEntry)).indices);
                    }
                }
            }

            lastCellIndex = newCellIndex;
            lastCellEntry = oldEntry;
            this.tmpChain[j++] = oldEntry;

            for (int k = 0; k < this.actMin; k++) {
                if (this.mins[k][this.minIndexes[k] - 1] == oldEntry) {
                    for (int l = this.minIndexes[k] - 2; l >= 0; l--) {
                        this.tmpChain[j++] = -this.mins[k][l];
                    }

                    this.tmpChain[j++] = Integer.MIN_VALUE;
                }
            }
        }

        if (j > 0) {
            System.arraycopy(this.tmpChain, 0, this.tmpChains[this.tmpChainsIndex].getChain(), 0, j);
            this.tmpChains[this.tmpChainsIndex].setStart(0);
            this.tmpChains[this.tmpChainsIndex].setEnd(j - 1);
            this.tmpChains[this.tmpChainsIndex].resetLength();
            this.globalStep.addChain(this.tmpChains[this.tmpChainsIndex]);
            this.tmpChainsIndex++;
        }
    }

    private void buildChain(TableEntry entry, int cellIndex, int cand, boolean set) {
        this.chainIndex = 0;
        int chainEntry = Chain.makeSEntry(cellIndex, cand, set);
        int index = -1;

        for (int i = 0; i < entry.entries.length; i++) {
            if (entry.entries[i] == chainEntry) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            Logger.getLogger(this.getClass().getName())
                    .log(Level.WARNING, "No chain entry for {0}/{1}/{2}/{3}", new Object[]{cellIndex, SolutionStep.getCellPrint(cellIndex), cand, set});
        } else {
            this.actMin = 0;

            for (int i = 0; i < this.minIndexes.length; i++) {
                this.minIndexes[i] = 0;
            }

            this.tmpSetC.clear();
            this.chainIndex = this.buildChain(entry, index, this.chain, false, this.tmpSetC);

            for (int minIndex = 0; minIndex < this.actMin; minIndex++) {
                this.minIndexes[minIndex] = this.buildChain(entry, entry.getEntryIndex(this.mins[minIndex][0]), this.mins[minIndex], true, this.tmpSetC);
            }
        }
    }

    private int buildChain(TableEntry entry, int entryIndex, int[] actChain, boolean isMin, SudokuSet chainSet) {
        int actChainIndex = 0;
        actChain[actChainIndex++] = entry.entries[entryIndex];
        int firstEntryIndex = entryIndex;
        boolean expanded = false;
        TableEntry orgEntry = entry;

        while (firstEntryIndex != 0 && actChainIndex < actChain.length) {
            if (entry.isExpanded(firstEntryIndex)) {
                if (entry.isExtendedTable(firstEntryIndex)) {
                    entry = this.extendedTable.get(orgEntry.getRetIndex(firstEntryIndex, 0));
                } else if (entry.isOnTable(firstEntryIndex)) {
                    entry = this.onTable[orgEntry.getRetIndex(firstEntryIndex, 0)];
                } else {
                    entry = this.offTable[orgEntry.getRetIndex(firstEntryIndex, 0)];
                }

                expanded = true;
                firstEntryIndex = entry.getEntryIndex(orgEntry.entries[firstEntryIndex]);
            }

            int tmpEntryIndex = firstEntryIndex;

            for (int i = 0; i < 5; i++) {
                entryIndex = entry.getRetIndex(tmpEntryIndex, i);
                if (i == 0) {
                    firstEntryIndex = entryIndex;
                    actChain[actChainIndex++] = entry.entries[entryIndex];
                    if (!isMin) {
                        chainSet.add(entry.getCellIndex(entryIndex));
                        if (Chain.getSNodeType(entry.entries[entryIndex]) == 1) {
                            int tmp = Chain.getSCellIndex2(entry.entries[entryIndex]);
                            if (tmp != -1) {
                                chainSet.add(tmp);
                            }

                            tmp = Chain.getSCellIndex3(entry.entries[entryIndex]);
                            if (tmp != -1) {
                                chainSet.add(tmp);
                            }
                        } else if (Chain.getSNodeType(entry.entries[entryIndex]) == 2) {
                            if (Chain.getSAlsIndex(entry.entries[entryIndex]) == -1) {
                                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "INVALID ALS_NODE: {0}", Chain.toString(entry.entries[entryIndex]));
                            }

                            chainSet.or(this.alses.get(Chain.getSAlsIndex(entry.entries[entryIndex])).indices);
                        }
                    } else if (chainSet.contains(entry.getCellIndex(entryIndex))) {
                        for (int j = 0; j < this.chainIndex; j++) {
                            if (this.chain[j] == entry.entries[entryIndex]) {
                                return actChainIndex;
                            }
                        }
                    }
                } else if (entryIndex != 0 && !isMin) {
                    this.mins[this.actMin][0] = entry.entries[entryIndex];
                    this.minIndexes[this.actMin++] = 1;
                }
            }

            if (expanded && firstEntryIndex == 0) {
                int retEntry = entry.entries[0];
                entry = orgEntry;
                firstEntryIndex = entry.getEntryIndex(retEntry);
                expanded = false;
            }
        }

        return actChainIndex;
    }

    private void printTable(String title, TableEntry entry) {
        System.out.println(title + ": ");
        int anz = 0;
        StringBuilder tmp = new StringBuilder();

        for (int i = 0; i < entry.index; i++) {
            if (!entry.isStrong(i)) {
            }

            tmp.append(this.printTableEntry(entry.entries[i]));

            for (int j = 0; j < entry.getRetIndexAnz(i); j++) {
                int retIndex = entry.getRetIndex(i, j);
                tmp.append(" (");
                if (entry.isExpanded(i)) {
                    tmp.append("EX:").append(retIndex).append(":").append(entry.isExtendedTable(i)).append("/").append(entry.isOnTable(i)).append("/");
                } else {
                    tmp.append(retIndex).append("/").append(this.printTableEntry(entry.entries[retIndex])).append(")");
                }
            }

            tmp.append(" ");
            if (++anz % 5 == 0) {
                tmp.append("\r\n");
            }
        }

        System.out.println(tmp.toString());
    }

    private String printTableEntry(int entry) {
        int index = Chain.getSCellIndex(entry);
        int candidate = Chain.getSCandidate(entry);
        boolean set = Chain.isSStrong(entry);
        String cell = SolutionStep.getCellPrint(index, false);
        if (Chain.getSNodeType(entry) == 1) {
            cell = SolutionStep.getCompactCellPrint(index, Chain.getSCellIndex2(entry), Chain.getSCellIndex3(entry));
        } else if (Chain.getSNodeType(entry) == 2) {
            cell = "ALS:" + SolutionStep.getAls(this.alses.get(Chain.getSAlsIndex(entry)));
        }

        return set ? cell + "=" + candidate : cell + "<>" + candidate;
    }

    public void printTableAnz() {
        if (DEBUG) {
            int onAnz = 0;
            int offAnz = 0;
            int entryAnz = 0;
            int maxEntryAnz = 0;

            for (int i = 0; i < this.onTable.length; i++) {
                if (this.onTable[i] != null) {
                    onAnz++;
                    entryAnz += this.onTable[i].index;
                    if (this.onTable[i].index > maxEntryAnz) {
                        maxEntryAnz = this.onTable[i].index;
                    }
                }

                if (this.offTable[i] != null) {
                    offAnz++;
                    entryAnz += this.offTable[i].index;
                    if (this.offTable[i].index > maxEntryAnz) {
                        maxEntryAnz = this.offTable[i].index;
                    }
                }
            }

            System.out.println("Tables: " + onAnz + " onTableEntries, " + offAnz + " offTableEntries, " + entryAnz + " Implikationen (" + maxEntryAnz + " max)");
        }
    }

    class TablingComparator implements Comparator<SolutionStep> {
        public int compare(SolutionStep o1, SolutionStep o2) {
            int sum1 = 0;
            int sum2 = 0;
            if (o1.getIndices().size() > 0 && o2.getIndices().isEmpty()) {
                return -1;
            }

            if (o1.getIndices().isEmpty() && o2.getIndices().size() > 0) {
                return 1;
            }

            if (o1.getIndices().size() > 0) {
                int result = o2.getIndices().size() - o1.getIndices().size();
                if (result != 0) {
                    return result;
                }

                if (!o1.isEquivalent(o2)) {
                    sum1 = o1.getSumme(o1.getIndices());
                    sum2 = o1.getSumme(o2.getIndices());
                    return sum1 == sum2 ? 1 : sum1 - sum2;
                }

                result = o1.getChainLength() - o2.getChainLength();
                if (result != 0) {
                    return result;
                }
            } else {
                int result = o2.getCandidatesToDelete().size() - o1.getCandidatesToDelete().size();
                if (result != 0) {
                    return result;
                }

                if (!o1.isEquivalent(o2)) {
                    result = o1.compareCandidatesToDelete(o2);
                    if (result != 0) {
                        return result;
                    }
                }

                result = o1.getChainLength() - o2.getChainLength();
                if (result != 0) {
                    return result;
                }
            }

            return 0;
        }
    }
}
