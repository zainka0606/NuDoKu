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
import java.util.List;
import java.util.ResourceBundle;

public class RestoreSavePointDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private MainFrame mainFrame;
    private List<GuiState> savePoints;
    private String[][] data;
    private String[] columnNames = new String[]{
            ResourceBundle.getBundle("intl/RestoreSavePoint").getString("RestoreSavePointDialog.col1"),
            ResourceBundle.getBundle("intl/RestoreSavePoint").getString("RestoreSavePointDialog.col2")
    };
    private boolean okPressed = false;
    private JButton cancelButton;
    private JScrollPane jScrollPane1;
    private JButton okButton;
    private JTable savePointTable;

    public RestoreSavePointDialog(Frame parent, boolean modal) {
        super(parent, modal);
        this.initComponents();
        this.mainFrame = (MainFrame) parent;
        this.savePoints = this.mainFrame.getSavePoints();
        this.initTable();
        this.getRootPane().setDefaultButton(this.okButton);
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(27, 0, false);
        Action escapeAction = new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                RestoreSavePointDialog.this.setVisible(false);
            }
        };
        this.getRootPane().getInputMap(2).put(escapeKeyStroke, "ESCAPE");
        this.getRootPane().getActionMap().put("ESCAPE", escapeAction);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                RestoreSavePointDialog dialog = new RestoreSavePointDialog(new JFrame(), true);
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
        this.okButton = new JButton();
        this.cancelButton = new JButton();
        this.jScrollPane1 = new JScrollPane();
        this.savePointTable = new JTable();
        this.setDefaultCloseOperation(2);
        ResourceBundle bundle = ResourceBundle.getBundle("intl/RestoreSavePoint");
        this.setTitle(bundle.getString("title"));
        this.okButton.setMnemonic(ResourceBundle.getBundle("intl/RestoreSavePoint").getString("RestoreSavePointDialog.okButton.mnemonic").charAt(0));
        this.okButton.setText(bundle.getString("RestoreSavePointDialog.okButton.text"));
        this.okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                RestoreSavePointDialog.this.okButtonActionPerformed(evt);
            }
        });
        this.cancelButton.setMnemonic(ResourceBundle.getBundle("intl/RestoreSavePoint").getString("RestoreSavePointDialog.cancelButton.mnemonic").charAt(0));
        this.cancelButton.setText(bundle.getString("RestoreSavePointDialog.cancelButton.text"));
        this.cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                RestoreSavePointDialog.this.cancelButtonActionPerformed(evt);
            }
        });
        this.savePointTable
                .setModel(
                        new DefaultTableModel(
                                new Object[][]{{null, null, null, null}, {null, null, null, null}, {null, null, null, null}, {null, null, null, null}},
                                new String[]{"Title 1", "Title 2", "Title 3", "Title 4"}
                        )
                );
        this.savePointTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                RestoreSavePointDialog.this.savePointTableMouseClicked(evt);
            }
        });
        this.jScrollPane1.setViewportView(this.savePointTable);
        GroupLayout layout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                layout.createParallelGroup(Alignment.LEADING)
                                                        .addGroup(
                                                                Alignment.TRAILING,
                                                                layout.createSequentialGroup()
                                                                        .addComponent(this.okButton)
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addComponent(this.cancelButton)
                                                        )
                                                        .addComponent(this.jScrollPane1, -1, 322, 32767)
                                        )
                                        .addContainerGap()
                        )
        );
        layout.linkSize(0, this.cancelButton, this.okButton);
        layout.setVerticalGroup(
                layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                Alignment.TRAILING,
                                layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(this.jScrollPane1, -2, 154, -2)
                                        .addPreferredGap(ComponentPlacement.RELATED, -1, 32767)
                                        .addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(this.okButton).addComponent(this.cancelButton))
                                        .addContainerGap()
                        )
        );
        this.pack();
    }

    private void okButtonActionPerformed(ActionEvent evt) {
        if (this.savePointTable.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    ResourceBundle.getBundle("intl/RestoreSavePoint").getString("RestoreSavePointDialog.error.message"),
                    ResourceBundle.getBundle("intl/RestoreSavePoint").getString("RestoreSavePointDialog.error.title"),
                    0
            );
        } else {
            this.mainFrame.setState(this.savePoints.get(this.savePointTable.getSelectedRow()));
            this.okPressed = true;
            this.setVisible(false);
        }
    }

    private void cancelButtonActionPerformed(ActionEvent evt) {
        this.okPressed = false;
        this.setVisible(false);
    }

    private void savePointTableMouseClicked(MouseEvent evt) {
        if (evt.getButton() == 1 && evt.getClickCount() == 2) {
            this.okButtonActionPerformed(null);
        }
    }

    private void initTable() {
        this.data = new String[this.savePoints.size()][2];
        DateFormat tf = DateFormat.getTimeInstance();

        for (int i = 0; i < this.savePoints.size(); i++) {
            GuiState state = this.savePoints.get(i);
            this.data[i][0] = tf.format(state.getTimestamp());
            this.data[i][1] = state.getName();
        }

        this.savePointTable.setModel(new DefaultTableModel(this.data, this.columnNames) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        });
        this.savePointTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                RestoreSavePointDialog.this.tableSelectionChanged(e);
            }
        });
        TableCellRenderer renderer = new RestoreSavePointDialog.MyTableCellRenderer();
        TableColumnModel cm = this.savePointTable.getColumnModel();

        for (int i = 0; i < cm.getColumnCount(); i++) {
            TableColumn column = cm.getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(50);
            } else {
                column.setPreferredWidth(150);
            }

            column.setCellRenderer(renderer);
        }

        renderer = this.savePointTable.getTableHeader().getDefaultRenderer();
        JLabel label = (JLabel) renderer;
        label.setHorizontalAlignment(0);
    }

    private void tableSelectionChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            this.mainFrame.setState(this.savePoints.get(this.savePointTable.getSelectedRow()));
        }
    }

    public boolean isOkPressed() {
        return this.okPressed;
    }

    class MyTableCellRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            RestoreSavePointDialog.MyTableCellRenderer comp = (RestoreSavePointDialog.MyTableCellRenderer) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column
            );
            comp.setHorizontalAlignment(0);
            return comp;
        }
    }
}
