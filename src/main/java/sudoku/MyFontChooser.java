package sudoku;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.ResourceBundle;

public class MyFontChooser extends JDialog implements ListSelectionListener {
    private static final long serialVersionUID = 1L;
    static MyFontChooser chooser = null;
    private static String[] fontNames = null;
    private static String[] styles = new String[]{
            ResourceBundle.getBundle("intl/MyFontChooser").getString("MyFontChooser.regular"),
            ResourceBundle.getBundle("intl/MyFontChooser").getString("MyFontChooser.italic"),
            ResourceBundle.getBundle("intl/MyFontChooser").getString("MyFontChooser.bold"),
            ResourceBundle.getBundle("intl/MyFontChooser").getString("MyFontChooser.bold_italic")
    };
    private static String[] size = new String[]{
            "6", "7", "8", "9", "10", "11", "12", "14", "16", "18", "20", "22", "24", "26", "28", "30", "36", "40", "50", "60"
    };
    private Font font;
    private JButton jbCancel;
    private JButton jbOK;
    private JLabel jlSchriftart;
    private JLabel jlSchriftgrad;
    private JLabel jlSchriftschnitt;
    private JList jliSchriftart;
    private JList jliSchriftgrad;
    private JList jliSchriftschnitt;
    private JPanel jpDemo;
    private JScrollPane jspSchriftart;
    private JScrollPane jspSchriftgrad;
    private JScrollPane jspSchriftschnitt;

    private MyFontChooser(Frame owner, boolean modal) {
        super(owner, modal);
        if (fontNames == null) {
            fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        }

        this.initComponents();
        this.jliSchriftart.addListSelectionListener(this);
        this.jliSchriftschnitt.addListSelectionListener(this);
        this.jliSchriftgrad.addListSelectionListener(this);
        this.getRootPane().setDefaultButton(this.jbOK);
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(27, 0, false);
        Action escapeAction = new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                MyFontChooser.this.jbCancelActionPerformed(e);
            }
        };
        this.getRootPane().getInputMap(2).put(escapeKeyStroke, "ESCAPE");
        this.getRootPane().getActionMap().put("ESCAPE", escapeAction);
    }

    public static Font showDialog(Frame owner, String title, Font initialFont) {
        if (initialFont == null) {
            initialFont = UIManager.getFont("Button.font");
        }

        if (chooser == null) {
            chooser = new MyFontChooser(owner, true);
        }

        chooser.setTitle(title);
        chooser.showFontChooser(initialFont);
        return chooser.font;
    }

    public static void main(String[] args) {
        Font font = showDialog(null, "Testtitel", new Font("Arial", 2, 14));
        System.out.println(font);
    }

    private void showFontChooser(Font font) {
        this.font = font;
        int index = Arrays.binarySearch(fontNames, font.getName());
        if (index >= 0) {
            this.jliSchriftart.setSelectedIndex(index);
            this.jliSchriftart.ensureIndexIsVisible(index);
        }

        int style = font.getStyle();
        byte var5;
        if (style == 0) {
            var5 = 0;
        } else if (style == 2) {
            var5 = 1;
        } else if (style == 1) {
            var5 = 2;
        } else {
            var5 = 3;
        }

        this.jliSchriftschnitt.setSelectedIndex(var5);
        this.jliSchriftschnitt.ensureIndexIsVisible(var5);
        int actSize = font.getSize();
        var5 = (byte) Arrays.binarySearch(size, String.valueOf(actSize));
        if (var5 < 0) {
            var5 = 6;
        }

        this.jliSchriftgrad.setSelectedIndex(var5);
        this.jliSchriftgrad.ensureIndexIsVisible(var5);
        this.setVisible(true);
    }

    private void initComponents() {
        this.jlSchriftart = new JLabel();
        this.jspSchriftart = new JScrollPane();
        this.jliSchriftart = new JList<>(fontNames);
        this.jlSchriftschnitt = new JLabel();
        this.jspSchriftschnitt = new JScrollPane();
        this.jliSchriftschnitt = new JList<>(styles);
        this.jlSchriftgrad = new JLabel();
        this.jspSchriftgrad = new JScrollPane();
        this.jliSchriftgrad = new JList<>(size);
        this.jpDemo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                MyFontChooser.this.updateDemoPanel(g);
            }
        };
        this.jbCancel = new JButton();
        this.jbOK = new JButton();
        this.setDefaultCloseOperation(2);
        ResourceBundle bundle = ResourceBundle.getBundle("intl/MyFontChooser");
        this.setTitle(bundle.getString("MyFontChooser.title"));
        this.jlSchriftart.setDisplayedMnemonic(ResourceBundle.getBundle("intl/MyFontChooser").getString("MyFontChooser.jlSchriftart.mnemonic").charAt(0));
        this.jlSchriftart.setLabelFor(this.jliSchriftart);
        this.jlSchriftart.setText(bundle.getString("MyFontChooser.jlSchriftart.text"));
        this.jspSchriftart.setVerticalScrollBarPolicy(22);
        this.jliSchriftart.setSelectionMode(0);
        this.jspSchriftart.setViewportView(this.jliSchriftart);
        this.jlSchriftschnitt.setDisplayedMnemonic(ResourceBundle.getBundle("intl/MyFontChooser").getString("MyFontChooser.jlSchriftSchnitt.mnemonic").charAt(0));
        this.jlSchriftschnitt.setLabelFor(this.jliSchriftschnitt);
        this.jlSchriftschnitt.setText(bundle.getString("MyFontChooser.jlSchriftschnitt.text"));
        this.jspSchriftschnitt.setVerticalScrollBarPolicy(22);
        this.jspSchriftschnitt.setViewportView(this.jliSchriftschnitt);
        this.jlSchriftgrad.setDisplayedMnemonic(ResourceBundle.getBundle("intl/MyFontChooser").getString("MyFontChooser.jlSchriftgrad.mnemonic").charAt(0));
        this.jlSchriftgrad.setLabelFor(this.jliSchriftgrad);
        this.jlSchriftgrad.setText(bundle.getString("MyFontChooser.jlSchriftgrad.text"));
        this.jspSchriftgrad.setVerticalScrollBarPolicy(22);
        this.jspSchriftgrad.setViewportView(this.jliSchriftgrad);
        this.jpDemo.setBorder(BorderFactory.createTitledBorder(bundle.getString("MyFontChooser.jpDemo.border.title")));
        this.jbCancel.setMnemonic(ResourceBundle.getBundle("intl/MyFontChooser").getString("MyFontChooser.jbCancel.mnemonic").charAt(0));
        this.jbCancel.setText(bundle.getString("MyFontChooser.jbCancel.text"));
        this.jbCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MyFontChooser.this.jbCancelActionPerformed(evt);
            }
        });
        this.jbOK.setMnemonic(ResourceBundle.getBundle("intl/MyFontChooser").getString("MyFontChooser.jbOK.mnemonic").charAt(0));
        this.jbOK.setText(bundle.getString("MyFontChooser.jbOK.text"));
        this.jbOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                MyFontChooser.this.jbOKActionPerformed(evt);
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
                                                                layout.createParallelGroup(Alignment.LEADING)
                                                                        .addGroup(
                                                                                Alignment.TRAILING,
                                                                                layout.createSequentialGroup()
                                                                                        .addGroup(
                                                                                                layout.createParallelGroup(Alignment.LEADING)
                                                                                                        .addComponent(this.jlSchriftart)
                                                                                                        .addComponent(this.jspSchriftart, -2, 180, -2)
                                                                                        )
                                                                                        .addGap(5, 5, 5)
                                                                                        .addGroup(
                                                                                                layout.createParallelGroup(Alignment.LEADING)
                                                                                                        .addComponent(this.jlSchriftschnitt)
                                                                                                        .addComponent(this.jspSchriftschnitt, -2, 100, -2)
                                                                                        )
                                                                                        .addGap(5, 5, 5)
                                                                                        .addGroup(
                                                                                                layout.createParallelGroup(Alignment.LEADING)
                                                                                                        .addComponent(this.jlSchriftgrad)
                                                                                                        .addComponent(this.jspSchriftgrad, -1, 90, 32767)
                                                                                        )
                                                                                        .addContainerGap()
                                                                        )
                                                                        .addGroup(Alignment.TRAILING, layout.createSequentialGroup().addComponent(this.jpDemo, -1, 380, 32767).addGap(10, 10, 10))
                                                        )
                                                        .addGroup(
                                                                Alignment.TRAILING,
                                                                layout.createSequentialGroup()
                                                                        .addComponent(this.jbOK)
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addComponent(this.jbCancel)
                                                                        .addContainerGap()
                                                        )
                                        )
                        )
        );
        layout.linkSize(0, this.jbCancel, this.jbOK);
        layout.setVerticalGroup(
                layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                layout.createParallelGroup(Alignment.LEADING)
                                                        .addComponent(this.jlSchriftart)
                                                        .addComponent(this.jlSchriftschnitt)
                                                        .addComponent(this.jlSchriftgrad)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(
                                                layout.createParallelGroup(Alignment.LEADING)
                                                        .addComponent(this.jspSchriftart, -2, 165, -2)
                                                        .addComponent(this.jspSchriftschnitt, -2, 165, -2)
                                                        .addComponent(this.jspSchriftgrad, -2, 165, -2)
                                        )
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.jpDemo, -2, 80, -2)
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(this.jbCancel, -1, 23, 32767).addComponent(this.jbOK, -1, 23, 32767))
                                        .addContainerGap()
                        )
        );
        this.pack();
    }

    private void jbOKActionPerformed(ActionEvent evt) {
        this.setVisible(false);
    }

    private void jbCancelActionPerformed(ActionEvent evt) {
        this.font = null;
        this.setVisible(false);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        String name = (String) this.jliSchriftart.getSelectedValue();
        int styleIndex = this.jliSchriftschnitt.getSelectedIndex();
        String value = (String) this.jliSchriftgrad.getSelectedValue();
        int tmpSize = 12;
        if (value != null) {
            tmpSize = Integer.parseInt(value);
        }

        int style = 0;
        switch (styleIndex) {
            case 0:
                style = 0;
                break;
            case 1:
                style = 2;
                break;
            case 2:
                style = 1;
                break;
            case 3:
                style = 3;
        }

        if (name != null && this.font != null && (!name.equals(this.font.getName()) || tmpSize != this.font.getSize() || style != this.font.getStyle())) {
            this.font = new Font(name, style, tmpSize);
            this.jpDemo.repaint();
        }
    }

    private void updateDemoPanel(Graphics g) {
        Graphics2D gr = (Graphics2D) g;
        gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Insets insets = this.jpDemo.getInsets();
        Rectangle dim = this.jpDemo.getBounds();
        Color tmpCol = gr.getColor();
        gr.setColor(this.jpDemo.getBackground());
        gr.fillRect(0, 0, this.jpDemo.getWidth(), this.jpDemo.getHeight());
        gr.setColor(tmpCol);
        gr.setFont(new Font(this.font.getName(), this.font.getStyle(), 24));
        FontMetrics metrics = gr.getFontMetrics();
        int yBase = (dim.height + metrics.getHeight()) / 2;
        gr.drawLine(insets.left, yBase, dim.width - insets.right, yBase);
        String text = "    123456789    ";
        Shape oldClip = gr.getClip();
        gr.setClip(insets.left, insets.top, dim.width - insets.left - insets.right, dim.height - insets.top - insets.bottom);
        gr.drawString(text, (this.jpDemo.getWidth() - metrics.stringWidth(text)) / 2, yBase);
        gr.setClip(oldClip);
    }
}
