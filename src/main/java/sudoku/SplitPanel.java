package sudoku;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class SplitPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private SudokuPanel sudokuPanel;
    private Component rightComponent;
    private JSplitPane splitPane;

    public SplitPanel() {
        this.initComponents();
        this.splitPane.getActionMap().getParent().remove("startResize");
        this.splitPane.getActionMap().getParent().remove("toggleFocus");
    }

    private void initComponents() {
        this.splitPane = new JSplitPane();
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent evt) {
                SplitPanel.this.formComponentResized(evt);
            }
        });
        this.splitPane.setDividerLocation(800);
        this.splitPane.setContinuousLayout(true);
        this.splitPane.setRequestFocusEnabled(false);
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING).addComponent(this.splitPane, -1, 423, 32767));
        layout.setVerticalGroup(layout.createParallelGroup(Alignment.LEADING).addComponent(this.splitPane, Alignment.TRAILING, -1, 440, 32767));
    }

    private void formComponentResized(ComponentEvent evt) {
        this.adjustDividerBar();
    }

    public void setSplitPane(SudokuPanel sudokuPanel, JPanel rightPanel) {
        int height = sudokuPanel.getHeight();
        int width = sudokuPanel.getWidth();
        Dimension preferredSize;
        if (height >= width) {
            preferredSize = new Dimension(height, width);
        } else {
            preferredSize = new Dimension(height, height);
        }

        sudokuPanel.setPreferredSize(preferredSize);
        this.sudokuPanel = sudokuPanel;
        this.splitPane.setLeftComponent(sudokuPanel);
        this.setRight(rightPanel);
        this.adjustDividerBar();
    }

    public void setRight(Component newRightComponent) {
        this.rightComponent = newRightComponent;
        this.splitPane.setRightComponent(newRightComponent);
        this.adjustDividerBar();
    }

    public boolean hasRight() {
        return this.splitPane.getRightComponent() != null;
    }

    private void adjustDividerBar() {
        if (this.sudokuPanel != null) {
            int height = this.sudokuPanel.getHeight();
            int maxWidth = (int) (this.getWidth() * 0.8);
            int pos = 0;
            pos = height > maxWidth ? maxWidth : height;
            this.splitPane.setDividerLocation(pos);
        }
    }

    public int getDividerLocation() {
        return this.splitPane.getDividerLocation();
    }

    public void setDividerLocation(int value) {
        this.splitPane.setDividerLocation(value);
    }
}
