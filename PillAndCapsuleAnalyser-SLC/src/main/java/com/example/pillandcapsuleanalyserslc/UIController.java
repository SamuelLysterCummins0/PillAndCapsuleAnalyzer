package com.example.pillandcapsuleanalyserslc;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;

public class UIController {
    @FXML
    private ImageView photoView;

    @FXML
    private ImageView bwImageView;

    @FXML
    private Rectangle colorDisplay;

    @FXML
    private MenuItem loadImageMenuItem;

    @FXML
    private Slider thresholdSlider;

    @FXML
    private Image originalImage;

    @FXML
    private Label thresholdValueLabel;

    private Color selectedSampleColor;

    @FXML
    private Label pillNameLabel;
    @FXML
    private Label pillSizeLabel;

    @FXML
    private TextField pillNameField;
    @FXML
    private StackPane imageContainer;
    @FXML
    private Label countLabel;
    @FXML
    private Canvas textOverlayCanvas;
    @FXML
    private Slider minGroupSizeSlider;
    @FXML
    private Label minGroupSizeValueLabel;

    private ImageProcessor imageProcessor = new ImageProcessor();

    private PillCapsuleAnalyser pillCapsuleAnalyser = new PillCapsuleAnalyser();
    private double colorThreshold = 0.20;

    public UIController() {

    }

    public void initialize() {
        photoView.setOnMouseMoved(this::updatePillInformationDisplay);
        photoView.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
            textOverlayCanvas.setWidth(newBounds.getWidth());
            textOverlayCanvas.setHeight(newBounds.getHeight());
        });
    }

    @FXML
    public void onThresholdChanged() {
        colorThreshold = thresholdSlider.getValue();
        thresholdValueLabel.setText(String.format("%.2f", colorThreshold));
    }

    @FXML
    public void onMinGroupSizeChanged() {
        int minGroupSize = (int) minGroupSizeSlider.getValue();
        minGroupSizeValueLabel.setText(String.format("Min Group Size: %d", minGroupSize));
        pillCapsuleAnalyser.setMinGroupSize(minGroupSize);
    }

    public void loadImage() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image files (*.png, *.jpg, *.gif)", "*.png", "*.jpg", "*.gif");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            // Clear previous image 
            photoView.setImage(null);
            bwImageView.setImage(null);
            GraphicsContext gc = textOverlayCanvas.getGraphicsContext2D();
            gc.clearRect(0, 0, textOverlayCanvas.getWidth(), textOverlayCanvas.getHeight());
            
            originalImage = new Image(file.toURI().toString());
            photoView.setImage(originalImage);
            
            selectedSampleColor = null;
            colorDisplay.setFill(Color.WHITE);
        }
    }


    @FXML
    public void onImageClicked(MouseEvent event) {
        if (originalImage == null) return;

        double imageViewX = photoView.getLayoutX();
        double imageViewY = photoView.getLayoutY();
        
        
        double displayedWidth = photoView.getBoundsInLocal().getWidth();
        double displayedHeight = photoView.getBoundsInLocal().getHeight();
        
        
        double scaleX = originalImage.getWidth() / displayedWidth;
        double scaleY = originalImage.getHeight() / displayedHeight;
        
        
        double mouseX = event.getX();
        double mouseY = event.getY();
        
        // Convert to image coordinates
        int x = (int) (mouseX * scaleX);
        int y = (int) (mouseY * scaleY);

        // Add bounds checking
        if (x >= 0 && x < originalImage.getWidth() && y >= 0 && y < originalImage.getHeight()) {
            try {
                selectedSampleColor = originalImage.getPixelReader().getColor(x, y);
                colorDisplay.setFill(selectedSampleColor);
            } catch (Exception e) {
                System.err.println("Error reading pixel at " + x + "," + y + ": " + e.getMessage());
            }
        }
    }

    @FXML
    public void onClearClicked(ActionEvent event) {
        photoView.setImage(null);
        bwImageView.setImage(null);
        originalImage = null;
        
        pillNameField.setText("");
        pillNameLabel.setText("Pill Name: ");
        pillSizeLabel.setText("Pill Size: ");
        countLabel.setText("Count: ");
        
        GraphicsContext gc = textOverlayCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, textOverlayCanvas.getWidth(), textOverlayCanvas.getHeight());
        
        pillCapsuleAnalyser = new PillCapsuleAnalyser();
    }





    @FXML
    public void onAnalyzeClicked(ActionEvent event) {
        String pillName = pillNameField.getText();
        if (pillName.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please enter a pill name.");
            alert.showAndWait();
            return;
        }

        if (originalImage != null && selectedSampleColor != null) {
            GraphicsContext gc = textOverlayCanvas.getGraphicsContext2D();
            gc.clearRect(0, 0, textOverlayCanvas.getWidth(), textOverlayCanvas.getHeight());
            
            photoView.setImage(originalImage);
            
            // Perform analysis
            Image bwImage = imageProcessor.convertToBlackAndWhite(originalImage, selectedSampleColor, colorThreshold);
            bwImageView.setImage(bwImage);
            
            Image resultImage = pillCapsuleAnalyser.analyzeImage(originalImage, selectedSampleColor, colorThreshold, pillName);
            if (resultImage != null) {
                photoView.setImage(resultImage);
                pillCapsuleAnalyser.drawSequenceNumbers(textOverlayCanvas);
            }
        }
    }

    private void updatePillInformationDisplay(MouseEvent event) {
        if (originalImage == null) return;

        double displayedWidth = photoView.getBoundsInLocal().getWidth();
        double displayedHeight = photoView.getBoundsInLocal().getHeight();
        
        double scaleX = originalImage.getWidth() / displayedWidth;
        double scaleY = originalImage.getHeight() / displayedHeight;
        
        double mouseX = event.getX();
        double mouseY = event.getY();
        
        int x = (int) (mouseX * scaleX);
        int y = (int) (mouseY * scaleY);

        if (x >= 0 && x < originalImage.getWidth() && y >= 0 && y < originalImage.getHeight()) {
            List<PillCapsuleAnalyser.Pill> foundPills = pillCapsuleAnalyser.findPillsAt(x, y);
            if (!foundPills.isEmpty()) {
                PillCapsuleAnalyser.Pill pill = foundPills.get(0);
                pillNameLabel.setText("Pill Name: " + pill.getName());
                pillSizeLabel.setText("Pill Size: " + pill.getSize());
                int count = pillCapsuleAnalyser.getPillCount(pill.getName());
                countLabel.setText("Count for " + pill.getName() + ": " + count);
            } else {
                pillNameLabel.setText("Pill Name: ");
                pillSizeLabel.setText("Pill Size: ");
                countLabel.setText("Count: ");
            }
        }
    }

}
