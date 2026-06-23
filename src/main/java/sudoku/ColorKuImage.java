package sudoku;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ColorKuImage extends BufferedImage {
    private static final int IMG_MIN = 10;
    private static final int IMG_MAX = 98;
    private static final int IMG_FACTOR = 4;
    private static BufferedImage sourceOverlay = null;
    private static BufferedImage lastOverlay = null;
    private Color color = null;

    public ColorKuImage(int size, Color color) {
        super(size, size, 6);
        this.color = color;
        this.createImage();
    }

    private void createImage() {
        long ticks = System.nanoTime();
        int sizeR = this.getWidth();
        if (sourceOverlay == null) {
            try {
                sourceOverlay = ImageIO.read(this.getClass().getResource("/img/ov078.png"));
            } catch (IOException ex) {
                Logger.getLogger(ColorKuImage.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
        }

        if (lastOverlay == null || lastOverlay != null && lastOverlay.getWidth() != sizeR) {
            lastOverlay = this.getScaledInstance(sourceOverlay, sizeR);
        }

        Graphics2D g2 = this.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(this.color);
        int delta = sizeR / 28;
        g2.fillOval(delta, 0, sizeR - delta, sizeR - delta);
        g2.drawImage(lastOverlay, 0, 0, null);
        ticks = System.nanoTime() - ticks;
    }

    private BufferedImage getScaledInstance(BufferedImage img, int targetSize) {
        BufferedImage ret = img;
        int size = img.getWidth();

        do {
            if (size > targetSize) {
                size /= 2;
                if (size < targetSize) {
                    size = targetSize;
                }
            } else {
                size = targetSize;
            }

            BufferedImage tmp = new BufferedImage(size, size, 2);
            Graphics2D g2 = tmp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.drawImage(ret, 0, 0, size, size, null);
            g2.dispose();
            ret = tmp;
        } while (size != targetSize);

        return ret;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
