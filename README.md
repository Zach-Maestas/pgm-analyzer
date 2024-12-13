
# PGM Image Clustering and Perceptron Analyzer

## Overview
This project implements a **clustering algorithm and perceptron-based classifier** designed to analyze and classify grayscale images in the PGM (Portable Gray Map) format. The program is capable of:

- Performing clustering using various similarity measures.
- Training and applying perceptrons to classify PGM images based on histogram data.
- Computing clustering quality through metrics like normalized histograms and similarity scores.

---

## Key Features

### 1. **PGM Image Processing**
- Supports reading and parsing PGM files.
- Computes histograms for pixel intensity distributions.
- Normalizes histograms for comparison and analysis.

### 2. **Clustering Algorithms**
- Implements a general clustering framework (`ClusteringAlgorithm`) for grouping images based on similarity.
- Specializations include:
  - **Agglomerative Clustering**: Hierarchical clustering that merges clusters iteratively.
  - **Custom Similarity Measures**: Clusters are compared using custom similarity metrics derived from histograms.

### 3. **Perceptron Training and Classification**
- Trains binary perceptrons to classify images into target classes based on their histogram features.
- Supports multi-class classification by training multiple perceptrons, one per class.
- Uses normalized histograms as input features for training and scoring.

### 4. **Clustering Quality Analysis**
- Evaluates clustering results using:
  - Histogram-based similarity measures.
  - Metrics like intersection and normalized difference.
- Outputs results with precision for clear evaluation.

---

## Project Structure

```
src/
├── clustering/
│   ├── Cluster.java                # Abstract base class for clusters
│   ├── PGMCluster.java             # Specialized cluster for PGM images
│   ├── ClusteringAlgorithm.java    # Abstract base class for clustering algorithms
│   ├── PerceptronClustering.java   # Clustering using perceptrons
│   ├── AgglomerativeClustering.java# Agglomerative clustering implementation
│   └── ...                         # Additional clustering-related classes
│
├── image/
│   ├── Image.java                  # Abstract base class for images
│   ├── PGMImage.java               # PGM image implementation
│   └── PNGImage.java (Optional)    # Future extension for PNG images
│
├── perceptron/
│   ├── Perceptron.java             # Binary perceptron for image classification
│   └── ...                         # Additional perceptron-related utilities
│
├── histogram/
│   ├── HistogramUtils.java         # Utilities for histogram computation
│   └── ...                         # Histogram-related functionality
│
└── AppRunner.java                  # Main entry point for the program
```

---

## How It Works

### 1. **Clustering Process**
- Each image is initially treated as its own cluster.
- Clusters are merged iteratively based on their similarity until a target number of clusters is reached.
- Similarity is computed using normalized histograms.

### 2. **Perceptron Training**
- Perceptrons are trained on PGM image histograms to classify images into target classes.
- The perceptron updates weights iteratively over multiple epochs to minimize classification error.

### 3. **Clustering Evaluation**
- Outputs clustering results, including:
  - Final cluster compositions.
  - Quality metrics based on histogram similarity.
- Allows fine-tuning of similarity measures and clustering parameters.

---

## Usage

### 1. **Setup**
1. Clone the repository:
   ```bash
   git clone https://github.com/<your-username>/<repo-name>.git
   ```
2. Compile the project:
   ```bash
   javac src/**/*.java
   ```
3. Run the main application:
   ```bash
   java src/AppRunner <training-images-file> <test-images-file> <target-num-clusters>
   ```

### 2. **Input Files**
- Provide PGM images as input in plain-text format with pixel intensity values.
- Example PGM file:
  ```
  P2
  # Example PGM Image
  5 5
  255
  0 0 0 0 0
  0 255 255 255 0
  0 255 0 255 0
  0 255 255 255 0
  0 0 0 0 0
  ```

### 3. **Results**
- Outputs clustering results to the console, including:
  - Cluster assignments.
  - Perceptron scores for each cluster.
  - Clustering quality metrics.

---

## Example Output
```
java src/AppRunner "'input_files/train/train.txt' 'input_files/test/test.txt'  '2'" :
class0_1.pgm class0_7.pgm class0_8.pgm class0_9.pgm 
class0_10.pgm class0_11.pgm class0_12.pgm class0_13.pgm class0_14.pgm class0_15.pgm class0_2.pgm class0_3.pgm class0_4.pgm class0_5.pgm class0_6.pgm

java src/AppRunner "'input_files/train/train.txt' 'input_files/test/test.txt'  '3'" :
class0_1.pgm class0_7.pgm class0_8.pgm class0_9.pgm 
class0_10.pgm class0_11.pgm class0_12.pgm class0_13.pgm class0_2.pgm class0_3.pgm class0_4.pgm class0_5.pgm class0_6.pgm 
class0_14.pgm class0_15.pgm
```

---

## Future Enhancements
- Add support for colored image formats like PNG.
- Implement additional clustering metrics.
- Create a web interface using Node.js to visualize clustering results.

---

## Contributions
Contributions are welcome! Please fork the repository and submit a pull request for any improvements or suggestions.

---

## Acknowledgments
This project was originally part of a GitHub Classroom assignment and has been refined for my personal portfolio. Special thanks to the course instructors and teaching assistants for their guidance.
