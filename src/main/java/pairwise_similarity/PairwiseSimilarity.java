package pairwise_similarity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import histogram.HistogramUtils;
import pgm.PGMImage;

/**
 * Calculates the pairwise similarity between images in a list based on
 * histogram intersection.
 * For each image, it finds the most similar image by comparing normalized
 * histograms and
 * stores the similarity score in a map.
 */
public class PairwiseSimilarity {
    /**
     * List of images for which pairwise similarity will be calculated.
     */
    private List<PGMImage> images;

    /**
     * Map storing pairs of images and their similarity scores.
     * The key is a string representing the pair of images, and the value is the
     * similarity score.
     */
    private Map<String, Double> imageSimilarityPairsMap;

    /**
     * Constructs a PairwiseSimilarity object and calculates the most similar image
     * pairs for each image
     * in the provided list.
     *
     * @param images a list of PGMImage objects to compare
     */
    public PairwiseSimilarity(List<PGMImage> images) {
        this.images = images;
        this.imageSimilarityPairsMap = new HashMap<>();

        findClosestImagePairs();
    }

    /**
     * Finds the closest image pairs by calculating the histogram intersection
     * similarity
     * for each image and storing the most similar pair in the map.
     */
    public void findClosestImagePairs() {
        for (PGMImage image : images) {
            PGMImage mostSimilarImage = findMostSimilarImage(image, images);
            if (mostSimilarImage != null) {
                double histogramIntersection = HistogramUtils.computeIntersection(image.getNormalizedHistogram(),
                        mostSimilarImage.getNormalizedHistogram());

                String key = image + " " + mostSimilarImage;
                imageSimilarityPairsMap.put(key, histogramIntersection);
            }
        }
    }

    /**
     * Finds the most similar image to the specified image from a list of images,
     * based on histogram intersection similarity.
     *
     * @param pgm    the image for which to find the most similar image
     * @param images the list of images to search
     * @return the PGMImage object most similar to the specified image, or null if
     *         none is found
     */
    private static PGMImage findMostSimilarImage(PGMImage inputImage, List<PGMImage> images) {
        double maxSimilarity = Double.MIN_VALUE;
        PGMImage mostSimilarImage = null;

        for (PGMImage image : images) {
            if (!inputImage.equals(image)) {
                double similarity = HistogramUtils.computeIntersection(inputImage.getNormalizedHistogram(),
                        image.getNormalizedHistogram());
                if (similarity > maxSimilarity) {
                    maxSimilarity = similarity;
                    mostSimilarImage = image;
                }
            }
        }
        return mostSimilarImage;
    }

    /**
     * Returns a string representation of the image pairs with their similarity
     * scores.
     * Each pair is represented by the names of the two images followed by the
     * similarity score.
     *
     * @return a string representation of the image pairs and their similarity
     *         scores
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Double> entry : imageSimilarityPairsMap.entrySet()) {
            sb.append(String.format("%s %.6f\n", entry.getKey(), entry.getValue()));
        }
        return sb.toString();
    }
}
