package sudoku;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GeneratorPatternPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final int UNITS = 9;
    private int borderWidth = 5;
    private boolean[] pattern = null;
    private int cellSize = -1;

    public GeneratorPatternPanel() {
        this.initComponents();
    }

    private void initComponents() {
        this.setBackground(new Color(255, 255, 255));
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                GeneratorPatternPanel.this.formMouseClicked(evt);
            }
        });
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING).addGap(0, 400, 32767));
        layout.setVerticalGroup(layout.createParallelGroup(Alignment.LEADING).addGap(0, 300, 32767));
    }

    private void formMouseClicked(MouseEvent evt) {
        if (this.pattern != null
                && this.cellSize >= 0
                && evt.getButton() == 1
                && evt.getX() >= this.borderWidth
                && evt.getX() <= this.borderWidth + 9 * this.cellSize
                && evt.getY() >= this.borderWidth
                && evt.getY() <= this.borderWidth + 9 * this.cellSize) {
            int col = (evt.getX() - this.borderWidth) / this.cellSize;
            int row = (evt.getY() - this.borderWidth) / this.cellSize;
            int index = row * 9 + col;
            this.pattern[index] = !this.pattern[index];
            this.updateAnzGivens();
            this.repaint();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        int width = this.getWidth();
        int height = this.getHeight();
        width = width < height ? width : height;
        height = height < width ? height : width;
        int offsetX = Math.max(this.getInsets().left, this.getInsets().right);
        int offsetY = Math.max(this.getInsets().top, this.getInsets().bottom);
        int bWidth = Math.max(offsetX, offsetY);
        this.borderWidth = Math.max(5, bWidth);
        this.cellSize = (width - 2 * this.borderWidth) / 9;
        g2.setColor(Color.BLACK);
        if (this.pattern != null) {
            for (int i = 0; i < this.pattern.length; i++) {
                if (this.pattern[i]) {
                    int row = i / 9;
                    int col = i % 9;
                    g2.fillRect(this.borderWidth + col * this.cellSize, this.borderWidth + row * this.cellSize, this.cellSize, this.cellSize);
                }
            }
        }

        int max = 9 * this.cellSize;

        for (int i = 0; i <= 9; i++) {
            g2.drawLine(this.borderWidth + 0, this.borderWidth + i * this.cellSize, this.borderWidth + max, this.borderWidth + i * this.cellSize);
            g2.drawLine(this.borderWidth + i * this.cellSize, this.borderWidth + 0, this.borderWidth + i * this.cellSize, this.borderWidth + max);
        }
    }

    private void updateAnzGivens() {
        Component generatorPanel = this.getParent().getParent();
        if (generatorPanel instanceof ConfigGeneratorPanel) {
            int anz = 0;
            if (this.pattern != null) {
                for (int i = 0; i < this.pattern.length; i++) {
                    if (this.pattern[i]) {
                        anz++;
                    }
                }
            }

            ((ConfigGeneratorPanel) generatorPanel).setAnzGivens(anz);
        }
    }

    public boolean[] getPattern() {
        return this.pattern;
    }

    public void setPattern(boolean[] pattern) {
        this.pattern = pattern;
        this.repaint();
        this.updateAnzGivens();
    }
}
