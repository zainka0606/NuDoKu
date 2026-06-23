package solver;

import sudoku.SolutionStep;

import java.util.Comparator;

class AlsComparator implements Comparator<SolutionStep> {
    private static final boolean debug = false;

    public int compare(SolutionStep o1, SolutionStep o2) {
        int sum1 = 0;
        int sum2 = 0;
        int result = o2.getCandidatesToDelete().size() - o1.getCandidatesToDelete().size();
        if (result != 0) {
            return result;
        }

        if (!o1.isEquivalent(o2)) {
            sum1 = o1.getIndexSumme(o1.getCandidatesToDelete());
            sum2 = o2.getIndexSumme(o2.getCandidatesToDelete());
            return sum1 - sum2;
        }

        result = o1.getAlses().size() - o2.getAlses().size();
        if (result != 0) {
            return result;
        }

        result = o1.getAlsesIndexCount() - o2.getAlsesIndexCount();
        return result != 0 ? result : o1.getType().compare(o2.getType());
    }
}
