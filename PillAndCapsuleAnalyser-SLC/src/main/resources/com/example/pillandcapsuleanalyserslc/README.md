
Pill and Capsule Analyzer
Overview
This project is a JavaFX application designed to identify pills and capsules in images based on color and pixel size. It uses a union-find algorithm to group and analyze white pixels from the image.

Features
Load Images: Choose an image file to analyze.
Select Color: Click on the image to select a sample color for detection.

Adjust Threshold: Use a slider to set the color detection threshold.

Set Min Group Size: Define the minimum size of detected groups to consider them valid pills.

Analyze Image: Process the image to identify and mark pills with bounding boxes.

Clear and Reset: Clear the current results and start fresh.

How It Works

Convert to Black and White: The image is converted to black and white based on the selected color and threshold.
Apply Union-Find: The union-find algorithm groups adjacent white pixels.

Draw Boundaries: Boundaries of detected pills are calculated and drawn on the image.

Display Results: Detected pills are labeled and highlighted with rectangles.

Usage

Load an Image: Click "Load Image" to select an image file.

Select a Sample Color: Click on the image to choose the color of the pills you want to detect.

Adjust Settings: Set the color threshold and minimum group size using the sliders.

Analyze: Click "Analyze" to process the image and identify pills.

View Results: The detected pills will be highlighted, and their details will be shown.