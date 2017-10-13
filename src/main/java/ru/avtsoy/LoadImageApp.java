package ru.avtsoy;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.Random;
import javax.imageio.*;
import javax.swing.*;

/**
 * This class demonstrates how to load an Image from an external file
 */
public class LoadImageApp extends Component {

    BufferedImage img;

    private int THRESHOLD = 130;

    public void paint(Graphics g) {
        g.drawImage(img, 0, 0, null);
    }

    public LoadImageApp() {
        try {
            img = ImageIO.read(new File("linear_gradient_east.png"));
//            thresholding(img);
//            randomDithering(img);
//            orderedDithering2x2(img);
            orderedDithering4x4(img);
        } catch (IOException e) {
        }
    }

    private void thresholding(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                Color color = new Color(img.getRGB(col, row), false);
                int r = color.getRed();
                int g = color.getGreen();
                int b = color.getBlue();
                double intensity = 0.299*r + 0.587*g + 0.114*b;
                if (intensity > THRESHOLD) {
                    img.setRGB(col,row,Color.WHITE.getRGB());
                } else {
                    img.setRGB(col,row,Color.BLACK.getRGB());
                }
            }
        }
    }

    private void randomDithering(BufferedImage img) {
        Random random = new Random();
        int width = img.getWidth();
        int height = img.getHeight();
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int thr = random.nextInt(255);
                Color color = new Color(img.getRGB(col, row), false);
                int r = color.getRed();
                int g = color.getGreen();
                int b = color.getBlue();
                double intensity = 0.299*r + 0.587*g + 0.114*b;
                if (intensity > thr) {
                    img.setRGB(col,row,Color.WHITE.getRGB());
                } else {
                    img.setRGB(col,row,Color.BLACK.getRGB());
                }
            }
        }
    }

    private void orderedDithering2x2(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        int matrix[][] = {
                { 1, 3 },
                { 4, 2 },
        };
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                Color color = new Color(img.getRGB(col, row), false);
                int r = color.getRed();
                int g = color.getGreen();
                int b = color.getBlue();
                int gray = (r+g+b)/3;
                int lol = (int)(gray + 255*(matrix[col % 2][row % 2] / 4.0 - 1.0/2));
                if (lol > THRESHOLD) {
                    img.setRGB(col,row,Color.WHITE.getRGB());
                } else {
                    img.setRGB(col,row,Color.BLACK.getRGB());
                }
            }
        }
    }

    private void orderedDithering4x4(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        int matrix[][] = {
                { 0, 8, 2, 10 },
                { 12, 4, 14, 6 },
                { 3, 11, 1, 9 },
                { 15, 7, 13, 5 },
        };
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                Color color = new Color(img.getRGB(col, row), false);
                int r = color.getRed();
                int g = color.getGreen();
                int b = color.getBlue();
                int gray = (r+g+b)/3;
                int lol = (int)(gray + 255*(matrix[col % 4][row % 4] / 16.0 - 1.0/2));
                if (lol > THRESHOLD) {
                    img.setRGB(col,row,Color.WHITE.getRGB());
                } else {
                    img.setRGB(col,row,Color.BLACK.getRGB());
                }
            }
        }
    }


    public Dimension getPreferredSize() {
        if (img == null) {
            return new Dimension(100,100);
        } else {
            return new Dimension(img.getWidth(null), img.getHeight(null));
        }
    }

    public static void main(String[] args) {

        JFrame f = new JFrame("Load Image Sample");

        f.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        f.add(new LoadImageApp());
        f.pack();
        f.setVisible(true);
    }


}