package sudoku;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager.LookAndFeelInfo;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterJob;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExtendedPrintDialog extends JDialog {
    private static final Icon[] images = new ImageIcon[]{
            new ImageIcon(Toolkit.getDefaultToolkit().getImage(ExtendedPrintDialog.class.getResource("/img/slh1th.png"))),
            new ImageIcon(Toolkit.getDefaultToolkit().getImage(ExtendedPrintDialog.class.getResource("/img/slh2th.png"))),
            new ImageIcon(Toolkit.getDefaultToolkit().getImage(ExtendedPrintDialog.class.getResource("/img/slh4th.png"))),
            new ImageIcon(Toolkit.getDefaultToolkit().getImage(ExtendedPrintDialog.class.getResource("/img/slq2th.png"))),
            new ImageIcon(Toolkit.getDefaultToolkit().getImage(ExtendedPrintDialog.class.getResource("/img/slq4th.png")))
    };
    private static final Integer[] layouts = new Integer[]{0, 1, 2, 3, 4};
    private static final String[] modeNames = new String[]{
            ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.playingMenuItem.text"),
            ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.learningMenuItem.text"),
            ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.practisingMenuItem.text")
    };
    private static final long serialVersionUID = 1L;
    private JTextField[] numberTextFields;
    private JComboBox[] levelComboBoxes;
    private JComboBox[] modeComboBoxes;
    private JCheckBox[] candCheckBoxes;
    private PageFormat pageFormat = null;
    private PrinterJob job = null;
    private int totalNumberOfPuzzles = 0;
    private JButton cancelButton;
    private JPanel jPanel1;
    private JScrollPane jScrollPane1;
    private JScrollPane jScrollPane2;
    private JList layoutList;
    private JComboBox level1ComboBox;
    private JLabel level1Label;
    private JComboBox level2ComboBox;
    private JLabel level2Label;
    private JComboBox level3ComboBox;
    private JLabel level3Label;
    private JComboBox level4ComboBox;
    private JLabel level4Label;
    private JComboBox level5ComboBox;
    private JLabel level5Label;
    private JCheckBox manualDuplexCheckBox;
    private JComboBox mode1ComboBox;
    private JLabel mode1Label;
    private JComboBox mode2ComboBox;
    private JLabel mode2Label;
    private JComboBox mode3ComboBox;
    private JLabel mode3Label;
    private JComboBox mode4ComboBox;
    private JLabel mode4Label;
    private JComboBox mode5ComboBox;
    private JLabel mode5Label;
    private JLabel numberOfPuzzles1Label;
    private JTextField numberOfPuzzles1TextField;
    private JLabel numberOfPuzzles2Label;
    private JTextField numberOfPuzzles2TextField;
    private JLabel numberOfPuzzles3Label;
    private JTextField numberOfPuzzles3TextField;
    private JLabel numberOfPuzzles4Label;
    private JTextField numberOfPuzzles4TextField;
    private JLabel numberOfPuzzles5Label;
    private JTextField numberOfPuzzles5TextField;
    private JButton pageSetupButton;
    private JCheckBox printAllBlackCheckBox;
    private JCheckBox printBookletCheckBox;
    private JButton printButton;
    private JCheckBox printCands1CheckBox;
    private JCheckBox printCands2CheckBox;
    private JCheckBox printCands3CheckBox;
    private JCheckBox printCands4CheckBox;
    private JCheckBox printCands5CheckBox;
    private JCheckBox printRatingCheckBox;
    private JPanel section1Panel;
    private JPanel section2Panel;
    private JPanel section3Panel;
    private JPanel section4Panel;
    private JPanel section5Panel;
    private JPanel sectionContainerPanel;

    public ExtendedPrintDialog(Frame parent, boolean modal) {
        super(parent, modal);
        this.initComponents();
        this.getRootPane().setDefaultButton(this.printButton);
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(27, 0, false);
        Action escapeAction = new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                ExtendedPrintDialog.this.setVisible(false);
            }
        };
        this.getRootPane().getInputMap(2).put(escapeKeyStroke, "ESCAPE");
        this.getRootPane().getActionMap().put("ESCAPE", escapeAction);
        this.layoutList.setVisibleRowCount(1);
        this.layoutList.setCellRenderer(new ExtendedPrintDialog.MyCellRenderer());
        this.layoutList.setListData(layouts);
        this.layoutList.setSelectedIndex(0);
        this.numberTextFields = new JTextField[]{
                this.numberOfPuzzles1TextField,
                this.numberOfPuzzles2TextField,
                this.numberOfPuzzles3TextField,
                this.numberOfPuzzles4TextField,
                this.numberOfPuzzles5TextField
        };
        this.levelComboBoxes = new JComboBox[]{this.level1ComboBox, this.level2ComboBox, this.level3ComboBox, this.level4ComboBox, this.level5ComboBox};
        this.modeComboBoxes = new JComboBox[]{this.mode1ComboBox, this.mode2ComboBox, this.mode3ComboBox, this.mode4ComboBox, this.mode5ComboBox};
        this.candCheckBoxes = new JCheckBox[]{
                this.printCands1CheckBox, this.printCands2CheckBox, this.printCands3CheckBox, this.printCands4CheckBox, this.printCands5CheckBox
        };

        for (int i = 0; i < this.numberTextFields.length; i++) {
            this.numberTextFields[i].setDocument(new NumbersOnlyDocument());
        }

        for (int i = 1; i < DifficultyType.values().length; i++) {
            for (int j = 0; j < this.levelComboBoxes.length; j++) {
                this.levelComboBoxes[j].addItem(Options.getInstance().getDifficultyLevels()[i].getName());
            }
        }

        for (int i = 0; i < modeNames.length; i++) {
            if (modeNames[i].endsWith("...")) {
                modeNames[i] = modeNames[i].substring(0, modeNames[i].length() - 3);
            }

            for (int j = 0; j < this.modeComboBoxes.length; j++) {
                this.modeComboBoxes[j].addItem(modeNames[i]);
            }
        }

        this.manualDuplexCheckBox.setEnabled(this.printBookletCheckBox.isSelected());
        this.job = SudokuUtil.getPrinterJob();
        this.pageFormat = SudokuUtil.getPageFormat();
        Paper paper = this.pageFormat.getPaper();
        double ind = 28.3464567;
        paper.setImageableArea(ind, ind, paper.getWidth() - 2.0 * ind, paper.getHeight() - 2.0 * ind);
        this.pageFormat.setPaper(paper);
    }

    public static void main(String[] args) {
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ExtendedPrintDialog.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(ExtendedPrintDialog.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ExtendedPrintDialog.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(ExtendedPrintDialog.class.getName()).log(Level.SEVERE, null, ex);
        }

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                ExtendedPrintDialog dialog = new ExtendedPrintDialog(new JFrame(), true);
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
        this.jScrollPane1 = new JScrollPane();
        this.layoutList = new JList();
        this.printRatingCheckBox = new JCheckBox();
        this.printAllBlackCheckBox = new JCheckBox();
        this.printBookletCheckBox = new JCheckBox();
        this.manualDuplexCheckBox = new JCheckBox();
        this.printButton = new JButton();
        this.pageSetupButton = new JButton();
        this.cancelButton = new JButton();
        this.jScrollPane2 = new JScrollPane();
        this.sectionContainerPanel = new JPanel();
        this.section1Panel = new JPanel();
        this.numberOfPuzzles1Label = new JLabel();
        this.numberOfPuzzles1TextField = new JTextField();
        this.level1Label = new JLabel();
        this.level1ComboBox = new JComboBox();
        this.mode1Label = new JLabel();
        this.mode1ComboBox = new JComboBox();
        this.printCands1CheckBox = new JCheckBox();
        this.section2Panel = new JPanel();
        this.numberOfPuzzles2Label = new JLabel();
        this.numberOfPuzzles2TextField = new JTextField();
        this.level2Label = new JLabel();
        this.level2ComboBox = new JComboBox();
        this.mode2Label = new JLabel();
        this.mode2ComboBox = new JComboBox();
        this.printCands2CheckBox = new JCheckBox();
        this.section3Panel = new JPanel();
        this.numberOfPuzzles3Label = new JLabel();
        this.numberOfPuzzles3TextField = new JTextField();
        this.level3Label = new JLabel();
        this.level3ComboBox = new JComboBox();
        this.mode3Label = new JLabel();
        this.mode3ComboBox = new JComboBox();
        this.printCands3CheckBox = new JCheckBox();
        this.section4Panel = new JPanel();
        this.numberOfPuzzles4Label = new JLabel();
        this.numberOfPuzzles4TextField = new JTextField();
        this.level4Label = new JLabel();
        this.level4ComboBox = new JComboBox();
        this.mode4Label = new JLabel();
        this.mode4ComboBox = new JComboBox();
        this.printCands4CheckBox = new JCheckBox();
        this.section5Panel = new JPanel();
        this.numberOfPuzzles5Label = new JLabel();
        this.numberOfPuzzles5TextField = new JTextField();
        this.level5Label = new JLabel();
        this.level5ComboBox = new JComboBox();
        this.mode5Label = new JLabel();
        this.mode5ComboBox = new JComboBox();
        this.printCands5CheckBox = new JCheckBox();
        this.setDefaultCloseOperation(2);
        ResourceBundle bundle = ResourceBundle.getBundle("intl/ExtendedPrintDialog");
        this.setTitle(bundle.getString("ExtendedPrintDialog.title"));
        this.jPanel1.setBorder(BorderFactory.createTitledBorder(bundle.getString("ExtendedPrintDialog.jPanel1.border.title")));
        this.jScrollPane1.setHorizontalScrollBarPolicy(32);
        this.jScrollPane1.setVerticalScrollBarPolicy(21);
        this.layoutList.setSelectionMode(0);
        this.layoutList.setLayoutOrientation(2);
        this.jScrollPane1.setViewportView(this.layoutList);
        this.printRatingCheckBox
                .setMnemonic(ResourceBundle.getBundle("intl/ExtendedPrintDialog").getString("ExtendedPrintDialog.printRatingCheckBox.mnemonic").charAt(0));
        this.printRatingCheckBox.setSelected(true);
        this.printRatingCheckBox.setText(bundle.getString("ExtendedPrintDialog.printRatingCheckBox.text"));
        this.printAllBlackCheckBox
                .setMnemonic(ResourceBundle.getBundle("intl/ExtendedPrintDialog").getString("ExtendedPrintDialog.printAllBlackCheckBox.mnemonic").charAt(0));
        this.printAllBlackCheckBox.setText(bundle.getString("ExtendedPrintDialog.printAllBlackCheckBox.text"));
        this.printBookletCheckBox
                .setMnemonic(ResourceBundle.getBundle("intl/ExtendedPrintDialog").getString("ExtendedPrintDialog.printBookletCheckBox.mnemonic").charAt(0));
        this.printBookletCheckBox.setText(bundle.getString("ExtendedPrintDialog.printBookletCheckBox.text"));
        this.printBookletCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ExtendedPrintDialog.this.printBookletCheckBoxActionPerformed(evt);
            }
        });
        this.manualDuplexCheckBox
                .setMnemonic(ResourceBundle.getBundle("intl/ExtendedPrintDialog").getString("ExtendedPrintDialog.manualDuplexCheckBox.mnemonic").charAt(0));
        this.manualDuplexCheckBox.setText(bundle.getString("ExtendedPrintDialog.manualDuplexCheckBox.text"));
        this.manualDuplexCheckBox.setEnabled(false);
        GroupLayout jPanel1Layout = new GroupLayout(this.jPanel1);
        this.jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                jPanel1Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                jPanel1Layout.createParallelGroup(Alignment.LEADING).addComponent(this.printRatingCheckBox).addComponent(this.printAllBlackCheckBox)
                                        )
                                        .addGap(18, 18, 18)
                                        .addGroup(
                                                jPanel1Layout.createParallelGroup(Alignment.LEADING).addComponent(this.manualDuplexCheckBox).addComponent(this.printBookletCheckBox)
                                        )
                                        .addContainerGap()
                        )
                        .addGroup(jPanel1Layout.createSequentialGroup().addGap(10, 10, 10).addComponent(this.jScrollPane1, -1, 464, 32767).addContainerGap(10, 32767))
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                jPanel1Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(this.jScrollPane1, -1, 148, 32767)
                                        .addPreferredGap(ComponentPlacement.UNRELATED)
                                        .addGroup(
                                                jPanel1Layout.createParallelGroup(Alignment.BASELINE).addComponent(this.printRatingCheckBox).addComponent(this.printBookletCheckBox)
                                        )
                                        .addPreferredGap(ComponentPlacement.UNRELATED)
                                        .addGroup(
                                                jPanel1Layout.createParallelGroup(Alignment.BASELINE).addComponent(this.printAllBlackCheckBox).addComponent(this.manualDuplexCheckBox)
                                        )
                                        .addContainerGap()
                        )
        );
        this.printButton.setMnemonic(ResourceBundle.getBundle("intl/ExtendedPrintDialog").getString("ExtendedPrintDialog.printButton.mnemonic").charAt(0));
        this.printButton.setText(bundle.getString("ExtendedPrintDialog.printButton.text"));
        this.printButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ExtendedPrintDialog.this.printButtonActionPerformed(evt);
            }
        });
        this.pageSetupButton
                .setMnemonic(ResourceBundle.getBundle("intl/ExtendedPrintDialog").getString("ExtendedPrintDialog.pageSetupButton.mnemonic").charAt(0));
        this.pageSetupButton.setText(bundle.getString("ExtendedPrintDialog.pageSetupButton.text"));
        this.pageSetupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ExtendedPrintDialog.this.pageSetupButtonActionPerformed(evt);
            }
        });
        this.cancelButton.setMnemonic(ResourceBundle.getBundle("intl/ExtendedPrintDialog").getString("ExtendedPrintDialog.cancelButton.mnemonic").charAt(0));
        this.cancelButton.setText(bundle.getString("ExtendedPrintDialog.cancelButton.text"));
        this.cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ExtendedPrintDialog.this.cancelButtonActionPerformed(evt);
            }
        });
        this.jScrollPane2.setVerticalScrollBarPolicy(22);
        this.section1Panel.setBorder(BorderFactory.createTitledBorder(bundle.getString("ExtendedPrintDialog.section1Panel.border.title")));
        this.numberOfPuzzles1Label.setLabelFor(this.numberOfPuzzles1TextField);
        this.numberOfPuzzles1Label.setText(bundle.getString("ExtendedPrintDialog.numberOfPuzzles1Label.text"));
        this.numberOfPuzzles1TextField.setText(bundle.getString("ExtendedPrintDialog.numberOfPuzzles1TextField.text"));
        this.level1Label.setLabelFor(this.level1ComboBox);
        this.level1Label.setText(bundle.getString("ExtendedPrintDialog.level1Label.text"));
        this.mode1Label.setLabelFor(this.mode1ComboBox);
        this.mode1Label.setText(bundle.getString("ExtendedPrintDialog.mode1Label.text"));
        this.printCands1CheckBox.setText(bundle.getString("ExtendedPrintDialog.printCands1CheckBox.text"));
        GroupLayout section1PanelLayout = new GroupLayout(this.section1Panel);
        this.section1Panel.setLayout(section1PanelLayout);
        section1PanelLayout.setHorizontalGroup(
                section1PanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                section1PanelLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                section1PanelLayout.createParallelGroup(Alignment.LEADING)
                                                        .addComponent(this.numberOfPuzzles1Label)
                                                        .addComponent(this.level1Label)
                                                        .addComponent(this.mode1Label)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                section1PanelLayout.createParallelGroup(Alignment.LEADING)
                                                        .addComponent(this.printCands1CheckBox)
                                                        .addComponent(this.numberOfPuzzles1TextField, -1, 349, 32767)
                                                        .addComponent(this.level1ComboBox, 0, 349, 32767)
                                                        .addComponent(this.mode1ComboBox, 0, 349, 32767)
                                        )
                                        .addContainerGap()
                        )
        );
        section1PanelLayout.setVerticalGroup(
                section1PanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                section1PanelLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                section1PanelLayout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(this.numberOfPuzzles1Label)
                                                        .addComponent(this.numberOfPuzzles1TextField, -2, -1, -2)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                section1PanelLayout.createParallelGroup(Alignment.BASELINE).addComponent(this.level1Label).addComponent(this.level1ComboBox, -2, -1, -2)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                section1PanelLayout.createParallelGroup(Alignment.BASELINE).addComponent(this.mode1Label).addComponent(this.mode1ComboBox, -2, -1, -2)
                                        )
                                        .addPreferredGap(ComponentPlacement.UNRELATED)
                                        .addComponent(this.printCands1CheckBox)
                                        .addContainerGap(-1, 32767)
                        )
        );
        this.section2Panel.setBorder(BorderFactory.createTitledBorder(bundle.getString("ExtendedPrintDialog.section2Panel.border.title")));
        this.numberOfPuzzles2Label.setLabelFor(this.numberOfPuzzles2TextField);
        this.numberOfPuzzles2Label.setText(bundle.getString("ExtendedPrintDialog.numberOfPuzzles1Label.text"));
        this.numberOfPuzzles2TextField.setText(bundle.getString("ExtendedPrintDialog.numberOfPuzzles1TextField.text"));
        this.level2Label.setLabelFor(this.level2ComboBox);
        this.level2Label.setText(bundle.getString("ExtendedPrintDialog.level1Label.text"));
        this.mode2Label.setLabelFor(this.mode2ComboBox);
        this.mode2Label.setText(bundle.getString("ExtendedPrintDialog.mode1Label.text"));
        this.printCands2CheckBox.setText(bundle.getString("ExtendedPrintDialog.printCands1CheckBox.text"));
        GroupLayout section2PanelLayout = new GroupLayout(this.section2Panel);
        this.section2Panel.setLayout(section2PanelLayout);
        section2PanelLayout.setHorizontalGroup(
                section2PanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                section2PanelLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                section2PanelLayout.createParallelGroup(Alignment.LEADING)
                                                        .addComponent(this.numberOfPuzzles2Label)
                                                        .addComponent(this.level2Label)
                                                        .addComponent(this.mode2Label)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                section2PanelLayout.createParallelGroup(Alignment.LEADING)
                                                        .addComponent(this.printCands2CheckBox)
                                                        .addComponent(this.numberOfPuzzles2TextField, -1, 349, 32767)
                                                        .addComponent(this.level2ComboBox, 0, 349, 32767)
                                                        .addComponent(this.mode2ComboBox, 0, 349, 32767)
                                        )
                                        .addContainerGap()
                        )
        );
        section2PanelLayout.setVerticalGroup(
                section2PanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                section2PanelLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                section2PanelLayout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(this.numberOfPuzzles2Label)
                                                        .addComponent(this.numberOfPuzzles2TextField, -2, -1, -2)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                section2PanelLayout.createParallelGroup(Alignment.BASELINE).addComponent(this.level2Label).addComponent(this.level2ComboBox, -2, -1, -2)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                section2PanelLayout.createParallelGroup(Alignment.BASELINE).addComponent(this.mode2Label).addComponent(this.mode2ComboBox, -2, -1, -2)
                                        )
                                        .addPreferredGap(ComponentPlacement.UNRELATED)
                                        .addComponent(this.printCands2CheckBox)
                                        .addContainerGap(-1, 32767)
                        )
        );
        this.section3Panel.setBorder(BorderFactory.createTitledBorder(bundle.getString("ExtendedPrintDialog.section3Panel.border.title")));
        this.numberOfPuzzles3Label.setLabelFor(this.numberOfPuzzles3TextField);
        this.numberOfPuzzles3Label.setText(bundle.getString("ExtendedPrintDialog.numberOfPuzzles1Label.text"));
        this.numberOfPuzzles3TextField.setText(bundle.getString("ExtendedPrintDialog.numberOfPuzzles1TextField.text"));
        this.level3Label.setLabelFor(this.level3Label);
        this.level3Label.setText(bundle.getString("ExtendedPrintDialog.level1Label.text"));
        this.mode3Label.setLabelFor(this.mode3ComboBox);
        this.mode3Label.setText(bundle.getString("ExtendedPrintDialog.mode1Label.text"));
        this.printCands3CheckBox.setText(bundle.getString("ExtendedPrintDialog.printCands1CheckBox.text"));
        GroupLayout section3PanelLayout = new GroupLayout(this.section3Panel);
        this.section3Panel.setLayout(section3PanelLayout);
        section3PanelLayout.setHorizontalGroup(
                section3PanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                section3PanelLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                section3PanelLayout.createParallelGroup(Alignment.LEADING)
                                                        .addComponent(this.numberOfPuzzles3Label)
                                                        .addComponent(this.level3Label)
                                                        .addComponent(this.mode3Label)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                section3PanelLayout.createParallelGroup(Alignment.LEADING)
                                                        .addComponent(this.printCands3CheckBox)
                                                        .addComponent(this.numberOfPuzzles3TextField, -1, 349, 32767)
                                                        .addComponent(this.level3ComboBox, 0, 349, 32767)
                                                        .addComponent(this.mode3ComboBox, 0, 349, 32767)
                                        )
                                        .addContainerGap()
                        )
        );
        section3PanelLayout.setVerticalGroup(
                section3PanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                section3PanelLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                section3PanelLayout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(this.numberOfPuzzles3Label)
                                                        .addComponent(this.numberOfPuzzles3TextField, -2, -1, -2)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                section3PanelLayout.createParallelGroup(Alignment.BASELINE).addComponent(this.level3Label).addComponent(this.level3ComboBox, -2, -1, -2)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                section3PanelLayout.createParallelGroup(Alignment.BASELINE).addComponent(this.mode3Label).addComponent(this.mode3ComboBox, -2, -1, -2)
                                        )
                                        .addPreferredGap(ComponentPlacement.UNRELATED)
                                        .addComponent(this.printCands3CheckBox)
                                        .addContainerGap(-1, 32767)
                        )
        );
        this.section4Panel.setBorder(BorderFactory.createTitledBorder(bundle.getString("ExtendedPrintDialog.section4Panel.border.title")));
        this.numberOfPuzzles4Label.setLabelFor(this.numberOfPuzzles4TextField);
        this.numberOfPuzzles4Label.setText(bundle.getString("ExtendedPrintDialog.numberOfPuzzles1Label.text"));
        this.numberOfPuzzles4TextField.setText(bundle.getString("ExtendedPrintDialog.numberOfPuzzles1TextField.text"));
        this.level4Label.setLabelFor(this.level4ComboBox);
        this.level4Label.setText(bundle.getString("ExtendedPrintDialog.level1Label.text"));
        this.mode4Label.setLabelFor(this.mode4ComboBox);
        this.mode4Label.setText(bundle.getString("ExtendedPrintDialog.mode1Label.text"));
        this.printCands4CheckBox.setText(bundle.getString("ExtendedPrintDialog.printCands1CheckBox.text"));
        GroupLayout section4PanelLayout = new GroupLayout(this.section4Panel);
        this.section4Panel.setLayout(section4PanelLayout);
        section4PanelLayout.setHorizontalGroup(
                section4PanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                section4PanelLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                section4PanelLayout.createParallelGroup(Alignment.LEADING)
                                                        .addComponent(this.numberOfPuzzles4Label)
                                                        .addComponent(this.level4Label)
                                                        .addComponent(this.mode4Label)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                section4PanelLayout.createParallelGroup(Alignment.LEADING)
                                                        .addComponent(this.printCands4CheckBox)
                                                        .addComponent(this.numberOfPuzzles4TextField, -1, 349, 32767)
                                                        .addComponent(this.level4ComboBox, 0, 349, 32767)
                                                        .addComponent(this.mode4ComboBox, 0, 349, 32767)
                                        )
                                        .addContainerGap()
                        )
        );
        section4PanelLayout.setVerticalGroup(
                section4PanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                section4PanelLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                section4PanelLayout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(this.numberOfPuzzles4Label)
                                                        .addComponent(this.numberOfPuzzles4TextField, -2, -1, -2)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                section4PanelLayout.createParallelGroup(Alignment.BASELINE).addComponent(this.level4Label).addComponent(this.level4ComboBox, -2, -1, -2)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                section4PanelLayout.createParallelGroup(Alignment.BASELINE).addComponent(this.mode4Label).addComponent(this.mode4ComboBox, -2, -1, -2)
                                        )
                                        .addPreferredGap(ComponentPlacement.UNRELATED)
                                        .addComponent(this.printCands4CheckBox)
                                        .addContainerGap(-1, 32767)
                        )
        );
        this.section5Panel.setBorder(BorderFactory.createTitledBorder(bundle.getString("ExtendedPrintDialog.section5Panel.border.title")));
        this.numberOfPuzzles5Label.setLabelFor(this.numberOfPuzzles5TextField);
        this.numberOfPuzzles5Label.setText(bundle.getString("ExtendedPrintDialog.numberOfPuzzles1Label.text"));
        this.numberOfPuzzles5TextField.setText(bundle.getString("ExtendedPrintDialog.numberOfPuzzles1TextField.text"));
        this.level5Label.setLabelFor(this.level5ComboBox);
        this.level5Label.setText(bundle.getString("ExtendedPrintDialog.level1Label.text"));
        this.mode5Label.setLabelFor(this.mode5ComboBox);
        this.mode5Label.setText(bundle.getString("ExtendedPrintDialog.mode1Label.text"));
        this.printCands5CheckBox.setText(bundle.getString("ExtendedPrintDialog.printCands1CheckBox.text"));
        GroupLayout section5PanelLayout = new GroupLayout(this.section5Panel);
        this.section5Panel.setLayout(section5PanelLayout);
        section5PanelLayout.setHorizontalGroup(
                section5PanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                section5PanelLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                section5PanelLayout.createParallelGroup(Alignment.LEADING)
                                                        .addComponent(this.numberOfPuzzles5Label)
                                                        .addComponent(this.level5Label)
                                                        .addComponent(this.mode5Label)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                section5PanelLayout.createParallelGroup(Alignment.LEADING)
                                                        .addComponent(this.printCands5CheckBox)
                                                        .addComponent(this.numberOfPuzzles5TextField, -1, 349, 32767)
                                                        .addComponent(this.level5ComboBox, 0, 349, 32767)
                                                        .addComponent(this.mode5ComboBox, 0, 349, 32767)
                                        )
                                        .addContainerGap()
                        )
        );
        section5PanelLayout.setVerticalGroup(
                section5PanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                section5PanelLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                section5PanelLayout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(this.numberOfPuzzles5Label)
                                                        .addComponent(this.numberOfPuzzles5TextField, -2, -1, -2)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                section5PanelLayout.createParallelGroup(Alignment.BASELINE).addComponent(this.level5Label).addComponent(this.level5ComboBox, -2, -1, -2)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                section5PanelLayout.createParallelGroup(Alignment.BASELINE).addComponent(this.mode5Label).addComponent(this.mode5ComboBox, -2, -1, -2)
                                        )
                                        .addPreferredGap(ComponentPlacement.UNRELATED)
                                        .addComponent(this.printCands5CheckBox)
                                        .addContainerGap(-1, 32767)
                        )
        );
        GroupLayout sectionContainerPanelLayout = new GroupLayout(this.sectionContainerPanel);
        this.sectionContainerPanel.setLayout(sectionContainerPanelLayout);
        sectionContainerPanelLayout.setHorizontalGroup(
                sectionContainerPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addComponent(this.section1Panel, -1, -1, 32767)
                        .addComponent(this.section2Panel, -1, -1, 32767)
                        .addComponent(this.section3Panel, -1, -1, 32767)
                        .addComponent(this.section4Panel, -1, -1, 32767)
                        .addComponent(this.section5Panel, -1, -1, 32767)
        );
        sectionContainerPanelLayout.setVerticalGroup(
                sectionContainerPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                sectionContainerPanelLayout.createSequentialGroup()
                                        .addComponent(this.section1Panel, -2, -1, -2)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.section2Panel, -2, -1, -2)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.section3Panel, -2, -1, -2)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.section4Panel, -2, -1, -2)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.section5Panel, -2, -1, -2)
                        )
        );
        this.jScrollPane2.setViewportView(this.sectionContainerPanel);
        GroupLayout layout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                Alignment.TRAILING,
                                layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                layout.createParallelGroup(Alignment.TRAILING)
                                                        .addComponent(this.jScrollPane2, Alignment.LEADING, -1, 496, 32767)
                                                        .addComponent(this.jPanel1, Alignment.LEADING, -1, -1, 32767)
                                                        .addGroup(
                                                                layout.createSequentialGroup()
                                                                        .addComponent(this.printButton)
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addComponent(this.pageSetupButton)
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addComponent(this.cancelButton)
                                                        )
                                        )
                                        .addContainerGap()
                        )
        );
        layout.linkSize(0, this.cancelButton, this.pageSetupButton, this.printButton);
        layout.setVerticalGroup(
                layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(this.jPanel1, -2, -1, -2)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.jScrollPane2, -1, 222, 32767)
                                        .addPreferredGap(ComponentPlacement.UNRELATED)
                                        .addGroup(
                                                layout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(this.printButton)
                                                        .addComponent(this.pageSetupButton)
                                                        .addComponent(this.cancelButton)
                                        )
                                        .addContainerGap()
                        )
        );
        this.pack();
    }

    private void cancelButtonActionPerformed(ActionEvent evt) {
        this.setVisible(false);
    }

    private void pageSetupButtonActionPerformed(ActionEvent evt) {
        this.adjustOrientation();
        this.pageFormat = this.job.pageDialog(this.pageFormat);
    }

    private void printButtonActionPerformed(ActionEvent evt) {
        this.totalNumberOfPuzzles = 0;

        for (int i = 0; i < this.numberTextFields.length; i++) {
            this.totalNumberOfPuzzles = this.totalNumberOfPuzzles + this.getNumberOfPuzzes(i);
        }

        if (this.totalNumberOfPuzzles == 0) {
            JOptionPane.showMessageDialog(
                    this,
                    ResourceBundle.getBundle("intl/ExtendedPrintDialog").getString("ExtendedPrintDialog.noPuzzles"),
                    ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.error"),
                    0
            );
        } else {
            int layout = this.layoutList.getSelectedIndex();
            boolean printRating = this.printRatingCheckBox.isSelected();
            boolean allBlack = this.printAllBlackCheckBox.isSelected();
            boolean printBooklet = this.printBookletCheckBox.isSelected();
            boolean manualDuplex = this.manualDuplexCheckBox.isSelected();
            if (printBooklet && layout < 3) {
                JOptionPane.showMessageDialog(
                        this,
                        ResourceBundle.getBundle("intl/ExtendedPrintDialog").getString("ExtendedPrintDialog.wrongOrientation"),
                        ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.error"),
                        0
                );
            } else {
                this.job = SudokuUtil.getPrinterJob();
                this.pageFormat = SudokuUtil.getPageFormat();
                this.adjustOrientation();
                ExtendedPrintProgressDialog dlg = new ExtendedPrintProgressDialog(
                        null,
                        true,
                        this.numberTextFields,
                        this.levelComboBoxes,
                        this.modeComboBoxes,
                        this.candCheckBoxes,
                        layout,
                        printRating,
                        allBlack,
                        printBooklet,
                        manualDuplex
                );
                this.job.setPrintable(dlg, this.pageFormat);
                if (this.job.printDialog()) {
                    dlg.setJob(this.job);
                    dlg.setVisible(true);
                    this.setVisible(false);
                }
            }
        }
    }

    private void printBookletCheckBoxActionPerformed(ActionEvent evt) {
        this.manualDuplexCheckBox.setEnabled(this.printBookletCheckBox.isSelected());
    }

    private int getNumberOfPuzzes(int index) {
        int ret = 0;

        try {
            ret = Integer.parseInt(this.numberTextFields[index].getText());
        } catch (NumberFormatException ex) {
            ret = 0;
        }

        return ret;
    }

    private void adjustOrientation() {
        this.job = SudokuUtil.getPrinterJob();
        this.pageFormat = SudokuUtil.getPageFormat();
        if (this.layoutList.getSelectedIndex() >= 3) {
            this.pageFormat.setOrientation(0);
        } else {
            this.pageFormat.setOrientation(1);
        }
    }

    class MyCellRenderer extends JLabel implements ListCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            this.setHorizontalAlignment(0);
            this.setText(null);
            if (!(value instanceof Integer)) {
                return this;
            }

            this.setIcon(ExtendedPrintDialog.images[(Integer) value]);
            if (isSelected) {
                this.setBackground(list.getSelectionBackground());
                this.setForeground(list.getSelectionForeground());
            } else {
                this.setBackground(list.getBackground());
                this.setForeground(list.getForeground());
            }

            this.setEnabled(list.isEnabled());
            this.setOpaque(true);
            return this;
        }
    }
}
