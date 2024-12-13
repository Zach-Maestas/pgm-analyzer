package clustering;

import java.util.List;

import histogram.HistogramUtils;
import pgm.PGMImage;

/**
 * Implements an agglomerative clustering algorithm for PGM images.
 * This class extends the abstract ClusteringAlgorithm and uses histogram
 * intersection to compute similarity between clusters.
 */
public class AgglomerativeClustering extends ClusteringAlgorithm {

    /**
     * Constructs an AgglomerativeClustering instance with a specified number of
     * clusters
     * and a list of images to cluster.
     *
     * @param targetNumClusters the target number of clusters
     * @param images            the list of PGMImage objects to cluster
     * @throws IllegalArgumentException if the number of clusters is invalid
     */
    public AgglomerativeClustering(int targetNumClusters, List<PGMImage> images) throws IllegalArgumentException {
        super(targetNumClusters, images);
        super.performClustering();
    }

    /**
     * Computes the similarity between two clusters using histogram intersection.
     * The similarity is calculated as the intersection of the normalized histograms
     * of the two clusters.
     *
     * @param cluster1 the first cluster
     * @param cluster2 the second cluster
     * @return a double value representing the similarity score between the two
     *         clusters
     */
    protected double computeSimilarity(Cluster cluster1, Cluster cluster2) {
        return HistogramUtils.computeIntersection(
                cluster1.getClusterNormHistogram(),
                cluster2.getClusterNormHistogram());
    }
}