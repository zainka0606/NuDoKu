package solver;

import sudoku.*;

import java.util.*;

public class AlsSolver extends AbstractSolver {
    private static final boolean DEBUG = false;
    private static final boolean TIMING = false;
    private static final int MAX_RC = 50;
    private static AlsComparator alsComparator = null;
    private static int anzCalls = 0;
    private static long allAlsesNanos = 0L;
    private static long allRcsNanos = 0L;
    private static long allNanos = 0L;
    private List<Als> alses = new ArrayList<>(500);
    private List<RestrictedCommon> restrictedCommons = new ArrayList<>(2000);
    private int[] startIndices = null;
    private int[] endIndices = null;
    private SortedMap<String, Integer> deletesMap = new TreeMap<>();
    private List<SolutionStep> steps = new ArrayList<>();
    private SolutionStep globalStep = new SolutionStep(SolutionType.HIDDEN_SINGLE);
    private RestrictedCommon[] chain = new RestrictedCommon[50];
    private int chainIndex = -1;
    private RestrictedCommon firstRC = null;
    private boolean[] alsInChain;
    private Als startAls;
    private int recDepth = 0;
    private int maxRecDepth = 0;
    private short possibleRestrictedCommonsSet = 0;
    private SudokuSet restrictedCommonBuddiesSet = new SudokuSet();
    private SudokuSet restrictedCommonIndexSet = new SudokuSet();
    private AlsSolver.RCForDeathBlossom[] rcdb = new AlsSolver.RCForDeathBlossom[81];
    private AlsSolver.RCForDeathBlossom aktRcdb = null;
    private SudokuSet aktDBIndices = new SudokuSet();
    private short aktDBCandidates = 0;
    private short[] incDBCand = new short[10];
    private int[] aktDBAls = new int[10];
    private SudokuSet dbIndicesPerCandidate = new SudokuSet();
    private int maxDBCand = 0;
    private int stemCellIndex = 0;
    private SudokuSet tmpSet = new SudokuSet();
    private SudokuSet tmpSet1 = new SudokuSet();

    public AlsSolver(SudokuStepFinder finder) {
        super(finder);
        if (alsComparator == null) {
            alsComparator = new AlsComparator();
        }
    }

    public static String getStatistics() {
        return "Statistic for getAlsXZ(): number of calls: "
                + anzCalls
                + ", total time: "
                + allNanos / 1000L
                + "us, average: "
                + allNanos / anzCalls / 1000L
                + "us\r\n"
                + "  getAlses(): total "
                + allAlsesNanos / 1000L
                + "us, average: "
                + allAlsesNanos / anzCalls / 1000L
                + "us\r\n"
                + "  getRCs(): total "
                + allRcsNanos / 1000L
                + "us, average: "
                + allRcsNanos / anzCalls / 1000L
                + "us\r\n";
    }

    public static void main(String[] args) {
        Sudoku2 sudoku = new Sudoku2();
        sudoku.setSudoku(
                ":0903-1:35:1+53+9+642+7+8+9847+2.3+6.72+6..8+9..638.+4+9..+7+4+91..+78.+6+5+7+2+8+1+6493+8+6+74+9..32+3.9.+7268.+2.56+8+37.9:139:334 355 534::"
        );
        SudokuSolver solver = SudokuSolverFactory.getDefaultSolverInstance();
        long millis = System.nanoTime();
        int itAnz = 1;
        List<SolutionStep> steps = null;

        for (int i = 0; i < itAnz; i++) {
            steps = solver.getStepFinder().getAllAlses(sudoku, false, false, true);
        }

        millis = (System.nanoTime() - millis) / itAnz;
        System.out.println("Find all ALS-XX: " + millis / 1000000.0 + "ms");
        Collections.sort(steps);

        for (int i = 0; i < steps.size(); i++) {
            System.out.println(steps.get(i));
        }

        System.out.println("Total: " + steps.size());
        System.exit(0);
    }

    @Override
    protected SolutionStep getStep(SolutionType type) {
        SolutionStep result = null;
        this.sudoku = this.finder.getSudoku();
        this.finder.setRcOnlyForward(true);
        switch (type) {
            case ALS_XZ:
                result = this.getAlsXZ(true);
                break;
            case ALS_XY_WING:
                result = this.getAlsXYWing(true);
                break;
            case ALS_XY_CHAIN:
                if (this.chain.length != 50) {
                    this.chain = new RestrictedCommon[50];
                }

                result = this.getAlsXYChain();
                break;
            case DEATH_BLOSSOM:
                result = this.getAlsDeathBlossom(true);
        }

        return result;
    }

    @Override
    protected boolean doStep(SolutionStep step) {
        boolean handled = true;
        this.sudoku = this.finder.getSudoku();
        switch (step.getType()) {
            case ALS_XZ:
            case ALS_XY_WING:
            case ALS_XY_CHAIN:
            case DEATH_BLOSSOM:
                for (Candidate cand : step.getCandidatesToDelete()) {
                    this.sudoku.delCandidate(cand.getIndex(), cand.getValue());
                }
                break;
            default:
                handled = false;
        }

        return handled;
    }

    protected List<SolutionStep> getAllAlses(boolean doXz, boolean doXy, boolean doChain) {
        this.sudoku = this.finder.getSudoku();
        List<SolutionStep> oldSteps = this.steps;
        List<SolutionStep> resultSteps = new ArrayList<>();
        this.finder.setRcOnlyForward(Options.getInstance().isAllStepsAlsChainForwardOnly());
        if (this.chain.length == 50) {
            this.chain = new RestrictedCommon[Options.getInstance().getAllStepsAlsChainLength()];
        }

        long millis1 = 0L;
        this.collectAllAlses();
        this.collectAllRestrictedCommons(Options.getInstance().isAllowAlsOverlap());
        if (doXz) {
            this.steps.clear();
            this.getAlsXZInt(false);
            Collections.sort(this.steps, alsComparator);
            resultSteps.addAll(this.steps);
        }

        if (doXy) {
            this.steps.clear();
            this.getAlsXYWingInt(false);
            Collections.sort(this.steps, alsComparator);
            resultSteps.addAll(this.steps);
        }

        if (doChain) {
            this.steps.clear();
            this.getAlsXYChainInt();
            Collections.sort(this.steps, alsComparator);
            resultSteps.addAll(this.steps);
        }

        this.steps = oldSteps;
        return resultSteps;
    }

    protected List<SolutionStep> getAllDeathBlossoms() {
        this.sudoku = this.finder.getSudoku();
        List<SolutionStep> oldSteps = this.steps;
        List<SolutionStep> resultSteps = new ArrayList<>();
        long millis1 = 0L;
        this.collectAllAlses();
        this.collectAllRCsForDeathBlossom();
        this.steps.clear();
        this.getAlsDeathBlossomInt(false);
        Collections.sort(this.steps, alsComparator);
        resultSteps.addAll(this.steps);
        this.steps = oldSteps;
        return resultSteps;
    }

    private SolutionStep getAlsDeathBlossom(boolean onlyOne) {
        this.steps.clear();
        this.collectAllAlses();
        this.collectAllRCsForDeathBlossom();
        SolutionStep step = this.getAlsDeathBlossomInt(onlyOne);
        if (!onlyOne && this.steps.size() > 0) {
            Collections.sort(this.steps, alsComparator);
            step = this.steps.get(0);
        }

        return step;
    }

    private SolutionStep getAlsXYChain() {
        this.steps.clear();
        this.collectAllAlses();
        this.collectAllRestrictedCommons(Options.getInstance().isAllowAlsOverlap());
        this.getAlsXYChainInt();
        if (this.steps.size() > 0) {
            Collections.sort(this.steps, alsComparator);
            return this.steps.get(0);
        } else {
            return null;
        }
    }

    private SolutionStep getAlsXYWing(boolean onlyOne) {
        this.steps.clear();
        this.collectAllAlses();
        this.collectAllRestrictedCommons(Options.getInstance().isAllowAlsOverlap());
        SolutionStep step = this.getAlsXYWingInt(onlyOne);
        if (!onlyOne && this.steps.size() > 0) {
            Collections.sort(this.steps, alsComparator);
            step = this.steps.get(0);
        }

        return step;
    }

    private SolutionStep getAlsXZ(boolean onlyOne) {
        long nanos = 0L;
        anzCalls++;
        this.steps.clear();
        this.collectAllAlses();
        this.collectAllRestrictedCommons(Options.getInstance().isAllowAlsOverlap());
        SolutionStep step = this.getAlsXZInt(onlyOne);
        if (!onlyOne && this.steps.size() > 0) {
            Collections.sort(this.steps, alsComparator);
            step = this.steps.get(0);
        }

        return step;
    }

    private SolutionStep getAlsXZInt(boolean onlyOne) {
        this.globalStep.reset();

        for (int i = 0; i < this.restrictedCommons.size(); i++) {
            RestrictedCommon rc = this.restrictedCommons.get(i);
            if (rc.getAls1() <= rc.getAls2()) {
                Als als1 = this.alses.get(rc.getAls1());
                Als als2 = this.alses.get(rc.getAls2());
                this.checkCandidatesToDelete(als1, als2, rc.getCand1());
                if (rc.getCand2() != 0) {
                    this.checkCandidatesToDelete(als1, als2, rc.getCand2());
                    boolean d1 = this.checkDoublyLinkedAls(als1, als2, rc.getCand1(), rc.getCand2());
                    boolean d2 = this.checkDoublyLinkedAls(als2, als1, rc.getCand1(), rc.getCand2());
                    if (d1 || d2) {
                        this.globalStep.getFins().clear();
                    }
                }

                if (this.globalStep.getCandidatesToDelete().size() > 0) {
                    this.globalStep.setType(SolutionType.ALS_XZ);
                    this.globalStep.addAls(als1.indices, als1.candidates);
                    this.globalStep.addAls(als2.indices, als2.candidates);
                    this.addRestrictedCommonToStep(als1, als2, rc.getCand1(), false);
                    if (rc.getCand2() != 0) {
                        this.addRestrictedCommonToStep(als1, als2, rc.getCand2(), false);
                    }

                    SolutionStep step = (SolutionStep) this.globalStep.clone();
                    if (onlyOne) {
                        return step;
                    }

                    this.steps.add(step);
                    this.globalStep.reset();
                }
            }
        }

        return null;
    }

    private SolutionStep getAlsXYWingInt(boolean onlyOne) {
        this.globalStep.reset();

        for (int i = 0; i < this.restrictedCommons.size(); i++) {
            RestrictedCommon rc1 = this.restrictedCommons.get(i);

            for (int j = i + 1; j < this.restrictedCommons.size(); j++) {
                RestrictedCommon rc2 = this.restrictedCommons.get(j);
                if ((rc1.getCand2() != 0 || rc2.getCand2() != 0 || rc1.getCand1() != rc2.getCand1())
                        && (
                        rc1.getAls1() == rc2.getAls1() && rc1.getAls2() != rc2.getAls2()
                                || rc1.getAls2() == rc2.getAls1() && rc1.getAls1() != rc2.getAls2()
                                || rc1.getAls1() == rc2.getAls2() && rc1.getAls2() != rc2.getAls1()
                                || rc1.getAls2() == rc2.getAls2() && rc1.getAls1() != rc2.getAls1()
                )) {
                    Als a = null;
                    Als b = null;
                    Als c = null;
                    if (rc1.getAls1() == rc2.getAls1()) {
                        c = this.alses.get(rc1.getAls1());
                        a = this.alses.get(rc1.getAls2());
                        b = this.alses.get(rc2.getAls2());
                    }

                    if (rc1.getAls1() == rc2.getAls2()) {
                        c = this.alses.get(rc1.getAls1());
                        a = this.alses.get(rc1.getAls2());
                        b = this.alses.get(rc2.getAls1());
                    }

                    if (rc1.getAls2() == rc2.getAls1()) {
                        c = this.alses.get(rc1.getAls2());
                        a = this.alses.get(rc1.getAls1());
                        b = this.alses.get(rc2.getAls2());
                    }

                    if (rc1.getAls2() == rc2.getAls2()) {
                        c = this.alses.get(rc1.getAls2());
                        a = this.alses.get(rc1.getAls1());
                        b = this.alses.get(rc2.getAls1());
                    }

                    if (!Options.getInstance().isAllowAlsOverlap()) {
                        this.tmpSet.set(a.indices);
                        if (!this.tmpSet.andEmpty(b.indices)) {
                            continue;
                        }
                    }

                    this.tmpSet.set(a.indices);
                    this.tmpSet.or(b.indices);
                    if (!this.tmpSet.equals(a.indices) && !this.tmpSet.equals(b.indices)) {
                        this.checkCandidatesToDelete(a, b, rc1.getCand1(), rc1.getCand2(), rc2.getCand1(), rc2.getCand2());
                        if (this.globalStep.getCandidatesToDelete().size() > 0) {
                            this.globalStep.setType(SolutionType.ALS_XY_WING);
                            this.globalStep.addAls(a.indices, a.candidates);
                            this.globalStep.addAls(b.indices, b.candidates);
                            this.globalStep.addAls(c.indices, c.candidates);
                            this.addRestrictedCommonToStep(a, c, rc1.getCand1(), false);
                            if (rc1.getCand2() != 0) {
                                this.addRestrictedCommonToStep(a, c, rc1.getCand2(), false);
                            }

                            this.addRestrictedCommonToStep(b, c, rc2.getCand1(), false);
                            if (rc2.getCand2() != 0) {
                                this.addRestrictedCommonToStep(b, c, rc2.getCand2(), false);
                            }

                            SolutionStep step = (SolutionStep) this.globalStep.clone();
                            if (onlyOne) {
                                return step;
                            }

                            this.steps.add(step);
                            this.globalStep.reset();
                        }
                    }
                }
            }
        }

        return null;
    }

    private void getAlsXYChainInt() {
        this.recDepth = 0;
        this.maxRecDepth = 0;
        this.deletesMap.clear();

        for (int i = 0; i < this.alses.size(); i++) {
            this.startAls = this.alses.get(i);
            this.chainIndex = 0;
            if (this.alsInChain != null && this.alsInChain.length >= this.alses.size()) {
                Arrays.fill(this.alsInChain, false);
            } else {
                this.alsInChain = new boolean[this.alses.size()];
            }

            this.alsInChain[i] = true;
            this.firstRC = null;
            this.getAlsXYChainRecursive(i, null);
        }
    }

    private void getAlsXYChainRecursive(int alsIndex, RestrictedCommon lastRC) {
        if (this.chainIndex < this.chain.length) {
            this.recDepth++;
            if (this.recDepth > this.maxRecDepth) {
                this.maxRecDepth = this.recDepth;
            }

            if (this.recDepth % 100 == 0) {
            }

            boolean firstTry = true;

            for (int i = this.startIndices[alsIndex]; i < this.endIndices[alsIndex]; i++) {
                RestrictedCommon rc = this.restrictedCommons.get(i);
                if (this.chainIndex < this.chain.length && rc.checkRC(lastRC, firstTry) && !this.alsInChain[rc.getAls2()]) {
                    Als aktAls = this.alses.get(rc.getAls2());
                    if (this.chainIndex == 0) {
                        this.firstRC = rc;
                    }

                    this.chain[this.chainIndex++] = rc;
                    this.alsInChain[rc.getAls2()] = true;
                    if (this.chainIndex >= 3) {
                        this.globalStep.getCandidatesToDelete().clear();
                        int c1 = 0;
                        int c2 = 0;
                        int c3 = 0;
                        int c4 = 0;
                        c1 = this.firstRC.getCand1();
                        c2 = this.firstRC.getCand2();
                        if (this.firstRC.getActualRC() == 1) {
                            c2 = 0;
                        } else if (this.firstRC.getActualRC() == 2) {
                            c1 = 0;
                        }

                        if (rc.getActualRC() == 1) {
                            c3 = rc.getCand1();
                        } else if (rc.getActualRC() == 2) {
                            c3 = rc.getCand2();
                        } else if (rc.getActualRC() == 3) {
                            c3 = rc.getCand1();
                            c4 = rc.getCand2();
                        }

                        this.checkCandidatesToDelete(this.startAls, aktAls, c1, c2, c3, c4, null);
                        if (this.globalStep.getCandidatesToDelete().size() > 0) {
                            this.globalStep.setType(SolutionType.ALS_XY_CHAIN);
                            this.globalStep.addAls(this.startAls.indices, this.startAls.candidates);
                            Als tmpAls = this.startAls;

                            for (int j = 0; j < this.chainIndex; j++) {
                                Als tmp = this.alses.get(this.chain[j].getAls2());
                                this.globalStep.addAls(tmp.indices, tmp.candidates);
                                this.globalStep.addRestrictedCommon((RestrictedCommon) this.chain[j].clone());
                                if (this.chain[j].getActualRC() == 1 || this.chain[j].getActualRC() == 3) {
                                    this.addRestrictedCommonToStep(tmpAls, tmp, this.chain[j].getCand1(), true);
                                }

                                if (this.chain[j].getActualRC() == 2 || this.chain[j].getActualRC() == 3) {
                                    this.addRestrictedCommonToStep(tmpAls, tmp, this.chain[j].getCand2(), true);
                                }

                                tmpAls = tmp;
                            }

                            boolean writeIt = true;
                            int replaceIndex = -1;
                            String elim = null;
                            if (Options.getInstance().isOnlyOneAlsPerStep()) {
                                elim = this.globalStep.getCandidateString();
                                Integer alreadyThere = this.deletesMap.get(elim);
                                if (alreadyThere != null) {
                                    SolutionStep tmp = this.steps.get(alreadyThere);
                                    if (tmp.getAlsesIndexCount() > this.globalStep.getAlsesIndexCount()) {
                                        writeIt = true;
                                        replaceIndex = alreadyThere;
                                    } else {
                                        writeIt = false;
                                    }
                                }
                            }

                            if (writeIt) {
                                if (replaceIndex != -1) {
                                    this.steps.remove(replaceIndex);
                                    this.steps.add(replaceIndex, (SolutionStep) this.globalStep.clone());
                                } else {
                                    this.steps.add((SolutionStep) this.globalStep.clone());
                                    if (elim != null) {
                                        this.deletesMap.put(elim, this.steps.size() - 1);
                                    }
                                }
                            }

                            this.globalStep.reset();
                        }
                    }

                    this.getAlsXYChainRecursive(rc.getAls2(), rc);
                    this.alsInChain[rc.getAls2()] = false;
                    this.chainIndex--;
                    if (lastRC == null) {
                        if (rc.getCand2() != 0 && firstTry) {
                            firstTry = false;
                            i--;
                        } else {
                            firstTry = true;
                        }
                    }
                }
            }

            this.recDepth--;
        }
    }

    private void showActAlsChain(int recDepth) {
    }

    private SolutionStep getAlsDeathBlossomInt(boolean onlyOne) {
        this.deletesMap.clear();
        this.globalStep.reset();
        this.globalStep.setType(SolutionType.DEATH_BLOSSOM);

        for (int i = 0; i < 81; i++) {
            if (this.sudoku.getValue(i) == 0 && this.rcdb[i] != null && this.sudoku.getCells()[i] == this.rcdb[i].candMask) {
                this.stemCellIndex = i;
                this.aktRcdb = this.rcdb[i];
                this.maxDBCand = 0;

                for (int j = 1; j <= 9; j++) {
                    if (this.aktRcdb.indices[j] > 0) {
                        this.maxDBCand = j;
                    }
                }

                this.aktDBIndices.clear();
                this.aktDBCandidates = 511;

                for (int j = 0; j < this.aktDBAls.length; j++) {
                    this.aktDBAls[j] = -1;
                }

                SolutionStep step = this.checkAlsDeathBlossomRecursive(1, onlyOne);
                if (onlyOne && step != null) {
                    return step;
                }
            }
        }

        return null;
    }

    private SolutionStep checkAlsDeathBlossomRecursive(int cand, boolean onlyOne) {
        if (cand > this.maxDBCand) {
            return null;
        }

        if (this.aktRcdb.indices[cand] > 0) {
            for (int i = 0; i < this.aktRcdb.indices[cand]; i++) {
                Als als = this.alses.get(this.aktRcdb.alsPerCandidate[cand][i]);
                if (Options.getInstance().isAllowAlsOverlap() || als.indices.andNotEquals(this.aktDBIndices)) {
                    short tmpCandSet = this.aktDBCandidates;
                    if ((tmpCandSet & als.candidates) != 0) {
                        this.aktDBAls[cand] = this.aktRcdb.alsPerCandidate[cand][i];
                        this.incDBCand[cand] = this.aktDBCandidates;
                        this.incDBCand[cand] = (short) (this.incDBCand[cand] & ~als.candidates);
                        this.aktDBCandidates = (short) (this.aktDBCandidates & als.candidates);
                        this.aktDBIndices.or(als.indices);
                        if (cand < this.maxDBCand) {
                            SolutionStep step = this.checkAlsDeathBlossomRecursive(cand + 1, onlyOne);
                            if (onlyOne && step != null) {
                                return step;
                            }
                        } else {
                            boolean found = false;
                            int[] cands = Sudoku2.POSSIBLE_VALUES[this.aktDBCandidates];

                            for (int j = 0; j < cands.length; j++) {
                                int checkCand = cands[j];
                                if (this.aktDBAls[checkCand] == -1) {
                                    boolean first = true;

                                    for (int k = 0; k < this.aktDBAls.length; k++) {
                                        if (this.aktDBAls[k] != -1) {
                                            if (first) {
                                                this.dbIndicesPerCandidate.set(this.alses.get(this.aktDBAls[k]).indicesPerCandidat[checkCand]);
                                                first = false;
                                            } else {
                                                this.dbIndicesPerCandidate.or(this.alses.get(this.aktDBAls[k]).indicesPerCandidat[checkCand]);
                                            }
                                        }
                                    }

                                    Sudoku2.getBuddies(this.dbIndicesPerCandidate, this.tmpSet);
                                    this.tmpSet.andNot(this.aktDBIndices);
                                    this.tmpSet.remove(this.stemCellIndex);
                                    this.tmpSet.and(this.finder.getCandidates()[checkCand]);
                                    if (!this.tmpSet.isEmpty()) {
                                        found = true;

                                        for (int k = 0; k < this.tmpSet.size(); k++) {
                                            this.globalStep.addCandidateToDelete(this.tmpSet.get(k), checkCand);
                                        }
                                    }
                                }
                            }

                            if (found) {
                                this.globalStep.addIndex(this.stemCellIndex);

                                for (int k = 1; k <= 9; k++) {
                                    if (this.aktDBAls[k] != -1) {
                                        Als tmpAls = this.alses.get(this.aktDBAls[k]);

                                        for (int l = 0; l < tmpAls.indicesPerCandidat[k].size(); l++) {
                                            this.globalStep.addFin(tmpAls.indicesPerCandidat[k].get(l), k);
                                        }

                                        this.globalStep.addFin(this.stemCellIndex, k);
                                        this.globalStep.addAls(tmpAls.indices, tmpAls.candidates);
                                        this.globalStep.addRestrictedCommon(new RestrictedCommon(0, 0, k, 0, 1));
                                    }
                                }

                                boolean writeIt = true;
                                int replaceIndex = -1;
                                String elim = null;
                                if (Options.getInstance().isOnlyOneAlsPerStep()) {
                                    elim = this.globalStep.getCandidateString();
                                    Integer alreadyThere = this.deletesMap.get(elim);
                                    if (alreadyThere != null) {
                                        SolutionStep tmp = this.steps.get(alreadyThere);
                                        if (tmp.getAlsesIndexCount() > this.globalStep.getAlsesIndexCount()) {
                                            writeIt = true;
                                            replaceIndex = alreadyThere;
                                        } else {
                                            writeIt = false;
                                        }
                                    }
                                }

                                if (writeIt) {
                                    if (replaceIndex != -1) {
                                        this.steps.remove(replaceIndex);
                                        this.steps.add(replaceIndex, (SolutionStep) this.globalStep.clone());
                                    } else {
                                        SolutionStep step = (SolutionStep) this.globalStep.clone();
                                        if (onlyOne) {
                                            return step;
                                        }

                                        this.steps.add(step);
                                        if (elim != null) {
                                            this.deletesMap.put(elim, this.steps.size() - 1);
                                        }
                                    }
                                }

                                this.globalStep.reset();
                                this.globalStep.setType(SolutionType.DEATH_BLOSSOM);
                            }
                        }

                        this.aktDBCandidates = (short) (this.aktDBCandidates | this.incDBCand[cand]);
                        this.aktDBIndices.andNot(als.indices);
                    }
                }
            }
        } else {
            this.aktDBAls[cand] = -1;
            SolutionStep step = this.checkAlsDeathBlossomRecursive(cand + 1, onlyOne);
            if (onlyOne && step != null) {
                return step;
            }
        }

        return null;
    }

    private void checkCandidatesToDelete(Als als1, Als als2, int restr1) {
        this.checkCandidatesToDelete(als1, als2, restr1, -1, -1, -1, null);
    }

    private void checkCandidatesToDelete(Als als1, Als als2, int restr1, int restr2, int restr3, int restr4) {
        this.checkCandidatesToDelete(als1, als2, restr1, restr2, restr3, restr4, null);
    }

    private void checkCandidatesToDelete(Als als1, Als als2, int restr1, int restr2, int restr3, int restr4, SudokuSet forbiddenIndices) {
        this.possibleRestrictedCommonsSet = als1.candidates;
        this.possibleRestrictedCommonsSet = (short) (this.possibleRestrictedCommonsSet & als2.candidates);
        if (restr1 != -1 && restr1 != 0) {
            this.possibleRestrictedCommonsSet = (short) (this.possibleRestrictedCommonsSet & ~Sudoku2.MASKS[restr1]);
        }

        if (restr2 != -1 && restr2 != 0) {
            this.possibleRestrictedCommonsSet = (short) (this.possibleRestrictedCommonsSet & ~Sudoku2.MASKS[restr2]);
        }

        if (restr3 != -1 && restr3 != 0) {
            this.possibleRestrictedCommonsSet = (short) (this.possibleRestrictedCommonsSet & ~Sudoku2.MASKS[restr3]);
        }

        if (restr4 != -1 && restr4 != 0) {
            this.possibleRestrictedCommonsSet = (short) (this.possibleRestrictedCommonsSet & ~Sudoku2.MASKS[restr4]);
        }

        if (this.possibleRestrictedCommonsSet != 0) {
            this.tmpSet.set(als1.buddies);
            if (!this.tmpSet.andEmpty(als2.buddies)) {
                if (forbiddenIndices != null) {
                    this.tmpSet.set(forbiddenIndices);
                } else {
                    this.tmpSet.set(als1.indices);
                    this.tmpSet.or(als2.indices);
                }

                int[] prcs = Sudoku2.POSSIBLE_VALUES[this.possibleRestrictedCommonsSet];

                for (int j = 0; j < prcs.length; j++) {
                    int cand = prcs[j];
                    this.restrictedCommonBuddiesSet.set(als1.buddiesPerCandidat[cand]);
                    this.restrictedCommonBuddiesSet.and(als2.buddiesPerCandidat[cand]);
                    if (forbiddenIndices != null) {
                        this.restrictedCommonBuddiesSet.andNot(forbiddenIndices);
                    }

                    if (!this.restrictedCommonBuddiesSet.isEmpty()) {
                        for (int l = 0; l < this.restrictedCommonBuddiesSet.size(); l++) {
                            this.globalStep.addCandidateToDelete(this.restrictedCommonBuddiesSet.get(l), cand);
                        }

                        this.tmpSet1.set(als1.indicesPerCandidat[cand]);
                        this.tmpSet1.or(als2.indicesPerCandidat[cand]);

                        for (int l = 0; l < this.tmpSet1.size(); l++) {
                            this.globalStep.addFin(this.tmpSet1.get(l), cand);
                        }
                    }
                }
            }
        }
    }

    private void addRestrictedCommonToStep(Als als1, Als als2, int cand, boolean withChain) {
        this.tmpSet.set(als1.indicesPerCandidat[cand]);
        this.tmpSet.or(als2.indicesPerCandidat[cand]);

        for (int i = 0; i < this.tmpSet.size(); i++) {
            this.globalStep.addEndoFin(this.tmpSet.get(i), cand);
        }

        if (withChain) {
            int minDist = Integer.MAX_VALUE;
            int minIndex1 = -1;
            int minIndex2 = -1;

            for (int i1 = 0; i1 < als1.indicesPerCandidat[cand].size(); i1++) {
                for (int i2 = 0; i2 < als2.indicesPerCandidat[cand].size(); i2++) {
                    int index1 = als1.indicesPerCandidat[cand].get(i1);
                    int index2 = als2.indicesPerCandidat[cand].get(i2);
                    int dx = Sudoku2.getLine(index1) - Sudoku2.getLine(index2);
                    int dy = Sudoku2.getCol(index1) - Sudoku2.getCol(index2);
                    int dist = dx * dx + dy * dy;
                    if (dist < minDist) {
                        minDist = dist;
                        minIndex1 = index1;
                        minIndex2 = index2;
                    }
                }
            }

            int[] tmpChain = new int[]{Chain.makeSEntry(minIndex1, cand, false), Chain.makeSEntry(minIndex2, cand, false)};
            this.globalStep.addChain(0, 1, tmpChain);
        }
    }

    private boolean checkDoublyLinkedAls(Als als1, Als als2, int rc1, int rc2) {
        boolean isDoubly = false;
        this.possibleRestrictedCommonsSet = als1.candidates;
        this.possibleRestrictedCommonsSet = (short) (this.possibleRestrictedCommonsSet & ~Sudoku2.MASKS[rc1]);
        this.possibleRestrictedCommonsSet = (short) (this.possibleRestrictedCommonsSet & ~Sudoku2.MASKS[rc2]);
        if (this.possibleRestrictedCommonsSet == 0) {
            return false;
        }

        int[] prcs = Sudoku2.POSSIBLE_VALUES[this.possibleRestrictedCommonsSet];

        for (int i = 0; i < prcs.length; i++) {
            int cand = prcs[i];
            this.restrictedCommonIndexSet.set(als1.buddiesPerCandidat[cand]);
            this.restrictedCommonIndexSet.andNot(als2.indices);
            if (!this.restrictedCommonIndexSet.isEmpty()) {
                for (int j = 0; j < this.restrictedCommonIndexSet.size(); j++) {
                    this.globalStep.addCandidateToDelete(this.restrictedCommonIndexSet.get(j), cand);
                    isDoubly = true;
                }
            }
        }

        return isDoubly;
    }

    private void collectAllRestrictedCommons(boolean withOverlap) {
        long ticks = 0L;
        this.restrictedCommons = this.finder.getRestrictedCommons(this.alses, withOverlap);
        this.startIndices = this.finder.getStartIndices();
        this.endIndices = this.finder.getEndIndices();
    }

    private void collectAllAlses() {
        long ticks = 0L;
        this.alses = this.finder.getAlses();
    }

    private void collectAllRCsForDeathBlossom() {
        long ticks = 0L;

        for (int i = 0; i < this.rcdb.length; i++) {
            Arrays.fill(this.rcdb, null);
        }

        for (int i = 0; i < this.alses.size(); i++) {
            Als act = this.alses.get(i);

            for (int j = 1; j <= 9; j++) {
                if ((act.candidates & Sudoku2.MASKS[j]) != 0) {
                    for (int k = 0; k < act.buddiesPerCandidat[j].size(); k++) {
                        int index = act.buddiesPerCandidat[j].get(k);
                        if (this.rcdb[index] == null) {
                            this.rcdb[index] = new AlsSolver.RCForDeathBlossom();
                        }

                        this.rcdb[index].addAlsForCandidate(i, j);
                    }
                }
            }
        }
    }

    class RCForDeathBlossom {
        short candMask;
        int[][] alsPerCandidate = new int[10][100];
        int[] indices = new int[10];

        public void addAlsForCandidate(int als, int candidate) {
            if (this.indices[candidate] < this.alsPerCandidate[candidate].length) {
                this.alsPerCandidate[candidate][this.indices[candidate]++] = als;
                this.candMask = (short) (this.candMask | Sudoku2.MASKS[candidate]);
            }
        }
    }
}
