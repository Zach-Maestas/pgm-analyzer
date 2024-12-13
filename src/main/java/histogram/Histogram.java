package histogram;

import java.util.Arrays;

/**
 * Represents a histogram for grayscale image values, partitioning pixel values
 * into
 * 64 bins, where each bin covers an interval of 4 grayscale values.
 */
public class Histogram {

    /**
     * The number of bins in the histogram.
     */
    private static final int HISTOGRAM_BIN_COUNT = 64;

    /**
     * The interval size for each histogram bin, representing 4 grayscale values.
     */
    private static final int INTERVAL_SIZE = 4;

    /**
     * Array holding the counts for each histogram bin.
     */
    private int[] histogramArray = new int[HISTOGRAM_BIN_COUNT];

    /**
     * Constructs a histogram from a 2D array of grayscale values.
     *
     * @param values a 2D array representing grayscale values of an image
     */
    public Histogram(int[][] values) {
        buildHistogram(values);
    }

    /**
     * Builds the histogram by iterating over the grayscale values and incrementing
     * the appropriate bin based on each pixel's value.
     *
     * @param values a 2D array representing grayscale values of an image
     */
    private void buildHistogram(int[][] values) {
        for (int row = 0; row < values.length; row++) {
            for (int col = 0; col < values[row].length; col++) {
                incrementHistogramBin(values[row][col]);
            }
        }
    }

    /**
     * Increments the appropriate bin for a given grayscale value.
     *
     * @param value the grayscale value (0-255) to be added to the histogram
     */
    private void incrementHistogramBin(int value) {
        int binIndex = value / INTERVAL_SIZE;
        histogramArray[binIndex]++;
    }

    /**
     * Returns the histogram array containing the counts for each bin.
     *
     * @return an array representing the histogram counts for each bin
     */
    public int[] getHistogramArray() {
        return histogramArray;
    }

    /**
     * Returns the number of bins in the histogram.
     *
     * @return the histogram bin count
     */
    public static int getHistogramBinCount() {
        return HISTOGRAM_BIN_COUNT;
    }

    /**
     * Returns a string representation of the histogram array.
     *
     * @return a string representing the histogram counts for each bin
     */
    @Override
    public String toString() {
        return Arrays.toString(histogramArray);
    }
}