package sudoku;

import javax.swing.*;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

public class CheckRenderer extends JPanel implements TreeCellRenderer {
    private static final long serialVersionUID = 1L;
    private JCheckBox check;
    private JLabel label;

    public CheckRenderer() {
        this.setLayout(null);
        this.add(this.check = new JCheckBox());
        this.add(this.label = new JLabel());
        this.check.setBackground(UIManager.getColor("Tree.textBackground"));
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        String stringValue = tree.convertValueToText(value, isSelected, expanded, leaf, row, hasFocus);
        this.setEnabled(tree.isEnabled());
        int selectionState = 2;
        if (value instanceof CheckNode) {
            selectionState = ((CheckNode) value).getSelectionState();
        }

        this.check.setSelected(selectionState != 0);
        this.check.setEnabled(selectionState != 1);
        this.label.setFont(tree.getFont());
        this.label.setText(stringValue);
        if (leaf) {
            this.label.setIcon(UIManager.getIcon("Tree.leafIcon"));
        } else if (expanded) {
            this.label.setIcon(UIManager.getIcon("Tree.openIcon"));
        } else {
            this.label.setIcon(UIManager.getIcon("Tree.closedIcon"));
        }

        if (isSelected) {
            this.label.setForeground(UIManager.getColor("Tree.selectionForeground"));
            this.label.setBackground(UIManager.getColor("Tree.selectionBackground"));
            this.setBackground(UIManager.getColor("Tree.selectionBackground"));
        } else {
            this.label.setForeground(UIManager.getColor("Tree.textForeground"));
            this.label.setBackground(UIManager.getColor("Tree.textBackground"));
            this.setBackground(UIManager.getColor("Tree.textBackground"));
        }

        return this;
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension dCheck = this.check.getPreferredSize();
        Dimension dLabel = this.label.getPreferredSize();
        return new Dimension(dCheck.width + dLabel.width, dCheck.height < dLabel.height ? dLabel.height : dCheck.height);
    }

    @Override
    public void doLayout() {
        Dimension dCheck = this.check.getPreferredSize();
        Dimension dLabel = this.label.getPreferredSize();
        int yCheck = 0;
        int yLabel = 0;
        if (dCheck.height < dLabel.height) {
            yCheck = (dLabel.height - dCheck.height) / 2;
        } else {
            yLabel = (dCheck.height - dLabel.height) / 2;
        }

        this.check.setLocation(0, yCheck);
        this.check.setBounds(0, yCheck, dCheck.width, dCheck.height);
        this.label.setLocation(dCheck.width, yLabel);
        this.label.setBounds(dCheck.width, yLabel, dLabel.width, dLabel.height);
    }
}
