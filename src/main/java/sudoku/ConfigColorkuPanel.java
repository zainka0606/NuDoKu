package sudoku;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ResourceBundle;

public class ConfigColorkuPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private JButton[] buttons = null;
    private Color[] colors = null;
    private MainFrame mainFrame;
    private JButton deviationButton;
    private JLabel deviationLabel;
    private JButton invalidButton;
    private JLabel invalidLabel;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JButton resetButton;
    private JButton v1Button;
    private JButton v2Button;
    private JButton v3Button;
    private JButton v4Button;
    private JButton v5Button;
    private JButton v6Button;
    private JButton v7Button;
    private JButton v8Button;
    private JButton v9Button;

    public ConfigColorkuPanel(Component mainFrame) {
        this.initComponents();
        this.buttons = new JButton[]{
                this.v1Button,
                this.v2Button,
                this.v3Button,
                this.v4Button,
                this.v5Button,
                this.v6Button,
                this.v7Button,
                this.v8Button,
                this.v9Button,
                this.invalidButton,
                this.deviationButton
        };
        this.mainFrame = (MainFrame) mainFrame;
        this.initAll(false);
    }

    private void initComponents() {
        this.jPanel1 = new JPanel();
        this.invalidLabel = new JLabel();
        this.deviationLabel = new JLabel();
        this.invalidButton = new JButton();
        this.deviationButton = new JButton();
        this.jPanel2 = new JPanel();
        this.v1Button = new JButton();
        this.v2Button = new JButton();
        this.v3Button = new JButton();
        this.v4Button = new JButton();
        this.v5Button = new JButton();
        this.v6Button = new JButton();
        this.v7Button = new JButton();
        this.v8Button = new JButton();
        this.v9Button = new JButton();
        this.resetButton = new JButton();
        ResourceBundle bundle = ResourceBundle.getBundle("intl/ConfigColorkuPanel");
        this.jPanel1.setBorder(BorderFactory.createTitledBorder(bundle.getString("ConfigColorkuPanel.jPanel1.text")));
        this.invalidLabel.setText(bundle.getString("ConfigColorkuPanel.invalidLabel.text"));
        this.deviationLabel.setText(bundle.getString("ConfigColorkuPanel.deviationLabel.text"));
        this.invalidButton.setText("...");
        this.invalidButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigColorkuPanel.this.invalidButtonActionPerformed(evt);
            }
        });
        this.deviationButton.setText("...");
        this.deviationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigColorkuPanel.this.deviationButtonActionPerformed(evt);
            }
        });
        this.jPanel2.setBorder(BorderFactory.createTitledBorder(bundle.getString("jPanel2.txt")));
        this.jPanel2.setLayout(new GridBagLayout());
        this.v1Button.setText(bundle.getString("ConfigColorkuPanel.v1Button.text"));
        this.v1Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigColorkuPanel.this.v1ButtonActionPerformed(evt);
            }
        });
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipady = 16;
        gridBagConstraints.insets = new Insets(6, 2, 6, 2);
        this.jPanel2.add(this.v1Button, gridBagConstraints);
        this.v2Button.setText(bundle.getString("ConfigColorkuPanel.v2Button.text"));
        this.v2Button.setActionCommand(bundle.getString("ConfigColorkuPanel.v2Button.actionCommand"));
        this.v2Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigColorkuPanel.this.v2ButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipady = 16;
        gridBagConstraints.insets = new Insets(0, 2, 0, 2);
        this.jPanel2.add(this.v2Button, gridBagConstraints);
        this.v3Button.setText(bundle.getString("ConfigColorkuPanel.v3Button.text"));
        this.v3Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigColorkuPanel.this.v3ButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipady = 16;
        gridBagConstraints.insets = new Insets(0, 2, 0, 2);
        this.jPanel2.add(this.v3Button, gridBagConstraints);
        this.v4Button.setText(bundle.getString("ConfigColorkuPanel.v4Button.text"));
        this.v4Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigColorkuPanel.this.v4ButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipady = 16;
        gridBagConstraints.insets = new Insets(6, 2, 6, 2);
        this.jPanel2.add(this.v4Button, gridBagConstraints);
        this.v5Button.setText(bundle.getString("ConfigColorkuPanel.v5Button.text"));
        this.v5Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigColorkuPanel.this.v5ButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipady = 16;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        this.jPanel2.add(this.v5Button, gridBagConstraints);
        this.v6Button.setText(bundle.getString("ConfigColorkuPanel.v6Button.text"));
        this.v6Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigColorkuPanel.this.v6ButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipady = 16;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        this.jPanel2.add(this.v6Button, gridBagConstraints);
        this.v7Button.setText(bundle.getString("ConfigColorkuPanel.v7Button.text"));
        this.v7Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigColorkuPanel.this.v7ButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipady = 16;
        gridBagConstraints.insets = new Insets(6, 2, 6, 2);
        this.jPanel2.add(this.v7Button, gridBagConstraints);
        this.v8Button.setText(bundle.getString("ConfigColorkuPanel.v8Button.text"));
        this.v8Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigColorkuPanel.this.v8ButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipady = 16;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        this.jPanel2.add(this.v8Button, gridBagConstraints);
        this.v9Button.setText(bundle.getString("ConfigColorkuPanel.v9Button.text"));
        this.v9Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigColorkuPanel.this.v9ButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipady = 16;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        this.jPanel2.add(this.v9Button, gridBagConstraints);
        GroupLayout jPanel1Layout = new GroupLayout(this.jPanel1);
        this.jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                jPanel1Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                jPanel1Layout.createParallelGroup(Alignment.LEADING)
                                                        .addComponent(this.jPanel2, -2, -1, -2)
                                                        .addGroup(
                                                                jPanel1Layout.createSequentialGroup()
                                                                        .addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING).addComponent(this.invalidLabel).addComponent(this.deviationLabel))
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addGroup(
                                                                                jPanel1Layout.createParallelGroup(Alignment.LEADING)
                                                                                        .addComponent(this.deviationButton, -2, 40, -2)
                                                                                        .addComponent(this.invalidButton, -2, 40, -2)
                                                                        )
                                                        )
                                        )
                                        .addContainerGap(-1, 32767)
                        )
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                jPanel1Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(jPanel1Layout.createParallelGroup(Alignment.BASELINE).addComponent(this.invalidLabel).addComponent(this.invalidButton, -2, 21, -2))
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                jPanel1Layout.createParallelGroup(Alignment.BASELINE).addComponent(this.deviationLabel).addComponent(this.deviationButton, -2, 21, -2)
                                        )
                                        .addPreferredGap(ComponentPlacement.UNRELATED)
                                        .addComponent(this.jPanel2, -2, -1, -2)
                                        .addContainerGap(-1, 32767)
                        )
        );
        this.resetButton.setMnemonic(ResourceBundle.getBundle("intl/ConfigColorkuPanel").getString("ConfigColorkuPanel.resetButton.mnemonic").charAt(0));
        this.resetButton.setText(bundle.getString("ConfigColorkuPanel.resetButton.text"));
        this.resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ConfigColorkuPanel.this.resetButtonActionPerformed(evt);
            }
        });
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup().addContainerGap().addComponent(this.jPanel1, -2, -1, -2).addContainerGap(349, 32767))
                        .addGroup(Alignment.TRAILING, layout.createSequentialGroup().addContainerGap(389, 32767).addComponent(this.resetButton).addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(this.jPanel1, -2, -1, -2)
                                        .addPreferredGap(ComponentPlacement.RELATED, 101, 32767)
                                        .addComponent(this.resetButton)
                                        .addContainerGap()
                        )
        );
    }

    private void resetButtonActionPerformed(ActionEvent evt) {
        this.initAll(true);
    }

    private void invalidButtonActionPerformed(ActionEvent evt) {
        this.chooseColor(9);
    }

    private void deviationButtonActionPerformed(ActionEvent evt) {
        this.chooseColor(10);
    }

    private void v1ButtonActionPerformed(ActionEvent evt) {
        this.chooseColor(0);
    }

    private void v2ButtonActionPerformed(ActionEvent evt) {
        this.chooseColor(1);
    }

    private void v4ButtonActionPerformed(ActionEvent evt) {
        this.chooseColor(2);
    }

    private void v5ButtonActionPerformed(ActionEvent evt) {
        this.chooseColor(3);
    }

    private void v3ButtonActionPerformed(ActionEvent evt) {
        this.chooseColor(4);
    }

    private void v6ButtonActionPerformed(ActionEvent evt) {
        this.chooseColor(5);
    }

    private void v7ButtonActionPerformed(ActionEvent evt) {
        this.chooseColor(6);
    }

    private void v8ButtonActionPerformed(ActionEvent evt) {
        this.chooseColor(7);
    }

    private void v9ButtonActionPerformed(ActionEvent evt) {
        this.chooseColor(8);
    }

    private void chooseColor(int index) {
        Color init = this.colors[index];
        Color color = JColorChooser.showDialog(this, ResourceBundle.getBundle("intl/ConfigColorPanel").getString("ConfigColorPanel.choose_color"), init);
        if (color != null) {
            this.colors[index] = color;
            this.initButton(this.buttons[index], color, index < 9);
        }
    }

    public void okPressed() {
        boolean changed = false;
        Color[] old = Options.getInstance().getColorKuColors();

        for (int i = 0; i < old.length; i++) {
            if (!old[i].equals(this.colors[i])) {
                changed = true;
            }
        }

        Options.getInstance().getColorKuColors()[0] = this.colors[0];
        Options.getInstance().getColorKuColors()[1] = this.colors[1];
        Options.getInstance().getColorKuColors()[2] = this.colors[2];
        Options.getInstance().getColorKuColors()[3] = this.colors[3];
        Options.getInstance().getColorKuColors()[4] = this.colors[4];
        Options.getInstance().getColorKuColors()[5] = this.colors[5];
        Options.getInstance().getColorKuColors()[6] = this.colors[6];
        Options.getInstance().getColorKuColors()[7] = this.colors[7];
        Options.getInstance().getColorKuColors()[8] = this.colors[8];
        Options.getInstance().getColorKuColors()[9] = this.colors[9];
        Options.getInstance().getColorKuColors()[10] = this.colors[10];
        if (changed) {
            this.mainFrame.getSudokuPanel().resetColorKuImages();
            this.mainFrame.repaint();
        }
    }

    private void initAll(boolean setDefault) {
        if (this.colors == null) {
            this.colors = new Color[this.buttons.length];
        }

        if (setDefault) {
            this.colors[0] = Options.COLORKU_COLORS[0];
            this.colors[1] = Options.COLORKU_COLORS[1];
            this.colors[2] = Options.COLORKU_COLORS[2];
            this.colors[3] = Options.COLORKU_COLORS[3];
            this.colors[4] = Options.COLORKU_COLORS[4];
            this.colors[5] = Options.COLORKU_COLORS[5];
            this.colors[6] = Options.COLORKU_COLORS[6];
            this.colors[7] = Options.COLORKU_COLORS[7];
            this.colors[8] = Options.COLORKU_COLORS[8];
            this.colors[9] = Options.COLORKU_COLORS[9];
            this.colors[10] = Options.COLORKU_COLORS[10];
        } else {
            this.colors[0] = Options.getInstance().getColorKuColor(1);
            this.colors[1] = Options.getInstance().getColorKuColor(2);
            this.colors[2] = Options.getInstance().getColorKuColor(3);
            this.colors[3] = Options.getInstance().getColorKuColor(4);
            this.colors[4] = Options.getInstance().getColorKuColor(5);
            this.colors[5] = Options.getInstance().getColorKuColor(6);
            this.colors[6] = Options.getInstance().getColorKuColor(7);
            this.colors[7] = Options.getInstance().getColorKuColor(8);
            this.colors[8] = Options.getInstance().getColorKuColor(9);
            this.colors[9] = Options.getInstance().getColorKuColor(10);
            this.colors[10] = Options.getInstance().getColorKuColor(11);
        }

        for (int i = 0; i < this.buttons.length - 2; i++) {
            this.initButton(this.buttons[i], this.colors[i], true);
        }

        this.initButton(this.buttons[9], this.colors[9], false);
        this.initButton(this.buttons[10], this.colors[10], false);
    }

    private void initButton(JButton button, Color color, boolean cand) {
        int size = 10;
        if (cand) {
            int var7 = 30;
            button.setIcon(new ImageIcon(new ColorKuImage(var7, color)));
        } else {
            Image img = new BufferedImage(size, size, 1);
            Graphics g = img.getGraphics();
            g.setColor(color);
            g.fillRect(0, 0, img.getWidth(null) - 1, img.getHeight(null) - 1);
            button.setIcon(new ImageIcon(img));
        }

        if (UIManager.getLookAndFeel().getName().equals("CDE/Motif")) {
            button.setBackground(color);
        }
    }
}
