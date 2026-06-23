package sudoku;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.ResourceBundle;

public class ConfigFindAllStepsPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static String[] fishSizes = new String[]{"2", "3", "4", "5", "6", "7"};
    private static String[] finSizes = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
    private static String[] fishTypes = new String[]{"Basic", "Basic/Franken", "Basic/Franken/Mutant"};
    private static String[] alsChainLengths = new String[]{"4", "5", "6", "7"};
    private StepConfig[] steps;
    private String fishCandidates;
    private String krakenFishCandidates;
    private JCheckBox alsBiDirCheckBox;
    private JComboBox alsChainLengthComboBox;
    private JLabel alsChainLengthLabel;
    private JPanel alsPanel;
    private JButton fishCandidatesButton;
    private JLabel fishCandidatesLabel;
    private JLabel fishCandidatesResultLabel;
    private JCheckBox fishCheckBox;
    private JCheckBox fishCheckTemplatesCheckBox;
    private JComboBox fishFromComboBox;
    private JLabel fishFromLabel;
    private JComboBox fishMaxEndoFinsComboBox;
    private JLabel fishMaxEndoFinsLabel;
    private JComboBox fishMaxFinsComboBox;
    private JLabel fishMaxFinsLabel;
    private JPanel fishPanel;
    private JComboBox fishToComboBox;
    private JLabel fishToLabel;
    private JComboBox fishTypeComboBox;
    private JLabel fishTypeLabel;
    private JPanel jPanel1;
    private JScrollPane jScrollPane1;
    private JButton krakenFishCandidatesButton;
    private JLabel krakenFishCandidatesLabel;
    private JLabel krakenFishCandidatesResultLabel;
    private JComboBox krakenFishFromComboBox;
    private JLabel krakenFishFromLabel;
    private JComboBox krakenFishMaxEndoFinsComboBox;
    private JLabel krakenFishMaxEndoFinsLabel;
    private JComboBox krakenFishMaxFinsComboBox;
    private JLabel krakenFishMaxFinsLabel;
    private JPanel krakenFishPanel;
    private JComboBox krakenFishToComboBox;
    private JLabel krakenFishToLabel;
    private JComboBox krakenFishTypeComboBox;
    private JLabel krakenFishTypeLabel;
    private JButton resetButton;
    private JTree stepTree;

    public ConfigFindAllStepsPanel() {
        this.initComponents();
        this.stepTree.setCellRenderer(new CheckRenderer());
        this.stepTree.getSelectionModel().setSelectionMode(1);
        this.stepTree.putClientProperty("JTree.lineStyle", "Angled");
        this.fishFromComboBox.removeAllItems();
        this.fishToComboBox.removeAllItems();
        this.krakenFishFromComboBox.removeAllItems();
        this.krakenFishToComboBox.removeAllItems();

        for (int i = 0; i < fishSizes.length; i++) {
            this.fishFromComboBox.addItem(fishSizes[i]);
            this.fishToComboBox.addItem(fishSizes[i]);
            this.krakenFishFromComboBox.addItem(fishSizes[i]);
            this.krakenFishToComboBox.addItem(fishSizes[i]);
        }

        this.fishMaxFinsComboBox.removeAllItems();
        this.fishMaxEndoFinsComboBox.removeAllItems();
        this.krakenFishMaxFinsComboBox.removeAllItems();
        this.krakenFishMaxEndoFinsComboBox.removeAllItems();

        for (int i = 0; i < finSizes.length; i++) {
            this.fishMaxFinsComboBox.addItem(finSizes[i]);
            this.fishMaxEndoFinsComboBox.addItem(finSizes[i]);
            this.krakenFishMaxFinsComboBox.addItem(finSizes[i]);
            this.krakenFishMaxEndoFinsComboBox.addItem(finSizes[i]);
        }

        this.fishTypeComboBox.removeAllItems();
        this.krakenFishTypeComboBox.removeAllItems();

        for (int i = 0; i < fishTypes.length; i++) {
            this.fishTypeComboBox.addItem(fishTypes[i]);
            this.krakenFishTypeComboBox.addItem(fishTypes[i]);
        }

        this.alsChainLengthComboBox.removeAllItems();

        for (int i = 0; i < alsChainLengths.length; i++) {
            this.alsChainLengthComboBox.addItem(alsChainLengths[i]);
        }

        this.initAll(false);
    }

    private void initComponents() {
        this.resetButton = new JButton();
        this.jPanel1 = new JPanel();
        this.jScrollPane1 = new JScrollPane();
        this.stepTree = new JTree();
        this.fishPanel = new JPanel();
        this.fishCheckBox = new JCheckBox();
        this.fishTypeLabel = new JLabel();
        this.fishTypeComboBox = new JComboBox();
        this.fishFromLabel = new JLabel();
        this.fishFromComboBox = new JComboBox();
        this.fishToLabel = new JLabel();
        this.fishToComboBox = new JComboBox();
        this.fishMaxFinsLabel = new JLabel();
        this.fishMaxFinsComboBox = new JComboBox();
        this.fishMaxEndoFinsLabel = new JLabel();
        this.fishMaxEndoFinsComboBox = new JComboBox();
        this.fishCheckTemplatesCheckBox = new JCheckBox();
        this.fishCandidatesLabel = new JLabel();
        this.fishCandidatesResultLabel = new JLabel();
        this.fishCandidatesButton = new JButton();
        this.krakenFishPanel = new JPanel();
        this.krakenFishMaxEndoFinsComboBox = new JComboBox();
        this.krakenFishMaxEndoFinsLabel = new JLabel();
        this.krakenFishMaxFinsLabel = new JLabel();
        this.krakenFishMaxFinsComboBox = new JComboBox();
        this.krakenFishFromLabel = new JLabel();
        this.krakenFishFromComboBox = new JComboBox();
        this.krakenFishToLabel = new JLabel();
        this.krakenFishToComboBox = new JComboBox();
        this.krakenFishTypeComboBox = new JComboBox();
        this.krakenFishTypeLabel = new JLabel();
        this.krakenFishCandidatesLabel = new JLabel();
        this.krakenFishCandidatesResultLabel = new JLabel();
        this.krakenFishCandidatesButton = new JButton();
        this.alsPanel = new JPanel();
        this.alsChainLengthLabel = new JLabel();
        this.alsChainLengthComboBox = new JComboBox();
        this.alsBiDirCheckBox = new JCheckBox();
        this.resetButton
                .setMnemonic(ResourceBundle.getBundle("intl/ConfigFindAllStepsPanel").getString("ConfigFindAllStepsPanel.resetButton.mnemonic").charAt(0));
        ResourceBundle bundle = ResourceBundle.getBundle("intl/ConfigFindAllStepsPanel");
        this.resetButton.setText(bundle.getString("ConfigFindAllStepsPanel.resetButton.text"));
        this.resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigFindAllStepsPanel.this.resetButtonActionPerformed(evt);
            }
        });
        this.stepTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                ConfigFindAllStepsPanel.this.stepTreeMousePressed(evt);
            }
        });
        this.jScrollPane1.setViewportView(this.stepTree);
        this.fishPanel.setBorder(BorderFactory.createTitledBorder(bundle.getString("ConfigFindAllStepsPanel.fishPanel.border")));
        this.fishCheckBox
                .setMnemonic(ResourceBundle.getBundle("intl/ConfigFindAllStepsPanel").getString("ConfigFindAllStepsPanel.fishCheckBox.mnemonic").charAt(0));
        this.fishCheckBox.setText(bundle.getString("ConfigFindAllStepsPanel.fishCheckBox.text"));
        this.fishTypeLabel
                .setDisplayedMnemonic(ResourceBundle.getBundle("intl/ConfigFindAllStepsPanel").getString("ConfigFindAllStepsPanel.fishTypeLabel.mnemonic").charAt(0));
        this.fishTypeLabel.setLabelFor(this.fishTypeComboBox);
        this.fishTypeLabel.setText(bundle.getString("ConfigFindAllStepsPanel.fishTypeLabel.text"));
        this.fishTypeComboBox.setModel(new DefaultComboBoxModel<>(new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}));
        this.fishFromLabel
                .setDisplayedMnemonic(ResourceBundle.getBundle("intl/ConfigFindAllStepsPanel").getString("ConfigFindAllStepsPanel.fishFromLabel.mnemonic").charAt(0));
        this.fishFromLabel.setLabelFor(this.fishFromComboBox);
        this.fishFromLabel.setText(bundle.getString("ConfigFindAllStepsPanel.fishFromLabel.text"));
        this.fishFromComboBox.setModel(new DefaultComboBoxModel<>(new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}));
        this.fishToLabel
                .setDisplayedMnemonic(ResourceBundle.getBundle("intl/ConfigFindAllStepsPanel").getString("ConfigFindAllStepsPanel.fishToLabel.mnemonic").charAt(0));
        this.fishToLabel.setLabelFor(this.fishToComboBox);
        this.fishToLabel.setText(bundle.getString("ConfigFindAllStepsPanel.fishToLabel.text"));
        this.fishToComboBox.setModel(new DefaultComboBoxModel<>(new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}));
        this.fishMaxFinsLabel
                .setDisplayedMnemonic(
                        ResourceBundle.getBundle("intl/ConfigFindAllStepsPanel").getString("ConfigFindAllStepsPanel.fishMaxFinsLabel.mnemonic").charAt(0)
                );
        this.fishMaxFinsLabel.setLabelFor(this.fishMaxFinsComboBox);
        this.fishMaxFinsLabel.setText(bundle.getString("ConfigFindAllStepsPanel.fishMaxFinsLabel.text"));
        this.fishMaxFinsComboBox.setModel(new DefaultComboBoxModel<>(new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}));
        this.fishMaxEndoFinsLabel
                .setDisplayedMnemonic(
                        ResourceBundle.getBundle("intl/ConfigFindAllStepsPanel").getString("ConfigFindAllStepsPanel.fishMaxEndoFinsLabel.mnemonic").charAt(0)
                );
        this.fishMaxEndoFinsLabel.setLabelFor(this.fishMaxEndoFinsComboBox);
        this.fishMaxEndoFinsLabel.setText(bundle.getString("ConfigFindAllStepsPanel.fishMaxEndoFinsLabel.text"));
        this.fishMaxEndoFinsComboBox.setModel(new DefaultComboBoxModel<>(new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}));
        this.fishCheckTemplatesCheckBox
                .setMnemonic(
                        ResourceBundle.getBundle("intl/ConfigFindAllStepsPanel").getString("ConfigFindAllStepsPanel.fishCheckTemplatesCheckBox.mnemonic").charAt(0)
                );
        this.fishCheckTemplatesCheckBox.setText(bundle.getString("ConfigFindAllStepsPanel.fishCheckTemplatesCheckBox.text"));
        this.fishCandidatesLabel.setText(bundle.getString("ConfigFindAllStepsPanel.fishCandidatesLabel.text"));
        this.fishCandidatesResultLabel.setText(bundle.getString("ConfigFindAllStepsPanel.fishCandidatesResultLabel.text"));
        this.fishCandidatesButton.setText(bundle.getString("ConfigFindAllStepsPanel.fishCandidatesButton.text"));
        this.fishCandidatesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigFindAllStepsPanel.this.fishCandidatesButtonActionPerformed(evt);
            }
        });
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
                                                                        .addContainerGap()
                                                                        .addGroup(
                                                                                fishPanelLayout.createParallelGroup(Alignment.LEADING)
                                                                                        .addComponent(this.fishTypeLabel)
                                                                                        .addComponent(this.fishFromLabel)
                                                                                        .addComponent(this.fishMaxFinsLabel)
                                                                                        .addComponent(this.fishMaxEndoFinsLabel)
                                                                        )
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addGroup(
                                                                                fishPanelLayout.createParallelGroup(Alignment.LEADING)
                                                                                        .addGroup(
                                                                                                fishPanelLayout.createSequentialGroup()
                                                                                                        .addGroup(
                                                                                                                fishPanelLayout.createParallelGroup(Alignment.LEADING)
                                                                                                                        .addComponent(this.fishFromComboBox, 0, -1, 32767)
                                                                                                                        .addComponent(this.fishMaxFinsComboBox, -2, -1, -2)
                                                                                                                        .addComponent(this.fishMaxEndoFinsComboBox, -2, -1, -2)
                                                                                                        )
                                                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                                                        .addComponent(this.fishToLabel)
                                                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                                                        .addComponent(this.fishToComboBox, 0, 96, 32767)
                                                                                        )
                                                                                        .addComponent(this.fishTypeComboBox, 0, 180, 32767)
                                                                        )
                                                        )
                                                        .addGroup(fishPanelLayout.createSequentialGroup().addGap(10, 10, 10).addComponent(this.fishCheckBox))
                                                        .addGroup(
                                                                fishPanelLayout.createSequentialGroup()
                                                                        .addContainerGap()
                                                                        .addGroup(
                                                                                fishPanelLayout.createParallelGroup(Alignment.LEADING)
                                                                                        .addComponent(this.fishCheckTemplatesCheckBox)
                                                                                        .addGroup(
                                                                                                fishPanelLayout.createSequentialGroup()
                                                                                                        .addComponent(this.fishCandidatesLabel)
                                                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                                                        .addComponent(this.fishCandidatesResultLabel)
                                                                                                        .addPreferredGap(ComponentPlacement.UNRELATED)
                                                                                                        .addComponent(this.fishCandidatesButton)
                                                                                        )
                                                                        )
                                                        )
                                        )
                                        .addContainerGap()
                        )
        );
        fishPanelLayout.linkSize(0, this.fishMaxEndoFinsComboBox, this.fishMaxFinsComboBox);
        fishPanelLayout.setVerticalGroup(
                fishPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                fishPanelLayout.createSequentialGroup()
                                        .addComponent(this.fishCheckBox)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                fishPanelLayout.createParallelGroup(Alignment.BASELINE).addComponent(this.fishTypeLabel).addComponent(this.fishTypeComboBox, -2, -1, -2)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                fishPanelLayout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(this.fishFromLabel)
                                                        .addComponent(this.fishFromComboBox, -2, -1, -2)
                                                        .addComponent(this.fishToLabel)
                                                        .addComponent(this.fishToComboBox, -2, -1, -2)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                fishPanelLayout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(this.fishMaxFinsLabel)
                                                        .addComponent(this.fishMaxFinsComboBox, -2, -1, -2)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                fishPanelLayout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(this.fishMaxEndoFinsLabel)
                                                        .addComponent(this.fishMaxEndoFinsComboBox, -2, -1, -2)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.fishCheckTemplatesCheckBox)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                fishPanelLayout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(this.fishCandidatesLabel)
                                                        .addComponent(this.fishCandidatesResultLabel)
                                                        .addComponent(this.fishCandidatesButton)
                                        )
                        )
        );
        this.krakenFishPanel.setBorder(BorderFactory.createTitledBorder(bundle.getString("ConfigFindAllStepsPanel.krakenFishPanel.border")));
        this.krakenFishMaxEndoFinsComboBox.setModel(new DefaultComboBoxModel<>(new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}));
        this.krakenFishMaxEndoFinsLabel
                .setDisplayedMnemonic(
                        ResourceBundle.getBundle("intl/ConfigFindAllStepsPanel").getString("ConfigFindAllStepsPanel.krakenFishMaxEndoFinsLabel.mnemonic").charAt(0)
                );
        this.krakenFishMaxEndoFinsLabel.setLabelFor(this.krakenFishMaxEndoFinsComboBox);
        this.krakenFishMaxEndoFinsLabel.setText(bundle.getString("ConfigFindAllStepsPanel.krakenFishMaxEndoFinsLabel.text"));
        this.krakenFishMaxFinsLabel
                .setDisplayedMnemonic(
                        ResourceBundle.getBundle("intl/ConfigFindAllStepsPanel").getString("ConfigFindAllStepsPanel.krakenFishMaxFinsLabel.mnemonic").charAt(0)
                );
        this.krakenFishMaxFinsLabel.setLabelFor(this.krakenFishMaxFinsComboBox);
        this.krakenFishMaxFinsLabel.setText(bundle.getString("ConfigFindAllStepsPanel.krakenFishMaxFinsLabel.text"));
        this.krakenFishMaxFinsComboBox.setModel(new DefaultComboBoxModel<>(new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}));
        this.krakenFishFromLabel
                .setDisplayedMnemonic(
                        ResourceBundle.getBundle("intl/ConfigFindAllStepsPanel").getString("ConfigFindAllStepsPanel.krakenFishFromLabel.mnemonic").charAt(0)
                );
        this.krakenFishFromLabel.setLabelFor(this.krakenFishFromComboBox);
        this.krakenFishFromLabel.setText(bundle.getString("ConfigFindAllStepsPanel.krakenFishFromLabel.text"));
        this.krakenFishFromComboBox.setModel(new DefaultComboBoxModel<>(new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}));
        this.krakenFishToLabel.setLabelFor(this.krakenFishToComboBox);
        this.krakenFishToLabel.setText(bundle.getString("ConfigFindAllStepsPanel.krakenFishToLabel.text"));
        this.krakenFishToComboBox.setModel(new DefaultComboBoxModel<>(new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}));
        this.krakenFishTypeComboBox.setModel(new DefaultComboBoxModel<>(new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}));
        this.krakenFishTypeLabel
                .setDisplayedMnemonic(
                        ResourceBundle.getBundle("intl/ConfigFindAllStepsPanel").getString("ConfigFindAllStepsPanel.krakenFishTypeLabel.mnemonic").charAt(0)
                );
        this.krakenFishTypeLabel.setLabelFor(this.krakenFishTypeComboBox);
        this.krakenFishTypeLabel.setText(bundle.getString("ConfigFindAllStepsPanel.krakenFishTypeLabel.text"));
        this.krakenFishCandidatesLabel.setText(bundle.getString("ConfigFindAllStepsPanel.krakenFishCandidatesLabel.text"));
        this.krakenFishCandidatesResultLabel.setText(bundle.getString("ConfigFindAllStepsPanel.krakenFishCandidatesResultLabel.text"));
        this.krakenFishCandidatesButton.setText(bundle.getString("ConfigFindAllStepsPanel.krakenFishCandidatesButton.text"));
        this.krakenFishCandidatesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigFindAllStepsPanel.this.krakenFishCandidatesButtonActionPerformed(evt);
            }
        });
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
                                                                                        .addComponent(this.krakenFishTypeLabel)
                                                                                        .addComponent(this.krakenFishFromLabel)
                                                                                        .addComponent(this.krakenFishMaxFinsLabel)
                                                                                        .addComponent(this.krakenFishMaxEndoFinsLabel)
                                                                        )
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addGroup(
                                                                                krakenFishPanelLayout.createParallelGroup(Alignment.LEADING)
                                                                                        .addGroup(
                                                                                                krakenFishPanelLayout.createSequentialGroup()
                                                                                                        .addGroup(
                                                                                                                krakenFishPanelLayout.createParallelGroup(Alignment.LEADING)
                                                                                                                        .addComponent(this.krakenFishFromComboBox, 0, -1, 32767)
                                                                                                                        .addComponent(this.krakenFishMaxFinsComboBox, -2, -1, -2)
                                                                                                                        .addComponent(this.krakenFishMaxEndoFinsComboBox, -2, -1, -2)
                                                                                                        )
                                                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                                                        .addComponent(this.krakenFishToLabel)
                                                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                                                        .addComponent(this.krakenFishToComboBox, 0, 96, 32767)
                                                                                        )
                                                                                        .addComponent(this.krakenFishTypeComboBox, 0, 180, 32767)
                                                                        )
                                                        )
                                                        .addGroup(
                                                                krakenFishPanelLayout.createSequentialGroup()
                                                                        .addComponent(this.krakenFishCandidatesLabel)
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addComponent(this.krakenFishCandidatesResultLabel)
                                                                        .addPreferredGap(ComponentPlacement.UNRELATED)
                                                                        .addComponent(this.krakenFishCandidatesButton)
                                                        )
                                        )
                                        .addContainerGap()
                        )
        );
        krakenFishPanelLayout.linkSize(0, this.krakenFishMaxEndoFinsComboBox, this.krakenFishMaxFinsComboBox);
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
                                                        .addComponent(this.krakenFishFromLabel)
                                                        .addComponent(this.krakenFishFromComboBox, -2, -1, -2)
                                                        .addComponent(this.krakenFishToLabel)
                                                        .addComponent(this.krakenFishToComboBox, -2, -1, -2)
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
                                                        .addComponent(this.krakenFishCandidatesLabel)
                                                        .addComponent(this.krakenFishCandidatesResultLabel)
                                                        .addComponent(this.krakenFishCandidatesButton)
                                        )
                                        .addContainerGap(-1, 32767)
                        )
        );
        this.alsPanel.setBorder(BorderFactory.createTitledBorder(bundle.getString("ConfigFindAllStepsPanel.alsPanel.border.title")));
        this.alsChainLengthLabel
                .setDisplayedMnemonic(
                        ResourceBundle.getBundle("intl/ConfigFindAllStepsPanel").getString("ConfigFindAllStepsPanel.alsChainLengthLabel.mnemonic").charAt(0)
                );
        this.alsChainLengthLabel.setLabelFor(this.alsChainLengthComboBox);
        this.alsChainLengthLabel.setText(bundle.getString("ConfigFindAllStepsPanel.alsChainLengthLabel.text"));
        this.alsChainLengthComboBox.setModel(new DefaultComboBoxModel<>(new String[]{"4", "5", "6", "7"}));
        this.alsBiDirCheckBox
                .setMnemonic(ResourceBundle.getBundle("intl/ConfigFindAllStepsPanel").getString("ConfigFindAllStepsPanel.alsBiDirCheckBox.mnemonic").charAt(0));
        this.alsBiDirCheckBox.setText(bundle.getString("ConfigFindAllStepsPanel.alsBiDirCheckBox.text"));
        GroupLayout alsPanelLayout = new GroupLayout(this.alsPanel);
        this.alsPanel.setLayout(alsPanelLayout);
        alsPanelLayout.setHorizontalGroup(
                alsPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                alsPanelLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(this.alsChainLengthLabel)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.alsChainLengthComboBox, -2, -1, -2)
                                        .addGap(18, 18, 18)
                                        .addComponent(this.alsBiDirCheckBox)
                                        .addContainerGap(-1, 32767)
                        )
        );
        alsPanelLayout.setVerticalGroup(
                alsPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                alsPanelLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                alsPanelLayout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(this.alsChainLengthLabel)
                                                        .addComponent(this.alsChainLengthComboBox, -2, -1, -2)
                                                        .addComponent(this.alsBiDirCheckBox)
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
                                        .addComponent(this.jScrollPane1, -2, 211, -2)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                jPanel1Layout.createParallelGroup(Alignment.LEADING)
                                                        .addGroup(jPanel1Layout.createSequentialGroup().addComponent(this.alsPanel, -1, -1, 32767).addGap(10, 10, 10))
                                                        .addComponent(this.krakenFishPanel, -1, -1, 32767)
                                                        .addComponent(this.fishPanel, -1, -1, 32767)
                                        )
                                        .addGap(0, 0, 0)
                        )
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                jPanel1Layout.createSequentialGroup()
                                        .addGroup(
                                                jPanel1Layout.createParallelGroup(Alignment.TRAILING, false)
                                                        .addComponent(this.jScrollPane1, Alignment.LEADING)
                                                        .addGroup(
                                                                Alignment.LEADING,
                                                                jPanel1Layout.createSequentialGroup()
                                                                        .addComponent(this.fishPanel, -2, -1, -2)
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addComponent(this.krakenFishPanel, -2, -1, -2)
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addComponent(this.alsPanel, -2, -1, -2)
                                                        )
                                        )
                                        .addContainerGap()
                        )
        );
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                Alignment.TRAILING,
                                layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                layout.createParallelGroup(Alignment.TRAILING).addComponent(this.jPanel1, Alignment.LEADING, -1, -1, 32767).addComponent(this.resetButton)
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
                                        .addComponent(this.jPanel1, -2, -1, -2)
                                        .addPreferredGap(ComponentPlacement.RELATED, -1, 32767)
                                        .addComponent(this.resetButton)
                                        .addContainerGap()
                        )
        );
    }

    private void stepTreeMousePressed(MouseEvent evt) {
        TreePath path = this.stepTree.getPathForLocation(evt.getX(), evt.getY());
        if (path != null) {
            CheckNode act = (CheckNode) path.getLastPathComponent();
            CheckNode last = (CheckNode) this.stepTree.getLastSelectedPathComponent();
            if (act != null && last != null && act == last) {
                last.toggleSelectionState();
                this.stepTree.repaint();
            }
        }
    }

    private void resetButtonActionPerformed(ActionEvent evt) {
        this.initAll(true);
    }

    private void fishCandidatesButtonActionPerformed(ActionEvent evt) {
        FishChooseCandidatesDialog dlg = new FishChooseCandidatesDialog(null, this.fishCandidates);
        dlg.setVisible(true);
        this.fishCandidates = dlg.getFishCandidates();
        this.setCandidateLabel(this.fishCandidatesResultLabel, this.fishCandidates);
    }

    private void krakenFishCandidatesButtonActionPerformed(ActionEvent evt) {
        FishChooseCandidatesDialog dlg = new FishChooseCandidatesDialog(null, this.krakenFishCandidates);
        dlg.setVisible(true);
        this.krakenFishCandidates = dlg.getFishCandidates();
        this.setCandidateLabel(this.krakenFishCandidatesResultLabel, this.krakenFishCandidates);
    }

    public void okPressed() {
        StepConfig[] orgSteps1 = Options.getInstance().solverSteps;

        for (int i = 0; i < this.steps.length; i++) {
            for (int j = 0; j < orgSteps1.length; j++) {
                if (this.steps[i].getType() == orgSteps1[j].getType()) {
                    orgSteps1[j].setAllStepsEnabled(this.steps[i].isAllStepsEnabled());
                    break;
                }
            }
        }

        StepConfig[] orgSteps2 = Options.getInstance().getOrgSolverSteps();

        for (int i = 0; i < this.steps.length; i++) {
            for (int j = 0; j < orgSteps2.length; j++) {
                if (this.steps[i].getType() == orgSteps2[j].getType()) {
                    orgSteps2[j].setAllStepsEnabled(this.steps[i].isAllStepsEnabled());
                    break;
                }
            }
        }

        Options.getInstance().setAllStepsSearchFish(this.fishCheckBox.isSelected());
        Options.getInstance().setAllStepsMaxFishType(this.fishTypeComboBox.getSelectedIndex());
        Options.getInstance().setAllStepsMinFishSize(this.fishFromComboBox.getSelectedIndex() + 2);
        Options.getInstance().setAllStepsMaxFishSize(this.fishToComboBox.getSelectedIndex() + 2);
        Options.getInstance().setAllStepsMaxFins(this.fishMaxFinsComboBox.getSelectedIndex());
        Options.getInstance().setAllStepsMaxEndoFins(this.fishMaxEndoFinsComboBox.getSelectedIndex());
        Options.getInstance().setAllStepsCheckTemplates(this.fishCheckTemplatesCheckBox.isSelected());
        Options.getInstance().setAllStepsKrakenMaxFishType(this.krakenFishTypeComboBox.getSelectedIndex());
        Options.getInstance().setAllStepsKrakenMinFishSize(this.krakenFishFromComboBox.getSelectedIndex() + 2);
        Options.getInstance().setAllStepsKrakenMaxFishSize(this.krakenFishToComboBox.getSelectedIndex() + 2);
        Options.getInstance().setAllStepsMaxKrakenFins(this.krakenFishMaxFinsComboBox.getSelectedIndex());
        Options.getInstance().setAllStepsMaxKrakenEndoFins(this.krakenFishMaxEndoFinsComboBox.getSelectedIndex());
        Options.getInstance().setAllStepsFishCandidates(this.fishCandidates);
        Options.getInstance().setAllStepsKrakenFishCandidates(this.krakenFishCandidates);
        Options.getInstance().setAllStepsAlsChainLength(this.alsChainLengthComboBox.getSelectedIndex() + 4);
        Options.getInstance().setAllStepsAlsChainForwardOnly(this.alsBiDirCheckBox.isSelected());
    }

    private void initAll(boolean setDefault) {
        if (setDefault) {
            this.steps = Options.getInstance().copyStepConfigs(Options.DEFAULT_SOLVER_STEPS, true, false);
            this.fishCheckBox.setSelected(true);
            this.fishTypeComboBox.setSelectedIndex(1);
            this.fishFromComboBox.setSelectedIndex(0);
            this.fishToComboBox.setSelectedIndex(2);
            this.fishMaxFinsComboBox.setSelectedIndex(5);
            this.fishMaxEndoFinsComboBox.setSelectedIndex(2);
            this.fishCheckTemplatesCheckBox.setSelected(true);
            this.krakenFishTypeComboBox.setSelectedIndex(1);
            this.krakenFishFromComboBox.setSelectedIndex(0);
            this.krakenFishToComboBox.setSelectedIndex(2);
            this.krakenFishMaxFinsComboBox.setSelectedIndex(2);
            this.krakenFishMaxEndoFinsComboBox.setSelectedIndex(0);
            this.fishCandidates = "111111111";
            this.krakenFishCandidates = "111111111";
            this.alsChainLengthComboBox.setSelectedIndex(2);
            this.alsBiDirCheckBox.setSelected(true);
        } else {
            this.steps = Options.getInstance().copyStepConfigs(Options.getInstance().solverSteps, true, false);
            this.fishCheckBox.setSelected(Options.getInstance().isAllStepsSearchFish());
            this.fishTypeComboBox.setSelectedIndex(Options.getInstance().getAllStepsMaxFishType());
            this.fishFromComboBox.setSelectedIndex(Options.getInstance().getAllStepsMinFishSize() - 2);
            this.fishToComboBox.setSelectedIndex(Options.getInstance().getAllStepsMaxFishSize() - 2);
            this.fishMaxFinsComboBox.setSelectedIndex(Options.getInstance().getAllStepsMaxFins());
            this.fishMaxEndoFinsComboBox.setSelectedIndex(Options.getInstance().getAllStepsMaxEndoFins());
            this.fishCheckTemplatesCheckBox.setSelected(Options.getInstance().isAllStepsCheckTemplates());
            this.krakenFishTypeComboBox.setSelectedIndex(Options.getInstance().getAllStepsKrakenMaxFishType());
            this.krakenFishFromComboBox.setSelectedIndex(Options.getInstance().getAllStepsKrakenMinFishSize() - 2);
            this.krakenFishToComboBox.setSelectedIndex(Options.getInstance().getAllStepsKrakenMaxFishSize() - 2);
            this.krakenFishMaxFinsComboBox.setSelectedIndex(Options.getInstance().getAllStepsMaxKrakenFins());
            this.krakenFishMaxEndoFinsComboBox.setSelectedIndex(Options.getInstance().getAllStepsMaxKrakenEndoFins());
            this.fishCandidates = Options.getInstance().getAllStepsFishCandidates();
            this.krakenFishCandidates = Options.getInstance().getAllStepsKrakenFishCandidates();
            this.alsChainLengthComboBox.setSelectedIndex(Options.getInstance().getAllStepsAlsChainLength() - 4);
            this.alsBiDirCheckBox.setSelected(Options.getInstance().isAllStepsAlsChainForwardOnly());
        }

        this.setCandidateLabels();
        this.buildTree();
    }

    private void setCandidateLabels() {
        this.setCandidateLabel(this.fishCandidatesResultLabel, this.fishCandidates);
        this.setCandidateLabel(this.krakenFishCandidatesResultLabel, this.krakenFishCandidates);
    }

    private void setCandidateLabel(JLabel label, String values) {
        StringBuilder tmp = new StringBuilder();
        int index = 0;
        int startCand = 0;
        int endCand = 0;

        while (index < values.length()) {
            while (index < values.length() && values.charAt(index) == '0') {
                index++;
            }

            if (index < values.length() && values.charAt(index) == '1') {
                startCand = index + 1;
            }

            while (index < values.length() && values.charAt(index) == '1') {
                index++;
            }

            if (startCand > 0) {
                endCand = index;
                if (startCand != endCand) {
                    tmp.append(startCand).append("-").append(endCand);
                } else {
                    tmp.append(startCand);
                }

                startCand = -1;
                tmp.append(",");
            }
        }

        if (startCand == 0) {
            tmp.append("-");
        }

        index = tmp.length() - 1;
        if (tmp.charAt(index) == ',') {
            tmp.delete(index, index + 1);
        }

        label.setText(tmp.toString());
    }

    public void buildTree() {
        CheckNode root = new CheckNode();

        for (int i = 0; i < this.steps.length; i++) {
            if (this.steps[i].getCategory() != SolutionCategory.BASIC_FISH
                    && this.steps[i].getCategory() != SolutionCategory.FINNED_BASIC_FISH
                    && this.steps[i].getCategory() != SolutionCategory.FINNED_FRANKEN_FISH
                    && this.steps[i].getCategory() != SolutionCategory.FINNED_MUTANT_FISH
                    && this.steps[i].getCategory() != SolutionCategory.FRANKEN_FISH
                    && this.steps[i].getCategory() != SolutionCategory.MUTANT_FISH
                    && this.steps[i].getType() != SolutionType.BRUTE_FORCE) {
                Enumeration<?> en = root.children();

                CheckNode act;
                for (act = null; en.hasMoreElements(); act = null) {
                    act = (CheckNode) en.nextElement();
                    if (act.getCategory() == this.steps[i].getCategory()) {
                        break;
                    }
                }

                if (act == null) {
                    act = new CheckNode(
                            this.steps[i].getCategoryName(), true, this.steps[i].isAllStepsEnabled() ? 2 : 0, null, true, false, false, this.steps[i].getCategory()
                    );
                    root.add(act);
                }

                act.add(
                        new CheckNode(this.steps[i].getType().getStepName(), false, this.steps[i].isAllStepsEnabled() ? 2 : 0, this.steps[i], true, false, false, null)
                );
                if (act.getSelectionState() == 2 && !this.steps[i].isAllStepsEnabled()) {
                    act.setSelectionState(1);
                }

                if (act.getSelectionState() == 0 && this.steps[i].isAllStepsEnabled()) {
                    act.setSelectionState(1);
                }
            }
        }

        DefaultTreeModel tmpModel = new DefaultTreeModel(root);
        this.stepTree.setModel(tmpModel);
        this.stepTree.setShowsRootHandles(true);
        this.stepTree.setRootVisible(false);
        this.stepTree.setRowHeight(-1);
    }
}
