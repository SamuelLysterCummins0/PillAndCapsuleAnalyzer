package com.example.pillandcapsuleanalyserslc;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class ImageProcessor {


    public Image convertToBlackAndWhite(Image originalImage, Color sampleColor, double threshold) {
        int width = (int) originalImage.getWidth();
        int height = (int) originalImage.getHeight();
        WritableImage bwImage = new WritableImage(width, height);
        PixelReader pixelReader = originalImage.getPixelReader();
        PixelWriter pixelWriter = bwImage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = pixelReader.getColor(x, y);
                double distance = color(color, sampleColor);
                if (distance < threshold) {
                    pixelWriter.setColor(x, y, Color.WHITE);
                } else {
                    pixelWriter.setColor(x, y, Color.BLACK);
                }
            }
        }

        return bwImage;
    }

    private double color(Color c1, Color c2) { //color distance between colors
        return Math.sqrt(Math.pow(c1.getRed() - c2.getRed(), 2) +
                Math.pow(c1.getGreen() - c2.getGreen(), 2) +
                Math.pow(c1.getBlue() - c2.getBlue(), 2));
    }
}