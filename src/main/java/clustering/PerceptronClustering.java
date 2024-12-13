package clustering;

import java.util.ArrayList;
import java.util.List;

import perceptron.Perceptron;
import pgm.PGMImage;

public class PerceptronClustering extends ClusteringAlgorithm {
    private List<PGMImage> trainingImages;
    private List<Integer> classes = new ArrayList<>();
    private List<Perceptron> perceptrons = new ArrayList<>();

    public PerceptronClustering(int targetNumClusters, List<PGMImage> trainingImages, List<PGMImage> testImages)
            throws IllegalArgumentException {
        super(targetNumClusters, testImages);
        this.trainingImages = trainingImages;

        parseImageClasses();
        initializePerceptrons();
        super.performClustering();
    }

    private void parseImageClasses() {
        for (PGMImage image : trainingImages) {
            if (!classes.contains(image.getClassLabel()))
                classes.add(image.getClassLabel());
        }
        // if (classes.size() < 2) {
        // throw new IllegalArgumentException("At least two classes are required");
        // throw new IllegalArgumentException("At least two classes are required");
        // } TODO: Check if necessary
    }

    private void initializePerceptrons() {
        for (Integer imageClass : classes) {
            perceptrons.add(new Perceptron(trainingImages, imageClass));
        }
    }

    @Override
    protected double computeSimilarity(Cluster cluster1, Cluster cluster2) {
        double similarity = 0.0;

        for (int i = 0; i < perceptrons.size(); i++) {
            double diff = calculateDifference(cluster1, cluster2, perceptrons.get(i));
            similarity += 1.0 / Math.pow(diff, 2.0);
        }
        return similarity;
    }

    private double calculateDifference(Cluster cluster1, Cluster cluster2, Perceptron perceptron) {
        double cluster1Score = perceptron.calculateScore(cluster1.getClusterNormHistogram());
        double cluster2Score = perceptron.calculateScore(cluster2.getClusterNormHistogram());

        return cluster1Score - cluster2Score;
    }

}
