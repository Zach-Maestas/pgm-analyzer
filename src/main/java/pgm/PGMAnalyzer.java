package pgm;

import java.util.List;

import clustering.AgglomerativeClustering;
import clustering.ClusteringAlgorithm;
import clustering.InvSquareDiffClustering;
import clustering.NinthClustering;
import clustering.PerceptronClustering;
import clustering.QuarterClustering;
import pairwise_similarity.PairwiseSimilarity;

/**
 * Analyzes a list of PGM images, providing methods for clustering analysis
 * and pairwise similarity analysis. Also supports pixel-by-pixel comparison
 * of images for more detailed analysis.
 */
public class PGMAnalyzer {

    /**
     * List of PGM images to be analyzed.
     */
    private List<PGMImage> pgmImages;

    /**
     * Constructs a PGMAnalyzer with a specified list of PGM images.
     *
     * @param pgmImages the list of PGM images to be analyzed
     * @throws IllegalArgumentException if the list is null or empty
     */
    public PGMAnalyzer(List<PGMImage> pgmImages) throws IllegalArgumentException {
        if (pgmImages == null || pgmImages.isEmpty()) {
            throw new IllegalArgumentException("No images to analyze");
        }
        this.pgmImages = pgmImages;
    }

    /**
     * Performs clustering analysis on the PGM images using a specified similarity
     * measure.
     * Supports multiple clustering algorithms based on the similarity measure.
     *
     * @param targetNumClusters the target number of clusters for the analysis
     * @param similarityMeasure the similarity measure (1 to 4) to use for
     *                          clustering:
     *                          1 - AgglomerativeClustering
     *                          2 - QuarterClustering
     *                          3 - InvSquareDiffClustering
     *                          4 - NinthClustering
     * @return a ClusteringAlgorithm object representing the clustering results
     * @throws IllegalArgumentException if an invalid similarity measure is provided
     */
    public ClusteringAlgorithm performClusteringAnalysis(int targetNumClusters, int similarityMeasure)
            throws IllegalArgumentException {
        ClusteringAlgorithm clustering;
        switch (similarityMeasure) {
            case 1:
                clustering = new AgglomerativeClustering(targetNumClusters, pgmImages);
                break;
            case 2:
                clustering = new QuarterClustering(targetNumClusters, pgmImages);
                break;
            case 3:
                clustering = new InvSquareDiffClustering(targetNumClusters, pgmImages);
                break;
            case 4:
                clustering = new NinthClustering(targetNumClusters, pgmImages);
                break;
            default:
                throw new IllegalArgumentException("Invalid similarity measure");
        }
        return clustering;
    }

    public PerceptronClustering performPerceptronClustering(int targetNumClusters, List<PGMImage> trainingImages, List<PGMImage> testImages){
        return new PerceptronClustering(targetNumClusters, trainingImages, testImages);
    }

    /**
     * Analyzes the pairwise similarities between the PGM images, returning a
     * PairwiseSimilarity object with the similarity results.
     *
     * @return a PairwiseSimilarity object containing pairwise similarity data
     */
    public PairwiseSimilarity analyzePairwiseSimilarities() {
        return new PairwiseSimilarity(pgmImages);
    }

    /**
     * Computes the sum of squared differences between corresponding pixels in two
     * images.
     *
     * @param pixels1 a 2D array representing the pixel values of the first image
     * @param pixels2 a 2D array representing the pixel values of the second image
     * @return the sum of squared pixel differences
     * @throws IllegalArgumentException if the images do not have matching
     *                                  dimensions
     */
    public static double pixelByPixelComparison(int[][] pixels1, int[][] pixels2)
            throws IllegalArgumentException {
        if (pixels1.length != pixels2.length || pixels1[0].length != pixels2[0].length) {
            throw new IllegalArgumentException("Image headers do not match");
        }

        double sumOfSquaredDiff = 0.0;

        for (int i = 0; i < pixels1.length; i++) {
            for (int j = 0; j < pixels1[0].length; j++) {
                double diff = pixels1[i][j] - pixels2[i][j];
                sumOfSquaredDiff += diff * diff;
            }
        }
        return sumOfSquaredDiff;
    }
}