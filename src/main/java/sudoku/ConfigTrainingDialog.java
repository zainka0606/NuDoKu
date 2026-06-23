package sudoku;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.text.MessageFormat;
import java.util.ResourceBundle;

public class ConfigTrainingDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private ConfigTrainigPanel myConfigTrainingPanel;
    private boolean okPressed;
    private JButton cancelButton;
    private JPanel containerPanel;
    private JButton okButton;

    public ConfigTrainingDialog(Frame parent, boolean modal) {
        super(parent, modal);
        this.initComponents();
        this.okPressed = false;
        this.getRootPane().setDefaultButton(this.okButton);
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(27, 0, false);
        Action escapeAction = new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                ConfigTrainingDialog.this.setVisible(false);
            }
        };
        this.getRootPane().getInputMap(2).put(escapeKeyStroke, "ESCAPE");
        this.getRootPane().getActionMap().put("ESCAPE", escapeAction);
        this.myConfigTrainingPanel = new ConfigTrainigPanel();
        this.containerPanel.add(this.myConfigTrainingPanel, "Center");
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
        }

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ConfigTrainingDialog(new JFrame(), true).setVisible(true);
            }
        });
    }

    private void initComponents() {
        this.okButton = new JButton();
        this.cancelButton = new JButton();
        this.containerPanel = new JPanel();
        this.setDefaultCloseOperation(2);
        ResourceBundle bundle = ResourceBundle.getBundle("intl/ConfigTrainingDialog");
        this.setTitle(bundle.getString("ConfigTrainingDialog.title"));
        this.okButton.setMnemonic(ResourceBundle.getBundle("intl/ConfigTrainingDialog").getString("ConfigTrainingDialog.okButton.mnemonic").charAt(0));
        this.okButton.setText(bundle.getString("ConfigTrainingDialog.okButton.text"));
        this.okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigTrainingDialog.this.okButtonActionPerformed(evt);
            }
        });
        this.cancelButton.setMnemonic(ResourceBundle.getBundle("intl/ConfigTrainingDialog").getString("ConfigTrainingDialog.cancelButton.mnemonic").charAt(0));
        this.cancelButton.setText(bundle.getString("ConfigTrainingDialog.cancelButton.text"));
        this.cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigTrainingDialog.this.cancelButtonActionPerformed(evt);
            }
        });
        this.containerPanel.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
        this.containerPanel.setLayout(new BorderLayout());
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
                                                        .addComponent(this.containerPanel, -1, 546, 32767)
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
                                        .addComponent(this.containerPanel, -1, 461, 32767)
                                        .addPreferredGap(ComponentPlacement.UNRELATED)
                                        .addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(this.okButton).addComponent(this.cancelButton))
                                        .addContainerGap()
                        )
        );
        this.pack();
    }

    private void okButtonActionPerformed(ActionEvent evt) {
        this.myConfigTrainingPanel.okPressed();

        try {
            Options.getInstance().writeOptions();
        } catch (FileNotFoundException ex) {
            MessageFormat formatter = new MessageFormat(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.invalid_filename"));
            String msg = formatter.format(new Object[]{ex.getLocalizedMessage()});
            JOptionPane.showMessageDialog(this, msg, ResourceBundle.getBundle("intl/ConfigDialog").getString("ConfigDialog.error"), 0);
        }

        this.okPressed = true;
        this.setVisible(false);
    }

    private void cancelButtonActionPerformed(ActionEvent evt) {
        this.okPressed = false;
        this.setVisible(false);
    }

    public boolean isOkPressed() {
        return this.okPressed;
    }
}
