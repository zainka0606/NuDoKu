package sudoku;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager.LookAndFeelInfo;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigGeneralPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private String language;
    private String laf;
    private List<Locale> availableLocales = new ArrayList<>();
    private List<String> availableIsoLanguages = new ArrayList<>();
    private List<String> availableLafs = new ArrayList<>();
    private List<String> availableLafClassNames = new ArrayList<>();
    private Component mainFrame;
    private JCheckBox alternativeMouseModeCheckBox;
    private JCheckBox colorValuesCheckBox;
    private JCheckBox defaultSizeCheckBox;
    private JCheckBox deleteCursorAfterCheckBox;
    private JTextField deleteCursorAfterMsTextField;
    private JCheckBox drawExtraBoxesCheckBox;
    private JCheckBox editModeAutoAdvanceCheckBox;
    private JLabel fontSizeLabel;
    private JTextField fontSizeTextField;
    private JLabel jLabel1;
    private JPanel jPanel2;
    private JPanel jPanel4;
    private JComboBox localComboBox;
    private JLabel localLabel;
    private JComboBox lookAndFeelComboBox;
    private JLabel lookAndFeelLabel;
    private JCheckBox onlySmallCursorsCheckBox;
    private JCheckBox onlySmallFiltersCheckBox;
    private JButton resetButton;
    private JCheckBox saveWindowLayoutCheckBox;
    private JCheckBox shiftKeyCheckBox;
    private JCheckBox showCandidatesCheckBox;
    private JCheckBox showColorKuCheckBox;
    private JCheckBox showDeviationsCheckBox;
    private JCheckBox showSudokuSolvedCheckBox;
    private JCheckBox showWrongValuesCheckBox;
    private JCheckBox toggleFilterAndOrCheckBox;

    public ConfigGeneralPanel(Component mainFrame) {
        this.mainFrame = mainFrame;
        this.initComponents();
        this.initLanguages();
        this.initLafs();
        this.deleteCursorAfterMsTextField.setDocument(new NumbersOnlyDocument());
        this.fontSizeTextField.setDocument(new NumbersOnlyDocument());
        this.initAll(false);
        this.deleteCursorAfterMsTextField.setEnabled(this.deleteCursorAfterCheckBox.isSelected());
        this.fontSizeTextField.setEnabled(!this.defaultSizeCheckBox.isSelected());
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
        this.jPanel2 = new JPanel();
        this.showCandidatesCheckBox = new JCheckBox();
        this.showWrongValuesCheckBox = new JCheckBox();
        this.showDeviationsCheckBox = new JCheckBox();
        this.saveWindowLayoutCheckBox = new JCheckBox();
        this.localLabel = new JLabel();
        this.localComboBox = new JComboBox();
        this.lookAndFeelComboBox = new JComboBox();
        this.lookAndFeelLabel = new JLabel();
        this.fontSizeLabel = new JLabel();
        this.fontSizeTextField = new JTextField();
        this.showColorKuCheckBox = new JCheckBox();
        this.defaultSizeCheckBox = new JCheckBox();
        this.resetButton = new JButton();
        this.jPanel4 = new JPanel();
        this.shiftKeyCheckBox = new JCheckBox();
        this.onlySmallCursorsCheckBox = new JCheckBox();
        this.colorValuesCheckBox = new JCheckBox();
        this.showSudokuSolvedCheckBox = new JCheckBox();
        this.deleteCursorAfterCheckBox = new JCheckBox();
        this.deleteCursorAfterMsTextField = new JTextField();
        this.jLabel1 = new JLabel();
        this.toggleFilterAndOrCheckBox = new JCheckBox();
        this.alternativeMouseModeCheckBox = new JCheckBox();
        this.onlySmallFiltersCheckBox = new JCheckBox();
        this.editModeAutoAdvanceCheckBox = new JCheckBox();
        this.drawExtraBoxesCheckBox = new JCheckBox();
        ResourceBundle bundle = ResourceBundle.getBundle("intl/ConfigGeneralPanel");
        this.jPanel2.setBorder(BorderFactory.createTitledBorder(bundle.getString("ConfigGeneralPanel.jPanel2.border.title")));
        this.showCandidatesCheckBox
                .setMnemonic(ResourceBundle.getBundle("intl/ConfigGeneralPanel").getString("ConfigGeneralPanel.showCandidatesCheckBox.mnemonic").charAt(0));
        this.showCandidatesCheckBox.setText(bundle.getString("ConfigGeneralPanel.showCandidatesCheckBox.text"));
        this.showCandidatesCheckBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        this.showCandidatesCheckBox.setMargin(new Insets(0, 0, 0, 0));
        this.showWrongValuesCheckBox
                .setMnemonic(ResourceBundle.getBundle("intl/ConfigGeneralPanel").getString("ConfigGeneralPanel.showWrongValuesCheckBox.mnemonic").charAt(0));
        this.showWrongValuesCheckBox.setText(bundle.getString("ConfigGeneralPanel.showWrongValuesCheckBox.text"));
        this.showWrongValuesCheckBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        this.showWrongValuesCheckBox.setMargin(new Insets(0, 0, 0, 0));
        this.showDeviationsCheckBox
                .setMnemonic(ResourceBundle.getBundle("intl/ConfigGeneralPanel").getString("ConfigGeneralPanel.showDeviationsCheckBox.mnemonic").charAt(0));
        this.showDeviationsCheckBox.setText(bundle.getString("ConfigGeneralPanel.showDeviationsCheckBox.text"));
        this.showDeviationsCheckBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        this.showDeviationsCheckBox.setMargin(new Insets(0, 0, 0, 0));
        this.saveWindowLayoutCheckBox
                .setMnemonic(ResourceBundle.getBundle("intl/ConfigGeneralPanel").getString("ConfigGeneralPanel.saveWindowLayoutCheckBox.mnemonic").charAt(0));
        this.saveWindowLayoutCheckBox.setText(bundle.getString("ConfigGeneralPanel.saveWindowLayoutCheckBox.text"));
        this.saveWindowLayoutCheckBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        this.saveWindowLayoutCheckBox.setMargin(new Insets(0, 0, 0, 0));
        this.localLabel.setDisplayedMnemonic(ResourceBundle.getBundle("intl/ConfigGeneralPanel").getString("ConfigGeneralPanel.localLabel.mnemonic").charAt(0));
        this.localLabel.setLabelFor(this.localComboBox);
        this.localLabel.setText(bundle.getString("ConfigGeneralPanel.localLabel.text"));
        this.lookAndFeelLabel
                .setDisplayedMnemonic(ResourceBundle.getBundle("intl/ConfigGeneralPanel").getString("ConfigGeneralPanel.lookAndFeelLabel.mnemonic").charAt(0));
        this.lookAndFeelLabel.setLabelFor(this.lookAndFeelComboBox);
        this.lookAndFeelLabel.setText(bundle.getString("ConfigGeneralPanel.lookAndFeelLabel.text"));
        this.fontSizeLabel
                .setDisplayedMnemonic(ResourceBundle.getBundle("intl/ConfigGeneralPanel").getString("ConfigGeneralPanel.fontSizeLabel.mnemonic").charAt(0));
        this.fontSizeLabel.setLabelFor(this.fontSizeTextField);
        this.fontSizeLabel.setText(bundle.getString("ConfigGeneralPanel.fontSizeLabel.text"));
        this.showColorKuCheckBox
                .setMnemonic(ResourceBundle.getBundle("intl/ConfigGeneralPanel").getString("ConfigGeneralPanel.showColorKuCheckBox.mnemonic").charAt(0));
        this.showColorKuCheckBox.setText(bundle.getString("ConfigGeneralPanel.showColorKuCheckBox.text"));
        this.showColorKuCheckBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        this.showColorKuCheckBox.setMargin(new Insets(0, 0, 0, 0));
        this.defaultSizeCheckBox
                .setMnemonic(ResourceBundle.getBundle("intl/ConfigGeneralPanel").getString("ConfigGeneralPanel.defaultSizeCheckBox.mnemonic").charAt(0));
        this.defaultSizeCheckBox.setText(bundle.getString("ConfigGeneralPanel.defaultSizeCheckBox.text"));
        this.defaultSizeCheckBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        this.defaultSizeCheckBox.setMargin(new Insets(0, 0, 0, 0));
        this.defaultSizeCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigGeneralPanel.this.defaultSizeCheckBoxActionPerformed(evt);
            }
        });
        GroupLayout jPanel2Layout = new GroupLayout(this.jPanel2);
        this.jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                jPanel2Layout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addGroup(jPanel2Layout.createParallelGroup(Alignment.LEADING).addComponent(this.localLabel).addComponent(this.lookAndFeelLabel))
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                jPanel2Layout.createParallelGroup(Alignment.LEADING)
                                                        .addComponent(this.localComboBox, 0, 120, 32767)
                                                        .addComponent(this.lookAndFeelComboBox, 0, 120, 32767)
                                        )
                                        .addGap(10, 10, 10)
                        )
                        .addGroup(
                                jPanel2Layout.createSequentialGroup()
                                        .addGroup(
                                                jPanel2Layout.createParallelGroup(Alignment.LEADING)
                                                        .addGroup(jPanel2Layout.createSequentialGroup().addContainerGap().addComponent(this.defaultSizeCheckBox))
                                                        .addGroup(
                                                                jPanel2Layout.createSequentialGroup()
                                                                        .addContainerGap()
                                                                        .addGroup(
                                                                                jPanel2Layout.createParallelGroup(Alignment.LEADING)
                                                                                        .addComponent(this.showCandidatesCheckBox)
                                                                                        .addComponent(this.showWrongValuesCheckBox)
                                                                                        .addComponent(this.showDeviationsCheckBox)
                                                                                        .addComponent(this.saveWindowLayoutCheckBox)
                                                                                        .addGroup(
                                                                                                jPanel2Layout.createSequentialGroup()
                                                                                                        .addComponent(this.fontSizeLabel)
                                                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                                                        .addComponent(this.fontSizeTextField, -2, 55, -2)
                                                                                        )
                                                                                        .addComponent(this.showColorKuCheckBox)
                                                                        )
                                                        )
                                        )
                                        .addContainerGap()
                        )
        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                jPanel2Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(jPanel2Layout.createParallelGroup(Alignment.BASELINE).addComponent(this.localLabel).addComponent(this.localComboBox, -2, -1, -2))
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                jPanel2Layout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(this.lookAndFeelLabel)
                                                        .addComponent(this.lookAndFeelComboBox, -2, -1, -2)
                                        )
                                        .addGap(18, 18, 18)
                                        .addComponent(this.defaultSizeCheckBox)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                jPanel2Layout.createParallelGroup(Alignment.BASELINE).addComponent(this.fontSizeLabel).addComponent(this.fontSizeTextField, -2, -1, -2)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.showCandidatesCheckBox)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.showWrongValuesCheckBox)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.showDeviationsCheckBox)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.saveWindowLayoutCheckBox)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.showColorKuCheckBox)
                                        .addContainerGap(47, 32767)
                        )
        );
        this.resetButton.setMnemonic(ResourceBundle.getBundle("intl/ConfigGeneralPanel").getString("ConfigGeneralPanel.resetButton.mnemonic").charAt(0));
        this.resetButton.setText(bundle.getString("ConfigGeneralPanel.resetButton.text"));
        this.resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigGeneralPanel.this.resetButtonActionPerformed(evt);
            }
        });
        this.jPanel4.setBorder(BorderFactory.createTitledBorder(bundle.getString("ConfigGeneralPanel.jPanel4.border.title")));
        this.shiftKeyCheckBox
                .setMnemonic(ResourceBundle.getBundle("intl/ConfigGeneralPanel").getString("ConfigGeneralPanel.shiftKeyCheckBox.mnemonic").charAt(0));
        this.shiftKeyCheckBox.setText(bundle.getString("ConfigGeneralPanel.shiftKeyCheckBox.text"));
        this.onlySmallCursorsCheckBox
                .setMnemonic(ResourceBundle.getBundle("intl/ConfigGeneralPanel").getString("ConfigGeneralPanel.onlySmallCursorsCheckBox.mnemonic").charAt(0));
        this.onlySmallCursorsCheckBox.setText(bundle.getString("ConfigGeneralPanel.onlySmallCursorsCheckBox.text"));
        this.colorValuesCheckBox
                .setMnemonic(ResourceBundle.getBundle("intl/ConfigGeneralPanel").getString("ConfigGeneralPanel.colorValuesCheckBox.mnemonics").charAt(0));
        this.colorValuesCheckBox.setText(bundle.getString("ConfigGeneralPanel.colorValuesCheckBox.text"));
        this.showSudokuSolvedCheckBox
                .setMnemonic(ResourceBundle.getBundle("intl/ConfigGeneralPanel").getString("ConfigGeneralPanel.showSudokuSolvedCheckBox.mnemonic").charAt(0));
        this.showSudokuSolvedCheckBox.setText(bundle.getString("ConfigGeneralPanel.showSudokuSolvedCheckBox.text"));
        this.deleteCursorAfterCheckBox
                .setMnemonic(ResourceBundle.getBundle("intl/ConfigGeneralPanel").getString("ConfigGeneralPanel.deleteCursorAfterCheckBox.mnemonic").charAt(0));
        this.deleteCursorAfterCheckBox.setText(bundle.getString("ConfigGeneralPanel.deleteCursorAfterCheckBox.text"));
        this.deleteCursorAfterCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigGeneralPanel.this.deleteCursorAfterCheckBoxActionPerformed(evt);
            }
        });
        this.deleteCursorAfterMsTextField.setText(bundle.getString("ConfigGeneralPanel.deleteCursorAfterMsTextField.text"));
        this.jLabel1.setText(bundle.getString("ConfigGeneralPanel.jLabel1.text"));
        this.toggleFilterAndOrCheckBox
                .setMnemonic(ResourceBundle.getBundle("intl/ConfigGeneralPanel").getString("ConfigGeneralPanel.toggleFilterAndOrCheckBox.mnemonic").charAt(0));
        this.toggleFilterAndOrCheckBox.setText(bundle.getString("ConfigGeneralPanel.toggleFilterAndOrCheckBox.text"));
        this.alternativeMouseModeCheckBox
                .setMnemonic(ResourceBundle.getBundle("intl/ConfigGeneralPanel").getString("ConfigGeneralPanel.alternativeMouseModeCheckBox.mnemonic").charAt(0));
        this.alternativeMouseModeCheckBox.setText(bundle.getString("ConfigGeneralPanel.alternativeMouseModeCheckBox.text"));
        this.onlySmallFiltersCheckBox
                .setMnemonic(ResourceBundle.getBundle("intl/ConfigGeneralPanel").getString("ConfigGeneralPanel.onlySmallFiltersCheckBox.mnemonic").charAt(0));
        this.onlySmallFiltersCheckBox.setText(bundle.getString("ConfigGeneralPanel.onlySmallFiltersCheckBox.text"));
        this.editModeAutoAdvanceCheckBox
                .setMnemonic(ResourceBundle.getBundle("intl/ConfigGeneralPanel").getString("ConfigGeneralPanel.editModeAutoAdvanceCheckBox.mnemonic").charAt(0));
        this.editModeAutoAdvanceCheckBox.setText(bundle.getString("ConfigGeneralPanel.editModeAutoAdvanceCheckBox.text"));
        this.drawExtraBoxesCheckBox
                .setMnemonic(ResourceBundle.getBundle("intl/ConfigGeneralPanel").getString("ConfigGeneralPanel.drawExtraBoxesCheckBox.mnemonic").charAt(0));
        this.drawExtraBoxesCheckBox.setText(bundle.getString("ConfigGeneralPanel.drawExtraBoxesCheckBox.text"));
        GroupLayout jPanel4Layout = new GroupLayout(this.jPanel4);
        this.jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
                jPanel4Layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                jPanel4Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                jPanel4Layout.createParallelGroup(Alignment.LEADING)
                                                        .addComponent(this.drawExtraBoxesCheckBox)
                                                        .addComponent(this.showSudokuSolvedCheckBox)
                                                        .addComponent(this.colorValuesCheckBox)
                                                        .addComponent(this.onlySmallCursorsCheckBox)
                                                        .addComponent(this.shiftKeyCheckBox)
                                                        .addComponent(this.toggleFilterAndOrCheckBox)
                                                        .addComponent(this.deleteCursorAfterCheckBox)
                                                        .addGroup(
                                                                jPanel4Layout.createSequentialGroup()
                                                                        .addGap(21, 21, 21)
                                                                        .addComponent(this.deleteCursorAfterMsTextField, -2, 51, -2)
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addComponent(this.jLabel1)
                                                        )
                                                        .addComponent(this.alternativeMouseModeCheckBox)
                                                        .addComponent(this.onlySmallFiltersCheckBox)
                                                        .addComponent(this.editModeAutoAdvanceCheckBox)
                                        )
                                        .addContainerGap(26, 32767)
                        )
        );
        jPanel4Layout.setVerticalGroup(
                jPanel4Layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                jPanel4Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(this.drawExtraBoxesCheckBox)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.shiftKeyCheckBox)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.onlySmallCursorsCheckBox)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.colorValuesCheckBox)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.showSudokuSolvedCheckBox)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.toggleFilterAndOrCheckBox)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.deleteCursorAfterCheckBox)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                jPanel4Layout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(this.deleteCursorAfterMsTextField, -2, -1, -2)
                                                        .addComponent(this.jLabel1)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.alternativeMouseModeCheckBox)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.onlySmallFiltersCheckBox)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.editModeAutoAdvanceCheckBox)
                                        .addContainerGap(-1, 32767)
                        )
        );
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
                                                                        .addComponent(this.jPanel2, -1, -1, 32767)
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addComponent(this.jPanel4, -1, -1, 32767)
                                                        )
                                                        .addGroup(
                                                                Alignment.TRAILING,
                                                                layout.createSequentialGroup().addPreferredGap(ComponentPlacement.RELATED, 342, 32767).addComponent(this.resetButton)
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
                                                        .addComponent(this.jPanel2, -2, -1, -2)
                                                        .addGroup(
                                                                layout.createSequentialGroup()
                                                                        .addComponent(this.jPanel4, -2, -1, -2)
                                                                        .addPreferredGap(ComponentPlacement.RELATED, 96, 32767)
                                                                        .addComponent(this.resetButton)
                                                        )
                                        )
                                        .addContainerGap()
                        )
        );
        layout.linkSize(1, this.jPanel2, this.jPanel4);
    }

    private void resetButtonActionPerformed(ActionEvent evt) {
        this.initAll(true);
    }

    private void deleteCursorAfterCheckBoxActionPerformed(ActionEvent evt) {
        this.deleteCursorAfterMsTextField.setEnabled(this.deleteCursorAfterCheckBox.isSelected());
    }

    private void defaultSizeCheckBoxActionPerformed(ActionEvent evt) {
        this.fontSizeTextField.setEnabled(!this.defaultSizeCheckBox.isSelected());
    }

    public void okPressed() {
        boolean oldUseDefaultFontSize = Options.getInstance().isUseDefaultFontSize();
        Options.getInstance().setUseDefaultFontSize(this.defaultSizeCheckBox.isSelected());
        int oldFontSize = Options.getInstance().getCustomFontSize();
        Options.getInstance().setCustomFontSize(Integer.parseInt(this.fontSizeTextField.getText()));
        Options.getInstance().setShowCandidates(this.showCandidatesCheckBox.isSelected());
        Options.getInstance().setShowWrongValues(this.showWrongValuesCheckBox.isSelected());
        Options.getInstance().setShowDeviations(this.showDeviationsCheckBox.isSelected());
        Options.getInstance().setShowColorKu(this.showColorKuCheckBox.isSelected());
        Options.getInstance().setSaveWindowLayout(this.saveWindowLayoutCheckBox.isSelected());
        Options.getInstance().setAlternativeMouseMode(this.alternativeMouseModeCheckBox.isSelected());
        Options.getInstance().setOnlySmallFilters(this.onlySmallFiltersCheckBox.isSelected());
        Options.getInstance().setEditModeAutoAdvance(this.editModeAutoAdvanceCheckBox.isSelected());
        Options.getInstance().setDrawMode(this.drawExtraBoxesCheckBox.isSelected() ? 0 : 1);
        if (!Options.getInstance().isUseDefaultFontSize() && (oldUseDefaultFontSize || oldFontSize != Options.getInstance().getCustomFontSize())
                || Options.getInstance().isUseDefaultFontSize() && !oldUseDefaultFontSize) {
            JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("intl/ConfigGeneralPanel").getString("GeneralConfigPanel.restart_program2"));
        }

        this.language = this.availableIsoLanguages.get(this.localComboBox.getSelectedIndex());
        if (!this.language.equals(Options.getInstance().getLanguage())) {
            JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("intl/ConfigGeneralPanel").getString("GeneralConfigPanel.restart_program"));
        }

        Options.getInstance().setLanguage(this.language);
        this.laf = this.availableLafClassNames.get(this.lookAndFeelComboBox.getSelectedIndex());
        if (!this.laf.equals(Options.getInstance().getLaf())) {
            Options.getInstance().setLaf(this.laf);

            try {
                SudokuUtil.setLookAndFeel();
                SwingUtilities.updateComponentTreeUI(this);
                SwingUtilities.updateComponentTreeUI(this.mainFrame);
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error setting LAF", ex);
            }
        }

        Options.getInstance().setUseShiftForRegionSelect(this.shiftKeyCheckBox.isSelected());
        Options.getInstance().setOnlySmallCursors(this.onlySmallCursorsCheckBox.isSelected());
        Options.getInstance().setColorValues(this.colorValuesCheckBox.isSelected());
        Options.getInstance().setShowSudokuSolved(this.showSudokuSolvedCheckBox.isSelected());
        Options.getInstance().setDeleteCursorDisplay(this.deleteCursorAfterCheckBox.isSelected());
        String number = this.deleteCursorAfterMsTextField.getText();
        if (number == null || number.isEmpty()) {
            number = "0";
        }

        Options.getInstance().setDeleteCursorDisplayLength(Integer.parseInt(number));
        Options.getInstance().setUseOrInsteadOfAndForFilter(this.toggleFilterAndOrCheckBox.isSelected());
    }

    private void initAll(boolean setDefault) {
        if (setDefault) {
            this.defaultSizeCheckBox.setSelected(true);
            this.fontSizeTextField.setText(String.valueOf(12));
            this.showCandidatesCheckBox.setSelected(true);
            this.showWrongValuesCheckBox.setSelected(true);
            this.showDeviationsCheckBox.setSelected(true);
            this.showColorKuCheckBox.setSelected(false);
            this.saveWindowLayoutCheckBox.setSelected(true);
            this.alternativeMouseModeCheckBox.setSelected(false);
            this.onlySmallFiltersCheckBox.setSelected(false);
            this.drawExtraBoxesCheckBox.setSelected(false);
            this.language = "";
            this.laf = "";
            this.shiftKeyCheckBox.setSelected(true);
            this.onlySmallCursorsCheckBox.setSelected(true);
            this.editModeAutoAdvanceCheckBox.setSelected(false);
            this.colorValuesCheckBox.setSelected(true);
            this.showSudokuSolvedCheckBox.setSelected(false);
            this.deleteCursorAfterCheckBox.setSelected(false);
            this.deleteCursorAfterMsTextField.setText(String.valueOf(1000));
            this.toggleFilterAndOrCheckBox.setSelected(false);
        } else {
            this.defaultSizeCheckBox.setSelected(Options.getInstance().isUseDefaultFontSize());
            this.fontSizeTextField.setText(String.valueOf(Options.getInstance().getCustomFontSize()));
            this.showCandidatesCheckBox.setSelected(Options.getInstance().isShowCandidates());
            this.showWrongValuesCheckBox.setSelected(Options.getInstance().isShowWrongValues());
            this.showDeviationsCheckBox.setSelected(Options.getInstance().isShowDeviations());
            this.showColorKuCheckBox.setSelected(Options.getInstance().isShowColorKu());
            this.saveWindowLayoutCheckBox.setSelected(Options.getInstance().isSaveWindowLayout());
            this.alternativeMouseModeCheckBox.setSelected(Options.getInstance().isAlternativeMouseMode());
            this.onlySmallFiltersCheckBox.setSelected(Options.getInstance().isOnlySmallFilters());
            this.drawExtraBoxesCheckBox.setSelected(Options.getInstance().getDrawMode() == 0);
            this.editModeAutoAdvanceCheckBox.setSelected(Options.getInstance().isEditModeAutoAdvance());
            this.language = Options.getInstance().getLanguage();
            this.laf = Options.getInstance().getLaf();
            this.shiftKeyCheckBox.setSelected(Options.getInstance().isUseShiftForRegionSelect());
            this.onlySmallCursorsCheckBox.setSelected(Options.getInstance().isOnlySmallCursors());
            this.colorValuesCheckBox.setSelected(Options.getInstance().isColorValues());
            this.showSudokuSolvedCheckBox.setSelected(Options.getInstance().isShowSudokuSolved());
            this.deleteCursorAfterCheckBox.setSelected(Options.getInstance().isDeleteCursorDisplay());
            this.deleteCursorAfterMsTextField.setText(String.valueOf(Options.getInstance().getDeleteCursorDisplayLength()));
            this.toggleFilterAndOrCheckBox.setSelected(Options.getInstance().isUseOrInsteadOfAndForFilter());
        }

        this.localComboBox.removeAllItems();
        this.localComboBox.addItem(ResourceBundle.getBundle("intl/ConfigGeneralPanel").getString("GeneralConfigPanel.automatic"));
        int languageIndex = 0;

        for (int i = 1; i < this.availableLocales.size(); i++) {
            this.localComboBox.addItem(this.availableLocales.get(i).getDisplayLanguage());
            if (this.language.equalsIgnoreCase(this.availableIsoLanguages.get(i))) {
                languageIndex = i;
            }
        }

        this.localComboBox.setSelectedIndex(languageIndex);
        this.lookAndFeelComboBox.removeAllItems();
        int lafIndex = 0;

        for (int i = 0; i < this.availableLafs.size(); i++) {
            this.lookAndFeelComboBox.addItem(this.availableLafs.get(i));
            if (this.laf.equals(this.availableLafClassNames.get(i))) {
                lafIndex = i;
            }
        }

        this.lookAndFeelComboBox.setSelectedIndex(lafIndex);
    }

    private void initLanguages() {
        this.availableIsoLanguages.clear();
        this.availableLocales.clear();
        this.availableIsoLanguages.add("");
        this.availableLocales.add(null);
        this.availableIsoLanguages.add("en");
        this.availableLocales.add(Locale.ENGLISH);
        Locale oldDefault = Locale.getDefault();
        Locale.setDefault(Locale.ENGLISH);
        String[] isoLanguages = Locale.getISOLanguages();

        for (String isoLang : isoLanguages) {
            Locale locale = new Locale(isoLang);
            ResourceBundle bundle = ResourceBundle.getBundle("intl/MainFrame", locale);
            if (bundle.getLocale().getLanguage().equals(isoLang)) {
                this.availableIsoLanguages.add(locale.getLanguage());
                this.availableLocales.add(bundle.getLocale());
            }
        }

        Locale.setDefault(oldDefault);
    }

    private void initLafs() {
        LookAndFeelInfo[] lafs = UIManager.getInstalledLookAndFeels();
        this.availableLafs.clear();
        this.availableLafClassNames.clear();
        this.availableLafs.add(ResourceBundle.getBundle("intl/ConfigGeneralPanel").getString("GeneralConfigPanel.system_default"));
        this.availableLafClassNames.add("");

        for (int i = 0; i < lafs.length; i++) {
            this.availableLafs.add(lafs[i].getName());
            this.availableLafClassNames.add(lafs[i].getClassName());
        }
    }
}
