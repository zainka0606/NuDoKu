package sudoku;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class HistoryDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private MainFrame mainFrame;
    private String[][] data;
    private String[] puzzles;
    private int[] stepLevels;
    private String[] columnNames = new String[]{
            ResourceBundle.getBundle("intl/HistoryDialog").getString("HistoryDialog.col1"),
            ResourceBundle.getBundle("intl/HistoryDialog").getString("HistoryDialog.col2"),
            ResourceBundle.getBundle("intl/HistoryDialog").getString("HistoryDialog.col3"),
            ResourceBundle.getBundle("intl/HistoryDialog").getString("HistoryDialog.col4")
    };
    private boolean okPressed = false;
    private boolean doubleClicked = false;
    private JButton cancelButton;
    private JTable historyTable;
    private JScrollPane jScrollPane1;
    private JButton okButton;
    private JCheckBox previewCheckBox;

    public HistoryDialog(Frame parent, boolean modal) {
        super(parent, modal);
        this.initComponents();
        this.mainFrame = (MainFrame) parent;
        this.initTable();
        this.previewCheckBox.setSelected(Options.getInstance().isHistoryPreview());
        this.getRootPane().setDefaultButton(this.okButton);
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(27, 0, false);
        Action escapeAction = new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                HistoryDialog.this.setVisible(false);
            }
        };
        this.getRootPane().getInputMap(2).put(escapeKeyStroke, "ESCAPE");
        this.getRootPane().getActionMap().put("ESCAPE", escapeAction);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                HistoryDialog dialog = new HistoryDialog(new JFrame(), true);
                dialog.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    private void initComponents() {
        this.jScrollPane1 = new JScrollPane();
        this.historyTable = new JTable();
        this.okButton = new JButton();
        this.cancelButton = new JButton();
        this.previewCheckBox = new JCheckBox();
        this.setDefaultCloseOperation(2);
        ResourceBundle bundle = ResourceBundle.getBundle("intl/HistoryDialog");
        this.setTitle(bundle.getString("title"));
        this.historyTable
                .setModel(
                        new DefaultTableModel(
                                new Object[][]{{null, null, null, null}, {null, null, null, null}, {null, null, null, null}, {null, null, null, null}},
                                new String[]{"Title 1", "Title 2", "Title 3", "Title 4"}
                        )
                );
        this.historyTable.setSelectionMode(0);
        this.historyTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                HistoryDialog.this.historyTableMouseClicked(evt);
            }
        });
        this.jScrollPane1.setViewportView(this.historyTable);
        this.historyTable.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("HistoryDialog.historyTable.columnModel.title0"));
        this.historyTable.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("HistoryDialog.historyTable.columnModel.title1"));
        this.historyTable.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("HistoryDialog.historyTable.columnModel.title2"));
        this.historyTable.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("HistoryDialog.historyTable.columnModel.title3"));
        this.okButton.setMnemonic(ResourceBundle.getBundle("intl/HistoryDialog").getString("HistoryDialog.okButton.mnemonic").charAt(0));
        this.okButton.setText(bundle.getString("HistoryDialog.okButton.text"));
        this.okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                HistoryDialog.this.okButtonActionPerformed(evt);
            }
        });
        this.cancelButton.setMnemonic(ResourceBundle.getBundle("intl/HistoryDialog").getString("HistoryDialog.cancelButton.mnemonic").charAt(0));
        this.cancelButton.setText(bundle.getString("HistoryDialog.cancelButton.text"));
        this.cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                HistoryDialog.this.cancelButtonActionPerformed(evt);
            }
        });
        this.previewCheckBox.setMnemonic(ResourceBundle.getBundle("intl/HistoryDialog").getString("HistoryDialog.previewCheckBox.mnemonic").charAt(0));
        this.previewCheckBox.setText(bundle.getString("HistoryDialog.previewCheckBox.text"));
        this.previewCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                HistoryDialog.this.previewCheckBoxActionPerformed(evt);
            }
        });
        GroupLayout layout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                layout.createParallelGroup(Alignment.LEADING)
                                                        .addComponent(this.previewCheckBox)
                                                        .addComponent(this.jScrollPane1, -1, 354, 32767)
                                                        .addGroup(
                                                                Alignment.TRAILING,
                                                                layout.createSequentialGroup()
                                                                        .addComponent(this.okButton)
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addComponent(this.cancelButton)
                                                        )
                                        )
                                        .addContainerGap()
                        )
        );
        layout.linkSize(0, this.cancelButton, this.okButton);
        layout.setVerticalGroup(
                layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(this.jScrollPane1, -2, 202, -2)
                                        .addPreferredGap(ComponentPlacement.UNRELATED)
                                        .addComponent(this.previewCheckBox)
                                        .addPreferredGap(ComponentPlacement.RELATED, -1, 32767)
                                        .addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(this.okButton).addComponent(this.cancelButton))
                                        .addContainerGap()
                        )
        );
        this.pack();
    }

    private void okButtonActionPerformed(ActionEvent evt) {
        if (this.historyTable.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    ResourceBundle.getBundle("intl/HistoryDialog").getString("HistoryDialog.error.message"),
                    ResourceBundle.getBundle("intl/HistoryDialog").getString("HistoryDialog.error.title"),
                    0
            );
        } else {
            this.okPressed = true;
            this.setVisible(false);
        }
    }

    private void cancelButtonActionPerformed(ActionEvent evt) {
        this.okPressed = false;
        this.setVisible(false);
    }

    private void historyTableMouseClicked(MouseEvent evt) {
        if (evt.getButton() == 1 && evt.getClickCount() == 2) {
            this.doubleClicked = true;
            this.okButtonActionPerformed(null);
        }
    }

    private void previewCheckBoxActionPerformed(ActionEvent evt) {
        Options.getInstance().setHistoryPreview(this.previewCheckBox.isSelected());
    }

    private void initTable() {
        List<String> list = Options.getInstance().getHistoryOfCreatedPuzzles();
        this.data = new String[list.size()][4];
        this.puzzles = new String[list.size()];
        this.stepLevels = new int[list.size()];
        DifficultyLevel[] levels = Options.getInstance().getDifficultyLevels();
        DateFormat df = DateFormat.getDateInstance();
        DateFormat tf = DateFormat.getTimeInstance();

        for (int i = 0; i < list.size(); i++) {
            String[] parts = list.get(i).split("#");
            this.puzzles[i] = parts[0];
            this.stepLevels[i] = Integer.parseInt(parts[1]);
            Date date = new Date(Long.parseLong(parts[3]));
            this.data[i][0] = df.format(date);
            this.data[i][1] = tf.format(date);
            this.data[i][2] = levels[this.stepLevels[i]].getName();
            this.data[i][3] = parts[2];
        }

        this.historyTable.setModel(new DefaultTableModel(this.data, this.columnNames) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        });
        this.historyTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                HistoryDialog.this.tableSelectionChanged(e);
            }
        });
        TableCellRenderer renderer = new HistoryDialog.MyTableCellRenderer();
        TableColumnModel cm = this.historyTable.getColumnModel();

        for (int i = 0; i < cm.getColumnCount(); i++) {
            TableColumn column = cm.getColumn(i);
            if (i != 0 && i != 1) {
                column.setPreferredWidth(50);
            } else {
                column.setPreferredWidth(100);
            }

            column.setCellRenderer(renderer);
        }

        renderer = this.historyTable.getTableHeader().getDefaultRenderer();
        JLabel label = (JLabel) renderer;
        label.setHorizontalAlignment(0);
    }

    private void tableSelectionChanged(ListSelectionEvent e) {
        if (this.previewCheckBox.isSelected() && !e.getValueIsAdjusting()) {
            this.mainFrame.setPuzzle(this.puzzles[this.historyTable.getSelectedRow()]);
        }
    }

    public String getSelectedPuzzle() {
        return this.okPressed ? this.puzzles[this.historyTable.getSelectedRow()] : null;
    }

    public boolean isDoubleClicked() {
        return this.doubleClicked;
    }

    class MyTableCellRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            HistoryDialog.MyTableCellRenderer comp = (HistoryDialog.MyTableCellRenderer) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column
            );
            if (!isSelected) {
                comp.setBackground(Options.getInstance().getDifficultyLevels()[HistoryDialog.this.stepLevels[row]].getBackgroundColor());
                comp.setForeground(Options.getInstance().getDifficultyLevels()[HistoryDialog.this.stepLevels[row]].getForegroundColor());
            }

            if (column == 3) {
                comp.setHorizontalAlignment(4);
            } else {
                comp.setHorizontalAlignment(0);
            }

            return comp;
        }
    }
}
