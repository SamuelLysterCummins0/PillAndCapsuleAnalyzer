package com.example.pillandcapsuleanalyserslc;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
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
            originalImage = new Image(file.toURI().toString());
            photoView.setImage(originalImage);
            bwImageView.setImage(null);
        }
    }


    @FXML
    public void onImageClicked(MouseEvent event) {
        if (originalImage == null) return;

        double imageViewWidth = photoView.getFitWidth();
        double imageViewHeight = photoView.getFitHeight();

        if(imageViewWidth <= 0) imageViewWidth = photoView.getLayoutBounds().getWidth();
        if(imageViewHeight <= 0) imageViewHeight = photoView.getLayoutBounds().getHeight();

        double scaleX = imageViewWidth / originalImage.getWidth();
        double scaleY = imageViewHeight / originalImage.getHeight();


        int x = (int) (event.getX() / scaleX);
        int y = (int) (event.getY() / scaleY);

        selectedSampleColor = originalImage.getPixelReader().getColor(x, y);
        colorDisplay.setFill(selectedSampleColor);
    }

    @FXML
    public void onClearClicked(ActionEvent event){
        pillCapsuleAnalyser.reset(textOverlayCanvas, originalImage);

        photoView.setImage(null);
    }





    @FXML
    public void onAnalyzeClicked(ActionEvent event) {
        String pillName = pillNameField.getText();
        if (pillName.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please enter a pill name.");
             alert.showAndWait();
            return ;
        }

        if (photoView.getImage() != null) {

            photoView.setImage(originalImage);

            Image bwImage = imageProcessor.convertToBlackAndWhite(photoView.getImage(), selectedSampleColor, colorThreshold);
            bwImageView.setImage(bwImage);

            Image resultImage = pillCapsuleAnalyser.analyzeImage(photoView.getImage(), selectedSampleColor, colorThreshold, pillName);
            photoView.setImage(resultImage);
            pillCapsuleAnalyser.drawSequenceNumbers(textOverlayCanvas);


        }
    }

    private void updatePillInformationDisplay(MouseEvent event) {
        if (originalImage == null) return;

        double scaleX = originalImage.getWidth() / photoView.getFitWidth();
        double scaleY = originalImage.getHeight() / photoView.getFitHeight();

        int x = (int) (event.getX() * scaleX);
        int y = (int) (event.getY() * scaleY);

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
