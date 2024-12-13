package perceptron;

import java.util.List;
import java.util.stream.IntStream;

import histogram.Histogram;
import pgm.PGMImage;

/**
 * Implements a simple Perceptron algorithm for binary classification of images,
 * specifically designed to classify images into a target class based on their
 * normalized histograms.
 */
public class Perceptron {
    /**
     * The number of epochs (iterations) for training.
     */
    private static final int NUM_EPOCHS = 100;

    /**
     * The target class label that the Perceptron aims to classify.
     */
    private final int targetClass;

    /**
     * The list of training samples used for training the Perceptron.
     */
    private List<PGMImage> trainingSamples;

    /**
     * The weights vector for the Perceptron, where each weight corresponds to
     * a bin in the histogram.
     */
    private double[] weights;

    /**
     * The bias term for the Perceptron.
     */
    private double bias = 0.0;

    /**
     * Constructs a Perceptron instance with a specified list of training samples
     * and a target class. Initializes the weights and begins training.
     *
     * @param trainingSamples the list of images to use for training
     * @param targetClass     the class label that the Perceptron will learn to
     *                        recognize
     * @throws IllegalArgumentException if the target class is not found in the
     *                                  samples
     */
    public Perceptron(List<PGMImage> trainingSamples, int targetClass) throws IllegalArgumentException {
        this.targetClass = targetClass;
        this.trainingSamples = trainingSamples;
        this.weights = new double[Histogram.getHistogramBinCount()];

        verifyTargetClassInSamples();
        train();
    }

    /**
     * Verifies that the target class exists within the training samples. If not,
     * an exception is thrown.
     *
     * @throws IllegalArgumentException if the target class label is not present in
     *                                  the samples
     */
    private void verifyTargetClassInSamples() {
        for (PGMImage img : trainingSamples) {
            if (img.getClassLabel() == targetClass) {
                return;
            }
        }
        throw new IllegalArgumentException("Target class label not found in provided samples");
    }

    /**
     * Trains the Perceptron by iterating over the training samples for a defined
     * number of epochs, updating the weights and bias based on the sample's
     * classification relative to the target class.
     */
    private void train() {
        for (int i = 0; i < NUM_EPOCHS; i++) {
            for (int j = 0; j < trainingSamples.size(); j++) {
                PGMImage sample = trainingSamples.get(j);
                boolean isTargetClass = (sample.getClassLabel() == targetClass) ? true : false;

                double perceptronValue = calculateScore(sample.getNormalizedHistogram());

                updateParameters(isTargetClass, perceptronValue, sample.getNormalizedHistogram());
            }
        }
    }

    /**
     * Updates the weights and bias of the Perceptron based on the error between
     * the desired output and the actual output.
     *
     * @param isPositiveSample    a boolean indicating if the sample belongs to the
     *                            target class
     * @param perceptronValue     the current weighted sum (plus bias) for the
     *                            sample
     * @param sampleNormHistogram the normalized histogram of the sample
     */
    private void updateParameters(boolean isTargetClass, double perceptronValue, double[] sampleNormHistogram) {
        int d = isTargetClass ? 1 : -1;
        double error = d - perceptronValue;

        IntStream.range(0, weights.length).forEach(i -> weights[i] += error * sampleNormHistogram[i]);
        bias += error;
    }

    /**
     * Computes the score for a given normalized histogram based on the current weights and bias.
     *
     * @param normHist The normalized histogram to score
     * @return The score as a double value
     */
    public double calculateScore(double[] normHist) {
        return IntStream.range(0, weights.length)
                .mapToDouble(i -> weights[i] * normHist[i])
                .sum() + bias; 
    }

    /**
     * Returns a string representation of the Perceptron's weights for each
     * histogram bin and includes the bias.
     *
     * @return a string with each weight formatted to six decimal places
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Double weight : weights) {
            sb.append(String.format("%.6f ", weight));
        }
        sb.append(String.format("%.6f ", bias));
        
        return sb.toString();
    }

}
