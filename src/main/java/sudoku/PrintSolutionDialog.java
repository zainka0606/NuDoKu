package sudoku;

import solver.SudokuSolver;
import solver.SudokuSolverFactory;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PrintSolutionDialog extends JDialog implements Printable {
    private static final double LINE_HEIGHT = 1.2;
    private static final long serialVersionUID = 1L;
    private SolutionStep[] steps = null;
    private boolean[] selected = null;
    private String initialState = "";
    private PageFormat pageFormat = null;
    private PrinterJob job = null;
    private int imageSize = 0;
    private Font bigFont;
    private Font smallFont;
    private int imagePrintSize;
    private SudokuPanel panel;
    private Sudoku2 sudoku;
    private Sudoku2 oldSudoku;
    private SudokuSolver solver;
    private int printIndex;
    private boolean compressedStarted;
    private boolean isSingles;
    private int lastPageSeen = -1;
    private String pageEntryState;
    private int printIndexES;
    private boolean compressedStartedES;
    private boolean isSinglesES;
    private boolean printDone;
    private JButton closeButton;
    private JCheckBox compressSSTSCheckBox;
    private JCheckBox compressSinglesCheckBox;
    private JButton copyButton;
    private JScrollPane jScrollPane1;
    private JPanel optionPanel;
    private JButton pageOptionsButton;
    private JButton printButton;
    private JList stepList;
    private JPanel stepsPanel;
    private JLabel titleLabel;
    private JTextField titleTextField;
    private JLabel widthLabel;
    private JTextField widthTextField;

    public PrintSolutionDialog(Frame parent, boolean modal, List<SolutionStep> stepsAsList, String initialState) {
        super(parent, modal);
        this.initComponents();
        this.getRootPane().setDefaultButton(this.printButton);
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(27, 0, false);
        Action escapeAction = new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                PrintSolutionDialog.this.setVisible(false);
            }
        };
        this.getRootPane().getInputMap(2).put(escapeKeyStroke, "ESCAPE");
        this.getRootPane().getActionMap().put("ESCAPE", escapeAction);
        this.steps = new SolutionStep[stepsAsList.size()];
        this.selected = new boolean[this.steps.length];

        for (int i = 0; i < this.steps.length; i++) {
            this.steps[i] = stepsAsList.get(i);
        }

        this.initialState = initialState;
        this.stepList.setSelectionMode(0);
        this.stepList.setCellRenderer(new PrintSolutionDialog.CheckBoxRenderer());
        this.stepList.setListData(this.steps);
        NumbersOnlyDocument doc = new NumbersOnlyDocument();
        this.widthTextField.setDocument(doc);
        this.widthTextField.setText("80");
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                PrintSolutionDialog dialog = new PrintSolutionDialog(new JFrame(), true, null, null);
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
        this.optionPanel = new JPanel();
        this.widthLabel = new JLabel();
        this.widthTextField = new JTextField();
        this.compressSinglesCheckBox = new JCheckBox();
        this.compressSSTSCheckBox = new JCheckBox();
        this.titleLabel = new JLabel();
        this.titleTextField = new JTextField();
        this.stepsPanel = new JPanel();
        this.jScrollPane1 = new JScrollPane();
        this.stepList = new JList();
        this.printButton = new JButton();
        this.copyButton = new JButton();
        this.pageOptionsButton = new JButton();
        this.closeButton = new JButton();
        this.setDefaultCloseOperation(2);
        ResourceBundle bundle = ResourceBundle.getBundle("intl/PrintSolutionDialog");
        this.setTitle(bundle.getString("PrintSolutionDialog.title"));
        this.optionPanel.setBorder(BorderFactory.createTitledBorder(bundle.getString("optionPanel.border.txt")));
        this.widthLabel.setDisplayedMnemonic(ResourceBundle.getBundle("intl/PrintSolutionDialog").getString("PrintSolutionDialog.widthLabel.mnemonic").charAt(0));
        this.widthLabel.setLabelFor(this.widthTextField);
        this.widthLabel.setText(bundle.getString("PrintSolutionDialog.widthLabel.text"));
        this.widthTextField.setText(bundle.getString("PrintSolutionDialog.widthTextField.text"));
        this.compressSinglesCheckBox
                .setMnemonic(ResourceBundle.getBundle("intl/PrintSolutionDialog").getString("PrintSolutionDialog.compressSinglesCheckBox.mnemonic").charAt(0));
        this.compressSinglesCheckBox.setText(bundle.getString("PrintSolutionDialog.compressSinglesCheckBox.text"));
        this.compressSinglesCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                PrintSolutionDialog.this.compressSinglesCheckBoxActionPerformed(evt);
            }
        });
        this.compressSSTSCheckBox
                .setMnemonic(ResourceBundle.getBundle("intl/PrintSolutionDialog").getString("PrintSolutionDialog.compressSSTSCheckBox.mnemonic").charAt(0));
        this.compressSSTSCheckBox.setText(bundle.getString("PrintSolutionDialog.compressSSTSCheckBox.text"));
        this.compressSSTSCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                PrintSolutionDialog.this.compressSSTSCheckBoxActionPerformed(evt);
            }
        });
        this.titleLabel.setDisplayedMnemonic(ResourceBundle.getBundle("intl/PrintSolutionDialog").getString("PrintSolutionDialog.titleLabel.mnemonic").charAt(0));
        this.titleLabel.setLabelFor(this.titleTextField);
        this.titleLabel.setText(bundle.getString("PrintSolutionDialog.titleLabel.text"));
        this.titleTextField.setText(bundle.getString("PrintSolutionDialog.titleTextField.text"));
        GroupLayout optionPanelLayout = new GroupLayout(this.optionPanel);
        this.optionPanel.setLayout(optionPanelLayout);
        optionPanelLayout.setHorizontalGroup(
                optionPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                optionPanelLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(optionPanelLayout.createParallelGroup(Alignment.LEADING).addComponent(this.titleLabel).addComponent(this.widthLabel))
                                        .addGap(26, 26, 26)
                                        .addGroup(
                                                optionPanelLayout.createParallelGroup(Alignment.LEADING)
                                                        .addGroup(
                                                                optionPanelLayout.createSequentialGroup()
                                                                        .addComponent(this.widthTextField, -2, 61, -2)
                                                                        .addGap(18, 18, 18)
                                                                        .addComponent(this.compressSinglesCheckBox)
                                                        )
                                                        .addGroup(optionPanelLayout.createSequentialGroup().addGap(79, 79, 79).addComponent(this.compressSSTSCheckBox))
                                                        .addComponent(this.titleTextField, -1, 344, 32767)
                                        )
                                        .addContainerGap()
                        )
        );
        optionPanelLayout.setVerticalGroup(
                optionPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                optionPanelLayout.createSequentialGroup()
                                        .addGroup(
                                                optionPanelLayout.createParallelGroup(Alignment.BASELINE).addComponent(this.titleLabel).addComponent(this.titleTextField, -2, -1, -2)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                optionPanelLayout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(this.widthLabel)
                                                        .addComponent(this.widthTextField, -2, -1, -2)
                                                        .addComponent(this.compressSinglesCheckBox)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.compressSSTSCheckBox)
                                        .addContainerGap(-1, 32767)
                        )
        );
        this.stepsPanel.setBorder(BorderFactory.createTitledBorder(bundle.getString("stepsPanel.border.text")));
        this.stepList.setModel(new AbstractListModel() {
            String[] strings = new String[]{"Item 1", "Item 2", "Item 3", "Item 4", "Item 5"};

            @Override
            public int getSize() {
                return this.strings.length;
            }

            @Override
            public Object getElementAt(int i) {
                return this.strings[i];
            }
        });
        this.stepList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                PrintSolutionDialog.this.stepListMouseClicked(evt);
            }
        });
        this.jScrollPane1.setViewportView(this.stepList);
        GroupLayout stepsPanelLayout = new GroupLayout(this.stepsPanel);
        this.stepsPanel.setLayout(stepsPanelLayout);
        stepsPanelLayout.setHorizontalGroup(
                stepsPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(stepsPanelLayout.createSequentialGroup().addContainerGap().addComponent(this.jScrollPane1, -1, 402, 32767).addContainerGap())
        );
        stepsPanelLayout.setVerticalGroup(
                stepsPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(stepsPanelLayout.createSequentialGroup().addComponent(this.jScrollPane1, -1, 315, 32767).addContainerGap())
        );
        this.printButton.setMnemonic(ResourceBundle.getBundle("intl/PrintSolutionDialog").getString("PrintSolutionDialog.printButton.mnemonic").charAt(0));
        this.printButton.setText(bundle.getString("PrintSolutionDialog.printButton.text"));
        this.printButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                PrintSolutionDialog.this.printButtonActionPerformed(evt);
            }
        });
        this.copyButton.setMnemonic(ResourceBundle.getBundle("intl/PrintSolutionDialog").getString("PrintSolutionDialog.copyButton.mnemonic").charAt(0));
        this.copyButton.setText(bundle.getString("PrintSolutionDialog.copyButton.text"));
        this.copyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                PrintSolutionDialog.this.copyButtonActionPerformed(evt);
            }
        });
        this.pageOptionsButton
                .setMnemonic(ResourceBundle.getBundle("intl/PrintSolutionDialog").getString("PrintSolutionDialog.pageOptionsButton.mnemonic").charAt(0));
        this.pageOptionsButton.setText(bundle.getString("PrintSolutionDialog.pageOptionsButton.text"));
        this.pageOptionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                PrintSolutionDialog.this.pageOptionsButtonActionPerformed(evt);
            }
        });
        this.closeButton.setMnemonic(ResourceBundle.getBundle("intl/PrintSolutionDialog").getString("PrintSolutionDialog.closeButton.mnemonic").charAt(0));
        this.closeButton.setText(bundle.getString("PrintSolutionDialog.closeButton.text"));
        this.closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                PrintSolutionDialog.this.closeButtonActionPerformed(evt);
            }
        });
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
                                                        .addComponent(this.stepsPanel, Alignment.LEADING, -1, -1, 32767)
                                                        .addComponent(this.optionPanel, Alignment.LEADING, -1, -1, 32767)
                                                        .addGroup(
                                                                layout.createSequentialGroup()
                                                                        .addComponent(this.printButton)
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addComponent(this.copyButton)
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addComponent(this.pageOptionsButton)
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addComponent(this.closeButton)
                                                        )
                                        )
                                        .addContainerGap()
                        )
        );
        layout.linkSize(0, this.closeButton, this.copyButton, this.printButton);
        layout.setVerticalGroup(
                layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                Alignment.TRAILING,
                                layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(this.optionPanel, -2, -1, -2)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.stepsPanel, -1, -1, 32767)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                layout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(this.closeButton)
                                                        .addComponent(this.pageOptionsButton)
                                                        .addComponent(this.copyButton)
                                                        .addComponent(this.printButton)
                                        )
                                        .addContainerGap()
                        )
        );
        this.pack();
    }

    private void stepListMouseClicked(MouseEvent evt) {
        int index = this.stepList.locationToIndex(evt.getPoint());
        if (index == this.stepList.getSelectedIndex()) {
            if ((!this.compressSinglesCheckBox.isSelected() || !SolutionType.isSingle(this.steps[index].getType()))
                    && (!this.compressSSTSCheckBox.isSelected() || !SolutionType.isSSTS(this.steps[index].getType()))) {
                this.selected[index] = !this.selected[index];
            }

            this.stepList.repaint();
        }
    }

    private void compressSinglesCheckBoxActionPerformed(ActionEvent evt) {
        this.stepList.repaint();
    }

    private void compressSSTSCheckBoxActionPerformed(ActionEvent evt) {
        if (this.compressSSTSCheckBox.isSelected()) {
            this.compressSinglesCheckBox.setEnabled(false);
        } else {
            this.compressSinglesCheckBox.setEnabled(true);
        }

        this.stepList.repaint();
    }

    private void closeButtonActionPerformed(ActionEvent evt) {
        this.setVisible(false);
    }

    private void copyButtonActionPerformed(ActionEvent evt) {
        this.copyToClipboard();
    }

    private void pageOptionsButtonActionPerformed(ActionEvent evt) {
        if (this.job == null) {
            this.job = PrinterJob.getPrinterJob();
        }

        if (this.pageFormat == null) {
            this.pageFormat = this.job.defaultPage();
        }

        this.pageFormat = this.job.pageDialog(this.pageFormat);
    }

    private void printButtonActionPerformed(ActionEvent evt) {
        this.imageSize = 80;

        try {
            this.imageSize = Integer.parseInt(this.widthTextField.getText());
        } catch (NumberFormatException ex) {
        }

        if (this.job == null) {
            this.job = PrinterJob.getPrinterJob();
        }

        if (this.pageFormat == null) {
            this.pageFormat = this.job.defaultPage();
        }

        try {
            this.job.setPrintable(this, this.pageFormat);
            if (this.job.printDialog()) {
                this.lastPageSeen = -1;
                this.printDone = false;
                this.job.print();
            }
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(this, ex.toString(), ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.error"), 0);
        }
    }

    private void copyToClipboard() {
        StringWriter writer = new StringWriter();
        PrintWriter out = new PrintWriter(new BufferedWriter(writer));
        String title = this.titleTextField.getText();
        if (!title.isEmpty()) {
            out.println(title);
            out.println();
        }

        this.sudoku = new Sudoku2();
        this.sudoku.setSudoku(this.initialState);
        this.solver = SudokuSolverFactory.getDefaultSolverInstance();
        this.oldSudoku = this.solver.getSudoku();
        this.solver.setSudoku(this.sudoku);
        out.println(ResourceBundle.getBundle("intl/PrintSolutionDialog").getString("PrintSolutionDialog.givens"));
        out.println(this.sudoku.getSudoku(ClipboardMode.CLUES_ONLY));
        out.println();
        out.println(ResourceBundle.getBundle("intl/PrintSolutionDialog").getString("PrintSolutionDialog.initialState"));
        out.println(this.sudoku.getSudoku(ClipboardMode.PM_GRID));
        out.println();
        this.compressedStarted = false;
        this.isSingles = true;

        for (int i = 0; i < this.steps.length; i++) {
            if (this.compressSinglesCheckBox.isSelected() && SolutionType.isSingle(this.steps[i].getType())
                    || this.compressSSTSCheckBox.isSelected() && SolutionType.isSSTS(this.steps[i].getType())) {
                if (!this.compressedStarted) {
                    this.compressedStarted = true;
                    this.isSingles = true;
                }

                if (!SolutionType.isSingle(this.steps[i].getType())) {
                    this.isSingles = false;
                }
            } else {
                if (this.compressedStarted) {
                    out.println();
                    if (this.isSingles) {
                        out.println(ResourceBundle.getBundle("intl/PrintSolutionDialog").getString("PrintSolutionDialog.singlesTo"));
                    } else {
                        out.println(ResourceBundle.getBundle("intl/PrintSolutionDialog").getString("PrintSolutionDialog.sstsTo"));
                    }

                    out.println(this.sudoku.getSudoku(ClipboardMode.PM_GRID));
                    this.compressedStarted = false;
                }

                if (this.selected[i]) {
                    out.println();
                    out.println(this.sudoku.getSudoku(ClipboardMode.PM_GRID_WITH_STEP, this.steps[i]));
                    out.println();
                } else {
                    out.println(this.steps[i].toString(2));
                }
            }

            this.solver.doStep(this.sudoku, this.steps[i]);
        }

        if (this.compressedStarted) {
            out.println();
            if (this.isSingles) {
                out.println(ResourceBundle.getBundle("intl/PrintSolutionDialog").getString("PrintSolutionDialog.singlesTo"));
            } else {
                out.println(ResourceBundle.getBundle("intl/PrintSolutionDialog").getString("PrintSolutionDialog.sstsTo"));
            }

            this.compressedStarted = false;
        }

        out.println();
        out.println(ResourceBundle.getBundle("intl/PrintSolutionDialog").getString("PrintSolutionDialog.solution"));
        out.println(this.sudoku.getSudoku(ClipboardMode.PM_GRID));
        out.flush();

        try {
            Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection content = new StringSelection(writer.toString());
            clip.setContents(content, null);
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error writing to clipboard", ex);
        }

        this.solver.setSudoku(this.oldSudoku);
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex > 0) {
            if (pageIndex == this.lastPageSeen) {
                this.panel.setSudoku(this.pageEntryState, true);
                this.printIndex = this.printIndexES;
                this.compressedStarted = this.compressedStartedES;
                this.isSingles = this.isSinglesES;
                this.printDone = false;
            } else {
                this.printIndexES = this.printIndex;
                this.compressedStartedES = this.compressedStarted;
                this.isSinglesES = this.isSingles;
                this.pageEntryState = this.sudoku.getSudoku(ClipboardMode.LIBRARY);
            }
        }

        this.lastPageSeen = pageIndex;
        if (this.printDone) {
            this.solver.setSudoku(this.oldSudoku);
            return 1;
        }

        Graphics2D printG2 = (Graphics2D) graphics;
        double scale = SudokuUtil.adjustGraphicsForPrinting(printG2);
        int resolution = (int) (72.0 * scale);
        printG2.translate((int) (pageFormat.getImageableX() * scale), (int) (pageFormat.getImageableY() * scale));
        int printWidth = (int) (pageFormat.getImageableWidth() * scale);
        int printHeight = (int) (pageFormat.getImageableHeight() * scale);
        int y = 0;
        if (pageIndex == 0) {
            Font tmpFont = Options.getInstance().getBigFont();
            this.bigFont = new Font(tmpFont.getName(), tmpFont.getStyle(), (int) (tmpFont.getSize() * scale));
            tmpFont = Options.getInstance().getSmallFont();
            this.smallFont = new Font(tmpFont.getName(), tmpFont.getStyle(), (int) (tmpFont.getSize() * scale));
            this.imagePrintSize = (int) (this.imageSize * resolution / 25.4);
            this.panel = new SudokuPanel(null);
            this.panel.setSudoku(this.initialState, true);
            this.sudoku = this.panel.getSudoku();
            this.solver = this.panel.getSolver();
            this.oldSudoku = this.solver.getSudoku();
            this.solver.setSudoku(this.sudoku);
            this.printIndex = 0;
            this.compressedStarted = false;
            printG2.setFont(this.bigFont);
            FontMetrics metrics = printG2.getFontMetrics();
            String title = this.titleTextField.getText();
            if (!title.isEmpty()) {
                int textWidth = metrics.stringWidth(title);
                int textHeight = metrics.getHeight();
                y = (int) (1.2 * textHeight);
                printG2.drawString(title, (printWidth - textWidth) / 2, textHeight);
            }

            printG2.setFont(this.smallFont);
            metrics = printG2.getFontMetrics();
            int lineHeight = (int) (1.2 * metrics.getHeight());
            y += lineHeight;
            printG2.drawString(ResourceBundle.getBundle("intl/PrintSolutionDialog").getString("PrintSolutionDialog.givens"), 0, y);
            String givens = this.sudoku.getSudoku(ClipboardMode.CLUES_ONLY);
            y += lineHeight;
            printG2.drawString(givens, 0, y);
            y += lineHeight;
            printG2.drawString(ResourceBundle.getBundle("intl/PrintSolutionDialog").getString("PrintSolutionDialog.initialState"), 0, y);
            this.panel.setStep(null);
            BufferedImage img = this.panel.getSudokuImage(this.imagePrintSize);
            y += lineHeight / 2;
            printG2.drawImage(img, null, 0, y);
            y += this.imagePrintSize + lineHeight / 2;
        }

        printG2.setFont(this.smallFont);
        FontMetrics metrics = printG2.getFontMetrics();
        int lineHeight = (int) (1.2 * metrics.getHeight());

        while (this.printIndex < this.steps.length) {
            if (this.compressSinglesCheckBox.isSelected() && SolutionType.isSingle(this.steps[this.printIndex].getType())
                    || this.compressSSTSCheckBox.isSelected() && SolutionType.isSSTS(this.steps[this.printIndex].getType())) {
                if (!this.compressedStarted) {
                    this.compressedStarted = true;
                    this.isSingles = true;
                }

                if (!SolutionType.isSingle(this.steps[this.printIndex].getType())) {
                    this.isSingles = false;
                }
            } else {
                if (this.compressedStarted) {
                    if (y + 3 * lineHeight + this.imagePrintSize > printHeight) {
                        return 0;
                    }

                    y += lineHeight;
                    y += lineHeight / 2;
                    if (this.isSingles) {
                        printG2.drawString(ResourceBundle.getBundle("intl/PrintSolutionDialog").getString("PrintSolutionDialog.singlesTo"), 0, y);
                    } else {
                        printG2.drawString(ResourceBundle.getBundle("intl/PrintSolutionDialog").getString("PrintSolutionDialog.sstsTo"), 0, y);
                    }

                    this.panel.setStep(null);
                    BufferedImage img = this.panel.getSudokuImage(this.imagePrintSize);
                    y += lineHeight / 2;
                    printG2.drawImage(img, null, 0, y);
                    y += this.imagePrintSize + lineHeight;
                    this.compressedStarted = false;
                }

                if (this.selected[this.printIndex]) {
                    if (y + 2 * lineHeight + this.imagePrintSize > printHeight) {
                        return 0;
                    }

                    this.panel.setStep(this.steps[this.printIndex]);
                    BufferedImage img = this.panel.getSudokuImage(this.imagePrintSize);
                    y += lineHeight / 2;
                    printG2.drawImage(img, null, 0, y);
                    y += this.imagePrintSize + lineHeight / 2;
                    y += lineHeight / 2;
                    printG2.drawString(this.steps[this.printIndex].toString(2), 0, y);
                    y += lineHeight / 2;
                } else {
                    if (y + lineHeight > printHeight) {
                        return 0;
                    }

                    y += lineHeight;
                    printG2.drawString(this.steps[this.printIndex].toString(2), 0, y);
                }
            }

            this.solver.doStep(this.sudoku, this.steps[this.printIndex]);
            this.printIndex++;
        }

        if (this.compressedStarted) {
            if (y + 1.5 * lineHeight > printHeight) {
                return 0;
            }

            y += lineHeight;
            y += lineHeight / 2;
            if (this.isSingles) {
                printG2.drawString(ResourceBundle.getBundle("intl/PrintSolutionDialog").getString("PrintSolutionDialog.singlesToEnd"), 0, y);
            } else {
                printG2.drawString(ResourceBundle.getBundle("intl/PrintSolutionDialog").getString("PrintSolutionDialog.sstsToEnd"), 0, y);
            }

            this.compressedStarted = false;
        }

        if (y + 2.5 * lineHeight + this.imagePrintSize > printHeight) {
            return 0;
        }

        y += lineHeight;
        y += lineHeight / 2;
        printG2.drawString(ResourceBundle.getBundle("intl/PrintSolutionDialog").getString("PrintSolutionDialog.solution"), 0, y);
        this.panel.setStep(null);
        BufferedImage img = this.panel.getSudokuImage(this.imagePrintSize);
        y += lineHeight / 2;
        printG2.drawImage(img, null, 0, y);
        this.printDone = true;
        return 0;
    }

    class CheckBoxRenderer extends JCheckBox implements ListCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getListCellRendererComponent(JList listBox, Object obj, int index, boolean isSelected, boolean hasFocus) {
            SolutionStep step = (SolutionStep) obj;
            this.setText(step.toString(2));
            this.setSelected(PrintSolutionDialog.this.selected[index]);
            if ((!PrintSolutionDialog.this.compressSinglesCheckBox.isSelected() || !SolutionType.isSingle(step.getType()))
                    && (!PrintSolutionDialog.this.compressSSTSCheckBox.isSelected() || !SolutionType.isSSTS(step.getType()))) {
                this.setEnabled(true);
            } else {
                this.setEnabled(false);
            }

            Color fg = Options.getInstance().getDifficultyLevels()[SolutionType.getStepConfig(step.getType()).getLevel()].getForegroundColor();
            if (isSelected) {
                fg = UIManager.getColor("List.selectionForeground");
                if (fg == null) {
                    fg = UIManager.getColor("List[Selected].textForeground");
                }

                if (fg == null) {
                    fg = Color.BLACK;
                }
            }

            if (!this.isEnabled()) {
                fg = UIManager.getColor("Button.disabledForeground");
                if (fg == null) {
                    fg = UIManager.getColor("Button[Disabled].textForeground");
                }

                if (fg == null) {
                    fg = Color.DARK_GRAY;
                }
            }

            if (isSelected) {
                Color bg = UIManager.getColor("List.selectionBackground");
                if (bg == null) {
                    bg = UIManager.getColor("List[Selected].textBackground");
                }

                if (bg == null) {
                    bg = Color.BLUE;
                }

                this.setBackground(bg);
                this.setForeground(fg);
            } else {
                this.setBackground(Options.getInstance().getDifficultyLevels()[SolutionType.getStepConfig(step.getType()).getLevel()].getBackgroundColor());
                this.setForeground(fg);
            }

            return this;
        }
    }
}
