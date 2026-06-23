package sudoku;

import solver.SudokuSolver;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ResourceBundle;

public class SummaryPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private MainFrame mainFrame;
    private SummaryPanel.SummaryTableModel model;
    private JScrollPane jScrollPane1;
    private JTable summaryTable;
    private JLabel titleLabel;

    public SummaryPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.initComponents();
        this.model = new SummaryPanel.SummaryTableModel();
        this.summaryTable.setModel(this.model);
        this.summaryTable.setDefaultRenderer(Object.class, new SummaryPanel.SummaryTableRenderer());
        TableColumn column = null;

        for (int i = 0; i < 3; i++) {
            column = this.summaryTable.getColumnModel().getColumn(i);
            if (i != 0 && i != 2) {
                column.setPreferredWidth(200);
            } else {
                column.setPreferredWidth(10);
            }
        }

        FontMetrics metrics = this.getFontMetrics(this.getFont());
        int rowHeight = (int) (metrics.getHeight() * 1.1);
        this.summaryTable.setRowHeight(rowHeight);
        int fontSize = 12;
        if (this.getFont().getSize() > 12) {
            fontSize = this.getFont().getSize();
        }

        Font font = this.titleLabel.getFont();
        this.titleLabel.setFont(new Font(font.getName(), 1, fontSize));
    }

    private void initComponents() {
        this.jScrollPane1 = new JScrollPane();
        this.summaryTable = new JTable();
        this.titleLabel = new JLabel();
        this.setLayout(new BorderLayout());
        this.summaryTable.setModel(new DefaultTableModel(new Object[0][], new String[0]));
        this.summaryTable.setCellSelectionEnabled(true);
        this.summaryTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                SummaryPanel.this.summaryTableMouseClicked(evt);
            }
        });
        this.jScrollPane1.setViewportView(this.summaryTable);
        this.add(this.jScrollPane1, "Center");
        this.titleLabel.setBackground(new Color(51, 51, 255));
        this.titleLabel.setForeground(new Color(255, 255, 255));
        this.titleLabel.setHorizontalAlignment(0);
        ResourceBundle bundle = ResourceBundle.getBundle("intl/SummaryPanel");
        this.titleLabel.setText(bundle.getString("SummaryPanel.titleLabel.text"));
        this.titleLabel.setOpaque(true);
        this.add(this.titleLabel, "First");
    }

    private void summaryTableMouseClicked(MouseEvent evt) {
        this.mainFrame.fixFocus();
    }

    public void setTitleLabelColors(Color fore, Color back) {
        this.titleLabel.setBackground(back);
        this.titleLabel.setForeground(fore);
    }

    public void initialize(SudokuSolver solver) {
        this.model.initialize(solver);
    }

    class SummaryTableModel extends AbstractTableModel {
        private static final long serialVersionUID = 1L;
        String[] columnNames = new String[]{
                ResourceBundle.getBundle("intl/SummaryPanel").getString("SummaryPanel.count"),
                ResourceBundle.getBundle("intl/SummaryPanel").getString("SummaryPanel.technique"),
                ResourceBundle.getBundle("intl/SummaryPanel").getString("SummaryPanel.score")
        };
        Object[][] content = new Object[][]{{"", "", "", null}};

        public void initialize(SudokuSolver solver) {
            if (solver != null) {
                this.content = new Object[solver.getAnzUsedSteps() + 1][4];
                int[] anzSteps = solver.getAnzSteps();
                int index = 0;

                for (int i = 0; i < anzSteps.length; i++) {
                    if (anzSteps[i] > 0) {
                        StepConfig config = Options.getInstance().solverSteps[i];
                        this.content[index][0] = Integer.toString(anzSteps[i]);
                        this.content[index][1] = config.getType().getStepName();
                        this.content[index][2] = Integer.toString(anzSteps[i] * config.getBaseScore() + config.getAdminScore());
                        this.content[index][3] = Options.getInstance().getDifficultyLevels()[config.getLevel()].getBackgroundColor();
                        index++;
                    }
                }

                this.content[index][1] = ResourceBundle.getBundle("intl/SummaryPanel").getString("SummaryPanel.sum");
                this.content[index][2] = Integer.toString(solver.getScore());
            } else {
                this.content = new Object[1][4];
                this.content[0][0] = "";
                this.content[0][1] = "";
                this.content[0][2] = "";
                this.content[0][3] = null;
            }

            this.fireTableDataChanged();
        }

        @Override
        public int getColumnCount() {
            return this.columnNames.length;
        }

        @Override
        public int getRowCount() {
            return this.content.length;
        }

        @Override
        public String getColumnName(int col) {
            return this.columnNames[col];
        }

        @Override
        public Object getValueAt(int row, int col) {
            return this.content[row][col];
        }
    }

    class SummaryTableRenderer extends JLabel implements TableCellRenderer {
        private static final long serialVersionUID = 1L;
        private Color backColor;

        SummaryTableRenderer() {
            this.setOpaque(true);
            this.backColor = this.getBackground();
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (SummaryPanel.this.model.content[row][3] != null) {
                this.setBackground((Color) SummaryPanel.this.model.content[row][3]);
            } else {
                this.setBackground(this.backColor);
            }

            String text = value != null ? value.toString() : "";
            if (column != 0 && column != 2) {
                this.setHorizontalAlignment(2);
                text = " " + text;
            } else {
                this.setHorizontalAlignment(4);
                text = text + " ";
            }

            this.setText(text);
            return this;
        }
    }
}
