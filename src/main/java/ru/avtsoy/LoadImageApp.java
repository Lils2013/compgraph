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
            img = ImageIO.read(new File("Lenna.png"));
            toGrayscale(img);
//            thresholding(img);
//            randomDithering(img);
//            orderedDithering2x2(img);
            orderedDithering4x4(img);
        } catch (IOException e) {
        }
    }

    private void toGrayscale(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int rgb = img.getRGB(col, row);
                int r = (rgb>>16)&0xff;
                int g = (rgb>>8)&0xff;
                int b = (rgb)&0xff;
                double intensity = 0.299*r + 0.587*g + 0.114*b;
                int gray = (int) (Math.round(intensity));
                img.setRGB(col,row,new Color(gray, gray, gray).getRGB());
            }
        }
    }

    private void thresholding(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int rgb = img.getRGB(col, row);
                int r = (rgb>>16)&0xff;
                int g = (rgb>>8)&0xff;
                int b = (rgb)&0xff;
                System.out.println(r + " " + g + " " + b);
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
                int rgb = img.getRGB(col, row);
                int r = (rgb>>16)&0xff;
                int g = (rgb>>8)&0xff;
                int b = (rgb)&0xff;
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
                int rgb = img.getRGB(col, row);
                int r = (rgb>>16)&0xff;
                int g = (rgb>>8)&0xff;
                int b = (rgb)&0xff;
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
                int rgb = img.getRGB(col, row);
                int r = (rgb>>16)&0xff;
                int g = (rgb>>8)&0xff;
                int b = (rgb)&0xff;
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