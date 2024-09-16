# Pill and Capsule Analyzer

## Overview
This project is a JavaFX application designed to identify pills and capsules in images based on color and pixel size. It uses a union-find algorithm to group and analyze white pixels from the image.

## Features
- **Load Images**: Choose an image file to analyze.
- **Select Color**: Click on the image to select a sample color for detection.
- **Adjust Threshold**: Use a slider to set the color detection threshold.
- **Set Min Group Size**: Define the minimum size of detected groups to consider them valid pills.
- **Analyze Image**: Process the image to identify and mark pills with bounding boxes.
- **Clear and Reset**: Clear the current results and start fresh.
- **Hover Details**: When hovering over detected pills or capsules, view their name, pixel size, and count in the details area.

## How It Works
1. **Convert to Black and White**: The image is converted to black and white based on the selected color and threshold.
2. **Apply Union-Find**: The union-find algorithm groups adjacent white pixels.
3. **Draw Boundaries**: Boundaries of detected pills are calculated and drawn on the image.
4. **Display Results**: Detected pills are labeled and highlighted with rectangles.
5. **Hover Information**: Hover over a detected pill or capsule to see its name, pixel size, and the count of that specific type displayed below.

## Usage
1. **Load an Image**: Click "Load Image" to select an image file.
2. **Select a Sample Color**: Click on the image to choose the color of the pills you want to detect.
3. **Adjust Settings**: Set the color threshold and minimum group size using the sliders.
4. **Analyze**: Click "Analyze" to process the image and identify pills.
5. **View Results**: The detected pills will be highlighted, and their details will be shown. Hover over a pill or capsule to view its name, pixel size, and count.

