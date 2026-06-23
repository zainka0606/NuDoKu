package sudoku;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KeyboardLayoutFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private JButton closeButton;
    private JScrollPane jScrollPane1;
    private JEditorPane kbdEditorPane;

    public KeyboardLayoutFrame() {
        this.initComponents();
        URL helpUrl = this.getClass().getResource("/help/keyboard.html");
        String language = Locale.getDefault().getLanguage().toLowerCase();
        String helpFileName = "/help/keyboard_" + language + ".html";

        try {
            URL tmpUrl = this.getClass().getResource(helpFileName);
            if (tmpUrl != null) {
                helpUrl = tmpUrl;
            }
        } catch (Exception ex) {
            Logger.getLogger(KeyboardLayoutFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            this.kbdEditorPane.setPage(helpUrl);
        } catch (IOException ex) {
            Logger.getLogger(KeyboardLayoutFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.getRootPane().setDefaultButton(this.closeButton);
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(27, 0, false);
        Action escapeAction = new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                KeyboardLayoutFrame.this.setVisible(false);
            }
        };
        this.getRootPane().getInputMap(2).put(escapeKeyStroke, "ESCAPE");
        this.getRootPane().getActionMap().put("ESCAPE", escapeAction);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new KeyboardLayoutFrame().setVisible(true);
            }
        });
    }

    private void initComponents() {
        this.closeButton = new JButton();
        this.jScrollPane1 = new JScrollPane();
        this.kbdEditorPane = new JEditorPane();
        this.setDefaultCloseOperation(2);
        ResourceBundle bundle = ResourceBundle.getBundle("intl/KeyboardLayoutFrame");
        this.setTitle(bundle.getString("KeyboardLayoutFrame.title"));
        this.setIconImage(this.getIcon());
        this.closeButton.setMnemonic(ResourceBundle.getBundle("intl/KeyboardLayoutFrame").getString("KeyboardLayoutFrame.closeButton.mnemonic").charAt(0));
        this.closeButton.setText(bundle.getString("KeyboardLayoutFrame.closeButton.text"));
        this.closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                KeyboardLayoutFrame.this.closeButtonActionPerformed(evt);
            }
        });
        this.kbdEditorPane.setEditable(false);
        this.jScrollPane1.setViewportView(this.kbdEditorPane);
        GroupLayout layout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                layout.createParallelGroup(Alignment.LEADING)
                                                        .addComponent(this.closeButton, Alignment.TRAILING)
                                                        .addComponent(this.jScrollPane1, -1, 521, 32767)
                                        )
                                        .addContainerGap()
                        )
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                Alignment.TRAILING,
                                layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(this.jScrollPane1, -1, 445, 32767)
                                        .addGap(18, 18, 18)
                                        .addComponent(this.closeButton)
                                        .addContainerGap()
                        )
        );
        this.pack();
    }

    private void closeButtonActionPerformed(ActionEvent evt) {
        this.setVisible(false);
    }

    private Image getIcon() {
        URL url = this.getClass().getResource("/img/help3.gif");
        return this.getToolkit().getImage(url);
    }
}
