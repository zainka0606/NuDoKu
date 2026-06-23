package sudoku;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

public class SudokuConsoleFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private StreamHandler consoleHandler = null;
    private JButton closeButton;
    private JTextArea consoleTextArea;
    private JScrollPane jScrollPane1;

    public SudokuConsoleFrame() {
        this.initComponents();
        PrintStream newOut = new PrintStream(new SudokuConsoleFrame.ConsoleOutputStream());
        System.setOut(newOut);
        System.setErr(newOut);
        this.consoleHandler = new StreamHandler(newOut, new SimpleFormatter());
        Logger rootLogger = Logger.getLogger("");
        rootLogger.addHandler(this.consoleHandler);
        rootLogger.setLevel(Level.CONFIG);
        this.getRootPane().setDefaultButton(this.closeButton);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SudokuConsoleFrame().setVisible(true);
            }
        });
    }

    private void initComponents() {
        this.jScrollPane1 = new JScrollPane();
        this.consoleTextArea = new JTextArea();
        this.closeButton = new JButton();
        this.setDefaultCloseOperation(3);
        ResourceBundle bundle = ResourceBundle.getBundle("intl/SudokuConsoleFrame");
        this.setTitle(bundle.getString("SudokuConsoleFrame.title"));
        this.consoleTextArea.setColumns(20);
        this.consoleTextArea.setRows(5);
        this.jScrollPane1.setViewportView(this.consoleTextArea);
        this.closeButton.setText(bundle.getString("SudokuConsoleForm.closeButton.text"));
        this.closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                SudokuConsoleFrame.this.closeButtonActionPerformed(evt);
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
                                                        .addComponent(this.jScrollPane1, -1, 803, 32767)
                                                        .addComponent(this.closeButton, Alignment.TRAILING)
                                        )
                                        .addContainerGap()
                        )
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(this.jScrollPane1, -1, 346, 32767)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.closeButton)
                                        .addContainerGap()
                        )
        );
        this.pack();
    }

    private void closeButtonActionPerformed(ActionEvent evt) {
        this.setVisible(false);
        System.exit(0);
    }

    public void setIn() {
        System.setIn(new SudokuConsoleFrame.ConsoleInputStream());
    }

    class ConsoleInputStream extends InputStream {
        @Override
        public int read() throws IOException {
            while (true) {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException ex) {
                    Logger.getLogger(SudokuConsoleFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    class ConsoleOutputStream extends OutputStream {
        private byte[] littlebuf = new byte[1];

        @Override
        public void write(int b) throws IOException {
            this.littlebuf[0] = (byte) b;
            String s = new String(this.littlebuf, 0, 1);
            SudokuConsoleFrame.this.consoleTextArea.append(s);
            SudokuConsoleFrame.this.consoleTextArea.setCaretPosition(SudokuConsoleFrame.this.consoleTextArea.getText().length());
        }

        @Override
        public void write(byte[] b) throws IOException {
            String s = new String(b, 0, b.length);
            SudokuConsoleFrame.this.consoleTextArea.append(s);
            SudokuConsoleFrame.this.consoleTextArea.setCaretPosition(SudokuConsoleFrame.this.consoleTextArea.getText().length());
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            String s = new String(b, off, len);
            SudokuConsoleFrame.this.consoleTextArea.append(s);
            SudokuConsoleFrame.this.consoleTextArea.setCaretPosition(SudokuConsoleFrame.this.consoleTextArea.getText().length());
        }

        @Override
        public void flush() throws IOException {
        }

        @Override
        public void close() throws IOException {
        }
    }
}
