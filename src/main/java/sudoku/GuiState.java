package sudoku;

import solver.SudokuSolver;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GuiState {
    private static final boolean DEBUG = false;
    private Sudoku2 sudoku = null;
    private Stack<Sudoku2> undoStack = null;
    private Stack<Sudoku2> redoStack = null;
    private SolutionStep step = null;
    private int chainIndex = -1;
    private SortedMap<Integer, Integer> coloringMap = null;
    private SortedMap<Integer, Integer> coloringCandidateMap = null;
    private List<SolutionStep> steps;
    private int[] anzSteps;
    private List<String> titels;
    private List<List<SolutionStep>> tabSteps;
    private String name;
    private Date timestamp;
    private SudokuPanel sudokuPanel;
    private SudokuSolver sudokuSolver;
    private SolutionPanel solutionPanel;

    public GuiState() {
    }

    public GuiState(SudokuPanel sudokuPanel, SudokuSolver sudokuSolver, SolutionPanel solutionPanel) {
        this.initialize(sudokuPanel, sudokuSolver, solutionPanel);
    }

    public final void initialize(SudokuPanel sudokuPanel, SudokuSolver sudokuSolver, SolutionPanel solutionPanel) {
        this.sudokuPanel = sudokuPanel;
        this.sudokuSolver = sudokuSolver;
        this.solutionPanel = solutionPanel;
    }

    public void get(boolean copy) {
        if (this.sudokuSolver != null) {
            this.sudokuSolver.getState(this, copy);
        }

        if (this.solutionPanel != null) {
            this.solutionPanel.getState(this, copy);
        }

        if (this.sudokuPanel != null) {
            this.sudokuPanel.getState(this, copy);
        }
    }

    public void set() {
        if (this.sudokuSolver != null) {
            this.sudokuSolver.setState(this);
        }

        if (this.solutionPanel != null) {
            this.solutionPanel.setState(this);
        }

        if (this.sudokuPanel != null) {
            this.sudokuPanel.setState(this);
        }
    }

    public void resetAnzSteps() {
        if (this.steps != null && this.anzSteps != null) {
            StepConfig[] cfg = Options.getInstance().solverSteps;
            int length = cfg.length;
            if (this.anzSteps.length != length) {
                this.anzSteps = new int[length];
            } else {
                for (int i = 0; i < this.anzSteps.length; i++) {
                    this.anzSteps[i] = 0;
                }
            }

            for (SolutionStep act : this.steps) {
                StepConfig config = act.getType().getStepConfig();
                if (config != null) {
                    int index = 0;
                    index = 0;

                    while (index < cfg.length && cfg[index] != config) {
                        index++;
                    }

                    if (index < cfg.length) {
                        this.anzSteps[index]++;
                    }
                }
            }
        } else {
            Logger.getLogger(GuiState.class.getName())
                    .log(Level.SEVERE, "Trying to reset anzSteps, but attributes have not been set ({0}/{1})!", new Object[]{this.steps, this.anzSteps});
        }
    }

    public Sudoku2 getSudoku() {
        return this.sudoku;
    }

    public void setSudoku(Sudoku2 sudoku) {
        this.sudoku = sudoku;
    }

    public Stack<Sudoku2> getUndoStack() {
        return this.undoStack;
    }

    public void setUndoStack(Stack<Sudoku2> undoStack) {
        this.undoStack = undoStack;
    }

    public Stack<Sudoku2> getRedoStack() {
        return this.redoStack;
    }

    public void setRedoStack(Stack<Sudoku2> redoStack) {
        this.redoStack = redoStack;
    }

    public SolutionStep getStep() {
        return this.step;
    }

    public void setStep(SolutionStep step) {
        this.step = step;
    }

    public int getChainIndex() {
        return this.chainIndex;
    }

    public void setChainIndex(int chainIndex) {
        this.chainIndex = chainIndex;
    }

    public SortedMap<Integer, Integer> getColoringMap() {
        return this.coloringMap;
    }

    public void setColoringMap(SortedMap<Integer, Integer> coloringMap) {
        this.coloringMap = coloringMap;
    }

    public SortedMap<Integer, Integer> getColoringCandidateMap() {
        return this.coloringCandidateMap;
    }

    public void setColoringCandidateMap(SortedMap<Integer, Integer> coloringCandidateMap) {
        this.coloringCandidateMap = coloringCandidateMap;
    }

    public List<SolutionStep> getSteps() {
        return this.steps;
    }

    public void setSteps(List<SolutionStep> steps) {
        this.steps = steps;
    }

    public int[] getAnzSteps() {
        return this.anzSteps;
    }

    public void setAnzSteps(int[] anzSteps) {
        this.anzSteps = anzSteps;
    }

    public List<String> getTitels() {
        return this.titels;
    }

    public void setTitels(List<String> titels) {
        this.titels = titels;
    }

    public List<List<SolutionStep>> getTabSteps() {
        return this.tabSteps;
    }

    public void setTabSteps(List<List<SolutionStep>> tabSteps) {
        if (this.steps == null || this.steps.isEmpty()) {
            if (tabSteps.get(0) != null) {
                this.steps = new ArrayList<>(tabSteps.get(0));
            } else {
                this.steps = new ArrayList<>();
            }
        }

        this.tabSteps = tabSteps;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
