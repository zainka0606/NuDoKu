package sudoku;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ResourceBundle;
import java.util.SortedMap;

public class CellZoomPanel extends JPanel {
    private static final int X_OFFSET = 10;
    private static final int Y_OFFSET = 33;
    private static final int SMALL_GAP = 6;
    private static final int LARGE_GAP = 14;
    private static final int COLOR_PANEL_MAX_HEIGHT = 50;
    private static final int DIFF_SIZE = 1;
    private static final String[] NUMBERS = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9"};
    private MainFrame mainFrame;
    private Font buttonFont = null;
    private Font iconFont = null;
    private int buttonFontSize = -1;
    private int defaultButtonFontSize = -1;
    private int defaultButtonHeight = -1;
    private JButton[] setValueButtons = null;
    private JButton[] toggleCandidatesButtons = null;
    private JPanel[] cellPanels = null;
    private JPanel[] candidatePanels = null;
    private Color normButtonForeground = null;
    private Color normButtonBackground = null;
    private int aktColor = -1;
    private SudokuPanel sudokuPanel;
    private int colorImageHeight = -1;
    private Icon[] colorKuIcons = new Icon[9];
    private JPanel candidateColorPanel;
    private JLabel cellColorLabel;
    private JPanel cellColorPanel;
    private JPanel chooseCandidateColor0Panel;
    private JPanel chooseCandidateColor1Panel;
    private JPanel chooseCandidateColor2Panel;
    private JPanel chooseCandidateColor3Panel;
    private JPanel chooseCandidateColor4Panel;
    private JPanel chooseCandidateColor5Panel;
    private JPanel chooseCandidateColor6Panel;
    private JPanel chooseCandidateColor7Panel;
    private JPanel chooseCandidateColor8Panel;
    private JPanel chooseCandidateColor9Panel;
    private JLabel chooseCandidateColorLabel;
    private JPanel chooseCandidateColorM1Panel;
    private JPanel chooseCandidateColorM2Panel;
    private JPanel chooseCandidateColorPanel;
    private JPanel chooseCellColor0Panel;
    private JPanel chooseCellColor1Panel;
    private JPanel chooseCellColor2Panel;
    private JPanel chooseCellColor3Panel;
    private JPanel chooseCellColor4Panel;
    private JPanel chooseCellColor5Panel;
    private JPanel chooseCellColor6Panel;
    private JPanel chooseCellColor7Panel;
    private JPanel chooseCellColor8Panel;
    private JPanel chooseCellColor9Panel;
    private JPanel chooseCellColorM1Panel;
    private JPanel chooseCellColorM2Panel;
    private JPanel chooseCellColorPanel;
    private JButton jFontButton;
    private JPanel jPanel1;
    private JButton setValueButton1;
    private JButton setValueButton2;
    private JButton setValueButton3;
    private JButton setValueButton4;
    private JButton setValueButton5;
    private JButton setValueButton6;
    private JButton setValueButton7;
    private JButton setValueButton8;
    private JButton setValueButton9;
    private JLabel setValueLabel;
    private JPanel setValuePanel;
    private JLabel titleLabel;
    private JButton toggleCandidatesButton1;
    private JButton toggleCandidatesButton2;
    private JButton toggleCandidatesButton3;
    private JButton toggleCandidatesButton4;
    private JButton toggleCandidatesButton5;
    private JButton toggleCandidatesButton6;
    private JButton toggleCandidatesButton7;
    private JButton toggleCandidatesButton8;
    private JButton toggleCandidatesButton9;
    private JLabel toggleCandidatesLabel;
    private JPanel toggleCandidatesPanel;

    public CellZoomPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.initComponents();
        this.setValueButtons = new JButton[]{
                this.setValueButton1,
                this.setValueButton2,
                this.setValueButton3,
                this.setValueButton4,
                this.setValueButton5,
                this.setValueButton6,
                this.setValueButton7,
                this.setValueButton8,
                this.setValueButton9
        };
        this.toggleCandidatesButtons = new JButton[]{
                this.toggleCandidatesButton1,
                this.toggleCandidatesButton2,
                this.toggleCandidatesButton3,
                this.toggleCandidatesButton4,
                this.toggleCandidatesButton5,
                this.toggleCandidatesButton6,
                this.toggleCandidatesButton7,
                this.toggleCandidatesButton8,
                this.toggleCandidatesButton9
        };
        this.normButtonForeground = this.setValueButton1.getForeground();
        this.normButtonBackground = this.setValueButton1.getBackground();
        this.cellPanels = new JPanel[]{
                this.chooseCellColorM2Panel,
                this.chooseCellColorM1Panel,
                this.chooseCellColor0Panel,
                this.chooseCellColor1Panel,
                this.chooseCellColor2Panel,
                this.chooseCellColor3Panel,
                this.chooseCellColor4Panel,
                this.chooseCellColor5Panel,
                this.chooseCellColor6Panel,
                this.chooseCellColor7Panel,
                this.chooseCellColor8Panel,
                this.chooseCellColor9Panel
        };
        this.candidatePanels = new JPanel[]{
                this.chooseCandidateColorM2Panel,
                this.chooseCandidateColorM1Panel,
                this.chooseCandidateColor0Panel,
                this.chooseCandidateColor1Panel,
                this.chooseCandidateColor2Panel,
                this.chooseCandidateColor3Panel,
                this.chooseCandidateColor4Panel,
                this.chooseCandidateColor5Panel,
                this.chooseCandidateColor6Panel,
                this.chooseCandidateColor7Panel,
                this.chooseCandidateColor8Panel,
                this.chooseCandidateColor9Panel
        };
        this.jFontButton.setVisible(false);
        this.buttonFont = this.jFontButton.getFont();
        this.buttonFontSize = 11;
        this.defaultButtonFontSize = this.buttonFontSize;
        this.defaultButtonHeight = 23;
        this.iconFont = new Font(this.buttonFont.getName(), this.buttonFont.getStyle(), this.defaultButtonFontSize - 1);
        int fontSize = Math.max(12, this.getFont().getSize());
        Font font = this.titleLabel.getFont();
        this.titleLabel.setFont(new Font(font.getName(), 1, fontSize));
        this.calculateLayout();
    }

    private void initComponents() {
        this.jPanel1 = new JPanel();
        this.titleLabel = new JLabel();
        this.setValueLabel = new JLabel();
        this.setValuePanel = new JPanel();
        this.setValueButton1 = new JButton();
        this.setValueButton2 = new JButton();
        this.setValueButton3 = new JButton();
        this.setValueButton4 = new JButton();
        this.setValueButton5 = new JButton();
        this.setValueButton6 = new JButton();
        this.setValueButton7 = new JButton();
        this.setValueButton8 = new JButton();
        this.setValueButton9 = new JButton();
        this.toggleCandidatesLabel = new JLabel();
        this.toggleCandidatesPanel = new JPanel();
        this.toggleCandidatesButton1 = new JButton();
        this.toggleCandidatesButton2 = new JButton();
        this.toggleCandidatesButton3 = new JButton();
        this.toggleCandidatesButton4 = new JButton();
        this.toggleCandidatesButton5 = new JButton();
        this.toggleCandidatesButton6 = new JButton();
        this.toggleCandidatesButton7 = new JButton();
        this.toggleCandidatesButton8 = new JButton();
        this.toggleCandidatesButton9 = new JButton();
        this.cellColorLabel = new JLabel();
        this.cellColorPanel = new JPanel();
        this.chooseCellColorPanel = new JPanel();
        this.chooseCellColor0Panel = new StatusColorPanel(0);
        this.chooseCellColor2Panel = new StatusColorPanel(2);
        this.chooseCellColor4Panel = new StatusColorPanel(4);
        this.chooseCellColor6Panel = new StatusColorPanel(6);
        this.chooseCellColor8Panel = new StatusColorPanel(8);
        this.chooseCellColorM1Panel = new StatusColorPanel(-1);
        this.chooseCellColor1Panel = new StatusColorPanel(1);
        this.chooseCellColor3Panel = new StatusColorPanel(3);
        this.chooseCellColor5Panel = new StatusColorPanel(5);
        this.chooseCellColor7Panel = new StatusColorPanel(7);
        this.chooseCellColor9Panel = new StatusColorPanel(9);
        this.chooseCellColorM2Panel = new StatusColorPanel(-2);
        this.chooseCandidateColorLabel = new JLabel();
        this.candidateColorPanel = new JPanel();
        this.chooseCandidateColorPanel = new JPanel();
        this.chooseCandidateColor0Panel = new StatusColorPanel(0);
        this.chooseCandidateColor2Panel = new StatusColorPanel(2);
        this.chooseCandidateColor4Panel = new StatusColorPanel(4);
        this.chooseCandidateColor6Panel = new StatusColorPanel(6);
        this.chooseCandidateColor8Panel = new StatusColorPanel(8);
        this.chooseCandidateColorM1Panel = new StatusColorPanel(-1);
        this.chooseCandidateColor1Panel = new StatusColorPanel(1);
        this.chooseCandidateColor3Panel = new StatusColorPanel(3);
        this.chooseCandidateColor5Panel = new StatusColorPanel(5);
        this.chooseCandidateColor7Panel = new StatusColorPanel(7);
        this.chooseCandidateColor9Panel = new StatusColorPanel(9);
        this.chooseCandidateColorM2Panel = new StatusColorPanel(-2);
        this.jFontButton = new JButton();
        GroupLayout jPanel1Layout = new GroupLayout(this.jPanel1);
        this.jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING).addGap(0, 100, 32767));
        jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING).addGap(0, 100, 32767));
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent evt) {
                CellZoomPanel.this.formComponentResized(evt);
            }
        });
        this.setLayout(null);
        this.titleLabel.setBackground(new Color(0, 51, 255));
        this.titleLabel.setFont(new Font("Tahoma", 1, 12));
        this.titleLabel.setForeground(new Color(255, 255, 255));
        this.titleLabel.setHorizontalAlignment(0);
        ResourceBundle bundle = ResourceBundle.getBundle("intl/CellZoomPanel");
        this.titleLabel.setText(bundle.getString("CellZoomPanel.titleLabel.text"));
        this.titleLabel.setOpaque(true);
        this.add(this.titleLabel);
        this.titleLabel.setBounds(0, 0, 63, 15);
        this.setValueLabel.setHorizontalAlignment(0);
        this.setValueLabel.setText(bundle.getString("CellZoomPanel.setValueLabel.text"));
        this.add(this.setValueLabel);
        this.setValueLabel.setBounds(0, 0, 49, 14);
        this.setValuePanel.setLayout(new GridLayout(3, 3));
        this.setValueButton1.setText("1");
        this.setValueButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                CellZoomPanel.this.setValueButton1ActionPerformed(evt);
            }
        });
        this.setValuePanel.add(this.setValueButton1);
        this.setValueButton2.setText("2");
        this.setValueButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                CellZoomPanel.this.setValueButton1ActionPerformed(evt);
            }
        });
        this.setValuePanel.add(this.setValueButton2);
        this.setValueButton3.setText("3");
        this.setValueButton3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                CellZoomPanel.this.setValueButton1ActionPerformed(evt);
            }
        });
        this.setValuePanel.add(this.setValueButton3);
        this.setValueButton4.setText("4");
        this.setValueButton4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                CellZoomPanel.this.setValueButton1ActionPerformed(evt);
            }
        });
        this.setValuePanel.add(this.setValueButton4);
        this.setValueButton5.setText("5");
        this.setValueButton5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                CellZoomPanel.this.setValueButton1ActionPerformed(evt);
            }
        });
        this.setValuePanel.add(this.setValueButton5);
        this.setValueButton6.setText("6");
        this.setValueButton6.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                CellZoomPanel.this.setValueButton1ActionPerformed(evt);
            }
        });
        this.setValuePanel.add(this.setValueButton6);
        this.setValueButton7.setText("7");
        this.setValueButton7.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                CellZoomPanel.this.setValueButton1ActionPerformed(evt);
            }
        });
        this.setValuePanel.add(this.setValueButton7);
        this.setValueButton8.setText("8");
        this.setValueButton8.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                CellZoomPanel.this.setValueButton1ActionPerformed(evt);
            }
        });
        this.setValuePanel.add(this.setValueButton8);
        this.setValueButton9.setText("9");
        this.setValueButton9.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                CellZoomPanel.this.setValueButton1ActionPerformed(evt);
            }
        });
        this.setValuePanel.add(this.setValueButton9);
        this.add(this.setValuePanel);
        this.setValuePanel.setBounds(0, 0, 117, 69);
        this.toggleCandidatesLabel.setHorizontalAlignment(0);
        this.toggleCandidatesLabel.setText(bundle.getString("CellZoomPanel.toggleCandidatesLabel.text"));
        this.add(this.toggleCandidatesLabel);
        this.toggleCandidatesLabel.setBounds(0, 0, 93, 14);
        this.toggleCandidatesPanel.setLayout(new GridLayout(3, 3));
        this.toggleCandidatesButton1.setText("1");
        this.toggleCandidatesButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                CellZoomPanel.this.toggleCandidatesButton1ActionPerformed(evt);
            }
        });
        this.toggleCandidatesPanel.add(this.toggleCandidatesButton1);
        this.toggleCandidatesButton2.setText("2");
        this.toggleCandidatesButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                CellZoomPanel.this.toggleCandidatesButton1ActionPerformed(evt);
            }
        });
        this.toggleCandidatesPanel.add(this.toggleCandidatesButton2);
        this.toggleCandidatesButton3.setText("3");
        this.toggleCandidatesButton3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                CellZoomPanel.this.toggleCandidatesButton1ActionPerformed(evt);
            }
        });
        this.toggleCandidatesPanel.add(this.toggleCandidatesButton3);
        this.toggleCandidatesButton4.setText("4");
        this.toggleCandidatesButton4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                CellZoomPanel.this.toggleCandidatesButton1ActionPerformed(evt);
            }
        });
        this.toggleCandidatesPanel.add(this.toggleCandidatesButton4);
        this.toggleCandidatesButton5.setText("5");
        this.toggleCandidatesButton5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                CellZoomPanel.this.toggleCandidatesButton1ActionPerformed(evt);
            }
        });
        this.toggleCandidatesPanel.add(this.toggleCandidatesButton5);
        this.toggleCandidatesButton6.setText("6");
        this.toggleCandidatesButton6.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                CellZoomPanel.this.toggleCandidatesButton1ActionPerformed(evt);
            }
        });
        this.toggleCandidatesPanel.add(this.toggleCandidatesButton6);
        this.toggleCandidatesButton7.setText("7");
        this.toggleCandidatesButton7.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                CellZoomPanel.this.toggleCandidatesButton1ActionPerformed(evt);
            }
        });
        this.toggleCandidatesPanel.add(this.toggleCandidatesButton7);
        this.toggleCandidatesButton8.setText("8");
        this.toggleCandidatesButton8.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                CellZoomPanel.this.toggleCandidatesButton1ActionPerformed(evt);
            }
        });
        this.toggleCandidatesPanel.add(this.toggleCandidatesButton8);
        this.toggleCandidatesButton9.setText("9");
        this.toggleCandidatesButton9.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                CellZoomPanel.this.toggleCandidatesButton1ActionPerformed(evt);
            }
        });
        this.toggleCandidatesPanel.add(this.toggleCandidatesButton9);
        this.add(this.toggleCandidatesPanel);
        this.toggleCandidatesPanel.setBounds(0, 0, 117, 69);
        this.cellColorLabel.setHorizontalAlignment(0);
        this.cellColorLabel.setText(bundle.getString("CellZoomPanel.colorCellsLabel.text"));
        this.add(this.cellColorLabel);
        this.cellColorLabel.setBounds(0, 0, 105, 14);
        this.cellColorPanel.setBackground(new Color(255, 255, 255));
        this.cellColorPanel.setBorder(BorderFactory.createBevelBorder(1));
        GroupLayout cellColorPanelLayout = new GroupLayout(this.cellColorPanel);
        this.cellColorPanel.setLayout(cellColorPanelLayout);
        cellColorPanelLayout.setHorizontalGroup(cellColorPanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 41, 32767));
        cellColorPanelLayout.setVerticalGroup(cellColorPanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 0, 32767));
        this.add(this.cellColorPanel);
        this.cellColorPanel.setBounds(0, 0, 45, 4);
        this.chooseCellColorPanel.setLayout(new GridLayout(2, 6, 1, 1));
        this.chooseCellColor0Panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                CellZoomPanel.this.chooseCellColor0PanelMouseClicked(evt);
            }
        });
        GroupLayout chooseCellColor0PanelLayout = new GroupLayout(this.chooseCellColor0Panel);
        this.chooseCellColor0Panel.setLayout(chooseCellColor0PanelLayout);
        chooseCellColor0PanelLayout.setHorizontalGroup(chooseCellColor0PanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 18, 32767));
        chooseCellColor0PanelLayout.setVerticalGroup(chooseCellColor0PanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 1, 32767));
        this.chooseCellColorPanel.add(this.chooseCellColor0Panel);
        this.chooseCellColor2Panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                CellZoomPanel.this.chooseCellColor0PanelMouseClicked(evt);
            }
        });
        GroupLayout chooseCellColor2PanelLayout = new GroupLayout(this.chooseCellColor2Panel);
        this.chooseCellColor2Panel.setLayout(chooseCellColor2PanelLayout);
        chooseCellColor2PanelLayout.setHorizontalGroup(chooseCellColor2PanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 18, 32767));
        chooseCellColor2PanelLayout.setVerticalGroup(chooseCellColor2PanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 1, 32767));
        this.chooseCellColorPanel.add(this.chooseCellColor2Panel);
        this.chooseCellColor4Panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                CellZoomPanel.this.chooseCellColor0PanelMouseClicked(evt);
            }
        });
        GroupLayout chooseCellColor4PanelLayout = new GroupLayout(this.chooseCellColor4Panel);
        this.chooseCellColor4Panel.setLayout(chooseCellColor4PanelLayout);
        chooseCellColor4PanelLayout.setHorizontalGroup(chooseCellColor4PanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 18, 32767));
        chooseCellColor4PanelLayout.setVerticalGroup(chooseCellColor4PanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 1, 32767));
        this.chooseCellColorPanel.add(this.chooseCellColor4Panel);
        this.chooseCellColor6Panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                CellZoomPanel.this.chooseCellColor0PanelMouseClicked(evt);
            }
        });
        GroupLayout chooseCellColor6PanelLayout = new GroupLayout(this.chooseCellColor6Panel);
        this.chooseCellColor6Panel.setLayout(chooseCellColor6PanelLayout);
        chooseCellColor6PanelLayout.setHorizontalGroup(chooseCellColor6PanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 18, 32767));
        chooseCellColor6PanelLayout.setVerticalGroup(chooseCellColor6PanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 1, 32767));
        this.chooseCellColorPanel.add(this.chooseCellColor6Panel);
        this.chooseCellColor8Panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                CellZoomPanel.this.chooseCellColor0PanelMouseClicked(evt);
            }
        });
        GroupLayout chooseCellColor8PanelLayout = new GroupLayout(this.chooseCellColor8Panel);
        this.chooseCellColor8Panel.setLayout(chooseCellColor8PanelLayout);
        chooseCellColor8PanelLayout.setHorizontalGroup(chooseCellColor8PanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 18, 32767));
        chooseCellColor8PanelLayout.setVerticalGroup(chooseCellColor8PanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 1, 32767));
        this.chooseCellColorPanel.add(this.chooseCellColor8Panel);
        this.chooseCellColorM1Panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                CellZoomPanel.this.chooseCellColor0PanelMouseClicked(evt);
            }
        });
        GroupLayout chooseCellColorM1PanelLayout = new GroupLayout(this.chooseCellColorM1Panel);
        this.chooseCellColorM1Panel.setLayout(chooseCellColorM1PanelLayout);
        chooseCellColorM1PanelLayout.setHorizontalGroup(chooseCellColorM1PanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 18, 32767));
        chooseCellColorM1PanelLayout.setVerticalGroup(chooseCellColorM1PanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 1, 32767));
        this.chooseCellColorPanel.add(this.chooseCellColorM1Panel);
        this.chooseCellColor1Panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                CellZoomPanel.this.chooseCellColor0PanelMouseClicked(evt);
            }
        });
        GroupLayout chooseCellColor1PanelLayout = new GroupLayout(this.chooseCellColor1Panel);
        this.chooseCellColor1Panel.setLayout(chooseCellColor1PanelLayout);
        chooseCellColor1PanelLayout.setHorizontalGroup(chooseCellColor1PanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 18, 32767));
        chooseCellColor1PanelLayout.setVerticalGroup(chooseCellColor1PanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 1, 32767));
        this.chooseCellColorPanel.add(this.chooseCellColor1Panel);
        this.chooseCellColor3Panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                CellZoomPanel.this.chooseCellColor0PanelMouseClicked(evt);
            }
        });
        GroupLayout chooseCellColor3PanelLayout = new GroupLayout(this.chooseCellColor3Panel);
        this.chooseCellColor3Panel.setLayout(chooseCellColor3PanelLayout);
        chooseCellColor3PanelLayout.setHorizontalGroup(chooseCellColor3PanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 18, 32767));
        chooseCellColor3PanelLayout.setVerticalGroup(chooseCellColor3PanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 1, 32767));
        this.chooseCellColorPanel.add(this.chooseCellColor3Panel);
        this.chooseCellColor5Panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                CellZoomPanel.this.chooseCellColor0PanelMouseClicked(evt);
            }
        });
        GroupLayout chooseCellColor5PanelLayout = new GroupLayout(this.chooseCellColor5Panel);
        this.chooseCellColor5Panel.setLayout(chooseCellColor5PanelLayout);
        chooseCellColor5PanelLayout.setHorizontalGroup(chooseCellColor5PanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 18, 32767));
        chooseCellColor5PanelLayout.setVerticalGroup(chooseCellColor5PanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 1, 32767));
        this.chooseCellColorPanel.add(this.chooseCellColor5Panel);
        this.chooseCellColor7Panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                CellZoomPanel.this.chooseCellColor0PanelMouseClicked(evt);
            }
        });
        GroupLayout chooseCellColor7PanelLayout = new GroupLayout(this.chooseCellColor7Panel);
        this.chooseCellColor7Panel.setLayout(chooseCellColor7PanelLayout);
        chooseCellColor7PanelLayout.setHorizontalGroup(chooseCellColor7PanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 18, 32767));
        chooseCellColor7PanelLayout.setVerticalGroup(chooseCellColor7PanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 1, 32767));
        this.chooseCellColorPanel.add(this.chooseCellColor7Panel);
        this.chooseCellColor9Panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                CellZoomPanel.this.chooseCellColor0PanelMouseClicked(evt);
            }
        });
        GroupLayout chooseCellColor9PanelLayout = new GroupLayout(this.chooseCellColor9Panel);
        this.chooseCellColor9Panel.setLayout(chooseCellColor9PanelLayout);
        chooseCellColor9PanelLayout.setHorizontalGroup(chooseCellColor9PanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 18, 32767));
        chooseCellColor9PanelLayout.setVerticalGroup(chooseCellColor9PanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 1, 32767));
        this.chooseCellColorPanel.add(this.chooseCellColor9Panel);
        this.chooseCellColorM2Panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                CellZoomPanel.this.chooseCellColor0PanelMouseClicked(evt);
            }
        });
        GroupLayout chooseCellColorM2PanelLayout = new GroupLayout(this.chooseCellColorM2Panel);
        this.chooseCellColorM2Panel.setLayout(chooseCellColorM2PanelLayout);
        chooseCellColorM2PanelLayout.setHorizontalGroup(chooseCellColorM2PanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 18, 32767));
        chooseCellColorM2PanelLayout.setVerticalGroup(chooseCellColorM2PanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 1, 32767));
        this.chooseCellColorPanel.add(this.chooseCellColorM2Panel);
        this.add(this.chooseCellColorPanel);
        this.chooseCellColorPanel.setBounds(0, 0, 113, 3);
        this.chooseCandidateColorLabel.setHorizontalAlignment(0);
        this.chooseCandidateColorLabel.setText(bundle.getString("CellZoomPanel.chooseCandidateColorLabel.text"));
        this.add(this.chooseCandidateColorLabel);
        this.chooseCandidateColorLabel.setBounds(0, 0, 142, 14);
        this.candidateColorPanel.setBackground(new Color(255, 255, 255));
        this.candidateColorPanel.setBorder(BorderFactory.createBevelBorder(1));
        GroupLayout candidateColorPanelLayout = new GroupLayout(this.candidateColorPanel);
        this.candidateColorPanel.setLayout(candidateColorPanelLayout);
        candidateColorPanelLayout.setHorizontalGroup(candidateColorPanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 41, 32767));
        candidateColorPanelLayout.setVerticalGroup(candidateColorPanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 40, 32767));
        this.add(this.candidateColorPanel);
        this.candidateColorPanel.setBounds(0, 0, 45, 44);
        this.chooseCandidateColorPanel.setLayout(new GridLayout(2, 5, 1, 1));
        this.chooseCandidateColor0Panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                CellZoomPanel.this.chooseCellColor0PanelMouseClicked(evt);
            }
        });
        GroupLayout chooseCandidateColor0PanelLayout = new GroupLayout(this.chooseCandidateColor0Panel);
        this.chooseCandidateColor0Panel.setLayout(chooseCandidateColor0PanelLayout);
        chooseCandidateColor0PanelLayout.setHorizontalGroup(chooseCandidateColor0PanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 18, 32767));
        chooseCandidateColor0PanelLayout.setVerticalGroup(chooseCandidateColor0PanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 21, 32767));
        this.chooseCandidateColorPanel.add(this.chooseCandidateColor0Panel);
        this.chooseCandidateColor2Panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                CellZoomPanel.this.chooseCellColor0PanelMouseClicked(evt);
            }
        });
        GroupLayout chooseCandidateColor2PanelLayout = new GroupLayout(this.chooseCandidateColor2Panel);
        this.chooseCandidateColor2Panel.setLayout(chooseCandidateColor2PanelLayout);
        chooseCandidateColor2PanelLayout.setHorizontalGroup(chooseCandidateColor2PanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 18, 32767));
        chooseCandidateColor2PanelLayout.setVerticalGroup(chooseCandidateColor2PanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 21, 32767));
        this.chooseCandidateColorPanel.add(this.chooseCandidateColor2Panel);
        this.chooseCandidateColor4Panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                CellZoomPanel.this.chooseCellColor0PanelMouseClicked(evt);
            }
        });
        GroupLayout chooseCandidateColor4PanelLayout = new GroupLayout(this.chooseCandidateColor4Panel);
        this.chooseCandidateColor4Panel.setLayout(chooseCandidateColor4PanelLayout);
        chooseCandidateColor4PanelLayout.setHorizontalGroup(chooseCandidateColor4PanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 18, 32767));
        chooseCandidateColor4PanelLayout.setVerticalGroup(chooseCandidateColor4PanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 21, 32767));
        this.chooseCandidateColorPanel.add(this.chooseCandidateColor4Panel);
        this.chooseCandidateColor6Panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                CellZoomPanel.this.chooseCellColor0PanelMouseClicked(evt);
            }
        });
        GroupLayout chooseCandidateColor6PanelLayout = new GroupLayout(this.chooseCandidateColor6Panel);
        this.chooseCandidateColor6Panel.setLayout(chooseCandidateColor6PanelLayout);
        chooseCandidateColor6PanelLayout.setHorizontalGroup(chooseCandidateColor6PanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 18, 32767));
        chooseCandidateColor6PanelLayout.setVerticalGroup(chooseCandidateColor6PanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 21, 32767));
        this.chooseCandidateColorPanel.add(this.chooseCandidateColor6Panel);
        this.chooseCandidateColor8Panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                CellZoomPanel.this.chooseCellColor0PanelMouseClicked(evt);
            }
        });
        GroupLayout chooseCandidateColor8PanelLayout = new GroupLayout(this.chooseCandidateColor8Panel);
        this.chooseCandidateColor8Panel.setLayout(chooseCandidateColor8PanelLayout);
        chooseCandidateColor8PanelLayout.setHorizontalGroup(chooseCandidateColor8PanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 18, 32767));
        chooseCandidateColor8PanelLayout.setVerticalGroup(chooseCandidateColor8PanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 21, 32767));
        this.chooseCandidateColorPanel.add(this.chooseCandidateColor8Panel);
        this.chooseCandidateColorM1Panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                CellZoomPanel.this.chooseCellColor0PanelMouseClicked(evt);
            }
        });
        GroupLayout chooseCandidateColorM1PanelLayout = new GroupLayout(this.chooseCandidateColorM1Panel);
        this.chooseCandidateColorM1Panel.setLayout(chooseCandidateColorM1PanelLayout);
        chooseCandidateColorM1PanelLayout.setHorizontalGroup(chooseCandidateColorM1PanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 18, 32767));
        chooseCandidateColorM1PanelLayout.setVerticalGroup(chooseCandidateColorM1PanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 21, 32767));
        this.chooseCandidateColorPanel.add(this.chooseCandidateColorM1Panel);
        this.chooseCandidateColor1Panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                CellZoomPanel.this.chooseCellColor0PanelMouseClicked(evt);
            }
        });
        GroupLayout chooseCandidateColor1PanelLayout = new GroupLayout(this.chooseCandidateColor1Panel);
        this.chooseCandidateColor1Panel.setLayout(chooseCandidateColor1PanelLayout);
        chooseCandidateColor1PanelLayout.setHorizontalGroup(chooseCandidateColor1PanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 18, 32767));
        chooseCandidateColor1PanelLayout.setVerticalGroup(chooseCandidateColor1PanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 21, 32767));
        this.chooseCandidateColorPanel.add(this.chooseCandidateColor1Panel);
        this.chooseCandidateColor3Panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                CellZoomPanel.this.chooseCellColor0PanelMouseClicked(evt);
            }
        });
        GroupLayout chooseCandidateColor3PanelLayout = new GroupLayout(this.chooseCandidateColor3Panel);
        this.chooseCandidateColor3Panel.setLayout(chooseCandidateColor3PanelLayout);
        chooseCandidateColor3PanelLayout.setHorizontalGroup(chooseCandidateColor3PanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 18, 32767));
        chooseCandidateColor3PanelLayout.setVerticalGroup(chooseCandidateColor3PanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 21, 32767));
        this.chooseCandidateColorPanel.add(this.chooseCandidateColor3Panel);
        this.chooseCandidateColor5Panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                CellZoomPanel.this.chooseCellColor0PanelMouseClicked(evt);
            }
        });
        GroupLayout chooseCandidateColor5PanelLayout = new GroupLayout(this.chooseCandidateColor5Panel);
        this.chooseCandidateColor5Panel.setLayout(chooseCandidateColor5PanelLayout);
        chooseCandidateColor5PanelLayout.setHorizontalGroup(chooseCandidateColor5PanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 18, 32767));
        chooseCandidateColor5PanelLayout.setVerticalGroup(chooseCandidateColor5PanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 21, 32767));
        this.chooseCandidateColorPanel.add(this.chooseCandidateColor5Panel);
        this.chooseCandidateColor7Panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                CellZoomPanel.this.chooseCellColor0PanelMouseClicked(evt);
            }
        });
        GroupLayout chooseCandidateColor7PanelLayout = new GroupLayout(this.chooseCandidateColor7Panel);
        this.chooseCandidateColor7Panel.setLayout(chooseCandidateColor7PanelLayout);
        chooseCandidateColor7PanelLayout.setHorizontalGroup(chooseCandidateColor7PanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 18, 32767));
        chooseCandidateColor7PanelLayout.setVerticalGroup(chooseCandidateColor7PanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 21, 32767));
        this.chooseCandidateColorPanel.add(this.chooseCandidateColor7Panel);
        this.chooseCandidateColor9Panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                CellZoomPanel.this.chooseCellColor0PanelMouseClicked(evt);
            }
        });
        GroupLayout chooseCandidateColor9PanelLayout = new GroupLayout(this.chooseCandidateColor9Panel);
        this.chooseCandidateColor9Panel.setLayout(chooseCandidateColor9PanelLayout);
        chooseCandidateColor9PanelLayout.setHorizontalGroup(chooseCandidateColor9PanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 18, 32767));
        chooseCandidateColor9PanelLayout.setVerticalGroup(chooseCandidateColor9PanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 21, 32767));
        this.chooseCandidateColorPanel.add(this.chooseCandidateColor9Panel);
        this.chooseCandidateColorM2Panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                CellZoomPanel.this.chooseCellColor0PanelMouseClicked(evt);
            }
        });
        GroupLayout chooseCandidateColorM2PanelLayout = new GroupLayout(this.chooseCandidateColorM2Panel);
        this.chooseCandidateColorM2Panel.setLayout(chooseCandidateColorM2PanelLayout);
        chooseCandidateColorM2PanelLayout.setHorizontalGroup(chooseCandidateColorM2PanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 18, 32767));
        chooseCandidateColorM2PanelLayout.setVerticalGroup(chooseCandidateColorM2PanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 21, 32767));
        this.chooseCandidateColorPanel.add(this.chooseCandidateColorM2Panel);
        this.add(this.chooseCandidateColorPanel);
        this.chooseCandidateColorPanel.setBounds(0, 0, 113, 43);
        this.jFontButton.setText("FontButton");
        this.jFontButton.setEnabled(false);
        this.add(this.jFontButton);
        this.jFontButton.setBounds(29, 130, 110, 23);
    }

    private void formComponentResized(ComponentEvent evt) {
        this.calculateLayout();
        this.printSize();
    }

    private void setValueButton1ActionPerformed(ActionEvent evt) {
        this.setValue((JButton) evt.getSource());
    }

    private void toggleCandidatesButton1ActionPerformed(ActionEvent evt) {
        this.handleCandidateChange((JButton) evt.getSource());
    }

    private void chooseCellColor0PanelMouseClicked(MouseEvent evt) {
        this.handleColorChange((JPanel) evt.getSource());
    }

    private void handleCandidateChange(JButton button) {
        int candidate = -1;

        for (int i = 0; i < this.toggleCandidatesButtons.length; i++) {
            if (button == this.toggleCandidatesButtons[i]) {
                candidate = i + 1;
                break;
            }
        }

        if (this.sudokuPanel != null && candidate != -1) {
            if (this.aktColor == -1) {
                this.sudokuPanel.toggleOrRemoveCandidateFromCellZoomPanel(candidate);
            } else {
                this.sudokuPanel.handleColoring(candidate);
            }
        }
    }

    private void setValue(JButton button) {
        int number = -1;

        for (int i = 0; i < this.setValueButtons.length; i++) {
            if (button == this.setValueButtons[i]) {
                number = i + 1;
                break;
            }
        }

        if (this.sudokuPanel != null && number != -1) {
            this.sudokuPanel.setCellFromCellZoomPanel(number);
        }
    }

    private void handleColorChange(JPanel panel) {
        boolean found = false;
        boolean isCell = false;
        int colorNumber = -1;

        for (int i = 0; i < this.cellPanels.length; i++) {
            if (panel == this.cellPanels[i]) {
                colorNumber = i - 2;
                isCell = true;
                found = true;
                break;
            }
        }

        if (!found) {
            for (int i = 0; i < this.candidatePanels.length; i++) {
                if (panel == this.candidatePanels[i]) {
                    colorNumber = i - 2;
                    isCell = false;
                    found = true;
                    break;
                }
            }
        }

        if (found && this.mainFrame != null) {
            this.mainFrame.setColoring(colorNumber, isCell);
        }
    }

    public final void calculateLayout() {
        if (this.defaultButtonHeight != -1) {
            int width = this.getWidth();
            int height = this.getHeight();
            int y = 33;
            FontMetrics metrics = this.getFontMetrics(this.getFont());
            int textHeight = metrics.getHeight();
            int labelHeight = 4 * textHeight;
            int availableVert = height - 33 - 80 - labelHeight;
            int buttonPanelHeight = availableVert * 2 / 6;
            int colorPanelHeight = availableVert / 6;
            if (colorPanelHeight > 50) {
                colorPanelHeight = 50;
            }

            if (buttonPanelHeight > width - 20) {
                buttonPanelHeight = width - 20;
            }

            if (buttonPanelHeight < 120) {
                colorPanelHeight -= 120 - buttonPanelHeight;
                buttonPanelHeight = 120;
            }

            int colorPanelGesWidth = colorPanelHeight * 4;
            if (colorPanelGesWidth > width - 20) {
                colorPanelHeight = (int) ((width - 20) / 4.5);
            }

            colorPanelGesWidth = colorPanelHeight * 4;
            int newColorImageHeight = colorPanelHeight * 2 / 3;
            this.titleLabel.setSize(width, textHeight);
            this.setValueLabel.setSize(width - 20, textHeight);
            this.setValueLabel.setLocation(10, y);
            y += textHeight;
            y += 6;
            this.setValuePanel.setSize(buttonPanelHeight, buttonPanelHeight);
            this.setValuePanel.setLocation((width - buttonPanelHeight) / 2, y);
            this.setValuePanel.doLayout();
            y += buttonPanelHeight;
            y += 14;
            this.toggleCandidatesLabel.setSize(width - 20, textHeight);
            this.toggleCandidatesLabel.setLocation(10, y);
            y += textHeight;
            y += 6;
            this.toggleCandidatesPanel.setSize(buttonPanelHeight, buttonPanelHeight);
            this.toggleCandidatesPanel.setLocation((width - buttonPanelHeight) / 2, y);
            this.toggleCandidatesPanel.doLayout();
            int cpx = (width - colorPanelGesWidth) / 2;
            y = height - 40 - textHeight - textHeight - 2 * colorPanelHeight;
            this.cellColorLabel.setSize(width - 20, textHeight);
            this.cellColorLabel.setLocation(10, y);
            y += textHeight;
            y += 6;
            this.cellColorPanel.setSize(colorPanelHeight * 2 / 3, colorPanelHeight * 2 / 3);
            this.cellColorPanel.setLocation(cpx, y + colorPanelHeight / 6);
            this.cellColorPanel.doLayout();
            this.chooseCellColorPanel.setSize(3 * colorPanelHeight, colorPanelHeight);
            this.chooseCellColorPanel.setLocation(cpx + colorPanelHeight, y);
            this.chooseCellColorPanel.doLayout();
            y += colorPanelHeight;
            y += 14;
            this.chooseCandidateColorLabel.setSize(width - 20, textHeight);
            this.chooseCandidateColorLabel.setLocation(10, y);
            y += textHeight;
            y += 6;
            this.candidateColorPanel.setSize(colorPanelHeight * 2 / 3, colorPanelHeight * 2 / 3);
            this.candidateColorPanel.setLocation(cpx, y + colorPanelHeight / 6);
            this.candidateColorPanel.doLayout();
            this.chooseCandidateColorPanel.setSize(3 * colorPanelHeight, colorPanelHeight);
            this.chooseCandidateColorPanel.setLocation(cpx + colorPanelHeight, y);
            this.chooseCandidateColorPanel.doLayout();
            int newFontSize = this.defaultButtonFontSize * buttonPanelHeight / (this.defaultButtonHeight * 4);
            if (newFontSize > 0 && newFontSize != this.buttonFontSize) {
                this.buttonFontSize = newFontSize;
                this.buttonFont = new Font(this.buttonFont.getName(), this.buttonFont.getStyle(), this.buttonFontSize);
                this.iconFont = new Font(this.buttonFont.getName(), this.buttonFont.getStyle(), this.buttonFontSize - 1);

                for (int i = 0; i < this.setValueButtons.length; i++) {
                    this.setValueButtons[i].setFont(this.buttonFont);
                    this.toggleCandidatesButtons[i].setFont(this.buttonFont);
                }
            }

            if (newColorImageHeight > 0 && Options.getInstance().isShowColorKuAct() && newColorImageHeight != this.colorImageHeight) {
                this.colorImageHeight = newColorImageHeight;

                for (int i = 0; i < this.colorKuIcons.length; i++) {
                    this.colorKuIcons[i] = new ImageIcon(new ColorKuImage(this.colorImageHeight, Options.getInstance().getColorKuColor(i + 1)));
                }
            }

            this.repaint();
        }
    }

    public void update(
            SudokuSet values,
            SudokuSet candidates,
            int aktColor,
            int index,
            boolean colorCellOrCandidate,
            boolean singleCell,
            SortedMap<Integer, Integer> coloredCells,
            SortedMap<Integer, Integer> coloredCandidates
    ) {
        for (int i = 0; i < this.setValueButtons.length; i++) {
            this.setValueButtons[i].setText("");
            this.setValueButtons[i].setEnabled(false);
            this.setValueButtons[i].setForeground(this.normButtonForeground);
            this.setValueButtons[i].setBackground(this.normButtonBackground);
            this.setValueButtons[i].setIcon(null);
            this.toggleCandidatesButtons[i].setText("");
            this.toggleCandidatesButtons[i].setEnabled(false);
            this.toggleCandidatesButtons[i].setForeground(this.normButtonForeground);
            this.toggleCandidatesButtons[i].setBackground(this.normButtonBackground);
            this.toggleCandidatesButtons[i].setIcon(null);
        }

        this.cellColorPanel.setBackground(Options.getInstance().getDefaultCellColor());
        this.candidateColorPanel.setBackground(Options.getInstance().getDefaultCellColor());
        this.aktColor = aktColor;
        if (aktColor == -1) {
            for (int i = 0; i < values.size(); i++) {
                int cand = values.get(i) - 1;
                if (cand >= 0 && cand <= 8) {
                    if (Options.getInstance().isShowColorKuAct()) {
                        this.setValueButtons[cand].setText(null);
                        this.setValueButtons[cand].setIcon(this.colorKuIcons[cand]);
                    } else {
                        this.setValueButtons[cand].setText(NUMBERS[cand]);
                        this.setValueButtons[cand].setIcon(null);
                    }

                    this.setValueButtons[cand].setEnabled(true);
                }
            }

            for (int i = 0; i < candidates.size(); i++) {
                int cand = candidates.get(i) - 1;
                if (cand >= 0 && cand <= 8) {
                    if (Options.getInstance().isShowColorKuAct()) {
                        this.toggleCandidatesButtons[cand].setText(null);
                        this.toggleCandidatesButtons[cand].setIcon(this.colorKuIcons[cand]);
                    } else {
                        this.toggleCandidatesButtons[cand].setText(NUMBERS[cand]);
                        this.toggleCandidatesButtons[cand].setIcon(null);
                    }

                    this.toggleCandidatesButtons[cand].setEnabled(true);
                }
            }

            if (singleCell) {
                this.toggleCandidatesLabel.setText(ResourceBundle.getBundle("intl/CellZoomPanel").getString("CellZoomPanel.toggleCandidatesLabel.text"));

                for (int i = 0; i < this.toggleCandidatesButtons.length; i++) {
                    this.toggleCandidatesButtons[i].setEnabled(true);
                }
            } else {
                this.toggleCandidatesLabel.setText(ResourceBundle.getBundle("intl/CellZoomPanel").getString("CellZoomPanel.toggleCandidatesLabel.text2"));
            }
        } else {
            if (colorCellOrCandidate) {
                this.cellColorPanel.setBackground(Options.getInstance().getColoringColors()[aktColor]);
            } else {
                this.candidateColorPanel.setBackground(Options.getInstance().getColoringColors()[aktColor]);
            }

            if (coloredCells != null && !colorCellOrCandidate) {
                for (int i = 0; i < candidates.size(); i++) {
                    int cand = candidates.get(i);
                    if (coloredCandidates.containsKey(index * 10 + cand)) {
                        int candIndex = coloredCandidates.get(index * 10 + cand);
                        Color candColor = Options.getInstance().getColoringColors()[candIndex];
                        this.toggleCandidatesButtons[cand - 1].setForeground(candColor);
                        this.toggleCandidatesButtons[cand - 1].setBackground(candColor);
                        this.toggleCandidatesButtons[cand - 1].setIcon(this.createImage(this.colorImageHeight, candIndex, cand));
                        this.toggleCandidatesButtons[cand - 1].setEnabled(true);
                    } else {
                        this.toggleCandidatesButtons[cand - 1].setText(NUMBERS[cand - 1]);
                        this.toggleCandidatesButtons[cand - 1].setEnabled(true);
                    }
                }
            }
        }
    }

    private ImageIcon createImage(int size, int colorIndex, int cand) {
        if (size > 0) {
            Image img = new BufferedImage(size, size, 1);
            Graphics2D g = (Graphics2D) img.getGraphics();
            Color color = Options.getInstance().getDefaultCellColor();
            if (colorIndex < Options.getInstance().getColoringColors().length) {
                color = Options.getInstance().getColoringColors()[colorIndex];
            }

            g.setColor(color);
            g.fillRect(0, 0, size, size);
            if (cand > 0) {
                if (Options.getInstance().isShowColorKuAct()) {
                    BufferedImage cImg = new ColorKuImage(size, Options.getInstance().getColorKuColor(cand));
                    g.drawImage(cImg, 0, 0, null);
                } else {
                    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g.setFont(this.iconFont);
                    FontMetrics fm = g.getFontMetrics();
                    String str = String.valueOf(cand);
                    int strWidth = fm.stringWidth(str);
                    int strHeight = fm.getAscent();
                    g.setColor(this.normButtonForeground);
                    g.drawString(String.valueOf(cand), (size - strWidth) / 2, (size + strHeight - 2) / 2);
                }
            }

            return new ImageIcon(img);
        } else {
            return null;
        }
    }

    private void printSize() {
    }

    public void setTitleLabelColors(Color fore, Color back) {
        this.titleLabel.setBackground(back);
        this.titleLabel.setForeground(fore);
    }

    public void setSudokuPanel(SudokuPanel sudokuPanel) {
        this.sudokuPanel = sudokuPanel;
    }
}
