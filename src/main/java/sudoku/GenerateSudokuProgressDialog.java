package sudoku;

import generator.BackgroundGenerator;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GenerateSudokuProgressDialog extends JDialog implements Runnable {
    private static final long serialVersionUID = 1L;
    private Sudoku2 sudoku;
    private Thread thread;
    private DifficultyLevel level;
    private GameMode mode;
    private BackgroundGenerator generator;
    private JButton cancelButton;
    private JPanel jPanel1;
    private JLabel progressLabel;

    public GenerateSudokuProgressDialog(Frame parent, boolean modal, DifficultyLevel level, GameMode mode) {
        super(parent, modal);
        this.initComponents();
        this.getRootPane().setDefaultButton(this.cancelButton);
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(27, 0, false);
        Action escapeAction = new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                GenerateSudokuProgressDialog.this.setVisible(false);
            }
        };
        this.getRootPane().getInputMap(2).put(escapeKeyStroke, "ESCAPE");
        this.getRootPane().getActionMap().put("ESCAPE", escapeAction);
        this.level = level;
        this.mode = mode;
        this.thread = new Thread(this);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        new GenerateSudokuProgressDialog(
                                new JFrame(), true, Options.getInstance().getDifficultyLevels()[DifficultyType.EASY.ordinal()], GameMode.PLAYING
                        )
                                .setVisible(true);
                    }
                }
        );
    }

    private void initComponents() {
        this.progressLabel = new JLabel();
        this.jPanel1 = new JPanel();
        this.cancelButton = new JButton();
        this.setDefaultCloseOperation(2);
        ResourceBundle bundle = ResourceBundle.getBundle("intl/GenerateSudokuProgressDialog");
        this.setTitle(bundle.getString("GenerateSudokuProgressDialog.title"));
        this.setLocationByPlatform(true);
        this.setModal(true);
        this.setResizable(false);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                GenerateSudokuProgressDialog.this.formWindowClosing(evt);
            }

            @Override
            public void windowOpened(WindowEvent evt) {
                GenerateSudokuProgressDialog.this.formWindowOpened(evt);
            }
        });
        this.progressLabel.setHorizontalAlignment(0);
        this.progressLabel.setText("0");
        this.cancelButton
                .setMnemonic(ResourceBundle.getBundle("intl/GenerateSudokuProgressDialog").getString("GenerateSudokuProgressDialog.cancelButton.mnemonic").charAt(0));
        this.cancelButton.setText(bundle.getString("GenerateSudokuProgressDialog.cancelButton.text"));
        this.cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                GenerateSudokuProgressDialog.this.cancelButtonActionPerformed(evt);
            }
        });
        this.jPanel1.add(this.cancelButton);
        GroupLayout layout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                layout.createParallelGroup(Alignment.LEADING).addComponent(this.jPanel1, -1, 196, 32767).addComponent(this.progressLabel, -1, 196, 32767)
                                        )
                                        .addContainerGap()
                        )
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addComponent(this.progressLabel)
                                        .addPreferredGap(ComponentPlacement.UNRELATED)
                                        .addComponent(this.jPanel1, -2, -1, -2)
                                        .addContainerGap(-1, 32767)
                        )
        );
        this.pack();
    }

    private void cancelButtonActionPerformed(ActionEvent evt) {
        this.thread.interrupt();

        try {
            this.thread.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Interrupted while waiting for generation thread", ex);
        }

        this.sudoku = null;
        this.setVisible(false);
    }

    private void formWindowClosing(WindowEvent evt) {
        this.cancelButtonActionPerformed(null);
    }

    private void formWindowOpened(WindowEvent evt) {
        this.thread.start();
    }

    @Override
    public void run() {
        this.generator = new BackgroundGenerator();
        this.sudoku = this.generator.generate(this.level, this.mode, this);
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                GenerateSudokuProgressDialog.this.setVisible(false);
            }
        });
    }

    public void updateProgressLabel() {
        this.progressLabel.setText(Integer.toString(this.generator.getAnz()));
    }

    public Sudoku2 getSudoku() {
        return this.sudoku;
    }
}
