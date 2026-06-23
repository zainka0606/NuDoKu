package sudoku;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ResourceBundle;

public class ConfigColorPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private JButton[] buttons = null;
    private Color[] colors = null;
    private JButton alsHintBGButton1;
    private JButton alsHintBGButton2;
    private JButton alsHintBGButton3;
    private JButton alsHintBGButton4;
    private JButton alsHintFGButton1;
    private JButton alsHintFGButton2;
    private JButton alsHintFGButton3;
    private JButton alsHintFGButton4;
    private JLabel alsLabel1;
    private JLabel alsLabel2;
    private JLabel alsLabel3;
    private JLabel alsLabel4;
    private JButton alternateBGButton;
    private JLabel alternateBGLabel;
    private JButton arrowButton;
    private JLabel arrowLabel;
    private JPanel backGroundPanel;
    private JButton candidatesButton;
    private JLabel candidatesLabel;
    private JButton cannibalisticHintBGButton;
    private JLabel cannibalisticHintBGLabel;
    private JButton cannibalisticHintFGButton;
    private JButton cluesButton;
    private JLabel cluesLabel;
    private JPanel coloringPanel;
    private JButton cursorBGButton;
    private JLabel cursorBGLabel;
    private JButton delHintBGButton;
    private JLabel delHintBGLabel;
    private JButton delHintFGButton;
    private JButton endoFinsHintBGButton;
    private JLabel endoFinsHintBGLabel;
    private JButton endoFinsHintFGButton;
    private JButton finsHintBGButton;
    private JLabel finsHintBGLabel;
    private JButton finsHintFGButton;
    private JPanel frameAndDigitsPanel;
    private JLabel frameLabel;
    private JButton frameStrongButton;
    private JButton frameWeakButton;
    private JPanel hintPanel;
    private JButton invalidBGButton;
    private JLabel invalidBGLabel;
    private JButton invalidFGButton;
    private JLabel invalidFGLabel;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JLabel normHintBGLabel;
    private JButton normalBGButton;
    private JLabel normalBGLabel;
    private JButton normalHintBGButton;
    private JButton normalHintFGButton;
    private JButton resetButton;
    private JButton stiftButtonA;
    private JButton stiftButtonB;
    private JButton stiftButtonC;
    private JButton stiftButtonD;
    private JButton stiftButtonE;
    private JButton stiftButtona;
    private JButton stiftButtonb;
    private JButton stiftButtonc;
    private JButton stiftButtond;
    private JButton stiftButtone;
    private JLabel stiftLabelA;
    private JLabel stiftLabelB;
    private JLabel stiftLabelC;
    private JLabel stiftLabelD;
    private JLabel stiftLabelE;
    private JButton validBGButton;
    private JLabel validBGLabel;
    private JButton valuesButton;
    private JLabel valuesLabel;
    private JButton wrongButton;
    private JLabel wrongLabel;

    public ConfigColorPanel() {
        this.initComponents();
        this.buttons = new JButton[]{
                this.frameStrongButton,
                this.frameWeakButton,
                this.cluesButton,
                this.valuesButton,
                this.candidatesButton,
                this.invalidFGButton,
                this.wrongButton,
                this.normalBGButton,
                this.cursorBGButton,
                this.invalidBGButton,
                this.validBGButton,
                this.normalHintFGButton,
                this.normalHintBGButton,
                this.delHintFGButton,
                this.delHintBGButton,
                this.finsHintFGButton,
                this.finsHintBGButton,
                this.endoFinsHintFGButton,
                this.endoFinsHintBGButton,
                this.cannibalisticHintFGButton,
                this.cannibalisticHintBGButton,
                this.arrowButton,
                this.alsHintFGButton1,
                this.alsHintBGButton1,
                this.alsHintFGButton2,
                this.alsHintBGButton2,
                this.alsHintFGButton3,
                this.alsHintBGButton3,
                this.alsHintFGButton4,
                this.alsHintBGButton4,
                this.stiftButtona,
                this.stiftButtonA,
                this.stiftButtonb,
                this.stiftButtonB,
                this.stiftButtonc,
                this.stiftButtonC,
                this.stiftButtond,
                this.stiftButtonD,
                this.stiftButtone,
                this.stiftButtonE,
                this.alternateBGButton
        };
        this.initAll(false);
    }

    private void initComponents() {
        this.jPanel1 = new JPanel();
        this.frameAndDigitsPanel = new JPanel();
        this.frameLabel = new JLabel();
        this.cluesLabel = new JLabel();
        this.valuesLabel = new JLabel();
        this.candidatesLabel = new JLabel();
        this.invalidFGLabel = new JLabel();
        this.wrongLabel = new JLabel();
        this.frameStrongButton = new JButton();
        this.frameWeakButton = new JButton();
        this.cluesButton = new JButton();
        this.valuesButton = new JButton();
        this.candidatesButton = new JButton();
        this.invalidFGButton = new JButton();
        this.wrongButton = new JButton();
        this.backGroundPanel = new JPanel();
        this.normalBGLabel = new JLabel();
        this.cursorBGLabel = new JLabel();
        this.invalidBGLabel = new JLabel();
        this.validBGLabel = new JLabel();
        this.normalBGButton = new JButton();
        this.cursorBGButton = new JButton();
        this.invalidBGButton = new JButton();
        this.validBGButton = new JButton();
        this.alsLabel4 = new JLabel();
        this.alsHintFGButton4 = new JButton();
        this.alsHintBGButton4 = new JButton();
        this.alsHintBGButton3 = new JButton();
        this.alsHintFGButton3 = new JButton();
        this.alsLabel3 = new JLabel();
        this.alsLabel2 = new JLabel();
        this.alsHintFGButton2 = new JButton();
        this.alsHintBGButton2 = new JButton();
        this.alsHintBGButton1 = new JButton();
        this.alsHintFGButton1 = new JButton();
        this.alsLabel1 = new JLabel();
        this.alternateBGLabel = new JLabel();
        this.alternateBGButton = new JButton();
        this.jPanel2 = new JPanel();
        this.hintPanel = new JPanel();
        this.normHintBGLabel = new JLabel();
        this.delHintBGLabel = new JLabel();
        this.finsHintBGLabel = new JLabel();
        this.endoFinsHintBGLabel = new JLabel();
        this.cannibalisticHintBGLabel = new JLabel();
        this.arrowLabel = new JLabel();
        this.normalHintFGButton = new JButton();
        this.delHintFGButton = new JButton();
        this.finsHintFGButton = new JButton();
        this.endoFinsHintFGButton = new JButton();
        this.cannibalisticHintFGButton = new JButton();
        this.arrowButton = new JButton();
        this.normalHintBGButton = new JButton();
        this.delHintBGButton = new JButton();
        this.finsHintBGButton = new JButton();
        this.endoFinsHintBGButton = new JButton();
        this.cannibalisticHintBGButton = new JButton();
        this.coloringPanel = new JPanel();
        this.stiftLabelA = new JLabel();
        this.stiftButtona = new JButton();
        this.stiftButtonA = new JButton();
        this.stiftButtonB = new JButton();
        this.stiftButtonb = new JButton();
        this.stiftLabelB = new JLabel();
        this.stiftLabelC = new JLabel();
        this.stiftButtonc = new JButton();
        this.stiftButtonC = new JButton();
        this.stiftButtonD = new JButton();
        this.stiftButtond = new JButton();
        this.stiftLabelD = new JLabel();
        this.stiftLabelE = new JLabel();
        this.stiftButtone = new JButton();
        this.stiftButtonE = new JButton();
        this.resetButton = new JButton();
        ResourceBundle bundle = ResourceBundle.getBundle("intl/ConfigColorPanel");
        this.frameAndDigitsPanel.setBorder(BorderFactory.createTitledBorder(bundle.getString("ConfigColorPanel.frameAndDigitsPanel.border.title")));
        this.frameLabel.setText(bundle.getString("ConfigColorPanel.frameLabel.text"));
        this.cluesLabel.setText(bundle.getString("ConfigColorPanel.cluesLabel.text"));
        this.valuesLabel.setText(bundle.getString("ConfigColorPanel.valuesLabel.text"));
        this.candidatesLabel.setText(bundle.getString("ConfigColorPanel.candidatesLabel.text"));
        this.invalidFGLabel.setText(bundle.getString("ConfigColorPanel.invalidFGLabel.text"));
        this.wrongLabel.setText(bundle.getString("ConfigColorPanel.wrongLabel.text"));
        this.frameStrongButton.setText("...");
        this.frameStrongButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigColorPanel.this.frameStrongButtonActionPerformed(evt);
            }
        });
        this.frameWeakButton.setText("...");
        this.frameWeakButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigColorPanel.this.frameWeakButtonActionPerformed(evt);
            }
        });
        this.cluesButton.setText("...");
        this.cluesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigColorPanel.this.cluesButtonActionPerformed(evt);
            }
        });
        this.valuesButton.setText("...");
        this.valuesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigColorPanel.this.valuesButtonActionPerformed(evt);
            }
        });
        this.candidatesButton.setText("...");
        this.candidatesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigColorPanel.this.candidatesButtonActionPerformed(evt);
            }
        });
        this.invalidFGButton.setText("...");
        this.invalidFGButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigColorPanel.this.invalidFGButtonActionPerformed(evt);
            }
        });
        this.wrongButton.setText("...");
        this.wrongButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigColorPanel.this.wrongButtonActionPerformed(evt);
            }
        });
        GroupLayout frameAndDigitsPanelLayout = new GroupLayout(this.frameAndDigitsPanel);
        this.frameAndDigitsPanel.setLayout(frameAndDigitsPanelLayout);
        frameAndDigitsPanelLayout.setHorizontalGroup(
                frameAndDigitsPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                frameAndDigitsPanelLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                frameAndDigitsPanelLayout.createParallelGroup(Alignment.LEADING)
                                                        .addComponent(this.frameLabel)
                                                        .addComponent(this.cluesLabel)
                                                        .addComponent(this.valuesLabel)
                                                        .addComponent(this.candidatesLabel)
                                                        .addComponent(this.invalidFGLabel)
                                                        .addComponent(this.wrongLabel)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                frameAndDigitsPanelLayout.createParallelGroup(Alignment.LEADING)
                                                        .addGroup(
                                                                frameAndDigitsPanelLayout.createSequentialGroup()
                                                                        .addComponent(this.frameStrongButton, -2, 40, -2)
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addComponent(this.frameWeakButton, -2, 40, -2)
                                                        )
                                                        .addComponent(this.cluesButton, -2, 40, -2)
                                                        .addComponent(this.valuesButton, -2, 40, -2)
                                                        .addComponent(this.candidatesButton, -2, 40, -2)
                                                        .addComponent(this.invalidFGButton, -2, 40, -2)
                                                        .addComponent(this.wrongButton, -2, 40, -2)
                                        )
                                        .addContainerGap(73, 32767)
                        )
        );
        frameAndDigitsPanelLayout.linkSize(
                0, this.candidatesButton, this.cluesButton, this.frameWeakButton, this.invalidFGButton, this.valuesButton, this.wrongButton
        );
        frameAndDigitsPanelLayout.setVerticalGroup(
                frameAndDigitsPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                frameAndDigitsPanelLayout.createSequentialGroup()
                                        .addGroup(
                                                frameAndDigitsPanelLayout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(this.frameLabel)
                                                        .addComponent(this.frameWeakButton, -2, 21, -2)
                                                        .addComponent(this.frameStrongButton, -2, 21, -2)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                frameAndDigitsPanelLayout.createParallelGroup(Alignment.BASELINE).addComponent(this.cluesLabel).addComponent(this.cluesButton, -2, 21, -2)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                frameAndDigitsPanelLayout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(this.valuesLabel)
                                                        .addComponent(this.valuesButton, -2, 21, -2)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                frameAndDigitsPanelLayout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(this.candidatesLabel)
                                                        .addComponent(this.candidatesButton, -2, 21, -2)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                frameAndDigitsPanelLayout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(this.invalidFGLabel)
                                                        .addComponent(this.invalidFGButton, -2, 21, -2)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                frameAndDigitsPanelLayout.createParallelGroup(Alignment.BASELINE).addComponent(this.wrongLabel).addComponent(this.wrongButton, -2, 21, -2)
                                        )
                                        .addContainerGap(-1, 32767)
                        )
        );
        this.backGroundPanel.setBorder(BorderFactory.createTitledBorder(null, bundle.getString("ConfigColorPanel.backGroundPanel.border.title")));
        this.normalBGLabel.setText(bundle.getString("ConfigColorPanel.normalBGLabel.text"));
        this.cursorBGLabel.setText(bundle.getString("ConfigColorPanel.cursorBGLabel.text"));
        this.invalidBGLabel.setText(bundle.getString("ConfigColorPanel.invalidBGLabel.text"));
        this.validBGLabel.setText(bundle.getString("ConfigColorPanel.validBGLabel.text"));
        this.normalBGButton.setText("...");
        this.normalBGButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigColorPanel.this.normalBGButtonActionPerformed(evt);
            }
        });
        this.cursorBGButton.setText("...");
        this.cursorBGButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigColorPanel.this.cursorBGButtonActionPerformed(evt);
            }
        });
        this.invalidBGButton.setText("...");
        this.invalidBGButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigColorPanel.this.invalidBGButtonActionPerformed(evt);
            }
        });
        this.validBGButton.setText("...");
        this.validBGButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigColorPanel.this.validBGButtonActionPerformed(evt);
            }
        });
        this.alsLabel4.setText(bundle.getString("ConfigColorPanel.alsLabel4.text"));
        this.alsHintFGButton4.setText("...");
        this.alsHintFGButton4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigColorPanel.this.alsHintFGButton4ActionPerformed(evt);
            }
        });
        this.alsHintBGButton4.setText("...");
        this.alsHintBGButton4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigColorPanel.this.alsHintBGButton4ActionPerformed(evt);
            }
        });
        this.alsHintBGButton3.setText("...");
        this.alsHintBGButton3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigColorPanel.this.alsHintBGButton3ActionPerformed(evt);
            }
        });
        this.alsHintFGButton3.setText("...");
        this.alsHintFGButton3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigColorPanel.this.alsHintFGButton3ActionPerformed(evt);
            }
        });
        this.alsLabel3.setText(bundle.getString("ConfigColorPanel.alsLabel3.text"));
        this.alsLabel2.setText(bundle.getString("ConfigColorPanel.alsLabel2.text"));
        this.alsHintFGButton2.setText("...");
        this.alsHintFGButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigColorPanel.this.alsHintFGButton2ActionPerformed(evt);
            }
        });
        this.alsHintBGButton2.setText("...");
        this.alsHintBGButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigColorPanel.this.alsHintBGButton2ActionPerformed(evt);
            }
        });
        this.alsHintBGButton1.setText("...");
        this.alsHintBGButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigColorPanel.this.alsHintBGButton1ActionPerformed(evt);
            }
        });
        this.alsHintFGButton1.setText("...");
        this.alsHintFGButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigColorPanel.this.alsHintFGButton1ActionPerformed(evt);
            }
        });
        this.alsLabel1.setText(bundle.getString("ConfigColorPanel.alsLabel1.text"));
        this.alternateBGLabel.setText(bundle.getString("ConfigColorPanel.alternateBGLabel.text"));
        this.alternateBGButton.setText("...");
        this.alternateBGButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigColorPanel.this.alternateBGButtonActionPerformed(evt);
            }
        });
        GroupLayout backGroundPanelLayout = new GroupLayout(this.backGroundPanel);
        this.backGroundPanel.setLayout(backGroundPanelLayout);
        backGroundPanelLayout.setHorizontalGroup(
                backGroundPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                backGroundPanelLayout.createSequentialGroup()
                                        .addGroup(
                                                backGroundPanelLayout.createParallelGroup(Alignment.LEADING)
                                                        .addComponent(this.normalBGLabel)
                                                        .addComponent(this.validBGLabel)
                                                        .addComponent(this.invalidBGLabel)
                                                        .addComponent(this.cursorBGLabel)
                                                        .addComponent(this.alternateBGLabel)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED, -1, 32767)
                                        .addGroup(
                                                backGroundPanelLayout.createParallelGroup(Alignment.LEADING)
                                                        .addComponent(this.alternateBGButton, -2, 40, -2)
                                                        .addComponent(this.validBGButton, -2, 40, -2)
                                                        .addComponent(this.invalidBGButton, -2, 40, -2)
                                                        .addComponent(this.cursorBGButton, -2, 40, -2)
                                                        .addComponent(this.normalBGButton, -2, 40, -2)
                                        )
                                        .addPreferredGap(ComponentPlacement.UNRELATED)
                                        .addGroup(
                                                backGroundPanelLayout.createParallelGroup(Alignment.LEADING)
                                                        .addGroup(
                                                                backGroundPanelLayout.createSequentialGroup()
                                                                        .addComponent(this.alsLabel1)
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addComponent(this.alsHintFGButton1, -2, 40, -2)
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addComponent(this.alsHintBGButton1, -2, 40, -2)
                                                        )
                                                        .addGroup(
                                                                backGroundPanelLayout.createSequentialGroup()
                                                                        .addComponent(this.alsLabel4)
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addComponent(this.alsHintFGButton4, -2, 40, -2)
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addComponent(this.alsHintBGButton4, -2, 40, -2)
                                                        )
                                                        .addGroup(
                                                                backGroundPanelLayout.createSequentialGroup()
                                                                        .addGroup(backGroundPanelLayout.createParallelGroup(Alignment.LEADING).addComponent(this.alsLabel2).addComponent(this.alsLabel3))
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addGroup(
                                                                                backGroundPanelLayout.createParallelGroup(Alignment.LEADING)
                                                                                        .addGroup(
                                                                                                backGroundPanelLayout.createSequentialGroup()
                                                                                                        .addComponent(this.alsHintFGButton2, -2, 40, -2)
                                                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                                                        .addComponent(this.alsHintBGButton2, -2, 40, -2)
                                                                                        )
                                                                                        .addGroup(
                                                                                                backGroundPanelLayout.createSequentialGroup()
                                                                                                        .addComponent(this.alsHintFGButton3, -2, 40, -2)
                                                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                                                        .addComponent(this.alsHintBGButton3, -2, 40, -2)
                                                                                        )
                                                                        )
                                                        )
                                        )
                                        .addGap(10, 10, 10)
                        )
        );
        backGroundPanelLayout.setVerticalGroup(
                backGroundPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                backGroundPanelLayout.createSequentialGroup()
                                        .addGroup(
                                                backGroundPanelLayout.createParallelGroup(Alignment.LEADING)
                                                        .addGroup(
                                                                backGroundPanelLayout.createSequentialGroup()
                                                                        .addGroup(
                                                                                backGroundPanelLayout.createParallelGroup(Alignment.BASELINE)
                                                                                        .addComponent(this.normalBGLabel)
                                                                                        .addComponent(this.normalBGButton, -2, 21, -2)
                                                                        )
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addGroup(
                                                                                backGroundPanelLayout.createParallelGroup(Alignment.BASELINE)
                                                                                        .addComponent(this.cursorBGLabel)
                                                                                        .addComponent(this.cursorBGButton, -2, 21, -2)
                                                                        )
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addGroup(
                                                                                backGroundPanelLayout.createParallelGroup(Alignment.BASELINE)
                                                                                        .addComponent(this.invalidBGLabel)
                                                                                        .addComponent(this.invalidBGButton, -2, 21, -2)
                                                                        )
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addGroup(
                                                                                backGroundPanelLayout.createParallelGroup(Alignment.BASELINE)
                                                                                        .addComponent(this.validBGLabel)
                                                                                        .addComponent(this.validBGButton, -2, 21, -2)
                                                                        )
                                                        )
                                                        .addGroup(
                                                                backGroundPanelLayout.createSequentialGroup()
                                                                        .addGroup(
                                                                                backGroundPanelLayout.createParallelGroup(Alignment.BASELINE)
                                                                                        .addComponent(this.alsLabel1)
                                                                                        .addComponent(this.alsHintFGButton1, -2, 21, -2)
                                                                                        .addComponent(this.alsHintBGButton1, -2, 21, -2)
                                                                        )
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addGroup(
                                                                                backGroundPanelLayout.createParallelGroup(Alignment.BASELINE)
                                                                                        .addComponent(this.alsLabel2)
                                                                                        .addComponent(this.alsHintFGButton2, -2, 21, -2)
                                                                                        .addComponent(this.alsHintBGButton2, -2, 21, -2)
                                                                        )
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addGroup(
                                                                                backGroundPanelLayout.createParallelGroup(Alignment.BASELINE)
                                                                                        .addComponent(this.alsLabel3)
                                                                                        .addComponent(this.alsHintBGButton3, -2, 21, -2)
                                                                                        .addComponent(this.alsHintFGButton3, -2, 21, -2)
                                                                        )
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addGroup(
                                                                                backGroundPanelLayout.createParallelGroup(Alignment.BASELINE)
                                                                                        .addComponent(this.alsLabel4)
                                                                                        .addComponent(this.alsHintFGButton4, -2, 21, -2)
                                                                                        .addComponent(this.alsHintBGButton4, -2, 21, -2)
                                                                        )
                                                        )
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                backGroundPanelLayout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(this.alternateBGLabel)
                                                        .addComponent(this.alternateBGButton, -2, 21, -2)
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
                                                        .addComponent(this.backGroundPanel, -1, -1, 32767)
                                                        .addComponent(this.frameAndDigitsPanel, -2, -1, -2)
                                        )
                        )
        );
        jPanel1Layout.linkSize(0, this.backGroundPanel, this.frameAndDigitsPanel);
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                jPanel1Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(this.frameAndDigitsPanel, -2, -1, -2)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.backGroundPanel, -2, -1, -2)
                                        .addContainerGap(91, 32767)
                        )
        );
        this.hintPanel.setBorder(BorderFactory.createTitledBorder(bundle.getString("ConfigColorPanel.hintPanel.border.title")));
        this.normHintBGLabel.setText(bundle.getString("ConfigColorPanel.normHintBGLabel.text"));
        this.delHintBGLabel.setText(bundle.getString("ConfigColorPanel.delHintBGLabel.text"));
        this.finsHintBGLabel.setText(bundle.getString("ConfigColorPanel.finsHintBGLabel.text"));
        this.endoFinsHintBGLabel.setText(bundle.getString("ConfigColorPanel.endoFinsHintBGLabel.text"));
        this.cannibalisticHintBGLabel.setText(bundle.getString("ConfigColorPanel.cannibalisticHintBGLabel.text"));
        this.arrowLabel.setText(bundle.getString("ConfigColorPanel.arrowLabel.text"));
        this.normalHintFGButton.setText("...");
        this.normalHintFGButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigColorPanel.this.normalHintFGButtonActionPerformed(evt);
            }
        });
        this.delHintFGButton.setText("...");
        this.delHintFGButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigColorPanel.this.delHintFGButtonActionPerformed(evt);
            }
        });
        this.finsHintFGButton.setText("...");
        this.finsHintFGButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigColorPanel.this.finsHintFGButtonActionPerformed(evt);
            }
        });
        this.endoFinsHintFGButton.setText("...");
        this.endoFinsHintFGButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigColorPanel.this.endoFinsHintFGButtonActionPerformed(evt);
            }
        });
        this.cannibalisticHintFGButton.setText("...");
        this.cannibalisticHintFGButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigColorPanel.this.cannibalisticHintFGButtonActionPerformed(evt);
            }
        });
        this.arrowButton.setText("...");
        this.arrowButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigColorPanel.this.arrowButtonActionPerformed(evt);
            }
        });
        this.normalHintBGButton.setText("...");
        this.normalHintBGButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigColorPanel.this.normalHintBGButtonActionPerformed(evt);
            }
        });
        this.delHintBGButton.setText("...");
        this.delHintBGButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigColorPanel.this.delHintBGButtonActionPerformed(evt);
            }
        });
        this.finsHintBGButton.setText("...");
        this.finsHintBGButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigColorPanel.this.finsHintBGButtonActionPerformed(evt);
            }
        });
        this.endoFinsHintBGButton.setText("...");
        this.endoFinsHintBGButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigColorPanel.this.endoFinsHintBGButtonActionPerformed(evt);
            }
        });
        this.cannibalisticHintBGButton.setText("...");
        this.cannibalisticHintBGButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigColorPanel.this.cannibalisticHintBGButtonActionPerformed(evt);
            }
        });
        GroupLayout hintPanelLayout = new GroupLayout(this.hintPanel);
        this.hintPanel.setLayout(hintPanelLayout);
        hintPanelLayout.setHorizontalGroup(
                hintPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                hintPanelLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                hintPanelLayout.createParallelGroup(Alignment.LEADING)
                                                        .addComponent(this.normHintBGLabel)
                                                        .addComponent(this.delHintBGLabel)
                                                        .addComponent(this.finsHintBGLabel)
                                                        .addComponent(this.endoFinsHintBGLabel)
                                                        .addComponent(this.cannibalisticHintBGLabel)
                                                        .addComponent(this.arrowLabel)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                hintPanelLayout.createParallelGroup(Alignment.LEADING)
                                                        .addComponent(this.arrowButton, -2, 40, -2)
                                                        .addGroup(
                                                                hintPanelLayout.createSequentialGroup()
                                                                        .addComponent(this.cannibalisticHintFGButton, -2, 40, -2)
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addComponent(this.cannibalisticHintBGButton, -2, 40, -2)
                                                        )
                                                        .addGroup(
                                                                hintPanelLayout.createSequentialGroup()
                                                                        .addComponent(this.endoFinsHintFGButton, -2, 40, -2)
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addComponent(this.endoFinsHintBGButton, -2, 40, -2)
                                                        )
                                                        .addGroup(
                                                                hintPanelLayout.createSequentialGroup()
                                                                        .addComponent(this.finsHintFGButton, -2, 40, -2)
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addComponent(this.finsHintBGButton, -2, 40, -2)
                                                        )
                                                        .addGroup(
                                                                hintPanelLayout.createSequentialGroup()
                                                                        .addComponent(this.delHintFGButton, -2, 40, -2)
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addComponent(this.delHintBGButton, -2, 40, -2)
                                                        )
                                                        .addGroup(
                                                                hintPanelLayout.createSequentialGroup()
                                                                        .addComponent(this.normalHintFGButton, -2, 40, -2)
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addComponent(this.normalHintBGButton, -2, 40, -2)
                                                        )
                                        )
                                        .addContainerGap(86, 32767)
                        )
        );
        hintPanelLayout.setVerticalGroup(
                hintPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                hintPanelLayout.createSequentialGroup()
                                        .addGroup(
                                                hintPanelLayout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(this.normHintBGLabel)
                                                        .addComponent(this.normalHintFGButton, -2, 21, -2)
                                                        .addComponent(this.normalHintBGButton, -2, 21, -2)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                hintPanelLayout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(this.delHintBGLabel)
                                                        .addComponent(this.delHintFGButton, -2, 21, -2)
                                                        .addComponent(this.delHintBGButton, -2, 21, -2)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                hintPanelLayout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(this.finsHintBGLabel)
                                                        .addComponent(this.finsHintFGButton, -2, 21, -2)
                                                        .addComponent(this.finsHintBGButton, -2, 21, -2)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                hintPanelLayout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(this.endoFinsHintBGLabel)
                                                        .addComponent(this.endoFinsHintFGButton, -2, 21, -2)
                                                        .addComponent(this.endoFinsHintBGButton, -2, 21, -2)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                hintPanelLayout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(this.cannibalisticHintBGLabel)
                                                        .addComponent(this.cannibalisticHintFGButton, -2, 21, -2)
                                                        .addComponent(this.cannibalisticHintBGButton, -2, 21, -2)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(hintPanelLayout.createParallelGroup(Alignment.BASELINE).addComponent(this.arrowLabel).addComponent(this.arrowButton, -2, 21, -2))
                                        .addContainerGap(-1, 32767)
                        )
        );
        this.coloringPanel.setBorder(BorderFactory.createTitledBorder(bundle.getString("ConfigColorPanel.coloringPanel.border.title")));
        this.stiftLabelA.setText(bundle.getString("ConfigColorPanel.stiftLabelA.text"));
        this.stiftButtona.setText("...");
        this.stiftButtona.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigColorPanel.this.stiftButtonaActionPerformed(evt);
            }
        });
        this.stiftButtonA.setText("...");
        this.stiftButtonA.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigColorPanel.this.stiftButtonAActionPerformed(evt);
            }
        });
        this.stiftButtonB.setText("...");
        this.stiftButtonB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigColorPanel.this.stiftButtonBActionPerformed(evt);
            }
        });
        this.stiftButtonb.setText("...");
        this.stiftButtonb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigColorPanel.this.stiftButtonbActionPerformed(evt);
            }
        });
        this.stiftLabelB.setText(bundle.getString("ConfigColorPanel.stiftLabelB.text"));
        this.stiftLabelC.setText(bundle.getString("ConfigColorPanel.stiftLabelC.text"));
        this.stiftButtonc.setText("...");
        this.stiftButtonc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigColorPanel.this.stiftButtoncActionPerformed(evt);
            }
        });
        this.stiftButtonC.setText("...");
        this.stiftButtonC.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigColorPanel.this.stiftButtonCActionPerformed(evt);
            }
        });
        this.stiftButtonD.setText("...");
        this.stiftButtonD.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigColorPanel.this.stiftButtonDActionPerformed(evt);
            }
        });
        this.stiftButtond.setText("...");
        this.stiftButtond.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigColorPanel.this.stiftButtondActionPerformed(evt);
            }
        });
        this.stiftLabelD.setText(bundle.getString("ConfigColorPanel.stiftLabelD.text"));
        this.stiftLabelE.setText(bundle.getString("ConfigColorPanel.stiftLabelE.text"));
        this.stiftButtone.setText("...");
        this.stiftButtone.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigColorPanel.this.stiftButtoneActionPerformed(evt);
            }
        });
        this.stiftButtonE.setText("...");
        this.stiftButtonE.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigColorPanel.this.stiftButtonEActionPerformed(evt);
            }
        });
        GroupLayout coloringPanelLayout = new GroupLayout(this.coloringPanel);
        this.coloringPanel.setLayout(coloringPanelLayout);
        coloringPanelLayout.setHorizontalGroup(
                coloringPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                coloringPanelLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                coloringPanelLayout.createParallelGroup(Alignment.LEADING)
                                                        .addGroup(
                                                                coloringPanelLayout.createSequentialGroup()
                                                                        .addComponent(this.stiftLabelA)
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addComponent(this.stiftButtona, -2, 40, -2)
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addComponent(this.stiftButtonA, -2, 40, -2)
                                                        )
                                                        .addGroup(
                                                                coloringPanelLayout.createSequentialGroup()
                                                                        .addComponent(this.stiftLabelD)
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addComponent(this.stiftButtond, -2, 40, -2)
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addComponent(this.stiftButtonD, -2, 40, -2)
                                                        )
                                                        .addGroup(
                                                                coloringPanelLayout.createSequentialGroup()
                                                                        .addGroup(
                                                                                coloringPanelLayout.createParallelGroup(Alignment.LEADING).addComponent(this.stiftLabelB).addComponent(this.stiftLabelC)
                                                                        )
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addGroup(
                                                                                coloringPanelLayout.createParallelGroup(Alignment.LEADING)
                                                                                        .addGroup(
                                                                                                coloringPanelLayout.createSequentialGroup()
                                                                                                        .addComponent(this.stiftButtonb, -2, 40, -2)
                                                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                                                        .addComponent(this.stiftButtonB, -2, 40, -2)
                                                                                        )
                                                                                        .addGroup(
                                                                                                coloringPanelLayout.createSequentialGroup()
                                                                                                        .addComponent(this.stiftButtonc, -2, 40, -2)
                                                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                                                        .addComponent(this.stiftButtonC, -2, 40, -2)
                                                                                        )
                                                                        )
                                                        )
                                                        .addGroup(
                                                                coloringPanelLayout.createSequentialGroup()
                                                                        .addComponent(this.stiftLabelE)
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addComponent(this.stiftButtone, -2, 40, -2)
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addComponent(this.stiftButtonE, -2, 40, -2)
                                                        )
                                        )
                                        .addContainerGap(114, 32767)
                        )
        );
        coloringPanelLayout.setVerticalGroup(
                coloringPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                coloringPanelLayout.createSequentialGroup()
                                        .addGroup(
                                                coloringPanelLayout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(this.stiftLabelA)
                                                        .addComponent(this.stiftButtona, -2, 21, -2)
                                                        .addComponent(this.stiftButtonA, -2, 21, -2)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                coloringPanelLayout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(this.stiftLabelB)
                                                        .addComponent(this.stiftButtonb, -2, 21, -2)
                                                        .addComponent(this.stiftButtonB, -2, 21, -2)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                coloringPanelLayout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(this.stiftLabelC)
                                                        .addComponent(this.stiftButtonC, -2, 21, -2)
                                                        .addComponent(this.stiftButtonc, -2, 21, -2)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                coloringPanelLayout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(this.stiftLabelD)
                                                        .addComponent(this.stiftButtond, -2, 21, -2)
                                                        .addComponent(this.stiftButtonD, -2, 21, -2)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED, -1, 32767)
                                        .addGroup(
                                                coloringPanelLayout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(this.stiftLabelE)
                                                        .addComponent(this.stiftButtone, -2, 21, -2)
                                                        .addComponent(this.stiftButtonE, -2, 21, -2)
                                        )
                                        .addContainerGap()
                        )
        );
        this.resetButton.setMnemonic(ResourceBundle.getBundle("intl/ConfigColorPanel").getString("ConfigColorPanel.resetButton.mnemonic").charAt(0));
        this.resetButton.setText(bundle.getString("ConfigColorPanel.resetButton.text"));
        this.resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigColorPanel.this.resetButtonActionPerformed(evt);
            }
        });
        GroupLayout jPanel2Layout = new GroupLayout(this.jPanel2);
        this.jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(Alignment.TRAILING, jPanel2Layout.createSequentialGroup().addContainerGap(141, 32767).addComponent(this.resetButton).addGap(10, 10, 10))
                        .addGroup(
                                Alignment.TRAILING,
                                jPanel2Layout.createSequentialGroup()
                                        .addGroup(
                                                jPanel2Layout.createParallelGroup(Alignment.TRAILING)
                                                        .addComponent(this.coloringPanel, Alignment.LEADING, -1, -1, 32767)
                                                        .addComponent(this.hintPanel, -1, -1, 32767)
                                        )
                                        .addContainerGap()
                        )
        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                jPanel2Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(this.hintPanel, -2, -1, -2)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.coloringPanel, -2, -1, -2)
                                        .addPreferredGap(ComponentPlacement.RELATED, 57, 32767)
                                        .addComponent(this.resetButton)
                                        .addContainerGap()
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

    private void alsHintBGButton4ActionPerformed(ActionEvent evt) {
        this.chooseColor(29);
    }

    private void alsHintFGButton4ActionPerformed(ActionEvent evt) {
        this.chooseColor(28);
    }

    private void alsHintBGButton3ActionPerformed(ActionEvent evt) {
        this.chooseColor(27);
    }

    private void alsHintFGButton3ActionPerformed(ActionEvent evt) {
        this.chooseColor(26);
    }

    private void alsHintBGButton2ActionPerformed(ActionEvent evt) {
        this.chooseColor(25);
    }

    private void alsHintFGButton2ActionPerformed(ActionEvent evt) {
        this.chooseColor(24);
    }

    private void alsHintBGButton1ActionPerformed(ActionEvent evt) {
        this.chooseColor(23);
    }

    private void alsHintFGButton1ActionPerformed(ActionEvent evt) {
        this.chooseColor(22);
    }

    private void cannibalisticHintBGButtonActionPerformed(ActionEvent evt) {
        this.chooseColor(20);
    }

    private void endoFinsHintBGButtonActionPerformed(ActionEvent evt) {
        this.chooseColor(18);
    }

    private void finsHintBGButtonActionPerformed(ActionEvent evt) {
        this.chooseColor(16);
    }

    private void delHintBGButtonActionPerformed(ActionEvent evt) {
        this.chooseColor(14);
    }

    private void normalHintBGButtonActionPerformed(ActionEvent evt) {
        this.chooseColor(12);
    }

    private void arrowButtonActionPerformed(ActionEvent evt) {
        this.chooseColor(21);
    }

    private void cannibalisticHintFGButtonActionPerformed(ActionEvent evt) {
        this.chooseColor(19);
    }

    private void endoFinsHintFGButtonActionPerformed(ActionEvent evt) {
        this.chooseColor(17);
    }

    private void finsHintFGButtonActionPerformed(ActionEvent evt) {
        this.chooseColor(15);
    }

    private void delHintFGButtonActionPerformed(ActionEvent evt) {
        this.chooseColor(13);
    }

    private void normalHintFGButtonActionPerformed(ActionEvent evt) {
        this.chooseColor(11);
    }

    private void validBGButtonActionPerformed(ActionEvent evt) {
        this.chooseColor(10);
    }

    private void invalidBGButtonActionPerformed(ActionEvent evt) {
        this.chooseColor(9);
    }

    private void cursorBGButtonActionPerformed(ActionEvent evt) {
        this.chooseColor(8);
    }

    private void normalBGButtonActionPerformed(ActionEvent evt) {
        this.chooseColor(7);
    }

    private void wrongButtonActionPerformed(ActionEvent evt) {
        this.chooseColor(6);
    }

    private void invalidFGButtonActionPerformed(ActionEvent evt) {
        this.chooseColor(5);
    }

    private void candidatesButtonActionPerformed(ActionEvent evt) {
        this.chooseColor(4);
    }

    private void valuesButtonActionPerformed(ActionEvent evt) {
        this.chooseColor(3);
    }

    private void cluesButtonActionPerformed(ActionEvent evt) {
        this.chooseColor(2);
    }

    private void frameWeakButtonActionPerformed(ActionEvent evt) {
        this.chooseColor(1);
    }

    private void frameStrongButtonActionPerformed(ActionEvent evt) {
        this.chooseColor(0);
    }

    private void stiftButtonaActionPerformed(ActionEvent evt) {
        this.chooseColor(30);
    }

    private void stiftButtonAActionPerformed(ActionEvent evt) {
        this.chooseColor(31);
    }

    private void stiftButtonBActionPerformed(ActionEvent evt) {
        this.chooseColor(33);
    }

    private void stiftButtonbActionPerformed(ActionEvent evt) {
        this.chooseColor(32);
    }

    private void stiftButtoncActionPerformed(ActionEvent evt) {
        this.chooseColor(34);
    }

    private void stiftButtonCActionPerformed(ActionEvent evt) {
        this.chooseColor(35);
    }

    private void stiftButtonDActionPerformed(ActionEvent evt) {
        this.chooseColor(37);
    }

    private void stiftButtondActionPerformed(ActionEvent evt) {
        this.chooseColor(36);
    }

    private void stiftButtoneActionPerformed(ActionEvent evt) {
        this.chooseColor(38);
    }

    private void stiftButtonEActionPerformed(ActionEvent evt) {
        this.chooseColor(39);
    }

    private void alternateBGButtonActionPerformed(ActionEvent evt) {
        this.chooseColor(40);
    }

    private void chooseColor(int index) {
        Color init = this.colors[index];
        Color color = JColorChooser.showDialog(this, ResourceBundle.getBundle("intl/ConfigColorPanel").getString("ConfigColorPanel.choose_color"), init);
        if (color != null) {
            this.colors[index] = color;
            this.initButton(this.buttons[index], color);
        }
    }

    public void okPressed() {
        Options.getInstance().setGridColor(this.colors[0]);
        Options.getInstance().setInnerGridColor(this.colors[1]);
        Options.getInstance().setCellFixedValueColor(this.colors[2]);
        Options.getInstance().setCellValueColor(this.colors[3]);
        Options.getInstance().setCandidateColor(this.colors[4]);
        Options.getInstance().setWrongValueColor(this.colors[5]);
        Options.getInstance().setDeviationColor(this.colors[6]);
        Options.getInstance().setDefaultCellColor(this.colors[7]);
        Options.getInstance().setAktCellColor(this.colors[8]);
        Options.getInstance().setInvalidCellColor(this.colors[9]);
        Options.getInstance().setPossibleCellColor(this.colors[10]);
        Options.getInstance().setHintCandidateColor(this.colors[11]);
        Options.getInstance().setHintCandidateBackColor(this.colors[12]);
        Options.getInstance().setHintCandidateDeleteColor(this.colors[13]);
        Options.getInstance().setHintCandidateDeleteBackColor(this.colors[14]);
        Options.getInstance().setHintCandidateFinColor(this.colors[15]);
        Options.getInstance().setHintCandidateFinBackColor(this.colors[16]);
        Options.getInstance().setHintCandidateEndoFinColor(this.colors[17]);
        Options.getInstance().setHintCandidateEndoFinBackColor(this.colors[18]);
        Options.getInstance().setHintCandidateCannibalisticColor(this.colors[19]);
        Options.getInstance().setHintCandidateCannibalisticBackColor(this.colors[20]);
        Options.getInstance().setArrowColor(this.colors[21]);
        Options.getInstance().getHintCandidateAlsColors()[0] = this.colors[22];
        Options.getInstance().getHintCandidateAlsBackColors()[0] = this.colors[23];
        Options.getInstance().getHintCandidateAlsColors()[1] = this.colors[24];
        Options.getInstance().getHintCandidateAlsBackColors()[1] = this.colors[25];
        Options.getInstance().getHintCandidateAlsColors()[2] = this.colors[26];
        Options.getInstance().getHintCandidateAlsBackColors()[2] = this.colors[27];
        Options.getInstance().getHintCandidateAlsColors()[3] = this.colors[28];
        Options.getInstance().getHintCandidateAlsBackColors()[3] = this.colors[29];
        Options.getInstance().getColoringColors()[0] = this.colors[30];
        Options.getInstance().getColoringColors()[1] = this.colors[31];
        Options.getInstance().getColoringColors()[2] = this.colors[32];
        Options.getInstance().getColoringColors()[3] = this.colors[33];
        Options.getInstance().getColoringColors()[4] = this.colors[34];
        Options.getInstance().getColoringColors()[5] = this.colors[35];
        Options.getInstance().getColoringColors()[6] = this.colors[36];
        Options.getInstance().getColoringColors()[7] = this.colors[37];
        Options.getInstance().getColoringColors()[8] = this.colors[38];
        Options.getInstance().getColoringColors()[9] = this.colors[39];
        Options.getInstance().setAlternateCellColor(this.colors[40]);
    }

    private void initAll(boolean setDefault) {
        if (this.colors == null) {
            this.colors = new Color[this.buttons.length];
        }

        if (setDefault) {
            this.colors[0] = Options.GRID_COLOR;
            this.colors[1] = Options.INNER_GRID_COLOR;
            this.colors[2] = Options.CELL_FIXED_VALUE_COLOR;
            this.colors[3] = Options.CELL_VALUE_COLOR;
            this.colors[4] = Options.CANDIDATE_COLOR;
            this.colors[5] = Options.WRONG_VALUE_COLOR;
            this.colors[6] = Options.DEVIATION_COLOR;
            this.colors[7] = Options.DEFAULT_CELL_COLOR;
            this.colors[8] = Options.AKT_CELL_COLOR;
            this.colors[9] = Options.INVALID_CELL_COLOR;
            this.colors[10] = Options.POSSIBLE_CELL_COLOR;
            this.colors[11] = Options.HINT_CANDIDATE_COLOR;
            this.colors[12] = Options.HINT_CANDIDATE_BACK_COLOR;
            this.colors[13] = Options.HINT_CANDIDATE_DELETE_COLOR;
            this.colors[14] = Options.HINT_CANDIDATE_DELETE_BACK_COLOR;
            this.colors[15] = Options.HINT_CANDIDATE_FIN_COLOR;
            this.colors[16] = Options.HINT_CANDIDATE_FIN_BACK_COLOR;
            this.colors[17] = Options.HINT_CANDIDATE_ENDO_FIN_COLOR;
            this.colors[18] = Options.HINT_CANDIDATE_ENDO_FIN_BACK_COLOR;
            this.colors[19] = Options.HINT_CANDIDATE_CANNIBALISTIC_COLOR;
            this.colors[20] = Options.HINT_CANDIDATE_CANNIBALISTIC_BACK_COLOR;
            this.colors[21] = Options.ARROW_COLOR;
            this.colors[22] = Options.HINT_CANDIDATE_ALS_COLORS[0];
            this.colors[23] = Options.HINT_CANDIDATE_ALS_BACK_COLORS[0];
            this.colors[24] = Options.HINT_CANDIDATE_ALS_COLORS[1];
            this.colors[25] = Options.HINT_CANDIDATE_ALS_BACK_COLORS[1];
            this.colors[26] = Options.HINT_CANDIDATE_ALS_COLORS[2];
            this.colors[27] = Options.HINT_CANDIDATE_ALS_BACK_COLORS[2];
            this.colors[28] = Options.HINT_CANDIDATE_ALS_COLORS[3];
            this.colors[29] = Options.HINT_CANDIDATE_ALS_BACK_COLORS[3];
            this.colors[30] = Options.COLORING_COLORS[0];
            this.colors[31] = Options.COLORING_COLORS[1];
            this.colors[32] = Options.COLORING_COLORS[2];
            this.colors[33] = Options.COLORING_COLORS[3];
            this.colors[34] = Options.COLORING_COLORS[4];
            this.colors[35] = Options.COLORING_COLORS[5];
            this.colors[36] = Options.COLORING_COLORS[6];
            this.colors[37] = Options.COLORING_COLORS[7];
            this.colors[38] = Options.COLORING_COLORS[8];
            this.colors[39] = Options.COLORING_COLORS[9];
            this.colors[40] = Options.ALTERNATE_CELL_COLOR;
        } else {
            this.colors[0] = Options.getInstance().getGridColor();
            this.colors[1] = Options.getInstance().getInnerGridColor();
            this.colors[2] = Options.getInstance().getCellFixedValueColor();
            this.colors[3] = Options.getInstance().getCellValueColor();
            this.colors[4] = Options.getInstance().getCandidateColor();
            this.colors[5] = Options.getInstance().getWrongValueColor();
            this.colors[6] = Options.getInstance().getDeviationColor();
            this.colors[7] = Options.getInstance().getDefaultCellColor();
            this.colors[8] = Options.getInstance().getAktCellColor();
            this.colors[9] = Options.getInstance().getInvalidCellColor();
            this.colors[10] = Options.getInstance().getPossibleCellColor();
            this.colors[11] = Options.getInstance().getHintCandidateColor();
            this.colors[12] = Options.getInstance().getHintCandidateBackColor();
            this.colors[13] = Options.getInstance().getHintCandidateDeleteColor();
            this.colors[14] = Options.getInstance().getHintCandidateDeleteBackColor();
            this.colors[15] = Options.getInstance().getHintCandidateFinColor();
            this.colors[16] = Options.getInstance().getHintCandidateFinBackColor();
            this.colors[17] = Options.getInstance().getHintCandidateEndoFinColor();
            this.colors[18] = Options.getInstance().getHintCandidateEndoFinBackColor();
            this.colors[19] = Options.getInstance().getHintCandidateCannibalisticColor();
            this.colors[20] = Options.getInstance().getHintCandidateCannibalisticBackColor();
            this.colors[21] = Options.getInstance().getArrowColor();
            this.colors[22] = Options.getInstance().getHintCandidateAlsColors()[0];
            this.colors[23] = Options.getInstance().getHintCandidateAlsBackColors()[0];
            this.colors[24] = Options.getInstance().getHintCandidateAlsColors()[1];
            this.colors[25] = Options.getInstance().getHintCandidateAlsBackColors()[1];
            this.colors[26] = Options.getInstance().getHintCandidateAlsColors()[2];
            this.colors[27] = Options.getInstance().getHintCandidateAlsBackColors()[2];
            this.colors[28] = Options.getInstance().getHintCandidateAlsColors()[3];
            this.colors[29] = Options.getInstance().getHintCandidateAlsBackColors()[3];
            this.colors[30] = Options.getInstance().getColoringColors()[0];
            this.colors[31] = Options.getInstance().getColoringColors()[1];
            this.colors[32] = Options.getInstance().getColoringColors()[2];
            this.colors[33] = Options.getInstance().getColoringColors()[3];
            this.colors[34] = Options.getInstance().getColoringColors()[4];
            this.colors[35] = Options.getInstance().getColoringColors()[5];
            this.colors[36] = Options.getInstance().getColoringColors()[6];
            this.colors[37] = Options.getInstance().getColoringColors()[7];
            this.colors[38] = Options.getInstance().getColoringColors()[8];
            this.colors[39] = Options.getInstance().getColoringColors()[9];
            this.colors[40] = Options.getInstance().getAlternateCellColor();
        }

        for (int i = 0; i < this.buttons.length; i++) {
            this.initButton(this.buttons[i], this.colors[i]);
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
