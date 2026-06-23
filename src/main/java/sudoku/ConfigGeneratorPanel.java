package sudoku;

import generator.GeneratorPattern;
import generator.SudokuGeneratorFactory;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigGeneratorPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private Color okColor;
    private Color errorColor = new Color(255, 150, 150);
    private ArrayList<GeneratorPattern> patterns = new ArrayList<>();
    private int patternIndex = -1;
    private GeneratorPatternPanel generatorPatternPanel1;
    private JButton jButtonChangeName;
    private JButton jButtonCheckPattern;
    private JButton jButtonLoad;
    private JButton jButtonNew;
    private JButton jButtonSave;
    private JComboBox jComboBoxPatterns;
    private JLabel jLabel1;
    private JLabel jLabelPattern;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JTextField numberOfGivensTextField;
    private JButton resetButton;

    public ConfigGeneratorPanel() {
        this.initComponents();
        this.okColor = this.numberOfGivensTextField.getBackground();
        this.setAnzGivens(0);
        this.initAll(false);
    }

    private void initComponents() {
        this.jPanel1 = new JPanel();
        this.generatorPatternPanel1 = new GeneratorPatternPanel();
        this.jLabel1 = new JLabel();
        this.numberOfGivensTextField = new JTextField();
        this.jPanel2 = new JPanel();
        this.resetButton = new JButton();
        this.jLabelPattern = new JLabel();
        this.jComboBoxPatterns = new JComboBox();
        this.jButtonNew = new JButton();
        this.jButtonLoad = new JButton();
        this.jButtonSave = new JButton();
        this.jButtonChangeName = new JButton();
        this.jButtonCheckPattern = new JButton();
        ResourceBundle bundle = ResourceBundle.getBundle("intl/ConfigGeneratorPanel");
        this.generatorPatternPanel1.setBorder(BorderFactory.createTitledBorder(bundle.getString("ConfigGeneratorPanel.generatorPatternPanel1.border.title")));
        GroupLayout generatorPatternPanel1Layout = new GroupLayout(this.generatorPatternPanel1);
        this.generatorPatternPanel1.setLayout(generatorPatternPanel1Layout);
        generatorPatternPanel1Layout.setHorizontalGroup(generatorPatternPanel1Layout.createParallelGroup(Alignment.LEADING).addGap(0, 219, 32767));
        generatorPatternPanel1Layout.setVerticalGroup(generatorPatternPanel1Layout.createParallelGroup(Alignment.LEADING).addGap(0, 209, 32767));
        this.jLabel1.setHorizontalAlignment(2);
        this.jLabel1.setText(bundle.getString("ConfigGeneratorPanel.jLabel1.text"));
        this.numberOfGivensTextField.setEditable(false);
        this.numberOfGivensTextField.setHorizontalAlignment(0);
        GroupLayout jPanel1Layout = new GroupLayout(this.jPanel1);
        this.jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                jPanel1Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                jPanel1Layout.createParallelGroup(Alignment.LEADING)
                                                        .addGroup(
                                                                jPanel1Layout.createSequentialGroup()
                                                                        .addComponent(this.jLabel1)
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addComponent(this.numberOfGivensTextField, -2, 37, -2)
                                                        )
                                                        .addComponent(this.generatorPatternPanel1, -2, -1, -2)
                                        )
                                        .addContainerGap(-1, 32767)
                        )
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                jPanel1Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(this.generatorPatternPanel1, -2, -1, -2)
                                        .addPreferredGap(ComponentPlacement.UNRELATED)
                                        .addGroup(
                                                jPanel1Layout.createParallelGroup(Alignment.BASELINE).addComponent(this.jLabel1).addComponent(this.numberOfGivensTextField, -2, -1, -2)
                                        )
                                        .addContainerGap(178, 32767)
                        )
        );
        this.resetButton.setMnemonic(ResourceBundle.getBundle("intl/ConfigGeneratorPanel").getString("ConfigGeneratorPanel.resetButton.mnemonic").charAt(0));
        this.resetButton.setText(bundle.getString("ConfigGeneratorPanel.resetButton.text"));
        this.resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigGeneratorPanel.this.resetButtonActionPerformed(evt);
            }
        });
        this.jLabelPattern
                .setDisplayedMnemonic(ResourceBundle.getBundle("intl/ConfigGeneratorPanel").getString("ConfigGeneratorPanel.jLabelPattern.mnemonic").charAt(0));
        this.jLabelPattern.setLabelFor(this.jComboBoxPatterns);
        this.jLabelPattern.setText(bundle.getString("ConfigGeneratorPanel.jLabelPattern.text"));
        this.jComboBoxPatterns.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigGeneratorPanel.this.jComboBoxPatternsActionPerformed(evt);
            }
        });
        this.jButtonNew.setMnemonic(ResourceBundle.getBundle("intl/ConfigGeneratorPanel").getString("ConfigGeneratorPanel.jButtonNew.mnemonic").charAt(0));
        this.jButtonNew.setText(bundle.getString("ConfigGeneratorPanel.jButtonNew.text"));
        this.jButtonNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigGeneratorPanel.this.jButtonNewActionPerformed(evt);
            }
        });
        this.jButtonLoad.setMnemonic(ResourceBundle.getBundle("intl/ConfigGeneratorPanel").getString("ConfigGeneratorPanel.jButtonLoad.mnemonic").charAt(0));
        this.jButtonLoad.setText(bundle.getString("ConfigGeneratorPanel.jButtonLoad.text"));
        this.jButtonLoad.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigGeneratorPanel.this.jButtonLoadActionPerformed(evt);
            }
        });
        this.jButtonSave.setMnemonic(ResourceBundle.getBundle("intl/ConfigGeneratorPanel").getString("ConfigGeneratorPanel.jButtonSave.mnemonic").charAt(0));
        this.jButtonSave.setText(bundle.getString("ConfigGeneratorPanel.jButtonSave.text"));
        this.jButtonSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigGeneratorPanel.this.jButtonSaveActionPerformed(evt);
            }
        });
        this.jButtonChangeName
                .setMnemonic(ResourceBundle.getBundle("intl/ConfigGeneratorPanel").getString("ConfigGeneratorPanel.jButtonChangeName.mnemonic").charAt(0));
        this.jButtonChangeName.setText(bundle.getString("ConfigGeneratorPanel.jButtonChangeName.text"));
        this.jButtonChangeName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigGeneratorPanel.this.jButtonChangeNameActionPerformed(evt);
            }
        });
        this.jButtonCheckPattern
                .setMnemonic(ResourceBundle.getBundle("intl/ConfigGeneratorPanel").getString("ConfigGeneratorPanel.jButtonCheckPattern.mnemonic").charAt(0));
        this.jButtonCheckPattern.setText(bundle.getString("ConfigGeneratorPanel.jButtonCheckPattern.text"));
        this.jButtonCheckPattern.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigGeneratorPanel.this.jButtonCheckPatternActionPerformed(evt);
            }
        });
        GroupLayout jPanel2Layout = new GroupLayout(this.jPanel2);
        this.jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                jPanel2Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                jPanel2Layout.createParallelGroup(Alignment.LEADING)
                                                        .addComponent(this.resetButton, Alignment.TRAILING)
                                                        .addGroup(
                                                                jPanel2Layout.createSequentialGroup()
                                                                        .addComponent(this.jLabelPattern)
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addComponent(this.jComboBoxPatterns, 0, 150, 32767)
                                                        )
                                                        .addComponent(this.jButtonNew, Alignment.TRAILING)
                                                        .addComponent(this.jButtonChangeName, Alignment.TRAILING)
                                                        .addComponent(this.jButtonLoad, Alignment.TRAILING)
                                                        .addComponent(this.jButtonSave, Alignment.TRAILING)
                                                        .addComponent(this.jButtonCheckPattern, Alignment.TRAILING)
                                        )
                                        .addContainerGap()
                        )
        );
        jPanel2Layout.linkSize(0, this.jButtonChangeName, this.jButtonCheckPattern, this.jButtonLoad, this.jButtonNew, this.jButtonSave);
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                Alignment.TRAILING,
                                jPanel2Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                jPanel2Layout.createParallelGroup(Alignment.BASELINE).addComponent(this.jLabelPattern).addComponent(this.jComboBoxPatterns, -2, -1, -2)
                                        )
                                        .addGap(18, 18, 18)
                                        .addComponent(this.jButtonNew)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.jButtonChangeName)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.jButtonLoad)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.jButtonSave)
                                        .addGap(18, 18, 18)
                                        .addComponent(this.jButtonCheckPattern)
                                        .addPreferredGap(ComponentPlacement.RELATED, 218, 32767)
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
                                        .addComponent(this.jPanel1, -2, -1, -2)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.jPanel2, -1, -1, 32767)
                        )
        );
        layout.setVerticalGroup(layout.createParallelGroup(Alignment.LEADING).addComponent(this.jPanel1, -1, -1, 32767).addComponent(this.jPanel2, -1, -1, 32767));
    }

    private void resetButtonActionPerformed(ActionEvent evt) {
        this.initAll(true);
    }

    private void jComboBoxPatternsActionPerformed(ActionEvent evt) {
        if (this.jComboBoxPatterns.getItemAt(0) != null) {
            int index = this.jComboBoxPatterns.getSelectedIndex();
            this.setPatternIndex(index - 1);
            if (index > 0) {
                this.generatorPatternPanel1.setPattern(this.patterns.get(this.patternIndex).getPattern());
            } else if (index == 0) {
                this.generatorPatternPanel1.setPattern(null);
            }
        }
    }

    private void jButtonNewActionPerformed(ActionEvent evt) {
        String defaultName = ResourceBundle.getBundle("intl/ConfigGeneratorPanel").getString("ConfigGeneratorPanel.pattern") + " " + (this.patterns.size() + 1);
        String name = JOptionPane.showInputDialog(
                this.jButtonNew, ResourceBundle.getBundle("intl/ConfigGeneratorPanel").getString("ConfigGeneratorPanel.patternname"), defaultName
        );
        if (name != null) {
            this.patterns.add(new GeneratorPattern(name));
            this.setPatternIndex(this.patterns.size() - 1);
            this.jComboBoxPatterns.addItem(name);
            this.jComboBoxPatterns.setSelectedIndex(this.patternIndex + 1);
            this.generatorPatternPanel1.setPattern(this.patterns.get(this.patternIndex).getPattern());
        }
    }

    private void jButtonChangeNameActionPerformed(ActionEvent evt) {
        String defaultName = (String) this.jComboBoxPatterns.getSelectedItem();
        String name = JOptionPane.showInputDialog(
                this.jButtonChangeName, ResourceBundle.getBundle("intl/ConfigGeneratorPanel").getString("ConfigGeneratorPanel.changepatternname"), defaultName
        );
        if (name != null) {
            this.patterns.get(this.patternIndex).setName(name);
            this.fillCombo(this.patternIndex);
        }
    }

    private void jButtonLoadActionPerformed(ActionEvent evt) {
        String extension = ResourceBundle.getBundle("intl/ConfigGeneratorPanel").getString("ConfigGeneratorPanel.extension");
        String description = ResourceBundle.getBundle("intl/ConfigGeneratorPanel").getString("ConfigGeneratorPanel.description");
        FileNameExtensionFilter hpat = new FileNameExtensionFilter(description, extension);
        JFileChooser chooser = new JFileChooser(Options.getInstance().getDefaultFileDir());
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.addChoosableFileFilter(hpat);
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == 0) {
            String path = chooser.getSelectedFile().getPath();
            path = path.substring(0, path.lastIndexOf(File.separatorChar));
            Options.getInstance().setDefaultFileDir(path);
            path = chooser.getSelectedFile().getAbsolutePath();

            try {
                XMLDecoder in = new XMLDecoder(new BufferedInputStream(new FileInputStream(path)));
                this.patterns = (ArrayList<GeneratorPattern>) in.readObject();
                this.setPatternIndex((Integer) in.readObject());
                in.close();
                this.fillCombo(this.patternIndex);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Options.class.getName()).log(Level.INFO, "Pattern file {0} not found (reading)", path);
            }
        }
    }

    private void jButtonSaveActionPerformed(ActionEvent evt) {
        String extension = ResourceBundle.getBundle("intl/ConfigGeneratorPanel").getString("ConfigGeneratorPanel.extension");
        String description = ResourceBundle.getBundle("intl/ConfigGeneratorPanel").getString("ConfigGeneratorPanel.description");
        FileNameExtensionFilter hpat = new FileNameExtensionFilter(description, extension);
        JFileChooser chooser = new JFileChooser(Options.getInstance().getDefaultFileDir());
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.addChoosableFileFilter(hpat);
        int returnVal = chooser.showSaveDialog(this);
        if (returnVal == 0) {
            String path = chooser.getSelectedFile().getPath();
            path = path.substring(0, path.lastIndexOf(File.separatorChar));
            if (!path.endsWith(extension)) {
                path = path + "." + extension;
            }

            Options.getInstance().setDefaultFileDir(path);
            path = chooser.getSelectedFile().getAbsolutePath();

            try {
                XMLEncoder out = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(path)));
                out.writeObject(this.patterns);
                out.writeObject(this.patternIndex);
                out.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Options.class.getName()).log(Level.INFO, "Pattern file {0} not found (writing)", path);
            }
        }
    }

    private void jButtonCheckPatternActionPerformed(ActionEvent evt) {
        if (this.patternIndex >= 0 && this.patternIndex <= this.patterns.size()) {
            GeneratorPattern act = this.patterns.get(this.patternIndex);
            if (act.getAnzGivens() < 17) {
                JOptionPane.showMessageDialog(this, "Pattern has to few positions set! Please change it and try again.", "Invalid", 1);
                act.setValid(false);
            } else {
                Cursor oldCursor = this.getCursor();
                this.setCursor(Cursor.getPredefinedCursor(3));
                if (SudokuGeneratorFactory.getDefaultGeneratorInstance().generateSudoku(true, act.getPattern()) != null) {
                    JOptionPane.showMessageDialog(this, "Pattern is valid!", "Valid", 1);
                    act.setValid(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Pattern is not valid! Please change it and try again.", "Invalid", 1);
                    act.setValid(false);
                }

                this.setCursor(oldCursor);
            }
        }
    }

    public void okPressed() {
        Options.getInstance().setGeneratorPatterns(this.copyGeneratorPatterns(this.patterns));
        Options.getInstance().setGeneratorPatternIndex(this.patternIndex);
    }

    private void initAll(boolean setDefault) {
        if (setDefault) {
            this.patterns = new ArrayList<>();
            this.setPatternIndex(-1);
        } else {
            this.patterns = this.copyGeneratorPatterns(Options.getInstance().getGeneratorPatterns());
            this.setPatternIndex(Options.getInstance().getGeneratorPatternIndex());
        }

        this.fillCombo(this.patternIndex);
    }

    private void fillCombo(int index) {
        this.jComboBoxPatterns.removeAllItems();
        this.jComboBoxPatterns.addItem(ResourceBundle.getBundle("intl/ConfigGeneratorPanel").getString("ConfigGeneratorPanel.nopattern"));

        for (GeneratorPattern p : this.patterns) {
            this.jComboBoxPatterns.addItem(p.getName());
        }

        this.setPatternIndex(index);
        this.jComboBoxPatterns.setSelectedIndex(index + 1);
    }

    private ArrayList<GeneratorPattern> copyGeneratorPatterns(ArrayList<GeneratorPattern> src) {
        ArrayList<GeneratorPattern> dest = new ArrayList<>(src.size());

        for (GeneratorPattern p : src) {
            dest.add(p.clone());
        }

        return dest;
    }

    public final void setAnzGivens(int anz) {
        this.numberOfGivensTextField.setText(anz + "");
        if (anz < 17) {
            this.numberOfGivensTextField.setBackground(this.errorColor);
        } else {
            this.numberOfGivensTextField.setBackground(this.okColor);
        }
    }

    private void setPatternIndex(int index) {
        this.patternIndex = index;
        this.jButtonCheckPattern.setEnabled(index != -1);
    }
}
