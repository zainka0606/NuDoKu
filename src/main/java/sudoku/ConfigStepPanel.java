package sudoku;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

public class ConfigStepPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static String[] fishTypes = new String[]{"Basic", "Basic/Franken", "Basic/Franken/Mutant"};
    private static String[] finSizes = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
    private static String[] fishSizes = new String[]{"2", "3", "4", "5", "6", "7"};
    private static String[] chainSizes = new String[]{
            "0",
            "1",
            "2",
            "3",
            "4",
            "5",
            "6",
            "7",
            "8",
            "9",
            "10",
            "11",
            "12",
            "13",
            "14",
            "15",
            "16",
            "17",
            "18",
            "19",
            "20",
            "21",
            "22",
            "23",
            "24",
            "25",
            "26",
            "27",
            "28",
            "29",
            "30",
            "31",
            "32",
            "33",
            "34",
            "35",
            "36",
            "37",
            "38",
            "39"
    };
    private static String[] fishDisplayTypes = new String[]{
            ResourceBundle.getBundle("intl/ConfigStepPanel").getString("ConfigStepPanel.fishDisplayTypeLabel.text1"),
            ResourceBundle.getBundle("intl/ConfigStepPanel").getString("ConfigStepPanel.fishDisplayTypeLabel.text2"),
            ResourceBundle.getBundle("intl/ConfigStepPanel").getString("ConfigStepPanel.fishDisplayTypeLabel.text3")
    };
    private JCheckBox allowAlsInTablingCheckBox;
    private JCheckBox allowAlsOverlapCheckBox;
    private JCheckBox allowDualsAndSiameseCheckBox;
    private JCheckBox allowMissingCandidatesCheckBox;
    private JPanel alsPanel;
    private JCheckBox checkTemplatesCheckBox;
    private JCheckBox erWithTwoCandidatesCheckBox;
    private JComboBox fishDisplayTypeComboBox;
    private JLabel fishDisplayTypeLabel;
    private JPanel fishPanel;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JPanel jPanel3;
    private JComboBox krakenFishMaxEndoFinsComboBox;
    private JLabel krakenFishMaxEndoFinsLabel;
    private JComboBox krakenFishMaxFinsComboBox;
    private JLabel krakenFishMaxFinsLabel;
    private JComboBox krakenFishMaxSizeComboBox;
    private JLabel krakenFishMaxSizeLabel;
    private JPanel krakenFishPanel;
    private JComboBox krakenFishTypeComboBox;
    private JLabel krakenFishTypeLabel;
    private JLabel lookAheadLabel;
    private JTextField lookAheadTextField;
    private JComboBox maxEndoFinsComboBox;
    private JLabel maxEndoFinsLabel;
    private JComboBox maxFinsComboBox;
    private JLabel maxFinsLabel;
    private JLabel maxTableEntryLengthLabel;
    private JTextField maxTableEntryLengthTextField;
    private JPanel miscellaneousPanel;
    private JCheckBox onlyOneAlsStepCheckBox;
    private JCheckBox onlyOneChainCheckBox;
    private JCheckBox onlyOneFishCheckBox;
    private JButton resetButton;
    private JCheckBox restrictChainSizeCheckBox;
    private JComboBox restrictChainSizeComboBox;
    private JLabel restrictChainSizeLabel;
    private JPanel tablingPanel;
    private JCheckBox useZeroInsteadOfDotCheckBox;

    public ConfigStepPanel() {
        this.initComponents();
        this.maxFinsComboBox.removeAllItems();
        this.maxEndoFinsComboBox.removeAllItems();
        this.krakenFishMaxFinsComboBox.removeAllItems();
        this.krakenFishMaxEndoFinsComboBox.removeAllItems();

        for (int i = 0; i < finSizes.length; i++) {
            this.maxFinsComboBox.addItem(finSizes[i]);
            this.maxEndoFinsComboBox.addItem(finSizes[i]);
            this.krakenFishMaxFinsComboBox.addItem(finSizes[i]);
            this.krakenFishMaxEndoFinsComboBox.addItem(finSizes[i]);
        }

        this.krakenFishTypeComboBox.removeAllItems();

        for (int i = 0; i < fishTypes.length; i++) {
            this.krakenFishTypeComboBox.addItem(fishTypes[i]);
        }

        this.krakenFishMaxSizeComboBox.removeAllItems();

        for (int i = 0; i < fishSizes.length; i++) {
            this.krakenFishMaxSizeComboBox.addItem(fishSizes[i]);
        }

        this.fishDisplayTypeComboBox.removeAllItems();

        for (int i = 0; i < fishDisplayTypes.length; i++) {
            this.fishDisplayTypeComboBox.addItem(fishDisplayTypes[i]);
        }

        for (int i = 0; i < chainSizes.length; i++) {
            this.restrictChainSizeComboBox.addItem(chainSizes[i]);
        }

        NumbersOnlyDocument doc = new NumbersOnlyDocument();
        this.maxTableEntryLengthTextField.setDocument(doc);
        doc = new NumbersOnlyDocument();
        this.lookAheadTextField.setDocument(doc);
        this.initAll(false);
    }

    private void initComponents() {
        this.jPanel2 = new JPanel();
        this.resetButton = new JButton();
        this.tablingPanel = new JPanel();
        this.maxTableEntryLengthLabel = new JLabel();
        this.lookAheadLabel = new JLabel();
        this.maxTableEntryLengthTextField = new JTextField();
        this.lookAheadTextField = new JTextField();
        this.onlyOneChainCheckBox = new JCheckBox();
        this.allowAlsInTablingCheckBox = new JCheckBox();
        this.miscellaneousPanel = new JPanel();
        this.useZeroInsteadOfDotCheckBox = new JCheckBox();
        this.erWithTwoCandidatesCheckBox = new JCheckBox();
        this.allowDualsAndSiameseCheckBox = new JCheckBox();
        this.allowMissingCandidatesCheckBox = new JCheckBox();
        this.alsPanel = new JPanel();
        this.allowAlsOverlapCheckBox = new JCheckBox();
        this.onlyOneAlsStepCheckBox = new JCheckBox();
        this.jPanel1 = new JPanel();
        this.fishPanel = new JPanel();
        this.maxFinsLabel = new JLabel();
        this.maxEndoFinsLabel = new JLabel();
        this.fishDisplayTypeLabel = new JLabel();
        this.maxFinsComboBox = new JComboBox();
        this.maxEndoFinsComboBox = new JComboBox();
        this.checkTemplatesCheckBox = new JCheckBox();
        this.onlyOneFishCheckBox = new JCheckBox();
        this.fishDisplayTypeComboBox = new JComboBox();
        this.jPanel3 = new JPanel();
        this.restrictChainSizeLabel = new JLabel();
        this.restrictChainSizeComboBox = new JComboBox();
        this.restrictChainSizeCheckBox = new JCheckBox();
        this.krakenFishPanel = new JPanel();
        this.krakenFishMaxEndoFinsComboBox = new JComboBox();
        this.krakenFishMaxEndoFinsLabel = new JLabel();
        this.krakenFishMaxFinsLabel = new JLabel();
        this.krakenFishMaxFinsComboBox = new JComboBox();
        this.krakenFishTypeComboBox = new JComboBox();
        this.krakenFishTypeLabel = new JLabel();
        this.krakenFishMaxSizeLabel = new JLabel();
        this.krakenFishMaxSizeComboBox = new JComboBox();
        this.resetButton.setMnemonic(ResourceBundle.getBundle("intl/ConfigStepPanel").getString("ConfigStepPanel.resetButton.mnemonic").charAt(0));
        ResourceBundle bundle = ResourceBundle.getBundle("intl/ConfigStepPanel");
        this.resetButton.setText(bundle.getString("ConfigStepPanel.resetButton.text"));
        this.resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigStepPanel.this.resetButtonActionPerformed(evt);
            }
        });
        this.tablingPanel.setBorder(BorderFactory.createTitledBorder(bundle.getString("ConfigStepPanel.tablingPanel.border.title")));
        this.maxTableEntryLengthLabel
                .setDisplayedMnemonic(ResourceBundle.getBundle("intl/ConfigStepPanel").getString("ConfigStepPanel.maxTableEntryLengthLabel.mnemonic").charAt(0));
        this.maxTableEntryLengthLabel.setLabelFor(this.maxTableEntryLengthTextField);
        this.maxTableEntryLengthLabel.setText(bundle.getString("ConfigStepPanel.maxTableEntryLengthLabel.text"));
        this.lookAheadLabel.setDisplayedMnemonic(ResourceBundle.getBundle("intl/ConfigStepPanel").getString("ConfigStepPanel.lookAheadLabel.mnemonic").charAt(0));
        this.lookAheadLabel.setLabelFor(this.lookAheadTextField);
        this.lookAheadLabel.setText(bundle.getString("ConfigStepPanel.lookAheadLabel.text"));
        this.onlyOneChainCheckBox
                .setMnemonic(ResourceBundle.getBundle("intl/ConfigStepPanel").getString("ConfigStepPanel.onlyOneChainCheckBox.mnemonic").charAt(0));
        this.onlyOneChainCheckBox.setText(bundle.getString("ConfigStepPanel.onlyOneChainCheckBox.text"));
        this.onlyOneChainCheckBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        this.onlyOneChainCheckBox.setMargin(new Insets(0, 0, 0, 0));
        this.allowAlsInTablingCheckBox.setMnemonic(ResourceBundle.getBundle("intl/ConfigStepPanel").getString("allowAlsInTablingCheckBox.mnemonic").charAt(0));
        this.allowAlsInTablingCheckBox.setText(bundle.getString("ConfigStepPanel.allowAlsInTablingCheckBox.text"));
        GroupLayout tablingPanelLayout = new GroupLayout(this.tablingPanel);
        this.tablingPanel.setLayout(tablingPanelLayout);
        tablingPanelLayout.setHorizontalGroup(
                tablingPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                tablingPanelLayout.createSequentialGroup()
                                        .addGroup(
                                                tablingPanelLayout.createParallelGroup(Alignment.LEADING)
                                                        .addGroup(
                                                                tablingPanelLayout.createSequentialGroup()
                                                                        .addContainerGap()
                                                                        .addGroup(
                                                                                tablingPanelLayout.createParallelGroup(Alignment.LEADING)
                                                                                        .addComponent(this.maxTableEntryLengthLabel)
                                                                                        .addComponent(this.lookAheadLabel)
                                                                        )
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addGroup(
                                                                                tablingPanelLayout.createParallelGroup(Alignment.LEADING)
                                                                                        .addComponent(this.lookAheadTextField, -1, 158, 32767)
                                                                                        .addComponent(this.maxTableEntryLengthTextField, -1, 158, 32767)
                                                                        )
                                                        )
                                                        .addGroup(tablingPanelLayout.createSequentialGroup().addGap(10, 10, 10).addComponent(this.onlyOneChainCheckBox))
                                                        .addGroup(tablingPanelLayout.createSequentialGroup().addContainerGap().addComponent(this.allowAlsInTablingCheckBox))
                                        )
                                        .addContainerGap()
                        )
        );
        tablingPanelLayout.setVerticalGroup(
                tablingPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                tablingPanelLayout.createSequentialGroup()
                                        .addGroup(
                                                tablingPanelLayout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(this.maxTableEntryLengthLabel)
                                                        .addComponent(this.maxTableEntryLengthTextField, -2, -1, -2)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                tablingPanelLayout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(this.lookAheadLabel)
                                                        .addComponent(this.lookAheadTextField, -2, -1, -2)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.onlyOneChainCheckBox)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.allowAlsInTablingCheckBox)
                        )
        );
        this.miscellaneousPanel.setBorder(BorderFactory.createTitledBorder(bundle.getString("ConfigStepPanel.miscellaneousPanel.border.title")));
        this.useZeroInsteadOfDotCheckBox
                .setMnemonic(ResourceBundle.getBundle("intl/ConfigStepPanel").getString("ConfigStepPanel.useZeroInsteadOfDotCheckBox.mnemonic").charAt(0));
        this.useZeroInsteadOfDotCheckBox.setText(bundle.getString("ConfigStepPanel.useZeroInsteadOfDotCheckBox.text"));
        this.erWithTwoCandidatesCheckBox
                .setMnemonic(ResourceBundle.getBundle("intl/ConfigStepPanel").getString("ConfigStepPanel.erWithTwoCandidatesCheckBox.mnemonic").charAt(0));
        this.erWithTwoCandidatesCheckBox.setText(bundle.getString("ConfigStepPanel.erWithTwoCandidatesCheckBox.text"));
        this.allowDualsAndSiameseCheckBox
                .setMnemonic(ResourceBundle.getBundle("intl/ConfigStepPanel").getString("ConfigStepPanel.allowDualsAndSiameseCheckBoxMnemonic").charAt(0));
        this.allowDualsAndSiameseCheckBox.setText(bundle.getString("ConfigStepPanel.allowDualsAndSiameseCheckBox.text"));
        this.allowMissingCandidatesCheckBox
                .setMnemonic(ResourceBundle.getBundle("intl/ConfigStepPanel").getString("ConfigStepPanel.allowMissingCandidatesCheckBox.mnemonic").charAt(0));
        this.allowMissingCandidatesCheckBox.setText(bundle.getString("ConfigStepPanel.allowMissingCandidatesCheckBox.text"));
        GroupLayout miscellaneousPanelLayout = new GroupLayout(this.miscellaneousPanel);
        this.miscellaneousPanel.setLayout(miscellaneousPanelLayout);
        miscellaneousPanelLayout.setHorizontalGroup(
                miscellaneousPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                miscellaneousPanelLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                miscellaneousPanelLayout.createParallelGroup(Alignment.LEADING)
                                                        .addComponent(this.erWithTwoCandidatesCheckBox)
                                                        .addComponent(this.useZeroInsteadOfDotCheckBox)
                                                        .addComponent(this.allowDualsAndSiameseCheckBox)
                                                        .addComponent(this.allowMissingCandidatesCheckBox)
                                        )
                                        .addContainerGap(53, 32767)
                        )
        );
        miscellaneousPanelLayout.setVerticalGroup(
                miscellaneousPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                miscellaneousPanelLayout.createSequentialGroup()
                                        .addComponent(this.erWithTwoCandidatesCheckBox)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.useZeroInsteadOfDotCheckBox)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.allowDualsAndSiameseCheckBox)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.allowMissingCandidatesCheckBox)
                                        .addContainerGap(-1, 32767)
                        )
        );
        this.alsPanel.setBorder(BorderFactory.createTitledBorder(bundle.getString("alsPanel.border.Text")));
        this.allowAlsOverlapCheckBox.setMnemonic(ResourceBundle.getBundle("intl/ConfigStepPanel").getString("allowAlsOverlapCheckBox.mnemonic").charAt(0));
        this.allowAlsOverlapCheckBox.setText(bundle.getString("ConfigStepPanel.allowAlsOverlapCheckBox.text"));
        this.onlyOneAlsStepCheckBox
                .setMnemonic(ResourceBundle.getBundle("intl/ConfigStepPanel").getString("ConfigStepPanel.onlyOneAlsStepCheckBox.mnemonic").charAt(0));
        this.onlyOneAlsStepCheckBox.setText(bundle.getString("ConfigStepPanel.onlyOneAlsStepCheckBox.text"));
        GroupLayout alsPanelLayout = new GroupLayout(this.alsPanel);
        this.alsPanel.setLayout(alsPanelLayout);
        alsPanelLayout.setHorizontalGroup(
                alsPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                alsPanelLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                alsPanelLayout.createParallelGroup(Alignment.LEADING).addComponent(this.allowAlsOverlapCheckBox).addComponent(this.onlyOneAlsStepCheckBox)
                                        )
                                        .addContainerGap(63, 32767)
                        )
        );
        alsPanelLayout.setVerticalGroup(
                alsPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                alsPanelLayout.createSequentialGroup()
                                        .addComponent(this.allowAlsOverlapCheckBox)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.onlyOneAlsStepCheckBox)
                        )
        );
        GroupLayout jPanel2Layout = new GroupLayout(this.jPanel2);
        this.jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                Alignment.TRAILING,
                                jPanel2Layout.createSequentialGroup()
                                        .addGroup(
                                                jPanel2Layout.createParallelGroup(Alignment.TRAILING)
                                                        .addGroup(jPanel2Layout.createSequentialGroup().addContainerGap(133, 32767).addComponent(this.resetButton))
                                                        .addComponent(this.alsPanel, Alignment.LEADING, -1, -1, 32767)
                                                        .addComponent(this.miscellaneousPanel, Alignment.LEADING, -1, -1, 32767)
                                                        .addComponent(this.tablingPanel, Alignment.LEADING, -1, -1, 32767)
                                        )
                                        .addContainerGap()
                        )
        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                Alignment.TRAILING,
                                jPanel2Layout.createSequentialGroup()
                                        .addComponent(this.tablingPanel, -2, -1, -2)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.miscellaneousPanel, -2, -1, -2)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.alsPanel, -2, -1, -2)
                                        .addPreferredGap(ComponentPlacement.RELATED, 80, 32767)
                                        .addComponent(this.resetButton)
                                        .addContainerGap()
                        )
        );
        this.fishPanel.setBorder(BorderFactory.createTitledBorder(bundle.getString("ConfigStepPanel.fishPanel.border.title")));
        this.maxFinsLabel.setDisplayedMnemonic(ResourceBundle.getBundle("intl/ConfigStepPanel").getString("ConfigStepPanel.maxFinsLabel.mnemonic").charAt(0));
        this.maxFinsLabel.setText(bundle.getString("ConfigStepPanel.maxFinsLabel.text"));
        this.maxEndoFinsLabel
                .setDisplayedMnemonic(ResourceBundle.getBundle("intl/ConfigStepPanel").getString("ConfigStepPanel.maxEndoFinsLabel.mnemonic").charAt(0));
        this.maxEndoFinsLabel.setText(bundle.getString("ConfigStepPanel.maxEndoFinsLabel.text"));
        this.fishDisplayTypeLabel
                .setDisplayedMnemonic(ResourceBundle.getBundle("intl/ConfigStepPanel").getString("ConfigStepPanel.fishDisplayTypeLabel.mnemonic").charAt(0));
        this.fishDisplayTypeLabel.setLabelFor(this.fishDisplayTypeComboBox);
        this.fishDisplayTypeLabel.setText(bundle.getString("ConfigStepPanel.fishDisplayTypeLabel.text"));
        this.checkTemplatesCheckBox
                .setMnemonic(ResourceBundle.getBundle("intl/ConfigStepPanel").getString("ConfigStepPanel.checkTemplatesCheckBox.mnemonic").charAt(0));
        this.checkTemplatesCheckBox.setText(bundle.getString("ConfigStepPanel.checkTemplatesCheckBox.text"));
        this.checkTemplatesCheckBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        this.checkTemplatesCheckBox.setMargin(new Insets(0, 0, 0, 0));
        this.onlyOneFishCheckBox
                .setMnemonic(ResourceBundle.getBundle("intl/ConfigStepPanel").getString("ConfigStepPanel.onlyOneFishCheckBox.mnemonic").charAt(0));
        this.onlyOneFishCheckBox.setText(bundle.getString("ConfigStepPanel.onlyOneFishCheckBox.text"));
        this.fishDisplayTypeComboBox.setModel(new DefaultComboBoxModel<>(new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}));
        GroupLayout fishPanelLayout = new GroupLayout(this.fishPanel);
        this.fishPanel.setLayout(fishPanelLayout);
        fishPanelLayout.setHorizontalGroup(
                fishPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                fishPanelLayout.createSequentialGroup()
                                        .addGroup(
                                                fishPanelLayout.createParallelGroup(Alignment.LEADING)
                                                        .addGroup(
                                                                fishPanelLayout.createSequentialGroup()
                                                                        .addGroup(
                                                                                fishPanelLayout.createParallelGroup(Alignment.LEADING)
                                                                                        .addGroup(
                                                                                                fishPanelLayout.createSequentialGroup()
                                                                                                        .addGap(10, 10, 10)
                                                                                                        .addGroup(
                                                                                                                fishPanelLayout.createParallelGroup(Alignment.LEADING)
                                                                                                                        .addComponent(this.maxEndoFinsLabel, -1, 125, 32767)
                                                                                                                        .addGroup(
                                                                                                                                fishPanelLayout.createSequentialGroup().addComponent(this.maxFinsLabel, -1, 98, 32767).addGap(27, 27, 27)
                                                                                                                        )
                                                                                                        )
                                                                                                        .addPreferredGap(ComponentPlacement.UNRELATED)
                                                                                        )
                                                                                        .addGroup(
                                                                                                fishPanelLayout.createSequentialGroup().addContainerGap().addComponent(this.fishDisplayTypeLabel).addGap(72, 72, 72)
                                                                                        )
                                                                        )
                                                                        .addGroup(
                                                                                fishPanelLayout.createParallelGroup(Alignment.LEADING)
                                                                                        .addComponent(this.maxFinsComboBox, 0, 59, 32767)
                                                                                        .addComponent(this.maxEndoFinsComboBox, 0, 59, 32767)
                                                                                        .addComponent(this.fishDisplayTypeComboBox, 0, 59, 32767)
                                                                        )
                                                        )
                                                        .addGroup(
                                                                fishPanelLayout.createSequentialGroup()
                                                                        .addContainerGap()
                                                                        .addGroup(
                                                                                fishPanelLayout.createParallelGroup(Alignment.LEADING)
                                                                                        .addGroup(fishPanelLayout.createSequentialGroup().addGap(4, 4, 4).addComponent(this.checkTemplatesCheckBox))
                                                                                        .addComponent(this.onlyOneFishCheckBox)
                                                                        )
                                                        )
                                        )
                                        .addContainerGap()
                        )
        );
        fishPanelLayout.setVerticalGroup(
                fishPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                fishPanelLayout.createSequentialGroup()
                                        .addGroup(
                                                fishPanelLayout.createParallelGroup(Alignment.BASELINE).addComponent(this.maxFinsLabel).addComponent(this.maxFinsComboBox, -2, -1, -2)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                fishPanelLayout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(this.maxEndoFinsLabel)
                                                        .addComponent(this.maxEndoFinsComboBox, -2, -1, -2)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED, -1, 32767)
                                        .addGroup(
                                                fishPanelLayout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(this.fishDisplayTypeLabel)
                                                        .addComponent(this.fishDisplayTypeComboBox, -2, -1, -2)
                                        )
                                        .addGap(14, 14, 14)
                                        .addComponent(this.checkTemplatesCheckBox)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.onlyOneFishCheckBox)
                                        .addContainerGap()
                        )
        );
        this.jPanel3.setBorder(BorderFactory.createTitledBorder(bundle.getString("ConfigStepPanel.jPanel3.border.title")));
        this.restrictChainSizeLabel
                .setDisplayedMnemonic(ResourceBundle.getBundle("intl/ConfigStepPanel").getString("ConfigStepPanel.restrictChainSizeLabel.mnemonic").charAt(0));
        this.restrictChainSizeLabel.setText(bundle.getString("ConfigStepPanel.restrictChainSizeLabel.text"));
        this.restrictChainSizeCheckBox
                .setMnemonic(ResourceBundle.getBundle("intl/ConfigStepPanel").getString("ConfigStepPanel.restrictChainSizeCheckBox.mnemonic").charAt(0));
        this.restrictChainSizeCheckBox.setText(bundle.getString("ConfigStepPanel.restrictChainSizeCheckBox.text"));
        this.restrictChainSizeCheckBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        this.restrictChainSizeCheckBox.setMargin(new Insets(0, 0, 0, 0));
        GroupLayout jPanel3Layout = new GroupLayout(this.jPanel3);
        this.jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                jPanel3Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                jPanel3Layout.createParallelGroup(Alignment.LEADING)
                                                        .addComponent(this.restrictChainSizeCheckBox)
                                                        .addGroup(
                                                                jPanel3Layout.createSequentialGroup()
                                                                        .addComponent(this.restrictChainSizeLabel, -1, 114, 32767)
                                                                        .addPreferredGap(ComponentPlacement.UNRELATED)
                                                                        .addComponent(this.restrictChainSizeComboBox, 0, 70, 32767)
                                                        )
                                        )
                                        .addContainerGap()
                        )
        );
        jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                jPanel3Layout.createSequentialGroup()
                                        .addGroup(
                                                jPanel3Layout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(this.restrictChainSizeLabel)
                                                        .addComponent(this.restrictChainSizeComboBox, -2, -1, -2)
                                        )
                                        .addPreferredGap(ComponentPlacement.UNRELATED)
                                        .addComponent(this.restrictChainSizeCheckBox)
                                        .addContainerGap(-1, 32767)
                        )
        );
        this.krakenFishPanel.setBorder(BorderFactory.createTitledBorder(bundle.getString("krakenFishPanel.border.Text")));
        this.krakenFishMaxEndoFinsComboBox.setModel(new DefaultComboBoxModel<>(new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}));
        this.krakenFishMaxEndoFinsLabel
                .setDisplayedMnemonic(ResourceBundle.getBundle("intl/ConfigStepPanel").getString("krakenFishMaxEndoFinsLabel.mnemonic").charAt(0));
        this.krakenFishMaxEndoFinsLabel.setLabelFor(this.krakenFishMaxEndoFinsLabel);
        this.krakenFishMaxEndoFinsLabel.setText(bundle.getString("ConfigStepPanel.krakenFishMaxEndoFinsLabel.text"));
        this.krakenFishMaxFinsLabel.setDisplayedMnemonic(ResourceBundle.getBundle("intl/ConfigStepPanel").getString("krakenFishMaxFinsLabel.mnemonic").charAt(0));
        this.krakenFishMaxFinsLabel.setLabelFor(this.krakenFishMaxFinsComboBox);
        this.krakenFishMaxFinsLabel.setText(bundle.getString("ConfigStepPanel.krakenFishMaxFinsLabel.text"));
        this.krakenFishMaxFinsComboBox.setModel(new DefaultComboBoxModel<>(new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}));
        this.krakenFishTypeComboBox.setModel(new DefaultComboBoxModel<>(new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}));
        this.krakenFishTypeLabel.setDisplayedMnemonic(ResourceBundle.getBundle("intl/ConfigStepPanel").getString("krakenFishTypeLabel.mnemonic").charAt(0));
        this.krakenFishTypeLabel.setLabelFor(this.krakenFishTypeComboBox);
        this.krakenFishTypeLabel.setText(bundle.getString("ConfigStepPanel.krakenFishTypeLabel.text"));
        this.krakenFishMaxSizeLabel.setDisplayedMnemonic(ResourceBundle.getBundle("intl/ConfigStepPanel").getString("krakenFishMaxSizeLabel.mnemonic").charAt(0));
        this.krakenFishMaxSizeLabel.setLabelFor(this.krakenFishMaxSizeComboBox);
        this.krakenFishMaxSizeLabel.setText(bundle.getString("ConfigStepPanel.krakenFishMaxSizeLabel.text"));
        this.krakenFishMaxSizeComboBox.setModel(new DefaultComboBoxModel<>(new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}));
        GroupLayout krakenFishPanelLayout = new GroupLayout(this.krakenFishPanel);
        this.krakenFishPanel.setLayout(krakenFishPanelLayout);
        krakenFishPanelLayout.setHorizontalGroup(
                krakenFishPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                krakenFishPanelLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                krakenFishPanelLayout.createParallelGroup(Alignment.LEADING)
                                                        .addGroup(
                                                                krakenFishPanelLayout.createSequentialGroup()
                                                                        .addGroup(
                                                                                krakenFishPanelLayout.createParallelGroup(Alignment.LEADING)
                                                                                        .addGroup(
                                                                                                krakenFishPanelLayout.createSequentialGroup().addComponent(this.krakenFishMaxFinsLabel, -1, 99, 32767).addGap(7, 7, 7)
                                                                                        )
                                                                                        .addComponent(this.krakenFishMaxEndoFinsLabel, -1, 106, 32767)
                                                                                        .addComponent(this.krakenFishTypeLabel, -1, 106, 32767)
                                                                        )
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                        )
                                                        .addGroup(krakenFishPanelLayout.createSequentialGroup().addComponent(this.krakenFishMaxSizeLabel).addGap(36, 36, 36))
                                        )
                                        .addGroup(
                                                krakenFishPanelLayout.createParallelGroup(Alignment.LEADING)
                                                        .addComponent(this.krakenFishMaxSizeComboBox, 0, 84, 32767)
                                                        .addComponent(this.krakenFishMaxEndoFinsComboBox, 0, 84, 32767)
                                                        .addComponent(this.krakenFishMaxFinsComboBox, 0, 84, 32767)
                                                        .addComponent(this.krakenFishTypeComboBox, 0, 84, 32767)
                                        )
                                        .addContainerGap()
                        )
        );
        krakenFishPanelLayout.setVerticalGroup(
                krakenFishPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                krakenFishPanelLayout.createSequentialGroup()
                                        .addGroup(
                                                krakenFishPanelLayout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(this.krakenFishTypeLabel)
                                                        .addComponent(this.krakenFishTypeComboBox, -2, -1, -2)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                krakenFishPanelLayout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(this.krakenFishMaxFinsLabel)
                                                        .addComponent(this.krakenFishMaxFinsComboBox, -2, -1, -2)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                krakenFishPanelLayout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(this.krakenFishMaxEndoFinsLabel)
                                                        .addComponent(this.krakenFishMaxEndoFinsComboBox, -2, -1, -2)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                krakenFishPanelLayout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(this.krakenFishMaxSizeLabel)
                                                        .addComponent(this.krakenFishMaxSizeComboBox, -2, -1, -2)
                                        )
                                        .addContainerGap(-1, 32767)
                        )
        );
        GroupLayout jPanel1Layout = new GroupLayout(this.jPanel1);
        this.jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                jPanel1Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                jPanel1Layout.createParallelGroup(Alignment.LEADING)
                                                        .addComponent(this.fishPanel, -1, -1, 32767)
                                                        .addComponent(this.krakenFishPanel, -1, -1, 32767)
                                                        .addComponent(this.jPanel3, -1, -1, 32767)
                                        )
                                        .addContainerGap()
                        )
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                jPanel1Layout.createSequentialGroup()
                                        .addComponent(this.fishPanel, -2, -1, -2)
                                        .addGap(1, 1, 1)
                                        .addComponent(this.krakenFishPanel, -2, -1, -2)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.jPanel3, -2, -1, -2)
                                        .addContainerGap(57, 32767)
                        )
        );
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addComponent(this.jPanel1, -1, -1, 32767)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.jPanel2, -1, -1, 32767)
                        )
        );
        layout.setVerticalGroup(layout.createParallelGroup(Alignment.LEADING).addComponent(this.jPanel1, -1, -1, 32767).addComponent(this.jPanel2, -1, -1, 32767));
    }

    private void resetButtonActionPerformed(ActionEvent evt) {
        this.initAll(true);
    }

    public void okPressed() {
        Options.getInstance().setMaxFins(Integer.parseInt((String) this.maxFinsComboBox.getSelectedItem()));
        Options.getInstance().setMaxEndoFins(Integer.parseInt((String) this.maxEndoFinsComboBox.getSelectedItem()));
        Options.getInstance().setCheckTemplates(this.checkTemplatesCheckBox.isSelected());
        Options.getInstance().setOnlyOneFishPerStep(this.onlyOneFishCheckBox.isSelected());
        Options.getInstance().setFishDisplayMode(this.fishDisplayTypeComboBox.getSelectedIndex());
        Options.getInstance().setRestrictChainLength(Integer.parseInt((String) this.restrictChainSizeComboBox.getSelectedItem()));
        Options.getInstance().setRestrictChainSize(this.restrictChainSizeCheckBox.isSelected());
        Options.getInstance().setMaxTableEntryLength(Integer.parseInt(this.maxTableEntryLengthTextField.getText()));
        Options.getInstance().setAnzTableLookAhead(Integer.parseInt(this.lookAheadTextField.getText()));
        Options.getInstance().setOnlyOneChainPerStep(this.onlyOneChainCheckBox.isSelected());
        Options.getInstance().setAllowAlsInTablingChains(this.allowAlsInTablingCheckBox.isSelected());
        Options.getInstance().setUseZeroInsteadOfDot(this.useZeroInsteadOfDotCheckBox.isSelected());
        Options.getInstance().setAllowErsWithOnlyTwoCandidates(this.erWithTwoCandidatesCheckBox.isSelected());
        Options.getInstance().setAllowDualsAndSiamese(this.allowDualsAndSiameseCheckBox.isSelected());
        Options.getInstance().setAllowUniquenessMissingCandidates(this.allowMissingCandidatesCheckBox.isSelected());
        Options.getInstance().setKrakenMaxFishType(this.krakenFishTypeComboBox.getSelectedIndex());
        Options.getInstance().setKrakenMaxFishSize(this.krakenFishMaxSizeComboBox.getSelectedIndex() + 2);
        Options.getInstance().setMaxKrakenFins(this.krakenFishMaxFinsComboBox.getSelectedIndex());
        Options.getInstance().setMaxKrakenEndoFins(this.krakenFishMaxEndoFinsComboBox.getSelectedIndex());
        Options.getInstance().setAllowAlsOverlap(this.allowAlsOverlapCheckBox.isSelected());
        Options.getInstance().setOnlyOneAlsPerStep(this.onlyOneAlsStepCheckBox.isSelected());
    }

    private void initAll(boolean setDefault) {
        if (setDefault) {
            this.maxFinsComboBox.setSelectedIndex(5);
            this.maxEndoFinsComboBox.setSelectedIndex(2);
            this.checkTemplatesCheckBox.setSelected(true);
            this.onlyOneFishCheckBox.setSelected(true);
            this.fishDisplayTypeComboBox.setSelectedIndex(0);
            this.restrictChainSizeComboBox.setSelectedIndex(20);
            this.restrictChainSizeCheckBox.setSelected(true);
            this.maxTableEntryLengthTextField.setText(Integer.toString(1000));
            this.lookAheadTextField.setText(Integer.toString(4));
            this.onlyOneChainCheckBox.setSelected(true);
            this.allowAlsInTablingCheckBox.setSelected(false);
            this.useZeroInsteadOfDotCheckBox.setSelected(false);
            this.erWithTwoCandidatesCheckBox.setSelected(false);
            this.allowDualsAndSiameseCheckBox.setSelected(false);
            this.allowMissingCandidatesCheckBox.setSelected(true);
            this.krakenFishTypeComboBox.setSelectedIndex(1);
            this.krakenFishMaxSizeComboBox.setSelectedIndex(2);
            this.krakenFishMaxFinsComboBox.setSelectedIndex(2);
            this.krakenFishMaxEndoFinsComboBox.setSelectedIndex(0);
            this.allowAlsOverlapCheckBox.setSelected(false);
            this.onlyOneAlsStepCheckBox.setSelected(true);
        } else {
            this.maxFinsComboBox.setSelectedIndex(Options.getInstance().getMaxFins());
            this.maxEndoFinsComboBox.setSelectedIndex(Options.getInstance().getMaxEndoFins());
            this.checkTemplatesCheckBox.setSelected(Options.getInstance().isCheckTemplates());
            this.onlyOneFishCheckBox.setSelected(Options.getInstance().isOnlyOneFishPerStep());
            this.fishDisplayTypeComboBox.setSelectedIndex(Options.getInstance().getFishDisplayMode());
            this.restrictChainSizeComboBox.setSelectedIndex(Options.getInstance().getRestrictChainLength());
            this.restrictChainSizeCheckBox.setSelected(Options.getInstance().isRestrictChainSize());
            this.maxTableEntryLengthTextField.setText(Integer.toString(Options.getInstance().getMaxTableEntryLength()));
            this.lookAheadTextField.setText(Integer.toString(Options.getInstance().getAnzTableLookAhead()));
            this.onlyOneChainCheckBox.setSelected(Options.getInstance().isOnlyOneChainPerStep());
            this.allowAlsInTablingCheckBox.setSelected(Options.getInstance().isAllowAlsInTablingChains());
            this.useZeroInsteadOfDotCheckBox.setSelected(Options.getInstance().isUseZeroInsteadOfDot());
            this.erWithTwoCandidatesCheckBox.setSelected(Options.getInstance().isAllowErsWithOnlyTwoCandidates());
            this.allowDualsAndSiameseCheckBox.setSelected(Options.getInstance().isAllowDualsAndSiamese());
            this.allowMissingCandidatesCheckBox.setSelected(Options.getInstance().isAllowUniquenessMissingCandidates());
            this.krakenFishTypeComboBox.setSelectedIndex(Options.getInstance().getKrakenMaxFishType());
            this.krakenFishMaxSizeComboBox.setSelectedIndex(Options.getInstance().getKrakenMaxFishSize() - 2);
            this.krakenFishMaxFinsComboBox.setSelectedIndex(Options.getInstance().getMaxKrakenFins());
            this.krakenFishMaxEndoFinsComboBox.setSelectedIndex(Options.getInstance().getMaxKrakenEndoFins());
            this.allowAlsOverlapCheckBox.setSelected(Options.getInstance().isAllowAlsOverlap());
            this.onlyOneAlsStepCheckBox.setSelected(Options.getInstance().isOnlyOneAlsPerStep());
        }
    }
}
