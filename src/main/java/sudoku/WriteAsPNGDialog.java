package sudoku;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ResourceBundle;

public class WriteAsPNGDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private File bildFile;
    private int aufloesung;
    private double bildSize;
    private int einheit;
    private boolean ok = false;
    private JRadioButton[] einheiten;
    private JButton abbrechenButton;
    private JButton bildSpeichernButton;
    private ButtonGroup einheitButtonGroup;
    private JPanel einheitPanel;
    private JRadioButton inchRadioButton;
    private JRadioButton mmRadioButton;
    private JRadioButton pixelRadioButton;
    private JLabel resolutionLabel;
    private JTextField resolutionTextField;
    private JLabel sizeLabel;
    private JPanel sizePanel;
    private JTextField sizeTextField;

    public WriteAsPNGDialog(Frame parent, boolean modal, double size, int aufloesung, int einheit) {
        super(parent, modal);
        this.initComponents();
        this.aufloesung = aufloesung;
        this.bildSize = size;
        this.einheit = einheit;
        this.bildFile = new File(Options.getInstance().getDefaultImageDir());
        this.einheiten = new JRadioButton[3];
        this.einheiten[0] = this.mmRadioButton;
        this.einheiten[1] = this.inchRadioButton;
        this.einheiten[2] = this.pixelRadioButton;
        this.resolutionTextField.setText(Integer.toString(aufloesung));
        this.sizeTextField.setText(Double.toString(size));
        this.einheiten[einheit].setSelected(true);
        this.getRootPane().setDefaultButton(this.bildSpeichernButton);
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(27, 0, false);
        Action escapeAction = new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                WriteAsPNGDialog.this.setVisible(false);
            }
        };
        this.getRootPane().getInputMap(2).put(escapeKeyStroke, "ESCAPE");
        this.getRootPane().getActionMap().put("ESCAPE", escapeAction);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new WriteAsPNGDialog(new JFrame(), true, 0.0, 0, 0).setVisible(true);
            }
        });
    }

    private void initComponents() {
        this.einheitButtonGroup = new ButtonGroup();
        this.sizePanel = new JPanel();
        this.sizeLabel = new JLabel();
        this.resolutionLabel = new JLabel();
        this.sizeTextField = new JTextField();
        this.resolutionTextField = new JTextField();
        this.einheitPanel = new JPanel();
        this.mmRadioButton = new JRadioButton();
        this.inchRadioButton = new JRadioButton();
        this.pixelRadioButton = new JRadioButton();
        this.bildSpeichernButton = new JButton();
        this.abbrechenButton = new JButton();
        this.setDefaultCloseOperation(2);
        ResourceBundle bundle = ResourceBundle.getBundle("intl/WriteAsPNGDialog");
        this.setTitle(bundle.getString("WriteAsPNGDialog.title"));
        this.sizePanel.setBorder(BorderFactory.createTitledBorder(bundle.getString("WriteAsPNGDialog.sizePanel.border.title")));
        this.sizeLabel.setDisplayedMnemonic(ResourceBundle.getBundle("intl/WriteAsPNGDialog").getString("WriteAsPNGDialog.sizeLabel.mnemonic").charAt(0));
        this.sizeLabel.setLabelFor(this.sizeTextField);
        this.sizeLabel.setText(bundle.getString("WriteAsPNGDialog.sizeLabel.text"));
        this.resolutionLabel
                .setDisplayedMnemonic(ResourceBundle.getBundle("intl/WriteAsPNGDialog").getString("WriteAsPNGDialog.resolutionLabel.mnemonic").charAt(0));
        this.resolutionLabel.setLabelFor(this.resolutionTextField);
        this.resolutionLabel.setText(bundle.getString("WriteAsPNGDialog.resolutionLabel.text"));
        GroupLayout sizePanelLayout = new GroupLayout(this.sizePanel);
        this.sizePanel.setLayout(sizePanelLayout);
        sizePanelLayout.setHorizontalGroup(
                sizePanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                sizePanelLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(sizePanelLayout.createParallelGroup(Alignment.LEADING).addComponent(this.sizeLabel).addComponent(this.resolutionLabel))
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                sizePanelLayout.createParallelGroup(Alignment.LEADING)
                                                        .addComponent(this.resolutionTextField, -1, 174, 32767)
                                                        .addComponent(this.sizeTextField, -1, 174, 32767)
                                        )
                                        .addContainerGap()
                        )
        );
        sizePanelLayout.setVerticalGroup(
                sizePanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                sizePanelLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                sizePanelLayout.createParallelGroup(Alignment.BASELINE, false).addComponent(this.sizeLabel).addComponent(this.sizeTextField, -2, -1, -2)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                sizePanelLayout.createParallelGroup(Alignment.BASELINE, false)
                                                        .addComponent(this.resolutionLabel)
                                                        .addComponent(this.resolutionTextField, -2, -1, -2)
                                        )
                                        .addContainerGap()
                        )
        );
        this.einheitPanel.setBorder(BorderFactory.createTitledBorder(bundle.getString("WriteAsPNGDialog.einheitPanel.border.title")));
        this.einheitButtonGroup.add(this.mmRadioButton);
        this.mmRadioButton.setMnemonic(ResourceBundle.getBundle("intl/WriteAsPNGDialog").getString("WriteAsPNGDialog.mmRadioButton.mnemonic").charAt(0));
        this.mmRadioButton.setSelected(true);
        this.mmRadioButton.setText(bundle.getString("WriteAsPNGDialog.mmRadioButton.text"));
        this.mmRadioButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        this.mmRadioButton.setMargin(new Insets(0, 0, 0, 0));
        this.einheitButtonGroup.add(this.inchRadioButton);
        this.inchRadioButton.setMnemonic(ResourceBundle.getBundle("intl/WriteAsPNGDialog").getString("WriteAsPNGDialog.inchRadioButton.mnemonic").charAt(0));
        this.inchRadioButton.setText(bundle.getString("WriteAsPNGDialog.inchRadioButton.text"));
        this.inchRadioButton.setToolTipText(bundle.getString("WriteAsPNGDialog.inchRadioButton.toolTipText"));
        this.inchRadioButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        this.inchRadioButton.setMargin(new Insets(0, 0, 0, 0));
        this.einheitButtonGroup.add(this.pixelRadioButton);
        this.pixelRadioButton.setMnemonic(ResourceBundle.getBundle("intl/WriteAsPNGDialog").getString("WriteAsPNGDialog.pixelRadioButton.mnemonic").charAt(0));
        this.pixelRadioButton.setText(bundle.getString("WriteAsPNGDialog.pixelRadioButton.text"));
        this.pixelRadioButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        this.pixelRadioButton.setMargin(new Insets(0, 0, 0, 0));
        GroupLayout einheitPanelLayout = new GroupLayout(this.einheitPanel);
        this.einheitPanel.setLayout(einheitPanelLayout);
        einheitPanelLayout.setHorizontalGroup(
                einheitPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                einheitPanelLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                einheitPanelLayout.createParallelGroup(Alignment.LEADING)
                                                        .addComponent(this.mmRadioButton)
                                                        .addComponent(this.inchRadioButton)
                                                        .addComponent(this.pixelRadioButton)
                                        )
                                        .addContainerGap(49, 32767)
                        )
        );
        einheitPanelLayout.setVerticalGroup(
                einheitPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                einheitPanelLayout.createSequentialGroup()
                                        .addComponent(this.mmRadioButton)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.inchRadioButton)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.pixelRadioButton)
                                        .addContainerGap(-1, 32767)
                        )
        );
        this.bildSpeichernButton
                .setMnemonic(ResourceBundle.getBundle("intl/WriteAsPNGDialog").getString("WriteAsPNGDialog.bildSpeichernButton.mnemonic").charAt(0));
        this.bildSpeichernButton.setText(bundle.getString("WriteAsPNGDialog.bildSpeichernButton.text"));
        this.bildSpeichernButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                WriteAsPNGDialog.this.bildSpeichernButtonActionPerformed(evt);
            }
        });
        this.abbrechenButton.setMnemonic(ResourceBundle.getBundle("intl/WriteAsPNGDialog").getString("WriteAsPNGDialog.abbrechenButton.mnemonic").charAt(0));
        this.abbrechenButton.setText(bundle.getString("WriteAsPNGDialog.abbrechenButton.text"));
        this.abbrechenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                WriteAsPNGDialog.this.abbrechenButtonActionPerformed(evt);
            }
        });
        GroupLayout layout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                layout.createParallelGroup(Alignment.LEADING)
                                                        .addGroup(
                                                                layout.createSequentialGroup()
                                                                        .addComponent(this.sizePanel, -1, -1, 32767)
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addComponent(this.einheitPanel, -1, -1, 32767)
                                                        )
                                                        .addGroup(
                                                                Alignment.TRAILING,
                                                                layout.createSequentialGroup()
                                                                        .addComponent(this.bildSpeichernButton)
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addComponent(this.abbrechenButton)
                                                        )
                                        )
                                        .addContainerGap()
                        )
        );
        layout.linkSize(0, this.abbrechenButton, this.bildSpeichernButton);
        layout.setVerticalGroup(
                layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                layout.createParallelGroup(Alignment.TRAILING, false)
                                                        .addComponent(this.sizePanel, Alignment.LEADING, -1, -1, 32767)
                                                        .addComponent(this.einheitPanel, Alignment.LEADING, -1, -1, 32767)
                                        )
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(this.abbrechenButton).addComponent(this.bildSpeichernButton))
                                        .addContainerGap()
                        )
        );
        this.pack();
    }

    private void bildSpeichernButtonActionPerformed(ActionEvent evt) {
        JFileChooser chooser = new JFileChooser(this.getBildFile());
        chooser.setFileFilter(
                new FileNameExtensionFilter("*.png " + ResourceBundle.getBundle("intl/WriteAsPNGDialog").getString("WriteAsPNGDialog.files"), "png")
        );
        int returnVal = chooser.showSaveDialog(this);
        if (returnVal == 0) {
            this.bildFile = chooser.getSelectedFile();
            String name = this.getBildFile().getAbsolutePath();
            if (!name.toLowerCase().endsWith(".png")) {
                name = name + ".png";

                try {
                    this.bildFile = new File(name);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            this,
                            ResourceBundle.getBundle("intl/WriteAsPNGDialog").getString("WriteAsPNGDialog.invalid_filename"),
                            ResourceBundle.getBundle("intl/WriteAsPNGDialog").getString("WriteAsPNGDialog.error"),
                            0
                    );
                    return;
                }
            }

            if (this.bildFile == null) {
                return;
            }

            try {
                this.aufloesung = Integer.parseInt(this.resolutionTextField.getText());
                this.bildSize = Double.parseDouble(this.sizeTextField.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        ResourceBundle.getBundle("intl/WriteAsPNGDialog").getString("WriteAsPNGDialog.invalid_input_format"),
                        ResourceBundle.getBundle("intl/WriteAsPNGDialog").getString("WriteAsPNGDialog.error"),
                        0
                );
                return;
            }

            for (int i = 0; i < this.einheiten.length; i++) {
                if (this.einheiten[i].isSelected()) {
                    this.einheit = i;
                    break;
                }
            }

            Options.getInstance().setDefaultImageDir(this.bildFile.getParent());
            this.ok = true;
            this.setVisible(false);
        }
    }

    private void abbrechenButtonActionPerformed(ActionEvent evt) {
        this.setVisible(false);
    }

    public File getBildFile() {
        return this.bildFile;
    }

    public int getAufloesung() {
        return this.aufloesung;
    }

    public double getBildSize() {
        return this.bildSize;
    }

    public int getEinheit() {
        return this.einheit;
    }

    public boolean isOk() {
        return this.ok;
    }
}
