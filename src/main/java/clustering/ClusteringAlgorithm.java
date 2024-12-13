package clustering;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import pgm.PGMImage;

/**
 * An abstract base class representing a generic clustering algorithm.
 * This class initializes clusters, performs clustering based on a similarity
 * measure,
 * and merges clusters until the target number of clusters is reached.
 */
public abstract class ClusteringAlgorithm {

    /**
     * The list of clusters maintained by the algorithm.
     */
    private List<Cluster> clusters = new ArrayList<>();

    /**
     * The list of images to be clustered.
     */
    private List<PGMImage> images;

    /**
     * The target number of clusters to achieve after clustering.
     */
    private int targetNumClusters;

    /**
     * Initializes a new clustering algorithm with a specified number of target
     * clusters
     * and a list of images to be clustered. It also performs the initial clustering
     * process.
     *
     * @param targetNumClusters the desired number of clusters after clustering
     * @param images            the list of images to be clustered
     * @throws IllegalArgumentException if the target number of clusters is less
     *                                  than 1
     *                                  or greater than the number of images
     */
    public ClusteringAlgorithm(int targetNumClusters, List<PGMImage> images) throws IllegalArgumentException {
        if (targetNumClusters > images.size() || targetNumClusters < 1) {
            throw new IllegalArgumentException("Number of clusters must be between 1 and " + images.size());
        }
        this.images = images;
        this.targetNumClusters = targetNumClusters;

        initializeClusters();
    }

    /**
     * Initializes the clusters by creating a single cluster for each image.
     * Each image is initially treated as an individual cluster.
     */
    private void initializeClusters() {
        for (int i = 0; i < images.size(); i++) {
            clusters.add(new Cluster(images.get(i)));
        }
    }

    /**
     * Performs the clustering process by repeatedly merging the most similar
     * clusters
     * until the target number of clusters is achieved.
     */
    protected void performClustering() {
        while (clusters.size() > targetNumClusters) {
            Cluster[] bestPair = findBestClusterPair();
            if (bestPair != null) {
                mergeClusters(bestPair[0], bestPair[1]);
            } else {
                break;
            }
        }
    }

    /**
     * Finds the pair of clusters with the highest similarity.
     *
     * @return an array of two clusters that are the most similar
     */
    /**
     * Finds the pair of clusters with the highest similarity.
     *
     * @return an array of two clusters that are the most similar
     */
    private Cluster[] findBestClusterPair() {
        Cluster[] mostSimilarClusters = new Cluster[2];
        double maxSimilarity = Double.MIN_VALUE;

        for (int i = 0; i < clusters.size(); i++) {
            for (int j = i + 1; j < clusters.size(); j++) {
                double similarity = computeSimilarity(clusters.get(i), clusters.get(j));
                if (similarity > maxSimilarity) {
                    maxSimilarity = similarity;
                    mostSimilarClusters[0] = clusters.get(i);
                    mostSimilarClusters[1] = clusters.get(j);
                }
            }
        }
        return mostSimilarClusters;
    }

    /**
     * Computes the similarity between two clusters. This method is abstract and
     * must
     * be implemented by subclasses to define a specific similarity measure.
     *
     * @param cluster1 the first cluster
     * @param cluster2 the second cluster
     * @return a double value representing the similarity between the two clusters
     */
    protected abstract double computeSimilarity(Cluster cluster1, Cluster cluster2);

    /**
     * Merges two clusters by adding all images from the second cluster into the
     * first cluster.
     * The second cluster is removed from the list of clusters after merging.
     *
     * @param cluster1 the cluster into which images will be merged
     * @param cluster2 the cluster that will be merged into the first cluster
     */
    protected void mergeClusters(Cluster cluster1, Cluster cluster2) {
        if (clusters.contains(cluster2)) {
            cluster1.merge(cluster2);
            clusters.remove(cluster2);
        }
    }

    /**
     * Returns the list of clusters after the clustering process.
     *
     * @return a list of clusters in the current state of the algorithm
     */
    public List<Cluster> getClusters() {
        return clusters;
    }

    /**
     * Returns a string representation of the clustering algorithm, with each
     * cluster
     * represented by a line listing the images in that cluster.
     *
     * @return a string representation of the clusters
     */
    @Override
    public String toString() {
        return clusters.stream()
                .map(Cluster::toString)
                .collect(Collectors.joining("\n"));
    }
}