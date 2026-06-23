package sudoku;

import generator.BackgroundGeneratorThread;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class ConfigLevelFontPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private JButton[] levelFGButtons;
    private JButton[] levelBGButtons;
    private DifficultyLevel[] levels;
    private Font[] fonts;
    private JLabel[] fontLabels;
    private double valueFactor;
    private double candidateFactor;
    private double hintFactor;
    private double boxFactor;
    private JLabel boxFactorLabel;
    private JTextField boxFactorTextField;
    private JTextField candidateFactorTextField;
    private JButton candidatesButton;
    private JLabel candidatesFactorLabel;
    private JLabel candidatesFontLabel;
    private JLabel candidatesLabel;
    private JButton easyBGButton;
    private JButton easyFGButton;
    private JLabel easyLabel;
    private JTextField easyTextField;
    private JButton extremeBGButton;
    private JButton extremeFGButton;
    private JLabel extremeLabel;
    private JTextField extremeTextField;
    private JButton hardBGButton;
    private JButton hardFGButton;
    private JLabel hardLabel;
    private JTextField hardTextField;
    private JLabel hintFactorLabel;
    private JTextField hintFactorTextField;
    private JButton incompleteBGButton;
    private JButton incompleteFGButton;
    private JLabel incompleteLabel;
    private JPanel jPanel1;
    private JPanel jPanel3;
    private JTextField jTextField1;
    private JButton mediumBGButton;
    private JButton mediumFGButton;
    private JLabel mediumLabel;
    private JTextField mediumTextField;
    private JButton printLargeButton;
    private JLabel printLargeFontLabel;
    private JLabel printLargeLabel;
    private JButton printSmallButton;
    private JLabel printSmallFontLabel;
    private JLabel printSmallLabel;
    private JButton resetButton;
    private JButton unfairBGButton;
    private JButton unfairFGButton;
    private JLabel unfairLabel;
    private JTextField unfairTextField;
    private JLabel valueFactorLabel;
    private JTextField valueFactorTextField;
    private JButton valuesButton;
    private JLabel valuesFontLabel;
    private JLabel valuesLabel;

    public ConfigLevelFontPanel(Component mainFrame) {
        this.initComponents();
        this.levelFGButtons = new JButton[Options.getInstance().getDifficultyLevels().length];
        this.levelFGButtons[0] = this.incompleteFGButton;
        this.levelFGButtons[1] = this.easyFGButton;
        this.levelFGButtons[2] = this.mediumFGButton;
        this.levelFGButtons[3] = this.hardFGButton;
        this.levelFGButtons[4] = this.unfairFGButton;
        this.levelFGButtons[5] = this.extremeFGButton;
        this.levelBGButtons = new JButton[Options.getInstance().getDifficultyLevels().length];
        this.levelBGButtons[0] = this.incompleteBGButton;
        this.levelBGButtons[1] = this.easyBGButton;
        this.levelBGButtons[2] = this.mediumBGButton;
        this.levelBGButtons[3] = this.hardBGButton;
        this.levelBGButtons[4] = this.unfairBGButton;
        this.levelBGButtons[5] = this.extremeBGButton;
        this.fonts = new Font[4];
        this.fontLabels = new JLabel[4];
        this.fontLabels[0] = this.valuesFontLabel;
        this.fontLabels[1] = this.candidatesFontLabel;
        this.fontLabels[2] = this.printLargeFontLabel;
        this.fontLabels[3] = this.printSmallFontLabel;
        this.easyTextField.setDocument(new NumbersOnlyDocument());
        this.mediumTextField.setDocument(new NumbersOnlyDocument());
        this.hardTextField.setDocument(new NumbersOnlyDocument());
        this.unfairTextField.setDocument(new NumbersOnlyDocument());
        this.extremeTextField.setDocument(new NumbersOnlyDocument());
        this.initAll(false);
    }

    public static void main(String[] args) {
        Locale oldDefault = Locale.getDefault();
        Locale.setDefault(Locale.ENGLISH);
        List<String> availableLanguages = new ArrayList<>();
        List<Locale> availableLocales = new ArrayList<>();
        String[] isoLanguages = Locale.getISOLanguages();

        for (String isoLang : isoLanguages) {
            Locale locale = new Locale(isoLang);
            String lang = locale.getDisplayLanguage();
            System.out.println("Locale: " + locale.toString() + " (" + lang + ")");
            ResourceBundle bundle = ResourceBundle.getBundle("intl/MainFrame", locale);
            System.out
                    .println(
                            "  Returned: "
                                    + bundle.getLocale().toString()
                                    + " ("
                                    + bundle.getLocale().getDisplayLanguage()
                                    + " - "
                                    + bundle.getString("MainFrame.error")
                                    + ")"
                    );
            if (bundle.getLocale().getLanguage().equals(isoLang)) {
                availableLanguages.add(locale.getDisplayLanguage());
                availableLocales.add(locale);
                System.out.println("  ADDED");
            }
        }

        System.out.println();
        System.out.println("Sprachen:");

        for (int i = 0; i < availableLanguages.size(); i++) {
            String la = availableLanguages.get(i);
            Locale lo = availableLocales.get(i);
            System.out.println("  " + la + " (" + lo + ")");
        }

        Locale.setDefault(oldDefault);
    }

    private void initComponents() {
        this.jPanel1 = new JPanel();
        this.easyLabel = new JLabel();
        this.mediumLabel = new JLabel();
        this.hardLabel = new JLabel();
        this.unfairLabel = new JLabel();
        this.extremeLabel = new JLabel();
        this.easyTextField = new JTextField();
        this.mediumTextField = new JTextField();
        this.hardTextField = new JTextField();
        this.unfairTextField = new JTextField();
        this.extremeTextField = new JTextField();
        this.easyFGButton = new JButton();
        this.mediumFGButton = new JButton();
        this.hardFGButton = new JButton();
        this.unfairFGButton = new JButton();
        this.extremeFGButton = new JButton();
        this.easyBGButton = new JButton();
        this.mediumBGButton = new JButton();
        this.hardBGButton = new JButton();
        this.unfairBGButton = new JButton();
        this.extremeBGButton = new JButton();
        this.incompleteLabel = new JLabel();
        this.incompleteFGButton = new JButton();
        this.jTextField1 = new JTextField();
        this.incompleteBGButton = new JButton();
        this.jPanel3 = new JPanel();
        this.valuesLabel = new JLabel();
        this.candidatesLabel = new JLabel();
        this.printLargeLabel = new JLabel();
        this.printSmallLabel = new JLabel();
        this.valuesFontLabel = new JLabel();
        this.candidatesFontLabel = new JLabel();
        this.printLargeFontLabel = new JLabel();
        this.printSmallFontLabel = new JLabel();
        this.valuesButton = new JButton();
        this.candidatesButton = new JButton();
        this.printLargeButton = new JButton();
        this.printSmallButton = new JButton();
        this.valueFactorLabel = new JLabel();
        this.candidatesFactorLabel = new JLabel();
        this.hintFactorLabel = new JLabel();
        this.valueFactorTextField = new JTextField();
        this.candidateFactorTextField = new JTextField();
        this.hintFactorTextField = new JTextField();
        this.boxFactorLabel = new JLabel();
        this.boxFactorTextField = new JTextField();
        this.resetButton = new JButton();
        ResourceBundle bundle = ResourceBundle.getBundle("intl/ConfigLevelFontPanel");
        this.jPanel1.setBorder(BorderFactory.createTitledBorder(bundle.getString("ConfigLevelFontPanel.jPanel1.border.title_1")));
        this.easyLabel.setDisplayedMnemonic(ResourceBundle.getBundle("intl/ConfigLevelFontPanel").getString("ConfigLevelFontPanel.easyLabel.mnemonic").charAt(0));
        this.easyLabel.setLabelFor(this.easyTextField);
        this.easyLabel.setText(bundle.getString("ConfigLevelFontPanel.easyLabel.text_1"));
        this.mediumLabel
                .setDisplayedMnemonic(ResourceBundle.getBundle("intl/ConfigLevelFontPanel").getString("ConfigLevelFontPanel.mediumLabel.mnemonic").charAt(0));
        this.mediumLabel.setLabelFor(this.mediumTextField);
        this.mediumLabel.setText(bundle.getString("ConfigLevelFontPanel.mediumLabel.text_1"));
        this.hardLabel.setDisplayedMnemonic(ResourceBundle.getBundle("intl/ConfigLevelFontPanel").getString("ConfigLevelFontPanel.hardLabel.mnemonic").charAt(0));
        this.hardLabel.setLabelFor(this.hardTextField);
        this.hardLabel.setText(bundle.getString("ConfigLevelFontPanel.hardLabel.text_1"));
        this.unfairLabel
                .setDisplayedMnemonic(ResourceBundle.getBundle("intl/ConfigLevelFontPanel").getString("ConfigLevelFontPanel.unfairLabel.mnemonic").charAt(0));
        this.unfairLabel.setLabelFor(this.unfairTextField);
        this.unfairLabel.setText(bundle.getString("ConfigLevelFontPanel.unfairLabel.text_1"));
        this.extremeLabel.setLabelFor(this.extremeTextField);
        this.extremeLabel.setText(bundle.getString("ConfigLevelFontPanel.extremeLabel.text_1"));
        this.extremeTextField.setEditable(false);
        this.easyFGButton.setMaximumSize(new Dimension(60, 20));
        this.easyFGButton.setMinimumSize(new Dimension(60, 20));
        this.easyFGButton.setPreferredSize(new Dimension(60, 20));
        this.easyFGButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigLevelFontPanel.this.easyFGButtonActionPerformed(evt);
            }
        });
        this.mediumFGButton.setMaximumSize(new Dimension(60, 20));
        this.mediumFGButton.setMinimumSize(new Dimension(60, 20));
        this.mediumFGButton.setPreferredSize(new Dimension(60, 20));
        this.mediumFGButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigLevelFontPanel.this.mediumFGButtonActionPerformed(evt);
            }
        });
        this.hardFGButton.setMaximumSize(new Dimension(60, 20));
        this.hardFGButton.setMinimumSize(new Dimension(60, 20));
        this.hardFGButton.setPreferredSize(new Dimension(60, 20));
        this.hardFGButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigLevelFontPanel.this.hardFGButtonActionPerformed(evt);
            }
        });
        this.unfairFGButton.setMaximumSize(new Dimension(60, 20));
        this.unfairFGButton.setMinimumSize(new Dimension(60, 20));
        this.unfairFGButton.setPreferredSize(new Dimension(60, 20));
        this.unfairFGButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigLevelFontPanel.this.unfairFGButtonActionPerformed(evt);
            }
        });
        this.extremeFGButton.setMaximumSize(new Dimension(60, 20));
        this.extremeFGButton.setMinimumSize(new Dimension(60, 20));
        this.extremeFGButton.setPreferredSize(new Dimension(60, 20));
        this.extremeFGButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigLevelFontPanel.this.extremeFGButtonActionPerformed(evt);
            }
        });
        this.easyBGButton.setMaximumSize(new Dimension(60, 20));
        this.easyBGButton.setMinimumSize(new Dimension(60, 20));
        this.easyBGButton.setPreferredSize(new Dimension(60, 20));
        this.easyBGButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigLevelFontPanel.this.easyBGButtonActionPerformed(evt);
            }
        });
        this.mediumBGButton.setMaximumSize(new Dimension(60, 20));
        this.mediumBGButton.setMinimumSize(new Dimension(60, 20));
        this.mediumBGButton.setPreferredSize(new Dimension(60, 20));
        this.mediumBGButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigLevelFontPanel.this.mediumBGButtonActionPerformed(evt);
            }
        });
        this.hardBGButton.setMaximumSize(new Dimension(60, 20));
        this.hardBGButton.setMinimumSize(new Dimension(60, 20));
        this.hardBGButton.setPreferredSize(new Dimension(60, 20));
        this.hardBGButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigLevelFontPanel.this.hardBGButtonActionPerformed(evt);
            }
        });
        this.unfairBGButton.setMaximumSize(new Dimension(60, 20));
        this.unfairBGButton.setMinimumSize(new Dimension(60, 20));
        this.unfairBGButton.setPreferredSize(new Dimension(60, 20));
        this.unfairBGButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigLevelFontPanel.this.unfairBGButtonActionPerformed(evt);
            }
        });
        this.extremeBGButton.setMaximumSize(new Dimension(60, 20));
        this.extremeBGButton.setMinimumSize(new Dimension(60, 20));
        this.extremeBGButton.setPreferredSize(new Dimension(60, 20));
        this.extremeBGButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigLevelFontPanel.this.extremeBGButtonActionPerformed(evt);
            }
        });
        this.incompleteLabel.setText("jLabel1");
        this.incompleteFGButton.setMaximumSize(new Dimension(60, 20));
        this.incompleteFGButton.setMinimumSize(new Dimension(60, 20));
        this.incompleteFGButton.setPreferredSize(new Dimension(60, 20));
        this.incompleteFGButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigLevelFontPanel.this.incompleteFGButtonActionPerformed(evt);
            }
        });
        this.jTextField1.setEditable(false);
        this.incompleteBGButton.setMaximumSize(new Dimension(60, 20));
        this.incompleteBGButton.setMinimumSize(new Dimension(60, 20));
        this.incompleteBGButton.setPreferredSize(new Dimension(60, 20));
        this.incompleteBGButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigLevelFontPanel.this.incompleteBGButtonActionPerformed(evt);
            }
        });
        GroupLayout jPanel1Layout = new GroupLayout(this.jPanel1);
        this.jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                jPanel1Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                jPanel1Layout.createParallelGroup(Alignment.LEADING)
                                                        .addComponent(this.easyLabel)
                                                        .addComponent(this.mediumLabel)
                                                        .addComponent(this.hardLabel)
                                                        .addComponent(this.unfairLabel)
                                                        .addComponent(this.extremeLabel)
                                                        .addComponent(this.incompleteLabel)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                jPanel1Layout.createParallelGroup(Alignment.LEADING)
                                                        .addGroup(
                                                                jPanel1Layout.createSequentialGroup()
                                                                        .addComponent(this.unfairTextField, -2, -1, -2)
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addComponent(this.unfairFGButton, 0, 0, 32767)
                                                        )
                                                        .addGroup(
                                                                jPanel1Layout.createSequentialGroup()
                                                                        .addComponent(this.hardTextField, -2, -1, -2)
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addComponent(this.hardFGButton, 0, 0, 32767)
                                                        )
                                                        .addGroup(
                                                                jPanel1Layout.createSequentialGroup()
                                                                        .addComponent(this.mediumTextField, -2, -1, -2)
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addComponent(this.mediumFGButton, 0, 0, 32767)
                                                        )
                                                        .addGroup(
                                                                jPanel1Layout.createSequentialGroup()
                                                                        .addComponent(this.easyTextField, -2, 57, -2)
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addComponent(this.easyFGButton, -2, 25, 32767)
                                                        )
                                                        .addGroup(
                                                                jPanel1Layout.createSequentialGroup()
                                                                        .addGroup(
                                                                                jPanel1Layout.createParallelGroup(Alignment.LEADING)
                                                                                        .addComponent(this.extremeTextField, -2, -1, -2)
                                                                                        .addComponent(this.jTextField1, -2, -1, -2)
                                                                        )
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addGroup(
                                                                                jPanel1Layout.createParallelGroup(Alignment.LEADING)
                                                                                        .addComponent(this.incompleteFGButton, -2, 25, 32767)
                                                                                        .addComponent(this.extremeFGButton, 0, 0, 32767)
                                                                        )
                                                        )
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                jPanel1Layout.createParallelGroup(Alignment.LEADING)
                                                        .addComponent(this.extremeBGButton, 0, 25, 32767)
                                                        .addComponent(this.unfairBGButton, 0, 25, 32767)
                                                        .addComponent(this.hardBGButton, 0, 25, 32767)
                                                        .addComponent(this.mediumBGButton, 0, 25, 32767)
                                                        .addComponent(this.easyBGButton, -2, 25, 32767)
                                                        .addComponent(this.incompleteBGButton, -2, 25, 32767)
                                        )
                                        .addGap(42, 42, 42)
                        )
        );
        jPanel1Layout.linkSize(0, this.easyTextField, this.extremeTextField, this.hardTextField, this.jTextField1, this.mediumTextField, this.unfairTextField);
        jPanel1Layout.linkSize(0, this.easyBGButton, this.extremeFGButton, this.hardFGButton, this.mediumFGButton, this.unfairFGButton);
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                jPanel1Layout.createSequentialGroup()
                                        .addGroup(
                                                jPanel1Layout.createParallelGroup(Alignment.LEADING)
                                                        .addGroup(
                                                                jPanel1Layout.createSequentialGroup()
                                                                        .addGroup(
                                                                                jPanel1Layout.createParallelGroup(Alignment.BASELINE)
                                                                                        .addComponent(this.easyLabel)
                                                                                        .addComponent(this.easyTextField, -2, -1, -2)
                                                                                        .addComponent(this.easyFGButton, -2, -1, -2)
                                                                        )
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addGroup(
                                                                                jPanel1Layout.createParallelGroup(Alignment.BASELINE)
                                                                                        .addComponent(this.mediumLabel)
                                                                                        .addComponent(this.mediumTextField, -2, -1, -2)
                                                                                        .addComponent(this.mediumFGButton, -2, -1, -2)
                                                                        )
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addGroup(
                                                                                jPanel1Layout.createParallelGroup(Alignment.BASELINE)
                                                                                        .addComponent(this.hardLabel)
                                                                                        .addComponent(this.hardTextField, -2, -1, -2)
                                                                                        .addComponent(this.hardFGButton, -2, -1, -2)
                                                                        )
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addGroup(
                                                                                jPanel1Layout.createParallelGroup(Alignment.BASELINE)
                                                                                        .addComponent(this.unfairLabel)
                                                                                        .addComponent(this.unfairTextField, -2, -1, -2)
                                                                                        .addComponent(this.unfairFGButton, -2, -1, -2)
                                                                        )
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addGroup(
                                                                                jPanel1Layout.createParallelGroup(Alignment.BASELINE)
                                                                                        .addComponent(this.extremeLabel)
                                                                                        .addComponent(this.extremeTextField, -2, -1, -2)
                                                                                        .addComponent(this.extremeFGButton, -2, -1, -2)
                                                                        )
                                                        )
                                                        .addGroup(
                                                                jPanel1Layout.createSequentialGroup()
                                                                        .addComponent(this.easyBGButton, -2, -1, -2)
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addComponent(this.mediumBGButton, -2, -1, -2)
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addComponent(this.hardBGButton, -2, -1, -2)
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addComponent(this.unfairBGButton, -2, -1, -2)
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addComponent(this.extremeBGButton, -2, -1, -2)
                                                        )
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                jPanel1Layout.createParallelGroup(Alignment.LEADING)
                                                        .addComponent(this.incompleteFGButton, -2, -1, -2)
                                                        .addGroup(
                                                                jPanel1Layout.createParallelGroup(Alignment.BASELINE).addComponent(this.incompleteLabel).addComponent(this.jTextField1, -2, -1, -2)
                                                        )
                                                        .addComponent(this.incompleteBGButton, -2, -1, -2)
                                        )
                                        .addContainerGap(49, 32767)
                        )
        );
        jPanel1Layout.linkSize(
                1,
                this.easyBGButton,
                this.easyFGButton,
                this.easyTextField,
                this.extremeBGButton,
                this.extremeFGButton,
                this.extremeTextField,
                this.hardBGButton,
                this.hardFGButton,
                this.hardTextField,
                this.incompleteBGButton,
                this.incompleteFGButton,
                this.jTextField1,
                this.mediumBGButton,
                this.mediumFGButton,
                this.mediumTextField,
                this.unfairBGButton,
                this.unfairFGButton,
                this.unfairTextField
        );
        this.jPanel3.setBorder(BorderFactory.createTitledBorder(bundle.getString("ConfigLevelFontPanel.jPanel3.border.title_1")));
        this.valuesLabel.setText(bundle.getString("ConfigLevelFontPanel.valuesLabel.text_1"));
        this.candidatesLabel.setText(bundle.getString("ConfigLevelFontPanel.candidatesLabel.text_1"));
        this.printLargeLabel.setText(bundle.getString("ConfigLevelFontPanel.printLargeLabel.text_1"));
        this.printSmallLabel.setText(bundle.getString("ConfigLevelFontPanel.printSmallLabel.text_1"));
        this.valuesFontLabel.setText("jLabel1");
        this.candidatesFontLabel.setText("jLabel2");
        this.printLargeFontLabel.setText("jLabel3");
        this.printSmallFontLabel.setText("jLabel4");
        this.valuesButton.setText("...");
        this.valuesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigLevelFontPanel.this.valuesButtonActionPerformed(evt);
            }
        });
        this.candidatesButton.setText("...");
        this.candidatesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigLevelFontPanel.this.candidatesButtonActionPerformed(evt);
            }
        });
        this.printLargeButton.setText("...");
        this.printLargeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigLevelFontPanel.this.printLargeButtonActionPerformed(evt);
            }
        });
        this.printSmallButton.setText("...");
        this.printSmallButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigLevelFontPanel.this.printSmallButtonActionPerformed(evt);
            }
        });
        this.valueFactorLabel
                .setDisplayedMnemonic(ResourceBundle.getBundle("intl/ConfigLevelFontPanel").getString("ConfigLevelFontPanel.valueFactorLabel.mnemonic").charAt(0));
        this.valueFactorLabel.setLabelFor(this.valueFactorTextField);
        this.valueFactorLabel.setText(bundle.getString("ConfigLevelFontPanel.valueFactorLabel.text_1"));
        this.candidatesFactorLabel
                .setDisplayedMnemonic(ResourceBundle.getBundle("intl/ConfigLevelFontPanel").getString("ConfigLevelFontPanel.candidatesFactorLabel.mnemonic").charAt(0));
        this.candidatesFactorLabel.setLabelFor(this.candidateFactorTextField);
        this.candidatesFactorLabel.setText(bundle.getString("ConfigLevelFontPanel.candidatesFactorLabel.text_1"));
        this.hintFactorLabel
                .setDisplayedMnemonic(ResourceBundle.getBundle("intl/ConfigLevelFontPanel").getString("ConfigLevelFontPanel.hintFactorLabel.mnemonic").charAt(0));
        this.hintFactorLabel.setLabelFor(this.hintFactorTextField);
        this.hintFactorLabel.setText(bundle.getString("ConfigLevelFontPanel.hintFactorLabel.text_1"));
        this.boxFactorLabel
                .setDisplayedMnemonic(ResourceBundle.getBundle("intl/ConfigLevelFontPanel").getString("ConfigLevelFontPanel.boxFactorLabel.mnemonic").charAt(0));
        this.boxFactorLabel.setLabelFor(this.boxFactorTextField);
        this.boxFactorLabel.setText(bundle.getString("ConfigLevelFontPanel.boxFactorLabel.text"));
        GroupLayout jPanel3Layout = new GroupLayout(this.jPanel3);
        this.jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                jPanel3Layout.createSequentialGroup()
                                        .addGroup(
                                                jPanel3Layout.createParallelGroup(Alignment.LEADING)
                                                        .addGroup(
                                                                jPanel3Layout.createSequentialGroup()
                                                                        .addGap(10, 10, 10)
                                                                        .addGroup(
                                                                                jPanel3Layout.createParallelGroup(Alignment.LEADING)
                                                                                        .addComponent(this.valuesLabel)
                                                                                        .addComponent(this.candidatesLabel)
                                                                                        .addComponent(this.printLargeLabel)
                                                                                        .addComponent(this.printSmallLabel)
                                                                        )
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addGroup(
                                                                                jPanel3Layout.createParallelGroup(Alignment.LEADING)
                                                                                        .addComponent(this.printSmallFontLabel)
                                                                                        .addComponent(this.printLargeFontLabel)
                                                                                        .addComponent(this.candidatesFontLabel)
                                                                                        .addComponent(this.valuesFontLabel)
                                                                        )
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addGroup(
                                                                                jPanel3Layout.createParallelGroup(Alignment.LEADING)
                                                                                        .addComponent(this.valuesButton)
                                                                                        .addComponent(this.candidatesButton)
                                                                                        .addComponent(this.printLargeButton)
                                                                                        .addComponent(this.printSmallButton)
                                                                        )
                                                        )
                                                        .addGroup(
                                                                jPanel3Layout.createSequentialGroup()
                                                                        .addContainerGap()
                                                                        .addGroup(
                                                                                jPanel3Layout.createParallelGroup(Alignment.LEADING)
                                                                                        .addComponent(this.valueFactorLabel)
                                                                                        .addComponent(this.candidatesFactorLabel)
                                                                                        .addComponent(this.hintFactorLabel)
                                                                                        .addComponent(this.boxFactorLabel)
                                                                        )
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addGroup(
                                                                                jPanel3Layout.createParallelGroup(Alignment.LEADING)
                                                                                        .addComponent(this.boxFactorTextField, -2, 21, -2)
                                                                                        .addComponent(this.hintFactorTextField, -2, -1, -2)
                                                                                        .addComponent(this.candidateFactorTextField, -2, -1, -2)
                                                                                        .addComponent(this.valueFactorTextField, -2, 60, -2)
                                                                        )
                                                        )
                                        )
                                        .addContainerGap(33, 32767)
                        )
        );
        jPanel3Layout.linkSize(0, this.boxFactorTextField, this.candidateFactorTextField, this.hintFactorTextField, this.valueFactorTextField);
        jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                jPanel3Layout.createSequentialGroup()
                                        .addGroup(
                                                jPanel3Layout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(this.valuesLabel)
                                                        .addComponent(this.valuesFontLabel)
                                                        .addComponent(this.valuesButton)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                jPanel3Layout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(this.candidatesLabel)
                                                        .addComponent(this.candidatesFontLabel)
                                                        .addComponent(this.candidatesButton)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                jPanel3Layout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(this.printLargeLabel)
                                                        .addComponent(this.printLargeFontLabel)
                                                        .addComponent(this.printLargeButton)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                jPanel3Layout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(this.printSmallLabel)
                                                        .addComponent(this.printSmallFontLabel)
                                                        .addComponent(this.printSmallButton)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                jPanel3Layout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(this.valueFactorLabel)
                                                        .addComponent(this.valueFactorTextField, -2, -1, -2)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                jPanel3Layout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(this.candidatesFactorLabel)
                                                        .addComponent(this.candidateFactorTextField, -2, -1, -2)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                jPanel3Layout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(this.hintFactorLabel)
                                                        .addComponent(this.hintFactorTextField, -2, -1, -2)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                jPanel3Layout.createParallelGroup(Alignment.BASELINE).addComponent(this.boxFactorLabel).addComponent(this.boxFactorTextField, -2, -1, -2)
                                        )
                                        .addContainerGap(-1, 32767)
                        )
        );
        this.resetButton.setMnemonic(ResourceBundle.getBundle("intl/ConfigLevelFontPanel").getString("ConfigLevelFontPanel.resetButton.mnemonic").charAt(0));
        this.resetButton.setText(bundle.getString("ConfigLevelFontPanel.resetButton.text_1"));
        this.resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigLevelFontPanel.this.resetButtonActionPerformed(evt);
            }
        });
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                layout.createParallelGroup(Alignment.LEADING)
                                                        .addGroup(
                                                                layout.createSequentialGroup()
                                                                        .addComponent(this.jPanel1, -1, -1, 32767)
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addComponent(this.jPanel3, -1, -1, 32767)
                                                        )
                                                        .addGroup(
                                                                Alignment.TRAILING,
                                                                layout.createSequentialGroup().addPreferredGap(ComponentPlacement.RELATED, 343, 32767).addComponent(this.resetButton)
                                                        )
                                        )
                                        .addContainerGap()
                        )
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                layout.createParallelGroup(Alignment.LEADING)
                                                        .addComponent(this.jPanel1, -2, -1, -2)
                                                        .addGroup(
                                                                layout.createSequentialGroup()
                                                                        .addComponent(this.jPanel3, -2, -1, -2)
                                                                        .addPreferredGap(ComponentPlacement.RELATED, 97, 32767)
                                                                        .addComponent(this.resetButton)
                                                        )
                                        )
                                        .addContainerGap()
                        )
        );
        layout.linkSize(1, this.jPanel1, this.jPanel3);
    }

    private void resetButtonActionPerformed(ActionEvent evt) {
        this.initAll(true);
    }

    private void printSmallButtonActionPerformed(ActionEvent evt) {
        this.chooseFont(3);
    }

    private void printLargeButtonActionPerformed(ActionEvent evt) {
        this.chooseFont(2);
    }

    private void candidatesButtonActionPerformed(ActionEvent evt) {
        this.chooseFont(1);
    }

    private void valuesButtonActionPerformed(ActionEvent evt) {
        this.chooseFont(0);
    }

    private void incompleteBGButtonActionPerformed(ActionEvent evt) {
        this.chooseColor(0, false);
    }

    private void incompleteFGButtonActionPerformed(ActionEvent evt) {
        this.chooseColor(0, true);
    }

    private void extremeBGButtonActionPerformed(ActionEvent evt) {
        this.chooseColor(5, false);
    }

    private void extremeFGButtonActionPerformed(ActionEvent evt) {
        this.chooseColor(5, true);
    }

    private void unfairBGButtonActionPerformed(ActionEvent evt) {
        this.chooseColor(4, false);
    }

    private void unfairFGButtonActionPerformed(ActionEvent evt) {
        this.chooseColor(4, true);
    }

    private void hardBGButtonActionPerformed(ActionEvent evt) {
        this.chooseColor(3, false);
    }

    private void hardFGButtonActionPerformed(ActionEvent evt) {
        this.chooseColor(3, true);
    }

    private void mediumBGButtonActionPerformed(ActionEvent evt) {
        this.chooseColor(2, false);
    }

    private void mediumFGButtonActionPerformed(ActionEvent evt) {
        this.chooseColor(2, true);
    }

    private void easyBGButtonActionPerformed(ActionEvent evt) {
        this.chooseColor(1, false);
    }

    private void easyFGButtonActionPerformed(ActionEvent evt) {
        this.chooseColor(1, true);
    }

    public void okPressed() {
        int[] oldScores = new int[this.levels.length];

        for (int i = 1; i < this.levels.length; i++) {
            oldScores[i] = this.levels[i].getMaxScore();
        }

        this.levels[1].setMaxScore(Integer.parseInt(this.easyTextField.getText()));
        this.levels[2].setMaxScore(Integer.parseInt(this.mediumTextField.getText()));
        this.levels[3].setMaxScore(Integer.parseInt(this.hardTextField.getText()));
        this.levels[4].setMaxScore(Integer.parseInt(this.unfairTextField.getText()));
        Options.getInstance().setDifficultyLevels(Options.getInstance().copyDifficultyLevels(this.levels));
        boolean scoresChanged = false;

        for (int i = 1; i < this.levels.length; i++) {
            if (this.levels[i].getMaxScore() != oldScores[i]) {
                scoresChanged = true;
                break;
            }
        }

        if (scoresChanged) {
            BackgroundGeneratorThread.getInstance().resetAll();
        }

        Options.getInstance().setDefaultValueFont(this.fonts[0]);
        Options.getInstance().setDefaultCandidateFont(this.fonts[1]);
        Options.getInstance().setBigFont(this.fonts[2]);
        Options.getInstance().setSmallFont(this.fonts[3]);

        try {
            Options.getInstance().setValueFontFactor(Double.parseDouble(this.valueFactorTextField.getText()));
        } catch (NumberFormatException ex) {
            Options.getInstance().setValueFontFactor(0.6);
        }

        try {
            Options.getInstance().setCandidateFontFactor(Double.parseDouble(this.candidateFactorTextField.getText()));
        } catch (NumberFormatException ex) {
            Options.getInstance().setCandidateFontFactor(0.25);
        }

        try {
            Options.getInstance().setHintBackFactor(Double.parseDouble(this.hintFactorTextField.getText()));
        } catch (NumberFormatException ex) {
            Options.getInstance().setHintBackFactor(1.6);
        }

        try {
            Options.getInstance().setBoxLineFactor(Double.parseDouble(this.boxFactorTextField.getText()));
        } catch (NumberFormatException ex) {
            Options.getInstance().setBoxLineFactor(1.5);
        }
    }

    private void initAll(boolean setDefault) {
        if (setDefault) {
            this.levels = Options.getInstance().copyDifficultyLevels(Options.DEFAULT_DIFFICULTY_LEVELS);
            this.fonts[0] = Options.DEFAULT_VALUE_FONT;
            this.fonts[1] = Options.DEFAULT_CANDIDATE_FONT;
            this.fonts[2] = Options.BIG_FONT;
            this.fonts[3] = Options.SMALL_FONT;
            this.valueFactor = 0.6;
            this.candidateFactor = 0.25;
            this.hintFactor = 1.6;
            this.boxFactor = 1.5;
        } else {
            this.levels = Options.getInstance().copyDifficultyLevels(Options.getInstance().getDifficultyLevels());
            this.fonts[0] = Options.getInstance().getDefaultValueFont();
            this.fonts[1] = Options.getInstance().getDefaultCandidateFont();
            this.fonts[2] = Options.getInstance().getBigFont();
            this.fonts[3] = Options.getInstance().getSmallFont();
            this.valueFactor = Options.getInstance().getValueFontFactor();
            this.candidateFactor = Options.getInstance().getCandidateFontFactor();
            this.hintFactor = Options.getInstance().getHintBackFactor();
            this.boxFactor = Options.getInstance().getBoxLineFactor();
        }

        this.initButtons();
        this.incompleteLabel.setText(this.levels[0].getName() + ":");
        this.easyLabel.setText(this.levels[1].getName() + ":");
        this.mediumLabel.setText(this.levels[2].getName() + ":");
        this.hardLabel.setText(this.levels[3].getName() + ":");
        this.unfairLabel.setText(this.levels[4].getName() + ":");
        this.extremeLabel.setText(this.levels[5].getName() + ":");
        this.easyTextField.setText(Integer.toString(this.levels[1].getMaxScore()));
        this.mediumTextField.setText(Integer.toString(this.levels[2].getMaxScore()));
        this.hardTextField.setText(Integer.toString(this.levels[3].getMaxScore()));
        this.unfairTextField.setText(Integer.toString(this.levels[4].getMaxScore()));

        for (int i = 0; i < this.fonts.length; i++) {
            this.setFont(this.fonts[i], this.fontLabels[i]);
        }

        this.valueFactorTextField.setText(Double.toString(this.valueFactor));
        this.candidateFactorTextField.setText(Double.toString(this.candidateFactor));
        this.hintFactorTextField.setText(Double.toString(this.hintFactor));
        this.boxFactorTextField.setText(Double.toString(this.boxFactor));
    }

    private void chooseFont(int index) {
        Font font = MyFontChooser.showDialog(
                null, ResourceBundle.getBundle("intl/ConfigLevelFontPanel").getString("ConfigLevelFontPanel.choose_font"), this.fonts[index]
        );
        if (font != null) {
            this.fonts[index] = font;
            this.setFont(this.fonts[index], this.fontLabels[index]);
        }
    }

    private void setFont(Font font, JLabel label) {
        int style = font.getStyle();
        String styleStr = "";
        switch (style) {
            case 0:
                styleStr = ResourceBundle.getBundle("intl/ConfigLevelFontPanel").getString("ConfigLevelFontPanel.regular");
                break;
            case 1:
                styleStr = ResourceBundle.getBundle("intl/ConfigLevelFontPanel").getString("ConfigLevelFontPanel.bold");
                break;
            case 2:
                styleStr = ResourceBundle.getBundle("intl/ConfigLevelFontPanel").getString("ConfigLevelFontPanel.italic");
                break;
            case 3:
                styleStr = ResourceBundle.getBundle("intl/ConfigLevelFontPanel").getString("ConfigLevelFontPanel.bold_italic");
        }

        label.setText(font.getName() + " " + font.getSize() + " " + styleStr);
    }

    private void chooseColor(int index, boolean foreGround) {
        Color init = foreGround ? this.levels[index].getForegroundColor() : this.levels[index].getBackgroundColor();
        Color color = JColorChooser.showDialog(this, ResourceBundle.getBundle("intl/ConfigLevelFontPanel").getString("ConfigLevelFontPanel.choose_color"), init);
        if (color != null) {
            if (foreGround) {
                this.levels[index].setForegroundColor(color);
                this.initButton(this.levelFGButtons[index], color);
            } else {
                this.levels[index].setBackgroundColor(color);
                this.initButton(this.levelBGButtons[index], color);
            }
        }
    }

    private void initButtons() {
        for (int i = 0; i < this.levelFGButtons.length; i++) {
            this.initButton(this.levelFGButtons[i], this.levels[i].getForegroundColor());
            this.initButton(this.levelBGButtons[i], this.levels[i].getBackgroundColor());
        }
    }

    private void initButton(JButton button, Color color) {
        Image img = new BufferedImage(10, 10, 1);
        Graphics g = img.getGraphics();
        g.setColor(color);
        g.fillRect(0, 0, 10, 10);
        button.setIcon(new ImageIcon(img));
        if (UIManager.getLookAndFeel().getName().equals("CDE/Motif")) {
            button.setBackground(color);
        }
    }
}
