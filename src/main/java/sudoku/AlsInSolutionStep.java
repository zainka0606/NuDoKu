package sudoku;

import solver.Als;

import java.util.ArrayList;
import java.util.List;

public class AlsInSolutionStep implements Cloneable {
    private List<Integer> indices = new ArrayList<>();
    private List<Integer> candidates = new ArrayList<>();
    private int chainPenalty = -1;

    public void addIndex(int index) {
        this.indices.add(index);
    }

    public void addCandidate(int cand) {
        this.candidates.add(cand);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        AlsInSolutionStep newAls = (AlsInSolutionStep) super.clone();
        newAls.indices = (List<Integer>) ((ArrayList) this.indices).clone();
        newAls.candidates = (List<Integer>) ((ArrayList) this.candidates).clone();
        return newAls;
    }

    public List<Integer> getIndices() {
        return this.indices;
    }

    public void setIndices(List<Integer> indices) {
        this.indices = indices;
    }

    public List<Integer> getCandidates() {
        return this.candidates;
    }

    public void setCandidates(List<Integer> candidates) {
        this.candidates = candidates;
    }

    public int getChainPenalty() {
        if (this.chainPenalty == -1) {
            this.chainPenalty = Als.getChainPenalty(this.indices.size());
        }

        return this.chainPenalty;
    }

    public void setChainPenalty(int chainPenalty) {
        this.chainPenalty = chainPenalty;
    }
}
