package ru.avtsoy;

import org.apache.commons.io.FilenameUtils;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.Objects;
import java.util.Random;
import javax.imageio.*;

public class LoadImageApp {

    private static int THRESHOLD = 130;

    private static BufferedImage toGrayscale(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int rgb = img.getRGB(col, row);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = (rgb) & 0xff;
                if (!(r == g && g == b)) {
                    double intensity = 0.299 * r + 0.587 * g + 0.114 * b;
                    int gray = (int) (Math.round(intensity));
                    img.setRGB(col, row, new Color(gray, gray, gray).getRGB());
                }
            }
        }
        return img;
    }

    private static BufferedImage thresholding(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int rgb = img.getRGB(col, row);
                double intensity = (rgb) & 0xff;
                if (intensity > THRESHOLD) {
                    img.setRGB(col, row, Color.WHITE.getRGB());
                } else {
                    img.setRGB(col, row, Color.BLACK.getRGB());
                }
            }
        }
        return img;
    }

    private static BufferedImage randomDithering(BufferedImage img) {
        Random random = new Random();
        int width = img.getWidth();
        int height = img.getHeight();
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int thr = random.nextInt(255);
                int rgb = img.getRGB(col, row);
                double intensity = (rgb) & 0xff;
                if (intensity > thr) {
                    img.setRGB(col, row, Color.WHITE.getRGB());
                } else {
                    img.setRGB(col, row, Color.BLACK.getRGB());
                }
            }
        }
        return img;
    }

    private static BufferedImage orderedDithering2x2(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        int matrix[][] = {
                {1, 3},
                {4, 2},
        };
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int rgb = img.getRGB(col, row);
                int gray = (rgb) & 0xff;
                int intensity = (int) (gray + 255 * (matrix[col % 2][row % 2] / 4.0 - 1.0 / 2));
                if (intensity > THRESHOLD) {
                    img.setRGB(col, row, Color.WHITE.getRGB());
                } else {
                    img.setRGB(col, row, Color.BLACK.getRGB());
                }
            }
        }
        return img;
    }

    private static BufferedImage orderedDithering4x4(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        int matrix[][] = {
                {0, 8, 2, 10},
                {12, 4, 14, 6},
                {3, 11, 1, 9},
                {15, 7, 13, 5},
        };
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int rgb = img.getRGB(col, row);
                int gray = (rgb) & 0xff;
                int intensity = (int) (gray + 255 * (matrix[col % 4][row % 4] / 16.0 - 1.0 / 2));
                if (intensity > THRESHOLD) {
                    img.setRGB(col, row, Color.WHITE.getRGB());
                } else {
                    img.setRGB(col, row, Color.BLACK.getRGB());
                }
            }
        }
        return img;
    }

    private static BufferedImage errorDiffusion1d(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        int[][] err = new int[width][height];
        int error;
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int rgb = img.getRGB(col, row);
                int gray = (rgb) & 0xff; //assuming image is already in grayscale
                if (gray + err[col][row] > THRESHOLD) {
                    img.setRGB(col, row, Color.WHITE.getRGB());
                    error = gray + err[col][row] - 255;
                } else {
                    img.setRGB(col, row, Color.BLACK.getRGB());
                    error = gray + err[col][row];
                }
                if (col + 1 < width)
                    err[col + 1][row] += error;
            }
        }
        return img;
    }

    private static BufferedImage errorDiffusion1dEvenOdd(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        int[][] err = new int[width][height];
        int error;
        for (int row = 0; row < height; row++) {
            if (row % 2 == 0) {
                for (int col = 0; col < width; col++) {
                    int rgb = img.getRGB(col, row);
                    int gray = (rgb) & 0xff; //assuming image is already in grayscale
                    if (gray + err[col][row] > THRESHOLD) {
                        img.setRGB(col, row, Color.WHITE.getRGB());
                        error = gray + err[col][row] - 255;
                    } else {
                        img.setRGB(col, row, Color.BLACK.getRGB());
                        error = gray + err[col][row];
                    }
                    if (col + 1 < width)
                        err[col + 1][row] += error;
                }
            } else {
                for (int col = width - 1; col >= 0; col--) {
                    int rgb = img.getRGB(col, row);
                    int gray = (rgb >> 16) & 0xff; //assuming image is already in grayscale
                    if (gray + err[col][row] > THRESHOLD) {
                        img.setRGB(col, row, Color.WHITE.getRGB());
                        error = gray + err[col][row] - 255;
                    } else {
                        img.setRGB(col, row, Color.BLACK.getRGB());
                        error = gray + err[col][row];
                    }
                    if (col - 1 >= 0)
                        err[col - 1][row] += error;
                }
            }
        }
        return img;
    }

    private static BufferedImage errorDiffusionFloydSteinberg(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        int[][] err = new int[width][height];
        int error;
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int rgb = img.getRGB(col, row);
                int gray = (rgb) & 0xff; //assuming image is already in grayscale
                if (gray + err[col][row] > THRESHOLD) {
                    img.setRGB(col, row, Color.WHITE.getRGB());
                    error = gray + err[col][row] - 255;
                } else {
                    img.setRGB(col, row, Color.BLACK.getRGB());
                    error = gray + err[col][row];
                }
                if (col + 1 < width)
                    err[col + 1][row] += (7 * error) / 16;
                if (col - 1 >= 0 && row + 1 < height)
                    err[col - 1][row + 1] += (3 * error) / 16;
                if (row + 1 < height)
                    err[col][row + 1] += (5 * error) / 16;
                if (col + 1 < width && row + 1 < height)
                    err[col + 1][row + 1] += (error) / 16;
            }
        }
        return img;
    }

    private static BufferedImage errorDiffusionFloydSteinbergEvenOdd(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        int[][] err = new int[width][height];
        int error;
        for (int row = 0; row < height; row++) {
            if (row % 2 == 0) {
                for (int col = 0; col < width; col++) {
                    int rgb = img.getRGB(col, row);
                    int gray = (rgb) & 0xff; //assuming image is already in greyscale
                    if (gray + err[col][row] > THRESHOLD) {
                        img.setRGB(col, row, Color.WHITE.getRGB());
                        error = gray + err[col][row] - 255;
                    } else {
                        img.setRGB(col, row, Color.BLACK.getRGB());
                        error = gray + err[col][row];
                    }
                    if (col + 1 < width)
                        err[col + 1][row] += (7 * error) / 16;
                    if (col - 1 >= 0 && row + 1 < height)
                        err[col - 1][row + 1] += (3 * error) / 16;
                    if (row + 1 < height)
                        err[col][row + 1] += (5 * error) / 16;
                    if (col + 1 < width && row + 1 < height)
                        err[col + 1][row + 1] += (error) / 16;
                }
            } else {
                for (int col = width - 1; col >= 0; col--) {
                    int rgb = img.getRGB(col, row);
                    int gray = (rgb) & 0xff; //assuming image is already in greyscale
                    if (gray + err[col][row] > THRESHOLD) {
                        img.setRGB(col, row, Color.WHITE.getRGB());
                        error = gray + err[col][row] - 255;
                    } else {
                        img.setRGB(col, row, Color.BLACK.getRGB());
                        error = gray + err[col][row];
                    }
                    if (col - 1 >= 0)
                        err[col - 1][row] += (7 * error) / 16;
                    if (col - 1 >= 0 && row + 1 < height)
                        err[col - 1][row + 1] += (error) / 16;
                    if (row + 1 < height)
                        err[col][row + 1] += (5 * error) / 16;
                    if (col + 1 < width && row + 1 < height)
                        err[col + 1][row + 1] += (3 * error) / 16;
                }
            }
        }
        return img;
    }

    public static void main(String[] args) throws IOException {
            BufferedImage img = ImageIO.read(new File(args[0]));
            img = toGrayscale(img);
            if (args.length > 2) {
                for (int i = 2; i < args.length; i++) {
                    if (Objects.equals(args[i], "-t"))
                        thresholding(img);
                    else if (Objects.equals(args[i], "-rd"))
                        randomDithering(img);
                    else if (Objects.equals(args[i], "-od2"))
                        orderedDithering2x2(img);
                    else if (Objects.equals(args[i], "-od4"))
                        orderedDithering4x4(img);
                    else if (Objects.equals(args[i], "-ed1"))
                        errorDiffusion1d(img);
                    else if (Objects.equals(args[i], "-ed1_eo"))
                        errorDiffusion1dEvenOdd(img);
                    else if (Objects.equals(args[i], "-edfs"))
                        errorDiffusionFloydSteinberg(img);
                    else if (Objects.equals(args[i], "-edfs_eo"))
                        errorDiffusionFloydSteinbergEvenOdd(img);
                    else {
                        System.err.println("Illegal argument: " + args[i]);
                        return;
                    }
                }
            }
            File outputfile = new File(args[1]);
            if (img != null) {
                String extension = FilenameUtils.getExtension(args[1]);
                ImageIO.write(img, extension, outputfile);
                System.out.println("Success! Image was saved to " + args[1]);
            } else {
                System.err.println("ERROR! FAIL");
            }
    }
}