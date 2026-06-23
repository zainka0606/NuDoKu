package sudoku;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.math.BigInteger;
import java.util.Enumeration;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SudokuUtil {
    public static String NEW_LINE = System.getProperty("line.separator");
    private static PrinterJob printerJob;
    private static PageFormat pageFormat;

    public static void clearStepListWithNullify(List<SolutionStep> steps) {
        if (steps != null) {
            for (int i = 0; i < steps.size(); i++) {
                steps.get(i).reset();
                steps.set(i, null);
            }

            steps.clear();
        }
    }

    public static void clearStepList(List<SolutionStep> steps) {
        if (steps != null) {
            for (int i = 0; i < steps.size(); i++) {
                steps.set(i, null);
            }

            steps.clear();
        }
    }

    public static int combinations(int n, int k) {
        if (n <= 167) {
            double fakN = 1.0;

            for (int i = 2; i <= n; i++) {
                fakN *= i;
            }

            double fakNMinusK = 1.0;

            for (int i = 2; i <= n - k; i++) {
                fakNMinusK *= i;
            }

            double fakK = 1.0;

            for (int i = 2; i <= k; i++) {
                fakK *= i;
            }

            return (int) (fakN / (fakNMinusK * fakK));
        } else {
            BigInteger fakN = BigInteger.ONE;

            for (int i = 2; i <= n; i++) {
                fakN = fakN.multiply(new BigInteger(i + ""));
            }

            BigInteger fakNMinusK = BigInteger.ONE;

            for (int i = 2; i <= n - k; i++) {
                fakNMinusK = fakNMinusK.multiply(new BigInteger(i + ""));
            }

            BigInteger fakK = BigInteger.ONE;

            for (int i = 2; i <= k; i++) {
                fakK = fakK.multiply(new BigInteger(i + ""));
            }

            fakNMinusK = fakNMinusK.multiply(fakK);
            fakN = fakN.divide(fakNMinusK);
            return fakN.intValue();
        }
    }

    public static PrinterJob getPrinterJob() {
        if (printerJob == null) {
            printerJob = PrinterJob.getPrinterJob();
        }

        return printerJob;
    }

    public static PageFormat getPageFormat() {
        if (pageFormat == null) {
            pageFormat = getPrinterJob().defaultPage();
        }

        return pageFormat;
    }

    public static double adjustGraphicsForPrinting(Graphics2D g2) {
        AffineTransform at = g2.getTransform();
        double[] matrix = new double[6];
        at.getMatrix(matrix);
        double scale = matrix[0];
        if (scale != 0.0) {
            matrix[0] = 1.0;
            matrix[3] = 1.0;
        } else {
            scale = matrix[2];
            matrix[1] = -1.0;
            matrix[2] = 1.0;
        }

        AffineTransform newAt = new AffineTransform(matrix);
        g2.setTransform(newAt);
        return scale;
    }

    public static void setLookAndFeel() {
        LookAndFeelInfo[] lafs = UIManager.getInstalledLookAndFeels();
        boolean found = false;
        String className = Options.getInstance().getLaf();
        String oldClassName = className;
        if (!className.isEmpty()) {
            String lafName = className.substring(className.lastIndexOf(46) + 1);

            for (int i = 0; i < lafs.length; i++) {
                if (lafs[i].getClassName().equals(className)) {
                    found = true;
                    break;
                }

                if (lafs[i].getClassName().endsWith(lafName)) {
                    className = lafs[i].getClassName();
                    Logger.getLogger(Main.class.getName()).log(Level.CONFIG, "laf package changed from {0} to {1}", new Object[]{oldClassName, className});
                    found = true;
                    break;
                }
            }
        }

        if (!found) {
            Options.getInstance().setLaf("");
            className = UIManager.getSystemLookAndFeelClassName();
        } else if (!oldClassName.equals(className)) {
            Options.getInstance().setLaf(className);
        }

        LookAndFeel instance = null;

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            instance = UIManager.getLookAndFeel();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            int fontSize = Options.getInstance().getCustomFontSize();
            if (!Options.getInstance().isUseDefaultFontSize()) {
                UIDefaults def = UIManager.getLookAndFeelDefaults();
                Object value = null;
                if ((value = def.get("defaultFont")) != null) {
                    Font font = (Font) value;
                    if (font.getSize() != fontSize) {
                        def.put("defaultFont", new FontUIResource(font.getName(), font.getStyle(), fontSize));
                    }
                }
            }

            UIManager.setLookAndFeel(instance);
            Logger.getLogger(Main.class.getName()).log(Level.CONFIG, "laf={0}", UIManager.getLookAndFeel().getName());
            if (!Options.getInstance().isUseDefaultFontSize()) {
                UIDefaults def = UIManager.getDefaults();
                Enumeration<Object> keys = def.keys();

                while (keys.hasMoreElements()) {
                    Object key = keys.nextElement();
                    Font font = def.getFont(key);
                    if (font != null && font.getSize() != fontSize) {
                        def.put(key, new FontUIResource(font.getName(), font.getStyle(), fontSize));
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Error changing LaF 3", ex);
        }
    }

    public static void printFontDefaults() {
        System.out.println("Default font settings: UIManager");
        UIDefaults def = UIManager.getDefaults();
        SortedMap<String, String> items = new TreeMap<>();
        Enumeration<Object> keys = def.keys();

        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Font font = def.getFont(key);
            if (font != null) {
                items.put(key.toString(), font.getName() + "/" + font.getStyle() + "/" + font.getSize());
            }
        }

        for (Entry<String, String> act : items.entrySet()) {
            System.out.println("     " + act.getKey() + ": " + act.getValue());
        }
    }

    public static String getSSFormatted(String values) {
        StringBuilder tmp = new StringBuilder();
        values = values.replace('0', '.');
        tmp.append(" *-----------*");
        tmp.append(NEW_LINE);
        writeSSLine(tmp, values, 0);
        writeSSLine(tmp, values, 9);
        writeSSLine(tmp, values, 18);
        tmp.append(" |---+---+---|");
        tmp.append(NEW_LINE);
        writeSSLine(tmp, values, 27);
        writeSSLine(tmp, values, 36);
        writeSSLine(tmp, values, 45);
        tmp.append(" |---+---+---|");
        tmp.append(NEW_LINE);
        writeSSLine(tmp, values, 54);
        writeSSLine(tmp, values, 63);
        writeSSLine(tmp, values, 72);
        tmp.append(" *-----------*");
        tmp.append(NEW_LINE);
        return tmp.toString();
    }

    private static void writeSSLine(StringBuilder tmp, String clues, int startIndex) {
        tmp.append(" |");
        tmp.append(clues.substring(startIndex + 0, startIndex + 3));
        tmp.append("|");
        tmp.append(clues.substring(startIndex + 3, startIndex + 6));
        tmp.append("|");
        tmp.append(clues.substring(startIndex + 6, startIndex + 9));
        tmp.append("|");
        tmp.append(NEW_LINE);
    }

    public static String getSSPMGrid(String grid) {
        String[] parts = grid.split(" ");
        String[] cells = new String[81];
        int maxLength = 0;
        int i = 0;
        int j = 0;

        while (i < parts.length) {
            if (!parts[i].isEmpty()) {
                char ch = parts[i].charAt(0);
                if (Character.isDigit(ch)) {
                    cells[j++] = parts[i];
                    if (parts[i].length() > maxLength) {
                        maxLength = parts[i].length();
                    }
                }
            }

            i++;
        }

        for (int ix = 0; ix < cells.length; ix++) {
            if (cells[ix].length() < maxLength) {
                j = maxLength - cells[ix].length();

                for (int jx = 0; jx < j; jx++) {
                    cells[ix] = cells[ix] + " ";
                }
            }
        }

        StringBuilder tmp = new StringBuilder();
        writeSSPMFrameLine(tmp, maxLength, true);
        writeSSPMLine(tmp, cells, 0);
        writeSSPMLine(tmp, cells, 9);
        writeSSPMLine(tmp, cells, 18);
        writeSSPMFrameLine(tmp, maxLength, false);
        writeSSPMLine(tmp, cells, 27);
        writeSSPMLine(tmp, cells, 36);
        writeSSPMLine(tmp, cells, 45);
        writeSSPMFrameLine(tmp, maxLength, false);
        writeSSPMLine(tmp, cells, 54);
        writeSSPMLine(tmp, cells, 63);
        writeSSPMLine(tmp, cells, 72);
        writeSSPMFrameLine(tmp, maxLength, true);
        return tmp.toString();
    }

    private static void writeSSPMLine(StringBuilder tmp, String[] cells, int index) {
        tmp.append(" | ");
        tmp.append(cells[index + 0]);
        tmp.append("  ");
        tmp.append(cells[index + 1]);
        tmp.append("  ");
        tmp.append(cells[index + 2]);
        tmp.append("  | ");
        tmp.append(cells[index + 3]);
        tmp.append("  ");
        tmp.append(cells[index + 4]);
        tmp.append("  ");
        tmp.append(cells[index + 5]);
        tmp.append("  | ");
        tmp.append(cells[index + 6]);
        tmp.append("  ");
        tmp.append(cells[index + 7]);
        tmp.append("  ");
        tmp.append(cells[index + 8]);
        tmp.append("  |");
        tmp.append(NEW_LINE);
    }

    private static void writeSSPMFrameLine(StringBuilder tmp, int maxLength, boolean outer) {
        tmp.append(" *");

        for (int i = 0; i < 3 * maxLength + 7; i++) {
            tmp.append("-");
        }

        if (outer) {
            tmp.append("-");
        } else {
            tmp.append("+");
        }

        for (int i = 0; i < 3 * maxLength + 7; i++) {
            tmp.append("-");
        }

        if (outer) {
            tmp.append("-");
        } else {
            tmp.append("+");
        }

        for (int i = 0; i < 3 * maxLength + 7; i++) {
            tmp.append("-");
        }

        if (outer) {
            tmp.append("*");
        } else {
            tmp.append("|");
        }

        tmp.append(NEW_LINE);
    }

    public static String getCandString(int candidate) {
        return Options.getInstance().isShowColorKuAct() ? String.valueOf(candidate) : String.valueOf(candidate);
    }

    public static void main(String[] args) {
        String grid = ".---------------.------------.-------------.| 1   78    38  | 2   49  6  | 47  39  5   || 9   67    5   | 3   8   14 | 47  16  2   || 36  4     2   | 19  7   5  | 8   36  19  |:---------------+------------+-------------:| 8   9     7   | 5   6   2  | 13  4   13  || 25  25    1   | 4   3   8  | 9   7   6   || 4   3     6   | 7   1   9  | 5   2   8   |:---------------+------------+-------------:| 36  16    4   | 8   5   7  | 2   19  139 || 7   158   89  | 19  2   3  | 6   58  4   || 25  1258  389 | 6   49  14 | 3   58  7   |'---------------'------------'-------------'";
        getSSPMGrid(grid);
    }
}
