package sudoku;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FindAllStepsProgressDialog extends JDialog {
    private static final int MAX_STEPS = 27;
    private static final long serialVersionUID = 1L;
    private List<SolutionStep> steps;
    private Thread thread;
    private long ticks;
    private JButton abbrechenButton;
    private JProgressBar fishProgressBar;
    private JProgressBar progressBar;
    private JLabel progressLabel;

    public FindAllStepsProgressDialog(Frame parent, boolean modal, Sudoku2 sudoku) {
        super(parent, modal);
        this.initComponents();
        this.getRootPane().setDefaultButton(this.abbrechenButton);
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(27, 0, false);
        Action escapeAction = new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                FindAllStepsProgressDialog.this.abbrechenButtonActionPerformed(null);
            }
        };
        this.getRootPane().getInputMap(2).put(escapeKeyStroke, "ESCAPE");
        this.getRootPane().getActionMap().put("ESCAPE", escapeAction);
        this.progressBar.setMinimum(0);
        this.progressBar.setMaximum(27);
        this.progressBar.setValue(0);
        this.steps = new ArrayList<>();
        FindAllSteps findAllSteps = new FindAllSteps(this.steps, sudoku, this);
        this.thread = new Thread(findAllSteps);
        this.thread.setPriority(10);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new FindAllStepsProgressDialog(new JFrame(), true, null).setVisible(true);
            }
        });
    }

    private void initComponents() {
        this.progressLabel = new JLabel();
        this.progressBar = new JProgressBar();
        this.abbrechenButton = new JButton();
        this.fishProgressBar = new JProgressBar();
        this.setDefaultCloseOperation(2);
        ResourceBundle bundle = ResourceBundle.getBundle("intl/FindAllStepsProgressDialog");
        this.setTitle(bundle.getString("FindAllStepsProgressDialog.title"));
        this.setResizable(false);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                FindAllStepsProgressDialog.this.formWindowClosing(evt);
            }

            @Override
            public void windowOpened(WindowEvent evt) {
                FindAllStepsProgressDialog.this.formWindowOpened(evt);
            }
        });
        this.progressLabel.setHorizontalAlignment(0);
        this.progressLabel.setText(bundle.getString("FindAllStepsProgressDialog.progressLabel.text"));
        this.progressBar.setStringPainted(true);
        this.abbrechenButton
                .setMnemonic(ResourceBundle.getBundle("intl/FindAllStepsProgressDialog").getString("FindAllStepsProgressDialog.abbrechenButton.mnemonic").charAt(0));
        this.abbrechenButton.setText(bundle.getString("FindAllStepsProgressDialog.abbrechenButton.text"));
        this.abbrechenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                FindAllStepsProgressDialog.this.abbrechenButtonActionPerformed(evt);
            }
        });
        this.fishProgressBar.setStringPainted(true);
        GroupLayout layout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addGroup(
                                                layout.createParallelGroup(Alignment.LEADING)
                                                        .addGroup(layout.createSequentialGroup().addContainerGap().addComponent(this.progressLabel, -1, 238, 32767))
                                                        .addGroup(layout.createSequentialGroup().addGap(83, 83, 83).addComponent(this.abbrechenButton))
                                                        .addGroup(layout.createSequentialGroup().addContainerGap().addComponent(this.fishProgressBar, -2, 238, -2))
                                                        .addGroup(layout.createSequentialGroup().addContainerGap().addComponent(this.progressBar, -1, 238, 32767))
                                        )
                                        .addContainerGap()
                        )
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(this.progressLabel)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.fishProgressBar, -2, -1, -2)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.progressBar, -2, -1, -2)
                                        .addPreferredGap(ComponentPlacement.RELATED, -1, 32767)
                                        .addComponent(this.abbrechenButton)
                                        .addContainerGap()
                        )
        );
        this.pack();
    }

    private void formWindowClosing(WindowEvent evt) {
        this.abbrechenButtonActionPerformed(null);
    }

    private void abbrechenButtonActionPerformed(ActionEvent evt) {
        this.thread.interrupt();

        try {
            this.thread.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Interrupted while waiting for AllSteps-thread", ex);
        }

        this.setVisible(false);
    }

    private void formWindowOpened(WindowEvent evt) {
        this.thread.start();
    }

    public void updateProgress(final String label, final int step) {
        EventQueue.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        FindAllStepsProgressDialog.this.progressLabel
                                .setText(ResourceBundle.getBundle("intl/FindAllStepsProgressDialog").getString("FindAllStepsProgressDialog.searching") + " " + label);
                        FindAllStepsProgressDialog.this.progressBar.setValue(step);
                    }
                }
        );
    }

    public void resetFishProgressBar(final int maxValue) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                FindAllStepsProgressDialog.this.fishProgressBar.setMaximum(maxValue);
                FindAllStepsProgressDialog.this.fishProgressBar.setValue(0);
            }
        });
    }

    public void updateFishProgressBar(final int actValue) {
        if (System.currentTimeMillis() - this.ticks > 1000L) {
            this.ticks = System.currentTimeMillis();
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    FindAllStepsProgressDialog.this.fishProgressBar.setValue(actValue);
                }
            });
        }
    }

    public List<SolutionStep> getSteps() {
        return this.steps;
    }

    public int getMaxSteps() {
        return 27;
    }
}
