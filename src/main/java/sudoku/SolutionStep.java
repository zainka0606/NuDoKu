package sudoku;

import solver.Als;
import solver.RestrictedCommon;

import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SolutionStep implements Comparable<SolutionStep>, Cloneable {
    private static final String[] entityNames = new String[]{
            ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.block"),
            ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.line"),
            ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.col"),
            ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.cell")
    };
    private static final String[] entityShortNames = new String[]{"b", "r", "c", ""};
    private static final DecimalFormat FISH_FORMAT = new DecimalFormat("#00");
    private SolutionType type;
    private SolutionType subType;
    private int entity;
    private int entityNumber;
    private int entity2;
    private int entity2Number;
    private boolean isSiamese;
    private int progressScoreSingles = -1;
    private int progressScoreSinglesOnly = -1;
    private int progressScore = -1;
    private List<Integer> values = new ArrayList<>();
    private List<Integer> indices = new ArrayList<>();
    private List<Candidate> candidatesToDelete = new ArrayList<>();
    private List<Candidate> cannibalistic = new ArrayList<>();
    private List<Candidate> fins = new ArrayList<>();
    private List<Candidate> endoFins = new ArrayList<>();
    private List<Entity> baseEntities = new ArrayList<>();
    private List<Entity> coverEntities = new ArrayList<>();
    private List<Chain> chains = new ArrayList<>();
    private List<AlsInSolutionStep> alses = new ArrayList<>();
    private SortedMap<Integer, Integer> colorCandidates = new TreeMap<>();
    private List<RestrictedCommon> restrictedCommons = new ArrayList<>();
    private SudokuSet potentialCannibalisticEliminations = new SudokuSet();
    private SudokuSet potentialEliminations = new SudokuSet();

    public SolutionStep() {
    }

    public SolutionStep(SolutionType type) {
        this.setType(type);
    }

    public static String getCellPrint(int index) {
        return getCellPrint(index, true);
    }

    public static String getCellPrint(int index, boolean withParen) {
        return withParen
                ? "[r" + (Sudoku2.getLine(index) + 1) + "c" + (Sudoku2.getCol(index) + 1) + "]"
                : "r" + (Sudoku2.getLine(index) + 1) + "c" + (Sudoku2.getCol(index) + 1);
    }

    public static String getCompactCellPrint(int index1, int index2, int index3) {
        TreeSet<Integer> tmpSet = new TreeSet<>();
        tmpSet.add(index1);
        tmpSet.add(index2);
        if (index3 != -1) {
            tmpSet.add(index3);
        }

        return getCompactCellPrint(tmpSet);
    }

    public static String getCompactCellPrint(SudokuSet set) {
        TreeSet<Integer> tmpSet = new TreeSet<>();

        for (int i = 0; i < set.size(); i++) {
            tmpSet.add(set.get(i));
        }

        return getCompactCellPrint(tmpSet);
    }

    public static String getCompactCellPrint(List<Integer> indices) {
        return getCompactCellPrint(indices, 0, indices.size() - 1);
    }

    public static String getCompactCellPrint(List<Integer> indices, int start, int end) {
        TreeSet<Integer> tmpSet = new TreeSet<>();

        for (int i = start; i <= end; i++) {
            tmpSet.add(indices.get(i));
        }

        return getCompactCellPrint(tmpSet);
    }

    public static String getCompactCellPrint(TreeSet<Integer> tmpSet) {
        StringBuilder tmp = new StringBuilder();
        boolean first = true;

        while (tmpSet.size() > 0) {
            int index = tmpSet.pollFirst();
            int line = Sudoku2.getLine(index);
            int col = Sudoku2.getCol(index);
            int anzLines = 1;
            int anzCols = 1;
            if (first) {
                first = false;
            } else {
                tmp.append(",");
            }

            tmp.append(getCellPrint(index));
            Iterator<Integer> it = tmpSet.iterator();

            while (it.hasNext()) {
                int i1 = it.next();
                int l1 = Sudoku2.getLine(i1);
                int c1 = Sudoku2.getCol(i1);
                if (l1 == line && anzLines == 1) {
                    int pIndex = tmp.lastIndexOf("]");
                    tmp.insert(pIndex, (int) (c1 + 1));
                    it.remove();
                    anzCols++;
                } else if (c1 == col && anzCols == 1) {
                    int pIndex = tmp.lastIndexOf("c");
                    tmp.insert(pIndex, (int) (l1 + 1));
                    it.remove();
                    anzLines++;
                }
            }
        }

        int index = 0;

        while ((index = tmp.indexOf("[")) != -1) {
            tmp.deleteCharAt(index);
        }

        while ((index = tmp.indexOf("]")) != -1) {
            tmp.deleteCharAt(index);
        }

        return tmp.toString();
    }

    public static String getStepName(SolutionType type) {
        return type.getStepName();
    }

    public static String getStepName(int type) {
        return SolutionType.values()[type].getStepName();
    }

    public static String getAls(Als als) {
        return getAls(als, true);
    }

    public static String getAls(Als als, boolean withCandidates) {
        StringBuilder tmp = new StringBuilder();
        TreeSet<Integer> set = new TreeSet<>();

        for (int i = 0; i < als.indices.size(); i++) {
            set.add(als.indices.get(i));
        }

        tmp.append(getCompactCellPrint(set));
        if (withCandidates) {
            tmp.append(" {");
            int[] cands = Sudoku2.POSSIBLE_VALUES[als.candidates];

            for (int i = 0; i < cands.length; i++) {
                tmp.append(cands[i]);
            }

            tmp.append("}");
        }

        return tmp.toString();
    }

    @Override
    public Object clone() {
        SolutionStep newStep = null;

        try {
            newStep = (SolutionStep) super.clone();
            newStep.type = this.type;
            newStep.entity = this.entity;
            newStep.entityNumber = this.entityNumber;
            newStep.entity2 = this.entity2;
            newStep.entity2Number = this.entity2Number;
            newStep.isSiamese = this.isSiamese;
            newStep.progressScoreSingles = this.progressScoreSingles;
            newStep.progressScoreSinglesOnly = this.progressScoreSinglesOnly;
            newStep.progressScore = this.progressScore;
            newStep.values = (List<Integer>) ((ArrayList) this.values).clone();
            newStep.indices = (List<Integer>) ((ArrayList) this.indices).clone();
            newStep.candidatesToDelete = (List<Candidate>) ((ArrayList) this.candidatesToDelete).clone();
            newStep.cannibalistic = (List<Candidate>) ((ArrayList) this.cannibalistic).clone();
            newStep.fins = (List<Candidate>) ((ArrayList) this.fins).clone();
            newStep.endoFins = (List<Candidate>) ((ArrayList) this.endoFins).clone();
            newStep.baseEntities = (List<Entity>) ((ArrayList) this.baseEntities).clone();
            newStep.coverEntities = (List<Entity>) ((ArrayList) this.coverEntities).clone();
            newStep.chains = (List<Chain>) ((ArrayList) this.chains).clone();
            newStep.alses = (List<AlsInSolutionStep>) ((ArrayList) this.alses).clone();
            newStep.colorCandidates = (SortedMap<Integer, Integer>) ((TreeMap) this.getColorCandidates()).clone();
            newStep.restrictedCommons = (List<RestrictedCommon>) ((ArrayList) this.restrictedCommons).clone();
            newStep.potentialCannibalisticEliminations = this.potentialCannibalisticEliminations.clone();
            newStep.potentialEliminations = this.potentialEliminations.clone();
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error while cloning", ex);
        }

        return newStep;
    }

    public void reset() {
        this.type = SolutionType.HIDDEN_SINGLE;
        this.entity = 0;
        this.entityNumber = 0;
        this.entity2 = 0;
        this.entity2Number = 0;
        this.isSiamese = false;
        this.progressScoreSingles = -1;
        this.progressScoreSinglesOnly = -1;
        this.progressScore = -1;
        this.values.clear();
        this.indices.clear();
        this.candidatesToDelete.clear();
        this.cannibalistic.clear();
        this.fins.clear();
        this.endoFins.clear();
        this.baseEntities.clear();
        this.coverEntities.clear();
        this.chains.clear();
        this.alses.clear();
        this.colorCandidates.clear();
        this.restrictedCommons.clear();
        this.potentialCannibalisticEliminations.clear();
        this.potentialEliminations.clear();
    }

    public StringBuffer getForcingChainString(Chain chain) {
        return this.getForcingChainString(chain.getChain(), chain.getStart(), chain.getEnd(), true);
    }

    public StringBuffer getForcingChainString(int[] chain, int start, int end, boolean weakLinks) {
        StringBuffer tmp = new StringBuffer();
        boolean inMin = false;
        this.appendForcingChainEntry(tmp, chain[start]);

        for (int i = start + 1; i <= end - 1; i++) {
            boolean blank = true;
            if (chain[i] == Integer.MIN_VALUE) {
                tmp.append(")");
                inMin = false;
            } else if (weakLinks
                    || Chain.isSStrong(chain[i])
                    || chain[i] <= 0 && (chain[i] >= 0 || chain[i + 1] >= 0 || chain[i + 1] == Integer.MIN_VALUE)
                    || Chain.getSNodeType(chain[i]) != 0) {
                if (chain[i] < 0 && !inMin) {
                    tmp.append(" (");
                    inMin = true;
                    blank = false;
                }

                if (chain[i] > 0 && inMin) {
                    tmp.append(")");
                    inMin = false;
                }

                if (blank) {
                    tmp.append(" ");
                }

                this.appendForcingChainEntry(tmp, chain[i]);
            }
        }

        tmp.append(" ");
        this.appendForcingChainEntry(tmp, chain[end]);
        return tmp;
    }

    public void appendForcingChainEntry(StringBuffer buf, int chainEntry) {
        int entry = chainEntry < 0 ? -chainEntry : chainEntry;
        switch (Chain.getSNodeType(entry)) {
            case 0:
                buf.append(getCellPrint(Chain.getSCellIndex(entry), false));
                break;
            case 1:
                buf.append(getCompactCellPrint(Chain.getSCellIndex(entry), Chain.getSCellIndex2(entry), Chain.getSCellIndex3(entry)));
                break;
            case 2:
                int alsIndex = Chain.getSCellIndex2(entry);
                if (alsIndex >= 0 && alsIndex < this.alses.size()) {
                    buf.append("ALS:");
                    this.getAls(buf, alsIndex, false);
                } else {
                    buf.append("UNKNOWN ALS");
                }
        }

        if (!Chain.isSStrong(entry)) {
            buf.append("<>");
        } else {
            buf.append("=");
        }

        buf.append(Chain.getSCandidate(entry));
    }

    public StringBuffer getChainString(Chain chain) {
        return this.getChainString(chain.getChain(), chain.getStart(), chain.getEnd(), false, true, true, false);
    }

    public StringBuffer getChainString(Chain chain, boolean internalFormat) {
        return this.getChainString(chain.getChain(), chain.getStart(), chain.getEnd(), true, true, true, internalFormat);
    }

    public StringBuffer getChainString(int[] chain, int start, int end, boolean alternate, boolean up) {
        return this.getChainString(chain, start, end, alternate, up, true, false);
    }

    public StringBuffer getChainString(int[] chain, int start, int end, boolean alternate, boolean up, boolean asNiceLoop, boolean internalFormat) {
        StringBuffer tmp = new StringBuffer();
        boolean isStrong = false;
        int lastIndex = -1;
        if (up) {
            for (int i = start; i <= end; i++) {
                if (internalFormat) {
                    if (i > start) {
                        tmp.append("-");
                    }

                    tmp.append(chain[i]);
                } else {
                    if (i == start + 1) {
                        isStrong = Chain.isSStrong(chain[i]);
                    } else {
                        isStrong = !isStrong;
                    }

                    if (!asNiceLoop || Chain.getSCellIndex(chain[i]) != lastIndex) {
                        lastIndex = Chain.getSCellIndex(chain[i]);
                        if (i > start) {
                            int cand = Chain.getSCandidate(chain[i]);
                            if (Chain.isSStrong(chain[i]) && (!alternate || isStrong)) {
                                tmp.append(" =");
                                tmp.append(cand);
                                tmp.append("= ");
                            } else {
                                tmp.append(" -");
                                tmp.append(cand);
                                tmp.append("- ");
                            }
                        }

                        switch (Chain.getSNodeType(chain[i])) {
                            case 0:
                                tmp.append(getCellPrint(Chain.getSCellIndex(chain[i]), false));
                                break;
                            case 1:
                                tmp.append(getCompactCellPrint(Chain.getSCellIndex(chain[i]), Chain.getSCellIndex2(chain[i]), Chain.getSCellIndex3(chain[i])));
                                break;
                            case 2:
                                int alsIndex = Chain.getSCellIndex2(chain[i]);
                                if (alsIndex < this.alses.size()) {
                                    tmp.append("ALS:");
                                    this.getAls(tmp, alsIndex, false);
                                } else {
                                    tmp.append("UNKNOWN ALS");
                                }
                                break;
                            default:
                                tmp.append("INV");
                        }
                    }
                }
            }
        } else {
            for (int i = end; i >= start; i--) {
                if (internalFormat) {
                    if (i > start) {
                        tmp.append("-");
                    }

                    tmp.append(chain[i]);
                } else {
                    if (i == end - 1) {
                        isStrong = Chain.isSStrong(chain[i + 1]);
                    } else {
                        isStrong = !isStrong;
                    }

                    if (Chain.getSCellIndex(chain[i + 1]) != lastIndex) {
                        lastIndex = Chain.getSCellIndex(chain[i + 1]);
                        if (i < end) {
                            int cand = Chain.getSCandidate(chain[i]);
                            if (Chain.isSStrong(chain[i + 1]) && (!alternate || isStrong)) {
                                tmp.append(" =");
                                tmp.append(cand);
                                tmp.append("= ");
                            } else {
                                tmp.append(" -");
                                tmp.append(cand);
                                tmp.append("- ");
                            }
                        }

                        switch (Chain.getSNodeType(chain[i])) {
                            case 0:
                                tmp.append(getCellPrint(Chain.getSCellIndex(chain[i]), false));
                                break;
                            case 1:
                                tmp.append(getCompactCellPrint(Chain.getSCellIndex(chain[i]), Chain.getSCellIndex2(chain[i]), Chain.getSCellIndex3(chain[i])));
                                break;
                            case 2:
                                int alsIndex = Chain.getSCellIndex2(chain[i]);
                                if (alsIndex < this.alses.size()) {
                                    tmp.append("ALS:");
                                    this.getAls(tmp, alsIndex, false);
                                } else {
                                    tmp.append("UNKNOWN ALS");
                                }
                        }
                    }
                }
            }
        }

        return tmp;
    }

    public String getValueIndexString() {
        StringBuilder tmp = new StringBuilder();

        for (int i = 0; i < this.values.size(); i++) {
            int value = this.values.get(i);

            for (int j = 0; j < this.indices.size(); j++) {
                int index = this.indices.get(j);
                tmp.append(value);
                tmp.append(Integer.toString(Sudoku2.getLine(index) + 1));
                tmp.append(Integer.toString(Sudoku2.getCol(index) + 1));
                tmp.append(" ");
            }
        }

        return tmp.toString().trim();
    }

    public String getSingleCandidateString() {
        return this.getStepName() + ": " + getCompactCellPrint(this.indices) + "=" + this.values.get(0);
    }

    public String getCandidateString() {
        return this.getCandidateString(false, false);
    }

    public String getCandidateString(boolean library) {
        return this.getCandidateString(library, false);
    }

    public String getCandidateString(boolean library, boolean statistics) {
        Collections.sort(this.candidatesToDelete);
        this.eliminateDoubleCandidatesToDelete();
        StringBuilder candBuff = new StringBuilder();
        int lastCand = -1;
        StringBuffer delPos = new StringBuffer();

        for (Candidate cand : this.candidatesToDelete) {
            if (cand.getValue() != lastCand) {
                if (lastCand != -1) {
                    candBuff.append("/");
                }

                candBuff.append(cand.getValue());
                lastCand = cand.getValue();
            }

            delPos.append(" ");
            if (library) {
                delPos.append(Integer.toString(cand.getValue()))
                        .append(Integer.toString(Sudoku2.getLine(cand.getIndex()) + 1))
                        .append(Integer.toString(Sudoku2.getCol(cand.getIndex()) + 1));
            }
        }

        if (library) {
            return delPos.toString().trim();
        }

        delPos = new StringBuffer();
        this.getCandidatesToDelete(delPos);
        delPos.delete(0, 4);
        if (statistics) {
            return candBuff.toString() + " (" + this.getAnzCandidatesToDelete() + ")" + " (0/0)";
        }

        String tmpStepName = this.getStepName();
        if (this.isSiamese) {
            tmpStepName = ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.siamese") + " " + this.getStepName();
        }

        return candBuff.toString() + " (" + this.getAnzCandidatesToDelete() + "):" + delPos.toString() + " (" + tmpStepName + ")";
    }

    private void eliminateDoubleCandidatesToDelete() {
        Set<Candidate> candSet = new TreeSet<>();

        for (int i = 0; i < this.candidatesToDelete.size(); i++) {
            candSet.add(this.candidatesToDelete.get(i));
        }

        this.candidatesToDelete.clear();

        for (Candidate cand : candSet) {
            this.candidatesToDelete.add(cand);
        }
    }

    public void addValue(int value) {
        if (value >= 1 && value <= 9) {
            this.values.add(value);
        } else {
            throw new RuntimeException(ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.invalid_setValue") + " (" + value + ")");
        }
    }

    public void addIndex(int index) {
        if (index >= 0 && index <= 80) {
            this.indices.add(index);
        } else {
            throw new RuntimeException(ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.invalid_setIndex") + " (" + index + ")");
        }
    }

    public void addCandidateToDelete(Candidate cand) {
        this.candidatesToDelete.add(cand);
    }

    public void addCandidateToDelete(int index, int candidate) {
        this.candidatesToDelete.add(new Candidate(index, candidate));
    }

    public void addCannibalistic(Candidate cand) {
        this.cannibalistic.add(cand);
    }

    public void addCannibalistic(int index, int candidate) {
        this.cannibalistic.add(new Candidate(index, candidate));
    }

    public void addFin(int index, int candidate) {
        this.addFin(new Candidate(index, candidate));
    }

    public void addFin(Candidate fin) {
        this.fins.add(fin);
    }

    public void addEndoFin(int index, int candidate) {
        this.endoFins.add(new Candidate(index, candidate));
    }

    public int getAnzCandidatesToDelete() {
        SortedSet<Candidate> tmpSet = new TreeSet<>();

        for (int i = 0; i < this.candidatesToDelete.size(); i++) {
            tmpSet.add(this.candidatesToDelete.get(i));
        }

        int anz = tmpSet.size();
        tmpSet.clear();
        SortedSet<Candidate> var3 = null;
        return anz;
    }

    public int getAnzSet() {
        if (this.type.isSingle()) {
            return 1;
        } else if ((
                this.type == SolutionType.FORCING_CHAIN
                        || this.type == SolutionType.FORCING_CHAIN_CONTRADICTION
                        || this.type == SolutionType.FORCING_CHAIN_VERITY
                        || this.type == SolutionType.FORCING_NET
                        || this.type == SolutionType.FORCING_NET_CONTRADICTION
                        || this.type == SolutionType.FORCING_NET_VERITY
        )
                && this.indices.size() > 0) {
            return 1;
        } else {
            return this.type == SolutionType.TEMPLATE_SET ? this.indices.size() : 0;
        }
    }

    public SolutionType getType() {
        return this.type;
    }

    public final void setType(SolutionType type) {
        boolean found = false;

        for (SolutionType t : SolutionType.values()) {
            if (t == type) {
                found = true;
                break;
            }
        }

        if (!found) {
            throw new RuntimeException(ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.invalid_setType") + " (" + type + ")");
        }

        this.type = type;
    }

    public List<Integer> getValues() {
        return this.values;
    }

    public void setValues(List<Integer> values) {
        this.values = values;
    }

    public List<Integer> getIndices() {
        return this.indices;
    }

    public void setIndices(List<Integer> indices) {
        this.indices = indices;
    }

    public List<Candidate> getCandidatesToDelete() {
        return this.candidatesToDelete;
    }

    public void setCandidatesToDelete(List<Candidate> candidatesToDelete) {
        this.candidatesToDelete = candidatesToDelete;
    }

    public List<Candidate> getCannibalistic() {
        return this.cannibalistic;
    }

    public void setCannibalistic(List<Candidate> cannibalistic) {
        this.cannibalistic = cannibalistic;
    }

    public List<Candidate> getFins() {
        return this.fins;
    }

    public void setFins(List<Candidate> fins) {
        this.fins = fins;
    }

    public List<Candidate> getEndoFins() {
        return this.endoFins;
    }

    public void setEndoFins(List<Candidate> endoFins) {
        this.endoFins = endoFins;
    }

    public String getStepName() {
        return this.type.getStepName();
    }

    public String getEntityName(int name) {
        return entityNames[name];
    }

    public String getEntityShortName(int name) {
        return entityShortNames[name];
    }

    public String getEntityName() {
        return entityNames[this.entity];
    }

    public String getEntityName2() {
        return entityNames[this.entity2];
    }

    public String getEntityShortName() {
        return entityShortNames[this.entity];
    }

    public String getEntityShortNameNumber() {
        return this.entity == 3 ? getCellPrint(this.entityNumber, false) : entityShortNames[this.entity] + Integer.toString(this.entityNumber + 1);
    }

    public String getEntityShortName2() {
        return entityShortNames[this.entity2];
    }

    @Override
    public String toString() {
        return this.toString(2);
    }

    public String toString(int art) {
        String str = null;
        int index = 0;
        switch (this.type) {
            case FULL_HOUSE:
            case HIDDEN_SINGLE:
            case NAKED_SINGLE:
                index = this.indices.get(0);
                str = this.getStepName();
                if (art == 1) {
                    str = str + ": " + this.values.get(0);
                } else if (art == 2) {
                    str = str + ": " + getCellPrint(index, false) + "=" + this.values.get(0);
                }
                break;
            case HIDDEN_QUADRUPLE:
            case NAKED_QUADRUPLE:
            case HIDDEN_TRIPLE:
            case NAKED_TRIPLE:
            case LOCKED_TRIPLE:
            case HIDDEN_PAIR:
            case NAKED_PAIR:
            case LOCKED_PAIR:
                index = this.indices.get(0);
                str = this.getStepName();
                StringBuffer tmp = new StringBuffer(str);
                if (art >= 1) {
                    tmp.append(": ");
                    if (this.type == SolutionType.HIDDEN_PAIR || this.type == SolutionType.NAKED_PAIR || this.type == SolutionType.LOCKED_PAIR) {
                        tmp.append(this.values.get(0));
                        tmp.append(",");
                        tmp.append(this.values.get(1));
                    } else if (this.type == SolutionType.HIDDEN_TRIPLE || this.type == SolutionType.NAKED_TRIPLE || this.type == SolutionType.LOCKED_TRIPLE) {
                        tmp.append(this.values.get(0));
                        tmp.append(",");
                        tmp.append(this.values.get(1));
                        tmp.append(",");
                        tmp.append(this.values.get(2));
                    } else if (this.type == SolutionType.HIDDEN_QUADRUPLE || this.type == SolutionType.NAKED_QUADRUPLE) {
                        tmp.append(this.values.get(0));
                        tmp.append(",");
                        tmp.append(this.values.get(1));
                        tmp.append(",");
                        tmp.append(this.values.get(2));
                        tmp.append(",");
                        tmp.append(this.values.get(3));
                    }
                }

                if (art >= 2) {
                    tmp.append(" ");
                    tmp.append(ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.in"));
                    tmp.append(" ");
                    tmp.append(getCompactCellPrint(this.indices));
                    this.getCandidatesToDelete(tmp);
                }

                str = tmp.toString();
                break;
            case LOCKED_CANDIDATES:
            case LOCKED_CANDIDATES_1:
            case LOCKED_CANDIDATES_2:
                str = this.getStepName();
                if (art >= 1) {
                    str = str + ": " + this.values.get(0);
                }

                if (art >= 2) {
                    str = str
                            + " "
                            + ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.in")
                            + " "
                            + this.getEntityShortName()
                            + this.getEntityNumber();
                    StringBuffer var45 = new StringBuffer(str);
                    this.getCandidatesToDelete(var45);
                    str = var45.toString();
                }
                break;
            case SKYSCRAPER:
            case TWO_STRING_KITE:
            case DUAL_TWO_STRING_KITE:
                str = this.getStepName();
                if (art >= 1) {
                    str = str + ": " + this.values.get(0);
                }

                if (art >= 2) {
                    str = str + " " + ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.in") + " " + getCompactCellPrint(this.indices, 0, 1);
                    if (this.type == SolutionType.DUAL_TWO_STRING_KITE) {
                        str = str + "/" + ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.in") + " " + getCompactCellPrint(this.indices, 4, 5);
                    }

                    str = str
                            + " ("
                            + ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.connected_by")
                            + " "
                            + getCompactCellPrint(this.indices, 2, 3)
                            + ")";
                    StringBuffer var44 = new StringBuffer(str);
                    this.getCandidatesToDelete(var44);
                    str = var44.toString();
                }
                break;
            case EMPTY_RECTANGLE:
            case DUAL_EMPTY_RECTANGLE:
                str = this.getStepName();
                if (art >= 1) {
                    str = str + ": " + this.values.get(0);
                }

                if (art >= 2) {
                    str = str
                            + " "
                            + ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.in")
                            + " "
                            + this.getEntityShortName()
                            + this.getEntityNumber()
                            + " ("
                            + getCompactCellPrint(this.indices, 0, 1);
                    if (this.type == SolutionType.DUAL_EMPTY_RECTANGLE) {
                        str = str + "/" + getCompactCellPrint(this.indices, 2, 3);
                    }

                    str = str + ")";
                    StringBuffer var43 = new StringBuffer(str);
                    this.getCandidatesToDelete(var43);
                    str = var43.toString();
                }
                break;
            case W_WING:
                str = this.getStepName();
                if (art >= 1) {
                    str = str + ": " + this.values.get(0) + "/" + this.values.get(1);
                }

                if (art >= 2) {
                    StringBuffer var42 = new StringBuffer(str);
                    var42.append(" ");
                    var42.append(ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.in"));
                    var42.append(" ");
                    var42.append(getCompactCellPrint(this.indices, 0, 1));
                    var42.append(" ");
                    var42.append(ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.connected_by"));
                    var42.append(" ");
                    var42.append(this.values.get(1));
                    var42.append(" ");
                    var42.append(ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.in"));
                    var42.append(" ");
                    this.getFinSet(var42, this.fins, false);
                    this.getCandidatesToDelete(var42);
                    str = var42.toString();
                }
                break;
            case XY_WING:
            case XYZ_WING:
                str = this.getStepName();
                if (art >= 1) {
                    str = str + ": " + this.values.get(0) + "/" + this.values.get(1);
                }

                if (art >= 2) {
                    str = str
                            + "/"
                            + this.values.get(2)
                            + " "
                            + ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.in")
                            + " "
                            + getCompactCellPrint(this.indices);
                    StringBuffer var41 = new StringBuffer(str);
                    this.getCandidatesToDelete(var41);
                    str = var41.toString();
                }
                break;
            case SIMPLE_COLORS:
            case SIMPLE_COLORS_TRAP:
            case SIMPLE_COLORS_WRAP:
            case MULTI_COLORS:
            case MULTI_COLORS_1:
            case MULTI_COLORS_2:
                str = this.getStepName();
                if (art >= 1) {
                    str = str + ": " + this.values.get(0);
                }

                if (art >= 2) {
                    StringBuffer var40 = new StringBuffer(str);
                    this.getColorCellPrint(var40);
                    this.getCandidatesToDelete(var40);
                    str = var40.toString();
                }
                break;
            case X_CHAIN:
            case XY_CHAIN:
            case REMOTE_PAIR:
            case TURBOT_FISH:
            case NICE_LOOP:
            case CONTINUOUS_NICE_LOOP:
            case DISCONTINUOUS_NICE_LOOP:
            case GROUPED_NICE_LOOP:
            case GROUPED_CONTINUOUS_NICE_LOOP:
            case GROUPED_DISCONTINUOUS_NICE_LOOP:
            case AIC:
            case GROUPED_AIC:
                str = this.getStepName();
                if (art >= 1) {
                    if (this.type == SolutionType.REMOTE_PAIR) {
                        str = str + ": " + this.values.get(0) + "/" + this.values.get(1);
                    } else {
                        str = str + ": " + this.getCandidatesToDeleteDigits();
                    }
                }

                if (art >= 2) {
                    List<Chain> dummy1 = this.getChains();
                    StringBuffer tmpChain = this.getChainString(this.getChains().get(0));
                    if (this.type == SolutionType.CONTINUOUS_NICE_LOOP || this.type == SolutionType.GROUPED_CONTINUOUS_NICE_LOOP) {
                        Chain ch = this.getChains().get(0);
                        int start = ch.getStart();
                        int cellIndex = ch.getCellIndex(start);

                        while (ch.getCellIndex(start) == cellIndex) {
                            start++;
                        }

                        int end = ch.getEnd();
                        cellIndex = ch.getCellIndex(end);

                        while (ch.getCellIndex(end) == cellIndex) {
                            end--;
                        }

                        tmpChain.insert(0, ch.getCandidate(++end) + "= ");
                        tmpChain.append(" =").append(ch.getCandidate(start));
                    }

                    if (this.type == SolutionType.AIC || this.type == SolutionType.GROUPED_AIC || this.type == SolutionType.XY_CHAIN) {
                        Chain ch = this.getChains().get(0);
                        tmpChain.insert(0, ch.getCandidate(ch.getStart()) + "- ");
                        tmpChain.append(" -").append(ch.getCandidate(ch.getEnd()));
                    }

                    str = str + " " + tmpChain;
                    StringBuffer var39 = new StringBuffer(str);
                    this.getCandidatesToDelete(var39);
                    str = var39.toString();
                }
                break;
            case FORCING_CHAIN:
            case FORCING_CHAIN_CONTRADICTION:
            case FORCING_CHAIN_VERITY:
            case FORCING_NET:
            case FORCING_NET_CONTRADICTION:
            case FORCING_NET_VERITY:
                str = this.getStepName();
                if (art >= 1) {
                }

                if (art >= 2) {
                    if (this.type == SolutionType.FORCING_CHAIN_CONTRADICTION || this.type == SolutionType.FORCING_NET_CONTRADICTION) {
                        str = str + " " + ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.in") + " " + this.getEntityShortNameNumber();
                    }

                    if (this.indices.size() > 0) {
                        str = str + " => " + getCellPrint(this.indices.get(0), false) + "=" + this.values.get(0);
                    } else {
                        StringBuffer var38 = new StringBuffer(str);
                        this.getCandidatesToDelete(var38);
                        str = var38.toString();
                    }

                    for (int i = 0; i < this.chains.size(); i++) {
                        str = str + "\r\n  " + this.getForcingChainString(this.getChains().get(i));
                    }
                }
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
                str = this.getStepName();
                if (art >= 1) {
                    str = str + ": " + this.values.get(0) + "/" + this.values.get(1);
                }

                if (art >= 2) {
                    str = str + " in " + getCompactCellPrint(this.indices);
                    StringBuffer tmpA = new StringBuffer(str);
                    this.getCandidatesToDelete(tmpA);
                    str = tmpA.toString();
                }
                break;
            case BUG_PLUS_1:
                str = this.getStepName();
                if (art >= 2) {
                    StringBuffer var36 = new StringBuffer(str);
                    this.getCandidatesToDelete(var36);
                    str = var36.toString();
                }
                break;
            case X_WING:
            case SWORDFISH:
            case JELLYFISH:
            case SQUIRMBAG:
            case WHALE:
            case LEVIATHAN:
            case FINNED_X_WING:
            case FINNED_SWORDFISH:
            case FINNED_JELLYFISH:
            case FINNED_SQUIRMBAG:
            case FINNED_WHALE:
            case FINNED_LEVIATHAN:
            case SASHIMI_X_WING:
            case SASHIMI_SWORDFISH:
            case SASHIMI_JELLYFISH:
            case SASHIMI_SQUIRMBAG:
            case SASHIMI_WHALE:
            case SASHIMI_LEVIATHAN:
            case FRANKEN_X_WING:
            case FRANKEN_SWORDFISH:
            case FRANKEN_JELLYFISH:
            case FRANKEN_SQUIRMBAG:
            case FRANKEN_WHALE:
            case FRANKEN_LEVIATHAN:
            case FINNED_FRANKEN_X_WING:
            case FINNED_FRANKEN_SWORDFISH:
            case FINNED_FRANKEN_JELLYFISH:
            case FINNED_FRANKEN_SQUIRMBAG:
            case FINNED_FRANKEN_WHALE:
            case FINNED_FRANKEN_LEVIATHAN:
            case MUTANT_X_WING:
            case MUTANT_SWORDFISH:
            case MUTANT_JELLYFISH:
            case MUTANT_SQUIRMBAG:
            case MUTANT_WHALE:
            case MUTANT_LEVIATHAN:
            case FINNED_MUTANT_X_WING:
            case FINNED_MUTANT_SWORDFISH:
            case FINNED_MUTANT_JELLYFISH:
            case FINNED_MUTANT_SQUIRMBAG:
            case FINNED_MUTANT_WHALE:
            case FINNED_MUTANT_LEVIATHAN:
            case KRAKEN_FISH:
            case KRAKEN_FISH_TYPE_1:
            case KRAKEN_FISH_TYPE_2:
                StringBuffer var35 = new StringBuffer();
                if (this.isSiamese) {
                    var35.append(ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.siamese")).append(" ");
                }

                var35.append(this.getStepName());
                if (art >= 1) {
                    if (this.type.isKrakenFish()) {
                        var35.append(": ");
                        this.getCandidatesToDelete(var35);
                        var35.append("\r\n  ").append(this.subType.getStepName());
                    }

                    var35.append(": ").append(this.values.get(0));
                }

                if (art >= 2) {
                    var35.append(" ");
                    this.getEntities(var35, this.baseEntities, true, false);
                    var35.append(" ");
                    this.getEntities(var35, this.coverEntities, true, true);
                    int displayMode = Options.getInstance().getFishDisplayMode();
                    if (this.type.isKrakenFish()) {
                        displayMode = 0;
                    }

                    switch (displayMode) {
                        case 0:
                            if (this.fins.size() > 0) {
                                var35.append(" ");
                                this.getFins(var35, false, true);
                            }

                            if (this.endoFins.size() > 0) {
                                var35.append(" ");
                                this.getFins(var35, true, true);
                            }
                            break;
                        case 1:
                            this.getFishStatistics(var35, false);
                            break;
                        case 2:
                            this.getFishStatistics(var35, true);
                    }

                    if (!this.type.isKrakenFish()) {
                        this.getCandidatesToDelete(var35);
                    }
                }

                if (this.type.isKrakenFish()) {
                    for (int i = 0; i < this.chains.size(); i++) {
                        var35.append("\r\n  ").append(this.getChainString(this.chains.get(i)));
                    }
                }

                str = var35.toString();
                break;
            case SUE_DE_COQ:
                str = this.getStepName();
                StringBuffer var34 = new StringBuffer(str + ": ");
                if (art >= 1) {
                    this.getIndexValueSet(var34);
                    str = var34.toString();
                }

                if (art >= 2) {
                    var34.append(" (");
                    this.getFinSet(var34, this.fins);
                    var34.append(", ");
                    this.getFinSet(var34, this.endoFins);
                    var34.append(")");
                    this.getCandidatesToDelete(var34);
                    str = var34.toString();
                }
                break;
            case ALS_XZ:
                str = this.getStepName();
                StringBuffer var33 = new StringBuffer(str + ": ");
                if (art >= 1) {
                    var33.append("A=");
                    this.getAls(var33, 0);
                    str = var33.toString();
                }

                if (art >= 2) {
                    var33.append(", B=");
                    this.getAls(var33, 1);
                    var33.append(", X=");
                    this.getAlsXorZ(var33, true);
                    if (!this.fins.isEmpty()) {
                        var33.append(", Z=");
                        this.getAlsXorZ(var33, false);
                    }

                    this.getCandidatesToDelete(var33);
                    str = var33.toString();
                }
                break;
            case ALS_XY_WING:
                str = this.getStepName();
                if (art == 1) {
                    StringBuffer var31 = new StringBuffer(str + ": ");
                    var31.append("C=");
                    this.getAls(var31, 2);
                    str = var31.toString();
                }

                if (art >= 2) {
                    StringBuffer tmp2 = new StringBuffer(str + ": ");
                    tmp2.append("A=");
                    this.getAls(tmp2, 0);
                    tmp2.append(", B=");
                    this.getAls(tmp2, 1);
                    tmp2.append(", C=");
                    this.getAls(tmp2, 2);
                    tmp2.append(", X,Y=");
                    this.getAlsXorZ(tmp2, true);
                    tmp2.append(", Z=");
                    this.getAlsXorZ(tmp2, false);
                    this.getCandidatesToDelete(tmp2);
                    str = tmp2.toString();
                }
                break;
            case ALS_XY_CHAIN:
                str = this.getStepName();
                if (this.restrictedCommons.isEmpty()) {
                    if (art == 1) {
                        StringBuffer var27 = new StringBuffer(str + ": ");
                        var27.append(ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.start")).append("=");
                        this.getAls(var27, 0);
                        var27.append(", ").append(ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.end")).append("=");
                        this.getAls(var27, this.alses.size() - 1);
                        str = var27.toString();
                    }

                    if (art >= 2) {
                        StringBuffer tmp3 = new StringBuffer(str + ": ");
                        char alsChar = 'A';
                        boolean first = true;

                        for (int i = 0; i < this.alses.size(); i++) {
                            if (first) {
                                first = false;
                            } else {
                                tmp3.append(", ");
                            }

                            tmp3.append(alsChar++);
                            tmp3.append("=");
                            this.getAls(tmp3, i);
                        }

                        tmp3.append(", RCs=");
                        this.getAlsXorZ(tmp3, true);
                        tmp3.append(", X=");
                        this.getAlsXorZ(tmp3, false);
                        this.getCandidatesToDelete(tmp3);
                        str = tmp3.toString();
                    }
                } else {
                    if (art == 1) {
                        StringBuffer tmp4 = new StringBuffer(str + ": ");
                        tmp4.append(ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.start")).append("=");
                        this.getAls(tmp4, 0);
                        tmp4.append(", ").append(ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.end")).append("=");
                        this.getAls(tmp4, this.alses.size() - 1);
                        str = tmp4.toString();
                    }

                    if (art >= 2) {
                        StringBuffer tmp5 = new StringBuffer(str + ": ");
                        this.getCandidatesToDeleteDigits(tmp5);
                        tmp5.append("- ");

                        for (int i = 0; i < this.alses.size(); i++) {
                            this.getAls(tmp5, i);
                            if (i < this.restrictedCommons.size()) {
                                this.getRestrictedCommon(this.restrictedCommons.get(i), tmp5);
                            }
                        }

                        tmp5.append(" -");
                        this.getCandidatesToDeleteDigits(tmp5);
                        this.getCandidatesToDelete(tmp5);
                        str = tmp5.toString();
                    }
                }
                break;
            case DEATH_BLOSSOM:
                str = this.getStepName();
                StringBuffer var26 = new StringBuffer(str + ": ");
                if (art >= 1) {
                    var26.append(getCellPrint(this.indices.get(0)));
                    str = var26.toString();
                }

                if (art >= 2) {
                    for (int i = 0; i < this.alses.size(); i++) {
                        var26.append(", ");
                        this.getRestrictedCommon(this.restrictedCommons.get(i), var26);
                        this.getAls(var26, i);
                    }

                    this.getCandidatesToDelete(var26);
                    str = var26.toString();
                }
                break;
            case TEMPLATE_SET:
                str = this.getStepName();
                if (art == 1) {
                    str = str + ": " + this.values.get(0);
                }

                if (art >= 2) {
                    StringBuffer var25 = new StringBuffer(str + ": ");
                    var25.append(getCompactCellPrint(this.indices)).append("=").append(this.values.get(0));
                    str = var25.toString();
                }
                break;
            case TEMPLATE_DEL:
                str = this.getStepName();
                if (art >= 1) {
                }

                if (art >= 2) {
                    StringBuffer var24 = new StringBuffer(str + ": ");
                    this.getCandidatesToDelete(var24);
                    str = var24.toString();
                }
                break;
            case BRUTE_FORCE:
                str = this.getStepName();
                if (art == 1) {
                    str = str + ": " + this.values.get(0);
                }

                if (art >= 2) {
                    StringBuffer var23 = new StringBuffer(str + ": ");
                    var23.append(getCompactCellPrint(this.indices)).append("=").append(this.values.get(0));
                    str = var23.toString();
                }
                break;
            case INCOMPLETE:
                str = ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.incomplete_solution");
                break;
            case GIVE_UP:
                StringBuffer tmp6 = new StringBuffer();
                tmp6.append(this.getStepName());
                if (art >= 1) {
                    tmp6.append(": ").append(ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.dont_know"));
                }

                str = tmp6.toString();
                break;
            default:
                throw new RuntimeException(ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.invalid_type") + " (" + this.type + ")!");
        }

        return str;
    }

    private void getFishStatistics(StringBuffer tmp, boolean cells) {
        tmp.append(" ");
        SudokuSet set = new SudokuSet();

        for (int i = 0; i < this.indices.size(); i++) {
            set.add(this.indices.get(i));
        }

        set.andNot(this.potentialCannibalisticEliminations);
        this.appendFishData(tmp, set, "V", cells);
        set.clear();

        for (int i = 0; i < this.fins.size(); i++) {
            set.add(this.fins.get(i).getIndex());
        }

        for (int i = 0; i < this.endoFins.size(); i++) {
            set.remove(this.endoFins.get(i).getIndex());
        }

        this.appendFishData(tmp, set, "XF", cells);
        set.clear();

        for (int i = 0; i < this.endoFins.size(); i++) {
            set.add(this.endoFins.get(i).getIndex());
        }

        this.appendFishData(tmp, set, "NF", cells);
        set.clear();

        for (int i = 0; i < this.candidatesToDelete.size(); i++) {
            set.add(this.candidatesToDelete.get(i).getIndex());
        }

        this.appendFishData(tmp, set, "EE", cells);
        set.clear();

        for (int i = 0; i < this.cannibalistic.size(); i++) {
            set.add(this.cannibalistic.get(i).getIndex());
        }

        this.appendFishData(tmp, set, "CE", cells);
        set.set(this.potentialEliminations);
        set.or(this.potentialCannibalisticEliminations);
        this.appendFishData(tmp, set, "PE", cells);
    }

    private void appendFishData(StringBuffer tmp, SudokuSet set, String prefix, boolean cells) {
        tmp.append(prefix);
        tmp.append("(");
        if (cells) {
            tmp.append(getCompactCellPrint(set));
        } else {
            tmp.append(FISH_FORMAT.format(set.size()));
        }

        tmp.append(") ");
    }

    private void getColorCellPrint(StringBuffer tmp) {
        tmp.append(" ");
        StringBuffer[] bufs = new StringBuffer[Options.getInstance().getColoringColors().length];

        for (int index : this.getColorCandidates().keySet()) {
            int color = this.getColorCandidates().get(index);
            if (bufs[color] == null) {
                bufs[color] = new StringBuffer();
                bufs[color].append("(");
            } else {
                bufs[color].append(",");
            }

            bufs[color].append(getCellPrint(index, false));
        }

        for (int i = 0; i < bufs.length; i++) {
            if (bufs[i] != null) {
                bufs[i].append(")");
                if (i % 2 != 0) {
                    tmp.append(" / ");
                } else if (i > 0) {
                    tmp.append(", ");
                }

                tmp.append(bufs[i]);
            }
        }
    }

    private void getAlsXorZ(StringBuffer tmp, boolean x) {
        List<Candidate> list = x ? this.endoFins : this.fins;
        TreeSet<Integer> cands = new TreeSet<>();

        for (int i = 0; i < list.size(); i++) {
            cands.add(list.get(i).getValue());
        }

        boolean first = true;

        for (int cand : cands) {
            if (first) {
                first = false;
            } else {
                tmp.append(",");
            }

            tmp.append(cand);
        }
    }

    public void getAls(StringBuffer tmp, int alsIndex) {
        this.getAls(tmp, alsIndex, true);
    }

    public void getAls(StringBuffer tmp, int alsIndex, boolean withCandidates) {
        AlsInSolutionStep als = this.alses.get(alsIndex);
        tmp.append(getCompactCellPrint(als.getIndices()));
        if (withCandidates) {
            tmp.append(" {");

            for (Integer cand : als.getCandidates()) {
                tmp.append(cand);
            }

            tmp.append("}");
        }
    }

    private void getIndexValueSet(StringBuffer tmp) {
        tmp.append(getCompactCellPrint(this.indices));
        tmp.append(" - {");

        for (Integer value : this.values) {
            tmp.append(value);
        }

        tmp.append("}");
    }

    private void getFinSet(StringBuffer tmp, List<Candidate> fins) {
        this.getFinSet(tmp, fins, true);
    }

    private void getFinSet(StringBuffer tmp, List<Candidate> fins, boolean withCandidates) {
        TreeSet<Integer> indexes = new TreeSet<>();
        TreeSet<Integer> candidates = new TreeSet<>();

        for (Candidate cand : fins) {
            indexes.add(cand.getIndex());
            candidates.add(cand.getValue());
        }

        for (int index : this.indices) {
            indexes.remove(index);
        }

        tmp.append(getCompactCellPrint(indexes));
        if (withCandidates) {
            tmp.append(" - {");

            for (int value : candidates) {
                tmp.append(value);
            }

            tmp.append("}");
        }
    }

    public void getEntities(StringBuffer tmp, List<Entity> entities) {
        this.getEntities(tmp, entities, false);
    }

    public void getEntities(StringBuffer tmp, List<Entity> entities, boolean library) {
        this.getEntities(tmp, entities, library, false);
    }

    public void getEntities(StringBuffer tmp, List<Entity> entities, boolean library, boolean checkSiamese) {
        boolean first = true;
        if (!library) {
            tmp.append("(");
        }

        int siameseIndex = entities.size() / 2 - 1;
        int lastEntityName = -1;
        int index = 0;

        for (Entity act : entities) {
            if (first) {
                first = false;
            } else if (!library) {
                tmp.append(", ");
            }

            if (library) {
                if (lastEntityName != act.getEntityName()) {
                    tmp.append(this.getEntityShortName(act.getEntityName()));
                }

                tmp.append(act.getEntityNumber());
            } else {
                tmp.append(this.getEntityName(act.getEntityName())).append(" ").append(act.getEntityNumber());
            }

            lastEntityName = act.getEntityName();
            if (checkSiamese && this.isSiamese && index == siameseIndex) {
                tmp.append("/");
                lastEntityName = -1;
            }

            index++;
        }

        if (!library) {
            tmp.append(")");
        }
    }

    private void getIndexes(StringBuffer tmp) {
        boolean first = true;

        for (int index : this.indices) {
            if (first) {
                first = false;
            } else {
                tmp.append(", ");
            }

            tmp.append(getCellPrint(index, false));
        }
    }

    private void getRestrictedCommon(RestrictedCommon rc, StringBuffer tmp) {
        int anz = 0;
        tmp.append(" -");
        if (rc.getActualRC() == 1 || rc.getActualRC() == 3) {
            tmp.append(rc.getCand1());
            anz++;
        }

        if (rc.getActualRC() == 2 || rc.getActualRC() == 3) {
            tmp.append(rc.getCand2());
            anz++;
        }

        tmp.append("- ");
    }

    private void getCandidatesToDeleteDigits(StringBuffer tmp) {
        SortedSet<Integer> candSet = new TreeSet<>();

        for (int i = 0; i < this.candidatesToDelete.size(); i++) {
            candSet.add(this.candidatesToDelete.get(i).getValue());
        }

        for (int value : candSet) {
            tmp.append(value);
        }
    }

    private String getCandidatesToDeleteDigits() {
        StringBuffer tmp = new StringBuffer();
        this.getCandidatesToDeleteDigits(tmp);
        int compactLength = tmp.length();

        for (int i = 0; i < compactLength - 1; i++) {
            tmp.insert(i * 2 + 1, "/");
        }

        return tmp.toString();
    }

    private void getCandidatesToDelete(StringBuffer tmp) {
        tmp.append(" => ");
        ArrayList<Candidate> tmpList = (ArrayList<Candidate>) ((ArrayList) this.candidatesToDelete).clone();
        boolean first = true;
        ArrayList<Integer> candList = new ArrayList<>();

        while (tmpList.size() > 0) {
            Candidate firstCand = tmpList.remove(0);
            candList.clear();
            candList.add(firstCand.getIndex());
            Iterator<Candidate> it = tmpList.iterator();

            while (it.hasNext()) {
                Candidate c1 = it.next();
                if (c1.getValue() == firstCand.getValue()) {
                    candList.add(c1.getIndex());
                    it.remove();
                }
            }

            if (first) {
                first = false;
            } else {
                tmp.append(", ");
            }

            tmp.append(getCompactCellPrint(candList));
            tmp.append("<>");
            tmp.append(firstCand.getValue());
        }
    }

    public void getFins(StringBuffer tmp, boolean endo) {
        this.getFins(tmp, endo, false);
    }

    public void getFins(StringBuffer tmp, boolean endo, boolean library) {
        List<Candidate> list = endo ? this.endoFins : this.fins;
        if (!list.isEmpty()) {
            if (!library) {
                if (list.size() == 1) {
                    if (endo) {
                        tmp.append(" ").append(ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.endofin_in")).append(" ");
                    } else {
                        tmp.append(" ").append(ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.fin_in")).append(" ");
                    }
                } else if (endo) {
                    tmp.append(" ").append(ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.endofins_in")).append(" ");
                } else {
                    tmp.append(" ").append(ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.fins_in")).append(" ");
                }
            }

            String finStr = endo ? "ef" : "f";
            boolean first = true;

            for (Candidate cand : list) {
                if (first) {
                    first = false;
                } else if (library) {
                    tmp.append(" ");
                } else {
                    tmp.append(", ");
                }

                if (library) {
                    tmp.append(finStr).append(getCellPrint(cand.getIndex(), false));
                } else {
                    tmp.append(getCellPrint(cand.getIndex(), false));
                }
            }
        }
    }

    public int getEntity() {
        return this.entity;
    }

    public void setEntity(int entity) {
        if (entity != 0 && entity != 1 && entity != 2 && entity != 3) {
            throw new RuntimeException(
                    ResourceBundle.getBundle("intl/SolutionStep").getString("SolutionStep.invalid_setEntity")
                            + " ("
                            + entity
                            + ResourceBundle.getBundle("intl/SolutionStep").getString(")")
            );
        }

        this.entity = entity;
    }

    public int getEntityNumber() {
        return this.entityNumber;
    }

    public void setEntityNumber(int entityNumber) {
        this.entityNumber = entityNumber;
    }

    public int getEntity2() {
        return this.entity2;
    }

    public void setEntity2(int entity2) {
        this.entity2 = entity2;
    }

    public int getEntity2Number() {
        return this.entity2Number;
    }

    public void setEntity2Number(int entity2Number) {
        this.entity2Number = entity2Number;
    }

    public void addBaseEntity(int name, int number) {
        this.baseEntities.add(new Entity(name, number));
    }

    public void addBaseEntity(Entity e) {
        this.baseEntities.add(e);
    }

    public void addCoverEntity(int name, int number) {
        this.coverEntities.add(new Entity(name, number));
    }

    public void addCoverEntity(Entity e) {
        this.coverEntities.add(e);
    }

    public void addChain(int start, int end, int[] chain) {
        this.chains.add(new Chain(start, end, chain));
    }

    public void addChain(Chain chain) {
        chain.resetLength();
        this.chains.add(chain);
    }

    public List<Chain> getChains() {
        return this.chains;
    }

    public void setChains(List<Chain> chains) {
        this.chains = chains;
    }

    public int getChainLength() {
        int length = 0;

        for (int i = 0; i < this.chains.size(); i++) {
            length += this.chains.get(i).getLength(this.alses);
        }

        return length;
    }

    public int getChainAnz() {
        return this.chains.size();
    }

    public boolean isNet() {
        if (this.chains.size() > 0) {
            for (int i = 0; i < this.chains.size(); i++) {
                Chain tmp = this.chains.get(i);

                for (int j = tmp.getStart(); j <= tmp.getEnd(); j++) {
                    if (tmp.getChain()[j] < 0) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public int getAlsesIndexCount() {
        int count = 0;

        for (AlsInSolutionStep als : this.alses) {
            count += als.getIndices().size();
        }

        return count;
    }

    public List<AlsInSolutionStep> getAlses() {
        return this.alses;
    }

    public void setAlses(List<AlsInSolutionStep> alses) {
        this.alses = alses;
    }

    public AlsInSolutionStep getAls(int index) {
        return this.alses.get(index);
    }

    public void addAls(AlsInSolutionStep newAls) {
        this.alses.add(newAls);
    }

    public void addAls(SudokuSet indices, SudokuSet candidates) {
        AlsInSolutionStep als = new AlsInSolutionStep();

        for (int i = 0; i < indices.size(); i++) {
            als.addIndex(indices.get(i));
        }

        for (int i = 0; i < candidates.size(); i++) {
            als.addCandidate(candidates.get(i));
        }

        this.alses.add(als);
    }

    public void addAls(SudokuSet indices, short candidates) {
        AlsInSolutionStep als = new AlsInSolutionStep();

        for (int i = 0; i < indices.size(); i++) {
            als.addIndex(indices.get(i));
        }

        int[] cands = Sudoku2.POSSIBLE_VALUES[candidates];

        for (int i = 0; i < cands.length; i++) {
            als.addCandidate(cands[i]);
        }

        this.alses.add(als);
    }

    public void addRestrictedCommon(RestrictedCommon rc) {
        this.restrictedCommons.add(rc);
    }

    public int getAlsIndex(int index, int chainIndex) {
        if (chainIndex == -1) {
            for (int i = 0; i < this.alses.size(); i++) {
                if (this.alses.get(i).getIndices().contains(index)) {
                    return i;
                }
            }
        } else {
            Chain chain = this.chains.get(chainIndex);

            for (int i = chain.getStart(); i <= chain.getEnd(); i++) {
                if (chain.getNodeType(i) == 2) {
                    int alsIndex = Chain.getSAlsIndex(chain.getChain()[i]);
                    AlsInSolutionStep als = this.alses.get(alsIndex);
                    if (als.getIndices().contains(index)) {
                        return alsIndex;
                    }
                }
            }
        }

        return -1;
    }

    public void addColorCandidate(int index, int color) {
        this.getColorCandidates().put(index, color);
    }

    public void addColorCandidates(SudokuSet indices, int color) {
        for (int i = 0; i < indices.size(); i++) {
            this.addColorCandidate(indices.get(i), color);
        }
    }

    public boolean isEqual(SolutionStep s) {
        return this.isEquivalent(s)
                && this.isEqualInteger(this.values, s.values)
                && this.isEqualInteger(this.indices, s.indices)
                && this.isEqualCandidate(this.fins, s.fins);
    }

    public boolean isEquivalent(SolutionStep s) {
        if (this.type.isFish() && s.getType().isFish()) {
            return true;
        } else if (this.type.isKrakenFish() && s.getType().isKrakenFish()) {
            return true;
        } else if (this.getType() != s.getType()) {
            return false;
        } else {
            return this.candidatesToDelete.size() > 0
                    ? this.isEqualCandidate(this.candidatesToDelete, s.candidatesToDelete)
                    : this.isEqualInteger(this.indices, s.indices);
        }
    }

    public boolean isSubStep(SolutionStep s) {
        if (s.candidatesToDelete.size() < this.candidatesToDelete.size()) {
            return false;
        }

        for (Candidate cand : this.candidatesToDelete) {
            if (!s.candidatesToDelete.contains(cand)) {
                return false;
            }
        }

        return true;
    }

    public boolean isSingle() {
        return this.isSingle(this.type);
    }

    public boolean isSingle(SolutionType type) {
        return type == SolutionType.FULL_HOUSE || type == SolutionType.HIDDEN_SINGLE || type == SolutionType.NAKED_SINGLE || type == SolutionType.TEMPLATE_SET;
    }

    public boolean isForcingChainSet() {
        return (
                this.type == SolutionType.FORCING_CHAIN
                        || this.type == SolutionType.FORCING_CHAIN_CONTRADICTION
                        || this.type == SolutionType.FORCING_CHAIN_VERITY
        )
                && this.indices.size() > 0
                || (this.type == SolutionType.FORCING_NET || this.type == SolutionType.FORCING_NET_CONTRADICTION || this.type == SolutionType.FORCING_NET_VERITY)
                && this.indices.size() > 0;
    }

    public int compareChainLengths(SolutionStep o) {
        return this.getChainLength() - o.getChainLength();
    }

    public int compareTo(SolutionStep o) {
        int sum1 = 0;
        int sum2 = 0;
        if (this.isSingle(this.type) && !this.isSingle(o.type)) {
            return -1;
        }

        if (!this.isSingle(this.type) && this.isSingle(o.type)) {
            return 1;
        }

        int result = o.candidatesToDelete.size() - this.candidatesToDelete.size();
        if (result != 0) {
            return result;
        }

        if (!this.isEquivalent(o)) {
            sum1 = this.getIndexSumme(this.candidatesToDelete);
            sum2 = this.getIndexSumme(o.candidatesToDelete);
            return sum1 - sum2;
        }

        if (this.type.isFish() && o.getType().isFish()) {
            int ret = this.type.compare(o.getType());
            if (ret != 0) {
                return ret;
            } else {
                ret = this.getCannibalistic().size() - o.getCannibalistic().size();
                if (ret != 0) {
                    return ret;
                } else {
                    ret = this.getEndoFins().size() - o.getEndoFins().size();
                    if (ret != 0) {
                        return ret;
                    } else {
                        ret = this.getFins().size() - o.getFins().size();
                        if (ret != 0) {
                            return ret;
                        } else if (!this.isEqualInteger(this.values, o.values)) {
                            sum1 = this.getSumme(this.values);
                            sum2 = this.getSumme(o.values);
                            return sum1 - sum2;
                        } else {
                            return 0;
                        }
                    }
                }
            }
        } else {
            if (this.type.isKrakenFish() && o.getType().isKrakenFish()) {
                int ret = this.subType.compare(o.getSubType());
                return ret != 0 ? ret : this.compareChainLengths(o);
            }

            int chainDiff = this.compareChainLengths(o);
            if (chainDiff != 0) {
                return chainDiff;
            }

            if (!this.isEqualInteger(this.values, o.values)) {
                sum1 = this.getSumme(this.values);
                sum2 = this.getSumme(o.values);
                return sum1 - sum2;
            }

            if (!this.isEqualInteger(this.indices, o.indices)) {
                if (this.indices.size() != o.indices.size()) {
                    return this.indices.size() - o.indices.size();
                }

                sum1 = this.getSumme(this.indices);
                sum2 = this.getSumme(o.indices);
                return sum2 - sum1;
            } else {
                return this.type.compare(o.getType());
            }
        }
    }

    public boolean isEqualValues(SolutionStep s) {
        return this.isEqualInteger(this.values, s.getValues());
    }

    private boolean isEqualInteger(List<Integer> l1, List<Integer> l2) {
        if (l1.size() != l2.size()) {
            return false;
        }

        int anz = l1.size();

        for (int i = 0; i < anz; i++) {
            int i1 = l1.get(i);
            boolean found = false;

            for (int j = 0; j < anz; j++) {
                int i2 = l2.get(j);
                if (i1 == i2) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                return false;
            }
        }

        return true;
    }

    public boolean isEqualCandidate(SolutionStep s) {
        return this.isEqualCandidate(this.candidatesToDelete, s.getCandidatesToDelete());
    }

    private boolean isEqualCandidate(List<Candidate> l1, List<Candidate> l2) {
        if (l1.size() != l2.size()) {
            return false;
        }

        int anz = l1.size();

        for (int i = 0; i < anz; i++) {
            Candidate c1 = l1.get(i);
            boolean found = false;

            for (int j = 0; j < anz; j++) {
                Candidate c2 = l2.get(j);
                if (c1.getIndex() == c2.getIndex() && c1.getValue() == c2.getValue()) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                return false;
            }
        }

        return true;
    }

    public int getIndexSumme(List<Candidate> list) {
        int sum = 0;
        int offset = 1;

        for (int i = 0; i < list.size(); i++) {
            sum += list.get(i).getIndex() * offset + list.get(i).getValue();
            offset += 80;
        }

        return sum;
    }

    public int getSumme(List<Integer> list) {
        int sum = 0;

        for (int i = 0; i < list.size(); i++) {
            sum += list.get(i);
        }

        return sum;
    }

    public int compareCandidatesToDelete(SolutionStep o) {
        int size1 = this.candidatesToDelete.size();
        int size2 = o.candidatesToDelete.size();
        if (size1 != size2) {
            return size2 - size1;
        }

        int result = 0;

        for (int i = 0; i < size1; i++) {
            Candidate c1 = this.candidatesToDelete.get(i);
            Candidate c2 = o.candidatesToDelete.get(i);
            result = c1.getIndex() * 10 + c1.getValue() - (c2.getIndex() * 10 + c2.getValue());
            if (result != 0) {
                return result;
            }
        }

        return 0;
    }

    public List<Entity> getBaseEntities() {
        return this.baseEntities;
    }

    public void setBaseEntities(List<Entity> baseEntities) {
        this.baseEntities = baseEntities;
    }

    public List<Entity> getCoverEntities() {
        return this.coverEntities;
    }

    public void setCoverEntities(List<Entity> coverEntities) {
        this.coverEntities = coverEntities;
    }

    public SortedMap<Integer, Integer> getColorCandidates() {
        return this.colorCandidates;
    }

    public void setColorCandidates(SortedMap<Integer, Integer> colorCandidates) {
        this.colorCandidates = colorCandidates;
    }

    public SolutionType getSubType() {
        return this.subType;
    }

    public void setSubType(SolutionType subType) {
        this.subType = subType;
    }

    public boolean isIsSiamese() {
        return this.isSiamese;
    }

    public void setIsSiamese(boolean isSiamese) {
        this.isSiamese = isSiamese;
    }

    public List<RestrictedCommon> getRestrictedCommons() {
        return this.restrictedCommons;
    }

    public void setRestrictedCommons(List<RestrictedCommon> restrictedCommons) {
        this.restrictedCommons = restrictedCommons;
    }

    public int getProgressScoreSingles() {
        return this.progressScoreSingles;
    }

    public void setProgressScoreSingles(int progressScoreSingles) {
        this.progressScoreSingles = progressScoreSingles;
    }

    public int getProgressScoreSinglesOnly() {
        return this.progressScoreSinglesOnly;
    }

    public void setProgressScoreSinglesOnly(int progressScoreSinglesOnly) {
        this.progressScoreSinglesOnly = progressScoreSinglesOnly;
    }

    public int getProgressScore() {
        return this.progressScore;
    }

    public void setProgressScore(int progressScore) {
        this.progressScore = progressScore;
    }

    public SudokuSet getPotentialCannibalisticEliminations() {
        return this.potentialCannibalisticEliminations;
    }

    public void setPotentialCannibalisticEliminations(SudokuSet potentialCannibalisticEliminations) {
        this.potentialCannibalisticEliminations = potentialCannibalisticEliminations;
    }

    public SudokuSet getPotentialEliminations() {
        return this.potentialEliminations;
    }

    public void setPotentialEliminations(SudokuSet potentialEliminations) {
        this.potentialEliminations = potentialEliminations;
    }
}
