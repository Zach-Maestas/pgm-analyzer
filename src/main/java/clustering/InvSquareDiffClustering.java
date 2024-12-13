package clustering;

import java.util.List;

import pgm.PGMAnalyzer;
import pgm.PGMImage;

/**
 * A clustering algorithm that computes the similarity between clusters
 * based on the inverse of the squared difference between their average images.
 */
public class InvSquareDiffClustering extends ClusteringAlgorithm {

    /**
     * Constructs an instance of InvSquareDiffClustering with a specified
     * number of target clusters and a list of images to be clustered.
     *
     * @param targetNumClusters the desired number of clusters
     * @param images            the list of images to be clustered
     * @throws IllegalArgumentException if the target number of clusters is less
     *                                  than 1
     *                                  or greater than the number of images
     */
    public InvSquareDiffClustering(int targetNumClusters, List<PGMImage> images) throws IllegalArgumentException {
        super(targetNumClusters, images);
        super.performClustering();
    }

    /**
     * Computes the similarity between two clusters using the inverse squared
     * difference of their average images. The similarity is calculated as:
     * 
     * <pre>
     * similarity = 1 / (squared difference + 1)
     * </pre>
     * 
     * where the squared difference is computed pixel-by-pixel.
     *
     * @param cluster1 the first cluster to compare
     * @param cluster2 the second cluster to compare
     * @return a similarity score between the two clusters, where a higher score
     *         indicates greater similarity
     */
    @Override
    protected double computeSimilarity(Cluster cluster1, Cluster cluster2) {
        double similarity = (1.0
                / (PGMAnalyzer.pixelByPixelComparison(cluster1.getAverageImage(), cluster2.getAverageImage()) + 1.0));
        return similarity;
    }
}
