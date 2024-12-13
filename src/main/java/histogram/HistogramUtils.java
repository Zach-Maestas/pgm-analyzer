package histogram;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * Utility class for performing operations on histograms, such as normalization,
 * dot product calculation, and histogram intersection.
 */
public class HistogramUtils {

    /**
     * Normalizes a histogram by dividing each bin by the sum of all bins, producing
     * a histogram where the values sum to 1.0.
     *
     * @param histogram an array of bin counts representing the histogram
     * @return a normalized histogram as an array of double values
     * @throws ArithmeticException if the histogram sum is zero
     */
    public static double[] normalizeHistogram(int[] histogram) {
        int histogramSum = Arrays.stream(histogram).sum();
        if (histogramSum == 0) {
            throw new ArithmeticException("Cannot normalize histogram with sum zero");
        }
        return Arrays.stream(histogram)
                .asDoubleStream()
                .map(value -> value / (double) histogramSum)
                .toArray();
    }

    /**
     * Extracts sub-histograms from an image's pixel array by dividing it into a
     * grid of
     * sub-regions. Each sub-region's histogram is calculated and stored in an
     * array.
     *
     * @param pixelsArray      a 2D array representing the pixel values of an image
     * @param numSubHistograms the number of sub-histograms to create
     * @return a 2D array where each row is a histogram for a sub-region of the
     *         image
     */
    public static int[][] extractSubHistograms(int[][] pixelsArray, int numSubHistograms) {
        int gridSize = (int) Math.ceil(Math.sqrt(numSubHistograms));
        int subHistograms[][] = new int[gridSize * gridSize][Histogram.getHistogramBinCount()];

        int subHeight = pixelsArray.length / gridSize;
        int subWidth = pixelsArray[0].length / gridSize;

        for (int i = 0; i < numSubHistograms; i++) {
            int row = i / gridSize;
            int col = i % gridSize;
            int[][] subPixels = extractSubRegion(pixelsArray, row, col, subHeight, subWidth);

            subHistograms[i] = new Histogram(subPixels).getHistogramArray();
        }

        return subHistograms;

    }

    /**
     * Extracts a specific sub-region of pixels from an image's pixel array.
     *
     * @param pixelsArray a 2D array representing the pixel values of an image
     * @param row         the row index of the sub-region in the grid
     * @param col         the column index of the sub-region in the grid
     * @param subHeight   the height of the sub-region
     * @param subWidth    the width of the sub-region
     * @return a 2D array representing the extracted sub-region of pixel values
     */
    private static int[][] extractSubRegion(int[][] pixelsArray, int row, int col, int subHeight, int subWidth) {
        int[][] subRegion = new int[subHeight][subHeight];
        int startY = row * subHeight;
        int startX = col * subWidth;

        for (int i = 0; i < subHeight; i++) {
            for (int j = 0; j < subWidth; j++) {
                subRegion[i][j] = pixelsArray[startY + i][startX + j];
            }
        }
        return subRegion;
    }

    /**
     * Computes the dot product of the normalized histograms of two images,
     * indicating
     * their similarity.
     *
     * @param pgm1 the first image
     * @param pgm2 the second image
     * @return the dot product of the two normalized histograms
     * @throws IllegalArgumentException if the histograms do not have the same
     *                                  length
     */
    public static double computeDotProduct(double[] normHist1, double[] normHist2) throws IllegalArgumentException {
        if (normHist1.length != normHist2.length) {
            throw new IllegalArgumentException("Histograms must have the same length");
        }

        return IntStream.range(0, normHist1.length)
                .mapToDouble(i -> normHist1[i] * normHist2[i])
                .sum();
    }

    /**
     * Computes the intersection between two normalized histograms by summing the
     * minimum values in each corresponding bin, which measures similarity.
     *
     * @param normHist1 the first normalized histogram
     * @param normHist2 the second normalized histogram
     * @return the intersection value as a double
     * @throws IllegalArgumentException if the histograms do not have the same
     *                                  length
     */
    public static double computeIntersection(double[] normHist1, double[] normHist2)
            throws IllegalArgumentException {
        if (normHist1.length != normHist2.length) {
            throw new IllegalArgumentException("Histograms must have the same length");
        }

        return IntStream.range(0, normHist1.length)
                .mapToDouble(i -> Math.min(normHist1[i], normHist2[i]))
                .sum();
    }
}
