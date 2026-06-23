package sudoku;

class StepStatistic {
    SolutionType type;
    int anzSet;
    int anzCandDel;
    int anzSteps;
    int anzInvalidSteps;
    int anzInvalidSet;
    int anzInvalidCandDel;

    StepStatistic(SolutionType type) {
        this.type = type;
    }
}
