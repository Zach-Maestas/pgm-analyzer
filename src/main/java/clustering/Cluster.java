package clustering;

import java.util.ArrayList;
import java.util.stream.Collectors;

import histogram.HistogramUtils;
import pgm.PGMImage;

import java.util.List;

/**
 * Represents a cluster of images, supporting merging, histogram normalization,
 * and partitioned histogram extracttion.
 */
public class Cluster {

    /**
     * List of images in this cluster.
     */
    private List<PGMImage> images = new ArrayList<>();

    /**
     * Histograms for each partition within the cluster's average image.
     */
    private int[][] clusterPartitionHistograms;

    /**
     * Average pixel values across all images in the cluster.
     */
    private int[][] averageImage;

    /**
     * Cumulative histogram representing the entire cluster.
     */
    private int[] clusterHistogram;

    /**
     * Normalized histogram of the cluster.
     */
    private double[] clusterNormHistogram;

    /**
     * Initializes a new cluster with a single image, setting the initial histogram,
     * normalized histogram, and average image to the values of this image.
     * 
     * @param image the initial image to add to this cluster
     */
    public Cluster(PGMImage image) {
        this.images.add(image);
        this.averageImage = image.getPixelsArray();
        this.clusterHistogram = image.getHistogramArray();
        this.clusterNormHistogram = image.getNormalizedHistogram();
    }

    /**
     * Merges another cluster into this cluster, adding all images, updating the
     * cumulative histogram, normalized histogram, and average image.
     * 
     * @param otherCluster the cluster to merge into this cluster
     */
    public void merge(Cluster otherCluster) {
        otherCluster.getImages().forEach(this::addImage);
        updateAverageImage(otherCluster);
        updateClusterHist(otherCluster);
        updateClusterNormHist();
    }

    /**
     * Adds a single image to this cluster, updating the cumulative histogram.
     * 
     * @param image the image to add to this cluster
     */
    public void addImage(PGMImage image) {
        images.add(image);

        int[] imageHistogram = image.getHistogramArray();
        for (int i = 0; i < imageHistogram.length; i++) {
            clusterHistogram[i] += imageHistogram[i];
        }
    }

    /**
     * Updates the histogram of the cluster based on the cumulative
     * pixel data
     */
    private void updateClusterHist(Cluster otherCluster) {
        int[] otherHistogram = otherCluster.clusterHistogram;
        for (int i = 0; i < clusterHistogram.length; i++) {
            clusterHistogram[i] += otherHistogram[i];
        }
    }

    /**
     * Updates the normalized histogram of the cluster based on the cumulative
     * histogram
     */
    private void updateClusterNormHist() {
        clusterNormHistogram = HistogramUtils.normalizeHistogram(clusterHistogram);
    }

    /**
     * Updates the average image of this cluster based on the current images and
     * the images in another cluster being merged.
     * 
     * @param otherCluster the other cluster whose images are being added to this
     *                     cluster
     */
    private void updateAverageImage(Cluster otherCluster) {
        int height = averageImage.length;
        int width = averageImage[0].length;
        int totalImages = images.size(); // Updated after merge
        int previousTotal = totalImages - otherCluster.getImages().size();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int otherPixelSum = otherCluster.getAverageImage()[i][j] * otherCluster.getImages().size();
                averageImage[i][j] = (int) ((averageImage[i][j] * previousTotal + otherPixelSum) / totalImages);
            }
        }
    }

    /**
     * Updates the partitioned histograms of the cluster's average image based on
     * the specified number of partitions.
     * 
     * @param numPartitions the number of partitions to divide the average image
     *                      into
     */
    protected void updateClusterPartitionHistograms(int numPartitions) {
        clusterPartitionHistograms = HistogramUtils.extractSubHistograms(averageImage, numPartitions);
    }

    /**
     * Retrieves the partitioned histograms of the cluster's average image. If the
     * partition histograms haven't been calculated for the specified number of
     * partitions,
     * it calculates them.
     * 
     * @param numPartitions the number of partitions for the average image histogram
     * @return a 2D array representing histograms for each parition of the average
     *         image
     */
    public int[][] getClusterPartitionHistograms(int numPartitions) {
        if (clusterPartitionHistograms == null || clusterPartitionHistograms.length != numPartitions) {
            updateClusterPartitionHistograms(numPartitions);
        }
        return clusterPartitionHistograms;
    }

    /**
     * Returns the average image of the cluster, which contains the average
     * across all images in the cluster.
     * 
     * @return a 2D array of average pixel values for the cluster
     */
    public int[][] getAverageImage() {
        return averageImage;
    }

    /**
     * Returns the normalized histogram of the cluster.
     * 
     * @return an array representing the normalized histogram of the cluster
     */
    public double[] getClusterNormHistogram() {
        return clusterNormHistogram;
    }

    /**
     * Retrieves the list of images in this cluster.
     * 
     * @return a list of images contained in the cluster
     */
    public List<PGMImage> getImages() {
        return images;
    }

    /**
     * Provides a string representation of the cluster, listing the names of images
     * in the cluster sorted alphabetically and separated by spaces.
     * 
     * @return a string representation of the cluster
     */
    @Override
    public String toString() {
        return images.stream()
                .map(PGMImage::toString)
                .sorted()
                .collect(Collectors.joining(" "));
    }
}