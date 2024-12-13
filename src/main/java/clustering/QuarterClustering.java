package clustering;

import java.util.List;
import java.util.stream.IntStream;

import histogram.HistogramUtils;
import pgm.PGMImage;

/**
 * A clustering algorithm that calculates similarity between clusters based on
 * the intersection of normalized histograms for 4-partitioned sub-regions
 * within each cluster's average image.
 */
public class QuarterClustering extends ClusteringAlgorithm {

    /**
     * Constructs an instance of QuarterClustering with a specified number of target
     * clusters and a list of images to be clustered.
     *
     * @param targetNumClusters the desired number of clusters
     * @param images            the list of images to be clustered
     * @throws IllegalArgumentException if the target number of clusters is less
     *                                  than 1
     *                                  or greater than the number of images
     */
    public QuarterClustering(int targetNumClusters, List<PGMImage> images) {
        super(targetNumClusters, images);
        super.performClustering();
    }

    /**
     * Computes the similarity between two clusters by partitioning each cluster's
     * average image into 4 sub-regions and calculating the intersection of
     * normalized
     * histograms for each corresponding partition. The final similarity is the
     * average
     * intersection across all 4 partitions.
     *
     * @param cluster1 the first cluster to compare
     * @param cluster2 the second cluster to compare
     * @return a similarity score between the two clusters, where a higher score
     *         indicates greater similarity
     */
    @Override
    protected double computeSimilarity(Cluster cluster1, Cluster cluster2) {
        int[][] subHistograms1 = cluster1.getClusterPartitionHistograms(4);
        int[][] subHistograms2 = cluster2.getClusterPartitionHistograms(4);

        double similarity = IntStream.range(0, subHistograms1.length)
                .mapToDouble(i -> {
                    double[] normalizedHist1 = HistogramUtils.normalizeHistogram(subHistograms1[i]);
                    double[] normalizedHist2 = HistogramUtils.normalizeHistogram(subHistograms2[i]);
                    return HistogramUtils.computeIntersection(normalizedHist1, normalizedHist2);
                })
                .sum();

        return similarity / subHistograms1.length;
    }

    /**
     * Merges two clusters by first merging the images in the second cluster
     * into the first cluster, then updating the partitioned histograms for
     * the merged cluster to maintain consistency with 4-partitioned sub-regions.
     *
     * @param cluster1 the cluster into which images will be merged
     * @param cluster2 the cluster that will be merged into the first cluster
     */
    @Override
    protected void mergeClusters(Cluster cluster1, Cluster cluster2) {
        super.mergeClusters(cluster1, cluster2);
        cluster1.updateClusterPartitionHistograms(4);
    }
}
