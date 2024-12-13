package clustering.quality;

import java.util.List;

import clustering.Cluster;
import clustering.ClusteringAlgorithm;
import pgm.PGMImage;

import java.util.HashMap;

/**
 * Analyzes the quality of clustering by calculating the proportion of images in
 * each cluster that belong to the dominant category (i.e., the most common
 * class label).
 */
public class ClusteringQualityAnalyzer {

    /**
     * Computes the clustering quality as the proportion of images in clusters that
     * belong to the dominant category in each cluster. The quality score is
     * calculated
     * as the sum of dominant category counts for each cluster divided by the total
     * number of images.
     *
     * @param clustering the clustering algorithm containing the clusters to analyze
     * @return a double value representing the clustering quality, where 1.0
     *         indicates
     *         perfect clustering (all clusters are pure) and values closer to 0
     *         indicate
     *         lower quality
     */
    public static double computeClusteringQuality(ClusteringAlgorithm clustering) {
        List<Cluster> clusters = clustering.getClusters();
        double dominantCategoryCount = 0.0;
        double totalImageCount = 0.0;

        for (Cluster cluster : clusters) {
            dominantCategoryCount += findNumImagesInDominantCategory(initializeCategoryCounts(cluster));
            totalImageCount += cluster.getImages().size();
        }
        return dominantCategoryCount / totalImageCount;
    }

    /**
     * Initializes a category count map for a given cluster, where the map keys
     * are category labels and values are the counts of images with that label
     * within the cluster.
     *
     * @param cluster the cluster to analyze
     * @return a HashMap where keys are category labels and values are the counts
     *         of images in the cluster with each label
     */
    private static HashMap<Integer, Integer> initializeCategoryCounts(Cluster cluster) {
        HashMap<Integer, Integer> categoryCounts = new HashMap<>();

        for (PGMImage img : cluster.getImages()) {
            int categoryNum = img.getClassLabel();
            categoryCounts.put(categoryNum, categoryCounts.getOrDefault(categoryNum, 0) + 1);
        }
        return categoryCounts;
    }

    /**
     * Finds the number of images in the dominant category within a cluster, where
     * the dominant category is defined as the category with the highest count.
     *
     * @param categoryCounts a map of category labels to counts of images in each
     *                       category
     * @return the count of images in the dominant category within the cluster
     */
    private static int findNumImagesInDominantCategory(HashMap<Integer, Integer> categoryCounts) {
        int maxInstances = 0;

        for (Integer count : categoryCounts.values()) {
            if (count > maxInstances) {
                maxInstances = count;
            }
        }
        return maxInstances;
    }
}