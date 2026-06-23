package sudoku;

import generator.BackgroundGeneratorThread;
import solver.SudokuSolver;
import solver.SudokuSolverFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JPopupMenu.Separator;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.Timer;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.net.URL;
import java.text.MessageFormat;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class MainFrame extends JFrame implements FlavorListener {
    public static final String VERSION = "HoDoKu - v2.2.0";
    public static final String BUILD;
    public static final String REV = "$LastChangedRevision: 116 $";
    private static final long serialVersionUID = 1L;
    private static final int TOGGLE_BUTTON_ICON_SIZE = 32;

    static {
        String[] dummy = "$LastChangedRevision: 116 $".split(" ");
        BUILD = "Build " + dummy[1];
    }

    private SudokuPanel sudokuPanel;
    private JToggleButton[] toggleButtons = new JToggleButton[10];
    private Icon[] toggleButtonIconsOrg = new Icon[10];
    private Icon[] emptyToggleButtonIconsOrg = new Icon[10];
    private ColorKuImage[] toggleButtonImagesColorKu = new ColorKuImage[10];
    private Icon[] toggleButtonIconsColorKu = new Icon[10];
    private Icon[] toggleButtonIcons = new Icon[10];
    private Icon[] emptyToggleButtonIcons = new Icon[10];
    private Icon emptyToggleButtonIconOrg = new ImageIcon(this.getClass().getResource("/img/f_0c.png"));
    private Icon emptyToggleButtonIconOrgColorKu = new ImageIcon(new ColorKuImage(32, Color.WHITE));
    private JRadioButtonMenuItem[] levelMenuItems = new JRadioButtonMenuItem[5];
    private JRadioButtonMenuItem[] modeMenuItems;
    private boolean oldShowDeviations = true;
    private boolean oldShowDeviationsValid = false;
    private boolean eingabeModus = false;
    private SplitPanel splitPanel = new SplitPanel();
    private SummaryPanel summaryPanel = new SummaryPanel(this);
    private SolutionPanel solutionPanel = new SolutionPanel(this);
    private AllStepsPanel allStepsPanel = new AllStepsPanel(this, null);
    private CellZoomPanel cellZoomPanel = new CellZoomPanel(this);
    private JTabbedPane tabPane = new JTabbedPane();
    private PageFormat pageFormat = null;
    private PrinterJob job = null;
    private double bildSize = 400.0;
    private int bildAufloesung = 96;
    private int bildEinheit = 2;
    private MainFrame.MyFileFilter[] puzzleFileSaveFilters = new MainFrame.MyFileFilter[]{
            new MainFrame.MyFileFilter(1),
            new MainFrame.MyFileFilter(2),
            new MainFrame.MyFileFilter(3),
            new MainFrame.MyFileFilter(4),
            new MainFrame.MyFileFilter(5),
            new MainFrame.MyFileFilter(6),
            new MainFrame.MyFileFilter(7),
            new MainFrame.MyFileFilter(9)
    };
    private MainFrame.MyFileFilter[] puzzleFileLoadFilters = new MainFrame.MyFileFilter[]{
            new MainFrame.MyFileFilter(1), new MainFrame.MyFileFilter(8), new MainFrame.MyFileFilter(9)
    };
    private MainFrame.MyFileFilter[] configFileFilters = new MainFrame.MyFileFilter[]{new MainFrame.MyFileFilter(0)};
    private MainFrame.MyCaretListener caretListener = new MainFrame.MyCaretListener();
    private boolean outerSplitPaneInitialized = false;
    private int resetHDivLocLoc = -1;
    private boolean resetHDivLoc = false;
    private long resetHDivLocTicks = 0L;
    private String configFileExt = ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.config_file_ext");
    private String solutionFileExt = ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.solution_file_ext");
    private String textFileExt = ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.text_file_ext");
    private String ssFileExt = ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.ss_file_ext");
    private MessageFormat formatter = new MessageFormat("");
    private List<GuiState> savePoints = new ArrayList<>();
    private boolean changingFullScreenMode = false;
    private ImageIcon[] progressImages = new ImageIcon[Options.DEFAULT_DIFFICULTY_LEVELS.length];
    private DifficultyLevel currentLevel = null;
    private int currentScore = 0;
    private JButton vageHintToggleButton = null;
    private JButton concreteHintToggleButton = null;
    private JButton showNextStepToggleButton = null;
    private JButton executeStepToggleButton = null;
    private JButton abortStepToggleButton = null;
    private JSeparator hintSeperator = null;
    private Timer clipTimer = new Timer(100, null);
    private String sudokuFileName = null;
    private int sudokuFileType = -1;
    private JMenuItem aboutMenuItem;
    private JRadioButtonMenuItem allStepsMenuItem;
    private JMenuItem alleHiddenSinglesSetzenMenuItem;
    private JMenu ansichtMenu;
    private JMenuItem askQuestionMenuItem;
    private JMenuItem backdoorSearchMenuItem;
    private JMenu bearbeitenMenu;
    private JMenuItem beendenMenuItem;
    private JRadioButtonMenuItem cellZoomMenuItem;
    private ButtonGroup colorButtonGroup;
    private JRadioButtonMenuItem colorCandidatesMenuItem;
    private JRadioButtonMenuItem colorCellsMenuItem;
    private JMenuItem configMenuItem;
    private JMenuItem copyCluesMenuItem;
    private JMenuItem copyFilledMenuItem;
    private JMenuItem copyLibraryMenuItem;
    private JMenuItem copyPmGridMenuItem;
    private JMenuItem copyPmGridWithStepMenuItem;
    private JMenuItem copySSMenuItem;
    private JMenuItem createSavePointMenuItem;
    private JMenu dateiMenu;
    private JMenuItem druckenMenuItem;
    private JMenuItem extendedPrintMenuItem;
    private JToggleButton f1ToggleButton;
    private JToggleButton f2ToggleButton;
    private JToggleButton f3ToggleButton;
    private JToggleButton f4ToggleButton;
    private JToggleButton f5ToggleButton;
    private JToggleButton f6ToggleButton;
    private JToggleButton f7ToggleButton;
    private JToggleButton f8ToggleButton;
    private JToggleButton f9ToggleButton;
    private JCheckBoxMenuItem fullScreenMenuItem;
    private JToggleButton fxyToggleButton;
    private JMenu helpMenu;
    private JPanel hintPanel;
    private JButton hinweisAbbrechenButton;
    private JButton hinweisAusfuehrenButton;
    private JTextArea hinweisTextArea;
    private JMenuItem historyMenuItem;
    private JMenuBar jMenuBar1;
    private JPanel jPanel1;
    private JScrollPane jScrollPane1;
    private JSeparator jSeparator1;
    private JSeparator jSeparator11;
    private JSeparator jSeparator12;
    private JSeparator jSeparator13;
    private Separator jSeparator20;
    private Separator jSeparator23;
    private JSeparator jSeparator24;
    private Separator jSeparator25;
    private Separator jSeparator26;
    private Separator jSeparator27;
    private Separator jSeparator28;
    private Separator jSeparator29;
    private Separator jSeparator30;
    private Separator jSeparator31;
    private Separator jSeparator32;
    private Separator jSeparator33;
    private Separator jSeparator34;
    private Separator jSeparator35;
    private Separator jSeparator36;
    private Separator jSeparator37;
    private Separator jSeparator38;
    private Separator jSeparator39;
    private Separator jSeparator6;
    private JSeparator jSeparator8;
    private JSeparator jSeparator9;
    private JToolBar jToolBar1;
    private JMenuItem keyMenuItem;
    private JRadioButtonMenuItem learningMenuItem;
    private ButtonGroup levelButtonGroup;
    private JComboBox levelComboBox;
    private JRadioButtonMenuItem levelExtremMenuItem;
    private JRadioButtonMenuItem levelKniffligMenuItem;
    private JRadioButtonMenuItem levelLeichtMenuItem;
    private JMenu levelMenu;
    private JRadioButtonMenuItem levelMittelMenuItem;
    private JRadioButtonMenuItem levelSchwerMenuItem;
    private JMenuItem loadConfigMenuItem;
    private JMenuItem loadPuzzleMenuItem;
    private JMenuItem loesungsSchrittMenuItem;
    private JMenuItem mediumHintMenuItem;
    private ButtonGroup modeButtonGroup;
    private JMenu modeMenu;
    private JMenuItem neuMenuItem;
    private JButton neuerHinweisButton;
    private JButton neuesSpielToolButton;
    private JMenu optionenMenu;
    private JSplitPane outerSplitPane;
    private JMenuItem pasteMenuItem;
    private JRadioButtonMenuItem playingMenuItem;
    private JRadioButtonMenuItem practisingMenuItem;
    private JLabel progressLabel;
    private JMenuItem projectHomePageMenuItem;
    private JMenu raetselMenu;
    private JToggleButton redGreenToggleButton;
    private JMenuItem redoMenuItem;
    private JButton redoToolButton;
    private JMenuItem reportErrorMenuItem;
    private JMenuItem resetSpielMenuItem;
    private JMenuItem resetViewMenuItem;
    private JMenuItem restartSpielMenuItem;
    private JMenuItem restoreSavePointMenuItem;
    private JMenuItem saveConfigAsMenuItem;
    private JMenuItem savePuzzleAsMenuItem;
    private JMenuItem savePuzzleMenuItem;
    private JMenuItem seiteEinrichtenMenuItem;
    private JMenuItem setGivensMenuItem;
    private JCheckBoxMenuItem showCandidatesMenuItem;
    private JCheckBoxMenuItem showColorKuMenuItem;
    private JCheckBoxMenuItem showDeviationsMenuItem;
    private JCheckBoxMenuItem showHintButtonsCheckBoxMenuItem;
    private JCheckBoxMenuItem showHintPanelMenuItem;
    private JCheckBoxMenuItem showToolBarMenuItem;
    private JCheckBoxMenuItem showWrongValuesMenuItem;
    private JRadioButtonMenuItem solutionMenuItem;
    private JButton solveUpToButton;
    private JMenuItem solvingGuideMenuItem;
    private JMenuItem speichernAlsBildMenuItem;
    private JMenuItem spielEditierenMenuItem;
    private JMenuItem spielEingebenMenuItem;
    private JMenuItem spielSpielenMenuItem;
    private JLabel statusLabelCellCandidate;
    private JLabel statusLabelLevel;
    private JLabel statusLabelModus;
    private JPanel statusLinePanel;
    private JPanel statusPanelColor1;
    private JPanel statusPanelColor2;
    private JPanel statusPanelColor3;
    private JPanel statusPanelColor4;
    private JPanel statusPanelColor5;
    private JPanel statusPanelColorClear;
    private JPanel statusPanelColorReset;
    private JPanel statusPanelColorResult;
    private JRadioButtonMenuItem sudokuOnlyMenuItem;
    private JRadioButtonMenuItem summaryMenuItem;
    private JMenuItem undoMenuItem;
    private JButton undoToolButton;
    private JMenuItem userManualMenuItem;
    private JMenuItem vageHintMenuItem;
    private ButtonGroup viewButtonGroup;

    public MainFrame(String launchFile) {
        if (launchFile != null && launchFile.endsWith("." + this.configFileExt)) {
            Options.readOptions(launchFile);
            BackgroundGeneratorThread.getInstance().resetAll();
        }

        Options.getInstance().checkAllFonts();
        this.initComponents();
        this.setTitleWithFile();
        this.outerSplitPane.getActionMap().getParent().remove("startResize");
        this.outerSplitPane.getActionMap().getParent().remove("toggleFocus");
        String fontName = "Arial";
        if (!Options.getInstance().checkFont(fontName)) {
            fontName = "SansSerif";
        }

        Font font = this.hinweisTextArea.getFont();
        font = new Font(fontName, font.getStyle(), this.bearbeitenMenu.getFont().getSize());
        this.hinweisTextArea.setFont(font);
        font = this.statusLinePanel.getFont();
        fontName = "Tahoma";
        if (!Options.getInstance().checkFont(fontName)) {
            fontName = font.getName();
        }

        int fontSize = 12;
        if (font.getSize() > fontSize) {
            fontSize = font.getSize();
        }

        font = new Font(fontName, this.getFont().getStyle(), fontSize);
        this.statusLabelCellCandidate.setFont(font);
        this.statusLabelLevel.setFont(font);
        this.statusLabelModus.setFont(font);
        this.progressLabel.setFont(font);
        int actLevel = Options.getInstance().getActLevel();
        Color lafMenuBackColor = UIManager.getColor("textHighlight");
        Color lafMenuColor = UIManager.getColor("textHighlightText");
        if (lafMenuBackColor == null) {
            lafMenuBackColor = Color.BLUE;
        }

        if (lafMenuColor == null) {
            lafMenuColor = Color.BLACK;
        }

        this.statusLinePanel.setBackground(lafMenuBackColor);
        this.statusLabelLevel.setForeground(lafMenuColor);
        this.summaryPanel.setTitleLabelColors(lafMenuColor, lafMenuBackColor);
        this.solutionPanel.setTitleLabelColors(lafMenuColor, lafMenuBackColor);
        this.cellZoomPanel.setTitleLabelColors(lafMenuColor, lafMenuBackColor);
        this.statusLabelModus.setText(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.playingMenuItem.text"));
        this.clipTimer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.clipTimer.stop();
                MainFrame.this.adjustPasteMenuItem();
            }
        });

        try {
            Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
            clip.addFlavorListener(this);
            this.adjustPasteMenuItem();
        } catch (IllegalStateException ex) {
            this.clipTimer.start();
        }

        this.sudokuPanel = new SudokuPanel(this);
        this.sudokuPanel.setCellZoomPanel(this.cellZoomPanel);
        this.cellZoomPanel.setSudokuPanel(this.sudokuPanel);
        this.outerSplitPane.setLeftComponent(this.splitPanel);
        this.splitPanel.setSplitPane(this.sudokuPanel, null);
        this.tabPane.addTab(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.summary"), this.summaryPanel);
        this.tabPane.addTab(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.solution_path"), this.solutionPanel);
        this.tabPane.addTab(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.all_steps"), this.allStepsPanel);
        this.tabPane.addTab(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.cell_zoom"), this.cellZoomPanel);
        this.tabPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                MainFrame.this.tabPaneMouseClicked(evt);
            }
        });
        if (Options.getInstance().isSaveWindowLayout()) {
            this.setWindowLayout(false);
        } else {
            this.setWindowLayout(true);
        }

        this.outerSplitPaneInitialized = false;
        if (Options.getInstance().getInitialXPos() != -1 && Options.getInstance().getInitialYPos() != -1) {
            Toolkit t = Toolkit.getDefaultToolkit();
            Dimension screenSize = t.getScreenSize();
            int x = Options.getInstance().getInitialXPos();
            int y = Options.getInstance().getInitialYPos();
            if (x + this.getWidth() > screenSize.width) {
                x = screenSize.width - this.getWidth() - 10;
            }

            if (y + this.getHeight() > screenSize.height) {
                y = screenSize.height - this.getHeight() - 10;
            }

            if (x < 0) {
                x = 0;
            }

            if (y < 0) {
                y = 0;
            }

            this.setLocation(x, y);
        }

        this.showHintPanelMenuItem.setSelected(Options.getInstance().isShowHintPanel());
        this.showToolBarMenuItem.setSelected(Options.getInstance().isShowToolBar());
        this.levelMenuItems[0] = this.levelLeichtMenuItem;
        this.levelMenuItems[1] = this.levelMittelMenuItem;
        this.levelMenuItems[2] = this.levelKniffligMenuItem;
        this.levelMenuItems[3] = this.levelSchwerMenuItem;
        this.levelMenuItems[4] = this.levelExtremMenuItem;
        FontMetrics metrics = this.levelComboBox.getFontMetrics(this.levelComboBox.getFont());
        int miWidth = 0;
        int miHeight = metrics.getHeight();
        Set<Character> mnemonics = new HashSet<>();

        for (int i = 1; i < DifficultyType.values().length; i++) {
            this.levelMenuItems[i - 1].setText(Options.getInstance().getDifficultyLevels()[i].getName());
            char mnemonic = 0;
            boolean mnemonicFound = false;

            for (int j = 0; j < Options.getInstance().getDifficultyLevels()[i].getName().length(); j++) {
                mnemonic = Options.getInstance().getDifficultyLevels()[i].getName().charAt(j);
                if (!mnemonics.contains(mnemonic)) {
                    mnemonicFound = true;
                    break;
                }
            }

            if (mnemonicFound) {
                mnemonics.add(mnemonic);
                this.levelMenuItems[i - 1].setMnemonic(mnemonic);
            }

            this.levelComboBox.addItem(Options.getInstance().getDifficultyLevels()[i].getName());
            int aktWidth = metrics.stringWidth(Options.getInstance().getDifficultyLevels()[i].getName());
            if (aktWidth > miWidth) {
                miWidth = aktWidth;
            }
        }

        Set<Character> var26 = null;
        this.modeMenuItems = new JRadioButtonMenuItem[]{this.playingMenuItem, this.learningMenuItem, this.practisingMenuItem};
        if (miWidth > 35) {
            Dimension newLevelSize = new Dimension(60 + (miWidth - 35) + 8, 20 + (miHeight - 14) + 3);
            this.levelComboBox.setMaximumSize(newLevelSize);
            this.levelComboBox.setMinimumSize(newLevelSize);
            this.levelComboBox.setPreferredSize(newLevelSize);
            this.levelComboBox.setSize(newLevelSize);
        }

        Options.getInstance().setActLevel(actLevel);
        this.check();
        this.toggleButtons[0] = this.f1ToggleButton;
        this.toggleButtons[1] = this.f2ToggleButton;
        this.toggleButtons[2] = this.f3ToggleButton;
        this.toggleButtons[3] = this.f4ToggleButton;
        this.toggleButtons[4] = this.f5ToggleButton;
        this.toggleButtons[5] = this.f6ToggleButton;
        this.toggleButtons[6] = this.f7ToggleButton;
        this.toggleButtons[7] = this.f8ToggleButton;
        this.toggleButtons[8] = this.f9ToggleButton;
        this.toggleButtons[9] = this.fxyToggleButton;
        int i = 0;

        for (int lim = this.toggleButtons.length; i < lim; i++) {
            this.toggleButtonIconsOrg[i] = this.toggleButtons[i].getIcon();
            this.toggleButtonIcons[i] = this.toggleButtons[i].getIcon();
            if (i < 9) {
                this.emptyToggleButtonIconsOrg[i] = new ImageIcon(this.getClass().getResource("/img/f_" + (i + 1) + "c_inactive.png"));
            } else {
                this.emptyToggleButtonIcons[i] = this.toggleButtons[i].getIcon();
            }
        }

        this.setToggleButton(null, false);
        this.prepareToggleButtonIcons(Options.getInstance().isShowColorKu());
        this.showColorKuMenuItem.setSelected(Options.getInstance().isShowColorKu());
        Options.getInstance().setShowColorKuAct(Options.getInstance().isShowColorKu());
        this.hinweisTextArea.addCaretListener(this.caretListener);
        this.createProgressLabelImages();
        this.progressLabel.setIcon(this.progressImages[0]);
        this.setMode(Options.getInstance().getGameMode(), false);
        this.setShowHintButtonsInToolbar();
        if (launchFile != null && launchFile.endsWith("." + this.solutionFileExt)) {
            this.loadFromFile(launchFile, 1);
        }

        if (launchFile != null && launchFile.endsWith("." + this.textFileExt)) {
            this.loadFromFile(launchFile, 8);
        }

        this.fixFocus();
        BackgroundGeneratorThread.getInstance().startCreation();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, "Error setting LaF", ex);
        }

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainFrame(null).setVisible(true);
            }
        });
    }

    private void initComponents() {
        this.levelButtonGroup = new ButtonGroup();
        this.viewButtonGroup = new ButtonGroup();
        this.colorButtonGroup = new ButtonGroup();
        this.modeButtonGroup = new ButtonGroup();
        this.statusLinePanel = new JPanel();
        this.statusPanelColorResult = new JPanel();
        this.jPanel1 = new JPanel();
        this.statusPanelColor1 = new StatusColorPanel(0);
        this.statusPanelColor2 = new StatusColorPanel(2);
        this.statusPanelColor3 = new StatusColorPanel(4);
        this.statusPanelColor4 = new StatusColorPanel(6);
        this.statusPanelColor5 = new StatusColorPanel(8);
        this.statusPanelColorClear = new StatusColorPanel(-1);
        this.statusPanelColorReset = new StatusColorPanel(-2);
        this.statusLabelCellCandidate = new JLabel();
        this.jSeparator1 = new JSeparator();
        this.statusLabelLevel = new JLabel();
        this.jSeparator8 = new JSeparator();
        this.progressLabel = new JLabel();
        this.jSeparator24 = new JSeparator();
        this.statusLabelModus = new JLabel();
        this.jToolBar1 = new JToolBar();
        this.undoToolButton = new JButton();
        this.redoToolButton = new JButton();
        this.jSeparator9 = new JSeparator();
        this.neuesSpielToolButton = new JButton();
        this.jSeparator12 = new JSeparator();
        this.levelComboBox = new JComboBox();
        this.jSeparator13 = new JSeparator();
        this.jSeparator11 = new JSeparator();
        this.redGreenToggleButton = new JToggleButton();
        this.f1ToggleButton = new JToggleButton();
        this.f2ToggleButton = new JToggleButton();
        this.f3ToggleButton = new JToggleButton();
        this.f4ToggleButton = new JToggleButton();
        this.f5ToggleButton = new JToggleButton();
        this.f6ToggleButton = new JToggleButton();
        this.f7ToggleButton = new JToggleButton();
        this.f8ToggleButton = new JToggleButton();
        this.f9ToggleButton = new JToggleButton();
        this.fxyToggleButton = new JToggleButton();
        this.outerSplitPane = new JSplitPane();
        this.hintPanel = new JPanel();
        this.neuerHinweisButton = new JButton();
        this.hinweisAusfuehrenButton = new JButton();
        this.solveUpToButton = new JButton();
        this.hinweisAbbrechenButton = new JButton();
        this.jScrollPane1 = new JScrollPane();
        this.hinweisTextArea = new JTextArea();
        this.jMenuBar1 = new JMenuBar();
        this.dateiMenu = new JMenu();
        this.neuMenuItem = new JMenuItem();
        this.jSeparator39 = new Separator();
        this.loadPuzzleMenuItem = new JMenuItem();
        this.savePuzzleMenuItem = new JMenuItem();
        this.savePuzzleAsMenuItem = new JMenuItem();
        this.loadConfigMenuItem = new JMenuItem();
        this.saveConfigAsMenuItem = new JMenuItem();
        this.jSeparator38 = new Separator();
        this.seiteEinrichtenMenuItem = new JMenuItem();
        this.druckenMenuItem = new JMenuItem();
        this.extendedPrintMenuItem = new JMenuItem();
        this.speichernAlsBildMenuItem = new JMenuItem();
        this.jSeparator37 = new Separator();
        this.spielEingebenMenuItem = new JMenuItem();
        this.spielEditierenMenuItem = new JMenuItem();
        this.spielSpielenMenuItem = new JMenuItem();
        this.jSeparator36 = new Separator();
        this.beendenMenuItem = new JMenuItem();
        this.bearbeitenMenu = new JMenu();
        this.undoMenuItem = new JMenuItem();
        this.redoMenuItem = new JMenuItem();
        this.jSeparator35 = new Separator();
        this.copyCluesMenuItem = new JMenuItem();
        this.copyFilledMenuItem = new JMenuItem();
        this.copyPmGridMenuItem = new JMenuItem();
        this.copyPmGridWithStepMenuItem = new JMenuItem();
        this.copyLibraryMenuItem = new JMenuItem();
        this.copySSMenuItem = new JMenuItem();
        this.pasteMenuItem = new JMenuItem();
        this.jSeparator34 = new Separator();
        this.restartSpielMenuItem = new JMenuItem();
        this.resetSpielMenuItem = new JMenuItem();
        this.jSeparator33 = new Separator();
        this.configMenuItem = new JMenuItem();
        this.modeMenu = new JMenu();
        this.playingMenuItem = new JRadioButtonMenuItem();
        this.learningMenuItem = new JRadioButtonMenuItem();
        this.practisingMenuItem = new JRadioButtonMenuItem();
        this.optionenMenu = new JMenu();
        this.showCandidatesMenuItem = new JCheckBoxMenuItem();
        this.showWrongValuesMenuItem = new JCheckBoxMenuItem();
        this.showDeviationsMenuItem = new JCheckBoxMenuItem();
        this.showColorKuMenuItem = new JCheckBoxMenuItem();
        this.jSeparator32 = new Separator();
        this.colorCellsMenuItem = new JRadioButtonMenuItem();
        this.colorCandidatesMenuItem = new JRadioButtonMenuItem();
        this.jSeparator31 = new Separator();
        this.levelMenu = new JMenu();
        this.levelLeichtMenuItem = new JRadioButtonMenuItem();
        this.levelMittelMenuItem = new JRadioButtonMenuItem();
        this.levelKniffligMenuItem = new JRadioButtonMenuItem();
        this.levelSchwerMenuItem = new JRadioButtonMenuItem();
        this.levelExtremMenuItem = new JRadioButtonMenuItem();
        this.raetselMenu = new JMenu();
        this.vageHintMenuItem = new JMenuItem();
        this.mediumHintMenuItem = new JMenuItem();
        this.loesungsSchrittMenuItem = new JMenuItem();
        this.jSeparator30 = new Separator();
        this.backdoorSearchMenuItem = new JMenuItem();
        this.historyMenuItem = new JMenuItem();
        this.createSavePointMenuItem = new JMenuItem();
        this.restoreSavePointMenuItem = new JMenuItem();
        this.jSeparator29 = new Separator();
        this.setGivensMenuItem = new JMenuItem();
        this.jSeparator28 = new Separator();
        this.alleHiddenSinglesSetzenMenuItem = new JMenuItem();
        this.ansichtMenu = new JMenu();
        this.sudokuOnlyMenuItem = new JRadioButtonMenuItem();
        this.jSeparator20 = new Separator();
        this.summaryMenuItem = new JRadioButtonMenuItem();
        this.solutionMenuItem = new JRadioButtonMenuItem();
        this.allStepsMenuItem = new JRadioButtonMenuItem();
        this.cellZoomMenuItem = new JRadioButtonMenuItem();
        this.jSeparator6 = new Separator();
        this.showHintPanelMenuItem = new JCheckBoxMenuItem();
        this.showToolBarMenuItem = new JCheckBoxMenuItem();
        this.showHintButtonsCheckBoxMenuItem = new JCheckBoxMenuItem();
        this.fullScreenMenuItem = new JCheckBoxMenuItem();
        this.jSeparator23 = new Separator();
        this.resetViewMenuItem = new JMenuItem();
        this.helpMenu = new JMenu();
        this.keyMenuItem = new JMenuItem();
        this.jSeparator26 = new Separator();
        this.userManualMenuItem = new JMenuItem();
        this.solvingGuideMenuItem = new JMenuItem();
        this.projectHomePageMenuItem = new JMenuItem();
        this.jSeparator27 = new Separator();
        this.reportErrorMenuItem = new JMenuItem();
        this.askQuestionMenuItem = new JMenuItem();
        this.jSeparator25 = new Separator();
        this.aboutMenuItem = new JMenuItem();
        this.setDefaultCloseOperation(3);
        ResourceBundle bundle = ResourceBundle.getBundle("intl/MainFrame");
        this.setTitle(bundle.getString("MainFrame.title"));
        this.setIconImage(this.getIcon());
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent evt) {
                MainFrame.this.formWindowClosed(evt);
            }

            @Override
            public void windowClosing(WindowEvent evt) {
                MainFrame.this.formWindowClosing(evt);
            }
        });
        this.statusLinePanel.setBackground(new Color(0, 153, 255));
        this.statusLinePanel.setLayout(new FlowLayout(0));
        this.statusPanelColorResult.setToolTipText(bundle.getString("MainFrame.statusPanelColorResult.toolTipText"));
        GroupLayout statusPanelColorResultLayout = new GroupLayout(this.statusPanelColorResult);
        this.statusPanelColorResult.setLayout(statusPanelColorResultLayout);
        statusPanelColorResultLayout.setHorizontalGroup(statusPanelColorResultLayout.createParallelGroup(Alignment.LEADING).addGap(0, 30, 32767));
        statusPanelColorResultLayout.setVerticalGroup(statusPanelColorResultLayout.createParallelGroup(Alignment.LEADING).addGap(0, 15, 32767));
        this.statusLinePanel.add(this.statusPanelColorResult);
        this.jPanel1.setOpaque(false);
        this.jPanel1.setLayout(new FlowLayout(1, 1, 0));
        this.statusPanelColor1.setToolTipText(bundle.getString("MainFrame.statusPanelColor1.toolTipText"));
        this.statusPanelColor1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                MainFrame.this.statusPanelColor1MouseClicked(evt);
            }
        });
        GroupLayout statusPanelColor1Layout = new GroupLayout(this.statusPanelColor1);
        this.statusPanelColor1.setLayout(statusPanelColor1Layout);
        statusPanelColor1Layout.setHorizontalGroup(statusPanelColor1Layout.createParallelGroup(Alignment.LEADING).addGap(0, 15, 32767));
        statusPanelColor1Layout.setVerticalGroup(statusPanelColor1Layout.createParallelGroup(Alignment.LEADING).addGap(0, 15, 32767));
        this.jPanel1.add(this.statusPanelColor1);
        this.statusPanelColor2.setToolTipText(bundle.getString("MainFrame.statusPanelColor2.toolTipText"));
        this.statusPanelColor2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                MainFrame.this.statusPanelColor2MouseClicked(evt);
            }
        });
        GroupLayout statusPanelColor2Layout = new GroupLayout(this.statusPanelColor2);
        this.statusPanelColor2.setLayout(statusPanelColor2Layout);
        statusPanelColor2Layout.setHorizontalGroup(statusPanelColor2Layout.createParallelGroup(Alignment.LEADING).addGap(0, 15, 32767));
        statusPanelColor2Layout.setVerticalGroup(statusPanelColor2Layout.createParallelGroup(Alignment.LEADING).addGap(0, 15, 32767));
        this.jPanel1.add(this.statusPanelColor2);
        this.statusPanelColor3.setToolTipText(bundle.getString("MainFrame.statusPanelColor3.toolTipText"));
        this.statusPanelColor3.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                MainFrame.this.statusPanelColor3MouseClicked(evt);
            }
        });
        GroupLayout statusPanelColor3Layout = new GroupLayout(this.statusPanelColor3);
        this.statusPanelColor3.setLayout(statusPanelColor3Layout);
        statusPanelColor3Layout.setHorizontalGroup(statusPanelColor3Layout.createParallelGroup(Alignment.LEADING).addGap(0, 15, 32767));
        statusPanelColor3Layout.setVerticalGroup(statusPanelColor3Layout.createParallelGroup(Alignment.LEADING).addGap(0, 15, 32767));
        this.jPanel1.add(this.statusPanelColor3);
        this.statusPanelColor4.setToolTipText(bundle.getString("MainFrame.statusPanelColor4.toolTipText"));
        this.statusPanelColor4.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                MainFrame.this.statusPanelColor4MouseClicked(evt);
            }
        });
        GroupLayout statusPanelColor4Layout = new GroupLayout(this.statusPanelColor4);
        this.statusPanelColor4.setLayout(statusPanelColor4Layout);
        statusPanelColor4Layout.setHorizontalGroup(statusPanelColor4Layout.createParallelGroup(Alignment.LEADING).addGap(0, 15, 32767));
        statusPanelColor4Layout.setVerticalGroup(statusPanelColor4Layout.createParallelGroup(Alignment.LEADING).addGap(0, 15, 32767));
        this.jPanel1.add(this.statusPanelColor4);
        this.statusPanelColor5.setToolTipText(bundle.getString("MainFrame.statusPanelColor5.toolTipText"));
        this.statusPanelColor5.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                MainFrame.this.statusPanelColor5MouseClicked(evt);
            }
        });
        GroupLayout statusPanelColor5Layout = new GroupLayout(this.statusPanelColor5);
        this.statusPanelColor5.setLayout(statusPanelColor5Layout);
        statusPanelColor5Layout.setHorizontalGroup(statusPanelColor5Layout.createParallelGroup(Alignment.LEADING).addGap(0, 15, 32767));
        statusPanelColor5Layout.setVerticalGroup(statusPanelColor5Layout.createParallelGroup(Alignment.LEADING).addGap(0, 15, 32767));
        this.jPanel1.add(this.statusPanelColor5);
        this.statusPanelColorClear.setToolTipText(bundle.getString("MainFrame.statusPanelColorClear.toolTipText"));
        this.statusPanelColorClear.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                MainFrame.this.statusPanelColorClearMouseClicked(evt);
            }
        });
        GroupLayout statusPanelColorClearLayout = new GroupLayout(this.statusPanelColorClear);
        this.statusPanelColorClear.setLayout(statusPanelColorClearLayout);
        statusPanelColorClearLayout.setHorizontalGroup(statusPanelColorClearLayout.createParallelGroup(Alignment.LEADING).addGap(0, 15, 32767));
        statusPanelColorClearLayout.setVerticalGroup(statusPanelColorClearLayout.createParallelGroup(Alignment.LEADING).addGap(0, 15, 32767));
        this.jPanel1.add(this.statusPanelColorClear);
        this.statusPanelColorReset.setToolTipText(bundle.getString("MainFrame.statusPanelColorReset.toolTipText"));
        this.statusPanelColorReset.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                MainFrame.this.statusPanelColorResetMouseClicked(evt);
            }
        });
        GroupLayout statusPanelColorResetLayout = new GroupLayout(this.statusPanelColorReset);
        this.statusPanelColorReset.setLayout(statusPanelColorResetLayout);
        statusPanelColorResetLayout.setHorizontalGroup(statusPanelColorResetLayout.createParallelGroup(Alignment.LEADING).addGap(0, 15, 32767));
        statusPanelColorResetLayout.setVerticalGroup(statusPanelColorResetLayout.createParallelGroup(Alignment.LEADING).addGap(0, 15, 32767));
        this.jPanel1.add(this.statusPanelColorReset);
        this.statusLinePanel.add(this.jPanel1);
        this.statusLabelCellCandidate.setText(bundle.getString("MainFrame.statusLabelCellCandidate.text.cell"));
        this.statusLabelCellCandidate.setToolTipText(bundle.getString("MainFrame.statusLabelCellCandidate.toolTipText"));
        this.statusLabelCellCandidate.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                MainFrame.this.statusLabelCellCandidateMouseClicked(evt);
            }
        });
        this.statusLinePanel.add(this.statusLabelCellCandidate);
        this.jSeparator1.setOrientation(1);
        this.jSeparator1.setPreferredSize(new Dimension(2, 17));
        this.statusLinePanel.add(this.jSeparator1);
        this.statusLabelLevel.setText(bundle.getString("MainFrame.statusLabelLevel.text"));
        this.statusLabelLevel.setToolTipText(bundle.getString("MainFrame.statusLabelLevel.toolTipText"));
        this.statusLinePanel.add(this.statusLabelLevel);
        this.jSeparator8.setOrientation(1);
        this.jSeparator8.setPreferredSize(new Dimension(2, 17));
        this.statusLinePanel.add(this.jSeparator8);
        this.progressLabel.setIcon(new ImageIcon(this.getClass().getResource("/img/invalid20.png")));
        this.progressLabel.setText("null");
        this.progressLabel.setToolTipText(bundle.getString("MainFrame.progressLabel.toolTipText"));
        this.statusLinePanel.add(this.progressLabel);
        this.jSeparator24.setOrientation(1);
        this.jSeparator24.setPreferredSize(new Dimension(2, 17));
        this.statusLinePanel.add(this.jSeparator24);
        this.statusLabelModus.setText(bundle.getString("MainFrame.statusLabelModus.textPlay"));
        this.statusLabelModus.setToolTipText(bundle.getString("MainFrame.statusLabelModus.toolTipText"));
        this.statusLinePanel.add(this.statusLabelModus);
        this.getContentPane().add(this.statusLinePanel, "South");
        this.undoToolButton.setIcon(new ImageIcon(this.getClass().getResource("/img/undo.png")));
        this.undoToolButton.setToolTipText(bundle.getString("MainFrame.undoToolButton.toolTipText"));
        this.undoToolButton.setEnabled(false);
        this.undoToolButton.setRequestFocusEnabled(false);
        this.undoToolButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.undoToolButtonActionPerformed(evt);
            }
        });
        this.jToolBar1.add(this.undoToolButton);
        this.redoToolButton.setIcon(new ImageIcon(this.getClass().getResource("/img/redo.png")));
        this.redoToolButton.setToolTipText(bundle.getString("MainFrame.redoToolButton.toolTipText"));
        this.redoToolButton.setEnabled(false);
        this.redoToolButton.setRequestFocusEnabled(false);
        this.redoToolButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.redoToolButtonActionPerformed(evt);
            }
        });
        this.jToolBar1.add(this.redoToolButton);
        this.jSeparator9.setOrientation(1);
        this.jSeparator9.setMaximumSize(new Dimension(5, 32767));
        this.jToolBar1.add(this.jSeparator9);
        this.neuesSpielToolButton.setIcon(new ImageIcon(this.getClass().getResource("/img/hodoku02-32.png")));
        this.neuesSpielToolButton.setToolTipText(bundle.getString("MainFrame.neuesSpielToolButton.toolTipText"));
        this.neuesSpielToolButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.neuesSpielToolButtonActionPerformed(evt);
            }
        });
        this.jToolBar1.add(this.neuesSpielToolButton);
        this.jSeparator12.setEnabled(false);
        this.jSeparator12.setMaximumSize(new Dimension(3, 0));
        this.jToolBar1.add(this.jSeparator12);
        this.levelComboBox.setToolTipText(bundle.getString("MainFrame.levelComboBox.toolTipText"));
        this.levelComboBox.setMaximumSize(new Dimension(80, 20));
        this.levelComboBox.setMinimumSize(new Dimension(15, 8));
        this.levelComboBox.setPreferredSize(new Dimension(20, 10));
        this.levelComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.levelComboBoxActionPerformed(evt);
            }
        });
        this.jToolBar1.add(this.levelComboBox);
        this.jSeparator13.setMaximumSize(new Dimension(3, 0));
        this.jToolBar1.add(this.jSeparator13);
        this.jSeparator11.setOrientation(1);
        this.jSeparator11.setMaximumSize(new Dimension(5, 32767));
        this.jToolBar1.add(this.jSeparator11);
        this.redGreenToggleButton.setIcon(new ImageIcon(this.getClass().getResource("/img/rgDeselected1.png")));
        this.redGreenToggleButton.setSelected(true);
        this.redGreenToggleButton.setToolTipText(bundle.getString("MainFrame.redGreenToggleButton.toolTipText"));
        this.redGreenToggleButton.setSelectedIcon(new ImageIcon(this.getClass().getResource("/img/rgSelected1.png")));
        this.redGreenToggleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.redGreenToggleButtonActionPerformed(evt);
            }
        });
        this.jToolBar1.add(this.redGreenToggleButton);
        this.f1ToggleButton.setIcon(new ImageIcon(this.getClass().getResource("/img/f_1c.png")));
        this.f1ToggleButton.setToolTipText(bundle.getString("MainFrame.f1ToggleButton.toolTipText"));
        this.f1ToggleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.f1ToggleButtonActionPerformed1(evt);
            }
        });
        this.jToolBar1.add(this.f1ToggleButton);
        this.f2ToggleButton.setIcon(new ImageIcon(this.getClass().getResource("/img/f_2c.png")));
        this.f2ToggleButton.setToolTipText(bundle.getString("MainFrame.f2ToggleButton.toolTipText"));
        this.f2ToggleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.f1ToggleButtonActionPerformed(evt);
            }
        });
        this.jToolBar1.add(this.f2ToggleButton);
        this.f3ToggleButton.setIcon(new ImageIcon(this.getClass().getResource("/img/f_3c.png")));
        this.f3ToggleButton.setToolTipText(bundle.getString("MainFrame.f3ToggleButton.toolTipText"));
        this.f3ToggleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.f1ToggleButtonActionPerformed(evt);
            }
        });
        this.jToolBar1.add(this.f3ToggleButton);
        this.f4ToggleButton.setIcon(new ImageIcon(this.getClass().getResource("/img/f_4c.png")));
        this.f4ToggleButton.setToolTipText(bundle.getString("MainFrame.f4ToggleButton.toolTipText"));
        this.f4ToggleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.f1ToggleButtonActionPerformed(evt);
            }
        });
        this.jToolBar1.add(this.f4ToggleButton);
        this.f5ToggleButton.setIcon(new ImageIcon(this.getClass().getResource("/img/f_5c.png")));
        this.f5ToggleButton.setToolTipText(bundle.getString("MainFrame.f5ToggleButton.toolTipText"));
        this.f5ToggleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.f1ToggleButtonActionPerformed(evt);
            }
        });
        this.jToolBar1.add(this.f5ToggleButton);
        this.f6ToggleButton.setIcon(new ImageIcon(this.getClass().getResource("/img/f_6c.png")));
        this.f6ToggleButton.setToolTipText(bundle.getString("MainFrame.f6ToggleButton.toolTipText"));
        this.f6ToggleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.f1ToggleButtonActionPerformed(evt);
            }
        });
        this.jToolBar1.add(this.f6ToggleButton);
        this.f7ToggleButton.setIcon(new ImageIcon(this.getClass().getResource("/img/f_7c.png")));
        this.f7ToggleButton.setToolTipText(bundle.getString("MainFrame.f7ToggleButton.toolTipText"));
        this.f7ToggleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.f1ToggleButtonActionPerformed(evt);
            }
        });
        this.jToolBar1.add(this.f7ToggleButton);
        this.f8ToggleButton.setIcon(new ImageIcon(this.getClass().getResource("/img/f_8c.png")));
        this.f8ToggleButton.setToolTipText(bundle.getString("MainFrame.f8ToggleButton.toolTipText"));
        this.f8ToggleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.f1ToggleButtonActionPerformed(evt);
            }
        });
        this.jToolBar1.add(this.f8ToggleButton);
        this.f9ToggleButton.setIcon(new ImageIcon(this.getClass().getResource("/img/f_9c.png")));
        this.f9ToggleButton.setToolTipText(bundle.getString("MainFrame.f9ToggleButton.toolTipText"));
        this.f9ToggleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.f1ToggleButtonActionPerformed(evt);
            }
        });
        this.jToolBar1.add(this.f9ToggleButton);
        this.fxyToggleButton.setIcon(new ImageIcon(this.getClass().getResource("/img/f_xyc.png")));
        this.fxyToggleButton.setToolTipText(bundle.getString("MainFrame.fxyToggleButton.toolTipText"));
        this.fxyToggleButton.setFocusable(false);
        this.fxyToggleButton.setHorizontalTextPosition(0);
        this.fxyToggleButton.setVerticalTextPosition(3);
        this.fxyToggleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.fxyToggleButtonf1ToggleButtonActionPerformed(evt);
            }
        });
        this.jToolBar1.add(this.fxyToggleButton);
        this.getContentPane().add(this.jToolBar1, "North");
        this.outerSplitPane.setDividerLocation(525);
        this.outerSplitPane.setOrientation(0);
        this.outerSplitPane.setResizeWeight(1.0);
        this.outerSplitPane.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                MainFrame.this.outerSplitPanePropertyChange(evt);
            }
        });
        this.hintPanel.setBorder(BorderFactory.createTitledBorder(bundle.getString("MainFrame.hintPanel.border.title")));
        this.hintPanel.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                MainFrame.this.hintPanelPropertyChange(evt);
            }
        });
        this.neuerHinweisButton.setMnemonic('n');
        this.neuerHinweisButton.setText(bundle.getString("MainFrame.neuerHinweisButton.text"));
        this.neuerHinweisButton.setToolTipText(bundle.getString("MainFrame.neuerHinweisButton.toolTipText"));
        this.neuerHinweisButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.neuerHinweisButtonActionPerformed(evt);
            }
        });
        this.hinweisAusfuehrenButton.setMnemonic('f');
        this.hinweisAusfuehrenButton.setText(bundle.getString("MainFrame.hinweisAusfuehrenButton.text"));
        this.hinweisAusfuehrenButton.setToolTipText(bundle.getString("MainFrame.hinweisAusfuehrenButton.toolTipText"));
        this.hinweisAusfuehrenButton.setEnabled(false);
        this.hinweisAusfuehrenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.hinweisAusfuehrenButtonActionPerformed(evt);
            }
        });
        this.solveUpToButton.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.solveUpToButton.mnemonic").charAt(0));
        this.solveUpToButton.setText(bundle.getString("MainFrame.solveUpToButton.text"));
        this.solveUpToButton.setToolTipText(bundle.getString("MainFrame.solveUpToButton.toolTipText"));
        this.solveUpToButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.solveUpToButtonActionPerformed(evt);
            }
        });
        this.hinweisAbbrechenButton.setMnemonic('a');
        this.hinweisAbbrechenButton.setText(bundle.getString("MainFrame.hinweisAbbrechenButton.text"));
        this.hinweisAbbrechenButton.setToolTipText(bundle.getString("MainFrame.hinweisAbbrechenButton.toolTipText"));
        this.hinweisAbbrechenButton.setEnabled(false);
        this.hinweisAbbrechenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.hinweisAbbrechenButtonActionPerformed(evt);
            }
        });
        this.hinweisTextArea.setColumns(20);
        this.hinweisTextArea.setEditable(false);
        this.hinweisTextArea.setLineWrap(true);
        this.hinweisTextArea.setRows(5);
        this.hinweisTextArea.setWrapStyleWord(true);
        this.jScrollPane1.setViewportView(this.hinweisTextArea);
        GroupLayout hintPanelLayout = new GroupLayout(this.hintPanel);
        this.hintPanel.setLayout(hintPanelLayout);
        hintPanelLayout.setHorizontalGroup(
                hintPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                Alignment.TRAILING,
                                hintPanelLayout.createSequentialGroup()
                                        .addComponent(this.jScrollPane1, -1, 476, 32767)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(hintPanelLayout.createParallelGroup(Alignment.TRAILING).addComponent(this.neuerHinweisButton).addComponent(this.solveUpToButton))
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                hintPanelLayout.createParallelGroup(Alignment.TRAILING)
                                                        .addComponent(this.hinweisAusfuehrenButton)
                                                        .addComponent(this.hinweisAbbrechenButton)
                                        )
                        )
        );
        hintPanelLayout.linkSize(0, this.hinweisAbbrechenButton, this.hinweisAusfuehrenButton, this.neuerHinweisButton, this.solveUpToButton);
        hintPanelLayout.setVerticalGroup(
                hintPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                hintPanelLayout.createSequentialGroup()
                                        .addGroup(
                                                hintPanelLayout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(this.hinweisAusfuehrenButton, -2, 23, -2)
                                                        .addComponent(this.neuerHinweisButton)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                hintPanelLayout.createParallelGroup(Alignment.BASELINE).addComponent(this.hinweisAbbrechenButton).addComponent(this.solveUpToButton)
                                        )
                        )
                        .addComponent(this.jScrollPane1, -1, 77, 32767)
        );
        hintPanelLayout.linkSize(1, this.hinweisAbbrechenButton, this.hinweisAusfuehrenButton, this.neuerHinweisButton, this.solveUpToButton);
        this.outerSplitPane.setRightComponent(this.hintPanel);
        this.getContentPane().add(this.outerSplitPane, "Center");
        this.dateiMenu.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.dateiMenuMnemonic").charAt(0));
        this.dateiMenu.setText(bundle.getString("MainFrame.dateiMenu.text"));
        this.neuMenuItem.setAccelerator(KeyStroke.getKeyStroke(78, 2));
        this.neuMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.neuMenuItemMnemonic").charAt(0));
        this.neuMenuItem.setText(bundle.getString("MainFrame.neuMenuItem.text"));
        this.neuMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.neuMenuItemActionPerformed(evt);
            }
        });
        this.dateiMenu.add(this.neuMenuItem);
        this.dateiMenu.add(this.jSeparator39);
        this.loadPuzzleMenuItem.setAccelerator(KeyStroke.getKeyStroke(79, 2));
        this.loadPuzzleMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.loadMenuItemMnemonic").charAt(0));
        this.loadPuzzleMenuItem.setText(bundle.getString("MainFrame.loadPuzzleMenuItem.text"));
        this.loadPuzzleMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.loadPuzzleMenuItemActionPerformed(evt);
            }
        });
        this.dateiMenu.add(this.loadPuzzleMenuItem);
        this.savePuzzleMenuItem.setAccelerator(KeyStroke.getKeyStroke(83, 2));
        this.savePuzzleMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.savePuzzleMenuItem.mnemonic").charAt(0));
        this.savePuzzleMenuItem.setText(bundle.getString("MainFrame.savePuzzleMenuItem.text"));
        this.savePuzzleMenuItem.setEnabled(false);
        this.savePuzzleMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.savePuzzleMenuItemActionPerformed(evt);
            }
        });
        this.dateiMenu.add(this.savePuzzleMenuItem);
        this.savePuzzleAsMenuItem.setAccelerator(KeyStroke.getKeyStroke(83, 1));
        this.savePuzzleAsMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.saveAsMenuItemMnemonic").charAt(0));
        this.savePuzzleAsMenuItem.setText(bundle.getString("MainFrame.savePuzzleAsMenuItem.text"));
        this.savePuzzleAsMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.savePuzzleAsMenuItemActionPerformed(evt);
            }
        });
        this.dateiMenu.add(this.savePuzzleAsMenuItem);
        this.loadConfigMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.loadConfigMenuItem.mnemonic").charAt(0));
        this.loadConfigMenuItem.setText(bundle.getString("MainFrame.loadConfigMenuItem.text"));
        this.loadConfigMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.loadConfigMenuItemActionPerformed(evt);
            }
        });
        this.dateiMenu.add(this.loadConfigMenuItem);
        this.saveConfigAsMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.saveConfigAsMenuItem.mnemonic").charAt(0));
        this.saveConfigAsMenuItem.setText(bundle.getString("MainFrame.saveConfigAsMenuItem.text"));
        this.saveConfigAsMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.saveConfigAsMenuItemActionPerformed(evt);
            }
        });
        this.dateiMenu.add(this.saveConfigAsMenuItem);
        this.dateiMenu.add(this.jSeparator38);
        this.seiteEinrichtenMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.seiteEinrichtenMenuItemMnemonic").charAt(0));
        this.seiteEinrichtenMenuItem.setText(bundle.getString("MainFrame.seiteEinrichtenMenuItem.text"));
        this.seiteEinrichtenMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.seiteEinrichtenMenuItemActionPerformed(evt);
            }
        });
        this.dateiMenu.add(this.seiteEinrichtenMenuItem);
        this.druckenMenuItem.setAccelerator(KeyStroke.getKeyStroke(80, 2));
        this.druckenMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.druckenMenuItemMnemonic").charAt(0));
        this.druckenMenuItem.setText(bundle.getString("MainFrame.druckenMenuItem.text"));
        this.druckenMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.druckenMenuItemActionPerformed(evt);
            }
        });
        this.dateiMenu.add(this.druckenMenuItem);
        this.extendedPrintMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.extendedPrintMenuItem.mnemonic").charAt(0));
        this.extendedPrintMenuItem.setText(bundle.getString("MainFrame.extendedPrintMenuItem.text"));
        this.extendedPrintMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.extendedPrintMenuItemActionPerformed(evt);
            }
        });
        this.dateiMenu.add(this.extendedPrintMenuItem);
        this.speichernAlsBildMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.speichernAlsBildMenuItemMnemonic").charAt(0));
        this.speichernAlsBildMenuItem.setText(bundle.getString("MainFrame.speichernAlsBildMenuItem.text"));
        this.speichernAlsBildMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.speichernAlsBildMenuItemActionPerformed(evt);
            }
        });
        this.dateiMenu.add(this.speichernAlsBildMenuItem);
        this.dateiMenu.add(this.jSeparator37);
        this.spielEingebenMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.spielEingebenMenuItemMnemonic").charAt(0));
        this.spielEingebenMenuItem.setText(bundle.getString("MainFrame.spielEingebenMenuItem.text"));
        this.spielEingebenMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.spielEingebenMenuItemActionPerformed(evt);
            }
        });
        this.dateiMenu.add(this.spielEingebenMenuItem);
        this.spielEditierenMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("spielEditierenMenuItemMnemonic").charAt(0));
        this.spielEditierenMenuItem.setText(bundle.getString("MainFrame.spielEditierenMenuItem.text"));
        this.spielEditierenMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.spielEditierenMenuItemActionPerformed(evt);
            }
        });
        this.dateiMenu.add(this.spielEditierenMenuItem);
        this.spielSpielenMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.spielenMenuItemMnemonic").charAt(0));
        this.spielSpielenMenuItem.setText(bundle.getString("MainFrame.spielSpielenMenuItem.text"));
        this.spielSpielenMenuItem.setEnabled(false);
        this.spielSpielenMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.spielSpielenMenuItemActionPerformed(evt);
            }
        });
        this.dateiMenu.add(this.spielSpielenMenuItem);
        this.dateiMenu.add(this.jSeparator36);
        this.beendenMenuItem.setAccelerator(KeyStroke.getKeyStroke(88, 8));
        this.beendenMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.beendenMenuItemMnemonic").charAt(0));
        this.beendenMenuItem.setText(bundle.getString("MainFrame.beendenMenuItem.text"));
        this.beendenMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.beendenMenuItemActionPerformed(evt);
            }
        });
        this.dateiMenu.add(this.beendenMenuItem);
        this.jMenuBar1.add(this.dateiMenu);
        this.bearbeitenMenu.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.bearbeitenMenuMnemonic").charAt(0));
        this.bearbeitenMenu.setText(bundle.getString("MainFrame.bearbeitenMenu.text"));
        this.undoMenuItem.setAccelerator(KeyStroke.getKeyStroke(90, 2));
        this.undoMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.undoMenuItemMnemonic").charAt(0));
        this.undoMenuItem.setText(bundle.getString("MainFrame.undoMenuItem.text"));
        this.undoMenuItem.setEnabled(false);
        this.undoMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.undoMenuItemActionPerformed(evt);
            }
        });
        this.bearbeitenMenu.add(this.undoMenuItem);
        this.redoMenuItem.setAccelerator(KeyStroke.getKeyStroke(89, 2));
        this.redoMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.redoMenuItemMnemonic").charAt(0));
        this.redoMenuItem.setText(bundle.getString("MainFrame.redoMenuItem.text"));
        this.redoMenuItem.setEnabled(false);
        this.redoMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.redoMenuItemActionPerformed(evt);
            }
        });
        this.bearbeitenMenu.add(this.redoMenuItem);
        this.bearbeitenMenu.add(this.jSeparator35);
        this.copyCluesMenuItem.setAccelerator(KeyStroke.getKeyStroke(71, 2));
        this.copyCluesMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.copyCluesMenuItemMnemonic").charAt(0));
        this.copyCluesMenuItem.setText(bundle.getString("MainFrame.copyCluesMenuItem.text"));
        this.copyCluesMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.copyCluesMenuItemActionPerformed(evt);
            }
        });
        this.bearbeitenMenu.add(this.copyCluesMenuItem);
        this.copyFilledMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.copyFilledMenuItemMnemonic").charAt(0));
        this.copyFilledMenuItem.setText(bundle.getString("MainFrame.copyFilledMenuItem.text"));
        this.copyFilledMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.copyFilledMenuItemActionPerformed(evt);
            }
        });
        this.bearbeitenMenu.add(this.copyFilledMenuItem);
        this.copyPmGridMenuItem.setAccelerator(KeyStroke.getKeyStroke(67, 2));
        this.copyPmGridMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.copyPmGridMenuItemMnemonic").charAt(0));
        this.copyPmGridMenuItem.setText(bundle.getString("MainFrame.copyPmGridMenuItem.text"));
        this.copyPmGridMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.copyPmGridMenuItemActionPerformed(evt);
            }
        });
        this.bearbeitenMenu.add(this.copyPmGridMenuItem);
        this.copyPmGridWithStepMenuItem
                .setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.copyPmGridWithStepMenuItemMnemonic").charAt(0));
        this.copyPmGridWithStepMenuItem.setText(bundle.getString("MainFrame.copyPmGridWithStepMenuItem.text"));
        this.copyPmGridWithStepMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.copyPmGridWithStepMenuItemActionPerformed(evt);
            }
        });
        this.bearbeitenMenu.add(this.copyPmGridWithStepMenuItem);
        this.copyLibraryMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.copyLibraryMenuItemMnemonic").charAt(0));
        this.copyLibraryMenuItem.setText(bundle.getString("MainFrame.copyLibraryMenuItem.text"));
        this.copyLibraryMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.copyLibraryMenuItemActionPerformed(evt);
            }
        });
        this.bearbeitenMenu.add(this.copyLibraryMenuItem);
        this.copySSMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.copySSMenuItem.mnemonic").charAt(0));
        this.copySSMenuItem.setText(bundle.getString("MainFrame.copySSMenuItem.text"));
        this.copySSMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.copySSMenuItemActionPerformed(evt);
            }
        });
        this.bearbeitenMenu.add(this.copySSMenuItem);
        this.pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(86, 2));
        this.pasteMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.pasteMenuItemMnemonic").charAt(0));
        this.pasteMenuItem.setText(bundle.getString("MainFrame.pasteMenuItem.text"));
        this.pasteMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.pasteMenuItemActionPerformed(evt);
            }
        });
        this.bearbeitenMenu.add(this.pasteMenuItem);
        this.bearbeitenMenu.add(this.jSeparator34);
        this.restartSpielMenuItem.setAccelerator(KeyStroke.getKeyStroke(82, 2));
        this.restartSpielMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.restartSpielMenuItemMnemonic").charAt(0));
        this.restartSpielMenuItem.setText(bundle.getString("MainFrame.restartSpielMenuItem.text"));
        this.restartSpielMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.restartSpielMenuItemActionPerformed(evt);
            }
        });
        this.bearbeitenMenu.add(this.restartSpielMenuItem);
        this.resetSpielMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.resetSpielMenuItemMnemonic").charAt(0));
        this.resetSpielMenuItem.setText(bundle.getString("MainFrame.resetSpielMenuItem.text"));
        this.resetSpielMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.resetSpielMenuItemActionPerformed(evt);
            }
        });
        this.bearbeitenMenu.add(this.resetSpielMenuItem);
        this.bearbeitenMenu.add(this.jSeparator33);
        this.configMenuItem.setAccelerator(KeyStroke.getKeyStroke(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.configMenuItemAccelerator")));
        this.configMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.configMenuItemMnemonic").charAt(0));
        this.configMenuItem.setText(bundle.getString("MainFrame.configMenuItem.text"));
        this.configMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.configMenuItemActionPerformed(evt);
            }
        });
        this.bearbeitenMenu.add(this.configMenuItem);
        this.jMenuBar1.add(this.bearbeitenMenu);
        this.modeMenu.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.modeMenu.mnemonic").charAt(0));
        this.modeMenu.setText(bundle.getString("MainFrame.modeMenu.text"));
        this.modeButtonGroup.add(this.playingMenuItem);
        this.playingMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.playingMenuItem.mnemonic").charAt(0));
        this.playingMenuItem.setSelected(true);
        this.playingMenuItem.setText(bundle.getString("MainFrame.playingMenuItem.text"));
        this.playingMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.playingMenuItemActionPerformed(evt);
            }
        });
        this.modeMenu.add(this.playingMenuItem);
        this.modeButtonGroup.add(this.learningMenuItem);
        this.learningMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.learningMenuItem.mnemonic").charAt(0));
        this.learningMenuItem.setText(bundle.getString("MainFrame.learningMenuItem.text"));
        this.learningMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.learningMenuItemActionPerformed(evt);
            }
        });
        this.modeMenu.add(this.learningMenuItem);
        this.modeButtonGroup.add(this.practisingMenuItem);
        this.practisingMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.practisingMenuItem.mnemonic").charAt(0));
        this.practisingMenuItem.setText(bundle.getString("MainFrame.practisingMenuItem.text"));
        this.practisingMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.practisingMenuItemActionPerformed(evt);
            }
        });
        this.modeMenu.add(this.practisingMenuItem);
        this.jMenuBar1.add(this.modeMenu);
        this.optionenMenu.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.optionenMenuMnemonic").charAt(0));
        this.optionenMenu.setText(bundle.getString("MainFrame.optionenMenu.text"));
        this.showCandidatesMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.showCandidatesMenuItemMnemonic").charAt(0));
        this.showCandidatesMenuItem.setText(bundle.getString("MainFrame.showCandidatesMenuItem.text"));
        this.showCandidatesMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.showCandidatesMenuItemActionPerformed(evt);
            }
        });
        this.optionenMenu.add(this.showCandidatesMenuItem);
        this.showWrongValuesMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.showWrongValuesMenuItemMnemonic").charAt(0));
        this.showWrongValuesMenuItem.setText(bundle.getString("MainFrame.showWrongValuesMenuItem.text"));
        this.showWrongValuesMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.showWrongValuesMenuItemActionPerformed(evt);
            }
        });
        this.optionenMenu.add(this.showWrongValuesMenuItem);
        this.showDeviationsMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.showDeviationsMenuItemMnemonic").charAt(0));
        this.showDeviationsMenuItem.setSelected(true);
        this.showDeviationsMenuItem.setText(bundle.getString("MainFrame.showDeviationsMenuItem.text"));
        this.showDeviationsMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.showDeviationsMenuItemActionPerformed(evt);
            }
        });
        this.optionenMenu.add(this.showDeviationsMenuItem);
        this.showColorKuMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.showColorKuMenuItem.mnemonic").charAt(0));
        this.showColorKuMenuItem.setSelected(true);
        this.showColorKuMenuItem.setText(bundle.getString("MainFrame.showColorKuMenuItem.text"));
        this.showColorKuMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.showColorKuMenuItemActionPerformed(evt);
            }
        });
        this.optionenMenu.add(this.showColorKuMenuItem);
        this.optionenMenu.add(this.jSeparator32);
        this.colorButtonGroup.add(this.colorCellsMenuItem);
        this.colorCellsMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.colorCellsMenuItem.mnemonic").charAt(0));
        this.colorCellsMenuItem.setSelected(true);
        this.colorCellsMenuItem.setText(bundle.getString("MainFrame.colorCellsMenuItem.text"));
        this.colorCellsMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.colorCellsMenuItemActionPerformed(evt);
            }
        });
        this.optionenMenu.add(this.colorCellsMenuItem);
        this.colorButtonGroup.add(this.colorCandidatesMenuItem);
        this.colorCandidatesMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.colorCandidatesMenuItem.mnemonic").charAt(0));
        this.colorCandidatesMenuItem.setText(bundle.getString("MainFrame.colorCandidatesMenuItem.text"));
        this.colorCandidatesMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.colorCandidatesMenuItemActionPerformed(evt);
            }
        });
        this.optionenMenu.add(this.colorCandidatesMenuItem);
        this.optionenMenu.add(this.jSeparator31);
        this.levelMenu.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.levelMenuMnemonic").charAt(0));
        this.levelMenu.setText(bundle.getString("MainFrame.levelMenu.text"));
        this.levelButtonGroup.add(this.levelLeichtMenuItem);
        this.levelLeichtMenuItem.setSelected(true);
        this.levelLeichtMenuItem.setText("Leicht");
        this.levelLeichtMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.levelLeichtMenuItemActionPerformed(evt);
            }
        });
        this.levelMenu.add(this.levelLeichtMenuItem);
        this.levelButtonGroup.add(this.levelMittelMenuItem);
        this.levelMittelMenuItem.setText("Mittel");
        this.levelMittelMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.levelMittelMenuItemActionPerformed(evt);
            }
        });
        this.levelMenu.add(this.levelMittelMenuItem);
        this.levelButtonGroup.add(this.levelKniffligMenuItem);
        this.levelKniffligMenuItem.setText("Schwer\n");
        this.levelKniffligMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.levelKniffligMenuItemActionPerformed(evt);
            }
        });
        this.levelMenu.add(this.levelKniffligMenuItem);
        this.levelButtonGroup.add(this.levelSchwerMenuItem);
        this.levelSchwerMenuItem.setText("Unfair");
        this.levelSchwerMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.levelSchwerMenuItemActionPerformed(evt);
            }
        });
        this.levelMenu.add(this.levelSchwerMenuItem);
        this.levelButtonGroup.add(this.levelExtremMenuItem);
        this.levelExtremMenuItem.setText("Extrem");
        this.levelExtremMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.levelExtremMenuItemActionPerformed(evt);
            }
        });
        this.levelMenu.add(this.levelExtremMenuItem);
        this.optionenMenu.add(this.levelMenu);
        this.jMenuBar1.add(this.optionenMenu);
        this.raetselMenu.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.raetselMenuMnemonic").charAt(0));
        this.raetselMenu.setText(bundle.getString("MainFrame.raetselMenu.text"));
        this.vageHintMenuItem.setAccelerator(KeyStroke.getKeyStroke(123, 8));
        this.vageHintMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.vageHintMenuItemMnemonic").charAt(0));
        this.vageHintMenuItem.setText(bundle.getString("MainFrame.vageHintMenuItem"));
        this.vageHintMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.vageHintMenuItemActionPerformed(evt);
            }
        });
        this.raetselMenu.add(this.vageHintMenuItem);
        this.mediumHintMenuItem.setAccelerator(KeyStroke.getKeyStroke(123, 2));
        this.mediumHintMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.mediumHintMenuItemMnemonic").charAt(0));
        this.mediumHintMenuItem.setText(bundle.getString("MainFrame.mediumHintMenuItem.text"));
        this.mediumHintMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.mediumHintMenuItemActionPerformed(evt);
            }
        });
        this.raetselMenu.add(this.mediumHintMenuItem);
        this.loesungsSchrittMenuItem.setAccelerator(KeyStroke.getKeyStroke(123, 0));
        this.loesungsSchrittMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.loesungsSchrittMenuItemMnemonic").charAt(0));
        this.loesungsSchrittMenuItem.setText(bundle.getString("MainFrame.loesungsSchrittMenuItem.text"));
        this.loesungsSchrittMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.loesungsSchrittMenuItemActionPerformed(evt);
            }
        });
        this.raetselMenu.add(this.loesungsSchrittMenuItem);
        this.raetselMenu.add(this.jSeparator30);
        this.backdoorSearchMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.backdoorSearchMenuItem.mnemonic").charAt(0));
        this.backdoorSearchMenuItem.setText(bundle.getString("MainFrame.backdoorSearchMenuItem.text"));
        this.backdoorSearchMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.backdoorSearchMenuItemActionPerformed(evt);
            }
        });
        this.raetselMenu.add(this.backdoorSearchMenuItem);
        this.historyMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.historyMenuItem.mnemonic").charAt(0));
        this.historyMenuItem.setText(bundle.getString("MainFrame.historyMenuItem.text"));
        this.historyMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.historyMenuItemActionPerformed(evt);
            }
        });
        this.raetselMenu.add(this.historyMenuItem);
        this.createSavePointMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.setSavePointMenuItem.mnemonic").charAt(0));
        this.createSavePointMenuItem.setText(bundle.getString("MainFrame.createSavePointMenuItem.text"));
        this.createSavePointMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.createSavePointMenuItemActionPerformed(evt);
            }
        });
        this.raetselMenu.add(this.createSavePointMenuItem);
        this.restoreSavePointMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.restoreSavePointMenuItem.mnemonic").charAt(0));
        this.restoreSavePointMenuItem.setText(bundle.getString("MainFrame.restoreSavePointMenuItem.text"));
        this.restoreSavePointMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.restoreSavePointMenuItemActionPerformed(evt);
            }
        });
        this.raetselMenu.add(this.restoreSavePointMenuItem);
        this.raetselMenu.add(this.jSeparator29);
        this.setGivensMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.setGivensMenuItem.mnemonic").charAt(0));
        this.setGivensMenuItem.setText(bundle.getString("MainFrame.setGivensMenuItem.text"));
        this.setGivensMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.setGivensMenuItemActionPerformed(evt);
            }
        });
        this.raetselMenu.add(this.setGivensMenuItem);
        this.raetselMenu.add(this.jSeparator28);
        this.alleHiddenSinglesSetzenMenuItem.setAccelerator(KeyStroke.getKeyStroke(122, 0));
        this.alleHiddenSinglesSetzenMenuItem
                .setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.alleHiddenSinglesSetzenMenuItemMnemonic").charAt(0));
        this.alleHiddenSinglesSetzenMenuItem.setText(bundle.getString("MainFrame.alleHiddenSinglesSetzenMenuItem.text"));
        this.alleHiddenSinglesSetzenMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.alleHiddenSinglesSetzenMenuItemActionPerformed(evt);
            }
        });
        this.raetselMenu.add(this.alleHiddenSinglesSetzenMenuItem);
        this.jMenuBar1.add(this.raetselMenu);
        this.ansichtMenu.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.ansichtMenuMnemonic").charAt(0));
        this.ansichtMenu.setText(bundle.getString("MainFrame.ansichtMenu.text"));
        this.sudokuOnlyMenuItem
                .setAccelerator(
                        KeyStroke.getKeyStroke(
                                "shift control " + ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.sudokuOnlyMenuItemMnemonic").toUpperCase().charAt(0)
                        )
                );
        this.viewButtonGroup.add(this.sudokuOnlyMenuItem);
        this.sudokuOnlyMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.sudokuOnlyMenuItemMnemonic").charAt(0));
        this.sudokuOnlyMenuItem.setSelected(true);
        this.sudokuOnlyMenuItem.setText(bundle.getString("MainFrame.sudokuOnlyMenuItem.text"));
        this.sudokuOnlyMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.sudokuOnlyMenuItemActionPerformed(evt);
            }
        });
        this.ansichtMenu.add(this.sudokuOnlyMenuItem);
        this.ansichtMenu.add(this.jSeparator20);
        this.summaryMenuItem
                .setAccelerator(
                        KeyStroke.getKeyStroke(
                                "shift control " + ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.summaryMenuItemMnemonic").toUpperCase().charAt(0)
                        )
                );
        this.viewButtonGroup.add(this.summaryMenuItem);
        this.summaryMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.summaryMenuItemMnemonic").charAt(0));
        this.summaryMenuItem.setText(bundle.getString("MainFrame.summaryMenuItem.text"));
        this.summaryMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.summaryMenuItemActionPerformed(evt);
            }
        });
        this.ansichtMenu.add(this.summaryMenuItem);
        this.solutionMenuItem
                .setAccelerator(
                        KeyStroke.getKeyStroke(
                                "shift control " + ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.solutionMenuItemMnemonic").toUpperCase().charAt(0)
                        )
                );
        this.viewButtonGroup.add(this.solutionMenuItem);
        this.solutionMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.solutionMenuItemMnemonic").charAt(0));
        this.solutionMenuItem.setText(bundle.getString("MainFrame.solutionMenuItem.text"));
        this.solutionMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.solutionMenuItemActionPerformed(evt);
            }
        });
        this.ansichtMenu.add(this.solutionMenuItem);
        this.allStepsMenuItem
                .setAccelerator(
                        KeyStroke.getKeyStroke(
                                "shift control " + ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.allStepsMenuItemMnemonic").toUpperCase().charAt(0)
                        )
                );
        this.viewButtonGroup.add(this.allStepsMenuItem);
        this.allStepsMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.allStepsMenuItemMnemonic").charAt(0));
        this.allStepsMenuItem.setText(bundle.getString("MainFrame.allStepsMenuItem.text"));
        this.allStepsMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.allStepsMenuItemActionPerformed(evt);
            }
        });
        this.ansichtMenu.add(this.allStepsMenuItem);
        this.cellZoomMenuItem
                .setAccelerator(
                        KeyStroke.getKeyStroke(
                                "shift control " + ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.cellZoomMenuItemMnemonic").toUpperCase().charAt(0)
                        )
                );
        this.viewButtonGroup.add(this.cellZoomMenuItem);
        this.cellZoomMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.cellZoomMenuItemMnemonic").charAt(0));
        this.cellZoomMenuItem.setText(bundle.getString("MainFrame.cellZoomMenuItem.text"));
        this.cellZoomMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.cellZoomMenuItemActionPerformed(evt);
            }
        });
        this.ansichtMenu.add(this.cellZoomMenuItem);
        this.ansichtMenu.add(this.jSeparator6);
        this.showHintPanelMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.showHintPanelMenuItem.mnemonic").charAt(0));
        this.showHintPanelMenuItem.setSelected(true);
        this.showHintPanelMenuItem.setText(bundle.getString("MainFrame.showHintPanelMenuItem.text"));
        this.showHintPanelMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.showHintPanelMenuItemActionPerformed(evt);
            }
        });
        this.ansichtMenu.add(this.showHintPanelMenuItem);
        this.showToolBarMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.showToolBarMenuItem.mnemonic").charAt(0));
        this.showToolBarMenuItem.setSelected(true);
        this.showToolBarMenuItem.setText(bundle.getString("MainFrame.showToolBarMenuItem.text"));
        this.showToolBarMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.showToolBarMenuItemActionPerformed(evt);
            }
        });
        this.ansichtMenu.add(this.showToolBarMenuItem);
        this.showHintButtonsCheckBoxMenuItem
                .setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.showHintButtonsCheckBoxMenuItem.mnemonic").charAt(0));
        this.showHintButtonsCheckBoxMenuItem.setText(bundle.getString("MainFrame.showHintButtonsCheckBoxMenuItem.text"));
        this.showHintButtonsCheckBoxMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.showHintButtonsCheckBoxMenuItemActionPerformed(evt);
            }
        });
        this.ansichtMenu.add(this.showHintButtonsCheckBoxMenuItem);
        this.fullScreenMenuItem.setAccelerator(KeyStroke.getKeyStroke(70, 3));
        this.fullScreenMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.fullScreenMenuItem.mnemonic").charAt(0));
        this.fullScreenMenuItem.setText(bundle.getString("MainFrame.fullScreenMenuItem.text"));
        this.fullScreenMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.fullScreenMenuItemActionPerformed(evt);
            }
        });
        this.ansichtMenu.add(this.fullScreenMenuItem);
        this.ansichtMenu.add(this.jSeparator23);
        this.resetViewMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.resetViewMenuItemMnemonic").charAt(0));
        this.resetViewMenuItem.setText(bundle.getString("MainFrame.resetViewMenuItem.text"));
        this.resetViewMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.resetViewMenuItemActionPerformed(evt);
            }
        });
        this.ansichtMenu.add(this.resetViewMenuItem);
        this.jMenuBar1.add(this.ansichtMenu);
        this.helpMenu.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.helpMenu.mnemonic").charAt(0));
        this.helpMenu.setText(bundle.getString("MainFrame.helpMenu.text"));
        this.keyMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.keyMenuItem.mnemonic").charAt(0));
        this.keyMenuItem.setText(bundle.getString("MainFrame.keyMenuItem.text"));
        this.keyMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.keyMenuItemActionPerformed(evt);
            }
        });
        this.helpMenu.add(this.keyMenuItem);
        this.helpMenu.add(this.jSeparator26);
        this.userManualMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.userManualMenuItem.mnemonic").charAt(0));
        this.userManualMenuItem.setText(bundle.getString("MainFrame.userManualMenuItem.text"));
        this.userManualMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.userManualMenuItemActionPerformed(evt);
            }
        });
        this.helpMenu.add(this.userManualMenuItem);
        this.solvingGuideMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.solvingGuideMenuItem.mnemonic").charAt(0));
        this.solvingGuideMenuItem.setText(bundle.getString("MainFrame.solvingGuideMenuItem.text"));
        this.solvingGuideMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.solvingGuideMenuItemActionPerformed(evt);
            }
        });
        this.helpMenu.add(this.solvingGuideMenuItem);
        this.projectHomePageMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.projectHomePageMenuItem.mnemonic").charAt(0));
        this.projectHomePageMenuItem.setText(bundle.getString("MainFrame.projectHomePageMenuItem.text"));
        this.projectHomePageMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.projectHomePageMenuItemActionPerformed(evt);
            }
        });
        this.helpMenu.add(this.projectHomePageMenuItem);
        this.helpMenu.add(this.jSeparator27);
        this.reportErrorMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.reportErrorMenuItem.mnemonic").charAt(0));
        this.reportErrorMenuItem.setText(bundle.getString("MainFrame.reportErrorMenuItem.text"));
        this.reportErrorMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.reportErrorMenuItemActionPerformed(evt);
            }
        });
        this.helpMenu.add(this.reportErrorMenuItem);
        this.askQuestionMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.askQuestionMenuItem.mnemonic").charAt(0));
        this.askQuestionMenuItem.setText(bundle.getString("MainFrame.askQuestionMenuItem.text"));
        this.askQuestionMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.askQuestionMenuItemActionPerformed(evt);
            }
        });
        this.helpMenu.add(this.askQuestionMenuItem);
        this.helpMenu.add(this.jSeparator25);
        this.aboutMenuItem.setMnemonic(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.aboutMenuItem.").charAt(0));
        this.aboutMenuItem.setText(bundle.getString("MainFrame.aboutMenuItem.text"));
        this.aboutMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MainFrame.this.aboutMenuItemActionPerformed(evt);
            }
        });
        this.helpMenu.add(this.aboutMenuItem);
        this.jMenuBar1.add(this.helpMenu);
        this.setJMenuBar(this.jMenuBar1);
        this.pack();
    }

    private void savePuzzleAsMenuItemActionPerformed(ActionEvent evt) {
        this.saveToFile(true);
    }

    private void loadPuzzleMenuItemActionPerformed(ActionEvent evt) {
        this.loadFromFile(true);
    }

    private void configMenuItemActionPerformed(ActionEvent evt) {
        new ConfigDialog(this, true, -1).setVisible(true);
        this.sudokuPanel.resetActiveColor();
        if (this.sudokuPanel.getActiveColor() != -1) {
            this.statusPanelColorResult.setBackground(Options.getInstance().getColoringColors()[this.sudokuPanel.getActiveColor()]);
        }

        this.sudokuPanel.setColorIconsInPopupMenu();
        this.check();
        this.fixFocus();
        this.sudokuPanel.repaint();
        this.repaint();
    }

    private void statusLabelCellCandidateMouseClicked(MouseEvent evt) {
        this.sudokuPanel.setColorCells(!this.sudokuPanel.isColorCells());
        this.check();
        this.fixFocus();
    }

    private void allStepsMenuItemActionPerformed(ActionEvent evt) {
        this.allStepsPanel.setSudoku(this.sudokuPanel.getSudoku());
        this.setSplitPane(this.allStepsPanel);
        this.repaint();
    }

    private void solutionMenuItemActionPerformed(ActionEvent evt) {
        this.setSplitPane(this.solutionPanel);
        this.repaint();
    }

    private void sudokuOnlyMenuItemActionPerformed(ActionEvent evt) {
        this.splitPanel.setRight(null);
        if (this.getExtendedState() != 6 && this.splitPanel.getBounds().getWidth() < this.splitPanel.getBounds().getHeight()) {
            this.setSize(this.getWidth() + 1, this.getHeight());
        }
    }

    private void summaryMenuItemActionPerformed(ActionEvent evt) {
        this.setSplitPane(this.summaryPanel);
        this.repaint();
    }

    private void speichernAlsBildMenuItemActionPerformed(ActionEvent evt) {
        WriteAsPNGDialog dlg = new WriteAsPNGDialog(this, true, this.bildSize, this.bildAufloesung, this.bildEinheit);
        dlg.setVisible(true);
        if (dlg.isOk()) {
            File bildFile = dlg.getBildFile();
            this.bildAufloesung = dlg.getAufloesung();
            this.bildSize = dlg.getBildSize();
            this.bildEinheit = dlg.getEinheit();
            int size = 0;
            switch (this.bildEinheit) {
                case 0:
                    size = (int) (this.bildSize / 25.4 * this.bildAufloesung);
                    break;
                case 1:
                    size = (int) (this.bildSize * this.bildAufloesung);
                    break;
                case 2:
                    size = (int) this.bildSize;
            }

            if (bildFile.exists()) {
                MessageFormat msgf = new MessageFormat("");
                Object[] args = new Object[]{bildFile.getName()};
                msgf.applyPattern(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.file_exists"));
                String warning = msgf.format(args);
                String title = ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.hint");
                if (JOptionPane.showConfirmDialog(null, warning, title, 0) != 0) {
                    return;
                }
            }

            this.sudokuPanel.saveSudokuAsPNG(bildFile, size, this.bildAufloesung);
        }
    }

    private void druckenMenuItemActionPerformed(ActionEvent evt) {
        if (this.job == null) {
            this.job = PrinterJob.getPrinterJob();
        }

        if (this.pageFormat == null) {
            this.pageFormat = this.job.defaultPage();
        }

        try {
            this.job.setPrintable(this.sudokuPanel, this.pageFormat);
            if (this.job.printDialog()) {
                this.job.print();
            }
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(this, ex.toString(), ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.error"), 0);
        }
    }

    private void seiteEinrichtenMenuItemActionPerformed(ActionEvent evt) {
        if (this.job == null) {
            this.job = PrinterJob.getPrinterJob();
        }

        if (this.pageFormat == null) {
            this.pageFormat = this.job.defaultPage();
        }

        this.pageFormat = this.job.pageDialog(this.pageFormat);
    }

    private void restartSpielMenuItemActionPerformed(ActionEvent evt) {
        if (JOptionPane.showConfirmDialog(
                this,
                ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.start_new_game"),
                ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.start_new"),
                0
        )
                == 0) {
            this.sudokuPanel.setSudoku(this.sudokuPanel.getSudokuString(ClipboardMode.CLUES_ONLY));
            this.sudokuPanel.checkProgress();
            this.allStepsPanel.setSudoku(this.sudokuPanel.getSudoku());
            this.initializeResultPanels();
            this.repaint();
            this.setSpielen(true);
            this.check();
            this.fixFocus();
        }
    }

    private void beendenMenuItemActionPerformed(ActionEvent evt) {
        this.formWindowClosed(null);
        System.exit(0);
    }

    private void copyLibraryMenuItemActionPerformed(ActionEvent evt) {
        this.copyToClipboard(ClipboardMode.LIBRARY, false);
    }

    private void copyPmGridWithStepMenuItemActionPerformed(ActionEvent evt) {
        SolutionStep activeStep = this.sudokuPanel.getStep();
        if (activeStep == null) {
            JOptionPane.showMessageDialog(
                    this,
                    ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.no_step_selected"),
                    ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.error"),
                    0
            );
        } else {
            this.copyToClipboard(ClipboardMode.PM_GRID_WITH_STEP, false);
        }
    }

    private void copyPmGridMenuItemActionPerformed(ActionEvent evt) {
        this.copyToClipboard(ClipboardMode.PM_GRID, false);
    }

    private void copyFilledMenuItemActionPerformed(ActionEvent evt) {
        this.copyToClipboard(ClipboardMode.VALUES_ONLY, false);
    }

    private void showDeviationsMenuItemActionPerformed(ActionEvent evt) {
        this.sudokuPanel.setShowDeviations(this.showDeviationsMenuItem.isSelected());
        this.check();
        this.fixFocus();
    }

    private void neuesSpielToolButtonActionPerformed(ActionEvent evt) {
        int actLevel = Options.getInstance().getActLevel();
        DifficultyLevel actDiffLevel = Options.getInstance().getDifficultyLevel(actLevel);
        if (Options.getInstance().getGameMode() == GameMode.LEARNING) {
            actDiffLevel = Options.getInstance().getDifficultyLevel(DifficultyType.EXTREME.ordinal());
        }

        String preGenSudoku = BackgroundGeneratorThread.getInstance().getSudoku(actDiffLevel, Options.getInstance().getGameMode());
        Sudoku2 tmpSudoku = null;
        if (preGenSudoku == null) {
            GenerateSudokuProgressDialog dlg = new GenerateSudokuProgressDialog(this, true, actDiffLevel, Options.getInstance().getGameMode());
            dlg.setVisible(true);
            tmpSudoku = dlg.getSudoku();
        } else {
            tmpSudoku = new Sudoku2();
            tmpSudoku.setSudoku(preGenSudoku, true);
            Sudoku2 solvedSudoku = tmpSudoku.clone();
            SudokuSolver solver = SudokuSolverFactory.getDefaultSolverInstance();
            solver.solve(actDiffLevel, solvedSudoku, true, null, false, Options.getInstance().solverSteps, Options.getInstance().getGameMode());
            tmpSudoku.setLevel(solvedSudoku.getLevel());
            tmpSudoku.setScore(solvedSudoku.getScore());
        }

        if (tmpSudoku != null) {
            this.sudokuPanel.setSudoku(tmpSudoku, true);
            this.allStepsPanel.setSudoku(this.sudokuPanel.getSudoku());
            this.initializeResultPanels();
            this.addSudokuToHistory(tmpSudoku);
            this.sudokuPanel.clearColoring();
            this.sudokuPanel.setShowHintCellValue(0);
            this.sudokuPanel.setShowInvalidOrPossibleCells(false);
            if (Options.getInstance().getGameMode() == GameMode.LEARNING) {
                Sudoku2 trainingSudoku = this.sudokuPanel.getSudoku();

                for (SolutionStep step : this.sudokuPanel.getSolver().getSteps()) {
                    if (step.getType().getStepConfig().isEnabledTraining()) {
                        break;
                    }

                    this.sudokuPanel.getSolver().doStep(trainingSudoku, step);
                }
            }

            this.clearSavePoints();
            this.sudokuFileName = null;
            this.setTitleWithFile();
            this.check();
        }

        this.setSpielen(true);
        this.fixFocus();
    }

    private void levelComboBoxActionPerformed(ActionEvent evt) {
        Options.getInstance().setActLevel(Options.getInstance().getDifficultyLevels()[this.levelComboBox.getSelectedIndex() + 1].getOrdinal());
        BackgroundGeneratorThread.getInstance().setNewLevel(Options.getInstance().getActLevel());
        this.check();
        this.fixFocus();
    }

    private void levelExtremMenuItemActionPerformed(ActionEvent evt) {
        this.setLevelFromMenu();
    }

    private void levelSchwerMenuItemActionPerformed(ActionEvent evt) {
        this.setLevelFromMenu();
    }

    private void levelKniffligMenuItemActionPerformed(ActionEvent evt) {
        this.setLevelFromMenu();
    }

    private void levelMittelMenuItemActionPerformed(ActionEvent evt) {
        this.setLevelFromMenu();
    }

    private void levelLeichtMenuItemActionPerformed(ActionEvent evt) {
        this.setLevelFromMenu();
    }

    private void f1ToggleButtonActionPerformed1(ActionEvent evt) {
        this.f1ToggleButtonActionPerformed(evt);
    }

    private void f1ToggleButtonActionPerformed(ActionEvent evt) {
        this.setToggleButton((JToggleButton) evt.getSource(), (evt.getModifiers() & 2) != 0);
    }

    private void redGreenToggleButtonActionPerformed(ActionEvent evt) {
        this.sudokuPanel.setInvalidCells(!this.sudokuPanel.isInvalidCells());
        this.sudokuPanel.repaint();
        this.check();
        this.fixFocus();
    }

    private void mediumHintMenuItemActionPerformed(ActionEvent evt) {
        this.getHint(1);
    }

    private void vageHintMenuItemActionPerformed(ActionEvent evt) {
        this.getHint(0);
    }

    private void alleHiddenSinglesSetzenMenuItemActionPerformed(ActionEvent evt) {
        this.hinweisAbbrechenButtonActionPerformed(null);
        SolutionStep step = null;

        while (
                (step = this.sudokuPanel.getNextStep(true)) != null
                        && (step.getType() == SolutionType.HIDDEN_SINGLE || step.getType() == SolutionType.FULL_HOUSE || step.getType() == SolutionType.NAKED_SINGLE)
        ) {
            this.sudokuPanel.doStep();
        }

        this.sudokuPanel.abortStep();
        this.sudokuPanel.checkProgress();
        this.fixFocus();
        this.repaint();
    }

    private void hinweisAbbrechenButtonActionPerformed(ActionEvent evt) {
        this.abortStep();
    }

    private void hinweisAusfuehrenButtonActionPerformed(ActionEvent evt) {
        this.sudokuPanel.doStep();
        this.sudokuPanel.checkProgress();
        this.hinweisTextArea.setText("");
        this.hinweisAbbrechenButton.setEnabled(false);
        this.hinweisAusfuehrenButton.setEnabled(false);
        if (this.executeStepToggleButton != null) {
            this.executeStepToggleButton.setEnabled(false);
        }

        if (this.abortStepToggleButton != null) {
            this.abortStepToggleButton.setEnabled(false);
        }

        this.fixFocus();
    }

    private void loesungsSchrittMenuItemActionPerformed(ActionEvent evt) {
        this.getHint(2);
    }

    private void neuerHinweisButtonActionPerformed(ActionEvent evt) {
        this.loesungsSchrittMenuItemActionPerformed(evt);
    }

    private void neuMenuItemActionPerformed(ActionEvent evt) {
        this.neuesSpielToolButtonActionPerformed(null);
    }

    private void copyCluesMenuItemActionPerformed(ActionEvent evt) {
        this.copyToClipboard(ClipboardMode.CLUES_ONLY, false);
    }

    private void showWrongValuesMenuItemActionPerformed(ActionEvent evt) {
        this.sudokuPanel.setShowWrongValues(this.showWrongValuesMenuItem.isSelected());
        this.check();
        this.fixFocus();
    }

    private void showCandidatesMenuItemActionPerformed(ActionEvent evt) {
        if (!this.showCandidatesMenuItem.isSelected()) {
            this.sudokuPanel.setShowCandidates(this.showCandidatesMenuItem.isSelected());
        } else if (this.sudokuPanel.getSudoku().userCandidatesEmpty()) {
            this.sudokuPanel.setShowCandidates(this.showCandidatesMenuItem.isSelected());
        } else {
            boolean doYes = true;
            if (!this.sudokuPanel.getSudoku().checkUserCands()) {
                int ret = JOptionPane.showConfirmDialog(
                        null,
                        ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.candidatesMissing"),
                        ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.error"),
                        1
                );
                if (ret == 2) {
                    this.showCandidatesMenuItem.setSelected(false);
                    this.fixFocus();
                    return;
                }

                if (ret == 0) {
                    doYes = true;
                } else {
                    doYes = false;
                }
            }

            if (doYes) {
                this.sudokuPanel.getSudoku().switchToAllCandidates();
                this.sudokuPanel.getSolver().setSudoku(this.sudokuPanel.getSudoku());
                this.sudokuPanel.checkProgress();
                this.sudokuPanel.setShowCandidates(this.showCandidatesMenuItem.isSelected());
            } else {
                this.sudokuPanel.getSudoku().rebuildAllCandidates();
                this.sudokuPanel.getSolver().setSudoku(this.sudokuPanel.getSudoku());
                this.sudokuPanel.checkProgress();
                this.sudokuPanel.setShowCandidates(this.showCandidatesMenuItem.isSelected());
            }
        }

        this.check();
        this.fixFocus();
    }

    private void redoToolButtonActionPerformed(ActionEvent evt) {
        this.sudokuPanel.redo();
        this.allStepsPanel.setSudoku(this.sudokuPanel.getSudoku());
        this.allStepsPanel.resetPanel();
        this.check();
        this.fixFocus();
    }

    private void redoMenuItemActionPerformed(ActionEvent evt) {
        this.sudokuPanel.redo();
        this.allStepsPanel.setSudoku(this.sudokuPanel.getSudoku());
        this.allStepsPanel.resetPanel();
        this.check();
        this.fixFocus();
    }

    private void undoToolButtonActionPerformed(ActionEvent evt) {
        this.sudokuPanel.undo();
        this.allStepsPanel.setSudoku(this.sudokuPanel.getSudoku());
        this.allStepsPanel.resetPanel();
        this.check();
        this.fixFocus();
    }

    private void undoMenuItemActionPerformed(ActionEvent evt) {
        this.sudokuPanel.undo();
        this.allStepsPanel.setSudoku(this.sudokuPanel.getSudoku());
        this.allStepsPanel.resetPanel();
        this.check();
        this.fixFocus();
    }

    private void pasteMenuItemActionPerformed(ActionEvent evt) {
        try {
            Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable clipboardContent = clip.getContents(this);
            if (clipboardContent != null && clipboardContent.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                String content = (String) clipboardContent.getTransferData(DataFlavor.stringFlavor);
                this.setPuzzle(content);
                this.clearSavePoints();
            }
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error pasting from clipboard", ex);
        }

        this.check();
        this.fixFocus();
    }

    private void outerSplitPanePropertyChange(PropertyChangeEvent evt) {
        if (!this.outerSplitPaneInitialized && this.outerSplitPane.getSize().getHeight() != 0.0 && this.hintPanel.getSize().getHeight() != 0.0) {
            this.outerSplitPaneInitialized = true;
            int diff = (int) (this.hintPanel.getMinimumSize().getHeight() - this.hintPanel.getSize().getHeight());
            if (diff > 0) {
                this.resetHDivLocLoc = this.outerSplitPane.getDividerLocation() - diff - 5;
                this.outerSplitPane.setDividerLocation(this.resetHDivLocLoc);
            }

            this.outerSplitPaneInitialized = false;
        }

        if (this.resetHDivLoc && this.outerSplitPane.getDividerLocation() != this.resetHDivLocLoc) {
            this.resetHDivLoc = false;
            if (System.currentTimeMillis() - this.resetHDivLocTicks < 1000L) {
                this.outerSplitPane.setDividerLocation(this.resetHDivLocLoc);
                this.setSize(this.getWidth() + 1, this.getHeight());
            }
        }
    }

    private void formWindowClosed(WindowEvent evt) {
        try {
            if (!this.changingFullScreenMode) {
                this.writeOptionsWithWindowState(null);
            }

            this.changingFullScreenMode = false;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, "Can't write options", ex);
        }
    }

    private void formWindowClosing(WindowEvent evt) {
        this.formWindowClosed(null);
    }

    private void solveUpToButtonActionPerformed(ActionEvent evt) {
        if (this.sudokuPanel != null) {
            this.sudokuPanel.solveUpTo();
        }

        this.check();
        this.fixFocus();
    }

    private void aboutMenuItemActionPerformed(ActionEvent evt) {
        new AboutDialog(this, true).setVisible(true);
        this.check();
        this.fixFocus();
    }

    private void keyMenuItemActionPerformed(ActionEvent evt) {
        new KeyboardLayoutFrame().setVisible(true);
        this.check();
        this.fixFocus();
    }

    private void resetViewMenuItemActionPerformed(ActionEvent evt) {
        this.setWindowLayout(true);
        this.check();
        this.fixFocus();
        this.repaint();
    }

    private void hintPanelPropertyChange(PropertyChangeEvent evt) {
        this.outerSplitPanePropertyChange(null);
    }

    private void spielEingebenMenuItemActionPerformed(ActionEvent evt) {
        if (this.sudokuPanel.getSolvedCellsAnz() != 0) {
            int antwort = JOptionPane.showConfirmDialog(
                    this,
                    ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.delete_sudoku"),
                    ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.new_input"),
                    0
            );
            if (antwort != 0) {
                return;
            }
        }

        this.sudokuPanel.setSudoku((String) null);
        this.sudokuPanel.checkProgress();
        this.allStepsPanel.setSudoku(this.sudokuPanel.getSudoku());
        this.resetResultPanels();
        this.sudokuPanel.setNoClues();
        this.hinweisAbbrechenButtonActionPerformed(null);
        this.setSpielen(false);
    }

    private void spielEditierenMenuItemActionPerformed(ActionEvent evt) {
        this.resetResultPanels();
        this.sudokuPanel.setNoClues();
        this.sudokuPanel.checkProgress();
        this.sudokuPanel.resetShowHintCellValues();
        this.hinweisAbbrechenButtonActionPerformed(null);
        this.setSpielen(false);
    }

    private void spielSpielenMenuItemActionPerformed(ActionEvent evt) {
        if (this.sudokuPanel.getSolvedCellsAnz() > 0) {
            this.sudokuPanel.setSudoku(this.sudokuPanel.getSudokuString(ClipboardMode.VALUES_ONLY));
            this.sudokuPanel.checkProgress();
            this.allStepsPanel.setSudoku(this.sudokuPanel.getSudoku());
            this.initializeResultPanels();
        }

        this.setSpielen(true);
    }

    private void resetSpielMenuItemActionPerformed(ActionEvent evt) {
        if (JOptionPane.showConfirmDialog(
                this,
                ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.reset_game"),
                ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.reset"),
                0
        )
                == 0) {
            this.sudokuPanel.setSudoku(this.sudokuPanel.getSudokuString(ClipboardMode.CLUES_ONLY));
            this.sudokuPanel.checkProgress();
            this.allStepsPanel.setSudoku(this.sudokuPanel.getSudoku());
            this.allStepsPanel.resetPanel();
            this.repaint();
            this.setSpielen(true);
            this.check();
            this.fixFocus();
        }
    }

    private void statusPanelColor1MouseClicked(MouseEvent evt) {
        this.coloringPanelClicked(0);
    }

    private void statusPanelColor2MouseClicked(MouseEvent evt) {
        this.coloringPanelClicked(2);
    }

    private void statusPanelColor3MouseClicked(MouseEvent evt) {
        this.coloringPanelClicked(4);
    }

    private void statusPanelColor4MouseClicked(MouseEvent evt) {
        this.coloringPanelClicked(6);
    }

    private void statusPanelColor5MouseClicked(MouseEvent evt) {
        this.coloringPanelClicked(8);
    }

    private void statusPanelColorClearMouseClicked(MouseEvent evt) {
        this.coloringPanelClicked(-1);
    }

    private void statusPanelColorResetMouseClicked(MouseEvent evt) {
        this.coloringPanelClicked(-2);
    }

    private void colorCellsMenuItemActionPerformed(ActionEvent evt) {
        this.sudokuPanel.setColorCells(true);
        this.check();
        this.fixFocus();
    }

    private void colorCandidatesMenuItemActionPerformed(ActionEvent evt) {
        this.sudokuPanel.setColorCells(false);
        this.check();
        this.fixFocus();
    }

    private void cellZoomMenuItemActionPerformed(ActionEvent evt) {
        this.setSplitPane(this.cellZoomPanel);
        this.repaint();
    }

    private void userManualMenuItemActionPerformed(ActionEvent evt) {
        MyBrowserLauncher.getInstance().launchUserManual();
    }

    private void solvingGuideMenuItemActionPerformed(ActionEvent evt) {
        MyBrowserLauncher.getInstance().launchSolvingGuide();
    }

    private void projectHomePageMenuItemActionPerformed(ActionEvent evt) {
        MyBrowserLauncher.getInstance().launchHomePage();
    }

    private void loadConfigMenuItemActionPerformed(ActionEvent evt) {
        this.loadFromFile(false);
    }

    private void saveConfigAsMenuItemActionPerformed(ActionEvent evt) {
        this.saveToFile(false);
    }

    private void historyMenuItemActionPerformed(ActionEvent evt) {
        GuiState state = new GuiState(this.sudokuPanel, this.sudokuPanel.getSolver(), this.solutionPanel);
        state.get(true);
        HistoryDialog dlg = new HistoryDialog(this, true);
        dlg.setVisible(true);
        String puzzle = dlg.getSelectedPuzzle();
        if (puzzle != null) {
            if (!dlg.isDoubleClicked()) {
                this.setPuzzle(puzzle);
            }

            this.clearSavePoints();
        } else {
            this.setState(state);
        }

        GuiState var5 = null;
    }

    private void createSavePointMenuItemActionPerformed(ActionEvent evt) {
        String defaultName = ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.createsp.default") + " " + (this.savePoints.size() + 1);
        String name = (String) JOptionPane.showInputDialog(
                this,
                ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.createsp.message"),
                ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.createsp.title"),
                3,
                null,
                null,
                defaultName
        );
        if (name != null) {
            GuiState state = new GuiState(this.sudokuPanel, this.sudokuPanel.getSolver(), this.solutionPanel);
            state.get(true);
            state.setName(name);
            state.setTimestamp(new Date());
            this.savePoints.add(state);
        }
    }

    private void restoreSavePointMenuItemActionPerformed(ActionEvent evt) {
        GuiState state = new GuiState(this.sudokuPanel, this.sudokuPanel.getSolver(), this.solutionPanel);
        state.get(true);
        RestoreSavePointDialog dlg = new RestoreSavePointDialog(this, true);
        dlg.setVisible(true);
        if (!dlg.isOkPressed()) {
            this.setState(state);
        }

        GuiState var4 = null;
    }

    private void playingMenuItemActionPerformed(ActionEvent evt) {
        this.setMode(GameMode.PLAYING, true);
        this.check();
    }

    private void learningMenuItemActionPerformed(ActionEvent evt) {
        this.setMode(GameMode.LEARNING, true);
        this.check();
    }

    private void practisingMenuItemActionPerformed(ActionEvent evt) {
        this.setMode(GameMode.PRACTISING, true);
        this.check();
    }

    private void backdoorSearchMenuItemActionPerformed(ActionEvent evt) {
        new BackdoorSearchDialog(this, true, this.sudokuPanel).setVisible(true);
    }

    private void setGivensMenuItemActionPerformed(ActionEvent evt) {
        SetGivensDialog dlg = new SetGivensDialog(this, true);
        dlg.setVisible(true);
        if (dlg.isOkPressed()) {
            String givens = dlg.getGivens();
            this.sudokuPanel.setGivens(givens);
        }
    }

    private void fullScreenMenuItemActionPerformed(ActionEvent evt) {
        if (this.fullScreenMenuItem.isSelected()) {
            this.changingFullScreenMode = true;
            this.saveWindowStateInOptions();
            this.dispose();
            this.setUndecorated(true);
            this.setExtendedState(6);
            this.hintPanel.setVisible(false);
            this.jToolBar1.setVisible(true);
            this.showToolBarMenuItem.setEnabled(false);
            this.showHintPanelMenuItem.setEnabled(false);
            this.setVisible(true);
        } else {
            this.changingFullScreenMode = true;
            this.dispose();
            this.setUndecorated(false);
            this.setExtendedState(0);
            this.showToolBarMenuItem.setEnabled(true);
            this.showHintPanelMenuItem.setEnabled(true);
            this.setWindowLayout(false);
            this.setVisible(true);
        }

        this.check();
        this.fixFocus();
    }

    private void showHintPanelMenuItemActionPerformed(ActionEvent evt) {
        Options.getInstance().setShowHintPanel(this.showHintPanelMenuItem.isSelected());
        this.hintPanel.setVisible(this.showHintPanelMenuItem.isSelected());
        if (Options.getInstance().isShowHintPanel()) {
            int horzDivLoc = Options.getInstance().getInitialHorzDividerLoc();
            if (horzDivLoc > this.getHeight() - 204) {
                horzDivLoc = this.getHeight() - 204;
                Options.getInstance().setInitialHorzDividerLoc(horzDivLoc);
            }

            this.outerSplitPane.setDividerLocation(horzDivLoc);
        }
    }

    private void showToolBarMenuItemActionPerformed(ActionEvent evt) {
        Options.getInstance().setShowToolBar(this.showToolBarMenuItem.isSelected());
        this.jToolBar1.setVisible(this.showToolBarMenuItem.isSelected());
    }

    private void fxyToggleButtonf1ToggleButtonActionPerformed(ActionEvent evt) {
        this.setToggleButton((JToggleButton) evt.getSource(), false);
    }

    private void showHintButtonsCheckBoxMenuItemActionPerformed(ActionEvent evt) {
        Options.getInstance().setShowHintButtonsInToolbar(this.showHintButtonsCheckBoxMenuItem.isSelected());
        this.setShowHintButtonsInToolbar();
    }

    private void extendedPrintMenuItemActionPerformed(ActionEvent evt) {
        new ExtendedPrintDialog(this, true).setVisible(true);
    }

    private void copySSMenuItemActionPerformed(ActionEvent evt) {
        this.copyToClipboard(null, true);
    }

    private void savePuzzleMenuItemActionPerformed(ActionEvent evt) {
        if (this.sudokuFileName != null) {
            try {
                this.saveToFile(true, this.sudokuFileName, this.sudokuFileType);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.toString(), ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.error"), 0);
                this.sudokuFileName = null;
            }
        }
    }

    private void showColorKuMenuItemActionPerformed(ActionEvent evt) {
        Options.getInstance().setShowColorKuAct(this.showColorKuMenuItem.isSelected());
        this.sudokuPanel.setShowColorKu();
        this.check();
        this.fixFocus();
    }

    private void askQuestionMenuItemActionPerformed(ActionEvent evt) {
        MyBrowserLauncher.getInstance().launchForum();
    }

    private void reportErrorMenuItemActionPerformed(ActionEvent evt) {
        MyBrowserLauncher.getInstance().launchTracker();
    }

    private void prepareToggleButtonIcons(boolean on) {
        if (on) {
            int i = 0;

            for (int lim = this.toggleButtons.length - 1; i < lim; i++) {
                if (this.toggleButtonImagesColorKu[i] == null || !this.toggleButtonImagesColorKu[i].getColor().equals(Options.getInstance().getColorKuColor(i + 1))
                ) {
                    this.toggleButtonImagesColorKu[i] = new ColorKuImage(32, Options.getInstance().getColorKuColor(i + 1));
                    this.toggleButtonIconsColorKu[i] = new ImageIcon(this.toggleButtonImagesColorKu[i]);
                }

                this.toggleButtonIcons[i] = this.toggleButtonIconsColorKu[i];
                this.emptyToggleButtonIcons[i] = this.emptyToggleButtonIconOrgColorKu;
            }
        } else {
            int i = 0;

            for (int lim = this.toggleButtons.length - 1; i < lim; i++) {
                this.toggleButtonIcons[i] = this.toggleButtonIconsOrg[i];
                this.emptyToggleButtonIcons[i] = this.emptyToggleButtonIconsOrg[i];
            }
        }
    }

    private void getHint(int mode) {
        if (this.sudokuPanel.getSudoku().isSolved()) {
            JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.already_solved"));
        } else if (this.sudokuPanel.getSudoku().getStatus() == SudokuStatus.EMPTY || this.sudokuPanel.getSudoku().getStatus() == SudokuStatus.INVALID) {
            JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.invalid_puzzle"));
        } else if (!this.sudokuPanel.isShowCandidates()) {
            JOptionPane.showMessageDialog(
                    this,
                    ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.not_available"),
                    ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.hint"),
                    1
            );
        } else if (!this.sudokuPanel.getSudoku().checkSudoku()) {
            JOptionPane.showMessageDialog(
                    this,
                    ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.invalid_values_or_candidates"),
                    ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.hint"),
                    1
            );
        } else {
            SolutionStep step = this.sudokuPanel.getNextStep(false);
            if (mode == 0 || mode == 1) {
                this.sudokuPanel.abortStep();
                this.fixFocus();
                if (step != null) {
                    int strMode = 0;
                    String msg = ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.vage_hint");
                    if (mode == 1) {
                        strMode = 1;
                        msg = ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.medium_hint");
                    }

                    JOptionPane.showMessageDialog(
                            this, ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.possible_step") + step.toString(strMode), msg, 1
                    );
                } else {
                    JOptionPane.showMessageDialog(
                            this,
                            ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.dont_know"),
                            ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.error"),
                            0
                    );
                }
            } else if (step != null) {
                this.setSolutionStep(step, false);
            } else {
                this.hinweisTextArea.setText(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.dont_know"));
                this.hinweisTextArea.setCaretPosition(0);
            }

            this.fixFocus();
            this.check();
        }
    }

    private void setMode(GameMode newMode, boolean showDialog) {
        if (newMode == GameMode.PLAYING) {
            Options.getInstance().setGameMode(newMode);
        } else {
            if (showDialog) {
                ConfigTrainingDialog dlg = new ConfigTrainingDialog(this, true);
                dlg.setVisible(true);
                if (!dlg.isOkPressed()) {
                    return;
                }
            }

            String techniques = Options.getInstance().getTrainingStepsString(true);
            if (techniques.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.notechniques"),
                        ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.error"),
                        0
                );
                Options.getInstance().setGameMode(GameMode.PLAYING);
            } else {
                Options.getInstance().setGameMode(newMode);
            }
        }
    }

    public void setPuzzle(String puzzle) {
        try {
            this.sudokuPanel.setSudoku(puzzle);
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error setting sudoku in SudokuPanel", ex);
        }

        this.allStepsPanel.setSudoku(this.sudokuPanel.getSudoku());
        this.initializeResultPanels();
        this.sudokuPanel.clearColoring();
        this.sudokuPanel.setShowHintCellValue(0);
        this.sudokuPanel.setShowInvalidOrPossibleCells(false);
        this.setSpielen(true);
        this.check();
        this.repaint();
    }

    public void setState(GuiState state) {
        state.set();
        this.summaryPanel.initialize(SudokuSolverFactory.getDefaultSolverInstance());
        this.allStepsPanel.setSudoku(this.sudokuPanel.getSudoku());
        this.allStepsPanel.resetPanel();
        this.setSolutionStep(state.getStep(), true);
        this.setSpielen(true);
        this.check();
        this.repaint();
    }

    private void addSudokuToHistory(Sudoku2 sudoku) {
        Options.getInstance().addSudokuToHistory(sudoku);
    }

    private void clearSavePoints() {
        for (int i = 0; i < this.savePoints.size(); i++) {
            this.savePoints.set(i, null);
        }

        this.savePoints.clear();
    }

    public void setColoring(int colorNumber, boolean isCell) {
        this.sudokuPanel.setColorCells(isCell);
        this.coloringPanelClicked(colorNumber);
        this.check();
        this.fixFocus();
    }

    public void coloringPanelClicked(int colorNumber) {
        if (colorNumber != -1 && colorNumber != -2) {
            this.statusPanelColorResult.setBackground(Options.getInstance().getColoringColors()[colorNumber]);
            this.sudokuPanel.setActiveColor(colorNumber);
        } else {
            this.statusPanelColorResult.setBackground(Options.getInstance().getDefaultCellColor());
            this.sudokuPanel.setActiveColor(-1);
            if (colorNumber == -2) {
                this.sudokuPanel.clearColoring();
                this.repaint();
            }
        }
    }

    private void tabPaneMouseClicked(MouseEvent evt) {
        if (evt.getButton() == 1) {
            switch (this.tabPane.getSelectedIndex()) {
                case 0:
                    this.summaryMenuItem.setSelected(true);
                    break;
                case 1:
                    this.solutionMenuItem.setSelected(true);
                    break;
                case 2:
                    this.allStepsMenuItem.setSelected(true);
                    break;
                case 3:
                    this.cellZoomMenuItem.setSelected(true);
            }
        }
    }

    private void saveWindowStateInOptions() {
        Options o = Options.getInstance();
        o.setInitialXPos(this.getX());
        o.setInitialYPos(this.getY());
        o.setInitialHeight(this.getHeight());
        o.setInitialWidth(this.getWidth());
        if (o.isShowHintPanel()) {
            o.setInitialHorzDividerLoc(this.outerSplitPane.getDividerLocation());
        }

        o.setInitialDisplayMode(0);
        if (this.summaryMenuItem.isSelected()) {
            o.setInitialDisplayMode(1);
        }

        if (this.solutionMenuItem.isSelected()) {
            o.setInitialDisplayMode(2);
        }

        if (this.allStepsMenuItem.isSelected()) {
            o.setInitialDisplayMode(3);
        }

        if (this.cellZoomMenuItem.isSelected()) {
            o.setInitialDisplayMode(4);
        }

        o.setInitialVertDividerLoc(-1);
        if (o.getInitialDisplayMode() != 0) {
            this.splitPanel.getDividerLocation();
        }
    }

    private void writeOptionsWithWindowState(String fileName) throws FileNotFoundException {
        this.saveWindowStateInOptions();
        if (fileName == null) {
            Options.getInstance().writeOptions();
        } else {
            Options.getInstance().writeOptions(fileName);
        }
    }

    private void setWindowLayout(boolean reset) {
        Options o = Options.getInstance();
        if (reset) {
            o.setInitialDisplayMode(0);
            o.setInitialHeight(844);
            o.setInitialHorzDividerLoc(627);
            o.setInitialVertDividerLoc(-1);
            o.setInitialWidth(643);
            o.setShowHintPanel(true);
            o.setShowToolBar(true);
        }

        this.jToolBar1.setVisible(o.isShowToolBar());
        this.hintPanel.setVisible(o.isShowHintPanel());
        Toolkit t = Toolkit.getDefaultToolkit();
        Dimension screenSize = t.getScreenSize();
        int width = o.getInitialWidth();
        int height = o.getInitialHeight();
        int horzDivLoc = o.getInitialHorzDividerLoc();
        if (screenSize.height - 45 < height) {
            height = screenSize.height - 45;
        }

        if (horzDivLoc > height - 204) {
            horzDivLoc = height - 204;
            Options.getInstance().setInitialHorzDividerLoc(horzDivLoc);
        }

        if (screenSize.width - 20 < width) {
            width = screenSize.width - 20;
        }

        this.setSize(width, height);
        switch (o.getInitialDisplayMode()) {
            case 0:
                this.splitPanel.setRight(null);
                this.sudokuOnlyMenuItem.setSelected(true);
                break;
            case 1:
                this.setSplitPane(this.summaryPanel);
                this.summaryMenuItem.setSelected(true);
                break;
            case 2:
                this.setSplitPane(this.solutionPanel);
                this.solutionMenuItem.setSelected(true);
                break;
            case 3:
                this.allStepsPanel.setSudoku(this.sudokuPanel.getSudoku());
                this.setSplitPane(this.allStepsPanel);
                this.allStepsMenuItem.setSelected(true);
                break;
            case 4:
                this.setSplitPane(this.cellZoomPanel);
                this.cellZoomMenuItem.setSelected(true);
        }

        if (o.getInitialVertDividerLoc() != -1) {
            this.splitPanel.setDividerLocation(o.getInitialVertDividerLoc());
        }

        this.outerSplitPane.setDividerLocation(horzDivLoc);
        this.outerSplitPaneInitialized = false;
        this.resetHDivLocLoc = horzDivLoc;
        this.resetHDivLocTicks = System.currentTimeMillis();
        this.resetHDivLoc = true;
    }

    private void resetResultPanels() {
        this.summaryPanel.initialize(null);
        this.solutionPanel.initialize(null);
        this.allStepsPanel.resetPanel();
    }

    private void initializeResultPanels() {
        this.summaryPanel.initialize(this.sudokuPanel.getSolver());
        this.solutionPanel.initialize(this.sudokuPanel.getSolver().getSteps());
        this.allStepsPanel.resetPanel();
    }

    private void setSplitPane(JPanel panel) {
        if (!this.splitPanel.hasRight()) {
            this.splitPanel.setRight(this.tabPane);
        }

        this.tabPane.setSelectedComponent(panel);
    }

    private void setSpielen(boolean isSpielen) {
        this.eingabeModus = !isSpielen;
        if (isSpielen) {
            if (this.oldShowDeviationsValid) {
                this.showDeviationsMenuItem.setSelected(this.oldShowDeviations);
                this.oldShowDeviationsValid = false;
            }
        } else {
            this.oldShowDeviations = this.showDeviationsMenuItem.isSelected();
            this.oldShowDeviationsValid = true;
            this.showDeviationsMenuItem.setSelected(false);
        }

        this.showDeviationsMenuItemActionPerformed(null);
        this.vageHintMenuItem.setEnabled(isSpielen);
        this.mediumHintMenuItem.setEnabled(isSpielen);
        this.loesungsSchrittMenuItem.setEnabled(isSpielen);
        this.alleHiddenSinglesSetzenMenuItem.setEnabled(isSpielen);
        this.showDeviationsMenuItem.setEnabled(isSpielen);
        this.showColorKuMenuItem.setEnabled(isSpielen);
        this.spielSpielenMenuItem.setEnabled(!isSpielen);
        this.spielEditierenMenuItem.setEnabled(isSpielen);
    }

    public void setSolutionStep(SolutionStep step, boolean setInSudokuPanel) {
        if (setInSudokuPanel) {
            if (step == null) {
                this.sudokuPanel.abortStep();
            } else {
                this.sudokuPanel.setStep(step);
            }
        }

        if (step == null) {
            this.hinweisTextArea.setText("");
            this.hinweisAbbrechenButton.setEnabled(false);
            this.hinweisAusfuehrenButton.setEnabled(false);
            if (this.executeStepToggleButton != null) {
                this.executeStepToggleButton.setEnabled(false);
            }

            if (this.abortStepToggleButton != null) {
                this.abortStepToggleButton.setEnabled(false);
            }
        } else {
            this.hinweisTextArea.setText(step.toString());
            this.hinweisTextArea.setCaretPosition(0);
            this.hinweisAbbrechenButton.setEnabled(true);
            this.hinweisAusfuehrenButton.setEnabled(true);
            this.getRootPane().setDefaultButton(this.hinweisAusfuehrenButton);
            if (this.executeStepToggleButton != null) {
                this.executeStepToggleButton.setEnabled(true);
            }

            if (this.abortStepToggleButton != null) {
                this.abortStepToggleButton.setEnabled(true);
            }
        }

        this.fixFocus();
    }

    private void copyToClipboard(ClipboardMode mode, boolean simpleSudoku) {
        String clipStr = "";
        if (simpleSudoku) {
            String dummy = this.sudokuPanel.getSudokuString(ClipboardMode.CLUES_ONLY);
            clipStr = SudokuUtil.getSSFormatted(dummy);
            clipStr = clipStr + SudokuUtil.NEW_LINE;
            clipStr = clipStr + SudokuUtil.NEW_LINE;
            dummy = this.sudokuPanel.getSudokuString(ClipboardMode.VALUES_ONLY);
            clipStr = clipStr + SudokuUtil.getSSFormatted(dummy);
            clipStr = clipStr + SudokuUtil.NEW_LINE;
            clipStr = clipStr + SudokuUtil.NEW_LINE;
            dummy = this.sudokuPanel.getSudokuString(ClipboardMode.PM_GRID);
            clipStr = clipStr + SudokuUtil.getSSPMGrid(dummy);
        } else {
            clipStr = this.sudokuPanel.getSudokuString(mode);
        }

        try {
            Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection content = new StringSelection(clipStr);
            clip.setContents(content, null);
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error writing to clipboard", ex);
        }

        this.fixFocus();
    }

    private void setToggleButton(JToggleButton button, boolean ctrlPressed) {
        if (button == null) {
            this.sudokuPanel.resetShowHintCellValues();
        } else {
            int index = 0;
            index = 0;

            while (index < this.toggleButtons.length && this.toggleButtons[index] != button) {
                index++;
            }

            if (index == 9) {
                this.sudokuPanel.setShowHintCellValue(index + 1);
            } else {
                boolean isActive = this.sudokuPanel.getShowHintCellValues()[index + 1];
                if (ctrlPressed) {
                    this.sudokuPanel.getShowHintCellValues()[index + 1] = !isActive;
                    this.sudokuPanel.getShowHintCellValues()[10] = false;
                } else if (isActive) {
                    this.sudokuPanel.resetShowHintCellValues();
                } else {
                    this.sudokuPanel.setShowHintCellValue(index + 1);
                }
            }

            this.sudokuPanel.checkIsShowInvalidOrPossibleCells();
        }

        this.check();
        this.sudokuPanel.repaint();
        this.fixFocus();
    }

    private void saveToFile(boolean puzzle) {
        JFileChooser chooser = new JFileChooser(Options.getInstance().getDefaultFileDir());
        chooser.setAcceptAllFileFilterUsed(false);
        MainFrame.MyFileFilter[] filters = this.puzzleFileSaveFilters;
        if (!puzzle) {
            filters = this.configFileFilters;
        }

        for (int i = 0; i < filters.length; i++) {
            chooser.addChoosableFileFilter(filters[i]);
        }

        int returnVal = chooser.showSaveDialog(this);
        if (returnVal == 0) {
            try {
                String path = chooser.getSelectedFile().getPath();
                path = path.substring(0, path.lastIndexOf(File.separatorChar));
                Options.getInstance().setDefaultFileDir(path);
                MainFrame.MyFileFilter actFilter = (MainFrame.MyFileFilter) chooser.getFileFilter();
                int filterType = actFilter.getType();
                path = chooser.getSelectedFile().getAbsolutePath();
                if (!puzzle) {
                    if (!path.endsWith("." + this.configFileExt)) {
                        path = path + "." + this.configFileExt;
                    }
                } else if (filterType == 1) {
                    if (!path.endsWith("." + this.solutionFileExt)) {
                        path = path + "." + this.solutionFileExt;
                    }
                } else if (filterType == 9) {
                    if (!path.endsWith("." + this.ssFileExt)) {
                        path = path + "." + this.ssFileExt;
                    }
                } else if (!path.endsWith("." + this.textFileExt)) {
                    path = path + "." + this.textFileExt;
                }

                File checkFile = new File(path);
                if (checkFile.exists()) {
                    MessageFormat msgf = new MessageFormat("");
                    Object[] args = new Object[]{checkFile.getName()};
                    msgf.applyPattern(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.file_exists"));
                    String warning = msgf.format(args);
                    String title = ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.hint");
                    if (JOptionPane.showConfirmDialog(null, warning, title, 0) != 0) {
                        return;
                    }
                }

                this.saveToFile(puzzle, path, filterType);
            } catch (Exception ex2) {
                JOptionPane.showMessageDialog(this, ex2.toString(), ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.error"), 0);
                this.sudokuFileName = null;
            }

            this.setTitleWithFile();
        }
    }

    private void saveToFile(boolean puzzle, String path, int filterType) throws FileNotFoundException, IOException {
        this.sudokuFileName = path;
        if (!puzzle) {
            this.writeOptionsWithWindowState(path);
        } else {
            String newLine = System.getProperty("line.separator");
            if (filterType == 1) {
                this.sudokuFileType = 1;
                ZipOutputStream zOut = new ZipOutputStream(new FileOutputStream(path));
                zOut.putNextEntry(new ZipEntry("SudokuData"));
                XMLEncoder out = new XMLEncoder(zOut);
                out.writeObject(this.sudokuPanel.getSudoku());
                out.writeObject(SudokuSolverFactory.getDefaultSolverInstance().getAnzSteps());
                out.writeObject(SudokuSolverFactory.getDefaultSolverInstance().getSteps());
                out.writeObject(this.solutionPanel.getTitels());
                out.writeObject(this.solutionPanel.getTabSteps());
                out.writeObject(this.savePoints);
                out.close();
                zOut.flush();
                zOut.close();
            } else if (filterType == 9) {
                this.sudokuFileType = 9;
                PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(path)));
                String clues = this.sudokuPanel.getSudokuString(ClipboardMode.CLUES_ONLY);
                out.println(SudokuUtil.getSSFormatted(clues));
                out.println();
                Sudoku2 tmpSudoku = this.sudokuPanel.getSudoku();

                for (int i = 0; i < 81; i++) {
                    if (tmpSudoku.getValue(i) != 0 && !tmpSudoku.isFixed(i)) {
                        out.printf("I%02d%d%n", i, tmpSudoku.getValue(i));
                    }
                }

                for (int i = 0; i < 81; i++) {
                    if (tmpSudoku.getValue(i) == 0) {
                        for (int j = 1; j <= 9; j++) {
                            if (tmpSudoku.isValidValue(i, j) && !tmpSudoku.isCandidate(i, j)) {
                                out.printf("E%02d%03d%n", i, j);
                            }
                        }
                    }
                }

                out.close();
            } else {
                this.sudokuFileType = 8;
                if (filterType == 8) {
                    filterType = 6;
                }

                BufferedWriter out = new BufferedWriter(new FileWriter(path));
                String line = "";
                switch (filterType) {
                    case 2:
                        line = this.sudokuPanel.getSudokuString(ClipboardMode.CLUES_ONLY);
                        break;
                    case 3:
                        line = this.sudokuPanel.getSudokuString(ClipboardMode.CLUES_ONLY_FORMATTED);
                        break;
                    case 4:
                        line = this.sudokuPanel.getSudokuString(ClipboardMode.PM_GRID);
                        break;
                    case 5:
                        line = this.sudokuPanel.getSudokuString(ClipboardMode.PM_GRID_WITH_STEP);
                        break;
                    case 6:
                        line = this.sudokuPanel.getSudokuString(ClipboardMode.CLUES_ONLY_FORMATTED);
                        line = line + newLine;
                        line = line + newLine;
                        line = line + this.sudokuPanel.getSudokuString(ClipboardMode.VALUES_ONLY_FORMATTED);
                        line = line + newLine;
                        line = line + newLine;
                        line = line + this.sudokuPanel.getSudokuString(ClipboardMode.PM_GRID);
                        break;
                    case 7:
                        line = this.sudokuPanel.getSudokuString(ClipboardMode.LIBRARY);
                }

                out.write(line);
                out.close();
            }
        }
    }

    private void loadFromFile(boolean puzzle) {
        JFileChooser chooser = new JFileChooser(Options.getInstance().getDefaultFileDir());
        chooser.setAcceptAllFileFilterUsed(false);
        MainFrame.MyFileFilter[] filters = this.puzzleFileLoadFilters;
        if (!puzzle) {
            filters = this.configFileFilters;
        }

        for (int i = 0; i < filters.length; i++) {
            chooser.addChoosableFileFilter(filters[i]);
        }

        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == 0) {
            String path = chooser.getSelectedFile().getPath();
            path = path.substring(0, path.lastIndexOf(File.separatorChar));
            Options.getInstance().setDefaultFileDir(path);
            path = chooser.getSelectedFile().getAbsolutePath();
            MainFrame.MyFileFilter filter = (MainFrame.MyFileFilter) chooser.getFileFilter();
            this.loadFromFile(path, filter.getType());
        }
    }

    private void loadFromFile(String path, int fileType) {
        try {
            this.sudokuFileName = path;
            this.sudokuFileType = fileType;
            if (fileType == 0) {
                Options.readOptions(path);
                BackgroundGeneratorThread.getInstance().resetAll();
                this.sudokuFileName = null;
            } else if (fileType == 1) {
                ZipInputStream zIn = new ZipInputStream(new FileInputStream(path));
                zIn.getNextEntry();
                XMLDecoder in = new XMLDecoder(zIn);
                GuiState state = new GuiState(this.sudokuPanel, this.sudokuPanel.getSolver(), this.solutionPanel);
                Object sudokuTemp = in.readObject();
                if (sudokuTemp instanceof Sudoku2) {
                    state.setSudoku((Sudoku2) sudokuTemp);
                } else {
                    Sudoku dummy = (Sudoku) sudokuTemp;
                    String sudokuTempLib = dummy.getSudoku(ClipboardMode.LIBRARY, null);
                    state.setSudoku(new Sudoku2());
                    state.getSudoku().setSudoku(sudokuTempLib, false);
                    state.getSudoku().setInitialState(dummy.getInitialState());
                    sudokuTemp = in.readObject();
                }

                state.setAnzSteps((int[]) in.readObject());
                state.setSteps((List<SolutionStep>) in.readObject());
                state.setTitels((List<String>) in.readObject());
                state.setTabSteps((List<List<SolutionStep>>) in.readObject());
                state.resetAnzSteps();

                try {
                    this.savePoints = (List<GuiState>) in.readObject();

                    for (int i = 0; i < this.savePoints.size(); i++) {
                        this.savePoints.get(i).initialize(this.sudokuPanel, SudokuSolverFactory.getDefaultSolverInstance(), this.solutionPanel);
                    }
                } catch (Exception ex) {
                    this.clearSavePoints();
                }

                in.close();
                this.setState(state);
                this.setMode(GameMode.PLAYING, true);
            } else if (fileType == 8) {
                BufferedReader in = new BufferedReader(new FileReader(path));
                StringBuilder tmp = new StringBuilder();
                String line = null;

                while ((line = in.readLine()) != null) {
                    tmp.append(line);
                    tmp.append("\r\n");
                }

                in.close();
                this.setPuzzle(tmp.toString());
                this.clearSavePoints();
            } else if (fileType == 9) {
                BufferedReader in = new BufferedReader(new FileReader(path));
                StringBuilder tmp = new StringBuilder();
                String line = null;

                while ((line = in.readLine()) != null && !line.trim().isEmpty()) {
                    tmp.append(line);
                    tmp.append("\r\n");
                }

                Sudoku2 tmpSudoku = new Sudoku2();
                tmpSudoku.setSudoku(tmp.toString());

                while ((line = in.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        char recordType = line.charAt(0);
                        if (recordType == 'I') {
                            int index = Integer.parseInt(line.substring(1, 3));
                            int candidate = Character.digit(line.charAt(3), 10);
                            tmpSudoku.setCell(index, candidate);
                        } else if (recordType == 'E') {
                            int index = Integer.parseInt(line.substring(1, 3));
                            int candidate = Integer.parseInt(line.substring(3, 6));
                            tmpSudoku.delCandidate(index, candidate);
                        }
                    }
                }

                in.close();
                this.setPuzzle(tmpSudoku.getSudoku(ClipboardMode.LIBRARY));
                this.clearSavePoints();
            } else {
                this.formatter.applyPattern(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.invalid_filename"));
                String msg = this.formatter.format(new Object[]{path});
                JOptionPane.showMessageDialog(this, msg, ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.error"), 0);
                this.sudokuFileName = null;
            }
        } catch (Exception ex2) {
            JOptionPane.showMessageDialog(this, ex2.toString(), ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.error"), 0);
            ex2.printStackTrace();
            this.sudokuFileName = null;
        }

        this.setTitleWithFile();
    }

    private void setTitleWithFile() {
        this.savePuzzleMenuItem.setEnabled(this.sudokuFileName != null);
        if (this.sudokuFileName == null) {
            this.setTitle("HoDoKu - v2.2.0");
        } else {
            int index = this.sudokuFileName.lastIndexOf(92) + 1;
            int index2 = this.sudokuFileName.lastIndexOf(47) + 1;
            if (index2 > index) {
                index = index2;
            }

            String fileName = this.sudokuFileName.substring(index);
            this.setTitle("HoDoKu - v2.2.0  (" + fileName + ")");
        }
    }

    private void setLevelFromMenu() {
        int selected = 0;

        for (int i = 0; i < this.levelMenuItems.length; i++) {
            if (this.levelMenuItems[i].isSelected()) {
                selected = i + 1;
                break;
            }
        }

        Options.getInstance().setActLevel(Options.getInstance().getDifficultyLevels()[selected].getOrdinal());
        BackgroundGeneratorThread.getInstance().setNewLevel(Options.getInstance().getActLevel());
        this.check();
        this.fixFocus();
    }

    public void stepAusfuehren() {
        this.hinweisAusfuehrenButtonActionPerformed(null);
    }

    public final void fixFocus() {
        this.sudokuPanel.requestFocusInWindow();
    }

    public SudokuPanel getSudokuPanel() {
        return this.sudokuPanel;
    }

    public SolutionPanel getSolutionPanel() {
        return this.solutionPanel;
    }

    public final void check() {
        if (this.sudokuPanel != null) {
            this.undoMenuItem.setEnabled(this.sudokuPanel.undoPossible());
            this.undoToolButton.setEnabled(this.sudokuPanel.undoPossible());
            this.redoMenuItem.setEnabled(this.sudokuPanel.redoPossible());
            this.redoToolButton.setEnabled(this.sudokuPanel.redoPossible());
            this.showCandidatesMenuItem.setSelected(this.sudokuPanel.isShowCandidates());
            this.showWrongValuesMenuItem.setSelected(this.sudokuPanel.isShowWrongValues());
            this.showDeviationsMenuItem.setSelected(this.sudokuPanel.isShowDeviations());
            this.showColorKuMenuItem.setSelected(Options.getInstance().isShowColorKuAct());
            this.prepareToggleButtonIcons(Options.getInstance().isShowColorKuAct());
            if (this.toggleButtons[0] != null) {
                boolean[] remainingCandidates = this.sudokuPanel.getRemainingCandidates();

                for (int i = 0; i < remainingCandidates.length; i++) {
                    JToggleButton button = this.toggleButtons[i];
                    if (remainingCandidates[i]) {
                        if (button.getIcon() != this.toggleButtonIcons[i]) {
                            button.setIcon(this.toggleButtonIcons[i]);
                        }
                    } else if (button.getIcon() != this.emptyToggleButtonIcons[i]) {
                        button.setIcon(this.emptyToggleButtonIcons[i]);
                    }
                }

                for (int i = 0; i < this.toggleButtons.length; i++) {
                    if (this.toggleButtons[i].isEnabled()) {
                        if (this.sudokuPanel.getShowHintCellValues()[i + 1]) {
                            this.toggleButtons[i].setSelected(true);
                        } else {
                            this.toggleButtons[i].setSelected(false);
                        }
                    }
                }
            }

            this.redGreenToggleButton.setSelected(this.sudokuPanel.isInvalidCells());
            Sudoku2 sudoku = this.sudokuPanel.getSudoku();
            if (sudoku != null) {
                DifficultyLevel tmpLevel = sudoku.getLevel();
                if (tmpLevel != null) {
                    this.statusLabelLevel.setText(StepConfig.getLevelName(tmpLevel) + " (" + sudoku.getScore() + ")");
                } else {
                    this.statusLabelLevel.setText("-");
                }

                this.setProgressLabel();
            } else {
                this.statusLabelLevel.setText(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.statusLabelLevel.text"));
            }

            if (this.sudokuPanel.isColorCells()) {
                this.colorCellsMenuItem.setSelected(true);
                this.statusLabelCellCandidate.setText(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.statusLabelCellCandidate.text.cell"));
            } else {
                this.colorCandidatesMenuItem.setSelected(true);
                this.statusLabelCellCandidate.setText(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.statusLabelCellCandidate.text.candidate"));
            }

            this.fixFocus();
        }

        if (Options.getInstance().getActLevel() != -1) {
            if (Options.getInstance().getGameMode() != GameMode.PLAYING) {
                int tmpLevel = Options.getInstance().getActLevel();

                for (StepConfig act : Options.getInstance().getOrgSolverSteps()) {
                    if (act.isEnabledTraining() && act.getLevel() > tmpLevel) {
                        tmpLevel = act.getLevel();
                    }
                }

                if (tmpLevel != Options.getInstance().getActLevel()) {
                    Options.getInstance().setActLevel(tmpLevel);
                }
            }

            int ord = Options.getInstance().getActLevel() - 1;
            if (this.levelMenuItems[ord] != null && this.levelComboBox.getItemCount() > ord) {
                this.levelMenuItems[ord].setSelected(true);
                this.levelComboBox.setSelectedIndex(ord);
            }

            int mOrdinal = Options.getInstance().getGameMode().ordinal();
            if (this.modeMenuItems != null && this.modeMenuItems[mOrdinal] != null) {
                this.modeMenuItems[mOrdinal].setSelected(true);
                String labelStr = this.modeMenuItems[mOrdinal].getText();
                if (labelStr.endsWith("...")) {
                    labelStr = labelStr.substring(0, labelStr.length() - 3);
                }

                if (Options.getInstance().getGameMode() != GameMode.PLAYING) {
                    labelStr = labelStr + " (" + Options.getInstance().getTrainingStepsString(true) + ")";
                }

                this.statusLabelModus.setText(labelStr);
            }

            this.showHintButtonsCheckBoxMenuItem.setSelected(Options.getInstance().isShowHintButtonsInToolbar());
        }

        this.statusLinePanel.invalidate();
    }

    private boolean isStringFlavorInClipboard() {
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        return clip.isDataFlavorAvailable(DataFlavor.stringFlavor);
    }

    private void adjustPasteMenuItem() {
        try {
            if (Main.OS_NAME.contains("mac")) {
                this.pasteMenuItem.setEnabled(true);
            } else if (this.isStringFlavorInClipboard()) {
                this.pasteMenuItem.setEnabled(true);
            } else {
                this.pasteMenuItem.setEnabled(false);
            }
        } catch (IllegalStateException ex) {
            this.clipTimer.start();
        }
    }

    @Override
    public void flavorsChanged(FlavorEvent e) {
        this.adjustPasteMenuItem();
    }

    private Image getIcon() {
        URL url = this.getClass().getResource("/img/hodoku02-32.png");
        return this.getToolkit().getImage(url);
    }

    public List<GuiState> getSavePoints() {
        return this.savePoints;
    }

    public void abortStep() {
        this.sudokuPanel.abortStep();
        this.hinweisTextArea.setText("");
        this.hinweisAbbrechenButton.setEnabled(false);
        this.hinweisAusfuehrenButton.setEnabled(false);
        if (this.executeStepToggleButton != null) {
            this.executeStepToggleButton.setEnabled(false);
        }

        if (this.abortStepToggleButton != null) {
            this.abortStepToggleButton.setEnabled(false);
        }

        this.fixFocus();
    }

    private void createProgressLabelImages() {
        try {
            this.progressImages[0] = new ImageIcon(this.getClass().getResource("/img/invalid20.png"));
            BufferedImage overlayImage = ImageIO.read(this.getClass().getResource("/img/ce1-20.png"));

            for (int i = 1; i < this.progressImages.length; i++) {
                BufferedImage act = new BufferedImage(20, 20, 6);
                Graphics2D gAct = (Graphics2D) act.getGraphics();
                gAct.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                gAct.setColor(Options.getInstance().getDifficultyLevels()[i].getBackgroundColor());
                gAct.fillOval(2, 2, 16, 16);
                gAct.drawImage(overlayImage, 0, 0, null);
                this.progressImages[i] = new ImageIcon(act);
            }
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error creating progressLabel images", ex);
        }
    }

    public void setProgressLabel() {
        if (this.sudokuPanel != null) {
            Sudoku2 sudoku = this.sudokuPanel.getSudoku();
            if (sudoku.getStatus() != SudokuStatus.VALID) {
                this.progressLabel.setIcon(this.progressImages[0]);
                this.progressLabel.setText("-");
            } else {
                if (this.getCurrentLevel() != null) {
                    this.progressLabel.setIcon(this.progressImages[this.getCurrentLevel().getOrdinal()]);
                    double proc = this.getCurrentScore();
                    int intProc = (int) (proc / sudoku.getScore() * 100.0);
                    if (intProc > 100) {
                        intProc = 100;
                    }

                    if (intProc < 1) {
                        intProc = 1;
                    }

                    if (this.getCurrentScore() == 0) {
                        intProc = 0;
                    }

                    this.progressLabel.setText(intProc + "%");
                }
            }
        }
    }

    public synchronized DifficultyLevel getCurrentLevel() {
        return this.currentLevel;
    }

    public synchronized void setCurrentLevel(DifficultyLevel currentLevel) {
        this.currentLevel = currentLevel;
    }

    public synchronized int getCurrentScore() {
        return this.currentScore;
    }

    public synchronized void setCurrentScore(int currentScore) {
        this.currentScore = currentScore;
    }

    private void setShowHintButtonsInToolbar() {
        if (this.vageHintToggleButton == null) {
            this.hintSeperator = new JSeparator();
            this.hintSeperator.setOrientation(1);
            this.hintSeperator.setMaximumSize(new Dimension(5, 32767));
            this.hintSeperator.setVisible(false);
            this.jToolBar1.add(this.hintSeperator);
            this.vageHintToggleButton = new JButton();
            this.vageHintToggleButton.setIcon(new ImageIcon(this.getClass().getResource("/img/vageHint.png")));
            this.vageHintToggleButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    MainFrame.this.hintToggleButtonActionPerformed(true);
                }
            });
            this.vageHintToggleButton.setToolTipText(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.vageHintToolButton.toolTipText"));
            this.vageHintToggleButton.setVisible(false);
            this.jToolBar1.add(this.vageHintToggleButton);
            this.concreteHintToggleButton = new JButton();
            this.concreteHintToggleButton.setIcon(new ImageIcon(this.getClass().getResource("/img/concreteHint.png")));
            this.concreteHintToggleButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    MainFrame.this.hintToggleButtonActionPerformed(false);
                }
            });
            this.concreteHintToggleButton.setVisible(false);
            this.concreteHintToggleButton.setToolTipText(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.concreteHintToolButton.toolTipText"));
            this.jToolBar1.add(this.concreteHintToggleButton);
            this.showNextStepToggleButton = new JButton();
            this.showNextStepToggleButton.setIcon(new ImageIcon(this.getClass().getResource("/img/nextHint.png")));
            this.showNextStepToggleButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    MainFrame.this.loesungsSchrittMenuItemActionPerformed(null);
                }
            });
            this.showNextStepToggleButton.setToolTipText(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.neuerHinweisButton.toolTipText"));
            this.showNextStepToggleButton.setVisible(false);
            this.jToolBar1.add(this.showNextStepToggleButton);
            this.executeStepToggleButton = new JButton();
            this.executeStepToggleButton.setIcon(new ImageIcon(this.getClass().getResource("/img/executeHint.png")));
            this.executeStepToggleButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    MainFrame.this.hinweisAusfuehrenButtonActionPerformed(null);
                }
            });
            this.executeStepToggleButton.setToolTipText(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.hinweisAusfuehrenButton.toolTipText"));
            this.executeStepToggleButton.setVisible(false);
            this.jToolBar1.add(this.executeStepToggleButton);
            this.abortStepToggleButton = new JButton();
            this.abortStepToggleButton.setIcon(new ImageIcon(this.getClass().getResource("/img/abortHint.png")));
            this.abortStepToggleButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    MainFrame.this.hinweisAbbrechenButtonActionPerformed(null);
                }
            });
            this.abortStepToggleButton.setToolTipText(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.hinweisAbbrechenButton.toolTipText"));
            this.abortStepToggleButton.setVisible(false);
            this.jToolBar1.add(this.abortStepToggleButton);
        }

        if (Options.getInstance().isShowHintButtonsInToolbar()) {
            this.hintSeperator.setVisible(true);
            this.vageHintToggleButton.setVisible(true);
            this.concreteHintToggleButton.setVisible(true);
            this.showNextStepToggleButton.setVisible(true);
            this.executeStepToggleButton.setVisible(true);
            this.abortStepToggleButton.setVisible(true);
            this.executeStepToggleButton.setEnabled(this.hinweisAusfuehrenButton.isEnabled());
            this.abortStepToggleButton.setEnabled(this.hinweisAusfuehrenButton.isEnabled());
        } else {
            this.hintSeperator.setVisible(false);
            this.vageHintToggleButton.setVisible(false);
            this.concreteHintToggleButton.setVisible(false);
            this.showNextStepToggleButton.setVisible(false);
            this.executeStepToggleButton.setVisible(false);
            this.abortStepToggleButton.setVisible(false);
        }

        this.check();
        this.fixFocus();
    }

    private void hintToggleButtonActionPerformed(boolean isVage) {
        if (isVage) {
            this.vageHintMenuItemActionPerformed(null);
        } else {
            this.mediumHintMenuItemActionPerformed(null);
        }
    }

    public boolean isEingabeModus() {
        return this.eingabeModus;
    }

    class MyCaretListener implements CaretListener {
        private boolean inUpdate = false;

        @Override
        public void caretUpdate(CaretEvent e) {
            if (!this.inUpdate) {
                String text = MainFrame.this.hinweisTextArea.getText();
                if (e.getDot() != 0 || e.getMark() != text.length()) {
                    int dot = e.getDot() > e.getMark() ? e.getDot() : e.getMark();
                    int line = 0;
                    int start = 0;
                    int end = 0;

                    for (int act = -1; act < dot; line++) {
                        act = text.indexOf(10, act + 1);
                        if (act == -1) {
                            end = text.length() - 1;
                            break;
                        }

                        if (act >= dot) {
                            end = act;
                            break;
                        }

                        start = act;
                    }

                    if (end > 0) {
                        this.inUpdate = true;
                        if (line == 0) {
                            MainFrame.this.sudokuPanel.setChainInStep(-1);
                        } else {
                            MainFrame.this.hinweisTextArea.setSelectionStart(start + 1);
                            MainFrame.this.hinweisTextArea.setSelectionEnd(end);
                            MainFrame.this.sudokuPanel.setChainInStep(line - 1);
                        }

                        this.inUpdate = false;
                    }
                }
            }
        }
    }

    class MyFileFilter extends FileFilter {
        private int type;

        MyFileFilter(int type) {
            this.type = type;
        }

        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }

            String[] parts = f.getName().split("\\.");
            if (parts.length > 1) {
                String ext = parts[parts.length - 1];
                switch (this.type) {
                    case 0:
                        if (ext.equalsIgnoreCase(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.config_file_ext"))) {
                            return true;
                        }
                        break;
                    case 1:
                        if (ext.equalsIgnoreCase(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.solution_file_ext"))) {
                            return true;
                        }
                        break;
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                        if (ext.equalsIgnoreCase(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.text_file_ext"))) {
                            return true;
                        }
                        break;
                    case 9:
                        if (ext.equalsIgnoreCase(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.ss_file_ext"))) {
                            return true;
                        }
                        break;
                    default:
                        return false;
                }
            }

            return false;
        }

        @Override
        public String getDescription() {
            switch (this.type) {
                case 0:
                    return ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.config_file_descr");
                case 1:
                    return ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.solution_file_descr");
                case 2:
                    return ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.solution_file_descr_gal");
                case 3:
                    return ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.solution_file_descr_gf");
                case 4:
                    return ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.solution_file_descr_pm");
                case 5:
                    return ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.solution_file_descr_pms");
                case 6:
                    return ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.solution_file_descr_pmg");
                case 7:
                    return ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.solution_file_descr_l");
                case 8:
                    return ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.solution_file_descr_text");
                case 9:
                    return ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.solution_file_descr_ss");
                default:
                    return ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.unknown_file_type");
            }
        }

        public int getType() {
            return this.type;
        }
    }
}
