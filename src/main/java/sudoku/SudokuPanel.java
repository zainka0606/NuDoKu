package sudoku;

import generator.SudokuGenerator;
import generator.SudokuGeneratorFactory;
import org.w3c.dom.Node;
import solver.SudokuSolver;
import solver.SudokuSolverFactory;
import solver.SudokuStepFinder;

import javax.imageio.*;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.FileImageOutputStream;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D.Double;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SudokuPanel extends JPanel implements Printable {
    private static final long serialVersionUID = 1L;
    private static final int[] KEY_CODES = new int[]{48, 49, 50, 51, 52, 53, 54, 55, 56, 57};
    private static final int DELTA = 5;
    private static final int DELTA_RAND = 5;
    private static BufferedImage[] colorKuImagesSmall = new BufferedImage[11];
    private static BufferedImage[] colorKuImagesLarge = new BufferedImage[9];
    private boolean showCandidates = Options.getInstance().isShowCandidates();
    private boolean showWrongValues = Options.getInstance().isShowWrongValues();
    private boolean showDeviations = Options.getInstance().isShowDeviations();
    private boolean invalidCells = Options.getInstance().isInvalidCells();
    private boolean showInvalidOrPossibleCells = false;
    private boolean[] showHintCellValues = new boolean[11];
    private boolean showAllCandidatesAkt = false;
    private boolean showAllCandidates = false;
    private int delta = 5;
    private int deltaRand = 5;
    private Font valueFont;
    private Font candidateFont;
    private int candidateHeight;
    private Font bigFont;
    private Font smallFont;
    private Sudoku2 sudoku;
    private SudokuSolver solver;
    private SudokuGenerator generator;
    private MainFrame mainFrame;
    private CellZoomPanel cellZoomPanel;
    private SolutionStep step;
    private int chainIndex = -1;
    private List<Integer> alsToShow = new ArrayList<>();
    private int oldWidth;
    private int width;
    private int height;
    private int cellSize;
    private int startSX;
    private int startSY;
    private Graphics2D g2;
    private java.awt.geom.CubicCurve2D.Double cubicCurve = new java.awt.geom.CubicCurve2D.Double();
    private Polygon arrow = new Polygon();
    private Stroke arrowStroke = new BasicStroke(1.5F, 1, 1);
    private Stroke strongLinkStroke = new BasicStroke(1.5F, 1, 1);
    private Stroke weakLinkStroke = new BasicStroke(1.5F, 1, 1, 10.0F, new float[]{5.0F}, 0.0F);
    private List<Double> points = new ArrayList<>(200);
    private double arrowLengthFactor = 0.16666666666666666;
    private double arrowHeightFactor = 0.3333333333333333;
    private int aktLine = 4;
    private int aktCol = 4;
    private int shiftLine = -1;
    private int shiftCol = -1;
    private Stack<Sudoku2> undoStack = new Stack<>();
    private Stack<Sudoku2> redoStack = new Stack<>();
    private SortedMap<Integer, Integer> coloringMap = new TreeMap<>();
    private SortedMap<Integer, Integer> coloringCandidateMap = new TreeMap<>();
    private int aktColorIndex = -1;
    private boolean colorCells = Options.getInstance().isColorCells();
    private Cursor colorCursor = null;
    private Cursor colorCursorShift = null;
    private Cursor oldCursor = null;
    private SortedSet<Integer> selectedCells = new TreeSet<>();
    private JMenuItem[] makeItems = null;
    private JMenuItem[] excludeItems = null;
    private JMenuItem[] toggleColorItems = null;
    private ProgressChecker progressChecker = null;
    private Timer deleteCursorTimer = new Timer(Options.getInstance().getDeleteCursorDisplayLength(), null);
    private long lastCursorChanged = -1L;
    private int lastPressedLine = -1;
    private int lastPressedCol = -1;
    private int lastPressedCand = -1;
    private int lastClickedLine = -1;
    private int lastClickedCol = -1;
    private int lastClickedCand = -1;
    private long lastClickedTime = 0L;
    private long doubleClickSpeed = -1L;
    private boolean[] remainingCandidates = new boolean[9];
    private JPopupMenu cellPopupMenu;
    private JMenuItem color1aMenuItem;
    private JMenuItem color1bMenuItem;
    private JMenuItem color2aMenuItem;
    private JMenuItem color2bMenuItem;
    private JMenuItem color3aMenuItem;
    private JMenuItem color3bMenuItem;
    private JMenuItem color4aMenuItem;
    private JMenuItem color4bMenuItem;
    private JMenuItem color5aMenuItem;
    private JMenuItem color5bMenuItem;
    private JMenuItem deleteValueMenuItem;
    private JPopupMenu deleteValuePopupMenu;
    private JMenuItem exclude1MenuItem;
    private JMenuItem exclude2MenuItem;
    private JMenuItem exclude3MenuItem;
    private JMenuItem exclude4MenuItem;
    private JMenuItem exclude5MenuItem;
    private JMenuItem exclude6MenuItem;
    private JMenuItem exclude7MenuItem;
    private JMenuItem exclude8MenuItem;
    private JMenuItem exclude9MenuItem;
    private JMenuItem excludeSeveralMenuItem;
    private JSeparator jSeparator1;
    private JSeparator jSeparator2;
    private JMenuItem make1MenuItem;
    private JMenuItem make2MenuItem;
    private JMenuItem make3MenuItem;
    private JMenuItem make4MenuItem;
    private JMenuItem make5MenuItem;
    private JMenuItem make6MenuItem;
    private JMenuItem make7MenuItem;
    private JMenuItem make8MenuItem;
    private JMenuItem make9MenuItem;

    public SudokuPanel(MainFrame mf) {
        this.mainFrame = mf;
        this.sudoku = new Sudoku2();
        this.sudoku.clearSudoku();
        this.setShowCandidates(Options.getInstance().isShowCandidates());
        this.generator = SudokuGeneratorFactory.getDefaultGeneratorInstance();
        this.solver = SudokuSolverFactory.getDefaultSolverInstance();
        this.solver.setSudoku(this.sudoku.clone());
        this.solver.solve();
        this.progressChecker = new ProgressChecker(this.mainFrame);
        this.initComponents();
        this.makeItems = new JMenuItem[]{
                this.make1MenuItem,
                this.make2MenuItem,
                this.make3MenuItem,
                this.make4MenuItem,
                this.make5MenuItem,
                this.make6MenuItem,
                this.make7MenuItem,
                this.make8MenuItem,
                this.make9MenuItem
        };
        this.excludeItems = new JMenuItem[]{
                this.exclude1MenuItem,
                this.exclude2MenuItem,
                this.exclude3MenuItem,
                this.exclude4MenuItem,
                this.exclude5MenuItem,
                this.exclude6MenuItem,
                this.exclude7MenuItem,
                this.exclude8MenuItem,
                this.exclude9MenuItem
        };
        this.toggleColorItems = new JMenuItem[]{
                this.color1aMenuItem,
                this.color1bMenuItem,
                this.color2aMenuItem,
                this.color2bMenuItem,
                this.color3aMenuItem,
                this.color3bMenuItem,
                this.color4aMenuItem,
                this.color4bMenuItem,
                this.color5aMenuItem,
                this.color5bMenuItem
        };
        this.setColorIconsInPopupMenu();
        this.updateCellZoomPanel();
        this.deleteCursorTimer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SudokuPanel.this.deleteCursorTimer.stop();
                SudokuPanel.this.lastCursorChanged = System.currentTimeMillis() - Options.getInstance().getDeleteCursorDisplayLength() - 100L;
                SudokuPanel.this.repaint();
            }
        });
        Object cs = Toolkit.getDefaultToolkit().getDesktopProperty("awt.multiClickInterval");
        if (cs instanceof Integer) {
            this.doubleClickSpeed = ((Integer) cs).intValue();
        }

        if (this.doubleClickSpeed == -1L) {
            this.doubleClickSpeed = 500L;
        }
    }

    private void initComponents() {
        this.cellPopupMenu = new JPopupMenu();
        this.make1MenuItem = new JMenuItem();
        this.make2MenuItem = new JMenuItem();
        this.make3MenuItem = new JMenuItem();
        this.make4MenuItem = new JMenuItem();
        this.make5MenuItem = new JMenuItem();
        this.make6MenuItem = new JMenuItem();
        this.make7MenuItem = new JMenuItem();
        this.make8MenuItem = new JMenuItem();
        this.make9MenuItem = new JMenuItem();
        this.jSeparator1 = new JSeparator();
        this.exclude1MenuItem = new JMenuItem();
        this.exclude2MenuItem = new JMenuItem();
        this.exclude3MenuItem = new JMenuItem();
        this.exclude4MenuItem = new JMenuItem();
        this.exclude5MenuItem = new JMenuItem();
        this.exclude6MenuItem = new JMenuItem();
        this.exclude7MenuItem = new JMenuItem();
        this.exclude8MenuItem = new JMenuItem();
        this.exclude9MenuItem = new JMenuItem();
        this.excludeSeveralMenuItem = new JMenuItem();
        this.jSeparator2 = new JSeparator();
        this.color1aMenuItem = new JMenuItem();
        this.color1bMenuItem = new JMenuItem();
        this.color2aMenuItem = new JMenuItem();
        this.color2bMenuItem = new JMenuItem();
        this.color3aMenuItem = new JMenuItem();
        this.color3bMenuItem = new JMenuItem();
        this.color4aMenuItem = new JMenuItem();
        this.color4bMenuItem = new JMenuItem();
        this.color5aMenuItem = new JMenuItem();
        this.color5bMenuItem = new JMenuItem();
        this.deleteValuePopupMenu = new JPopupMenu();
        this.deleteValueMenuItem = new JMenuItem();
        ResourceBundle bundle = ResourceBundle.getBundle("intl/SudokuPanel");
        this.make1MenuItem.setText(bundle.getString("SudokuPanel.popup.make1"));
        this.make1MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                SudokuPanel.this.make1MenuItemActionPerformed(evt);
            }
        });
        this.cellPopupMenu.add(this.make1MenuItem);
        this.make2MenuItem.setText(bundle.getString("SudokuPanel.popup.make2"));
        this.make2MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                SudokuPanel.this.make1MenuItemActionPerformed(evt);
            }
        });
        this.cellPopupMenu.add(this.make2MenuItem);
        this.make3MenuItem.setText(bundle.getString("SudokuPanel.popup.make3"));
        this.make3MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                SudokuPanel.this.make1MenuItemActionPerformed(evt);
            }
        });
        this.cellPopupMenu.add(this.make3MenuItem);
        this.make4MenuItem.setText(bundle.getString("SudokuPanel.popup.make4"));
        this.make4MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                SudokuPanel.this.make1MenuItemActionPerformed(evt);
            }
        });
        this.cellPopupMenu.add(this.make4MenuItem);
        this.make5MenuItem.setText(bundle.getString("SudokuPanel.popup.make5"));
        this.make5MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                SudokuPanel.this.make1MenuItemActionPerformed(evt);
            }
        });
        this.cellPopupMenu.add(this.make5MenuItem);
        this.make6MenuItem.setText(bundle.getString("SudokuPanel.popup.make6"));
        this.make6MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                SudokuPanel.this.make1MenuItemActionPerformed(evt);
            }
        });
        this.cellPopupMenu.add(this.make6MenuItem);
        this.make7MenuItem.setText(bundle.getString("SudokuPanel.popup.make7"));
        this.make7MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                SudokuPanel.this.make1MenuItemActionPerformed(evt);
            }
        });
        this.cellPopupMenu.add(this.make7MenuItem);
        this.make8MenuItem.setText(bundle.getString("SudokuPanel.popup.make8"));
        this.make8MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                SudokuPanel.this.make1MenuItemActionPerformed(evt);
            }
        });
        this.cellPopupMenu.add(this.make8MenuItem);
        this.make9MenuItem.setText(bundle.getString("SudokuPanel.popup.make9"));
        this.make9MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                SudokuPanel.this.make1MenuItemActionPerformed(evt);
            }
        });
        this.cellPopupMenu.add(this.make9MenuItem);
        this.cellPopupMenu.add(this.jSeparator1);
        this.exclude1MenuItem.setText(bundle.getString("SudokuPanel.popup.exclude1"));
        this.exclude1MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                SudokuPanel.this.exclude1MenuItemActionPerformed(evt);
            }
        });
        this.cellPopupMenu.add(this.exclude1MenuItem);
        this.exclude2MenuItem.setText(bundle.getString("SudokuPanel.popup.exclude2"));
        this.exclude2MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                SudokuPanel.this.exclude1MenuItemActionPerformed(evt);
            }
        });
        this.cellPopupMenu.add(this.exclude2MenuItem);
        this.exclude3MenuItem.setText(bundle.getString("SudokuPanel.popup.exclude3"));
        this.exclude3MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                SudokuPanel.this.exclude1MenuItemActionPerformed(evt);
            }
        });
        this.cellPopupMenu.add(this.exclude3MenuItem);
        this.exclude4MenuItem.setText(bundle.getString("SudokuPanel.popup.exclude4"));
        this.exclude4MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                SudokuPanel.this.exclude1MenuItemActionPerformed(evt);
            }
        });
        this.cellPopupMenu.add(this.exclude4MenuItem);
        this.exclude5MenuItem.setText(bundle.getString("SudokuPanel.popup.exclude5"));
        this.exclude5MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                SudokuPanel.this.exclude1MenuItemActionPerformed(evt);
            }
        });
        this.cellPopupMenu.add(this.exclude5MenuItem);
        this.exclude6MenuItem.setText(bundle.getString("SudokuPanel.popup.exclude6"));
        this.exclude6MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                SudokuPanel.this.exclude1MenuItemActionPerformed(evt);
            }
        });
        this.cellPopupMenu.add(this.exclude6MenuItem);
        this.exclude7MenuItem.setText(bundle.getString("SudokuPanel.popup.exclude7"));
        this.exclude7MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                SudokuPanel.this.exclude1MenuItemActionPerformed(evt);
            }
        });
        this.cellPopupMenu.add(this.exclude7MenuItem);
        this.exclude8MenuItem.setText(bundle.getString("SudokuPanel.popup.exclude8"));
        this.exclude8MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                SudokuPanel.this.exclude1MenuItemActionPerformed(evt);
            }
        });
        this.cellPopupMenu.add(this.exclude8MenuItem);
        this.exclude9MenuItem.setText(bundle.getString("SudokuPanel.popup.exclude9"));
        this.exclude9MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                SudokuPanel.this.exclude1MenuItemActionPerformed(evt);
            }
        });
        this.cellPopupMenu.add(this.exclude9MenuItem);
        this.excludeSeveralMenuItem.setText(bundle.getString("SudokuPanel.popup.several"));
        this.excludeSeveralMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                SudokuPanel.this.excludeSeveralMenuItemActionPerformed(evt);
            }
        });
        this.cellPopupMenu.add(this.excludeSeveralMenuItem);
        this.cellPopupMenu.add(this.jSeparator2);
        this.color1aMenuItem.setText(bundle.getString("SudokuPanel.popup.color1a"));
        this.color1aMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                SudokuPanel.this.color1aMenuItemActionPerformed(evt);
            }
        });
        this.cellPopupMenu.add(this.color1aMenuItem);
        this.color1bMenuItem.setText(bundle.getString("SudokuPanel.popup.color1b"));
        this.color1bMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                SudokuPanel.this.color1aMenuItemActionPerformed(evt);
            }
        });
        this.cellPopupMenu.add(this.color1bMenuItem);
        this.color2aMenuItem.setText(bundle.getString("SudokuPanel.popup.color2a"));
        this.color2aMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                SudokuPanel.this.color1aMenuItemActionPerformed(evt);
            }
        });
        this.cellPopupMenu.add(this.color2aMenuItem);
        this.color2bMenuItem.setText(bundle.getString("SudokuPanel.popup.color2b"));
        this.color2bMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                SudokuPanel.this.color1aMenuItemActionPerformed(evt);
            }
        });
        this.cellPopupMenu.add(this.color2bMenuItem);
        this.color3aMenuItem.setText(bundle.getString("SudokuPanel.popup.color3a"));
        this.color3aMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                SudokuPanel.this.color1aMenuItemActionPerformed(evt);
            }
        });
        this.cellPopupMenu.add(this.color3aMenuItem);
        this.color3bMenuItem.setText(bundle.getString("SudokuPanel.popup.color3b"));
        this.color3bMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                SudokuPanel.this.color1aMenuItemActionPerformed(evt);
            }
        });
        this.cellPopupMenu.add(this.color3bMenuItem);
        this.color4aMenuItem.setText(bundle.getString("SudokuPanel.popup.color4a"));
        this.color4aMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                SudokuPanel.this.color1aMenuItemActionPerformed(evt);
            }
        });
        this.cellPopupMenu.add(this.color4aMenuItem);
        this.color4bMenuItem.setText(bundle.getString("SudokuPanel.popup.color4b"));
        this.color4bMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                SudokuPanel.this.color1aMenuItemActionPerformed(evt);
            }
        });
        this.cellPopupMenu.add(this.color4bMenuItem);
        this.color5aMenuItem.setText(bundle.getString("SudokuPanel.popup.color5a"));
        this.color5aMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                SudokuPanel.this.color1aMenuItemActionPerformed(evt);
            }
        });
        this.cellPopupMenu.add(this.color5aMenuItem);
        this.color5bMenuItem.setText(bundle.getString("SudokuPanel.popup.color5b"));
        this.color5bMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                SudokuPanel.this.color1aMenuItemActionPerformed(evt);
            }
        });
        this.cellPopupMenu.add(this.color5bMenuItem);
        this.deleteValueMenuItem.setText(bundle.getString("SudokuPanel.deleteValueItem.text"));
        this.deleteValueMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                SudokuPanel.this.deleteValueMenuItemActionPerformed(evt);
            }
        });
        this.deleteValuePopupMenu.add(this.deleteValueMenuItem);
        this.setBackground(new Color(255, 255, 255));
        this.setMinimumSize(new Dimension(300, 300));
        this.setPreferredSize(new Dimension(600, 600));
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                SudokuPanel.this.formMouseClicked(evt);
            }

            @Override
            public void mouseReleased(MouseEvent evt) {
                SudokuPanel.this.formMouseReleased(evt);
            }

            @Override
            public void mousePressed(MouseEvent evt) {
                SudokuPanel.this.formMousePressed(evt);
            }
        });
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                SudokuPanel.this.formKeyPressed(evt);
            }

            @Override
            public void keyReleased(KeyEvent evt) {
                SudokuPanel.this.formKeyReleased(evt);
            }
        });
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING).addGap(0, 600, 32767));
        layout.setVerticalGroup(layout.createParallelGroup(Alignment.LEADING).addGap(0, 600, 32767));
    }

    private void formKeyReleased(KeyEvent evt) {
        this.handleKeysReleased(evt);
        this.updateCellZoomPanel();
        this.mainFrame.fixFocus();
    }

    private void formKeyPressed(KeyEvent evt) {
        int keyCode = evt.getKeyCode();
        switch (keyCode) {
            case 27:
                this.mainFrame.coloringPanelClicked(-1);
                this.clearRegion();
                if (this.step != null) {
                    this.mainFrame.abortStep();
                }
                break;
            default:
                this.handleKeys(evt);
        }

        this.updateCellZoomPanel();
        this.mainFrame.fixFocus();
    }

    private void formMouseClicked(MouseEvent evt) {
    }

    private void make1MenuItemActionPerformed(ActionEvent evt) {
        this.popupSetCell((JMenuItem) evt.getSource());
    }

    private void exclude1MenuItemActionPerformed(ActionEvent evt) {
        this.popupExcludeCandidate((JMenuItem) evt.getSource());
    }

    private void excludeSeveralMenuItemActionPerformed(ActionEvent evt) {
        String input = JOptionPane.showInputDialog(
                this,
                ResourceBundle.getBundle("intl/SudokuPanel").getString("SudokuPanel.cmessage"),
                ResourceBundle.getBundle("intl/SudokuPanel").getString("SudokuPanel.ctitle"),
                3
        );
        if (input != null) {
            this.undoStack.push(this.sudoku.clone());
            boolean changed = false;

            for (int i = 0; i < input.length(); i++) {
                char digit = input.charAt(i);
                if (Character.isDigit(digit) && this.removeCandidateFromActiveCells(Character.getNumericValue(digit))) {
                    changed = true;
                }
            }

            if (changed) {
                this.redoStack.clear();
                this.checkProgress();
            } else {
                this.undoStack.pop();
            }

            this.updateCellZoomPanel();
            this.mainFrame.check();
            this.repaint();
        }
    }

    private void color1aMenuItemActionPerformed(ActionEvent evt) {
        this.popupToggleColor((JMenuItem) evt.getSource());
    }

    private void formMousePressed(MouseEvent evt) {
        this.lastPressedLine = this.getLine(evt.getPoint());
        this.lastPressedCol = this.getCol(evt.getPoint());
        this.lastPressedCand = this.getCandidate(evt.getPoint(), this.lastPressedLine, this.lastPressedCol);
    }

    private void formMouseReleased(MouseEvent evt) {
        int line = this.getLine(evt.getPoint());
        int col = this.getCol(evt.getPoint());
        int cand = this.getCandidate(evt.getPoint(), line, col);
        if (line == this.lastPressedLine && col == this.lastPressedCol && cand == this.lastPressedCand) {
            long ticks = System.currentTimeMillis();
            if (this.lastClickedTime != -1L
                    && ticks - this.lastClickedTime <= this.doubleClickSpeed
                    && line == this.lastClickedLine
                    && col == this.lastClickedCol
                    && cand == this.lastClickedCand) {
                this.handleMouseClicked(evt, true);
                this.lastClickedTime = -1L;
            } else {
                this.handleMouseClicked(evt, false);
                this.lastClickedTime = ticks;
            }

            this.lastClickedLine = line;
            this.lastClickedCol = col;
            this.lastClickedCand = cand;
        }

        this.lastPressedLine = -1;
        this.lastPressedCol = -1;
        this.lastPressedCand = -1;
    }

    private void deleteValueMenuItemActionPerformed(ActionEvent evt) {
        this.popupDeleteValueFromCell();
    }

    private void handleMouseClicked(MouseEvent evt, boolean doubleClick) {
        this.undoStack.push(this.sudoku.clone());
        boolean changed = false;
        int line = this.getLine(evt.getPoint());
        int col = this.getCol(evt.getPoint());
        int cand = this.getCandidate(evt.getPoint(), line, col);
        boolean ctrlPressed = (evt.getModifiersEx() & 128) != 0;
        boolean shiftPressed = (evt.getModifiersEx() & 64) != 0;
        if (line >= 0 && line <= 8 && col >= 0 && col <= 8) {
            if (evt.getButton() == 3) {
                if (!Options.getInstance().isAlternativeMouseMode()) {
                    this.showPopupMenu(line, col);
                } else if (this.selectedCells.contains(Sudoku2.getIndex(line, col))) {
                    if (cand != -1) {
                        changed = this.toggleCandidateInAktCells(cand);
                    }
                } else {
                    this.setAktRowCol(line, col);
                    this.clearRegion();
                    if (this.sudoku.getValue(line, col) != 0 && !this.sudoku.isFixed(line, col)) {
                        this.deleteValuePopupMenu.show(this, this.getX(line, col) + this.cellSize, this.getY(line, col));
                    } else {
                        int showHintCellValue = this.getShowHintCellValue();
                        if ((cand == -1 || !this.sudoku.isCandidate(line, col, cand, !this.showCandidates)) && showHintCellValue != 0) {
                            if (this.showDeviations && this.sudoku.isSolutionSet() && cand == this.sudoku.getSolution(this.aktLine, this.aktCol)) {
                                this.toggleCandidateInCell(this.aktLine, this.aktCol, cand);
                            } else {
                                this.toggleCandidateInCell(this.aktLine, this.aktCol, showHintCellValue);
                            }
                        } else if (cand != -1) {
                            this.toggleCandidateInCell(this.aktLine, this.aktCol, cand);
                        }

                        changed = true;
                    }
                }
            } else if (this.aktColorIndex != -1) {
                int colorNumber = this.aktColorIndex;
                if (shiftPressed || evt.getButton() == 2) {
                    if (colorNumber % 2 == 0) {
                        colorNumber++;
                    } else {
                        colorNumber--;
                    }
                }

                if (this.colorCells) {
                    this.handleColoring(line, col, -1, colorNumber);
                } else if (cand != -1) {
                    this.handleColoring(line, col, cand, colorNumber);
                }

                this.setAktRowCol(line, col);
            } else if (evt.getButton() == 1) {
                int index = Sudoku2.getIndex(line, col);
                if (doubleClick) {
                    if (ctrlPressed) {
                        if (cand != -1) {
                            if (this.sudoku.isCandidate(index, cand, !this.showCandidates)) {
                                this.sudoku.setCandidate(index, cand, false, !this.showCandidates);
                            } else {
                                this.sudoku.setCandidate(index, cand, true, !this.showCandidates);
                            }

                            this.clearRegion();
                            changed = true;
                        }
                    } else if (this.sudoku.getValue(index) == 0) {
                        int showHintCellValue = this.getShowHintCellValue();
                        if (this.sudoku.getAnzCandidates(index, !this.showCandidates) == 1) {
                            int actCand = this.sudoku.getAllCandidates(index, !this.showCandidates)[0];
                            this.setCell(line, col, actCand);
                            changed = true;
                        } else if (showHintCellValue != 0 && this.isHiddenSingle(showHintCellValue, line, col)) {
                            this.setCell(line, col, showHintCellValue);
                            changed = true;
                        } else if (cand != -1) {
                            if (this.sudoku.isCandidate(index, cand, !this.showCandidates)) {
                                this.setCell(line, col, cand);
                            }

                            changed = true;
                        }
                    }
                } else if (!doubleClick) {
                    if (ctrlPressed) {
                        if (this.selectedCells.size() == 0) {
                            this.selectedCells.add(Sudoku2.getIndex(this.aktLine, this.aktCol));
                            this.selectedCells.add(Sudoku2.getIndex(line, col));
                            this.setAktRowCol(line, col);
                        } else {
                            int index2 = Sudoku2.getIndex(line, col);
                            if (this.selectedCells.contains(index2)) {
                                this.selectedCells.remove(index2);
                            } else {
                                this.selectedCells.add(Sudoku2.getIndex(line, col));
                            }

                            this.setAktRowCol(line, col);
                        }
                    } else if (shiftPressed) {
                        if (Options.getInstance().isUseShiftForRegionSelect()) {
                            this.selectRegion(line, col);
                        } else if (cand != -1) {
                            if (this.sudoku.isCandidate(index, cand, !this.showCandidates)) {
                                this.sudoku.setCandidate(index, cand, false, !this.showCandidates);
                            } else {
                                this.sudoku.setCandidate(index, cand, true, !this.showCandidates);
                            }

                            this.clearRegion();
                            changed = true;
                        }
                    } else {
                        if (!Options.getInstance().isAlternativeMouseMode()
                                || Options.getInstance().isAlternativeMouseMode() && !this.selectedCells.contains(Sudoku2.getIndex(line, col))) {
                            this.setAktRowCol(line, col);
                            this.clearRegion();
                        }

                        if (Options.getInstance().isAlternativeMouseMode()) {
                            if (this.sudoku.getValue(index) == 0) {
                                if (this.selectedCells.isEmpty()) {
                                    int showHintCellValue = this.getShowHintCellValue();
                                    if (this.sudoku.getAnzCandidates(index, !this.showCandidates) == 1) {
                                        int actCand = this.sudoku.getAllCandidates(index, !this.showCandidates)[0];
                                        this.setCell(line, col, actCand);
                                        changed = true;
                                    } else if (showHintCellValue != 0 && this.isHiddenSingle(showHintCellValue, line, col)) {
                                        this.setCell(line, col, showHintCellValue);
                                        changed = true;
                                    } else if (cand != -1) {
                                        if (this.sudoku.isCandidate(index, cand, !this.showCandidates)) {
                                            this.setCell(line, col, cand);
                                        }

                                        changed = true;
                                    }
                                } else if (cand == -1 || !this.sudoku.isCandidate(index, cand, !this.showCandidates)) {
                                    this.setAktRowCol(line, col);
                                    this.clearRegion();
                                } else if (cand != -1) {
                                    List<Integer> cells = new ArrayList<>();

                                    for (int selIndex : this.selectedCells) {
                                        if (this.sudoku.getValue(selIndex) == 0 && this.sudoku.isCandidate(selIndex, cand, !this.showCandidates)) {
                                            cells.add(selIndex);
                                        }
                                    }

                                    for (int selIndex : cells) {
                                        this.setCell(Sudoku2.getLine(selIndex), Sudoku2.getCol(selIndex), cand);
                                    }
                                }
                            } else {
                                this.setAktRowCol(line, col);
                                this.clearRegion();
                            }

                            changed = true;
                        }
                    }
                }
            }

            if (changed) {
                this.redoStack.clear();
                this.checkProgress();
            } else {
                this.undoStack.pop();
            }

            this.updateCellZoomPanel();
            this.mainFrame.check();
            this.repaint();
        }
    }

    private void setAktRowCol(int row, int col) {
        if (this.aktLine != row) {
            this.aktLine = row;
        }

        if (this.aktCol != col) {
            this.aktCol = col;
        }

        if (Options.getInstance().isDeleteCursorDisplay()) {
            this.deleteCursorTimer.stop();
            this.lastCursorChanged = System.currentTimeMillis();
            this.deleteCursorTimer.setDelay(Options.getInstance().getDeleteCursorDisplayLength());
            this.deleteCursorTimer.setInitialDelay(Options.getInstance().getDeleteCursorDisplayLength());
            this.deleteCursorTimer.start();
        }
    }

    public void getState(GuiState state, boolean copy) {
        state.setChainIndex(this.chainIndex);
        state.setUndoStack((Stack<Sudoku2>) this.undoStack.clone());
        state.setRedoStack((Stack<Sudoku2>) this.redoStack.clone());
        state.setColoringMap((SortedMap<Integer, Integer>) ((TreeMap) this.coloringMap).clone());
        state.setColoringCandidateMap((SortedMap<Integer, Integer>) ((TreeMap) this.coloringCandidateMap).clone());
        state.setSudoku(this.sudoku);
        state.setStep(this.step);
        if (copy) {
            state.setSudoku(this.sudoku.clone());
            if (this.step != null) {
                state.setStep((SolutionStep) this.step.clone());
            }
        }
    }

    public void setState(GuiState state) {
        this.chainIndex = state.getChainIndex();
        if (state.getUndoStack() != null) {
            this.undoStack = state.getUndoStack();
        } else {
            this.undoStack.clear();
        }

        if (state.getRedoStack() != null) {
            this.redoStack = state.getRedoStack();
        } else {
            this.redoStack.clear();
        }

        if (state.getColoringMap() != null) {
            this.coloringMap = state.getColoringMap();
        } else {
            this.coloringMap.clear();
        }

        if (state.getColoringCandidateMap() != null) {
            this.coloringCandidateMap = state.getColoringCandidateMap();
        } else {
            this.coloringCandidateMap.clear();
        }

        this.sudoku = state.getSudoku();
        this.sudoku.checkSudoku();
        this.step = state.getStep();
        this.updateCellZoomPanel();
        this.checkProgress();
        this.mainFrame.check();
        this.repaint();
    }

    private void checkShowAllCandidates(int modifiers, int keyCode) {
        boolean oldShowAllCandidatesAkt = this.showAllCandidatesAkt;
        this.showAllCandidatesAkt = false;
        if ((modifiers & 64) != 0 && (modifiers & 128) != 0) {
            this.showAllCandidatesAkt = true;
        }

        boolean oldShowAllCandidates = this.showAllCandidates;
        this.showAllCandidates = false;
        if ((modifiers & 64) != 0 && (modifiers & 512) != 0) {
            this.showAllCandidates = true;
        }

        if (oldShowAllCandidatesAkt != this.showAllCandidatesAkt || oldShowAllCandidates != this.showAllCandidates) {
            this.repaint();
        }
    }

    public void handleKeysReleased(KeyEvent evt) {
        int modifiers = evt.getModifiersEx();
        int keyCode = 0;
        this.checkShowAllCandidates(modifiers, keyCode);
        if (this.aktColorIndex >= 0 && this.getCursor() == this.colorCursorShift) {
            this.setCursor(this.colorCursor);
        }
    }

    public void handleKeys(KeyEvent evt) {
        boolean changed = false;
        this.undoStack.push(this.sudoku.clone());
        int keyCode = evt.getKeyCode();
        int modifiers = evt.getModifiersEx();
        this.checkShowAllCandidates(modifiers, keyCode);
        if (this.aktColorIndex >= 0 && (modifiers & 64) != 0 && this.getCursor() == this.colorCursor) {
            this.setCursor(this.colorCursorShift);
        }

        char keyChar = evt.getKeyChar();
        if (Character.isDigit(keyChar)) {
            keyCode = KEY_CODES[keyChar - '0'];
        }

        int number = 0;
        boolean clearSelectedRegion = true;
        switch (keyCode) {
            case 8:
            case 48:
            case 96:
            case 127:
                if ((modifiers & 128) == 0) {
                    if (this.sudoku.getValue(this.aktLine, this.aktCol) != 0 && !this.sudoku.isFixed(this.aktLine, this.aktCol)) {
                        this.sudoku.setCell(this.aktLine, this.aktCol, 0);
                        changed = true;
                    }

                    if (this.mainFrame.isEingabeModus() && Options.getInstance().isEditModeAutoAdvance()) {
                        if (this.aktCol < 8) {
                            this.setAktRowCol(this.aktLine, this.aktCol + 1);
                        } else if (this.aktLine < 8) {
                            this.setAktRowCol(this.aktLine + 1, 0);
                        }
                    }
                }
                break;
            case 9:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 33:
            case 34:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
            case 46:
            case 47:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
            case 64:
            case 70:
            case 71:
            case 72:
            case 73:
            case 74:
            case 75:
            case 76:
            case 77:
            case 78:
            case 79:
            case 80:
            case 81:
            case 83:
            case 84:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            case 90:
            case 91:
            case 92:
            case 93:
            case 94:
            case 95:
            case 106:
            case 107:
            case 108:
            case 109:
            case 110:
            case 111:
            case 121:
            case 122:
            case 123:
            case 124:
            case 125:
            case 126:
            case 128:
            case 129:
            case 130:
            case 131:
            case 132:
            case 133:
            case 134:
            case 135:
            case 136:
            case 137:
            case 138:
            case 139:
            case 140:
            case 141:
            case 142:
            case 143:
            case 144:
            case 145:
            case 146:
            case 147:
            case 148:
            case 149:
            case 150:
            case 151:
            case 152:
            case 153:
            case 154:
            case 155:
            case 156:
            case 157:
            case 158:
            case 159:
            case 160:
            default:
                short rem = this.sudoku.getRemainingCandidates();
                char ch = evt.getKeyChar();
                if (ch == '<' || ch == '>' || ch == ',' || ch == '.') {
                    boolean isUp = ch == '>' || ch == '.';
                    if (this.isShowInvalidOrPossibleCells()) {
                        int cand = 0;

                        for (int i = 1; i < this.showHintCellValues.length - 1; i++) {
                            if (this.showHintCellValues[i]) {
                                cand = i;
                                if (!isUp) {
                                    break;
                                }
                            }
                        }

                        int count = 0;

                        do {
                            if (isUp) {
                                if (++cand > 9) {
                                    cand = 1;
                                }
                            } else if (--cand < 1) {
                                cand = 9;
                            }
                        } while (++count < 8 && (Sudoku2.MASKS[cand] & rem) == 0);

                        if (count < 8) {
                            this.setShowHintCellValue(cand);
                            this.checkIsShowInvalidOrPossibleCells();
                        }
                    }
                }
                break;
            case 10:
                int index = Sudoku2.getIndex(this.aktLine, this.aktCol);
                if (this.sudoku.getValue(index) == 0) {
                    int showHintCellValue = this.getShowHintCellValue();
                    if (this.sudoku.getAnzCandidates(index, !this.showCandidates) == 1) {
                        int actCand = this.sudoku.getAllCandidates(index, !this.showCandidates)[0];
                        this.setCell(this.aktLine, this.aktCol, actCand);
                        changed = true;
                    } else if (showHintCellValue != 0 && this.isHiddenSingle(showHintCellValue, this.aktLine, this.aktCol)) {
                        this.setCell(this.aktLine, this.aktCol, showHintCellValue);
                        changed = true;
                    }
                }
                break;
            case 32:
                int candidate = this.getShowHintCellValue();
                if (this.isShowInvalidOrPossibleCells() && candidate != 0) {
                    changed = this.toggleCandidateInAktCells(candidate);
                }
                break;
            case 35:
                if ((modifiers & 64) != 0) {
                    this.setShift();
                    if ((modifiers & 128) != 0) {
                        this.shiftLine = 8;
                    } else {
                        this.shiftCol = 8;
                    }

                    this.selectRegion(this.shiftLine, this.shiftCol);
                    clearSelectedRegion = false;
                } else if ((modifiers & 128) != 0) {
                    this.setAktRowCol(8, this.aktCol);
                } else {
                    this.setAktRowCol(this.aktLine, 8);
                }

                if (clearSelectedRegion) {
                    this.clearRegion();
                }
                break;
            case 36:
                if ((modifiers & 64) != 0) {
                    this.setShift();
                    if ((modifiers & 128) != 0) {
                        this.shiftLine = 0;
                    } else {
                        this.shiftCol = 0;
                    }

                    this.selectRegion(this.shiftLine, this.shiftCol);
                    clearSelectedRegion = false;
                } else if ((modifiers & 128) != 0) {
                    this.setAktRowCol(0, this.aktCol);
                } else {
                    this.setAktRowCol(this.aktLine, 0);
                }

                if (clearSelectedRegion) {
                    this.clearRegion();
                }
                break;
            case 37:
                if ((modifiers & 128) != 0 && (modifiers & 64) != 0 && this.getShowHintCellValue() != 0) {
                    int indexx = this.findNextHintCandidate(this.aktLine, this.aktCol, keyCode);
                    this.setAktRowCol(Sudoku2.getLine(indexx), Sudoku2.getCol(indexx));
                } else if (this.aktCol > 0) {
                    this.setAktRowCol(this.aktLine, this.aktCol - 1);
                    if ((modifiers & 128) != 0) {
                        while (this.aktCol > 0 && this.sudoku.getValue(this.aktLine, this.aktCol) != 0) {
                            this.setAktRowCol(this.aktLine, this.aktCol - 1);
                        }
                    } else if ((modifiers & 64) != 0) {
                        this.setAktRowCol(this.aktLine, this.aktCol + 1);
                        this.setShift();
                        if (this.shiftCol > 0) {
                            this.shiftCol--;
                        }

                        this.selectRegion(this.shiftLine, this.shiftCol);
                        clearSelectedRegion = false;
                    }
                }

                if (clearSelectedRegion) {
                    this.clearRegion();
                }
                break;
            case 38:
                if ((modifiers & 128) != 0 && (modifiers & 64) != 0 && this.getShowHintCellValue() != 0) {
                    int indexx = this.findNextHintCandidate(this.aktLine, this.aktCol, keyCode);
                    this.setAktRowCol(Sudoku2.getLine(indexx), Sudoku2.getCol(indexx));
                } else if (this.aktLine > 0) {
                    this.setAktRowCol(this.aktLine - 1, this.aktCol);
                    if ((modifiers & 128) != 0) {
                        while (this.aktLine > 0 && this.sudoku.getValue(this.aktLine, this.aktCol) != 0) {
                            this.setAktRowCol(this.aktLine - 1, this.aktCol);
                        }
                    } else if ((modifiers & 64) != 0) {
                        this.setAktRowCol(this.aktLine + 1, this.aktCol);
                        this.setShift();
                        if (this.shiftLine > 0) {
                            this.shiftLine--;
                        }

                        this.selectRegion(this.shiftLine, this.shiftCol);
                        clearSelectedRegion = false;
                    }
                }

                if (clearSelectedRegion) {
                    this.clearRegion();
                }
                break;
            case 39:
                if ((modifiers & 128) != 0 && (modifiers & 64) != 0 && this.getShowHintCellValue() != 0) {
                    int indexx = this.findNextHintCandidate(this.aktLine, this.aktCol, keyCode);
                    this.setAktRowCol(Sudoku2.getLine(indexx), Sudoku2.getCol(indexx));
                } else if (this.aktCol < 8) {
                    this.setAktRowCol(this.aktLine, this.aktCol + 1);
                    if ((modifiers & 128) != 0) {
                        while (this.aktCol < 8 && this.sudoku.getValue(this.aktLine, this.aktCol) != 0) {
                            this.setAktRowCol(this.aktLine, this.aktCol + 1);
                        }
                    } else if ((modifiers & 64) != 0) {
                        this.setAktRowCol(this.aktLine, this.aktCol - 1);
                        this.setShift();
                        if (this.shiftCol < 8) {
                            this.shiftCol++;
                        }

                        this.selectRegion(this.shiftLine, this.shiftCol);
                        clearSelectedRegion = false;
                    }
                }

                if (clearSelectedRegion) {
                    this.clearRegion();
                }
                break;
            case 40:
                if ((modifiers & 128) != 0 && (modifiers & 64) != 0 && this.getShowHintCellValue() != 0) {
                    int indexx = this.findNextHintCandidate(this.aktLine, this.aktCol, keyCode);
                    this.setAktRowCol(Sudoku2.getLine(indexx), Sudoku2.getCol(indexx));
                } else if (this.aktLine < 8) {
                    this.setAktRowCol(this.aktLine + 1, this.aktCol);
                    if ((modifiers & 128) != 0) {
                        while (this.aktLine < 8 && this.sudoku.getValue(this.aktLine, this.aktCol) != 0) {
                            this.setAktRowCol(this.aktLine + 1, this.aktCol);
                        }
                    } else if ((modifiers & 64) != 0) {
                        this.setAktRowCol(this.aktLine - 1, this.aktCol);
                        this.setShift();
                        if (this.shiftLine < 8) {
                            this.shiftLine++;
                        }

                        this.selectRegion(this.shiftLine, this.shiftCol);
                        clearSelectedRegion = false;
                    }
                }

                if (clearSelectedRegion) {
                    this.clearRegion();
                }
                break;
            case 57:
            case 105:
                number++;
            case 56:
            case 104:
                number++;
            case 55:
            case 103:
                number++;
            case 54:
            case 102:
                number++;
            case 53:
            case 101:
                number++;
            case 52:
            case 100:
                number++;
            case 51:
            case 99:
                number++;
            case 50:
            case 98:
                number++;
            case 49:
            case 97:
                number++;
                if ((modifiers & 128) == 0) {
                    if (this.selectedCells.isEmpty()) {
                        this.setCell(this.aktLine, this.aktCol, number);
                        if (this.mainFrame.isEingabeModus() && Options.getInstance().isEditModeAutoAdvance()) {
                            if (this.aktCol < 8) {
                                this.setAktRowCol(this.aktLine, this.aktCol + 1);
                            } else if (this.aktLine < 8) {
                                this.setAktRowCol(this.aktLine + 1, 0);
                            }
                        }
                    } else {
                        List<Integer> cells = new ArrayList<>();

                        for (int indexx : this.selectedCells) {
                            if (this.sudoku.getValue(indexx) == 0 && this.sudoku.isCandidate(indexx, number, !this.showCandidates)) {
                                cells.add(indexx);
                            }
                        }

                        for (int indexx : cells) {
                            this.setCell(Sudoku2.getLine(indexx), Sudoku2.getCol(indexx), number);
                        }
                    }

                    changed = true;
                } else if (this.selectedCells.isEmpty()) {
                    this.toggleCandidateInCell(this.aktLine, this.aktCol, number);
                    changed = true;
                } else {
                    changed = this.toggleCandidateInAktCells(number);
                }
                break;
            case 69:
                number++;
            case 68:
                number++;
            case 67:
                number++;
            case 66:
                number++;
            case 65:
                if ((modifiers & 512) == 0 && (modifiers & 8192) == 0 && (modifiers & 128) == 0 && (modifiers & 256) == 0) {
                    number *= 2;
                    if ((modifiers & 64) != 0) {
                        number++;
                    }

                    this.handleColoring(-1, number);
                }
                break;
            case 82:
                this.clearColoring();
                break;
            case 120:
                number++;
            case 119:
                number++;
            case 118:
                number++;
            case 117:
                number++;
            case 116:
                number++;
            case 115:
                number++;
            case 114:
                number++;
            case 113:
                number++;
            case 112:
                number++;
                if ((modifiers & 512) == 0) {
                    if ((modifiers & 128) != 0) {
                        this.showHintCellValues[number] = !this.showHintCellValues[number];
                        this.showHintCellValues[10] = false;
                    } else {
                        if ((modifiers & 64) != 0) {
                            this.invalidCells = !this.invalidCells;
                        }

                        this.setShowHintCellValue(number);
                    }

                    this.checkIsShowInvalidOrPossibleCells();
                }
        }

        if (changed) {
            this.redoStack.clear();
            this.checkProgress();
        } else {
            this.undoStack.pop();
        }

        this.updateCellZoomPanel();
        this.mainFrame.check();
        this.repaint();
    }

    private void clearRegion() {
        this.selectedCells.clear();
        this.shiftLine = -1;
        this.shiftCol = -1;
    }

    private void setShift() {
        if (this.shiftLine == -1) {
            this.shiftLine = this.aktLine;
            this.shiftCol = this.aktCol;
        }
    }

    private void selectRegion(int line, int col) {
        this.selectedCells.clear();
        if (line != this.aktLine || col != this.aktCol) {
            int cStart = col < this.aktCol ? col : this.aktCol;
            int lStart = line < this.aktLine ? line : this.aktLine;

            for (int i = cStart; i <= cStart + Math.abs(col - this.aktCol); i++) {
                for (int j = lStart; j <= lStart + Math.abs(line - this.aktLine); j++) {
                    this.selectedCells.add(Sudoku2.getIndex(j, i));
                }
            }
        }
    }

    private int findNextHintCandidate(int line, int col, int mode) {
        int index = Sudoku2.getIndex(line, col);
        int showHintCellValue = this.getShowHintCellValue();
        if (showHintCellValue == 0) {
            return index;
        }

        switch (mode) {
            case 37:
                if (--index < 0) {
                    return index + 1;
                }

                while (index >= 0) {
                    if (this.sudoku.getValue(index) == 0 && this.sudoku.isCandidate(index, showHintCellValue, !this.showCandidates)) {
                        return index;
                    }

                    index--;
                }

                if (index < 0) {
                    index = Sudoku2.getIndex(line, col);
                }
                break;
            case 38:
                if (--line < 0) {
                    line = 8;
                    if (--col < 0) {
                        return index;
                    }
                }

                for (int i = col; i >= 0; i--) {
                    for (int j = i == col ? line : 8; j >= 0; j--) {
                        if (this.sudoku.getValue(j, i) == 0 && this.sudoku.isCandidate(j, i, showHintCellValue, !this.showCandidates)) {
                            return Sudoku2.getIndex(j, i);
                        }
                    }
                }
                break;
            case 39:
                if (++index >= this.sudoku.getCells().length) {
                    return index - 1;
                }

                while (index < this.sudoku.getCells().length) {
                    if (this.sudoku.getValue(index) == 0 && this.sudoku.isCandidate(index, showHintCellValue, !this.showCandidates)) {
                        return index;
                    }

                    index++;
                }

                if (index >= this.sudoku.getCells().length) {
                    index = Sudoku2.getIndex(line, col);
                }
                break;
            case 40:
                if (++line == 9) {
                    line = 0;
                    if (++col == 9) {
                        return index;
                    }
                }

                for (int i = col; i < 9; i++) {
                    for (int j = i == col ? line : 0; j < 9; j++) {
                        if (this.sudoku.getValue(j, i) == 0 && this.sudoku.isCandidate(j, i, showHintCellValue, !this.showCandidates)) {
                            return Sudoku2.getIndex(j, i);
                        }
                    }
                }
        }

        return index;
    }

    public void clearColoring() {
        this.coloringMap.clear();
        this.coloringCandidateMap.clear();
        this.setActiveColor(-1);
        this.updateCellZoomPanel();
        this.mainFrame.check();
    }

    private void handleColoring(int candidate, int colorNumber) {
        if (this.selectedCells.isEmpty()) {
            this.handleColoring(this.aktLine, this.aktCol, candidate, colorNumber);
        } else {
            for (int index : this.selectedCells) {
                this.handleColoring(Sudoku2.getLine(index), Sudoku2.getCol(index), candidate, colorNumber);
            }
        }
    }

    public void handleColoring(int candidate) {
        this.handleColoring(this.aktLine, this.aktCol, candidate, this.aktColorIndex);
        this.repaint();
        this.updateCellZoomPanel();
        this.mainFrame.fixFocus();
    }

    private void handleColoring(int line, int col, int candidate, int colorNumber) {
        if (Options.getInstance().isColorValues() || this.sudoku.getValue(line, col) == 0) {
            SortedMap<Integer, Integer> map = this.coloringMap;
            int key = Sudoku2.getIndex(line, col);
            if (candidate != -1) {
                key = key * 10 + candidate;
                map = this.coloringCandidateMap;
            }

            if (map.containsKey(key) && map.get(key) == colorNumber) {
                map.remove(key);
            } else {
                map.put(key, colorNumber);
            }

            this.updateCellZoomPanel();
        }
    }

    public void setCellFromCellZoomPanel(int number) {
        this.undoStack.push(this.sudoku.clone());
        if (this.selectedCells.isEmpty()) {
            this.setCell(this.aktLine, this.aktCol, number);
        } else {
            for (int index : this.selectedCells) {
                this.setCell(Sudoku2.getLine(index), Sudoku2.getCol(index), number);
            }
        }

        this.updateCellZoomPanel();
        this.mainFrame.check();
        this.repaint();
    }

    private void setCell(int line, int col, int number) {
        int index = Sudoku2.getIndex(line, col);
        if (!this.sudoku.isFixed(index) && this.sudoku.getValue(index) != number) {
            if (this.sudoku.getValue(index) != 0) {
                this.sudoku.setCell(line, col, 0);
            }

            this.sudoku.setCell(line, col, number);
            this.repaint();
            if (this.sudoku.isSolved() && Options.getInstance().isShowSudokuSolved()) {
                JOptionPane.showMessageDialog(
                        this,
                        ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.sudoku_solved"),
                        ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.congratulations"),
                        1
                );
            }
        }
    }

    private boolean toggleCandidateInAktCells(int candidate) {
        boolean changed = false;
        if (this.selectedCells.isEmpty()) {
            this.toggleCandidateInCell(this.aktLine, this.aktCol, candidate);
            return true;
        }

        boolean candPresent = false;

        for (int index : this.selectedCells) {
            if (this.sudoku.getValue(index) == 0 && this.sudoku.isCandidate(index, candidate, !this.showCandidates)) {
                candPresent = true;
                break;
            }
        }

        for (int index : this.selectedCells) {
            if (candPresent) {
                if (this.sudoku.getValue(index) == 0 && this.sudoku.isCandidate(index, candidate, !this.showCandidates)) {
                    this.sudoku.setCandidate(index, candidate, false, !this.showCandidates);
                    changed = true;
                }
            } else if (this.sudoku.getValue(index) == 0 && !this.sudoku.isCandidate(index, candidate, !this.showCandidates)) {
                this.sudoku.setCandidate(index, candidate, true, !this.showCandidates);
                changed = true;
            }
        }

        this.updateCellZoomPanel();
        return changed;
    }

    private void toggleCandidateInCell(int line, int col, int candidate) {
        int index = Sudoku2.getIndex(line, col);
        if (this.sudoku.getValue(index) == 0) {
            if (this.sudoku.isCandidate(index, candidate, !this.showCandidates)) {
                this.sudoku.setCandidate(index, candidate, false, !this.showCandidates);
            } else {
                this.sudoku.setCandidate(index, candidate, true, !this.showCandidates);
            }
        }

        this.updateCellZoomPanel();
    }

    public BufferedImage getSudokuImage(int size) {
        return this.getSudokuImage(size, false);
    }

    public BufferedImage getSudokuImage(int size, boolean allBlack) {
        BufferedImage fileImage = new BufferedImage(size, size, 5);
        Graphics2D g = fileImage.createGraphics();
        this.g2 = g;
        this.g2.setColor(Color.WHITE);
        this.g2.fillRect(0, 0, size, size);
        this.drawPage(size, size, true, false, allBlack, 1.0);
        return fileImage;
    }

    public void printSudoku(Graphics2D g, int x, int y, int size, boolean allBlack, double scale) {
        Graphics2D oldG2 = this.g2;
        this.g2 = g;
        AffineTransform trans = g.getTransform();
        g.translate(x, y);
        this.g2.setColor(Color.WHITE);
        this.g2.fillRect(0, 0, size, size);
        this.drawPage(size, size, true, true, allBlack, scale);
        g.setTransform(trans);
        this.g2 = oldG2;
    }

    public void saveSudokuAsPNG(File file, int size, int dpi) {
        BufferedImage fileImage = this.getSudokuImage(size);
        this.writePNG(fileImage, dpi, file);
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex > 0) {
            return 1;
        }

        Graphics2D printG2 = (Graphics2D) graphics;
        double scale = SudokuUtil.adjustGraphicsForPrinting(printG2);
        printG2.translate((int) (pageFormat.getImageableX() * scale), (int) (pageFormat.getImageableY() * scale));
        int printWidth = (int) (pageFormat.getImageableWidth() * scale);
        int printHeight = (int) (pageFormat.getImageableHeight() * scale);
        Font tmpFont = Options.getInstance().getBigFont();
        this.bigFont = new Font(tmpFont.getName(), tmpFont.getStyle(), (int) (tmpFont.getSize() * scale));
        tmpFont = Options.getInstance().getSmallFont();
        this.smallFont = new Font(tmpFont.getName(), tmpFont.getStyle(), (int) (tmpFont.getSize() * scale));
        printG2.setFont(this.bigFont);
        String title = "HoDoKu - v2.2.0";
        FontMetrics metrics = printG2.getFontMetrics();
        int textWidth = metrics.stringWidth(title);
        int textHeight = metrics.getHeight();
        int y = 2 * textHeight;
        printG2.drawString(title, (printWidth - textWidth) / 2, textHeight);
        printG2.setFont(this.smallFont);
        if (this.sudoku != null && this.sudoku.getLevel() != null) {
            title = this.sudoku.getLevel().getName() + " (" + this.sudoku.getScore() + ")";
            metrics = printG2.getFontMetrics();
            textWidth = metrics.stringWidth(title);
            textHeight = metrics.getHeight();
            printG2.drawString(title, (printWidth - textWidth) / 2, y);
            y += textHeight;
        }

        printG2.translate(0, y);
        this.g2 = printG2;
        this.drawPage(printWidth, printHeight, true, true, false, scale);
        return 0;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.g2 = (Graphics2D) g;
        this.drawPage(this.getBounds().width, this.getBounds().height, false, true, false, 1.0);
    }

    private void drawPage(int totalWidth, int totalHeight, boolean isPrint, boolean withBorder, boolean allBlack, double scale) {
        this.g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        this.g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        if (this.lastCursorChanged == -1L) {
            this.lastCursorChanged = System.currentTimeMillis();
        }

        this.width = totalWidth;
        this.height = totalHeight;
        this.width = this.height < this.width ? this.height : this.width;
        this.height = this.width < this.height ? this.width : this.height;
        float strokeWidth = 0.002F * this.width;
        if (this.width > 1000) {
            strokeWidth *= 1.5F;
        }

        float boxStrokeWidth = (float) (strokeWidth * Options.getInstance().getBoxLineFactor());
        int strokeWidthInt = Math.round(boxStrokeWidth / 2.0F);
        this.delta = totalWidth / 100;
        this.deltaRand = totalWidth / 100;
        if (this.deltaRand < strokeWidthInt) {
            this.deltaRand = strokeWidthInt;
        }

        if (Options.getInstance().getDrawMode() == 1) {
            this.delta = 0;
        }

        if (withBorder) {
            this.cellSize = (this.width - 4 * this.delta - 2 * this.deltaRand) / 9;
        } else {
            this.cellSize = (this.width - 4 * this.delta) / 9;
        }

        this.width = this.height = this.cellSize * 9 + 4 * this.delta;
        this.startSX = (totalWidth - this.width) / 2;
        if (isPrint && withBorder) {
            this.startSY = 0;
        } else {
            this.startSY = (totalHeight - this.height) / 2;
        }

        int colorKuCellSize = (int) (this.cellSize * 0.9);
        Font tmpFont = Options.getInstance().getDefaultValueFont();
        if (this.valueFont != null
                && (
                !this.valueFont.getName().equals(tmpFont.getName())
                        || this.valueFont.getStyle() != tmpFont.getStyle()
                        || this.valueFont.getSize() != (int) (this.cellSize * Options.getInstance().getValueFontFactor())
        )) {
            this.valueFont = new Font(tmpFont.getName(), tmpFont.getStyle(), (int) (this.cellSize * Options.getInstance().getValueFontFactor()));
        }

        tmpFont = Options.getInstance().getDefaultCandidateFont();
        if (this.candidateFont != null
                && (
                !this.candidateFont.getName().equals(tmpFont.getName())
                        || this.candidateFont.getStyle() != tmpFont.getStyle()
                        || this.candidateFont.getSize() != (int) (this.cellSize * Options.getInstance().getCandidateFontFactor())
        )) {
            int oldCandidateHeight = this.candidateHeight;
            this.candidateFont = new Font(tmpFont.getName(), tmpFont.getStyle(), (int) (this.cellSize * Options.getInstance().getCandidateFontFactor()));
            FontMetrics cm = this.getFontMetrics(this.candidateFont);
            this.candidateHeight = (int) ((cm.getAscent() - cm.getDescent()) * 1.3);
            if (this.candidateHeight != oldCandidateHeight) {
                this.resetColorKuImages();
            }
        }

        if (this.oldWidth != this.width) {
            int oldCandidateHeight = this.candidateHeight;
            this.oldWidth = this.width;
            this.valueFont = new Font(
                    Options.getInstance().getDefaultValueFont().getName(),
                    Options.getInstance().getDefaultValueFont().getStyle(),
                    (int) (this.cellSize * Options.getInstance().getValueFontFactor())
            );
            this.candidateFont = new Font(
                    Options.getInstance().getDefaultCandidateFont().getName(),
                    Options.getInstance().getDefaultCandidateFont().getStyle(),
                    (int) (this.cellSize * Options.getInstance().getCandidateFontFactor())
            );
            FontMetrics cm = this.getFontMetrics(this.candidateFont);
            this.candidateHeight = (int) ((cm.getAscent() - cm.getDescent()) * 1.3);
            if (this.candidateHeight != oldCandidateHeight) {
                this.resetColorKuImages();
            }
        }

        double dx = 0.0;
        double dy = 0.0;
        double dcx = 0.0;
        double dcy = 0.0;
        double ddy = 0.0;

        for (int line = 0; line < 9; line++) {
            for (int col = 0; col < 9; col++) {
                this.g2.setColor(Options.getInstance().getDefaultCellColor());
                if (Sudoku2.getBlock(Sudoku2.getIndex(line, col)) % 2 != 0) {
                    this.g2.setColor(Options.getInstance().getAlternateCellColor());
                }

                int cellIndex = Sudoku2.getIndex(line, col);
                boolean isSelected = this.selectedCells.isEmpty() && line == this.aktLine && col == this.aktCol || this.selectedCells.contains(cellIndex);
                if (isSelected
                        && this.selectedCells.isEmpty()
                        && Options.getInstance().isDeleteCursorDisplay()
                        && System.currentTimeMillis() - this.lastCursorChanged > Options.getInstance().getDeleteCursorDisplayLength()) {
                    isSelected = false;
                }

                if (isSelected && !isPrint && !Options.getInstance().isOnlySmallCursors()) {
                    this.setColor(this.g2, allBlack, Options.getInstance().getAktCellColor());
                }

                boolean candidateValid = false;
                if (this.showInvalidOrPossibleCells && this.showCandidates) {
                    candidateValid = this.sudoku.areCandidatesValid(cellIndex, this.showHintCellValues, false);
                }

                if (this.isShowInvalidOrPossibleCells()
                        && this.isInvalidCells()
                        && (this.sudoku.getValue(cellIndex) != 0 || this.showInvalidOrPossibleCells && !candidateValid)) {
                    this.setColor(this.g2, allBlack, Options.getInstance().getInvalidCellColor());
                }

                if (this.isShowInvalidOrPossibleCells()
                        && !this.isInvalidCells()
                        && this.sudoku.getValue(cellIndex) == 0
                        && candidateValid
                        && !Options.getInstance().isOnlySmallFilters()) {
                    this.setColor(this.g2, allBlack, Options.getInstance().getPossibleCellColor());
                }

                if (this.coloringMap.containsKey(cellIndex) && (this.sudoku.getValue(cellIndex) == 0 || Options.getInstance().isColorValues())) {
                    this.setColor(this.g2, allBlack, Options.getInstance().getColoringColors()[this.coloringMap.get(cellIndex)]);
                }

                this.g2.fillRect(this.getX(line, col), this.getY(line, col), this.cellSize, this.cellSize);
                if (isSelected && !isPrint && this.g2.getColor() != Options.getInstance().getAktCellColor()) {
                    this.setColor(this.g2, allBlack, Options.getInstance().getAktCellColor());
                    int frameSize = (int) (this.cellSize * Options.getInstance().getCursorFrameSize());
                    int cx = this.getX(line, col);
                    int cy = this.getY(line, col);
                    this.g2.fillRect(cx, cy, this.cellSize, frameSize);
                    this.g2.fillRect(cx, cy, frameSize, this.cellSize);
                    this.g2.fillRect(cx + this.cellSize - frameSize, cy, frameSize, this.cellSize);
                    this.g2.fillRect(cx, cy + this.cellSize - frameSize, this.cellSize, frameSize);
                }

                int startX = this.getX(line, col);
                int startY = this.getY(line, col);
                Color offColor = null;
                int offCand = 0;
                if (this.sudoku.getValue(cellIndex) != 0) {
                    this.setColor(this.g2, allBlack, Options.getInstance().getCellValueColor());
                    if (this.sudoku.isFixed(cellIndex)) {
                        this.setColor(this.g2, allBlack, Options.getInstance().getCellFixedValueColor());
                    } else if (this.isShowWrongValues() && !this.sudoku.isValidValue(line, col, this.sudoku.getValue(cellIndex))) {
                        offColor = Options.getInstance().getColorKuColor(10);
                        int var82 = 10;
                        this.setColor(this.g2, allBlack, Options.getInstance().getWrongValueColor());
                    } else if (this.isShowDeviations() && this.sudoku.isSolutionSet() && this.sudoku.getValue(cellIndex) != this.sudoku.getSolution(cellIndex)) {
                        offColor = Options.getInstance().getColorKuColor(11);
                        int var81 = 11;
                        this.setColor(this.g2, allBlack, Options.getInstance().getDeviationColor());
                    }

                    this.g2.setFont(this.valueFont);
                    dx = (this.cellSize - this.g2.getFontMetrics().stringWidth("8")) / 2.0;
                    dy = (this.cellSize + this.g2.getFontMetrics().getAscent() - this.g2.getFontMetrics().getDescent()) / 2.0;
                    int value = this.sudoku.getValue(cellIndex);
                    if (Options.getInstance().isShowColorKuAct()) {
                        this.drawColorBox(
                                value,
                                this.g2,
                                this.getX(line, col) + (this.cellSize - colorKuCellSize) / 2,
                                this.getY(line, col) + (this.cellSize - colorKuCellSize) / 2,
                                colorKuCellSize,
                                true
                        );
                        if (offColor != null) {
                            this.setColor(this.g2, allBlack, offColor);
                            this.g2.drawString("X", (int) (startX + dx), (int) (startY + dy));
                        }
                    } else {
                        this.g2.drawString(Integer.toString(value), (int) (startX + dx), (int) (startY + dy));
                    }
                } else {
                    this.g2.setFont(this.candidateFont);
                    boolean userCandidates = !this.showCandidates;
                    if (this.showAllCandidates || this.showAllCandidatesAkt && line == this.aktLine && col == this.aktCol) {
                        userCandidates = false;
                    }

                    double third = this.cellSize / 3.0;
                    dcx = (third - this.g2.getFontMetrics().stringWidth("8")) / 2.0;
                    dcy = (third + this.g2.getFontMetrics().getAscent() - this.g2.getFontMetrics().getDescent()) / 2.0;
                    ddy = (this.g2.getFontMetrics().getAscent() - this.g2.getFontMetrics().getDescent()) * Options.getInstance().getHintBackFactor();

                    for (int i = 1; i <= 9; i++) {
                        offColor = null;
                        if (this.sudoku.isCandidate(cellIndex, i, userCandidates)
                                || this.showCandidates && this.showDeviations && this.sudoku.isSolutionSet() && i == this.sudoku.getSolution(cellIndex)) {
                            Color hintColor = null;
                            Color candColor = null;
                            candColor = Options.getInstance().getCandidateColor();
                            double shiftX = (i - 1) % 3 * third;
                            double shiftY = (i - 1) / 3 * third;
                            if (Options.getInstance().isShowColorKuAct()) {
                                int ccx = (int) Math.round(startX + shiftX + third / 2.0 - this.candidateHeight / 2.0);
                                int ccy = (int) Math.round(startY + shiftY + third / 2.0 - this.candidateHeight / 2.0);
                                this.drawColorBox(i, this.g2, ccx, ccy, this.candidateHeight, false);
                            }

                            if (this.step != null) {
                                int index = Sudoku2.getIndex(line, col);
                                if (this.step.getIndices().indexOf(index) >= 0 && this.step.getValues().indexOf(i) >= 0) {
                                    hintColor = Options.getInstance().getHintCandidateBackColor();
                                    candColor = Options.getInstance().getHintCandidateColor();
                                }

                                int alsIndex = this.step.getAlsIndex(index, this.chainIndex);
                                if (alsIndex != -1 && (this.chainIndex == -1 && !this.step.getType().isKrakenFish() || this.alsToShow.contains(alsIndex))) {
                                    hintColor = Options.getInstance().getHintCandidateAlsBackColors()[alsIndex
                                            % Options.getInstance().getHintCandidateAlsBackColors().length];
                                    candColor = Options.getInstance().getHintCandidateAlsColors()[alsIndex % Options.getInstance().getHintCandidateAlsColors().length];
                                }

                                for (int k = 0; k < this.step.getChains().size(); k++) {
                                    if ((!this.step.getType().isKrakenFish() || this.chainIndex != -1) && (this.chainIndex == -1 || k == this.chainIndex)) {
                                        Chain chain = this.step.getChains().get(k);

                                        for (int j = chain.getStart(); j <= chain.getEnd(); j++) {
                                            if (chain.getChain()[j] != Integer.MIN_VALUE) {
                                                int chainEntry = Math.abs(chain.getChain()[j]);
                                                int index1 = -1;
                                                int index2 = -1;
                                                int index3 = -1;
                                                if (Chain.getSNodeType(chainEntry) == 0) {
                                                    index1 = Chain.getSCellIndex(chainEntry);
                                                }

                                                if (Chain.getSNodeType(chainEntry) == 1) {
                                                    index1 = Chain.getSCellIndex(chainEntry);
                                                    index2 = Chain.getSCellIndex2(chainEntry);
                                                    index3 = Chain.getSCellIndex3(chainEntry);
                                                }

                                                if ((index == index1 || index == index2 || index == index3) && Chain.getSCandidate(chainEntry) == i) {
                                                    if (Chain.isSStrong(chainEntry)) {
                                                        hintColor = Options.getInstance().getHintCandidateBackColor();
                                                        candColor = Options.getInstance().getHintCandidateColor();
                                                    } else {
                                                        hintColor = Options.getInstance().getHintCandidateFinBackColor();
                                                        candColor = Options.getInstance().getHintCandidateFinColor();
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                for (Candidate cand : this.step.getFins()) {
                                    if (cand.getIndex() == index && cand.getValue() == i) {
                                        hintColor = Options.getInstance().getHintCandidateFinBackColor();
                                        candColor = Options.getInstance().getHintCandidateFinColor();
                                    }
                                }

                                for (Candidate cand : this.step.getEndoFins()) {
                                    if (cand.getIndex() == index && cand.getValue() == i) {
                                        hintColor = Options.getInstance().getHintCandidateEndoFinBackColor();
                                        candColor = Options.getInstance().getHintCandidateEndoFinColor();
                                    }
                                }

                                if (this.step.getValues().contains(i) && this.step.getColorCandidates().containsKey(index)) {
                                    hintColor = Options.getInstance().getColoringColors()[this.step.getColorCandidates().get(index)];
                                    candColor = Options.getInstance().getCandidateColor();
                                }

                                for (Candidate cand : this.step.getCandidatesToDelete()) {
                                    if (cand.getIndex() == index && cand.getValue() == i) {
                                        hintColor = Options.getInstance().getHintCandidateDeleteBackColor();
                                        candColor = Options.getInstance().getHintCandidateDeleteColor();
                                    }
                                }

                                for (Candidate cand : this.step.getCannibalistic()) {
                                    if (cand.getIndex() == index && cand.getValue() == i) {
                                        hintColor = Options.getInstance().getHintCandidateCannibalisticBackColor();
                                        candColor = Options.getInstance().getHintCandidateCannibalisticColor();
                                    }
                                }
                            }

                            if (this.isShowWrongValues() && !this.sudoku.isCandidateValid(cellIndex, i, userCandidates)) {
                                offColor = Options.getInstance().getColorKuColor(10);
                                offCand = 10;
                                candColor = Options.getInstance().getWrongValueColor();
                            }

                            if (!this.sudoku.isCandidate(cellIndex, i, userCandidates)
                                    && this.isShowDeviations()
                                    && this.sudoku.isSolutionSet()
                                    && i == this.sudoku.getSolution(cellIndex)) {
                                offColor = Options.getInstance().getColorKuColor(11);
                                offCand = 11;
                                candColor = Options.getInstance().getDeviationColor();
                            }

                            if (this.isShowInvalidOrPossibleCells()
                                    && !this.isInvalidCells()
                                    && this.showHintCellValues[i]
                                    && Options.getInstance().isOnlySmallFilters()) {
                                this.setColor(this.g2, allBlack, Options.getInstance().getPossibleCellColor());
                                this.g2
                                        .fillRect(
                                                (int) Math.round(startX + shiftX + third / 2.0 - ddy / 2.0),
                                                (int) Math.round(startY + shiftY + third / 2.0 - ddy / 2.0),
                                                (int) Math.round(ddy),
                                                (int) Math.round(ddy)
                                        );
                            }

                            Color coloringColor = null;
                            if (this.coloringCandidateMap.containsKey(cellIndex * 10 + i)) {
                                coloringColor = Options.getInstance().getColoringColors()[this.coloringCandidateMap.get(cellIndex * 10 + i)];
                            }

                            if (coloringColor != null) {
                                this.setColor(this.g2, allBlack, coloringColor);
                                this.g2
                                        .fillRect(
                                                (int) Math.round(startX + shiftX + third / 2.0 - ddy / 2.0),
                                                (int) Math.round(startY + shiftY + third / 2.0 - ddy / 2.0),
                                                (int) Math.round(ddy),
                                                (int) Math.round(ddy)
                                        );
                            }

                            if (hintColor != null) {
                                this.setColor(this.g2, allBlack, hintColor);
                                this.g2
                                        .fillOval(
                                                (int) Math.round(startX + shiftX + third / 2.0 - ddy / 2.0),
                                                (int) Math.round(startY + shiftY + third / 2.0 - ddy / 2.0),
                                                (int) Math.round(ddy),
                                                (int) Math.round(ddy)
                                        );
                            }

                            this.setColor(this.g2, allBlack, candColor);
                            if (!Options.getInstance().isShowColorKuAct()) {
                                this.g2.drawString(Integer.toString(i), (int) Math.round(startX + dcx + shiftX), (int) Math.round(startY + dcy + shiftY));
                            } else if (offColor != null) {
                                int ccx = (int) Math.round(startX + shiftX + third / 2.0 - this.candidateHeight / 2.0);
                                int ccy = (int) Math.round(startY + shiftY + third / 2.0 - this.candidateHeight / 2.0);
                                this.drawColorBox(offCand, this.g2, ccx, ccy, this.candidateHeight, false);
                            }
                        }
                    }
                }
            }
        }

        switch (Options.getInstance().getDrawMode()) {
            case 0:
                if (allBlack) {
                    this.g2.setStroke(new BasicStroke(strokeWidth / 2.0F));
                } else {
                    this.g2.setStroke(new BasicStroke(strokeWidth));
                }

                this.setColor(this.g2, allBlack, Options.getInstance().getInnerGridColor());
                this.drawBlockLine(this.delta + this.startSX, 1 * this.delta + this.startSY, true);
                this.drawBlockLine(this.delta + this.startSX, 2 * this.delta + this.startSY + 3 * this.cellSize, true);
                this.drawBlockLine(this.delta + this.startSX, 3 * this.delta + this.startSY + 6 * this.cellSize, true);
                this.setColor(this.g2, allBlack, Options.getInstance().getGridColor());
                this.g2.setStroke(new BasicStroke(boxStrokeWidth));
                this.g2.drawRect(this.startSX, this.startSY, this.width, this.height);

                for (int i = 0; i < 3; i++) {
                    this.g2
                            .drawRect((i + 1) * this.delta + this.startSX + i * 3 * this.cellSize, 1 * this.delta + this.startSY, 3 * this.cellSize, 3 * this.cellSize);
                    this.g2
                            .drawRect(
                                    (i + 1) * this.delta + this.startSX + i * 3 * this.cellSize,
                                    2 * this.delta + this.startSY + 3 * this.cellSize,
                                    3 * this.cellSize,
                                    3 * this.cellSize
                            );
                    this.g2
                            .drawRect(
                                    (i + 1) * this.delta + this.startSX + i * 3 * this.cellSize,
                                    3 * this.delta + this.startSY + 6 * this.cellSize,
                                    3 * this.cellSize,
                                    3 * this.cellSize
                            );
                }
                break;
            case 1:
                if (allBlack) {
                    this.g2.setStroke(new BasicStroke(strokeWidth / 2.0F));
                } else {
                    this.g2.setStroke(new BasicStroke(strokeWidth));
                }

                this.setColor(this.g2, allBlack, Options.getInstance().getInnerGridColor());
                this.drawBlockLine(this.delta + this.startSX, 1 * this.delta + this.startSY, false);
                this.drawBlockLine(this.delta + this.startSX, 2 * this.delta + this.startSY + 3 * this.cellSize, false);
                this.drawBlockLine(this.delta + this.startSX, 3 * this.delta + this.startSY + 6 * this.cellSize, false);
                this.setColor(this.g2, allBlack, Options.getInstance().getGridColor());
                this.g2.setStroke(new BasicStroke(boxStrokeWidth));
                this.g2.drawRect(this.startSX, this.startSY, this.width, this.height);

                for (int i = 0; i < 3; i++) {
                    this.g2.drawLine(this.startSX, this.startSY + i * 3 * this.cellSize, this.startSX + 9 * this.cellSize, this.startSY + i * 3 * this.cellSize);
                    this.g2.drawLine(this.startSX + i * 3 * this.cellSize, this.startSY, this.startSX + i * 3 * this.cellSize, this.startSY + 9 * this.cellSize);
                }
        }

        if (this.step != null && !this.step.getChains().isEmpty()) {
            this.points.clear();

            for (int ci = 0; ci < this.step.getChainAnz(); ci++) {
                if ((!this.step.getType().isKrakenFish() || this.chainIndex != -1) && (this.chainIndex == -1 || this.chainIndex == ci)) {
                    Chain chain = this.step.getChains().get(ci);

                    for (int i = chain.getStart(); i <= chain.getEnd(); i++) {
                        int che = Math.abs(chain.getChain()[i]);
                        this.points.add(this.getCandKoord(Chain.getSCellIndex(che), Chain.getSCandidate(che), this.cellSize));
                        if (Chain.getSNodeType(che) == 1) {
                            int indexC = Chain.getSCellIndex2(che);
                            if (indexC != -1) {
                                this.points.add(this.getCandKoord(indexC, Chain.getSCandidate(che), this.cellSize));
                            }

                            indexC = Chain.getSCellIndex3(che);
                            if (indexC != -1) {
                                this.points.add(this.getCandKoord(indexC, Chain.getSCandidate(che), this.cellSize));
                            }
                        }
                    }
                }
            }

            for (Candidate cand : this.step.getCandidatesToDelete()) {
                this.points.add(this.getCandKoord(cand.getIndex(), cand.getValue(), this.cellSize));
            }

            for (int ai = 0; ai < this.step.getAlses().size(); ai++) {
                if ((!this.step.getType().isKrakenFish() || this.chainIndex != -1) && (this.chainIndex == -1 || this.alsToShow.contains(ai))) {
                    AlsInSolutionStep als = this.step.getAlses().get(ai);

                    for (int i = 0; i < als.getIndices().size(); i++) {
                        int index = als.getIndices().get(i);
                        int[] cands = this.sudoku.getAllCandidates(index);

                        for (int j = 0; j < cands.length; j++) {
                            this.points.add(this.getCandKoord(index, cands[j], this.cellSize));
                        }
                    }
                }
            }

            for (int ci = 0; ci < this.step.getChainAnz(); ci++) {
                if ((!this.step.getType().isKrakenFish() || this.chainIndex != -1) && (this.chainIndex == -1 || ci == this.chainIndex)) {
                    Chain chain = this.step.getChains().get(ci);
                    this.drawChain(this.g2, chain, this.cellSize, ddy, allBlack);
                }
            }
        }
    }

    private void setColor(Graphics2D g2, boolean allBlack, Color color) {
        if (allBlack) {
            g2.setColor(Color.BLACK);
        } else {
            g2.setColor(color);
        }
    }

    private void drawChain(Graphics2D g2, Chain chain, int cellSize, double ddy, boolean allBlack) {
        int[] ch = chain.getChain();
        List<Double> points1 = new ArrayList<>(chain.getEnd() + 1);

        for (int i = 0; i <= chain.getEnd(); i++) {
            if (i < chain.getStart()) {
                points1.add(null);
            } else {
                int che = Math.abs(ch[i]);
                points1.add(this.getCandKoord(Chain.getSCellIndex(che), Chain.getSCandidate(che), cellSize));
            }
        }

        Stroke oldStroke = g2.getStroke();
        int oldChe = 0;
        int oldIndex = 0;
        int index = 0;

        for (int i = chain.getStart(); i < chain.getEnd(); i++) {
            if (ch[i + 1] != Integer.MIN_VALUE) {
                index = i;
                int che = Math.abs(ch[i]);
                int che1 = Math.abs(ch[i + 1]);
                if (ch[i] > 0 && ch[i + 1] < 0) {
                    oldChe = che;
                    oldIndex = i;
                }

                if (ch[i] == Integer.MIN_VALUE && ch[i + 1] < 0) {
                    che = oldChe;
                    index = oldIndex;
                }

                if (ch[i] < 0 && ch[i + 1] > 0) {
                    che = oldChe;
                    index = oldIndex;
                }

                if (Chain.getSCellIndex(che) != Chain.getSCellIndex(che1)) {
                    this.setColor(g2, allBlack, Options.getInstance().getArrowColor());
                    if (Chain.isSStrong(che1)) {
                        g2.setStroke(this.strongLinkStroke);
                    } else {
                        g2.setStroke(this.weakLinkStroke);
                    }

                    this.drawArrow(g2, index, i + 1, cellSize, ddy, points1);
                }
            }
        }

        g2.setStroke(oldStroke);
    }

    private void drawArrow(Graphics2D g2, int index1, int index2, int cellSize, double ddy, List<Double> points1) {
        Double p1 = (Double) points1.get(index1).clone();
        Double p2 = (Double) points1.get(index2).clone();
        double length = p1.distance(p2);
        double deltaX = p2.x - p1.x;
        double deltaY = p2.y - p1.y;
        double alpha = Math.atan2(deltaY, deltaX);
        this.adjustEndPoints(p1, p2, alpha, ddy);
        double epsilon = 0.1;
        double dx1 = deltaX;
        double dy1 = deltaY;
        boolean doesIntersect = false;

        for (int i = 0; i < this.points.size(); i++) {
            if (!this.points.get(i).equals(points1.get(index1)) && !this.points.get(i).equals(points1.get(index2))) {
                Double point = this.points.get(i);
                double dx2 = point.x - p1.x;
                double dy2 = point.y - p1.y;
                if (Math.signum(dx1) == Math.signum(dx2)
                        && Math.signum(dy1) == Math.signum(dy2)
                        && Math.abs(dx2) <= Math.abs(dx1)
                        && Math.abs(dy2) <= Math.abs(dy1)
                        && (dx1 == 0.0 || dy1 == 0.0 || Math.abs(dx1 / dy1 - dx2 / dy2) < epsilon)) {
                    doesIntersect = true;
                    break;
                }
            }
        }

        if (length < 2.0 * ddy) {
            doesIntersect = true;
        }

        double aAlpha = alpha;
        if (doesIntersect) {
            double bezierLength = 20.0;
            if (length < 2.0 * ddy) {
                bezierLength = length / 4.0;
            }

            this.rotatePoint(points1.get(index1), p1, -Math.PI / 4);
            this.rotatePoint(points1.get(index2), p2, Math.PI / 4);
            double var51 = alpha - (Math.PI / 4);
            double bX1 = p1.x + bezierLength * Math.cos(var51);
            double bY1 = p1.y + bezierLength * Math.sin(var51);
            aAlpha = alpha + (Math.PI / 4);
            double bX2 = p2.x - bezierLength * Math.cos(aAlpha);
            double bY2 = p2.y - bezierLength * Math.sin(aAlpha);
            this.cubicCurve.setCurve(p1.x, p1.y, bX1, bY1, bX2, bY2, p2.x, p2.y);
            g2.draw(this.cubicCurve);
        } else {
            g2.drawLine((int) Math.round(p1.x), (int) Math.round(p1.y), (int) Math.round(p2.x), (int) Math.round(p2.y));
        }

        g2.setStroke(this.arrowStroke);
        double arrowLength = cellSize * this.arrowLengthFactor;
        double arrowHeight = arrowLength * this.arrowHeightFactor;
        if (length > arrowLength * 2.0 + ddy) {
            double sin = Math.sin(aAlpha);
            double cos = Math.cos(aAlpha);
            double aX = p2.x - cos * arrowLength;
            double aY = p2.y - sin * arrowLength;
            if (doesIntersect) {
                double aXTemp = 0.0;
                double aYTemp = 0.0;
                double eps = java.lang.Double.MAX_VALUE;
                double[] tmpPoints = new double[6];

                for (PathIterator pIt = this.cubicCurve.getPathIterator(null, 0.01); !pIt.isDone(); pIt.next()) {
                    int type = pIt.currentSegment(tmpPoints);
                    double dist = p2.distance(tmpPoints[0], tmpPoints[1]);
                    if (Math.abs(dist - arrowLength) < eps) {
                        eps = Math.abs(dist - arrowLength);
                        aXTemp = tmpPoints[0];
                        aYTemp = tmpPoints[1];
                    }
                }

                aX = aXTemp;
                aY = aYTemp;
                aAlpha = Math.atan2(p2.y - aY, p2.x - aX);
                sin = Math.sin(aAlpha);
                cos = Math.cos(aAlpha);
            }

            double daX = sin * arrowHeight;
            double daY = cos * arrowHeight;
            this.arrow.reset();
            this.arrow.addPoint((int) Math.round(aX - daX), (int) Math.round(aY + daY));
            this.arrow.addPoint((int) Math.round(p2.x), (int) Math.round(p2.y));
            this.arrow.addPoint((int) Math.round(aX + daX), (int) Math.round(aY - daY));
            g2.fill(this.arrow);
            g2.draw(this.arrow);
        }
    }

    private void rotatePoint(Double p1, Double p2, double angle) {
        p2.x = p2.x - p1.x;
        p2.y = p2.y - p1.y;
        double sinAngle = Math.sin(angle);
        double cosAngle = Math.cos(angle);
        double xact = p2.x;
        double yact = p2.y;
        p2.x = xact * cosAngle - yact * sinAngle;
        p2.y = xact * sinAngle + yact * cosAngle;
        p2.x = p2.x + p1.x;
        p2.y = p2.y + p1.y;
    }

    private void adjustEndPoints(Double p1, Double p2, double alpha, double ddy) {
        double tmpDelta = ddy / 2.0 + 4.0;
        int pX = (int) (tmpDelta * Math.cos(alpha));
        int pY = (int) (tmpDelta * Math.sin(alpha));
        p1.x += pX;
        p1.y += pY;
        p2.x -= pX;
        p2.y -= pY;
    }

    private Double getCandKoord(int index, int cand, int cellSize) {
        double third = cellSize / 3;
        double startX = this.getX(Sudoku2.getLine(index), Sudoku2.getCol(index));
        double startY = this.getY(Sudoku2.getLine(index), Sudoku2.getCol(index));
        double shiftX = (cand - 1) % 3 * third;
        double shiftY = (cand - 1) / 3 * third;
        double x = startX + shiftX + third / 2.0;
        double y = startY + shiftY + third / 2.0;
        return new Double(x, y);
    }

    private int getX(int line, int col) {
        int x = col * this.cellSize + this.delta + this.startSX;
        if (col > 2) {
            x += this.delta;
        }

        if (col > 5) {
            x += this.delta;
        }

        return x;
    }

    private int getY(int line, int col) {
        int y = line * this.cellSize + this.delta + this.startSY;
        if (line > 2) {
            y += this.delta;
        }

        if (line > 5) {
            y += this.delta;
        }

        return y;
    }

    private int getLine(Point p) {
        double tmp = p.y - this.startSY - this.delta;
        if ((!(tmp >= 3 * this.cellSize) || !(tmp <= 3 * this.cellSize + this.delta))
                && (!(tmp >= 6 * this.cellSize + this.delta) || !(tmp <= 6 * this.cellSize + 2 * this.delta))) {
            if (tmp > 3 * this.cellSize) {
                tmp -= this.delta;
            }

            if (tmp > 6 * this.cellSize) {
                tmp -= this.delta;
            }

            return (int) Math.ceil(tmp / this.cellSize - 1.0);
        } else {
            return -1;
        }
    }

    private int getCol(Point p) {
        double tmp = p.x - this.startSX - this.delta;
        if ((!(tmp >= 3 * this.cellSize) || !(tmp <= 3 * this.cellSize + this.delta))
                && (!(tmp >= 6 * this.cellSize + this.delta) || !(tmp <= 6 * this.cellSize + 2 * this.delta))) {
            if (tmp > 3 * this.cellSize) {
                tmp -= this.delta;
            }

            if (tmp > 6 * this.cellSize) {
                tmp -= this.delta;
            }

            return (int) Math.ceil(tmp / this.cellSize - 1.0);
        } else {
            return -1;
        }
    }

    private int getCandidate(Point p, int line, int col) {
        if (line >= 0 && col >= 0) {
            double startX = this.startSX + col * this.cellSize;
            if (col > 2) {
                startX += this.delta;
            }

            if (col > 5) {
                startX += this.delta;
            }

            double startY = this.startSY + line * this.cellSize;
            if (line > 2) {
                startY += this.delta;
            }

            if (line > 5) {
                startY += this.delta;
            }

            int candidate = -1;
            double cs3 = this.cellSize / 3.0;
            double dx = cs3;
            double leftDx = 0.0;

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    double sx = startX + i * cs3 + leftDx;
                    double sy = startY + j * cs3 + leftDx;
                    if (p.x >= sx && p.x <= sx + dx && p.y >= sy && p.y <= sy + dx) {
                        return j * 3 + i + 1;
                    }
                }
            }

            return -1;
        } else {
            return -1;
        }
    }

    public int getActiveColor() {
        return this.aktColorIndex;
    }

    public void setActiveColor(int colorNumber) {
        this.aktColorIndex = colorNumber;
        if (this.aktColorIndex < 0) {
            if (this.oldCursor != null) {
                this.setCursor(this.oldCursor);
                this.colorCursor = null;
                this.colorCursorShift = null;
            }
        } else {
            if (this.oldCursor == null) {
                this.oldCursor = this.getCursor();
            }

            this.createColorCursors();
            this.setCursor(this.colorCursor);
        }

        this.clearRegion();
        this.updateCellZoomPanel();
    }

    public void resetActiveColor() {
        int temp = this.aktColorIndex;
        this.setActiveColor(-1);
        this.setActiveColor(temp);
    }

    private void drawBlockLine(int x, int y, boolean withRect) {
        this.drawBlock(x, y, withRect);
        this.drawBlock(x + 3 * this.cellSize + this.delta, y, withRect);
        this.drawBlock(x + 6 * this.cellSize + 2 * this.delta, y, withRect);
    }

    private void drawBlock(int x, int y, boolean withRect) {
        if (withRect) {
            this.g2.drawRect(x, y, 3 * this.cellSize, 3 * this.cellSize);
        }

        this.g2.drawLine(x, y + 1 * this.cellSize, x + 3 * this.cellSize, y + 1 * this.cellSize);
        this.g2.drawLine(x, y + 2 * this.cellSize, x + 3 * this.cellSize, y + 2 * this.cellSize);
        this.g2.drawLine(x + 1 * this.cellSize, y, x + 1 * this.cellSize, y + 3 * this.cellSize);
        this.g2.drawLine(x + 2 * this.cellSize, y, x + 2 * this.cellSize, y + 3 * this.cellSize);
    }

    public Sudoku2 getSudoku() {
        return this.sudoku;
    }

    public void setSudoku(Sudoku2 newSudoku) {
        this.setSudoku(newSudoku.getSudoku(ClipboardMode.PM_GRID, null), false);
    }

    public void setSudoku(String init) {
        this.setSudoku(init, false);
    }

    public boolean isShowCandidates() {
        return this.showCandidates;
    }

    public final void setShowCandidates(boolean showCandidates) {
        this.showCandidates = showCandidates;
        this.repaint();
    }

    public boolean isShowWrongValues() {
        return this.showWrongValues;
    }

    public void setShowWrongValues(boolean showWrongValues) {
        this.showWrongValues = showWrongValues;
        this.repaint();
    }

    public boolean undoPossible() {
        return this.undoStack.size() > 0;
    }

    public boolean redoPossible() {
        return this.redoStack.size() > 0;
    }

    public void undo() {
        if (this.undoPossible()) {
            this.redoStack.push(this.sudoku);
            this.sudoku = this.undoStack.pop();
            this.updateCellZoomPanel();
            this.checkProgress();
            this.mainFrame.setCurrentLevel(this.sudoku.getLevel());
            this.mainFrame.setCurrentScore(this.sudoku.getScore());
            this.mainFrame.check();
            this.repaint();
        }
    }

    public void redo() {
        if (this.redoPossible()) {
            this.undoStack.push(this.sudoku);
            this.sudoku = this.redoStack.pop();
            this.updateCellZoomPanel();
            this.checkProgress();
            this.mainFrame.setCurrentLevel(this.sudoku.getLevel());
            this.mainFrame.setCurrentScore(this.sudoku.getScore());
            this.mainFrame.check();
            this.repaint();
        }
    }

    public void clearUndoRedo() {
        this.undoStack.clear();
        this.redoStack.clear();
    }

    public void setSudoku(Sudoku2 newSudoku, boolean alreadySolved) {
        this.setSudoku(newSudoku.getSudoku(ClipboardMode.PM_GRID, null), alreadySolved);
    }

    public void setSudoku(String init, boolean alreadySolved) {
        this.step = null;
        this.setChainInStep(-1);
        this.undoStack.clear();
        this.redoStack.clear();
        this.coloringMap.clear();
        this.resetShowHintCellValues();
        if (init != null && init.length() != 0) {
            this.sudoku.setSudoku(init);
            this.sudoku.setLevel(Options.getInstance().getDifficultyLevels()[DifficultyType.EASY.ordinal()]);
            this.sudoku.setScore(0);
            Sudoku2 tmpSudoku = this.sudoku.clone();
            if (!alreadySolved) {
                this.getSolver().setSudoku(tmpSudoku);
            }

            int anzSolutions = this.generator.getNumberOfSolutions(this.sudoku);
            if (anzSolutions == 0) {
                JOptionPane.showMessageDialog(
                        this,
                        ResourceBundle.getBundle("intl/SudokuPanel").getString("SudokuPanel.no_solution"),
                        ResourceBundle.getBundle("intl/SudokuPanel").getString("SudokuPanel.invalid_puzzle"),
                        0
                );
                this.sudoku.setStatus(SudokuStatus.INVALID);
            } else if (anzSolutions > 1) {
                JOptionPane.showMessageDialog(
                        this,
                        ResourceBundle.getBundle("intl/SudokuPanel").getString("SudokuPanel.multiple_solutions"),
                        ResourceBundle.getBundle("intl/SudokuPanel").getString("SudokuPanel.invalid_puzzle"),
                        0
                );
                this.sudoku.setStatus(SudokuStatus.MULTIPLE_SOLUTIONS);
            } else if (!this.sudoku.checkSudoku()) {
                JOptionPane.showMessageDialog(
                        this,
                        ResourceBundle.getBundle("intl/SudokuPanel").getString("SudokuPanel.wrong_values"),
                        ResourceBundle.getBundle("intl/SudokuPanel").getString("SudokuPanel.invalid_puzzle"),
                        0
                );
                this.sudoku.setStatus(SudokuStatus.INVALID);
            } else {
                this.sudoku.setStatus(SudokuStatus.VALID);
                if (this.sudoku.getFixedCellsAnz() > 17) {
                    Sudoku2 fixedOnly = new Sudoku2();
                    fixedOnly.setSudoku(this.sudoku.getSudoku(ClipboardMode.CLUES_ONLY));
                    int anzFixedSol = this.generator.getNumberOfSolutions(fixedOnly);
                    this.sudoku.setStatusGivens(anzFixedSol);
                }

                if (!alreadySolved) {
                    tmpSudoku.setStatus(SudokuStatus.VALID);
                    tmpSudoku.setStatusGivens(this.sudoku.getStatusGivens());
                    tmpSudoku.setSolution(this.sudoku.getSolution());
                    this.getSolver().solve(true);
                }

                this.sudoku.setLevel(this.getSolver().getSudoku().getLevel());
                this.sudoku.setScore(this.getSolver().getSudoku().getScore());
            }
        } else {
            this.sudoku = new Sudoku2();
        }

        this.updateCellZoomPanel();
        if (this.mainFrame != null) {
            this.mainFrame.setCurrentLevel(this.sudoku.getLevel());
            this.mainFrame.setCurrentScore(this.sudoku.getScore());
            this.mainFrame.check();
        }

        this.repaint();
    }

    public String getSudokuString(ClipboardMode mode) {
        return this.sudoku.getSudoku(mode, this.step);
    }

    public SudokuSolver getSolver() {
        return this.solver;
    }

    public void solveUpTo() {
        SolutionStep actStep = null;
        boolean changed = false;
        this.undoStack.push(this.sudoku.clone());

        for (GameMode gm = Options.getInstance().getGameMode();
             (actStep = this.solver.getHint(this.sudoku, false)) != null
                     && (gm == GameMode.PLAYING ? actStep.getType().getStepConfig().isEnabledProgress() : !actStep.getType().getStepConfig().isEnabledTraining());
             changed = true
        ) {
            this.getSolver().doStep(this.sudoku, actStep);
        }

        if (changed) {
            this.redoStack.clear();
        } else {
            this.undoStack.pop();
        }

        this.step = null;
        this.setChainInStep(-1);
        this.updateCellZoomPanel();
        this.checkProgress();
        this.mainFrame.check();
        this.repaint();
    }

    public SolutionStep getNextStep(boolean singlesOnly) {
        this.step = this.solver.getHint(this.sudoku, singlesOnly);
        this.setChainInStep(-1);
        this.repaint();
        return this.step;
    }

    public SolutionStep getStep() {
        return this.step;
    }

    public void setStep(SolutionStep step) {
        this.step = step;
        this.setChainInStep(-1);
        this.repaint();
    }

    public void setChainInStep(int chainIndex) {
        if (this.step == null) {
            chainIndex = -1;
        } else if (this.step.getType().isKrakenFish() && chainIndex > -1) {
            chainIndex--;
        }

        if (chainIndex >= 0 && chainIndex > this.step.getChainAnz() - 1) {
            chainIndex = -1;
        }

        this.chainIndex = chainIndex;
        this.alsToShow.clear();
        if (chainIndex != -1) {
            Chain chain = this.step.getChains().get(chainIndex);

            for (int i = chain.getStart(); i <= chain.getEnd(); i++) {
                if (chain.getNodeType(i) == 2) {
                    this.alsToShow.add(Chain.getSAlsIndex(chain.getChain()[i]));
                }
            }
        }

        this.repaint();
    }

    public void doStep() {
        if (this.step != null) {
            this.undoStack.push(this.sudoku.clone());
            this.redoStack.clear();
            this.getSolver().doStep(this.sudoku, this.step);
            this.step = null;
            this.setChainInStep(-1);
            this.updateCellZoomPanel();
            this.checkProgress();
            this.mainFrame.check();
            this.repaint();
            if (this.sudoku.isSolved() && Options.getInstance().isShowSudokuSolved()) {
                JOptionPane.showMessageDialog(
                        this,
                        ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.sudoku_solved"),
                        ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.congratulations"),
                        1
                );
            }
        }
    }

    public void abortStep() {
        this.step = null;
        this.setChainInStep(-1);
        this.repaint();
    }

    public int getSolvedCellsAnz() {
        return this.sudoku.getSolvedCellsAnz();
    }

    public void setNoClues() {
        this.sudoku.setNoClues();
        this.repaint();
    }

    public boolean isInvalidCells() {
        return this.invalidCells;
    }

    public void setInvalidCells(boolean invalidCells) {
        this.invalidCells = invalidCells;
    }

    public boolean isShowInvalidOrPossibleCells() {
        return this.showInvalidOrPossibleCells;
    }

    public void setShowInvalidOrPossibleCells(boolean showInvalidOrPossibleCells) {
        this.showInvalidOrPossibleCells = showInvalidOrPossibleCells;
    }

    public boolean[] getShowHintCellValues() {
        return this.showHintCellValues;
    }

    public void setShowHintCellValues(boolean[] showHintCellValues) {
        this.showHintCellValues = showHintCellValues;
    }

    public void resetShowHintCellValues() {
        for (int i = 1; i < this.showHintCellValues.length; i++) {
            this.showHintCellValues[i] = false;
        }

        this.showInvalidOrPossibleCells = false;
    }

    public boolean isShowDeviations() {
        return this.showDeviations;
    }

    public void setShowDeviations(boolean showDeviations) {
        this.showDeviations = showDeviations;
        this.mainFrame.check();
        this.repaint();
    }

    private void writePNG(BufferedImage bi, int dpi, File file) {
        Iterator<ImageWriter> i = ImageIO.getImageWritersByFormatName("png");
        if (i.hasNext()) {
            ImageWriter imageWriter = i.next();
            ImageWriteParam param = imageWriter.getDefaultWriteParam();
            ImageTypeSpecifier its = new ImageTypeSpecifier(bi.getColorModel(), bi.getSampleModel());
            IIOMetadata iomd = imageWriter.getDefaultImageMetadata(its, param);
            String formatName = "javax_imageio_png_1.0";
            Node node = iomd.getAsTree(formatName);
            int dpiRes = (int) (dpi / 2.54 * 100.0);
            IIOMetadataNode res = new IIOMetadataNode("pHYs");
            res.setAttribute("pixelsPerUnitXAxis", String.valueOf(dpiRes));
            res.setAttribute("pixelsPerUnitYAxis", String.valueOf(dpiRes));
            res.setAttribute("unitSpecifier", "meter");
            node.appendChild(res);

            try {
                iomd.setFromTree(formatName, node);
            } catch (IIOInvalidTreeException e) {
                JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), ResourceBundle.getBundle("intl/SudokuPanel").getString("SudokuPanel.error"), 0);
            }

            IIOImage iioimage = new IIOImage(bi, null, iomd);

            try {
                FileImageOutputStream out = new FileImageOutputStream(file);
                imageWriter.setOutput(out);
                imageWriter.write(iioimage);
                out.close();
                String companionFileName = file.getPath();
                if (companionFileName.toLowerCase().endsWith(".png")) {
                    companionFileName = companionFileName.substring(0, companionFileName.length() - 4);
                }

                companionFileName = companionFileName + ".txt";
                PrintWriter cOut = new PrintWriter(new BufferedWriter(new FileWriter(companionFileName)));
                cOut.println(this.getSudokuString(ClipboardMode.CLUES_ONLY));
                cOut.println(this.getSudokuString(ClipboardMode.LIBRARY));
                cOut.println(this.getSudokuString(ClipboardMode.PM_GRID));
                if (this.step != null) {
                    cOut.println(this.getSudokuString(ClipboardMode.PM_GRID_WITH_STEP));
                }

                cOut.close();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), ResourceBundle.getBundle("intl/SudokuPanel").getString("SudokuPanel.error"), 0);
            }
        }
    }

    public boolean isColorCells() {
        return this.colorCells;
    }

    public void setColorCells(boolean colorCells) {
        this.colorCells = colorCells;
        Options.getInstance().setColorCells(colorCells);
        this.updateCellZoomPanel();
    }

    private void createColorCursors() {
        try {
            Point cursorHotSpot = new Point(2, 4);
            BufferedImage img1 = ImageIO.read(this.getClass().getResource("/img/c_color.png"));
            Graphics2D gImg1 = (Graphics2D) img1.getGraphics();
            gImg1.setColor(Options.getInstance().getColoringColors()[this.aktColorIndex]);
            gImg1.fillRect(19, 18, 12, 12);
            this.colorCursor = Toolkit.getDefaultToolkit().createCustomCursor(img1, cursorHotSpot, "c_strong");
            BufferedImage img2 = ImageIO.read(this.getClass().getResource("/img/c_color.png"));
            Graphics2D gImg2 = (Graphics2D) img2.getGraphics();
            if (this.aktColorIndex % 2 == 0) {
                gImg2.setColor(Options.getInstance().getColoringColors()[this.aktColorIndex + 1]);
            } else {
                gImg2.setColor(Options.getInstance().getColoringColors()[this.aktColorIndex - 1]);
            }

            gImg2.fillRect(19, 18, 12, 12);
            this.colorCursorShift = Toolkit.getDefaultToolkit().createCustomCursor(img2, cursorHotSpot, "c_weak");
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error creating color cursors", ex);
        }
    }

    private boolean isHiddenSingle(int candidate, int line, int col) {
        this.sudoku.rebuildInternalData();
        SudokuStepFinder finder = SudokuSolverFactory.getDefaultSolverInstance().getStepFinder();

        for (SolutionStep act : finder.findAllHiddenSingles(this.sudoku)) {
            if (act.getType() == SolutionType.HIDDEN_SINGLE && act.getValues().get(0) == candidate && act.getIndices().get(0) == Sudoku2.getIndex(line, col)) {
                return true;
            }
        }

        return false;
    }

    public final void setColorIconsInPopupMenu() {
        this.setColorIconInPopupMenu(this.color1aMenuItem, Options.getInstance().getColoringColors()[0], false);
        this.setColorIconInPopupMenu(this.color1bMenuItem, Options.getInstance().getColoringColors()[1], false);
        this.setColorIconInPopupMenu(this.color2aMenuItem, Options.getInstance().getColoringColors()[2], false);
        this.setColorIconInPopupMenu(this.color2bMenuItem, Options.getInstance().getColoringColors()[3], false);
        this.setColorIconInPopupMenu(this.color3aMenuItem, Options.getInstance().getColoringColors()[4], false);
        this.setColorIconInPopupMenu(this.color3bMenuItem, Options.getInstance().getColoringColors()[5], false);
        this.setColorIconInPopupMenu(this.color4aMenuItem, Options.getInstance().getColoringColors()[6], false);
        this.setColorIconInPopupMenu(this.color4bMenuItem, Options.getInstance().getColoringColors()[7], false);
        this.setColorIconInPopupMenu(this.color5aMenuItem, Options.getInstance().getColoringColors()[8], false);
        this.setColorIconInPopupMenu(this.color5bMenuItem, Options.getInstance().getColoringColors()[9], false);
    }

    public void setColorkuInPopupMenu(boolean on) {
        if (on) {
            this.setColorIconInPopupMenu(this.make1MenuItem, Options.getInstance().getColorKuColor(1), true);
            this.setColorIconInPopupMenu(this.make2MenuItem, Options.getInstance().getColorKuColor(2), true);
            this.setColorIconInPopupMenu(this.make3MenuItem, Options.getInstance().getColorKuColor(3), true);
            this.setColorIconInPopupMenu(this.make4MenuItem, Options.getInstance().getColorKuColor(4), true);
            this.setColorIconInPopupMenu(this.make5MenuItem, Options.getInstance().getColorKuColor(5), true);
            this.setColorIconInPopupMenu(this.make6MenuItem, Options.getInstance().getColorKuColor(6), true);
            this.setColorIconInPopupMenu(this.make7MenuItem, Options.getInstance().getColorKuColor(7), true);
            this.setColorIconInPopupMenu(this.make8MenuItem, Options.getInstance().getColorKuColor(8), true);
            this.setColorIconInPopupMenu(this.make9MenuItem, Options.getInstance().getColorKuColor(9), true);
            this.setColorIconInPopupMenu(this.exclude1MenuItem, Options.getInstance().getColorKuColor(1), true);
            this.setColorIconInPopupMenu(this.exclude2MenuItem, Options.getInstance().getColorKuColor(2), true);
            this.setColorIconInPopupMenu(this.exclude3MenuItem, Options.getInstance().getColorKuColor(3), true);
            this.setColorIconInPopupMenu(this.exclude4MenuItem, Options.getInstance().getColorKuColor(4), true);
            this.setColorIconInPopupMenu(this.exclude5MenuItem, Options.getInstance().getColorKuColor(5), true);
            this.setColorIconInPopupMenu(this.exclude6MenuItem, Options.getInstance().getColorKuColor(6), true);
            this.setColorIconInPopupMenu(this.exclude7MenuItem, Options.getInstance().getColorKuColor(7), true);
            this.setColorIconInPopupMenu(this.exclude8MenuItem, Options.getInstance().getColorKuColor(8), true);
            this.setColorIconInPopupMenu(this.exclude9MenuItem, Options.getInstance().getColorKuColor(9), true);
            this.excludeSeveralMenuItem.setEnabled(false);
        } else {
            this.setColorIconInPopupMenu(this.make1MenuItem, null, false);
            this.setColorIconInPopupMenu(this.make2MenuItem, null, false);
            this.setColorIconInPopupMenu(this.make3MenuItem, null, false);
            this.setColorIconInPopupMenu(this.make4MenuItem, null, false);
            this.setColorIconInPopupMenu(this.make5MenuItem, null, false);
            this.setColorIconInPopupMenu(this.make6MenuItem, null, false);
            this.setColorIconInPopupMenu(this.make7MenuItem, null, false);
            this.setColorIconInPopupMenu(this.make8MenuItem, null, false);
            this.setColorIconInPopupMenu(this.make9MenuItem, null, false);
            this.setColorIconInPopupMenu(this.exclude1MenuItem, null, false);
            this.setColorIconInPopupMenu(this.exclude2MenuItem, null, false);
            this.setColorIconInPopupMenu(this.exclude3MenuItem, null, false);
            this.setColorIconInPopupMenu(this.exclude4MenuItem, null, false);
            this.setColorIconInPopupMenu(this.exclude5MenuItem, null, false);
            this.setColorIconInPopupMenu(this.exclude6MenuItem, null, false);
            this.setColorIconInPopupMenu(this.exclude7MenuItem, null, false);
            this.setColorIconInPopupMenu(this.exclude8MenuItem, null, false);
            this.setColorIconInPopupMenu(this.exclude9MenuItem, null, false);
            this.excludeSeveralMenuItem.setEnabled(true);
        }
    }

    private void setColorIconInPopupMenu(JMenuItem item, Color color, boolean colorKu) {
        if (color == null) {
            item.setIcon(null);
        } else {
            try {
                BufferedImage img = null;
                if (colorKu) {
                    img = new ColorKuImage(12, color);
                } else {
                    img = ImageIO.read(this.getClass().getResource("/img/c_icon.png"));
                    Graphics2D gImg = (Graphics2D) img.getGraphics();
                    gImg.setColor(color);
                    gImg.fillRect(1, 1, 12, 12);
                }

                item.setIcon(new ImageIcon(img));
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error setting color icons in popup menu", ex);
            }
        }
    }

    private SudokuSet collectCandidates(boolean intersection) {
        SudokuSet resultSet = new SudokuSet();
        SudokuSet tmpSet = new SudokuSet();
        if (intersection) {
            resultSet.setAll();
        }

        if (this.selectedCells.isEmpty()) {
            if (this.sudoku.getValue(this.aktLine, this.aktCol) == 0) {
                this.sudoku.getCandidateSet(this.aktLine, this.aktCol, tmpSet);
                if (intersection) {
                    resultSet.and(tmpSet);
                } else {
                    resultSet.or(tmpSet);
                }
            }
        } else {
            boolean emptyCellsOnly = true;

            for (int index : this.selectedCells) {
                if (this.sudoku.getValue(index) == 0) {
                    emptyCellsOnly = false;
                    this.sudoku.getCandidateSet(index, tmpSet);
                    if (intersection) {
                        resultSet.and(tmpSet);
                    } else {
                        resultSet.or(tmpSet);
                    }
                }
            }

            if (intersection && emptyCellsOnly) {
                resultSet.clear();
            }
        }

        return resultSet;
    }

    private void showPopupMenu(int line, int col) {
        this.jSeparator2.setVisible(true);
        if (this.sudoku.getValue(line, col) != 0 && this.selectedCells.isEmpty()) {
            if (!this.sudoku.isFixed(this.aktLine, this.aktCol)) {
                this.setAktRowCol(line, col);
                this.deleteValuePopupMenu.show(this, this.getX(line, col) + this.cellSize, this.getY(line, col));
            }
        } else {
            if (this.selectedCells.isEmpty()) {
                this.setAktRowCol(line, col);
            }

            this.excludeSeveralMenuItem.setVisible(false);

            for (int i = 1; i <= 9; i++) {
                this.makeItems[i - 1].setVisible(false);
                this.excludeItems[i - 1].setVisible(false);
            }

            SudokuSet candSet = this.collectCandidates(true);

            for (int i = 0; i < candSet.size(); i++) {
                this.makeItems[candSet.get(i) - 1].setVisible(true);
            }

            candSet = this.collectCandidates(false);
            if (candSet.size() > 1) {
                if (candSet.size() > 2) {
                    this.excludeSeveralMenuItem.setVisible(true);
                }

                for (int i = 0; i < candSet.size(); i++) {
                    this.excludeItems[candSet.get(i) - 1].setVisible(true);
                }
            } else {
                this.jSeparator2.setVisible(false);
            }

            this.cellPopupMenu.show(this, this.getX(line, col) + this.cellSize, this.getY(line, col));
        }
    }

    private void popupSetCell(JMenuItem menuItem) {
        int candidate = -1;

        for (int i = 0; i < this.makeItems.length; i++) {
            if (this.makeItems[i] == menuItem) {
                candidate = i + 1;
                break;
            }
        }

        if (candidate != -1) {
            this.undoStack.push(this.sudoku.clone());
            boolean changed = false;
            if (this.selectedCells.isEmpty()) {
                if (this.sudoku.getValue(this.aktLine, this.aktCol) == 0) {
                    this.setCell(this.aktLine, this.aktCol, candidate);
                    changed = true;
                }
            } else {
                for (int index : this.selectedCells) {
                    if (this.sudoku.getValue(index) == 0) {
                        this.sudoku.setCell(index, candidate);
                        changed = true;
                    }
                }
            }

            if (changed) {
                this.redoStack.clear();
                this.checkProgress();
            } else {
                this.undoStack.pop();
            }

            this.updateCellZoomPanel();
            this.mainFrame.check();
            this.mainFrame.fixFocus();
            this.repaint();
        }
    }

    private void popupDeleteValueFromCell() {
        this.undoStack.push(this.sudoku.clone());
        boolean changed = false;
        if (this.sudoku.getValue(this.aktLine, this.aktCol) != 0 && !this.sudoku.isFixed(this.aktLine, this.aktCol)) {
            this.sudoku.setCell(this.aktLine, this.aktCol, 0);
            changed = true;
        }

        if (changed) {
            this.redoStack.clear();
            this.checkProgress();
        } else {
            this.undoStack.pop();
        }

        this.updateCellZoomPanel();
        this.mainFrame.fixFocus();
        this.mainFrame.check();
        this.repaint();
    }

    private boolean removeCandidateFromActiveCells(int candidate) {
        boolean changed = false;
        if (this.selectedCells.isEmpty()) {
            int index = Sudoku2.getIndex(this.aktLine, this.aktCol);
            if (this.sudoku.getValue(index) == 0 && this.sudoku.isCandidate(index, candidate, !this.showCandidates)) {
                this.sudoku.setCandidate(index, candidate, false, !this.showCandidates);
                changed = true;
            }
        } else {
            for (int index : this.selectedCells) {
                if (this.sudoku.getValue(index) == 0 && this.sudoku.isCandidate(index, candidate, !this.showCandidates)) {
                    this.sudoku.setCandidate(index, candidate, false, !this.showCandidates);
                    changed = true;
                }
            }
        }

        return changed;
    }

    public void toggleOrRemoveCandidateFromCellZoomPanel(int candidate) {
        if (candidate != -1) {
            this.undoStack.push(this.sudoku.clone());
            boolean changed = false;
            if (this.selectedCells.isEmpty()) {
                int index = Sudoku2.getIndex(this.aktLine, this.aktCol);
                if (this.sudoku.isCandidate(index, candidate, !this.showCandidates)) {
                    this.sudoku.setCandidate(index, candidate, false, !this.showCandidates);
                } else {
                    this.sudoku.setCandidate(index, candidate, true, !this.showCandidates);
                }

                changed = true;
            } else {
                changed = this.removeCandidateFromActiveCells(candidate);
            }

            if (changed) {
                this.redoStack.clear();
                this.checkProgress();
            } else {
                this.undoStack.pop();
            }

            this.updateCellZoomPanel();
            this.mainFrame.check();
            this.repaint();
        }
    }

    private void popupExcludeCandidate(JMenuItem menuItem) {
        int candidate = -1;

        for (int i = 0; i < this.excludeItems.length; i++) {
            if (this.excludeItems[i] == menuItem) {
                candidate = i + 1;
                break;
            }
        }

        if (candidate != -1) {
            this.undoStack.push(this.sudoku.clone());
            boolean changed = this.removeCandidateFromActiveCells(candidate);
            if (changed) {
                this.redoStack.clear();
                this.checkProgress();
            } else {
                this.undoStack.pop();
            }

            this.updateCellZoomPanel();
            this.mainFrame.check();
            this.mainFrame.fixFocus();
            this.repaint();
        }
    }

    private void popupToggleColor(JMenuItem menuItem) {
        int color = -1;

        for (int i = 0; i < this.toggleColorItems.length; i++) {
            if (this.toggleColorItems[i] == menuItem) {
                color = i;
                break;
            }
        }

        if (color != -1) {
            this.handleColoring(this.aktLine, this.aktCol, -1, color);
            this.updateCellZoomPanel();
            this.mainFrame.check();
            this.repaint();
        }
    }

    public CellZoomPanel getCellZoomPanel() {
        return this.cellZoomPanel;
    }

    public void setCellZoomPanel(CellZoomPanel cellZoomPanel) {
        this.cellZoomPanel = cellZoomPanel;
    }

    private void updateCellZoomPanel() {
        if (this.cellZoomPanel != null) {
            int index = Sudoku2.getIndex(this.aktLine, this.aktCol);
            boolean singleCell = this.selectedCells.isEmpty() && this.sudoku.getValue(index) == 0;
            if (this.aktColorIndex == -1) {
                if (this.sudoku.getValue(index) != 0 && this.selectedCells.isEmpty()) {
                    this.cellZoomPanel.update(SudokuSetBase.EMPTY_SET, SudokuSetBase.EMPTY_SET, -1, index, false, singleCell, null, null);
                } else {
                    SudokuSet valueSet = this.collectCandidates(true);
                    SudokuSet candSet = this.collectCandidates(false);
                    this.cellZoomPanel.update(valueSet, candSet, -1, index, false, singleCell, null, null);
                }
            } else if (this.selectedCells.isEmpty() && (!this.selectedCells.isEmpty() || this.sudoku.getValue(index) == 0)) {
                SudokuSet valueSet = this.collectCandidates(true);
                SudokuSet candSet = this.collectCandidates(false);
                this.cellZoomPanel.update(valueSet, candSet, this.aktColorIndex, index, this.colorCells, singleCell, this.coloringMap, this.coloringCandidateMap);
            } else {
                this.cellZoomPanel.update(SudokuSetBase.EMPTY_SET, SudokuSetBase.EMPTY_SET, this.aktColorIndex, index, this.colorCells, singleCell, null, null);
            }
        }
    }

    public void setGivens(String givens) {
        this.undoStack.push(this.sudoku.clone());
        this.sudoku.setGivens(givens);
        this.updateCellZoomPanel();
        this.repaint();
        this.mainFrame.check();
    }

    public void checkProgress() {
        int anz = this.sudoku.getSolvedCellsAnz();
        if (anz == 0) {
            this.sudoku.setStatus(SudokuStatus.EMPTY);
            this.sudoku.setStatusGivens(SudokuStatus.EMPTY);
        } else if (anz <= 17) {
            this.sudoku.setStatus(SudokuStatus.INVALID);
            this.sudoku.setStatusGivens(SudokuStatus.INVALID);
        } else {
            int anzSol = this.generator.getNumberOfSolutions(this.sudoku);
            this.sudoku.setStatus(anzSol);
            if (anzSol == 1) {
                this.progressChecker.startCheck(this.sudoku);
            }
        }
    }

    private int getShowHintCellValue() {
        int value = 0;

        for (int i = 1; i < this.showHintCellValues.length - 1; i++) {
            if (this.showHintCellValues[i]) {
                if (value != 0) {
                    return 0;
                }

                value = i;
            }
        }

        return value;
    }

    public void setShowHintCellValue(int candidate) {
        if (candidate == 10) {
            for (int i = 0; i < this.showHintCellValues.length - 1; i++) {
                this.showHintCellValues[i] = false;
            }

            this.showHintCellValues[10] = !this.showHintCellValues[10];
        } else {
            this.showHintCellValues[10] = false;

            for (int i = 0; i < this.showHintCellValues.length - 1; i++) {
                if (i == candidate) {
                    this.showHintCellValues[i] = !this.showHintCellValues[i];
                } else {
                    this.showHintCellValues[i] = false;
                }
            }
        }
    }

    public void checkIsShowInvalidOrPossibleCells() {
        this.showInvalidOrPossibleCells = false;

        for (int i = 1; i < this.showHintCellValues.length; i++) {
            if (this.showHintCellValues[i]) {
                this.showInvalidOrPossibleCells = true;
            }
        }
    }

    public void setShowColorKu() {
        this.setColorkuInPopupMenu(Options.getInstance().isShowColorKuAct());
        this.cellZoomPanel.calculateLayout();
        this.updateCellZoomPanel();
        this.repaint();
    }

    public void resetColorKuImages() {
        for (int i = 0; i < colorKuImagesLarge.length; i++) {
            colorKuImagesLarge[i] = null;
            colorKuImagesSmall[i] = null;
        }
    }

    private void drawColorBox(int n, Graphics gc, int cx, int cy, int boxSize, boolean large) {
        BufferedImage[] images = null;
        if (large) {
            images = colorKuImagesLarge;
        } else {
            images = colorKuImagesSmall;
        }

        if (images[0] == null || images[0].getWidth() != boxSize) {
            for (int i = 0; i < images.length; i++) {
                images[i] = new ColorKuImage(boxSize, Options.getInstance().getColorKuColor(i + 1));
            }
        }

        gc.drawImage(images[n - 1], cx, cy, null);
    }

    public boolean[] getRemainingCandidates() {
        for (int i = 0; i < this.remainingCandidates.length; i++) {
            this.remainingCandidates[i] = false;
        }

        if (this.isShowCandidates()) {
            int[] cands = Sudoku2.POSSIBLE_VALUES[this.sudoku.getRemainingCandidates()];

            for (int i = 0; i < cands.length; i++) {
                this.remainingCandidates[cands[i] - 1] = true;
            }
        }

        return this.remainingCandidates;
    }
}
