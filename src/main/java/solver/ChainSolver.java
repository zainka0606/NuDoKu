package solver;

import sudoku.*;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChainSolver extends AbstractSolver {
    private static final int MAX_CHAIN_LENGTH = 162;
    private static final int X_CHAIN = 0;
    private static final int XY_CHAIN = 1;
    private static final int REMOTE_PAIR = 2;
    private static final int NICE_LOOP = 3;
    private static final int TURBOT_FISH = 4;
    private static ChainSolver.ChainComparator chainComparator = null;

    static {
        chainComparator = new ChainSolver.ChainComparator();
    }

    private ChainSolver.StackEntry[] stack = new ChainSolver.StackEntry[162];
    private int stackLevel;
    private int chainMaxLength;
    private long startCellSetM1;
    private long startCellSetM2;
    private long startCellSet2M1;
    private long startCellSet2M2;
    private int[] links = new int[20000];
    private int[] startIndices = new int[810];
    private int[] endIndices = new int[810];
    private int[] chain = new int[162];
    private SudokuSet chainSet = new SudokuSet();
    private int startIndex = 0;
    private int startCandidate = 0;
    private int startCandidate2 = 0;
    private int rpCell = 0;
    private SudokuSet checkBuddies = new SudokuSet();
    private SudokuSet rpCand1 = new SudokuSet();
    private SudokuSet rpCand2 = new SudokuSet();
    private SudokuSet rpTmp = new SudokuSet();
    private SortedMap<String, Integer> deletesMap = new TreeMap<>();
    private SolutionStep globalStep = new SolutionStep(SolutionType.FULL_HOUSE);
    private List<SolutionStep> steps;
    private int lastStepNumber = -1;
    private boolean turbotOrXSeen;
    private long linkTNanos;
    private long chainTNanos;
    private long linkRNanos;
    private long chainRNanos;
    private long linkXNanos;
    private long chainXNanos;
    private long linkYNanos;
    private long chainYNanos;
    private int anzT;
    private int anzR;
    private int anzX;
    private int anzY;

    public ChainSolver(SudokuStepFinder finder) {
        super(finder);

        for (int i = 0; i < this.stack.length; i++) {
            this.stack[i] = new ChainSolver.StackEntry();
        }
    }

    public static void main(String[] args) {
        Sudoku2 sudoku = new Sudoku2();
        sudoku.setSudoku(":0703:4:5.91673.8.63548.191..23956.952413..6316782495...9561328.53916..637824951.91675..3:432 462 298:498:");
        sudoku.setSudoku(":0403:1:9+3...8.+4+5..7.4..+38.843.....16+259+7+3+8+4+8+75+4+2+3...+3+4+9+18+6+5724+9+3+7518+26....3.4+5.....+6+4.9+3::182::");
        SudokuSolver solver = SudokuSolverFactory.getDefaultSolverInstance();
        boolean singleHint = false;
        if (singleHint) {
            SolutionStep step = solver.getHint(sudoku, false);
            System.out.println(step);
        } else {
            List<SolutionStep> steps = solver.getStepFinder().getAllChains(sudoku);
            solver.getStepFinder().printStatistics();
            if (steps.size() > 0) {
                Collections.sort(steps);

                for (SolutionStep actStep : steps) {
                    System.out.println(actStep);
                }
            }
        }

        System.exit(0);
    }

    @Override
    protected SolutionStep getStep(SolutionType type) {
        SolutionStep result = null;
        this.sudoku = this.finder.getSudoku();
        switch (type) {
            case X_CHAIN:
                result = this.getXChains();
                break;
            case XY_CHAIN:
                result = this.getXYChains();
                break;
            case REMOTE_PAIR:
                result = this.getRemotePairs();
                break;
            case TURBOT_FISH:
                result = this.getTurbotChains();
        }

        return result;
    }

    @Override
    protected boolean doStep(SolutionStep step) {
        boolean handled = true;
        this.sudoku = this.finder.getSudoku();
        switch (step.getType()) {
            case X_CHAIN:
            case XY_CHAIN:
            case REMOTE_PAIR:
            case TURBOT_FISH:
            case NICE_LOOP:
                for (Candidate cand : step.getCandidatesToDelete()) {
                    this.sudoku.delCandidate(cand.getIndex(), cand.getValue());
                }
                break;
            default:
                handled = false;
        }

        return handled;
    }

    private SolutionStep getXChains() {
        this.steps = new ArrayList<>();
        this.getChains(0);
        if (this.steps.size() > 0) {
            Collections.sort(this.steps);
            return this.steps.get(0);
        } else {
            return null;
        }
    }

    private SolutionStep getTurbotChains() {
        this.steps = new ArrayList<>();
        this.getChains(4);
        if (this.steps.size() > 0) {
            Collections.sort(this.steps);
            return this.steps.get(0);
        } else {
            return null;
        }
    }

    private SolutionStep getXYChains() {
        this.steps = new ArrayList<>();
        this.getChains(1);
        if (this.steps.size() > 0) {
            Collections.sort(this.steps);
            return this.steps.get(0);
        } else {
            return null;
        }
    }

    private SolutionStep getRemotePairs() {
        this.steps = new ArrayList<>();
        this.getChains(2);
        if (this.steps.size() > 0) {
            Collections.sort(this.steps);
            return this.steps.get(0);
        } else {
            return null;
        }
    }

    protected List<SolutionStep> getAllChains() {
        this.sudoku = this.finder.getSudoku();
        List<SolutionStep> tmpSteps = new ArrayList<>();
        tmpSteps = this.getAllChains(tmpSteps);
        Collections.sort(tmpSteps, chainComparator);
        return tmpSteps;
    }

    private List<SolutionStep> getAllChains(List<SolutionStep> allSteps) {
        long ticks = System.currentTimeMillis();
        this.steps = new ArrayList<>();
        allSteps.clear();
        this.getChains(4);
        Collections.sort(this.steps);
        this.getChains(0);
        Collections.sort(this.steps);
        this.getChains(1);
        Collections.sort(this.steps);
        this.getChains(2);
        Collections.sort(this.steps);
        allSteps.addAll(this.steps);
        ticks = System.currentTimeMillis() - ticks;
        Logger.getLogger(this.getClass().getName()).log(Level.FINE, "getAllChains() gesamt: {0}ms", ticks);
        return allSteps;
    }

    private void getChains(int type) {
        long nanos = System.nanoTime();
        this.getAllLinks(type);
        this.chainMaxLength = 161;
        if (Options.getInstance().isRestrictChainSize()) {
            if (type == 3) {
                this.chainMaxLength = Options.getInstance().getRestrictNiceLoopLength();
            } else {
                this.chainMaxLength = Options.getInstance().getRestrictChainLength();
            }
        }

        if (type == 4) {
            this.chainMaxLength = 3;
        }

        this.deletesMap.clear();
        boolean onlyOne = false;
        if (!onlyOne || type == 4) {
            for (this.startIndex = 0; this.startIndex < this.sudoku.getCells().length; this.startIndex++) {
                if (this.sudoku.getValue(this.startIndex) == 0 && (!onlyOne || this.startIndex == 10)) {
                    int[] startCandidates = this.sudoku.getAllCandidates(this.startIndex);

                    for (int i = 0; i < startCandidates.length; i++) {
                        this.startCandidate = startCandidates[i];
                        if (!onlyOne || this.startCandidate == 1) {
                            int linkStartIndex = this.startIndex * 10 + this.startCandidate;

                            for (int linkIndex = this.startIndices[linkStartIndex]; linkIndex < this.endIndices[linkStartIndex]; linkIndex++) {
                                if ((type != 0 && type != 1 && type != 2 && type != 4 || Chain.isSStrong(this.links[linkIndex]))
                                        && (type != 0 && type != 4 || Chain.getSCandidate(this.links[linkIndex]) == this.startCandidate)
                                        && (type != 1 && type != 2 || this.sudoku.getAnzCandidates(Chain.getSCellIndex(this.links[linkIndex])) == 2)
                                        && (type != 1 && type != 2 || Chain.getSCellIndex(this.links[linkIndex]) == this.startIndex)) {
                                    if (type == 2) {
                                        this.rpCell = this.sudoku.getCell(this.startIndex);
                                        int[] cands = this.sudoku.getAllCandidates(this.startIndex);
                                        if (cands[0] != this.startCandidate) {
                                            this.startCandidate2 = cands[0];
                                        } else {
                                            this.startCandidate2 = cands[1];
                                        }
                                    }

                                    this.stackLevel = 1;
                                    this.chain[0] = Chain.makeSEntry(this.startIndex, this.startCandidate, false);
                                    this.chain[1] = this.links[linkIndex];
                                    ChainSolver.StackEntry entry = this.stack[this.stackLevel];
                                    entry.cellIndex = Chain.getSCellIndex(this.chain[1]);
                                    entry.candidate = Chain.getSCandidate(this.chain[1]);
                                    entry.strongOnly = !Chain.isSStrong(this.chain[1]);
                                    entry.aktIndex = this.startIndices[entry.cellIndex * 10 + entry.candidate];
                                    entry.endIndex = this.endIndices[entry.cellIndex * 10 + entry.candidate];
                                    this.chainSet.clear();
                                    this.chainSet.add(this.startIndex);
                                    this.startCellSetM1 = Sudoku2.buddiesM1[this.startIndex] & this.finder.getCandidates()[this.startCandidate].getMask1();
                                    this.startCellSetM2 = Sudoku2.buddiesM2[this.startIndex] & this.finder.getCandidates()[this.startCandidate].getMask2();
                                    if (type == 2) {
                                        this.startCellSet2M1 = Sudoku2.buddiesM1[this.startIndex] & this.finder.getCandidates()[this.startCandidate2].getMask1();
                                        this.startCellSet2M2 = Sudoku2.buddiesM2[this.startIndex] & this.finder.getCandidates()[this.startCandidate2].getMask2();
                                    }

                                    this.getChain(entry, type);
                                }
                            }
                        }
                    }
                }
            }

            switch (type) {
                case 0:
                    this.chainXNanos = this.chainXNanos + (System.nanoTime() - nanos);
                    this.anzX++;
                    break;
                case 1:
                    this.chainYNanos = this.chainYNanos + (System.nanoTime() - nanos);
                    this.anzY++;
                    break;
                case 2:
                    this.chainRNanos = this.chainRNanos + (System.nanoTime() - nanos);
                    this.anzR++;
                case 3:
                default:
                    break;
                case 4:
                    this.chainTNanos = this.chainTNanos + (System.nanoTime() - nanos);
                    this.anzT++;
            }
        }
    }

    private void getChain(ChainSolver.StackEntry entry, int typ) {
        while (true) {
            if (entry.aktIndex >= entry.endIndex) {
                this.stackLevel--;
                entry = this.stack[this.stackLevel];
                this.chainSet.remove(entry.cellIndex);
                if (this.stackLevel <= 0) {
                    return;
                }
            } else {
                int newLink = this.links[entry.aktIndex++];
                boolean newLinkIsStrong = Chain.isSStrong(newLink);
                if (!entry.strongOnly || newLinkIsStrong) {
                    int newLinkIndex = Chain.getSCellIndex(newLink);
                    int newLinkCandidate = Chain.getSCandidate(newLink);
                    if ((entry.cellIndex != newLinkIndex || entry.candidate != newLinkCandidate)
                            && (typ != 2 || this.sudoku.getCell(newLinkIndex) == this.rpCell)
                            && (typ != 0 && typ != 4 || newLinkCandidate == this.startCandidate)
                            && (typ != 1 && typ != 2 || this.sudoku.getAnzCandidates(newLinkIndex) == 2)
                            && (typ != 1 && typ != 2 || !entry.strongOnly || newLinkIndex == entry.cellIndex)) {
                        boolean isLoop = false;
                        if (this.chainSet.contains(newLinkIndex)) {
                            if (this.startIndex != newLinkIndex) {
                                continue;
                            }

                            isLoop = true;
                        }

                        this.chainSet.add(entry.cellIndex);
                        if (!entry.strongOnly && newLinkIsStrong) {
                            newLink = Chain.setSStrong(newLink, false);
                            newLinkIsStrong = false;
                        }

                        this.chain[++this.stackLevel] = newLink;
                        if (typ == 3) {
                            if (isLoop) {
                                this.checkNiceLoop(newLink, this.stackLevel);
                            }
                        } else if (this.stackLevel > 1 && newLinkIsStrong && newLinkCandidate == this.startCandidate) {
                            long m1 = this.startCellSetM1 & Sudoku2.buddiesM1[newLinkIndex];
                            long m2 = this.startCellSetM2 & Sudoku2.buddiesM2[newLinkIndex];
                            if (m1 != 0L || m2 != 0L) {
                                switch (typ) {
                                    case 0:
                                        this.checkXChain(m1, m2, false);
                                        break;
                                    case 1:
                                        this.checkXYChain(m1, m2);
                                        break;
                                    case 2:
                                        if (this.stackLevel >= 7) {
                                            this.checkRemotePairs(m1, m2, newLinkIndex);
                                        }
                                    case 3:
                                    default:
                                        break;
                                    case 4:
                                        if (this.stackLevel == 3) {
                                            this.checkXChain(m1, m2, true);
                                        }
                                }
                            }
                        }

                        boolean oldStrongOnly = entry.strongOnly;
                        entry = this.stack[this.stackLevel];
                        if (this.stackLevel < this.chainMaxLength && !isLoop) {
                            entry.cellIndex = newLinkIndex;
                            entry.candidate = newLinkCandidate;
                            entry.strongOnly = !oldStrongOnly;
                            entry.aktIndex = this.startIndices[entry.cellIndex * 10 + entry.candidate];
                            entry.endIndex = this.endIndices[entry.cellIndex * 10 + entry.candidate];
                        } else {
                            entry.aktIndex = entry.endIndex;
                        }
                    }
                }
            }
        }
    }

    private void checkXChain(long m1, long m2, boolean isTurbot) {
        this.globalStep.reset();
        if (isTurbot) {
            this.globalStep.setType(SolutionType.TURBOT_FISH);
        } else {
            this.globalStep.setType(SolutionType.X_CHAIN);
        }

        this.globalStep.addValue(this.startCandidate);
        this.checkBuddies.set(m1, m2);

        for (int i = 0; i < this.checkBuddies.size(); i++) {
            this.globalStep.addCandidateToDelete(this.checkBuddies.get(i), this.startCandidate);
        }

        if (!isTurbot) {
            String del = this.globalStep.getCandidateString();
            Integer oldLength = this.deletesMap.get(del);
            if (oldLength != null && oldLength <= this.stackLevel) {
                return;
            }

            this.deletesMap.put(del, this.stackLevel);
        }

        int[] newChain = new int[this.stackLevel + 1];
        System.arraycopy(this.chain, 0, newChain, 0, newChain.length);
        this.globalStep.addChain(0, this.stackLevel, newChain);
        this.steps.add((SolutionStep) this.globalStep.clone());
    }

    private void checkXYChain(long m1, long m2) {
        this.globalStep.reset();
        this.globalStep.setType(SolutionType.XY_CHAIN);
        this.globalStep.addValue(this.startCandidate);
        this.checkBuddies.set(m1, m2);

        for (int i = 0; i < this.checkBuddies.size(); i++) {
            this.globalStep.addCandidateToDelete(this.checkBuddies.get(i), this.startCandidate);
        }

        String del = this.globalStep.getCandidateString();
        Integer oldLength = this.deletesMap.get(del);
        if (oldLength == null || oldLength > this.stackLevel) {
            this.deletesMap.put(del, this.stackLevel);
            int[] newChain = new int[this.stackLevel + 1];
            System.arraycopy(this.chain, 0, newChain, 0, newChain.length);
            this.globalStep.addChain(0, this.stackLevel, newChain);
            this.steps.add((SolutionStep) this.globalStep.clone());
        }
    }

    private void checkRemotePairs(long m1, long m2, int endIndex) {
        this.globalStep.reset();
        this.globalStep.setType(SolutionType.REMOTE_PAIR);
        this.rpCand1.clear();
        this.rpCand2.clear();
        if (this.stackLevel > 7) {
            for (int i = 0; i <= this.stackLevel; i += 2) {
                for (int j = i + 6; j <= this.stackLevel; j += 4) {
                    this.rpTmp.set(Sudoku2.buddies[Chain.getSCellIndex(this.chain[i])]);
                    this.rpTmp.and(Sudoku2.buddies[Chain.getSCellIndex(this.chain[j])]);
                    this.checkBuddies.set(this.rpTmp);
                    this.checkBuddies.and(this.finder.getCandidates()[this.startCandidate]);
                    this.rpCand1.or(this.checkBuddies);
                    this.checkBuddies.set(this.rpTmp);
                    this.checkBuddies.and(this.finder.getCandidates()[this.startCandidate2]);
                    this.rpCand2.or(this.checkBuddies);
                }
            }
        } else {
            long m21 = this.startCellSet2M1 & Sudoku2.buddiesM1[endIndex];
            long m22 = this.startCellSet2M2 & Sudoku2.buddiesM2[endIndex];
            this.rpCand1.set(m1, m2);
            this.rpCand2.set(m21, m22);
        }

        this.globalStep.addValue(this.startCandidate);
        this.globalStep.addValue(this.startCandidate2);

        for (int i = 0; i < this.rpCand1.size(); i++) {
            this.globalStep.addCandidateToDelete(this.rpCand1.get(i), this.startCandidate);
        }

        for (int i = 0; i < this.rpCand2.size(); i++) {
            this.globalStep.addCandidateToDelete(this.rpCand2.get(i), this.startCandidate2);
        }

        String del = this.globalStep.getCandidateString();
        Integer oldLength = this.deletesMap.get(del);
        if (oldLength == null || oldLength > this.stackLevel) {
            this.deletesMap.put(del, this.stackLevel);
            int[] newChain = new int[this.stackLevel + 1];
            System.arraycopy(this.chain, 0, newChain, 0, newChain.length);
            this.globalStep.addChain(0, this.stackLevel, newChain);
            this.steps.add((SolutionStep) this.globalStep.clone());
        }
    }

    private void checkNiceLoop(int lastLink, int chainIndex) {
        int endIndex = Chain.getSCellIndex(lastLink);
        if (endIndex == this.startIndex) {
            this.globalStep.reset();
            this.globalStep.setType(SolutionType.DISCONTINUOUS_NICE_LOOP);
            boolean firstLinkStrong = Chain.isSStrong(this.chain[1]);
            boolean lastLinkStrong = Chain.isSStrong(lastLink);
            int endCandidate = Chain.getSCandidate(lastLink);
            if (!firstLinkStrong && !lastLinkStrong && this.startCandidate == endCandidate) {
                this.globalStep.addCandidateToDelete(this.startIndex, this.startCandidate);
            } else if (firstLinkStrong && lastLinkStrong && this.startCandidate == endCandidate) {
                int[] cands = this.sudoku.getAllCandidates(this.startIndex);

                for (int i = 0; i < cands.length; i++) {
                    if (cands[i] != this.startCandidate) {
                        this.globalStep.addCandidateToDelete(this.startIndex, cands[i]);
                    }
                }
            } else if (firstLinkStrong != lastLinkStrong && this.startCandidate != endCandidate) {
                if (!firstLinkStrong) {
                    this.globalStep.addCandidateToDelete(this.startIndex, this.startCandidate);
                } else {
                    this.globalStep.addCandidateToDelete(this.startIndex, endCandidate);
                }
            } else if (!firstLinkStrong && !lastLinkStrong && this.sudoku.getAnzCandidates(this.startIndex) == 2 && this.startCandidate != endCandidate
                    || firstLinkStrong && lastLinkStrong && this.startCandidate != endCandidate
                    || firstLinkStrong != lastLinkStrong && this.startCandidate == endCandidate) {
                this.globalStep.setType(SolutionType.CONTINUOUS_NICE_LOOP);

                for (int i = 1; i <= chainIndex; i++) {
                    if (Chain.isSStrong(this.chain[i])
                            && i <= chainIndex - 2
                            && Chain.getSCellIndex(this.chain[i - 1]) != Chain.getSCellIndex(this.chain[i])
                            && !Chain.isSStrong(this.chain[i + 1])
                            && Chain.getSCellIndex(this.chain[i]) == Chain.getSCellIndex(this.chain[i + 1])
                            && Chain.isSStrong(this.chain[i + 2])
                            && Chain.getSCellIndex(this.chain[i + 1]) != Chain.getSCellIndex(this.chain[i + 2])) {
                        int c1 = Chain.getSCandidate(this.chain[i]);
                        int c2 = Chain.getSCandidate(this.chain[i + 2]);
                        int[] cands = this.sudoku.getAllCandidates(Chain.getSCellIndex(this.chain[i]));

                        for (int j = 0; j < cands.length; j++) {
                            if (cands[j] != c1 && cands[j] != c2) {
                                this.globalStep.addCandidateToDelete(Chain.getSCellIndex(this.chain[i]), cands[j]);
                            }
                        }
                    }

                    if (!Chain.isSStrong(this.chain[i]) && Chain.getSCellIndex(this.chain[i - 1]) != Chain.getSCellIndex(this.chain[i])) {
                        this.checkBuddies.set(Sudoku2.buddies[Chain.getSCellIndex(this.chain[i - 1])]);
                        this.checkBuddies.and(Sudoku2.buddies[Chain.getSCellIndex(this.chain[i])]);
                        this.checkBuddies.andNot(this.chainSet);
                        this.checkBuddies.remove(endIndex);
                        this.checkBuddies.and(this.finder.getCandidates()[Chain.getSCandidate(this.chain[i])]);
                        if (!this.checkBuddies.isEmpty()) {
                            for (int j = 0; j < this.checkBuddies.size(); j++) {
                                this.globalStep.addCandidateToDelete(this.checkBuddies.get(j), Chain.getSCandidate(this.chain[i]));
                            }
                        }
                    }
                }
            }

            if (this.globalStep.getCandidatesToDelete().size() > 0) {
                String del = this.globalStep.getCandidateString();
                Integer oldLength = this.deletesMap.get(del);
                if (oldLength != null && oldLength <= chainIndex) {
                    return;
                }

                this.deletesMap.put(del, chainIndex);
                int[] newChain = new int[chainIndex + 1];
                System.arraycopy(this.chain, 0, newChain, 0, newChain.length);
                this.globalStep.addChain(0, chainIndex, newChain);
                this.steps.add((SolutionStep) this.globalStep.clone());
            }
        }
    }

    private void getAllLinks(int type) {
        if (!this.turbotOrXSeen || type != 4 && type != 0 || this.lastStepNumber != this.finder.getStepNumber()) {
            if (type != 4 && type != 0) {
                this.turbotOrXSeen = false;
            } else {
                this.turbotOrXSeen = true;
            }

            this.lastStepNumber = this.finder.getStepNumber();
            long nanos = System.nanoTime();
            int index = 0;
            int startEndIndex = 0;
            byte[][] free = this.sudoku.getFree();

            for (int cellIndex = 0; cellIndex < this.sudoku.getCells().length; cellIndex++) {
                short cell = this.sudoku.getCell(cellIndex);
                if (cell != 0 && (type != 2 && type != 1 || Sudoku2.ANZ_VALUES[cell] == 2)) {
                    for (int cellCandidate = 1; cellCandidate <= 9; cellCandidate++) {
                        startEndIndex = cellIndex * 10 + cellCandidate;
                        if (!this.sudoku.isCandidate(cellIndex, cellCandidate)) {
                            this.startIndices[startEndIndex] = index;
                            this.endIndices[startEndIndex] = index;
                        } else {
                            this.startIndices[startEndIndex] = index;
                            int[] cands = Sudoku2.POSSIBLE_VALUES[cell];
                            if (Sudoku2.ANZ_VALUES[cell] == 2) {
                                if (type != 0 && type != 4) {
                                    int cand = cands[0];
                                    if (cand == cellCandidate) {
                                        cand = cands[1];
                                    }

                                    this.links[index++] = Chain.makeSEntry(cellIndex, cand, true);
                                }
                            } else if (type == 3) {
                                for (int k = 0; k < cands.length; k++) {
                                    if (cands[k] != cellCandidate) {
                                        this.links[index++] = Chain.makeSEntry(cellIndex, cands[k], false);
                                    }
                                }
                            }

                            for (int constr = 0; constr < Sudoku2.CONSTRAINTS[cellIndex].length; constr++) {
                                boolean strong = false;
                                if (free[Sudoku2.CONSTRAINTS[cellIndex][constr]][cellCandidate] == 2 && (type == 0 || type == 4 || type == 3)) {
                                    strong = true;
                                }

                                int[] indices = Sudoku2.ALL_UNITS[Sudoku2.CONSTRAINTS[cellIndex][constr]];

                                for (int k = 0; k < indices.length; k++) {
                                    if (indices[k] != cellIndex && this.sudoku.isCandidate(indices[k], cellCandidate)) {
                                        short cell2 = this.sudoku.getCell(indices[k]);
                                        if ((type != 2 || cell2 == cell)
                                                && (type != 1 || Sudoku2.ANZ_VALUES[cell2] == 2)
                                                && (
                                                constr != 2
                                                        || Sudoku2.getLine(cellIndex) != Sudoku2.getLine(indices[k]) && Sudoku2.getCol(cellIndex) != Sudoku2.getCol(indices[k])
                                        )) {
                                            this.links[index++] = Chain.makeSEntry(indices[k], cellCandidate, strong);
                                        }
                                    }
                                }
                            }

                            this.endIndices[startEndIndex] = index;
                        }
                    }
                }
            }

            switch (type) {
                case 0:
                    this.linkXNanos = this.linkXNanos + (System.nanoTime() - nanos);
                    break;
                case 1:
                    this.linkYNanos = this.linkYNanos + (System.nanoTime() - nanos);
                    break;
                case 2:
                    this.linkRNanos = this.linkRNanos + (System.nanoTime() - nanos);
                case 3:
                default:
                    break;
                case 4:
                    this.linkTNanos = this.linkTNanos + (System.nanoTime() - nanos);
            }
        }
    }

    private void printSet(String text, long m1, long m2) {
        SudokuSetBase set = new SudokuSetBase();
        set.setMask1(m1);
        set.setMask2(m2);
        set.setInitialized(false);
        System.out.println(text + ": " + set);
    }

    protected void printStatistics() {
        double danzT = this.anzT * 1000.0;
        double danzR = this.anzR * 1000.0;
        double danzX = this.anzX * 1000.0;
        double danzY = this.anzY * 1000.0;
        System.out
                .printf(
                        "Turbot:   %6d/%6.2fus/%6.2fus/%6.2fus\r\n",
                        this.anzT,
                        this.chainTNanos / danzT,
                        this.linkTNanos / danzT,
                        (this.chainTNanos - this.linkTNanos) / danzT
                );
        System.out
                .printf(
                        "RP:       %6d/%6.2fus/%6.2fus/%6.2fus\r\n",
                        this.anzR,
                        this.chainRNanos / danzR,
                        this.linkRNanos / danzR,
                        (this.chainRNanos - this.linkRNanos) / danzR
                );
        System.out
                .printf(
                        "X-Chain:  %6d/%6.2fus/%6.2fus/%6.2fus\r\n",
                        this.anzX,
                        this.chainXNanos / danzX,
                        this.linkXNanos / danzX,
                        (this.chainXNanos - this.linkXNanos) / danzX
                );
        System.out
                .printf(
                        "XY-Chain: %6d/%6.2fus/%6.2fus/%6.2fus\r\n",
                        this.anzY,
                        this.chainYNanos / danzY,
                        this.linkYNanos / danzY,
                        (this.chainYNanos - this.linkYNanos) / danzY
                );
    }

    static class ChainComparator implements Comparator<SolutionStep> {
        public int compare(SolutionStep o1, SolutionStep o2) {
            return o1.getType().ordinal() != o2.getType().ordinal() ? o1.getType().ordinal() - o2.getType().ordinal() : o1.compareTo(o2);
        }
    }

    class StackEntry {
        int cellIndex;
        int candidate;
        boolean strongOnly;
        int aktIndex;
        int endIndex;
    }
}
