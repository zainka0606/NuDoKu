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

public class AboutDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private JButton closeButton;
    private JLabel copyleftLabel;
    private JLabel copyrightLabel;
    private JLabel logoLabel;
    private JLabel versionLabel;

    public AboutDialog(Frame parent, boolean modal) {
        super(parent, modal);
        this.initComponents();
        this.versionLabel.setText("HoDoKu - v2.2.0 (" + MainFrame.BUILD + ")");
        this.getRootPane().setDefaultButton(this.closeButton);
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(27, 0, false);
        Action escapeAction = new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                AboutDialog.this.setVisible(false);
            }
        };
        this.getRootPane().getInputMap(2).put(escapeKeyStroke, "ESCAPE");
        this.getRootPane().getActionMap().put("ESCAPE", escapeAction);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                AboutDialog dialog = new AboutDialog(new JFrame(), true);
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
        this.versionLabel = new JLabel();
        this.copyrightLabel = new JLabel();
        this.copyleftLabel = new JLabel();
        this.logoLabel = new JLabel();
        this.closeButton = new JButton();
        this.setDefaultCloseOperation(2);
        ResourceBundle bundle = ResourceBundle.getBundle("intl/AboutDialog");
        this.setTitle(bundle.getString("AboutDialog.title"));
        this.versionLabel.setFont(new Font("Tahoma", 1, 18));
        this.versionLabel.setHorizontalAlignment(0);
        this.versionLabel.setText("HoDoKu v1.0");
        this.copyrightLabel.setHorizontalAlignment(0);
        this.copyrightLabel.setText(bundle.getString("AboutDialog.copyrightLabel.text"));
        this.copyleftLabel.setHorizontalAlignment(0);
        this.copyleftLabel.setText(bundle.getString("AboutDialog.copyleftLabel.text"));
        this.logoLabel.setHorizontalAlignment(0);
        this.logoLabel.setIcon(new ImageIcon(this.getClass().getResource("/img/gplv3-127x51.png")));
        this.closeButton.setMnemonic(ResourceBundle.getBundle("intl/AboutDialog").getString("AboutDialog.closeButton.mnemonic").charAt(0));
        this.closeButton.setText(bundle.getString("AboutDialog.closeButton.text"));
        this.closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                AboutDialog.this.closeButtonActionPerformed(evt);
            }
        });
        GroupLayout layout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addGroup(
                                                layout.createParallelGroup(Alignment.LEADING)
                                                        .addGroup(
                                                                layout.createSequentialGroup()
                                                                        .addContainerGap()
                                                                        .addGroup(
                                                                                layout.createParallelGroup(Alignment.LEADING)
                                                                                        .addComponent(this.logoLabel, -1, 375, 32767)
                                                                                        .addComponent(this.copyrightLabel, -1, 375, 32767)
                                                                                        .addComponent(this.versionLabel, -1, 375, 32767)
                                                                                        .addComponent(this.copyleftLabel, -1, 375, 32767)
                                                                        )
                                                        )
                                                        .addGroup(layout.createSequentialGroup().addGap(147, 147, 147).addComponent(this.closeButton, -2, 102, -2))
                                        )
                                        .addContainerGap()
                        )
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(this.versionLabel)
                                        .addPreferredGap(ComponentPlacement.UNRELATED)
                                        .addComponent(this.copyrightLabel, -2, -1, -2)
                                        .addGap(18, 18, 18)
                                        .addComponent(this.copyleftLabel, -2, -1, -2)
                                        .addGap(18, 18, 18)
                                        .addComponent(this.logoLabel)
                                        .addPreferredGap(ComponentPlacement.RELATED, 49, 32767)
                                        .addComponent(this.closeButton)
                                        .addContainerGap()
                        )
        );
        this.pack();
    }

    private void closeButtonActionPerformed(ActionEvent evt) {
        this.setVisible(false);
    }
}
