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

public class FishChooseCandidatesDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private JCheckBox[] checkBoxes;
    private String fishCandidates;
    private JButton cancelButton;
    private JCheckBox cand1CheckBox;
    private JCheckBox cand2CheckBox;
    private JCheckBox cand3CheckBox;
    private JCheckBox cand4CheckBox;
    private JCheckBox cand5CheckBox;
    private JCheckBox cand6CheckBox;
    private JCheckBox cand7CheckBox;
    private JCheckBox cand8CheckBox;
    private JCheckBox cand9CheckBox;
    private JButton clearAllButton;
    private JPanel jPanel1;
    private JButton okButton;
    private JButton setAllButton;

    public FishChooseCandidatesDialog(Frame parent, String fishCandidates) {
        super(parent, true);
        this.initComponents();
        this.fishCandidates = fishCandidates;
        this.checkBoxes = new JCheckBox[]{
                this.cand1CheckBox,
                this.cand2CheckBox,
                this.cand3CheckBox,
                this.cand4CheckBox,
                this.cand5CheckBox,
                this.cand6CheckBox,
                this.cand7CheckBox,
                this.cand8CheckBox,
                this.cand9CheckBox
        };
        this.setCheckBoxes(fishCandidates);
        this.getRootPane().setDefaultButton(this.okButton);
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(27, 0, false);
        Action escapeAction = new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                FishChooseCandidatesDialog.this.setVisible(false);
            }
        };
        this.getRootPane().getInputMap(2).put(escapeKeyStroke, "ESCAPE");
        this.getRootPane().getActionMap().put("ESCAPE", escapeAction);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                FishChooseCandidatesDialog dialog = new FishChooseCandidatesDialog(new JFrame(), "101010101");
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
        this.jPanel1 = new JPanel();
        this.cand1CheckBox = new JCheckBox();
        this.cand2CheckBox = new JCheckBox();
        this.cand3CheckBox = new JCheckBox();
        this.cand4CheckBox = new JCheckBox();
        this.cand5CheckBox = new JCheckBox();
        this.cand6CheckBox = new JCheckBox();
        this.cand7CheckBox = new JCheckBox();
        this.cand8CheckBox = new JCheckBox();
        this.cand9CheckBox = new JCheckBox();
        this.okButton = new JButton();
        this.cancelButton = new JButton();
        this.clearAllButton = new JButton();
        this.setAllButton = new JButton();
        this.setDefaultCloseOperation(2);
        ResourceBundle bundle = ResourceBundle.getBundle("intl/FishChooseCandidatesDialog");
        this.setTitle(bundle.getString("FishChooseCandidatesDialog.title"));
        this.jPanel1.setBorder(BorderFactory.createTitledBorder(bundle.getString("FishChooseCandidatesDialog.jPanel1.border.title")));
        this.cand1CheckBox.setMnemonic(ResourceBundle.getBundle("intl/FishChooseCandidatesDialog").getString("cand1CheckBox.mnemonic").charAt(0));
        this.cand1CheckBox.setText(bundle.getString("FishChooseCandidatesDialog.cand1CheckBox.text"));
        this.cand2CheckBox.setMnemonic(ResourceBundle.getBundle("intl/FishChooseCandidatesDialog").getString("cand2CheckBox.mnemonic").charAt(0));
        this.cand2CheckBox.setText(bundle.getString("FishChooseCandidatesDialog.cand2CheckBox.text"));
        this.cand3CheckBox.setMnemonic(ResourceBundle.getBundle("intl/FishChooseCandidatesDialog").getString("cand3CheckBox.mnemonic").charAt(0));
        this.cand3CheckBox.setText(bundle.getString("FishChooseCandidatesDialog.cand3CheckBox.text"));
        this.cand4CheckBox.setMnemonic(ResourceBundle.getBundle("intl/FishChooseCandidatesDialog").getString("cand4CheckBox.mnemonic").charAt(0));
        this.cand4CheckBox.setText(bundle.getString("FishChooseCandidatesDialog.cand4CheckBox.text"));
        this.cand5CheckBox.setMnemonic(ResourceBundle.getBundle("intl/FishChooseCandidatesDialog").getString("cand5CheckBox.mnemonic").charAt(0));
        this.cand5CheckBox.setText(bundle.getString("FishChooseCandidatesDialog.cand5CheckBox.text"));
        this.cand6CheckBox.setMnemonic(ResourceBundle.getBundle("intl/FishChooseCandidatesDialog").getString("cand6CheckBox.mnemonic").charAt(0));
        this.cand6CheckBox.setText(bundle.getString("FishChooseCandidatesDialog.cand6CheckBox.text"));
        this.cand7CheckBox.setMnemonic(ResourceBundle.getBundle("intl/FishChooseCandidatesDialog").getString("cand7CheckBox.mnemonic").charAt(0));
        this.cand7CheckBox.setText(bundle.getString("FishChooseCandidatesDialog.cand7CheckBox.text"));
        this.cand8CheckBox.setMnemonic(ResourceBundle.getBundle("intl/FishChooseCandidatesDialog").getString("cand8CheckBox.mnemonic").charAt(0));
        this.cand8CheckBox.setText(bundle.getString("FishChooseCandidatesDialog.cand8CheckBox.text"));
        this.cand9CheckBox.setMnemonic(ResourceBundle.getBundle("intl/FishChooseCandidatesDialog").getString("cand9CheckBox.mnemonic").charAt(0));
        this.cand9CheckBox.setText(bundle.getString("FishChooseCandidatesDialog.cand9CheckBox.text"));
        GroupLayout jPanel1Layout = new GroupLayout(this.jPanel1);
        this.jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                jPanel1Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                jPanel1Layout.createParallelGroup(Alignment.LEADING)
                                                        .addGroup(
                                                                jPanel1Layout.createSequentialGroup()
                                                                        .addComponent(this.cand1CheckBox)
                                                                        .addPreferredGap(ComponentPlacement.UNRELATED)
                                                                        .addComponent(this.cand2CheckBox)
                                                                        .addPreferredGap(ComponentPlacement.UNRELATED)
                                                                        .addComponent(this.cand3CheckBox)
                                                        )
                                                        .addGroup(
                                                                jPanel1Layout.createSequentialGroup()
                                                                        .addComponent(this.cand4CheckBox)
                                                                        .addPreferredGap(ComponentPlacement.UNRELATED)
                                                                        .addComponent(this.cand5CheckBox)
                                                                        .addPreferredGap(ComponentPlacement.UNRELATED)
                                                                        .addComponent(this.cand6CheckBox)
                                                        )
                                                        .addGroup(
                                                                jPanel1Layout.createSequentialGroup()
                                                                        .addComponent(this.cand7CheckBox)
                                                                        .addPreferredGap(ComponentPlacement.UNRELATED)
                                                                        .addComponent(this.cand8CheckBox)
                                                                        .addPreferredGap(ComponentPlacement.UNRELATED)
                                                                        .addComponent(this.cand9CheckBox)
                                                        )
                                        )
                                        .addContainerGap(-1, 32767)
                        )
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                jPanel1Layout.createSequentialGroup()
                                        .addGroup(
                                                jPanel1Layout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(this.cand1CheckBox)
                                                        .addComponent(this.cand2CheckBox)
                                                        .addComponent(this.cand3CheckBox)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                jPanel1Layout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(this.cand4CheckBox)
                                                        .addComponent(this.cand5CheckBox)
                                                        .addComponent(this.cand6CheckBox)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                jPanel1Layout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(this.cand7CheckBox)
                                                        .addComponent(this.cand8CheckBox)
                                                        .addComponent(this.cand9CheckBox)
                                        )
                                        .addContainerGap(-1, 32767)
                        )
        );
        this.okButton.setMnemonic(ResourceBundle.getBundle("intl/FishChooseCandidatesDialog").getString("okButton.mnemonic").charAt(0));
        this.okButton.setText(bundle.getString("FishChooseCandidatesDialog.okButton.text"));
        this.okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                FishChooseCandidatesDialog.this.okButtonActionPerformed(evt);
            }
        });
        this.cancelButton.setMnemonic(ResourceBundle.getBundle("intl/FishChooseCandidatesDialog").getString("cancelButton.mnemonic").charAt(0));
        this.cancelButton.setText(bundle.getString("FishChooseCandidatesDialog.cancelButton.text"));
        this.cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                FishChooseCandidatesDialog.this.cancelButtonActionPerformed(evt);
            }
        });
        this.clearAllButton.setMnemonic(ResourceBundle.getBundle("intl/FishChooseCandidatesDialog").getString("clearAllButton.mnemonic").charAt(0));
        this.clearAllButton.setText(bundle.getString("FishChooseCandidatesDialog.clearAllButton.text"));
        this.clearAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                FishChooseCandidatesDialog.this.clearAllButtonActionPerformed(evt);
            }
        });
        this.setAllButton.setMnemonic(ResourceBundle.getBundle("intl/FishChooseCandidatesDialog").getString("setAllButton.mnemonic").charAt(0));
        this.setAllButton.setText(bundle.getString("FishChooseCandidatesDialog.setAllButton.text"));
        this.setAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                FishChooseCandidatesDialog.this.setAllButtonActionPerformed(evt);
            }
        });
        GroupLayout layout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addContainerGap(76, 32767)
                                        .addComponent(this.okButton)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.cancelButton)
                                        .addContainerGap()
                        )
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(this.jPanel1, -2, -1, -2)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(Alignment.LEADING).addComponent(this.clearAllButton).addComponent(this.setAllButton))
                                        .addContainerGap(-1, 32767)
                        )
        );
        layout.linkSize(0, this.cancelButton, this.okButton);
        layout.linkSize(0, this.clearAllButton, this.setAllButton);
        layout.setVerticalGroup(
                layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                Alignment.TRAILING,
                                layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                layout.createParallelGroup(Alignment.TRAILING)
                                                        .addComponent(this.jPanel1, -2, -1, -2)
                                                        .addGroup(
                                                                Alignment.LEADING,
                                                                layout.createSequentialGroup()
                                                                        .addGap(5, 5, 5)
                                                                        .addComponent(this.setAllButton)
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addComponent(this.clearAllButton)
                                                        )
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED, -1, 32767)
                                        .addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(this.okButton).addComponent(this.cancelButton))
                                        .addContainerGap()
                        )
        );
        this.pack();
    }

    private void cancelButtonActionPerformed(ActionEvent evt) {
        this.setVisible(false);
    }

    private void okButtonActionPerformed(ActionEvent evt) {
        String result = "";

        for (int i = 0; i < this.checkBoxes.length; i++) {
            if (this.checkBoxes[i].isSelected()) {
                result = result + "1";
            } else {
                result = result + "0";
            }
        }

        this.fishCandidates = result;
        this.setVisible(false);
    }

    private void setAllButtonActionPerformed(ActionEvent evt) {
        this.setCheckBoxes("111111111");
    }

    private void clearAllButtonActionPerformed(ActionEvent evt) {
        this.setCheckBoxes("000000000");
    }

    private void setCheckBoxes(String values) {
        for (int i = 0; i < this.checkBoxes.length; i++) {
            boolean check = false;
            if (values.length() > i && values.charAt(i) == '1') {
                check = true;
            }

            this.checkBoxes[i].setSelected(check);
        }
    }

    public String getFishCandidates() {
        return this.fishCandidates;
    }
}
