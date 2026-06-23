package sudoku;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import java.awt.*;
import java.util.ResourceBundle;

public class StatusColorPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private boolean reset = false;
    private Font font = null;
    private int index = -1;

    public StatusColorPanel(int index) {
        this.index = index;
        if (index == -2) {
            this.reset = true;
            this.font = new Font("SansSerif", 0, 11);
        }

        this.setColor();
        this.initComponents();
    }

    private void initComponents() {
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING).addGap(0, 15, 32767));
        layout.setVerticalGroup(layout.createParallelGroup(Alignment.LEADING).addGap(0, 15, 32767));
    }

    @Override
    protected void paintComponent(Graphics g) {
        this.setColor();
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int w = this.getWidth() - 1;
        int h = this.getHeight() - 1;
        g2.fillRect(0, 0, w, h);
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawLine(w, 0, w, h);
        g2.drawLine(0, h, w, h);
        g2.setColor(Color.DARK_GRAY);
        g2.drawLine(0, 0, w, 0);
        g2.drawLine(0, 0, 0, h);
        if (this.reset) {
            g2.setFont(this.font);
            FontMetrics metrics = g2.getFontMetrics();
            String output = ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.statusPanelReset.text");
            int height = metrics.getAscent();
            int width = metrics.stringWidth(output);
            g2.drawString(output, w / 2 - width / 2 + 0, h / 2 + height / 2 - 1);
        }
    }

    private void setColor() {
        Color back = null;
        if (this.index >= 0) {
            back = Options.getInstance().getColoringColors()[this.index];
        } else {
            back = Options.getInstance().getDefaultCellColor();
        }

        this.setForeground(back);
        this.setBackground(back);
    }
}
