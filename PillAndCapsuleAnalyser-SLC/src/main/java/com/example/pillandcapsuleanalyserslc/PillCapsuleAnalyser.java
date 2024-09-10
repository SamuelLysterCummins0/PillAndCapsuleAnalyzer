package com.example.pillandcapsuleanalyserslc;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PillCapsuleAnalyser {
    private ImageProcessor imageProcessor;
    private UnionFind unionFind;
    private int imageWidth;
    private int imageHeight;
    private int minGroupSize = 1000;

    private WritableImage resultImage = null;
    private Boundary[] boundaries;



    public PillCapsuleAnalyser() {
        this.imageProcessor = new ImageProcessor();
    }


    public Image analyzeImage(Image originalImage, Color sampleColor, double threshold, String pillName) {
        imageWidth = (int) originalImage.getWidth();
        imageHeight = (int) originalImage.getHeight();
        this.unionFind = new UnionFind(imageWidth * imageHeight);

        // convert image to black and white
        Image bwImage = imageProcessor.convertToBlackAndWhite(originalImage, sampleColor, threshold);
        PixelReader bwReader = bwImage.getPixelReader();
        // set up arrays to keep track of the size and boundaries of each group of white pixels.
        this.boundaries = new Boundary[imageWidth * imageHeight];
        for (int i = 0; i < imageWidth * imageHeight; i++) {
            boundaries[i] = new Boundary(); // initialize boundaries
        }

        // Apply union find algorithm to group adjacent white pixels
        for (int y = 0; y < imageHeight; y++) {
            for (int x = 0; x < imageWidth; x++) {
                if (bwReader.getColor(x, y).equals(Color.WHITE)) {
                    int index = x + y * imageWidth; //flat array
                    whitePixels(bwReader, index, x, y);
                }
            }
        }

        // calculate the boundaries for each component found by union find
        calculateBoundaries(bwReader, boundaries);

        //writable image for drawing the results
        if (resultImage == null) {
            resultImage = new WritableImage(imageWidth, imageHeight);
        }
        PixelWriter writer= resultImage.getPixelWriter();
        PixelReader originalReader = originalImage.getPixelReader();
            for (int y = 0; y < imageHeight; y++) {
                for (int x = 0; x < imageWidth; x++) {
                    writer.setColor(x, y, originalReader.getColor(x, y));
                }
            }


        // identify groups and draw rectangles
        for (int i = 0; i < boundaries.length; i++) {
            int root = unionFind.find(i);
            if (i == root) { //check if i is the root
                int size = unionFind.getSize(i);
                if (size > minGroupSize && !isRootOfExistingPill(i)) {
                    Pill pill = new Pill(pillName, boundaries[i], size);
                    pillList.add(pill);
                    sequenceNumbersAndSort();
                }
            }
        }

        drawRectangles(writer);

        return resultImage;
    }

    public void reset(Canvas textCanvas, Image originalImage) {
        // Clear the pill list
        pillList.clear();

        // Re-initialize boundaries
        boundaries = new Boundary[imageWidth * imageHeight];
        for (int i = 0; i < imageWidth * imageHeight; i++) {
            boundaries[i] = new Boundary();
        }

        // Reset Union-Find
        unionFind = new UnionFind(imageWidth * imageHeight);

        // Create a new writable image
        resultImage = new WritableImage(imageWidth, imageHeight);
        PixelWriter writer = resultImage.getPixelWriter();
        PixelReader originalReader = originalImage.getPixelReader();

        for (int y = 0; y < imageHeight; y++) {
            for (int x = 0; x < imageWidth; x++) {
                writer.setColor(x, y, originalReader.getColor(x, y));
            }
        }

        // Update the canvas to reflect the new writable image
        GraphicsContext gc = textCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, textCanvas.getWidth(), textCanvas.getHeight()); // Clear any previous drawings on the canvas
    }





    public void setMinGroupSize(int minGroupSize) {
        this.minGroupSize = minGroupSize;
    }
    private boolean isRootOfExistingPill(int index) {
        for (Pill pill : pillList) {
            if (pill.getBoundary().isInside(boundaries[index].minX, boundaries[index].minY)
                    || pill.getBoundary().isInside(boundaries[index].maxX, boundaries[index].maxY)) {
                return true;
            }
        }
        return false;
    }


    private void whitePixels(PixelReader bwReader, int currentIndex, int x, int y){
        if (x > 0 && bwReader.getColor(x - 1, y).equals(Color.WHITE)) {
            unionFind.union(currentIndex, currentIndex - 1);
        }
        if (y > 0 && bwReader.getColor(x, y - 1).equals(Color.WHITE)) {
            unionFind.union(currentIndex, currentIndex - imageWidth);
        }
        if (x < imageWidth - 1 && bwReader.getColor(x + 1, y).equals(Color.WHITE)) {
            unionFind.union(currentIndex, currentIndex + 1);
        }
        if (y < imageHeight - 1 && bwReader.getColor(x, y + 1).equals(Color.WHITE)) {
            unionFind.union(currentIndex, currentIndex + imageWidth);
        }
    }
    private void calculateBoundaries(PixelReader reader, Boundary[] boundaries) {
        for (int y = 0; y < imageHeight; y++) {
            for (int x = 0; x < imageWidth; x++) {
                int index = x + y * imageWidth;
                int root = unionFind.find(index);
                if (reader.getColor(x, y).equals(Color.WHITE)) {
                    // update the boundary of the root
                    boundaries[root].updateBoundary(x, y);
                }
            }
        }
    }

    private void drawRectangles(PixelWriter writer) {
        for (Pill pill : pillList) {
            if (pill.getSize() > minGroupSize) {
                Boundary boundary = pill.getBoundary();
                for (int x = boundary.minX; x <= boundary.maxX; x++) {
                    writer.setColor(x, boundary.minY, Color.RED);
                    writer.setColor(x, boundary.maxY, Color.RED);
                }
                for (int y = boundary.minY; y <= boundary.maxY; y++) {
                    writer.setColor(boundary.minX, y, Color.RED);
                    writer.setColor(boundary.maxX, y, Color.RED);
                }
            }
        }
    }

     public void drawSequenceNumbers(Canvas textCanvas) {
         GraphicsContext gc = textCanvas.getGraphicsContext2D();
         gc.clearRect(0, 0, textCanvas.getWidth(), textCanvas.getHeight());
         gc.setFill(Color.BLUE);

             for (Pill pill : pillList) {
                 if (pill.getSize() > minGroupSize) {
                     Boundary boundary = pill.getBoundary();
                     //  bottom right of the boundary
                     int textX = boundary.maxX - 10;
                     int textY = boundary.maxY - 5;
                     String sequenceNumberText = String.valueOf(pill.getSequenceNumber());
                     gc.fillText(sequenceNumberText, textX, textY);
                 }
             }
         }



    private void sequenceNumbersAndSort(){
     pillList.sort(Comparator.comparingInt(pill -> pill.getBoundary().minY));
       int sequenceNumber = 1;
     for(Pill pill: pillList){
          pill.setSequenceNumber(sequenceNumber++);
      }
      }





    public int getPillCount(String name) {
        int count = 0;
        for (Pill pill : pillList) {
            if (pill.getName().equals(name)) {
                count++;
            }
        }
        return count;
    }

    public List<Pill> getPills() {
        return pillList;
    }
    public static class Boundary {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        public void updateBoundary(int x, int y) {
            if (x < minX) minX = x;
            if (y < minY) minY = y;
            if (x > maxX) maxX = x;
            if (y > maxY) maxY = y;
        }

        public boolean isInside(int x, int y) {
            return x >= minX && x <= maxX && y >= minY && y <= maxY;
        }

    }



    public List<Pill> findPillsAt(int x, int y) {
        List<Pill> foundPills = new ArrayList<>();
        for (Pill pill : pillList) {
            if (pill.boundary.isInside(x, y)) {
                foundPills.add(pill);
            }
        }
        return foundPills;
    }


    private List<Pill> pillList = new ArrayList<>();


    public class Pill{
        String name;
        Boundary boundary;
        int size;
          int sequenceNumber;

        public Pill(String name, Boundary boundary, int size){

            if(name !=null){
                this.name = name;
            }
            else{
                this.name = "n/a";
            }
            this.boundary = boundary;
            this.size = size;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getSize() {
            return size;
        }

        public Boundary getBoundary() {
            return boundary;
        }

        public int getSequenceNumber() {
             return sequenceNumber;
        }

         public void setSequenceNumber(int sequenceNumber) {
            this.sequenceNumber = sequenceNumber;
         }
    }
}
