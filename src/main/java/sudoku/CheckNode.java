package sudoku;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Enumeration;

public class CheckNode extends DefaultMutableTreeNode {
    protected static final int NONE = 0;
    protected static final int HALF = 1;
    protected static final int FULL = 2;
    private static final long serialVersionUID = 1L;
    private int selectionState;
    private StepConfig step;
    private boolean allSteps;
    private SolutionCategory category;
    private boolean heuristics;
    private boolean training;

    public CheckNode() {
        this(null);
    }

    public CheckNode(Object userObject) {
        this(userObject, true, 0, null, false, false, false, null);
    }

    public CheckNode(
            Object userObject,
            boolean allowsChildren,
            int selectionState,
            StepConfig step,
            boolean allSteps,
            boolean heuristics,
            boolean training,
            SolutionCategory category
    ) {
        super(userObject, allowsChildren);
        this.selectionState = selectionState;
        this.step = step;
        this.allSteps = allSteps;
        this.heuristics = heuristics;
        this.training = training;
        this.category = category;
    }

    public void toggleSelectionState() {
        if (this.children == null) {
            this.selectionState = this.selectionState == 2 ? 0 : 2;
            this.adjustModel(this);
            int actState = -1;
            CheckNode tmpParent = (CheckNode) this.getParent();

            for (int i = 0; i < tmpParent.children.size(); i++) {
                CheckNode act = (CheckNode) tmpParent.children.get(i);
                if (actState == -1) {
                    actState = act.selectionState;
                } else if (actState != act.selectionState) {
                    actState = 1;
                    break;
                }
            }

            tmpParent.selectionState = actState;
        } else {
            this.selectionState = this.selectionState == 2 ? 0 : 2;
            Enumeration<?> enumeration = this.children.elements();

            while (enumeration.hasMoreElements()) {
                CheckNode node = (CheckNode) enumeration.nextElement();
                node.selectionState = this.selectionState;
                this.adjustModel(node);
            }
        }
    }

    private void adjustModel(CheckNode node) {
        if (node.step != null) {
            if (this.allSteps) {
                node.step.setAllStepsEnabled(node.selectionState == 2);
            } else if (this.heuristics) {
                node.step.setEnabledProgress(node.selectionState == 2);
            } else if (this.training) {
                node.step.setEnabledTraining(node.selectionState == 2);
            } else {
                node.step.setEnabled(node.selectionState == 2);
            }
        }
    }

    public int getSelectionState() {
        return this.selectionState;
    }

    public void setSelectionState(int selectionState) {
        this.selectionState = selectionState;
    }

    public SolutionCategory getCategory() {
        return this.category;
    }
}
