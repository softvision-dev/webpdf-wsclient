package net.webpdf.wsclient.testsuite;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class ImageHelper {

    /**
     * This will compare the pixel-wise RGB values of the given image and the stored image.
     */
    public static double compare(BufferedImage expectedImage, BufferedImage actualImage) throws IOException {
        if (expectedImage == null) {
            throw new IOException("Expected image object is null");
        }

        if (actualImage == null) {
            throw new IOException("Actual image object is null");
        }

        int width1 = actualImage.getWidth();
        int width2 = expectedImage.getWidth();
        int height1 = actualImage.getHeight();
        int height2 = expectedImage.getHeight();

        if ((width1 != width2) || (height1 != height2)) {
            throw new IOException("The width or height of expected and actual image differ");
        }

        double diff = 0.0;
        for (int y = 0; y < height1; y++) {
            for (int x = 0; x < width1; x++) {
                int rgb1 = actualImage.getRGB(x, y);
                int rgb2 = expectedImage.getRGB(x, y);
                int r1 = (rgb1 >> 16) & 0xff;
                int g1 = (rgb1 >> 8) & 0xff;
                int b1 = (rgb1) & 0xff;
                int r2 = (rgb2 >> 16) & 0xff;
                int g2 = (rgb2 >> 8) & 0xff;
                int b2 = (rgb2) & 0xff;
                diff += Math.abs(r1 - r2);
                diff += Math.abs(g1 - g2);
                diff += Math.abs(b1 - b2);
            }
        }
        double n = width1 * height1 * 3;
        double p = diff / n / 255.0;
        return p * 100.0;
    }
}
