package sudoku;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;

public class SetGivensDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private boolean okPressed = false;
    private String givens = null;
    private JButton cancelButton;
    private JTextArea givensTextArea;
    private JLabel jLabel1;
    private JScrollPane jScrollPane1;
    private JButton okButton;

    public SetGivensDialog(Frame parent, boolean modal) {
        super(parent, modal);
        this.initComponents();
        this.getRootPane().setDefaultButton(this.okButton);
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(27, 0, false);
        Action escapeAction = new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                SetGivensDialog.this.setVisible(false);
            }
        };
        this.getRootPane().getInputMap(2).put(escapeKeyStroke, "ESCAPE");
        this.getRootPane().getActionMap().put("ESCAPE", escapeAction);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                SetGivensDialog dialog = new SetGivensDialog(new JFrame(), true);
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
        this.jLabel1 = new JLabel();
        this.jScrollPane1 = new JScrollPane();
        this.givensTextArea = new JTextArea();
        this.okButton = new JButton();
        this.cancelButton = new JButton();
        this.setDefaultCloseOperation(2);
        ResourceBundle bundle = ResourceBundle.getBundle("intl/SetGivensDialog");
        this.setTitle(bundle.getString("SetGivensDialog.title"));
        this.jLabel1.setText(bundle.getString("SetGivensDialog.jLabel1.text"));
        this.givensTextArea.setColumns(20);
        this.givensTextArea.setRows(5);
        this.jScrollPane1.setViewportView(this.givensTextArea);
        this.okButton.setMnemonic(ResourceBundle.getBundle("intl/SetGivensDialog").getString("SetGivensDialog.okButton.mnemonic").charAt(0));
        this.okButton.setText(bundle.getString("SetGivensDialog.okButton.text"));
        this.okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                SetGivensDialog.this.okButtonActionPerformed(evt);
            }
        });
        this.cancelButton.setMnemonic(ResourceBundle.getBundle("intl/SetGivensDialog").getString("SetGivensDialog.cancelButton.mnemonics").charAt(0));
        this.cancelButton.setText(bundle.getString("SetGivensDialog.cancelButton.text"));
        this.cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                SetGivensDialog.this.cancelButtonActionPerformed(evt);
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
                                                        .addComponent(this.jScrollPane1, -1, 404, 32767)
                                                        .addComponent(this.jLabel1)
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
                                        .addComponent(this.jLabel1)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.jScrollPane1, -2, 319, -2)
                                        .addPreferredGap(ComponentPlacement.RELATED, -1, 32767)
                                        .addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(this.okButton).addComponent(this.cancelButton))
                                        .addContainerGap()
                        )
        );
        this.pack();
    }

    private void cancelButtonActionPerformed(ActionEvent evt) {
        this.okPressed = false;
        this.setVisible(false);
    }

    private void okButtonActionPerformed(ActionEvent evt) {
        String text = this.givensTextArea.getText();
        String lineEnd = null;
        if (text.contains("\r\n")) {
            lineEnd = "\r\n";
        } else if (text.contains("\r")) {
            lineEnd = "\r";
        } else if (text.contains("\n")) {
            lineEnd = "\n";
        }

        String[] lines = text.split(text);
        if (lineEnd != null) {
            lines = text.split(lineEnd);
        } else {
            lines = new String[]{text};
        }

        StringBuilder output = new StringBuilder();

        for (int i = 0; i < lines.length; i++) {
            if (lines[i] != null) {
                lines[i] = lines[i].trim();
                if (lines[i].contains("---")) {
                    for (int j = i; j < lines.length - 1; j++) {
                        lines[j] = lines[j + 1];
                    }

                    lines[lines.length - 1] = null;
                }
            }

            if (lines[i] != null) {
                StringBuffer tmp = new StringBuffer(lines[i].trim());

                for (int j = 0; j < tmp.length(); j++) {
                    char ch = tmp.charAt(j);
                    if (!Character.isDigit(ch) && ch != '.') {
                        tmp.deleteCharAt(j);
                        j--;
                    }
                }

                output.append(tmp);
            }
        }

        this.givens = output.toString();
        if (this.givens.length() != 81) {
            JOptionPane.showMessageDialog(
                    this.rootPane,
                    ResourceBundle.getBundle("intl/SetGivensDialog").getString("SetGivensDialog.error.message"),
                    ResourceBundle.getBundle("intl/SetGivensDialog").getString("SetGivensDialog.error.title"),
                    0
            );
            this.givens = null;
        } else {
            this.okPressed = true;
            this.setVisible(false);
        }
    }

    public boolean isOkPressed() {
        return this.okPressed;
    }

    public String getGivens() {
        return this.givens;
    }
}
